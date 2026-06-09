/** 
 * Tipos de movimiento permitidos por el sistema 
 */
export type TipoMovimiento = 'ENTRADA' | 'SALIDA' | 'TRASPASO' | 'AJUSTE' | 'DEVOLUCION';

/**
 * Representa el objeto completo que viene del servidor (Detalle)
 */
export interface MovimientoInventario {
  id: number;
  tipoMovimiento: TipoMovimiento;
  cantidad: number;
  precioUnitario: number | null;
  estadoMovimiento: string;
  motivo: string;
  observaciones?: string;
  numeroReferencia: string;
  fechaMovimiento: string;
  fechaAprobacion: string | null;
  
  // Producto
  productoId: number;
  productoNombre: string;
  
  // Origen
  subUbicacionOrigenId: number | null;
  subUbicacionOrigenNombre: string | null;
  
  // Destino
  subUbicacionDestinoId: number | null;
  subUbicacionDestinoNombre: string | null;
  
  // Responsables
  realizadoPorId: number;
  realizadoPorNombre: string;
  aprobadoPorId: number | null;
  aprobadoPorNombre: string | null;
}

/**
 * Para listados rápidos e historial en tablas
 */
export interface MovimientoInventarioListItem {
  id: number;
  tipoMovimiento: TipoMovimiento;
  cantidad: number;
  fechaMovimiento: string;
  productoNombre: string;
  subUbicacionOrigenNombre: string | null;
  subUbicacionDestinoNombre: string | null;
  numeroReferencia: string;
  estadoMovimiento: string;
}

// --- REQUESTS (Lo que envías al Servidor) ---

/**
 * Objeto para registrar un nuevo movimiento
 * Basado en los requerimientos del Stored Procedure
 */
export interface MovimientoInventarioRequest {
  tipoMovimiento: TipoMovimiento;
  productoId: number;
  cantidad: number;
  precioUnitario?: number | null;
  subUbicacionOrigenId?: number | null;
  subUbicacionDestinoId?: number | null;
  motivo: string;
  observaciones?: string;
  numeroReferencia: string;
  usuarioResponsableId: number; // El ID del usuario que realiza la acción
}

/**
 * Interfaz para la respuesta simple del servidor al insertar
 */
export interface MovimientoResponse {
  exitoso: boolean;
  mensaje: string;
  id: number;
}

