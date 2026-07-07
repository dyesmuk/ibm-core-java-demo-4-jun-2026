# Module 04 — Working with Lists and Conditionals

## Learning Objectives
- Render arrays with `.map()` and the `key` prop
- Understand why keys exist and how to pick them correctly
- Apply every conditional rendering pattern
- EMS: department filter, active/inactive toggle, empty state

---

## 4.1 Rendering Lists with `.map()`

The only way to render an array in React is to `.map()` it into JSX elements.

```tsx
const fruits = ['Apple', 'Banana', 'Cherry']

<ul>
  {fruits.map(fruit => (
    <li key={fruit}>{fruit}</li>
  ))}
</ul>
```

Without the `key` prop React prints a warning:
> **Warning: Each child in a list should have a unique "key" prop.**

---

## 4.2 The `key` Prop — Why It Exists

`key` is React's tracking ID for list items. It must be **unique among siblings** and **stable** across renders.

```tsx
// ✅ Use a real unique ID — best practice
{employees.map(emp => (
  <EmployeeCard key={emp.id} employee={emp} onRemove={handleRemove} />
))}

// ✅ Unique email also works
{employees.map(emp => (
  <EmployeeCard key={emp.email} employee={emp} />
))}
```

### How React uses the key

When the list changes (add, remove, reorder), React matches elements across renders by key:

```
Before:          After:
key=1  Alice     key=1  Alice    → keep (same key)
key=2  Bob       key=3  Carol    → key=2 gone → remove DOM node
key=3  Carol     key=4  David    → key=4 new → create DOM node
```

Without keys, React can't match elements intelligently and may re-render the entire list or produce incorrect output.

### ❌ Don't use array index as key

```tsx
// ❌ Breaks when list is reordered or filtered
{employees.map((emp, index) => (
  <EmployeeCard key={index} employee={emp} />
))}
```

If you remove the first item, all remaining items' indexes shift down. React sees the same keys but different data → wrong component gets updated. Causes subtle input bugs (typed text appearing in wrong field).

**Use index only when:** list is static and never reorders, items have no stable identity.

---

## 4.3 Conditional Rendering — Five Patterns

### Pattern 1: Ternary — "this OR that"

```tsx
// Inline
<span>{employee.isActive ? '● Active' : '○ Inactive'}</span>

// Larger blocks
{isLoading
  ? <p>Loading employees...</p>
  : <EmployeeList employees={employees} />
}
```

### Pattern 2: Short-circuit `&&` — "show OR nothing"

```tsx
{employees.length === 0 && <p>No employees found.</p>}
{isAdmin && <button>Delete All</button>}
{error && <p>Error: {error}</p>}
```

> ⚠️ **Classic gotcha:** `{count && <p>Items: {count}</p>}` renders `0` on screen when `count` is 0 because `0` is falsy but still renderable!
>
> Fix: `{count > 0 && <p>Items: {count}</p>}` or use a ternary.

### Pattern 3: Nullish coalescing in content

```tsx
<td>{employee.department ?? '—'}</td>
<p>{employee.bio || 'No bio provided'}</p>
```

### Pattern 4: Early return / guard clause

```tsx
function EmployeeDetail({ id }: { id: number | null }) {
  if (id === null) {
    return <p>Select an employee to view details.</p>
  }

  const employee = getEmployee(id)

  if (!employee) {
    return <p>Employee not found.</p>
  }

  // Happy path — employee is guaranteed to exist here
  return (
    <div>
      <h2>{employee.name}</h2>
      <p>{employee.department}</p>
    </div>
  )
}
```

### Pattern 5: Variable before return

```tsx
function StatusBanner({ status }: { status: 'loading' | 'error' | 'empty' | 'ready' }) {
  let content: React.ReactNode

  if (status === 'loading') content = <p>Loading...</p>
  else if (status === 'error') content = <p>Something went wrong.</p>
  else if (status === 'empty') content = <p>No data available.</p>
  else content = null

  return <div>{content}</div>
}
```

