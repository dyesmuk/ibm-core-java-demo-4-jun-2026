# Bonus Modules — Webpack, Next.js, Animations & Redux Saga

> These are concept-level overviews. Each topic could fill its own course — the goal here is to give you enough to understand what these tools are, why they exist, and where to dig deeper.

---

# Bonus 01 — Working with Webpack

## What Is Webpack?

Webpack is a **module bundler** — it takes your entire project (JS, CSS, images, fonts, JSON) and bundles it into optimised output files the browser can load.

Vite uses Rollup under the hood, but Webpack is still the most widely used bundler in enterprise projects (Create React App, older projects, many corporate stacks).

## Core Concepts

```
Entry → Dependency Graph → Loaders → Plugins → Output

Entry:   src/main.tsx         ← where Webpack starts
Output:  dist/bundle.js       ← what it produces
Loaders: transform file types (TS→JS, CSS→JS module, SVG→component)
Plugins: do things Loaders can't (HTML injection, env vars, minification)
```

## Minimal `webpack.config.js` for React + TypeScript

```js
const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')

module.exports = {
  entry: './src/main.tsx',

  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: '[name].[contenthash].js',   // content hash = cache busting
    clean: true,
  },

  resolve: {
    extensions: ['.tsx', '.ts', '.js'],    // try these extensions in order
  },

  module: {
    rules: [
      // TypeScript + JSX via Babel
      {
        test: /\.(ts|tsx)$/,
        use: 'babel-loader',
        exclude: /node_modules/,
      },
      // CSS Modules
      {
        test: /\.module\.css$/,
        use: ['style-loader', 'css-loader'],
      },
      // Global CSS
      {
        test: /\.css$/,
        exclude: /\.module\.css$/,
        use: ['style-loader', 'css-loader'],
      },
      // Images
      {
        test: /\.(png|jpg|svg|gif)$/,
        type: 'asset/resource',
      },
    ],
  },

  plugins: [
    new HtmlWebpackPlugin({
      template: './index.html',   // injects <script> automatically
    }),
  ],

  devServer: {
    port: 3000,
    historyApiFallback: true,    // SPA routing — all 404s serve index.html
    hot: true,                   // hot module replacement
  },
}
```

## Key Differences: Webpack vs Vite

| | Webpack | Vite |
|--|---------|------|
| Dev server startup | Bundles everything first (~10-30s) | Serves ESM natively (instant) |
| HMR speed | Rebundles changed module | Only sends the changed file |
| Config | Verbose, explicit | Convention-first, minimal |
| Ecosystem | Massive, mature | Growing fast |
| CRA default | Yes | Yes (CRA is deprecated — use Vite) |
| Enterprise legacy | Very common | Increasingly common |

## When You'll See Webpack

- Legacy React projects (anything before 2022)
- Projects that used Create React App
- Custom enterprise build pipelines
- Next.js uses a custom Webpack (or now Turbopack)

---

# Bonus 02 — Next.js: React for Production

## What Is Next.js?

Next.js is a **React framework** built by Vercel that adds:
- File-based routing (no React Router needed)
- Server-Side Rendering (SSR)
- Static Site Generation (SSG)
- API Routes (backend in the same project)
- Server Components (React 19 feature)
- Built-in image optimisation, fonts, metadata

## Vite React vs Next.js

| | Vite React (what we built) | Next.js |
|--|--------------------------|---------|
| Rendering | Client only (CSR) | SSR / SSG / CSR / streaming |
| Routing | React Router library | File-based (`app/` folder) |
| API | Separate backend | Built-in API routes |
| SEO | Poor (empty HTML on first load) | Excellent (pre-rendered HTML) |
| Setup | Minimal | More opinionated |

## App Router (Next.js 13+)

```
app/
├── layout.tsx              ← Root layout (header, nav) — always rendered
├── page.tsx                ← Route: /
├── employees/
│   ├── page.tsx            ← Route: /employees
│   └── [id]/
│       └── page.tsx        ← Route: /employees/123
└── api/
    └── employees/
        └── route.ts        ← API endpoint: /api/employees
```

