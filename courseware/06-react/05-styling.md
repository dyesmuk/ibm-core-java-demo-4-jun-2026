# Module 05 — Styling React Components

## Learning Objectives
- Understand every styling approach available in React
- Know when to use each approach
- Apply global CSS, CSS Modules, and dynamic class names
- EMS: structured, theme-ready styling without any CSS-in-JS library

---

## 5.1 Four Approaches — Quick Comparison

| Approach | Scope | Dynamic | Setup |
|----------|-------|---------|-------|
| Global CSS | Global (all files) | Via class toggling | Zero |
| CSS Modules | Per-component | Via class toggling | Zero (Vite supports it) |
| Inline styles | Per-element | Direct JS values | Zero |
| CSS-in-JS (styled-components, Emotion) | Per-component | Full JS | Install library |

For the IBM training we focus on **Global CSS + CSS Modules** — production-ready, zero dependencies, supported by Vite out of the box.

---

## 5.2 Global CSS

Imported in `main.tsx` — applies everywhere.

```css
/* src/index.css */

/* Reset */
*, *::before, *::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

/* Design tokens as CSS custom properties */
:root {
  --color-primary:    #0062ff;
  --color-danger:     #da1e28;
  --color-success:    #24a148;
  --color-text:       #161616;
  --color-text-muted: #525252;
  --color-border:     #e0e0e0;
  --color-bg:         #f4f4f4;
  --color-surface:    #ffffff;

  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-pill: 999px;

  --space-xs: 4px;
  --space-sm: 8px;
  --space-md: 16px;
  --space-lg: 24px;
  --space-xl: 32px;

  --font-sm: 13px;
  --font-md: 14px;
  --font-base: 16px;
  --font-lg: 18px;
}

body {
  font-family: system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif;
  font-size: var(--font-base);
  color: var(--color-text);
  background: var(--color-bg);
  line-height: 1.6;
}

/* Utility classes — use these in JSX className */
.container { max-width: 1100px; margin: 0 auto; padding: var(--space-lg); }
.card      { background: var(--color-surface); border: 1px solid var(--color-border); border-radius: var(--radius-md); padding: var(--space-md); }
.grid      { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: var(--space-md); }
.flex      { display: flex; gap: var(--space-sm); align-items: center; }
.badge     { display: inline-block; padding: 2px 10px; border-radius: var(--radius-pill); font-size: var(--font-sm); font-weight: 600; }
.badge--active   { background: #defbe6; color: #0e6027; }
.badge--inactive { background: #fff1f1; color: #a2191f; }
.btn       { padding: var(--space-sm) var(--space-md); border: none; border-radius: var(--radius-sm); cursor: pointer; font-size: var(--font-md); font-weight: 600; }
.btn--primary { background: var(--color-primary); color: #fff; }
.btn--danger  { background: var(--color-danger); color: #fff; }
.btn--ghost   { background: transparent; border: 1px solid var(--color-border); color: var(--color-text); }
.text-muted   { color: var(--color-text-muted); font-size: var(--font-md); }
.mb-md { margin-bottom: var(--space-md); }
.mb-lg { margin-bottom: var(--space-lg); }
```

Usage:

```tsx
<div className="container">
  <div className="grid">
    {employees.map(emp => (
      <div key={emp.id} className="card">
        <h3>{emp.name}</h3>
        <span className={`badge ${emp.isActive ? 'badge--active' : 'badge--inactive'}`}>
          {emp.isActive ? 'Active' : 'Inactive'}
        </span>
      </div>
    ))}
  </div>
</div>
```

---

## 5.3 CSS Modules

A CSS Module is a `.module.css` file where every class name is **automatically scoped** to the component that imports it. No class name collisions across the whole codebase.

```css
/* src/components/EmployeeCard.module.css */

.card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: var(--space-md);
  position: relative;
  transition: box-shadow 0.2s;
}

.card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.name {
  font-size: var(--font-lg);
  font-weight: 600;
  margin-bottom: var(--space-xs);
}

.meta {
  color: var(--color-text-muted);
  font-size: var(--font-md);
  margin: 2px 0;
}

.salary {
  font-weight: 700;
  color: var(--color-primary);
  margin-top: var(--space-sm);
}

.removeBtn {
  position: absolute;
  top: var(--space-sm);
  right: var(--space-sm);
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-text-muted);
  font-size: 18px;
  line-height: 1;
  padding: 2px 6px;
  border-radius: var(--radius-sm);
}

.removeBtn:hover {
  color: var(--color-danger);
  background: #fff1f1;
}
```

