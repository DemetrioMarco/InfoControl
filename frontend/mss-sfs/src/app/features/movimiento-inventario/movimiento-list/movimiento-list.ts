import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MovimientoInventarioService } from '../../../core/services/movimiento-inventario.service';
import { DocumentoMovimientoService } from '../../../core/services/documento-movimiento.service';
import { SerieProductoService } from '../../../core/services/serie-producto.service';
import { MovimientoInventario } from '../../../core/models/movimiento-inventario.model';
import { DocumentoMovimientoResponse } from '../../../core/models/documento-movimiento.model';
import { SerieProductoResponse } from '../../../core/models/serie-producto.model';

@Component({
  selector: 'app-movimiento-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './movimiento-list.html',
  styleUrl: './movimiento-list.css'
})
export class MovimientoList implements OnInit {
  private movimientoService = inject(MovimientoInventarioService);
  private documentoService = inject(DocumentoMovimientoService);
  private serieService = inject(SerieProductoService);

  movimientos = signal<MovimientoInventario[]>([]);
  tipoFiltro = signal<string>('TODOS');
  loading = signal(false);

  // Estados del Modal
  modalVisible = signal(false);
  movimientoSeleccionado = signal<MovimientoInventario | null>(null);
  documentosMovimiento = signal<DocumentoMovimientoResponse[]>([]);
  seriesMovimiento = signal<SerieProductoResponse[]>([]);
  loadingDocumentos = signal(false);
  loadingSeries = signal(false);

  ngOnInit(): void {
    this.cargarMovimientos();
  }

  cargarMovimientos(): void {
    this.loading.set(true);
    const observable = this.tipoFiltro() === 'TODOS'
      ? this.movimientoService.getAll()
      : this.movimientoService.getByTipo(this.tipoFiltro());

    observable.subscribe({
      next: (data) => { this.movimientos.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  abrirDetalle(movimiento: MovimientoInventario): void {
    this.movimientoSeleccionado.set(movimiento);
    this.modalVisible.set(true);
    
    // Reset de datos previos
    this.seriesMovimiento.set([]);
    this.documentosMovimiento.set([]);

    // 1. Cargar Series (Lógica de ubicación)
    const subUbicacionId = movimiento.tipoMovimiento === 'SALIDA' 
      ? movimiento.subUbicacionOrigenId 
      : movimiento.subUbicacionDestinoId;

    if (subUbicacionId && movimiento.productoId) {
      this.loadingSeries.set(true);
      this.serieService.getByProductoIdAndSubUbicacionId(movimiento.productoId, subUbicacionId).subscribe({
        next: (series) => {
          this.seriesMovimiento.set(series);
          this.loadingSeries.set(false);
        },
        error: () => this.loadingSeries.set(false)
      });
    }

    // 2. Cargar Documentos (Solo entradas)
    if (movimiento.tipoMovimiento === 'ENTRADA') {
      this.loadingDocumentos.set(true);
      this.documentoService.obtenerPorMovimiento(movimiento.id!).subscribe({
        next: (docs) => {
          this.documentosMovimiento.set(docs || []);
          this.loadingDocumentos.set(false);
        },
        error: () => this.loadingDocumentos.set(false)
      });
    }
  }

  cerrarDetalle(): void {
    this.modalVisible.set(false);
    this.movimientoSeleccionado.set(null);
    this.loadingSeries.set(false);
    this.loadingDocumentos.set(false);
  }

  getBadgeClass(tipo: string): string {
    const classes: Record<string, string> = {
      'ENTRADA': 'bg-success-subtle text-success border-success',
      'SALIDA': 'bg-danger-subtle text-danger border-danger',
      'TRASPASO': 'bg-primary-subtle text-primary border-primary'
    };
    return classes[tipo] || 'bg-secondary-subtle';
  }

  abrirDocumento(ruta: string) {
    const url = this.documentoService.getDownloadUrl(ruta);
    if (url) window.open(url, '_blank');
  }
}
