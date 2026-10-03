import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { Activity } from '../../core/models';
import { AppShellComponent } from '../../shared/app-shell.component';

@Component({
  standalone: true,
  imports: [AppShellComponent, DatePipe],
  templateUrl: './activities.component.html',
  styleUrl: './activities.component.scss'
})
export class ActivitiesComponent {
  private readonly api = inject(ApiService);
  readonly items = signal<Activity[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly busyId = signal('');
  readonly notice = signal('');
  constructor() { this.load(); }

  load(): void {
    this.loading.set(true); this.error.set('');
    this.api.activities().subscribe({ next: values => { this.items.set(values); this.loading.set(false); }, error: () => { this.error.set('Your activity log could not load. Please try again.'); this.loading.set(false); } });
  }
  name(type: string): string { return type.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, character => character.toUpperCase()); }
  generate(activity: Activity): void {
    this.busyId.set(activity.id); this.notice.set('');
    this.api.generateRecommendation(activity.id).subscribe({
      next: () => { this.busyId.set(''); this.notice.set(`A new note for your ${this.name(activity.type).toLowerCase()} is ready.`); },
      error: () => { this.busyId.set(''); this.notice.set('Personal guidance is unavailable right now. Check that the AI provider is configured.'); }
    });
  }
}
