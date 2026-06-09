import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MovimientoInventarioService } from '../../../core/services/movimiento-inventario.service';
import { DocumentoMovimientoService } from '../../../core/services/documento-movimiento.service';
import { MovimientoInventario } from '../../../core/models/movimiento-inventario.model';
import { DocumentoMovimientoResponse } from '../../../core/models/documento-movimiento.model';

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

  movimientos = signal<MovimientoInventario[]>([]);
  tipoFiltro = signal<string>('TODOS');
  loading = signal(false);

  modalVisible = signal(false);
  movimientoSeleccionado = signal<MovimientoInventario | null>(null);
  documentosMovimiento = signal<DocumentoMovimientoResponse[]>([]);
  loadingDocumentos = signal(false);

  ngOnInit(): void {
    this.cargarMovimientos();
  }

  cargarMovimientos(): void {
    this.loading.set(true);

    const observable = this.tipoFiltro() === 'TODOS'
      ? this.movimientoService.getAll()
      : this.movimientoService.getByTipo(this.tipoFiltro());

    observable.subscribe({
      next: (data) => {
        this.movimientos.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  getBadgeClass(tipo: string): string {
    const classes: Record<string, string> = {
      'ENTRADA': 'bg-success-subtle text-success border-success',
      'SALIDA': 'bg-danger-subtle text-danger border-danger',
      'TRASPASO': 'bg-primary-subtle text-primary border-primary'
    };
    return classes[tipo] || 'bg-secondary-subtle';
  }

  abrirDetalle(movimiento: MovimientoInventario): void {
    this.movimientoSeleccionado.set(movimiento);
    this.documentosMovimiento.set([]);
    this.modalVisible.set(true);

    if (movimiento.tipoMovimiento === 'ENTRADA') {
      this.loadingDocumentos.set(true);

      this.documentoService.obtenerPorMovimiento(movimiento.id!).subscribe({
        next: (docs) => {
          this.documentosMovimiento.set(docs || []);
          this.loadingDocumentos.set(false);
        },
        error: () => {
          this.documentosMovimiento.set([]);
          this.loadingDocumentos.set(false);
        }
      });
    }
  }

  cerrarDetalle(): void {
    this.modalVisible.set(false);
    this.movimientoSeleccionado.set(null);
    this.documentosMovimiento.set([]);
    this.loadingDocumentos.set(false);
  }

  abrirDocumento(rutaArchivo: string): void {
    const url = this.documentoService.getDownloadUrl(rutaArchivo);
    if (url) {
      window.open(url, '_blank', 'noopener,noreferrer');
    }
  }
}