```tsx
// app/employees/page.tsx — Server Component (default in App Router)
// Runs on the server — can fetch data directly, no useEffect needed

async function EmployeesPage() {
  // Direct server-side fetch — not visible in the browser bundle
  const response = await fetch('https://api.ibm-ems.com/employees', {
    next: { revalidate: 60 },   // cache for 60 seconds (ISR)
  })
  const employees = await response.json()

  return (
    <div>
      <h1>Employees</h1>
      {employees.map((emp: Employee) => (
        <p key={emp.id}>{emp.name}</p>
      ))}
    </div>
  )
}

export default EmployeesPage
```

```tsx
// app/employees/[id]/page.tsx — Dynamic route
interface Props { params: Promise<{ id: string }> }

async function EmployeeDetailPage({ params }: Props) {
  const { id } = await params   // React 19: params is now a Promise
  const response = await fetch(`https://api.ibm-ems.com/employees/${id}`)
  const employee = await response.json()

  return <h1>{employee.name}</h1>
}
```

```ts
// app/api/employees/route.ts — API route (runs on server)
import { NextRequest, NextResponse } from 'next/server'

export async function GET() {
  const employees = await db.findAll()
  return NextResponse.json(employees)
}

export async function POST(request: NextRequest) {
  const body = await request.json()
  const newEmployee = await db.create(body)
  return NextResponse.json(newEmployee, { status: 201 })
}
```

## When to Use Next.js

- SEO is important (marketing pages, public employee directory)
- First-load performance is critical
- You want backend + frontend in one project
- Building a full-stack app with a single team

---

# Bonus 03 — Animations in React

## CSS Transitions (Zero JS)

```css
/* EmployeeCard.module.css */

.card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

/* Fade in on mount with CSS @keyframes */
.fadeIn {
  animation: fadeIn 0.3s ease forwards;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}
```

## Framer Motion — Production-Grade Animations

```bash
npm install framer-motion
```

```tsx
import { motion, AnimatePresence } from 'framer-motion'

// Animate on mount
function EmployeeCard({ employee }: Props) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, scale: 0.95 }}
      transition={{ duration: 0.2 }}
    >
      <h3>{employee.name}</h3>
    </motion.div>
  )
}

// Animate list — items fade in/out when filtered
function EmployeeList({ employees }: Props) {
  return (
    <div>
      <AnimatePresence mode="popLayout">
        {employees.map(emp => (
          <motion.div
            key={emp.id}
            layout                           // smoothly reflows remaining items
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: 20 }}
          >
            <EmployeeCard employee={emp} onRemove={onRemove} />
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  )
}

// Page transition
function PageWrapper({ children }: { children: ReactNode }) {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.15 }}
    >
      {children}
    </motion.div>
  )
}
```

## Key Framer Motion Concepts

| Prop | Purpose |
|------|---------|
| `initial` | State before mount / animation starts |
| `animate` | Target state to animate to |
| `exit` | State to animate to on unmount |
| `transition` | Duration, easing, delay |
| `layout` | Animate layout changes (position, size) |
| `whileHover` | State while cursor is over element |
| `whileTap` | State while element is pressed |
| `drag` | Make element draggable |

---

# Bonus 04 — Redux Saga: Complex Async Flows

## What Is Redux Saga?

Redux Saga is a middleware that handles **complex async side effects** using JavaScript generators. It's an alternative to `createAsyncThunk` for cases where thunks become hard to manage.

## Generator Functions — Quick Intro

```ts
function* counter() {
  yield 1    // pauses here, returns 1
  yield 2    // pauses here, returns 2
  yield 3    // pauses here, returns 3
}

const gen = counter()
gen.next()   // { value: 1, done: false }
gen.next()   // { value: 2, done: false }
gen.next()   // { value: 3, done: true }
```

Sagas use `yield` to pause while waiting for async operations — but written synchronously.

## Saga vs Thunk

| | Thunk | Saga |
|--|-------|------|
| Complexity | Simple async | Complex flows |
| Cancellation | Manual AbortController | Built-in `takeLatest` |
| Race conditions | Manual | `race()` effect |
| Testing | Mock functions | Pure effect objects |
| Readability | Good for simple | Better for complex sequences |

## Basic Saga Example

```bash
npm install redux-saga
```

```ts
// src/features/employees/employeesSaga.ts
import { call, put, takeLatest, takeEvery } from 'redux-saga/effects'
import { employeeService } from '../../services/employeeService'
import {
  fetchEmployeesStart,
  fetchEmployeesSuccess,
  fetchEmployeesFailure,
  deleteEmployeeSuccess,
} from './employeesSlice'

