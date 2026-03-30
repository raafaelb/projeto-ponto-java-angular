import { Routes } from '@angular/router';
import { CompanyLayoutComponent } from '../../layouts/company-layout/company-layout.component';

export const COMPANY_ROUTES: Routes = [
  {
    path: '',
    component: CompanyLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('../admin/pages/dashboard/dashboard.component').then((m) => m.DashboardComponent)
      },
      {
        path: 'employees',
        loadComponent: () => import('../admin/pages/employee/funcionario-list/funcionario-list.component').then((m) => m.FuncionarioListComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('../admin/pages/users/user-list/user-list.component').then((m) => m.UserListComponent)
      }
    ]
  }
];
