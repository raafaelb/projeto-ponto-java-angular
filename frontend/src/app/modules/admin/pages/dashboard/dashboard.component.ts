import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CompanyService } from '../../services/company.service';
import { EmployeeService } from '../../services/employee.service';
import { UsuarioService } from '../../services/usuario.service';
import { DepartmentService } from '../../services/department.service';
import { TeamService } from '../../services/team.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  isLoading = true;
  companyCount = 0;
  employeeCount = 0;
  userCount = 0;
  departmentCount = 0;
  teamCount = 0;

  constructor(
    private companyService: CompanyService,
    private employeeService: EmployeeService,
    private usuarioService: UsuarioService,
    private departmentService: DepartmentService,
    private teamService: TeamService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    const companyId = user?.companyId || undefined;
    const isCompany = user?.role === 'COMPANY';

    this.companyService.list().subscribe({
      next: (companies) => {
        this.companyCount = companies.length;
      },
      error: () => {
        this.companyCount = 0;
      }
    });

    if (isCompany) {
      this.employeeService.list().subscribe({
        next: (employees) => {
          this.employeeCount = employees.length;
        }
      });

      this.departmentService.list().subscribe({
        next: (departments) => {
          this.departmentCount = departments.length;
        }
      });

      this.teamService.list().subscribe({
        next: (teams) => {
          this.teamCount = teams.length;
        }
      });
    }

    this.usuarioService.list(companyId).subscribe({
      next: (users) => {
        this.userCount = users.length;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }
}
