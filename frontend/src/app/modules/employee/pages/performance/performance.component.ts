import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PerformancePhaseService } from '../../../admin/services/performance-phase.service';
import { PerformanceGoal, PerformanceReview } from '../../../../shared/models/phase3.model';

@Component({
  selector: 'app-employee-performance',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatTableModule,
    MatInputModule,
    MatSnackBarModule
  ],
  templateUrl: './performance.component.html',
  styleUrls: ['./performance.component.scss']
})
export class EmployeePerformanceComponent implements OnInit {
  goals: PerformanceGoal[] = [];
  reviews: PerformanceReview[] = [];
  reviewColumns = ['period', 'selfScore', 'managerScore', 'status', 'actions'];
  goalColumns = ['title', 'weight', 'dueDate', 'status'];

  selfReviewForm;

  constructor(
    private fb: FormBuilder,
    private performanceService: PerformancePhaseService,
    private snackBar: MatSnackBar
  ) {
    this.selfReviewForm = this.fb.group({
      selfScore: [4, [Validators.required, Validators.min(1), Validators.max(5)]],
      selfComment: ['']
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.performanceService.listOwnGoals().subscribe((goals) => (this.goals = goals));
    this.performanceService.listOwnReviews().subscribe((reviews) => (this.reviews = reviews));
  }

  submitSelfReview(review: PerformanceReview): void {
    if (!review.id) return;
    const formValue = this.selfReviewForm.getRawValue();
    this.performanceService
      .submitSelfReview(review.id, {
        selfScore: Number(formValue.selfScore || 4),
        selfComment: formValue.selfComment?.trim() || ''
      })
      .subscribe(() => {
        this.snackBar.open('Autoavaliacao enviada.', 'OK', { duration: 2200 });
        this.load();
      });
  }
}
