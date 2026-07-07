# React MCQ Assessment — Question Bank (75 Questions)

> **Instructions:** One correct answer per question unless noted. Covers Modules 00–14 + Bonus topics.

---

## Getting Started & JSX (Q1–Q10)

**Q1.** What is the output of this code in the browser before React runs?

```html
<body>
  <div id="root"></div>
  <script type="module" src="/src/main.tsx"></script>
</body>
```

- A) The complete rendered app
- B) An empty page — `div#root` is empty until React runs
- C) A JavaScript error because JSX is not valid HTML
- D) The raw JSX string

**Answer: B**
*The div is empty. React fills it in when `createRoot().render()` runs.*

---

**Q2.** What does JSX compile to?

- A) HTML that browsers can directly parse
- B) A string of HTML text
- C) Calls to `React.createElement()` which return plain JavaScript objects
- D) TypeScript interfaces

**Answer: C**

---

**Q3.** Which of the following JSX is **correct**?

- A) `<div class="box"><img src="x.png"></div>`
- B) `<div className="box"><img src="x.png" /></div>`
- C) `<div className="box"><img src="x.png"></div>`
- D) `<DIV className="box"><IMG src="x.png" /></DIV>`

**Answer: B**
*`class` → `className`; all tags must close; lowercase for HTML elements.*

---

**Q4.** What does `<StrictMode>` do in production?

- A) Renders every component twice to catch bugs
- B) Enables strict TypeScript checking
- C) Nothing — it's a complete no-op in production
- D) Slows renders to test performance

**Answer: C**

---

**Q5.** Which statement about function components is true?

- A) They must be named with lowercase letters
- B) They must return a string
- C) They must start with an uppercase letter and return JSX or null
- D) They can only accept two props

**Answer: C**

---

**Q6.** What renders when this code runs?

```tsx
const count = 0
return <div>{count && <p>Items: {count}</p>}</div>
```

- A) Nothing inside the div
- B) `<p>Items: 0</p>`
- C) The number `0` renders inside the div
- D) A blank `<p>` tag

**Answer: C**
*When `count = 0`, `0 && <p>...</p>` evaluates to `0` — React renders the number. Use `count > 0 && ...` to fix.*

---

**Q7.** What is a React Fragment and when would you use it?

- A) A way to load components lazily
- B) An empty wrapper `<>...</>` that groups elements without adding a DOM node
- C) A special component for error handling
- D) A way to pass data between sibling components

**Answer: B**

---

**Q8.** What is wrong with this JSX?

```tsx
function App() {
  return (
    <h1>Title</h1>
    <p>Body</p>
  )
}
```

- A) `<h1>` must have a className
- B) Functions cannot return JSX
- C) JSX must have a single root element — two siblings are not allowed
- D) `<p>` cannot follow `<h1>`

**Answer: C**

---

**Q9.** In JSX, which characters are used for JavaScript expressions?

- A) `{{ }}`
- B) `( )`
- C) `{ }`
- D) `${ }`

**Answer: C**

---

**Q10.** Where does React mount the app in a Vite project?

- A) Directly into `<body>`
- B) Into `<div id="root">` by calling `createRoot().render()`
- C) Into the first `<main>` element
- D) React creates a new HTML file

**Answer: B**

---

## Props, State & Events (Q11–Q22)

**Q11.** What is the fundamental rule of props in React?

- A) Props can be modified by child components
- B) Props are read-only in the child — they flow only from parent to child
- C) Props must be strings or numbers
- D) A component can have at most five props

**Answer: B**

---

**Q12.** What does calling `setCount(count + 1)` actually do?

- A) Immediately updates the `count` variable
- B) Schedules a re-render and updates the state for the next render
- C) Modifies the DOM directly
- D) Creates a new component instance

**Answer: B**

---

**Q13.** What is wrong with this state update?

```tsx
const handleAdd = () => {
  employees.push(newEmployee)
  setEmployees(employees)
}
```

