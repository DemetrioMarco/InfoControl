import { Component, OnInit, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ProductoService } from '../../../../core/services/producto.service';
import { CategoriaService } from '../../../../core/services/categoria.service';
import { SubcategoriaService } from '../../../../core/services/subcategoria.service';
import { ProveedorService } from '../../../../core/services/proveedor.service';
import { UnidadMedidaService } from '../../../../core/services/unidad-medida.service';
import { Producto } from '../../../../core/models/producto.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-producto-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './producto-form.html',
  styleUrls: ['./producto-form.css']
})
export class ProductoForm implements OnInit {
  private fb = inject(FormBuilder);
  private productoService = inject(ProductoService);
  private catService = inject(CategoriaService);
  private subService = inject(SubcategoriaService);
  private provService = inject(ProveedorService);
  private uniService = inject(UnidadMedidaService);

  id = input<number | null>(null);
  onClose = output<boolean>();

  // Señales para los catálogos
  categorias = signal<any[]>([]);
  subcategorias = signal<any[]>([]);
  subcategoriasFiltradas = signal<any[]>([]);
  proveedores = signal<any[]>([]);
  unidades = signal<any[]>([]);

  form: FormGroup = this.fb.group({
    nombre: ['', [Validators.required]],
    codigoInterno: ['', [Validators.required]],
    descripcion: [''],
    precioUnitario: [0, [Validators.required, Validators.min(0)]],
    stockMinimo: [0, [Validators.required, Validators.min(0)]],
    stockMaximo: [0, [Validators.required, Validators.min(0)]],
    stockActual: [0, [Validators.required, Validators.min(0)]],
    categoriaId: [null, [Validators.required]],
    subcategoriaId: [null, [Validators.required]], // Ahora es requerido
    proveedorId: [null, [Validators.required]],
    unidadMedidaId: [null, [Validators.required]]
  });

  isEdit = signal(false);
  loading = signal(false);

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  private cargarCatalogos(): void {
    this.loading.set(true);
    forkJoin({
      cats: this.catService.getAll(),
      subs: this.subService.getAll(),
      provs: this.provService.getAll(),
      unis: this.uniService.getActivos()
    }).subscribe({
      next: (res) => {
        this.categorias.set(res.cats);
        this.subcategorias.set(res.subs);
        this.proveedores.set(res.provs);
        this.unidades.set(res.unis);
        
        if (this.id()) {
          this.isEdit.set(true);
          this.loadProducto(this.id()!);
        }
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  loadProducto(id: number): void {
    this.productoService.getById(id).subscribe({
      next: (prod: Producto) => {
        this.form.patchValue(prod);
        this.filtrarSubcategorias(prod.categoriaId);
      },
      error: () => this.close(false)
    });
  }

  onCategoriaChange(): void {
    const catId = this.form.get('categoriaId')?.value;
    this.form.get('subcategoriaId')?.setValue(null);
    this.filtrarSubcategorias(catId);
  }

  private filtrarSubcategorias(catId: any): void {
    if (!catId) {
      this.subcategoriasFiltradas.set([]);
      return;
    }
    const filtradas = this.subcategorias().filter(s => s.categoriaId == catId);
    this.subcategoriasFiltradas.set(filtradas);
  }

  save(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    const request = this.form.value;
    const action = this.isEdit() 
      ? this.productoService.update(this.id()!, request)
      : this.productoService.create(request);

    action.subscribe({
      next: () => {
        Swal.fire('Éxito', `Producto ${this.isEdit() ? 'actualizado' : 'creado'}`, 'success');
        this.close(true);
      },
      error: () => this.loading.set(false)
    });
  }

  close(refresh: boolean = false): void {
    this.onClose.emit(refresh);
  }
}
