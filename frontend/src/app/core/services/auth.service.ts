import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { UserInfo } from '../../shared/models/menu.model';
import { Role } from '../../core/enums/role.enum';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'user_info';

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string): Observable<any> {
    console.log('🔐 Enviando login para:', username);
    
    return this.http.post<any>(`${environment.apiUrl}/api/auth/login`, { 
      username, 
      password 
    }).pipe(
      tap(response => {
        console.log('📦 Resposta COMPLETA do backend:', response);
        console.log('📦 Tipo da resposta:', typeof response);
        console.log('📦 Chaves da resposta:', Object.keys(response));
        
        // Verifique a estrutura real
        if (response?.token) {
          console.log('✅ Token recebido:', response.token.substring(0, 50) + '...');
          this.setToken(response.token);
          
          // Verifique DIFERENTES possíveis localizações do user
          const userData = response.user || response.data?.user || response;
          console.log('👤 Tentando extrair user data:', userData);
          
          if (userData && typeof userData === 'object') {
            console.log('✅ Dados do usuário encontrados:', userData);
            this.setUserInfo(userData);
          } else {
            console.warn('⚠️ Estrutura de user não encontrada ou inválida');
            
            // Crie dados mínimos do usuário a partir do token
            const minimalUser = {
              username: username,
              role: 'ADMIN' // Default, ajuste conforme necessário
            };
            console.log('🛠 Criando dados mínimos:', minimalUser);
            this.setUserInfo(minimalUser);
          }
          
          // Redireciona baseado no role
          const role = this.getUserRole();
          console.log('🎯 Role detectada:', role);
          this.redirectByRole(role || 'ADMIN');
          
        } else {
          console.error('❌ Token não encontrado na resposta');
        }
      }),
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Login error:', error);
    let errorMessage = 'Ocorreu um erro inesperado';
    
    if (error.status === 0) {
      errorMessage = 'Erro de conexão. Verifique sua internet e tente novamente.';
    } else if (error.status === 401) {
      errorMessage = 'Credenciais inválidas. Por favor, tente novamente.';
    } else if (error.status >= 400 && error.status < 500) {
      errorMessage = error.error?.message || 'Requisição inválida';
    } else if (error.status >= 500) {
      errorMessage = 'Erro no servidor. Tente novamente mais tarde.';
    }
    
    return throwError(() => new Error(errorMessage));
  }

   redirectByRole(role: string): void {
    console.log(role);
    switch (role) {
      case 'ADMIN':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'COMPANY':
        this.router.navigate(['/company/dashboard']);
        break;
      case 'EMPLOYEE':
        this.router.navigate(['/employee/ponto']);
        break;
      default:
        this.router.navigate(['/']);
    }
  }

  setUserInfo(user: any): void {
    console.log('💾 Salvando user info no localStorage...');
    
    // Garanta que é um objeto válido
    if (!user || typeof user !== 'object') {
      console.error('❌ Dados do usuário inválidos:', user);
      return;
    }
    
    // Adicione campos essenciais se não existirem
    const userInfo = {
      id: user.id || 1,
      username: user.username || 'admin',
      email: user.email || 'admin@example.com',
      name: user.name || 'Administrador',
      role: user.role || 'ADMIN',
      companyId: user.companyId || null,
      avatar: user.avatar || null,
      permissions: user.permissions || ['READ', 'WRITE', 'DELETE']
    };
    
    console.log('💾 User info a ser salvo:', userInfo);
    localStorage.setItem(this.USER_KEY, JSON.stringify(userInfo));
    
    // Verifique se foi salvo
    const saved = localStorage.getItem(this.USER_KEY);
    console.log('✓ User info salvo?', !!saved);
    if (saved) {
      console.log('✓ Conteúdo salvo:', JSON.parse(saved));
    }
  }

  getUserInfo(): any {
    const userData = localStorage.getItem(this.USER_KEY);
    console.log('📖 Lendo user info do localStorage:', userData);
    
    if (!userData) {
      console.warn('⚠️ Nenhum user info encontrado no localStorage');
      return null;
    }
    
    try {
      const parsed = JSON.parse(userData);
      console.log('📖 User info parseado:', parsed);
      return parsed;
    } catch (error) {
      console.error('❌ Erro ao parsear user info:', error);
      return null;
    }
  }

  getUserRole(): string | null {
    const user = this.getUserInfo();
    const role = user ? user.role : null;
    console.log('🎭 Role obtida:', role);
    return role;
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return this.hasToken();
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getUser(): any {
    const userData = localStorage.getItem(this.USER_KEY);
    return userData ? JSON.parse(userData) : null;
  }

  private setToken(token: string): void {
    console.log('💾 Salvando token no localStorage:', token.substring(0, 50) + '...');
    localStorage.setItem(this.TOKEN_KEY, token);
    
    // Verifique se foi salvo
    const savedToken = localStorage.getItem(this.TOKEN_KEY);
    console.log('✅ Token salvo?', !!savedToken);
    if (savedToken) {
      console.log('✅ Token (primeiros 50 chars):', savedToken.substring(0, 50) + '...');
    }
  }

  private setUserData(user: any): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  private hasToken(): boolean {
    const token = this.getToken();
    return token !== null && token !== undefined && token !== '';
  }
}