import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmpresaListComponent } from './components/empresa/empresa-list/empresa-list.component';

const routes: Routes = [
  { path: '', redirectTo: '/empresas', pathMatch: 'full' },
  { path: 'empresas', component: EmpresaListComponent },
  // { path: 'empresas/nova', component: EmpresaFormComponent },
  // { path: 'empresas/:id', component: EmpresaFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }