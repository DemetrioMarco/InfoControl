import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import Swal from 'sweetalert2';

import { MovimientoInventarioService } from '../../core/services/movimiento-inventario.service';
import { ProductoService } from '../../core/services/producto.service';
import { SubUbicacionService } from '../../core/services/sububicacion.service';
import { DocumentoMovimientoService } from '../../core/services/documento-movimiento.service';

import {
  MovimientoInventarioRequest,
  TipoMovimiento,
  MovimientoResponse
} from '../../core/models/movimiento-inventario.model';

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

  productos = signal<any[]>([]);
  subUbicaciones = signal<any[]>([]);
  loading = signal(false);

  archivoSeleccionado = signal<File | null>(null);
  nombreArchivo = signal<string>('');
  maxFileSize = 10 * 1024 * 1024; // 10 MB

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

  ngOnInit(): void {
    this.cargarCatalogos();
    this.watchTipoMovimiento();
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
      const origen = this.form.get('subUbicacionOrigenId');
      const destino = this.form.get('subUbicacionDestinoId');

      if (tipo !== 'ENTRADA') {
        this.limpiarArchivo();
      }

      origen?.clearValidators();
      destino?.clearValidators();
      origen?.setValue(null);
      destino?.setValue(null);

      if (tipo === 'ENTRADA') {
        destino?.setValidators([Validators.required]);
      } else if (tipo === 'SALIDA') {
        origen?.setValidators([Validators.required]);
      } else if (tipo === 'TRASPASO') {
        origen?.setValidators([Validators.required]);
        destino?.setValidators([Validators.required]);
      }

      origen?.updateValueAndValidity();
      destino?.updateValueAndValidity();
    });
  }

  onArchivoChange(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      this.limpiarArchivo();
      return;
    }

    const file = input.files[0];

    if (file.size > this.maxFileSize) {
      Swal.fire(
        'Archivo demasiado grande',
        'El documento no puede superar 10 MB.',
        'error'
      );
      this.limpiarArchivo();
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
    const userJson = localStorage.getItem('auth_user');
    if (userJson) {
      const user = JSON.parse(userJson);
      return user.id;
    }
    return 0;
  }

  async guardar(): Promise<void> {
  if (this.form.invalid) {
    this.form.markAllAsTouched();
    return;
  }

  const val = this.form.getRawValue();

  if (val.tipoMovimiento === 'TRASPASO' && val.subUbicacionOrigenId === val.subUbicacionDestinoId) {
    Swal.fire('Error', 'El origen y destino no pueden ser iguales', 'error');
    return;
  }

  this.loading.set(true);

  const request: MovimientoInventarioRequest = {
    ...val,
    usuarioResponsableId: this.getUserId()
  };

  try {
    const res = await firstValueFrom(this.movimientoService.create(request));
    const movimientoId = res.id;

    console.log(movimientoId);
    if (this.esEntrada && this.archivoSeleccionado() && movimientoId) {
      try {
        await firstValueFrom(
          this.documentoMovimientoService.subirDocumento(
            movimientoId,
            this.archivoSeleccionado() as File,
            this.getUserId()
          )
        );

        Swal.fire({
          icon: 'success',
          title: 'Registrado',
          text: res.mensaje ?? 'Movimiento registrado y documento cargado correctamente.',
          timer: 2000,
          showConfirmButton: false
        });
      } catch (error) {
        Swal.fire({
          icon: 'warning',
          title: 'Movimiento registrado',
          text: 'El movimiento se guardó, pero no se pudo cargar el documento.'
        });
      }
    } else {
      Swal.fire({
        icon: 'success',
        title: 'Registrado',
        text: res.mensaje ?? 'Movimiento registrado correctamente.',
        timer: 2000,
        showConfirmButton: false
      });
    }

    this.resetFormulario();
  } catch (error) {
    Swal.fire('Error', 'No se pudo registrar el movimiento', 'error');
  } finally {
    this.loading.set(false);
  }
}


  resetFormulario(): void {
    this.form.reset({ tipoMovimiento: 'ENTRADA' });
    this.limpiarArchivo();
  }
}
