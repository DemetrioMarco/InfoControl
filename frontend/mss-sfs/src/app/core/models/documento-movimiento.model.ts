/**
 * Respuesta del servidor para los documentos adjuntos
 */
export interface DocumentoMovimientoResponse {
  id: number;
  movimientoId: number;
  nombreArchivo: string;
  rutaArchivo: string;
  tipoDocumento: string;
  subidoPorId: number;
  subidoPorNombre: string;
  fechaSubida: string;
}
