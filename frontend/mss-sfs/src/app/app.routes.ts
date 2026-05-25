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
        loadComponent: () => import('./features/login/login').then(m => m.Login),
        title: 'Login'
    },
    {
        path: 'app',
        canActivate: [authGuard],
        loadComponent: () => import('./shared/components/layout/layout').then(m => m.Layout),
        children: [
            {
                path: 'dashboard',
                canActivate: [roleGuard],
                data: { roles: [Role.SUPER_ADMIN, Role.ADMIN] },
                loadComponent: () => import('./features/dashboard/dashboard').then(m => m.Dashboard),
                title: 'Dashboard'
            },
            {
                path: 'users',
                canActivate: [roleGuard],
                data: { roles: [Role.SUPER_ADMIN] },
                loadComponent: () => import('./features/users/users-list').then(m => m.UsersList),
                title: 'Usuarios'
            },
            // --- Sección de Inventario ---
            {
                path: 'inventary',
                canActivate: [roleGuard],
                data: { roles: [Role.SUPER_ADMIN, Role.ADMIN] },
                children: [
                    {
                        path: 'products',
                        loadComponent: () => import('./features/productos/pages/producto-list/producto-list').then(m => m.ProductoList),
                        title: 'Productos'
                    },
                    {
                        path: 'movements',
                        loadComponent: () => import('./features/movimiento-inventario/movimiento-inventario').then(m => m.MovimientoInventario),
                        title: 'Movimientos de Inventario'
                    },
                    {
                        path: 'movements-history',
                        loadComponent: () => import('./features/movimiento-inventario/movimiento-list/movimiento-list').then(m => m.MovimientoList),
                        title: 'Historial de Movimientos'
                    }

                ]
            },
            // --- Sección de Reportes ---
            {
                path: 'reports',
                canActivate: [roleGuard],
                data: { roles: [Role.SUPER_ADMIN, Role.ADMIN] },
                children: [
                    {
                        path: 'stock-location',
                        loadComponent: () => import('./features/reports/stock-report/stock-report').then(m => m.StockReport),
                        title: 'Stock por Ubicación'
                    },
                    {
                        path: 'stock-product',
                        loadComponent: () => import('./features/reports/stock-product-report/stock-product-report').then(m => m.StockProductReport),
                        title: 'Stock por Producto'
                    },
                   {
                        path: 'product-audit', // <-- Nueva ruta
                        loadComponent: () => import('./features/reports/product-audit/product-audit').then(m => m.ProductAudit),
                        title: 'Trazabilidad de Producto'
                    },
                ]
            },
            // --- Sección de Catálogos ---
            {
                path: 'catalogs',
                canActivate: [roleGuard],
                data: { roles: [Role.SUPER_ADMIN, Role.ADMIN] },
                children: [
                    {
                        path: 'suppliers',
                        loadComponent: () => import('./features/catalogs/suppliers/supplier-list').then(m => m.SupplierList),
                        title: 'Proveedores'
                    },
                    {
                        path: 'categorias',
                        loadComponent: () => import('./features/catalogs/categories/category-list').then(m => m.CategoryList),
                        title: 'Categorías'
                    },
                    {
                        path: 'subcategorias',
                        loadComponent: () => import('./features/catalogs/subcategories/subcategory-list').then(m => m.SubcategoryList),
                        title: 'Sub-Categorías'
                    },
                    {
                        path: 'tipo-ubicacion',
                        loadComponent: () => import('./features/catalogs/location-types/location-type-list').then(m => m.LocationTypeList),
                        title: 'Tipo Ubicación'
                    },
                    {
                        path: 'ubicaciones',
                        loadComponent: () => import('./features/catalogs/locations/location-list').then(m => m.LocationList),
                        title: 'Ubicación'
                    },
                    {
                        path: 'sub-ubicacion',
                        loadComponent: () => import('./features/catalogs/sububicacion/sub-ubicacion-list').then(m => m.SubUbicacionList),
                        title: 'Sub Ubicación'
                    },
                    {
                        path: 'unidades-medida',
                        loadComponent: () => import('./features/catalogs/unit-measure/unit-measure-list').then(m => m.UnitMeasureList),
                        title: 'Unidad de Medida'
                    }

                ]
            }
        ]
    },
    {
        path: '**',
        redirectTo: 'login'
    },
];
