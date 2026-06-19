# Module 04 — Document Structure: Doctype, `<html>`, `<head>`, `<body>`

## 4.1 The Complete HTML5 Boilerplate

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="Brief description of the page (150–160 chars)">
  <meta name="author" content="Your Name / Company">

  <!-- Preconnect to external origins you'll load from -->
  <link rel="preconnect" href="https://fonts.googleapis.com">

  <!-- Favicon -->
  <link rel="icon" type="image/svg+xml" href="/images/favicon.svg">
  <link rel="icon" type="image/png" href="/images/favicon.png">

  <!-- Stylesheets (load before body to avoid FOUC) -->
  <link rel="stylesheet" href="/css/normalize.css">
  <link rel="stylesheet" href="/css/main.css">

  <!-- Page title -->
  <title>Page Title — Site Name</title>
</head>
<body>

  <!-- Page content here -->

  <!-- Scripts at end of body (or use defer in head) -->
  <script src="/js/app.js" defer></script>
</body>
</html>
```

Every element in this boilerplate has a reason. Let's examine each one.

---

## 4.2 The Doctype

```html
<!DOCTYPE html>
```

The `DOCTYPE` (Document Type Declaration) tells the browser which version of HTML to expect. Without it, browsers enter **Quirks Mode** — a backwards-compatibility mode that emulates bugs from 1990s browsers. You almost never want Quirks Mode.

### Quirks Mode vs. Standards Mode

| | Quirks Mode | Standards Mode |
|--|-------------|----------------|
| Triggered by | Missing or incorrect doctype | `<!DOCTYPE html>` present |
| Box model | Old IE broken box model | W3C standard box model |
| CSS behaviour | Unpredictable | Spec-compliant |
| Should you use it? | Never intentionally | Always |

### Historical Context

Old HTML doctypes were long and hard to remember:

```html
<!-- HTML 4.01 Strict — who can remember this? -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
  "http://www.w3.org/TR/html4/strict.dtd">

<!-- HTML5 — just this: -->
<!DOCTYPE html>
```

The HTML5 doctype was deliberately designed to be the minimum string that triggers standards mode in all browsers.

> **Thymeleaf Note:** Thymeleaf HTML templates use `<!DOCTYPE html>` at the top. Thymeleaf processes the file as a valid HTML document, so the doctype must be present for Thymeleaf's HTML parser to work correctly.

---

## 4.3 The `<html>` Element

```html
<html lang="en">
```

The root element wrapping all content. The `lang` attribute is **required for accessibility**:

- Screen readers use `lang` to select the correct text-to-speech voice
- Spell checkers use it for dictionaries
- Search engines use it for language-targeted results
- CSS can use `:lang()` pseudo-class for language-specific styling

### Language Codes

```html
<html lang="en">          <!-- English -->
<html lang="en-US">       <!-- English (United States) -->
<html lang="en-GB">       <!-- English (Great Britain) -->
<html lang="hi">          <!-- Hindi -->
<html lang="ta">          <!-- Tamil -->
<html lang="ar" dir="rtl"> <!-- Arabic, right-to-left -->
<html lang="zh-CN">       <!-- Simplified Chinese -->
```

For multi-language enterprise applications (common in Indian software companies serving global clients), set `lang` dynamically:

```html
<!-- Thymeleaf -->
<html th:lang="${#locale.language}" xmlns:th="http://www.thymeleaf.org">
```

---

## 4.4 The `<head>` Element

The `<head>` contains **metadata** — information *about* the document rather than content to display. Nothing in `<head>` is directly rendered (except `<title>` which appears in the browser tab).

### 4.4.1 Character Set

```html
<meta charset="UTF-8">
```

**Must be the first element in `<head>`,** within the first 1024 bytes of the document. The browser must know the encoding before it can parse any other content.

### 4.4.2 Viewport Meta Tag

```html
<meta name="viewport" content="width=device-width, initial-scale=1.0">
```

This is **essential for responsive design**. Without it, mobile browsers zoom out to show a desktop layout at a small size.

| `content` value | Effect |
|----------------|--------|
| `width=device-width` | Set viewport width to device's screen width |
| `initial-scale=1.0` | No zoom on load |
| `maximum-scale=1.0` | Prevent user zoom (accessibility issue — avoid) |

> **Accessibility warning:** Never use `user-scalable=no` or `maximum-scale=1`. Visually impaired users depend on zoom. Disabling it fails WCAG 1.4.4 (Resize text).

### 4.4.3 Meta Description

```html
<meta name="description" content="A comprehensive dashboard for managing enterprise accounts.">
```

- Shown in Google search results under the page title
- Keep between 150–160 characters
- Unique per page
- Not directly a ranking factor but affects click-through rate

### 4.4.4 The `<title>` Element

```html
<title>Invoice List — Acme Corp Admin</title>
```

The page title appears in:
- Browser tab
- Browser history
- Bookmarks
- Search engine results (up to ~60 chars shown)
- Screen reader announcements when the page loads

**Best practice for enterprise apps:** `Page Name — Application Name`

```html
<!-- Thymeleaf: per-page title with fallback -->
<title th:text="${pageTitle != null} ? ${pageTitle + ' — Acme Corp'} : 'Acme Corp'">
  Acme Corp
