import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EmployeeService } from '../../../admin/services/employee.service';
import { CompensationPhaseService } from '../../../admin/services/compensation-phase.service';
import { Employee } from '../../../../shared/models/funcionario.model';
import { BonusRequest, SalaryAdjustment } from '../../../../shared/models/phase3.model';

@Component({
  selector: 'app-company-compensation',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './compensation.component.html',
  styleUrls: ['./compensation.component.scss']
})
export class CompanyCompensationComponent implements OnInit {
  employees: Employee[] = [];
  salaryAdjustments: SalaryAdjustment[] = [];
  bonusRequests: BonusRequest[] = [];

  salaryColumns = ['employee', 'previousSalary', 'newSalary', 'effectiveDate', 'status', 'actions'];
  bonusColumns = ['employee', 'amount', 'referenceDate', 'status', 'actions'];

  salaryForm;

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private compensationService: CompensationPhaseService,
    private snackBar: MatSnackBar
  ) {
    this.salaryForm = this.fb.group({
      employeeId: [null as number | null, Validators.required],
      newSalary: [0, [Validators.required, Validators.min(1)]],
      effectiveDate: ['', Validators.required],
      reason: ['']
    });
  }

  ngOnInit(): void {
    this.employeeService.list().subscribe((employees) => (this.employees = employees));
    this.load();
  }

  load(): void {
    this.compensationService.listCompanySalaryAdjustments().subscribe((data) => (this.salaryAdjustments = data));
    this.compensationService.listCompanyBonusRequests().subscribe((data) => (this.bonusRequests = data));
  }

  createSalaryAdjustment(): void {
    if (this.salaryForm.invalid) {
      this.salaryForm.markAllAsTouched();
      return;
    }

    const value = this.salaryForm.getRawValue();
    const payload: SalaryAdjustment = {
      employeeId: value.employeeId as number,
      newSalary: Number(value.newSalary),
      effectiveDate: value.effectiveDate || '',
      reason: value.reason?.trim() || null
    };

    this.compensationService.createSalaryAdjustment(payload).subscribe(() => {
      this.snackBar.open('Ajuste salarial criado.', 'OK', { duration: 2200 });
      this.salaryForm.reset({ employeeId: null, newSalary: 0, effectiveDate: '', reason: '' });
      this.load();
    });
  }

  approveSalary(item: SalaryAdjustment): void {
    if (!item.id) return;
    this.compensationService.approveSalaryAdjustment(item.id).subscribe(() => this.load());
  }

  rejectSalary(item: SalaryAdjustment): void {
    if (!item.id) return;
    this.compensationService.rejectSalaryAdjustment(item.id).subscribe(() => this.load());
  }

  approveBonus(item: BonusRequest): void {
    if (!item.id) return;
    this.compensationService.approveBonusRequest(item.id).subscribe(() => this.load());
  }

  rejectBonus(item: BonusRequest): void {
    if (!item.id) return;
    this.compensationService.rejectBonusRequest(item.id).subscribe(() => this.load());
  }
}
