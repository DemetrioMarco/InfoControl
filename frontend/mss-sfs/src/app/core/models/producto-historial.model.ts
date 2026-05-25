export interface ProductoHistorial {
  id: number;
  productoId: number;
  fechaEvento: string;
  tipoEvento: 'ENTRADA' | 'SALIDA' | 'TRASPASO';
  ubicacionOrigenId?: number;
  ubicacionDestinoId?: number;
  cantidadMovida: number;
  cantidadAnterior: number;
  cantidadNueva: number;
  referenciaMovimientoId: number;
  referenciaExterna: string;
  usuarioResponsable: string;
  observaciones: string;
  fechaCreacion: string;
}
