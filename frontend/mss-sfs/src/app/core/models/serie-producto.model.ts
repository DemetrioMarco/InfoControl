export interface SerieProductoRequest {
  productoId: number;
  subUbicacionId: number;
  serie: string;
}

export interface SerieProductoResponse {
  id: number;
  productoId: number;
  productoCodigoInterno: string;
  productoNombre: string;
  subUbicacionId: number;
  subUbicacionNombre: string;
  serie: string;
  createdAt: string;
  updatedAt: string;
}
