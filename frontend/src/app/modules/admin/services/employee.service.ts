import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Employee, WorkdayCurrentStatus, WorkdaySummary } from '../../../shared/models/funcionario.model';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly apiUrl = `${environment.apiUrl}/api/employees`;
  private readonly workdayUrl = `${environment.apiUrl}/api/workday`;

  constructor(private http: HttpClient) {}

  list(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.apiUrl);
  }

  create(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.apiUrl, employee);
  }

  update(id: number, employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiUrl}/${id}`, employee);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  currentWorkday(): Observable<WorkdayCurrentStatus> {
    return this.http.get<WorkdayCurrentStatus>(`${this.workdayUrl}/current`);
  }

  clockIn() {
    return this.http.post(`${this.workdayUrl}/clock-in`, {});
  }

  clockOut() {
    return this.http.post(`${this.workdayUrl}/clock-out`, {});
  }

  records(startDate?: string, endDate?: string): Observable<WorkdaySummary> {
    let params = new HttpParams();
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    if (endDate) {
      params = params.set('endDate', endDate);
    }

    return this.http.get<WorkdaySummary>(`${this.workdayUrl}/records`, { params });
  }
}
