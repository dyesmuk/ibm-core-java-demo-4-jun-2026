# Angular 21 MCQ Assessment — Question Bank (80 Questions)

> **Instructions:** One correct answer per question. Covers all 13 modules.

---

## Getting Started & Architecture (Q1–Q8)

**Q1.** What command creates a new Angular 21 project with standalone components, routing, and CSS?

- A) `ng new my-app --module --style=css`
- B) `ng new my-app --routing --style=css --standalone`
- C) `ng create my-app --template angular-ts`
- D) `npx create-angular my-app`

**Answer: B**

---

**Q2.** What does the `selector` property in `@Component` define?

- A) The CSS class applied to the component
- B) The custom HTML tag used to render this component in templates
- C) The route path this component responds to
- D) The component's module membership

**Answer: B**

---

**Q3.** What is the role of `bootstrapApplication()` in a modern Angular app?

- A) Installs Angular dependencies via npm
- B) Starts the Angular application by mounting the root component and applying app config
- C) Registers all routes
- D) Compiles templates ahead of time

**Answer: B**

---

**Q4.** In a standalone Angular component, what does the `imports` array in `@Component` do?

- A) Imports the component's TypeScript dependencies
- B) Declares which other standalone components, directives, and pipes this component can use in its template
- C) Registers the component with Angular's module system
- D) Defines the component's child routes

**Answer: B**

---

**Q5.** What is the purpose of `app.config.ts` in a standalone Angular application?

- A) Configures ESLint and Prettier
- B) Provides global configuration — router, HTTP client, and other app-wide providers
- C) Defines all component styles
- D) Contains the application's environment variables

**Answer: B**

---

**Q6.** What is the key difference between Angular (framework) and React (library)?

- A) Angular uses JavaScript; React uses TypeScript
- B) Angular bundles routing, HTTP, forms, and DI; React handles only the View layer
- C) React is faster in all scenarios
- D) Angular cannot be used for mobile applications

**Answer: B**

---

**Q7.** What does `ng generate component employees/employee-card` do?

- A) Creates a route for `/employees/employee-card`
- B) Creates the component files and wires them correctly — `.ts`, `.html`, `.css`, `.spec.ts`
- C) Only creates the TypeScript class file
- D) Adds the component to the root `NgModule`

**Answer: B**

---

**Q8.** Why does Angular require `<base href="/">` in `index.html`?

- A) It sets the app's title in the browser tab
- B) Angular Router uses it as the base path for all route calculations
- C) It enables CORS for API requests
- D) It tells the browser which CSS file to load first

**Answer: B**

---

## Data Binding & Signals (Q9–Q20)

**Q9.** Which data binding type is used to display a TypeScript expression as text in the template?

- A) `[property]="expr"`
- B) `(event)="handler()"`
- C) `{{ expression }}`
- D) `[(ngModel)]="prop"`

**Answer: C**

---

**Q10.** What does property binding `[disabled]="isLoading()"` do?

- A) Adds a CSS class named `disabled` to the element
- B) Binds the DOM `disabled` property to the value of `isLoading()` — enabling/disabling the element
- C) Calls the `isLoading()` method when the element is disabled
- D) Creates a two-way binding for the disabled state

**Answer: B**

---

**Q11.** What does two-way binding `[(ngModel)]="searchTerm"` require in the component?

- A) `ReactiveFormsModule` imported
- B) `FormsModule` imported in the component's `imports` array
- C) A `@ViewChild('searchTerm')` reference
- D) A custom setter method named `setSearchTerm`

**Answer: B**

---

**Q12.** What is a Signal in Angular?

- A) A method to make HTTP requests
- B) A reactive value container — reading it tracks dependencies, writing it triggers re-renders in components that read it
- C) A type of event emitter for component outputs
- D) An Angular Router event

**Answer: B**

---

**Q13.** How do you read the current value of a signal named `count` in a template?

- A) `{{ count }}`
- B) `{{ count() }}`
- C) `{{ count.value }}`
- D) `{{ count | async }}`

**Answer: B**

---

**Q14.** What is the difference between `signal.set()` and `signal.update()`?

- A) `set()` is for primitives; `update()` is for objects
- B) `set()` replaces the value directly; `update()` derives the new value from the current one via a callback
- C) `update()` is synchronous; `set()` is asynchronous
- D) They are identical

**Answer: B**

---

**Q15.** What does `computed()` return?

- A) A writable signal
- B) A read-only signal whose value is automatically derived from other signals it reads
- C) An Observable
- D) A Promise

