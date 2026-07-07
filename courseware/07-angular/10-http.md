# Module 10 — Making HTTP Requests

## Learning Objectives
- Set up Angular `HttpClient` with `provideHttpClient()`
- Make GET, POST, PUT, PATCH, DELETE requests
- Use typed HTTP responses
- Add HTTP interceptors (auth, logging, error handling)
- Handle errors with RxJS and Signals
- EMS: fetch real employee data from an API

---

## 10.1 Setup

```ts
// src/app/app.config.ts
import { ApplicationConfig }   from '@angular/core'
import { provideRouter }       from '@angular/router'
import { provideHttpClient, withInterceptors } from '@angular/common/http'
import { routes }              from './app.routes'
import { authInterceptor }     from './auth/interceptors/auth.interceptor'
import { errorInterceptor }    from './shared/interceptors/error.interceptor'

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor, errorInterceptor])
    ),
  ],
}
```

---

## 10.2 Environment Configuration

```ts
// src/environments/environment.ts
export const environment = {
  production:  false,
  apiBaseUrl: 'https://jsonplaceholder.typicode.com',
}

// src/environments/environment.production.ts
export const environment = {
  production:  true,
  apiBaseUrl: 'https://api.ibm-ems.com/v1',
}
```

---

## 10.3 Employee Service — HTTP Version

```ts
// src/app/employees/employee.service.ts
import { Injectable, inject, signal, computed } from '@angular/core'
import { HttpClient, HttpParams }               from '@angular/common/http'
import { Observable, throwError }               from 'rxjs'
import { catchError, tap, map }                 from 'rxjs/operators'
import { environment }                          from '../../environments/environment'
import type { Employee }                        from '../shared/models/employee.model'

// JSONPlaceholder user shape
interface ApiUser {
  id:       number
  name:     string
  email:    string
  phone:    string
  company:  { name: string }
  address:  { city: string; zipcode: string }
}

const DEPARTMENTS = ['Engineering', 'Marketing', 'HR', 'Finance', 'Sales'] as const

function mapApiUser(user: ApiUser): Employee {
  return {
    id:         user.id,
    name:       user.name,
    email:      user.email,
    phone:      user.phone,
    department: DEPARTMENTS[user.id % DEPARTMENTS.length],
    salary:     500_000 + (user.id * 73_333) % 1_500_000,
    isActive:   user.id % 5 !== 0,
    joinDate:   `202${user.id % 4}-0${(user.id % 9) + 1}-15`,
  }
}

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private http    = inject(HttpClient)
  private baseUrl = `${environment.apiBaseUrl}/users`

  // ── Signals for state ──────────────────────────────────────────────────────
  private _employees  = signal<Employee[]>([])
  private _loading    = signal(false)
  private _error      = signal<string | null>(null)

  readonly employees  = this._employees.asReadonly()
  readonly loading    = this._loading.asReadonly()
  readonly error      = this._error.asReadonly()

  readonly activeCount = computed(() => this._employees().filter(e => e.isActive).length)

  // ── HTTP methods (return Observables) ─────────────────────────────────────

  loadAll(): void {
    this._loading.set(true)
    this._error.set(null)

    this.http.get<ApiUser[]>(this.baseUrl)
      .pipe(
        map(users => users.map(mapApiUser)),
        catchError(err => {
          this._error.set(err.message ?? 'Failed to load employees')
          this._loading.set(false)
          return throwError(() => err)
        })
      )
      .subscribe(employees => {
        this._employees.set(employees)
        this._loading.set(false)
      })
  }

  getById$(id: number): Observable<Employee> {
    return this.http.get<ApiUser>(`${this.baseUrl}/${id}`)
      .pipe(
        map(mapApiUser),
        catchError(err => throwError(() => new Error(`Employee ${id} not found`)))
      )
  }

  create$(dto: Omit<Employee, 'id'>): Observable<Employee> {
    return this.http.post<ApiUser>(this.baseUrl, dto)
      .pipe(
        map(user => ({ ...mapApiUser(user), ...dto, id: Date.now() })),
        tap(created => this._employees.update(list => [...list, created]))
      )
  }

  update$(id: number, changes: Partial<Employee>): Observable<Employee> {
    return this.http.patch<ApiUser>(`${this.baseUrl}/${id}`, changes)
      .pipe(
        map(user => ({ ...mapApiUser(user), ...changes })),
        tap(updated => {
          this._employees.update(list =>
            list.map(e => e.id === id ? updated : e)
          )
        })
      )
  }

  delete$(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`)
      .pipe(
        tap(() => this._employees.update(list => list.filter(e => e.id !== id)))
      )
  }

  // ── Query params example ──────────────────────────────────────────────────

  search$(term: string): Observable<Employee[]> {
    const params = new HttpParams().set('q', term)
    return this.http.get<ApiUser[]>(this.baseUrl, { params })
      .pipe(map(users => users.map(mapApiUser)))
  }
}
```

---

## 10.4 Using the Service in Components

```ts
// src/app/employees/employee-list-page/employee-list-page.component.ts
import { Component, inject, OnInit } from '@angular/core'
import { EmployeeService }            from '../employee.service'
import { EmployeeCardComponent }      from '../employee-card/employee-card.component'

