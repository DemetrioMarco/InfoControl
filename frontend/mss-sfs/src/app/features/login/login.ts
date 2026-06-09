import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize, take } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  loading = false;
  errorMessage = '';
  accessDeniedMessage = '';

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const error = params['error'];

      this.accessDeniedMessage = error || '';

      if (!error && this.authService.isAuthenticated()) {
        this.router.navigate(['/app/dashboard']);
      }
    });
  }



  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.loading = true;
    this.form.disable();

    const { email, password } = this.form.getRawValue();

    this.authService
      .login({ email, password })
      .pipe(
        finalize(() => {
          this.loading = false;
          this.form.enable();
        })
      )
      .subscribe({
        next: () => {
          this.accessDeniedMessage = '';
          this.router.navigate(['/app/dashboard']);
        },
        error: (err) => {
          this.errorMessage =
            err.error?.message ||
            err.message ||
            'Error al iniciar sesión';
        },
      });
  }

  get emailControl() {
    return this.form.controls.email;
  }

  get passwordControl() {
    return this.form.controls.password;
  }
}
