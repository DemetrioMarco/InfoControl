import { Role } from "./role.enum";

export interface UserResponse {
  id: number;
  nombre: string;
  email: string;
  rol: Role;
  enabled: boolean;
  createdAt: string;
  updatedAt?: string;
}

export interface UserFormModel {
  nombre: string;
  email: string;
  password: string;
  confirmPassword: string;
  rol: Role;
  enabled: boolean;
}

export interface CreateUserDto {
  nombre: string;
  email: string;
  password: string;
  rol: Role;
  enabled: boolean;
}

export interface UpdateUserDto {
  nombre: string;
  email: string;
  password?: string;
  rol: Role;
  enabled: boolean;
}

export interface UpdateUserStatusDto {
  enabled: boolean;
}