---

## 4.4 Rendering Nothing

Return `null` to render nothing — no DOM node, no whitespace, no wrapper.

```tsx
function DevBanner() {
  if (!import.meta.env.DEV) return null
  return <div>⚠️ Dev Mode</div>
}
```

---

## 4.5 Fragments with Keys

When mapping to multiple adjacent elements but no wrapper div:

```tsx
import { Fragment } from 'react'

// <> shorthand can't have a key — use Fragment explicitly
{employees.map(emp => (
  <Fragment key={emp.id}>
    <dt>{emp.name}</dt>
    <dd>{emp.department}</dd>
  </Fragment>
))}
```

---

## 4.6 EMS Project — Department Filter + Empty State

### Update `src/App.tsx`

```tsx
import { useState, useMemo } from 'react'
import EmployeeCard from './components/EmployeeCard'
import { INITIAL_EMPLOYEES } from './data/employees'
import type { Employee, Department } from './types'

let nextId = INITIAL_EMPLOYEES.length + 1

const DEPARTMENTS: Department[] = ['All', 'Engineering', 'Marketing', 'HR', 'Finance', 'Sales']

function App() {
  const [employees, setEmployees]       = useState<Employee[]>(INITIAL_EMPLOYEES)
  const [filter, setFilter]             = useState<Department>('All')
  const [showInactive, setShowInactive] = useState(true)
  const [newName, setNewName]           = useState('')
  const [newDept, setNewDept]           = useState<Exclude<Department,'All'>>('Engineering')

  // Derived list — recalculates only when its dependencies change
  const filtered = useMemo(() =>
    employees
      .filter(e => filter === 'All' || e.department === filter)
      .filter(e => showInactive || e.isActive),
    [employees, filter, showInactive]
  )

  const handleRemove = (id: number) =>
    setEmployees(prev => prev.filter(e => e.id !== id))

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

      {/* Add form */}
      <div>
        <input
          value={newName}
          onChange={e => setNewName(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleAdd()}
          placeholder="New employee name..."
        />
        <select
          value={newDept}
          onChange={e => setNewDept(e.target.value as Exclude<Department, 'All'>)}
        >
          {DEPARTMENTS.filter(d => d !== 'All').map(d => (
            <option key={d} value={d}>{d}</option>
          ))}
        </select>
        <button onClick={handleAdd} disabled={!newName.trim()}>+ Add</button>
      </div>

      {/* Filters */}
      <div>
        {DEPARTMENTS.map(dept => (
          <button
            key={dept}
            onClick={() => setFilter(dept)}
          >
            {dept}{filter === dept ? ' ✓' : ''}
          </button>
        ))}
        <label>
          <input
            type="checkbox"
            checked={showInactive}
            onChange={e => setShowInactive(e.target.checked)}
          />
          Show inactive
        </label>
      </div>

      <p>Showing {filtered.length} of {employees.length} employees</p>

      {/* Employee list OR empty state */}
      {filtered.length > 0
        ? (
          <div>
            {filtered.map(emp => (
              <EmployeeCard key={emp.id} employee={emp} onRemove={handleRemove} />
            ))}
          </div>
        )
        : (
          <div>
            <p>🔍 No employees found</p>
            <p>
              {filter !== 'All'
                ? `No employees in ${filter}. Try a different department.`
                : 'Add your first employee above.'}
            </p>
          </div>
        )
      }
    </div>
  )
}

export default App
```

---

## Summary

| Concept | Pattern |
|---------|---------|
| Render list | `arr.map(item => <El key={item.id} />)` |
| Key | Unique stable ID — NOT array index |
| If/else render | `{cond ? <A /> : <B />}` |
| Show or nothing | `{cond && <A />}` — watch the `0` gotcha |
| Guard clause | `if (!x) return <Fallback />` before main return |
| Render nothing | `return null` |
| Derived list | `useMemo` wrapping `.filter()` |

**Next → Module 05: Styling React Components**
