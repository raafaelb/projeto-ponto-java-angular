import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatSelectModule, MatProgressSpinnerModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  loginTypes = [
    { value: 'ADMIN', label: 'Administrador' },
    { value: 'COMPANY', label: 'Empresa' },
    { value: 'EMPLOYEE', label: 'Funcionário' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      tipo: ['ADMIN', Validators.required],
      username: ['admin', [Validators.required]],
      password: ['admin123', [Validators.required]]
    });
  }

  get f() { return this.loginForm.controls; }

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(
      this.f['username'].value,
      this.f['password'].value
    ).subscribe({
      next: () => {
        // Redirecionamento é feito pelo AuthService
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.message;
        this.isLoading = false;
      }
    });
  }

  // Para desenvolvimento: login rápido por tipo
  quickLogin(tipo: string) {
    this.loginForm.patchValue({ tipo });
    
    switch (tipo) {
      case 'ADMIN':
        this.loginForm.patchValue({ 
          username: 'admin', 
          password: 'admin123' 
        });
        break;
      case 'COMPANY':
        this.loginForm.patchValue({ 
          username: 'empresa@teste.com', 
          password: 'empresa123' 
        });
        break;
      case 'EMPLOYEE':
        this.loginForm.patchValue({ 
          username: 'funcionario@teste.com', 
          password: 'func123' 
        });
        break;
    }
    
    this.onSubmit();
  }
}