import { Component, inject } from '@angular/core';

import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { Role } from '../../../../core/models/role.enum';


@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {

  private readonly authService = inject(AuthService);
  
  user = this.authService.getUser();
  readonly Role = Role;

  hasRole(...roles: Role[]): boolean {
    return roles.includes(this.user?.rol as Role);
  }

  logout(): void{
    this.authService.logout();
  }

}
