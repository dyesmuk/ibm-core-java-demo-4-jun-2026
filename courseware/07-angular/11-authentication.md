# Module 11 — Authentication & Route Protection

## Learning Objectives
- Implement JWT-based login/logout flow
- Build an `AuthService` with Signals
- Protect routes with functional guards
- Attach tokens via HTTP interceptor
- Show role-based UI elements
- EMS: login page, token persistence, admin-only routes

---

## 11.1 Auth Flow Overview

```
User visits /employees/create (protected)
        ↓
authGuard runs → checks AuthService.isLoggedIn()
        ↓
    ┌──────────────────┐
  Not logged in    Logged in
        ↓                ↓
Redirect to       Check role if needed
/login?returnUrl       ↓
        ↓         Render the component
User logs in
        ↓
AuthService stores token + user
        ↓
Redirect back to /employees/create
```

---

## 11.2 Auth Models and Service

```ts
// src/app/shared/models/auth.model.ts
export interface LoginCredentials {
  email:    string
  password: string
}

export interface AuthUser {
  id:    number
  name:  string
  email: string
  role:  'admin' | 'manager' | 'user'
  token: string
}

export interface LoginResponse {
  user:  AuthUser
  token: string
}
```

```ts
// src/app/auth/auth.service.ts
import { Injectable, signal, computed, inject } from '@angular/core'
import { HttpClient }  from '@angular/common/http'
import { Router }      from '@angular/router'
import { tap }         from 'rxjs/operators'
import { Observable }  from 'rxjs'
import { environment } from '../../environments/environment'
import type { AuthUser, LoginCredentials, LoginResponse } from '../shared/models/auth.model'

const TOKEN_KEY = 'ems_token'
const USER_KEY  = 'ems_user'

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http   = inject(HttpClient)
  private router = inject(Router)

  // ── Signals ──────────────────────────────────────────────────────────────
  private _currentUser = signal<AuthUser | null>(this.loadUserFromStorage())

  readonly currentUser    = this._currentUser.asReadonly()
  readonly isLoggedIn     = computed(() => this._currentUser() !== null)
  readonly isAdmin        = computed(() => this._currentUser()?.role === 'admin')
  readonly isManager      = computed(() => ['admin', 'manager'].includes(this._currentUser()?.role ?? ''))
  readonly userName       = computed(() => this._currentUser()?.name ?? '')

  // ── Restore session from localStorage ───────────────────────────────────
  private loadUserFromStorage(): AuthUser | null {
    try {
      const stored = localStorage.getItem(USER_KEY)
      return stored ? JSON.parse(stored) as AuthUser : null
    } catch {
      return null
    }
  }

  // ── Token helpers ────────────────────────────────────────────────────────
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
  }

  private saveSession(user: AuthUser): void {
    localStorage.setItem(TOKEN_KEY, user.token)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
    this._currentUser.set(user)
  }

  private clearSession(): void {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    this._currentUser.set(null)
  }

  // ── Auth operations ──────────────────────────────────────────────────────
  login(credentials: LoginCredentials): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiBaseUrl}/auth/login`, credentials)
      .pipe(
        tap(response => this.saveSession(response.user))
      )
  }

  logout(): void {
    this.clearSession()
    this.router.navigate(['/login'])
  }

  hasRole(roles: string[]): boolean {
    return roles.includes(this._currentUser()?.role ?? '')
  }
}
```

---

## 11.3 Mock Auth Service (No Backend Needed)

```ts
// src/app/auth/mock-auth.service.ts
import { Injectable, signal, computed } from '@angular/core'
import { Router }   from '@angular/router'
import { inject }   from '@angular/core'
import { Observable, of, throwError, timer } from 'rxjs'
import { switchMap } from 'rxjs/operators'
import type { AuthUser, LoginCredentials, LoginResponse } from '../shared/models/auth.model'

const MOCK_USERS: (AuthUser & { password: string })[] = [
  { id: 1, name: 'Admin User',   email: 'admin@ibm.com',   password: 'admin123',   role: 'admin',   token: 'mock-admin-token'   },
  { id: 2, name: 'Manager Maya', email: 'manager@ibm.com', password: 'manager123', role: 'manager', token: 'mock-manager-token' },
  { id: 3, name: 'User Rahul',   email: 'user@ibm.com',    password: 'user123',    role: 'user',    token: 'mock-user-token'    },
]

