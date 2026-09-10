import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Root shell of the application.
 *
 * This component renders the global navigation bar and the active route via RouterOutlet.
 * It exposes the minimal UI logic needed to know whether the current user is connected,
 * whether they are admin, and how to log out cleanly from the browser session.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  /**
   * Application title displayed in the top navigation bar.
   */
  title = 'Business Case';

  constructor(private auth: AuthService, private router: Router) {}

  /**
   * Returns whether the user currently has a valid session token in storage.
   */
  isLoggedIn(): boolean {
    return this.auth.isAuthenticated();
  }

  /**
   * Returns whether the current user has the admin role in the decoded JWT payload.
   */
  isAdmin(): boolean {
    return this.auth.isAdmin();
  }

  /**
   * Clears the JWT and redirects the user to the login screen.
   */
  logout(event: Event): void {
    event.preventDefault();
    this.auth.clearCredentials();
    this.router.navigate(['/login']);
  }
}
