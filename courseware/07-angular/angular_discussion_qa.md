# Angular 21 — Discussion Questions & Answers

---

## Fundas

**1. What is Angular? Is it a framework or library?**
Angular is a full-featured front-end **framework** built and maintained by Google. Unlike React (which only handles the View), Angular bundles routing, HTTP, forms, dependency injection, animations, and testing tools — everything you need to build a complete web application is included.

**2. What is a component in Angular?**
A component is a TypeScript class decorated with `@Component` that controls a piece of the UI. It consists of a template (HTML), styles (CSS), and a class (TypeScript logic). The `selector` defines the custom HTML tag used to place it in other templates — e.g. `<app-employee-card>`.

**3. What is a module (NgModule vs standalone component)?**
An NgModule (`@NgModule`) is the legacy way to group related components, directives, pipes, and services. Angular 17+ (and our Angular 21 course) uses **standalone components** — each component imports its own dependencies directly with `standalone: true`, eliminating the need for NgModule in most cases. NgModule still exists for backward compatibility.

**4. Difference between component and service?**
A component handles the **View** — what the user sees and interacts with. A service handles **business logic and data** — API calls, state management, utility functions. Separating them makes code reusable and testable. Services are injected into components via Dependency Injection; they do not have templates.

**5. What is data binding?**
Data binding is the mechanism that keeps the component class and its template in sync. When component data changes, the template updates automatically, and when the user interacts with the template, the component can react. Angular has four types of data binding.

**6. Types of data binding in Angular?**
Four types: (1) **Interpolation** `{{ }}` — class to template, display text; (2) **Property binding** `[prop]="expr"` — class to template, set DOM properties; (3) **Event binding** `(event)="handler()"` — template to class, respond to user actions; (4) **Two-way binding** `[(ngModel)]="prop"` — both directions simultaneously, keeps input and class in sync.

**7. What is interpolation?**
Interpolation uses double curly braces `{{ expression }}` to embed TypeScript expressions into the template as text. The expression is evaluated and its result is converted to a string. Example: `<h3>{{ employee.name }}</h3>` or `{{ count * 2 }}`. It is one-way: class → template.

**8. What is property binding?**
Property binding uses square brackets `[property]="expression"` to set a DOM element's property or a child component's `@Input()` to the value of a TypeScript expression. Example: `<img [src]="employee.avatarUrl" />` or `<button [disabled]="isSubmitting">`. It is one-way: class → template.

**9. What is event binding?**
Event binding uses parentheses `(event)="handler()"` to listen for DOM events and call a component method when they occur. Example: `<button (click)="deleteEmployee()">` or `<input (keydown.enter)="onSearch()">`. It is one-way: template → class.

**10. What is two-way binding?**
Two-way binding combines property binding and event binding in one syntax `[(ngModel)]="property"`. When the component property changes, the template updates; when the user types in the input, the component property updates. Requires `FormsModule` to be imported. The `[()]` syntax is nicknamed "banana in a box."

---

## Concepts

**11. What is a directive?**
A directive is a class that adds behaviour to or modifies an element in the DOM. Angular has three kinds: components (directives with templates), attribute directives (change appearance or behaviour of existing elements), and structural directives (add or remove elements from the DOM).

**12. Difference between structural and attribute directives?**
Structural directives change the DOM layout by adding or removing elements — they use `*` prefix: `*ngIf`, `*ngFor`. Attribute directives modify the appearance or behaviour of an existing element without adding/removing it: `ngClass`, `ngStyle`, or custom ones. In Angular 17+, `@if` and `@for` replace structural directives in templates.

**13. Explain `*ngIf`, `*ngFor`, `*ngSwitch`?**
`*ngIf="condition"` adds or removes an element based on a boolean condition. `*ngFor="let item of list"` renders a template for each item in an array. `*ngSwitch` (with `*ngSwitchCase`) renders the element matching a given value. In Angular 17+, the preferred equivalents are `@if`, `@for`, and `@switch` — same behaviour, cleaner syntax, no import needed.

