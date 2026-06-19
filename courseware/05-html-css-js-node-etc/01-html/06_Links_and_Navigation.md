# Module 06 — Hyperlinks & Navigation

## 6.1 The Anchor Element `<a>`

The anchor element creates hyperlinks — the fundamental unit of the web:

```html
<a href="https://spring.io">Spring Framework</a>
```

### Anatomy of a Hyperlink

```html
<a href="https://example.com/page"
   target="_blank"
   rel="noopener noreferrer"
   title="Opens example.com in a new tab">
  Link Text
</a>
```

| Attribute | Purpose |
|-----------|---------|
| `href` | Destination URL — the **required** attribute |
| `target` | Where to open (`_self`, `_blank`, `_parent`, `_top`) |
| `rel` | Relationship between pages |
| `title` | Tooltip text |
| `download` | Trigger a file download instead of navigation |
| `hreflang` | Language of the linked page |
| `type` | MIME type of the linked resource |

---

## 6.2 Types of URLs

### Absolute URLs

Complete URL including protocol and domain:

```html
<a href="https://www.example.com/products/item?id=42">View Item</a>
```

Use for: external sites, CDN resources, canonical links.

### Root-Relative URLs

Relative to the domain root — preferred in enterprise applications:

```html
<a href="/dashboard">Dashboard</a>
<a href="/api/users">Users API</a>
<img src="/images/logo.svg" alt="Logo">
```

In Spring Boot with Thymeleaf, use `@{}` which automatically prepends the context path:

```html
<!-- If app deployed at /myapp/, this becomes href="/myapp/dashboard" -->
<a th:href="@{/dashboard}">Dashboard</a>

<!-- With path variables and query params -->
<a th:href="@{/users/{id}(id=${user.id})}">View Profile</a>
<a th:href="@{/search(q=${query},page=${currentPage})}">Search</a>
```

### Document-Relative URLs

Relative to the current file's location — avoid in server-rendered apps (confusing, fragile):

```html
<!-- Relative to the current file — fragile in web apps -->
<a href="../about.html">About</a>
<a href="products/item.html">Item</a>
```

### Same-Page Anchors

Navigate to an element on the same page using an `id`:

```html
<!-- The link -->
<a href="#contact">Jump to Contact Section</a>

<!-- The target element -->
<section id="contact">
  <h2>Contact Us</h2>
  ...
</section>
```

Use for: "back to top" links, table of contents, skip navigation links.

---

## 6.3 The `target` Attribute

```html
<!-- Open in same tab (default) -->
<a href="/dashboard" target="_self">Dashboard</a>

<!-- Open in a new tab/window -->
<a href="https://external-docs.example.com" target="_blank">Documentation</a>

<!-- Open in parent frame -->
<a href="/page" target="_parent">Up</a>

<!-- Open in full window, replacing any frames -->
<a href="/logout" target="_top">Logout</a>
```

### Security: Always Use `rel="noopener noreferrer"` with `target="_blank"`

Without `rel="noopener"`, the opened page can access your page via `window.opener` — a security vulnerability called **reverse tabnapping**:

```html
<!-- ❌ Vulnerable -->
<a href="https://example.com" target="_blank">External Link</a>

<!-- ✅ Secure -->
<a href="https://example.com" target="_blank" rel="noopener noreferrer">External Link</a>
```

- `noopener`: Prevents the new page from accessing `window.opener`
- `noreferrer`: Also prevents passing the `Referer` header (for privacy)

> **Enterprise Note:** This is commonly flagged in web security scans. Automate it with linting rules.

---

## 6.4 Special `href` Values

### Email Links

```html
<a href="mailto:support@company.com">Email Support</a>

<!-- With subject and body (URL-encoded) -->
<a href="mailto:hr@company.com?subject=Leave%20Request&body=Hi%20Team%2C">
  Apply for Leave
</a>
```

### Phone Links

```html
<a href="tel:+918012345678">+91 80 1234 5678</a>
```

### Download Links

```html
<!-- Force download with a custom filename -->
<a href="/reports/q3-2024.pdf" download="Q3-Report-2024.pdf">
  Download Q3 Report (PDF)
</a>

<!-- Thymeleaf: generate download link for a dynamic resource -->
<a th:href="@{/export/invoices/{id}(id=${invoice.id})}" 
   th:download="${'Invoice-' + invoice.number + '.pdf'}">
  Download Invoice
</a>
```

### JavaScript Pseudo-Protocol — Avoid