**Answer: B**

---

**Q16.** When does an `effect()` run?

- A) Only once, on component initialisation
- B) Immediately, and again whenever any signal it reads changes
- C) Only when explicitly called
- D) On every Angular change detection cycle

**Answer: B**

---

**Q17.** What is wrong with this code?

```ts
effect(() => {
  const list = this.employees()
  this.employees.set(list.filter(e => e.isActive))
})
```

- A) `effect` cannot read signals
- B) The effect reads `employees()` AND writes to `employees` — causes an infinite loop
- C) `filter` is not available inside effects
- D) Nothing is wrong

**Answer: B**

---

**Q18.** What does `signal.asReadonly()` return?

- A) A deep-frozen copy of the signal's value
- B) A read-only version of the signal — can be read but not written externally
- C) A new signal with the same value
- D) An Observable of the signal's values

**Answer: B**

---

**Q19.** What is the Angular 17+ syntax for conditionally rendering an element?

- A) `*ngIf="condition"`
- B) `@if (condition) { } @else { }`
- C) `[hidden]="!condition"`
- D) `<ng-if [when]="condition">`

**Answer: B**

---

**Q20.** In `@for (emp of employees(); track emp.id)`, what does `track` do?

- A) Tracks mouse events on the element
- B) Tells Angular which property uniquely identifies each item, enabling efficient DOM updates when the list changes
- C) Adds a CSS tracking class to each element
- D) Records analytics on list interactions

**Answer: B**

---

## Components & Directives (Q21–Q32)

**Q21.** What is the modern Angular 17+ API for declaring a component input?

- A) `@Input() name: string`
- B) `name = input<string>()`
- C) `name = signal<string>('')`
- D) `@Prop() name!: string`

**Answer: B**

---

**Q22.** What does `input.required<Employee>()` mean?

- A) The input is a required form field
- B) Angular will throw a compile-time error if the parent doesn't provide this input
- C) The input must be of type `Employee` but is optional
- D) The input is validated against the `Employee` schema

**Answer: B**

---

**Q23.** How do you emit an event from a child component using the modern `output()` API?

- A) `this.myOutput.next(value)`
- B) `this.myOutput.emit(value)`
- C) `this.myOutput.dispatch(value)`
- D) `this.myOutput.send(value)`

**Answer: B**

---

**Q24.** What Angular lifecycle hook runs once after the component is first initialised and its inputs are available?

- A) `constructor()`
- B) `ngOnInit()`
- C) `ngAfterViewInit()`
- D) `ngOnChanges()`

**Answer: B**

---

**Q25.** What does `ngOnDestroy()` provide that `constructor()` does not?

- A) Access to component inputs
- B) A hook to clean up subscriptions, timers, and event listeners before the component is removed
- C) Access to the component's template
- D) DI injection of services

**Answer: B**

---

**Q26.** What does `ViewEncapsulation.Emulated` (the default) do?

- A) Makes component styles global
- B) Adds Angular-generated attribute selectors to CSS rules so styles only apply to that component's elements
- C) Uses the browser's Shadow DOM
- D) Disables all scoped styling

**Answer: B**

---

**Q27.** What does `<ng-content select="[card-title]">` do?

- A) Creates a slot that projects content with the `card-title` attribute from the parent template
- B) Selects the first child component named `card-title`
- C) Renders the component whose selector is `card-title`
- D) Applies the `card-title` CSS class to the projected content

**Answer: A**

---

**Q28.** Which directive adds/removes a CSS class based on a condition without using `ngClass`?

- A) `[style.class]="condition"`
- B) `[class.active]="condition"`
- C) `*ngClass="condition"`
- D) `(class)="condition"`

**Answer: B**

---

**Q29.** What does the `*` prefix on a directive like `*ngIf` indicate?

- A) The directive is deprecated
- B) It's a structural directive — syntactic sugar that expands to `<ng-template [ngIf]="...">`
- C) The directive is applied to the host element only
- D) It's a required directive that must be present

**Answer: B**

---

**Q30.** When building a custom attribute directive, why use `Renderer2` instead of directly manipulating `nativeElement`?

- A) `Renderer2` is faster
- B) `Renderer2` provides a safe abstraction that works in server-side rendering and non-browser environments
- C) `nativeElement` is read-only in directives
- D) `Renderer2` automatically runs inside Angular zones

**Answer: B**

---

**Q31.** What is the `@ViewChild` decorator used for?

