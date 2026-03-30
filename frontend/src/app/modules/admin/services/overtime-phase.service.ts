import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OvertimeRequest } from '../../../shared/models/time-absence.model';

@Injectable({ providedIn: 'root' })
export class OvertimePhaseService {
  private readonly apiUrl = `${environment.apiUrl}/api/overtime`;

  constructor(private http: HttpClient) {}

  create(payload: OvertimeRequest): Observable<OvertimeRequest> {
    return this.http.post<OvertimeRequest>(this.apiUrl, payload);
  }

  listOwn(): Observable<OvertimeRequest[]> {
    return this.http.get<OvertimeRequest[]>(`${this.apiUrl}/me`);
  }

  listCompany(onlyPending = false): Observable<OvertimeRequest[]> {
    const params = new HttpParams().set('onlyPending', String(onlyPending));
    return this.http.get<OvertimeRequest[]>(`${this.apiUrl}/company`, { params });
  }

  approve(id: number, comment?: string): Observable<OvertimeRequest> {
    return this.http.post<OvertimeRequest>(`${this.apiUrl}/${id}/approve`, { comment: comment || null });
  }

  reject(id: number, comment?: string): Observable<OvertimeRequest> {
    return this.http.post<OvertimeRequest>(`${this.apiUrl}/${id}/reject`, { comment: comment || null });
  }
}
