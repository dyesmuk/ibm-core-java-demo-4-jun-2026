# CSS Module 06 — Typography

## 6.1 Font Families

```css
/* System font stack: fastest, no network request, native feel */
body {
  font-family: system-ui, -apple-system, BlinkMacSystemFont,
               'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell,
               'Helvetica Neue', Arial, sans-serif;
}

/* Monospace stack */
code, pre, kbd, samp {
  font-family: 'JetBrains Mono', 'Fira Code', 'Cascadia Code',
               Consolas, 'Courier New', monospace;
}

/* Serif for long-form reading */
.article-body {
  font-family: Georgia, 'Times New Roman', Times, serif;
}
```

### Web Fonts with `@font-face`

```css
/* Self-hosted (best performance, no third-party dependency) */
@font-face {
  font-family: 'Inter';
  src: url('/fonts/inter-var.woff2') format('woff2');
  font-weight: 100 900;       /* variable font: range of weights */
  font-style: normal;
  font-display: swap;         /* show fallback font while loading */
}

/* Or via Google Fonts (easy, no self-hosting) */
/* In <head>: */
/* <link rel="preconnect" href="https://fonts.googleapis.com"> */
/* <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap"> */

body { font-family: 'Inter', system-ui, sans-serif; }
```

`font-display: swap` ensures text is visible immediately using a fallback font while the web font loads, rather than showing invisible text (FOIT — Flash of Invisible Text).

---

## 6.2 Font Size

```css
/* Base size on html element — all rem values reference this */
html { font-size: 16px; }

/* Scale using rem (no compounding) */
h1 { font-size: 2.5rem; }    /* 40px */
h2 { font-size: 2rem; }      /* 32px */
h3 { font-size: 1.75rem; }   /* 28px */
h4 { font-size: 1.5rem; }    /* 24px */
h5 { font-size: 1.25rem; }   /* 20px */
h6 { font-size: 1rem; }      /* 16px */
p  { font-size: 1rem; }      /* 16px */
small { font-size: 0.875rem; } /* 14px */
.caption { font-size: 0.75rem; } /* 12px */

/* Fluid typography: scales between viewport sizes */
h1 {
  font-size: clamp(1.75rem, 4vw + 1rem, 3rem);
}
```

### Type Scale

A harmonious type scale is built on a ratio. Common ratios:

| Ratio | Name | Example |
|-------|------|---------|
| 1.067 | Minor Second | 12, 12.8, 13.7, 14.6... |
| 1.125 | Major Second | 12, 13.5, 15.2, 17.1... |
| 1.25  | Major Third | 12, 15, 18.75, 23.4... |
| 1.333 | Perfect Fourth | 12, 16, 21.3, 28.4... |
| 1.5   | Perfect Fifth | 12, 18, 27, 40.5... |

---

## 6.3 Font Weight, Style, and Variant

```css
/* Font weight */
.thin       { font-weight: 100; }
.light      { font-weight: 300; }
.regular    { font-weight: 400; }  /* normal */
.medium     { font-weight: 500; }
.semibold   { font-weight: 600; }
.bold       { font-weight: 700; }  /* bold */
.extrabold  { font-weight: 800; }
.black      { font-weight: 900; }

/* Font style */
.italic   { font-style: italic; }
.oblique  { font-style: oblique; }
.normal   { font-style: normal; }

/* Small caps */
.small-caps { font-variant: small-caps; }
```

---

## 6.4 Line Height and Letter Spacing

```css
/* Line height (unitless preferred — inherits as multiplier, not fixed px) */
body     { line-height: 1.6; }      /* 1.6× font size */
headings { line-height: 1.2; }      /* tighter for large text */
.compact { line-height: 1.4; }
.loose   { line-height: 2; }

/* Letter spacing */
h1       { letter-spacing: -0.02em; }   /* tighter for headings */
.caption { letter-spacing: 0.05em; }    /* looser for small text */
.badge   { letter-spacing: 0.08em; text-transform: uppercase; }

/* Word spacing */
.generous { word-spacing: 0.1em; }

/* Optimal line length: 45–75 characters */
.article-body {
  max-width: 65ch;    /* ~65 characters wide */
  line-height: 1.7;
}
```

---

## 6.5 Text Alignment and Decoration

```css
/* Alignment */
.left    { text-align: left; }     /* default in LTR */
.centre  { text-align: center; }
.right   { text-align: right; }
.justify { text-align: justify; }  /* avoid on screen; use for print */

/* Use logical property for RTL support */
.body-text { text-align: start; }  /* left in LTR, right in RTL */

/* Decoration */
a:hover       { text-decoration: underline; }
a             { text-decoration-color: currentColor; }
.strikethrough { text-decoration: line-through; }
.overline      { text-decoration: overline; }

/* Modern text-decoration shorthand */
a {
  text-decoration: underline 2px var(--color-primary);
  text-underline-offset: 3px;  /* gap between text and underline */
}

/* Transform */
.uppercase   { text-transform: uppercase; }
.lowercase   { text-transform: lowercase; }
.capitalize  { text-transform: capitalize; }
.badge       { text-transform: uppercase; letter-spacing: 0.06em; font-size: 0.75rem; }
```

---

## 6.6 Text Overflow and Wrapping

```css
/* Truncate with ellipsis (requires all three) */
.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

/* Clamp to N lines (multi-line truncation) */
.clamp-3-lines {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Word breaking for long strings (URLs, IDs) */
.word-break { word-break: break-all; }
.overflow-wrap { overflow-wrap: break-word; }  /* preferred: breaks at word boundaries */

/* White space control */
.code-inline { white-space: pre; }           /* preserve spaces and newlines */
.no-wrap     { white-space: nowrap; }        /* prevent wrapping */
.pre-wrap    { white-space: pre-wrap; }      /* preserve spaces, allow wrapping */
```

---

## 6.7 Vertical Rhythm

Consistent spacing between text elements creates a harmonious page:

```css
/* Base vertical rhythm on line-height */
:root {
  --line-height: 1.6;
  --spacing-unit: calc(1rem * var(--line-height)); /* = 1.6rem = 25.6px */
}

/* Headings: space above, moderate space below */
h1, h2, h3, h4, h5, h6 {
  margin-top: 2em;
  margin-bottom: 0.5em;
  line-height: 1.2;
}

/* First heading: no top margin */
h1:first-child, h2:first-child { margin-top: 0; }

/* Paragraph spacing */
p + p { margin-top: 1rem; }

/* Adjacent heading: reduce top margin */
h1 + h2, h2 + h3 { margin-top: 0.5em; }
```

---

## Key Takeaways

- Use a system font stack for performance; web fonts for branded experience
- `font-display: swap` prevents invisible text during font load
- Use unitless `line-height` (e.g., `1.6`) — inherits as a multiplier
- `clamp()` enables fluid typography without media queries
- Keep article text to `~65ch` width for optimal readability

---

## Self-Check Questions

1. What is FOIT and how does `font-display: swap` solve it?
2. Why use unitless `line-height: 1.6` instead of `line-height: 25.6px`?
3. How do you truncate a single line of text with an ellipsis?
4. What CSS property limits text to 3 lines with an ellipsis?
5. Why is `text-align: start` better than `text-align: left` for international applications?
