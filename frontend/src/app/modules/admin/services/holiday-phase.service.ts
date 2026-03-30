import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Holiday } from '../../../shared/models/time-absence.model';

@Injectable({ providedIn: 'root' })
export class HolidayPhaseService {
  private readonly apiUrl = `${environment.apiUrl}/api/holidays`;

  constructor(private http: HttpClient) {}

  list(): Observable<Holiday[]> {
    return this.http.get<Holiday[]>(this.apiUrl);
  }

  create(payload: Holiday): Observable<Holiday> {
    return this.http.post<Holiday>(this.apiUrl, payload);
  }

  update(id: number, payload: Holiday): Observable<Holiday> {
    return this.http.put<Holiday>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
