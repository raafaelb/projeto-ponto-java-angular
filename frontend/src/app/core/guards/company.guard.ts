import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const companyGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const userRole = authService.getUserRole();
  
  if (userRole === 'COMPANY') {
    return true;
  }

  // Redireciona para área apropriada
  if (userRole === 'ADMIN') {
    router.navigate(['/admin/dashboard']);
  } else if (userRole === 'EMPLOYEE') {
    router.navigate(['/employee/ponto']);
  } else {
    router.navigate(['/login']);
  }
  
  return false;
};