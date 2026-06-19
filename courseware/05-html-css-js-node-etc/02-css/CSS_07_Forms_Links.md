# CSS Module 07 — Styling Forms & Links

## 7.1 Why Form Styling Is Tricky

Form controls have default styles that vary significantly across browsers and operating systems. Some controls (like `<select>`, `<input type="range">`, `<input type="checkbox">`) are notoriously hard to style because they use OS-native rendering.

Strategy:
1. **Reset** browser defaults
2. **Override** properties that CSS can reliably control
3. **Use appearance: none** to take full control of custom elements
4. **Accept** some OS-level styling for complex controls (date pickers, etc.)

---

## 7.2 Base Form Resets

```css
/* Apply to all form elements */
input,
textarea,
select,
button {
  font-family: inherit;    /* inherit from body — browsers don't do this by default */
  font-size: 1rem;         /* reset browser default */
  line-height: 1.5;
  color: inherit;
  margin: 0;
  box-sizing: border-box;
}

/* Remove iOS styling */
input,
textarea,
select {
  -webkit-appearance: none;
  appearance: none;
}
```

---

## 7.3 Styling Text Inputs

```css
/* Base input style */
.input,
input[type="text"],
input[type="email"],
input[type="password"],
input[type="tel"],
input[type="url"],
input[type="search"],
input[type="number"],
textarea,
select {
  display: block;
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-md);
  background-color: white;
  color: var(--color-text);
  font-size: 1rem;
  line-height: 1.5;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

/* Hover state */
.input:hover,
input[type="text"]:hover {
  border-color: var(--color-border-hover, #adb5bd);
}

/* Focus state — CRITICAL: always visible */
.input:focus,
input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.2);
}

/* Disabled state */
.input:disabled,
input:disabled {
  background-color: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
  opacity: 1;    /* override browser's default opacity reduction */
}

/* Read-only */
input:read-only {
  background-color: #f8f9fa;
  cursor: default;
}

/* Placeholder */
input::placeholder,
textarea::placeholder {
  color: #adb5bd;
  opacity: 1;        /* Firefox reduces opacity by default */
}

/* Validation states */
input:user-invalid,                 /* after user interaction */
input.is-invalid {                  /* added by JS/server validation */
  border-color: var(--color-error);
  box-shadow: 0 0 0 3px rgba(220, 53, 69, 0.2);
}

input:user-valid,
input.is-valid {
  border-color: var(--color-success);
}
```

---

## 7.4 Styling Textareas

```css
textarea {
  display: block;
  width: 100%;
  min-height: 120px;
  resize: vertical;    /* allow vertical resize only (default is both) */
  padding: 0.5rem 0.75rem;
  line-height: 1.6;
}

/* Prevent horizontal resize that breaks layout */
textarea { resize: vertical; }

/* Fixed size (no resize) */
textarea.no-resize { resize: none; }
```

---

## 7.5 Styling Select Dropdowns

```css
select {
  display: block;
  width: 100%;
  padding: 0.5rem 2.5rem 0.5rem 0.75rem; /* extra right padding for custom arrow */
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-md);
  background-color: white;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 16px;
  cursor: pointer;
  -webkit-appearance: none;
  appearance: none;
}

select:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.2);
  outline: none;
}
```

---

## 7.6 Custom Checkboxes and Radio Buttons

The hardest controls to style. Two approaches:

### Approach 1: CSS-Only Custom Styling

```css
/* Hide native control, style the label */
.checkbox-wrapper input[type="checkbox"],
.radio-wrapper input[type="radio"] {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  border: 0;
}

/* Style the label as the visual control */
.checkbox-wrapper label,
.radio-wrapper label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  user-select: none;
}

/* Custom checkbox box: the ::before pseudo-element */
.checkbox-wrapper label::before {
  content: "";
  display: inline-block;
  width: 1.125rem;
  height: 1.125rem;
  border: 2px solid var(--color-border);
  border-radius: 3px;
  background: white;
  flex-shrink: 0;
  transition: all 0.15s ease;
}

/* Checked state */
.checkbox-wrapper input:checked + label::before {
  background: var(--color-primary);
  border-color: var(--color-primary);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16'%3E%3Cpath d='M13 4L6 11 3 8' stroke='white' stroke-width='2' fill='none' stroke-linecap='round'/%3E%3C/svg%3E");
}

/* Focus ring on the visual element */
.checkbox-wrapper input:focus-visible + label::before {
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.3);
}

/* Radio: circle shape */
.radio-wrapper label::before {
  border-radius: 50%;
}
.radio-wrapper input:checked + label::before {
  background: white;
  border-color: var(--color-primary);
  box-shadow: inset 0 0 0 4px var(--color-primary);
}
```

### Approach 2: `accent-color` (Simple, Modern)

```css
/* accent-color: simplest approach — browser renders the control in your brand colour */
input[type="checkbox"],
input[type="radio"] {
  accent-color: var(--color-primary);
  width: 1rem;
  height: 1rem;
}
```

