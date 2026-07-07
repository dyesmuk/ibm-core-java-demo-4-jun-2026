# Module 11 — Redux: Centralised State Management

## Learning Objectives
- Understand when and why to use Redux over Context + useState
- Set up Redux Toolkit (the modern, official way)
- Create slices, reducers, and async thunks
- Write selectors with `createSelector`
- Connect Redux to React components
- EMS: migrate employee state into a Redux store

---

## 11.1 When to Use Redux

| State type | Best location |
|-----------|--------------|
| Local UI state (modal open, input value) | `useState` in the component |
| Shared state for a subtree | Lifted state + props / Context |
| Auth state (user, token) | Context or Redux |
| Server data used across many pages (employees list) | **Redux** |
| Complex state with many actions from many places | **Redux** |
| State that needs time-travel debugging | **Redux** |

> **Rule of thumb:** If you find yourself passing the same data through 3+ component layers, or the same data is needed on multiple unrelated pages, Redux is the right call.

```bash
npm install @reduxjs/toolkit react-redux
```

---

## 11.2 How Redux Works — One-Way Data Flow

```
User clicks "Delete Employee"
        ↓
Component calls dispatch(deleteEmployee(id))
        ↓
Redux runs employeesSlice reducer(currentState, action)
        ↓
Reducer returns new state (immutable update)
        ↓
Store saves new state
        ↓
React re-renders components that selected the changed state
```

This is strictly one-way — data never goes backwards.

---

## 11.3 Store Setup

```ts
// src/store/index.ts
import { configureStore } from '@reduxjs/toolkit'
import employeesReducer from '../features/employees/employeesSlice'
import authReducer      from '../features/auth/authSlice'

export const store = configureStore({
  reducer: {
    employees: employeesReducer,
    auth:      authReducer,
  },
  // redux-thunk is included by default — no extra setup
})

// Infer types from the store itself — always stays accurate
export type RootState   = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
```

```ts
// src/store/hooks.ts — typed wrappers (always use these, not the raw ones)
import { useDispatch, useSelector } from 'react-redux'
import type { RootState, AppDispatch } from './index'

export const useAppDispatch = () => useDispatch<AppDispatch>()
export const useAppSelector = <T>(selector: (state: RootState) => T): T =>
  useSelector<RootState, T>(selector)
```

```tsx
// src/main.tsx — provide the store
import { Provider } from 'react-redux'
import { store }    from './store'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </Provider>
  </StrictMode>
)
```

---

## 11.4 Employee Slice

