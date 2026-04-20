import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UnidadMedidaService } from '../../../core/services/unidad-medida.service';
import { UnidadMedida, UnidadMedidaCreate, UnidadMedidaUpdate } from '../../../core/models/unidad-medida.model';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Component({
  selector: 'app-unit-measurement-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './unit-measurement-list.html',
  styleUrl: './unit-measurement-list.css'
})
export class UnitMeasurementList implements OnInit {
  private unitService = inject(UnidadMedidaService);
  unidades: UnidadMedida[] = [];

  ngOnInit(): void {
    this.loadUnidades();
  }

  loadUnidades(): void {
    this.unitService.getAll().subscribe(data => this.unidades = data);
  }

  toggleStatus(unit: UnidadMedida): void {
    this.unitService.toggleActivo(unit.id).subscribe({
      next: () => this.loadUnidades(),
      error: () => {
        Swal.fire('Error', 'No se pudo cambiar el estado', 'error');
        this.loadUnidades();
      }
    });
  }

  async openModal(unit?: UnidadMedida) {
    const { value: formValues } = await Swal.fire({
      title: unit ? 'Editar Unidad de Medida' : 'Nueva Unidad de Medida',
      html: `
        <div class="text-start">
          <label class="form-label fw-bold">Código (Abreviatura)</label>
          <input id="swal-code" class="form-control mb-3 text-uppercase" placeholder="Ej: KG, UND, MTS" value="${unit?.codigo || ''}">
          
          <label class="form-label fw-bold">Nombre</label>
          <input id="swal-name" class="form-control mb-3" placeholder="Ej: Kilogramos" value="${unit?.nombre || ''}">
          
          <label class="form-label fw-bold">Descripción</label>
          <textarea id="swal-desc" class="form-control" rows="3" placeholder="Opcional">${unit?.descripcion || ''}</textarea>
        </div>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Guardar',
      cancelButtonText: 'Cancelar',
      preConfirm: () => {
        const codigo = (document.getElementById('swal-code') as HTMLInputElement).value.trim().toUpperCase();
        const nombre = (document.getElementById('swal-name') as HTMLInputElement).value.trim();
        const descripcion = (document.getElementById('swal-desc') as HTMLTextAreaElement).value.trim();
        
        if (!codigo) return Swal.showValidationMessage('El código es obligatorio');
        if (!nombre) return Swal.showValidationMessage('El nombre es obligatorio');
        
        return { codigo, nombre, descripcion };
      }
    });

    if (formValues) {
      if (unit) {
        const updateData: UnidadMedidaUpdate = { ...formValues, activo: unit.activo };
        this.unitService.update(unit.id, updateData).subscribe(() => this.onSuccess());
      } else {
        const createData: UnidadMedidaCreate = formValues;
        this.unitService.create(createData).subscribe(() => this.onSuccess());
      }
    }
  }

  deleteUnidad(id: number): void {
    Swal.fire({
      title: '¿Eliminar unidad de medida?',
      text: "Esta acción podría afectar a productos existentes.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar'
    }).then((result: SweetAlertResult) => {
      if (result.isConfirmed) {
        this.unitService.delete(id).subscribe(() => this.onSuccess());
      }
    });
  }

  private onSuccess(): void {
    this.loadUnidades();
    Swal.fire({ icon: 'success', title: 'Completado', timer: 1500, showConfirmButton: false });
  }
}
