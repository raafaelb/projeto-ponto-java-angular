import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AbsenceService } from '../../../admin/services/absence.service';
import { OvertimePhaseService } from '../../../admin/services/overtime-phase.service';
import { AbsenceRequest, OvertimeRequest } from '../../../../shared/models/time-absence.model';

@Component({
  selector: 'app-employee-requests',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTableModule,
    MatSnackBarModule
  ],
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.scss']
})
export class EmployeeRequestsComponent implements OnInit {
  absences: AbsenceRequest[] = [];
  overtimes: OvertimeRequest[] = [];

  absenceColumns = ['type', 'period', 'status'];
  overtimeColumns = ['date', 'minutes', 'status'];

  absenceForm;
  overtimeForm;

  constructor(
    private fb: FormBuilder,
    private absenceService: AbsenceService,
    private overtimeService: OvertimePhaseService,
    private snackBar: MatSnackBar
  ) {
    this.absenceForm = this.fb.group({
      type: ['VACATION' as 'VACATION' | 'SICK_LEAVE' | 'PERSONAL', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      reason: ['']
    });

    this.overtimeForm = this.fb.group({
      workDate: ['', Validators.required],
      requestedMinutes: [60, Validators.required],
      reason: ['']
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.absenceService.listOwn().subscribe((data) => (this.absences = data));
    this.overtimeService.listOwn().subscribe((data) => (this.overtimes = data));
  }

  submitAbsence(): void {
    if (this.absenceForm.invalid) return;

    const payload = this.absenceForm.getRawValue() as AbsenceRequest;
    this.absenceService.create(payload).subscribe(() => {
      this.snackBar.open('Solicitacao de ausencia enviada.', 'OK', { duration: 2200 });
      this.absenceForm.reset({ type: 'VACATION', startDate: '', endDate: '', reason: '' });
      this.load();
    });
  }

  submitOvertime(): void {
    if (this.overtimeForm.invalid) return;

    const payload = {
      ...this.overtimeForm.getRawValue(),
      requestedMinutes: Number(this.overtimeForm.getRawValue().requestedMinutes)
    } as OvertimeRequest;

    this.overtimeService.create(payload).subscribe(() => {
      this.snackBar.open('Solicitacao de hora extra enviada.', 'OK', { duration: 2200 });
      this.overtimeForm.reset({ workDate: '', requestedMinutes: 60, reason: '' });
      this.load();
    });
  }
}
