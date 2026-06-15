export type TipoMovimientoCorrelativo = 'ENTRADA' | 'SALIDA' | 'TRASPASO' | 'AJUSTE';

export interface CorrelativoMovimientoPreviewResponse {
  id?: number;
  tipoMovimiento: TipoMovimientoCorrelativo;
  anio: number;
  ultimoNumero: number;
  codigoSiguiente: string;
}

export interface CorrelativoMovimientoRequest {
  tipoMovimiento: TipoMovimientoCorrelativo;
  anio?: number;
}
