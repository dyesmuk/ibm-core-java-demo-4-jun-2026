# CSS Module 04 — The Box Model, Borders & Backgrounds

## 4.1 The CSS Box Model

Every HTML element is rendered as a rectangular box. The **box model** defines how that box is sized and spaced.

```
┌──────────────────────────────────────────┐
│                  margin                  │
│  ┌────────────────────────────────────┐  │
│  │              border                │  │
│  │  ┌──────────────────────────────┐  │  │
│  │  │           padding            │  │  │
│  │  │  ┌────────────────────────┐  │  │  │
│  │  │  │        content         │  │  │  │
│  │  │  │    (width × height)    │  │  │  │
│  │  │  └────────────────────────┘  │  │  │
│  │  └──────────────────────────────┘  │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
```

### The Two Box Sizing Models

```css
/* Default (content-box): width = content only */
/* Total element width = width + padding + border */
.default {
  box-sizing: content-box;
  width: 300px;
  padding: 20px;
  border: 2px solid;
  /* Total: 300 + 40 + 4 = 344px */
}

/* Border-box: width = content + padding + border */
/* Total element width = exactly what you specify */
.intuitive {
  box-sizing: border-box;
  width: 300px;
  padding: 20px;
  border: 2px solid;
  /* Total: 300px (padding and border eat into the 300px) */
}
```

### Always Use `border-box`

The universal reset:

```css
/* Apply at top of every stylesheet */
*, *::before, *::after {
  box-sizing: border-box;
}
```

`border-box` is far more intuitive for layout — a 300px column stays 300px regardless of padding. This is now the industry standard.

---

## 4.2 Width and Height

```css
.box {
  width: 300px;         /* fixed width */
  height: 200px;        /* fixed height */
  
  max-width: 1200px;    /* never wider than this */
  min-width: 320px;     /* never narrower than this */
  
  max-height: 400px;    /* cap height — use with overflow */
  min-height: 100vh;    /* at least full viewport height */
}
```

### Intrinsic Sizing Keywords

```css
.item {
  width: fit-content;   /* shrink to content, never wider than available space */
  width: max-content;   /* content's natural width, may overflow */
  width: min-content;   /* smallest possible (e.g., longest word width) */
}
```

---

## 4.3 Margin

Margin creates space **outside** the element's border:

```css
/* All four sides */
.box { margin: 20px; }

/* Vertical | Horizontal */
.box { margin: 20px 40px; }

/* Top | Horizontal | Bottom */
.box { margin: 10px 20px 30px; }

/* Top | Right | Bottom | Left (clockwise from top) */
.box { margin: 10px 20px 30px 40px; }

/* Individual sides */
.box {
  margin-top: 20px;
  margin-right: 10px;
  margin-bottom: 20px;
  margin-left: 10px;
}

/* Logical properties */
.box {
  margin-block: 20px;    /* top and bottom */
  margin-inline: auto;   /* left and right — auto centres block elements */
}
```

### Centering with `margin: auto`

```css
/* Centre a block element horizontally */
.container {
  max-width: 1200px;
  width: 100%;
  margin-inline: auto;  /* or: margin: 0 auto; */
}
```

### Margin Collapse

Adjacent vertical margins **collapse** — they don't add up:

```css
p { margin-bottom: 20px; }
h2 { margin-top: 30px; }

/* Between a <p> followed by <h2>: 
   margin is NOT 20px + 30px = 50px
   margin IS max(20px, 30px) = 30px */
```

**Margin collapse occurs when:**
- Two block elements are vertical siblings
- Parent and first/last child (if no border/padding between them)
- Empty blocks

**Margin collapse does NOT occur in:**
- Flexbox containers
- Grid containers
- Floated elements
- Inline-block elements
- Elements with `overflow` other than `visible`

---

## 4.4 Padding

Padding creates space **inside** the element's border (between border and content):

```css
/* Same shorthand as margin */
.btn { padding: 0.5rem 1.25rem; }
.card { padding: 1.5rem; }

.hero {
  padding-block: 5rem;
  padding-inline: 2rem;
}
```

**Key differences from margin:**
- Padding is part of the element's background area — background colour fills padding
- Padding never collapses
- Cannot be negative (unlike margin)

---

## 4.5 Borders

```css
/* Shorthand: width style color */
.card    { border: 1px solid #dee2e6; }
.error   { border: 2px solid var(--color-error); }
.focus   { border: 3px solid var(--color-primary); }

/* Individual sides */
.divider { border-bottom: 1px solid #eee; }
.sidebar { border-right: 4px solid var(--color-primary); }

/* Individual properties */
.custom-border {
  border-width: 1px;
  border-style: solid;       /* solid, dashed, dotted, double, groove, ridge, none */
  border-color: #dee2e6;
}

/* Individual sides with individual properties */
.fancy {
  border-top-width: 3px;
  border-top-style: solid;
  border-top-color: var(--color-primary);
  border-bottom: 1px solid #eee;
  border-left: none;
  border-right: none;
}

/* Border radius */
.btn   { border-radius: 4px; }
.card  { border-radius: 8px; }
.avatar { border-radius: 50%; }   /* circle */
.badge { border-radius: 9999px; } /* pill shape */

/* Individual corners */
.custom-radius {
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
}

/* Shorthand: top-left top-right bottom-right bottom-left */
.custom-radius { border-radius: 8px 8px 0 0; }
```

