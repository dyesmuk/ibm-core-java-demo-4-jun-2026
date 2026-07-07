# Module 06 — Debugging React Apps

## Learning Objectives
- Use React DevTools to inspect the component tree, props, and state
- Apply systematic console debugging strategies
- Identify and fix the most common React bugs
- Implement Error Boundaries for graceful failure

---

## 6.1 React DevTools

The single most important debugging tool for React.

**Install:**
- Chrome → search "React Developer Tools" in Chrome Web Store
- Firefox → Firefox Add-ons

After installing, open DevTools (`F12`) — you'll see two new tabs: **Components** and **Profiler**.

### Components Tab

```
▼ App
  ▼ div
    ▼ EmployeeCard
        props
          employee: { id: 1, name: "Alice Johnson", ... }
          onRemove: ƒ()
        hooks
          State: [{ id:1, name:"Alice"... }, { id:2, ... }]   ← useState value
          State: "Engineering"                                  ← filter state
```

**What you can do:**
- Click any component → see its current **props** and **state** on the right panel
- **Edit state live** → click the pencil icon next to any value and change it
- **Search** for a component by name in the search bar
- **Highlight updates** → settings ⚙ → "Highlight updates when components render" → components flash on re-render

### Profiler Tab

1. Click record ●
2. Interact with your app (type, click, filter)
3. Stop recording
4. Each bar = one render; bar height = time taken
5. Click any bar → which component re-rendered and why

---

## 6.2 Console Strategies

```tsx
// Basic log
console.log('employees:', employees)
console.log('filter:', filter, '→ filtered count:', filtered.length)

// Table format — best for arrays of objects
console.table(employees)

// Grouped output
console.group('EmployeeCard render')
console.log('employee:', employee)
console.log('isActive:', employee.isActive)
console.groupEnd()

// Time an operation
console.time('filter')
const result = employees.filter(/* heavy operation */)
console.timeEnd('filter')   // prints "filter: 2.3ms"

// Dev-only log
if (import.meta.env.DEV) {
  console.log('[DEBUG]', someValue)
}
```

---

## 6.3 The `debugger` Statement

Pauses execution in DevTools at that exact line:

```tsx
const handleAdd = () => {
  debugger    // ← execution stops here when DevTools is open
  if (!newName.trim()) return
  // ... step through from here
}
```

Alternatively: click any line number in DevTools → Sources tab → sets a breakpoint there.

---

## 6.4 The Most Common React Bugs

### Bug 1 — State mutation (doesn't re-render)

```tsx
// ❌ Mutating state directly — React doesn't detect the change
const handleToggle = (id: number) => {
  const emp = employees.find(e => e.id === id)
  if (emp) emp.isActive = !emp.isActive    // mutates object in place
  setEmployees(employees)                   // same reference → React sees no change
}

// ✅ Create a new array with a new object
const handleToggle = (id: number) => {
  setEmployees(prev =>
    prev.map(e => e.id === id ? { ...e, isActive: !e.isActive } : e)
  )
}
```

### Bug 2 — Stale closure

```tsx
// ❌ All three calls read the same stale count
const handleTripleIncrement = () => {
  setCount(count + 1)   // count = 5
  setCount(count + 1)   // count still = 5
  setCount(count + 1)   // count still = 5
  // result: 6, not 8
}

// ✅ Functional update always reads the latest state
const handleTripleIncrement = () => {
  setCount(prev => prev + 1)   // prev = 5  → 6
  setCount(prev => prev + 1)   // prev = 6  → 7
  setCount(prev => prev + 1)   // prev = 7  → 8
}
```

### Bug 3 — `useEffect` infinite loop

```tsx
// ❌ No dependency array → runs after every render → triggers state update → renders again
useEffect(() => {
  setEmployees(processData(rawData))
})   // no [] → infinite loop

// ❌ Object/array literal in deps → new reference every render → infinite loop
useEffect(() => {
  fetchEmployees(options)
}, [options])   // options = {} defined inside the component

// ✅ Empty array → runs once on mount
useEffect(() => {
  fetchData()
}, [])

// ✅ Move the object inside the effect
useEffect(() => {
  const options = { page: 1 }
  fetchEmployees(options)
}, [])

// ✅ Stable primitive in deps
useEffect(() => {
  fetchEmployee(id)
}, [id])
```

### Bug 4 — Event handler called instead of passed

```tsx
// ❌ handleRemove(emp.id) is CALLED during render — returns undefined to onClick
<button onClick={handleRemove(emp.id)}>Remove</button>

// ✅ Wrap in arrow function to defer the call
<button onClick={() => handleRemove(emp.id)}>Remove</button>

// ✅ When no args needed — pass reference directly
<button onClick={handleClose}>Close</button>
```

