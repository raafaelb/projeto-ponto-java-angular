import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BaseLayoutComponent } from '../base-layout/base-layout.component';
import { UserInfo, MenuItem } from '../../shared/models/menu.model';

@Component({
  selector: 'app-employee-layout',
  standalone: true,
  imports: [BaseLayoutComponent],
  template: `
    <app-base-layout
      [userInfo]="userInfo"
      [menuItems]="menuItems"
      [pageTitle]="'Meu Ponto'"
      [headerActions]="headerActions"
      (logout)="onLogout()"
      (actionClicked)="onActionClick($event)">
    </app-base-layout>
  `
})
export class EmployeeLayoutComponent {
  userInfo: UserInfo = {
    id: 1,
    name: 'João Funcionário',
    email: 'joao@empresa.com',
    role: 'EMPLOYEE',
    companyId: 1
  };

  menuItems: MenuItem[] = [
    { label: 'Meu Ponto', icon: 'schedule', route: '/employee/ponto', permission: ['EMPLOYEE'] },
    { label: 'Meus Registros', icon: 'history', route: '/employee/registros', permission: ['EMPLOYEE'] },
    { label: 'Meus Dados', icon: 'person', route: '/employee/dados', permission: ['EMPLOYEE'] },
    { label: 'Solicitações', icon: 'request_page', route: '/employee/solicitacoes', permission: ['EMPLOYEE'] }
  ];

  headerActions: any[] = [
    {
      label: 'Bater Ponto',
      icon: 'alarm_add',
      type: 'REGISTER_POINT',
      color: 'accent'
    }
  ];

  constructor(private router: Router) {}

  onLogout(): void {
    localStorage.removeItem('auth_token');
    this.router.navigate(['/login']);
  }

  onActionClick(action: any): void {
    if (action.type === 'REGISTER_POINT') {
      this.baterPonto();
    }
  }

  baterPonto(): void {
    console.log('Batendo ponto...');
    // Implementar lógica de bater ponto
    alert('Ponto registrado com sucesso!');
  }
}