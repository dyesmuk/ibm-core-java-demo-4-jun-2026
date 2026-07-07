# Module 02 — Components & Databinding Deep Dive

## Learning Objectives
- Pass data into components with `@Input()` / `input()`
- Emit events from child to parent with `@Output()` / `output()`
- Use view encapsulation effectively
- Understand component lifecycle hooks
- Use `ContentChild`, `ViewChild` for DOM/child access
- EMS: full component tree with proper data flow

---

## 2.1 Component Inputs — Receiving Data from Parent

### Modern API: `input()` signal (Angular 17+)

```ts
import { Component, input, computed } from '@angular/core'
import type { Employee } from '../../shared/models/employee.model'

@Component({ selector: 'app-employee-card', standalone: true, /* ... */ })
export class EmployeeCardComponent {
  // Required input — Angular throws if parent doesn't provide it
  employee = input.required<Employee>()

  // Optional input with default value
  compact  = input<boolean>(false)

  // Optional input, no default
  highlightId = input<number | null>(null)

  // Computed from input signal
  isHighlighted = computed(() => this.highlightId() === this.employee().id)
  salaryFormatted = computed(() =>
    new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' })
      .format(this.employee().salary)
  )
}
```

```html
<!-- Parent template -->
<app-employee-card
  [employee]="emp"
  [compact]="isCompactView()"
  [highlightId]="selectedId()"
/>
```

### Legacy API: `@Input()` decorator (still common in older codebases)

```ts
import { Component, Input } from '@angular/core'

@Component({ /* ... */ })
export class EmployeeCardComponent {
  @Input({ required: true }) employee!: Employee
  @Input() compact = false

  // Input with transform (Angular 16+)
  @Input({ transform: (v: string) => Number(v) }) salary = 0
}
```

> **Prefer `input()` signals in new code.** `@Input()` is still fully supported and you'll see it everywhere in existing Angular projects.

---

## 2.2 Component Outputs — Emitting Events to Parent

### Modern API: `output()` (Angular 17+)

```ts
import { Component, input, output } from '@angular/core'
import type { Employee } from '../../shared/models/employee.model'

@Component({ selector: 'app-employee-card', standalone: true, /* ... */ })
export class EmployeeCardComponent {
  employee  = input.required<Employee>()

  // Declare outputs — what this component can emit
  select    = output<number>()      // emits employee id
  remove    = output<number>()      // emits employee id
  toggleActive = output<number>()

  onSelect() {
    this.select.emit(this.employee().id)
  }

  onRemove() {
    this.remove.emit(this.employee().id)
  }

  onToggle() {
    this.toggleActive.emit(this.employee().id)
  }
}
```

```html
<!-- employee-card.component.html -->
<div class="card" (click)="onSelect()">
  <h3>{{ employee().name }}</h3>
  <button (click)="onToggle(); $event.stopPropagation()">
    {{ employee().isActive ? 'Deactivate' : 'Activate' }}
  </button>
  <button (click)="onRemove(); $event.stopPropagation()">Remove</button>
</div>
```

```html
<!-- Parent template -->
<app-employee-card
  [employee]="emp"
  (select)="handleSelect($event)"
  (remove)="handleRemove($event)"
  (toggleActive)="handleToggle($event)"
/>
```

### Legacy API: `@Output()` + `EventEmitter`

```ts
import { Component, Input, Output, EventEmitter } from '@angular/core'

@Component({ /* ... */ })
export class EmployeeCardComponent {
  @Input({ required: true }) employee!: Employee
  @Output() select       = new EventEmitter<number>()
  @Output() remove       = new EventEmitter<number>()
  @Output() toggleActive = new EventEmitter<number>()
}
```

---

## 2.3 Parent ↔ Child Data Flow — Full EMS Example

```
AppComponent  (owns employees signal, handles all mutations)
    │
    ├── [employee]="emp"   ──▶  EmployeeCardComponent
    │        (select)  ◀──  id
    │        (remove)  ◀──  id
    │        (toggleActive) ◀── id
    │
    └── EmployeeListComponent
         [employees]="filteredEmployees()"
         (employeeAction)="handleAction($event)"
```

### `employee-list.component.ts`

```ts
import { Component, input, output } from '@angular/core'
import { EmployeeCardComponent } from '../employee-card/employee-card.component'
import type { Employee } from '../../shared/models/employee.model'

export interface EmployeeAction {
  type:       'select' | 'remove' | 'toggle'
  employeeId: number
}

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [EmployeeCardComponent],
  templateUrl: './employee-list.component.html',
})
export class EmployeeListComponent {
  employees      = input.required<Employee[]>()
  selectedId     = input<number | null>(null)
  employeeAction = output<EmployeeAction>()

  onAction(type: EmployeeAction['type'], employeeId: number) {
    this.employeeAction.emit({ type, employeeId })
  }
}
```

```html
<!-- employee-list.component.html -->
<div class="employee-grid">
  @for (emp of employees(); track emp.id) {
    <app-employee-card
      [employee]="emp"
      [highlightId]="selectedId()"
      (select)="onAction('select', $event)"
      (remove)="onAction('remove', $event)"
      (toggleActive)="onAction('toggle', $event)"
    />
  } @empty {
    <div class="empty-state">
      <p>No employees found.</p>
    </div>
  }
</div>
```

---

## 2.4 View Encapsulation

By default, Angular **scopes component styles** so they only apply inside that component — class names don't leak.

```ts
import { Component, ViewEncapsulation } from '@angular/core'

@Component({
  // ViewEncapsulation.Emulated (default) — Angular adds attribute selectors
  //   .card[_ngcontent-abc123] { } — styles don't leak
  encapsulation: ViewEncapsulation.Emulated,   // default

  // ViewEncapsulation.None — styles become global
  encapsulation: ViewEncapsulation.None,

  // ViewEncapsulation.ShadowDom — real Shadow DOM
  encapsulation: ViewEncapsulation.ShadowDom,
})
export class EmployeeCardComponent {}
```

