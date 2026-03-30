import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { TimeReportPhaseService } from '../../../admin/services/time-report-phase.service';
import { TimeReport } from '../../../../shared/models/time-absence.model';

@Component({
  selector: 'app-company-reports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatCardModule, MatButtonModule, MatTableModule],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class CompanyReportsComponent implements OnInit {
  report: TimeReport = { rows: [], totalWorkedMinutes: 0, totalApprovedOvertimeMinutes: 0, totalAbsenceDays: 0, totalAnomalies: 0 };
  displayedColumns = ['employee', 'department', 'team', 'worked', 'overtime', 'absences', 'anomalies'];
  form;

  constructor(private fb: FormBuilder, private reportService: TimeReportPhaseService) {
    this.form = this.fb.group({ startDate: [''], endDate: [''] });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const { startDate, endDate } = this.form.getRawValue();
    this.reportService.report(startDate || undefined, endDate || undefined).subscribe((data) => (this.report = data));
  }

  exportCsv(): void {
    const { startDate, endDate } = this.form.getRawValue();
    this.reportService.exportCsv(startDate || undefined, endDate || undefined).subscribe((blob) => {
      this.download(blob, 'time-report.csv');
    });
  }

  exportPdf(): void {
    const { startDate, endDate } = this.form.getRawValue();
    this.reportService.exportPdf(startDate || undefined, endDate || undefined).subscribe((blob) => {
      this.download(blob, 'time-report.pdf');
    });
  }

  private download(blob: Blob, fileName: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  formatMinutes(total: number): string {
    const h = Math.floor(total / 60);
    const m = total % 60;
    return `${h}h ${m}m`;
  }
}
