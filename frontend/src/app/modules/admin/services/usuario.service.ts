import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthUser } from '../../../shared/models/user.model';

export interface UserPayload {
  username: string;
  name: string;
  email: string;
  password?: string;
  role: 'ADMIN' | 'COMPANY' | 'EMPLOYEE';
  companyId?: number | null;
  active?: boolean;
}

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly apiUrl = `${environment.apiUrl}/api/users`;

  constructor(private http: HttpClient) {}

  list(companyId?: number): Observable<AuthUser[]> {
    let params = new HttpParams();
    if (companyId) {
      params = params.set('companyId', companyId);
    }

    return this.http.get<AuthUser[]>(this.apiUrl, { params });
  }

  create(payload: UserPayload): Observable<AuthUser> {
    return this.http.post<AuthUser>(this.apiUrl, payload);
  }

  update(id: number, payload: UserPayload): Observable<AuthUser> {
    return this.http.put<AuthUser>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