**Rule:** Keep the default `Emulated`. Only use `None` for global utility styles you deliberately want to share, and only in a component whose sole purpose is providing those styles.

---

## 2.5 Component Lifecycle Hooks

Angular calls these methods at specific points in a component's life:

```
constructor()          → class created (DI runs)
ngOnInit()             → inputs available, component initialised
ngOnChanges()          → inputs changed (runs before ngOnInit too)
ngDoCheck()            → every change detection cycle
ngAfterContentInit()   → content projection (<ng-content>) done
ngAfterContentChecked()→ projected content checked
ngAfterViewInit()      → component view + children initialised
ngAfterViewChecked()   → view + children checked
ngOnDestroy()          → component about to be removed from DOM
```

### The ones you'll use daily

```ts
import {
  Component, OnInit, OnDestroy, OnChanges,
  SimpleChanges, input, effect
} from '@angular/core'

@Component({ /* ... */ })
export class EmployeeDetailComponent implements OnInit, OnDestroy, OnChanges {
  employeeId = input.required<number>()

  ngOnChanges(changes: SimpleChanges) {
    // Fires when input values change
    // SimpleChanges maps input name → { previousValue, currentValue, firstChange }
    if (changes['employeeId'] && !changes['employeeId'].firstChange) {
      this.loadEmployee(this.employeeId())
    }
  }

  ngOnInit() {
    // Runs once after first ngOnChanges
    // Safe to access inputs here
    this.loadEmployee(this.employeeId())
  }

  ngOnDestroy() {
    // Clean up subscriptions, intervals, event listeners
    this.subscription?.unsubscribe()
  }

  private loadEmployee(id: number) {
    // fetch from service
  }
}
```

> **With signals and `effect()`:** you often don't need `ngOnChanges` — an `effect()` that reads an input signal will react automatically when the input changes.

```ts
export class EmployeeDetailComponent {
  employeeId = input.required<number>()

  constructor(private employeeService: EmployeeService) {
    effect(() => {
      // Runs whenever employeeId() changes (including first render)
      this.employeeService.loadEmployee(this.employeeId())
    })
  }
}
```

---

## 2.6 ViewChild and ElementRef

Access a child component or DOM element directly.

```ts
import { Component, ViewChild, ElementRef, AfterViewInit } from '@angular/core'
import { SearchBarComponent } from './search-bar/search-bar.component'

@Component({ /* ... */ })
export class EmployeeListComponent implements AfterViewInit {
  @ViewChild('searchInput') searchInput!: ElementRef<HTMLInputElement>
  @ViewChild(SearchBarComponent) searchBar!: SearchBarComponent

  ngAfterViewInit() {
    // Available after the view is initialised
    this.searchInput.nativeElement.focus()
  }

  focusSearch() {
    this.searchBar.focus()   // call child component method
  }
}
```

```html
<!-- Use #templateRef to mark an element for ViewChild -->
<input #searchInput type="text" placeholder="Search..." />
<app-search-bar />
```

---

## 2.7 Content Projection with `ng-content`

Pass template content from parent into a child's template.

```html
<!-- card.component.html -->
<div class="card">
  <div class="card-header">
    <ng-content select="[card-title]" />  <!-- named slot -->
  </div>
  <div class="card-body">
    <ng-content />                         <!-- default slot -->
  </div>
  <div class="card-footer">
    <ng-content select="[card-actions]" />
  </div>
</div>
```

```html
<!-- Parent usage -->
<app-card>
  <h3 card-title>Alice Johnson</h3>
  <p>Engineering Department</p>
  <button card-actions (click)="edit()">Edit</button>
</app-card>
```

---

## 2.8 EMS — Complete Component Architecture

```
AppComponent
├── NavbarComponent
├── EmployeeFiltersComponent  (filter, search — emits filter changes up)
├── EmployeeListComponent     (receives employees[], emits actions up)
│   └── EmployeeCardComponent (receives employee, emits select/remove/toggle)
└── EmployeeDetailComponent   (receives selectedId, shows details)
```

```ts
// app.component.ts — owns all state
@Component({ /* ... */ })
export class AppComponent {
  private employeeService = inject(EmployeeService)

  employees   = this.employeeService.employees      // signal<Employee[]>
  selectedId  = signal<number | null>(null)
  filter      = signal<DepartmentName>('All')
  searchTerm  = signal<string>('')

  filteredEmployees = computed(() =>
    this.employees()
      .filter(e => this.filter() === 'All' || e.department === this.filter())
      .filter(e => e.name.toLowerCase().includes(this.searchTerm().toLowerCase()))
  )

  handleEmployeeAction(action: EmployeeAction) {
    switch (action.type) {
      case 'select': this.selectedId.set(action.employeeId); break
      case 'remove': this.employeeService.remove(action.employeeId); break
      case 'toggle': this.employeeService.toggleActive(action.employeeId); break
    }
  }
}
```

---

## Summary

| Concept | Modern API | Legacy API |
|---------|-----------|-----------|
| Input | `input()` / `input.required()` | `@Input()` |
| Output | `output<T>()` | `@Output() + EventEmitter<T>` |
| Read input | `this.myInput()` | `this.myInput` |
| Emit event | `this.myOutput.emit(value)` | `this.myOutput.emit(value)` |
| Lifecycle | `ngOnInit`, `ngOnDestroy`, `ngOnChanges` | Same |
| DOM access | `@ViewChild` + `ElementRef` | Same |
| Content projection | `<ng-content select="...">` | Same |

**Next → Module 03: Debugging Angular Apps**
