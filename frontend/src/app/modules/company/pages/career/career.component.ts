import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CareerPhaseService } from '../../../admin/services/career-phase.service';
import { EmployeeService } from '../../../admin/services/employee.service';
import { CareerLevel, PromotionRequest, SkillAssessment } from '../../../../shared/models/phase3.model';
import { Employee } from '../../../../shared/models/funcionario.model';

@Component({
  selector: 'app-company-career',
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
  templateUrl: './career.component.html',
  styleUrls: ['./career.component.scss']
})
export class CompanyCareerComponent implements OnInit {
  levels: CareerLevel[] = [];
  skills: SkillAssessment[] = [];
  promotions: PromotionRequest[] = [];
  employees: Employee[] = [];

  levelColumns = ['name', 'rank', 'salaryBand', 'actions'];
  skillColumns = ['employee', 'skill', 'current', 'target', 'lastDate'];
  promotionColumns = ['employee', 'fromTo', 'effectiveDate', 'status', 'actions'];

  levelForm;
  skillForm;
  assignForm;

  constructor(
    private fb: FormBuilder,
    private careerService: CareerPhaseService,
    private employeeService: EmployeeService,
    private snackBar: MatSnackBar
  ) {
    this.levelForm = this.fb.group({
      name: ['', Validators.required],
      rankOrder: [1, Validators.required],
      description: [''],
      minSalary: [0],
      maxSalary: [0]
    });

    this.skillForm = this.fb.group({
      employeeId: [null as number | null, Validators.required],
      skillName: ['', Validators.required],
      currentLevel: [1, [Validators.required, Validators.min(1), Validators.max(5)]],
      targetLevel: [3, [Validators.min(1), Validators.max(5)]],
      lastAssessedDate: [''],
      notes: ['']
    });

    this.assignForm = this.fb.group({
      employeeId: [null as number | null, Validators.required],
      levelId: [null as number | null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.employeeService.list().subscribe((employees) => (this.employees = employees));
    this.load();
  }

  load(): void {
    this.careerService.listCareerLevels().subscribe((levels) => (this.levels = levels));
    this.careerService.listCompanySkills().subscribe((skills) => (this.skills = skills));
    this.careerService.listCompanyPromotionRequests().subscribe((promotions) => (this.promotions = promotions));
  }

  createLevel(): void {
    if (this.levelForm.invalid) {
      this.levelForm.markAllAsTouched();
      return;
    }

    const value = this.levelForm.getRawValue();
    const payload: CareerLevel = {
      name: (value.name || '').trim(),
      rankOrder: Number(value.rankOrder),
      description: value.description?.trim() || null,
      minSalary: Number(value.minSalary || 0),
      maxSalary: Number(value.maxSalary || 0)
    };

    this.careerService.createCareerLevel(payload).subscribe(() => {
      this.snackBar.open('Nivel cadastrado.', 'OK', { duration: 2000 });
      this.levelForm.reset({ name: '', rankOrder: 1, description: '', minSalary: 0, maxSalary: 0 });
      this.load();
    });
  }

  createSkill(): void {
    if (this.skillForm.invalid) {
      this.skillForm.markAllAsTouched();
      return;
    }

    const value = this.skillForm.getRawValue();
    const payload: SkillAssessment = {
      employeeId: value.employeeId as number,
      skillName: (value.skillName || '').trim(),
      currentLevel: Number(value.currentLevel),
      targetLevel: Number(value.targetLevel || 0),
      lastAssessedDate: value.lastAssessedDate || null,
      notes: value.notes?.trim() || null
    };

    this.careerService.createSkillAssessment(payload).subscribe(() => {
      this.snackBar.open('Skill assessment registrada.', 'OK', { duration: 2000 });
      this.skillForm.reset({ employeeId: null, skillName: '', currentLevel: 1, targetLevel: 3, lastAssessedDate: '', notes: '' });
      this.load();
    });
  }

  assignLevel(): void {
    if (this.assignForm.invalid) {
      this.assignForm.markAllAsTouched();
      return;
    }
    const value = this.assignForm.getRawValue();
    this.careerService.assignEmployeeLevel(value.levelId as number, value.employeeId as number).subscribe(() => {
      this.snackBar.open('Nivel atribuido ao funcionario.', 'OK', { duration: 2000 });
      this.assignForm.reset({ employeeId: null, levelId: null });
      this.load();
    });
  }

  deleteLevel(level: CareerLevel): void {
    if (!level.id) return;
    this.careerService.deleteCareerLevel(level.id).subscribe(() => this.load());
  }

  approvePromotion(request: PromotionRequest): void {
    if (!request.id) return;
    this.careerService.approvePromotionRequest(request.id).subscribe(() => this.load());
  }

  rejectPromotion(request: PromotionRequest): void {
    if (!request.id) return;
    this.careerService.rejectPromotionRequest(request.id).subscribe(() => this.load());
  }
}
