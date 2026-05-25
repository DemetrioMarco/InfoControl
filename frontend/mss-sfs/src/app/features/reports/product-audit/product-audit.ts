import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoHistorialService } from '../../../core/services/producto-historial.service';
import { ProductoService } from '../../../core/services/producto.service';
import { ProductoHistorial } from '../../../core/models/producto-historial.model';
import { UserService } from '../../../core/services/user.service';
import { catchError, forkJoin, of } from 'rxjs';

@Component({
  selector: 'app-product-audit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-audit.html',
  styleUrls: ['./product-audit.css']
})
export class ProductAudit implements OnInit {
  private historialService = inject(ProductoHistorialService);
  private productoService = inject(ProductoService);
  private usuarioService = inject(UserService);

  historial = signal<ProductoHistorial[]>([]);
  productosDisponibles = signal<any[]>([]);
  nombresUsuarios = signal<Record<string, string>>({}); 
  isLoading = signal(false);
  isAdvanced = signal(false);

  filtros = {
    productoId: '',
    tipoEvento: '',
    fechaInicio: '',
    fechaFin: ''
  };

  ngOnInit() {
    this.productoService.getAll().subscribe(prods => this.productosDisponibles.set(prods));
  }

  toggleAdvanced() {
    this.isAdvanced.set(!this.isAdvanced());
    if (!this.isAdvanced()) {
      this.filtros.tipoEvento = '';
      this.filtros.fechaInicio = '';
      this.filtros.fechaFin = '';
      if (this.filtros.productoId) this.consultar();
    }
  }

  consultar() {
    const id = Number(this.filtros.productoId);
    if (!id) return;

    this.isLoading.set(true);

    // Decidimos qué endpoint usar
    const peticion = this.isAdvanced() 
      ? this.historialService.buscar(this.prepararFiltros()) 
      : this.historialService.getPorProducto(id);

    peticion.subscribe({
      next: (res) => {
        this.historial.set(res);
        this.cargarNombresUsuarios(res);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.historial.set([]);
      }
    });
  }

  private prepararFiltros() {
    const f = { ...this.filtros };
    if (f.fechaInicio) f.fechaInicio += 'T00:00:00';
    if (f.fechaFin) f.fechaFin += 'T23:59:59';
    return f;
  }

  getBadgeClass(tipo: string) {
    if (tipo === 'ENTRADA') return 'badge-entrada';
    if (tipo === 'SALIDA') return 'badge-salida';
    return 'badge-traspaso';
  }

  private cargarNombresUsuarios(data: ProductoHistorial[]) {
    // 1. Extraer IDs únicos como strings
    const idsStrings = [...new Set(data.map(h => h.usuarioResponsable))].filter(id => !!id);
    
    // 2. Filtrar solo los que no tenemos ya en el mapa
    const idsParaConsultar = idsStrings.filter(id => !this.nombresUsuarios()[id]);

    if (idsParaConsultar.length === 0) return;

    // 3. Crear peticiones convirtiendo el string a Number para el servicio
    const peticiones = idsParaConsultar.map(idStr => {
      const idNumerico = Number(idStr); // <--- AQUÍ HACES EL CASTING A NÚMERO
      
      return this.usuarioService.getById(idNumerico).pipe(
        // Retornamos un objeto que mantenga el id original (string) para mapearlo fácil después
        catchError(() => of({ idOriginal: idStr, nombreCompleto: 'Desconocido' }))
      );
    });

    forkJoin(peticiones).subscribe(usuarios => {
      this.nombresUsuarios.update(mapaActual => {
        const nuevoMapa = { ...mapaActual };
        
        usuarios.forEach((u: any) => {
          // Si el servicio devolvió el usuario, usamos su ID original (string) como llave
          // Si el servicio no devuelve el id original, usamos el que ya conocemos
          const llave = u.id?.toString() || u.idOriginal; 
          nuevoMapa[llave] = u.nombreCompleto || u.nombre || 'Usuario';
        });
        
        return nuevoMapa;
      });
    });
  }
}
