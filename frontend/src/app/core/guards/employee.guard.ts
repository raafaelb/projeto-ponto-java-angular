import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const employeeGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const userRole = authService.getUserRole();
  if (userRole === 'EMPLOYEE') {
    return true;
  }

  if (userRole === 'ADMIN') {
    return router.parseUrl('/admin/dashboard');
  }

  return router.parseUrl('/company/dashboard');
};
