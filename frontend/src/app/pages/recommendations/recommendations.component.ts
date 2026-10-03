import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { Recommendation } from '../../core/models';
import { AppShellComponent } from '../../shared/app-shell.component';

@Component({
  standalone: true,
  imports: [AppShellComponent, DatePipe],
  templateUrl: './recommendations.component.html',
  styleUrl: './recommendations.component.scss'
})
export class RecommendationsComponent {
  private readonly api = inject(ApiService);
  readonly recommendations = signal<Recommendation[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  constructor() { this.load(); }
  load(): void {
    this.loading.set(true); this.error.set('');
    this.api.recommendations().subscribe({ next: values => { this.recommendations.set(values); this.loading.set(false); }, error: () => { this.error.set('Your recommendations could not load right now.'); this.loading.set(false); } });
  }
  label(type: string): string { return type.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, character => character.toUpperCase()); }
}