**14. What is a pipe?**
A pipe transforms data for display in the template without mutating the original value. Used with the `|` operator: `{{ employee.salary | currency:'INR' }}` or `{{ joinDate | date:'dd MMM yyyy' }}`. Pipes keep transformation logic out of the component class and make templates readable.

**15. Built-in vs custom pipes?**
Angular provides built-in pipes: `date`, `currency`, `number`, `percent`, `uppercase`, `lowercase`, `json`, `async`, `slice`, `keyvalue`. Custom pipes are created with `@Pipe({ name: 'myPipe' })` when you need domain-specific transformations — e.g. a `salaryRange` pipe that converts a number to "Senior (₹10L–₹20L)".

**16. What is dependency injection?**
Dependency Injection (DI) is a design pattern where Angular's framework creates and manages service instances and injects them into components or other services automatically. You declare what you need (via `inject()` or constructor parameters), and Angular's injector provides the correct instance based on the provider configuration.

**17. What is a service?**
A service is a TypeScript class decorated with `@Injectable` that contains reusable logic not tied to any specific view — API calls, state management, utility functions, notifications. Services are singletons (one instance per application by default with `providedIn: 'root'`) and are shared across any number of components.

**18. What is HttpClient?**
`HttpClient` is Angular's built-in service for making HTTP requests. It returns **Observables** (not Promises), supports typed responses (`http.get<Employee[]>('/api/employees')`), and integrates with interceptors for auth tokens and error handling. Set it up with `provideHttpClient()` in `app.config.ts`.

**19. Observable vs Promise?**
A Promise resolves once with a single value and starts immediately. An Observable is lazy (starts only when subscribed), can emit multiple values over time, and is cancellable via `unsubscribe()`. Observables also have 100+ RxJS operators for transforming streams. Angular's HttpClient, Router events, and form value changes all use Observables.

**20. What is RxJS?**
RxJS (Reactive Extensions for JavaScript) is a library for reactive programming using Observables. Angular uses it extensively for HTTP, routing, and forms. Key concepts: Observable (stream of values), Observer (subscriber), operators (`map`, `filter`, `switchMap`, `debounceTime`, `catchError`), and Subject (both Observable and Observer).

**21. What is `subscribe()`?**
`subscribe()` activates an Observable and starts receiving its values. It takes up to three callbacks: `next` (called for each value), `error` (called on error), `complete` (called when stream ends). Every subscription must be cleaned up with `unsubscribe()` or operators like `takeUntilDestroyed()` to prevent memory leaks.

**22. What is the `async` pipe?**
The `async` pipe subscribes to an Observable or Promise in the template and automatically unsubscribes when the component is destroyed. Example: `{{ employees$ | async }}`. It eliminates manual subscription management in the component class. With Signals (`signal()`, `computed()`), the `async` pipe is rarely needed — signals are read directly in templates.

---

## Depth

**23. What is change detection?**
Change detection is Angular's mechanism for keeping the template in sync with the component's data. Angular checks component properties and updates the DOM when values change. Traditionally powered by Zone.js, modern Angular uses Signals for more efficient, fine-grained change detection.

**24. Default vs OnPush change detection?**
Default strategy checks the component on every browser event, timer, or HTTP response — it is always safe but can be slow for large trees. `OnPush` (`ChangeDetectionStrategy.OnPush`) only checks when: input signal/reference changes, a DOM event fires inside the component, or `markForCheck()` is called manually. With Signals + `OnPush`, Angular becomes maximally efficient — only components reading a changed signal re-render.

**25. What is Zone.js's role in Angular?**
Zone.js patches browser async APIs (setTimeout, Promise, event listeners) and notifies Angular when any async operation completes, triggering change detection. This is how Angular knows to re-check the component tree after a click or HTTP response — without you calling anything manually. Angular's roadmap is moving away from Zone.js — Signals eventually replace it for change detection.

**26. What is a lifecycle hook?**
A lifecycle hook is an interface method that Angular calls at specific moments in a component's life — creation, input changes, view initialisation, and destruction. Implementing these methods lets you run code at the right time — e.g. fetching data after inputs are available, or cleaning up subscriptions before the component is removed.

