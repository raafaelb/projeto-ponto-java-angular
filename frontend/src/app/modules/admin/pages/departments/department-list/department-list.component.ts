import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DepartmentService } from '../../../services/department.service';
import { Department } from '../../../../../shared/models/hr.model';

@Component({
  selector: 'app-department-list',
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
    MatSnackBarModule
  ],
  templateUrl: './department-list.component.html',
  styleUrls: ['./department-list.component.scss']
})
export class DepartmentListComponent implements OnInit {
  departments: Department[] = [];
  displayedColumns = ['name', 'description', 'actions'];
  editingId: number | null = null;
  form;

  constructor(
    private fb: FormBuilder,
    private departmentService: DepartmentService,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.departmentService.list().subscribe((data) => (this.departments = data));
  }

  edit(item: Department): void {
    this.editingId = item.id || null;
    this.form.patchValue({ name: item.name, description: item.description || '' });
  }

  reset(): void {
    this.editingId = null;
    this.form.reset({ name: '', description: '' });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload = {
      name: (raw.name ?? '').trim(),
      description: raw.description?.trim() || null
    } as Department;

    const request = this.editingId
      ? this.departmentService.update(this.editingId, payload)
      : this.departmentService.create(payload);

    request.subscribe({
      next: () => {
        this.snackBar.open(this.editingId ? 'Departamento atualizado.' : 'Departamento criado.', 'OK', { duration: 2200 });
        this.reset();
        this.load();
      }
    });
  }

  remove(item: Department): void {
    if (!item.id) {
      return;
    }

    this.departmentService.delete(item.id).subscribe({
      next: () => {
        this.snackBar.open('Departamento removido.', 'OK', { duration: 2200 });
        this.load();
      }
    });
  }
}
