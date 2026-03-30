import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { interval, Subscription } from 'rxjs';
import { EmployeeService } from '../../../admin/services/employee.service';
import { WorkdayCurrentStatus, WorkdayRecord, WorkdaySummary } from '../../../../shared/models/funcionario.model';

@Component({
  selector: 'app-workday',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatTableModule,
    MatSnackBarModule
  ],
  templateUrl: './workday.component.html',
  styleUrls: ['./workday.component.scss']
})
export class WorkdayComponent implements OnInit, OnDestroy {
  current: WorkdayCurrentStatus = { clockedIn: false, clockInAt: null, workedMinutesUntilNow: 0 };
  summary: WorkdaySummary = { records: [], totalWorkedMinutes: 0 };
  displayedColumns: string[] = ['clockIn', 'clockOut', 'worked'];
  tickerSub?: Subscription;

  filterForm;

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private snackBar: MatSnackBar
  ) {
    this.filterForm = this.fb.group({
      startDate: [''],
      endDate: ['']
    });
  }

  ngOnInit(): void {
    this.refreshCurrent();
    this.loadRecords();

    this.tickerSub = interval(60000).subscribe(() => {
      if (this.current.clockedIn) {
        this.current.workedMinutesUntilNow += 1;
      }
    });
  }

  ngOnDestroy(): void {
    this.tickerSub?.unsubscribe();
  }

  refreshCurrent(): void {
    this.employeeService.currentWorkday().subscribe({
      next: (status) => {
        this.current = status;
      }
    });
  }

  clockIn(): void {
    this.employeeService.clockIn().subscribe({
      next: () => {
        this.snackBar.open('Jornada iniciada.', 'OK', { duration: 2200 });
        this.refreshCurrent();
        this.loadRecords();
      }
    });
  }

  clockOut(): void {
    this.employeeService.clockOut().subscribe({
      next: () => {
        this.snackBar.open('Jornada finalizada.', 'OK', { duration: 2200 });
        this.refreshCurrent();
        this.loadRecords();
      }
    });
  }

  loadRecords(): void {
    const { startDate, endDate } = this.filterForm.getRawValue();
    this.employeeService.records(startDate || undefined, endDate || undefined).subscribe({
      next: (summary) => {
        this.summary = summary;
      }
    });
  }

  formatMinutes(totalMinutes: number): string {
    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;
    return `${hours}h ${minutes}m`;
  }
}
