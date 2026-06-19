# CSS Module 02 — Selectors: Complete Reference

## 2.1 Why Selectors Matter

The selector determines *which* elements your rule applies to. Writing the wrong selector is the most common source of CSS bugs. Master selectors and half your CSS problems disappear.

---

## 2.2 Basic Selectors

### Type (Element) Selector

Matches all elements of a given type:

```css
p    { line-height: 1.6; }
h1   { font-size: 2.5rem; }
a    { color: var(--color-link); }
ul   { padding-left: 1.5rem; }
```

**Specificity:** 0,0,1

### Class Selector

Matches elements with a specific class. Most common selector in enterprise CSS:

```css
.btn           { padding: 0.5rem 1rem; border-radius: 4px; }
.btn-primary   { background: var(--color-primary); color: white; }
.card          { border: 1px solid #ddd; border-radius: 8px; }
.error-message { color: var(--color-error); font-size: 0.875rem; }
```

**Specificity:** 0,1,0

### ID Selector

Matches the one element with a specific id:

```css
#main-content  { max-width: 1200px; margin: 0 auto; }
#loginForm     { max-width: 400px; }
```

**Specificity:** 1,0,0 — very high. Avoid in shared stylesheets; ID styles are nearly impossible to override without `!important`.

### Universal Selector

Matches every element:

```css
/* Box-sizing reset (the most common use of *) */
*, *::before, *::after {
  box-sizing: border-box;
}
```

**Specificity:** 0,0,0

---

## 2.3 Attribute Selectors

Extremely powerful, underused in enterprise CSS:

```css
/* Has the attribute (any value) */
[required]              { border-left: 3px solid var(--color-warning); }
[disabled]              { opacity: 0.5; cursor: not-allowed; }

/* Exact attribute value */
[type="submit"]         { background: var(--color-primary); }
[type="checkbox"]       { width: 1.25rem; height: 1.25rem; }
[method="post"]         { }  /* targets forms */

/* Attribute value contains word (space-separated) */
[class~="btn"]          { cursor: pointer; }

/* Attribute value starts with */
[href^="https"]         { /* secure links */ }
[href^="mailto"]        { /* email links */ }
[src^="data:"]          { /* data URI images */ }

/* Attribute value ends with */
[href$=".pdf"]          { /* PDF links — add icon */ }
[href$=".xlsx"]         { /* Excel files */ }

/* Attribute value contains substring */
[href*="external"]      { /* links with "external" anywhere in URL */ }
[class*="col-"]         { float: left; }

/* Attribute value starts with (or equals), for language codes */
[lang|="en"]            { quotes: """ """ "'" "'"; }

/* Case-insensitive matching */
[type="TEXT" i]         { /* matches type="text", type="TEXT", etc. */ }
```

### Practical Attribute Selector Patterns

```css
/* Add PDF icon to PDF links automatically */
a[href$=".pdf"]::after {
  content: " (PDF)";
  font-size: 0.8em;
  color: var(--color-muted);
}

/* External link icon */
a[href^="http"]:not([href*="yourdomain.com"])::after {
  content: " ↗";
}

/* Required field indicator */
input[required] + label::after,
label:has(+ input[required])::after {
  content: " *";
  color: var(--color-error);
  aria-hidden: true;
}
```

---

## 2.4 Pseudo-Class Selectors

Pseudo-classes match elements in specific **states** or **positions**:

### User Action States

```css
/* Link states — must be in this order: LVHA */
a:link      { color: var(--color-link); }          /* unvisited */
a:visited   { color: var(--color-link-visited); }  /* visited */
a:hover     { text-decoration: underline; }        /* mouse over */
a:active    { color: var(--color-link-active); }   /* being clicked */

/* Focus — CRITICAL for keyboard accessibility */
:focus {
  outline: 3px solid var(--color-focus);
  outline-offset: 2px;
}
/* Remove only when replacing with visible alternative */
:focus:not(:focus-visible) { outline: none; }
:focus-visible { outline: 3px solid var(--color-focus); }

/* Hover — avoid using alone for interactive states */
button:hover { background-color: var(--color-primary-dark); }
.card:hover  { box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
```

### Form States

