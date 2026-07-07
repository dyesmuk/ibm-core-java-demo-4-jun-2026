# Module 08 — Reaching Out to the Web: HTTP & Ajax with Axios

## Learning Objectives
- Understand why Axios is preferred over raw `fetch`
- Set up a centralised Axios instance with interceptors
- Build a typed service layer
- Handle loading, error, and success states cleanly
- EMS: replace seed data with real API calls (JSONPlaceholder as mock backend)

---

## 8.1 Why Axios over `fetch`?

| Feature | `fetch` (built-in) | `axios` |
|---------|--------------------|---------|
| Auto JSON parse response | ❌ manual `.json()` | ✅ automatic |
| Throws on 4xx/5xx | ❌ only throws on network failure | ✅ throws for all error status codes |
| Request interceptors | ❌ | ✅ |
| Response interceptors | ❌ | ✅ |
| Base URL config | ❌ | ✅ |
| Request timeout | ❌ manual AbortController | ✅ one option |
| TypeScript generics | minimal | ✅ excellent (`axios.get<Employee[]>`) |
| Upload progress | ❌ | ✅ |

```bash
npm install axios
```

---

## 8.2 Environment Variables

Vite uses `.env` files. Only variables prefixed with `VITE_` are exposed to your frontend code.

```bash
# .env
VITE_API_BASE_URL=https://jsonplaceholder.typicode.com

# .env.production
VITE_API_BASE_URL=https://api.ibm-ems.com/v1
```

```ts
// Access in code
const baseURL = import.meta.env.VITE_API_BASE_URL
const isDev   = import.meta.env.DEV     // boolean
const mode    = import.meta.env.MODE    // 'development' | 'production'
```

> **Never put secrets (private keys, DB passwords) in frontend env files** — they are visible in the production bundle.

---

## 8.3 Centralised Axios Instance

Never call `axios.get(...)` directly in components. Create one configured instance, import it everywhere.

```ts
// src/services/api.ts
import axios from 'axios'
import type { AxiosError } from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'https://jsonplaceholder.typicode.com',
  timeout: 10_000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

// ── Request interceptor ──────────────────────────────────────────────
// Runs before EVERY request — great place to attach auth tokens
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('ems_token')
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  },
  error => Promise.reject(error)
)

// ── Response interceptor ─────────────────────────────────────────────
// Runs after EVERY response — handle global errors once
api.interceptors.response.use(
  response => response,   // 2xx → pass through
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('ems_token')
      window.location.href = '/login'   // force re-login
    }
    if (error.response?.status >= 500) {
      console.error('Server error:', error.response.data)
    }
    return Promise.reject(error)
  }
)

export default api
```

---

## 8.4 Typed Service Layer

Each feature gets its own service file. Services make API calls and return typed data — components never import `api` directly.

### Update types

```ts
// src/types/index.ts
export interface Employee {
  id: number
  name: string
  email: string
  department: string
  salary: number
  isActive: boolean
  joinDate: string
  phone?: string
  address?: { city: string; country: string }
}

export interface CreateEmployeeDto {
  name: string
  email: string
  department: string
  salary: number
}

export type UpdateEmployeeDto = Partial<CreateEmployeeDto>
export type Department = 'All' | 'Engineering' | 'Marketing' | 'HR' | 'Finance' | 'Sales'
```

### Employee service

```ts
// src/services/employeeService.ts
import api from './api'
import type { Employee, CreateEmployeeDto, UpdateEmployeeDto } from '../types'

// JSONPlaceholder /users response shape
interface JPUser {
  id: number
  name: string
  email: string
  phone: string
  company: { name: string }
  address: { city: string; zipcode: string }
}

const DEPARTMENTS = ['Engineering', 'Marketing', 'HR', 'Finance', 'Sales'] as const

// Map JSONPlaceholder user → our Employee shape
function mapUser(user: JPUser): Employee {
  return {
    id:         user.id,
    name:       user.name,
    email:      user.email,
    phone:      user.phone,
    department: DEPARTMENTS[user.id % DEPARTMENTS.length],
    salary:     55000 + (user.id * 7333) % 60000,
    isActive:   user.id % 4 !== 0,
    joinDate:   `202${user.id % 4}-0${(user.id % 9) + 1}-15`,
    address:    { city: user.address.city, country: 'India' },
  }
}

export const employeeService = {
  getAll: async (): Promise<Employee[]> => {
    const { data } = await api.get<JPUser[]>('/users')
    return data.map(mapUser)
  },

  getById: async (id: number): Promise<Employee> => {
    const { data } = await api.get<JPUser>(`/users/${id}`)
    return mapUser(data)
  },

  create: async (dto: CreateEmployeeDto): Promise<Employee> => {
    // JSONPlaceholder fakes creation — echoes back what you sent + id: 11
    const { data } = await api.post<JPUser>('/users', dto)
    return { ...mapUser(data), ...dto, id: Date.now() }
  },

  update: async (id: number, dto: UpdateEmployeeDto): Promise<Employee> => {
    const { data } = await api.put<JPUser>(`/users/${id}`, dto)
    return { ...mapUser(data), ...dto }
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/users/${id}`)
  },
}
```

---

## 8.5 Loading, Error, and Success State Pattern

Every async operation needs three pieces of state:

```tsx
// src/hooks/useEmployeesApi.ts
import { useState, useEffect, useCallback } from 'react'
import type { Employee } from '../types'
import { employeeService } from '../services/employeeService'

