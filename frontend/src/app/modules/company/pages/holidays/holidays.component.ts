import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { HolidayPhaseService } from '../../../admin/services/holiday-phase.service';
import { Holiday } from '../../../../shared/models/time-absence.model';

@Component({
  selector: 'app-company-holidays',
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
    MatSlideToggleModule
  ],
  templateUrl: './holidays.component.html',
  styleUrls: ['./holidays.component.scss']
})
export class CompanyHolidaysComponent implements OnInit {
  holidays: Holiday[] = [];
  displayedColumns = ['date', 'name', 'optional', 'actions'];
  editingId: number | null = null;
  form;

  constructor(private fb: FormBuilder, private holidayService: HolidayPhaseService) {
    this.form = this.fb.group({
      holidayDate: ['', Validators.required],
      name: ['', Validators.required],
      optionalHoliday: [false]
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.holidayService.list().subscribe((data) => (this.holidays = data));
  }

  edit(h: Holiday): void {
    this.editingId = h.id || null;
    this.form.patchValue({
      holidayDate: h.holidayDate,
      name: h.name,
      optionalHoliday: !!h.optionalHoliday
    });
  }

  reset(): void {
    this.editingId = null;
    this.form.reset({ holidayDate: '', name: '', optionalHoliday: false });
  }

  save(): void {
    if (this.form.invalid) return;
    const payload = this.form.getRawValue() as Holiday;
    const req = this.editingId
      ? this.holidayService.update(this.editingId, payload)
      : this.holidayService.create(payload);
    req.subscribe(() => {
      this.reset();
      this.load();
    });
  }

  remove(h: Holiday): void {
    if (!h.id) return;
    this.holidayService.delete(h.id).subscribe(() => this.load());
  }
}
