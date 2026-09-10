import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Protects admin-only screens.
 * Only users with the ROLE_ADMIN claim can access those routes.
 */
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  const roles = authService.getRoles();
  if (roles.includes('ROLE_ADMIN')) {
    return true;
  }

  return router.createUrlTree(['/login']);
};
