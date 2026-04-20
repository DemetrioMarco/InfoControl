import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SubcategoriaService } from '../../../core/services/subcategoria.service';
import { CategoriaService } from '../../../core/services/categoria.service';
import { Subcategoria, SubcategoriaCreate, SubcategoriaUpdate } from '../../../core/models/subcategoria.model';
import { Categoria } from '../../../core/models/categoria.model';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Component({
  selector: 'app-subcategory-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './subcategory-list.html',
  styleUrl: './subcategory-list.css'
})
export class SubcategoryList implements OnInit {
  private subService = inject(SubcategoriaService);
  private catService = inject(CategoriaService);

  // Signals para estado reactivo
  subcategorias = signal<Subcategoria[]>([]);
  categoriasActivas = signal<Categoria[]>([]);

  ngOnInit(): void {
    this.loadSubcategorias();
    this.loadCategoriasActivas();
  }

  loadSubcategorias(): void {
    this.subService.getAll().subscribe({
      next: (data) => this.subcategorias.set(data),
      error: (err) => console.error('Error al cargar subcategorías', err)
    });
  }

  loadCategoriasActivas(): void {
    this.catService.getActivos().subscribe({
      next: (data) => this.categoriasActivas.set(data),
      error: (err) => console.error('Error al cargar categorías activas', err)
    });
  }

  toggleStatus(sub: Subcategoria): void {
    this.subService.toggleActivo(sub.id).subscribe({
      next: () => this.loadSubcategorias(),
      error: () => {
        Swal.fire('Error', 'No se pudo cambiar el estado', 'error');
        this.loadSubcategorias();
      }
    });
  }

  async openModal(sub?: Subcategoria) {
    // Generar opciones del select desde el signal de categorías
    const optionsHtml = this.categoriasActivas()
      .map(c => `<option value="${c.id}" ${sub?.categoriaId === c.id ? 'selected' : ''}>${c.nombre}</option>`)
      .join('');

    const { value: formValues } = await Swal.fire({
      title: sub ? 'Editar Subcategoría' : 'Nueva Subcategoría',
      html: `
        <div class="text-start">
          <label class="form-label fw-bold small text-muted">Categoría Padre</label>
          <select id="swal-catId" class="form-select mb-3">
            <option value="">Seleccione una categoría...</option>
            ${optionsHtml}
          </select>

          <label class="form-label fw-bold small text-muted">Nombre de Subcategoría</label>
          <input id="swal-name" class="form-control mb-3" placeholder="Ej: Jeringas" value="${sub?.nombre || ''}">
          
          <label class="form-label fw-bold small text-muted">Descripción</label>
          <textarea id="swal-desc" class="form-control" rows="3" placeholder="Opcional">${sub?.descripcion || ''}</textarea>
        </div>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Guardar',
      cancelButtonText: 'Cancelar',
      preConfirm: () => {
        const categoriaId = (document.getElementById('swal-catId') as HTMLSelectElement).value;
        const nombre = (document.getElementById('swal-name') as HTMLInputElement).value.trim();
        const descripcion = (document.getElementById('swal-desc') as HTMLTextAreaElement).value.trim();

        if (!categoriaId) return Swal.showValidationMessage('Debe seleccionar una categoría');
        if (!nombre) return Swal.showValidationMessage('El nombre es obligatorio');

        return { categoriaId: Number(categoriaId), nombre, descripcion };
      }
    });

    if (formValues) {
      if (sub) {
        const updateData: SubcategoriaUpdate = { ...formValues, activo: sub.activo };
        this.subService.update(sub.id, updateData).subscribe(() => this.onSuccess());
      } else {
        const createData: SubcategoriaCreate = formValues;
        this.subService.create(createData).subscribe(() => this.onSuccess());
      }
    }
  }

  deleteSub(id: number): void {
    Swal.fire({
      title: '¿Eliminar subcategoría?',
      text: "Esta acción no se puede deshacer.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar'
    }).then((result: SweetAlertResult) => {
      if (result.isConfirmed) {
        this.subService.delete(id).subscribe(() => this.onSuccess());
      }
    });
  }

  private onSuccess(): void {
    this.loadSubcategorias();
    Swal.fire({ icon: 'success', title: 'Completado', timer: 1500, showConfirmButton: false });
  }
}
