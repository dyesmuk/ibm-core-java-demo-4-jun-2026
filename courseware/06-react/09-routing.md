# Module 09 — Multi-Page Feeling with React Router

## Learning Objectives
- Understand client-side vs server-side routing
- Set up React Router v6 with nested routes and layouts
- Use `Link`, `NavLink`, `useNavigate`, `useParams`, `useSearchParams`
- Build protected routes
- EMS: Home, Employee List, Employee Detail, Create, and 404 pages

---

## 9.1 SPA Routing Explained

Traditional website: every URL causes a full page reload from the server.

SPA with React Router: one HTML file, React intercepts navigation and swaps components — no page reload, instant transitions.

```
Traditional:
  /employees      → server sends employees.html (full reload)
  /employees/1    → server sends detail.html     (full reload)

SPA (React Router):
  /employees      → React swaps in <EmployeesPage />   (instant)
  /employees/1    → React swaps in <EmployeeDetailPage /> (instant)
```

```bash
npm install react-router-dom
```

---

## 9.2 Core API Reference

| API | Purpose |
|-----|---------|
| `<BrowserRouter>` | Provides routing context via the History API |
| `<Routes>` | Container that picks the best matching `<Route>` |
| `<Route path element>` | Maps a URL pattern to a component |
| `<Link to>` | Navigate without page reload |
| `<NavLink to>` | Like Link, adds `active` class when route matches |
| `<Outlet />` | Where child routes render inside a layout |
| `<Navigate to>` | Declarative redirect |
| `useNavigate()` | Programmatic navigation |
| `useParams()` | Extract `:param` values from URL |
| `useSearchParams()` | Read/write `?key=value` query string |
| `useLocation()` | Current location object |

---

## 9.3 Setup — Wrap App in BrowserRouter

```tsx
// src/main.tsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App.tsx'
import './index.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>
)
```

---

## 9.4 Route Structure

```tsx
// src/App.tsx
import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/layout/Layout'
import HomePage from './pages/HomePage'
import EmployeesPage from './pages/EmployeesPage'
import EmployeeDetailPage from './pages/EmployeeDetailPage'
import CreateEmployeePage from './pages/CreateEmployeePage'
import NotFoundPage from './pages/NotFoundPage'
import ProtectedRoute from './components/ProtectedRoute'

function App() {
  return (
    <Routes>
      {/* Layout route — all children share the same header/nav */}
      <Route path="/" element={<Layout />}>

        {/* Index route — renders at exactly "/" */}
        <Route index element={<HomePage />} />

        {/* Public routes */}
        <Route path="employees"    element={<EmployeesPage />} />
        <Route path="employees/:id" element={<EmployeeDetailPage />} />

        {/* Protected routes — require auth */}
        <Route element={<ProtectedRoute />}>
          <Route path="employees/create" element={<CreateEmployeePage />} />
        </Route>

        {/* Redirect legacy path */}
        <Route path="staff" element={<Navigate to="/employees" replace />} />

        {/* 404 — must be last */}
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
```

---

## 9.5 Layout with `<Outlet />`

The `<Outlet />` is where child routes render inside the layout.

```tsx
// src/components/layout/Layout.tsx
import { Outlet } from 'react-router-dom'
import NavBar from './NavBar'

function Layout() {
  return (
    <div>
      <NavBar />
      <main>
        <Outlet />    {/* child route renders here */}
      </main>
    </div>
  )
}

export default Layout
```

```tsx
// src/components/layout/NavBar.tsx
import { NavLink } from 'react-router-dom'

function NavBar() {
  return (
    <nav>
      <span>🏢 IBM EMS</span>
      <NavLink to="/">Home</NavLink>
      <NavLink to="/employees">Employees</NavLink>
      {/* NavLink automatically adds class="active" when route matches */}
    </nav>
  )
}

export default NavBar
```

---

## 9.6 `useNavigate` — Programmatic Navigation

```tsx
import { useNavigate } from 'react-router-dom'

function CreateEmployeePage() {
  const navigate = useNavigate()

  const handleSubmit = async (data: CreateEmployeeDto) => {
    const newEmployee = await employeeService.create(data)
    navigate(`/employees/${newEmployee.id}`)    // go to detail page
    // navigate(-1)                             // go back one step
    // navigate('/employees', { replace: true }) // replace history entry
  }
}
```

---