</title>
```

### 4.4.5 Stylesheets

```html
<link rel="stylesheet" href="/css/normalize.css">
<link rel="stylesheet" href="/css/main.css">
```

Stylesheets are **render-blocking** — the browser pauses DOM rendering until they are downloaded and parsed. This is intentional: you don't want users to see unstyled content flash. Put stylesheets in `<head>`.

**For Thymeleaf:**
```html
<link rel="stylesheet" th:href="@{/css/main.css}">
```
`@{}` is Thymeleaf's URL expression — it prepends the application context path automatically, essential when your app is deployed at a path like `/myapp/`.

### 4.4.6 Resource Hints

Speed up loading by telling the browser about resources it will need:

```html
<!-- Resolve DNS early for a third-party domain -->
<link rel="dns-prefetch" href="https://api.external-service.com">

<!-- Establish connection early (includes DNS + TCP + TLS) -->
<link rel="preconnect" href="https://fonts.googleapis.com">

<!-- Fetch and cache a resource you'll need soon -->
<link rel="prefetch" href="/css/print.css" as="style">

<!-- Fetch and parse a resource this page needs urgently -->
<link rel="preload" href="/fonts/inter.woff2" as="font" type="font/woff2" crossorigin>
```

### 4.4.7 Favicon

```html
<link rel="icon" type="image/svg+xml" href="/images/favicon.svg">
<link rel="icon" type="image/png" sizes="32x32" href="/images/favicon-32.png">
<link rel="apple-touch-icon" href="/images/apple-touch-icon.png">
```

Modern approach: SVG favicon (scalable, single file) with PNG fallback.

### 4.4.8 Open Graph & Social Meta Tags

For enterprise applications with public-facing pages that may be shared on social media:

```html
<!-- Open Graph (Facebook, LinkedIn, WhatsApp) -->
<meta property="og:title" content="Q3 Report — Acme Corp">
<meta property="og:description" content="Quarterly financial summary for stakeholders.">
<meta property="og:image" content="https://example.com/images/og-report.png">
<meta property="og:url" content="https://example.com/reports/q3">
<meta property="og:type" content="article">

<!-- Twitter Card -->
<meta name="twitter:card" content="summary_large_image">
<meta name="twitter:title" content="Q3 Report — Acme Corp">
```

---

## 4.5 The `<body>` Element

All visible page content goes inside `<body>`. There is exactly one `<body>` per document.

```html
<body>
  <header>...</header>
  <nav>...</nav>
  <main>
    <article>...</article>
    <aside>...</aside>
  </main>
  <footer>...</footer>
</body>
```

### Script Loading Strategies

```html
<!-- ❌ Blocks parsing — parser must stop, fetch, execute, then resume -->
<head>
  <script src="/js/app.js"></script>
</head>

<!-- ✅ defer: downloads in parallel, executes after HTML parsed, in order -->
<head>
  <script src="/js/app.js" defer></script>
</head>

<!-- ✅ async: downloads in parallel, executes immediately when ready (order not guaranteed) -->
<head>
  <script src="/js/analytics.js" async></script>
</head>

<!-- ✅ Traditional: put scripts at end of body -->
<body>
  <!-- content -->
  <script src="/js/app.js"></script>
</body>
```

**Recommendation for enterprise Spring Boot apps:**
- Use `defer` for application scripts in `<head>`
- Use `async` for analytics and third-party scripts
- `defer` preserves execution order, which matters when scripts depend on each other

---

## 4.6 Complete Thymeleaf Layout Example

A real-world Thymeleaf base layout:

```html
<!DOCTYPE html>
<html th:lang="${#locale.language}" xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="_csrf" th:content="${_csrf.token}">
  <meta name="_csrf_header" th:content="${_csrf.headerName}">

  <title layout:title-pattern="$CONTENT_TITLE - Acme Corp">Acme Corp</title>

  <link rel="icon" type="image/svg+xml" th:href="@{/images/favicon.svg}">
  <link rel="stylesheet" th:href="@{/css/main.css}">

  <!-- Page-specific head content injected here -->
  <th:block layout:fragment="extra-head"></th:block>
</head>
<body>
  <header th:replace="~{layout/header :: header}"></header>
  <nav th:replace="~{layout/nav :: nav}"></nav>

  <main id="main-content" role="main">
    <!-- Page content injected here -->
    <div layout:fragment="content">
      <p>Default content</p>
    </div>
  </main>

  <footer th:replace="~{layout/footer :: footer}"></footer>

  <script th:src="@{/js/app.js}" defer></script>
  <th:block layout:fragment="extra-scripts"></th:block>
</body>
</html>
```

---

## Key Takeaways

- `<!DOCTYPE html>` triggers Standards Mode — always include it as the very first line.
- `<meta charset="UTF-8">` must be the first element in `<head>`.
- The `lang` attribute on `<html>` is required for accessibility and i18n.
- Viewport meta tag is essential for responsive design.
- Use `defer` for scripts in `<head>`; never block rendering unnecessarily.
- Thymeleaf's `th:href="@{/path}"` handles context paths automatically.

---

## Self-Check Questions

1. What is Quirks Mode and how do you prevent it?
2. Why must `<meta charset="UTF-8">` be within the first 1024 bytes?
3. What is the difference between `defer` and `async` on a `<script>` tag?
4. Why should you never set `user-scalable=no` on the viewport meta tag?
5. How does Thymeleaf's `@{}` URL expression help in enterprise deployments?
