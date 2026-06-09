import { CommonModule } from '@angular/common';
import {
  Component,
  DestroyRef,
  EventEmitter,
  Output,
  computed,
  effect,
  inject,
  input,
  signal,
} from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { catchError, map, of, take } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Role } from '../../core/models/role.enum';
import { UserResponse } from '../../core/models/user-model';
import { UserService } from '../../core/services/user.service';

type UserFormControls = {
  nombre: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
  confirmPassword: FormControl<string>;
  rol: FormControl<Role>;
  enabled: FormControl<boolean>;
};

type CreateUserPayload = {
  nombre: string;
  email: string;
  password: string;
  rol: Role;
  enabled: boolean;
};

type UpdateUserPayload = {
  nombre: string;
  email: string;
  rol: Role;
  enabled: boolean;
  password?: string;
};

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-form.html',
  styleUrl: './user-form.css',
})
export class UserForm {
  readonly isOpen = input(false);
  readonly editingUser = input<UserResponse | null>(null);

  @Output() close = new EventEmitter<void>();
  @Output() userCreated = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly destroyRef = inject(DestroyRef);

  readonly roles = Object.values(Role);
  readonly isEditMode = computed(() => !!this.editingUser());

  readonly showPassword = signal(false);
  readonly showConfirmPassword = signal(false);
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);

  form!: FormGroup<UserFormControls>;

  constructor() {
    this.initForm();

    this.form.controls.password.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((val) => {
        if (this.isEditMode() && !this.hasText(val)) {
          this.form.controls.confirmPassword.setValue('', { emitEvent: false });
          this.form.controls.confirmPassword.markAsPristine();
          this.form.controls.confirmPassword.markAsUntouched();
          this.form.updateValueAndValidity({ emitEvent: false });
        }
      });

    effect(() => {
      if (!this.form) return;

      if (this.isOpen()) {
        const editing = this.editingUser();
        editing ? this.loadEditingUser(editing) : this.resetForCreate();
      } else {
        this.resetState();
      }
    });
  }

  private initForm(): void {
    this.form = this.fb.nonNullable.group<UserFormControls>(
      {
        nombre: this.fb.nonNullable.control('', [
          Validators.required,
          Validators.minLength(3),
        ]),
        email: this.fb.nonNullable.control('', {
          validators: [Validators.required, Validators.email],
          asyncValidators: [this.emailUniqueValidator()],
          updateOn: 'blur',
        }),
        password: this.fb.nonNullable.control(''),
        confirmPassword: this.fb.nonNullable.control(''),
        rol: this.fb.nonNullable.control(Role.OPERADOR, [Validators.required]),
        enabled: this.fb.nonNullable.control(true),
      },
      {
        validators: [this.passwordMatchValidator()],
      }
    );
  }

  private setupModeValidators(): void {
    const { password, confirmPassword } = this.form.controls;
    const strength = this.passwordStrengthValidator();

    if (this.isEditMode()) {
      password.setValidators([Validators.minLength(8), strength]);
      confirmPassword.clearValidators();
    } else {
      password.setValidators([Validators.required, Validators.minLength(8), strength]);
      confirmPassword.setValidators([Validators.required]);
    }

    password.updateValueAndValidity({ emitEvent: false });
    confirmPassword.updateValueAndValidity({ emitEvent: false });
    this.form.updateValueAndValidity({ emitEvent: false });
  }

  private loadEditingUser(user: UserResponse): void {
    this.setupModeValidators();

    this.form.reset(
      {
        nombre: user.nombre,
        email: user.email,
        password: '',
        confirmPassword: '',
        rol: user.rol,
        enabled: user.enabled,
      },
      { emitEvent: false }
    );

    this.clearUIState();
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  private resetForCreate(): void {
    this.setupModeValidators();

    this.form.reset(
      {
        nombre: '',
        email: '',
        password: '',
        confirmPassword: '',
        rol: Role.OPERADOR,
        enabled: true,
      },
      { emitEvent: false }
    );

    this.clearUIState();
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  private resetState(): void {
    this.clearUIState();
    this.form?.markAsPristine();
    this.form?.markAsUntouched();
  }

  private clearUIState(): void {
    this.errorMessage.set(null);
    this.showPassword.set(false);
    this.showConfirmPassword.set(false);
    this.isSubmitting.set(false);
    this.form?.setErrors(null);
  }

  private hasText(value: unknown): boolean {
    return String(value ?? '').trim().length > 0;
  }

  private emailUniqueValidator(): AsyncValidatorFn {
    return (control: AbstractControl) => {
      const email = String(control.value ?? '').trim().toLowerCase();
      if (!email) return of(null);

      const editing = this.editingUser();
      const editingEmail = String(editing?.email ?? '').trim().toLowerCase();

      if (this.isEditMode() && email === editingEmail) return of(null);

      return this.userService.checkEmailExists(email, editing?.id).pipe(
        map((exists) => (exists ? { emailTaken: true } : null)),
        catchError(() => of(null)),
        take(1)
      );
    };
  }

  private passwordStrengthValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const val = String(control.value ?? '');
      if (!val) return null;

      return /[A-Z]/.test(val) && /[0-9]/.test(val)
        ? null
        : { weakPassword: true };
    };
  }

  private passwordMatchValidator(): ValidatorFn {
    return (group: AbstractControl): ValidationErrors | null => {
      const pass = String(group.get('password')?.value ?? '');
      const conf = String(group.get('confirmPassword')?.value ?? '');

      if (this.isEditMode() && !pass) return null;
      return (!pass && !conf) || pass === conf ? null : { passwordMismatch: true };
    };
  }

  onSubmit(): void {
    if (this.form.invalid || this.form.pending || this.isSubmitting()) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const raw = this.form.getRawValue();

    const basePayload = {
      nombre: raw.nombre.trim(),
      email: raw.email.trim(),
      rol: raw.rol,
      enabled: raw.enabled,
    };

    if (this.isEditMode()) {
      const payload: UpdateUserPayload = {
        ...basePayload,
        ...(this.hasText(raw.password) ? { password: raw.password } : {}),
      };

      this.userService.update(this.editingUser()!.id, payload).subscribe({
        next: () => {
          this.userCreated.emit();
          this.closeModal();
        },
        error: (err) => this.handleError(err, 'Error al actualizar'),
      });
    } else {
      const payload: CreateUserPayload = {
        ...basePayload,
        password: raw.password,
      };

      this.userService.create(payload).subscribe({
        next: () => {
          this.userCreated.emit();
          this.closeModal();
        },
        error: (err) => this.handleError(err, 'Error al crear'),
      });
    }
  }

  private handleError(
    err: { status?: number; error?: { message?: string } },
    fallback: string
  ): void {
    this.isSubmitting.set(false);

    if (err.status === 409) {
      const emailCtrl = this.form.controls.email;
      emailCtrl.setErrors({
        ...(emailCtrl.errors ?? {}),
        emailTaken: true,
      });
      emailCtrl.markAsTouched();
      this.errorMessage.set('El email ya está en uso');
      return;
    }

    this.errorMessage.set(err.error?.message || fallback);
  }

  closeModal(): void {
    this.resetState();
    this.close.emit();
  }

  togglePasswordVisibility(): void {
    this.showPassword.update((v) => !v);
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword.update((v) => !v);
  }

  getPasswordStrength(): 'weak' | 'medium' | 'strong' | '' {
    const p = String(this.form?.controls.password.value ?? '');
    if (!p) return '';
    if (p.length < 8) return 'weak';
    return /[A-Z]/.test(p) && /[0-9]/.test(p) ? 'strong' : 'medium';
  }

  isFieldInvalid(name: keyof UserFormControls): boolean {
    const ctrl = this.form.controls[name];
    return ctrl.invalid && (ctrl.dirty || ctrl.touched);
  }

  showConfirmPasswordField(): boolean {
    return !this.isEditMode() || this.hasText(this.form.controls.password.value);
  }
}
