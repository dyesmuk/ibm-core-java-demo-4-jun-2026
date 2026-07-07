# Module 12 — Dynamic Components, Angular Modules & Optimising Angular Apps

## Learning Objectives
- Create components dynamically at runtime
- Understand Angular Modules (NgModule) — legacy context
- Implement lazy loading for performance
- Use `OnPush` change detection strategy
- Optimise bundles with code splitting
- EMS: dynamic modal, confirmation dialog, lazy feature modules

---

## 12.1 Dynamic Components

Dynamic components are created and inserted into the DOM at runtime — not via a static template tag.

Common use cases: modals, toasts, confirmation dialogs, tooltips.

### Dynamic Component with `ViewContainerRef`

```ts
// src/app/shared/services/modal.service.ts
import {
  Injectable, inject, signal,
  ApplicationRef, createComponent, EnvironmentInjector,
} from '@angular/core'
import { ConfirmDialogComponent } from '../components/confirm-dialog/confirm-dialog.component'

export interface ConfirmOptions {
  title:       string
  message:     string
  confirmText?: string
  cancelText?:  string
}

@Injectable({ providedIn: 'root' })
export class ModalService {
  private appRef        = inject(ApplicationRef)
  private injector      = inject(EnvironmentInjector)

  confirm(options: ConfirmOptions): Promise<boolean> {
    return new Promise(resolve => {
      // 1. Create the component dynamically
      const componentRef = createComponent(ConfirmDialogComponent, {
        environmentInjector: this.injector,
      })

      // 2. Set inputs
      componentRef.setInput('title',       options.title)
      componentRef.setInput('message',     options.message)
      componentRef.setInput('confirmText', options.confirmText ?? 'Confirm')
      componentRef.setInput('cancelText',  options.cancelText  ?? 'Cancel')

      // 3. Listen for output events
      componentRef.instance.confirmed.subscribe(() => {
        resolve(true)
        this.destroy(componentRef, host)
      })
      componentRef.instance.cancelled.subscribe(() => {
        resolve(false)
        this.destroy(componentRef, host)
      })

      // 4. Attach to Angular's change detection
      this.appRef.attachView(componentRef.hostView)

      // 5. Add to the DOM
      const host = document.createElement('div')
      document.body.appendChild(host)
      host.appendChild(componentRef.location.nativeElement)
    })
  }

  private destroy(componentRef: any, host: HTMLElement) {
    this.appRef.detachView(componentRef.hostView)
    componentRef.destroy()
    document.body.removeChild(host)
  }
}
```

```ts
// src/app/shared/components/confirm-dialog/confirm-dialog.component.ts
import { Component, input, output } from '@angular/core'

@Component({
  selector:    'app-confirm-dialog',
  standalone:  true,
  templateUrl: './confirm-dialog.component.html',
  styleUrl:    './confirm-dialog.component.css',
})
export class ConfirmDialogComponent {
  title       = input<string>('Confirm')
  message     = input<string>('Are you sure?')
  confirmText = input<string>('Confirm')
  cancelText  = input<string>('Cancel')

  confirmed = output<void>()
  cancelled = output<void>()
}
```

```html
<!-- confirm-dialog.component.html -->
<div class="modal-backdrop" (click)="cancelled.emit()">
  <div class="modal-dialog" (click)="$event.stopPropagation()">
    <h2>{{ title() }}</h2>
    <p>{{ message() }}</p>
    <div class="modal-actions">
      <button (click)="cancelled.emit()">{{ cancelText() }}</button>
      <button class="btn-danger" (click)="confirmed.emit()">{{ confirmText() }}</button>
    </div>
  </div>
</div>
```

```css
/* confirm-dialog.component.css */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-dialog {
  background: white;
  border-radius: 8px;
  padding: 32px;
  min-width: 320px;
  max-width: 480px;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.btn-danger {
  background: var(--color-danger);
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}
```

```ts
// Using the modal in a component
@Component({ /* ... */ })
export class EmployeeListPageComponent {
  private modalService    = inject(ModalService)
  private employeeService = inject(EmployeeService)
  private notifications   = inject(NotificationService)

  async onDelete(employee: Employee) {
    const confirmed = await this.modalService.confirm({
      title:       'Delete Employee',
      message:     `Are you sure you want to remove ${employee.name}? This cannot be undone.`,
      confirmText: 'Delete',
      cancelText:  'Keep',
    })

    if (confirmed) {
      this.employeeService.remove(employee.id)
      this.notifications.success(`${employee.name} has been removed.`)
    }
  }
}
```

---

## 12.2 NgContent + Dynamic Loading via `@defer`

Angular 17+ introduces `@defer` — a declarative way to lazy-load parts of the template.

```html
<!-- Load charts only when they come into view -->
@defer (on viewport) {
  <app-salary-chart [employees]="employees()" />
} @placeholder {
  <div class="chart-placeholder">Chart loading...</div>
} @loading (minimum 300ms) {
  <div class="spinner">Loading chart...</div>
} @error {
  <p>Could not load chart.</p>
}

<!-- Defer on user interaction -->
@defer (on interaction) {
  <app-employee-audit-log [employeeId]="id()" />
} @placeholder {
  <button>Load Audit Log</button>
}

<!-- Defer on idle browser state -->
@defer (on idle) {
  <app-recommendations />
}

<!-- Defer with a condition -->
@defer (when showDetails()) {
  <app-employee-detail [employee]="employee()" />
}
```

---

