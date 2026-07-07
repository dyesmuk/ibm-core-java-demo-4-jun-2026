# Module 13 — Testing React Applications

## Learning Objectives
- Understand what to test and what not to test
- Set up Vitest and React Testing Library
- Write unit tests for components, hooks, and services
- Write integration tests with user interactions
- Mock API calls with MSW
- EMS: tests for EmployeeCard, EmployeesPage, useEmployees hook

---

## 13.1 Testing Philosophy

**Test behaviour, not implementation.**

```
✅ Test: "clicking Delete removes the employee from the list"
❌ Test: "handleDelete function sets state correctly"

✅ Test: "form shows error when email is invalid"
❌ Test: "setError is called with the right string"
```

Users don't care about your implementation. Tests should not either.

### Testing pyramid for React

```
          ┌────────────────────┐
          │   E2E tests        │  ← Cypress, Playwright (few, slow)
          │   full user flows  │
          ├────────────────────┤
          │ Integration tests  │  ← React Testing Library (most)
          │ component + deps   │
          ├────────────────────┤
          │   Unit tests       │  ← Vitest (utils, hooks, pure fns)
          └────────────────────┘
```

---

## 13.2 Setup

```bash
npm install -D vitest @testing-library/react @testing-library/user-event @testing-library/jest-dom jsdom msw
```

### `vite.config.ts`

```ts
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',              // simulate browser DOM
    globals: true,                     // no need to import describe, it, expect
    setupFiles: ['./src/test/setup.ts'],
  },
})
```

### `src/test/setup.ts`

```ts
import '@testing-library/jest-dom'   // adds toBeInTheDocument(), toHaveValue(), etc.
```

### `tsconfig.json` — add types

```json
{
  "compilerOptions": {
    "types": ["vitest/globals", "@testing-library/jest-dom"]
  }
}
```

### `package.json` — add scripts

```json
{
  "scripts": {
    "test":     "vitest",
    "test:ui":  "vitest --ui",
    "coverage": "vitest run --coverage"
  }
}
```

---

## 13.3 Testing Utilities

```tsx
// src/test/test-utils.tsx
// Wrap components with all your providers so every test gets the same setup

import { render } from '@testing-library/react'
import type { RenderOptions } from '@testing-library/react'
import type { ReactElement, ReactNode } from 'react'
import { MemoryRouter } from 'react-router-dom'
import { Provider } from 'react-redux'
import { configureStore } from '@reduxjs/toolkit'
import employeesReducer from '../features/employees/employeesSlice'
import authReducer from '../features/auth/authSlice'

function AllProviders({ children }: { children: ReactNode }) {
  const store = configureStore({
    reducer: { employees: employeesReducer, auth: authReducer },
  })

  return (
    <Provider store={store}>
      <MemoryRouter>
        {children}
      </MemoryRouter>
    </Provider>
  )
}

// Override RTL's render with our wrapped version
const customRender = (ui: ReactElement, options?: RenderOptions) =>
  render(ui, { wrapper: AllProviders, ...options })

// Re-export everything from RTL so tests only import from here
export * from '@testing-library/react'
export { customRender as render }
```

---

## 13.4 Testing Components

### `EmployeeCard` tests

