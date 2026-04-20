import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProveedorService } from '../../../core/services/proveedor.service';
import { Proveedor } from '../../../core/models/proveedor.model';
import { SupplierForm } from './supplier-form';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-supplier-list',
  standalone: true,
  imports: [CommonModule, FormsModule, SupplierForm],
  templateUrl: './supplier-list.html',
  styleUrl: './supplier-list.css'
})
export class SupplierList implements OnInit {
  private supplierService = inject(ProveedorService);

  allSuppliers = signal<Proveedor[]>([]);
  searchTerm = signal<string>('');
  isModalOpen = signal(false);
  editingSupplier = signal<Proveedor | null>(null);

  ngOnInit(): void { this.loadSuppliers(); }

  loadSuppliers(): void {
    this.supplierService.getAll().subscribe(data => this.allSuppliers.set(data));
  }

  filteredSuppliers = computed(() => {
    const term = this.searchTerm().toLowerCase();
    return this.allSuppliers().filter(s => 
      s.razonSocial.toLowerCase().includes(term) || s.rut?.toLowerCase().includes(term)
    );
  });

  openModal(supplier?: Proveedor): void {
    this.editingSupplier.set(supplier ?? null);
    this.isModalOpen.set(true);
  }

  toggleActive(supplier: Proveedor): void {
    this.supplierService.toggleActivo(supplier.id).subscribe(() => this.loadSuppliers());
  }

  deleteSupplier(id: number): void {
    Swal.fire({
      title: '¿Eliminar proveedor?',
      text: 'Esta acción no se puede deshacer',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.supplierService.delete(id).subscribe({
          next: () => {
            this.loadSuppliers();
            Swal.fire('Eliminado', 'El proveedor ha sido quitado.', 'success');
          },
          error: () => Swal.fire('Error', 'No se pudo eliminar', 'error')
        });
      }
    });
  }
}