**27. Explain `ngOnInit`, `ngOnChanges`, `ngOnDestroy`?**
`ngOnChanges(changes: SimpleChanges)` fires whenever an `@Input()` value changes, including before `ngOnInit`. `ngOnInit()` fires once after the first `ngOnChanges` — inputs are available and the component is ready; this is where you load initial data. `ngOnDestroy()` fires just before the component is removed from the DOM — clean up subscriptions, timers, and event listeners here.

**28. What happens when an input changes?**
`ngOnChanges` fires with a `SimpleChanges` object that contains the previous and current value of each changed input. With the modern `input()` signal API, you can use `effect(() => { ... this.myInput() ... })` instead — it automatically re-runs whenever the input signal's value changes, giving you the same behaviour with cleaner code.

**29. What is ViewChild?**
`@ViewChild` lets a parent component get a reference to a child component instance, directive, or native DOM element in its template. The reference is available in `ngAfterViewInit()`. Example: `@ViewChild(SearchBarComponent) searchBar!: SearchBarComponent;` — the parent can then call `this.searchBar.focus()` directly.

**30. What is content projection?**
Content projection is passing HTML content from a parent template into a child component's template via `<ng-content>`. It's Angular's equivalent of React's `children` prop. Named slots use `select` attribute: `<ng-content select="[card-title]">` receives elements with the `card-title` attribute from the parent.

**31. What is a standalone component (Angular 21)?**
A standalone component has `standalone: true` in its `@Component` decorator and imports its own dependencies (other components, directives, pipes) directly in the `imports` array. It does not need to be declared in an NgModule. Since Angular 17, standalone is the default for all new projects — it reduces boilerplate and makes the component self-contained.

**32. Difference between module-based and standalone architecture?**
Module-based: components are declared in an `@NgModule` which also imports shared modules (`CommonModule`, `HttpClientModule`). All declarations in a module see each other. Standalone: each component declares its own imports, is self-contained, and the app bootstraps with `bootstrapApplication()` instead of a root module. Standalone is simpler, more tree-shakable, and is the future of Angular.

---

## Routing

**33. What is Angular routing?**
Angular Router is a built-in library that maps URL paths to components. It intercepts browser navigation, renders the matching component into a `<router-outlet>`, and updates the browser history — without a full page reload. This is what makes Angular a Single Page Application (SPA).

**34. How to configure routes?**
Define a `Routes` array mapping paths to components and pass it to `provideRouter(routes)` in `app.config.ts`. Example: `{ path: 'employees', loadComponent: () => import('./employees-page.component').then(m => m.EmployeesPageComponent) }`. With `withComponentInputBinding()`, route params are automatically bound to component `input()` signals.

**35. What is `<router-outlet>`?**
`<router-outlet>` is a placeholder directive in the template where Angular renders the component that matches the current URL. The app shell (navigation, header) stays fixed and only the outlet's content changes on navigation. Nested outlets (child routes inside parent components) are also supported.

**36. What is a route parameter?**
A route parameter is a variable segment in the URL path, defined with a colon: `{ path: 'employees/:id' }`. The value of `id` changes per navigation. With `withComponentInputBinding()`, the parameter is automatically injected as a component input: `id = input.required<string>()`. Without it, use `ActivatedRoute.paramMap` Observable.

**37. How to pass data between routes?**
Three ways: (1) **Route params** — in the URL path `/:id` — for identifying resources; (2) **Query params** — `?dept=Engineering&page=2` — for optional filters, good for bookmarking; (3) **Route state** — `router.navigate(['/employees'], { state: { fromDashboard: true } })` — temporary data not in the URL, lost on refresh.

**38. What are route guards?**
Route guards are functions that run before a route activates and can block or redirect navigation. Types: `CanActivateFn` (block access), `CanDeactivateFn` (warn on unsaved changes), `CanMatchFn` (decide if a route config matches). Modern Angular uses functional guards — plain functions using `inject()` internally, no class required.

**39. What is lazy loading?**
Lazy loading means a route's component code is bundled into a separate file and only downloaded when the user navigates to that route. Use `loadComponent` or `loadChildren` in the route config. The initial bundle is smaller, so the app loads faster.

