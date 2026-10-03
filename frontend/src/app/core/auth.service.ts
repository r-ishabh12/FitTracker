import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, of, switchMap, tap } from 'rxjs';
import { ApiService } from './api.service';
import { User } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  readonly user = signal<User | null>(null);
  private initialized = false;

  ensureSession(): Observable<User | null> {
    if (this.initialized) return of(this.user());
    return this.api.csrf().pipe(
      switchMap(() => this.api.me()),
      tap(user => { this.user.set(user); this.initialized = true; }),
      catchError(() => { this.user.set(null); this.initialized = true; return of(null); })
    );
  }

  login(email: string, password: string): Observable<User> {
    return this.api.csrf().pipe(switchMap(() => this.api.login(email, password)), tap(user => {
      this.user.set(user); this.initialized = true;
    }));
  }

  register(data: { email: string; password: string; firstName: string; lastName: string }): Observable<User> {
    return this.api.csrf().pipe(switchMap(() => this.api.register(data)));
  }

  logout(): Observable<void> {
    return this.api.csrf().pipe(switchMap(() => this.api.logout()), tap(() => {
      this.user.set(null); this.initialized = true; void this.router.navigate(['/']);
    }));
  }
}
