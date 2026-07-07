# Module 07 — Diving Deeper into Components & React Internals

## Learning Objectives
- Master `useEffect`, `useRef`, `useCallback`, `useMemo`
- Understand when and why React re-renders
- Prevent unnecessary re-renders with `React.memo`
- Build reusable custom hooks
- Share state across the tree with Context API
- EMS: `useEmployees` custom hook, theme context

---

## 7.1 Component Lifecycle — Function Component View

```
MOUNT                    UPDATE                   UNMOUNT
Component renders        Props or state           Component removed
to DOM for first time    changed → re-renders     from DOM
       ↓                        ↓                        ↓
useEffect(fn, [])        useEffect(fn, [dep])     Cleanup function
runs after mount         runs on dep change       runs on unmount
```

---

## 7.2 `useEffect` — In Depth

Runs **after** the browser paints. This is where side effects live: data fetching, subscriptions, DOM manipulation, timers.

### Dependency array — four modes

```tsx
// Mode 1 — No array: runs after EVERY render
useEffect(() => {
  document.title = `EMS — ${employees.length} employees`
})

// Mode 2 — Empty array: runs ONCE after mount only
useEffect(() => {
  loadSavedPreferences()
}, [])

// Mode 3 — With deps: runs when any listed dep changes
useEffect(() => {
  filterAndSortEmployees(filter, sortKey)
}, [filter, sortKey])

// Mode 4 — Cleanup returned: runs before next effect and on unmount
useEffect(() => {
  const handler = (e: KeyboardEvent) => {
    if (e.key === 'Escape') closeModal()
  }
  document.addEventListener('keydown', handler)
  return () => document.removeEventListener('keydown', handler)  // cleanup
}, [])
```

### Cleanup — critical patterns

```tsx
// Cancel in-flight fetch when component unmounts
useEffect(() => {
  const controller = new AbortController()

  fetch('/api/employees', { signal: controller.signal })
    .then(r => r.json())
    .then(setEmployees)
    .catch(err => {
      if (err.name !== 'AbortError') console.error(err)
    })

  return () => controller.abort()   // cancel if component unmounts mid-request
}, [])

// Clear interval on unmount
useEffect(() => {
  const id = setInterval(() => setTick(t => t + 1), 1000)
  return () => clearInterval(id)
}, [])
```

### What you MUST NOT do in useEffect

```tsx
// ❌ useEffect callback cannot be async
useEffect(async () => {      // never do this
  const data = await fetchData()
  setEmployees(data)
}, [])

// ✅ Wrap async logic in an inner function
useEffect(() => {
  const load = async () => {
    const data = await fetchData()
    setEmployees(data)
  }
  load()
}, [])
```

---

## 7.3 `useRef`

`useRef` stores a mutable value that:
1. Persists across renders (not reset on re-render)
2. Does **NOT** trigger a re-render when changed
3. Can hold a DOM element reference

### DOM access

```tsx
function SearchBar() {
  const inputRef = useRef<HTMLInputElement>(null)

  // Auto-focus on mount
  useEffect(() => {
    inputRef.current?.focus()
  }, [])

  // Focus when '/' is pressed anywhere on the page
  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.key === '/' && document.activeElement !== inputRef.current) {
        e.preventDefault()
        inputRef.current?.focus()
      }
    }
    document.addEventListener('keydown', handler)
    return () => document.removeEventListener('keydown', handler)
  }, [])

  return <input ref={inputRef} placeholder="Search employees… (press /)" />
}
```

### Mutable value without causing re-renders

```tsx
// Use case: debounce auto-save — store timer ID without triggering re-render
function AutoSave({ data }: { data: Employee[] }) {
  const timerRef = useRef<ReturnType<typeof setTimeout>>()

  useEffect(() => {
    clearTimeout(timerRef.current)
    timerRef.current = setTimeout(() => {
      localStorage.setItem('ems_employees', JSON.stringify(data))
    }, 500)
    return () => clearTimeout(timerRef.current)
  }, [data])

  return null
}

// Use case: track previous value
function useEmployeeCount(count: number) {
  const prevRef = useRef(count)
  useEffect(() => { prevRef.current = count })
  return prevRef.current   // previous render's count
}
```

---

## 7.4 `useMemo` — Cache Expensive Computations

`useMemo` memoizes the result of a computation — only recalculates when dependencies change.

