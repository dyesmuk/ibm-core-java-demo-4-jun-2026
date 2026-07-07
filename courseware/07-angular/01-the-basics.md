# Module 01 — The Basics: Components, Templates & Data Binding

## Learning Objectives
- Understand what a component is in Angular
- Know all four data binding types
- Use Signals for reactive state
- Understand Angular's change detection model
- EMS: Build the first `EmployeeCardComponent`

---

## 1.1 The Building Block: Components

Every Angular UI is made of **components**. A component is a TypeScript class decorated with `@Component` that controls a piece of the UI.

Three files per component (or one file if you prefer inline):

```
employee-card/
├── employee-card.component.ts    ← class, decorator, logic
├── employee-card.component.html  ← template
└── employee-card.component.css   ← styles (scoped to this component)
```

Generate with the CLI (always use the CLI — it wires everything correctly):

```bash
ng generate component employees/employee-card
# shorthand:
ng g c employees/employee-card
```

### Anatomy of a component

```ts
// src/app/employees/employee-card/employee-card.component.ts
import { Component, signal } from '@angular/core'

@Component({
  selector:    'app-employee-card',  // custom HTML tag used in templates
  standalone:  true,
  imports:     [],                   // other components/directives/pipes this uses
  templateUrl: './employee-card.component.html',
  styleUrl:    './employee-card.component.css',
})
export class EmployeeCardComponent {
  // Component logic lives here as class properties and methods
  name    = 'Alice Johnson'
  salary  = 95000
  isActive = true
}
```

```html
<!-- employee-card.component.html -->
<div class="card">
  <h3>{{ name }}</h3>
  <p>Salary: {{ salary | currency }}</p>
</div>
```

Use it in a parent template:

```html
<!-- app.component.html -->
<app-employee-card />
```

And import it:

```ts
// app.component.ts
imports: [EmployeeCardComponent],
```

---

## 1.2 The Four Types of Data Binding

Angular's template binding syntax is the most important thing to master.

### Type 1 — Interpolation `{{ }}`

Output a TypeScript expression as text in the template.

```html
<h3>{{ employee.name }}</h3>
<p>{{ 2 + 2 }}</p>
<p>{{ employee.name.toUpperCase() }}</p>
<p>{{ isActive ? 'Active' : 'Inactive' }}</p>
```

**Direction: Component → Template (one-way)**

### Type 2 — Property Binding `[property]="expression"`

Bind a DOM property or component input to a TypeScript expression.

```html
<!-- DOM property binding -->
<img [src]="employee.avatarUrl" [alt]="employee.name" />
<input [value]="searchTerm" />
<button [disabled]="isSubmitting">Save</button>

<!-- Class and style binding -->
<div [class.active]="employee.isActive">
<div [style.color]="employee.isActive ? 'green' : 'red'">

<!-- Note: [attr.xxx] for HTML attributes (not DOM properties) -->
<td [attr.colspan]="colSpan">
```

**Direction: Component → Template (one-way)**

> Property binding vs interpolation: `[src]="imageUrl"` and `src="{{ imageUrl }}"` are equivalent for strings. Use property binding when the value is not a string (boolean, number, object).

### Type 3 — Event Binding `(event)="handler()"`

Listen for DOM events and call a method.

```html
<button (click)="handleDelete()">Delete</button>
<input (input)="onInput($event)" />
<input (keydown.enter)="onEnter()" />
<form (ngSubmit)="onSubmit()">
<div (mouseover)="onHover($event)">
```

**Direction: Template → Component (one-way)**

```ts
// In the component class
handleDelete() {
  console.log('Delete clicked')
}

onInput(event: Event) {
  const value = (event.target as HTMLInputElement).value
  this.searchTerm = value
}
```

### Type 4 — Two-Way Binding `[(ngModel)]="property"`

Combines property binding + event binding into one. The template and component stay in sync.

```html
<!-- requires FormsModule imported in the component -->
<input [(ngModel)]="searchTerm" placeholder="Search employees..." />
<p>You typed: {{ searchTerm }}</p>
```

```ts
import { FormsModule } from '@angular/forms'

@Component({
  imports: [FormsModule],
  // ...
})
export class SearchComponent {
  searchTerm = ''
}
```

