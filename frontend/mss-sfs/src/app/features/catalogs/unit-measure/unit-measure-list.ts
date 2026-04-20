import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UnidadMedidaService } from '../../../core/services/unidad-medida.service';
import { UnidadMedida, UnidadMedidaCreate, UnidadMedidaUpdate } from '../../../core/models/unidad-medida.model';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Component({
  selector: 'app-unit-measure-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './unit-measure-list.html',
  styleUrl: './unit-measure-list.css'
})
export class UnitMeasureList implements OnInit {
  private unidadService = inject(UnidadMedidaService);
  unidades = signal<UnidadMedida[]>([]);

  ngOnInit(): void {
    this.loadUnidades();
  }

  loadUnidades(): void {
    this.unidadService.getAll().subscribe({
      next: (data) => this.unidades.set(data),
      error: (err) => console.error('Error al cargar unidades:', err)
    });
  }

  toggleStatus(unidad: UnidadMedida): void {
    this.unidadService.toggleActivo(unidad.id).subscribe({
      next: () => this.loadUnidades(),
      error: () => {
        Swal.fire('Error', 'No se pudo cambiar el estado', 'error');
        this.loadUnidades();
      }
    });
  }

  async openModal(unidad?: UnidadMedida) {
    const { value: formValues } = await Swal.fire({
      title: unidad ? 'Editar Unidad de Medida' : 'Nueva Unidad de Medida',
      html: `
        <div class="text-start">
          <div class="row">
            <div class="col-4">
              <label class="form-label fw-bold small text-muted">Código</label>
              <input id="swal-codigo" class="form-control mb-3" placeholder="Ej: KG" value="${unidad?.codigo || ''}">
            </div>
            <div class="col-8">
              <label class="form-label fw-bold small text-muted">Nombre</label>
              <input id="swal-name" class="form-control mb-3" placeholder="Ej: Kilogramos" value="${unidad?.nombre || ''}">
            </div>
          </div>
          
          <label class="form-label fw-bold small text-muted">Descripción</label>
          <textarea id="swal-desc" class="form-control" rows="3" placeholder="Opcional">${unidad?.descripcion || ''}</textarea>
        </div>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Guardar',
      cancelButtonText: 'Cancelar',
      preConfirm: () => {
        const codigo = (document.getElementById('swal-codigo') as HTMLInputElement).value.trim();
        const nombre = (document.getElementById('swal-name') as HTMLInputElement).value.trim();
        const descripcion = (document.getElementById('swal-desc') as HTMLTextAreaElement).value.trim();

        if (!codigo) return Swal.showValidationMessage('El código es obligatorio');
        if (!nombre) return Swal.showValidationMessage('El nombre es obligatorio');

        return { codigo, nombre, descripcion };
      }
    });

    if (formValues) {
      if (unidad) {
        // Objeto para UnidadMedidaUpdate
        const updateData: UnidadMedidaUpdate = { 
          codigo: formValues.codigo,
          nombre: formValues.nombre,
          descripcion: formValues.descripcion,
          activo: unidad.activo 
        };
        this.unidadService.update(unidad.id, updateData).subscribe(() => this.onSuccess());
      } else {
        // Objeto para UnidadMedidaCreate
        const createData: UnidadMedidaCreate = formValues;
        this.unidadService.create(createData).subscribe(() => this.onSuccess());
      }
    }
  }

  deleteUnidad(id: number): void {
    Swal.fire({
      title: '¿Eliminar unidad?',
      text: "Esta acción no se puede deshacer.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Eliminar'
    }).then((result: SweetAlertResult) => {
      if (result.isConfirmed) {
        this.unidadService.delete(id).subscribe(() => this.onSuccess());
      }
    });
  }

  private onSuccess(): void {
    this.loadUnidades();
    Swal.fire({ icon: 'success', title: 'Completado', timer: 1500, showConfirmButton: false });
  }
}
