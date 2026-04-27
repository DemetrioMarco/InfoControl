import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService } from '../../../../core/services/producto.service';
import { ProductoListItem } from '../../../../core/models/producto.model';

import Swal from 'sweetalert2';
import { ProductoForm } from '../../components/producto-form/producto-form';

@Component({
  selector: 'app-producto-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductoForm],
  templateUrl: './producto-list.html',
  styleUrls: ['./producto-list.css']
})
export class ProductoList implements OnInit {
  private productoService = inject(ProductoService);

  // Signals para estado reactivo
  productos = signal<ProductoListItem[]>([]);
  searchTerm = signal('');
  loading = signal(false);
  showModal = signal(false);
  selectedId = signal<number | null>(null);

  // Filtro reactivo
  filteredProductos = computed(() => {
    const term = this.searchTerm().toLowerCase();
    return this.productos().filter(p => 
      p.nombre.toLowerCase().includes(term) || 
      p.codigoInterno.toLowerCase().includes(term) ||
      p.categoriaNombre.toLowerCase().includes(term)
    );
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.productoService.getAll().subscribe({
      next: (data) => {
        this.productos.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  openModal(id?: number): void {
    this.selectedId.set(id || null);
    this.showModal.set(true);
  }

  closeModal(refresh: boolean = false): void {
    this.showModal.set(false);
    this.selectedId.set(null);
    if (refresh) this.load();
  }

  onToggleActivo(id: number): void {
    this.productoService.toggleActivo(id).subscribe({
      next: () => {
        this.productos.update(prev => prev.map(p => 
          p.id === id ? { ...p, activo: !p.activo } : p
        ));
      }
    });
  }

    onDelete(id: number): void {
    Swal.fire({
      title: '¿Eliminar producto?',
      text: 'Esta acción no se puede deshacer',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      customClass: { 
        confirmButton: 'btn btn-danger me-2', 
        cancelButton: 'btn btn-light' 
      },
      buttonsStyling: false // Para que use las clases de Bootstrap correctamente
    }).then(result => { // <--- CAMBIO AQUÍ: .then() en lugar de .subscribe()
      if (result.isConfirmed) {
        this.productoService.delete(id).subscribe({
          next: () => {
            this.productos.update(prev => prev.filter(p => p.id !== id));
            Swal.fire('Eliminado', 'Producto borrado correctamente', 'success');
          },
          error: (err) => {
            Swal.fire('Error', 'No se pudo eliminar el producto', 'error');
          }
        });
      }
    });
  }


  getStockClass(estado: string): string {
    const classes: Record<string, string> = {
      'NORMAL': 'bg-success-subtle text-success',
      'BAJO': 'bg-danger-subtle text-danger',
      'SOBRE_STOCK': 'bg-warning-subtle text-dark'
    };
    return classes[estado] || 'bg-secondary-subtle';
  }
}
