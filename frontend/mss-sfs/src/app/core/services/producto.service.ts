import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  Producto, 
  ProductoListItem, 
  ProductoStock, 
  ProductoRequest // <--- Interfaz unificada
} from '../models/producto.model';
import { environment } from '../../../environment/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly apiUrl = `${environment.apiUrl}/api/productos`;

  user = this.authService.getUser();

  // ==================== LISTADOS ====================

  getAll(): Observable<ProductoListItem[]> {
    return this.http.get<ProductoListItem[]>(this.apiUrl);
  }

  getByCategoria(categoriaId: number): Observable<ProductoListItem[]> {
    return this.http.get<ProductoListItem[]>(`${this.apiUrl}/categoria/${categoriaId}`);
  }

  getByProveedor(proveedorId: number): Observable<ProductoListItem[]> {
    return this.http.get<ProductoListItem[]>(`${this.apiUrl}/proveedor/${proveedorId}`);
  }

  // ==================== DETALLE ====================

  getById(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/${id}`);
  }

  getByCodigoInterno(codigoInterno: string): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/codigo/${codigoInterno}`);
  }

  // ==================== ACCIONES (CUD) ====================

  // Ahora acepta ProductoRequest para mayor flexibilidad
  create(producto: ProductoRequest): Observable<Producto> {
    const payload: ProductoRequest = {
      ...producto,
      creadoPor: this.user?.id
    };
    return this.http.post<Producto>(this.apiUrl, payload);
  }

  // Ahora acepta ProductoRequest para mayor flexibilidad
  update(id: number, producto: ProductoRequest): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiUrl}/${id}`, producto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleActivo(id: number): Observable<Producto> {
    return this.http.patch<Producto>(`${this.apiUrl}/${id}/toggle-activo`, {});
  }

  // ==================== STOCK Y STATS ====================

  getStockBajo(): Observable<ProductoStock[]> {
    return this.http.get<ProductoStock[]>(`${this.apiUrl}/stock/bajo`);
  }

  getStockExceso(): Observable<ProductoStock[]> {
    return this.http.get<ProductoStock[]>(`${this.apiUrl}/stock/exceso`);
  }

  getTotalActivos(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/stats/total-activos`);
  }
}
