import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CompensationPhaseService } from '../../../admin/services/compensation-phase.service';
import { BonusRequest, SalaryAdjustment } from '../../../../shared/models/phase3.model';

@Component({
  selector: 'app-employee-compensation',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule
  ],
  templateUrl: './compensation.component.html',
  styleUrls: ['./compensation.component.scss']
})
export class EmployeeCompensationComponent implements OnInit {
  salaryAdjustments: SalaryAdjustment[] = [];
  bonusRequests: BonusRequest[] = [];

  salaryColumns = ['previousSalary', 'newSalary', 'effectiveDate', 'status', 'reviewComment'];
  bonusColumns = ['amount', 'referenceDate', 'status', 'reviewComment'];

  bonusForm;

  constructor(
    private fb: FormBuilder,
    private compensationService: CompensationPhaseService,
    private snackBar: MatSnackBar
  ) {
    this.bonusForm = this.fb.group({
      amount: [0, [Validators.required, Validators.min(1)]],
      referenceDate: ['', Validators.required],
      reason: ['']
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.compensationService.listOwnSalaryAdjustments().subscribe((data) => (this.salaryAdjustments = data));
    this.compensationService.listOwnBonusRequests().subscribe((data) => (this.bonusRequests = data));
  }

  requestBonus(): void {
    if (this.bonusForm.invalid) {
      this.bonusForm.markAllAsTouched();
      return;
    }

    const value = this.bonusForm.getRawValue();
    const payload: BonusRequest = {
      amount: Number(value.amount),
      referenceDate: value.referenceDate || '',
      reason: value.reason?.trim() || null
    };
    this.compensationService.createBonusRequest(payload).subscribe(() => {
      this.snackBar.open('Solicitacao de bonus enviada.', 'OK', { duration: 2200 });
      this.bonusForm.reset({ amount: 0, referenceDate: '', reason: '' });
      this.load();
    });
  }
}
