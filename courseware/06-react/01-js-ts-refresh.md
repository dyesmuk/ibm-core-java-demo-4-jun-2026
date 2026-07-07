# Module 01 — JavaScript & TypeScript Refresh

## Learning Objectives
- Recall the ES6+ patterns React uses every single day
- Understand TypeScript type annotations as used in React code
- Know destructuring, spread, async/await cold

> **Skip or skim** this module if you're already writing ES2022+ JavaScript and basic TypeScript daily.

---

## 1.1 `const` and `let` — Forget `var` Exists

```ts
const MAX_EMPLOYEES = 100   // block-scoped, can't be reassigned
let count = 0               // block-scoped, can be reassigned
count = 1                   // ✅

// const with objects — binding can't change, but the object can
const user = { name: 'Alice' }
user.name = 'Bob'           // ✅ mutating is fine
user = {}                   // ❌ reassigning the variable is not
```

**React rule:** Use `const` for everything. `let` only in loops or when you know you'll reassign. Never `var`.

---

## 1.2 Arrow Functions

```ts
// Traditional
function add(a: number, b: number): number { return a + b }

// Arrow — same behaviour
const add = (a: number, b: number): number => a + b

// Single param — parens optional, but keep them for consistency
const double = (n: number) => n * 2

// No params
const greet = () => 'Hello!'

// Returning an object — wrap in parens to avoid {} being read as a code block
const makeUser = (name: string) => ({ name, active: true })
```

**React uses arrow functions constantly:**

```tsx
// Event handlers
<button onClick={() => console.log('clicked')}>Click</button>

// Array methods in JSX
{employees.map(emp => <EmployeeCard key={emp.id} employee={emp} />)}

// useEffect callbacks
useEffect(() => { fetchData() }, [])

// Functional state updates
setCount(prev => prev + 1)
```

---

## 1.3 Destructuring

Extract values from objects and arrays into named variables — the most-used pattern in React props.

### Object destructuring

```ts
const employee = {
  id: 1,
  name: 'Alice Johnson',
  department: 'Engineering',
  salary: 95000,
}

// Without destructuring
const name = employee.name
const dept = employee.department

// With destructuring
const { name, department } = employee

// Rename while destructuring
const { name: fullName, department: dept } = employee

// Default value (used when the property is undefined)
const { name, role = 'employee' } = employee

// Nested
const employee2 = { ...employee, address: { city: 'Bengaluru' } }
const { address: { city } } = employee2   // city → 'Bengaluru'

// In function parameters — how React props work
function EmployeeCard({ name, department, salary }: EmployeeCardProps) {
  return <p>{name} — {department}</p>
}
```

### Array destructuring

```ts
const colors = ['red', 'green', 'blue']
const [first, second] = colors    // first='red', second='green'

// Skip elements
const [, , third] = colors        // third='blue'

// useState ALWAYS uses array destructuring
const [count, setCount] = useState(0)
//     ↑ value  ↑ setter function
```

---

## 1.4 Spread Operator

```ts
// Arrays
const a = [1, 2, 3]
const b = [4, 5, 6]
const merged = [...a, ...b]       // [1,2,3,4,5,6]
const copy   = [...a]             // shallow copy

// Objects
const defaults = { theme: 'light', lang: 'en' }
const overrides = { lang: 'hi' }
const config = { ...defaults, ...overrides }
// → { theme: 'light', lang: 'hi' }  (right side wins on conflicts)
```

**Most common React state update patterns:**

```ts
// Update one field in an object
setEmployee(prev => ({ ...prev, name: 'Bob' }))

// Add item to array
setList(prev => [...prev, newItem])

// Remove item from array
setList(prev => prev.filter(item => item.id !== id))

// Update one item in array
setList(prev => prev.map(item =>
  item.id === id ? { ...item, isActive: false } : item
))
```

### Rest parameters

```ts
// Collect remaining into array
function sum(first: number, ...rest: number[]) {
  return rest.reduce((acc, n) => acc + n, first)
}
sum(1, 2, 3, 4)   // 10

// Common in React: separate "own" props from pass-through props
function Wrapper({ className, ...rest }: Props) {
  return <div className={`wrapper ${className}`} {...rest} />
}
```

---

## 1.5 Template Literals

```ts
const name = 'Alice'
const salary = 95000

// Old way
'Hello, ' + name + '! Salary: $' + salary.toLocaleString()

// Template literal — always prefer this
`Hello, ${name}! Salary: $${salary.toLocaleString()}`

// Any expression works inside ${}
`${count > 1 ? 'employees' : 'employee'}`
`${new Date().getFullYear()}`
```

---

## 1.6 Array Methods — The Backbone of List Rendering

