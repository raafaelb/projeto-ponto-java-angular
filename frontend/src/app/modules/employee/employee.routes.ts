import { Routes } from '@angular/router';
import { EmployeeLayoutComponent } from '../../layouts/employee-layout/employee-layout.component';

export const EMPLOYEE_ROUTES: Routes = [
  {
    path: '',
    component: EmployeeLayoutComponent,
    children: [
      { path: '', redirectTo: 'workday', pathMatch: 'full' },
      {
        path: 'workday',
        loadComponent: () => import('./pages/workday/workday.component').then((m) => m.WorkdayComponent)
      },
      {
        path: 'requests',
        loadComponent: () => import('./pages/requests/requests.component').then((m) => m.EmployeeRequestsComponent)
      },
      {
        path: 'performance',
        loadComponent: () => import('./pages/performance/performance.component').then((m) => m.EmployeePerformanceComponent)
      },
      {
        path: 'compensation',
        loadComponent: () => import('./pages/compensation/compensation.component').then((m) => m.EmployeeCompensationComponent)
      },
      {
        path: 'career',
        loadComponent: () => import('./pages/career/career.component').then((m) => m.EmployeeCareerComponent)
      },
      {
        path: 'payroll',
        loadComponent: () => import('./pages/payroll/payroll.component').then((m) => m.EmployeePayrollComponent)
      },
      {
        path: 'benefits',
        loadComponent: () => import('./pages/benefits/benefits.component').then((m) => m.EmployeeBenefitsComponent)
      },
      {
        path: 'compliance',
        loadComponent: () => import('./pages/compliance/compliance.component').then((m) => m.EmployeeComplianceComponent)
      }
    ]
  }
];
