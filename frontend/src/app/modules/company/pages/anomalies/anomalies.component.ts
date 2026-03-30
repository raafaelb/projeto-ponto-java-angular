import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { AnomalyService } from '../../../admin/services/anomaly.service';
import { AttendanceAnomaly } from '../../../../shared/models/time-absence.model';

@Component({
  selector: 'app-company-anomalies',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatTableModule],
  templateUrl: './anomalies.component.html',
  styleUrls: ['./anomalies.component.scss']
})
export class CompanyAnomaliesComponent implements OnInit {
  anomalies: AttendanceAnomaly[] = [];
  displayedColumns = ['date', 'employee', 'type', 'description', 'resolved', 'actions'];

  constructor(private anomalyService: AnomalyService) {}

  ngOnInit(): void {
    this.load();
  }

  generate(): void {
    this.anomalyService.generate().subscribe((data) => (this.anomalies = data));
  }

  load(): void {
    this.anomalyService.list(false).subscribe((data) => (this.anomalies = data));
  }

  resolve(item: AttendanceAnomaly): void {
    this.anomalyService.resolve(item.id).subscribe(() => this.load());
  }
}
