import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  MovimientoInventario,
  MovimientoInventarioRequest,
  MovimientoResponse
} from '../models/movimiento-inventario.model';
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class MovimientoInventarioService {
  private readonly http = inject(HttpClient);
  // URL basada en el endpoint que proporcionaste
  private readonly apiUrl = `${environment.apiUrl}/api/v1/movimientos-inventario`;

  /**
   * Obtiene el historial completo de movimientos
   * @returns Observable con la lista de movimientos registrados
   */
  getAll(): Observable<MovimientoInventario[]> {
    return this.http.get<MovimientoInventario[]>(this.apiUrl);
  }

  /**
   * Registra un nuevo movimiento de inventario (Entrada, Salida, Traspaso, etc.)
   * @param movimiento Objeto con los datos del movimiento a registrar
   * @returns Mensaje de éxito del servidor
   */
  create(movimiento: MovimientoInventarioRequest): Observable<MovimientoResponse> {
    return this.http.post<MovimientoResponse>(this.apiUrl, movimiento);
  }

  /**
   * Obtiene los movimientos filtrados por un producto específico (Opcional, muy útil para UX)
   * @param productoId ID del producto
   */
  getByProducto(productoId: number): Observable<MovimientoInventario[]> {
    return this.http.get<MovimientoInventario[]>(`${this.apiUrl}/producto/${productoId}`);
  }

  /**
   * Obtiene un movimiento por su ID para ver detalles específicos
   * @param id ID del movimiento
   */
  getById(id: number): Observable<MovimientoInventario> {
    return this.http.get<MovimientoInventario>(`${this.apiUrl}/${id}`);
  }

  /**
   * Obtener movimientos
   */
  // Añadir este método al servicio ya creado
  getByTipo(tipo: string): Observable<MovimientoInventario[]> {
    return this.http.get<MovimientoInventario[]>(`${this.apiUrl}/tipo/${tipo}`);
  }

}
