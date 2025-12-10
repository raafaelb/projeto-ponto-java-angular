import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminLayoutComponent } from '../../layouts/admin-layout/admin-layout.component';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      { 
        path: '', 
        redirectTo: 'dashboard', 
        pathMatch: 'full' 
      },
      
      // Dashboard
      { 
        path: 'dashboard', 
        loadComponent: () => import('./pages/dashboard/dashboard.component')
          .then(m => m.DashboardComponent)
      },
      
      // Companies
      { 
        path: 'companies', 
        loadComponent: () => import('./pages/companies/company-list/company-list.component')
          .then(m => m.CompanyListComponent)
      },
      { 
        path: 'companies/new', 
        loadComponent: () => import('./pages/companies/company-form/company-form.component')
          .then(m => m.CompanyFormComponent)
      },
      { 
        path: 'companies/:id', 
        loadComponent: () => import('./pages/companies/company-form/company-form.component')
          .then(m => m.CompanyFormComponent)
      },
      
      // // Usuários (futuro)
      // { 
      //   path: 'usuarios', 
      //   loadComponent: () => import('./pages/usuarios/usuario-list/usuario-list.component')
      //     .then(m => m.UsuarioListComponent)
      // },
      
      
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(ADMIN_ROUTES)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }