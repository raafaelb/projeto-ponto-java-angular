import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { CompanyService } from '../../../services/company.service';

@Component({
  selector: 'app-company-form',
  templateUrl: './company-form.component.html',
  styleUrls: ['./company-form.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSnackBarModule,
    MatCardModule
  ]
})
export class CompanyFormComponent implements OnInit {
  companyForm: FormGroup;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute,
    private companyService: CompanyService
  ) {
    this.companyForm = this.fb.group({
      id: [''],
      nomeFantasia: ['', Validators.required],
      razaoSocial: ['', Validators.required],
      cnpj: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.isEditMode = true;
      this.companyService.getById(id).subscribe((company) => this.companyForm.patchValue(company));
    }
  }

  onSubmit(): void {
    if (this.companyForm.invalid) {
      return;
    }

    const payload = this.companyForm.getRawValue();
    const request = this.isEditMode
      ? this.companyService.update(payload.id, payload)
      : this.companyService.create(payload);

    request.subscribe({
      next: () => {
        this.snackBar.open(this.isEditMode ? 'Empresa atualizada.' : 'Empresa criada.', 'OK', { duration: 2400 });
        this.router.navigate(['/admin/companies']);
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/admin/companies']);
  }
}
