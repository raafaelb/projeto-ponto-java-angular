import { Component, OnInit } from '@angular/core';
import { CompanyService } from '../../../services/company.service';
import { Company } from '../../../../../shared/models/company.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-company-list',
  standalone: true,
  imports: [CommonModule, RouterModule, MatTableModule, MatButtonModule, MatIconModule, MatCardModule],
  templateUrl: './company-list.component.html',
  styleUrls: ['./company-list.component.scss']
})
export class CompanyListComponent implements OnInit {
  companies: Company[] = [];
  displayedColumns: string[] = ['cnpj', 'razaoSocial', 'nomeFantasia', 'acoes'];

  constructor(private companyService: CompanyService) {}

  ngOnInit(): void {
    this.loadCompanies();
  }

  loadCompanies(): void {
    this.companyService.list().subscribe({
      next: (companies) => (this.companies = companies)
    });
  }

  deleteCompany(id: number): void {
    this.companyService.delete(id).subscribe({
      next: () => this.loadCompanies()
    });
  }
}