**Direction: Both ways — changes in template update component, changes in component update template**

### All four types — summary table

| Syntax | Direction | Use for |
|--------|-----------|---------|
| `{{ value }}` | Component → Template | Display text |
| `[property]="expr"` | Component → Template | DOM properties, inputs |
| `(event)="handler()"` | Template → Component | User actions |
| `[(ngModel)]="prop"` | Both | Form inputs with sync |

---

## 1.3 Signals — Modern Reactive State in Angular

Before Angular 16, change detection relied on **Zone.js** — a library that patches browser APIs to detect when anything async happens, then triggers a full change detection pass.

**Angular 16+ introduces Signals** — a fine-grained reactivity system. Only components that read a changed signal re-render. No Zone.js needed.

### Creating and using signals

```ts
import { Component, signal, computed, effect } from '@angular/core'

@Component({ /* ... */ })
export class EmployeeListComponent {
  // signal() creates a reactive value
  employees  = signal<Employee[]>([])
  filter     = signal<string>('All')
  searchTerm = signal<string>('')

  // computed() derives a value from other signals — updates automatically
  filteredEmployees = computed(() =>
    this.employees()
      .filter(e => this.filter() === 'All' || e.department === this.filter())
      .filter(e => e.name.toLowerCase().includes(this.searchTerm().toLowerCase()))
  )

  activeCount = computed(() =>
    this.employees().filter(e => e.isActive).length
  )

  // effect() runs a side effect when signals change
  constructor() {
    effect(() => {
      // This runs whenever employees() changes
      console.log('Employees updated:', this.employees().length)
    })
  }
}
```

### Reading and writing signals

```ts
// Read a signal — call it like a function
const list = this.employees()    // returns Employee[]
const term = this.searchTerm()   // returns string

// Write — use .set() to replace the value
this.filter.set('Engineering')
this.searchTerm.set('')

// Update — use .update() to derive new value from old one
this.employees.update(list => [...list, newEmployee])
this.employees.update(list => list.filter(e => e.id !== id))
```

### Signals in templates

Angular templates automatically track signal reads — no `.subscribe()`, no `async` pipe needed.

```html
<!-- Signals are read by calling them with () in templates -->
<p>Total employees: {{ employees().length }}</p>
<p>Active: {{ activeCount() }}</p>

@for (emp of filteredEmployees(); track emp.id) {
  <app-employee-card [employee]="emp" />
}
```

### Zone.js context (historical)

Before signals, a component class used plain properties:

```ts
// OLD Zone.js approach
export class OldComponent {
  employees: Employee[] = []
  filter = 'All'

  setFilter(dept: string) {
    this.filter = dept   // Zone.js detects this and triggers change detection
  }
}
```

Zone.js worked but had a cost: any async event (setTimeout, HTTP call, user click) triggered change detection on the **entire component tree**, even components with no changed data. Signals fix this by tracking exactly which components read which signals.

> **In this course:** We use Signals throughout. When you encounter Zone.js-based code at work, the component logic is the same — only the reactivity mechanism differs.

---

## 1.4 Types & Interfaces

Always type your data — Angular + TypeScript together catch errors at compile time.

```ts
// src/app/shared/models/employee.model.ts
export interface Employee {
  id:         number
  name:       string
  email:      string
  phone?:     string
  department: string
  salary:     number
  isActive:   boolean
  joinDate:   string
  role?:      'admin' | 'manager' | 'user'
}

export interface Department {
  id:   number
  name: string
  headId?: number
}

export type DepartmentName = 'All' | 'Engineering' | 'Marketing' | 'HR' | 'Finance' | 'Sales'
```

---

## 1.5 EMS Project — First Real Component

### Generate

```bash
ng g c employees/employee-card
ng g c employees/employee-list
```

### `src/app/employees/employee-card/employee-card.component.ts`

```ts
import { Component, input } from '@angular/core'
import { CurrencyPipe, DatePipe } from '@angular/common'
import type { Employee } from '../../shared/models/employee.model'

@Component({
  selector: 'app-employee-card',
  standalone: true,
  imports: [CurrencyPipe, DatePipe],
  templateUrl: './employee-card.component.html',
  styleUrl: './employee-card.component.css',
})
export class EmployeeCardComponent {
  // input() signal — receives data from parent
  employee = input.required<Employee>()
}
```

