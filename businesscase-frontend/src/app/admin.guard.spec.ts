import { TestBed } from '@angular/core/testing';
import { Injector, runInInjectionContext } from '@angular/core';
import { Router, provideRouter } from '@angular/router';
import { beforeEach, describe, expect, it } from 'vitest';
import { adminGuard } from './admin.guard';
import { AuthService } from './auth.service';

describe('adminGuard', () => {
  beforeEach(() => {
    if (typeof globalThis.sessionStorage !== 'undefined') {
      globalThis.sessionStorage.clear();
    }
    TestBed.configureTestingModule({
      providers: [provideRouter([])]
    });
  });

  it('allows access for ADMIN users', () => {
    const payload = btoa(JSON.stringify({ roles: ['ROLE_ADMIN'] }));
    const token = `header.${payload}.signature`;
    const authService = TestBed.inject(AuthService);
    authService.setToken(token);

    const injector = TestBed.inject(Injector);
    const result = runInInjectionContext(injector, () => adminGuard({} as any, { url: '/users' } as any));
    expect(result).toBe(true);
  });

  it('redirects USER users to login', () => {
    const payload = btoa(JSON.stringify({ roles: ['ROLE_USER'] }));
    const token = `header.${payload}.signature`;
    const authService = TestBed.inject(AuthService);
    authService.setToken(token);

    const injector = TestBed.inject(Injector);
    const router = TestBed.inject(Router);
    const result = runInInjectionContext(injector, () => adminGuard({} as any, { url: '/users' } as any));
    expect(result).toEqual(router.createUrlTree(['/login']));
  });
});