- A) Getting a reference to a parent component
- B) Getting a reference to a child component, directive, or DOM element in the template
- C) Accessing route parameters
- D) Defining a dependency to be injected

**Answer: B**

---

**Q32.** In what lifecycle hook is `@ViewChild` reference first available?

- A) `ngOnInit`
- B) `ngAfterViewInit`
- C) `ngOnChanges`
- D) `constructor`

**Answer: B**

---

## Services, DI & Routing (Q33–Q45)

**Q33.** What does `providedIn: 'root'` in `@Injectable` do?

- A) Makes the service available only in the root module
- B) Creates a single shared instance of the service for the entire application
- C) Registers the service as a component provider
- D) Makes the service available only to child components

**Answer: B**

---

**Q34.** What is the difference between `inject(MyService)` and constructor injection?

- A) `inject()` creates a new instance; constructor injection reuses the singleton
- B) They both access the same injected instance — `inject()` is the modern functional API; constructor injection is the legacy OOP style
- C) Constructor injection is required for abstract services
- D) `inject()` only works in services, not components

**Answer: B**

---

**Q35.** A component provides `providers: [MyService]`. What happens?

- A) `MyService` becomes a singleton for the whole app
- B) A fresh instance of `MyService` is created for that component and its children — separate from the root instance
- C) The component takes over the root `MyService` instance
- D) An error is thrown if `MyService` already has `providedIn: 'root'`

**Answer: B**

---

**Q36.** What does `withComponentInputBinding()` in `provideRouter()` enable?

- A) Two-way binding for route parameters
- B) Automatic binding of route params, query params, and route data to component `input()` signals
- C) Bi-directional navigation between components
- D) Type checking for route parameters

**Answer: B**

---

**Q37.** What is the purpose of `<router-outlet>`?

- A) To generate anchor links for all routes
- B) A placeholder where the matched route's component is rendered
- C) To preload all lazy-loaded routes on startup
- D) To define route guards

**Answer: B**

---

**Q38.** What does `routerLinkActive="nav-active"` do?

- A) Makes the link navigate only when the route is active
- B) Adds the `nav-active` CSS class to the element when its `routerLink` matches the current URL
- C) Activates lazy loading for the route
- D) Disables the link when the route is not available

**Answer: B**

---

**Q39.** How do you navigate programmatically to `/employees/1/edit` in Angular?

- A) `window.location.href = '/employees/1/edit'`
- B) `this.router.navigate(['/employees', 1, 'edit'])`
- C) `this.router.go('/employees/1/edit')`
- D) `<a [navigate]="['/employees', 1, 'edit']">`

**Answer: B**

---

**Q40.** What does a `CanActivateFn` guard return to BLOCK navigation?

- A) `false` or a `UrlTree` redirecting elsewhere
- B) `null`
- C) `undefined`
- D) An error object

**Answer: A**
*Returns `false` to block, or a `UrlTree` (from `router.createUrlTree(...)`) to redirect.*

---

**Q41.** What is the benefit of `loadComponent` in a route definition?

- A) It loads the component synchronously for better performance
- B) The component code is bundled separately and only downloaded when the user navigates to that route
- C) It applies a loading animation automatically
- D) It validates the component's inputs before rendering

**Answer: B**

---

**Q42.** How do you store filter state in the URL query string in Angular?

- A) Use `Router.setQueryParam()`
- B) Use `Router.navigate([], { queryParams: { dept: 'Engineering' }, queryParamsHandling: 'merge' })`
- C) Store it in `window.location.search`
- D) Use `ActivatedRoute.setParams()`

**Answer: B**

---

**Q43.** What does `queryParamsHandling: 'merge'` do when navigating?

- A) Merges the new query params with those from the previous navigation history
- B) Preserves existing query params and adds/updates only the specified ones
- C) Merges query params from all active routes
- D) Combines route params and query params into one object

**Answer: B**

---

**Q44.** What is the `canDeactivate` guard used for?

- A) Blocking navigation away from a route, e.g. to warn about unsaved form changes
- B) Preventing a component from being destroyed
- C) Checking user permissions before loading a route
- D) Deactivating animations when leaving a route

**Answer: A**

---

**Q45.** What does `router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } })` return?

- A) A string URL for programmatic navigation
- B) A `UrlTree` object that Angular Router uses to redirect the user
- C) A route configuration object
- D) A guard that redirects to login

**Answer: B**

---

## Observables, Forms & HTTP (Q46–Q60)

