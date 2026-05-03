import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import Swal, { SweetAlertResult } from 'sweetalert2';

import { SubUbicacionService } from '../../../core/services/sububicacion.service';
import { UbicacionService } from '../../../core/services/ubicacion.service';
import { SubUbicacion, SubUbicacionCreate, SubUbicacionUpdate } from '../../../core/models/sububicacion.model';
import { Ubicacion } from '../../../core/models/ubicacion.model';

@Component({
  selector: 'app-sub-ubicacion-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sub-ubicacion-list.html',
  styleUrl: './sub-ubicacion-list.css'
})
export class SubUbicacionList implements OnInit {
  private subUbiService = inject(SubUbicacionService);
  private ubiService = inject(UbicacionService);

  // Signals para estado reactivo
  subUbicaciones = signal<SubUbicacion[]>([]);
  ubicacionesActivas = signal<Ubicacion[]>([]);

  ngOnInit(): void {
    this.loadSubUbicaciones();
    this.loadUbicacionesActivas();
  }

  loadSubUbicaciones(): void {
    this.subUbiService.getAll().subscribe({
      next: (data) => this.subUbicaciones.set(data),
      error: (err) => console.error('Error al cargar sub-ubicaciones', err)
    });
  }

  loadUbicacionesActivas(): void {
    this.ubiService.getActivos().subscribe({
      next: (data) => this.ubicacionesActivas.set(data),
      error: (err) => console.error('Error al cargar ubicaciones activas', err)
    });
  }

  toggleStatus(sub: SubUbicacion): void {
    this.subUbiService.toggleActivo(sub.id).subscribe({
      next: () => this.loadSubUbicaciones(),
      error: () => {
        Swal.fire('Error', 'No se pudo cambiar el estado', 'error');
        this.loadSubUbicaciones();
      }
    });
  }

  async openModal(sub?: SubUbicacion) {
    // Generar opciones del select desde el signal de ubicaciones
    const optionsHtml = this.ubicacionesActivas()
      .map(u => `<option value="${u.id}" ${sub?.ubicacionId === u.id ? 'selected' : ''}>${u.nombre}</option>`)
      .join('');

    const { value: formValues } = await Swal.fire({
      title: sub ? 'Editar Sub-ubicación' : 'Nueva Sub-ubicación',
      html: `
        <div class="text-start">
          <label class="form-label fw-bold small text-muted">Ubicación Principal</label>
          <select id="swal-ubiId" class="form-select mb-3">
            <option value="">Seleccione una ubicación...</option>
            ${optionsHtml}
          </select>

          <label class="form-label fw-bold small text-muted">Nombre de Sub-ubicación</label>
          <input id="swal-name" class="form-control mb-3" placeholder="Ej: Pasillo A, Estante 3" value="${sub?.nombre || ''}">
          
          <label class="form-label fw-bold small text-muted">Descripción</label>
          <textarea id="swal-desc" class="form-control" rows="3" placeholder="Opcional">${sub?.descripcion || ''}</textarea>
        </div>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Guardar',
      cancelButtonText: 'Cancelar',
      preConfirm: () => {
        const ubicacionId = (document.getElementById('swal-ubiId') as HTMLSelectElement).value;
        const nombre = (document.getElementById('swal-name') as HTMLInputElement).value.trim();
        const descripcion = (document.getElementById('swal-desc') as HTMLTextAreaElement).value.trim();

        if (!ubicacionId) return Swal.showValidationMessage('Debe seleccionar una ubicación principal');
        if (!nombre) return Swal.showValidationMessage('El nombre es obligatorio');

        return { ubicacionId: Number(ubicacionId), nombre, descripcion };
      }
    });

    if (formValues) {
      if (sub) {
        const updateData: SubUbicacionUpdate = { ...formValues, activo: sub.activo };
        this.subUbiService.update(sub.id, updateData).subscribe(() => this.onSuccess());
      } else {
        const createData: SubUbicacionCreate = formValues;
        console.info("enviando: {}", createData);
        this.subUbiService.create(createData).subscribe(() => this.onSuccess());
      }
    }
  }

  deleteSubUbicacion(id: number): void {
    Swal.fire({
      title: '¿Eliminar sub-ubicación?',
      text: "Esta acción no se puede deshacer.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar'
    }).then((result: SweetAlertResult) => {
      if (result.isConfirmed) {
        this.subUbiService.delete(id).subscribe(() => this.onSuccess());
      }
    });
  }

  private onSuccess(): void {
    this.loadSubUbicaciones();
    Swal.fire({ icon: 'success', title: 'Completado', timer: 1500, showConfirmButton: false });
  }
}
