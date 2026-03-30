import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BonusRequest, SalaryAdjustment } from '../../../shared/models/phase3.model';

@Injectable({ providedIn: 'root' })
export class CompensationPhaseService {
  private readonly apiUrl = `${environment.apiUrl}/api/compensation`;

  constructor(private http: HttpClient) {}

  createSalaryAdjustment(payload: SalaryAdjustment): Observable<SalaryAdjustment> {
    return this.http.post<SalaryAdjustment>(`${this.apiUrl}/salary-adjustments`, payload);
  }

  listCompanySalaryAdjustments(): Observable<SalaryAdjustment[]> {
    return this.http.get<SalaryAdjustment[]>(`${this.apiUrl}/salary-adjustments/company`);
  }

  listOwnSalaryAdjustments(): Observable<SalaryAdjustment[]> {
    return this.http.get<SalaryAdjustment[]>(`${this.apiUrl}/salary-adjustments/me`);
  }

  approveSalaryAdjustment(id: number, comment?: string): Observable<SalaryAdjustment> {
    return this.http.post<SalaryAdjustment>(`${this.apiUrl}/salary-adjustments/${id}/approve`, { comment: comment || null });
  }

  rejectSalaryAdjustment(id: number, comment?: string): Observable<SalaryAdjustment> {
    return this.http.post<SalaryAdjustment>(`${this.apiUrl}/salary-adjustments/${id}/reject`, { comment: comment || null });
  }

  createBonusRequest(payload: BonusRequest): Observable<BonusRequest> {
    return this.http.post<BonusRequest>(`${this.apiUrl}/bonus-requests`, payload);
  }

  listOwnBonusRequests(): Observable<BonusRequest[]> {
    return this.http.get<BonusRequest[]>(`${this.apiUrl}/bonus-requests/me`);
  }

  listCompanyBonusRequests(): Observable<BonusRequest[]> {
    return this.http.get<BonusRequest[]>(`${this.apiUrl}/bonus-requests/company`);
  }

  approveBonusRequest(id: number, comment?: string): Observable<BonusRequest> {
    return this.http.post<BonusRequest>(`${this.apiUrl}/bonus-requests/${id}/approve`, { comment: comment || null });
  }

  rejectBonusRequest(id: number, comment?: string): Observable<BonusRequest> {
    return this.http.post<BonusRequest>(`${this.apiUrl}/bonus-requests/${id}/reject`, { comment: comment || null });
  }
}
