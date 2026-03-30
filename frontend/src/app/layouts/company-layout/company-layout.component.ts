import { Component } from '@angular/core';
import { BaseLayoutComponent } from '../base-layout/base-layout.component';
import { MenuItem, UserInfo } from '../../shared/models/menu.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-company-layout',
  standalone: true,
  imports: [BaseLayoutComponent],
  template: `
    <app-base-layout
      [userInfo]="userInfo"
      [menuItems]="menuItems"
      pageTitle="Portal da Empresa"
      (logout)="onLogout()">
    </app-base-layout>
  `
})
export class CompanyLayoutComponent {
  userInfo: UserInfo;
  menuItems: MenuItem[] = [
    { label: 'Visao Geral', icon: 'insights', route: '/company/dashboard' },
    { label: 'Funcionarios', icon: 'badge', route: '/company/employees' },
    { label: 'Departamentos', icon: 'domain', route: '/company/departments' },
    { label: 'Times', icon: 'groups', route: '/company/teams' },
    { label: 'Organograma', icon: 'account_tree', route: '/company/org-chart' },
    { label: 'Ausencias', icon: 'event_busy', route: '/company/absences' },
    { label: 'Hora Extra', icon: 'schedule', route: '/company/overtime' },
    { label: 'Feriados', icon: 'event', route: '/company/holidays' },
    { label: 'Anomalias', icon: 'warning', route: '/company/anomalies' },
    { label: 'Relatorios', icon: 'bar_chart', route: '/company/reports' },
    { label: 'Performance', icon: 'track_changes', route: '/company/performance' },
    { label: 'Compensacao', icon: 'payments', route: '/company/compensation' },
    { label: 'Carreira', icon: 'trending_up', route: '/company/career' },
    { label: 'Usuarios', icon: 'manage_accounts', route: '/company/users' }
  ];

  constructor(private authService: AuthService) {
    const user = this.authService.getUser();
    this.userInfo = {
      id: user?.id || 0,
      name: user?.name || 'Empresa',
      email: user?.email || 'empresa@sistema.com',
      role: 'COMPANY',
      companyId: user?.companyId || undefined
    };
  }

  onLogout(): void {
    this.authService.logout();
  }
}
