import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly busy = signal(false);
  readonly error = signal('');
  readonly form = this.formBuilder.nonNullable.group({ email: ['', [Validators.required, Validators.email]], password: ['', Validators.required] });

  submit(): void {
    this.error.set('');
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.busy.set(true);
    this.auth.login(this.form.getRawValue().email, this.form.getRawValue().password).subscribe({
      next: () => { this.busy.set(false); void this.router.navigate(['/app']); },
      error: (error: HttpErrorResponse) => {
        this.busy.set(false);
        this.error.set(error.status === 401 ? 'That email and password do not match. Try again.' : 'We could not sign you in right now. Please try again.');
      }
    });
  }
}
