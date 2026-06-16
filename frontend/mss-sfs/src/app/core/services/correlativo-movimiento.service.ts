import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { CorrelativoMovimientoPreviewResponse, TipoMovimientoCorrelativo } from '../models/correlativo-movimiento.model';

@Injectable({
  providedIn: 'root'
})
export class CorrelativoMovimientoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/api/correlativos-movimiento`;

  preview(tipoMovimiento: TipoMovimientoCorrelativo): Observable<CorrelativoMovimientoPreviewResponse> {
    return this.http.get<CorrelativoMovimientoPreviewResponse>(`${this.apiUrl}/preview/${tipoMovimiento}`);
  }

  siguiente(tipoMovimiento: TipoMovimientoCorrelativo): Observable<CorrelativoMovimientoPreviewResponse> {
    return this.http.get<CorrelativoMovimientoPreviewResponse>(
      `${this.apiUrl}/siguiente/${tipoMovimiento}`,
      {}
    );
  }

  getAll(): Observable<CorrelativoMovimientoPreviewResponse[]> {
    return this.http.get<CorrelativoMovimientoPreviewResponse[]>(this.apiUrl);
  }

  getById(id: number): Observable<CorrelativoMovimientoPreviewResponse> {
    return this.http.get<CorrelativoMovimientoPreviewResponse>(`${this.apiUrl}/${id}`);
  }
}
