import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CompliancePhaseService } from '../../../admin/services/compliance-phase.service';
import { ComplianceAuditEvent, PolicyAcknowledgment, PolicyDocument } from '../../../../shared/models/phase4.model';

@Component({
  selector: 'app-company-compliance',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatTableModule,
    MatInputModule,
    MatSlideToggleModule,
    MatSnackBarModule
  ],
  templateUrl: './compliance.component.html',
  styleUrls: ['./compliance.component.scss']
})
export class CompanyComplianceComponent implements OnInit {
  policies: PolicyDocument[] = [];
  acknowledgments: PolicyAcknowledgment[] = [];
  events: ComplianceAuditEvent[] = [];

  policyColumns = ['title', 'version', 'effectiveDate', 'active'];
  ackColumns = ['policy', 'status', 'acknowledgedAt'];
  eventColumns = ['eventType', 'severity', 'message', 'createdBy', 'createdAt'];

  policyForm;

  constructor(private fb: FormBuilder, private complianceService: CompliancePhaseService, private snackBar: MatSnackBar) {
    this.policyForm = this.fb.group({
      title: ['', Validators.required],
      version: ['1.0', Validators.required],
      effectiveDate: ['', Validators.required],
      contentSummary: [''],
      active: [true]
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.complianceService.listCompanyPolicies().subscribe((policies) => (this.policies = policies));
    this.complianceService.listCompanyAcks(false).subscribe((acks) => (this.acknowledgments = acks));
    this.complianceService.listAuditEvents().subscribe((events) => (this.events = events));
  }

  createPolicy(): void {
    if (this.policyForm.invalid) return;
    const value = this.policyForm.getRawValue();
    this.complianceService
      .createPolicy({
        title: (value.title || '').trim(),
        version: (value.version || '').trim(),
        effectiveDate: value.effectiveDate || '',
        contentSummary: value.contentSummary?.trim() || null,
        active: !!value.active
      })
      .subscribe(() => {
        this.snackBar.open('Politica criada.', 'OK', { duration: 2000 });
        this.policyForm.reset({ title: '', version: '1.0', effectiveDate: '', contentSummary: '', active: true });
        this.load();
      });
  }
}
