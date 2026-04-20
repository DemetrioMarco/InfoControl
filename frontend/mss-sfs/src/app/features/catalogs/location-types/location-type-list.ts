import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TipoUbicacionService } from '../../../core/services/tipo-ubicacion.service';
import { TipoUbicacion, TipoUbicacionCreate, TipoUbicacionUpdate } from '../../../core/models/tipo-ubicacion.model';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Component({
  selector: 'app-location-type-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './location-type-list.html',
  styleUrl: './location-type-list.css'
})
export class LocationTypeList implements OnInit {
  private tipoService = inject(TipoUbicacionService);
  tiposUbicacion = signal<TipoUbicacion[]>([]);

  ngOnInit(): void {
    this.loadTipos();
  }

  loadTipos(): void {
    this.tipoService.getAll().subscribe({
      next: (data) => this.tiposUbicacion.set(data),
      error: (err) => console.error('Error al cargar tipos:', err)
    });
  }

  toggleStatus(tipo: TipoUbicacion): void {
    this.tipoService.toggleActivo(tipo.id).subscribe({
      next: () => this.loadTipos(),
      error: () => {
        Swal.fire('Error', 'No se pudo actualizar el estado', 'error');
        this.loadTipos();
      }
    });
  }

  async openModal(tipo?: TipoUbicacion) {
    const { value: formValues } = await Swal.fire({
      title: tipo ? 'Editar Tipo de Ubicación' : 'Nuevo Tipo de Ubicación',
      html: `
        <div class="text-start">
          <div class="row">
            <div class="col-4">
              <label class="form-label fw-bold small text-muted">Código</label>
              <input id="swal-codigo" class="form-control mb-3" placeholder="Ej: ALM" value="${tipo?.codigo || ''}">
            </div>
            <div class="col-8">
              <label class="form-label fw-bold small text-muted">Nombre</label>
              <input id="swal-name" class="form-control mb-3" placeholder="Ej: Almacén Principal" value="${tipo?.nombre || ''}">
            </div>
          </div>
          
          <label class="form-label fw-bold small text-muted">Descripción</label>
          <textarea id="swal-desc" class="form-control" rows="3" placeholder="Opcional">${tipo?.descripcion || ''}</textarea>
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
      if (tipo) {
        const updateData: TipoUbicacionUpdate = { 
          codigo: formValues.codigo,
          nombre: formValues.nombre, 
          descripcion: formValues.descripcion,
          activo: tipo.activo 
        };
        this.tipoService.update(tipo.id, updateData).subscribe(() => this.onSuccess());
      } else {
        const createData: TipoUbicacionCreate = formValues;
        this.tipoService.create(createData).subscribe(() => this.onSuccess());
      }
    }
  }

  deleteTipo(id: number): void {
    Swal.fire({
      title: '¿Eliminar tipo?',
      text: "Esta acción no se puede deshacer.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Eliminar'
    }).then((result: SweetAlertResult) => {
      if (result.isConfirmed) {
        this.tipoService.delete(id).subscribe(() => this.onSuccess());
      }
    });
  }

  private onSuccess(): void {
    this.loadTipos();
    Swal.fire({ icon: 'success', title: 'Completado', timer: 1500, showConfirmButton: false });
  }
}
