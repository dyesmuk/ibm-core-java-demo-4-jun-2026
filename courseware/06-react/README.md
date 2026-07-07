# IBM EMS — React + TypeScript Courseware

> **Stack:** React 19 · TypeScript · Vite · React Router 6 · Redux Toolkit · Axios · React Hook Form · Zod · Vitest  
> **Project:** Employee Management System — built incrementally across every module  
> **Audience:** Java developers entering Full Stack; prior JS/TS knowledge from the JS courseware assumed

---

## Module Index

| # | File | Topics Covered |
|---|------|---------------|
| 00 | `00-getting-started.md` | What is React, Vite scaffold, project anatomy, execution flow, cleaning boilerplate |
| 01 | `01-js-ts-refresh.md` | ES6+, arrow functions, destructuring, spread, async/await, TypeScript essentials |
| 02 | `02-jsx-and-components.md` | JSX rules, function components, composition, file conventions, first EmployeeCard |
| 03 | `03-props-state-events.md` | Props, useState, events, object/array state, lifting state up |
| 04 | `04-lists-and-conditionals.md` | `.map()`, key prop, all conditional rendering patterns, department filter, empty state |
| 05 | `05-styling.md` | Global CSS, CSS Modules, custom properties, clsx, dynamic classes, dark mode |
| 06 | `06-debugging.md` | React DevTools, console strategies, 7 common bugs, TypeScript as debugger, Error Boundaries |
| 07 | `07-deep-dive-components.md` | useEffect, useRef, useMemo, useCallback, React.memo, custom hooks, Context API |
| 08 | `08-http-ajax.md` | Axios setup, interceptors, typed service layer, loading/error/data pattern, cancel on unmount |
| 09 | `09-routing.md` | React Router v6, nested routes, Outlet, useParams, useSearchParams, protected routes |
| 10 | `10-forms-validation.md` | Controlled inputs, all input types, React Hook Form, Zod, cross-field validation, edit forms |
| 11 | `11-redux.md` | Redux Toolkit, configureStore, slices, createAsyncThunk, createSelector, DevTools |
| 12 | `12-authentication.md` | JWT flow, AuthContext, mock auth, login page, ProtectedRoute, role-based UI |
| 13 | `13-testing.md` | Vitest, React Testing Library, userEvent, renderHook, MSW, Redux slice tests |
| 14 | `14-deployment.md` | Production build, env vars, Vercel, Netlify, Docker, nginx, GitHub Actions CI/CD |
| 15 | `15-bonus-and-roundup.md` | Webpack, Next.js App Router, Framer Motion, Redux Saga, React 19 hooks, ecosystem map |
| 16 | `16-mcq-assessment.md` | 75 MCQs with answers covering all modules |

---

## Quick Start

```bash
npm create vite@latest ibm-ems-app -- --template react-ts
cd ibm-ems-app
npm install
npm run dev
```

Then follow the modules in order. Each module builds on the previous.

---

## Suggested Delivery Schedule

| Day | Modules | Topics |
|-----|---------|--------|
| Day 1 AM | 00, 01 | Setup, JS/TS refresh, project tour |
| Day 1 PM | 02, 03 | JSX, components, props, state, events |
| Day 2 AM | 04, 05 | Lists, conditionals, styling |
| Day 2 PM | 06, 07 | Debugging, advanced hooks, custom hooks, Context |
| Day 3 AM | 08, 09 | HTTP/Axios, React Router |
| Day 3 PM | 10 | Forms, React Hook Form, Zod validation |
| Day 4 AM | 11 | Redux Toolkit |
| Day 4 PM | 12 | Authentication, JWT, protected routes |
| Day 5 AM | 13 | Testing |
| Day 5 PM | 14, 15 | Deployment, bonus topics |
| Assessment | 16 | 75-question MCQ |

---

## What Gets Built Module by Module

