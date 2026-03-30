import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AttendanceAnomaly } from '../../../shared/models/time-absence.model';

@Injectable({ providedIn: 'root' })
export class AnomalyService {
  private readonly apiUrl = `${environment.apiUrl}/api/anomalies`;

  constructor(private http: HttpClient) {}

  generate(startDate?: string, endDate?: string): Observable<AttendanceAnomaly[]> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.post<AttendanceAnomaly[]>(`${this.apiUrl}/generate`, {}, { params });
  }

  list(resolved?: boolean): Observable<AttendanceAnomaly[]> {
    let params = new HttpParams();
    if (resolved !== undefined) {
      params = params.set('resolved', String(resolved));
    }
    return this.http.get<AttendanceAnomaly[]>(this.apiUrl, { params });
  }

  resolve(id: number, comment?: string): Observable<AttendanceAnomaly> {
    return this.http.post<AttendanceAnomaly>(`${this.apiUrl}/${id}/resolve`, { comment: comment || null });
  }
}
