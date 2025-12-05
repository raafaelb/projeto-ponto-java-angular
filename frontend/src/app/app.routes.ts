import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { companyGuard } from './core/guards/company.guard';
import { employeeGuard } from './core/guards/employee.guard';
import { noAuthGuard } from './core/guards/no-auth.guard';

export const routes: Routes = [
  // Pública
  {
    path: '',
    loadComponent: () => import('./modules/home/home.component')
      .then(m => m.HomeComponent)
  },

  // Área de Auth (protegida por noAuthGuard)
  {
    path: 'login',
    canActivate: [noAuthGuard],
    loadChildren: () => import('./modules/auth/auth.routes')
      .then(m => m.AuthRoutingModule)
  },

  // Admin (Protegido + Role específica)
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    loadChildren: () => import('./modules/admin/admin.routes')
      .then(m => m.ADMIN_ROUTES)
  },

  // Company (Protegido + Role específica)
  {
    path: 'company',
    canActivate: [authGuard, companyGuard],
    loadChildren: () => import('./modules/company/company.routes')
      .then(m => m.COMPANY_ROUTES)
  },

  // Employee (Protegido + Role específica)
  {
    path: 'employee',
    canActivate: [authGuard, employeeGuard],
    loadChildren: () => import('./modules/employee/employee.routes')
      .then(m => m.EMPLOYEE_ROUTES)
  },

  // Redirecionamentos
  { path: 'login', redirectTo: 'auth/login' },
  { path: '**', redirectTo: '' }
];
