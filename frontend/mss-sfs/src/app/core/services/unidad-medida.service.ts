import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UnidadMedida, UnidadMedidaCreate, UnidadMedidaUpdate } from '../models/unidad-medida.model';
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class UnidadMedidaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/unidad-medida`;

  getAll(): Observable<UnidadMedida[]> {
    return this.http.get<UnidadMedida[]>(this.apiUrl);
  }

  getActivos(): Observable<UnidadMedida[]> {
    return this.http.get<UnidadMedida[]>(`${this.apiUrl}/activos`);
  }

  buscar(busqueda: string): Observable<UnidadMedida[]> {
    const params = new HttpParams().set('busqueda', busqueda);
    return this.http.get<UnidadMedida[]>(`${this.apiUrl}/buscar`, { params });
  }

  getById(id: number): Observable<UnidadMedida> {
    return this.http.get<UnidadMedida>(`${this.apiUrl}/${id}`);
  }

  create(data: UnidadMedidaCreate): Observable<UnidadMedida> {
    return this.http.post<UnidadMedida>(this.apiUrl, data);
  }

  update(id: number, data: UnidadMedidaUpdate): Observable<UnidadMedida> {
    return this.http.put<UnidadMedida>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleActivo(id: number): Observable<UnidadMedida> {
    return this.http.patch<UnidadMedida>(`${this.apiUrl}/${id}/toggle-activo`, {});
  }
}