## 12.3 Angular Modules (NgModule) — Historical Context

Before Angular 14, every application was built with `NgModule`. Understanding NgModules is essential when working on existing codebases.

```ts
// OLD — what you'll see in legacy Angular apps
import { NgModule }         from '@angular/core'
import { BrowserModule }    from '@angular/platform-browser'
import { HttpClientModule } from '@angular/common/http'
import { AppRoutingModule } from './app-routing.module'
import { AppComponent }     from './app.component'
import { EmployeeCardComponent } from './employees/employee-card/employee-card.component'

@NgModule({
  declarations: [
    AppComponent,             // all components/pipes/directives in this module
    EmployeeCardComponent,
  ],
  imports: [
    BrowserModule,            // always needed in root module
    HttpClientModule,         // HTTP support
    AppRoutingModule,         // routing
  ],
  providers: [/* services */],
  bootstrap: [AppComponent],  // the root component
})
export class AppModule {}
```

```ts
// main.ts (NgModule-based)
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic'
import { AppModule } from './app/app.module'
platformBrowserDynamic().bootstrapModule(AppModule)
```

### NgModule vs Standalone comparison

| | NgModule (legacy) | Standalone (modern) |
|--|------------------|---------------------|
| Component declares itself | ❌ declared in module | ✅ `standalone: true` |
| Import dependencies | In module's `imports` | In component's `imports` |
| Providers | In module's `providers` | `provideXxx()` in `app.config.ts` |
| Lazy loading | `loadChildren: () => import(...Module)` | `loadComponent` / `loadChildren` with routes |

---

## 12.4 OnPush Change Detection

By default Angular checks all components on every event. `OnPush` tells Angular: only re-render this component when:
- Its `@Input()` / `input()` references change
- An Observable / Signal it reads emits a new value
- A DOM event inside it fires
- `ChangeDetectorRef.markForCheck()` is called manually

```ts
import { Component, ChangeDetectionStrategy, input } from '@angular/core'
import type { Employee } from '../../shared/models/employee.model'

@Component({
  selector:          'app-employee-card',
  standalone:        true,
  changeDetection:   ChangeDetectionStrategy.OnPush,   // add this
  templateUrl:       './employee-card.component.html',
})
export class EmployeeCardComponent {
  employee = input.required<Employee>()
  // With signals + OnPush: Angular is maximally efficient
  // Re-renders only when employee() signal reference changes
}
```

> **With Signals:** Angular 19+ can run without Zone.js entirely (`zoneless` mode). Signals + `OnPush` is today's stepping stone — adopt it for all leaf components.

---

## 12.5 Lazy Loading Feature Routes

Already seen in Module 06, but let's see the complete pattern:

```ts
// src/app/app.routes.ts
export const routes: Routes = [
  {
    path: 'employees',
    loadChildren: () =>
      import('./employees/employees.routes').then(m => m.employeeRoutes),
  },
  {
    path: 'departments',
    loadChildren: () =>
      import('./departments/departments.routes').then(m => m.departmentRoutes),
  },
  {
    path: 'projects',
    loadChildren: () =>
      import('./projects/projects.routes').then(m => m.projectRoutes),
  },
]
```

**Effect on bundle:** The app bundle is split. When a user visits `/employees`, only the employee code downloads. Department and project code never downloads unless visited.

```bash
ng build --configuration=production
# dist/ibm-ems-angular/
#   main-HASH.js           ← core app (small)
#   chunk-employees-HASH.js ← lazy loaded
#   chunk-departments-HASH.js
#   chunk-projects-HASH.js
```

---

## 12.6 Preloading Strategies

By default, lazy chunks load on demand. Preloading strategies download them in the background after the initial page loads.

```ts
import { provideRouter, withPreloading, PreloadAllModules } from '@angular/router'

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(
      routes,
      withPreloading(PreloadAllModules),   // download all lazy chunks in background
      withComponentInputBinding(),
    ),
  ],
}
```

Custom strategy — only preload routes with `{ preload: true }` in route data:

```ts
import { PreloadingStrategy, Route } from '@angular/router'
import { Observable, of } from 'rxjs'

export class SelectivePreloadingStrategy implements PreloadingStrategy {
  preload(route: Route, load: () => Observable<any>): Observable<any> {
    return route.data?.['preload'] ? load() : of(null)
  }
}
```

---

## 12.7 Bundle Analysis

```bash
# Install the analyzer
npm install -D webpack-bundle-analyzer

# Build with stats
ng build --stats-json

# Analyse
npx webpack-bundle-analyzer dist/ibm-ems-angular/stats.json
```

**Common optimisations after analysis:**
- Split large vendor libraries (lodash, moment) using barrel imports
- Remove unused locale data from Angular's `@angular/common/locales`
- Use `@defer` for heavy components below the fold
- Tree-shake unused RxJS operators (use `rxjs/operators` imports, not `rxjs`)

---

## Summary

| Technique | Impact |
|-----------|--------|
| `createComponent()` | Create components at runtime (modals, dialogs) |
| `@defer` | Lazy-load template blocks on viewport/idle/interaction |
| Lazy routing | Split app into chunks — smaller initial bundle |
| `OnPush` change detection | Skip unnecessary re-renders |
| `PreloadAllModules` | Download lazy chunks in background after initial load |
| `withComponentInputBinding()` | Route params → component inputs |
| Bundle analyser | Find what's making the bundle large |

**Next → Module 13: Deploying an Angular App**
