
// Estructura de la fila en la tabla principal de listado
export interface TomaInventarioList {
  id: number;
  subUbicacionId: number;
  fechaProgramada: string;
  estado: string;
  fechaCreacion: string;
  totalDetalles: number;
}

// Estructura detallada (lo que devuelven los endpoints GET /{id} y POST)
export interface TomaInventarioDetail {
  id: number;
  subUbicacionId: number;
  fechaProgramada: string;
  fechaCreacion: string;
  detalles: DetalleResponse[];
}

export interface DetalleResponse {
  id: number;
  productoId: number;
  cantidadSistema: number;
  cantidadFisica: number | null;
  diferencia: number | null;
}

// Para el envío del conteo (PATCH)
export interface RegistrarConteoRequest {
  conteos: ConteoDetalle[];
}

export interface ConteoDetalle {
  detalleId: number;
  cantidadFisica: number;
}

export interface UpdateTomaRequest {
  fechaProgramada: string;
  estado: string;
  detalles: {
    detalleId: number;
    productoId: number;
    cantidadSistema: number;
  }[];
}
