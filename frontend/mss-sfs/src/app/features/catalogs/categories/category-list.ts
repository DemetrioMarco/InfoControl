import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoriaService } from '../../../core/services/categoria.service';
import { Categoria, CategoriaCreate, CategoriaUpdate } from '../../../core/models/categoria.model';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './category-list.html',
  styleUrl: './category-list.css'
})
export class CategoryList implements OnInit {
  private catService = inject(CategoriaService);
  
  // Signal para manejar el estado de la lista
  categorias = signal<Categoria[]>([]);

  ngOnInit(): void {
    this.loadCategorias();
  }

  loadCategorias(): void {
    this.catService.getAll().subscribe({
      next: (data) => this.categorias.set(data),
      error: (err) => console.error('Error al cargar categorías', err)
    });
  }

  toggleStatus(cat: Categoria): void {
    this.catService.toggleActivo(cat.id).subscribe({
      next: () => this.loadCategorias(),
      error: () => {
        Swal.fire('Error', 'No se pudo cambiar el estado', 'error');
        this.loadCategorias();
      }
    });
  }

  async openModal(cat?: Categoria) {
    const { value: formValues } = await Swal.fire({
      title: cat ? 'Editar Categoría' : 'Nueva Categoría',
      html: `
        <div class="text-start">
          <label class="form-label fw-bold">Nombre</label>
          <input id="swal-name" class="form-control mb-3" placeholder="Ej: Insumos Médicos" value="${cat?.nombre || ''}">
          <label class="form-label fw-bold">Descripción</label>
          <textarea id="swal-desc" class="form-control" rows="3" placeholder="Opcional">${cat?.descripcion || ''}</textarea>
        </div>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Guardar',
      cancelButtonText: 'Cancelar',
      preConfirm: () => {
        const nombre = (document.getElementById('swal-name') as HTMLInputElement).value.trim();
        const descripcion = (document.getElementById('swal-desc') as HTMLTextAreaElement).value.trim();
        if (!nombre) return Swal.showValidationMessage('El nombre es obligatorio');
        return { nombre, descripcion };
      }
    });

    if (formValues) {
      if (cat) {
        const updateData: CategoriaUpdate = { ...formValues, activo: cat.activo };
        this.catService.update(cat.id, updateData).subscribe(() => this.onSuccess());
      } else {
        const createData: CategoriaCreate = formValues;
        this.catService.create(createData).subscribe(() => this.onSuccess());
      }
    }
  }

  deleteCategoria(id: number): void {
    Swal.fire({
      title: '¿Eliminar categoría?',
      text: "Se eliminarán también las subcategorías asociadas.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar'
    }).then((result: SweetAlertResult) => {
      if (result.isConfirmed) {
        this.catService.delete(id).subscribe(() => this.onSuccess());
      }
    });
  }

  private onSuccess(): void {
    this.loadCategorias();
    Swal.fire({ icon: 'success', title: 'Operación exitosa', timer: 1500, showConfirmButton: false });
  }
}
