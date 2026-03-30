import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TimeReport } from '../../../shared/models/time-absence.model';

@Injectable({ providedIn: 'root' })
export class TimeReportPhaseService {
  private readonly apiUrl = `${environment.apiUrl}/api/reports/time`;

  constructor(private http: HttpClient) {}

  report(startDate?: string, endDate?: string): Observable<TimeReport> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get<TimeReport>(this.apiUrl, { params });
  }

  exportCsv(startDate?: string, endDate?: string): Observable<Blob> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get(`${this.apiUrl}/export/csv`, { params, responseType: 'blob' });
  }

  exportPdf(startDate?: string, endDate?: string): Observable<Blob> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    return this.http.get(`${this.apiUrl}/export/pdf`, { params, responseType: 'blob' });
  }
}
