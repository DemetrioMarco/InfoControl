export interface SubUbicacion {
  id: number;
  ubicacionId: number;
  ubicacionNombre: string;
  nombre: string;
  descripcion: string;
  activo: boolean;
  fechaCreacion: string;
  fechaModificacion: string;
}

export interface SubUbicacionCreate {
  ubicacionId: number;
  nombre: string;
  descripcion?: string;
}

export interface SubUbicacionUpdate {
  ubicacionId: number;
  nombre: string;
  descripcion?: string;
  activo: boolean;
}
