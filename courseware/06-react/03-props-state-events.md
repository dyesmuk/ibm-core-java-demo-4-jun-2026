# Module 03 — Props, State, and Events

## Learning Objectives
- Pass data into components using props
- Manage changing data with `useState`
- Handle user interactions with event handlers
- Understand the re-render cycle
- EMS: make `EmployeeCard` accept real data, add and remove employees

---

## 3.1 Props — Passing Data Into Components

Right now `EmployeeCard` is hardcoded. Props let you pass different data to each instance.

**Props = function parameters for components.**

```
Parent Component
  │  passes props down
  ↓
Child Component
  reads props, renders them
  CANNOT modify props — read-only
```

### Rules of props

1. Props flow **one way** — parent → child only
2. Props are **read-only** in the child — never mutate them
3. They're just regular function parameters under the hood

### Update `EmployeeCard` to accept props

```tsx
// src/components/EmployeeCard.tsx
import type { Employee } from '../types'

interface EmployeeCardProps {
  employee: Employee
  onRemove: (id: number) => void
}

function EmployeeCard({ employee, onRemove }: EmployeeCardProps) {
  return (
    <div>
      <button onClick={() => onRemove(employee.id)}>✕</button>
      <h3>{employee.name}</h3>
      <p>{employee.department}</p>
      <p>{employee.email}</p>
      <p>Joined: {new Date(employee.joinDate).toLocaleDateString()}</p>
      <p>${employee.salary.toLocaleString()} / yr</p>
      <span>{employee.isActive ? '● Active' : '○ Inactive'}</span>
    </div>
  )
}

export default EmployeeCard
```

### Optional props and defaults

```tsx
interface EmployeeCardProps {
  employee: Employee
  onRemove: (id: number) => void
  compact?: boolean           // optional — TypeScript knows it may be undefined
  onSelect?: (id: number) => void
}

function EmployeeCard({
  employee,
  onRemove,
  compact = false,            // default value if not provided
  onSelect,
}: EmployeeCardProps) {
  // compact is always boolean here
}
```

---

## 3.2 The `children` Prop

Whatever you put between opening and closing tags becomes `children` — a built-in prop.

```tsx
interface SectionProps {
  title: string
  children: React.ReactNode    // anything React can render
}

function Section({ title, children }: SectionProps) {
  return (
    <section>
      <h2>{title}</h2>
      <div>{children}</div>
    </section>
  )
}

// Usage — content between tags becomes children
<Section title="Engineering Team">
  <EmployeeCard employee={alice} onRemove={handleRemove} />
  <EmployeeCard employee={bob} onRemove={handleRemove} />
</Section>
```

---

## 3.3 State — Data That Changes Over Time

Props are read-only. **State** is data owned by a component that can change. When state changes, React re-renders the component.

### `useState` hook

```tsx
import { useState } from 'react'

const [currentValue, setterFunction] = useState(initialValue)

// Example
const [count, setCount] = useState(0)
//     ↑ value   ↑ setter    ↑ initial
```

### What happens when state changes

```
1. setCount(count + 1) is called
2. React schedules a re-render
3. Component function runs again from the top
4. count has the new value this time
5. JSX is re-evaluated with new value
6. React diffs Virtual DOM vs previous Virtual DOM
7. Only changed DOM nodes are updated
8. Browser repaints those nodes
```

That's why you **must use the setter** — mutating the variable directly skips step 2 and nothing updates.

```tsx
count = count + 1   // ❌ React has no idea — UI stays stale
setCount(count + 1) // ✅ React knows, schedules re-render
```

### Counter example

```tsx
import { useState } from 'react'

function Counter() {
  const [count, setCount] = useState(0)

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>+</button>
      <button onClick={() => setCount(count - 1)}>−</button>
      <button onClick={() => setCount(0)}>Reset</button>
    </div>
  )
}
```

---

## 3.4 State with Objects

Always spread the previous state and override only the changed field.

```tsx
interface FormData {
  name: string
  email: string
  department: string
}

function EmployeeForm() {
  const [form, setForm] = useState<FormData>({
    name: '',
    email: '',
    department: 'Engineering',
  })

  const updateField = (field: keyof FormData, value: string) => {
    // ✅ Create new object — spread preserves all other fields
    setForm(prev => ({ ...prev, [field]: value }))
  }

  // ❌ Never do this — mutates existing object, React won't notice
  // form.name = 'Alice'
  // setForm(form)

  return (
    <form>
      <input
        value={form.name}
        onChange={e => updateField('name', e.target.value)}
        placeholder="Name"
      />
      <input
        value={form.email}
        onChange={e => updateField('email', e.target.value)}
        placeholder="Email"
      />
    </form>
  )
}
```

---

## 3.5 State with Arrays

Always return a **new array** — never push/splice the existing one.

```tsx
// Add
setEmployees(prev => [...prev, newEmployee])

// Remove
setEmployees(prev => prev.filter(e => e.id !== idToRemove))

// Update one element
setEmployees(prev =>
  prev.map(e => e.id === targetId ? { ...e, isActive: !e.isActive } : e)
)
```

---

## 3.6 The Functional Update Pattern

When new state depends on previous state, use the callback form to avoid stale closures:

```tsx
// ❌ May use stale state in batch updates
setCount(count + 1)
setCount(count + 1)   // both read the same stale count
// result: count + 1, not count + 2

// ✅ Always uses the latest state
setCount(prev => prev + 1)
setCount(prev => prev + 1)
// result: count + 2 ✅
```

**Rule:** Whenever new state is derived from previous state, use `prev =>` form.

---

