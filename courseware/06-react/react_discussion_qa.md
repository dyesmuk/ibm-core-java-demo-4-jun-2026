# ReactJS — Discussion Questions & Answers

---

## Fundas

**1. What happens when a React component re-renders?**

The component function runs again from the top, JSX is re-evaluated, and React diffs the new Virtual DOM against the previous one. Only the DOM nodes that actually changed are updated — not the entire page.

**2. What is JSX? Is it HTML?**

JSX is a syntax extension for JavaScript that looks like HTML but is not HTML. It compiles to `React.createElement()` calls, which return plain JavaScript objects describing the UI. Browsers never see JSX — Babel/Vite transforms it at build time.

**3. Explain: `const [count, setCount] = useState(0);`**

`useState(0)` creates a state variable `count` with an initial value of `0` and a setter function `setCount`. Calling `setCount(newValue)` tells React the state changed and schedules a re-render with the new value.

**4. Why do we need `key` in lists?**

`key` is React's tracking ID for list items across re-renders. It helps React identify which items were added, removed, or reordered, so it can update only the changed DOM nodes instead of re-rendering the whole list.

**5. Difference between props and state?**

Props are read-only inputs passed from a parent component — a child cannot change them. State is data owned and managed inside a component that can change over time, triggering a re-render when updated via the setter.

**6. Can a component change its own props?**

No. Props are immutable in the child. If a child needs to communicate back, it emits an event via a callback function passed as a prop, and the parent updates its own state — which flows back down as new props.

**7. Role of useState?**

`useState` lets functional components hold and manage local reactive data. When the setter is called, React schedules a re-render so the UI reflects the new value. Without it, changes to plain variables would not update the screen.

**8. What if we do `count = count + 1` instead of `setCount(count + 1)`?**

The variable value changes in memory, but React has no idea the change happened. No re-render is scheduled, so the UI stays stale showing the old value. Always use the setter function.

**9. Why is React a library and not a framework?**

React focuses only on the View layer — rendering UI and keeping it in sync with data. Routing, HTTP, state management, and forms are handled by separate libraries you choose (React Router, Axios, Redux, etc.). A full framework like Angular bundles all of these.

**10. What is a functional component?**

A functional component is a plain JavaScript function that accepts props and returns JSX. Since React Hooks (v16.8), functional components can manage state and side effects, making class components largely unnecessary.

---

## Concepts

**11. Controlled vs uncontrolled components?**

A controlled component has its value managed by React state — the input's value is always driven by a state variable, and `onChange` updates it. An uncontrolled component stores its value in the DOM, accessed via a `ref`. Controlled is preferred because the state is the single source of truth and validation is easier.

**12. When to use uncontrolled components?**

Uncontrolled components are useful for simple, low-stakes inputs (search bars, file inputs) where you only need the value on submit and don't need real-time validation. They also cause fewer re-renders since state is not updated on every keystroke.

**13. What is lifting state up?**

When two sibling components need to share the same data, you move the state up to their nearest common parent. The parent owns the state and passes it down via props. This creates a single source of truth and eliminates state sync problems.

**14. What is prop drilling?**

Prop drilling is passing props through multiple intermediate components that don't use them, just to get the data to a deeply nested child. It makes code hard to maintain. Solutions include Context API or a state management library like Redux.

**15. What is useEffect?**

`useEffect` is the hook for running side effects after a render — data fetching, subscriptions, timers, manual DOM manipulation. It runs after the browser has painted the screen. The dependency array controls when it re-runs.

**16. No dependency array in useEffect?**

Without a dependency array, `useEffect` runs after every single render. This can cause infinite loops if the effect updates state, so it's rarely the right choice. Always think about what the effect depends on.

**17. Empty dependency array `[]` in useEffect?**

The effect runs exactly once, after the component first mounts — like `componentDidMount` in class components. Use this for one-time setup: initial data fetch, event listener registration, etc.

**18. Explain `useEffect(() => { console.log("Hello") }, []);`**

This logs "Hello" exactly once, after the component mounts. The empty `[]` means no dependencies — the effect never re-runs. In StrictMode (development), it runs twice to help catch cleanup issues.

**19. useEffect vs useLayoutEffect?**

`useEffect` runs asynchronously after the browser has painted — the user sees the update before the effect fires. `useLayoutEffect` runs synchronously after DOM mutations but before the browser paints — use it for measuring DOM layout or preventing visual flicker. Prefer `useEffect` unless you have a specific layout-measurement need.

**20. React events vs native JS events?**

React wraps native DOM events in a **SyntheticEvent** system that normalises behaviour across browsers. The API is similar (`onClick`, `onChange`) but event names are camelCase. In React 17+, events are attached to the root container, not individual DOM nodes.

