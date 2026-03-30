import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CompliancePhaseService } from '../../../admin/services/compliance-phase.service';
import { PolicyAcknowledgment, PolicyDocument } from '../../../../shared/models/phase4.model';

@Component({
  selector: 'app-employee-compliance',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatTableModule, MatSnackBarModule],
  templateUrl: './compliance.component.html',
  styleUrls: ['./compliance.component.scss']
})
export class EmployeeComplianceComponent implements OnInit {
  policies: PolicyDocument[] = [];
  acknowledgments: PolicyAcknowledgment[] = [];
  policyColumns = ['title', 'version', 'effectiveDate', 'actions'];
  ackColumns = ['policy', 'status', 'acknowledgedAt'];

  constructor(private complianceService: CompliancePhaseService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.complianceService.listEmployeePolicies().subscribe((policies) => (this.policies = policies));
    this.complianceService.listOwnAcks().subscribe((acks) => (this.acknowledgments = acks));
  }

  acknowledge(policy: PolicyDocument): void {
    if (!policy.id) return;
    this.complianceService.acknowledgePolicy(policy.id).subscribe(() => {
      this.snackBar.open('Politica reconhecida.', 'OK', { duration: 2000 });
      this.load();
    });
  }
}
