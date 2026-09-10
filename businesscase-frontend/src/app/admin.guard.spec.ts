import { TestBed } from '@angular/core/testing';
import { Injector, runInInjectionContext } from '@angular/core';
import { Router, provideRouter } from '@angular/router';
import { beforeEach, describe, expect, it } from 'vitest';
import { adminGuard } from './admin.guard';

describe('adminGuard', () => {
  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideRouter([])]
    });
  });

  it('allows access for ADMIN users', () => {
    const token = btoa(JSON.stringify({ roles: ['ROLE_ADMIN'] }));
    sessionStorage.setItem('businesscase.accessToken', token);

    const injector = TestBed.inject(Injector);
    const result = runInInjectionContext(injector, () => adminGuard({} as any, { url: '/users' } as any));
    expect(result).toBe(true);
  });

  it('redirects USER users to login', () => {
    const token = btoa(JSON.stringify({ roles: ['ROLE_USER'] }));
    sessionStorage.setItem('businesscase.accessToken', token);

    const injector = TestBed.inject(Injector);
    const router = TestBed.inject(Router);
    const result = runInInjectionContext(injector, () => adminGuard({} as any, { url: '/users' } as any));
    expect(result).toEqual(router.createUrlTree(['/login']));
  });
});
