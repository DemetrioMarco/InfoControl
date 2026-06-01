// services/toma-inventario.service.ts
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { RegistrarConteoRequest, TomaInventarioDetail } from '../models/intentory.model';


@Injectable({ providedIn: 'root' })
export class TomaInventarioService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/tomas-inventario`;

  // Para listar tomas que fueron programadas y no han terminado
  getPendientes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`);
  }

  // Retorna el detalle completo tras crear
  crear(request: any): Observable<TomaInventarioDetail> {
    return this.http.post<TomaInventarioDetail>(this.apiUrl, request);
  }

  // Recibe el ID y el objeto RegistrarConteoRequest
  registrarConteo(id: number, request: RegistrarConteoRequest): Observable<TomaInventarioDetail> {
    return this.http.patch<TomaInventarioDetail>(`${this.apiUrl}/${id}/registrar-conteo`, request);
  }

  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  actualizar(id: number, request: any): Observable<any> {
    console.log(id);
    console.log(request);
    return this.http.put(`${this.apiUrl}/${id}`, request);
  }
}
