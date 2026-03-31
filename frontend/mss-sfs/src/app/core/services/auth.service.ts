import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthResponse, LoginRequest, User } from '../models/auth-response.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly apiUrl = 'http://localhost:8080';
  private readonly ACCESS_TOKEN_KEY = 'accessToken';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_KEY = 'auth_user';

  login(credentials: LoginRequest): Observable<AuthResponse> {
  return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, credentials).pipe(
    tap((response) => {
      console.log('AuthService.login - response received', response);
     
      this.setSession(response);
      
    }),
    catchError((err) => {
      console.error('AuthService.login - catchError:', err);
      return throwError(() => err);
    })
  );
}

setSession(authResponse: AuthResponse): void {
  try {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, authResponse.access_token);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, authResponse.refresh_token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(authResponse.user));
    console.log('setSession: tokens saved');
  } catch (err) {
    console.error('setSession error:', err);
    throw err;
  }
}

  getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  getUser(): User | null {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  isAuthenticated(): boolean {
    const token = this.getAccessToken();
    return !!token;
  }

  logout(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.router.navigate(['/login']);
  }
}
