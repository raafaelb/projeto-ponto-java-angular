import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { Company, CompanyService } from '../../../services/company.service';

@Component({
  selector: 'app-company-form',
  templateUrl: './company-form.component.html',
  styleUrls: ['./company-form.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSnackBarModule,
    NgxMaskDirective
  ],
  providers: [provideNgxMask()]
})
export class CompanyFormComponent implements OnInit {
  companyForm: FormGroup;
  isEditMode = false;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute,
    private companyService: CompanyService
  ) {
    this.companyForm = this.fb.group({
      id: [''],
      nomeFantasia: ['', Validators.required],
      razaoSocial: ['', Validators.required],
      cnpj: ['', [Validators.required, Validators.pattern(/^\d{2}\.\d{3}\.\d{3}\/\d{4}\-\d{2}$/)]]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.loadCompany(Number(id));
      }
    });
  }

  private loadCompany(id: number): void {
    this.loading = true;
    this.companyService.buscarPorId(id).subscribe({
      next: (company) => {
        this.companyForm.patchValue({
          id: company.id,
          nomeFantasia: company.nomeFantasia,
          razaoSocial: company.razaoSocial,
          cnpj: company.cnpj
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading company:', error);
        this.snackBar.open('Erro ao carregar os dados da empresa', 'Fechar', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        this.router.navigate(['/admin/companies']);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.companyForm.valid) {
      this.loading = true;

      // Clean the CNPJ by removing all non-numeric characters
      const formValue = {
        ...this.companyForm.value,
        cnpj: this.companyForm.value.cnpj.replace(/\D/g, '')
      };

      console.log('Form submitted (cleaned CNPJ):', formValue);
      
      const request = this.isEditMode
        ? this.companyService.atualizar(formValue.id, formValue)
        : this.companyService.criar(formValue);
      
      request.subscribe({
        next: (response) => {
          console.log(`Company ${this.isEditMode ? 'updated' : 'created'} successfully:`, response);
          this.loading = false;
          this.snackBar.open(
            `Empresa ${this.isEditMode ? 'atualizada' : 'cadastrada'} com sucesso!`,
            'Fechar',
            { duration: 3000 }
          );
          this.router.navigate(['/admin/companies']);
        },
        error: (error) => {
          console.error(`Error ${this.isEditMode ? 'updating' : 'creating'} company:`, error);
          this.loading = false;
          this.snackBar.open(
            `Erro ao ${this.isEditMode ? 'atualizar' : 'cadastrar'} empresa`,
            'Fechar',
            { duration: 3000 }
          );
        }
      });
    }
  }

  // Add this method to handle cancel button click
  onCancel(): void {
    this.router.navigate(['/admin/companies']);
  }

  // Helper method to show form errors
  getErrorMessage(controlName: string): string {
    const control = this.companyForm.get(controlName);
    
    if (control?.hasError('required')) {
      return 'Campo obrigatório';
    }
    
    if (control?.hasError('pattern')) {
      return 'Formato inválido';
    }
    
    return '';
  }
}