@Injectable({ providedIn: 'root' })
export class MockAuthService {
  private router = inject(Router)

  private _currentUser = signal<AuthUser | null>(null)

  readonly currentUser = this._currentUser.asReadonly()
  readonly isLoggedIn  = computed(() => this._currentUser() !== null)
  readonly isAdmin     = computed(() => this._currentUser()?.role === 'admin')
  readonly isManager   = computed(() => ['admin','manager'].includes(this._currentUser()?.role ?? ''))

  getToken(): string | null {
    return this._currentUser()?.token ?? null
  }

  login(credentials: LoginCredentials): Observable<LoginResponse> {
    return timer(600).pipe(
      switchMap(() => {
        const found = MOCK_USERS.find(
          u => u.email === credentials.email && u.password === credentials.password
        )
        if (!found) return throwError(() => new Error('Invalid email or password'))

        const { password, ...user } = found
        this._currentUser.set(user)
        return of({ user, token: user.token })
      })
    )
  }

  logout(): void {
    this._currentUser.set(null)
    this.router.navigate(['/login'])
  }

  hasRole(roles: string[]): boolean {
    return roles.includes(this._currentUser()?.role ?? '')
  }
}
```

---

## 11.4 Login Component

```bash
ng g c auth/login
```

```ts
// src/app/auth/login/login.component.ts
import { Component, inject, signal } from '@angular/core'
import { Router, ActivatedRoute }     from '@angular/router'
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms'
import { MockAuthService }            from '../mock-auth.service'

@Component({
  selector:    'app-login',
  standalone:  true,
  imports:     [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl:    './login.component.css',
})
export class LoginComponent {
  private fb          = inject(FormBuilder)
  private authService = inject(MockAuthService)
  private router      = inject(Router)
  private route       = inject(ActivatedRoute)

  isSubmitting = signal(false)
  error        = signal<string | null>(null)

  form = this.fb.group({
    email:    ['admin@ibm.com', [Validators.required, Validators.email]],
    password: ['admin123',     [Validators.required, Validators.minLength(6)]],
  })

  get emailCtrl()    { return this.form.get('email')! }
  get passwordCtrl() { return this.form.get('password')! }

  onSubmit() {
    if (this.form.invalid) return

    this.isSubmitting.set(true)
    this.error.set(null)

    const returnUrl = this.route.snapshot.queryParams['returnUrl'] ?? '/employees'

    this.authService.login(this.form.value as { email: string; password: string })
      .subscribe({
        next: () => {
          this.router.navigateByUrl(returnUrl)
        },
        error: (err: Error) => {
          this.error.set(err.message)
          this.isSubmitting.set(false)
        },
      })
  }
}
```

```html
<!-- login.component.html -->
<div class="login-page">
  <div class="login-card">
    <h1>IBM EMS</h1>
    <p class="subtitle">Sign in to continue</p>

    <form [formGroup]="form" (ngSubmit)="onSubmit()">

      <div class="form-group">
        <label for="email">Email</label>
        <input
          id="email"
          type="email"
          formControlName="email"
          autocomplete="email"
          placeholder="admin@ibm.com"
        />
        @if (emailCtrl.touched && emailCtrl.errors?.['email']) {
          <span class="error">Enter a valid email</span>
        }
      </div>

      <div class="form-group">
        <label for="password">Password</label>
        <input
          id="password"
          type="password"
          formControlName="password"
          autocomplete="current-password"
          placeholder="••••••••"
        />
      </div>

      @if (error()) {
        <div class="error-banner">{{ error() }}</div>
      }

      <button type="submit" [disabled]="form.invalid || isSubmitting()">
        {{ isSubmitting() ? 'Signing in...' : 'Sign In' }}
      </button>
    </form>

    <div class="demo-credentials">
      <p><strong>Demo accounts:</strong></p>
      <p>admin@ibm.com / admin123 (Admin)</p>
      <p>manager@ibm.com / manager123 (Manager)</p>
      <p>user@ibm.com / user123 (User)</p>
    </div>
  </div>
</div>
```

```css
/* login.component.css */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f4f4f4;
}

.login-card {
  background: white;
  border-radius: 8px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
}

.login-card h1 {
  color: var(--color-primary);
  margin-bottom: 4px;
}

.subtitle {
  color: #525252;
  margin-bottom: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 16px;
}

.form-group input {
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 14px;
}

