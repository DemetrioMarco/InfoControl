import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { Role } from './core/models/role.enum';


export const routes: Routes = [
    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: 'login',
        loadComponent: () => import('./features/login/login').then( m => m.Login)
    },
    {
        path: 'app',
        canActivate: [authGuard],
        loadComponent: () => import('./shared/components/layout/layout').then( m => m.Layout),
        children: [
             {
                path: 'dashboard',
                canActivate: [roleGuard],
                data: { roles : [Role.SUPER_ADMIN, Role.ADMIN]},
                loadComponent: () => import('./features/dashboard/dashboard').then( m=> m.Dashboard)
            },
            {
                path: 'users',
                canActivate: [roleGuard],
                data: { roles : [Role.SUPER_ADMIN, Role.ADMIN]},
                loadComponent: () => import ('./features/users/users-list').then( m => m.UsersList )
            }
        ]
    },
   
    {
        path: '**',
        redirectTo: 'login'
    },
    
];