**40. Why lazy loading?**
The initial bundle contains only the code needed for the first screen. Feature code (e.g. admin panel, reports) only downloads when the user actually navigates there. This dramatically reduces the first load time, especially on mobile or slow connections. Combined with `PreloadAllModules`, the chunks are downloaded in the background after the app loads.

---

## Forms

**41. Template-driven vs reactive forms?**
Template-driven forms define the form model in the HTML template using `ngModel` — simpler but harder to test and not suitable for dynamic forms. Reactive forms define the model in the TypeScript class using `FormBuilder`/`FormGroup` — more verbose but fully testable, scalable, and supports dynamic fields and complex validation. Use reactive forms for anything beyond a 2-3 field form.

**42. What is FormControl and FormGroup?**
`FormControl` represents a single form input — it tracks the value, validity, and touched/dirty state. `FormGroup` is a collection of `FormControl`s that represents an entire form or a section of it. `FormBuilder` is a shorthand service to create them concisely: `this.fb.group({ name: ['', Validators.required], email: [''] })`.

**43. What is validation in Angular forms?**
Validation in reactive forms is done by passing validators to `FormControl` or `FormGroup`. Built-in validators: `Validators.required`, `Validators.email`, `Validators.minLength(n)`, `Validators.pattern(regex)`. Validation state is tracked on each control: `.valid`, `.invalid`, `.errors`, `.touched`. In templates, use `@if (nameCtrl.touched && nameCtrl.errors?.['required'])` to show error messages.

**44. Custom validators?**
A custom validator is a function that takes an `AbstractControl` and returns an error object `{ errorKey: true }` if invalid, or `null` if valid. Example: `function uniqueEmailValidator(): ValidatorFn { return (ctrl) => emailExists(ctrl.value) ? { emailTaken: true } : null }`. Async validators (for server checks) return an Observable or Promise of the error object.

**45. When to use reactive forms?**
Use reactive forms for: any form with validation, forms with dynamic fields (add/remove items), forms that need to be unit-tested without a DOM, multi-step wizards, and forms with async validation (checking email uniqueness on the server). Template-driven forms are acceptable only for very simple, 2-3 field forms with no complex logic.

---

## Real Time

**46. API call not showing data in UI — why?**
Most common causes: (1) **Missing subscription** — the Observable from `HttpClient` was not subscribed to (Observables are lazy), (2) **Forgot `await`** — if using a Promise-based approach, (3) **Change detection issue** — the response arrived but the signal/state wasn't updated, (4) **Error swallowed** — the HTTP call failed silently and `catchError` returned an empty value.

**47. Observable data but UI not updating — why?**
With Zone.js: `ChangeDetectorRef.markForCheck()` may need to be called if using `OnPush` and updating a value outside Angular's zone. With Signals: ensure you're calling `.set()` or `.update()` on the signal — directly mutating an object inside a signal without replacing it won't trigger reactivity. With the `async` pipe: ensure the pipe is actually in the template.

**48. Too many API calls happening — fix?**
(1) Check `useEffect`/`ngOnInit` — ensure the API call isn't triggered by a dependency that changes frequently, (2) Debounce search inputs with `debounceTime(300)` + `distinctUntilChanged()`, (3) Use `switchMap` instead of `mergeMap` for search — it cancels the previous request, (4) Check that subscriptions are not duplicated (multiple calls to `subscribe()` on the same Observable).

**49. Parent to child communication?**
Use `@Input()` decorator (legacy) or `input()` signal (Angular 17+, preferred). The parent passes data as an attribute in the template: `<app-employee-card [employee]="emp" />`. The child reads it as a property. With `input()`, it's a read-only signal — `this.employee()` in the child.

**50. Child to parent communication?**
Use `@Output()` with `EventEmitter` (legacy) or `output()` (Angular 17+, preferred). The child emits an event: `this.select.emit(id)`. The parent listens: `<app-employee-card (select)="handleSelect($event)" />`. The `$event` variable carries the emitted value.

