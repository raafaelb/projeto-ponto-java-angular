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
import { MatSelectModule } from '@angular/material/select';
import { EmployeeService } from '../../../services/employee.service';
import { DepartmentService } from '../../../services/department.service';
import { TeamService } from '../../../services/team.service';
import { CareerPhaseService } from '../../../services/career-phase.service';
import { Employee } from '../../../../../shared/models/funcionario.model';
import { Department, Team } from '../../../../../shared/models/hr.model';
import { CareerLevel } from '../../../../../shared/models/phase3.model';

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
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './funcionario-list.component.html',
  styleUrls: ['./funcionario-list.component.scss']
})
export class FuncionarioListComponent implements OnInit {
  displayedColumns = ['employeeCode', 'name', 'position', 'department', 'team', 'manager', 'actions'];
  employees: Employee[] = [];
  departments: Department[] = [];
  teams: Team[] = [];
  careerLevels: CareerLevel[] = [];
  editingId: number | null = null;
  isSaving = false;
  errorMessage = '';
  form;

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private departmentService: DepartmentService,
    private teamService: TeamService,
    private careerService: CareerPhaseService,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      employeeCode: ['', [Validators.required]],
      username: ['', [Validators.required]],
      password: [''],
      position: ['', [Validators.required]],
      hiringDate: ['', [Validators.required]],
      birthDate: [''],
      phone: [''],
      address: [''],
      emergencyContactName: [''],
      emergencyContactPhone: [''],
      contractType: [''],
      contractStartDate: [''],
      contractEndDate: [''],
      departmentId: [null as number | null],
      teamId: [null as number | null],
      managerEmployeeId: [null as number | null],
      careerLevelId: [null as number | null],
      currentSalary: [0],
      active: [true]
    });
  }

  ngOnInit(): void {
    this.loadBaseData();
    this.load();
  }

  loadBaseData(): void {
    this.departmentService.list().subscribe((departments) => (this.departments = departments));
    this.teamService.list().subscribe((teams) => (this.teams = teams));
    this.careerService.listCareerLevels().subscribe((levels) => (this.careerLevels = levels));
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
      employeeCode: employee.employeeCode,
      username: employee.username,
      password: '',
      position: employee.position,
      hiringDate: employee.hiringDate,
      birthDate: employee.birthDate || '',
      phone: employee.phone || '',
      address: employee.address || '',
      emergencyContactName: employee.emergencyContactName || '',
      emergencyContactPhone: employee.emergencyContactPhone || '',
      contractType: employee.contractType || '',
      contractStartDate: employee.contractStartDate || '',
      contractEndDate: employee.contractEndDate || '',
      departmentId: employee.departmentId || null,
      teamId: employee.teamId || null,
      managerEmployeeId: employee.managerEmployeeId || null,
      careerLevelId: employee.careerLevelId || null,
      currentSalary: employee.currentSalary || 0,
      active: employee.active ?? true
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.form.reset({
      active: true,
      password: '',
      departmentId: null,
      teamId: null,
      managerEmployeeId: null,
      careerLevelId: null,
      currentSalary: 0
    });
    this.errorMessage = '';
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
      employeeCode: raw.employeeCode?.trim(),
      password: raw.password?.trim() || undefined,
      phone: raw.phone?.trim() || null,
      address: raw.address?.trim() || null,
      emergencyContactName: raw.emergencyContactName?.trim() || null,
      emergencyContactPhone: raw.emergencyContactPhone?.trim() || null,
      contractType: raw.contractType?.trim() || null
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
        this.errorMessage = error?.error?.message || 'Nao foi possivel salvar. Verifique os dados e tente novamente.';
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
