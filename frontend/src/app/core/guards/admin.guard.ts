import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.getUserRole() === 'ADMIN') {
    return true;
  }

  if (authService.getUserRole() === 'EMPLOYEE') {
    return router.parseUrl('/employee/workday');
  }

  return router.parseUrl('/company/dashboard');
};
