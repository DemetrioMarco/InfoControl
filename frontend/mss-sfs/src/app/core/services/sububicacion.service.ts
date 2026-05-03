import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environment/environment';
import { SubUbicacion, SubUbicacionCreate, SubUbicacionUpdate } from '../models/sububicacion.model';

@Injectable({
  providedIn: 'root'
})
export class SubUbicacionService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/sububicaciones`;

  getAll(): Observable<SubUbicacion[]> {
    return this.http.get<SubUbicacion[]>(this.apiUrl);
  }

  getActivos(): Observable<SubUbicacion[]> {
    return this.http.get<SubUbicacion[]>(`${this.apiUrl}/activos`);
  }

  getByUbicacionActivos(ubicacionId: number): Observable<SubUbicacion[]> {
    return this.http.get<SubUbicacion[]>(`${this.apiUrl}/by-ubicacion/${ubicacionId}/activos`);
  }

  getById(id: number): Observable<SubUbicacion> {
    return this.http.get<SubUbicacion>(`${this.apiUrl}/${id}`);
  }

  create(data: SubUbicacionCreate): Observable<SubUbicacion> {
    return this.http.post<SubUbicacion>(this.apiUrl, data);
  }

  update(id: number, data: SubUbicacionUpdate): Observable<SubUbicacion> {
    return this.http.put<SubUbicacion>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleActivo(id: number): Observable<SubUbicacion> {
    return this.http.patch<SubUbicacion>(`${this.apiUrl}/${id}/toggle-activo`, {});
  }
}
