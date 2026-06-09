import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { DocumentoMovimientoResponse } from '../models/documento-movimiento.model';

@Injectable({
  providedIn: 'root'
})
export class DocumentoMovimientoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/api/documentos-movimiento`;

  /**
   * Sube un archivo asociado a un movimiento.
   */
  subirDocumento(
    movimientoId: number,
    file: File,
    usuarioId: number
  ): Observable<DocumentoMovimientoResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('usuarioId', usuarioId.toString());

    console.log(formData);

    return this.http.post<DocumentoMovimientoResponse>(
      `${this.apiUrl}/${movimientoId}`,
      formData
    );
  }

  /**
   * Obtiene los documentos asociados a un movimiento.
   */
  obtenerPorMovimiento(movimientoId: number): Observable<DocumentoMovimientoResponse[]> {
    return this.http.get<DocumentoMovimientoResponse[]>(
      `${this.apiUrl}/${movimientoId}`
    );
  }

  /**
   * Construye una URL pública del archivo.
   */
  getDownloadUrl(rutaArchivo: string): string {
    if (!rutaArchivo) {
      return '';
    }

    if (/^https?:\/\//i.test(rutaArchivo)) {
      return rutaArchivo;
    }

    const baseUrl = environment.apiUrl.replace(/\/api\/?$/, '');

    const rutaNormalizada = rutaArchivo
      .replace(/^\/+/, '')
      .replace(/^uploads\/?/, '');

    return `${baseUrl}/uploads/${rutaNormalizada}`;
  }
}
