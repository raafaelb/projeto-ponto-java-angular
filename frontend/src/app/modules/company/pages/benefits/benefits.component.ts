import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BenefitsPhaseService } from '../../../admin/services/benefits-phase.service';
import { BenefitEnrollment, BenefitPlan } from '../../../../shared/models/phase4.model';

@Component({
  selector: 'app-company-benefits',
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
  templateUrl: './benefits.component.html',
  styleUrls: ['./benefits.component.scss']
})
export class CompanyBenefitsComponent implements OnInit {
  plans: BenefitPlan[] = [];
  enrollments: BenefitEnrollment[] = [];
  planColumns = ['name', 'employerCost', 'employeeCost', 'active'];
  enrollmentColumns = ['employee', 'plan', 'status', 'actions'];
  planForm;

  constructor(private fb: FormBuilder, private benefitsService: BenefitsPhaseService, private snackBar: MatSnackBar) {
    this.planForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      monthlyEmployerCost: [0, Validators.required],
      monthlyEmployeeCost: [0, Validators.required],
      active: [true]
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.benefitsService.listCompanyPlans().subscribe((plans) => (this.plans = plans));
    this.benefitsService.listCompanyEnrollments(false).subscribe((enrollments) => (this.enrollments = enrollments));
  }

  createPlan(): void {
    if (this.planForm.invalid) return;
    const value = this.planForm.getRawValue();
    this.benefitsService
      .createPlan({
        name: (value.name || '').trim(),
        description: value.description?.trim() || null,
        monthlyEmployerCost: Number(value.monthlyEmployerCost),
        monthlyEmployeeCost: Number(value.monthlyEmployeeCost),
        active: !!value.active
      })
      .subscribe(() => {
        this.snackBar.open('Plano de beneficios criado.', 'OK', { duration: 2000 });
        this.planForm.reset({ name: '', description: '', monthlyEmployerCost: 0, monthlyEmployeeCost: 0, active: true });
        this.load();
      });
  }

  approve(item: BenefitEnrollment): void {
    if (!item.id) return;
    this.benefitsService.approveEnrollment(item.id).subscribe(() => this.load());
  }

  cancel(item: BenefitEnrollment): void {
    if (!item.id) return;
    this.benefitsService.cancelEnrollment(item.id).subscribe(() => this.load());
  }
}