### `src/app/employees/employee-card/employee-card.component.html`

```html
<div class="card">
  <div class="card-header">
    <h3>{{ employee().name }}</h3>
    <span [class]="employee().isActive ? 'badge badge--active' : 'badge badge--inactive'">
      {{ employee().isActive ? 'Active' : 'Inactive' }}
    </span>
  </div>

  <div class="card-body">
    <p><strong>Department:</strong> {{ employee().department }}</p>
    <p><strong>Email:</strong> {{ employee().email }}</p>
    <p><strong>Joined:</strong> {{ employee().joinDate | date:'mediumDate' }}</p>
    <p class="salary">{{ employee().salary | currency:'INR':'symbol':'1.0-0' }}</p>
  </div>
</div>
```

### `src/app/employees/employee-card/employee-card.component.css`

```css
.card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 16px;
  transition: box-shadow 0.2s;
}

.card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.badge {
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.badge--active   { background: #defbe6; color: #0e6027; }
.badge--inactive { background: #fff1f1; color: #a2191f; }

.salary {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-primary);
  margin-top: 8px;
}
```

### `src/app/app.component.ts` — use the card

```ts
import { Component, signal } from '@angular/core'
import { RouterOutlet }       from '@angular/router'
import { EmployeeCardComponent } from './employees/employee-card/employee-card.component'
import type { Employee } from './shared/models/employee.model'

@Component({
  selector:    'app-root',
  standalone:  true,
  imports:     [RouterOutlet, EmployeeCardComponent],
  templateUrl: './app.component.html',
  styleUrl:    './app.component.css',
})
export class AppComponent {
  employees = signal<Employee[]>([
    {
      id: 1, name: 'Alice Johnson', email: 'alice@ibm.com',
      department: 'Engineering', salary: 1200000, isActive: true,
      joinDate: '2021-03-15',
    },
    {
      id: 2, name: 'Bob Smith', email: 'bob@ibm.com',
      department: 'Marketing', salary: 950000, isActive: true,
      joinDate: '2020-07-01',
    },
  ])
}
```

```html
<!-- app.component.html -->
<h1>IBM Employee Management System</h1>
<div class="employee-grid">
  @for (emp of employees(); track emp.id) {
    <app-employee-card [employee]="emp" />
  }
</div>
```

---

## 1.6 Angular's New Control Flow Syntax (Angular 17+)

Angular 17 introduced built-in control flow in templates, replacing the old structural directives `*ngIf` and `*ngFor` for new projects.

```html
<!-- @if — replaces *ngIf -->
@if (employees().length > 0) {
  <p>{{ employees().length }} employees found</p>
} @else {
  <p>No employees found.</p>
}

<!-- @for — replaces *ngFor -->
<!-- track is mandatory — replaces trackBy -->
@for (emp of employees(); track emp.id) {
  <app-employee-card [employee]="emp" />
} @empty {
  <p>No employees yet.</p>
}

<!-- @switch — replaces *ngSwitch -->
@switch (employee().role) {
  @case ('admin')   { <span>Administrator</span> }
  @case ('manager') { <span>Manager</span> }
  @default          { <span>Employee</span> }
}
```

> **Old syntax** (`*ngIf`, `*ngFor`) still works — you'll see it in legacy code and is covered in Module 05. New projects should use `@if` / `@for` / `@switch`.

---

## Summary

| Concept | Syntax |
|---------|--------|
| Interpolation | `{{ value }}` |
| Property binding | `[property]="expr"` |
| Event binding | `(event)="method()"` |
| Two-way binding | `[(ngModel)]="prop"` |
| Signal create | `mySignal = signal(initialValue)` |
| Signal read | `this.mySignal()` or `mySignal()` in template |
| Signal write | `this.mySignal.set(newValue)` |
| Signal update | `this.mySignal.update(old => newValue)` |
| Computed signal | `computed(() => derivedValue)` |
| Template `@if` | `@if (cond) { } @else { }` |
| Template `@for` | `@for (item of list(); track item.id) { }` |

**Next → Module 02: Components & Databinding Deep Dive**
