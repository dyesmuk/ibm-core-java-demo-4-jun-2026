# Module 04 — Directives Deep Dive

## Learning Objectives
- Distinguish attribute, structural, and component directives
- Use all built-in Angular directives
- Build custom attribute and structural directives
- Use the new `@if`, `@for`, `@switch` control flow
- EMS: highlight directive, truncate directive, permission directive

---

## 4.1 What Are Directives?

Directives extend HTML elements with new behaviour or appearance. Angular has three kinds:

| Kind | What it does | Example |
|------|-------------|---------|
| **Component** | Directive with a template — what we've been building | `<app-employee-card>` |
| **Attribute** | Changes appearance/behaviour of an element | `ngClass`, `ngStyle`, custom |
| **Structural** | Adds/removes elements from the DOM | `@if`, `@for`, `*ngIf`, `*ngFor` |

---

## 4.2 Built-in Attribute Directives

### `NgClass`

```html
<!-- Object syntax — most flexible -->
<div [ngClass]="{
  'card--active':   employee.isActive,
  'card--selected': employee.id === selectedId(),
  'card--compact':  compact()
}">

<!-- Array syntax -->
<div [ngClass]="['card', employee.isActive ? 'card--active' : 'card--inactive']">

<!-- String syntax -->
<div [ngClass]="cardClass()">
```

```ts
// In component
cardClass = computed(() => {
  const base = 'card'
  const active = this.employee().isActive ? 'card--active' : 'card--inactive'
  const selected = this.isSelected() ? 'card--selected' : ''
  return [base, active, selected].filter(Boolean).join(' ')
})
```

> For most cases, prefer the built-in `[class.xxx]` binding — cleaner and more performant:

```html
<div
  class="card"
  [class.card--active]="employee().isActive"
  [class.card--selected]="isSelected()"
>
```

### `NgStyle`

```html
<!-- Object of CSS properties -->
<div [ngStyle]="{
  'font-size':   compact() ? '12px' : '14px',
  'font-weight': employee().isActive ? '600' : '400'
}">

<!-- Prefer individual [style.property] bindings when possible -->
<div
  [style.font-size]="compact() ? '12px' : '14px'"
  [style.opacity]="employee().isActive ? 1 : 0.6"
>
```

---

## 4.3 Built-in Structural Directives (old syntax)

The `*` prefix is syntactic sugar — `*ngIf` expands to `[ngIf]` on an `<ng-template>`. You'll encounter these constantly in legacy code.

```html
<!-- Import: NgIf from @angular/common -->
<div *ngIf="employees().length > 0; else emptyState">
  <p>{{ employees().length }} employees found</p>
</div>

<ng-template #emptyState>
  <p>No employees found.</p>
</ng-template>

<!-- *ngIf with then/else -->
<div *ngIf="isLoading(); then loadingTpl; else contentTpl"></div>
<ng-template #loadingTpl><p>Loading...</p></ng-template>
<ng-template #contentTpl><app-employee-list /></ng-template>
```

```html
<!-- *ngFor — Import: NgFor from @angular/common -->
<app-employee-card
  *ngFor="let emp of filteredEmployees(); trackBy: trackById"
  [employee]="emp"
/>

<!-- *ngFor with index, first, last, even, odd -->
<li
  *ngFor="let emp of employees(); let i = index; let isLast = last"
  [class.last]="isLast"
>
  {{ i + 1 }}. {{ emp.name }}
</li>
```

```ts
// trackBy function — improves performance, like track in @for
trackById(index: number, employee: Employee): number {
  return employee.id
}
```

```html
<!-- *ngSwitch -->
<div [ngSwitch]="employee().role">
  <span *ngSwitchCase="'admin'">Administrator</span>
  <span *ngSwitchCase="'manager'">Manager</span>
  <span *ngSwitchDefault>Employee</span>
</div>
```

---

## 4.4 New Control Flow Syntax (Angular 17+ — Preferred)

```html
<!-- @if / @else if / @else -->
@if (isLoading()) {
  <p>Loading employees...</p>
} @else if (error()) {
  <p class="error">{{ error() }}</p>
} @else if (filteredEmployees().length === 0) {
  <div class="empty-state">
    <p>No employees match your search.</p>
  </div>
} @else {
  <div class="employee-grid">
    @for (emp of filteredEmployees(); track emp.id) {
      <app-employee-card [employee]="emp" />
    }
  </div>
}

<!-- @for with @empty -->
@for (emp of employees(); track emp.id) {
  <app-employee-card [employee]="emp" />
} @empty {
  <p>No employees yet. Add one above.</p>
}

<!-- @for with index and other loop variables -->
@for (emp of employees(); track emp.id; let i = $index, isFirst = $first, isLast = $last) {
  <div [class.first]="isFirst" [class.last]="isLast">
    {{ i + 1 }}. {{ emp.name }}
  </div>
}

<!-- @switch -->
@switch (selectedTab()) {
  @case ('employees')   { <app-employee-list /> }
  @case ('departments') { <app-department-list /> }
  @case ('projects')    { <app-project-list /> }
  @default              { <p>Select a tab</p> }
}
```

---

## 4.5 Building Custom Attribute Directives

Custom attribute directives let you attach reusable behaviour to any element.

### Example 1: Highlight Directive

Highlights an element when a condition is true.

```bash
ng g directive shared/directives/highlight
```

