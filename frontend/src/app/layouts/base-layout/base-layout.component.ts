import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { UserInfo, MenuItem } from '../../shared/models/menu.model';

@Component({
  selector: 'app-base-layout',
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    MatSidenavModule, MatIconModule, MatButtonModule,
    MatToolbarModule, MatListModule, MatMenuModule
  ],
  templateUrl: './base-layout.component.html',
  styleUrls: ['./base-layout.component.scss']
})
export class BaseLayoutComponent {
  @Input() userInfo!: UserInfo;
  @Input() menuItems: MenuItem[] = [];
  @Input() pageTitle: string = 'Sistema de Ponto';
  @Input() headerActions: any[] = [];
  
  @Output() logout = new EventEmitter<void>();
  @Output() actionClicked = new EventEmitter<any>();
  
  isSidebarOpen = true;

  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  onLogout(): void {
    this.logout.emit();
  }

  onActionClick(action: any): void {
    this.actionClicked.emit(action);
  }

  // Filtra menu items baseado na role do usuário
  getFilteredMenuItems(): MenuItem[] {
    return this.menuItems.filter(item => 
      !item.permission || item.permission.includes(this.userInfo.role)
    );
  }
}