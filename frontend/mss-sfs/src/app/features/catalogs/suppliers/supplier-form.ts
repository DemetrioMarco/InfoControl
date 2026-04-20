import { Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProveedorService } from '../../../core/services/proveedor.service';
import { Proveedor } from '../../../core/models/proveedor.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-supplier-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './supplier-form.html',
  styleUrl: './supplier-form.css'
})
export class SupplierForm implements OnInit, OnChanges {
  @Input() isOpen = false;
  @Input() editingSupplier: Proveedor | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  private readonly supplierService = inject(ProveedorService);

  form!: FormGroup;
  isSubmitting = signal(false);

  ngOnInit(): void {
    this.initForm();
  }

  ngOnChanges(): void {
    if (this.isOpen && this.editingSupplier) {
      this.form?.patchValue(this.editingSupplier);
    } else if (this.isOpen) {
      this.form?.reset({ activo: true, pais: 'Chile' });
    }
  }

  private initForm(): void {
    this.form = this.fb.group({
      rut: [''],
      razonSocial: ['', [Validators.required]],
      nombreFantasia: [''],
      giro: [''],
      contactoNombre: [''],
      contactoTelefono: [''],
      contactoEmail: ['', [Validators.email]],
      direccion: [''],
      comuna: [''],
      ciudad: [''],
      pais: ['Chile'],
      observaciones: [''],
      activo: [true]
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.isSubmitting.set(true);
    const supplierData = this.form.getRawValue();

    const request = this.editingSupplier 
      ? this.supplierService.update(this.editingSupplier.id, supplierData)
      : this.supplierService.create(supplierData);

    request.subscribe({
      next: () => {
        this.isSubmitting.set(false);
        Swal.fire({ icon: 'success', title: 'Guardado', text: 'Proveedor procesado correctamente', timer: 1500, showConfirmButton: false });
        this.saved.emit();
        this.closeModal();
      },
      error: (err) => {
        this.isSubmitting.set(false);
        Swal.fire('Error', err.error?.message || 'No se pudo guardar el proveedor', 'error');
      }
    });
  }

  closeModal(): void {
    this.form.reset();
    this.close.emit();
  }
}
