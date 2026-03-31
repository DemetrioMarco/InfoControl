import { Role } from './role.enum';

export interface CreateUserRequest {
  nombre: string;
  email: string;
  password: string;
  rol: Role;
  enabled: boolean;
}

export class CreateUserRequestDTO implements CreateUserRequest {
  constructor(
    public nombre: string,
    public email: string,
    public password: string,
    public rol: Role,
    public enabled: boolean = true
  ) {
    this.validate();
  }

  private validate(): void {
    if (!this.nombre || this.nombre.length < 2) {
      throw new Error('Nombre debe tener mínimo 2 caracteres');
    }
    if (!this.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
      throw new Error('Email inválido');
    }
    if (!this.password || this.password.length < 8) {
      throw new Error('Contraseña debe tener mínimo 8 caracteres');
    }
    if (!Object.values(Role).includes(this.rol)) {
      throw new Error('Rol inválido');
    }
  }
}
