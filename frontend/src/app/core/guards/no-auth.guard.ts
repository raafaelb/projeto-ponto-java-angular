import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const noAuthGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Se o usuário já está logado, redireciona para o painel adequado
  if (authService.isLoggedIn()) {
    const userRole = authService.getUserRole();

    switch (userRole) {
      case 'ADMIN':
        return router.parseUrl('/admin/dashboard');
      case 'COMPANY':
        return router.parseUrl('/company/dashboard');
      case 'EMPLOYEE':
        return router.parseUrl('/employee/ponto');
      default:
        return router.parseUrl('/');
    }
  }

  // Se não está logado, permite acesso à rota pública (login)
  return true;
};