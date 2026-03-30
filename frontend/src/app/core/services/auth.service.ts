import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { AuthUser, LoginResponse } from '../../shared/models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'user_info';

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/api/auth/login`, { username, password }).pipe(
      tap((response) => {
        localStorage.setItem(this.TOKEN_KEY, response.token);
        localStorage.setItem(this.USER_KEY, JSON.stringify(response.user));
      }),
      catchError((error) => this.handleError(error))
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getUser(): AuthUser | null {
    const raw = localStorage.getItem(this.USER_KEY);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as AuthUser;
    } catch {
      return null;
    }
  }

  getUserRole(): AuthUser['role'] | null {
    return this.getUser()?.role ?? null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  redirectByRole(): void {
    const role = this.getUserRole();
    if (role === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
      return;
    }

    if (role === 'COMPANY') {
      this.router.navigate(['/company/dashboard']);
      return;
    }

    if (role === 'EMPLOYEE') {
      this.router.navigate(['/employee/workday']);
      return;
    }

    this.router.navigate(['/login']);
  }

  private handleError(error: HttpErrorResponse) {
    if (error.status === 401) {
      return throwError(() => new Error('Credenciais invalidas.'));
    }

    if (error.status >= 500) {
      return throwError(() => new Error('Erro interno do servidor.'));
    }

    return throwError(() => new Error(error.error?.message || 'Falha ao autenticar.'));
  }
}
