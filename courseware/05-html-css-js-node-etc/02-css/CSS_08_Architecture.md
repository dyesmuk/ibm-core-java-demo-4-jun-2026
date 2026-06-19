# CSS Module 08 — CSS Architecture: Normalize, Organisation & Design Systems

## 8.1 Normalize.css

**Normalize.css** is a small stylesheet that makes browser default styles consistent across browsers, while preserving useful defaults (unlike a hard reset that wipes everything).

### What Normalize Does

- Makes `<h1>` inside `<section>` the same size everywhere
- Corrects font inheritance on `<button>`, `<input>`, `<textarea>`
- Fixes line height in all browsers
- Normalises `<pre>` font size and wrapping
- Corrects colour inheritance in Firefox
- Removes inner padding from Firefox's search inputs

### Adding Normalize to Your Spring Boot Project

```html
<!-- Option 1: CDN (development only) -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/normalize/8.0.1/normalize.min.css">

<!-- Option 2: Self-hosted (recommended for production) -->
<!-- Download normalize.css, place in src/main/resources/static/css/ -->
<link rel="stylesheet" th:href="@{/css/normalize.css}">
<link rel="stylesheet" th:href="@{/css/main.css}">   <!-- always AFTER normalize -->
```

### Modern Alternative: A Custom Reset

Many teams prefer a minimal custom reset over full Normalize:

```css
/* Modern CSS Reset (Andy Bell, 2023) */
*, *::before, *::after {
  box-sizing: border-box;
}

* {
  margin: 0;
}

body {
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
}

img, picture, video, canvas, svg {
  display: block;
  max-width: 100%;
}

input, button, textarea, select {
  font: inherit;
}

p, h1, h2, h3, h4, h5, h6 {
  overflow-wrap: break-word;
}

#root, #__next {
  isolation: isolate;
}
```

---

## 8.2 CSS Architecture: File Organisation

For enterprise projects, a well-organised CSS structure is essential:

### The 7-1 Pattern (Sass-based)

```
styles/
├── abstracts/          ← No output: variables, mixins, functions
│   ├── _variables.css
│   ├── _mixins.scss
│   └── _functions.scss
├── vendors/            ← Third-party CSS
│   └── _normalize.css
├── base/               ← Global defaults
│   ├── _reset.css
│   └── _typography.css
├── layout/             ← Structural layout
│   ├── _header.css
│   ├── _footer.css
│   ├── _sidebar.css
│   └── _grid.css
├── components/         ← Reusable UI components
│   ├── _buttons.css
│   ├── _cards.css
│   ├── _forms.css
│   ├── _tables.css
│   └── _modals.css
├── pages/              ← Page-specific styles
│   ├── _dashboard.css
│   └── _login.css
└── main.css            ← Imports all files in order
```

### main.css Imports

```css
/* 1. External / vendor */
@import 'vendors/normalize';

/* 2. Abstracts (no output) */
@import 'abstracts/variables';
@import 'abstracts/mixins';

/* 3. Base */
@import 'base/reset';
@import 'base/typography';

/* 4. Layout */
@import 'layout/grid';
@import 'layout/header';
@import 'layout/footer';

/* 5. Components */
@import 'components/buttons';
@import 'components/cards';
@import 'components/forms';

/* 6. Pages */
@import 'pages/dashboard';
@import 'pages/login';
```

---

## 8.3 CSS Custom Properties as a Design System

