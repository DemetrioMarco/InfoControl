import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MovimientoInventarioService } from '../../../core/services/movimiento-inventario.service';
import { MovimientoInventario } from '../../../core/models/movimiento-inventario.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-movimiento-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './movimiento-list.html',
  styleUrl: './movimiento-list.css'
})
export class MovimientoList implements OnInit {
  private movimientoService = inject(MovimientoInventarioService);

  movimientos = signal<MovimientoInventario[]>([]);
  tipoFiltro = signal<string>('TODOS');
  loading = signal(false);

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
}
