import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import Swal from 'sweetalert2';

import { MovimientoInventarioService } from '../../core/services/movimiento-inventario.service';
import { ProductoService } from '../../core/services/producto.service';
import { SubUbicacionService } from '../../core/services/sububicacion.service';
import { DocumentoMovimientoService } from '../../core/services/documento-movimiento.service';
import { CorrelativoMovimientoService } from '../../core/services/correlativo-movimiento.service';

import { MovimientoInventarioRequest, TipoMovimiento } from '../../core/models/movimiento-inventario.model';
import { TipoMovimientoCorrelativo } from '../../core/models/correlativo-movimiento.model';
import { SerieProductoService } from '../../core/services/serie-producto.service';
import { SerieProductoResponse } from '../../core/models/serie-producto.model';

@Component({
  selector: 'app-movimiento-inventario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './movimiento-inventario.html',
  styleUrl: './movimiento-inventario.css'
})
export class MovimientoInventario implements OnInit {
  private fb = inject(FormBuilder);
  private movimientoService = inject(MovimientoInventarioService);
  private productoService = inject(ProductoService);
  private subUbicacionService = inject(SubUbicacionService);
  private documentoMovimientoService = inject(DocumentoMovimientoService);
  private correlativoMovimientoService = inject(CorrelativoMovimientoService);
  private serieProductoService = inject(SerieProductoService);

  productos = signal<any[]>([]);
  subUbicaciones = signal<any[]>([]);
  loading = signal(false);

  archivoSeleccionado = signal<File | null>(null);
  nombreArchivo = signal<string>('');
  maxFileSize = 10 * 1024 * 1024;
  folioHeader = signal<string>('...');

  seriesCapturadas = signal<string[]>([]);
  seriesDisponibles = signal<SerieProductoResponse[]>([]);
  seriesSeleccionadas = signal<SerieProductoResponse[]>([]);
  filtroSerie = signal('');

  form: FormGroup = this.fb.group({
    tipoMovimiento: ['ENTRADA', [Validators.required]],
    productoId: [null, [Validators.required]],
    cantidad: [null, [Validators.required, Validators.min(1)]],
    precioUnitario: [null],
    subUbicacionOrigenId: [null],
    subUbicacionDestinoId: [null],
    numeroReferencia: ['', [Validators.required]],
    motivo: ['', [Validators.required]],
    observaciones: ['']
  });

  seriesFiltradas = computed(() => {
    const term = this.filtroSerie().toLowerCase();
    return this.seriesDisponibles().filter(s => s.serie.toLowerCase().includes(term));
  });

  get cantidadCoincideConSeries(): boolean {
    const cantidad = this.form.get('cantidad')?.value || 0;
    if (this.esEntrada) {
      return this.seriesCapturadas().length === cantidad;
    } else {
      return this.seriesSeleccionadas().length === cantidad;
    }
  }

  ngOnInit(): void {
    this.cargarCatalogos();
    // this.watchTipoMovimiento();
    this.watchFormChanges(); 
   // this.cargarPreviewCorrelativo(this.form.get('tipoMovimiento')?.value);
    this.cargarPreviewCorrelativo('ENTRADA');
  }

  private watchFormChanges(): void {
  // Escucha el tipo de movimiento para TODO (Folio, Series y Validaciones)
  this.form.get('tipoMovimiento')?.valueChanges.subscribe((tipo) => {
    this.validarCamposPorTipo(tipo); // Ejecuta tus validaciones de siempre
    this.cargarPreviewCorrelativo(tipo); // Carga el folio instantáneo
    this.seriesCapturadas.set([]);
    this.seriesSeleccionadas.set([]);
    this.cargarSeriesDisponibles();
  });

  // Escucha producto o sub-ubicación para las series
  ['productoId', 'subUbicacionOrigenId'].forEach(field => {
    this.form.get(field)?.valueChanges.subscribe(() => this.cargarSeriesDisponibles());
  });
}

  private async cargarSeriesDisponibles() {
    const tipo = this.form.get('tipoMovimiento')?.value;
    const productoId = this.form.get('productoId')?.value;
    const origenId = this.form.get('subUbicacionOrigenId')?.value;

    if (tipo !== 'ENTRADA' && productoId && origenId) {
      const data = await firstValueFrom(this.serieProductoService.getByProductoIdAndSubUbicacionId(productoId, origenId));
      this.seriesDisponibles.set(data);
    } else {
      this.seriesDisponibles.set([]);
    }
    this.seriesSeleccionadas.set([]);
  }

  agregarSerie(input: HTMLInputElement) {
    const valor = input.value.trim().toUpperCase();
    const cantidadMax = this.form.get('cantidad')?.value || 0;

    if (valor && !this.seriesCapturadas().includes(valor) && this.seriesCapturadas().length < cantidadMax) {
      this.seriesCapturadas.update(prev => [...prev, valor]);
      input.value = '';
    }
  }

  removerSerie(index: number) {
    this.seriesCapturadas.update(prev => prev.filter((_, i) => i !== index));
  }

  toggleSeleccionSerie(serie: SerieProductoResponse) {
    const cantidadMax = this.form.get('cantidad')?.value || 0;
    const seleccionadas = this.seriesSeleccionadas();
    const index = seleccionadas.findIndex(s => s.id === serie.id);

    if (index > -1) {
      this.seriesSeleccionadas.update(prev => prev.filter((_, i) => i !== index));
    } else if (seleccionadas.length < cantidadMax) {
      this.seriesSeleccionadas.update(prev => [...prev, serie]);
    }
  }

