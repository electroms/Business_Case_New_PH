import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

/**
 * Centralizes authentication state in the browser.
 *
 * The JWT stays in memory only to reduce exposure to XSS and browser storage leakage.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private token = '';

  constructor(private http: HttpClient) {}

  setToken(token: string): void {
    this.token = token;
  }

  login(username: string, password: string): Observable<TokenResponse> {
    return this.http.post<TokenResponse>('/api/auth/login', { username, password }).pipe(
      tap(response => {
        this.token = response.accessToken;
      })
    );
  }

  clearCredentials() {
    this.token = '';
  }

  getAuthorizationHeader(): string | null {
    if (!this.token) return null;
    return `Bearer ${this.token}`;
  }

  getRoles(): string[] {
    if (!this.token) {
      return [];
    }

    try {
      const payload = this.token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      const roles = decoded.roles ?? [];
      return Array.isArray(roles) ? roles : [roles];
    } catch {
      return [];
    }
  }

  isAuthenticated(): boolean {
    return !!this.token;
  }

  isAdmin(): boolean {
    return this.getRoles().includes('ROLE_ADMIN');
  }
}