**Q46.** What is the key difference between an Observable and a Promise?

- A) Promises are async; Observables are synchronous
- B) Observables are lazy and cancellable and can emit multiple values; Promises start immediately and emit one value
- C) Observables only work in Angular; Promises are standard JavaScript
- D) Promises are better for HTTP requests

**Answer: B**

---

**Q47.** What does `switchMap` do in an RxJS pipe?

- A) Switches between two Observables alternately
- B) Cancels the previous inner Observable and subscribes to a new one on each source emission — ideal for search-as-you-type
- C) Merges all inner Observables concurrently
- D) Converts an Observable to a Promise

**Answer: B**

---

**Q48.** What does `debounceTime(300)` do?

- A) Delays the subscription to an Observable by 300ms
- B) Waits until no new values have been emitted for 300ms, then emits the last value — useful for search input
- C) Throttles emissions to one per 300ms
- D) Cancels the Observable if it doesn't emit within 300ms

**Answer: B**

---

**Q49.** What is a `BehaviorSubject` and how does it differ from a plain `Subject`?

- A) `BehaviorSubject` is immutable; `Subject` is mutable
- B) `BehaviorSubject` requires an initial value and emits the current value to new subscribers; `Subject` emits nothing to late subscribers
- C) `Subject` can be used in templates; `BehaviorSubject` cannot
- D) They are identical

**Answer: B**

---

**Q50.** What does `toSignal(observable$, { initialValue: [] })` do?

- A) Converts a Signal to an Observable
- B) Converts an Observable to a Signal — subscribes automatically and returns the latest emitted value as a signal
- C) Creates a new Observable that emits signal values
- D) Pipes the Observable through the Angular change detector

**Answer: B**

---

**Q51.** What does `takeUntilDestroyed()` do?

- A) Destroys the component when the Observable completes
- B) Automatically unsubscribes from an Observable when the current component or service is destroyed
- C) Takes values from the Observable until it errors
- D) Limits the Observable to one emission then destroys it

**Answer: B**

---

**Q52.** What is the difference between Reactive Forms and Template-Driven Forms?

- A) Reactive Forms use HTML5 validation; Template-Driven use JavaScript
- B) In Reactive Forms, the form model is defined in TypeScript (testable, dynamic); in Template-Driven, it's defined in HTML (simpler, less control)
- C) Template-Driven Forms support `FormArray`; Reactive Forms do not
- D) Reactive Forms only work with `HttpClient`

**Answer: B**

---

**Q53.** What does `form.markAllAsTouched()` do?

- A) Marks all controls as pristine
- B) Marks all controls as touched — triggers error messages for all invalid fields at once (useful when user submits an invalid form)
- C) Submits the form
- D) Resets all form values to their initial state

**Answer: B**

---

**Q54.** What is the third parameter array in `this.fb.group({ email: ['', [Validators.required], [asyncValidator]] })` used for?

- A) Default values for the control
- B) Async validators — validators that return an Observable or Promise, used for server-side checks like email uniqueness
- C) CSS classes to apply to the control
- D) Disabled state configuration

**Answer: B**

---

**Q55.** What does `form.patchValue(data)` do compared to `form.setValue(data)`?

- A) `patchValue` validates the data; `setValue` skips validation
- B) `patchValue` updates only the provided fields; `setValue` requires ALL fields to be provided or throws
- C) `patchValue` is async; `setValue` is synchronous
- D) They are identical

**Answer: B**

---

**Q56.** How do you register `provideHttpClient` in a modern Angular standalone app?

- A) Import `HttpClientModule` in every component that needs it
- B) Add `provideHttpClient(withInterceptors([...]))` to the `providers` array in `app.config.ts`
- C) Inject `HttpClient` directly without any configuration
- D) Add `HttpClient` to `@NgModule` imports

**Answer: B**

---

**Q57.** What does an `HttpInterceptorFn` intercept?

- A) DOM events in the component tree
- B) Every outgoing HTTP request and incoming response — used for auth tokens, error handling, and logging
- C) Angular Router navigation events
- D) RxJS stream emissions

**Answer: B**

---

**Q58.** What does `req.clone({ setHeaders: { Authorization: 'Bearer token' } })` do in an interceptor?

- A) Mutates the original request to add the header
- B) Creates a modified copy of the request with the new header — the original is immutable
- C) Delays the request until the token is available
- D) Caches the request

**Answer: B**

---

**Q59.** What does `finalize(() => loading.stop())` in an HTTP pipe guarantee?

