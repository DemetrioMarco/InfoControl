import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, OnInit, Output, output } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { Role } from '../../core/models/role.enum';
import { catchError, debounceTime, distinctUntilChanged, map, of, switchMap } from 'rxjs';
import { UserResponse } from '../../core/models/user-model';

@Component({
  selector: 'app-user-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-form.html',
  styleUrl: './user-form.css',
})
export class UserForm implements OnInit{

  @Input() isOpen = false;
  @Input() editingUser: UserResponse | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() userCreated = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);

  form!: FormGroup<{
    nombre: FormControl<string>;
    email: FormControl<string>;
    password: FormControl<string>;
    confirmPassword: FormControl<string>;
    rol: FormControl<Role>;
    enabled: FormControl<boolean>;
  }>;
  
  roles = Object.values(Role);
  showPassword = false;
  showConfirmPassword = false;
  isSubmitting = false;
  errorMessage: string | null = null;
  isEditMode = false;

  ngOnInit(): void {
    this.initForm();
  }

  ngOnChanges(): void {
    if (this.isOpen && this.editingUser) {
      this.isEditMode = true;
      this.populateForm();
    } else {
      this.isEditMode = false;
      this.form?.reset({ rol: Role.OPERADOR, enabled: true });
    }
  }

  private initForm(): void {
   const fb = this.fb.nonNullable;

    this.form = this.fb.nonNullable.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email], [this.emailUniqueValidator()]],
      password: ['', [Validators.required, Validators.minLength(8), this.passwordValidator()]],
      confirmPassword: ['', Validators.required],
      rol: [Role.OPERADOR, Validators.required],
      enabled: [true],
    }, {
      validators: this.passwordMatchValidator()
    });
  }

  private populateForm(): void {
    if (this.editingUser) {
      this.form.patchValue({
        nombre: this.editingUser.nombre,
        email: this.editingUser.email,
        rol: this.editingUser.rol,
        enabled: this.editingUser.enabled,
      });
      
      this.form.get('password')?.clearAsyncValidators();
      this.form.get('password')?.clearValidators();
      this.form.get('password')?.setValidators([Validators.minLength(8), this.passwordValidator()]);
      this.form.get('password')?.updateValueAndValidity();
      
      this.form.get('confirmPassword')?.clearValidators();
      this.form.get('confirmPassword')?.updateValueAndValidity();
    }
  }


  private emailUniqueValidator() {
    return (control: AbstractControl) => {
      if (!control.value) return of(null);
      
      return control.valueChanges.pipe(
        debounceTime(500),
        distinctUntilChanged(),
        switchMap(email => {
          return this.userService.getAll().pipe(
            map(users => {
              const exists = users.some(user => user.email === email);
              return exists ? { emailTaken: true } : null;
            }),
            catchError(() => of(null))
          );
        })
      );
    };
  }

  private passwordValidator() {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;

      const hasUppercase = /[A-Z]/.test(value);
      const hasNumber = /[0-9]/.test(value);

      if (!hasUppercase || !hasNumber) {
        return { weakPassword: true };
      }
      return null;
    };
  }

  private passwordMatchValidator() {
    return (group: AbstractControl): ValidationErrors | null => {
      const password = group.get('password')?.value;
      const confirmPassword = group.get('confirmPassword')?.value;

      if (password && confirmPassword && password !== confirmPassword) {
        group.get('confirmPassword')?.setErrors({ passwordMismatch: true });
        return { passwordMismatch: true };
      }
      return null;
    };
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.isSubmitting = true;
    this.errorMessage = null;

    const { confirmPassword, ...payload } = this.form.getRawValue();


    if (this.isEditMode && this.editingUser) {
      this.updateUser(payload);
    } else {
      this.createUser(payload);
    }
  }

  private createUser(payload: any): void {
    this.userService.create(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.userCreated.emit();
        this.closeModal();
      },
      error: (err) => {
        this.isSubmitting = false;
        
        if (err.status === 409) {
          this.form.get('email')?.setErrors({ emailTaken: true });
          this.errorMessage = 'Este email ya está registrado';
        } else {
          this.errorMessage = err.error?.message || 'Error al crear el usuario';
        }
      }
    });
  }

  private updateUser(payload: any): void {
    if (!this.editingUser) return;

    this.userService.update(this.editingUser.id, payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.userCreated.emit();
        this.closeModal();
      },
      error: (err) => {
        this.isSubmitting = false;
        
        if (err.status === 409) {
          this.form.get('email')?.setErrors({ emailTaken: true });
          this.errorMessage = 'Este email ya está registrado';
        } else {
          this.errorMessage = err.error?.message || 'Error al actualizar el usuario';
        }
      }
    });
  }

  closeModal(): void {
    this.form.reset({ rol: Role.OPERADOR, enabled: true });
    this.errorMessage = null;
    this.showPassword = false;
    this.showConfirmPassword = false;
    this.isEditMode = false;
    this.close.emit();
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  getPasswordStrength(): string {
    const password = this.form.get('password')?.value;
    if (!password) return '';
    if (password.length < 8) return 'weak';
    if (/[A-Z]/.test(password) && /[0-9]/.test(password)) return 'strong';
    return 'medium';
  }


}