```css
input:disabled          { opacity: 0.5; background: #f5f5f5; }
input:enabled           { background: white; }
input:checked           { /* styled checkbox/radio */ }
input:required          { border-left: 3px solid orange; }
input:optional          { border-left: 3px solid #ddd; }

/* HTML5 constraint validation states */
input:valid             { border-color: var(--color-success); }
input:invalid           { border-color: var(--color-error); }
/* Only show errors after the user has interacted (use :user-invalid in newer browsers) */
input:user-invalid      { border-color: var(--color-error); }

input:placeholder-shown { /* while placeholder is visible */ }
input:not(:placeholder-shown) { /* when user has typed something */ }

/* Read-only and read-write */
input:read-only         { background: #f8f8f8; }
input:read-write        { background: white; }
```

### Structural Pseudo-Classes

```css
/* Position in parent */
li:first-child          { border-top: none; }
li:last-child           { border-bottom: none; }
li:nth-child(2)         { /* 2nd item */ }
li:nth-child(odd)       { background: #f9f9f9; }   /* 1st, 3rd, 5th... */
li:nth-child(even)      { background: white; }     /* 2nd, 4th, 6th... */
li:nth-child(3n)        { /* every 3rd: 3, 6, 9 */ }
li:nth-child(3n+1)      { /* 1, 4, 7, 10... */ }
li:nth-last-child(1)    { /* last item */ }

/* Type-specific: counts only same-type siblings */
p:first-of-type         { font-size: 1.1em; }  /* first <p> among siblings */
h2:last-of-type         { margin-bottom: 0; }

/* Negation */
button:not([disabled])  { cursor: pointer; }
input:not([type="hidden"]):not([type="submit"]) { border: 1px solid #ccc; }
li:not(:last-child)     { border-bottom: 1px solid #eee; }

/* Only child */
li:only-child           { list-style: none; }  /* hide bullets when only one item */
```

### Other Useful Pseudo-Classes

```css
:root                   { /* html element — use for CSS custom properties */ }
:empty                  { display: none; }  /* hide empty elements */
:target                 { /* element whose id matches URL anchor */ 
                          background: yellow; }
:is(h1, h2, h3)         { margin-top: 2rem; }  /* matches any of these */
:where(h1, h2, h3)      { margin-top: 2rem; }  /* like :is() but 0 specificity */
:has(> img)             { padding: 0; }         /* parent selector! */
```

---

## 2.5 Pseudo-Element Selectors

Pseudo-elements create virtual elements before or after content:

```css
/* Insert content before/after an element */
.required-label::before { content: "* "; color: var(--color-error); }
.external-link::after   { content: " ↗"; font-size: 0.8em; }

/* First line and first letter */
article p:first-child::first-line   { font-weight: 600; }
article p:first-child::first-letter { font-size: 3em; float: left; }

/* Custom placeholder styling */
input::placeholder      { color: #999; font-style: italic; }

/* Selection styling */
::selection             { background: var(--color-primary); color: white; }

/* Custom scrollbar (Webkit) */
::-webkit-scrollbar        { width: 8px; }
::-webkit-scrollbar-track  { background: #f1f1f1; }
::-webkit-scrollbar-thumb  { background: #888; border-radius: 4px; }
```

---

## 2.6 Combinator Selectors

Combinators express relationships between elements:

### Descendant Combinator (space)

```css
/* Any <a> anywhere inside .nav */
.nav a { color: white; text-decoration: none; }

/* Any <p> anywhere inside .card */
.card p { margin-bottom: 0.5rem; }
```

**Specificity:** sum of both selectors

### Child Combinator `>`

```css
/* Only direct <li> children of <ul> — not nested list items */
ul > li { border-bottom: 1px solid #eee; }

/* Only direct children with class .item inside .grid */
.grid > .item { padding: 1rem; }
```

### Adjacent Sibling Combinator `+`

```css
/* The <p> immediately following an <h2> */
h2 + p { margin-top: 0; }

/* Error message after an invalid input */
input:invalid + .error-message { display: block; }

/* Label immediately before a checkbox (label comes after in HTML) */
input[type="checkbox"] + label { cursor: pointer; }
```

### General Sibling Combinator `~`

```css
/* All <p> elements that follow an <h2> (same parent) */
h2 ~ p { padding-left: 1rem; }

/* All .tab-panel after a checked radio button */
input[type="radio"]:checked ~ .tab-panel { display: block; }
```

