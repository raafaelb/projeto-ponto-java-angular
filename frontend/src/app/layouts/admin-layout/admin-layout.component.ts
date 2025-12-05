import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BaseLayoutComponent } from '../base-layout/base-layout.component';
import { UserInfo, MenuItem } from '../../shared/models/menu.model';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [BaseLayoutComponent],
  template: `
    <app-base-layout
      [userInfo]="userInfo"
      [menuItems]="menuItems"
      [pageTitle]="'Administração'"
      [headerActions]="headerActions"
      (logout)="onLogout()"
      (actionClicked)="onActionClick($event)">
    </app-base-layout>
  `
})
export class AdminLayoutComponent {
  // Em produção, pegar do AuthService
  userInfo: UserInfo = {
    id: 1,
    name: 'Administrador',
    email: 'admin@sistema.com',
    role: 'ADMIN'
  };

  menuItems: MenuItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/admin/dashboard', permission: ['ADMIN'] },
    { label: 'Empresas', icon: 'business', route: '/admin/companies', permission: ['ADMIN'] },
    { label: 'Usuários', icon: 'people', route: '/admin/usuarios', permission: ['ADMIN'] },
    { label: 'Funcionários', icon: 'badge', route: '/admin/funcionarios', permission: ['ADMIN'] },
    { label: 'Relatórios', icon: 'assessment', route: '/admin/relatorios', permission: ['ADMIN'] },
    { label: 'Configurações', icon: 'settings', route: '/admin/configuracoes', permission: ['ADMIN'] }
  ];

  headerActions: any[] = [
    // Ações específicas do admin no header
  ];

  constructor(private router: Router) {}

  onLogout(): void {
    // Lógica de logout
    localStorage.removeItem('auth_token');
    this.router.navigate(['/login']);
  }

  onActionClick(action: any): void {
    console.log('Admin action:', action);
  }
}