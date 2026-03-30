import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ComplianceAuditEvent, PolicyAcknowledgment, PolicyDocument } from '../../../shared/models/phase4.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CompliancePhaseService {
  private readonly apiUrl = `${environment.apiUrl}/api/compliance`;

  constructor(private http: HttpClient) {}

  createPolicy(payload: PolicyDocument): Observable<PolicyDocument> {
    return this.http.post<PolicyDocument>(`${this.apiUrl}/policies`, payload);
  }

  listCompanyPolicies(): Observable<PolicyDocument[]> {
    return this.http.get<PolicyDocument[]>(`${this.apiUrl}/policies/company`);
  }

  listEmployeePolicies(): Observable<PolicyDocument[]> {
    return this.http.get<PolicyDocument[]>(`${this.apiUrl}/policies/me`);
  }

  acknowledgePolicy(policyId: number): Observable<PolicyAcknowledgment> {
    return this.http.post<PolicyAcknowledgment>(`${this.apiUrl}/policies/${policyId}/ack`, {});
  }

  listOwnAcks(): Observable<PolicyAcknowledgment[]> {
    return this.http.get<PolicyAcknowledgment[]>(`${this.apiUrl}/acks/me`);
  }

  listCompanyAcks(onlyPending = false): Observable<PolicyAcknowledgment[]> {
    const params = new HttpParams().set('onlyPending', String(onlyPending));
    return this.http.get<PolicyAcknowledgment[]>(`${this.apiUrl}/acks/company`, { params });
  }

  listAuditEvents(): Observable<ComplianceAuditEvent[]> {
    return this.http.get<ComplianceAuditEvent[]>(`${this.apiUrl}/audit-events`);
  }
}