**51. Sibling communication?**
Siblings cannot communicate directly in Angular. Two options: (1) **Lift to parent** — parent acts as a hub, receives event from one child and passes data to the other via input; (2) **Shared service** — inject the same service into both siblings; one writes to a `signal()`, the other reads it — they stay in sync automatically.

**52. When to use a service for state sharing?**
Use a service when: (1) multiple unrelated components need the same data, (2) the data persists across route changes, (3) the data comes from an API and should be cached, (4) sibling components need to communicate without involving a parent. Services with Signals are Angular's built-in state management solution for most use cases.

**53. Form with 20 fields — which approach?**
Always use **reactive forms**. With 20 fields you need: programmatic control of each field, cross-field validation, conditional field visibility, server-side async validation, and unit-testable logic. Template-driven forms become unmanageable at this scale. Use `FormBuilder.group()` and extract repeated patterns into methods.

**54. Performance optimisation in Angular?**
(1) Use `ChangeDetectionStrategy.OnPush` on all components, (2) Use Signals — they trigger only the components that read the changed signal, (3) Lazy-load feature routes with `loadComponent`/`loadChildren`, (4) Use `@defer` for below-the-fold content, (5) Use `track` in `@for` (like `trackBy` in `*ngFor`), (6) Use `async` pipe instead of manual subscriptions, (7) Avoid expensive computations in templates — move to `computed()` signals.

**55. When to split components?**
Split when: (1) a component template exceeds ~100 lines, (2) a logical section is reused in multiple places, (3) a section has independent state that doesn't affect the parent, (4) a section has a clear, distinct responsibility. Keep splits meaningful — `<app-employee-card>` for a card, `<app-salary-chart>` for a chart, not `<app-employee-card-name-section>`.

---

## More

**56. Can we use Angular without modules?**
Yes, since Angular 14 with standalone components and confirmed as the default in Angular 17+. Use `bootstrapApplication(AppComponent, appConfig)` instead of a root `NgModule`. Each component manages its own imports. NgModule is still supported for legacy projects but is no longer needed for new ones.

**57. What is AOT vs JIT compilation?**
AOT (Ahead-of-Time): Angular compiles templates to JavaScript during the build — smaller bundle, faster startup, catches template errors at build time (not runtime). JIT (Just-in-Time): compilation happens in the browser at runtime — slower startup, larger bundle, but useful for development. `ng serve` uses JIT by default for speed; `ng build --configuration=production` always uses AOT.

**58. What is tree shaking?**
Tree shaking is a build optimisation that removes unused code from the final JavaScript bundle. The Angular CLI (using esbuild/Webpack) statically analyses imports and eliminates functions, services, and modules that are never referenced. Standalone components improve tree shaking because each component only imports exactly what it uses — unused pipes and directives are eliminated.

**59. What is Angular CLI?**
Angular CLI (`@angular/cli`) is the official command-line tool for creating, developing, testing, and building Angular applications. Key commands: `ng new` (scaffold), `ng generate component/service/pipe` (create files correctly wired), `ng serve` (dev server), `ng build --configuration=production` (prod build), `ng test` (run tests), `ng lint` (code quality).

**60. What is `environment.ts`?**
`environment.ts` (and `environment.production.ts`) stores environment-specific configuration values — API base URL, feature flags, debug settings. During `ng build`, the CLI uses `fileReplacements` to swap the development file with the production file automatically. Access values in code with `import { environment } from '../environments/environment'`.

---

## Additional Questions

**61. What is the difference between `@Input()` and `input()` signal?**
`@Input()` (legacy) is a decorator — the value is a plain TypeScript property, no reactivity built in. `input()` (Angular 17+) returns a read-only signal — the value is reactive and can be read in templates with `()` and used inside `computed()` and `effect()`. `input.required<T>()` throws a compile-time error if the parent doesn't provide the value.

**62. What is the difference between `@Output()` + `EventEmitter` and `output()`?**
Both emit events from child to parent. `@Output()` with `EventEmitter` is the legacy API — `new EventEmitter<number>()` and `emit(value)`. `output<T>()` (Angular 17+) is the modern API — same `emit(value)` method but no need to extend `EventEmitter`. The template binding syntax `(select)="handler($event)"` is identical for both.

