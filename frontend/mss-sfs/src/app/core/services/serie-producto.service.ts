import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { SerieProductoRequest, SerieProductoResponse } from '../models/serie-producto.model';

@Injectable({
  providedIn: 'root'
})
export class SerieProductoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/api/series-producto`;

  getByProductoIdAndSubUbicacionId(
    productoId: number,
    subUbicacionId: number
  ): Observable<SerieProductoResponse[]> {
    return this.http.get<SerieProductoResponse[]>(
      `${this.apiUrl}/by-producto/${productoId}/sububicacion/${subUbicacionId}`
    );
  }

  create(request: SerieProductoRequest): Observable<SerieProductoResponse> {
    return this.http.post<SerieProductoResponse>(this.apiUrl, request);
  }

  update(id: number, request: SerieProductoRequest): Observable<SerieProductoResponse> {
    return this.http.put<SerieProductoResponse>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
