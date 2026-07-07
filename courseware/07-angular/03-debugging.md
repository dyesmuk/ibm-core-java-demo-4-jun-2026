# Module 03 — Debugging Angular Apps

## Learning Objectives
- Use Angular DevTools to inspect components and signals
- Debug templates and TypeScript effectively
- Understand common Angular errors and their fixes
- Use source maps in the browser
- EMS: add debugging infrastructure to the project

---

## 3.1 Angular DevTools

Install the **Angular DevTools** browser extension (Chrome / Firefox).

After installing, open browser DevTools (`F12`) → you'll see an **Angular** tab.

### Component Tree Panel

```
▼ AppComponent
    ▼ NavbarComponent
    ▼ EmployeeListComponent
        employees: Signal([...4 items])
        filter: Signal("Engineering")
        filteredEmployees: Computed([...2 items])
      ▼ EmployeeCardComponent  ← click to inspect
          employee: { id:1, name:"Alice", ... }
          isHighlighted: false
```

**What you can do:**
- Click any component → see its **properties, inputs, signals** on the right
- Edit property values live — see the view update immediately
- Search components by name
- View the full injector hierarchy (dependency injection chain)

### Profiler Panel

1. Click Record
2. Interact (type, click, filter)
3. Stop recording
4. See which components were checked and how long each took
5. Click any bar → what triggered the change detection cycle

---

## 3.2 Source Maps and Browser Debugging

The Angular CLI generates source maps in dev mode. This means your browser can show you **the original TypeScript** even though it runs compiled JavaScript.

1. Open DevTools → **Sources** tab
2. Find `webpack://` or your project under the file tree
3. Navigate to `src/app/...` — these are your real `.ts` files
4. Click any line number to set a breakpoint
5. Trigger the code → execution pauses, you can inspect variables

```ts
// Or use the debugger statement
handleEmployeeAction(action: EmployeeAction) {
  debugger   // execution pauses here in DevTools
  switch (action.type) {
    case 'select': this.selectedId.set(action.employeeId); break
  }
}
```

---

## 3.3 Console Debugging Strategies

```ts
// Basic log
console.log('employees:', this.employees())
console.log('filtered count:', this.filteredEmployees().length)

// Table — great for arrays of objects
console.table(this.employees())

// Group related logs
console.group('Signal update')
console.log('old filter:', oldFilter)
console.log('new filter:', this.filter())
console.groupEnd()

// Track signal changes with effect()
constructor() {
  effect(() => {
    console.log('[DEBUG] employees changed:', this.employees().length)
    console.log('[DEBUG] filter:', this.filter())
  })
}

// Dev-only logging
if (!environment.production) {
  console.log('Debug:', someValue)
}
```

---

## 3.4 Common Angular Errors and Fixes

### Error 1 — "Can't bind to 'X' since it isn't a known property"

```
Error: Can't bind to 'employee' since it isn't a known property of 'app-employee-card'
```

**Cause:** The component isn't imported in the current component.

```ts
// ❌ EmployeeCardComponent is used in template but not imported
@Component({
  imports: [],   // missing EmployeeCardComponent
})
export class EmployeeListComponent {}

// ✅ Fix: add it to imports
@Component({
  imports: [EmployeeCardComponent],
})
export class EmployeeListComponent {}
```

---

### Error 2 — "NG0100: ExpressionChangedAfterItHasBeenCheckedError"

```
ERROR Error: NG0100: ExpressionChangedAfterItHasBeenCheckedError
```

**Cause:** A value changes after Angular has already checked it in development mode (Zone.js). Less common with Signals.

```ts
// ❌ Setting state in ngAfterViewInit (runs after check)
ngAfterViewInit() {
  this.title = 'New Title'   // triggers this error
}

// ✅ Wrap in setTimeout (defers to next tick)
ngAfterViewInit() {
  setTimeout(() => this.title.set('New Title'))
}

// ✅ Or better: use a signal and set it in ngOnInit
ngOnInit() {
  this.title.set('New Title')   // runs during check
}
```

---

### Error 3 — "NullInjectorError: No provider for X"

```
NullInjectorError: R3InjectorError: No provider for EmployeeService!
```

**Cause:** Service not provided anywhere.

```ts
// ❌ Service with no providedIn
@Injectable()
export class EmployeeService {}

// ✅ Fix: add providedIn: 'root'
@Injectable({ providedIn: 'root' })
export class EmployeeService {}

// Or provide in component if needed
@Component({
  providers: [EmployeeService],   // component-scoped instance
})
```

