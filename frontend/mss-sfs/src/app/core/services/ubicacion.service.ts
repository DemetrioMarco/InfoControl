import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ubicacion, UbicacionCreate, UbicacionUpdate } from '../models/ubicacion.model';
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class UbicacionService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/ubicaciones`;

  getAll(): Observable<Ubicacion[]> {
    return this.http.get<Ubicacion[]>(this.apiUrl);
  }

  getActivos(): Observable<Ubicacion[]> {
    return this.http.get<Ubicacion[]>(`${this.apiUrl}/activos`);
  }

  getByTipoActivos(tipoUbicacionId: number): Observable<Ubicacion[]> {
    return this.http.get<Ubicacion[]>(`${this.apiUrl}/por-tipo/${tipoUbicacionId}`);
  }

  getById(id: number): Observable<Ubicacion> {
    return this.http.get<Ubicacion>(`${this.apiUrl}/${id}`);
  }

  create(data: UbicacionCreate): Observable<Ubicacion> {
    return this.http.post<Ubicacion>(this.apiUrl, data);
  }

  update(id: number, data: UbicacionUpdate): Observable<Ubicacion> {
    return this.http.put<Ubicacion>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleActivo(id: number): Observable<Ubicacion> {
    return this.http.patch<Ubicacion>(`${this.apiUrl}/${id}/toggle-activo`, {});
  }
}
