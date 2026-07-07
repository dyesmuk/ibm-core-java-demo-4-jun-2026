# Module 06 — Changing Pages with Routing

## Learning Objectives
- Configure Angular Router with standalone components
- Use route parameters, query parameters, and child routes
- Navigate programmatically with `Router` service
- Implement route guards for protection
- Lazy load feature modules for performance
- EMS: multi-page navigation — list, detail, create, edit, 404

---

## 6.1 How Angular Router Works

Angular Router intercepts browser URL changes and maps them to components — no full page reload.

```
URL: /employees/3/edit
        ↓
Router reads routes config
        ↓
Matches: { path: 'employees/:id/edit', component: EditEmployeePage }
        ↓
Renders EditEmployeePage inside <router-outlet>
        ↓
Component reads :id from ActivatedRoute
```

---

## 6.2 Route Configuration

```ts
// src/app/app.routes.ts
import { Routes } from '@angular/router'
import { authGuard }     from './auth/guards/auth.guard'
import { adminGuard }    from './auth/guards/admin.guard'

export const routes: Routes = [
  // Redirect root to /employees
  {
    path: '',
    redirectTo: '/employees',
    pathMatch: 'full',
  },

  // Eagerly loaded routes (loaded immediately)
  {
    path: 'login',
    loadComponent: () =>
      import('./auth/login/login.component').then(m => m.LoginComponent),
  },

  // Feature routes — lazy loaded
  {
    path: 'employees',
    loadChildren: () =>
      import('./employees/employees.routes').then(m => m.employeeRoutes),
  },

  {
    path: 'departments',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./departments/departments.routes').then(m => m.departmentRoutes),
  },

  // 404 — must be last
  {
    path: '**',
    loadComponent: () =>
      import('./shared/pages/not-found/not-found.component').then(m => m.NotFoundComponent),
  },
]
```

```ts
// src/app/employees/employees.routes.ts
import { Routes } from '@angular/router'
import { authGuard } from '../auth/guards/auth.guard'

export const employeeRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./employee-list/employee-list-page.component').then(m => m.EmployeeListPageComponent),
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./employee-detail/employee-detail-page.component').then(m => m.EmployeeDetailPageComponent),
  },
  {
    path: 'create',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./employee-form/employee-form-page.component').then(m => m.EmployeeFormPageComponent),
  },
  {
    path: ':id/edit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./employee-form/employee-form-page.component').then(m => m.EmployeeFormPageComponent),
    data: { mode: 'edit' },
  },
]
```

### Register in `app.config.ts`

```ts
// src/app/app.config.ts
import { ApplicationConfig } from '@angular/core'
import { provideRouter, withComponentInputBinding } from '@angular/router'
import { routes } from './app.routes'

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(
      routes,
      withComponentInputBinding(),    // maps route params to component inputs
    ),
  ],
}
```

`withComponentInputBinding()` is a powerful Angular 16+ feature — route params, query params, and route data are automatically bound to component `@Input()` / `input()` signals.

---

## 6.3 Router Outlet and Navigation Links

```html
<!-- app.component.html -->
<nav>
  <a routerLink="/employees" routerLinkActive="nav-active">Employees</a>
  <a routerLink="/departments" routerLinkActive="nav-active">Departments</a>
  <a routerLink="/projects" routerLinkActive="nav-active">Projects</a>
</nav>

<main>
  <router-outlet />   <!-- matched component renders here -->
</main>
```

```ts
// app.component.ts — must import RouterLink, RouterLinkActive, RouterOutlet
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router'

@Component({
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  // ...
})
export class AppComponent {}
```

```html
<!-- routerLinkActive with exact match -->
<a routerLink="/"
   routerLinkActive="active"
   [routerLinkActiveOptions]="{ exact: true }">
  Home
</a>

<!-- Dynamic links -->
<a [routerLink]="['/employees', emp.id]">{{ emp.name }}</a>
<a [routerLink]="['/employees', emp.id, 'edit']">Edit</a>
```

---

## 6.4 Reading Route Parameters

### With `withComponentInputBinding()` — cleanest approach (Angular 16+)