// Worker saga — handles one fetchEmployees request
function* fetchEmployeesSaga() {
  try {
    const employees: Employee[] = yield call(employeeService.getAll)
    yield put(fetchEmployeesSuccess(employees))
  } catch (err) {
    yield put(fetchEmployeesFailure(err instanceof Error ? err.message : 'Failed'))
  }
}

// Watcher saga — listens for actions and forks workers
// takeLatest: cancels previous in-flight request if action fires again
function* watchFetchEmployees() {
  yield takeLatest(fetchEmployeesStart.type, fetchEmployeesSaga)
}

// Root saga — combines all watchers
export function* rootSaga() {
  yield all([
    watchFetchEmployees(),
    // watchDeleteEmployee(),
    // watchCreateEmployee(),
  ])
}
```

```ts
// Store setup with saga middleware
import createSagaMiddleware from 'redux-saga'
import { configureStore } from '@reduxjs/toolkit'
import { rootSaga } from './features/employees/employeesSaga'

const sagaMiddleware = createSagaMiddleware()

export const store = configureStore({
  reducer: { employees: employeesReducer },
  middleware: getDefault => getDefault().concat(sagaMiddleware),
})

sagaMiddleware.run(rootSaga)   // start the root saga
```

## When to Use Redux Saga

- Long-running operations that can be cancelled
- Debounced search (wait for user to stop typing, then fire)
- Race conditions (fire two requests, use whichever responds first)
- Complex state machines (checkout flow: select → validate → pay → confirm)
- Retry logic with backoff

For most apps: **stick with `createAsyncThunk`**. Reach for Saga when you find yourself building complex orchestration logic inside thunks.

---

# Bonus 05 — React Hooks Complete Reference

## Built-in Hooks — All of Them

| Hook | Purpose |
|------|---------|
| `useState` | Local component state |
| `useReducer` | Complex state with many transitions |
| `useEffect` | Side effects after render |
| `useLayoutEffect` | Side effects before browser paint (DOM measurements) |
| `useRef` | DOM access, mutable value without re-render |
| `useMemo` | Memoise expensive computed value |
| `useCallback` | Memoise function reference |
| `useContext` | Read from a Context |
| `useId` | Generate stable unique ID (for aria, labels) |
| `useImperativeHandle` | Expose API from child to parent via ref |
| `useDeferredValue` | Defer a value update for low-priority rendering |
| `useTransition` | Mark state update as non-urgent |
| `useSyncExternalStore` | Subscribe to external stores (Redux, Zustand) |
| `useDebugValue` | Custom label for DevTools |
| `useInsertionEffect` | CSS-in-JS libraries only |

## React 19 New Hooks

```tsx
// useOptimistic — instant UI update while awaiting server confirmation
import { useOptimistic } from 'react'

function EmployeeList({ employees, onToggle }: Props) {
  const [optimisticEmployees, addOptimisticUpdate] = useOptimistic(
    employees,
    (state, { id, isActive }: { id: number; isActive: boolean }) =>
      state.map(e => e.id === id ? { ...e, isActive } : e)
  )

  const handleToggle = async (id: number, currentActive: boolean) => {
    // Update UI instantly (optimistic)
    addOptimisticUpdate({ id, isActive: !currentActive })
    // Then make the real API call
    await employeeService.update(id, { isActive: !currentActive })
    // On error, React reverts the optimistic update automatically
    onToggle(id)
  }

  return (
    <div>
      {optimisticEmployees.map(emp => (
        <EmployeeCard
          key={emp.id}
          employee={emp}
          onToggle={() => handleToggle(emp.id, emp.isActive)}
          onRemove={onRemove}
        />
      ))}
    </div>
  )
}
```

```tsx
// useFormStatus — read form submission state from inside child components
// (works with React 19 Server Actions)
import { useFormStatus } from 'react-dom'

function SubmitButton() {
  const { pending } = useFormStatus()

  return (
    <button type="submit" disabled={pending}>
      {pending ? 'Saving…' : 'Save Employee'}
    </button>
  )
}
```

```tsx
// useActionState — manage form action state (replaces useState + submit handler)
import { useActionState } from 'react'

async function createEmployeeAction(prevState: string | null, formData: FormData) {
  try {
    await employeeService.create({
      name:       formData.get('name') as string,
      email:      formData.get('email') as string,
      department: formData.get('department') as string,
    })
    return null   // success — clear error
  } catch (err) {
    return err instanceof Error ? err.message : 'Failed to create'
  }
}