## 9.7 `useParams` — URL Parameters

```tsx
// Route definition:  path="employees/:id"

// src/pages/EmployeeDetailPage.tsx
import { useParams } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { employeeService } from '../services/employeeService'
import type { Employee } from '../types'

function EmployeeDetailPage() {
  const { id } = useParams<{ id: string }>()   // id is always a string
  const [employee, setEmployee] = useState<Employee | null>(null)
  const [loading,  setLoading]  = useState(true)
  const [error,    setError]    = useState<string | null>(null)

  useEffect(() => {
    if (!id) return

    const load = async () => {
      try {
        setLoading(true)
        const data = await employeeService.getById(Number(id))
        setEmployee(data)
      } catch {
        setError('Employee not found')
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [id])

  if (loading) return <p>Loading…</p>
  if (error)   return <p>{error}</p>
  if (!employee) return null

  return (
    <div>
      <h1>{employee.name}</h1>
      <p>{employee.department}</p>
      <p>{employee.email}</p>
      <p>${employee.salary.toLocaleString()} / yr</p>
    </div>
  )
}

export default EmployeeDetailPage
```

---

## 9.8 `useSearchParams` — Query String

```tsx
// URL: /employees?dept=Engineering&search=alice&page=2

import { useSearchParams } from 'react-router-dom'

function EmployeesPage() {
  const [searchParams, setSearchParams] = useSearchParams()

  const dept   = searchParams.get('dept')   ?? 'All'
  const search = searchParams.get('search') ?? ''
  const page   = Number(searchParams.get('page') ?? '1')

  const updateFilter = (dept: string) => {
    setSearchParams(prev => {
      prev.set('dept', dept)
      prev.set('page', '1')   // reset to first page on filter change
      return prev
    })
  }

  // Now the filter is reflected in the URL — shareable, back-button works
}
```

---

## 9.9 Protected Routes

```tsx
// src/components/ProtectedRoute.tsx
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function ProtectedRoute() {
  const { user } = useAuth()
  const location = useLocation()

  if (!user) {
    // Redirect to login, but remember where they were going
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  return <Outlet />   // render child routes if authenticated
}

export default ProtectedRoute
```

```tsx
// After login, redirect back to the originally requested page
function LoginPage() {
  const navigate   = useNavigate()
  const location   = useLocation()
  const { login }  = useAuth()

  const from = (location.state as { from?: Location })?.from?.pathname ?? '/'

  const handleLogin = async (credentials: LoginCredentials) => {
    await login(credentials)
    navigate(from, { replace: true })   // go back to where they were
  }
}
```

---

## 9.10 EMS Pages

```tsx
// src/pages/HomePage.tsx
import { Link } from 'react-router-dom'

function HomePage() {
  return (
    <div>
      <h1>Welcome to IBM EMS</h1>
      <p>Manage your organisation's workforce in one place.</p>
      <Link to="/employees">View All Employees →</Link>
    </div>
  )
}

export default HomePage
```

```tsx
// src/pages/NotFoundPage.tsx
import { Link } from 'react-router-dom'

function NotFoundPage() {
  return (
    <div>
      <h1>404 — Page Not Found</h1>
      <p>The page you're looking for doesn't exist.</p>
      <Link to="/">Go Home</Link>
    </div>
  )
}

export default NotFoundPage
```

---

## 9.11 Final Route Map

```
/                      → HomePage
/employees             → EmployeesPage   (list + filter + search)
/employees/:id         → EmployeeDetailPage
/employees/create      → CreateEmployeePage   [protected]
/employees/:id/edit    → EditEmployeePage     [protected]
/login                 → LoginPage
/departments           → DepartmentsPage
*                      → NotFoundPage (404)
```

---

## Summary

| API | Use for |
|-----|---------|
| `<BrowserRouter>` | Wrap entire app once in main.tsx |
| `<Routes> + <Route>` | Define URL → component mappings |
| `<Outlet />` | Render child routes inside layout |
| `<Link>` | Navigate without reload (replaces `<a>`) |
| `<NavLink>` | Nav links that show active state |
| `useNavigate()` | Navigate after events (form submit, delete) |
| `useParams()` | Get `:id` from URL |
| `useSearchParams()` | Sync filter/sort/page state to URL |
| `<ProtectedRoute>` | Guard routes that need authentication |

**Next → Module 10: Forms and Validation**
