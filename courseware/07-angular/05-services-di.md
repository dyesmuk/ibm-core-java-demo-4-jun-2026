# Module 05 — Services & Dependency Injection

## Learning Objectives
- Understand what a service is and why it exists
- Use Angular's Dependency Injection container
- Provide services at different scopes (root, component, module)
- Build a full `EmployeeService` with Signals
- Share state between unrelated components
- EMS: `EmployeeService`, `DepartmentService`, `NotificationService`

---

## 5.1 Why Services?

Components should only handle **what the user sees**. Business logic, data management, and cross-component communication belong in services.

```
Without services:                  With services:
AppComponent                       AppComponent
  owns employees array               injects EmployeeService
  makes HTTP calls          vs       EmployeeService owns state + API calls
  filters/sorts data                 Components inject and use it
  handles authentication             Any component can read the same data
```

**Services solve:**
- Logic reuse across components
- Shared state — multiple components reading the same data
- Separation of concerns — component = view; service = business logic
- Testability — inject mock services in tests

---

## 5.2 Creating a Service

```bash
ng generate service employees/employee
# creates: src/app/employees/employee.service.ts
```

```ts
// src/app/employees/employee.service.ts
import { Injectable, signal, computed } from '@angular/core'
import type { Employee, DepartmentName } from '../shared/models/employee.model'
import { INITIAL_EMPLOYEES } from '../shared/data/employees.data'

@Injectable({
  providedIn: 'root'   // singleton — same instance everywhere in the app
})
export class EmployeeService {
  // Private signals — no external mutation
  private _employees  = signal<Employee[]>(INITIAL_EMPLOYEES)
  private _loading    = signal<boolean>(false)
  private _error      = signal<string | null>(null)

  // Public read-only view of the signals
  readonly employees  = this._employees.asReadonly()
  readonly loading    = this._loading.asReadonly()
  readonly error      = this._error.asReadonly()

  // Computed derived signals
  readonly activeEmployees  = computed(() => this._employees().filter(e => e.isActive))
  readonly totalCount       = computed(() => this._employees().length)
  readonly activeCount      = computed(() => this.activeEmployees().length)
  readonly departmentCounts = computed(() => {
    const counts: Record<string, number> = {}
    this._employees().forEach(e => {
      counts[e.department] = (counts[e.department] ?? 0) + 1
    })
    return counts
  })

  private nextId = this._employees().length + 1

  // ── CRUD methods ─────────────────────────────────────────────────────────

  getById(id: number): Employee | undefined {
    return this._employees().find(e => e.id === id)
  }

  add(dto: Omit<Employee, 'id'>): Employee {
    const newEmployee: Employee = { ...dto, id: this.nextId++ }
    this._employees.update(list => [...list, newEmployee])
    return newEmployee
  }

  update(id: number, changes: Partial<Employee>): void {
    this._employees.update(list =>
      list.map(e => e.id === id ? { ...e, ...changes } : e)
    )
  }

  remove(id: number): void {
    this._employees.update(list => list.filter(e => e.id !== id))
  }

  toggleActive(id: number): void {
    this.update(id, {
      isActive: !this.getById(id)?.isActive
    })
  }

  // ── Filter helpers (if not using computed in component) ──────────────────

  getByDepartment(dept: DepartmentName): Employee[] {
    if (dept === 'All') return this._employees()
    return this._employees().filter(e => e.department === dept)
  }

  search(term: string): Employee[] {
    const lower = term.toLowerCase()
    return this._employees().filter(e =>
      e.name.toLowerCase().includes(lower) ||
      e.email.toLowerCase().includes(lower)
    )
  }
}
```

---

## 5.3 Injecting Services

### Modern: `inject()` function (Angular 14+ — preferred)

```ts
import { Component, inject, computed, signal } from '@angular/core'
import { EmployeeService } from '../employee.service'
import type { DepartmentName } from '../../shared/models/employee.model'

@Component({ /* ... */ })
export class EmployeeListComponent {
  // inject() works at field initialisation time or in constructor
  private employeeService = inject(EmployeeService)

  // Use service signals directly in templates
  employees = this.employeeService.employees
  loading   = this.employeeService.loading

  filter    = signal<DepartmentName>('All')
  search    = signal<string>('')

  filteredEmployees = computed(() =>
    this.employeeService.employees()
      .filter(e => this.filter() === 'All' || e.department === this.filter())
      .filter(e => e.name.toLowerCase().includes(this.search().toLowerCase()))
  )

  onRemove(id: number)       { this.employeeService.remove(id) }
  onToggle(id: number)       { this.employeeService.toggleActive(id) }
  onFilterChange(dept: DepartmentName) { this.filter.set(dept) }
}
```

### Legacy: Constructor injection (still works, you'll see this everywhere)

```ts
import { Component } from '@angular/core'
import { EmployeeService } from '../employee.service'

@Component({ /* ... */ })
export class EmployeeListComponent {
  constructor(private employeeService: EmployeeService) {}

  // Same access: this.employeeService.employees()
}
```

---

