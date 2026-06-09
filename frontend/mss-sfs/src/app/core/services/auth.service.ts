import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environment/environment';
import { AuthResponse, LoginRequest, User } from '../models/auth-response.model';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = `${environment.apiUrl}/auth`;

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refresh_token');
  }

  getUser(): User | null {
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;

    try {
      return JSON.parse(userStr) as User;
    } catch {
      this.clearTokens();
      return null;
    }
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  saveTokens(response: AuthResponse): void {
    localStorage.setItem('access_token', response.access_token);
    localStorage.setItem('refresh_token', response.refresh_token);

    if (response.user) {
      localStorage.setItem('user', JSON.stringify(response.user));
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => this.saveTokens(response))
    );
  }

  refreshAccessToken(): Observable<AuthResponse> {
    const refresh_token = this.getRefreshToken();

    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refresh_token }).pipe(
      tap((response) => this.saveTokens(response))
    );
  }

  clearTokens(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user');
  }

  logout(redirect: boolean = true): void {
    this.clearTokens();

    if (redirect && this.router.url !== '/login') {
      this.router.navigate(['/login']);
    }
  }

}
