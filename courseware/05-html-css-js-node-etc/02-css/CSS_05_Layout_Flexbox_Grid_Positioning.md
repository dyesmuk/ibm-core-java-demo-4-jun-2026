# CSS Module 05 — Layout: Display, Float, Flexbox, Grid & Positioning

## 5.1 The `display` Property

`display` controls how an element participates in layout.

### Block vs Inline vs Inline-Block

```css
/* Block: full width, starts on new line, respects width/height */
display: block;     /* div, p, h1-h6, ul, li, table, form */

/* Inline: flows with text, width/height/top-bottom margins ignored */
display: inline;    /* span, a, strong, em, img (sort of) */

/* Inline-block: flows with text BUT respects width/height/margins */
display: inline-block;   /* the old way to do side-by-side blocks */

/* Grid and Flex: modern layout */
display: flex;
display: grid;
display: inline-flex;
display: inline-grid;

/* Hide completely: not rendered, no space taken */
display: none;      

/* Hide visually but keep space */
visibility: hidden;
```

### Visually Hidden (Accessible)

```css
/* Screen-reader-only: hidden visually, readable by assistive tech */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
```

---

## 5.2 Float and Clear (Legacy, Still Encountered)

Float was the primary layout mechanism before Flexbox. You will encounter it in legacy code.

```css
/* Float an image, text wraps around it */
.article-image {
  float: left;
  margin-right: 1.5rem;
  margin-bottom: 1rem;
}

/* Clear: prevent content from wrapping alongside floated elements */
.clear { clear: both; }       /* clear both sides */
.clear-left { clear: left; }  /* clear left floats only */
.clear-right { clear: right; }

/* Clearfix: make parent contain its floated children */
.clearfix::after {
  content: "";
  display: table;
  clear: both;
}
```

**Modern usage:** Float text wrapping around an image is still valid. But for multi-column layout, use Flexbox or Grid.

---

## 5.3 Flexbox — One-Dimensional Layout

Flexbox arranges items along a **single axis** (row or column).

### Flex Container Properties

```css
.flex-container {
  display: flex;                          /* or inline-flex */
  
  /* Main axis direction */
  flex-direction: row;                    /* row | row-reverse | column | column-reverse */
  
  /* Wrapping */
  flex-wrap: wrap;                        /* nowrap | wrap | wrap-reverse */
  
  /* Shorthand: direction + wrap */
  flex-flow: row wrap;
  
  /* Alignment on main axis */
  justify-content: flex-start;           /* flex-start | flex-end | center | 
                                            space-between | space-around | space-evenly */
  
  /* Alignment on cross axis */
  align-items: stretch;                  /* stretch | flex-start | flex-end | 
                                            center | baseline */
  
  /* Multi-line cross-axis alignment */
  align-content: flex-start;            /* stretch | flex-start | flex-end | 
                                           center | space-between | space-around */
  
  /* Gap between items */
  gap: 1rem;                             /* row-gap and column-gap */
  gap: 1rem 2rem;                        /* row-gap column-gap */
  row-gap: 1rem;
  column-gap: 2rem;
}
```

### Flex Item Properties

```css
.flex-item {
  /* Grow: how much to grow if extra space */
  flex-grow: 0;                          /* default: don't grow */
  flex-grow: 1;                          /* take available space */
  
  /* Shrink: how much to shrink if too little space */
  flex-shrink: 1;                        /* default: can shrink */
  flex-shrink: 0;                        /* don't shrink (fixed width) */
  
  /* Basis: initial size before grow/shrink */
  flex-basis: auto;                      /* content size */
  flex-basis: 200px;                     /* fixed starting size */
  flex-basis: 0;                         /* ignore content size */
  
  /* Shorthand: grow shrink basis */
  flex: 0 0 auto;                        /* don't grow, don't shrink, auto size */
  flex: 1;                               /* = flex: 1 1 0 — grow and shrink equally */
  flex: 1 1 auto;                        /* grow and shrink from natural size */
  flex: none;                            /* = flex: 0 0 auto — rigid */
  
  /* Override align-items for this item */
  align-self: center;
  
  /* Order (default: 0) */
  order: -1;                             /* move to beginning */
  order: 2;                              /* move towards end */
}
```

