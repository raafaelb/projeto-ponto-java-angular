import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AbsenceRequest } from '../../../shared/models/time-absence.model';

@Injectable({ providedIn: 'root' })
export class AbsenceService {
  private readonly apiUrl = `${environment.apiUrl}/api/absences`;

  constructor(private http: HttpClient) {}

  create(payload: AbsenceRequest): Observable<AbsenceRequest> {
    return this.http.post<AbsenceRequest>(this.apiUrl, payload);
  }

  listOwn(): Observable<AbsenceRequest[]> {
    return this.http.get<AbsenceRequest[]>(`${this.apiUrl}/me`);
  }

  listCompany(onlyPending = false): Observable<AbsenceRequest[]> {
    const params = new HttpParams().set('onlyPending', String(onlyPending));
    return this.http.get<AbsenceRequest[]>(`${this.apiUrl}/company`, { params });
  }

  approve(id: number, comment?: string): Observable<AbsenceRequest> {
    return this.http.post<AbsenceRequest>(`${this.apiUrl}/${id}/approve`, { comment: comment || null });
  }

  reject(id: number, comment?: string): Observable<AbsenceRequest> {
    return this.http.post<AbsenceRequest>(`${this.apiUrl}/${id}/reject`, { comment: comment || null });
  }
}
