import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BaseLayoutComponent } from '../base-layout/base-layout.component';
import { MenuItem, UserInfo } from '../../shared/models/menu.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [BaseLayoutComponent],
  template: `
    <app-base-layout
      [userInfo]="userInfo"
      [menuItems]="menuItems"
      pageTitle="Painel Administrativo"
      (logout)="onLogout()">
    </app-base-layout>
  `
})
export class AdminLayoutComponent {
  userInfo: UserInfo;
  menuItems: MenuItem[] = [
    { label: 'Visao Geral', icon: 'insights', route: '/admin/dashboard' },
    { label: 'Empresas', icon: 'apartment', route: '/admin/companies' },
    { label: 'Usuarios', icon: 'manage_accounts', route: '/admin/users' }
  ];

  constructor(private authService: AuthService, private router: Router) {
    const user = this.authService.getUser();
    this.userInfo = {
      id: user?.id || 0,
      name: user?.name || 'Administrador',
      email: user?.email || 'admin@sistema.com',
      role: 'ADMIN'
    };
  }

  onLogout(): void {
    this.authService.logout();
  }
}