```ts
// src/app/shared/directives/highlight.directive.ts
import {
  Directive, ElementRef, Renderer2,
  input, effect, inject
} from '@angular/core'

@Directive({
  selector: '[appHighlight]',   // usage: <div appHighlight>
  standalone: true,
})
export class HighlightDirective {
  appHighlight = input<boolean>(false)   // the condition
  color        = input<string>('#fff3cd')  // highlight color

  private el       = inject(ElementRef)
  private renderer = inject(Renderer2)   // safe DOM manipulation

  constructor() {
    effect(() => {
      if (this.appHighlight()) {
        this.renderer.setStyle(this.el.nativeElement, 'background-color', this.color())
        this.renderer.setStyle(this.el.nativeElement, 'border-left', '4px solid #f0ad4e')
      } else {
        this.renderer.removeStyle(this.el.nativeElement, 'background-color')
        this.renderer.removeStyle(this.el.nativeElement, 'border-left')
      }
    })
  }
}
```

```html
<!-- Usage -->
<app-employee-card
  [appHighlight]="emp.id === selectedId()"
  [color]="'#e8f4fd'"
/>
```

### Example 2: Permission Directive

Show elements only to users with the right role.

```ts
// src/app/shared/directives/has-permission.directive.ts
import { Directive, TemplateRef, ViewContainerRef, input, effect, inject } from '@angular/core'
import { AuthService } from '../../auth/auth.service'

@Directive({
  selector: '[appHasPermission]',
  standalone: true,
})
export class HasPermissionDirective {
  appHasPermission = input<string[]>([])   // e.g. ['admin', 'manager']

  private templateRef     = inject(TemplateRef<unknown>)
  private viewContainer   = inject(ViewContainerRef)
  private authService     = inject(AuthService)

  constructor() {
    effect(() => {
      const userRole   = this.authService.currentUser()?.role ?? 'user'
      const hasAccess  = this.appHasPermission().includes(userRole)

      this.viewContainer.clear()
      if (hasAccess) {
        this.viewContainer.createEmbeddedView(this.templateRef)
      }
    })
  }
}
```

```html
<!-- Usage — shows only to admin and manager -->
<button *appHasPermission="['admin', 'manager']" (click)="deleteEmployee()">
  Delete Employee
</button>

<!-- Or with new control flow for simple role checks: -->
@if (authService.currentUser()?.role === 'admin') {
  <button (click)="deleteEmployee()">Delete</button>
}
```

### Example 3: Click Outside Directive

Emit an event when the user clicks outside an element (useful for dropdowns, modals).

```ts
// src/app/shared/directives/click-outside.directive.ts
import { Directive, ElementRef, output, inject, OnInit, OnDestroy } from '@angular/core'

@Directive({
  selector: '[appClickOutside]',
  standalone: true,
})
export class ClickOutsideDirective implements OnInit, OnDestroy {
  clickOutside = output<void>()

  private el = inject(ElementRef)
  private handler = (event: MouseEvent) => {
    if (!this.el.nativeElement.contains(event.target)) {
      this.clickOutside.emit()
    }
  }

  ngOnInit() {
    document.addEventListener('click', this.handler, true)
  }

  ngOnDestroy() {
    document.removeEventListener('click', this.handler, true)
  }
}
```

```html
<div class="dropdown" [appClickOutside] (clickOutside)="closeDropdown()">
  <!-- dropdown content -->
</div>
```

---

## 4.6 Building Custom Structural Directives

Custom structural directives control when elements are added to or removed from the DOM.

### Example: Repeat N Times

```ts
// src/app/shared/directives/repeat.directive.ts
import {
  Directive, TemplateRef, ViewContainerRef,
  input, effect, inject
} from '@angular/core'

@Directive({
  selector: '[appRepeat]',
  standalone: true,
})
export class RepeatDirective {
  appRepeat = input<number>(0)

  private templateRef   = inject(TemplateRef<{ $implicit: number }>)
  private viewContainer = inject(ViewContainerRef)

  constructor() {
    effect(() => {
      this.viewContainer.clear()
      for (let i = 0; i < this.appRepeat(); i++) {
        this.viewContainer.createEmbeddedView(this.templateRef, { $implicit: i })
      }
    })
  }
}
```

```html
<!-- Usage -->
<div *appRepeat="3; let i">Row {{ i + 1 }}</div>
```

---

## 4.7 `NgClass` vs `[class.x]` vs CSS — When to Use What

| Approach | Use when |
|----------|----------|
| `[class.active]="condition"` | One class toggled by one condition |
| `[ngClass]="{ ... }"` | Multiple classes from multiple conditions |
| `[class]="computedString()"` | Class string computed in TypeScript |
| CSS `:host` selector | Style the host element from inside the component |
| CSS custom properties | Theme/design tokens — prefer this |

```css
/* :host — styles the component's own host element */
:host {
  display: block;
}

:host(.card--selected) {
  border: 2px solid var(--color-primary);
}
```

---

## Summary

| Directive type | Example | Use for |
|---------------|---------|---------|
| Attribute | `[class.active]`, `[ngClass]`, custom | Modify existing elements |
| Structural (new) | `@if`, `@for`, `@switch` | Add/remove elements (preferred in Angular 17+) |
| Structural (old) | `*ngIf`, `*ngFor`, `*ngSwitch` | Same — legacy syntax |
| Custom attribute | `appHighlight`, `appHasPermission` | Reusable DOM behaviour |
| Custom structural | `*appRepeat` | Reusable template instantiation |

**Next → Module 05: Services & Dependency Injection**