```ts
// src/features/employees/employeesSlice.ts
import { createSlice, createAsyncThunk, createSelector } from '@reduxjs/toolkit'
import type { PayloadAction } from '@reduxjs/toolkit'
import type { Employee, Department } from '../../types'
import { employeeService } from '../../services/employeeService'
import type { RootState } from '../../store'

// ── Async thunks (handle API calls outside reducers) ──────────────────────────

export const fetchEmployees = createAsyncThunk(
  'employees/fetchAll',
  async (_, { rejectWithValue }) => {
    try {
      return await employeeService.getAll()
    } catch (err) {
      return rejectWithValue(err instanceof Error ? err.message : 'Failed to fetch')
    }
  }
)

export const createEmployee = createAsyncThunk(
  'employees/create',
  async (dto: Omit<Employee, 'id'>, { rejectWithValue }) => {
    try {
      return await employeeService.create(dto)
    } catch (err) {
      return rejectWithValue(err instanceof Error ? err.message : 'Failed to create')
    }
  }
)

export const deleteEmployee = createAsyncThunk(
  'employees/delete',
  async (id: number, { rejectWithValue }) => {
    try {
      await employeeService.delete(id)
      return id   // return id so reducer knows which one to remove
    } catch (err) {
      return rejectWithValue(err instanceof Error ? err.message : 'Failed to delete')
    }
  }
)

// ── Slice ─────────────────────────────────────────────────────────────────────

interface EmployeesState {
  list:        Employee[]
  loading:     boolean
  error:       string | null
  filter:      Department
  search:      string
  selectedId:  number | null
}

const initialState: EmployeesState = {
  list:       [],
  loading:    false,
  error:      null,
  filter:     'All',
  search:     '',
  selectedId: null,
}

const employeesSlice = createSlice({
  name: 'employees',
  initialState,

  // Synchronous reducers — use Immer, so "mutations" are actually safe
  reducers: {
    setFilter(state, action: PayloadAction<Department>) {
      state.filter = action.payload
    },
    setSearch(state, action: PayloadAction<string>) {
      state.search = action.payload
    },
    setSelectedId(state, action: PayloadAction<number | null>) {
      state.selectedId = action.payload
    },
    toggleActive(state, action: PayloadAction<number>) {
      const emp = state.list.find(e => e.id === action.payload)
      if (emp) emp.isActive = !emp.isActive   // Immer makes this safe
    },
    clearError(state) {
      state.error = null
    },
  },

  // Async thunk lifecycle handlers
  extraReducers: builder => {
    // fetchEmployees
    builder
      .addCase(fetchEmployees.pending, state => {
        state.loading = true
        state.error   = null
      })
      .addCase(fetchEmployees.fulfilled, (state, action) => {
        state.loading = false
        state.list    = action.payload
      })
      .addCase(fetchEmployees.rejected, (state, action) => {
        state.loading = false
        state.error   = action.payload as string
      })

    // createEmployee
    builder
      .addCase(createEmployee.fulfilled, (state, action) => {
        state.list.push(action.payload)
      })

    // deleteEmployee
    builder
      .addCase(deleteEmployee.fulfilled, (state, action) => {
        state.list = state.list.filter(e => e.id !== action.payload)
      })
  },
})

export const { setFilter, setSearch, setSelectedId, toggleActive, clearError } =
  employeesSlice.actions

export default employeesSlice.reducer

// ── Selectors ─────────────────────────────────────────────────────────────────

// Basic selectors
export const selectAllEmployees = (state: RootState) => state.employees.list
export const selectFilter       = (state: RootState) => state.employees.filter
export const selectSearch       = (state: RootState) => state.employees.search
export const selectLoading      = (state: RootState) => state.employees.loading
export const selectError        = (state: RootState) => state.employees.error

// Memoised derived selector — only recalculates when deps change
export const selectFilteredEmployees = createSelector(
  [selectAllEmployees, selectFilter, selectSearch],
  (employees, filter, search) =>
    employees
      .filter(e => filter === 'All' || e.department === filter)
      .filter(e => e.name.toLowerCase().includes(search.toLowerCase()))
      .sort((a, b) => a.name.localeCompare(b.name))
)

export const selectStats = createSelector(
  selectAllEmployees,
  employees => ({
    total:       employees.length,
    active:      employees.filter(e => e.isActive).length,
    departments: new Set(employees.map(e => e.department)).size,
    avgSalary:   Math.round(employees.reduce((s, e) => s + e.salary, 0) / employees.length) || 0,
  })
)

export const selectEmployeeById = (id: number) =>
  createSelector(selectAllEmployees, list => list.find(e => e.id === id))
```

---

## 11.5 Using Redux in Components