**21. Why useRef?**

`useRef` serves two purposes: (1) accessing a DOM element directly — `inputRef.current.focus()`, and (2) storing a mutable value that persists across renders without triggering a re-render when changed. It's the escape hatch for values that React doesn't need to track.

**22. useRef vs useState?**

Both persist values across renders, but `useState` triggers a re-render when the value changes while `useRef` does not. Use `useState` for values that affect the UI. Use `useRef` for timers, DOM references, or previous-value tracking where a re-render is unnecessary.

---

## Depth

**23. Why Virtual DOM?**

Manipulating the real DOM is slow. React maintains a lightweight JavaScript object tree (Virtual DOM) representing the UI. When state changes, React creates a new Virtual DOM, diffs it against the previous one, and applies only the minimal set of real DOM changes. This diffing is much faster than re-rendering everything.

**24. What is reconciliation?**

Reconciliation is React's algorithm for comparing the old and new Virtual DOM trees and deciding the minimum number of real DOM operations needed. React uses the `key` prop on lists and the element `type` as heuristics to make this O(n) instead of O(n³).

**25. What is batching?**

Batching means React groups multiple `setState` calls from the same event handler into a single re-render. React 18 extended this to async code too (automatic batching). Instead of 3 state updates causing 3 re-renders, they cause 1 — improving performance.

**26. Why are state updates asynchronous?**

React doesn't update state and re-render immediately to avoid unnecessary re-renders. It schedules updates and batches them for the next render cycle. This is why reading `count` immediately after `setCount(count + 1)` still shows the old value — the update hasn't applied yet.

**27. What happens when setState is called?**

React marks the component as needing a re-render and adds the new state to a queue. In the next render cycle, it processes the queue, runs the component function with the new state, diffs the output against the previous Virtual DOM, and patches the real DOM.

**28. Why not use array index as key?**

When items are reordered, filtered, or removed, their indexes change. React uses the key to match old and new elements — if indexes shift, React incorrectly reuses the wrong DOM nodes or component state, causing subtle rendering bugs (text appearing in the wrong input, for example).

**29. What is memoization?**

Memoization is caching the result of an expensive computation and returning the cached result when the inputs haven't changed. In React, `useMemo` memoizes computed values and `useCallback` memoizes function references to prevent unnecessary recalculations and re-renders.

**30. What is React.memo?**

`React.memo` is a higher-order component that wraps a component and prevents it from re-rendering if its props haven't changed (shallow comparison). It's useful for pure presentational components that receive the same props frequently, like list item cards.

**31. useMemo vs useCallback?**

`useMemo` memoizes the **return value** of a function — use it for expensive calculations. `useCallback` memoizes the **function reference itself** — use it when passing callbacks to `React.memo` children so they don't see a new prop reference on every parent render.

**32. Why hooks over class components?**

Hooks let you extract and reuse stateful logic without changing the component hierarchy (custom hooks). Class components required lifecycle methods (`componentDidMount`, `componentDidUpdate`, etc.) scattered across the class, making related logic hard to follow. Hooks co-locate related logic and are easier to test.

---

## Real Time

**33. 10-input form: controlled or uncontrolled?**

Use controlled components with React Hook Form (or plain `useState`). With 10 fields, you need validation, error messages, and submit handling — all of which need access to the current values. Uncontrolled forms are impractical at this scale.

**34. Too many re-renders?**

Check: (1) state being set inside a render-time expression, (2) new object/array references passed as props every render, (3) `useEffect` with wrong or missing dependencies causing loops. Use React DevTools Profiler to identify which component is re-rendering and why.

**35. Share data between siblings?**

Lift the shared state up to the nearest common parent and pass it down via props. For deeply nested or widely shared data, use Context API or a state management library (Redux, Zustand) to avoid prop drilling.

**36. Prop drilling solution?**

Use React Context API for medium-scale sharing (theme, auth, language). For complex application state with many actions, use Redux Toolkit or Zustand. Context is simpler but re-renders all consumers when the value changes — Redux has more granular subscriptions.

**37. API calling multiple times — why?**

The most common cause is a missing or incorrect `useEffect` dependency array. If the dependency array contains an object or function defined inside the component, a new reference is created every render, triggering the effect again. Solutions: stable references with `useCallback`/`useMemo`, or move the value outside the component.

**38. State updated but UI not changing — why?**

Most likely cause: **direct mutation** of state. `array.push()` or `object.field = value` mutates in place — React sees the same reference and skips the re-render. Always return a new reference: `[...arr, item]` or `{ ...obj, field: value }`.

**39. List not updating correctly?**

