import { Routes } from '@angular/router';
import { EmployeeLayoutComponent } from '../../layouts/employee-layout/employee-layout.component';

export const EMPLOYEE_ROUTES: Routes = [
  {
    path: '',
    component: EmployeeLayoutComponent,
    children: [
      { path: '', redirectTo: 'ponto', pathMatch: 'full' },
    //   { 
    //     path: 'ponto', 
    //     loadComponent: () => import('./pages/ponto/ponto.component')
    //       .then(m => m.PontoComponent)
    //   },
    //   { 
    //     path: 'registros', 
    //     loadComponent: () => import('./pages/registros/registro-list.component')
    //       .then(m => m.RegistroListComponent)
    //   },
    //   { 
    //     path: 'dados', 
    //     loadComponent: () => import('./pages/dados/dados.component')
    //       .then(m => m.DadosComponent)
    //   },
    //   { 
    //     path: 'solicitacoes', 
    //     loadComponent: () => import('./pages/solicitacoes/solicitacao-list.component')
    //       .then(m => m.SolicitacaoListComponent)
    //   }
    ]
  }
];