```tsx
// src/pages/EmployeesPage.tsx
import { useEffect } from 'react'
import { useAppDispatch, useAppSelector } from '../store/hooks'
import {
  fetchEmployees,
  deleteEmployee,
  setFilter,
  setSearch,
  selectFilteredEmployees,
  selectStats,
  selectLoading,
  selectError,
} from '../features/employees/employeesSlice'
import type { Department } from '../types'
import EmployeeCard from '../components/EmployeeCard'

function EmployeesPage() {
  const dispatch  = useAppDispatch()
  const employees = useAppSelector(selectFilteredEmployees)
  const stats     = useAppSelector(selectStats)
  const loading   = useAppSelector(selectLoading)
  const error     = useAppSelector(selectError)

  useEffect(() => {
    dispatch(fetchEmployees())
  }, [dispatch])

  if (loading) return <p>Loading employees…</p>
  if (error)   return <p>Error: {error}</p>

  return (
    <div>
      <h1>Employees</h1>
      <p>{stats.total} total · {stats.active} active · {stats.departments} departments</p>

      <input
        placeholder="Search employees…"
        onChange={e => dispatch(setSearch(e.target.value))}
      />

      {['All', 'Engineering', 'Marketing', 'HR', 'Finance', 'Sales'].map(dept => (
        <button key={dept} onClick={() => dispatch(setFilter(dept as Department))}>
          {dept}
        </button>
      ))}

      {employees.map(emp => (
        <EmployeeCard
          key={emp.id}
          employee={emp}
          onRemove={id => dispatch(deleteEmployee(id))}
        />
      ))}
    </div>
  )
}

export default EmployeesPage
```

---

## 11.6 Auth Slice

```ts
// src/features/auth/authSlice.ts
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import type { PayloadAction } from '@reduxjs/toolkit'
import { authService } from '../../services/authService'
import type { AuthUser, LoginCredentials } from '../../services/authService'
import type { RootState } from '../../store'

export const login = createAsyncThunk(
  'auth/login',
  async (credentials: LoginCredentials, { rejectWithValue }) => {
    try {
      const user = await authService.login(credentials)
      authService.saveToken(user.token)
      return user
    } catch (err) {
      return rejectWithValue(err instanceof Error ? err.message : 'Login failed')
    }
  }
)

export const logout = createAsyncThunk('auth/logout', async () => {
  authService.removeToken()
  await authService.logout().catch(() => {})
})

interface AuthState {
  user:    AuthUser | null
  loading: boolean
  error:   string | null
}

const authSlice = createSlice({
  name: 'auth',
  initialState: { user: null, loading: false, error: null } as AuthState,
  reducers: {
    clearAuthError(state) { state.error = null },
  },
  extraReducers: builder => {
    builder
      .addCase(login.pending, state => { state.loading = true; state.error = null })
      .addCase(login.fulfilled, (state, action: PayloadAction<AuthUser>) => {
        state.loading = false
        state.user    = action.payload
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false
        state.error   = action.payload as string
      })
      .addCase(logout.fulfilled, state => { state.user = null })
  },
})

export const { clearAuthError } = authSlice.actions
export default authSlice.reducer

export const selectCurrentUser = (state: RootState) => state.auth.user
export const selectIsAdmin     = (state: RootState) => state.auth.user?.role === 'admin'
export const selectAuthLoading = (state: RootState) => state.auth.loading
export const selectAuthError   = (state: RootState) => state.auth.error
```

---

## 11.7 Redux DevTools

Install the Redux DevTools browser extension.

What you get:
- **State tree** — full store state at any point in time
- **Action log** — every action dispatched, with its payload
- **Time-travel** — jump back to any previous state
- **Diff view** — see exactly what changed after each action

```
Actions dispatched in order:
  employees/fetchAll/pending   { }
  employees/fetchAll/fulfilled { employees: [...10 items] }
  employees/setFilter          { payload: "Engineering" }
  employees/setSearch          { payload: "alice" }
```

---

## 11.8 Redux vs Context — Choose Right

| | Context API | Redux Toolkit |
|--|------------|---------------|
| Setup | Zero | Install + configure store |
| DevTools | No | Yes (time-travel, diff) |
| Async | Manual (useEffect) | createAsyncThunk |
| Performance | Rerenders whole subtree | Granular selector subscriptions |
| Best for | Theme, auth, small shared state | Complex app state, server data |

---

## Summary

- Redux Toolkit = modern Redux — no boilerplate, Immer built in
- Slices bundle state + reducers + actions together
- `createAsyncThunk` handles loading/success/error lifecycle for API calls
- `createSelector` = memoised selectors that only recalculate when needed
- Use `useAppDispatch` and `useAppSelector` (typed wrappers) — never the raw versions

**Next → Module 12: Authentication**
