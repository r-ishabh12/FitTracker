import { Component, Input, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'fit-app-shell',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './app-shell.component.html',
  styleUrl: './app-shell.component.scss'
})
export class AppShellComponent {
  @Input() pageTitle = 'Overview';
  readonly auth = inject(AuthService);
  readonly today = new Intl.DateTimeFormat('en', { weekday: 'long', month: 'long', day: 'numeric' }).format(new Date());
  logout(): void { this.auth.logout().subscribe(); }
}
