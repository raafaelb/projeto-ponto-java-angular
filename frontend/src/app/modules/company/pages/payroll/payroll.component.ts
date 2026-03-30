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
import { PayrollPhaseService } from '../../../admin/services/payroll-phase.service';
import { EmployeeService } from '../../../admin/services/employee.service';
import { Employee } from '../../../../shared/models/funcionario.model';
import { PayrollCycle, Payslip } from '../../../../shared/models/phase4.model';

@Component({
  selector: 'app-company-payroll',
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
  templateUrl: './payroll.component.html',
  styleUrls: ['./payroll.component.scss']
})
export class CompanyPayrollComponent implements OnInit {
  employees: Employee[] = [];
  cycles: PayrollCycle[] = [];
  payslips: Payslip[] = [];
  cycleColumns = ['period', 'paymentDate', 'status', 'actions'];
  payslipColumns = ['employee', 'gross', 'deductions', 'tax', 'overtime', 'bonus', 'net'];

  cycleForm;
  payslipForm;

  constructor(
    private fb: FormBuilder,
    private payrollService: PayrollPhaseService,
    private employeeService: EmployeeService,
    private snackBar: MatSnackBar
  ) {
    this.cycleForm = this.fb.group({
      periodStart: ['', Validators.required],
      periodEnd: ['', Validators.required],
      paymentDate: [''],
      notes: ['']
    });
    this.payslipForm = this.fb.group({
      payrollCycleId: [null as number | null, Validators.required],
      employeeId: [null as number | null, Validators.required],
      grossPay: [0, Validators.required],
      deductions: [0, Validators.required],
      taxWithheld: [0, Validators.required],
      overtimePay: [0, Validators.required],
      bonusPay: [0, Validators.required]
    });
  }

  ngOnInit(): void {
    this.employeeService.list().subscribe((employees) => (this.employees = employees));
    this.load();
  }

  load(): void {
    this.payrollService.listCycles().subscribe((cycles) => (this.cycles = cycles));
    this.payrollService.listCompanyPayslips().subscribe((payslips) => (this.payslips = payslips));
  }

  createCycle(): void {
    if (this.cycleForm.invalid) return;
    const value = this.cycleForm.getRawValue();
    this.payrollService
      .createCycle({
        periodStart: value.periodStart || '',
        periodEnd: value.periodEnd || '',
        paymentDate: value.paymentDate || null,
        notes: value.notes?.trim() || null
      })
      .subscribe(() => {
        this.snackBar.open('Ciclo de folha criado.', 'OK', { duration: 2000 });
        this.cycleForm.reset({ periodStart: '', periodEnd: '', paymentDate: '', notes: '' });
        this.load();
      });
  }

  closeCycle(cycle: PayrollCycle): void {
    if (!cycle.id) return;
    this.payrollService.closeCycle(cycle.id).subscribe(() => this.load());
  }

  createPayslip(): void {
    if (this.payslipForm.invalid) return;
    const value = this.payslipForm.getRawValue();
    this.payrollService
      .createPayslip({
        payrollCycleId: Number(value.payrollCycleId),
        employeeId: Number(value.employeeId),
        grossPay: Number(value.grossPay),
        deductions: Number(value.deductions),
        taxWithheld: Number(value.taxWithheld),
        overtimePay: Number(value.overtimePay),
        bonusPay: Number(value.bonusPay)
      } as Payslip)
      .subscribe(() => {
        this.snackBar.open('Holerite gerado.', 'OK', { duration: 2000 });
        this.payslipForm.reset({
          payrollCycleId: null,
          employeeId: null,
          grossPay: 0,
          deductions: 0,
          taxWithheld: 0,
          overtimePay: 0,
          bonusPay: 0
        });
        this.load();
      });
  }
}
