import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { PayrollPhaseService } from '../../../admin/services/payroll-phase.service';
import { Payslip } from '../../../../shared/models/phase4.model';

@Component({
  selector: 'app-employee-payroll',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule],
  templateUrl: './payroll.component.html',
  styleUrls: ['./payroll.component.scss']
})
export class EmployeePayrollComponent implements OnInit {
  payslips: Payslip[] = [];
  columns = ['createdAt', 'gross', 'deductions', 'tax', 'overtime', 'bonus', 'net'];

  constructor(private payrollService: PayrollPhaseService) {}

  ngOnInit(): void {
    this.payrollService.listOwnPayslips().subscribe((payslips) => (this.payslips = payslips));
  }
}
