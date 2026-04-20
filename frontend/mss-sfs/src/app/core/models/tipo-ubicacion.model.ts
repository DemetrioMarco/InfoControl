export interface TipoUbicacion {
  id: number;
  codigo: string;
  nombre: string;
  descripcion: string;
  activo: boolean;
  fechaCreacion: string;
  fechaModificacion: string;
}

export interface TipoUbicacionCreate {
  codigo: string;
  nombre: string;
  descripcion?: string;
}

export interface TipoUbicacionUpdate {
  codigo: string;
  nombre: string;
  descripcion?: string;
  activo: boolean;
}