Route params are automatically bound to component inputs. No `ActivatedRoute` injection needed.

```ts
// employee-detail-page.component.ts
// Route: /employees/:id
import { Component, input, computed, inject, effect } from '@angular/core'
import { EmployeeService } from '../employee.service'

@Component({ standalone: true, /* ... */ })
export class EmployeeDetailPageComponent {
  // 'id' matches the :id param in the route — Angular binds it automatically
  id = input.required<string>()

  private employeeService = inject(EmployeeService)

  employee = computed(() =>
    this.employeeService.getById(Number(this.id()))
  )
}
```

```html
<!-- employee-detail-page.component.html -->
@if (employee()) {
  <h1>{{ employee()!.name }}</h1>
  <p>{{ employee()!.department }}</p>
  <p>{{ employee()!.email }}</p>
} @else {
  <p>Employee not found.</p>
}
```

### With `ActivatedRoute` — legacy / manual approach

```ts
import { Component, inject, signal, effect } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { EmployeeService } from '../employee.service'
import type { Employee } from '../../shared/models/employee.model'

@Component({ standalone: true, /* ... */ })
export class EmployeeDetailPageComponent {
  private route           = inject(ActivatedRoute)
  private employeeService = inject(EmployeeService)

  employee = signal<Employee | null>(null)

  constructor() {
    // route.params is an Observable — covered in Module 08
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'))
      this.employee.set(this.employeeService.getById(id) ?? null)
    })
  }
}
```

---

## 6.5 Query Parameters

Query parameters (`/employees?dept=Engineering&page=2`) are great for filter/sort/pagination state because they survive page refresh and are shareable.

```ts
// Reading query params with component input binding
// Route: /employees?dept=Engineering&page=2
@Component({ standalone: true, /* ... */ })
export class EmployeeListPageComponent {
  dept   = input<string>('All')    // maps to ?dept=
  page   = input<string>('1')      // maps to ?page=

  private employeeService = inject(EmployeeService)

  currentPage = computed(() => Number(this.page()))

  filteredEmployees = computed(() =>
    this.employeeService.employees()
      .filter(e => this.dept() === 'All' || e.department === this.dept())
  )
}
```

```ts
// Writing query params — updating URL without navigation
import { Router } from '@angular/router'

@Component({ standalone: true, /* ... */ })
export class EmployeeListPageComponent {
  private router = inject(Router)

  onFilterChange(dept: string) {
    this.router.navigate([], {
      queryParams: { dept, page: 1 },
      queryParamsHandling: 'merge',   // preserve other query params
    })
  }
}
```

---

## 6.6 Route Data

Pass static data to routes — useful for titles, breadcrumbs, permissions.

```ts
// routes
{
  path: ':id/edit',
  component: EmployeeFormPageComponent,
  data: { mode: 'edit', title: 'Edit Employee' },
},
{
  path: 'create',
  component: EmployeeFormPageComponent,
  data: { mode: 'create', title: 'New Employee' },
},
```

```ts
// Read with component input binding
@Component({ standalone: true, /* ... */ })
export class EmployeeFormPageComponent {
  mode  = input<string>('create')   // bound from route data
  title = input<string>('New Employee')
}
```

---

## 6.7 Programmatic Navigation

```ts
import { Component, inject } from '@angular/core'
import { Router } from '@angular/router'
import { EmployeeService } from '../employee.service'

@Component({ standalone: true, /* ... */ })
export class EmployeeFormPageComponent {
  private router          = inject(Router)
  private employeeService = inject(EmployeeService)

  onSave(formValue: Partial<Employee>) {
    const saved = this.employeeService.add(formValue as Omit<Employee, 'id'>)
    this.router.navigate(['/employees', saved.id])   // go to detail page
  }

  onCancel() {
    this.router.navigate(['/employees'])         // go to list
    // or:
    this.router.navigateByUrl('/employees')      // absolute URL string
    // or go back:
    history.back()
  }
}
```

---

## 6.8 Route Guards

Guards run before a route is activated and can block navigation.

### `authGuard` — functional guard (Angular 15+ — preferred)

