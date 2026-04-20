export interface Ubicacion {
  id: number;
  nombre: string;
  tipoUbicacionId: number;
  tipoUbicacionNombre: string;
  descripcion: string;
  direccion: string;
  responsable: string;
  esPrincipal: boolean;
  activo: boolean;
  fechaCreacion: string;
  fechaModificacion: string;
}

export interface UbicacionCreate {
  nombre: string;
  tipoUbicacionId: number;
  descripcion?: string;
  direccion?: string;
  responsable?: string;
  esPrincipal?: boolean;
}

export interface UbicacionUpdate {
  nombre: string;
  tipoUbicacionId: number;
  descripcion?: string;
  direccion?: string;
  responsable?: string;
  esPrincipal: boolean;
  activo: boolean;
}
