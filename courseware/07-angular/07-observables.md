# Module 07 — Understanding Observables & RxJS

## Learning Objectives
- Understand what an Observable is and how it differs from a Promise
- Use core RxJS operators effectively
- Subscribe and unsubscribe safely
- Convert between Observables and Signals
- Use the `async` pipe
- EMS: reactive search, autocomplete, real-time updates

---

## 7.1 Observable vs Promise

Both handle async operations — but they behave differently.

| | Promise | Observable |
|--|---------|-----------|
| Values | One | Zero, one, or many |
| Lazy | ❌ Starts immediately | ✅ Starts only when subscribed |
| Cancellable | ❌ | ✅ (via `unsubscribe`) |
| Operators | `.then()`, `.catch()` | 100+ RxJS operators |
| Angular HTTP | Returns Observable | ✅ |
| Route params | — | Observable stream |
| WebSocket | — | Observable stream |

```ts
// Promise — one value, starts immediately
const promise = fetch('/api/employees').then(r => r.json())
// Already running. Can't cancel.

// Observable — lazy, cancellable, can emit multiple values
import { interval } from 'rxjs'
const ticks$ = interval(1000)   // not running yet
const sub = ticks$.subscribe(n => console.log(n))  // starts now
// 0, 1, 2, 3 ... every second
sub.unsubscribe()   // stops the timer, cleans up
```

> **Convention:** Observable variables end with `$` — `employees$`, `search$`.

---

## 7.2 Creating Observables

```ts
import { Observable, of, from, interval, timer, fromEvent } from 'rxjs'

// of — emit fixed values and complete
const name$ = of('Alice', 'Bob', 'Carol')
name$.subscribe(name => console.log(name))
// Alice, Bob, Carol, (complete)

// from — convert Promise, Array, Iterable to Observable
const arr$ = from([1, 2, 3])
const promise$ = from(fetch('/api/data'))

// interval — emit incrementing number every N ms
const tick$ = interval(1000)   // 0, 1, 2, 3...

// timer(delay, interval) — emit after delay, then repeat
const delayed$ = timer(2000, 1000)   // waits 2s, then 0, 1, 2...

// fromEvent — DOM events as Observable
const click$ = fromEvent<MouseEvent>(document, 'click')
const input$ = fromEvent<InputEvent>(inputElement, 'input')

// Custom Observable
const custom$ = new Observable<string>(observer => {
  observer.next('First value')
  observer.next('Second value')
  setTimeout(() => {
    observer.next('After 1 second')
    observer.complete()   // no more values
  }, 1000)

  // Teardown logic (runs on unsubscribe)
  return () => {
    console.log('Cleaned up')
  }
})
```

---

## 7.3 Subscribing and Unsubscribing

```ts
import { Component, OnInit, OnDestroy } from '@angular/core'
import { Subscription } from 'rxjs'
import { interval } from 'rxjs'

@Component({ /* ... */ })
export class TimerComponent implements OnInit, OnDestroy {
  private sub = new Subscription()   // container for multiple subscriptions

  ngOnInit() {
    const timer$ = interval(1000)

    // Add subscriptions to the container
    this.sub.add(
      timer$.subscribe(n => console.log('Tick:', n))
    )
  }

  ngOnDestroy() {
    this.sub.unsubscribe()   // clean up everything — prevents memory leaks
  }
}
```

> **Best practice with signals (Angular 16+):** Use `takeUntilDestroyed()` — automatically unsubscribes when the component destroys. No manual `ngOnDestroy` needed.

```ts
import { takeUntilDestroyed } from '@angular/core/rxjs-interop'
import { interval } from 'rxjs'

@Component({ /* ... */ })
export class TimerComponent {
  constructor() {
    // Automatically unsubscribes when this component destroys
    interval(1000)
      .pipe(takeUntilDestroyed())
      .subscribe(n => console.log('Tick:', n))
  }
}
```

