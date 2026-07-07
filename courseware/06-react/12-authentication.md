# Module 12 — Adding Authentication

## Learning Objectives
- Implement a complete login/logout flow with JWT
- Store tokens securely and attach them to requests
- Protect routes so only authenticated users can access them
- Show role-based UI elements
- EMS: Login page, AuthContext, protected routes for create/edit/delete

---

## 12.1 Auth Flow Overview

```
User visits /employees/create
        ↓
ProtectedRoute checks for token
        ↓
    ┌───────────────┐
  No token       Token found
    ↓                 ↓
Redirect         Decode & verify
to /login            ↓
            ┌────────────────┐
          Invalid         Valid
            ↓                ↓
        Redirect       Set user in context
        to /login       → render the page
```

---

## 12.2 Auth Service

```ts
// src/services/authService.ts
import api from './api'

export interface LoginCredentials {
  email:    string
  password: string
}

export interface AuthUser {
  id:    number
  name:  string
  email: string
  role:  'admin' | 'manager' | 'user'
  token: string
}

const TOKEN_KEY = 'ems_token'

export const authService = {
  login: async (credentials: LoginCredentials): Promise<AuthUser> => {
    const { data } = await api.post<AuthUser>('/auth/login', credentials)
    return data
  },

  logout: async (): Promise<void> => {
    await api.post('/auth/logout').catch(() => {})   // fire and forget
  },

  getProfile: async (): Promise<AuthUser> => {
    const { data } = await api.get<AuthUser>('/auth/me')
    return data
  },

  saveToken:   (token: string) => localStorage.setItem(TOKEN_KEY, token),
  getToken:    ()               => localStorage.getItem(TOKEN_KEY),
  removeToken: ()               => localStorage.removeItem(TOKEN_KEY),
}
```

### Mock auth (until you have a real backend)

```ts
// src/services/mockAuthService.ts
import type { LoginCredentials, AuthUser } from './authService'

const MOCK_USERS = [
  { id: 1, name: 'Admin User',   email: 'admin@ibm.com',   password: 'admin123',  role: 'admin'   as const },
  { id: 2, name: 'Manager Bob',  email: 'manager@ibm.com', password: 'manager123',role: 'manager' as const },
  { id: 3, name: 'Regular User', email: 'user@ibm.com',    password: 'user123',   role: 'user'    as const },
]

export const mockAuthService = {
  login: async ({ email, password }: LoginCredentials): Promise<AuthUser> => {
    await new Promise(r => setTimeout(r, 600))   // simulate network delay

    const found = MOCK_USERS.find(u => u.email === email && u.password === password)
    if (!found) throw new Error('Invalid email or password')

    const token = `mock-jwt-${found.id}-${Date.now()}`
    const { password: _, ...user } = found
    return { ...user, token }
  },

  logout:     async () => {},
  getProfile: async (): Promise<AuthUser> => {
    throw new Error('Not implemented in mock')
  },

  saveToken:   authService.saveToken,
  getToken:    authService.getToken,
  removeToken: authService.removeToken,
}
```

---

## 12.3 Auth Context

```tsx
// src/context/AuthContext.tsx
import { createContext, useContext, useState, useEffect } from 'react'
import type { ReactNode } from 'react'
import { mockAuthService as authService } from '../services/mockAuthService'
import type { AuthUser, LoginCredentials } from '../services/authService'

interface AuthContextValue {
  user:          AuthUser | null
  isLoading:     boolean
  login:         (credentials: LoginCredentials) => Promise<void>
  logout:        () => Promise<void>
  isAuthenticated: boolean
  isAdmin:       boolean
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user,      setUser]      = useState<AuthUser | null>(null)
  const [isLoading, setIsLoading] = useState(true)   // checking token on mount

  // On mount — check if there's a saved session
  useEffect(() => {
    const token = authService.getToken()
    if (!token) {
      setIsLoading(false)
      return
    }
    // In production: verify token with server
    // authService.getProfile()
    //   .then(setUser)
    //   .catch(() => authService.removeToken())
    //   .finally(() => setIsLoading(false))

    // For mock: just clear loading
    setIsLoading(false)
  }, [])

  const login = async (credentials: LoginCredentials) => {
    const loggedInUser = await authService.login(credentials)
    authService.saveToken(loggedInUser.token)
    setUser(loggedInUser)
  }

  const logout = async () => {
    await authService.logout()
    authService.removeToken()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{
      user,
      isLoading,
      login,
      logout,
      isAuthenticated: !!user,
      isAdmin:         user?.role === 'admin',
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>')
  return ctx
}
```

```tsx
// src/main.tsx — add AuthProvider
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>
      <BrowserRouter>
        <AuthProvider>
          <App />
        </AuthProvider>
      </BrowserRouter>
    </Provider>
  </StrictMode>
)
```

---

## 12.4 Login Page

