import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UbicacionService } from '../../../core/services/ubicacion.service';
import { TipoUbicacionService } from '../../../core/services/tipo-ubicacion.service';
import { Ubicacion, UbicacionCreate, UbicacionUpdate } from '../../../core/models/ubicacion.model';
import { TipoUbicacion } from '../../../core/models/tipo-ubicacion.model';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Component({
  selector: 'app-location-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './location-list.html',
  styleUrl: './location-list.css'
})
export class LocationList implements OnInit {
  private ubiService = inject(UbicacionService);
  private tipoService = inject(TipoUbicacionService);

  // Signals para estado reactivo
  ubicaciones = signal<Ubicacion[]>([]);
  tiposActivos = signal<TipoUbicacion[]>([]);

  ngOnInit(): void {
    this.loadUbicaciones();
    this.loadTiposActivos();
  }

  loadUbicaciones(): void {
    this.ubiService.getAll().subscribe({
      next: (data) => this.ubicaciones.set(data),
      error: (err) => console.error('Error al cargar ubicaciones:', err)
    });
  }

  loadTiposActivos(): void {
    this.tipoService.getActivos().subscribe({
      next: (data) => this.tiposActivos.set(data),
      error: (err) => console.error('Error al cargar tipos de ubicación:', err)
    });
  }

  toggleStatus(ubi: Ubicacion): void {
    this.ubiService.toggleActivo(ubi.id).subscribe({
      next: () => this.loadUbicaciones(),
      error: () => {
        Swal.fire('Error', 'No se pudo cambiar el estado', 'error');
        this.loadUbicaciones();
      }
    });
  }

  async openModal(ubi?: Ubicacion) {
    const optionsHtml = this.tiposActivos()
      .map(t => `<option value="${t.id}" ${ubi?.tipoUbicacionId === t.id ? 'selected' : ''}>${t.nombre}</option>`)
      .join('');

    const { value: formValues } = await Swal.fire({
      title: ubi ? 'Editar Ubicación' : 'Nueva Ubicación',
      html: `
        <div class="text-start">
          <label class="form-label fw-bold small text-muted">Tipo de Espacio</label>
          <select id="swal-tipoId" class="form-select mb-3">
            <option value="">Seleccione tipo...</option>
            ${optionsHtml}
          </select>

          <label class="form-label fw-bold small text-muted">Nombre/Código de Ubicación</label>
          <input id="swal-name" class="form-control mb-3" placeholder="Ej: Pasillo A-1" value="${ubi?.nombre || ''}">
          
          <label class="form-label fw-bold small text-muted">Descripción / Notas</label>
          <textarea id="swal-desc" class="form-control" rows="3" placeholder="Opcional">${ubi?.descripcion || ''}</textarea>
        </div>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Guardar',
      cancelButtonText: 'Cancelar',
      preConfirm: () => {
        const tipoUbicacionId = (document.getElementById('swal-tipoId') as HTMLSelectElement).value;
        const nombre = (document.getElementById('swal-name') as HTMLInputElement).value.trim();
        const descripcion = (document.getElementById('swal-desc') as HTMLTextAreaElement).value.trim();

        if (!tipoUbicacionId) return Swal.showValidationMessage('Debe seleccionar un tipo');
        if (!nombre) return Swal.showValidationMessage('El nombre es obligatorio');

        return { tipoUbicacionId: Number(tipoUbicacionId), nombre, descripcion };
      }
    });

    if (formValues) {
      if (ubi) {
        const updateData: UbicacionUpdate = { ...formValues, activo: ubi.activo };
        this.ubiService.update(ubi.id, updateData).subscribe(() => this.onSuccess());
      } else {
        const createData: UbicacionCreate = formValues;
        this.ubiService.create(createData).subscribe(() => this.onSuccess());
      }
    }
  }

  deleteUbicacion(id: number): void {
    Swal.fire({
      title: '¿Eliminar ubicación?',
      text: "Asegúrese de que no existan productos asignados aquí.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Eliminar'
    }).then((result: SweetAlertResult) => {
      if (result.isConfirmed) {
        this.ubiService.delete(id).subscribe(() => this.onSuccess());
      }
    });
  }

  private onSuccess(): void {
    this.loadUbicaciones();
    Swal.fire({ icon: 'success', title: 'Operación exitosa', timer: 1500, showConfirmButton: false });
  }
}
