import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DepartmentService } from '../../../services/department.service';
import { TeamService } from '../../../services/team.service';
import { Department, Team } from '../../../../../shared/models/hr.model';

@Component({
  selector: 'app-team-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatTableModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './team-list.component.html',
  styleUrls: ['./team-list.component.scss']
})
export class TeamListComponent implements OnInit {
  teams: Team[] = [];
  departments: Department[] = [];
  displayedColumns = ['name', 'department', 'description', 'actions'];
  editingId: number | null = null;
  form;

  constructor(
    private fb: FormBuilder,
    private teamService: TeamService,
    private departmentService: DepartmentService,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      departmentId: [null as number | null],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.departmentService.list().subscribe((data) => (this.departments = data));
    this.teamService.list().subscribe((data) => (this.teams = data));
  }

  edit(item: Team): void {
    this.editingId = item.id || null;
    this.form.patchValue({
      name: item.name,
      departmentId: item.departmentId || null,
      description: item.description || ''
    });
  }

  reset(): void {
    this.editingId = null;
    this.form.reset({ name: '', departmentId: null, description: '' });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload = {
      name: (raw.name ?? '').trim(),
      departmentId: raw.departmentId || null,
      description: raw.description?.trim() || null
    } as Team;

    const request = this.editingId
      ? this.teamService.update(this.editingId, payload)
      : this.teamService.create(payload);

    request.subscribe({
      next: () => {
        this.snackBar.open(this.editingId ? 'Time atualizado.' : 'Time criado.', 'OK', { duration: 2200 });
        this.reset();
        this.load();
      }
    });
  }

  remove(item: Team): void {
    if (!item.id) {
      return;
    }

    this.teamService.delete(item.id).subscribe({
      next: () => {
        this.snackBar.open('Time removido.', 'OK', { duration: 2200 });
        this.load();
      }
    });
  }
}