```tsx
// src/components/EmployeeCard.test.tsx
import { render, screen, fireEvent } from '../test/test-utils'
import { vi } from 'vitest'
import EmployeeCard from './EmployeeCard'
import type { Employee } from '../types'

const mockEmployee: Employee = {
  id: 1,
  name: 'Alice Johnson',
  email: 'alice@ibm.com',
  department: 'Engineering',
  salary: 95000,
  isActive: true,
  joinDate: '2021-03-15',
}

describe('EmployeeCard', () => {
  it('renders employee information', () => {
    render(<EmployeeCard employee={mockEmployee} onRemove={() => {}} />)

    expect(screen.getByText('Alice Johnson')).toBeInTheDocument()
    expect(screen.getByText('Engineering')).toBeInTheDocument()
    expect(screen.getByText('alice@ibm.com')).toBeInTheDocument()
    expect(screen.getByText(/95,000/)).toBeInTheDocument()
  })

  it('shows Active badge when employee is active', () => {
    render(<EmployeeCard employee={mockEmployee} onRemove={() => {}} />)
    expect(screen.getByText('Active')).toBeInTheDocument()
  })

  it('shows Inactive badge when employee is inactive', () => {
    const inactive = { ...mockEmployee, isActive: false }
    render(<EmployeeCard employee={inactive} onRemove={() => {}} />)
    expect(screen.getByText('Inactive')).toBeInTheDocument()
  })

  it('calls onRemove with employee id when remove button is clicked', () => {
    const handleRemove = vi.fn()
    render(<EmployeeCard employee={mockEmployee} onRemove={handleRemove} />)

    fireEvent.click(screen.getByRole('button', { name: /remove alice/i }))

    expect(handleRemove).toHaveBeenCalledTimes(1)
    expect(handleRemove).toHaveBeenCalledWith(1)
  })

  it('does not render remove button if onRemove is not provided', () => {
    // When the remove button is optional
    render(<EmployeeCard employee={mockEmployee} onRemove={undefined as never} />)
    expect(screen.queryByRole('button', { name: /remove/i })).not.toBeInTheDocument()
  })
})
```

---

## 13.5 Testing User Interactions with `userEvent`

`userEvent` simulates real browser events (full keyboard, pointer events) — more realistic than `fireEvent`.

```tsx
// src/components/EmployeeForm.test.tsx
import { render, screen, waitFor } from '../test/test-utils'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'
import CreateEmployeePage from '../pages/CreateEmployeePage'
import { employeeService } from '../services/employeeService'

vi.mock('../services/employeeService')   // auto-mock the module

describe('CreateEmployeePage', () => {
  const user = userEvent.setup()

  beforeEach(() => {
    vi.mocked(employeeService.create).mockResolvedValue({
      id: 99, name: 'Bob Smith', email: 'bob@ibm.com',
      department: 'Marketing', salary: 70000, isActive: true, joinDate: '2024-01-01',
    })
  })

  afterEach(() => vi.clearAllMocks())

  it('shows validation errors when form is submitted empty', async () => {
    render(<CreateEmployeePage />)

    await user.click(screen.getByRole('button', { name: /create employee/i }))

    expect(await screen.findByText('Name is required')).toBeInTheDocument()
    expect(screen.getByText('Email is required')).toBeInTheDocument()
  })

  it('submits form with valid data and calls employeeService.create', async () => {
    render(<CreateEmployeePage />)

    await user.type(screen.getByLabelText(/full name/i), 'Bob Smith')
    await user.type(screen.getByLabelText(/email/i), 'bob@ibm.com')
    await user.selectOptions(screen.getByLabelText(/department/i), 'Marketing')
    await user.clear(screen.getByLabelText(/salary/i))
    await user.type(screen.getByLabelText(/salary/i), '70000')

    await user.click(screen.getByRole('button', { name: /create employee/i }))

    await waitFor(() => {
      expect(employeeService.create).toHaveBeenCalledWith(
        expect.objectContaining({
          name:       'Bob Smith',
          email:      'bob@ibm.com',
          department: 'Marketing',
          salary:     70000,
        })
      )
    })
  })

  it('shows error message when API call fails', async () => {
    vi.mocked(employeeService.create).mockRejectedValue(new Error('Server error'))

    render(<CreateEmployeePage />)

    await user.type(screen.getByLabelText(/full name/i), 'Bob Smith')
    await user.type(screen.getByLabelText(/email/i), 'bob@ibm.com')
    await user.click(screen.getByRole('button', { name: /create employee/i }))

    expect(await screen.findByText(/server error/i)).toBeInTheDocument()
  })
})
```

---

## 13.6 Testing Custom Hooks