```tsx
// src/components/EmployeeCard.tsx
import styles from './EmployeeCard.module.css'
import type { Employee } from '../types'

interface EmployeeCardProps {
  employee: Employee
  onRemove: (id: number) => void
}

function EmployeeCard({ employee, onRemove }: EmployeeCardProps) {
  return (
    <div className={styles.card}>
      <button
        className={styles.removeBtn}
        onClick={() => onRemove(employee.id)}
        aria-label={`Remove ${employee.name}`}
      >
        ✕
      </button>

      <h3 className={styles.name}>{employee.name}</h3>
      <p className={styles.meta}>{employee.department}</p>
      <p className={styles.meta}>{employee.email}</p>
      <p className={styles.meta}>
        Joined {new Date(employee.joinDate).toLocaleDateString()}
      </p>
      <p className={styles.salary}>${employee.salary.toLocaleString()} / yr</p>

      <span className={`badge ${employee.isActive ? 'badge--active' : 'badge--inactive'}`}>
        {employee.isActive ? 'Active' : 'Inactive'}
      </span>
    </div>
  )
}

export default EmployeeCard
```

What Vite generates behind the scenes:

```html
<!-- The actual class name in the DOM is hashed — no collisions possible -->
<div class="EmployeeCard_card__3xKp1">
```

---

## 5.4 Dynamic Class Names

Three ways to combine class names conditionally:

```tsx
// 1. Template literal — simple cases
<span className={`badge ${emp.isActive ? 'badge--active' : 'badge--inactive'}`}>

// 2. Array join — multiple conditionals
<button
  className={[
    styles.btn,
    isLoading ? styles.loading : '',
    disabled ? styles.disabled : '',
  ].filter(Boolean).join(' ')}
>

// 3. clsx (lightweight library — recommended for complex cases)
// npm install clsx
import clsx from 'clsx'

<button
  className={clsx(
    styles.btn,
    styles.btnPrimary,
    { [styles.loading]: isLoading },
    { [styles.disabled]: disabled }
  )}
>

// clsx with global utilities
<div className={clsx('card', styles.employeeCard, { [styles.selected]: isSelected })}>
```

Install clsx:

```bash
npm install clsx
```

---

## 5.5 Inline Styles — When to Use

Inline styles in React use a JavaScript object with camelCase properties. Use them only for **values that change at runtime** and can't be predetermined in a CSS file.

```tsx
// Good use: value computed at runtime
<div style={{ width: `${progress}%` }}>
<div style={{ color: employee.departmentColor }}>
<div style={{ transform: `rotate(${angle}deg)` }}>

// Bad use: static values (put these in CSS)
<div style={{ fontSize: '14px', color: '#525252' }}>  // ❌ use a class instead
```

Note: CSS custom properties do NOT work in inline style objects. Use a CSS class and `var(--token)` for design tokens.

---

## 5.6 Conditional Classes with CSS Modules

```tsx
// src/components/FilterButton.tsx
import styles from './FilterButton.module.css'

interface FilterButtonProps {
  label: string
  isActive: boolean
  onClick: () => void
}

function FilterButton({ label, isActive, onClick }: FilterButtonProps) {
  return (
    <button
      className={clsx(styles.filterBtn, { [styles.active]: isActive })}
      onClick={onClick}
    >
      {label}
    </button>
  )
}
```

```css
/* FilterButton.module.css */
.filterBtn {
  padding: 6px 16px;
  border: 1px solid var(--color-primary);
  border-radius: var(--radius-pill);
  background: transparent;
  color: var(--color-primary);
  cursor: pointer;
  font-size: var(--font-sm);
  font-weight: 500;
}

.filterBtn.active {
  background: var(--color-primary);
  color: #fff;
  font-weight: 700;
}
```

---

## 5.7 Dark Mode with CSS Custom Properties

Because we defined tokens in `:root`, dark mode is just overriding tokens — no JS needed.

```css
/* src/index.css */

@media (prefers-color-scheme: dark) {
  :root {
    --color-text:    #f4f4f4;
    --color-bg:      #161616;
    --color-surface: #262626;
    --color-border:  #393939;
  }
}

/* Or toggle with a class for user-controlled dark mode */
[data-theme='dark'] {
  --color-text:    #f4f4f4;
  --color-bg:      #161616;
  --color-surface: #262626;
  --color-border:  #393939;
}
```

Apply in React:

```tsx
// In your ThemeContext or App.tsx
document.documentElement.setAttribute('data-theme', theme)
```

All components pick up the new token values automatically — no code changes needed in individual components.

---

## 5.8 EMS Folder Structure After Module 05

```
src/
├── components/
│   ├── EmployeeCard.tsx
│   ├── EmployeeCard.module.css
│   ├── FilterButton.tsx
│   └── FilterButton.module.css
├── pages/
│   └── EmployeesPage.module.css
├── index.css           ← global reset + tokens + utilities
└── App.module.css
```

---

## Summary

| Approach | Use for |
|----------|---------|
| Global CSS + custom properties | Design tokens, resets, utility classes |
| CSS Modules | Per-component styles — zero class name collisions |
| `clsx` | Combining/toggling multiple class names cleanly |
| Inline styles | Runtime values only (progress bars, angles, dynamic colors) |
| CSS-in-JS | Not covered — adds bundle weight, consider only for complex theming |

**Next → Module 06: Debugging React Apps**
