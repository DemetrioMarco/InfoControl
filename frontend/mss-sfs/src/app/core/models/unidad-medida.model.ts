export interface UnidadMedida {
  id: number;
  codigo: string;
  nombre: string;
  descripcion: string;
  activo: boolean;
  fechaCreacion: string;
  fechaModificacion: string;
}

export interface UnidadMedidaCreate {
  codigo: string;
  nombre: string;
  descripcion?: string;
}

export interface UnidadMedidaUpdate {
  codigo: string;
  nombre: string;
  descripcion?: string;
  activo: boolean;
}
