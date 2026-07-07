# Module 02 — JSX and Components

## Learning Objectives
- Understand what JSX compiles to
- Know every JSX rule cold
- Write function components
- Compose components into a UI tree
- EMS: create the first `EmployeeCard` component

---

## 2.1 What Is JSX?

JSX looks like HTML inside JavaScript. It is **not** HTML — it's a syntax extension that gets compiled into regular JavaScript function calls.

```tsx
// What you write
const element = <h1 className="title">Hello World</h1>

// What it compiles to
const element = React.createElement('h1', { className: 'title' }, 'Hello World')
```

That `React.createElement` call returns a plain JavaScript object — a "virtual DOM node":

```js
{
  type: 'h1',
  props: { className: 'title', children: 'Hello World' },
  key: null,
  ref: null
}
```

React builds a tree of these objects (the Virtual DOM), then figures out what real DOM changes to make. You never call `React.createElement` yourself — the compiler handles it.

---

## 2.2 JSX Rules — All of Them

### Rule 1 — Single root element

```tsx
// ❌ Two sibling roots — compiler error
function Bad() {
  return (
    <h1>Title</h1>
    <p>Body</p>
  )
}

// ✅ Wrap in a div
function Good() {
  return (
    <div>
      <h1>Title</h1>
      <p>Body</p>
    </div>
  )
}

// ✅ Fragment — no extra DOM node (preferred when div adds no meaning)
function Good() {
  return (
    <>
      <h1>Title</h1>
      <p>Body</p>
    </>
  )
}
```

### Rule 2 — Every tag must be closed

```tsx
// ❌
<img src="photo.jpg">
<input type="text">

// ✅
<img src="photo.jpg" />
<input type="text" />
<br />
```

### Rule 3 — `class` → `className`, `for` → `htmlFor`

```tsx
// ❌ Reserved JS keywords
<div class="container">
<label for="name">

// ✅
<div className="container">
<label htmlFor="name">
```

### Rule 4 — camelCase attributes

```tsx
// ❌
<div onclick="fn()" tabindex="0">

// ✅
<div onClick={fn} tabIndex={0}>
// Exception: data-* and aria-* attributes stay hyphenated
<div data-testid="card" aria-label="Employee card">
```

### Rule 5 — JavaScript expressions go inside `{}`

```tsx
const name = 'Alice'
const isAdmin = true

<p>{name}</p>
<p>{2 + 2}</p>
<p>{name.toUpperCase()}</p>
<p>{isAdmin ? 'Admin' : 'User'}</p>
<img src={employee.avatarUrl} alt={employee.name} />
```

### Rule 6 — `{}` takes expressions, not statements

```tsx
// ❌ if is a statement
<p>{if (x > 0) { 'positive' }}</p>

// ✅ ternary is an expression
<p>{x > 0 ? 'positive' : 'zero'}</p>

// ✅ short-circuit works
<p>{isAdmin && <span>Admin</span>}</p>
```

### HTML vs JSX cheat sheet

| HTML | JSX |
|------|-----|
| `class="..."` | `className="..."` |
| `for="..."` | `htmlFor="..."` |
| `onclick="fn()"` | `onClick={fn}` |
| `tabindex="0"` | `tabIndex={0}` |
| `style="color:red"` | `style={{ color: 'red' }}` |
| `<img>` | `<img />` |
| `<!-- comment -->` | `{/* comment */}` |

---

## 2.3 Expressions vs Statements

| Expressions ✅ (allowed in {}) | Statements ❌ (not allowed) |
|-------------------------------|----------------------------|
| `name` | `let name = 'Alice'` |
| `2 + 2` | `if (x) { }` |
| `arr.map(...)` | `for (let i ...)` |
| `condition ? A : B` | `switch (...)` |
| `condition && <El />` | `return ...` |
| Function calls | Function declarations |

---

## 2.4 Function Components

A React component is a **function** that:
1. Starts with an **uppercase letter** (mandatory)
2. Returns **JSX** or `null`

```tsx
// Minimal
function Greeting() {
  return <h1>Hello, World!</h1>
}

// Arrow function — equally valid
const Greeting = () => <h1>Hello, World!</h1>

// Multi-line JSX — wrap in parentheses
const Greeting = () => (
  <div>
    <h1>Hello, World!</h1>
    <p>Welcome to EMS</p>
  </div>
)
```

