import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { UserResponse } from '../../core/models/user-model';
import { CommonModule } from '@angular/common';
import { Role } from '../../core/models/role.enum';
import { UserForm } from './user-form';
import { ConfirmModal } from '../../shared/components/confirm-modal/confirm-modal';

@Component({
  selector: 'app-users-list',
  imports: [CommonModule, UserForm, ConfirmModal],
  templateUrl: './users-list.html',
  styleUrl: './users-list.css',
})
export class UsersList implements OnInit{
  
  private readonly userService = inject(UserService);
  
  users = signal<UserResponse[]>([]);
  isModalOpen = signal<boolean>(false);
  editingUser = signal<UserResponse | null>(null);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);

  readonly pageSize = 10;
  currentPage = signal(1);

// 🔹 Fuente real (sin paginar)
allUsers = signal<UserResponse[]>([]);

// 🔹 Filtro (si existe)
filteredUsers = computed(() => this.allUsers());

// 🔹 Total correcto
totalUsers = computed(() => this.filteredUsers().length);

// 🔹 Total páginas
totalPages = computed(() =>
  Math.max(1, Math.ceil(this.totalUsers() / this.pageSize))
);

// 🔹 Datos paginados
paginatedUsers = computed(() => {
  const start = (this.currentPage() - 1) * this.pageSize;
  return this.filteredUsers().slice(start, start + this.pageSize);
});

paginationItems = computed(() => {
  const total = this.totalPages();
  const current = this.currentPage();
  const pages: (number | '...')[] = [];

  if (total <= 7) {
    return Array.from({ length: total }, (_, i) => i + 1);
  }

  pages.push(1);

  if (current > 4) pages.push('...');

  const start = Math.max(2, current - 1);
  const end = Math.min(total - 1, current + 1);

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }

  if (current < total - 3) pages.push('...');

  pages.push(total);

  return pages;
});



  // Confirm modal
  confirmOpen    = signal<boolean>(false);
  confirmTitle   = signal<string>('');
  confirmMessage = signal<string>('');
  confirmSubMessage = signal<string>('');
  confirmLabel   = signal<string>('Confirmar');
  confirmClass   = signal<string>('btn-danger');
  private pendingAction: (() => void) | null = null;
  
  readonly Role = Role;

  
  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers():void {
    this.loading.set(true);
    this.error.set(null);

    this.userService.getAll().subscribe({
      next: (data) => {
        this.allUsers.set(data); // ← era this.users.set(data)
        this.currentPage.set(1);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Error al cargar los usuarios');
        this.loading.set(false);
      }
    });
  }

  goToPage(page: number | '...'): void {
    if (page === '...') return;

    const total = this.totalPages();

    if (page < 1 || page > total || page === this.currentPage()) return;

    this.currentPage.set(page);
  }

  prevPage(): void {
    this.currentPage.update(p => Math.max(1, p - 1));
  }

  nextPage(): void {
    this.currentPage.update(p => Math.min(this.totalPages(), p + 1));
  }

   openModal(user?: UserResponse): void {
    if (user) {
      this.editingUser.set(user);
    } else {
      this.editingUser.set(null);
    }
    this.isModalOpen.set(true);
  }

  closeModal(): void {
    this.isModalOpen.set(false);
    this.editingUser.set(null);
  }

  onUserCreated(): void {
    this.closeModal();
    this.loadUsers();
  }

  private openConfirm(title: string, message: string, subMessage: string, label: string, cssClass: string, action: () => void): void {
  this.confirmTitle.set(title);
  this.confirmMessage.set(message);
  this.confirmSubMessage.set(subMessage);
  this.confirmLabel.set(label);
  this.confirmClass.set(cssClass);
  this.pendingAction = action;
  this.confirmOpen.set(true);
}

  onConfirmed(): void {
    this.confirmOpen.set(false);
    this.pendingAction?.();
    this.pendingAction = null;
  }

  onCancelled(): void {
    this.confirmOpen.set(false);
    this.pendingAction = null;
  }
  
  toggleEnabled(user: UserResponse): void {
    const action = user.enabled ? 'desactivar' : 'activar';
    const btnClass = user.enabled ? 'btn-warning' : 'btn-success';

    this.openConfirm(
      'Cambiar estado',
      `¿Estás seguro de ${action} al usuario ${user.nombre}?`,
      '',
      action.charAt(0).toUpperCase() + action.slice(1),
      btnClass,
      () => this.userService.toggleEnabled(user.id, !user.enabled).subscribe({
        next: (updated) => this.allUsers.update(list => list.map(u => u.id === updated.id ? updated : u)),
        error: () => this.error.set('Error al actualizar el estado del usuario')
      })
    );
  }

  deleteUser(id: number): void {
    const user = this.allUsers().find(u => u.id === id);

    this.openConfirm(
      'Eliminar usuario',
      `¿Estás seguro de eliminar a ${user?.nombre ?? 'este usuario'}?`,
      'Esta acción no se puede deshacer.', 
      'Eliminar',
      'btn-danger',
      () => this.userService.delete(id).subscribe({
        next: () => {
      this.allUsers.update(list => list.filter(u => u.id !== id)); // ← era this.users
      if (this.paginatedUsers().length === 0 && this.currentPage() > 1) {
        this.currentPage.update(p => p - 1);
      }
    },
        error: () => this.error.set('Error al eliminar el usuario')
      })
    );
  }


}