### Common Flexbox Patterns

```css
/* Navigation bar */
.nav {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

/* Push last item to far right */
.nav .nav-logo { margin-right: auto; }

/* Card grid (auto-wrap) */
.card-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
}
.card-grid .card {
  flex: 1 1 280px; /* grow, shrink, base 280px */
  max-width: 400px;
}

/* Vertical centring */
.centred {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

/* Form row: label + input side by side */
.form-row {
  display: flex;
  align-items: baseline;
  gap: 1rem;
}
.form-row label { flex: 0 0 160px; text-align: right; }
.form-row input  { flex: 1; }

/* Header with logo + nav + actions */
.site-header {
  display: flex;
  align-items: center;
  padding: 0 2rem;
}
.site-header .logo    { margin-right: auto; }
.site-header nav      { display: flex; gap: 1rem; }
.site-header .actions { margin-left: auto; display: flex; gap: 0.5rem; }
```

---

## 5.4 CSS Grid — Two-Dimensional Layout

CSS Grid arranges items in **rows AND columns**.

### Grid Container Properties

```css
.grid-container {
  display: grid;

  /* Define columns */
  grid-template-columns: 200px 1fr 1fr;        /* 3 cols: fixed + 2 equal */
  grid-template-columns: repeat(3, 1fr);        /* 3 equal columns */
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); /* responsive */
  grid-template-columns: 250px 1fr;            /* sidebar + content */

  /* Define rows */
  grid-template-rows: auto;                     /* rows size to content */
  grid-template-rows: 80px 1fr 60px;           /* header + main + footer */

  /* Named areas */
  grid-template-areas:
    "header  header"
    "sidebar main  "
    "footer  footer";

  /* Gap */
  gap: 1.5rem;
  row-gap: 1rem;
  column-gap: 2rem;

  /* Alignment */
  justify-items: stretch;   /* horizontal alignment of items in cells */
  align-items: stretch;     /* vertical alignment of items in cells */
  justify-content: start;   /* alignment of grid tracks in container */
  align-content: start;
}
```

### Grid Item Placement

```css
/* By column/row number */
.hero-image {
  grid-column: 1 / 3;   /* span from column line 1 to 3 */
  grid-row: 1 / 2;
}

/* Span syntax */
.full-width { grid-column: 1 / -1; }     /* span all columns */
.double-wide { grid-column: span 2; }   /* span 2 columns */

/* Named areas */
.site-header { grid-area: header; }
.sidebar     { grid-area: sidebar; }
.main        { grid-area: main; }
.site-footer { grid-area: footer; }

/* Override cell alignment */
.centred-item {
  justify-self: center;
  align-self: center;
}
```

### Common Grid Patterns

```css
/* Holy Grail Layout */
.page {
  display: grid;
  grid-template-columns: 250px 1fr 200px;
  grid-template-rows: 80px 1fr 60px;
  grid-template-areas:
    "header  header  header "
    "sidebar main    aside  "
    "footer  footer  footer ";
  min-height: 100vh;
}

/* Responsive card grid: auto-fill creates as many columns as fit */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1.5rem;
}

/* Magazine layout */
.magazine {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 1rem;
}
.feature-article { grid-column: span 4; grid-row: span 2; }
.side-article    { grid-column: span 2; }

/* CSS Grid for alignment only (no defined tracks) */
.form-grid {
  display: grid;
  grid-template-columns: max-content 1fr;
  gap: 1rem 2rem;
  align-items: center;
}
```

---

## 5.5 CSS Positioning

### `position: static` (Default)

Elements follow normal document flow. `top`, `right`, `bottom`, `left`, `z-index` have no effect.

### `position: relative`

Element stays in normal flow but can be offset. Creates a positioning context for absolute children.

```css
.card {
  position: relative;  /* establishes positioning context */
}

.card .badge {
  position: absolute;   /* positioned relative to .card */
  top: 0.5rem;
  right: 0.5rem;
}
```

### `position: absolute`

Removed from flow. Positioned relative to nearest **positioned ancestor** (non-static):

