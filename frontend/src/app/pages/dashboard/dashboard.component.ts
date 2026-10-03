import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ActivityType, DashboardSummary, Recommendation } from '../../core/models';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { AppShellComponent } from '../../shared/app-shell.component';

@Component({
  standalone: true,
  imports: [AppShellComponent, DatePipe, ReactiveFormsModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  private readonly api = inject(ApiService);
  private readonly builder = inject(FormBuilder);
  readonly auth = inject(AuthService);
  readonly summary = signal<DashboardSummary | null>(null);
  readonly recommendations = signal<Recommendation[]>([]);
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly error = signal('');
  readonly formMessage = signal('');
  readonly types: { value: ActivityType; label: string }[] = [
    { value: 'RUNNING', label: 'Running' }, { value: 'WALKING', label: 'Walking' }, { value: 'CYCLING', label: 'Cycling' },
    { value: 'SWIMMING', label: 'Swimming' }, { value: 'WEIGHT_TRAINING', label: 'Strength training' },
    { value: 'YOGA', label: 'Yoga' }, { value: 'HIIT', label: 'HIIT' }, { value: 'CARDIO', label: 'Cardio' },
    { value: 'STRETCHING', label: 'Stretching' }, { value: 'OTHER', label: 'Other' }
  ];
  readonly form = this.builder.nonNullable.group({
    type: ['RUNNING' as ActivityType, Validators.required], duration: [30, [Validators.required, Validators.min(1)]],
    calories: [''], startTime: [this.localNow(), Validators.required]
  });

  constructor() { this.load(); }

  load(): void {
    this.loading.set(true); this.error.set('');
    forkJoin({ summary: this.api.summary(), recommendations: this.api.recommendations() }).subscribe({
      next: data => { this.summary.set(data.summary); this.recommendations.set(data.recommendations); this.loading.set(false); },
      error: () => { this.error.set('Your dashboard could not load. Check your connection and try again.'); this.loading.set(false); }
    });
  }

  logActivity(): void {
    this.formMessage.set('');
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving.set(true);
    const value = this.form.getRawValue();
    this.api.createActivity({
      type: value.type, duration: Number(value.duration), caloriesBurned: value.calories ? Number(value.calories) : null,
      startTime: value.startTime, additionalMetrics: {}
    }).subscribe({
      next: () => { this.saving.set(false); this.formMessage.set('Activity added to your week.'); this.form.patchValue({ duration: 30, calories: '', startTime: this.localNow() }); this.load(); },
      error: () => { this.saving.set(false); this.formMessage.set('We could not save that session. Please try again.'); }
    });
  }

  barHeight(minutes: number): number {
    const max = Math.max(1, ...(this.summary()?.weeklyActivity.map(day => day.minutes) ?? [1]));
    return minutes === 0 ? 5 : Math.max(12, Math.round(minutes / max * 100));
  }

  activityName(value: string): string { return value.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, character => character.toUpperCase()); }
  trackRecommendation(activityId: string): void {
    this.api.generateRecommendation(activityId).subscribe({ next: recommendation => this.recommendations.update(items => [recommendation, ...items]), error: () => this.formMessage.set('Recommendations are temporarily unavailable. Add an OpenAI key to enable them.') });
  }

  private localNow(): string {
    const date = new Date(); date.setMinutes(date.getMinutes() - date.getTimezoneOffset()); return date.toISOString().slice(0, 16);
  }
}
