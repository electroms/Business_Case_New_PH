import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

/**
 * Simple read-only list view of users.
 *
 * This component was kept as a lightweight admin list screen and complements the richer
 * management screen located in the admin module. It loads the protected /api/users endpoint
 * and displays the users returned by the backend.
 */
interface UserSummary {
  id: number;
  username: string;
  roles: string;
  enabled: boolean;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  /**
   * Users returned by the backend.
   */
  users: UserSummary[] = [];

  /**
   * Error message displayed when the list endpoint fails or access is denied.
   */
  error = '';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private router: Router
  ) {}

  /**
   * Redirects anonymous users to the login page and loads the protected list if authenticated.
   */
  ngOnInit(): void {
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<UserSummary[]>('/api/users').subscribe({
      next: data => this.users = data,
      error: () => {
        this.error = 'Accès refusé ou erreur serveur.';
      }
    });
  }
}