```tsx
// src/pages/LoginPage.tsx
import { useState } from 'react'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function LoginPage() {
  const { login, isAuthenticated } = useAuth()
  const navigate  = useNavigate()
  const location  = useLocation()

  const [email,    setEmail]    = useState('')
  const [password, setPassword] = useState('')
  const [error,    setError]    = useState<string | null>(null)
  const [loading,  setLoading]  = useState(false)

  // Already logged in → redirect
  const from = (location.state as { from?: { pathname: string } })?.from?.pathname ?? '/'
  if (isAuthenticated) {
    navigate(from, { replace: true })
    return null
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    try {
      await login({ email, password })
      navigate(from, { replace: true })   // go to originally requested page
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <h1>Sign in to IBM EMS</h1>

      <form onSubmit={handleSubmit} noValidate>
        <label htmlFor="email">Email</label>
        <input
          id="email"
          type="email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          placeholder="admin@ibm.com"
          autoComplete="email"
          required
        />

        <label htmlFor="password">Password</label>
        <input
          id="password"
          type="password"
          value={password}
          onChange={e => setPassword(e.target.value)}
          placeholder="••••••••"
          autoComplete="current-password"
          required
        />

        {error && <p role="alert">{error}</p>}

        <button type="submit" disabled={loading || !email || !password}>
          {loading ? 'Signing in…' : 'Sign In'}
        </button>
      </form>

      <p>
        Demo credentials: <code>admin@ibm.com</code> / <code>admin123</code>
      </p>
    </div>
  )
}

export default LoginPage
```

---

## 12.5 Protected Route

```tsx
// src/components/ProtectedRoute.tsx
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

interface ProtectedRouteProps {
  requireAdmin?: boolean
}

function ProtectedRoute({ requireAdmin = false }: ProtectedRouteProps) {
  const { isAuthenticated, isAdmin, isLoading } = useAuth()
  const location = useLocation()

  // Still checking token on mount — don't redirect yet
  if (isLoading) return <p>Loading…</p>

  // Not logged in
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  // Logged in but not admin when admin is required
  if (requireAdmin && !isAdmin) {
    return <Navigate to="/unauthorized" replace />
  }

  return <Outlet />
}

export default ProtectedRoute
```

```tsx
// src/App.tsx — use in route config
<Routes>
  <Route path="/" element={<Layout />}>
    <Route index element={<HomePage />} />
    <Route path="employees" element={<EmployeesPage />} />
    <Route path="employees/:id" element={<EmployeeDetailPage />} />
    <Route path="login" element={<LoginPage />} />

    {/* Requires login */}
    <Route element={<ProtectedRoute />}>
      <Route path="employees/create"    element={<CreateEmployeePage />} />
      <Route path="employees/:id/edit"  element={<EditEmployeePage />} />
    </Route>

    {/* Requires admin role */}
    <Route element={<ProtectedRoute requireAdmin />}>
      <Route path="admin/departments"   element={<DepartmentsAdminPage />} />
    </Route>

    <Route path="*" element={<NotFoundPage />} />
  </Route>
</Routes>
```

---

## 12.6 Role-Based UI

```tsx
// Show/hide UI based on role
import { useAuth } from '../context/AuthContext'

function EmployeeCard({ employee, onRemove }: Props) {
  const { isAdmin, user } = useAuth()

  return (
    <div>
      <h3>{employee.name}</h3>
      <p>{employee.department}</p>

      {/* Only admins can delete */}
      {isAdmin && (
        <button onClick={() => onRemove(employee.id)}>Delete</button>
      )}

      {/* Managers and admins can edit */}
      {(user?.role === 'admin' || user?.role === 'manager') && (
        <Link to={`/employees/${employee.id}/edit`}>Edit</Link>
      )}

      {/* Users can view their own profile */}
      {user?.id === employee.id && (
        <span>That's you!</span>
      )}
    </div>
  )
}
```

---

## 12.7 Logout

```tsx
// Anywhere in the nav — the logout button
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'

function NavBar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/login', { replace: true })
  }

  return (
    <nav>
      <span>IBM EMS</span>
      {user
        ? (
          <>
            <span>Hello, {user.name}</span>
            <button onClick={handleLogout}>Sign Out</button>
          </>
        )
        : <Link to="/login">Sign In</Link>
      }
    </nav>
  )
}
```

---

## 12.8 Token Storage — Security Note

| Storage | XSS risk | CSRF risk | Accessible in JS |
|---------|----------|-----------|-----------------|
| `localStorage` | ⚠️ Yes | ✅ No | Yes |
| `sessionStorage` | ⚠️ Yes | ✅ No | Yes |
| `httpOnly` cookie | ✅ No | ⚠️ Yes (mitigated) | No |

**For this training:** `localStorage` is fine for learning and demos.  
**For production:** store JWT in an `httpOnly` cookie — inaccessible to JavaScript, protects against XSS.

---

## Summary

- Auth state lives in Context (or Redux `authSlice`) — not component state
- `ProtectedRoute` uses `<Outlet />` — same pattern as layout routes
- Save `location` before redirect so you can bounce users back after login
- Role-based UI: check `user.role` in components, not just in routes
- Always call `e.preventDefault()` in the login form handler

**Next → Module 13: Testing**