### Bug 5 — Reading state immediately after setting it

```tsx
// ❌ State updates are asynchronous — count is still the old value on the next line
setCount(count + 1)
sendToServer(count)   // sends wrong (old) value

// ✅ Compute new value first, use it in both places
const newCount = count + 1
setCount(newCount)
sendToServer(newCount)

// ✅ Or react to the state change with useEffect
useEffect(() => {
  sendToServer(count)
}, [count])
```

### Bug 6 — Missing key / index as key

```tsx
// ❌ Missing key → warning + broken re-rendering
{employees.map(emp => <EmployeeCard employee={emp} />)}

// ❌ Index key → breaks when list reorders or is filtered
{employees.map((emp, i) => <EmployeeCard key={i} employee={emp} />)}

// ✅ Stable unique ID
{employees.map(emp => <EmployeeCard key={emp.id} employee={emp} />)}
```

### Bug 7 — The `{0 && ...}` gotcha

```tsx
// ❌ When count = 0, renders the number "0" on screen
{count && <p>You have {count} items</p>}

// ✅ Explicit boolean check
{count > 0 && <p>You have {count} items</p>}

// ✅ Or ternary
{count ? <p>You have {count} items</p> : null}
```

---

## 6.5 TypeScript as a Debugger

TypeScript catches whole categories of bugs before your code ever runs:

```tsx
// TS error: Property 'nme' does not exist on type 'Employee'
<p>{employee.nme}</p>          // ← red squiggle in VS Code

// TS error: Argument of type 'string' is not assignable to type 'number'
setCount('five')

// TS error: Property 'onRemove' is missing
<EmployeeCard employee={emp} />   // ← if onRemove is required in props interface
```

Trust the red squiggles — they're telling you the truth.

---

## 6.6 Error Boundaries

By default, an unhandled error in a component unmounts the **entire app** — the user sees a blank white screen. Error Boundaries catch the error and show a fallback UI instead.

Error Boundaries must be **class components** — this is the one case where class components are still required in 2025.

```tsx
// src/components/ErrorBoundary.tsx
import { Component, ReactNode } from 'react'
import type { ErrorInfo } from 'react'

interface Props {
  children: ReactNode
  fallback?: ReactNode
}

interface State {
  hasError: boolean
  error: Error | null
}

class ErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false, error: null }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('ErrorBoundary caught:', error.message)
    console.error('Component stack:', info.componentStack)
    // In production: Sentry.captureException(error)
  }

  handleRetry = () => this.setState({ hasError: false, error: null })

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) return this.props.fallback

      return (
        <div role="alert">
          <h3>Something went wrong</h3>
          <p>{this.state.error?.message}</p>
          <button onClick={this.handleRetry}>Try Again</button>
        </div>
      )
    }

    return this.props.children
  }
}

export default ErrorBoundary
```

### Using Error Boundaries

```tsx
// Global catch-all in main.tsx
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ErrorBoundary>
      <App />
    </ErrorBoundary>
  </StrictMode>
)

// Per-section — isolate failures so one section can fail without killing the rest
function Dashboard() {
  return (
    <div>
      <ErrorBoundary fallback={<p>Stats unavailable</p>}>
        <StatsDashboard />
      </ErrorBoundary>
      <ErrorBoundary fallback={<p>Employee list failed to load</p>}>
        <EmployeeList />
      </ErrorBoundary>
    </div>
  )
}
```

> **What Error Boundaries do NOT catch:** errors inside event handlers, async code, or the Error Boundary component itself. Use regular `try/catch` for those.

---

## 6.7 Debugging Checklist

When something isn't working, go through this in order:

```
1.  Is there a console error or warning? Read it carefully — the message is usually accurate.
2.  Open React DevTools → Components → check props and state on the suspect component.
3.  Add console.log() right before the suspicious line and right after.
4.  Is state being mutated directly? Use spread/filter/map to create new objects.
5.  Is the useEffect dependency array correct? Too few deps → stale data. Too many → infinite loop.
6.  Is an event handler being called instead of passed (onClick={fn()} vs onClick={fn})?
7.  Does TypeScript show a red squiggle? Fix it.
8.  Check the Profiler — is an unrelated component re-rendering constantly?
9.  Is there a missing or bad key in a list?
10. Add a debugger statement and step through line by line.
```

---

## Summary

| Tool | Best for |
|------|----------|
| React DevTools — Components | Inspect live props/state, edit values |
| React DevTools — Profiler | Find unnecessary re-renders |
| `console.table(arr)` | Debug arrays of objects |
| `debugger` | Pause execution, step through |
| Error Boundary | Catch render errors, show user-friendly fallback |
| TypeScript squiggles | Catch type errors before runtime |

**Next → Module 07: Diving Deeper into Components & React Internals**
