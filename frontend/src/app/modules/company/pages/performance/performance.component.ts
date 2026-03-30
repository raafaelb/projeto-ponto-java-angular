import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EmployeeService } from '../../../admin/services/employee.service';
import { PerformancePhaseService } from '../../../admin/services/performance-phase.service';
import { Employee } from '../../../../shared/models/funcionario.model';
import { PerformanceGoal, PerformanceReview } from '../../../../shared/models/phase3.model';

@Component({
  selector: 'app-company-performance',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatTableModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './performance.component.html',
  styleUrls: ['./performance.component.scss']
})
export class CompanyPerformanceComponent implements OnInit {
  employees: Employee[] = [];
  goals: PerformanceGoal[] = [];
  reviews: PerformanceReview[] = [];

  goalColumns = ['employee', 'title', 'weight', 'dueDate', 'status'];
  reviewColumns = ['employee', 'period', 'selfScore', 'managerScore', 'status', 'actions'];

  goalForm;
  reviewForm;
  managerReviewForm;

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private performanceService: PerformancePhaseService,
    private snackBar: MatSnackBar
  ) {
    this.goalForm = this.fb.group({
      employeeId: [null as number | null, Validators.required],
      title: ['', Validators.required],
      description: [''],
      weight: [20, [Validators.required, Validators.min(1), Validators.max(100)]],
      dueDate: [''],
      status: ['NOT_STARTED']
    });

    this.reviewForm = this.fb.group({
      employeeId: [null as number | null, Validators.required],
      periodStart: ['', Validators.required],
      periodEnd: ['', Validators.required]
    });

    this.managerReviewForm = this.fb.group({
      managerScore: [4, [Validators.required, Validators.min(1), Validators.max(5)]],
      managerFeedback: ['']
    });
  }

  ngOnInit(): void {
    this.employeeService.list().subscribe((employees) => (this.employees = employees));
    this.load();
  }

  load(): void {
    this.performanceService.listCompanyGoals().subscribe((goals) => (this.goals = goals));
    this.performanceService.listCompanyReviews().subscribe((reviews) => (this.reviews = reviews));
  }

  createGoal(): void {
    if (this.goalForm.invalid) {
      this.goalForm.markAllAsTouched();
      return;
    }

    const value = this.goalForm.getRawValue();
    const employeeId = value.employeeId as number;
    const payload: PerformanceGoal = {
      title: (value.title || '').trim(),
      description: value.description?.trim() || null,
      weight: Number(value.weight),
      dueDate: value.dueDate || null,
      status: value.status as PerformanceGoal['status']
    };

    this.performanceService.createGoal(employeeId, payload).subscribe(() => {
      this.snackBar.open('Meta cadastrada.', 'OK', { duration: 2200 });
      this.goalForm.reset({ employeeId: null, title: '', description: '', weight: 20, dueDate: '', status: 'NOT_STARTED' });
      this.load();
    });
  }

  createReview(): void {
    if (this.reviewForm.invalid) {
      this.reviewForm.markAllAsTouched();
      return;
    }
    const value = this.reviewForm.getRawValue();
    this.performanceService
      .createReview({
        employeeId: value.employeeId as number,
        periodStart: value.periodStart || '',
        periodEnd: value.periodEnd || ''
      })
      .subscribe(() => {
        this.snackBar.open('Avaliacao criada.', 'OK', { duration: 2200 });
        this.reviewForm.reset({ employeeId: null, periodStart: '', periodEnd: '' });
        this.load();
      });
  }

  closeReview(review: PerformanceReview): void {
    if (!review.id) return;
    const managerScore = Number(this.managerReviewForm.getRawValue().managerScore || 4);
    const managerFeedback = this.managerReviewForm.getRawValue().managerFeedback?.trim() || '';

    this.performanceService.submitManagerReview(review.id, { managerScore, managerFeedback }).subscribe(() => {
      this.snackBar.open('Feedback do gestor registrado.', 'OK', { duration: 2200 });
      this.load();
    });
  }
}