```css
/* tokens.css — Design tokens: the single source of truth */
:root {
  /* ══════════════════════════════════════
     COLOUR PALETTE (Primitive tokens)
     ══════════════════════════════════════ */
  /* Primary: Blue */
  --blue-50:  #eff6ff;
  --blue-100: #dbeafe;
  --blue-500: #3b82f6;
  --blue-600: #2563eb;
  --blue-700: #1d4ed8;
  --blue-900: #1e3a8a;

  /* Neutral */
  --gray-50:  #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-400: #9ca3af;
  --gray-500: #6b7280;
  --gray-700: #374151;
  --gray-900: #111827;

  /* Semantic (Green, Red, Yellow, Orange) */
  --green-500: #22c55e;
  --red-500:   #ef4444;
  --yellow-500: #eab308;

  /* ══════════════════════════════════════
     SEMANTIC TOKENS (reference primitives)
     ══════════════════════════════════════ */

  /* Brand */
  --color-primary:       var(--blue-600);
  --color-primary-hover: var(--blue-700);
  --color-primary-light: var(--blue-50);

  /* Status */
  --color-success:       var(--green-500);
  --color-error:         var(--red-500);
  --color-warning:       var(--yellow-500);

  /* Text */
  --color-text:          var(--gray-900);
  --color-text-muted:    var(--gray-500);
  --color-text-inverse:  white;

  /* Backgrounds */
  --color-bg:            white;
  --color-bg-subtle:     var(--gray-50);
  --color-bg-muted:      var(--gray-100);

  /* Borders */
  --color-border:        var(--gray-200);
  --color-border-strong: var(--gray-400);

  /* Links */
  --color-link:          var(--blue-600);
  --color-link-visited:  #7e22ce;

  /* Focus */
  --color-focus:         var(--blue-500);

  /* ══════════════════════════════════════
     TYPOGRAPHY
     ══════════════════════════════════════ */
  --font-sans:   'Inter', system-ui, -apple-system, sans-serif;
  --font-mono:   'JetBrains Mono', 'Fira Code', Consolas, monospace;

  --text-xs:   0.75rem;    /* 12px */
  --text-sm:   0.875rem;   /* 14px */
  --text-base: 1rem;       /* 16px */
  --text-lg:   1.125rem;   /* 18px */
  --text-xl:   1.25rem;    /* 20px */
  --text-2xl:  1.5rem;     /* 24px */
  --text-3xl:  1.875rem;   /* 30px */
  --text-4xl:  2.25rem;    /* 36px */
  --text-5xl:  3rem;       /* 48px */

  --font-normal:    400;
  --font-medium:    500;
  --font-semibold:  600;
  --font-bold:      700;

  --leading-tight:  1.25;
  --leading-snug:   1.375;
  --leading-normal: 1.5;
  --leading-relaxed: 1.625;
  --leading-loose:  2;

  /* ══════════════════════════════════════
     SPACING (based on 4px grid)
     ══════════════════════════════════════ */
  --space-1:  0.25rem;  /* 4px */
  --space-2:  0.5rem;   /* 8px */
  --space-3:  0.75rem;  /* 12px */
  --space-4:  1rem;     /* 16px */
  --space-5:  1.25rem;  /* 20px */
  --space-6:  1.5rem;   /* 24px */
  --space-8:  2rem;     /* 32px */
  --space-10: 2.5rem;   /* 40px */
  --space-12: 3rem;     /* 48px */
  --space-16: 4rem;     /* 64px */
  --space-20: 5rem;     /* 80px */

  /* ══════════════════════════════════════
     BORDER RADIUS
     ══════════════════════════════════════ */
  --radius-sm:   3px;
  --radius-md:   6px;
  --radius-lg:   12px;
  --radius-xl:   16px;
  --radius-full: 9999px;

  /* ══════════════════════════════════════
     SHADOWS
     ══════════════════════════════════════ */
  --shadow-sm:  0 1px 2px rgb(0 0 0 / 5%);
  --shadow-md:  0 4px 6px -1px rgb(0 0 0 / 10%), 0 2px 4px -2px rgb(0 0 0 / 10%);
  --shadow-lg:  0 10px 15px -3px rgb(0 0 0 / 10%), 0 4px 6px -4px rgb(0 0 0 / 10%);
  --shadow-xl:  0 20px 25px -5px rgb(0 0 0 / 10%), 0 8px 10px -6px rgb(0 0 0 / 10%);
  --shadow-inner: inset 0 2px 4px 0 rgb(0 0 0 / 5%);

  /* ══════════════════════════════════════
     TRANSITIONS
     ══════════════════════════════════════ */
  --transition-fast:   150ms ease;
  --transition-base:   200ms ease;
  --transition-slow:   300ms ease;

  /* ══════════════════════════════════════
     Z-INDEX SCALE
     ══════════════════════════════════════ */
  --z-base:     0;
  --z-raised:   10;
  --z-dropdown: 100;
  --z-sticky:   200;
  --z-overlay:  300;
  --z-modal:    400;
  --z-toast:    500;
  --z-tooltip:  600;

  /* ══════════════════════════════════════
     LAYOUT
     ══════════════════════════════════════ */
  --container-sm:  640px;
  --container-md:  768px;
  --container-lg:  1024px;
  --container-xl:  1280px;
  --container-2xl: 1536px;

  --header-height: 64px;
  --sidebar-width: 256px;
}
```

---

## 8.4 Component CSS Pattern

