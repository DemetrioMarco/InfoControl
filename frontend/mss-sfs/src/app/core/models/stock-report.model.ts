export interface ProductoStock {
  productoId: number;
  codigoInterno: string;
  nombreProducto: string;
  cantidad: number;
}

export interface StockPorSubUbicacion {
  subUbicacionId: number;
  subUbicacionNombre: string;
  ubicacionId: number;
  ubicacionNombre: string;
  tipoUbicacionId: number;
  tipoUbicacionNombre: string;
  codigoTipoUbicacion: string;
  stockTotal: number;
  productos: ProductoStock[];
}