## 5.4 Dependency Injection — How It Works

Angular maintains a hierarchical injector tree. When you request a service, Angular walks up the tree to find the nearest provider.

```
Root Injector (providedIn: 'root')
├── EmployeeService          ← singleton, one instance for entire app
├── AuthService
└── HttpClient

  Platform Injector
  └── (Angular internals)

    Component Injector (providers: [SomeService] in @Component)
    └── SomeService          ← new instance just for this component + its children
```

### `providedIn: 'root'` vs component-level

```ts
// providedIn: 'root' — singleton, shared across the whole app
// Use for: shared state, HTTP services, auth
@Injectable({ providedIn: 'root' })
export class EmployeeService {}

// Component-level — new instance per component
// Use for: stateful services that should reset when component destroys
@Component({
  providers: [EmployeeFormService],   // fresh instance for this component
})
export class CreateEmployeeComponent {
  private formService = inject(EmployeeFormService)
}
```

---

## 5.5 Service-to-Service Injection

Services can inject other services:

```ts
// src/app/employees/employee.service.ts
import { Injectable, signal, inject } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { NotificationService } from '../shared/services/notification.service'
import { AuthService } from '../auth/auth.service'

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private http             = inject(HttpClient)
  private notifications    = inject(NotificationService)
  private auth             = inject(AuthService)
  // ...
}
```

---

## 5.6 `NotificationService` — Cross-Component Communication

Services with Signals enable any component to communicate without parent-child relationships.

```ts
// src/app/shared/services/notification.service.ts
import { Injectable, signal } from '@angular/core'

export interface Notification {
  id:      number
  type:    'success' | 'error' | 'info' | 'warning'
  message: string
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private _notifications = signal<Notification[]>([])
  readonly notifications = this._notifications.asReadonly()

  private nextId = 1

  show(type: Notification['type'], message: string, durationMs = 4000): void {
    const notification: Notification = { id: this.nextId++, type, message }
    this._notifications.update(list => [...list, notification])

    if (durationMs > 0) {
      setTimeout(() => this.dismiss(notification.id), durationMs)
    }
  }

  success(message: string) { this.show('success', message) }
  error  (message: string) { this.show('error', message, 0) }  // manual dismiss
  info   (message: string) { this.show('info', message) }

  dismiss(id: number): void {
    this._notifications.update(list => list.filter(n => n.id !== id))
  }
}
```

```ts
// employee.service.ts — use NotificationService after operations
remove(id: number): void {
  const name = this.getById(id)?.name
  this._employees.update(list => list.filter(e => e.id !== id))
  this.notifications.success(`${name} removed successfully`)
}
```

```html
<!-- notification-toast.component.html — shown in AppComponent -->
@for (n of notifications.notifications(); track n.id) {
  <div class="toast toast--{{ n.type }}">
    <span>{{ n.message }}</span>
    <button (click)="notifications.dismiss(n.id)">✕</button>
  </div>
}
```

---

## 5.7 `DepartmentService`

```ts
// src/app/departments/department.service.ts
import { Injectable, signal, computed, inject } from '@angular/core'
import { EmployeeService } from '../employees/employee.service'
import type { Department } from '../shared/models/employee.model'

@Injectable({ providedIn: 'root' })
export class DepartmentService {
  private employeeService = inject(EmployeeService)

  private _departments = signal<Department[]>([
    { id: 1, name: 'Engineering' },
    { id: 2, name: 'Marketing' },
    { id: 3, name: 'HR' },
    { id: 4, name: 'Finance' },
    { id: 5, name: 'Sales' },
  ])

  readonly departments = this._departments.asReadonly()

  // Cross-service computed — combines both services
  readonly departmentStats = computed(() =>
    this._departments().map(dept => ({
      ...dept,
      employeeCount: this.employeeService.employees()
        .filter(e => e.department === dept.name).length,
      activeCount: this.employeeService.employees()
        .filter(e => e.department === dept.name && e.isActive).length,
    }))
  )

  add(name: string): void {
    const next = this._departments().length + 1
    this._departments.update(list => [...list, { id: next, name }])
  }
}
```

---

## 5.8 EMS Service Architecture

```
src/app/
├── employees/
│   └── employee.service.ts          ← CRUD, signals, filtering
├── departments/
│   └── department.service.ts        ← Dept CRUD, cross-service stats
├── auth/
│   └── auth.service.ts              ← User, login, token (Module 12)
└── shared/
    └── services/
        ├── notification.service.ts  ← Cross-component toast messages
        └── storage.service.ts       ← localStorage abstraction
```

---

## Summary

| Concept | Key Rule |
|---------|----------|
| `@Injectable({ providedIn: 'root' })` | Singleton — one instance, shared everywhere |
| `inject()` | Preferred injection method in Angular 14+ |
| Constructor injection | Legacy — still works, common in older code |
| Component-level providers | New instance per component |
| Service signals | Private `signal()` + public `asReadonly()` |
| Cross-component communication | Service with signals — any component can read/write |

**Next → Module 06: Routing**
