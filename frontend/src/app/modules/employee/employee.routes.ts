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
      }
    ]
  }
];