@Component({
  selector:    'app-employee-list-page',
  standalone:  true,
  imports:     [EmployeeCardComponent],
  templateUrl: './employee-list-page.component.html',
})
export class EmployeeListPageComponent implements OnInit {
  employeeService = inject(EmployeeService)

  ngOnInit() {
    this.employeeService.loadAll()   // triggers HTTP, updates signals
  }
}
```

```html
<!-- employee-list-page.component.html -->
@if (employeeService.loading()) {
  <div class="loading-state">
    <p>Loading employees...</p>
  </div>
}

@if (employeeService.error()) {
  <div class="error-state">
    <p>{{ employeeService.error() }}</p>
    <button (click)="employeeService.loadAll()">Retry</button>
  </div>
}

@if (!employeeService.loading() && !employeeService.error()) {
  <div class="employee-grid">
    @for (emp of employeeService.employees(); track emp.id) {
      <app-employee-card [employee]="emp" />
    } @empty {
      <p>No employees found.</p>
    }
  </div>
}
```

---

## 10.5 HTTP Interceptors

Interceptors run on every outgoing request or incoming response — one place for cross-cutting concerns.

### Auth Interceptor — attach token to every request

```ts
// src/app/auth/interceptors/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http'
import { inject }            from '@angular/core'
import { AuthService }       from '../auth.service'

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth  = inject(AuthService)
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

### Error Interceptor — global error handling

```ts
// src/app/shared/interceptors/error.interceptor.ts
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http'
import { inject }            from '@angular/core'
import { Router }            from '@angular/router'
import { catchError }        from 'rxjs/operators'
import { throwError }        from 'rxjs'
import { NotificationService } from '../services/notification.service'

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router        = inject(Router)
  const notifications = inject(NotificationService)

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      switch (error.status) {
        case 0:
          notifications.error('Network error. Check your connection.')
          break
        case 401:
          router.navigate(['/login'])
          break
        case 403:
          notifications.error('You do not have permission for this action.')
          break
        case 404:
          notifications.error('The requested resource was not found.')
          break
        case 500:
          notifications.error('Server error. Please try again later.')
          break
        default:
          notifications.error(`Unexpected error (${error.status})`)
      }
      return throwError(() => error)
    })
  )
}
```

### Loading Interceptor — global loading spinner

```ts
// src/app/shared/interceptors/loading.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http'
import { inject }            from '@angular/core'
import { finalize }          from 'rxjs/operators'
import { LoadingService }    from '../services/loading.service'

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const loading = inject(LoadingService)
  loading.start()

  return next(req).pipe(
    finalize(() => loading.stop())   // runs after complete OR error
  )
}
```

```ts
// src/app/shared/services/loading.service.ts
import { Injectable, signal, computed } from '@angular/core'

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private activeRequests = signal(0)
  readonly isLoading = computed(() => this.activeRequests() > 0)

  start() { this.activeRequests.update(n => n + 1) }
  stop()  { this.activeRequests.update(n => Math.max(0, n - 1)) }
}
```

---

## 10.6 HTTP Request Types — Full Reference

```ts
// GET — fetch data
this.http.get<Employee[]>('/api/employees')
this.http.get<Employee>(`/api/employees/${id}`)

// GET with query params
this.http.get<Employee[]>('/api/employees', {
  params: new HttpParams()
    .set('department', 'Engineering')
    .set('page', '1')
    .set('limit', '20'),
})

// POST — create
this.http.post<Employee>('/api/employees', newEmployeeData)

// PUT — full replace
this.http.put<Employee>(`/api/employees/${id}`, fullEmployeeData)

// PATCH — partial update
this.http.patch<Employee>(`/api/employees/${id}`, { isActive: false })

// DELETE
this.http.delete<void>(`/api/employees/${id}`)

// Custom headers
this.http.get<Employee[]>('/api/employees', {
  headers: { 'X-Custom-Header': 'value' }
})

// Get the full response (status, headers, body)
this.http.get<Employee[]>('/api/employees', { observe: 'response' })
  .subscribe(response => {
    console.log('Status:', response.status)
    console.log('Headers:', response.headers)
    console.log('Body:', response.body)
  })
```

---

## 10.7 Converting HTTP Observables to Signals

```ts
import { toSignal }     from '@angular/core/rxjs-interop'
import { inject }       from '@angular/core'
import { HttpClient }   from '@angular/common/http'
import { catchError }   from 'rxjs/operators'
import { of }           from 'rxjs'

@Component({ /* ... */ })
export class DepartmentListComponent {
  private http = inject(HttpClient)

  // toSignal converts an Observable to a Signal
  // Angular subscribes and unsubscribes automatically
  departments = toSignal(
    this.http.get<Department[]>('/api/departments').pipe(
      catchError(() => of([] as Department[]))
    ),
    { initialValue: [] as Department[] }
  )
}
```

```html
<!-- No loading state needed for simple cases -->
@for (dept of departments(); track dept.id) {
  <p>{{ dept.name }}</p>
}
```

---

## Summary

- Register `provideHttpClient(withInterceptors([...]))` in `app.config.ts`
- Inject `HttpClient` via `inject(HttpClient)` — never use `new`
- HTTP methods return cold Observables — subscribe or use `toSignal()`
- Interceptors: one place for auth tokens, error handling, loading state
- Use `tap()` to update local signals after successful HTTP calls
- Use `catchError()` to handle errors gracefully without crashing the stream

**Next → Module 11: Authentication & Route Protection**
