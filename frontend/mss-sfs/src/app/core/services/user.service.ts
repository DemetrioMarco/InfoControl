import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import {
  CreateUserDto,
  UpdateUserDto,
  UpdateUserStatusDto,
  UserResponse,
} from '../models/user-model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/api/usuarios`;

  getAll(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(this.apiUrl);
  }

  getById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.apiUrl}/${id}`);
  }

  create(payload: CreateUserDto): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.apiUrl, payload);
  }

  update(id: number, payload: UpdateUserDto): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleEnabled(id: number, enabled: boolean): Observable<UserResponse> {
    const payload: UpdateUserStatusDto = { enabled };
    return this.http.patch<UserResponse>(`${this.apiUrl}/${id}/status`, payload);
  }

  checkEmailExists(email: string, excludeId?: number | null): Observable<boolean> {
    let params = new HttpParams().set('email', email);

    if (excludeId !== undefined && excludeId !== null) {
      params = params.set('excludeId', String(excludeId));
    }

    return this.http.get<boolean>(`${this.apiUrl}/exists-email`, { params });
  }
}
