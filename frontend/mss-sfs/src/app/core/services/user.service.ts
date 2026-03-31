import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { UserResponse } from '../models/user-model';


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

  create(payload: Partial<UserResponse>): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.apiUrl, payload);
  }

  update(id: number, payload: Partial<UserResponse>): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleEnabled(id: number, enabled: boolean): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${this.apiUrl}/${id}/status`, { enabled });
  }
}