```tsx
// Without useMemo — recalculates on EVERY render, even unrelated state changes
const stats = {
  total:     employees.length,
  active:    employees.filter(e => e.isActive).length,
  avgSalary: employees.reduce((s, e) => s + e.salary, 0) / employees.length,
}

// With useMemo — only recalculates when employees changes
const stats = useMemo(() => ({
  total:       employees.length,
  active:      employees.filter(e => e.isActive).length,
  avgSalary:   Math.round(employees.reduce((s, e) => s + e.salary, 0) / employees.length) || 0,
  departments: [...new Set(employees.map(e => e.department))].sort(),
}), [employees])

// Filtered + sorted list — expensive when list is large
const filtered = useMemo(() =>
  employees
    .filter(e => filter === 'All' || e.department === filter)
    .filter(e => showInactive || e.isActive)
    .filter(e => e.name.toLowerCase().includes(search.toLowerCase()))
    .sort((a, b) => a.name.localeCompare(b.name)),
  [employees, filter, showInactive, search]
)
```

> **When to use:** When you have a genuinely expensive operation on a large dataset. Don't memoize everything — `useMemo` itself has overhead.

---

## 7.5 `useCallback` — Stable Function References

`useCallback` returns a memoized function. The same function reference is returned between renders unless dependencies change.

```tsx
// Without useCallback — new function reference every render
const handleRemove = (id: number) => {
  setEmployees(prev => prev.filter(e => e.id !== id))
}
// Child components that receive this as a prop see a "new" prop every render

// With useCallback — same reference between renders
const handleRemove = useCallback((id: number) => {
  setEmployees(prev => prev.filter(e => e.id !== id))
}, [])   // no deps — setEmployees is stable
```

> **When `useCallback` actually matters:** Only when the function is passed as a prop to a `React.memo` component. Without `React.memo` on the child, `useCallback` achieves nothing.

---

## 7.6 `React.memo` — Skip Unnecessary Re-renders

By default, when a parent re-renders, **all its children re-render** even if their props didn't change. `React.memo` wraps a component so it only re-renders when its props actually change.

```tsx
// Without React.memo — re-renders whenever parent re-renders
function EmployeeCard({ employee, onRemove }: Props) {
  return <div>{employee.name}</div>
}

// With React.memo — skips re-render if employee and onRemove are the same references
const EmployeeCard = React.memo(function EmployeeCard({ employee, onRemove }: Props) {
  console.log('EmployeeCard rendered:', employee.name)
  return <div>{employee.name}</div>
})

export default EmployeeCard
```

For `React.memo` to work with function props, the function must be stable — use `useCallback`:

```tsx
// In App.tsx
const handleRemove = useCallback((id: number) => {
  setEmployees(prev => prev.filter(e => e.id !== id))
}, [])

// Now: typing in the search input → App re-renders → handleRemove is same ref
// → React.memo sees same props → EmployeeCard skips re-render ✅
```

---

## 7.7 Custom Hooks

A custom hook is a **function named with `use`** that calls other hooks inside. It's how you extract stateful logic and reuse it across components.

```tsx
// src/hooks/useLocalStorage.ts
import { useState, useEffect } from 'react'

function useLocalStorage<T>(key: string, initialValue: T) {
  const [value, setValue] = useState<T>(() => {
    try {
      const stored = localStorage.getItem(key)
      return stored ? (JSON.parse(stored) as T) : initialValue
    } catch {
      return initialValue
    }
  })

  useEffect(() => {
    try {
      localStorage.setItem(key, JSON.stringify(value))
    } catch {
      console.warn(`useLocalStorage: couldn't save "${key}"`)
    }
  }, [key, value])

  return [value, setValue] as const
}

export default useLocalStorage
```

```tsx
// src/hooks/useDebounce.ts
import { useState, useEffect } from 'react'

function useDebounce<T>(value: T, delayMs: number): T {
  const [debounced, setDebounced] = useState<T>(value)

  useEffect(() => {
    const timer = setTimeout(() => setDebounced(value), delayMs)
    return () => clearTimeout(timer)
  }, [value, delayMs])

  return debounced
}

export default useDebounce
```

### `useEmployees` — EMS domain hook

This is the most impactful refactor we'll do. All employee logic leaves `App.tsx` and goes into a single hook.

```tsx
// src/hooks/useEmployees.ts
import { useState, useMemo, useCallback } from 'react'
import type { Employee, Department } from '../types'
import { INITIAL_EMPLOYEES } from '../data/employees'

let nextId = INITIAL_EMPLOYEES.length + 1

export interface UseEmployeesReturn {
  employees:      Employee[]
  filtered:       Employee[]
  filter:         Department
  search:         string
  showInactive:   boolean
  stats:          { total: number; active: number; departments: number; avgSalary: number }
  setFilter:      (d: Department) => void
  setSearch:      (s: string) => void
  setShowInactive:(v: boolean) => void
  addEmployee:    (name: string, department: Exclude<Department, 'All'>) => void
  removeEmployee: (id: number) => void
  toggleActive:   (id: number) => void
}

