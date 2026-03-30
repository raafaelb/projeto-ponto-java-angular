import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CareerLevel, PromotionRequest, SkillAssessment } from '../../../shared/models/phase3.model';
import { environment } from '../../../environments/environment';
import { Employee } from '../../../shared/models/funcionario.model';

@Injectable({ providedIn: 'root' })
export class CareerPhaseService {
  private readonly apiUrl = `${environment.apiUrl}/api/career`;

  constructor(private http: HttpClient) {}

  createCareerLevel(payload: CareerLevel): Observable<CareerLevel> {
    return this.http.post<CareerLevel>(`${this.apiUrl}/levels`, payload);
  }

  listCareerLevels(): Observable<CareerLevel[]> {
    return this.http.get<CareerLevel[]>(`${this.apiUrl}/levels`);
  }

  deleteCareerLevel(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/levels/${id}`);
  }

  assignEmployeeLevel(levelId: number, employeeId: number): Observable<Employee> {
    return this.http.post<Employee>(`${this.apiUrl}/levels/${levelId}/assign/${employeeId}`, {});
  }

  createSkillAssessment(payload: SkillAssessment): Observable<SkillAssessment> {
    return this.http.post<SkillAssessment>(`${this.apiUrl}/skills`, payload);
  }

  listCompanySkills(): Observable<SkillAssessment[]> {
    return this.http.get<SkillAssessment[]>(`${this.apiUrl}/skills/company`);
  }

  listOwnSkills(): Observable<SkillAssessment[]> {
    return this.http.get<SkillAssessment[]>(`${this.apiUrl}/skills/me`);
  }

  createPromotionRequest(payload: PromotionRequest): Observable<PromotionRequest> {
    return this.http.post<PromotionRequest>(`${this.apiUrl}/promotions`, payload);
  }

  listOwnPromotionRequests(): Observable<PromotionRequest[]> {
    return this.http.get<PromotionRequest[]>(`${this.apiUrl}/promotions/me`);
  }

  listCompanyPromotionRequests(): Observable<PromotionRequest[]> {
    return this.http.get<PromotionRequest[]>(`${this.apiUrl}/promotions/company`);
  }

  approvePromotionRequest(id: number, comment?: string): Observable<PromotionRequest> {
    return this.http.post<PromotionRequest>(`${this.apiUrl}/promotions/${id}/approve`, { comment: comment || null });
  }

  rejectPromotionRequest(id: number, comment?: string): Observable<PromotionRequest> {
    return this.http.post<PromotionRequest>(`${this.apiUrl}/promotions/${id}/reject`, { comment: comment || null });
  }
}
