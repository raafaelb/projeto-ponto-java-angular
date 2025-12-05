import { Component, OnInit } from '@angular/core';
import { Company, CompanyService } from '../../../services/company.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-company-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './company-list.component.html',
  styleUrls: ['./company-list.component.scss']
})
export class CompanyListComponent implements OnInit {
  companies: Company[] = [];
  displayedColumns: string[] = ['id', 'cnpj', 'razaoSocial', 'nomeFantasia', 'acoes'];

  constructor(private companyService: CompanyService) { }

  ngOnInit(): void {
    this.loadCompanies();
  }

  loadCompanies(): void {
    this.companyService.listarTodos().subscribe({
      next: (companies) => this.companies = companies,
      error: (error) => console.error('Erro ao carregar companies:', error)
    });
  }

  deleteCompany(id: number): void {
    if (confirm('Tem certeza que deseja excluir esta empresa?')) {
      this.companyService.deletar(id).subscribe({
        next: () => this.loadCompanies(),
        error: (error) => console.error('Erro ao deletar empresa:', error)
      });
    }
  }
}