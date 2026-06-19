# CSS Module 03 — The Cascade, Inheritance & Units

## 3.1 The Cascade — How CSS Resolves Conflicts

When multiple CSS rules compete to style the same element, the **cascade** determines the winner. The cascade evaluates rules in priority order:

### Priority Order (Highest to Lowest)

1. **`!important` declarations** (in order of specificity and source order)
2. **Inline styles** (`style=""` attribute)
3. **ID selectors**
4. **Class, attribute, and pseudo-class selectors**
5. **Element and pseudo-element selectors**
6. **Inherited values** (from parent elements)
7. **Browser defaults** (user agent stylesheet)

### Same Specificity: Source Order Wins

When two rules have equal specificity, the **last one** in source order wins:

```css
p { color: blue; }   /* Rule 1 */
p { color: red; }    /* Rule 2 — wins (comes later) */
```

This is why **stylesheet load order matters**:

```html
<!-- normalize.css must come before main.css — main.css overrides normalise -->
<link rel="stylesheet" href="/css/normalize.css">
<link rel="stylesheet" href="/css/main.css">
<link rel="stylesheet" href="/css/page-specific.css">  <!-- overrides main.css -->
```

### Cascade Layers (Modern CSS)

CSS Cascade Layers give you explicit control over override order:

```css
/* Declare layer order (first = lowest priority) */
@layer reset, base, components, utilities;

@layer reset {
  * { margin: 0; padding: 0; box-sizing: border-box; }
}

@layer base {
  body { font-family: var(--font-body); }
  h1 { font-size: 2.5rem; }
}

@layer components {
  .btn { padding: 0.5rem 1rem; }
}

@layer utilities {
  .mt-auto { margin-top: auto !important; }
}
```

---

## 3.2 Inheritance

Some CSS properties are **inherited** by child elements automatically; others are not.

### Inherited Properties (subset)

These flow down from parent to child:

```
color           font-family      font-size        font-weight
font-style      font-variant     line-height      letter-spacing
text-align      text-transform   text-decoration  word-spacing
list-style      cursor           visibility       quotes
```

### Non-Inherited Properties (subset)

These must be explicitly set on each element:

```
margin          padding          border           background
width           height           display          position
top/right/bottom/left            float            overflow
box-shadow      border-radius
```

### Why Inheritance Matters

```css
/* Set font on body — ALL text descendants inherit it */
body {
  font-family: 'Inter', sans-serif;
  color: #333;
  font-size: 16px;
  line-height: 1.6;
}

/* No need to set font-family on every element */
/* p, h1-h6, span, li, td, etc. all inherit from body */
```

```html
<body>  ← font-family: 'Inter', color: #333
  <div>
    <p>This text inherits font and colour from body</p>
    <ul>
      <li>So does this list item</li>
    </ul>
  </div>
</body>
```

### Controlling Inheritance

```css
/* Force inheritance (useful for non-inherited properties) */
.child-inherits-border {
  border: inherit;
  background: inherit;
}

/* Reset to initial/default value */
.reset-colour {
  color: initial;         /* browser default */
}

/* Inherit from parent explicitly */
.btn {
  color: inherit;         /* inherits parent colour */
  font-size: inherit;     /* inherits parent font-size */
}

/* Reset everything to initial values */
.isolated-component {
  all: initial;
}

/* Reset but keep inheritance */
.isolated-component {
  all: unset;
}
```

---

## 3.3 Computed Values

CSS values go through several stages of calculation:

1. **Specified value** — what you wrote: `font-size: 1.5em`
2. **Computed value** — relative values resolved: `font-size: 24px` (if parent is 16px)
3. **Used value** — after layout calculations: final pixel value
4. **Actual value** — after browser rounding: `24px`

### Why This Matters in Practice

```css
/* Parent: 16px */
.parent {
  font-size: 16px;
}

/* Child: 1.5em = 1.5 × 16px = 24px */
.parent .child {
  font-size: 1.5em;
}

/* Grandchild: 1.5em = 1.5 × 24px = 36px — compounding! */
.parent .child .grandchild {
  font-size: 1.5em;
}
```

This **em compounding** is why `rem` (root em) is often preferred for font sizes.

---

## 3.4 CSS Units

### Absolute Units

These have fixed physical sizes:

| Unit | Equivalent | Use case |
|------|-----------|---------|
| `px` | 1/96th of an inch (logical) | Most CSS, borders, shadows |
| `pt` | 1/72nd of an inch | Print stylesheets only |
| `cm`, `mm`, `in` | Physical measurements | Print stylesheets only |

> **Note:** CSS `px` is not a physical pixel on high-DPI (retina) screens. It's a logical pixel — a `1px` CSS value may be 2 or 3 physical pixels. This is why images look crisp on retina when you specify `@2x` versions.

### Relative Units

These scale relative to some reference:

| Unit | Relative to | Use case |
|------|-----------|---------|
| `em` | Parent element's font-size | Padding/margin relative to text size |
| `rem` | Root (`<html>`) font-size | Font sizes, spacing (no compounding) |
| `%` | Parent element's dimension | Widths, max-widths |
| `vw` | 1% of viewport width | Fluid layouts, hero sections |
| `vh` | 1% of viewport height | Full-screen sections |
| `vmin` | 1% of smaller viewport dimension | |
| `vmax` | 1% of larger viewport dimension | |
| `ch` | Width of `0` character | Form field widths |
| `ex` | x-height of current font | |
| `lh` | Line height | |
| `dvh` | Dynamic viewport height (excludes mobile browser chrome) | Mobile full-screen |