button[type="submit"] {
  width: 100%;
  padding: 12px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-banner {
  background: #fff1f1;
  color: var(--color-danger);
  border: 1px solid var(--color-danger);
  border-radius: 4px;
  padding: 10px 12px;
  margin-bottom: 12px;
  font-size: 14px;
}

.error { color: var(--color-danger); font-size: 12px; }

.demo-credentials {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
  font-size: 13px;
  color: #525252;
}
```

---

## 11.5 Route Guards

```ts
// src/app/auth/guards/auth.guard.ts
import { inject }           from '@angular/core'
import { CanActivateFn, Router } from '@angular/router'
import { MockAuthService }  from '../mock-auth.service'

export const authGuard: CanActivateFn = (route, state) => {
  const auth   = inject(MockAuthService)
  const router = inject(Router)

  if (auth.isLoggedIn()) return true

  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  })
}
```

```ts
// src/app/auth/guards/role.guard.ts
import { inject }           from '@angular/core'
import { CanActivateFn, Router } from '@angular/router'
import { MockAuthService }  from '../mock-auth.service'

// Factory guard — creates a guard that checks a specific role
export function roleGuard(allowedRoles: string[]): CanActivateFn {
  return () => {
    const auth   = inject(MockAuthService)
    const router = inject(Router)

    if (auth.hasRole(allowedRoles)) return true
    return router.createUrlTree(['/unauthorized'])
  }
}
```

```ts
// app.routes.ts — use the guards
{
  path: 'employees/create',
  canActivate: [authGuard],
  loadComponent: () => import('./employees/employee-form-page/employee-form-page.component')
    .then(m => m.EmployeeFormPageComponent),
},
{
  path: 'admin',
  canActivate: [authGuard, roleGuard(['admin'])],
  loadChildren: () => import('./admin/admin.routes').then(m => m.adminRoutes),
},
```

---

## 11.6 Auth Interceptor

```ts
// src/app/auth/interceptors/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http'
import { inject }            from '@angular/core'
import { MockAuthService }   from '../mock-auth.service'

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth  = inject(MockAuthService)
  const token = auth.getToken()

  if (token) {
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    })
    return next(authReq)
  }

  return next(req)
}
```

---

## 11.7 Role-Based UI

```html
<!-- navbar.component.html -->
<nav>
  <a routerLink="/employees">Employees</a>
  <a routerLink="/departments">Departments</a>

  @if (auth.isManager()) {
    <a routerLink="/reports">Reports</a>
  }

  @if (auth.isAdmin()) {
    <a routerLink="/admin">Admin</a>
  }

  @if (auth.isLoggedIn()) {
    <span>{{ auth.userName() }}</span>
    <button (click)="auth.logout()">Sign Out</button>
  } @else {
    <a routerLink="/login">Sign In</a>
  }
</nav>
```

```html
<!-- employee-detail.component.html -->
<div class="actions">
  @if (auth.isLoggedIn()) {
    <a [routerLink]="['/employees', employee().id, 'edit']">Edit</a>
  }

  @if (auth.isAdmin()) {
    <button (click)="onDelete()">Delete Employee</button>
  }
</div>
```

---

## 11.8 Token Security — Production Notes

| Storage | XSS Safe | CSRF Safe | Notes |
|---------|----------|-----------|-------|
| `localStorage` | ❌ | ✅ | Simple, common in SPAs |
| `sessionStorage` | ❌ | ✅ | Cleared when tab closes |
| `httpOnly` cookie | ✅ | ❌ (mitigated) | Safest — can't be read by JS |
| Memory (signal) | ✅ | ✅ | Lost on refresh — needs refresh token |

**For training/demos:** `localStorage` is fine.  
**For production:** Use `httpOnly` cookies + CSRF tokens, or in-memory tokens + refresh token rotation.

---

## Summary

| Concept | Implementation |
|---------|---------------|
| Auth state | `signal<AuthUser \| null>` in `AuthService` |
| Login | POST to `/auth/login`, store token + user |
| Logout | Clear storage, navigate to `/login` |
| Guard | `CanActivateFn` — returns `true` or `UrlTree` |
| Role guard | Factory function returning `CanActivateFn` |
| Token attachment | HTTP interceptor sets `Authorization` header |
| Role-based UI | `@if (auth.isAdmin())` in templates |
| Return URL | `?returnUrl=` query param, navigate after login |

**Next → Module 12: Dynamic Components**
