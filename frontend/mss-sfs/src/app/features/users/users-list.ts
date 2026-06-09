import { CommonModule } from '@angular/common';
import {
  Component,
  computed,
  effect,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import Swal from 'sweetalert2';
import { UserService } from '../../core/services/user.service';
import { UserResponse } from '../../core/models/user-model';
import { Role } from '../../core/models/role.enum';
import { UserForm } from './user-form';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [CommonModule, UserForm],
  templateUrl: './users-list.html',
  styleUrl: './users-list.css',
})
export class UsersList implements OnInit {
  private readonly userService = inject(UserService);

  readonly Role = Role;
  readonly pageSize = 10;

  readonly allUsers = signal<UserResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly currentPage = signal(1);

  readonly searchInput = signal('');
  readonly searchTerm = signal('');

  readonly isModalOpen = signal(false);
  readonly editingUser = signal<UserResponse | null>(null);

  readonly actionLoading = signal(false);

  constructor() {
    effect((onCleanup) => {
      const value = this.searchInput();

      const handle = window.setTimeout(() => {
        this.searchTerm.set(value);
        this.currentPage.set(1);
      }, 250);

      onCleanup(() => window.clearTimeout(handle));
    });

    effect(() => {
      const totalPages = this.totalPages();
      const currentPage = this.currentPage();

      if (currentPage > totalPages) {
        this.currentPage.set(totalPages);
        return;
      }

      if (currentPage < 1) {
        this.currentPage.set(1);
      }
    });
  }

  readonly filteredUsers = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const users = this.allUsers();

    if (!term) return users;

    return users.filter((user) => {
      const nombre = user.nombre?.toLowerCase() ?? '';
      const email = user.email?.toLowerCase() ?? '';
      const rol = user.rol?.toLowerCase() ?? '';
      const id = String(user.id);

      return (
        nombre.includes(term) ||
        email.includes(term) ||
        rol.includes(term) ||
        id.includes(term)
      );
    });
  });

  readonly totalUsers = computed(() => this.filteredUsers().length);

  readonly totalPages = computed(() =>
    Math.max(1, Math.ceil(this.totalUsers() / this.pageSize))
  );

  readonly safeCurrentPage = computed(() =>
    Math.min(this.currentPage(), this.totalPages())
  );

  readonly paginatedUsers = computed(() => {
    const start = (this.safeCurrentPage() - 1) * this.pageSize;
    return this.filteredUsers().slice(start, start + this.pageSize);
  });

  readonly paginationItems = computed(() => {
    const total = this.totalPages();
    const current = this.safeCurrentPage();

    if (total <= 7) {
      return Array.from({ length: total }, (_, i) => i + 1);
    }

    const pages: Array<number | '...'> = [];
    const addPage = (page: number | '...') => pages.push(page);

    addPage(1);

    if (current > 4) addPage('...');

    const start = Math.max(2, current - 1);
    const end = Math.min(total - 1, current + 1);

    for (let i = start; i <= end; i++) addPage(i);

    if (current < total - 3) addPage('...');

    addPage(total);

    return pages;
  });

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.error.set(null);

    this.userService.getAll().subscribe({
      next: (data) => {
        this.allUsers.set(data);
        this.currentPage.set(1);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Error al cargar los usuarios');
        this.loading.set(false);
      },
    });
  }

  onSearchChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchInput.set(value);
  }

  clearSearch(): void {
    this.searchInput.set('');
    this.searchTerm.set('');
    this.currentPage.set(1);
  }

  goToPage(page: number | '...'): void {
    if (page === '...') return;

    const total = this.totalPages();
    if (page < 1 || page > total || page === this.currentPage()) return;

    this.currentPage.set(page);
  }

  prevPage(): void {
    this.currentPage.update((p) => Math.max(1, p - 1));
  }

  nextPage(): void {
    this.currentPage.update((p) => Math.min(this.totalPages(), p + 1));
  }

  openModal(user?: UserResponse): void {
    this.editingUser.set(user ?? null);
    this.isModalOpen.set(true);
  }

  closeModal(): void {
    this.isModalOpen.set(false);
    this.editingUser.set(null);
  }

  onUserCreated(): void {
    this.loadUsers();
  }

  async toggleEnabled(user: UserResponse): Promise<void> {
    if (this.actionLoading()) return;

    const action = user.enabled ? 'desactivar' : 'activar';

    const result = await Swal.fire({
      title: 'Cambiar estado',
      text: `¿Estás seguro de ${action} al usuario ${user.nombre}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: action.charAt(0).toUpperCase() + action.slice(1),
      cancelButtonText: 'Cancelar',
      confirmButtonColor: user.enabled ? '#f59e0b' : '#10b981',
      cancelButtonColor: '#6c757d',
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;

    this.actionLoading.set(true);

    this.userService.toggleEnabled(user.id, !user.enabled).subscribe({
      next: (updatedUser) => {
        this.allUsers.update((users) =>
          users.map((u) => (u.id === updatedUser.id ? updatedUser : u))
        );

        this.actionLoading.set(false);

        Swal.fire({
          title: 'Actualizado',
          text: 'El estado del usuario fue actualizado correctamente.',
          icon: 'success',
          timer: 1800,
          showConfirmButton: false,
        });
      },
      error: () => {
        this.actionLoading.set(false);
        this.error.set('Error al actualizar el estado del usuario');
        Swal.fire({
          title: 'Error',
          text: 'No se pudo actualizar el estado del usuario.',
          icon: 'error',
          confirmButtonText: 'Aceptar',
        });
      },
    });
  }

  async deleteUser(id: number): Promise<void> {
    if (this.actionLoading()) return;

    const user = this.allUsers().find((u) => u.id === id);

    const result = await Swal.fire({
      title: 'Eliminar usuario',
      html: `¿Estás seguro de eliminar a <strong>${user?.nombre ?? 'este usuario'}</strong>?<br><small>Esta acción no se puede deshacer.</small>`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6c757d',
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;

    this.actionLoading.set(true);

    this.userService.delete(id).subscribe({
      next: () => {
        this.actionLoading.set(false);
        this.loadUsers();
        Swal.fire({
          title: 'Eliminado',
          text: 'El usuario fue eliminado correctamente.',
          icon: 'success',
          timer: 1800,
          showConfirmButton: false,
        });
      },
      error: () => {
        this.actionLoading.set(false);
        this.error.set('Error al eliminar el usuario');
        Swal.fire({
          title: 'Error',
          text: 'No se pudo eliminar el usuario.',
          icon: 'error',
          confirmButtonText: 'Aceptar',
        });
      },
    });
  }
}