**63. What is `inject()` and how is it different from constructor injection?**
`inject()` is a function available in Angular 14+ that retrieves a dependency from the current injection context. It can be used at field initialisation time: `private service = inject(EmployeeService)`. Constructor injection does the same but requires the dependency in the constructor signature. `inject()` is more flexible and pairs well with standalone components.

**64. What is the difference between `signal()` and `computed()`?**
`signal()` creates a **writable** reactive value — you can call `.set()` and `.update()` on it. `computed()` creates a **read-only** derived value that automatically recalculates when any signal it reads changes. You cannot write to a computed signal. Use `signal()` for raw state, `computed()` for derived state.

**65. What is `effect()` and when should you use it?**
`effect()` registers a side-effect function that automatically re-runs whenever any signal it reads changes. Use it for: logging, syncing to `localStorage`, updating a non-Angular library when signal changes. Do NOT use it to update other signals — use `computed()` for that. An `effect()` that writes to a signal it also reads causes an infinite loop.

**66. What is `toSignal()` and `toObservable()`?**
`toSignal(observable$)` converts an Observable to a Signal — Angular subscribes and unsubscribes automatically, and the signal's value updates whenever the Observable emits. `toObservable(mySignal)` converts a Signal to an Observable — useful when you need RxJS operators like `debounceTime` or `switchMap` on signal values. Both are in `@angular/core/rxjs-interop`.

**67. What is the `@defer` block?**
`@defer` is an Angular 17+ template feature for declarative lazy loading. Content inside `@defer` is rendered only when a trigger fires — `on viewport` (element scrolled into view), `on idle` (browser is idle), `on interaction` (user clicks), or `when condition()` (a boolean). It reduces initial bundle size without any JavaScript imports or `React.lazy`-style code.

**68. What is `withComponentInputBinding()` in `provideRouter()`?**
`withComponentInputBinding()` is a router feature (Angular 16+) that automatically binds route params, query params, and route `data` to component `@Input()` / `input()` signals — no need to inject `ActivatedRoute` and subscribe to `paramMap`. Example: route `{ path: 'employees/:id' }` → component `id = input.required<string>()`.

**69. What is the difference between `ClusterIP`, `NodePort`, and the Angular Router?**
This is a trick question — the first two are Kubernetes service types (DevOps), not Angular. The Angular equivalent of "routing traffic to the right component" is the **Router** + `<router-outlet>`. Don't confuse infrastructure routing with client-side UI routing.

**70. What is a resolver in Angular routing?**
A resolver is a service that runs before a route activates and pre-fetches data needed by the component. The component receives the data via `ActivatedRoute.data` and doesn't need to handle loading state itself. Useful when you want the component to always render with data already available, avoiding a loading spinner inside the component.

**71. What is `ActivatedRoute`?**
`ActivatedRoute` is an Angular service injected into a component that provides information about the currently active route — params, query params, fragment, route data, and URL snapshot. With `withComponentInputBinding()`, you rarely need `ActivatedRoute` directly for params — they come as `input()` signals automatically.

**72. What is the difference between `switchMap`, `mergeMap`, and `concatMap`?**
All three map each emission to an inner Observable. `switchMap` cancels the previous inner Observable on each new emission — perfect for search (only care about the latest request). `mergeMap` runs all inner Observables concurrently — use for parallel independent operations. `concatMap` queues inner Observables and runs them sequentially — use when order matters.

**73. What is a Subject vs BehaviorSubject?**
Both are Observables you can push values into. A `Subject` doesn't have a current value — late subscribers miss previous emissions. A `BehaviorSubject` requires an initial value and emits the current value immediately to new subscribers — great for state management where a component needs the current value when it subscribes. `BehaviorSubject.value` reads the current value synchronously.