export function useEmployees(): UseEmployeesReturn {
  const [employees,    setEmployees]    = useState<Employee[]>(INITIAL_EMPLOYEES)
  const [filter,       setFilter]       = useState<Department>('All')
  const [search,       setSearch]       = useState('')
  const [showInactive, setShowInactive] = useState(true)

  const filtered = useMemo(() =>
    employees
      .filter(e => filter === 'All' || e.department === filter)
      .filter(e => showInactive || e.isActive)
      .filter(e => e.name.toLowerCase().includes(search.toLowerCase()))
      .sort((a, b) => a.name.localeCompare(b.name)),
    [employees, filter, showInactive, search]
  )

  const stats = useMemo(() => ({
    total:       employees.length,
    active:      employees.filter(e => e.isActive).length,
    departments: new Set(employees.map(e => e.department)).size,
    avgSalary:   Math.round(employees.reduce((s, e) => s + e.salary, 0) / employees.length) || 0,
  }), [employees])

  const addEmployee = useCallback((name: string, department: Exclude<Department, 'All'>) => {
    setEmployees(prev => [...prev, {
      id: nextId++,
      name: name.trim(),
      email: `${name.toLowerCase().replace(/\s+/g, '.')}@ibm.com`,
      department,
      salary: 70000,
      isActive: true,
      joinDate: new Date().toISOString().split('T')[0],
    }])
  }, [])

  const removeEmployee = useCallback((id: number) => {
    setEmployees(prev => prev.filter(e => e.id !== id))
  }, [])

  const toggleActive = useCallback((id: number) => {
    setEmployees(prev =>
      prev.map(e => e.id === id ? { ...e, isActive: !e.isActive } : e)
    )
  }, [])

  return {
    employees, filtered, filter, search, showInactive, stats,
    setFilter, setSearch, setShowInactive,
    addEmployee, removeEmployee, toggleActive,
  }
}
```

```tsx
// App.tsx is now clean — just UI, zero business logic
import { useEmployees } from './hooks/useEmployees'

function App() {
  const { filtered, filter, stats, setFilter, addEmployee, removeEmployee } = useEmployees()
  // render only
}
```

---

## 7.8 Context API — Share State Without Prop Drilling

**Prop drilling:** passing a prop through many intermediate layers just to get it to a deeply nested child.

```
App (owns theme)
  └── Layout (passes theme down)
        └── Sidebar (passes theme down)
              └── NavItem (finally uses theme)
```

Context lets any component in the tree read shared data directly.

### Create a Theme Context

```tsx
// src/context/ThemeContext.tsx
import { createContext, useContext, useState } from 'react'
import type { ReactNode } from 'react'

type Theme = 'light' | 'dark'

interface ThemeContextValue {
  theme: Theme
  toggleTheme: () => void
}

const ThemeContext = createContext<ThemeContextValue | null>(null)

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<Theme>('light')
  const toggleTheme = () => setTheme(t => t === 'light' ? 'dark' : 'light')

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  )
}

// Custom hook — always use this, not useContext directly
export function useTheme(): ThemeContextValue {
  const ctx = useContext(ThemeContext)
  if (!ctx) throw new Error('useTheme must be used inside <ThemeProvider>')
  return ctx
}
```

```tsx
// src/main.tsx — wrap the app
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider>
      <App />
    </ThemeProvider>
  </StrictMode>
)
```

```tsx
// Any component anywhere can now read the theme
import { useTheme } from '../context/ThemeContext'

function Header() {
  const { theme, toggleTheme } = useTheme()
  return (
    <header data-theme={theme}>
      <span>IBM EMS</span>
      <button onClick={toggleTheme}>{theme === 'dark' ? '☀️' : '🌙'}</button>
    </header>
  )
}
```

---

## 7.9 Final Folder Structure After Module 07

```
src/
├── components/
│   ├── EmployeeCard.tsx
│   ├── EmployeeCard.module.css
│   └── ErrorBoundary.tsx
├── context/
│   └── ThemeContext.tsx
├── data/
│   └── employees.ts
├── hooks/
│   ├── useDebounce.ts
│   ├── useEmployees.ts
│   └── useLocalStorage.ts
├── types/
│   └── index.ts
├── App.tsx
├── index.css
└── main.tsx
```

---

## Summary

| Hook / Pattern | When to use |
|----------------|-------------|
| `useEffect(fn, [])` | Run once on mount |
| `useEffect(fn, [dep])` | React to specific value changes |
| `useEffect` return | Clean up listeners, timers, in-flight requests |
| `useRef` | DOM access, stable value that doesn't trigger re-render |
| `useMemo` | Expensive derived computation on large data |
| `useCallback` | Stable function reference for `React.memo` children |
| `React.memo` | Skip re-render when props haven't changed |
| Custom hook | Extract + reuse stateful logic across components |
| Context API | Share state without prop drilling |

**Next → Module 08: HTTP & Ajax with Axios**
