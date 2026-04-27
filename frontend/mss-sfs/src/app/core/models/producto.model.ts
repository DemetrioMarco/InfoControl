export interface Producto {
  id: number;
  codigoInterno: string;
  nombre: string;
  descripcion: string;
  categoriaId: number;
  categoriaNombre: string;
  subcategoriaId: number;
  subcategoriaNombre: string;
  unidadMedidaId: number;
  unidadMedidaNombre: string;
  proveedorId: number;
  proveedorRazonSocial: string;
  stockActual: number;
  stockMinimo: number;
  stockMaximo: number;
  estadoStock: string;
  precioUnitario: number;
  precioTotal: number;
  estado: string;
  activo: boolean;
  creadoPor: number;
  modificadoPor: number;
  fechaCreacion: string;
  fechaModificacion: string;
}

export interface ProductoListItem {
  id: number;
  codigoInterno: string;
  nombre: string;
  categoriaNombre: string;
  subcategoriaNombre: string;
  unidadMedidaNombre: string;
  proveedorNombre: string;
  stockActual: number;
  estadoStock: string;
  precioUnitario: number;
  activo: boolean;
}

// --- REQUESTS (Lo que envías al Servidor) ---

/** 
 * INTERFAZ OPTIMIZADA: Esta unifica los campos del formulario.
 * Se usa para Create y Update, evitando repetir lógica en el componente.
 */
export interface ProductoRequest {
  codigoInterno: string;
  nombre: string;
  descripcion?: string;
  categoriaId: number;
  subcategoriaId: number;
  unidadMedidaId: number;
  proveedorId?: number;
  creadoPor?: number;
  stockMinimo: number;
  stockMaximo: number;
  stockActual?: number; // Solo se suele enviar en la creación
  precioUnitario: number;
  activo?: boolean;    // Solo se suele enviar en la edición
}

/** Para reportes o vistas rápidas de inventario (ProductoResponse.Stock) */
export interface ProductoStock {
  id: number;
  codigoInterno: string;
  nombre: string;
  stockActual: number;
  stockMinimo: number;
  stockMaximo: number;
  estadoStock: string;
  unidadMedidaNombre: string;
}
