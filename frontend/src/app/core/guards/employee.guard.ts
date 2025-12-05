import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const employeeGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const userRole = authService.getUserRole();
  
  if (userRole === 'EMPLOYEE') {
    return true;
  }

  // Redireciona para área apropriada
  if (userRole === 'ADMIN') {
    router.navigate(['/admin/dashboard']);
  } else if (userRole === 'COMPANY') {
    router.navigate(['/company/dashboard']);
  } else {
    router.navigate(['/login']);
  }
  
  return false;
};