  get esEntrada(): boolean {
    return this.form.get('tipoMovimiento')?.value === 'ENTRADA';
  }

  private cargarCatalogos(): void {
    this.productoService.getAll().subscribe(data => this.productos.set(data));
    this.subUbicacionService.getAll().subscribe(data => this.subUbicaciones.set(data));
  }

  private watchTipoMovimiento(): void {
    this.form.get('tipoMovimiento')?.valueChanges.subscribe((tipo: TipoMovimiento) => {
      this.seriesCapturadas.set([]);
      this.seriesSeleccionadas.set([]);
      this.validarCamposPorTipo(tipo);
      this.cargarPreviewCorrelativo(tipo);
    });
  }

  private cargarPreviewCorrelativo(tipo: TipoMovimiento): void {
  if (!this.esTipoMovimientoCorrelativo(tipo)) {
    this.folioHeader.set('---'); // <-- Actualiza signal
    this.form.get('numeroReferencia')?.setValue('');
    return;
  }
  
  this.correlativoMovimientoService.preview(tipo).subscribe({
    next: (response) => {
      this.folioHeader.set(response.codigoSiguiente); // <-- Actualiza signal
      this.form.get('numeroReferencia')?.setValue(response.codigoSiguiente);
    },
    error: () => {
      this.folioHeader.set('ERROR');
      this.form.get('numeroReferencia')?.setValue('ERROR');
    }
  });
}

  private esTipoMovimientoCorrelativo(tipo: TipoMovimiento): tipo is TipoMovimientoCorrelativo {
    return tipo === 'ENTRADA' || tipo === 'SALIDA' || tipo === 'TRASPASO' || tipo === 'AJUSTE';
  }

  onArchivoChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];
    if (file.size > this.maxFileSize) {
      Swal.fire('Error', 'El documento no puede superar 10 MB.', 'error');
      input.value = '';
      return;
    }
    this.archivoSeleccionado.set(file);
    this.nombreArchivo.set(file.name);
  }

  limpiarArchivo(): void {
    this.archivoSeleccionado.set(null);
    this.nombreArchivo.set('');
  }

  getUserId(): number {
    const userJson = localStorage.getItem('user');
    return userJson ? JSON.parse(userJson).id : 0;
  }

  async guardar(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (!this.cantidadCoincideConSeries) {
      const actual = this.esEntrada ? this.seriesCapturadas().length : this.seriesSeleccionadas().length;
      Swal.fire('Error', `La cantidad $$${this.form.value.cantidad}$$ no coincide con las series asignadas ($$${actual}$$).`, 'error');
      return;
    }

    const val = this.form.getRawValue();
    this.loading.set(true);

    try {
      // 1. Guardar Movimiento
      const res = await firstValueFrom(this.movimientoService.create({ ...val, usuarioResponsableId: this.getUserId() }));
      await firstValueFrom(this.correlativoMovimientoService.siguiente(val.tipoMovimiento));
      const movimientoId = res.id;

      // 2. Procesar Series (NUEVO)
      if (this.esEntrada) {
        for (const s of this.seriesCapturadas()) {
          await firstValueFrom(this.serieProductoService.create({
            productoId: val.productoId,
            subUbicacionId: val.subUbicacionDestinoId,
            serie: s
          }));
        }
      } else {
        for (const s of this.seriesSeleccionadas()) {
          if (val.tipoMovimiento === 'TRASPASO') {
            await firstValueFrom(this.serieProductoService.update(s.id, {
              productoId: val.productoId,
              subUbicacionId: val.subUbicacionDestinoId,
              serie: s.serie
            }));
          } else {
            await firstValueFrom(this.serieProductoService.delete(s.id));
          }
        }
      }

      // 3. Documento
      if (this.esEntrada && this.archivoSeleccionado() && movimientoId) {
        await firstValueFrom(this.documentoMovimientoService.subirDocumento(movimientoId, this.archivoSeleccionado()!, this.getUserId()));
      }

      Swal.fire('Éxito', res.mensaje || 'Movimiento y series procesados.', 'success');
      this.resetFormulario();
    } catch (error) {
      Swal.fire('Error', 'No se pudo completar la operación.', 'error');
    } finally {
      this.loading.set(false);
    }
  }

  resetFormulario(): void {
    this.form.reset({ tipoMovimiento: 'ENTRADA' });
    this.seriesCapturadas.set([]);
    this.seriesSeleccionadas.set([]);
    this.limpiarArchivo();
    this.cargarPreviewCorrelativo('ENTRADA');
  }

  private validarCamposPorTipo(tipo: any) {
    const origen = this.form.get('subUbicacionOrigenId');
    const destino = this.form.get('subUbicacionDestinoId');
    origen?.clearValidators();
    destino?.clearValidators();
    if (tipo === 'ENTRADA') destino?.setValidators([Validators.required]);
    else if (tipo === 'SALIDA') origen?.setValidators([Validators.required]);
    else if (tipo === 'TRASPASO') {
      origen?.setValidators([Validators.required]);
      destino?.setValidators([Validators.required]);
    }
    origen?.updateValueAndValidity();
    destino?.updateValueAndValidity();
    if (tipo !== 'ENTRADA') this.limpiarArchivo();
  }
}