```css
/* Tooltip */
.tooltip-container {
  position: relative;  /* becomes the reference */
}

.tooltip {
  position: absolute;
  top: calc(100% + 8px);  /* below the container */
  left: 50%;
  transform: translateX(-50%);  /* centre horizontally */
  z-index: var(--z-dropdown);
}

/* Full overlay on parent */
.overlay {
  position: absolute;
  inset: 0;  /* shorthand for top:0 right:0 bottom:0 left:0 */
  background: rgba(0,0,0,0.5);
}
```

### `position: fixed`

Positioned relative to the **viewport**. Doesn't scroll with the page:

```css
/* Sticky header */
.site-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: var(--z-sticky);
  background: white;
  box-shadow: var(--shadow-sm);
}

/* Offset content to account for fixed header */
body { padding-top: 80px; }  /* same as header height */

/* Toast notification */
.toast-container {
  position: fixed;
  bottom: 1.5rem;
  right: 1.5rem;
  z-index: var(--z-toast);
}
```

### `position: sticky`

Scrolls normally until it hits a threshold, then sticks:

```css
/* Table header that sticks to top when scrolling */
thead th {
  position: sticky;
  top: 0;
  background: white;  /* needed to cover scrolling content */
  z-index: 1;
}

/* Sidebar that sticks */
.sidebar {
  position: sticky;
  top: calc(80px + 1rem);  /* account for fixed header */
  max-height: calc(100vh - 80px - 2rem);
  overflow-y: auto;
}
```

### `z-index`

Controls stacking order. Only works on positioned elements (non-static):

```css
:root {
  --z-base:     0;
  --z-dropdown: 100;
  --z-sticky:   200;
  --z-overlay:  300;
  --z-modal:    400;
  --z-toast:    500;
}

.dropdown  { z-index: var(--z-dropdown); }
.modal     { z-index: var(--z-modal); }
.toast     { z-index: var(--z-toast); }
```

---

## 5.6 Responsive Layout with Media Queries

```css
/* Mobile-first: base styles for small screens */
.container {
  width: 100%;
  padding-inline: 1rem;
}

.grid {
  display: grid;
  grid-template-columns: 1fr;  /* single column on mobile */
  gap: 1rem;
}

/* Tablet: 640px+ */
@media (min-width: 640px) {
  .container { padding-inline: 1.5rem; }
  .grid { grid-template-columns: repeat(2, 1fr); }
}

/* Desktop: 1024px+ */
@media (min-width: 1024px) {
  .container { padding-inline: 2rem; }
  .grid { grid-template-columns: repeat(3, 1fr); }
}

/* Large desktop: 1280px+ */
@media (min-width: 1280px) {
  .container { max-width: 1200px; margin-inline: auto; }
  .grid { grid-template-columns: repeat(4, 1fr); }
}

/* Print media query */
@media print {
  .no-print, nav, .sidebar, .ads { display: none; }
  body { font-size: 12pt; color: black; }
  a::after { content: " (" attr(href) ")"; }  /* print URLs */
}

/* Accessibility: reduced motion */
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}

/* Dark mode */
@media (prefers-color-scheme: dark) {
  :root { /* override custom properties */ }
}
```

### Common Breakpoints

```css
/* Tailwind-inspired breakpoints */
/* sm  */ @media (min-width: 640px)  { }
/* md  */ @media (min-width: 768px)  { }
/* lg  */ @media (min-width: 1024px) { }
/* xl  */ @media (min-width: 1280px) { }
/* 2xl */ @media (min-width: 1536px) { }
```

---

## Key Takeaways

- `display: flex` for one-dimensional layouts; `display: grid` for two-dimensional layouts
- Float is legacy — use for text wrapping around images only
- `position: relative` creates a positioning context for `absolute` children
- `position: sticky` is excellent for table headers and sidebars
- Keep a z-index scale in custom properties to manage stacking order
- Write CSS mobile-first: base styles for small screens, `min-width` queries to enhance for larger

---

## Self-Check Questions

1. What is the difference between `flex-grow`, `flex-shrink`, and `flex-basis`?
2. How do you create a two-column layout (sidebar + main) using CSS Grid?
3. What is the difference between `position: fixed` and `position: sticky`?
4. How do you vertically and horizontally centre an element using Flexbox?
5. What does `grid-column: 1 / -1` mean?
