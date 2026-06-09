import {
  HttpErrorResponse,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';
import { inject } from '@angular/core';
import { BehaviorSubject, throwError } from 'rxjs';
import { catchError, filter, finalize, switchMap, take } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

const AUTH_PATHS = new Set([
  '/auth/login',
  '/auth/refresh'
]);

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | Error | null>(null);

function getPathname(url: string): string {
  return new URL(url, window.location.origin).pathname;
}

function isAuthRequest(url: string): boolean {
  return AUTH_PATHS.has(getPathname(url));
}

function addTokenHeader(request: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return request.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
}

function addTokenHeaderIfExists(
  request: HttpRequest<unknown>,
  token: string | null
): HttpRequest<unknown> {
  return token ? addTokenHeader(request, token) : request;
}

function resetRefreshState(): void {
  isRefreshing = false;
  refreshTokenSubject.next(null);
}

function handle401Error(
  request: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authService: AuthService
) {
  if (!isRefreshing) {
    const refreshToken = authService.getRefreshToken();

    if (!refreshToken) {
      resetRefreshState();
      authService.logout();
      return throwError(() => new Error('No refresh token available'));
    }

    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshAccessToken().pipe(
      switchMap((response) => {
        refreshTokenSubject.next(response.access_token);
        return next(addTokenHeader(request, response.access_token));
      }),
      catchError((error: HttpErrorResponse) => {
        refreshTokenSubject.next(new Error('Refresh token failed'));
        resetRefreshState();
        authService.logout();
        return throwError(() => error);
      }),
      finalize(() => {
        isRefreshing = false;
      })
    );
  }

  return refreshTokenSubject.pipe(
    filter((value): value is string | Error => value !== null),
    take(1),
    switchMap((value) => {
      if (value instanceof Error) {
        return throwError(() => value);
      }

      return next(addTokenHeader(request, value));
    })
  );
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const isAuth = isAuthRequest(req.url);

  const authReq = isAuth
    ? req
    : addTokenHeaderIfExists(req, authService.getToken());

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !isAuth) {
        return handle401Error(authReq, next, authService);
      }

      return throwError(() => error);
    })
  );
};