### Why uppercase?

```tsx
<div />       // lowercase → React creates a real HTML element
<Greeting />  // uppercase → React calls the Greeting() function
```

React uses the capitalisation to distinguish HTML elements from component functions. Lowercase = DOM node; uppercase = your code.

### Returning null

```tsx
function DevBanner() {
  if (!import.meta.env.DEV) return null   // nothing in production
  return <div>⚠️ Development Mode</div>
}
```

---

## 2.5 Composing Components

Complex UIs are built by nesting simple components. This is the core mental model of React.

```
App
└── Layout
    ├── Header
    │   └── NavBar
    └── Main
        └── EmployeeGrid
            ├── EmployeeCard
            ├── EmployeeCard
            └── EmployeeCard
```

```tsx
function NavBar() {
  return <nav>IBM EMS</nav>
}

function Header() {
  return (
    <header>
      <NavBar />
    </header>
  )
}

function App() {
  return (
    <div>
      <Header />
      <main>Content here</main>
    </div>
  )
}
```

---

## 2.6 File and Folder Conventions

```
src/
├── components/          ← Reusable, shared UI components
│   └── EmployeeCard.tsx
├── pages/               ← Page-level components (used by the router)
│   └── EmployeesPage.tsx
├── hooks/               ← Custom hooks
│   └── useEmployees.ts
├── services/            ← API calls
│   └── employeeService.ts
├── types/               ← Shared TypeScript interfaces
│   └── index.ts
├── data/                ← Seed/mock data
│   └── employees.ts
├── App.tsx
├── main.tsx
└── index.css
```

**One component per file. File name = component name.**

---

## 2.7 EMS Project — First Component

### `src/types/index.ts` — define the shape once

```ts
export interface Employee {
  id: number
  name: string
  email: string
  department: string
  salary: number
  isActive: boolean
  joinDate: string
}

export type Department = 'Engineering' | 'Marketing' | 'HR' | 'Finance' | 'Sales' | 'All'
```

### `src/components/EmployeeCard.tsx`

```tsx
import type { Employee } from '../types'

interface EmployeeCardProps {
  employee: Employee
}

function EmployeeCard({ employee }: EmployeeCardProps) {
  return (
    <div>
      <h3>{employee.name}</h3>
      <p>{employee.department}</p>
      <p>{employee.email}</p>
      <p>${employee.salary.toLocaleString()} / yr</p>
      <span>{employee.isActive ? 'Active' : 'Inactive'}</span>
    </div>
  )
}

export default EmployeeCard
```

### `src/App.tsx`

```tsx
import EmployeeCard from './components/EmployeeCard'
import type { Employee } from './types'

const sampleEmployee: Employee = {
  id: 1,
  name: 'Alice Johnson',
  email: 'alice@ibm.com',
  department: 'Engineering',
  salary: 95000,
  isActive: true,
  joinDate: '2021-03-15',
}

function App() {
  return (
    <div>
      <h1>IBM Employee Management System</h1>
      <EmployeeCard employee={sampleEmployee} />
    </div>
  )
}

export default App
```

---

## 2.8 Export Patterns

```tsx
// Named export
export function EmployeeCard() { return <div /> }
export type { Employee }

// Default export
function EmployeeCard() { return <div /> }
export default EmployeeCard

// Barrel — re-export from index.ts in a folder
// src/components/index.ts
export { default as EmployeeCard } from './EmployeeCard'
export { default as EmployeeList } from './EmployeeList'

// Usage
import { EmployeeCard, EmployeeList } from './components'
```

---

## 2.9 JSX Comments

```tsx
function App() {
  return (
    <div>
      {/* This is how you comment in JSX */}
      <h1>IBM EMS</h1>
      {/* TODO: Add employee count badge */}
    </div>
  )
}
```

---

## Summary

- JSX compiles to `React.createElement()` — it's JavaScript, not HTML
- Every tag must close, one root element, `className` not `class`
- Uppercase = component function call; lowercase = HTML element
- Compose big UIs from small, single-purpose components
- One component per file, types in `src/types/index.ts`

**Next → Module 03: Props, State, and Events**
