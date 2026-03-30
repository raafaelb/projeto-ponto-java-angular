import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AbsenceService } from '../../../admin/services/absence.service';
import { AbsenceRequest } from '../../../../shared/models/time-absence.model';

@Component({
  selector: 'app-company-absences',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatTableModule, MatSnackBarModule],
  templateUrl: './absences.component.html',
  styleUrls: ['./absences.component.scss']
})
export class CompanyAbsencesComponent implements OnInit {
  requests: AbsenceRequest[] = [];
  displayedColumns = ['employee', 'type', 'period', 'reason', 'status', 'actions'];

  constructor(private absenceService: AbsenceService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.absenceService.listCompany(false).subscribe((data) => (this.requests = data));
  }

  approve(item: AbsenceRequest): void {
    if (!item.id) return;
    this.absenceService.approve(item.id).subscribe(() => {
      this.snackBar.open('Solicitacao aprovada.', 'OK', { duration: 2200 });
      this.load();
    });
  }

  reject(item: AbsenceRequest): void {
    if (!item.id) return;
    this.absenceService.reject(item.id).subscribe(() => {
      this.snackBar.open('Solicitacao rejeitada.', 'OK', { duration: 2200 });
      this.load();
    });
  }
}
