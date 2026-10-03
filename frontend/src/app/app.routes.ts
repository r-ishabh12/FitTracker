import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/landing/landing.component').then(module => module.LandingComponent) },
  { path: 'login', loadComponent: () => import('./pages/auth/login.component').then(module => module.LoginComponent) },
  { path: 'register', loadComponent: () => import('./pages/auth/register.component').then(module => module.RegisterComponent) },
  {
    path: 'app', pathMatch: 'full', canActivate: [authGuard],
    loadComponent: () => import('./pages/dashboard/dashboard.component').then(module => module.DashboardComponent)
  },
  {
    path: 'app/activities', canActivate: [authGuard],
    loadComponent: () => import('./pages/activities/activities.component').then(module => module.ActivitiesComponent)
  },
  {
    path: 'app/recommendations', canActivate: [authGuard],
    loadComponent: () => import('./pages/recommendations/recommendations.component').then(module => module.RecommendationsComponent)
  },
  { path: '**', redirectTo: '' }
];
