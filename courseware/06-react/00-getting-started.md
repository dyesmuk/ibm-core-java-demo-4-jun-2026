# Module 00 — Getting Started with React

## Learning Objectives
- Understand what React is and why it exists
- Scaffold a React + TypeScript project with Vite
- Understand every file in the generated project
- Trace the full execution flow from browser request to pixels on screen
- Clean the boilerplate and ship your first output

---

## 0.1 What Is React?

React is a **JavaScript library** (not a framework) created by Meta for building user interfaces. It does one job: rendering UI and keeping it in sync with your data.

| Idea | What it means |
|------|---------------|
| **Component-based** | UI is broken into small, self-contained, reusable pieces |
| **Declarative** | You describe *what* the UI should look like; React decides *how* to update the DOM |
| **Virtual DOM** | React keeps a lightweight in-memory copy of the DOM and only patches what actually changed |
| **Unidirectional data flow** | Data always travels parent → child; makes bugs easier to trace |

### React is a library, not a framework

React handles only the **View layer**. Everything else — routing, HTTP, state management, forms — is handled by libraries you pick yourself. That's why companies use React with very different stacks.

---

## 0.2 Prerequisites

| Tool | Minimum | Check |
|------|---------|-------|
| Node.js | 20 LTS | `node -v` |
| npm | 10 | `npm -v` |
| VS Code | latest | — |

Download Node from [nodejs.org](https://nodejs.org) → choose **LTS**.

### VS Code extensions — install these now

- **ESLint** — flags code problems inline as you type
- **Prettier** — auto-formats on save
- **ES7+ React/Redux snippets** — type `rfc` → full component skeleton
- **Auto Rename Tag** — rename JSX closing tag when you edit the opening one
- **TypeScript Importer** — auto-adds `import` statements

---

## 0.3 Creating the Project

```bash
npm create vite@latest ibm-ems-app -- --template react-ts
cd ibm-ems-app
npm install
npm run dev
```

**Command breakdown:**

| Part | Meaning |
|------|---------|
| `npm create vite@latest` | Run the latest Vite project scaffolder |
| `ibm-ems-app` | Folder name |
| `--template react-ts` | React + TypeScript template |

Open **http://localhost:5173** — you see the default Vite + React welcome page.

---

## 0.4 Project File Anatomy

```
ibm-ems-app/
│
├── public/                 ← Static files served as-is (favicon, robots.txt)
│
├── src/                    ← All your code lives here
│   ├── assets/             ← Images, fonts, SVGs
│   ├── App.tsx             ← Root component
│   ├── App.css             ← Styles for App (we'll delete this)
│   ├── index.css           ← Global styles
│   └── main.tsx            ← Entry point — boots React
│
├── index.html              ← The single HTML file (SPA shell)
├── package.json            ← Dependencies + scripts
├── tsconfig.json           ← TypeScript config
└── vite.config.ts          ← Vite build config
```

### The three most important files

#### `index.html`
```html
<body>
  <div id="root"></div>
  <script type="module" src="/src/main.tsx"></script>
</body>
```
The whole app lives inside `<div id="root">`. This is why it's called a **Single Page Application** — one HTML file, React handles everything inside it.

#### `src/main.tsx`
```tsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
```
This is the **entry point**. It finds the `root` div and mounts your React app inside it.

#### `src/App.tsx`
The root component. Everything the user sees is a descendant of this.

---

## 0.5 Full Execution Flow

```
① Browser requests http://localhost:5173
        ↓
② Vite dev server sends index.html
        ↓
③ Browser parses HTML → finds <div id="root"> (empty)
                       → finds <script src="/src/main.tsx">
        ↓
④ Vite transforms main.tsx:
   TypeScript → JavaScript
   JSX → React.createElement() calls
        ↓
⑤ main.tsx runs:
   createRoot(div#root).render(<App />)
        ↓
⑥ React calls App() → returns JSX
        ↓
⑦ React builds Virtual DOM tree from JSX
        ↓
⑧ React compares Virtual DOM vs real DOM (first render: all new)
        ↓
⑨ React writes actual DOM nodes into div#root
        ↓
⑩ Browser paints → user sees the UI
```

When you change state, steps ⑥–⑩ repeat, but step ⑧ makes sure only changed DOM nodes are touched. That's the efficiency win.

---

## 0.6 Available Scripts

```bash
npm run dev       # Start dev server with hot reload
npm run build     # Compile + bundle for production → dist/
npm run preview   # Locally serve the production build
npm run lint      # Run ESLint
```

---

## 0.7 Cleaning the Boilerplate — Hello EMS

Let's wipe the generated noise and start clean.

### `src/App.tsx` — replace entirely

```tsx
function App() {
  return (
    <div>
      <h1>IBM Employee Management System</h1>
      <p>Let's build something.</p>
    </div>
  )
}

export default App
```

### `src/index.css` — minimal reset

```css
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: system-ui, sans-serif; }
```

### Delete these files
- `src/App.css`
- `src/assets/react.svg`
- `public/vite.svg`

Also remove `import './App.css'` from `App.tsx` if present.

Save — browser hot-reloads, you see a clean "IBM Employee Management System" heading.

---

## 0.8 Understanding StrictMode

`<StrictMode>` wraps the app in `main.tsx`:

```tsx
<StrictMode>
  <App />
</StrictMode>
```

**In development:**
- Renders every component **twice** to catch bugs where rendering has side effects
- Runs every `useEffect` **twice** to verify cleanup works
- Warns about deprecated API usage

**In production:** StrictMode is a complete no-op — zero performance cost.

> If you see console logs appearing twice, that's StrictMode. It means your setup is working correctly.

---

## 0.9 The EMS Project Plan

Every module adds real features to the same running app:

| Module | Feature added to EMS |
|--------|----------------------|
| 02 | `EmployeeCard` component |
| 03 | Props, state, add/remove employees |
| 04 | List rendering, department filter |
| 05 | Styling approaches |
| 06 | Debugging tools |
| 07 | Custom hooks, Context API |
| 08 | Fetch employees from API with Axios |
| 09 | Multi-page routing (list, detail, 404) |
| 10 | Create/Edit forms with validation |
| 11 | Redux Toolkit for global state |
| 12 | Login, JWT auth, protected routes |
| 13 | Tests with Vitest + React Testing Library |
| 14 | Build and deploy |

---

## Summary

- React is a UI library — it does the View layer, you choose the rest
- Vite is our build tool — fast, TypeScript-ready, zero config needed
- `index.html` → `main.tsx` → `App.tsx` is the bootstrap chain
- `StrictMode` double-renders in dev to catch bugs early
- Virtual DOM + diffing = only changed DOM nodes get updated

**Next → Module 01: JavaScript & TypeScript Refresh**
