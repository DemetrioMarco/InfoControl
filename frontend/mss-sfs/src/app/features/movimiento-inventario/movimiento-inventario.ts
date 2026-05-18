import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { MovimientoInventarioService } from '../../core/services/movimiento-inventario.service';
import { ProductoService } from '../../core/services/producto.service';
import { SubUbicacionService } from '../../core/services/sububicacion.service';
import { MovimientoInventarioRequest, TipoMovimiento } from '../../core/models/movimiento-inventario.model';

@Component({
  selector: 'app-movimiento-inventario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './movimiento-inventario.html',
  styleUrl: './movimiento-inventario.css'
})
export class MovimientoInventario implements OnInit {
  private fb = inject(FormBuilder);
  private movimientoService = inject(MovimientoInventarioService);
  private productoService = inject(ProductoService);
  private subUbicacionService = inject(SubUbicacionService);

  // Señales para catálogos
  productos = signal<any[]>([]);
  subUbicaciones = signal<any[]>([]);
  loading = signal(false);

  form: FormGroup = this.fb.group({
    tipoMovimiento: ['ENTRADA', [Validators.required]],
    productoId: [null, [Validators.required]],
    cantidad: [null, [Validators.required, Validators.min(1)]],
    precioUnitario: [null],
    subUbicacionOrigenId: [null],
    subUbicacionDestinoId: [null],
    numeroReferencia: ['', [Validators.required]],
    motivo: ['', [Validators.required]],
    observaciones: ['']
  });

  ngOnInit(): void {
    this.cargarCatalogos();
    this.watchTipoMovimiento();
  }

  private cargarCatalogos(): void {
    this.productoService.getAll().subscribe(data => this.productos.set(data));
    this.subUbicacionService.getAll().subscribe(data => this.subUbicaciones.set(data));
  }

  /**
   * Ajusta las validaciones de Origen/Destino según el tipo de movimiento
   */
  private watchTipoMovimiento(): void {
    this.form.get('tipoMovimiento')?.valueChanges.subscribe((tipo: TipoMovimiento) => {
      const origen = this.form.get('subUbicacionOrigenId');
      const destino = this.form.get('subUbicacionDestinoId');

      // Reset de campos
      origen?.clearValidators();
      destino?.clearValidators();
      origen?.setValue(null);
      destino?.setValue(null);

      if (tipo === 'ENTRADA') {
        destino?.setValidators([Validators.required]);
      } else if (tipo === 'SALIDA') {
        origen?.setValidators([Validators.required]);
      } else if (tipo === 'TRASPASO') {
        origen?.setValidators([Validators.required]);
        destino?.setValidators([Validators.required]);
      }

      origen?.updateValueAndValidity();
      destino?.updateValueAndValidity();
    });
  }

  getUserId(): number {
    const userJson = localStorage.getItem('auth_user');
    if (userJson) {
      const user = JSON.parse(userJson);
      return user.id;
    }
    return 0;
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    // Validación extra para traspasos
    const val = this.form.value;
    if (val.tipoMovimiento === 'TRASPASO' && val.subUbicacionOrigenId === val.subUbicacionDestinoId) {
      Swal.fire('Error', 'El origen y destino no pueden ser iguales', 'error');
      return;
    }

    this.loading.set(true);
    const request: MovimientoInventarioRequest = {
      ...val,
      usuarioResponsableId: this.getUserId()
    };

    this.movimientoService.create(request).subscribe({
      next: (res) => {
        Swal.fire({
          icon: 'success',
          title: 'Registrado',
          text: res.mensaje,
          timer: 2000,
          showConfirmButton: false
        });
        this.form.reset({ tipoMovimiento: 'ENTRADA' });
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        Swal.fire('Error', 'No se pudo registrar el movimiento', 'error');
      }
    });
  }
}
