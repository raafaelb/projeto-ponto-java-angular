import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { OvertimePhaseService } from '../../../admin/services/overtime-phase.service';
import { OvertimeRequest } from '../../../../shared/models/time-absence.model';

@Component({
  selector: 'app-company-overtime',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatTableModule, MatSnackBarModule],
  templateUrl: './overtime.component.html',
  styleUrls: ['./overtime.component.scss']
})
export class CompanyOvertimeComponent implements OnInit {
  requests: OvertimeRequest[] = [];
  displayedColumns = ['employee', 'workDate', 'minutes', 'reason', 'status', 'actions'];

  constructor(private overtimeService: OvertimePhaseService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.overtimeService.listCompany(false).subscribe((data) => (this.requests = data));
  }

  approve(item: OvertimeRequest): void {
    if (!item.id) return;
    this.overtimeService.approve(item.id).subscribe(() => {
      this.snackBar.open('Hora extra aprovada.', 'OK', { duration: 2200 });
      this.load();
    });
  }

  reject(item: OvertimeRequest): void {
    if (!item.id) return;
    this.overtimeService.reject(item.id).subscribe(() => {
      this.snackBar.open('Hora extra rejeitada.', 'OK', { duration: 2200 });
      this.load();
    });
  }
}
