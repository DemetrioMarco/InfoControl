import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit{

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  loading = false;
  errorMessage = '';
  accessDeniedMessage = '';

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

 
  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const error = params['error'];
      if (error) {
        this.accessDeniedMessage = error;
      }
    });
  }

  onSubmit(): void {

  if (this.form.invalid) {
    this.form.markAllAsTouched();
    return;
  }

  this.errorMessage = '';
  this.form.disable();

  this.authService.login({
    email: this.form.value.email!,
    password: this.form.value.password!
  })
  .pipe(
    finalize(() => {
      this.form.enable(); // 🔥 reactivar form SIEMPRE
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
    }
  });
}

}