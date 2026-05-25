import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import { ProductoHistorial } from '../models/producto-historial.model';
import { environment } from '../../../environment/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProductoHistorialService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/producto-historial`;

    getPorProducto(productoId: number) {
        return this.http.get<ProductoHistorial[]>(`${this.apiUrl}/producto/${productoId}`);
    }

    buscar(filtros: any) {
        let params = new HttpParams();
        if (filtros.productoId) params = params.set('productoId', filtros.productoId);
        if (filtros.tipoEvento) params = params.set('tipoEvento', filtros.tipoEvento);
        if (filtros.fechaInicio) params = params.set('fechaInicio', filtros.fechaInicio);
        if (filtros.fechaFin) params = params.set('fechaFin', filtros.fechaFin);

        return this.http.get<ProductoHistorial[]>(`${this.apiUrl}/buscar`, { params });
    }
    
}