## 3.7 Events

React wraps native DOM events in a SyntheticEvent that works consistently cross-browser.

```tsx
// Click
const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
  e.preventDefault()
  console.log('clicked')
}
<button onClick={handleClick}>Click me</button>

// ❌ Common mistake — calling vs passing
<button onClick={handleClick()}>  // ❌ calls it immediately during render
<button onClick={handleClick}>    // ✅ passes the function reference

// Input change
const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
  setValue(e.target.value)
}
<input onChange={handleChange} />

// Form submit
const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
  e.preventDefault()   // ALWAYS prevent default on form submit
  // process data
}
<form onSubmit={handleSubmit}>

// Passing arguments — wrap in arrow function
const handleDelete = (id: number) => {
  setEmployees(prev => prev.filter(e => e.id !== id))
}
<button onClick={() => handleDelete(employee.id)}>Delete</button>
```

### Common event type reference

| Event | TypeScript Type |
|-------|----------------|
| `onClick` | `MouseEvent<HTMLButtonElement>` |
| `onChange` on input | `ChangeEvent<HTMLInputElement>` |
| `onChange` on select | `ChangeEvent<HTMLSelectElement>` |
| `onSubmit` | `FormEvent<HTMLFormElement>` |
| `onKeyDown` | `KeyboardEvent<HTMLInputElement>` |
| `onFocus` / `onBlur` | `FocusEvent<HTMLInputElement>` |

---

## 3.8 Lifting State Up

When two sibling components need the same data, move state **up** to their nearest common parent. The parent owns it and passes it down.

```
App — owns `employees` state (single source of truth)
├── AddEmployeeForm — calls onAdd(newEmp)
└── EmployeeList    — receives employees[], calls onRemove(id)
```

```tsx
function App() {
  const [employees, setEmployees] = useState<Employee[]>(INITIAL_EMPLOYEES)

  const addEmployee = (emp: Employee) => {
    setEmployees(prev => [...prev, emp])
  }

  const removeEmployee = (id: number) => {
    setEmployees(prev => prev.filter(e => e.id !== id))
  }

  return (
    <div>
      <AddEmployeeForm onAdd={addEmployee} />
      <EmployeeList employees={employees} onRemove={removeEmployee} />
    </div>
  )
}
```

---

## 3.9 EMS Project — Employees as State

### `src/data/employees.ts`

```ts
import type { Employee } from '../types'

export const INITIAL_EMPLOYEES: Employee[] = [
  {
    id: 1, name: 'Alice Johnson', email: 'alice@ibm.com',
    department: 'Engineering', salary: 95000, isActive: true, joinDate: '2021-03-15',
  },
  {
    id: 2, name: 'Bob Smith', email: 'bob@ibm.com',
    department: 'Marketing', salary: 72000, isActive: true, joinDate: '2020-07-01',
  },
  {
    id: 3, name: 'Carol White', email: 'carol@ibm.com',
    department: 'Engineering', salary: 88000, isActive: false, joinDate: '2019-11-20',
  },
  {
    id: 4, name: 'David Lee', email: 'david@ibm.com',
    department: 'HR', salary: 68000, isActive: true, joinDate: '2022-01-10',
  },
]
```

### `src/App.tsx`

```tsx
import { useState } from 'react'
import EmployeeCard from './components/EmployeeCard'
import { INITIAL_EMPLOYEES } from './data/employees'
import type { Employee } from './types'

let nextId = INITIAL_EMPLOYEES.length + 1

function App() {
  const [employees, setEmployees] = useState<Employee[]>(INITIAL_EMPLOYEES)
  const [newName, setNewName]     = useState('')
  const [newDept, setNewDept]     = useState('Engineering')

  const handleRemove = (id: number) => {
    setEmployees(prev => prev.filter(e => e.id !== id))
  }

  const handleAdd = () => {
    if (!newName.trim()) return

    const emp: Employee = {
      id: nextId++,
      name: newName.trim(),
      email: `${newName.toLowerCase().replace(/\s+/g, '.')}@ibm.com`,
      department: newDept,
      salary: 70000,
      isActive: true,
      joinDate: new Date().toISOString().split('T')[0],
    }

    setEmployees(prev => [...prev, emp])
    setNewName('')
  }

  return (
    <div>
      <h1>IBM Employee Management System</h1>
      <p>{employees.length} employee{employees.length !== 1 ? 's' : ''}</p>

      <div>
        <input
          value={newName}
          onChange={e => setNewName(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleAdd()}
          placeholder="Employee name..."
        />
        <select value={newDept} onChange={e => setNewDept(e.target.value)}>
          <option>Engineering</option>
          <option>Marketing</option>
          <option>HR</option>
          <option>Finance</option>
          <option>Sales</option>
        </select>
        <button onClick={handleAdd} disabled={!newName.trim()}>
          Add Employee
        </button>
      </div>

      <div>
        {employees.map(emp => (
          <EmployeeCard key={emp.id} employee={emp} onRemove={handleRemove} />
        ))}
      </div>

      {employees.length === 0 && <p>No employees yet. Add one above.</p>}
    </div>
  )
}

export default App
```

---

## Summary

| Concept | Key Rule |
|---------|----------|
| Props | Read-only in child; flow parent → child |
| State | `useState` — triggers re-render when setter is called |
| Events | Pass the function reference, don't call it during render |
| Object state | Always spread: `{ ...prev, field: value }` |
| Array state | Always new array: `[...prev, item]`, `.filter()`, `.map()` |
| Lifting state | Move shared state to nearest common parent |

**Next → Module 04: Lists and Conditionals**
