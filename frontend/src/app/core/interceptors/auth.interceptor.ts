import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  
  console.log(`🚀 AuthInterceptor - INICIADO para: ${req.method} ${req.url}`);
  
  const token = localStorage.getItem('auth_token');
  
  console.log('🔍 Token no localStorage:', token ? 'PRESENTE' : 'AUSENTE');
  if (token) {
    console.log('🔍 Token (primeiros 50 chars):', token.substring(0, 50) + '...');
  }
  
  let authReq = req;
  
  if (token) {
    console.log('➕ Adicionando header Authorization...');
    
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    
    console.log('✅ Header Authorization adicionado:', 
      authReq.headers.get('Authorization')?.substring(0, 70) + '...');
  } else {
    console.warn('⚠️ NÃO há token! A requisição será enviada SEM autenticação.');
  }
  
  return next(authReq).pipe(
    catchError((error) => {
      console.error('❌ Erro na requisição:', {
        url: req.url,
        status: error.status,
        message: error.message,
        error: error.error
      });
      
      if (error.status === 401 || error.status === 403) {
        console.error('🔐 Token inválido ou expirado. Limpando localStorage...');
        
        localStorage.removeItem('auth_token');
        localStorage.removeItem('user_info');
        
        router.navigate(['/auth/login'], {
          queryParams: { returnUrl: router.url }
        });
      }
      
      return throwError(() => error);
    })
  );
};