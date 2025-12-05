import { Routes } from '@angular/router';
import { CompanyLayoutComponent } from '../../layouts/company-layout/company-layout.component';

export const COMPANY_ROUTES: Routes = [
  {
    path: '',
    component: CompanyLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    //   { 
    //     path: 'dashboard', 
    //     loadComponent: () => import('./pages/dashboard/dashboard.component')
    //       .then(m => m.DashboardComponent)
    //   },
    //   { 
    //     path: 'funcionarios', 
    //     loadComponent: () => import('./pages/funcionarios/funcionario-list.component')
    //       .then(m => m.FuncionarioListComponent)
    //   },
    //   { 
    //     path: 'pontos', 
    //     loadComponent: () => import('./pages/pontos/ponto-list.component')
    //       .then(m => m.PontoListComponent)
    //   },
    //   { 
    //     path: 'relatorios', 
    //     loadComponent: () => import('./pages/relatorios/relatorio-list.component')
    //       .then(m => m.RelatorioListComponent)
    //   },
    //   { 
    //     path: 'conta', 
    //     loadComponent: () => import('./pages/conta/conta.component')
    //       .then(m => m.ContaComponent)
    //   }
    ]
  }
];