### Practical Unit Guide

```css
/* Root font size */
html { font-size: 16px; }  /* or 62.5% for 1rem = 10px */

/* Font sizes: use rem */
h1 { font-size: 2.5rem; }   /* 40px */
h2 { font-size: 2rem; }     /* 32px */
p  { font-size: 1rem; }     /* 16px */
small { font-size: 0.875rem; } /* 14px */

/* Spacing: rem for consistent scale */
.section { padding: 4rem 2rem; }  /* 64px top/bottom, 32px sides */
.btn { padding: 0.5rem 1.25rem; } /* 8px top/bottom, 20px sides */

/* Widths: % or vw for responsiveness */
.container { max-width: 1200px; width: 100%; }
.hero { min-height: 100vh; }

/* Component padding: em (scales with font size) */
.card { padding: 1.5em; }

/* Borders, shadows: px (don't need to scale) */
.input { border: 1px solid #ddd; }
.card  { box-shadow: 0 2px 4px rgba(0,0,0,0.1); }

/* Form widths: ch (character-based) */
input[type="date"] { width: 12ch; }
input[type="number"] { width: 6ch; }

/* Avoid: px for font sizes (blocks user zoom in old IE) */
```

### `clamp()` — Fluid Typography

```css
/* clamp(minimum, ideal, maximum) */
h1 {
  font-size: clamp(1.5rem, 5vw, 3rem);
  /* 
     Never smaller than 1.5rem (24px)
     Ideally 5% of viewport width
     Never larger than 3rem (48px)
  */
}

.container {
  padding-inline: clamp(1rem, 5%, 3rem);
}
```

### Logical Properties (Modern CSS)

```css
/* Traditional: tied to physical directions */
margin-left: 1rem;
margin-right: 1rem;
padding-top: 2rem;
padding-bottom: 1rem;

/* Logical: adapts to writing direction (RTL support) */
margin-inline: 1rem;   /* left + right (or right + left in RTL) */
margin-block: 2rem 1rem;  /* top bottom */
padding-inline-start: 1rem; /* left in LTR, right in RTL */
```

Logical properties are essential for building applications that support Arabic, Hebrew, and other RTL languages.

---

## 3.5 CSS Custom Properties (Variables)

Custom properties store reusable values:

```css
/* Define on :root — available everywhere */
:root {
  /* Colour palette */
  --color-primary:      #0066cc;
  --color-primary-dark: #0052a3;
  --color-secondary:    #6c757d;
  --color-success:      #28a745;
  --color-warning:      #ffc107;
  --color-error:        #dc3545;
  --color-text:         #333333;
  --color-text-muted:   #6c757d;
  --color-bg:           #ffffff;
  --color-border:       #dee2e6;

  /* Typography */
  --font-sans:    'Inter', system-ui, -apple-system, sans-serif;
  --font-mono:    'JetBrains Mono', 'Fira Code', monospace;
  --font-size-base: 1rem;
  --line-height-base: 1.6;

  /* Spacing scale */
  --space-xs:   0.25rem;  /*  4px */
  --space-sm:   0.5rem;   /*  8px */
  --space-md:   1rem;     /* 16px */
  --space-lg:   1.5rem;   /* 24px */
  --space-xl:   2rem;     /* 32px */
  --space-2xl:  3rem;     /* 48px */
  --space-3xl:  4rem;     /* 64px */

  /* Border radius */
  --radius-sm:  4px;
  --radius-md:  8px;
  --radius-lg:  16px;
  --radius-full: 9999px;

  /* Shadows */
  --shadow-sm:  0 1px 3px rgba(0,0,0,0.12);
  --shadow-md:  0 4px 12px rgba(0,0,0,0.15);
  --shadow-lg:  0 8px 24px rgba(0,0,0,0.18);

  /* Z-index scale */
  --z-dropdown: 100;
  --z-sticky:   200;
  --z-modal:    300;
  --z-toast:    400;
}

/* Usage */
.btn-primary {
  background-color: var(--color-primary);
  color: white;
  padding: var(--space-sm) var(--space-lg);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.btn-primary:hover {
  background-color: var(--color-primary-dark);
}
```

### Dark Mode with Custom Properties

```css
:root {
  --color-bg: #ffffff;
  --color-text: #333333;
  --color-card-bg: #f8f9fa;
}

@media (prefers-color-scheme: dark) {
  :root {
    --color-bg: #1a1a2e;
    --color-text: #e0e0e0;
    --color-card-bg: #16213e;
  }
}

/* All components that use these variables automatically adapt — no changes needed */
body { background: var(--color-bg); color: var(--color-text); }
.card { background: var(--color-card-bg); }
```

---

## Key Takeaways

- The cascade resolves conflicts: `!important` > inline > ID > class > element > inherited > defaults
- Source order is the tiebreaker for equal specificity — last rule wins
- Inherited properties flow down automatically (colour, font, line-height)
- Use `rem` for font sizes (no compounding), `em` for component-relative spacing
- Custom properties (`--variable`) are the foundation of a maintainable design system

---

## Self-Check Questions

1. Two rules both match a `<p>` element with the same specificity. Which wins?
2. Why does setting `font-family` on `body` affect all text on the page?
3. What is the difference between `em` and `rem`?
4. Why is `clamp()` useful for responsive typography?
5. How do CSS custom properties enable dark mode without duplicating component styles?