- A) The loading stops only on success
- B) The loading stops whether the request succeeds or fails — runs on complete and on error
- C) The loading stops after a fixed timeout
- D) The loading stops before the HTTP response is processed

**Answer: B**

---

**Q60.** What does `HttpParams` do in Angular?

- A) Parses HTTP response bodies
- B) Builds URL query strings in an immutable, type-safe way for GET requests
- C) Validates HTTP request headers
- D) Retries failed requests

**Answer: B**

---

## Authentication, Performance & Testing (Q61–Q75)

**Q61.** Where does an Angular `CanActivateFn` guard run?

- A) After the component renders
- B) Before the route's component is loaded or rendered — blocking navigation if conditions aren't met
- C) Inside the component's `ngOnInit`
- D) In the HTTP interceptor pipeline

**Answer: B**

---

**Q62.** What does `router.createUrlTree(['/login'], { queryParams: { returnUrl: '...' } })` return from a guard?

- A) A boolean `false`
- B) A `UrlTree` that Angular interprets as a redirect to `/login` with the return URL as a query param
- C) A navigation promise
- D) An error to be caught

**Answer: B**

---

**Q63.** What is `ChangeDetectionStrategy.OnPush` used for?

- A) Forcing Angular to check the component on every event regardless of input changes
- B) Telling Angular to skip change detection for this component unless its signal/input references change or a DOM event fires — improves performance
- C) Making all component properties immutable
- D) Using the Shadow DOM for this component

**Answer: B**

---

**Q64.** What does `@defer (on viewport)` do?

- A) Defers rendering until the user scrolls the element into the visible viewport
- B) Loads the content immediately but animates it into view
- C) Defers HTTP requests until the element is visible
- D) Reduces the component's change detection frequency

**Answer: A**

---

**Q65.** What is the benefit of `loadChildren` (lazy loading) in route configuration?

- A) Routes load faster because all code is pre-downloaded
- B) The code for that route is bundled separately and only downloaded when the user navigates there — reduces initial bundle size
- C) It automatically caches the component in `localStorage`
- D) It enables parallel rendering of multiple routes

**Answer: B**

---

**Q66.** What does `PreloadAllModules` do as a preloading strategy?

- A) Loads all lazy modules before the app starts
- B) Downloads all lazy route chunks in the background after the initial page load — instant navigation on first visit
- C) Preloads only the routes the user has previously visited
- D) Caches all routes in Service Worker

**Answer: B**

---

**Q67.** What is `createComponent()` used for in Angular?

- A) Generating component files with the Angular CLI
- B) Programmatically creating and inserting a component at runtime — used for dynamic dialogs, modals, and toasts
- C) Creating a component factory for dependency injection
- D) Compiling a component's template at runtime

**Answer: B**

---

**Q68.** In `TestBed.configureTestingModule()`, what is the purpose of the `providers` array?

- A) Importing Angular modules for testing
- B) Registering or mocking services that the component under test depends on
- C) Declaring which components to test
- D) Setting up HTTP mocks

**Answer: B**

---

**Q69.** How do you set an `input()` signal on a component in a unit test?

- A) `component.myInput = value`
- B) `fixture.componentRef.setInput('myInput', value)`
- C) `TestBed.setInput('myInput', value)`
- D) `component.myInput.set(value)`

**Answer: B**

---

**Q70.** What does `jasmine.createSpyObj('service', ['method1', 'method2'])` create?

- A) A real service with two methods
- B) A mock object with the specified methods as spy functions — can track calls and return values
- C) A test fixture for the service
- D) An HttpClient mock

**Answer: B**

---

**Q71.** What does `fixture.detectChanges()` do in Angular unit tests?

- A) Detects syntax errors in the component template
- B) Triggers Angular change detection, causing the component to re-render based on current state
- C) Detects missing imports
- D) Runs all lifecycle hooks from scratch

**Answer: B**

---

**Q72.** What is `AOT (Ahead-of-Time) compilation` in Angular?

- A) Templates are compiled in the browser at runtime
- B) Templates are compiled to JavaScript during the build — faster startup, catches template errors earlier, smaller runtime bundle
- C) A code optimisation that runs after deployment
- D) Angular's name for tree-shaking

**Answer: B**

---

**Q73.** What does the `ng-content` directive do?

- A) Inserts dynamic content from a service
- B) Projects content from the parent template into the child component's template — enables reusable container components
- C) Creates a content delivery network for assets
- D) Generates HTML content from TypeScript data

