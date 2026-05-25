import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StockPorSubUbicacion } from '../models/stock-report.model';
import { environment } from '../../../environment/environment';


@Injectable({ providedIn: 'root' })
export class ReporteStockService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/stock-ubicacion`;

  getPorSubUbicacion(): Observable<StockPorSubUbicacion[]> {
    return this.http.get<StockPorSubUbicacion[]>(`${this.apiUrl}/por-sub-ubicacion`);
  }
  
  exportarPdf(subUbicacionId: number) {
    return this.http.get(`${this.apiUrl}/por-sub-ubicacion/pdf`, {
      params: new HttpParams().set('subUbicacionId', subUbicacionId),
      responseType: 'blob'
    });
  }

  exportarExcel(subUbicacionId: number) {
    return this.http.get(`${this.apiUrl}/por-sub-ubicacion/excel`, {
      params: new HttpParams().set('subUbicacionId', subUbicacionId),
      responseType: 'blob'
    });
  }
}