```css
/* components/card.css */

/* Block */
.card {
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

/* Elements */
.card__header {
  padding: var(--space-4) var(--space-6);
  border-bottom: 1px solid var(--color-border);
}

.card__title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--color-text);
  margin: 0;
}

.card__body {
  padding: var(--space-6);
}

.card__footer {
  padding: var(--space-4) var(--space-6);
  border-top: 1px solid var(--color-border);
  background: var(--color-bg-subtle);
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
}

/* Modifiers */
.card--elevated {
  box-shadow: var(--shadow-lg);
}

.card--highlighted {
  border-color: var(--color-primary);
  border-width: 2px;
}

.card--compact .card__header,
.card--compact .card__body {
  padding: var(--space-4);
}

/* Interactive card */
.card--interactive {
  cursor: pointer;
  transition: box-shadow var(--transition-base), transform var(--transition-base);
}
.card--interactive:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}
.card--interactive:focus-within {
  box-shadow: 0 0 0 3px var(--color-focus);
}
```

---

## 8.5 Utility Classes

For frequently used atomic styles (inspired by Tailwind, but handwritten):

```css
/* utilities.css */

/* Display */
.hidden       { display: none; }
.sr-only      { /* screen reader only — see Module 05 */ }
.flex         { display: flex; }
.grid         { display: grid; }
.block        { display: block; }
.inline-block { display: inline-block; }

/* Flex utilities */
.flex-col      { flex-direction: column; }
.flex-wrap     { flex-wrap: wrap; }
.items-center  { align-items: center; }
.items-start   { align-items: flex-start; }
.justify-between { justify-content: space-between; }
.justify-center  { justify-content: center; }
.gap-4         { gap: var(--space-4); }
.gap-6         { gap: var(--space-6); }
.flex-1        { flex: 1; }
.flex-none     { flex: none; }

/* Spacing */
.mt-auto       { margin-top: auto; }
.mx-auto       { margin-inline: auto; }
.p-4           { padding: var(--space-4); }
.p-6           { padding: var(--space-6); }

/* Text */
.text-center   { text-align: center; }
.text-muted    { color: var(--color-text-muted); }
.text-sm       { font-size: var(--text-sm); }
.font-bold     { font-weight: var(--font-bold); }
.truncate      { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

/* Width */
.w-full        { width: 100%; }
.max-w-lg      { max-width: var(--container-sm); }

/* Borders */
.rounded       { border-radius: var(--radius-md); }
.rounded-full  { border-radius: var(--radius-full); }
.border        { border: 1px solid var(--color-border); }

/* Overflow */
.overflow-hidden { overflow: hidden; }
.overflow-auto   { overflow: auto; }
```

---

## 8.6 Dark Mode

```css
/* tokens.css — dark mode token overrides */
@media (prefers-color-scheme: dark) {
  :root {
    --color-bg:           #0f172a;
    --color-bg-subtle:    #1e293b;
    --color-bg-muted:     #334155;
    --color-text:         #f1f5f9;
    --color-text-muted:   #94a3b8;
    --color-border:       #334155;
    --color-border-strong: #475569;
  }
}

/* Allow user preference toggle with a class */
[data-theme="dark"] {
  --color-bg:        #0f172a;
  --color-text:      #f1f5f9;
  /* etc. */
}
```

---

## 8.7 CSS in Spring Boot — Caching Strategy

```java
// WebMvcConfigurer: add cache headers for static assets
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**", "/js/**", "/images/**")
            .addResourceLocations("classpath:/static/css/",
                                  "classpath:/static/js/",
                                  "classpath:/static/images/")
            .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)
                                         .cachePublic()
                                         .immutable());
    }
}
```

For cache-busting, use Spring's resource versioning:

```properties
# application.properties
spring.web.resources.chain.strategy.content.enabled=true
spring.web.resources.chain.strategy.content.paths=/css/**,/js/**
```

```html
<!-- Thymeleaf generates: /css/main-abc12345.css (hash changes when file changes) -->
<link rel="stylesheet" th:href="@{/css/main.css}">
```

---

## Key Takeaways

- Use Normalize.css or a custom reset to level browser defaults; load it before your own CSS
- Organise CSS by: vendors → abstracts → base → layout → components → pages
- Design tokens with CSS custom properties are the foundation of maintainable design systems
- Use BEM naming (block__element--modifier) for component CSS
- Separate primitive tokens (colours) from semantic tokens (purpose) for theme flexibility
- Enable content-based cache busting in Spring Boot for production performance

---

## Self-Check Questions

1. What is the difference between a CSS reset and Normalize.css?
2. Why should you separate primitive colour tokens from semantic tokens?
3. How does BEM naming help with CSS specificity?
4. How do you implement dark mode using CSS custom properties?
5. How does Spring Boot's content-based resource versioning improve performance?