**Answer: B**

---

**Q74.** What does `withInterceptors([authInterceptor])` in `provideHttpClient()` do?

- A) Adds the interceptors to the application's DI tree as services
- B) Registers functional interceptors that run on every HTTP request and response
- C) Validates the interceptors at compile time
- D) Applies interceptors only to POST requests

**Answer: B**

---

**Q75.** What is `NgModule` in Angular's legacy architecture?

- A) A TypeScript module (file-level export)
- B) A class decorated with `@NgModule` that groups components, pipes, directives, and imports — the old way to organise Angular code before standalone components
- C) Angular's HTTP client module
- D) A CLI tool for module management

**Answer: B**

---

## Advanced & Signals (Q76–Q80)

**Q76.** What is the purpose of the `@defer` block's `@placeholder`?

- A) Holds a fallback value for the deferred signal
- B) Shows content before the deferred block starts loading — replaced when the real content loads
- C) Defers the placeholder rendering to improve performance
- D) A template reference for the loading spinner

**Answer: B**

---

**Q77.** How does `toObservable(mySignal)` behave?

- A) Converts the signal to a Promise
- B) Creates an Observable that emits whenever the signal's value changes
- C) Subscribes to the signal and logs each change
- D) Freeze the signal value into an Observable snapshot

**Answer: B**

---

**Q78.** What does `signal.asReadonly()` prevent?

- A) Reading the signal value in child components
- B) External callers from calling `.set()` or `.update()` — the signal can only be modified through the service that owns it
- C) The signal from being used in computed()
- D) Change detection from tracking the signal

**Answer: B**

---

**Q79.** A functional route guard returns `router.createUrlTree(['/login'])`. What happens?

- A) The guard throws an error and navigation fails
- B) Angular cancels the current navigation and redirects the user to `/login`
- C) The guard logs the user out and reloads the page
- D) Nothing — return values from guards are ignored

**Answer: B**

---

**Q80.** What is the Angular "zoneless" mode?

- A) Running Angular without any CSS zones
- B) Running Angular without Zone.js — change detection is driven entirely by Signals instead of patching browser APIs
- C) A mode that disables animations for performance
- D) Running Angular in a Web Worker without access to the DOM

**Answer: B**

---

## Answer Key

| Q | A | Q | A | Q | A | Q | A | Q | A |
|---|---|---|---|---|---|---|---|---|---|
| 1 | B | 17 | B | 33 | B | 49 | B | 65 | B |
| 2 | B | 18 | B | 34 | B | 50 | B | 66 | B |
| 3 | B | 19 | B | 35 | B | 51 | B | 67 | B |
| 4 | B | 20 | B | 36 | B | 52 | B | 68 | B |
| 5 | B | 21 | B | 37 | B | 53 | B | 69 | B |
| 6 | B | 22 | B | 38 | B | 54 | B | 70 | B |
| 7 | B | 23 | B | 39 | B | 55 | B | 71 | B |
| 8 | B | 24 | B | 40 | A | 56 | B | 72 | B |
| 9 | C | 25 | B | 41 | B | 57 | B | 73 | B |
| 10 | B | 26 | B | 42 | B | 58 | B | 74 | B |
| 11 | B | 27 | A | 43 | B | 59 | B | 75 | B |
| 12 | B | 28 | B | 44 | A | 60 | B | 76 | B |
| 13 | B | 29 | B | 45 | B | 61 | B | 77 | B |
| 14 | B | 30 | B | 46 | B | 62 | B | 78 | B |
| 15 | B | 31 | B | 47 | B | 63 | B | 79 | B |
| 16 | B | 32 | B | 48 | B | 64 | A | 80 | B |

---

## Topic Coverage

| Module | Topic | Questions |
|--------|-------|-----------|
| 00 | Getting Started & Architecture | Q1–Q8 |
| 01 | Data Binding & Signals | Q9–Q20 |
| 02 | Components & Directives | Q21–Q32 |
| 05 | Services & DI | Q33–Q35 |
| 06 | Routing | Q36–Q45 |
| 07 | Observables & RxJS | Q46–Q51 |
| 08 | Forms | Q52–Q55 |
| 10 | HTTP | Q56–Q60 |
| 11 | Authentication | Q61–Q62 |
| 12 | Performance & Dynamic Components | Q63–Q67 |
| 13 | Testing | Q68–Q72 |
| Mixed | Advanced Signals & Angular 21 | Q73–Q80 |