```
Module 02 → EmployeeCard component (hardcoded)
Module 03 → EmployeeCard with props, add/remove employees with useState
Module 04 → Department filter, search, empty state with useMemo
Module 05 → CSS Modules styling, design tokens, dark mode ready
Module 06 → ErrorBoundary, debugging tools
Module 07 → useEmployees custom hook, ThemeContext — App.tsx is clean
Module 08 → Replace seed data with Axios API calls, loading/error states
Module 09 → Multi-page app: /, /employees, /employees/:id, /login, /404
Module 10 → Create Employee form, Edit Employee form, Zod validation
Module 11 → State moved to Redux store, DevTools debugging
Module 12 → Login page, auth token, protected create/edit/delete routes
Module 13 → Full test suite: components, hooks, pages, API mocks
Module 14 → Production build → deployed to Vercel with CI/CD
```

---

## React 19 Features Used

| Feature | Module | What it does |
|---------|--------|--------------|
| `useOptimistic` | 15 (Bonus) | Instant UI updates before server confirms |
| `useActionState` | 15 (Bonus) | Form state tied to async actions |
| `useFormStatus` | 15 (Bonus) | Read form pending state from child |
| `createRoot` | 00 | Mount the app (was already React 18+) |
| Async params | 15 (Bonus) | Next.js: `params` is now a Promise |

---

## IBM Topics Mapping

IBM course topics → courseware modules:

| IBM Topic | Module |
|-----------|--------|
| Getting Started | 00 |
| Next Generation JavaScript (Optional) | 01 |
| Base Features & Syntax | 02, 03 |
| Lists and Conditionals | 04 |
| Styling | 05 |
| Debugging | 06 |
| Deep Dive Components & React Internals | 07 |
| Real App (EMS — replaces Burger Builder) | 03–12 |
| Reaching out to the Web (HTTP/Ajax) | 08 |
| Routing | 09 |
| Forms and Validation | 10 |
| Redux | 11 |
| Authentication | 12 |
| Testing | 13 |
| Deploying | 14 |
| Webpack (Bonus) | 15 |
| Next.js (Bonus) | 15 |
| Animations (Bonus) | 15 |
| Redux Saga (Bonus) | 15 |
| React Hooks Reference | 15 |
| Course Roundup | 15 |

---

## Key Patterns to Know for the MCQ

```tsx
// 1. useState array update — never mutate
setEmployees(prev => [...prev, newEmp])                         // add
setEmployees(prev => prev.filter(e => e.id !== id))            // remove
setEmployees(prev => prev.map(e => e.id === id ? {...e, ...changes} : e)) // update

// 2. Functional update for derived state
setCount(prev => prev + 1)

// 3. useEffect patterns
useEffect(() => { fetchData() }, [])                           // once on mount
useEffect(() => { refetch(id) }, [id])                        // when id changes
useEffect(() => {
  const sub = subscribe()
  return () => sub.unsubscribe()                               // cleanup
}, [])

// 4. List rendering
{items.map(item => <Card key={item.id} item={item} />)}

// 5. Safe deep access
const city = user?.address?.city ?? 'Unknown'

// 6. Axios with loading/error
const [data, setData]     = useState<Employee[]>([])
const [loading, setLoading] = useState(true)
const [error, setError]   = useState<string | null>(null)

// 7. Redux dispatch
dispatch(fetchEmployees())
dispatch(setFilter('Engineering'))

// 8. Protected route pattern
if (!isAuthenticated) return <Navigate to="/login" state={{ from: location }} replace />
return <Outlet />

// 9. Form submit with prevent default
const handleSubmit = (e: React.FormEvent) => {
  e.preventDefault()
  // process
}

// 10. Lazy loading
const Page = lazy(() => import('./pages/Page'))
<Suspense fallback={<p>Loading…</p>}><Page /></Suspense>
```

---

## Tech Versions

```
react                   19.x
react-dom               19.x
typescript              5.x
vite                    6.x
react-router-dom        6.x
@reduxjs/toolkit        2.x
react-redux             9.x
axios                   1.x
react-hook-form         7.x
zod                     3.x
@hookform/resolvers     3.x
clsx                    2.x
vitest                  2.x
@testing-library/react  16.x
@testing-library/user-event 14.x
msw                     2.x
```