Use `renderHook` to test hooks in isolation.

```tsx
// src/hooks/useEmployees.test.ts
import { renderHook, act } from '@testing-library/react'
import { useEmployees } from './useEmployees'

describe('useEmployees', () => {
  it('initialises with default values', () => {
    const { result } = renderHook(() => useEmployees())

    expect(result.current.filter).toBe('All')
    expect(result.current.search).toBe('')
    expect(result.current.employees.length).toBeGreaterThan(0)
  })

  it('filters employees by department', () => {
    const { result } = renderHook(() => useEmployees())

    act(() => {
      result.current.setFilter('Engineering')
    })

    expect(result.current.filtered.every(e => e.department === 'Engineering')).toBe(true)
  })

  it('adds a new employee', () => {
    const { result } = renderHook(() => useEmployees())
    const initialCount = result.current.employees.length

    act(() => {
      result.current.addEmployee('Test User', 'HR')
    })

    expect(result.current.employees.length).toBe(initialCount + 1)
    expect(result.current.employees.at(-1)?.name).toBe('Test User')
  })

  it('removes an employee by id', () => {
    const { result } = renderHook(() => useEmployees())
    const firstId = result.current.employees[0].id

    act(() => {
      result.current.removeEmployee(firstId)
    })

    expect(result.current.employees.find(e => e.id === firstId)).toBeUndefined()
  })

  it('searches employees by name', () => {
    const { result } = renderHook(() => useEmployees())

    act(() => {
      result.current.setSearch('alice')
    })

    expect(result.current.filtered.every(e =>
      e.name.toLowerCase().includes('alice')
    )).toBe(true)
  })
})
```

---

## 13.7 Mocking APIs with MSW

MSW (Mock Service Worker) intercepts real network requests at the service worker level — no mocking inside the tests.

```ts
// src/test/mocks/handlers.ts
import { http, HttpResponse } from 'msw'
import type { Employee } from '../../types'

const mockEmployees: Employee[] = [
  { id: 1, name: 'Alice Johnson', email: 'alice@ibm.com', department: 'Engineering', salary: 95000, isActive: true, joinDate: '2021-03-15' },
  { id: 2, name: 'Bob Smith',     email: 'bob@ibm.com',   department: 'Marketing',   salary: 72000, isActive: true, joinDate: '2020-07-01' },
]

export const handlers = [
  http.get('*/users', () => HttpResponse.json(mockEmployees)),

  http.get('*/users/:id', ({ params }) => {
    const employee = mockEmployees.find(e => e.id === Number(params.id))
    if (!employee) return new HttpResponse(null, { status: 404 })
    return HttpResponse.json(employee)
  }),

  http.post('*/users', async ({ request }) => {
    const body = await request.json() as Partial<Employee>
    const newEmployee: Employee = {
      ...body as Employee,
      id: Date.now(),
    }
    return HttpResponse.json(newEmployee, { status: 201 })
  }),

  http.delete('*/users/:id', () => new HttpResponse(null, { status: 204 })),
]
```

```ts
// src/test/mocks/server.ts
import { setupServer } from 'msw/node'
import { handlers } from './handlers'

export const server = setupServer(...handlers)
```

```ts
// src/test/setup.ts — register MSW for all tests
import '@testing-library/jest-dom'
import { server } from './mocks/server'

beforeAll(()  => server.listen({ onUnhandledRequest: 'error' }))
afterEach(()  => server.resetHandlers())   // reset overrides after each test
afterAll(()   => server.close())
```