- A) `push` is not a valid array method
- B) `setEmployees` requires a callback function
- C) State is mutated directly — React sees the same reference and won't re-render
- D) Nothing is wrong

**Answer: C**

---

**Q14.** When should you use the functional update form `setCount(prev => prev + 1)`?

- A) Always — it's the only correct way
- B) When the new state depends on the previous state to avoid stale closure bugs
- C) Only when using TypeScript
- D) Only inside `useEffect`

**Answer: B**

---

**Q15.** What is the correct way to pass an argument to an event handler?

```tsx
// handleDelete needs the employee id
```

- A) `<button onClick={handleDelete(emp.id)}>Delete</button>`
- B) `<button onClick={() => handleDelete(emp.id)}>Delete</button>`
- C) `<button onClick={handleDelete, emp.id}>Delete</button>`
- D) `<button onDelete={() => handleDelete(emp.id)}>Delete</button>`

**Answer: B**
*A calls the function during render. B passes a new function that calls it on click.*

---

**Q16.** What is "lifting state up"?

- A) Moving component files to a higher folder
- B) Moving shared state to the nearest common ancestor component
- C) Using Redux instead of useState
- D) Moving state from a hook into the component

**Answer: B**

---

**Q17.** What TypeScript type should you use for the `onChange` event of a text input?

- A) `Event`
- B) `React.InputEvent`
- C) `React.ChangeEvent<HTMLInputElement>`
- D) `React.SyntheticEvent<string>`

**Answer: C**

---

**Q18.** What does this state update do?

```tsx
setEmployee(prev => ({ ...prev, isActive: false }))
```

- A) Replaces the entire employee object with `{ isActive: false }`
- B) Creates a new object with all previous fields preserved but `isActive` set to `false`
- C) Mutates the existing employee object
- D) Throws an error because spread is not valid inside setState

**Answer: B**

---

**Q19.** What is the `children` prop?

- A) An array of all components in the app
- B) Content placed between a component's opening and closing tags, available as `props.children`
- C) The first child element in the DOM
- D) A special prop only available in class components

**Answer: B**

---

**Q20.** Which conditional renders nothing when `isAdmin` is `false`?

- A) `{isAdmin ? <AdminPanel /> : ''}`
- B) `{isAdmin && <AdminPanel />}`
- C) `{!isAdmin || <AdminPanel />}`
- D) Both A and B

**Answer: D**
*A renders an empty string (React renders nothing for `''`). B uses short-circuit. Both work.*

---

**Q21.** What happens if you call `setState` multiple times synchronously?

```tsx
setCount(count + 1)
setCount(count + 1)
setCount(count + 1)
```

- A) Count increases by 3
- B) Count increases by 1 (all three read the same stale `count`)
- C) React throws an error for multiple state calls
- D) Count increases by 2

**Answer: B**
*All three calls read the same stale `count`. Fix: use `setCount(prev => prev + 1)` three times.*

---

**Q22.** Which is NOT a valid use of conditional rendering?

```tsx
// A
{isActive ? <p>Active</p> : <p>Inactive</p>}

// B
{isActive && <p>Active</p>}

// C
{if (isActive) { return <p>Active</p> }}

// D
{isActive ? <p>Active</p> : null}
```

- A) A
- B) B
- C) C
- D) D

**Answer: C**
*`if` is a statement, not an expression. It cannot be used inside `{}`.*

---

## Lists, Keys & Hooks (Q23–Q33)

**Q23.** Why should you NOT use array index as a `key` prop?

- A) It's slower than using IDs
- B) When items are added, removed, or reordered, React mismatches elements and causes rendering bugs
- C) TypeScript doesn't support numeric keys
- D) Indexes are not unique

**Answer: B**

---

**Q24.** Where must the `key` prop be placed?

- A) On the outermost element inside the `.map()` callback
- B) On every element inside the component
- C) On the parent of the `.map()` call
- D) Inside the child component as a regular prop

**Answer: A**
*The key goes on the element returned by map — not inside the component (components can't read `props.key`).*

---

**Q25.** What does `useEffect` with an empty dependency array `[]` do?

- A) Runs after every render
- B) Runs once after the component first mounts
- C) Runs before the component renders
- D) Runs only on unmount

