import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Department } from '../../../shared/models/hr.model';

@Injectable({ providedIn: 'root' })
export class DepartmentService {
  private readonly apiUrl = `${environment.apiUrl}/api/departments`;

  constructor(private http: HttpClient) {}

  list(): Observable<Department[]> {
    return this.http.get<Department[]>(this.apiUrl);
  }

  create(payload: Department): Observable<Department> {
    return this.http.post<Department>(this.apiUrl, payload);
  }

  update(id: number, payload: Department): Observable<Department> {
    return this.http.put<Department>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
