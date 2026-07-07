# IBM EMS — Angular 21 Courseware

> **Stack:** Angular 21 · TypeScript · Angular Router · Angular HttpClient · Reactive Forms · Signals · RxJS · Jasmine/Karma  
> **Project:** Employee Management System — built incrementally across every module  
> **Audience:** Java / React developers entering Angular; basic TypeScript assumed  
> **Approach:** Signals-first, standalone components throughout, NgModule as legacy context only

---

## Module Index

| # | File | Topics Covered |
|---|------|---------------|
| 00 | `00-getting-started.md` | What Angular is, CLI setup, project anatomy, bootstrap sequence, `ng` commands |
| 01 | `01-the-basics.md` | 4 data binding types, Signals (`signal`, `computed`, `effect`), `@if`/`@for`/`@switch`, first component |
| 02 | `02-components-databinding.md` | `input()`/`output()`, lifecycle hooks, `@ViewChild`, `ng-content`, full component tree |
| 03 | `03-debugging.md` | Angular DevTools, source maps, 7 common errors + fixes, strict mode, signal debugging |
| 04 | `04-directives.md` | Attribute directives (`ngClass`, `ngStyle`), structural (`@if`, `@for`, `*ngIf`, `*ngFor`), custom directives |
| 05 | `05-services-di.md` | `@Injectable`, `inject()`, scope levels, service-to-service injection, `NotificationService` |
| 06 | `06-routing.md` | `provideRouter`, `withComponentInputBinding`, lazy loading, guards, query params, named outlets |
| 07 | `07-observables.md` | Observable vs Promise, RxJS operators, `Subject`/`BehaviorSubject`, `toSignal`/`toObservable`, `async` pipe |
| 08 | `08-forms.md` | Reactive Forms (FormBuilder, FormGroup, FormArray), custom validators, template-driven basics |
| 09 | `09-pipes.md` | All built-in pipes, 5 custom pipes (salary range, dept color, filter, time-ago, truncate) |
| 10 | `10-http.md` | `provideHttpClient`, typed service, `HttpParams`, interceptors (auth, error, loading) |
| 11 | `11-authentication.md` | JWT flow, `AuthService` with Signals, mock auth, login form, functional guards, role-based UI |
| 12 | `12-dynamic-modules-optimisation.md` | Dynamic components, `@defer`, NgModule (legacy), `OnPush`, lazy loading, bundle analysis |
| 13 | `13-deployment-testing-cli-roundup.md` | Production build, Vercel/Netlify/Docker/CI-CD, unit testing with TestBed, CLI deep-dive, course roundup |
| 14 | `14-mcq-assessment.md` | 80 MCQs with full answer key and topic coverage map |

---

## Quick Start

```bash
# Install Angular CLI 21
npm install -g @angular/cli@21

# Create project
ng new ibm-ems-angular --routing --style=css --standalone
cd ibm-ems-angular
ng serve
# Open http://localhost:4200
```

---

## Suggested Delivery Schedule

| Day | Modules | Focus |
|-----|---------|-------|
| Day 1 AM | 00, 01 | Setup, CLI, data binding, Signals fundamentals |
| Day 1 PM | 02, 03 | Component tree (inputs/outputs), debugging |
| Day 2 AM | 04, 05 | Directives, Services & Dependency Injection |
| Day 2 PM | 06 | Routing — lazy loading, guards, params |
| Day 3 AM | 07 | Observables, RxJS, Signal ↔ Observable interop |
| Day 3 PM | 08 | Reactive Forms + template-driven basics |
| Day 4 AM | 09, 10 | Pipes + HTTP with interceptors |
| Day 4 PM | 11 | Authentication, route protection |
| Day 5 AM | 12 | Dynamic components, `@defer`, OnPush, NgModule context |
| Day 5 PM | 13 | Testing, deployment, CLI deep-dive, roundup |
| Assessment | 14 | 80-question MCQ |

---

## IBM Course Topics Mapping

