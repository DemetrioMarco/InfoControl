export interface Proveedor {
  id: number;
  rut: string;
  razonSocial: string;
  nombreFantasia: string;
  giro: string;
  contactoNombre: string;
  contactoTelefono: string;
  contactoEmail: string;
  direccion: string;
  comuna: string;
  ciudad: string;
  pais: string;
  observaciones: string;
  activo: boolean;
  fechaCreacion: string;
  fechaModificacion: string;
}

export interface ProveedorCreate {
  rut?: string;
  razonSocial: string;
  nombreFantasia?: string;
  giro?: string;
  contactoNombre?: string;
  contactoTelefono?: string;
  contactoEmail?: string;
  direccion?: string;
  comuna?: string;
  ciudad?: string;
  pais?: string;
  observaciones?: string;
}

export interface ProveedorUpdate extends ProveedorCreate {
  activo: boolean;
}
