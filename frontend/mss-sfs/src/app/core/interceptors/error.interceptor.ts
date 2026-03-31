import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {

      // manejo global SOLO sesión
      if (err.status === 401) {
        authService.logout();
      }

      // 🔥 IMPORTANTE: regresar el error ORIGINAL
      return throwError(() => err);
    })
  );
};