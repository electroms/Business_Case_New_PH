import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private username = '';
  private password = '';

  setCredentials(username: string, password: string) {
    this.username = username;
    this.password = password;
  }

  clearCredentials() {
    this.username = '';
    this.password = '';
  }

  getAuthorizationHeader(): string | null {
    if (!this.username || !this.password) return null;
    return 'Basic ' + btoa(`${this.username}:${this.password}`);
  }

  isAuthenticated(): boolean {
    return !!this.username && !!this.password;
  }
}
