import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';
import { Role } from '../models/role.enum';

export const roleGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.getUser();
  const requiredRoles = route.data['roles'] as Role[];

  // Si no hay roles requeridos, permite el acceso
  if(!requiredRoles || requiredRoles.length === 0){
    return true;
  }

  // Si no hay usuario, redirige a login
  if(!user){
    return router.createUrlTree(['/login']);
  }

  // Verificar si el rol del usuario está en la lista de roles permitidos
  if(requiredRoles.includes(user.rol)){
    return true;
  }
  
  console.warn('Acceso denegado: Rol insuficiente');
  return router.createUrlTree(['/login'],{
    queryParams: { error: 'No tienes permisos para acceder a esta sección.' }
  });
};
