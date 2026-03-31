import { Role } from "./role.enum";

export interface User {
  id: number;
  nombre: string;
  email: string;
  rol: Role;
}

export interface AuthResponse {
  access_token: string;
  refresh_token: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface ApiErrorResponse {
  status: number;
  message: string;
  errors: unknown;
}
