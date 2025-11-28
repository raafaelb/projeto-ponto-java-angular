import { Component, OnInit } from '@angular/core';
import { Empresa, EmpresaService } from '../../../services/empresa.service';

@Component({
  selector: 'app-empresa-list',
  templateUrl: './empresa-list.component.html',
  styleUrls: ['./empresa-list.component.scss']
})
export class EmpresaListComponent implements OnInit {
  empresas: Empresa[] = [];
  displayedColumns: string[] = ['id', 'cnpj', 'razaoSocial', 'nomeFantasia', 'acoes'];

  constructor(private empresaService: EmpresaService) { }

  ngOnInit(): void {
    this.carregarEmpresas();
  }

  carregarEmpresas(): void {
    this.empresaService.listarTodos().subscribe({
      next: (empresas) => this.empresas = empresas,
      error: (error) => console.error('Erro ao carregar empresas:', error)
    });
  }

  deletarEmpresa(id: number): void {
    if (confirm('Tem certeza que deseja excluir esta empresa?')) {
      this.empresaService.deletar(id).subscribe({
        next: () => this.carregarEmpresas(),
        error: (error) => console.error('Erro ao deletar empresa:', error)
      });
    }
  }
}