**Answer: B**

---

**Q26.** What is the cleanup function in `useEffect` used for?

```tsx
useEffect(() => {
  const id = setInterval(tick, 1000)
  return () => clearInterval(id)   // ← this
}, [])
```

- A) To reset state when the component re-renders
- B) To run synchronously before the render
- C) To clear side effects before the effect runs again or before unmount
- D) To cancel the useEffect from running

**Answer: C**

---

**Q27.** Why can't `useEffect` callback be async?

- A) It's a TypeScript restriction
- B) async functions return a Promise, but useEffect expects either nothing or a cleanup function
- C) Async is not supported in React hooks
- D) It would cause an infinite loop

**Answer: B**

---

**Q28.** What is a custom hook?

- A) A hook provided by a third-party library
- B) A function whose name starts with `use` and which can call other hooks
- C) A hook that only works with class components
- D) A renamed version of `useState`

**Answer: B**

---

**Q29.** What is the difference between `useMemo` and `useCallback`?

- A) `useMemo` is for class components; `useCallback` is for function components
- B) `useMemo` returns a memoised value; `useCallback` returns a memoised function
- C) They are identical — just different names
- D) `useCallback` re-runs on every render; `useMemo` does not

**Answer: B**

---

**Q30.** When does `React.memo` actually prevent a re-render?

- A) Always — it skips re-render in all cases
- B) When props passed to the component are the same references as previous render
- C) Only when there are no props
- D) When the component returns `null`

**Answer: B**

---

**Q31.** What is prop drilling?

- A) Passing too many props to one component
- B) Passing props through multiple intermediate components that don't need them, just to reach a deeply nested child
- C) Drilling down into a prop object using optional chaining
- D) A performance problem caused by too many props

**Answer: B**

---

**Q32.** What is the Context API used for?

- A) Replacing all uses of `useState`
- B) Making HTTP requests without Axios
- C) Sharing values (theme, auth user, language) across the component tree without prop drilling
- D) Managing async state

**Answer: C**

---

**Q33.** Which hook rule is being violated here?

```tsx
function EmployeeCard({ isAdmin }: Props) {
  if (isAdmin) {
    const [note, setNote] = useState('')   // ← here
  }
  return <div>...</div>
}
```

- A) useState is misspelled
- B) Hooks must be called at the top level — never inside conditions, loops, or nested functions
- C) useState cannot be used in function components
- D) useState cannot be called with an empty string

**Answer: B**

---

## HTTP, Routing & Forms (Q34–Q48)

**Q34.** Why is Axios preferred over native `fetch` for React projects?

- A) `fetch` is not available in Node.js
- B) Axios automatically parses JSON, throws on 4xx/5xx, and supports interceptors
- C) `fetch` requires more dependencies
- D) Axios is faster at making requests

**Answer: B**

---

**Q35.** What is the purpose of Axios request interceptors?

- A) To cancel requests that take too long
- B) To attach headers (e.g. auth tokens) to every outgoing request automatically
- C) To transform the request URL
- D) To cache API responses

**Answer: B**

---

**Q36.** What will `response.ok` be when a server returns a 404 status?

- A) `true`
- B) `false`
- C) `undefined`
- D) It throws an error automatically

**Answer: B**
*`fetch` only rejects on network failure. A 404 is still a "successful" HTTP response — you must check `response.ok`.*

---

**Q37.** What is the loading/error/data pattern used for?

- A) Form validation states
- B) Managing the three possible states of an async operation: in progress, failed, succeeded
- C) Route transition states
- D) Redux reducer states

**Answer: B**

---

**Q38.** In React Router v6, what does `<Outlet />` render?

- A) The previous route's component
- B) A loading spinner
- C) The matched child route's component inside a layout
- D) A 404 error page

**Answer: C**

---

**Q39.** How do you read a URL parameter like `/employees/:id` in a component?

