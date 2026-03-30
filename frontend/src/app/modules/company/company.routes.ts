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
        path: 'performance',
        loadComponent: () => import('./pages/performance/performance.component').then((m) => m.CompanyPerformanceComponent)
      },
      {
        path: 'compensation',
        loadComponent: () => import('./pages/compensation/compensation.component').then((m) => m.CompanyCompensationComponent)
      },
      {
        path: 'career',
        loadComponent: () => import('./pages/career/career.component').then((m) => m.CompanyCareerComponent)
      },
      {
        path: 'payroll',
        loadComponent: () => import('./pages/payroll/payroll.component').then((m) => m.CompanyPayrollComponent)
      },
      {
        path: 'benefits',
        loadComponent: () => import('./pages/benefits/benefits.component').then((m) => m.CompanyBenefitsComponent)
      },
      {
        path: 'compliance',
        loadComponent: () => import('./pages/compliance/compliance.component').then((m) => m.CompanyComplianceComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('../admin/pages/users/user-list/user-list.component').then((m) => m.UserListComponent)
      }
    ]
  }
];