Check the `key` prop. If keys are wrong (using index, or missing), React reuses the wrong DOM nodes. Also verify the state array is being replaced (not mutated) — `setList([...prev, newItem])` not `list.push(newItem)`.

**40. Performance optimisation?**

(1) `React.memo` for pure components, (2) `useMemo`/`useCallback` for stable references, (3) code splitting + `React.lazy` for route-level lazy loading, (4) `useTransition` for non-urgent updates, (5) virtualization (`react-window`) for long lists, (6) avoid anonymous functions/objects in props.

**41. When to split components?**

Split when: (1) a component has too much responsibility (does more than one thing), (2) part of the JSX is reused in multiple places, (3) a section has its own independent state, (4) a section makes the parent file hard to read (over ~150 lines is a signal). Keep splits meaningful — don't split just for the sake of it.

**42. When to avoid state?**

Avoid state when: (1) a value can be derived from existing state or props — use a computed variable instead, (2) a value doesn't affect the UI — use a `ref`, (3) the value is only needed temporarily during an event handler. Unnecessary state makes components harder to reason about.

---

## More

**43. Hooks inside loops or conditions?**

Not allowed. React tracks hooks by their call order on every render. If a hook is inside a condition or loop, the order can change between renders, causing React to associate the wrong state with the wrong hook. The Rules of Hooks enforce: always call hooks at the top level.

**44. Hook inside a normal function?**

Not allowed. Hooks can only be called inside React function components or custom hooks (functions whose name starts with `use`). Calling a hook inside a regular utility function breaks the Rules of Hooks and will throw an error.

**45. Is Virtual DOM the only reason React is fast?**

No. React's performance comes from: Virtual DOM diffing (reconciliation), batching of state updates, selective re-rendering (only changed components), memoization tools (`React.memo`, `useMemo`), and code splitting. Any one of these alone wouldn't be sufficient.

**46. Does React re-render the full DOM?**

No. React re-renders components (runs their functions) when state changes, but only updates the specific real DOM nodes that differ from the previous Virtual DOM snapshot. The diffing step ensures minimal DOM writes.

**47. Can we use React without JSX?**

Yes. JSX is syntactic sugar. `<h1>Hello</h1>` compiles to `React.createElement('h1', null, 'Hello')`. You can write your entire application using `React.createElement` calls, but it becomes unreadable quickly. JSX exists to make the code look and feel natural.

---

## Additional Questions

**48. What is the Context API?**

Context API lets you share values (theme, user, language) across the component tree without prop drilling. You create a context with `createContext()`, wrap a subtree with a `Provider`, and any descendant can read it with `useContext()`. It's best for low-frequency updates like theme or auth.

**49. What is useReducer and when to use it?**

`useReducer` is an alternative to `useState` for complex state logic. It takes a reducer function `(state, action) => newState` and an initial state, similar to Redux. Use it when: next state depends on previous state in complex ways, multiple sub-values change together, or state transitions are hard to follow with multiple `useState` calls.

**50. What is the difference between useContext and Redux?**

`useContext` is built-in, zero-setup, and good for simple shared values. Redux (or Redux Toolkit) adds a predictable state container, DevTools for time-travel debugging, middleware for async logic, and granular subscriptions. Use Context for simple cases; Redux for complex app-wide state with many actions.

**51. What is React.lazy and Suspense?**

`React.lazy` enables code splitting — the component's code is loaded in a separate chunk and only downloaded when the component is first rendered. `Suspense` wraps lazy components and shows a fallback UI (like a spinner) while the chunk is loading. Together they reduce the initial bundle size.

**52. What is the difference between client-side rendering (CSR) and server-side rendering (SSR) in React?**

CSR (standard React): the browser downloads an empty HTML file, downloads the JavaScript bundle, and React renders the UI in the browser. SSR (Next.js): the server pre-renders the HTML and sends it to the browser — better for SEO and first-paint performance, but adds server complexity.

**53. What are custom hooks?**

A custom hook is a function whose name starts with `use` that calls other hooks inside. It lets you extract and reuse stateful logic across components without changing the component hierarchy. Example: `useEmployees()` that encapsulates fetch, loading, error, and filter state.

**54. What is the StrictMode double-render in development?**

In development, `<StrictMode>` intentionally renders components twice and runs effects twice to help detect side effects in render functions or missing `useEffect` cleanup. In production, double-rendering does not happen and there is zero performance cost.

**55. What is the difference between `null`, `undefined`, and `false` in JSX rendering?**

React renders `null`, `undefined`, and `false` as nothing (no DOM output). This makes conditional rendering clean: `{isAdmin && <AdminPanel />}` — when `isAdmin` is `false`, nothing is rendered. Careful: `{0 && <p>Items</p>}` renders `0` because `0` is a renderable value, not ignored.

