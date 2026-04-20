import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TipoUbicacion, TipoUbicacionCreate, TipoUbicacionUpdate } from '../models/tipo-ubicacion.model';
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class TipoUbicacionService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/tipo-ubicacion`;

  getAll(): Observable<TipoUbicacion[]> {
    return this.http.get<TipoUbicacion[]>(this.apiUrl);
  }

  getActivos(): Observable<TipoUbicacion[]> {
    return this.http.get<TipoUbicacion[]>(`${this.apiUrl}/activos`);
  }

  getById(id: number): Observable<TipoUbicacion> {
    return this.http.get<TipoUbicacion>(`${this.apiUrl}/${id}`);
  }

  create(data: TipoUbicacionCreate): Observable<TipoUbicacion> {
    return this.http.post<TipoUbicacion>(this.apiUrl, data);
  }

  update(id: number, data: TipoUbicacionUpdate): Observable<TipoUbicacion> {
    return this.http.put<TipoUbicacion>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleActivo(id: number): Observable<TipoUbicacion> {
    return this.http.patch<TipoUbicacion>(`${this.apiUrl}/${id}/toggle-activo`, {});
  }
}