| IBM Topic | Module |
|-----------|--------|
| Getting Started | 00 |
| The Basics | 01 |
| Course Project — The Basics | 01–02 |
| Debugging | 03 |
| Components & Databinding Deep Dive | 02 |
| Course Project — Components & Databinding | 02 |
| Directives Deep Dive | 04 |
| Course Project — Directives | 04 |
| Services & Dependency Injection | 05 |
| Course Project — Services & DI | 05 |
| Changing Pages with Routing | 06 |
| Course Project — Routing | 06 |
| Understanding Observables | 07 |
| Course Project — Observables | 07 |
| Handling Forms | 08 |
| Course Project — Forms | 08 |
| Pipes | 09 |
| Making HTTP Requests | 10 |
| Course Project — HTTP | 10 |
| Authentication & Route Protection | 11 |
| Dynamic Components | 12 |
| Angular Modules & Optimising | 12 |
| Deploying | 13 |
| Unit Testing | 13 |
| Angular as a Platform & CLI | 13 |
| Course Roundup | 13 |

---

## What Gets Built Module by Module

```
Module 01 → EmployeeCardComponent (hardcoded data, Signals intro)
Module 02 → input()/output() wiring, full parent-child tree
Module 03 → Debugging tools, ErrorBoundary equivalent, strict mode
Module 04 → Directives on cards, filter buttons, permission directive
Module 05 → EmployeeService (Signals), DepartmentService, NotificationService
Module 06 → Multi-page: /employees, /employees/:id, /employees/create, 404
Module 07 → Reactive search (debounce + switchMap), toSignal/toObservable
Module 08 → Create/Edit employee reactive form, custom validators, FormArray
Module 09 → Salary pipe, DeptColor pipe, TimeAgo pipe, filter pipe
Module 10 → HttpClient replaces seed data, interceptors for auth + errors
Module 11 → Login page, AuthService, authGuard, roleGuard, role-based UI
Module 12 → Dynamic confirm dialog, @defer for heavy components, OnPush
Module 13 → Full test suite, production build, Docker, GitHub Actions CI/CD
```

---

## Key Angular 21 Patterns — Quick Reference

```ts
// Signal basics
count     = signal(0)
doubled   = computed(() => this.count() * 2)
// In template: {{ count() }}, {{ doubled() }}
// Write: this.count.set(5)  |  this.count.update(n => n + 1)

// Component input/output (modern)
employee  = input.required<Employee>()
select    = output<number>()
// In template: {{ employee().name }}
// Emit: this.select.emit(this.employee().id)

// Service injection (modern)
private employeeService = inject(EmployeeService)

// Dependency safe readonly signal
private _list = signal<Employee[]>([])
readonly list = this._list.asReadonly()

// Observable → Signal
results = toSignal(this.search$.pipe(debounceTime(300), switchMap(...)), { initialValue: [] })

// Signal → Observable  
private search$ = toObservable(this.searchTerm)

// Route params via component input binding
id = input.required<string>()   // bound from :id in route

// HTTP call
this.http.get<Employee[]>('/api/employees').pipe(
  catchError(err => { this._error.set(err.message); return EMPTY })
).subscribe(data => this._employees.set(data))

// Functional guard
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService)
  return auth.isLoggedIn()
    ? true
    : inject(Router).createUrlTree(['/login'], { queryParams: { returnUrl: state.url } })
}

// OnPush — most efficient change detection
@Component({ changeDetection: ChangeDetectionStrategy.OnPush })
```

---

## Angular 21 vs Legacy Angular — At a Glance

| Feature | Angular 21 (this course) | Legacy Angular |
|---------|-------------------------|----------------|
| Components | `standalone: true` | Declared in `NgModule` |
| Bootstrap | `bootstrapApplication()` | `platformBrowserDynamic().bootstrapModule(AppModule)` |
| Providers | `provideRouter()`, `provideHttpClient()` in `app.config.ts` | `NgModule` imports |
| Inputs | `input()` / `input.required()` | `@Input()` |
| Outputs | `output<T>()` | `@Output() + EventEmitter<T>` |
| State | Signals | Zone.js + plain class properties |
| Template conditionals | `@if` / `@for` / `@switch` | `*ngIf` / `*ngFor` / `*ngSwitch` |
| Lazy loading | `loadComponent` / `loadChildren` | `loadChildren: () => import(...Module)` |
| Interceptors | Functional `HttpInterceptorFn` | Class-based `HttpInterceptor` |

---

## Tech Versions

```
@angular/core            21.x
@angular/cli             21.x
@angular/router          21.x
@angular/common          21.x
@angular/forms           21.x
@angular/platform-browser 21.x
rxjs                     7.x
typescript               5.x
jasmine                  5.x
karma                    6.x
```