**74. What is `takeUntilDestroyed()` and why use it?**
`takeUntilDestroyed()` is an Angular 16+ RxJS operator that automatically completes an Observable subscription when the current component or service is destroyed — no manual `ngOnDestroy` + `unsubscribe()` required. Import from `@angular/core/rxjs-interop`. It replaces the `Subject` + `takeUntil` pattern that was previously the standard way to prevent memory leaks.

**75. What is Angular's `HttpInterceptorFn`?**
A functional HTTP interceptor is a function that runs on every outgoing request or incoming response — the ideal place for auth token attachment, global error handling, and loading state. Register with `provideHttpClient(withInterceptors([authInterceptor, errorInterceptor]))` in `app.config.ts`. Modern Angular uses functions instead of the legacy class-based `HttpInterceptor`.

**76. What is `OnPush` change detection and when should you use it?**
`OnPush` (`ChangeDetectionStrategy.OnPush`) tells Angular to skip change detection for a component unless: (1) an `@Input()` or `input()` reference changes, (2) an Observable the template uses emits (via `async` pipe), (3) a signal the template reads changes, or (4) an event fires inside the component. Apply it to all "leaf" components (like list item cards) that receive immutable data — it dramatically reduces unnecessary re-renders.

**77. What is the Angular `inject()` function used for outside a constructor?**
`inject()` works anywhere within an injection context — class constructors, field initialisers, and factory providers. This means you can inject services at field level: `private service = inject(EmployeeService)` instead of the constructor, making component code more concise and allowing injection in functional guards, interceptors, and `runInInjectionContext`.

**78. What happens if you forget to unsubscribe from an Observable?**
The Observable keeps running and holding memory even after the component is destroyed. Every time the component is created and destroyed (e.g. navigating back and forth), a new subscription is created but the old ones aren't cleaned up — this is a memory leak. Solutions: `takeUntilDestroyed()`, `async` pipe, converting to signals with `toSignal()`, or manual `ngOnDestroy` + `unsubscribe()`.

**79. What is the difference between `ngOnInit` and the constructor in Angular?**
The constructor runs when the TypeScript class is instantiated — Angular DI uses it to inject dependencies. At this point, inputs are not yet available. `ngOnInit` runs after Angular has set the component's input properties — safe to use `this.myInput()`. Put DI injection in the constructor (or use `inject()`); put initialisation logic that needs inputs in `ngOnInit`.

**80. What is Zoneless Angular?**
Zoneless Angular (experimental in Angular 19, maturing in Angular 21) removes the Zone.js dependency entirely. Change detection is driven purely by Signals — Angular only checks and updates the parts of the UI that read a changed signal. This eliminates the overhead of Zone.js patching browser APIs and produces smaller bundles. Opt in with `provideExperimentalZonelessChangeDetection()` in `app.config.ts`.

---

## Answer Summary Table

| # | Question (short) | Key answer |
|---|---------|------------|
| 1 | Framework or library? | Full framework — routing, HTTP, forms, DI all built in |
| 2 | Component | Class + template + styles; `@Component` decorator |
| 3 | NgModule vs standalone | NgModule groups declarations; standalone imports own deps |
| 4 | Component vs service | Component = view; service = logic/data |
| 5 | Data binding | Sync between class and template |
| 6 | Types of binding | Interpolation, property, event, two-way |
| 11 | Directive | Modifies DOM behaviour or structure |
| 12 | Structural vs attribute | Add/remove elements vs change appearance |
| 16 | DI | Framework provides dependencies automatically |
| 19 | Observable vs Promise | Multiple values, lazy, cancellable vs one value |
| 23 | Change detection | Keeps template in sync with data |
| 24 | Default vs OnPush | Always checks vs only on input/signal change |
| 25 | Zone.js | Patches async APIs, triggers change detection |
| 31 | Standalone component | `standalone: true`, imports own deps, no NgModule |
| 39 | Lazy loading | Load code only when route is visited |
| 45 | When reactive forms? | Complex forms, dynamic fields, testing |
| 57 | AOT vs JIT | Build time vs runtime compilation |
| 64 | signal() vs computed() | Writable vs read-only derived |
| 67 | @defer | Declarative lazy rendering in templates |
| 80 | Zoneless | Signals-only change detection, no Zone.js |
