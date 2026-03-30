import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BenefitsPhaseService } from '../../../admin/services/benefits-phase.service';
import { BenefitEnrollment, BenefitPlan } from '../../../../shared/models/phase4.model';

@Component({
  selector: 'app-employee-benefits',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatTableModule, MatSnackBarModule],
  templateUrl: './benefits.component.html',
  styleUrls: ['./benefits.component.scss']
})
export class EmployeeBenefitsComponent implements OnInit {
  plans: BenefitPlan[] = [];
  enrollments: BenefitEnrollment[] = [];
  planColumns = ['name', 'employerCost', 'employeeCost', 'actions'];
  enrollmentColumns = ['plan', 'status', 'startDate', 'endDate', 'reviewComment'];

  constructor(private benefitsService: BenefitsPhaseService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.benefitsService.listEmployeeAvailablePlans().subscribe((plans) => (this.plans = plans));
    this.benefitsService.listOwnEnrollments().subscribe((enrollments) => (this.enrollments = enrollments));
  }

  request(plan: BenefitPlan): void {
    if (!plan.id) return;
    this.benefitsService.requestEnrollment(plan.id).subscribe(() => {
      this.snackBar.open('Solicitacao de adesao enviada.', 'OK', { duration: 2000 });
      this.load();
    });
  }
}
