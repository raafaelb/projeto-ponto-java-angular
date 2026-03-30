import { Routes } from '@angular/router';
import { AdminLayoutComponent } from '../../layouts/admin-layout/admin-layout.component';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent)
      },
      {
        path: 'companies',
        loadComponent: () => import('./pages/companies/company-list/company-list.component').then((m) => m.CompanyListComponent)
      },
      {
        path: 'companies/new',
        loadComponent: () => import('./pages/companies/company-form/company-form.component').then((m) => m.CompanyFormComponent)
      },
      {
        path: 'companies/:id',
        loadComponent: () => import('./pages/companies/company-form/company-form.component').then((m) => m.CompanyFormComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('./pages/users/user-list/user-list.component').then((m) => m.UserListComponent)
      }
    ]
  }
];
