import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
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
    // Initialize component
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
      
      //Envia a requisição para a API
      this.companyService.criar(formValue).subscribe({
        next: (response) => {
          console.log('Company created successfully:', response);
          this.loading = false;
          this.snackBar.open('Empresa cadastrada com sucesso!', 'Fechar', { duration: 3000 });
          this.router.navigate(['/admin/companies']);
        },
        error: (error) => {
          console.error('Error creating company:', error);
          this.loading = false;
          this.snackBar.open('Erro ao cadastrar empresa', 'Fechar', { duration: 3000 });
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
