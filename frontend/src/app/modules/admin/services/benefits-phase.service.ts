import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BenefitEnrollment, BenefitPlan } from '../../../shared/models/phase4.model';

@Injectable({ providedIn: 'root' })
export class BenefitsPhaseService {
  private readonly apiUrl = `${environment.apiUrl}/api/benefits`;

  constructor(private http: HttpClient) {}

  createPlan(payload: BenefitPlan): Observable<BenefitPlan> {
    return this.http.post<BenefitPlan>(`${this.apiUrl}/plans`, payload);
  }

  listCompanyPlans(): Observable<BenefitPlan[]> {
    return this.http.get<BenefitPlan[]>(`${this.apiUrl}/plans/company`);
  }

  listEmployeeAvailablePlans(): Observable<BenefitPlan[]> {
    return this.http.get<BenefitPlan[]>(`${this.apiUrl}/plans/me`);
  }

  requestEnrollment(benefitPlanId: number): Observable<BenefitEnrollment> {
    return this.http.post<BenefitEnrollment>(`${this.apiUrl}/enrollments`, { benefitPlanId });
  }

  listOwnEnrollments(): Observable<BenefitEnrollment[]> {
    return this.http.get<BenefitEnrollment[]>(`${this.apiUrl}/enrollments/me`);
  }

  listCompanyEnrollments(onlyPending = false): Observable<BenefitEnrollment[]> {
    const params = new HttpParams().set('onlyPending', String(onlyPending));
    return this.http.get<BenefitEnrollment[]>(`${this.apiUrl}/enrollments/company`, { params });
  }

  approveEnrollment(id: number, comment?: string): Observable<BenefitEnrollment> {
    return this.http.post<BenefitEnrollment>(`${this.apiUrl}/enrollments/${id}/approve`, { comment: comment || null });
  }

  cancelEnrollment(id: number, comment?: string): Observable<BenefitEnrollment> {
    return this.http.post<BenefitEnrollment>(`${this.apiUrl}/enrollments/${id}/cancel`, { comment: comment || null });
  }
}