interface UseEmployeesApiReturn {
  employees: Employee[]
  loading:   boolean
  error:     string | null
  reload:    () => void
  remove:    (id: number) => Promise<void>
}

export function useEmployeesApi(): UseEmployeesApiReturn {
  const [employees, setEmployees] = useState<Employee[]>([])
  const [loading,   setLoading]   = useState(true)
  const [error,     setError]     = useState<string | null>(null)

  const fetchEmployees = useCallback(async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await employeeService.getAll()
      setEmployees(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load employees')
    } finally {
      setLoading(false)   // always runs — hides spinner
    }
  }, [])

  useEffect(() => {
    fetchEmployees()
  }, [fetchEmployees])

  const remove = useCallback(async (id: number) => {
    try {
      await employeeService.delete(id)
      setEmployees(prev => prev.filter(e => e.id !== id))
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to delete employee')
    }
  }, [])

  return { employees, loading, error, reload: fetchEmployees, remove }
}
```

### Using it in the page

```tsx
// src/pages/EmployeesPage.tsx
import { useEmployeesApi } from '../hooks/useEmployeesApi'
import EmployeeCard from '../components/EmployeeCard'

function EmployeesPage() {
  const { employees, loading, error, reload, remove } = useEmployeesApi()

  if (loading) return <p>Loading employees…</p>
  if (error)   return (
    <div>
      <p>Error: {error}</p>
      <button onClick={reload}>Retry</button>
    </div>
  )

  return (
    <div>
      <h1>Employees ({employees.length})</h1>
      {employees.map(emp => (
        <EmployeeCard key={emp.id} employee={emp} onRemove={remove} />
      ))}
    </div>
  )
}

export default EmployeesPage
```

---

## 8.6 Error Handling — Axios Specifics

```tsx
import axios from 'axios'

async function fetchWithErrorHandling() {
  try {
    const { data } = await api.get<Employee[]>('/employees')
    return data
  } catch (err) {
    if (axios.isAxiosError(err)) {
      // HTTP error from the server
      const status  = err.response?.status
      const message = err.response?.data?.message ?? err.message
      console.error(`API error ${status}:`, message)
      throw new Error(message)
    }
    // Network error, timeout, etc.
    throw err
  }
}
```

---

## 8.7 Cancel Requests on Unmount

```tsx
useEffect(() => {
  const controller = new AbortController()

  const load = async () => {
    try {
      const { data } = await api.get<Employee[]>('/users', {
        signal: controller.signal
      })
      setEmployees(data.map(mapUser))
    } catch (err) {
      if (axios.isCancel(err)) return   // ignore cancellation
      setError('Failed to load')
    } finally {
      setLoading(false)
    }
  }

  load()
  return () => controller.abort()   // cancel if component unmounts
}, [])
```

---

## 8.8 The Complete EMS Service Architecture

```
src/
├── services/
│   ├── api.ts                    ← Axios instance + interceptors
│   ├── employeeService.ts        ← Employee CRUD
│   └── departmentService.ts      ← Department calls (later)
├── hooks/
│   ├── useEmployeesApi.ts        ← Fetch, loading, error state
│   └── useEmployeeDetail.ts      ← Single employee
└── pages/
    ├── EmployeesPage.tsx
    └── EmployeeDetailPage.tsx
```

---

## Summary

- Never use `axios` directly in components — always go through the service layer
- Centralise auth token attachment in the request interceptor
- Handle 401 globally in the response interceptor
- Every async operation needs `loading`, `error`, and `data` state
- Extract API hooks into `src/hooks/` — keeps pages clean
- Cancel in-flight requests on unmount to avoid memory leaks

**Next → Module 09: Multi-Page Routing**