```tsx
// src/pages/EmployeesPage.test.tsx
import { render, screen, waitFor } from '../test/test-utils'
import { server }       from '../test/mocks/server'
import { http, HttpResponse } from 'msw'
import EmployeesPage    from './EmployeesPage'

describe('EmployeesPage', () => {
  it('shows loading state initially', () => {
    render(<EmployeesPage />)
    expect(screen.getByText(/loading/i)).toBeInTheDocument()
  })

  it('renders employee list after successful fetch', async () => {
    render(<EmployeesPage />)

    await waitFor(() => {
      expect(screen.getByText('Alice Johnson')).toBeInTheDocument()
      expect(screen.getByText('Bob Smith')).toBeInTheDocument()
    })
  })

  it('shows error message when fetch fails', async () => {
    // Override handler for this test only
    server.use(
      http.get('*/users', () => new HttpResponse(null, { status: 500 }))
    )

    render(<EmployeesPage />)

    expect(await screen.findByText(/failed to load/i)).toBeInTheDocument()
  })

  it('filters employees by department', async () => {
    render(<EmployeesPage />)
    await screen.findByText('Alice Johnson')   // wait for load

    const filterBtn = screen.getByRole('button', { name: /engineering/i })
    fireEvent.click(filterBtn)

    await waitFor(() => {
      expect(screen.getByText('Alice Johnson')).toBeInTheDocument()
      expect(screen.queryByText('Bob Smith')).not.toBeInTheDocument()
    })
  })
})
```

---

## 13.8 Testing Redux Slices

```ts
// src/features/employees/employeesSlice.test.ts
import { configureStore } from '@reduxjs/toolkit'
import employeesReducer, {
  setFilter,
  setSearch,
  toggleActive,
  fetchEmployees,
  selectFilteredEmployees,
} from './employeesSlice'

// Helper: create a fresh store for each test
function makeStore() {
  return configureStore({ reducer: { employees: employeesReducer } })
}

describe('employeesSlice', () => {
  it('sets filter', () => {
    const store = makeStore()
    store.dispatch(setFilter('Engineering'))
    expect(store.getState().employees.filter).toBe('Engineering')
  })

  it('sets search', () => {
    const store = makeStore()
    store.dispatch(setSearch('alice'))
    expect(store.getState().employees.search).toBe('alice')
  })

  it('toggles employee active status', () => {
    const store = makeStore()
    // After fetch resolves, toggle employee 1
    store.dispatch({
      type: fetchEmployees.fulfilled.type,
      payload: [
        { id: 1, name: 'Alice', isActive: true, department: 'Engineering', email: 'a@a.com', salary: 90000, joinDate: '2021-01-01' },
      ],
    })

    store.dispatch(toggleActive(1))
    const emp = store.getState().employees.list.find(e => e.id === 1)
    expect(emp?.isActive).toBe(false)
  })

  it('shows loading state while fetching', () => {
    const store = makeStore()
    store.dispatch({ type: fetchEmployees.pending.type })
    expect(store.getState().employees.loading).toBe(true)
  })
})
```

---

## 13.9 Query Cheat Sheet

```tsx
// Queries — in order of preference
screen.getByRole('button', { name: /submit/i })    // ✅ most accessible
screen.getByLabelText(/email/i)                    // ✅ form inputs
screen.getByPlaceholderText(/search/i)             // OK for search inputs
screen.getByText(/alice johnson/i)                 // OK for static content
screen.getByTestId('employee-card')                // last resort

// Async variants — wait for element to appear
await screen.findByText('Alice Johnson')           // polls until found or times out
await screen.findByRole('heading', { name: /employees/i })

// queryBy — returns null if not found (use to assert absence)
expect(screen.queryByText('Delete')).not.toBeInTheDocument()

// getAllBy — returns array when multiple matches expected
const cards = screen.getAllByRole('article')
expect(cards).toHaveLength(3)
```

---

## Summary

| Tool | Purpose |
|------|---------|
| Vitest | Test runner, assertions, mocking |
| React Testing Library | Render components, query DOM |
| `userEvent` | Simulate realistic user interactions |
| MSW | Intercept HTTP requests with mock handlers |
| `renderHook` | Test custom hooks in isolation |
| `vi.mock` | Auto-mock a module |
| `vi.fn()` | Create a mock function (spy) |

**Next → Module 14: Deploying the App**
