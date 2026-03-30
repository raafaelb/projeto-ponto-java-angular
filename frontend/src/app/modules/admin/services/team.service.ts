import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Team } from '../../../shared/models/hr.model';

@Injectable({ providedIn: 'root' })
export class TeamService {
  private readonly apiUrl = `${environment.apiUrl}/api/teams`;

  constructor(private http: HttpClient) {}

  list(): Observable<Team[]> {
    return this.http.get<Team[]>(this.apiUrl);
  }

  create(payload: Team): Observable<Team> {
    return this.http.post<Team>(this.apiUrl, payload);
  }

  update(id: number, payload: Team): Observable<Team> {
    return this.http.put<Team>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
