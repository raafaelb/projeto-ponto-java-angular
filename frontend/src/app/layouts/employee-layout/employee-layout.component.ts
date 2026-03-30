import { Component } from '@angular/core';
import { BaseLayoutComponent } from '../base-layout/base-layout.component';
import { MenuItem, UserInfo } from '../../shared/models/menu.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-employee-layout',
  standalone: true,
  imports: [BaseLayoutComponent],
  template: `
    <app-base-layout
      [userInfo]="userInfo"
      [menuItems]="menuItems"
      pageTitle="Meu Ponto"
      (logout)="onLogout()">
    </app-base-layout>
  `
})
export class EmployeeLayoutComponent {
  userInfo: UserInfo;
  menuItems: MenuItem[] = [
    { label: 'Jornada', icon: 'schedule', route: '/employee/workday' },
    { label: 'Solicitacoes', icon: 'request_page', route: '/employee/requests' },
    { label: 'Performance', icon: 'insights', route: '/employee/performance' },
    { label: 'Compensacao', icon: 'payments', route: '/employee/compensation' },
    { label: 'Carreira', icon: 'school', route: '/employee/career' }
  ];

  constructor(private authService: AuthService) {
    const user = this.authService.getUser();
    this.userInfo = {
      id: user?.id || 0,
      name: user?.name || 'Funcionario',
      email: user?.email || 'funcionario@sistema.com',
      role: 'EMPLOYEE'
    };
  }

  onLogout(): void {
    this.authService.logout();
  }
}
