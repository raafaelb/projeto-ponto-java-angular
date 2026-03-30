import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EmployeeService } from '../../../services/employee.service';
import { Employee } from '../../../../../shared/models/funcionario.model';

@Component({
  selector: 'app-funcionario-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    MatSlideToggleModule,
    MatSnackBarModule
  ],
  templateUrl: './funcionario-list.component.html',
  styleUrls: ['./funcionario-list.component.scss']
})
export class FuncionarioListComponent implements OnInit {
  displayedColumns = ['name', 'email', 'username', 'position', 'hiringDate', 'active', 'actions'];
  employees: Employee[] = [];
  editingId: number | null = null;
  isSaving = false;
  errorMessage = '';
  form;

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required]],
      password: [''],
      position: ['', [Validators.required]],
      hiringDate: ['', [Validators.required]],
      active: [true]
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.employeeService.list().subscribe({
      next: (employees) => {
        this.employees = employees;
      }
    });
  }

  edit(employee: Employee): void {
    this.editingId = employee.id || null;
    this.form.patchValue({
      name: employee.name,
      email: employee.email,
      username: employee.username,
      password: '',
      position: employee.position,
      hiringDate: employee.hiringDate,
      active: employee.active ?? true
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.form.reset({ active: true, password: '' });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload = {
      ...raw,
      username: raw.username?.trim(),
      email: raw.email?.trim(),
      password: raw.password?.trim() || undefined
    } as Employee;

    if (!this.editingId && !payload.password) {
      this.snackBar.open('Senha e obrigatoria para novo funcionario.', 'OK', { duration: 2800 });
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';

    const request = this.editingId
      ? this.employeeService.update(this.editingId, payload)
      : this.employeeService.create(payload);

    request.subscribe({
      next: () => {
        this.snackBar.open(this.editingId ? 'Funcionario atualizado.' : 'Funcionario criado.', 'OK', {
          duration: 2500
        });
        this.isSaving = false;
        this.resetForm();
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.message || 'Nao foi possivel salvar. Verifique username/email/senha e tente novamente.';
        this.snackBar.open(this.errorMessage, 'OK', { duration: 3200 });
        this.isSaving = false;
      }
    });
  }

  remove(employee: Employee): void {
    if (!employee.id) {
      return;
    }

    this.employeeService.delete(employee.id).subscribe({
      next: () => {
        this.snackBar.open('Funcionario removido.', 'OK', { duration: 2500 });
        this.load();
      }
    });
  }
}
