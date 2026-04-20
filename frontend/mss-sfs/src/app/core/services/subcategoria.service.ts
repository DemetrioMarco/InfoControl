import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Subcategoria, SubcategoriaCreate, SubcategoriaUpdate } from '../models/subcategoria.model';
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class SubcategoriaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/subcategorias`;

  getAll(): Observable<Subcategoria[]> {
    return this.http.get<Subcategoria[]>(this.apiUrl);
  }

  getActivos(): Observable<Subcategoria[]> {
    return this.http.get<Subcategoria[]>(`${this.apiUrl}/activos`);
  }

  getByCategoriaActivos(categoriaId: number): Observable<Subcategoria[]> {
    return this.http.get<Subcategoria[]>(`${this.apiUrl}/by-categoria/${categoriaId}/activos`);
  }

  getById(id: number): Observable<Subcategoria> {
    return this.http.get<Subcategoria>(`${this.apiUrl}/${id}`);
  }

  create(data: SubcategoriaCreate): Observable<Subcategoria> {
    return this.http.post<Subcategoria>(this.apiUrl, data);
  }

  update(id: number, data: SubcategoriaUpdate): Observable<Subcategoria> {
    return this.http.put<Subcategoria>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleActivo(id: number): Observable<Subcategoria> {
    return this.http.patch<Subcategoria>(`${this.apiUrl}/${id}/toggle-activo`, {});
  }
}
