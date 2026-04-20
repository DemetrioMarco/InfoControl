export interface Subcategoria {
  id: number;
  categoriaId: number;
  categoriaNombre: string;
  nombre: string;
  descripcion: string;
  activo: boolean;
  fechaCreacion: string;
  fechaModificacion: string;
}

export interface SubcategoriaCreate {
  categoriaId: number;
  nombre: string;
  descripcion?: string;
}

export interface SubcategoriaUpdate {
  categoriaId: number;
  nombre: string;
  descripcion?: string;
  activo: boolean;
}
