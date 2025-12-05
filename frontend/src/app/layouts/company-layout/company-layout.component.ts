import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BaseLayoutComponent } from '../base-layout/base-layout.component';
import { UserInfo, MenuItem } from '../../shared/models/menu.model';

@Component({
  selector: 'app-company-layout',
  standalone: true,
  imports: [BaseLayoutComponent],
  template: `
    <app-base-layout
      [userInfo]="userInfo"
      [menuItems]="menuItems"
      [pageTitle]="'Minha Empresa'"
      [headerActions]="headerActions"
      (logout)="onLogout()"
      (actionClicked)="onActionClick($event)">
    </app-base-layout>
  `
})
export class CompanyLayoutComponent {
  userInfo: UserInfo = {
    id: 1,
    name: 'Empresa ABC Ltda',
    email: 'empresa@empresa.com',
    role: 'COMPANY',
    companyId: 1
  };

  menuItems: MenuItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/company/dashboard', permission: ['COMPANY'] },
    { label: 'Funcionários', icon: 'people', route: '/company/funcionarios', permission: ['COMPANY'] },
    { label: 'Pontos', icon: 'schedule', route: '/company/pontos', permission: ['COMPANY'] },
    { label: 'Relatórios', icon: 'assessment', route: '/company/relatorios', permission: ['COMPANY'] },
    { label: 'Minha Conta', icon: 'business', route: '/company/conta', permission: ['COMPANY'] }
  ];

  headerActions: any[] = [
    // Ações específicas da empresa
  ];

  constructor(private router: Router) {}

  onLogout(): void {
    localStorage.removeItem('auth_token');
    this.router.navigate(['/login']);
  }

  onActionClick(action: any): void {
    console.log('Company action:', action);
  }
}