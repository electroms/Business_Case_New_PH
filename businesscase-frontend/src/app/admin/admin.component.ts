import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

/**
 * Lightweight DTO representing the user data returned by the backend list API.
 */
interface UserSummary {
  id: number;
  username: string;
  roles: string;
  enabled: boolean;
}

/**
 * Payload used to create a new user from the admin form.
 */
interface CreateUserRequest {
  username: string;
  password: string;
  roles: string;
  enabled: boolean;
}

/**
 * Administration screen for user lifecycle management.
 *
 * This component is only available to users with ROLE_ADMIN. It provides:
 * - a list of existing users from the backend,
 * - a form to create a new user,
 * - the ability to delete a user after confirmation.
 *
 * It acts as the gateway between the Angular UI and the protected admin API.
 */
@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  /**
   * Current list of users retrieved from the server.
   */
  users: UserSummary[] = [];

  /**
   * Form model bound to the creation form in the template.
   */
  form = {
    username: '',
    password: '',
    roles: 'ROLE_USER',
    enabled: true
  };

  /**
   * Error message visible to the admin when an action fails.
   */
  error = '';

  /**
   * Success message used after a deletion or creation action.
   */
  success = '';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private router: Router
  ) {}

  /**
   * Ensures the current user is authenticated and has the admin role before loading the screen.
   */
  ngOnInit(): void {
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.auth.isAdmin()) {
      this.router.navigate(['/dashboard']);
      return;
    }

    this.loadUsers();
  }

  /**
   * Fetches the current list of users from the protected backend endpoint.
   */
  loadUsers(): void {
    this.http.get<UserSummary[]>('/api/users').subscribe({
      next: data => this.users = data,
      error: () => this.error = 'Vous n’êtes pas autorisé à consulter cet écran.'
    });
  }

  /**
   * Sends a new user creation request to the backend.
   * On success, the form is reset and the user list is refreshed.
   */
  createUser(): void {
    this.error = '';
    this.success = '';

    const payload: CreateUserRequest = {
      username: this.form.username.trim(),
      password: this.form.password,
      roles: this.form.roles,
      enabled: this.form.enabled
    };

    this.http.post<UserSummary>('/api/users', payload).subscribe({
      next: user => {
        this.success = `Utilisateur ${user.username} créé.`;
        this.form = { username: '', password: '', roles: 'ROLE_USER', enabled: true };
        this.loadUsers();
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Impossible de créer l’utilisateur.';
      }
    });
  }

  /**
   * Deletes a selected user after confirmation.
   * The backend enforces business rules such as blocking self-deletion or deleting the last admin.
   */
  deleteUser(user: UserSummary): void {
    if (!confirm(`Supprimer l’utilisateur ${user.username} ?`)) {
      return;
    }

    this.http.delete(`/api/users/${user.id}`).subscribe({
      next: () => {
        this.success = `Utilisateur ${user.username} supprimé.`;
        this.loadUsers();
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Suppression impossible.';
      }
    });
  }
}