- A) `const id = useLocation().params.id`
- B) `const { id } = useParams()`
- C) `const id = useRoute().id`
- D) `const id = props.match.id`

**Answer: B**

---

**Q40.** What is the correct way to navigate programmatically after a form submit?

- A) `window.location.href = '/employees'`
- B) `<Navigate to="/employees" />`
- C) `const navigate = useNavigate(); navigate('/employees')`
- D) `router.push('/employees')`

**Answer: C**

---

**Q41.** What is `useSearchParams` used for?

- A) Searching the Redux store
- B) Reading and writing URL query parameters (`?key=value`)
- C) Filtering the DOM
- D) Debouncing search input

**Answer: B**

---

**Q42.** What is a controlled input in React?

- A) An input whose value is stored in the DOM and read with a ref
- B) An input whose value is driven by React state and synced with `onChange`
- C) An input with built-in validation
- D) An input that is disabled

**Answer: B**

---

**Q43.** Why must you call `e.preventDefault()` in a form `onSubmit` handler?

- A) To prevent the input values from being cleared
- B) To prevent the browser's default form behaviour of making a GET/POST request and refreshing the page
- C) To stop React from re-rendering
- D) To prevent validation from running

**Answer: B**

---

**Q44.** What does React Hook Form's `register` function do?

- A) Registers the component with the Redux store
- B) Connects an input element to the form — tracks its value, validation, and dirty state
- C) Creates a new form field in the DOM
- D) Subscribes to a WebSocket for live data

**Answer: B**

---

**Q45.** What does Zod's `z.infer<typeof schema>` do?

- A) Runs the validation at runtime
- B) Extracts the TypeScript type from a Zod schema — single source of truth for type and validation
- C) Converts the schema to JSON
- D) Creates a default value from the schema

**Answer: B**

---

**Q46.** When should you use `reset(values)` in React Hook Form?

- A) To clear all form errors
- B) To go back to the previous page
- C) To populate an edit form with existing data (replaces all field values)
- D) To resubmit the form

**Answer: C**

---

**Q47.** What does `isDirty` in React Hook Form tell you?

- A) Whether the form has validation errors
- B) Whether at least one field value differs from its defaultValue
- C) Whether the form has been submitted
- D) Whether an API call is in progress

**Answer: B**

---

**Q48.** What is the `noValidate` attribute on a `<form>` element used for in React?

- A) To disable all form validation
- B) To disable the browser's native validation UI so React Hook Form can handle validation instead
- C) To prevent the form from submitting
- D) To mark the form as optional

**Answer: B**

---

## Redux (Q49–Q57)

**Q49.** What is Redux Toolkit's `createSlice` used for?

- A) Splitting the app into micro-frontends
- B) Bundling state shape, reducer logic, and action creators into one declaration
- C) Creating lazy-loaded components
- D) Splitting the Redux store into separate files

**Answer: B**

---

**Q50.** How does Redux Toolkit allow you to "mutate" state inside reducers without actually mutating it?

- A) By making a deep copy before every action
- B) By using Immer under the hood — Immer tracks mutations and returns a new object
- C) By converting state to a string and back
- D) By freezing the state object

**Answer: B**

---

**Q51.** What does `createAsyncThunk` return?

- A) A plain function
- B) A thunk creator that dispatches `pending`, `fulfilled`, and `rejected` actions automatically
- C) A Promise
- D) A React hook

**Answer: B**

---

**Q52.** What are `extraReducers` in a Redux slice used for?

- A) Additional reducers for testing
- B) Handling actions created outside this slice, including async thunk lifecycle actions
- C) Providing default values for state
- D) Splitting one reducer into multiple files

**Answer: B**

---

**Q53.** What is a `createSelector` selector?

- A) A CSS selector that targets React components
- B) A memoised selector that only recomputes derived data when its input selectors return new values
- C) A way to select components in React DevTools
- D) A TypeScript utility type

**Answer: B**

---

**Q54.** Why use typed wrappers `useAppDispatch` and `useAppSelector`?

