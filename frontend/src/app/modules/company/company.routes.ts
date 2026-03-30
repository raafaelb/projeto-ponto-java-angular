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
        path: 'departments',
        loadComponent: () => import('../admin/pages/departments/department-list/department-list.component').then((m) => m.DepartmentListComponent)
      },
      {
        path: 'teams',
        loadComponent: () => import('../admin/pages/teams/team-list/team-list.component').then((m) => m.TeamListComponent)
      },
      {
        path: 'org-chart',
        loadComponent: () => import('../admin/pages/org-chart/org-chart.component').then((m) => m.OrgChartComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('../admin/pages/users/user-list/user-list.component').then((m) => m.UserListComponent)
      }
    ]
  }
];