**56. What is forwardRef?**

`forwardRef` lets a parent component pass a `ref` through to a child component's DOM element. Without it, `ref` is not forwarded automatically — only props are. Useful for UI libraries where the parent needs to control focus or measure a DOM element inside a child component.

**57. What is useImperativeHandle?**

Used with `forwardRef`, it lets a child component customise what the parent sees when accessing its `ref`. Instead of exposing the entire DOM element, the child can expose only specific methods — `{ focus, reset }` — making the API explicit and controlled.

**58. What is the difference between React 18 and earlier versions?**

React 18 introduced: **Concurrent Mode** (rendering can be interrupted), **automatic batching** (state updates in async code are also batched), `useTransition` and `useDeferredValue` for non-urgent updates, `Suspense` on the server, and the new `createRoot` API. React 19 added `useOptimistic`, `useActionState`, and `useFormStatus`.

**59. What is useTransition?**

`useTransition` marks a state update as non-urgent. React can interrupt it to handle more urgent updates (like user input) first. Returns `[isPending, startTransition]`. Use it for transitions like tab switches or search results where a slight delay is acceptable but the input must stay responsive.

**60. What is useDeferredValue?**

`useDeferredValue` accepts a value and returns a deferred version that lags behind — React updates the deferred value with lower priority. Use it to keep expensive re-renders (like a filtered long list) from blocking fast updates (like an input's cursor). Similar purpose to debouncing but built into React's scheduler.

**61. What is the reconciliation algorithm's key assumption?**

React assumes elements of different types produce different trees and destroys the old tree when the type changes. For lists, it uses the `key` prop to identify elements across renders. This makes reconciliation O(n) instead of the theoretical O(n³) for a general tree diff.

**62. What is an error boundary?**

Error boundaries are class components that implement `static getDerivedStateFromError()` and `componentDidCatch()`. They catch JavaScript errors in their child component tree, log them, and display a fallback UI instead of crashing the entire app. They do NOT catch errors in event handlers or async code.

**63. What is the difference between `onClick={() => fn()}` and `onClick={fn}`?**

`onClick={fn}` passes the function reference — `fn` is called with the event object when clicked. `onClick={() => fn()}` creates a new arrow function on every render that calls `fn` with no arguments. Use the reference form when possible; use the arrow form when you need to pass arguments like `onClick={() => deleteItem(id)}`.

**64. What is prop types or TypeScript in React?**

PropTypes is a runtime type-checking library for props in plain JavaScript React. TypeScript provides compile-time type checking through interface/type definitions. In production React projects, TypeScript is strongly preferred because it catches errors before the code runs and improves IDE support.

**65. What is the difference between mounting, updating, and unmounting?**

Mounting: component appears in the DOM for the first time — `useEffect(fn, [])` runs. Updating: props or state change — component re-renders. Unmounting: component is removed from the DOM — the cleanup function returned from `useEffect` runs. Understanding this lifecycle is essential for managing subscriptions and timers correctly.

---

## Answer Summary Table

| # | Question (short) | Key answer |
|---|------|------------|
| 1 | Re-render | Function reruns, Virtual DOM diffs, minimal DOM update |
| 2 | JSX | Syntax extension → React.createElement() |
| 3 | useState(0) | state variable + setter |
| 4 | key in lists | React tracking ID for efficient updates |
| 5 | props vs state | props = read-only from parent; state = internal, mutable |
| 6 | Change own props | No — use callback prop |
| 7 | useState role | Reactive local data that triggers re-render |
| 8 | count = count+1 | No re-render — must use setter |
| 9 | Library not framework | Only handles View layer |
| 10 | Functional component | JS function returning JSX |
| 11 | Controlled vs uncontrolled | State vs DOM (ref) |
| 12 | When uncontrolled | Simple forms, file input |
| 13 | Lifting state up | Move to nearest common parent |
| 14 | Prop drilling | Passing through uninvolved layers |
| 15 | useEffect | Side effects after render |
| 16 | No dep array | Runs after every render |
| 17 | Empty dep array | Runs once on mount |
| 23 | Virtual DOM | Lightweight JS tree for efficient diffing |
| 24 | Reconciliation | Diff algorithm → minimal DOM ops |
| 25 | Batching | Multiple setState → one re-render |
| 30 | React.memo | Skip re-render if props unchanged |
| 31 | useMemo vs useCallback | Value vs function |
| 43 | Hooks in loops | Not allowed — breaks call order |
| 53 | Custom hooks | Reusable stateful logic in a `use` function |