- A) They are required by Redux Toolkit
- B) They provide TypeScript types for the dispatch function and store state, giving autocomplete and type safety
- C) They are faster than the standard hooks
- D) They prevent memory leaks

**Answer: B**

---

**Q55.** When should you prefer Redux over Context API?

- A) Always — Redux is always better
- B) For server data used across many pages, complex state with many actions, or when DevTools debugging is needed
- C) Only when the app has more than 10 components
- D) Only for authentication state

**Answer: B**

---

**Q56.** What is the purpose of Redux DevTools?

- A) Profiling component render performance
- B) Inspecting state, replaying actions, and time-travelling to any previous state
- C) Linting Redux code
- D) Auto-generating reducers

**Answer: B**

---

**Q57.** What does `rejectWithValue` do in a `createAsyncThunk`?

- A) Throws a JavaScript error
- B) Passes a custom error payload to the `rejected` action, which can be read in `extraReducers`
- C) Cancels the async operation
- D) Retries the failed request

**Answer: B**

---

## Authentication & Testing (Q58–Q67)

**Q58.** Where is the JWT token typically attached to API requests in a React app?

- A) In the request body
- B) In the URL query string
- C) In the `Authorization: Bearer <token>` header — ideally in an Axios interceptor
- D) In a cookie set by the frontend

**Answer: C**

---

**Q59.** What is the purpose of a `ProtectedRoute` component?

- A) To add a loading spinner to all pages
- B) To check authentication (and optionally role) before rendering a route, redirecting to login if not authenticated
- C) To validate form data before navigation
- D) To protect against CSRF attacks

**Answer: B**

---

**Q60.** Why do we save `location` before redirecting to login?

- A) For analytics tracking
- B) So the user can be sent back to the page they originally tried to visit after logging in
- C) Because React Router requires it
- D) To populate the login form with their email

**Answer: B**

---

**Q61.** What is the risk of storing a JWT in `localStorage`?

- A) localStorage is too slow for token storage
- B) localStorage is accessible to JavaScript, making tokens vulnerable to XSS attacks
- C) Tokens in localStorage expire immediately
- D) localStorage cannot store strings

**Answer: B**

---

**Q62.** What does React Testing Library's `screen.getByRole` query?

- A) Gets elements by their CSS role class
- B) Gets elements by their ARIA role — the most accessible and recommended query method
- C) Gets elements by their Redux role
- D) Gets elements by their component name

**Answer: B**

---

**Q63.** What is the difference between `getBy` and `findBy` queries in React Testing Library?

- A) `getBy` returns an array; `findBy` returns a single element
- B) `getBy` throws immediately if element not found; `findBy` is async and waits (polls) until found or times out
- C) `findBy` only works with forms
- D) They are identical

**Answer: B**

---

**Q64.** Why use `userEvent` over `fireEvent` in tests?

- A) `userEvent` is faster
- B) `userEvent` simulates complete browser user interactions (pointers, keyboard) more realistically than `fireEvent`
- C) `fireEvent` is deprecated
- D) `userEvent` works with class components

**Answer: B**

---

**Q65.** What does MSW (Mock Service Worker) intercept?

- A) Redux actions
- B) Real HTTP requests at the network level — without modifying any application code
- C) React component renders
- D) CSS stylesheets

**Answer: B**

---

**Q66.** What should you test according to React Testing Library's philosophy?

- A) Implementation details like internal state and function calls
- B) What the user sees and does — rendered output and user interactions
- C) The number of renders a component performs
- D) The structure of props

**Answer: B**

---

**Q67.** What does `vi.fn()` create in Vitest?

- A) A virtual file system
- B) A mock function (spy) that records calls, arguments, and return values
- C) A virtual component for testing
- D) A test fixture

**Answer: B**

---

## Deployment, Performance & Advanced (Q68–Q75)

**Q68.** What does `npm run build` produce for a Vite React app?

- A) A compiled TypeScript project
- B) A `dist/` folder with minified, tree-shaken, hashed JavaScript and CSS files ready for a static server
- C) A Node.js server
- D) A Docker image

