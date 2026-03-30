import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const noAuthGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    return true;
  }

  const role = authService.getUserRole();
  if (role === 'ADMIN') {
    return router.parseUrl('/admin/dashboard');
  }

  if (role === 'COMPANY') {
    return router.parseUrl('/company/dashboard');
  }

  if (role === 'EMPLOYEE') {
    return router.parseUrl('/employee/workday');
  }

  return router.parseUrl('/login');
};