```ts
// src/app/auth/guards/auth.guard.ts
import { inject }       from '@angular/core'
import { CanActivateFn, Router } from '@angular/router'
import { AuthService } from '../auth.service'

export const authGuard: CanActivateFn = (route, state) => {
  const auth   = inject(AuthService)
  const router = inject(Router)

  if (auth.isLoggedIn()) {
    return true
  }

  // Not logged in — redirect to login, remember original destination
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url },
  })
}
```

```ts
// src/app/auth/guards/admin.guard.ts
import { inject }       from '@angular/core'
import { CanActivateFn, Router } from '@angular/router'
import { AuthService } from '../auth.service'

export const adminGuard: CanActivateFn = () => {
  const auth   = inject(AuthService)
  const router = inject(Router)

  if (auth.currentUser()?.role === 'admin') return true
  return router.createUrlTree(['/unauthorized'])
}
```

```ts
// In routes config
{
  path: 'employees/create',
  canActivate: [authGuard],
  loadComponent: () => import('./employee-form/employee-form-page.component')
    .then(m => m.EmployeeFormPageComponent),
},
```

### `canDeactivate` — unsaved changes guard

```ts
// src/app/auth/guards/unsaved-changes.guard.ts
import { CanDeactivateFn } from '@angular/router'

export interface CanComponentDeactivate {
  canDeactivate: () => boolean
}

export const unsavedChangesGuard: CanDeactivateFn<CanComponentDeactivate> =
  (component) => {
    if (component.canDeactivate()) return true
    return window.confirm('You have unsaved changes. Leave anyway?')
  }
```

```ts
// In the form component
export class EmployeeFormPageComponent implements CanComponentDeactivate {
  private isDirty = signal(false)

  canDeactivate(): boolean {
    return !this.isDirty()
  }
}
```

---

## 6.9 Lazy Loading

Route-level lazy loading (already shown above with `loadComponent` / `loadChildren`) splits the app into chunks that only download when the user navigates to that route.

```ts
// Without lazy loading — all code downloaded upfront
import { EmployeeListPageComponent } from './employees/employee-list-page.component'
{ path: 'employees', component: EmployeeListPageComponent }

// With lazy loading — code downloaded only when /employees is visited
{
  path: 'employees',
  loadComponent: () =>
    import('./employees/employee-list-page.component')
      .then(m => m.EmployeeListPageComponent),
}
```

Check the network tab — on first load, employee code is absent. Navigate to `/employees` — the chunk downloads.

---

## 6.10 Named Router Outlets (Advanced)

```html
<!-- Two outlets — primary and sidebar -->
<router-outlet />
<router-outlet name="sidebar" />
```

```ts
// Navigate to fill both outlets
this.router.navigate([
  { outlets: { primary: ['employees'], sidebar: ['employee-preview', id] } }
])
```

---

## 6.11 EMS Full Route Map

```
/                            → redirect to /employees
/login                       → LoginComponent
/employees                   → EmployeeListPageComponent
/employees/create            → EmployeeFormPageComponent (mode: create) [auth guard]
/employees/:id               → EmployeeDetailPageComponent
/employees/:id/edit          → EmployeeFormPageComponent (mode: edit) [auth guard]
/departments                 → DepartmentListPageComponent [auth guard]
/departments/:id             → DepartmentDetailPageComponent [auth guard]
/projects                    → ProjectListPageComponent [auth guard]
/unauthorized                → UnauthorizedComponent
**                           → NotFoundComponent (404)
```

---

## Summary

| API | Purpose |
|-----|---------|
| `provideRouter(routes)` | Register router in app config |
| `withComponentInputBinding()` | Auto-bind route params to inputs |
| `<router-outlet>` | Where matched component renders |
| `routerLink` | Navigation links (no reload) |
| `routerLinkActive` | Add CSS class when route matches |
| `input()` with param name | Read route/query params (with `withComponentInputBinding`) |
| `Router.navigate()` | Programmatic navigation |
| `CanActivateFn` | Guard routes from unauthorized access |
| `loadComponent` | Lazy load a single component |
| `loadChildren` | Lazy load a feature's routes |

**Next → Module 07: Understanding Observables**