---

## 2.7 Grouping Selectors

Apply the same declarations to multiple selectors — use commas:

```css
/* Instead of repeating: */
h1 { margin-bottom: 1rem; }
h2 { margin-bottom: 1rem; }
h3 { margin-bottom: 1rem; }

/* Group them: */
h1, h2, h3, h4, h5, h6 { margin-bottom: 1rem; }

/* Or use :is() for cleaner grouping */
:is(h1, h2, h3, h4, h5, h6) { margin-bottom: 1rem; }

/* Grouping different selector types */
.btn-primary,
.btn-secondary,
[type="submit"] {
  display: inline-block;
  padding: 0.5rem 1.25rem;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}
```

---

## 2.8 CSS Specificity

When multiple rules match the same element, **specificity** determines which one wins.

### Specificity Calculation

Think of specificity as a three-column number (A, B, C):

| Selector type | A | B | C | Example |
|--------------|---|---|---|---------|
| Inline style | 1 | 0 | 0 | `style="color:red"` |
| ID selector | 0 | 1 | 0 | `#header` |
| Class, pseudo-class, attribute | 0 | 0-n | 0 | `.btn`, `:hover`, `[type]` |
| Element, pseudo-element | 0 | 0 | 0-n | `div`, `::before` |
| Universal `*`, `:where()`, combinators | 0 | 0 | 0 | |

```css
/* Specificity: 0,0,1 */
p { color: black; }

/* Specificity: 0,1,0 */
.intro { color: blue; }

/* Specificity: 0,1,1 (class + element) */
p.intro { color: green; }

/* Specificity: 1,0,0 */
#main { color: red; }

/* Specificity: 0,0,0 (inline style: 1,0,0,0 — different column) */
* { color: grey; }
```

### Comparing Specificity

Compare left to right:
- `1,0,0` beats `0,99,99` — ONE id beats any number of classes
- `0,2,0` beats `0,1,10` — more classes win over more elements
- Equal specificity: **last rule wins** (source order)

```css
/* Which colour wins? */
p { color: black; }         /* 0,0,1 */
.intro { color: blue; }    /* 0,1,0 */
p.intro { color: green; }  /* 0,1,1 ← WINS: highest specificity */
```

### Specificity Pitfalls

```css
/* ❌ Never use !important in shared stylesheets */
.btn { color: white !important; }  /* breaks override chain */

/* ❌ Don't chain IDs and classes unnecessarily */
#sidebar .nav ul li a { color: red; }  /* specificity: 1,1,3 — nightmare to override */

/* ✅ Keep specificity low and flat */
.sidebar-link { color: var(--color-nav-link); }  /* specificity: 0,1,0 — easy to override */
```

### When Is `!important` Acceptable?

1. Utility classes that must always apply: `.sr-only { display: none !important; }`
2. Overriding third-party styles you cannot modify
3. Never in component styles you own

---

## 2.9 Selector Performance

CSS selector matching goes right-to-left. The **key selector** (rightmost) is evaluated first:

```css
/* Browser first finds ALL <a> tags, then checks if parent is .nav */
.nav a { color: white; }

/* More efficient: browser finds .nav-link — more targeted */
.nav-link { color: white; }
```

In practice, modern browsers optimise selector matching so well that selector performance is almost never a real bottleneck. Prioritise readability. Only optimise if profiling shows a genuine issue.

---

## Key Takeaways

- Master the six selector types: type, class, ID, attribute, pseudo-class, pseudo-element
- Combinators express relationships: space (descendant), `>` (child), `+` (adjacent sibling), `~` (general sibling)
- Specificity: inline > ID > class/pseudo-class/attribute > element — compare left to right
- Keep specificity low and flat; avoid `!important` in component CSS
- Use `:focus-visible` for keyboard-visible focus styles

---

## Self-Check Questions

1. What is the specificity of `#sidebar .nav > li a:hover`?
2. Write a selector that targets only PDF links using an attribute selector.
3. What is the difference between `li:first-child` and `li:first-of-type`?
4. How does the child combinator (`>`) differ from the descendant combinator (space)?
5. When is it appropriate to use `!important`?
