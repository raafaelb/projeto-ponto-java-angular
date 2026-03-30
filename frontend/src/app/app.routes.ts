import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { companyGuard } from './core/guards/company.guard';
import { employeeGuard } from './core/guards/employee.guard';
import { noAuthGuard } from './core/guards/no-auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./modules/home/home.component').then((m) => m.HomeComponent)
  },
  {
    path: 'login',
    canActivate: [noAuthGuard],
    loadComponent: () => import('./modules/auth/pages/login/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    loadChildren: () => import('./modules/admin/admin.routes').then((m) => m.ADMIN_ROUTES)
  },
  {
    path: 'company',
    canActivate: [authGuard, companyGuard],
    loadChildren: () => import('./modules/company/company.routes').then((m) => m.COMPANY_ROUTES)
  },
  {
    path: 'employee',
    canActivate: [authGuard, employeeGuard],
    loadChildren: () => import('./modules/employee/employee.routes').then((m) => m.EMPLOYEE_ROUTES)
  },
  { path: '**', redirectTo: '' }
];