```html
<!-- ❌ Avoid: href="javascript:void(0)" -->
<a href="javascript:void(0)" onclick="doSomething()">Click</a>

<!-- ✅ Better: use a button for actions, not navigation -->
<button type="button" onclick="doSomething()">Click</button>

<!-- ✅ Or: meaningful href with JS enhancement -->
<a href="/action" id="myLink">Click</a>
```

---

## 6.5 Link Accessibility

### Descriptive Link Text

Screen readers often navigate by listing all links on a page. "Click here" or "Read more" out of context is meaningless:

```html
<!-- ❌ Bad: meaningless out of context -->
<a href="/reports/q3">Click here</a>
<a href="/products/42">Read more</a>

<!-- ✅ Good: descriptive -->
<a href="/reports/q3">Download Q3 2024 Financial Report</a>
<a href="/products/42">View details for Widget Pro</a>

<!-- ✅ When context makes short text acceptable, use aria-label -->
<article>
  <h2>Widget Pro</h2>
  <p>A professional widget for enterprise use.</p>
  <a href="/products/42" aria-label="Read more about Widget Pro">Read more</a>
</article>
```

### Skip Navigation Links

For keyboard and screen reader users, provide a link to skip repetitive navigation:

```html
<!-- First element in <body> -->
<a href="#main-content" class="skip-link">Skip to main content</a>

<!-- Visually hidden until focused -->
<style>
.skip-link {
  position: absolute;
  left: -9999px;
  top: 0;
}
.skip-link:focus {
  left: 0;
  /* visible styles */
}
</style>
```

---

## 6.6 Navigation with `<nav>`

The `<nav>` element marks a section of major navigation links:

```html
<nav aria-label="Main navigation">
  <ul>
    <li><a href="/" aria-current="page">Home</a></li>
    <li><a href="/products">Products</a></li>
    <li><a href="/services">Services</a></li>
    <li><a href="/contact">Contact</a></li>
  </ul>
</nav>
```

- Use `aria-label` to distinguish multiple `<nav>` elements (e.g., main nav vs. footer nav)
- Use `aria-current="page"` on the link for the current page (screen readers announce "current")
- Navigation links should be in a list — it tells screen readers how many items there are

### In Thymeleaf — Active Link Highlighting

```html
<nav aria-label="Main navigation">
  <ul>
    <li>
      <a th:href="@{/}" 
         th:classappend="${currentPage == 'home'} ? 'active' : ''">Home</a>
    </li>
    <li>
      <a th:href="@{/dashboard}"
         th:attr="aria-current=${currentPage == 'dashboard'} ? 'page' : null"
         th:classappend="${currentPage == 'dashboard'} ? 'active' : ''">Dashboard</a>
    </li>
  </ul>
</nav>
```

---

## 6.7 Breadcrumbs

Standard navigation pattern for enterprise applications with deep hierarchies:

```html
<nav aria-label="Breadcrumb">
  <ol>
    <li><a href="/">Home</a></li>
    <li><a href="/admin">Administration</a></li>
    <li><a href="/admin/users">Users</a></li>
    <li><span aria-current="page">Edit User: John Doe</span></li>
  </ol>
</nav>
```

Use `<ol>` (ordered list) because the order of breadcrumbs is meaningful.

### Structured Data for SEO

```html
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "BreadcrumbList",
  "itemListElement": [
    { "@type": "ListItem", "position": 1, "name": "Home", "item": "https://example.com/" },
    { "@type": "ListItem", "position": 2, "name": "Admin", "item": "https://example.com/admin" },
    { "@type": "ListItem", "position": 3, "name": "Users", "item": "https://example.com/admin/users" }
  ]
}
</script>
```

---

## Key Takeaways

- Always use `rel="noopener noreferrer"` with `target="_blank"`.
- In Thymeleaf, use `@{}` for all URLs — it handles context paths.
- Use descriptive link text; add `aria-label` when context alone isn't enough.
- Include a skip navigation link as the first focusable element.
- Use `aria-current="page"` on the active navigation link.
- Use `<nav>` + `<ul>` for navigation; use `<ol>` for ordered sequences like breadcrumbs.

---

## Self-Check Questions

1. What is reverse tabnapping and how do you prevent it?
2. Why should you avoid `href="javascript:void(0)"`?
3. How do you generate URLs with path variables in Thymeleaf?
4. What is `aria-current="page"` and why is it important?
5. Why use `<ol>` instead of `<ul>` for breadcrumbs?