---

## 7.4 Core RxJS Operators

Operators transform Observable streams. They are pure functions — they never modify the source.

### `map` — transform each value

```ts
import { map } from 'rxjs/operators'

employees$.pipe(
  map(employees => employees.filter(e => e.isActive))
)

// Or transform the type
employees$.pipe(
  map(employees => employees.length)  // Observable<Employee[]> → Observable<number>
)
```

### `filter` — pass only matching values

```ts
import { filter } from 'rxjs/operators'

clicks$.pipe(
  filter((event: MouseEvent) => event.button === 0)  // left-click only
)
```

### `switchMap` — replace inner Observable on each emission (most used!)

Cancel the previous inner Observable and start a new one. Essential for search-as-you-type.

```ts
import { switchMap } from 'rxjs/operators'

// Every time searchTerm changes, cancel old request, start new one
searchTerm$.pipe(
  switchMap(term => this.employeeService.search(term))
)
// If user types "a", "al", "ali" fast:
// Request for "a" → cancelled
// Request for "al" → cancelled
// Request for "ali" → completes ✅
```

### `mergeMap` — run inner Observables concurrently

```ts
import { mergeMap } from 'rxjs/operators'

// All delete requests run simultaneously
employeeIds$.pipe(
  mergeMap(id => this.employeeService.delete$(id))
)
```

### `concatMap` — run inner Observables sequentially

```ts
import { concatMap } from 'rxjs/operators'

// Save employees one by one, in order
toSave$.pipe(
  concatMap(employee => this.employeeService.save$(employee))
)
```

### `debounceTime` — wait for pause before emitting

```ts
import { debounceTime } from 'rxjs/operators'

searchInput$.pipe(
  debounceTime(300)   // wait 300ms after user stops typing
)
```

### `distinctUntilChanged` — only emit when value actually changes

```ts
import { distinctUntilChanged } from 'rxjs/operators'

searchInput$.pipe(
  debounceTime(300),
  distinctUntilChanged()   // don't re-search if value is the same
)
```

### `catchError` — handle errors in the stream

```ts
import { catchError } from 'rxjs/operators'
import { EMPTY, of } from 'rxjs'

employees$.pipe(
  catchError(err => {
    console.error(err)
    return of([])   // return empty array instead of crashing
    // return EMPTY  // complete without emitting
  })
)
```

### `tap` — side effects without modifying the stream

```ts
import { tap } from 'rxjs/operators'

employees$.pipe(
  tap(employees => console.log('Received:', employees.length)),
  map(employees => employees.filter(e => e.isActive)),
  tap(active => console.log('Active:', active.length))
)
```

### `combineLatest` — combine multiple streams

```ts
import { combineLatest } from 'rxjs'

// Emits whenever either employees OR filter changes
combineLatest([employees$, filter$]).pipe(
  map(([employees, filter]) =>
    filter === 'All' ? employees : employees.filter(e => e.department === filter)
  )
)
```

---

## 7.5 `Subject` — Observable You Can Push To

A `Subject` is both an Observable and an Observer — you can both subscribe to it and push values into it.

```ts
import { Subject, BehaviorSubject } from 'rxjs'

// Subject — no initial value, late subscribers miss past emissions
const action$ = new Subject<string>()
action$.subscribe(a => console.log('Action:', a))
action$.next('delete')    // 'Action: delete'
action$.next('create')    // 'Action: create'

// BehaviorSubject — has initial value, late subscribers get the current value
const filter$ = new BehaviorSubject<string>('All')
filter$.subscribe(f => console.log('Filter:', f))   // immediately logs 'All'
filter$.next('Engineering')   // logs 'Engineering'

// Read current value without subscribing
console.log(filter$.value)   // 'Engineering'
```

---

## 7.6 Converting Between Observables and Signals

Angular 16+ provides utilities in `@angular/core/rxjs-interop`:

```ts
import { toSignal, toObservable } from '@angular/core/rxjs-interop'
import { inject, signal }         from '@angular/core'
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators'
import { EmployeeService } from '../employee.service'

@Component({ /* ... */ })
export class EmployeeSearchComponent {
  private employeeService = inject(EmployeeService)

  // Signal → Observable → (operators) → Signal
  searchTerm = signal<string>('')

  // Convert signal to Observable so we can use RxJS operators
  private searchTerm$ = toObservable(this.searchTerm)

  // Apply operators, then convert back to Signal
  searchResults = toSignal(
    this.searchTerm$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term =>
        term.length >= 2
          ? this.employeeService.search$(term)
          : of([])
      ),
      catchError(() => of([]))
    ),
    { initialValue: [] as Employee[] }   // required — signal needs initial value
  )
}
```

```html
<!-- Template uses the signal directly — no async pipe needed -->
@for (emp of searchResults(); track emp.id) {
  <app-employee-card [employee]="emp" />
}
```

---

## 7.7 The `async` Pipe

For templates that work with Observables directly (without converting to signals):

```ts
@Component({
  imports: [AsyncPipe, NgFor],
  template: `
    @if (employees$ | async; as employees) {
      @for (emp of employees; track emp.id) {
        <app-employee-card [employee]="emp" />
      }
    }
  `
})
export class EmployeeListComponent {
  employees$ = this.employeeService.getAll$()
}
```

**`async` pipe advantages:**
- Subscribes automatically when component renders
- Unsubscribes automatically when component destroys (no memory leak)
- Triggers change detection when new value arrives

> **With signals:** You rarely need `async` pipe — signals in templates work without it.

---

## 7.8 EMS — Reactive Search with Signals + RxJS

```ts
// src/app/employees/employee-search/employee-search.component.ts
import { Component, inject, signal }      from '@angular/core'
import { toSignal, toObservable }          from '@angular/core/rxjs-interop'
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators'
import { of }                              from 'rxjs'
import { EmployeeService }                 from '../employee.service'
import { FormsModule }                     from '@angular/forms'
import { EmployeeCardComponent }           from '../employee-card/employee-card.component'

@Component({
  selector:   'app-employee-search',
  standalone: true,
  imports:    [FormsModule, EmployeeCardComponent],
  templateUrl: './employee-search.component.html',
})
export class EmployeeSearchComponent {
  private employeeService = inject(EmployeeService)

  searchTerm = signal('')

  results = toSignal(
    toObservable(this.searchTerm).pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term =>
        term.trim().length >= 2
          ? of(this.employeeService.search(term))  // local search
          : of([])
      )
    ),
    { initialValue: [] as Employee[] }
  )

  isSearching = signal(false)
}
```

```html
<!-- employee-search.component.html -->
<input
  [(ngModel)]="searchTerm"
  placeholder="Search employees (min 2 characters)..."
/>

@if (searchTerm().length >= 2) {
  <p>{{ results().length }} results for "{{ searchTerm() }}"</p>
  @for (emp of results(); track emp.id) {
    <app-employee-card [employee]="emp" />
  } @empty {
    <p>No employees match your search.</p>
  }
}
```

---

## Summary

| Concept | Key points |
|---------|-----------|
| Observable | Lazy, cancellable, 0-N values |
| `subscribe()` | Starts the Observable |
| `unsubscribe()` | Stops and cleans up |
| `takeUntilDestroyed()` | Auto-unsubscribe on component destroy |
| `switchMap` | Cancel old, start new — search/navigation |
| `debounceTime` | Wait for pause before acting |
| `combineLatest` | Combine multiple streams |
| `BehaviorSubject` | Observable with current value |
| `toSignal()` | Observable → Signal |
| `toObservable()` | Signal → Observable |
| `async` pipe | Subscribe in template, auto-unsubscribe |

**Next → Module 08: Handling Forms in Angular**
