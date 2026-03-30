import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UsuarioService, UserPayload } from '../../../services/usuario.service';
import { AuthService } from '../../../../../core/services/auth.service';
import { CompanyService } from '../../../services/company.service';
import { Company } from '../../../../../shared/models/company.model';
import { AuthUser } from '../../../../../shared/models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatInputModule,
    MatSlideToggleModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {
  displayedColumns = ['name', 'email', 'role', 'active', 'actions'];
  users: AuthUser[] = [];
  companies: Company[] = [];
  editingId: number | null = null;
  isAdmin = false;
  form;

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private companyService: CompanyService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      username: ['', [Validators.required]],
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: [''],
      role: ['COMPANY' as 'ADMIN' | 'COMPANY' | 'EMPLOYEE', [Validators.required]],
      companyId: [null as number | null],
      active: [true]
    });
  }

  ngOnInit(): void {
    this.isAdmin = this.authService.getUserRole() === 'ADMIN';
    this.loadCompanies();
    this.loadUsers();

    if (!this.isAdmin) {
      this.form.patchValue({
        companyId: this.authService.getUser()?.companyId || null,
        role: 'COMPANY'
      });
    }
  }

  loadCompanies(): void {
    if (!this.isAdmin) {
      return;
    }

    this.companyService.list().subscribe({
      next: (companies) => (this.companies = companies)
    });
  }

  loadUsers(): void {
    const companyId = this.authService.getUser()?.companyId || undefined;
    this.usuarioService.list(companyId).subscribe({
      next: (users) => {
        this.users = users;
      }
    });
  }

  edit(user: AuthUser): void {
    this.editingId = user.id;
    this.form.patchValue({
      username: user.username,
      name: user.name,
      email: user.email,
      password: '',
      role: user.role,
      companyId: user.companyId || null,
      active: user.active ?? true
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.form.reset({
      role: this.isAdmin ? 'COMPANY' : 'COMPANY',
      active: true,
      companyId: this.isAdmin ? null : this.authService.getUser()?.companyId || null
    });
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }

    const payload = this.form.getRawValue() as UserPayload;

    if (!this.isAdmin) {
      payload.role = 'COMPANY';
      payload.companyId = this.authService.getUser()?.companyId || null;
    }

    const request = this.editingId
      ? this.usuarioService.update(this.editingId, payload)
      : this.usuarioService.create(payload);

    request.subscribe({
      next: () => {
        this.snackBar.open(this.editingId ? 'Usuario atualizado.' : 'Usuario criado.', 'OK', {
          duration: 2400
        });
        this.resetForm();
        this.loadUsers();
      }
    });
  }

  remove(user: AuthUser): void {
    if (!user.id) {
      return;
    }

    this.usuarioService.delete(user.id).subscribe({
      next: () => {
        this.snackBar.open('Usuario removido.', 'OK', { duration: 2400 });
        this.loadUsers();
      }
    });
  }
}