---

### Error 4 — "Cannot read properties of undefined"

```
TypeError: Cannot read properties of undefined (reading 'name')
```

**Cause:** Async data not yet loaded, or optional chain missing.

```ts
// ❌ employee might be null/undefined before API call returns
{{ employee.name }}

// ✅ Use @if to guard
@if (employee()) {
  <h3>{{ employee()!.name }}</h3>
}

// ✅ Or optional chaining in template
{{ employee()?.name }}
```

---

### Error 5 — Infinite loop / Maximum call stack exceeded

**Cause:** An `effect()` writes to a signal it also reads.

```ts
// ❌ effect reads AND writes the same signal → infinite loop
effect(() => {
  const employees = this.employees()
  this.employees.set(employees.filter(e => e.isActive))  // writes employees!
})

// ✅ Use computed() for derived state
activeEmployees = computed(() => this.employees().filter(e => e.isActive))
```

---

### Error 6 — Template binding to non-existent property

```
Property 'nme' does not exist on type 'Employee'
```

Angular templates are **type-checked** at compile time with `strictTemplates: true`. The error appears in the terminal during `ng serve`.

```html
<!-- ❌ typo -->
{{ employee().nme }}

<!-- ✅ correct -->
{{ employee().name }}
```

Enable strict templates in `tsconfig.json`:

```json
{
  "angularCompilerOptions": {
    "strictTemplates": true
  }
}
```

---

### Error 7 — `track` missing in `@for`

```
Error: @for loop must have a "track" expression
```

```html
<!-- ❌ Missing track -->
@for (emp of employees()) {
  <app-employee-card [employee]="emp" />
}

<!-- ✅ Always track by unique identifier -->
@for (emp of employees(); track emp.id) {
  <app-employee-card [employee]="emp" />
}

<!-- track $index only when items have no stable id -->
@for (item of list(); track $index) {
```

---

## 3.5 Angular Strict Mode — Your Best Friend

Angular's strict mode catches most errors at compile time, not at runtime.

```json
// tsconfig.json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true
  },
  "angularCompilerOptions": {
    "strictTemplates":         true,   // type-check templates
    "strictInjectionParameters": true, // catch DI issues early
    "strictInputAccessModifiers": true
  }
}
```

When `ng serve` shows a red error, read it — Angular errors are descriptive and usually point to the exact line.

---

## 3.6 Debugging Signals Specifically

```ts
import { effect } from '@angular/core'

// Log every change to every signal — great during development
constructor() {
  effect(() => {
    console.group('=== Signal state ===')
    console.log('employees:', this.employees().length)
    console.log('filter:', this.filter())
    console.log('search:', this.searchTerm())
    console.log('filtered:', this.filteredEmployees().length)
    console.groupEnd()
  })
}
```

Angular DevTools also shows computed signal values — click a component and look for `Computed` next to the property name.

---

## 3.7 Debugging Template Rendering

```html
<!-- Temporarily display signal values in the template -->
<pre style="background:#f0f0f0; padding:8px; font-size:12px">
Filter: {{ filter() }}
Search: {{ searchTerm() }}
Total: {{ employees().length }}
Filtered: {{ filteredEmployees().length }}
</pre>

<!-- Check if a section renders at all -->
@if (true) {
  DEBUG: This section renders
}
```

---

## 3.8 Debugging Checklist

```
1.  Read the terminal error in full — Angular errors are verbose but accurate.
2.  Is the component missing from imports array?
3.  Is the service missing providedIn or not in providers[]?
4.  Is the signal null/undefined? Add @if or optional chaining.
5.  Open Angular DevTools → check component properties and signal values.
6.  Add console.log() inside the method or effect() being debugged.
7.  Use debugger; to pause execution and inspect.
8.  Is strict templates enabled? It catches template bugs at compile time.
9.  Does the @for have a track expression?
10. Is an effect() accidentally writing to a signal it reads?
```

---

## Summary

| Tool | Best for |
|------|----------|
| Angular DevTools — Component tree | Inspect live inputs, signals, computed values |
| Angular DevTools — Profiler | Find slow change detection cycles |
| Terminal (`ng serve` output) | Compile-time errors, type errors in templates |
| `debugger` + Browser Sources | Step through TypeScript line by line |
| `effect()` with `console.log` | Track signal changes as they happen |
| Strict mode (`strictTemplates`) | Catch template bugs before they reach the browser |

**Next → Module 04: Directives Deep Dive**