### `outline` vs `border`

```css
/* outline: doesn't affect layout (no box model space), 
   usually used for focus indicators */
:focus-visible {
  outline: 3px solid var(--color-primary);
  outline-offset: 2px;  /* space between element and outline */
  border-radius: 4px;   /* outline can follow border-radius in modern browsers */
}

/* Remove default outline only when replacing with visible alternative */
button:focus { outline: none; }  /* ❌ Never do this — accessibility failure */
button:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px var(--color-primary); /* ✅ Replace with visible ring */
}
```

---

## 4.6 Box Shadow

```css
/* Single shadow: offset-x offset-y blur-radius spread-radius color */
.card { box-shadow: 0 2px 4px rgba(0,0,0,0.12); }

/* Multiple shadows */
.card-elevated {
  box-shadow:
    0 1px 3px rgba(0,0,0,0.12),
    0 4px 12px rgba(0,0,0,0.08);
}

/* Inset shadow (inside the element) */
.pressed { box-shadow: inset 0 2px 4px rgba(0,0,0,0.2); }

/* Focus ring using box-shadow (useful when border would shift layout) */
button:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px white, 0 0 0 5px var(--color-primary);
}
```

---

## 4.7 Background Properties

### Background Colour

```css
.hero    { background-color: var(--color-primary); }
.card    { background-color: white; }
.warning { background-color: #fff3cd; }

/* Transparent (default) */
.overlay { background-color: rgba(0, 0, 0, 0.5); }
.glass   { background-color: rgba(255, 255, 255, 0.1); }
```

### Background Images

```css
/* Basic image */
.hero {
  background-image: url('/images/hero-bg.jpg');
  background-size: cover;      /* fill container, crop if needed */
  background-position: center;
  background-repeat: no-repeat;
  min-height: 60vh;
}

/* With colour fallback */
.hero {
  background-color: var(--color-primary); /* shown while image loads */
  background-image: url('/images/hero-bg.jpg');
  background-size: cover;
  background-position: center center;
}

/* Pattern/texture */
.pattern-bg {
  background-image: url('/images/pattern.svg');
  background-size: 40px 40px; /* tile size */
  background-repeat: repeat;
}

/* Shorthand: color image position/size repeat attachment */
.hero {
  background: var(--color-primary) url('/images/hero.jpg') center/cover no-repeat;
}
```

### Background Size

```css
/* cover: scales to fill, crops if needed (most common) */
.hero { background-size: cover; }

/* contain: scales to fit, may leave gaps */
.logo-bg { background-size: contain; }

/* Explicit size */
.pattern { background-size: 40px 40px; }
.pattern { background-size: 100% auto; }
```

### Multiple Backgrounds

```css
/* Layered backgrounds: first = on top */
.overlay-card {
  background:
    linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)),  /* dark overlay */
    url('/images/card-bg.jpg') center/cover;
}
```

### CSS Gradients

```css
/* Linear gradient */
.gradient-btn {
  background: linear-gradient(135deg, #0066cc, #00ccff);
}

/* Top to bottom (default) */
.gradient {
  background: linear-gradient(#0066cc, #004499);
}

/* Radial gradient */
.radial {
  background: radial-gradient(circle at center, #0066cc, #004499);
}

/* Conic gradient */
.pie-chart {
  background: conic-gradient(
    var(--color-primary) 0% 60%,
    var(--color-secondary) 60% 85%,
    var(--color-muted) 85% 100%
  );
  border-radius: 50%;
}
```

---

## 4.8 Overflow

```css
/* visible (default): content can overflow its box */
.tooltip { overflow: visible; }

/* hidden: clip overflow (also creates a new BFC) */
.card { overflow: hidden; }  /* hides overflowing content + clips border-radius */

/* scroll: always show scrollbars */
.code-block { overflow: scroll; }

/* auto: show scrollbars only when needed (most common) */
.table-wrapper { overflow: auto; }
.sidebar       { overflow-y: auto; }

/* Individual axes */
.horizontal-scroll { overflow-x: auto; overflow-y: hidden; }
```

---

## Key Takeaways

- Always use `box-sizing: border-box` globally — makes sizing intuitive
- Padding is inside the border; margin is outside; border is between them
- Margin collapses vertically between block siblings; padding never collapses
- Use `outline` + `outline-offset` for focus rings; never remove focus styling without replacing it
- CSS custom properties for spacing values create a consistent design system

---

## Self-Check Questions

1. What is the total rendered width of an element with `width: 200px`, `padding: 20px`, `border: 2px solid` in `content-box` model? In `border-box`?
2. When do vertical margins collapse? When do they not?
3. What is the difference between `outline` and `border`?
4. How do you create a dark overlay on a background image using CSS?
5. Why is `overflow: hidden` on a card element useful for border-radius clipping?
