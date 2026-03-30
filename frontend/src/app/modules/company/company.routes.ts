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
        path: 'absences',
        loadComponent: () => import('./pages/absences/absences.component').then((m) => m.CompanyAbsencesComponent)
      },
      {
        path: 'overtime',
        loadComponent: () => import('./pages/overtime/overtime.component').then((m) => m.CompanyOvertimeComponent)
      },
      {
        path: 'holidays',
        loadComponent: () => import('./pages/holidays/holidays.component').then((m) => m.CompanyHolidaysComponent)
      },
      {
        path: 'anomalies',
        loadComponent: () => import('./pages/anomalies/anomalies.component').then((m) => m.CompanyAnomaliesComponent)
      },
      {
        path: 'reports',
        loadComponent: () => import('./pages/reports/reports.component').then((m) => m.CompanyReportsComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('../admin/pages/users/user-list/user-list.component').then((m) => m.UserListComponent)
      }
    ]
  }
];