```ts
const employees = [
  { id: 1, name: 'Alice', dept: 'Engineering', salary: 95000, isActive: true },
  { id: 2, name: 'Bob',   dept: 'Marketing',   salary: 72000, isActive: true },
  { id: 3, name: 'Carol', dept: 'Engineering', salary: 88000, isActive: false },
]

// map — transform every element, return new array of same length
employees.map(e => e.name)            // ['Alice','Bob','Carol']

// filter — keep elements matching condition, return shorter array
employees.filter(e => e.isActive)     // [Alice, Bob]

// find — first matching element or undefined
employees.find(e => e.id === 2)       // Bob's object

// findIndex — index of first match or -1
employees.findIndex(e => e.id === 2)  // 1

// some / every
employees.some(e => e.salary > 90000)   // true (Alice)
employees.every(e => e.isActive)        // false (Carol)

// reduce — accumulate to single value
employees.reduce((sum, e) => sum + e.salary, 0)   // 255000

// sort — always copy first (sort mutates!)
[...employees].sort((a, b) => a.name.localeCompare(b.name))
```

---

## 1.7 Modules — Import and Export

Every React file is a module.

```ts
// Named exports
export const formatSalary = (n: number) => `$${n.toLocaleString()}`
export const getInitials  = (name: string) => name.split(' ').map(w => w[0]).join('')

// Import named exports
import { formatSalary, getInitials } from './utils/formatters'
import { formatSalary as fmt }        from './utils/formatters'  // rename

// Default export — one per file
export default function EmployeeCard() { /* ... */ }

// Import default (any name works)
import EmployeeCard from './components/EmployeeCard'

// Barrel re-exports (clean public API for a folder)
// src/components/index.ts
export { default as EmployeeCard } from './EmployeeCard'
export { default as EmployeeList } from './EmployeeList'

// Usage
import { EmployeeCard, EmployeeList } from './components'
```

---

## 1.8 Async / Await

API calls in React are async — this must be second nature.

```ts
// ❌ This does NOT wait for the server
const data = fetch('/api/employees')
console.log(data)   // Promise { <pending> } — NOT the data!

// ✅ async/await — reads like synchronous code
async function loadEmployees() {
  try {
    const response = await fetch('/api/employees')
    if (!response.ok) throw new Error(`HTTP ${response.status}`)
    const data = await response.json()
    return data
  } catch (error) {
    console.error('Failed to load:', error)
    throw error
  }
}
```

**In React's `useEffect`:**

```tsx
useEffect(() => {
  // useEffect callback cannot be async itself
  // → create an async inner function and call it
  const load = async () => {
    const data = await loadEmployees()
    setEmployees(data)
  }
  load()
}, [])
```

---

## 1.9 Optional Chaining and Nullish Coalescing

```ts
// Optional chaining ?. — safe property access without crashing
const city = employee?.address?.city        // undefined if any part is null/undefined
const first = employees?.[0]               // undefined if array is null/undefined

// Nullish coalescing ?? — default only for null or undefined (NOT for 0 or "")
const role  = employee.role ?? 'viewer'    // 'viewer' only if role is null/undefined
const count = employee.count ?? 0          // keeps 0 if count is 0

// Combine them
const city = user?.address?.city ?? 'Unknown'
```

---

## 1.10 TypeScript Essentials for React

### Basic types

```ts
const name: string    = 'Alice'
const age: number     = 30
const active: boolean = true
const ids: number[]   = [1, 2, 3]
```

### Interfaces — preferred for objects and props

```ts
interface Employee {
  id: number
  name: string
  email: string
  department: string
  salary: number
  isActive: boolean
  joinDate: string
  address?: {          // optional — may be undefined
    city: string
    country: string
  }
}
```

### Type aliases — good for unions

```ts
type Department = 'Engineering' | 'Marketing' | 'HR' | 'Finance' | 'Sales'
type Status     = 'active' | 'inactive' | 'on-leave'
```

### Generics

```ts
// useState with type parameter
const [employees, setEmployees] = useState<Employee[]>([])
const [selected, setSelected]   = useState<Employee | null>(null)

// Generic function
function findById<T extends { id: number }>(list: T[], id: number): T | undefined {
  return list.find(item => item.id === id)
}
```

### Props typing

```ts
// Pattern A — interface (preferred)
interface EmployeeCardProps {
  employee: Employee
  onSelect: (id: number) => void
  isSelected?: boolean
}
function EmployeeCard({ employee, onSelect, isSelected = false }: EmployeeCardProps) {}

// Pattern B — inline (quick and dirty)
function Badge({ label, count }: { label: string; count: number }) {}
```

### Common React TypeScript types

```ts
import type { ReactNode, MouseEvent, ChangeEvent, FormEvent } from 'react'

// Children
interface LayoutProps {
  children: ReactNode    // anything React can render (elements, strings, arrays, null)
}

// Event types
const handleClick  = (e: MouseEvent<HTMLButtonElement>)  => {}
const handleChange = (e: ChangeEvent<HTMLInputElement>)  => {}
const handleSubmit = (e: FormEvent<HTMLFormElement>)      => {}
```

---

## Summary

| Pattern | React use |
|---------|-----------|
| `const` / arrow functions | Every component and handler |
| Object destructuring | Every component's props |
| Array destructuring | `useState`, `useReducer` |
| Spread `...` | State updates (never mutate!) |
| `map / filter / find` | List rendering and derived data |
| `async/await` | Data fetching in `useEffect` |
| `?.` and `??` | Safe access to API data |
| TypeScript interfaces | Every prop type, every API response |

**Next → Module 02: JSX and Components**
