import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PerformanceGoal, PerformanceReview } from '../../../shared/models/phase3.model';

@Injectable({ providedIn: 'root' })
export class PerformancePhaseService {
  private readonly apiUrl = `${environment.apiUrl}/api/performance`;

  constructor(private http: HttpClient) {}

  createGoal(employeeId: number, payload: PerformanceGoal): Observable<PerformanceGoal> {
    return this.http.post<PerformanceGoal>(`${this.apiUrl}/goals/employee/${employeeId}`, payload);
  }

  updateGoal(id: number, payload: PerformanceGoal): Observable<PerformanceGoal> {
    return this.http.put<PerformanceGoal>(`${this.apiUrl}/goals/${id}`, payload);
  }

  listCompanyGoals(): Observable<PerformanceGoal[]> {
    return this.http.get<PerformanceGoal[]>(`${this.apiUrl}/goals/company`);
  }

  listOwnGoals(): Observable<PerformanceGoal[]> {
    return this.http.get<PerformanceGoal[]>(`${this.apiUrl}/goals/me`);
  }

  createReview(payload: Pick<PerformanceReview, 'employeeId' | 'periodStart' | 'periodEnd'>): Observable<PerformanceReview> {
    return this.http.post<PerformanceReview>(`${this.apiUrl}/reviews`, payload);
  }

  submitSelfReview(id: number, payload: Pick<PerformanceReview, 'selfScore' | 'selfComment'>): Observable<PerformanceReview> {
    return this.http.post<PerformanceReview>(`${this.apiUrl}/reviews/${id}/self`, payload);
  }

  submitManagerReview(id: number, payload: Pick<PerformanceReview, 'managerScore' | 'managerFeedback'>): Observable<PerformanceReview> {
    return this.http.post<PerformanceReview>(`${this.apiUrl}/reviews/${id}/manager`, payload);
  }

  listCompanyReviews(): Observable<PerformanceReview[]> {
    return this.http.get<PerformanceReview[]>(`${this.apiUrl}/reviews/company`);
  }

  listOwnReviews(): Observable<PerformanceReview[]> {
    return this.http.get<PerformanceReview[]>(`${this.apiUrl}/reviews/me`);
  }
}
