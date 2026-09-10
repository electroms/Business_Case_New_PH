import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

/**
 * Dashboard view showing the current user context and global app information.
 * It calls the protected /api/dashboard endpoint and renders the returned payload.
 */
interface DashboardDto {
  username: string;
  applicationName: string;
  totalUsers: number;
  isAdmin: boolean;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  dashboard: DashboardDto | null = null;
  error = '';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<DashboardDto>('/api/dashboard').subscribe({
      next: data => this.dashboard = data,
      error: () => {
        this.error = 'Impossible de charger le tableau de bord.';
        this.auth.clearCredentials();
        this.router.navigate(['/login']);
      }
    });
  }
}
