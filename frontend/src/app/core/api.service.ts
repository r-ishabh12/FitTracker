import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Activity, ActivityInput, DashboardSummary, Recommendation, User } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  csrf(): Observable<{ token: string }> { return this.http.get<{ token: string }>('/api/auth/csrf'); }
  login(email: string, password: string): Observable<User> { return this.http.post<User>('/api/auth/login', { email, password }); }
  register(data: { email: string; password: string; firstName: string; lastName: string }): Observable<User> {
    return this.http.post<User>('/api/auth/register', data);
  }
  me(): Observable<User> { return this.http.get<User>('/api/auth/me'); }
  logout(): Observable<void> { return this.http.post<void>('/api/auth/logout', {}); }
  summary(): Observable<DashboardSummary> { return this.http.get<DashboardSummary>('/api/dashboard/summary'); }
  activities(): Observable<Activity[]> { return this.http.get<Activity[]>('/api/activities'); }
  createActivity(input: ActivityInput): Observable<Activity> { return this.http.post<Activity>('/api/activities', input); }
  recommendations(): Observable<Recommendation[]> { return this.http.get<Recommendation[]>('/api/recommendations'); }
  generateRecommendation(activityId: string): Observable<Recommendation> {
    return this.http.post<Recommendation>(`/api/recommendations/${activityId}/generate`, {});
  }
}
