import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CareerPhaseService } from '../../../admin/services/career-phase.service';
import { CareerLevel, PromotionRequest, SkillAssessment } from '../../../../shared/models/phase3.model';

@Component({
  selector: 'app-employee-career',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './career.component.html',
  styleUrls: ['./career.component.scss']
})
export class EmployeeCareerComponent implements OnInit {
  levels: CareerLevel[] = [];
  skills: SkillAssessment[] = [];
  promotions: PromotionRequest[] = [];

  skillColumns = ['skill', 'current', 'target', 'lastDate', 'notes'];
  promotionColumns = ['fromTo', 'effectiveDate', 'status', 'reviewComment'];

  promotionForm;

  constructor(
    private fb: FormBuilder,
    private careerService: CareerPhaseService,
    private snackBar: MatSnackBar
  ) {
    this.promotionForm = this.fb.group({
      toLevelId: [null as number | null, Validators.required],
      justification: [''],
      effectiveDate: ['']
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.careerService.listCareerLevels().subscribe((levels) => (this.levels = levels));
    this.careerService.listOwnSkills().subscribe((skills) => (this.skills = skills));
    this.careerService.listOwnPromotionRequests().subscribe((promotions) => (this.promotions = promotions));
  }

  requestPromotion(): void {
    if (this.promotionForm.invalid) {
      this.promotionForm.markAllAsTouched();
      return;
    }
    const value = this.promotionForm.getRawValue();
    this.careerService
      .createPromotionRequest({
        toLevelId: value.toLevelId as number,
        justification: value.justification?.trim() || null,
        effectiveDate: value.effectiveDate || null
      })
      .subscribe(() => {
        this.snackBar.open('Solicitacao de promocao enviada.', 'OK', { duration: 2200 });
        this.promotionForm.reset({ toLevelId: null, justification: '', effectiveDate: '' });
        this.load();
      });
  }
}