`accent-color` is supported in all modern browsers. It doesn't give full design control but is accessible and cross-platform.

---

## 7.7 Styling Buttons

```css
/* Button reset */
button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-family: inherit;
  font-size: 1rem;
  font-weight: 500;
  line-height: 1;
  cursor: pointer;
  border: 2px solid transparent;
  border-radius: var(--radius-md);
  padding: 0.5rem 1.25rem;
  text-decoration: none;
  transition: background-color 0.15s, border-color 0.15s, box-shadow 0.15s;
  -webkit-appearance: none;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.65;
}

/* Primary button */
.btn-primary {
  background-color: var(--color-primary);
  color: white;
  border-color: var(--color-primary);
}
.btn-primary:hover:not(:disabled) {
  background-color: var(--color-primary-dark);
  border-color: var(--color-primary-dark);
}
.btn-primary:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.4);
}

/* Secondary (outline) button */
.btn-secondary {
  background-color: transparent;
  color: var(--color-primary);
  border-color: var(--color-primary);
}
.btn-secondary:hover:not(:disabled) {
  background-color: var(--color-primary);
  color: white;
}

/* Danger button */
.btn-danger {
  background-color: var(--color-error);
  color: white;
  border-color: var(--color-error);
}

/* Ghost button */
.btn-ghost {
  background-color: transparent;
  color: var(--color-text);
  border-color: transparent;
}
.btn-ghost:hover:not(:disabled) {
  background-color: rgba(0,0,0,0.05);
}

/* Button sizes */
.btn-sm  { font-size: 0.875rem; padding: 0.25rem 0.75rem; }
.btn-lg  { font-size: 1.125rem; padding: 0.75rem 1.75rem; }

/* Full-width button */
.btn-block { display: flex; width: 100%; }
```

---

## 7.8 Complete Form Layout

```css
/* Form layout */
.form { max-width: 480px; }

/* Field group: label + input + error */
.field {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-bottom: 1.25rem;
}

.field label {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-text);
}

.field .hint {
  font-size: 0.8rem;
  color: var(--color-text-muted);
}

.field .error-message {
  font-size: 0.8rem;
  color: var(--color-error);
  display: none;
}

.field.has-error .error-message { display: block; }
.field.has-error input { border-color: var(--color-error); }

/* Form actions */
.form-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
  margin-top: 1.5rem;
}

/* Required indicator */
.field.required label::after {
  content: " *";
  color: var(--color-error);
}
```

---

## 7.9 Styling Links

```css
/* Ensure all link states are styled (LVHA order) */
a:link    { color: var(--color-link, #0066cc); }
a:visited { color: var(--color-link-visited, #5500cc); }
a:hover   { color: var(--color-link-hover, #0052a3); }
a:active  { color: var(--color-link-active, #003d7a); }

/* Base link style */
a {
  color: var(--color-primary);
  text-decoration: underline;
  text-decoration-color: transparent;  /* hidden by default */
  text-underline-offset: 3px;
  transition: text-decoration-color 0.15s;
}
a:hover {
  text-decoration-color: currentColor; /* visible on hover */
}

/* Focus */
a:focus-visible {
  outline: 3px solid var(--color-primary);
  outline-offset: 2px;
  border-radius: 2px;
}

/* Navigation links: no underline */
.nav a {
  text-decoration: none;
  font-weight: 500;
  color: var(--color-nav-text);
}
.nav a:hover,
.nav a[aria-current="page"] {
  color: var(--color-primary);
}
.nav a[aria-current="page"] {
  font-weight: 600;
}

/* Footer links */
.footer a {
  color: var(--color-text-muted);
  text-decoration: none;
}
.footer a:hover { text-decoration: underline; }

/* Skip link (accessible navigation) */
.skip-link {
  position: absolute;
  left: -9999px;
  top: 0;
  padding: 0.5rem 1rem;
  background: var(--color-primary);
  color: white;
  text-decoration: none;
  border-radius: 0 0 var(--radius-md) 0;
  z-index: 999;
}
.skip-link:focus {
  left: 0;
}
```

---

## Key Takeaways

- Reset `font-family: inherit` on all form elements — browsers ignore this by default
- Always style `:focus` or `:focus-visible` — removing focus without replacing it fails WCAG
- Use `appearance: none` + custom background SVG for styled `<select>` dropdowns
- `accent-color` is the simplest way to brand checkboxes and radios
- Style links in LVHA order: `:link`, `:visited`, `:hover`, `:active`
- Never remove link underlines in body text — they are a key accessibility cue

---

## Self-Check Questions

1. Why do you need to set `font-family: inherit` on form elements?
2. What is `appearance: none` used for and what does it enable?
3. What is `accent-color` and when should you use it?
4. Why must links be styled in the order `:link`, `:visited`, `:hover`, `:active`?
5. How do you make a skip link that is hidden until keyboard-focused?
