import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const header = this.auth.getAuthorizationHeader();
    if (header) {
      const cloned = req.clone({ setHeaders: { Authorization: header } });
      return next.handle(cloned);
    }
    return next.handle(req);
  }
}