**Answer: B**

---

**Q69.** What must you configure on a server (Vercel, Netlify, nginx) for React Router to work?

- A) CORS headers
- B) A rewrite rule that serves `index.html` for all paths, so React Router can handle routing client-side
- C) A reverse proxy to port 3000
- D) SSL certificates

**Answer: B**

---

**Q70.** What does `React.lazy()` do?

- A) Makes a component render slowly for testing
- B) Creates a lazily loaded component — the code is split into a separate chunk and only downloaded when the route is visited
- C) Memoises a component
- D) Delays the initial render by 500ms

**Answer: B**

---

**Q71.** What does `<Suspense fallback={<p>Loading…</p>}>` do?

- A) Adds a loading spinner globally
- B) Shows the fallback UI while lazily loaded components or async data is being fetched
- C) Replaces Error Boundaries
- D) Suspends all re-renders for 1 second

**Answer: B**

---

**Q72.** What is the Virtual DOM?

- A) A virtual machine that runs React
- B) A lightweight in-memory JavaScript object tree that React uses to calculate the minimum DOM changes needed before updating the real DOM
- C) A way to render components on the server
- D) A mock DOM used in tests

**Answer: B**

---

**Q73.** What does React's reconciliation process do?

- A) Validates TypeScript types at runtime
- B) Compares the previous Virtual DOM tree with the new one and applies only the differences to the real DOM
- C) Synchronises Redux state with component state
- D) Resolves imports in the module graph

**Answer: B**

---

**Q74.** What does `useOptimistic` (React 19) allow you to do?

- A) Optimise the number of re-renders
- B) Show an instant UI update while an async operation (like an API call) is pending, reverting automatically if it fails
- C) Skip error boundaries
- D) Batch multiple state updates

**Answer: B**

---

**Q75.** Which rendering strategy does a plain Vite React app use by default?

- A) Server-Side Rendering (SSR)
- B) Static Site Generation (SSG)
- C) Client-Side Rendering (CSR) — the browser downloads JS and renders the UI
- D) Incremental Static Regeneration (ISR)

**Answer: C**

---

## Answer Key

| Q | A | Q | A | Q | A | Q | A | Q | A |
|---|---|---|---|---|---|---|---|---|---|
| 1 | B | 16 | B | 31 | B | 46 | C | 61 | B |
| 2 | C | 17 | C | 32 | C | 47 | B | 62 | B |
| 3 | B | 18 | B | 33 | B | 48 | B | 63 | B |
| 4 | C | 19 | B | 34 | B | 49 | B | 64 | B |
| 5 | C | 20 | D | 35 | B | 50 | B | 65 | B |
| 6 | C | 21 | B | 36 | B | 51 | B | 66 | B |
| 7 | B | 22 | C | 37 | B | 52 | B | 67 | B |
| 8 | C | 23 | B | 38 | C | 53 | B | 68 | B |
| 9 | C | 24 | A | 39 | B | 54 | B | 69 | B |
| 10 | B | 25 | B | 40 | C | 55 | B | 70 | B |
| 11 | B | 26 | C | 41 | B | 56 | B | 71 | B |
| 12 | B | 27 | B | 42 | B | 57 | B | 72 | B |
| 13 | C | 28 | B | 43 | B | 58 | C | 73 | B |
| 14 | B | 29 | B | 44 | B | 59 | B | 74 | B |
| 15 | B | 30 | B | 45 | B | 60 | B | 75 | C |

---

## Topic Coverage

| Module | Topic | Questions |
|--------|-------|-----------|
| 00–02 | Getting Started & JSX | Q1–Q10 |
| 03–04 | Props, State & Events | Q11–Q22 |
| 04–07 | Lists, Keys & Hooks | Q23–Q33 |
| 08–10 | HTTP, Routing & Forms | Q34–Q48 |
| 11 | Redux Toolkit | Q49–Q57 |
| 12–13 | Auth & Testing | Q58–Q67 |
| 14–Bonus | Deployment & Advanced | Q68–Q75 |