function CreateForm() {
  const [error, formAction, isPending] = useActionState(createEmployeeAction, null)

  return (
    <form action={formAction}>
      <input name="name" placeholder="Name" />
      <input name="email" placeholder="Email" />
      <select name="department">
        <option value="Engineering">Engineering</option>
        <option value="HR">HR</option>
      </select>
      {error && <p>{error}</p>}
      <button type="submit" disabled={isPending}>
        {isPending ? 'Creating…' : 'Create'}
      </button>
    </form>
  )
}
```

## Rules of Hooks — Non-Negotiable

```tsx
// ❌ Inside a condition — React can't track hook order
if (isLoggedIn) {
  const [name, setName] = useState('')   // NEVER
}

// ❌ Inside a loop
for (const emp of employees) {
  useEffect(() => { /* ... */ }, [])     // NEVER
}

// ❌ Inside a regular function (not a component or custom hook)
function processData() {
  const [result, setResult] = useState(null)  // NEVER
}

// ✅ Always at the top level of a component or custom hook
function EmployeeCard() {
  const [expanded, setExpanded] = useState(false)   // ✅
  useEffect(() => { /* ... */ }, [])                // ✅
}

// ✅ Custom hooks can call other hooks
function useEmployeeFilter(employees: Employee[]) {
  const [filter, setFilter] = useState('All')         // ✅
  const filtered = useMemo(() => /* ... */, [filter])  // ✅
  return { filter, setFilter, filtered }
}
```

---

# Bonus 06 — Next Steps & Course Roundup

## What You Built

| Feature | Key concepts applied |
|---------|---------------------|
| Employee list with filter + search | `useState`, `useMemo`, `map`, conditional rendering |
| CSS Modules styling | CSS custom properties, scoped classes, `clsx` |
| API integration | Axios, service layer, loading/error states, `useEffect` |
| Multi-page navigation | React Router v6, nested routes, `useParams`, protected routes |
| Create/Edit forms | React Hook Form, Zod validation, controlled inputs |
| Global state | Redux Toolkit, slices, `createAsyncThunk`, `createSelector` |
| Authentication | JWT flow, AuthContext, role-based UI |
| Testing | Vitest, RTL, `userEvent`, MSW |
| Deployment | Vite build, Vercel, Docker, GitHub Actions |

## The React Ecosystem Map

```
Core
  React 19
  TypeScript

Routing
  React Router v6      ← what we used
  TanStack Router      ← type-safe alternative

State Management
  useState / useReducer ← local state
  Context API           ← shared state (small-medium apps)
  Redux Toolkit         ← what we used (large apps)
  Zustand               ← lightweight alternative
  Jotai / Recoil        ← atomic state

Data Fetching
  Axios                 ← what we used
  TanStack Query        ← caching, background sync, infinite scroll

Forms
  React Hook Form       ← what we used
  Zod                   ← schema validation

Styling
  CSS Modules           ← what we used
  Tailwind CSS          ← utility-first, very popular
  styled-components     ← CSS-in-JS

Testing
  Vitest + RTL + MSW    ← what we used
  Jest + RTL            ← also very common
  Playwright / Cypress  ← E2E testing

Build
  Vite                  ← what we used (most modern projects)
  Next.js               ← when you need SSR/SSG
  Webpack               ← legacy / enterprise
```

## What to Learn Next

1. **TanStack Query** — if your app is heavy on API data, Query handles caching, background refetching, and pagination far better than manual `useEffect` + state
2. **Tailwind CSS** — utility-first styling, massively popular in 2025
3. **Next.js App Router** — when your project needs SEO, SSR, or backend routes
4. **Playwright** — E2E testing (tests real browser, full user flows)
5. **Storybook** — develop and document components in isolation
6. **React Native** — same React concepts, builds iOS and Android apps

## Project Ideas to Solidify Skills

| Project | New skills practiced |
|---------|---------------------|
| Department Management module | Relational data, drill-down navigation |
| Project Tracker | Drag-and-drop, complex forms, many-to-many |
| Real-time notifications | WebSockets, `useEffect` cleanup |
| Dark/Light theme | CSS custom properties, Context |
| CSV import/export | File input, data transformation |
| Role-based admin panel | Granular permissions, complex routing |
