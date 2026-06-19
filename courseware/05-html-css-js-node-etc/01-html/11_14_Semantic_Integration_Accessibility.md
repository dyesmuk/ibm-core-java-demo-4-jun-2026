# Module 11 — Semantic HTML5: Layout Elements

## 11.1 Why Semantics Matter

HTML5 introduced elements that describe their *purpose*, not just their appearance. Compare:

```html
<!-- Non-semantic: div soup — was the norm before HTML5 -->
<div id="header">
  <div class="nav">
    <div class="nav-item"><a href="/">Home</a></div>
  </div>
</div>
<div id="main">
  <div class="sidebar"></div>
  <div class="content"></div>
</div>
<div id="footer"></div>

<!-- Semantic HTML5: self-documenting, accessible, SEO-friendly -->
<header>
  <nav aria-label="Main">
    <ul><li><a href="/">Home</a></li></ul>
  </nav>
</header>
<main>
  <aside>Sidebar</aside>
  <article>Content</article>
</main>
<footer>Footer</footer>
```

**Benefits of semantic HTML:**
- Screen readers navigate by landmark regions (header, main, nav, footer)
- Search engines understand page structure
- Code is self-documenting
- CSS targets elements by meaning, not class names
- Easier to maintain

---

## 11.2 Page-Level Layout Elements

### `<header>`

The introductory content for a page or section:

```html
<!-- Page header -->
<header>
  <a href="/" class="logo">
    <img src="/images/logo.svg" alt="Acme Corp">
  </a>
  <nav aria-label="Main navigation">...</nav>
  <div class="header-actions">
    <button class="btn-search" aria-label="Open search">🔍</button>
    <a href="/account">My Account</a>
  </div>
</header>

<!-- Article header (inside an article element) -->
<article>
  <header>
    <h2>Microservices with Spring Boot</h2>
    <p>Published by <a href="/authors/jane">Jane Smith</a> on 
       <time datetime="2024-11-15">15 November 2024</time></p>
  </header>
  <p>Article content...</p>
</article>
```

### `<footer>`

Closing content for a page or section:

```html
<!-- Page footer -->
<footer>
  <div class="footer-grid">
    <section aria-labelledby="footer-about">
      <h2 id="footer-about">About Acme Corp</h2>
      <p>We build enterprise software solutions...</p>
    </section>
    <nav aria-label="Footer navigation">
      <h2>Quick Links</h2>
      <ul>
        <li><a href="/about">About</a></li>
        <li><a href="/careers">Careers</a></li>
        <li><a href="/contact">Contact</a></li>
      </ul>
    </nav>
    <address>
      <strong>Contact Us</strong><br>
      <a href="mailto:info@acme.com">info@acme.com</a>
    </address>
  </div>
  <p class="copyright">
    <small>© <time datetime="2024">2024</time> Acme Corp. All rights reserved.</small>
  </p>
</footer>
```

### `<main>`

The primary content of the page. **One per page.** Not for headers, navs, or footers:

```html
<body>
  <header>...</header>
  <nav>...</nav>
  <main id="main-content">  <!-- id for skip-to-content link -->
    <!-- All primary content here -->
    <h1>Page Title</h1>
    <article>...</article>
    <aside>...</aside>
  </main>
  <footer>...</footer>
</body>
```

### `<nav>`

A block of major navigation links:

```html
<!-- Main navigation -->
<nav aria-label="Main navigation">
  <ul>
    <li><a href="/">Home</a></li>
    <li><a href="/products" aria-current="page">Products</a></li>
    <li><a href="/about">About</a></li>
  </ul>
</nav>

<!-- Pagination navigation -->
<nav aria-label="Pagination">
  <a href="?page=1" rel="prev">← Previous</a>
  <span>Page 2 of 10</span>
  <a href="?page=3" rel="next">Next →</a>
</nav>
```

---

## 11.3 Content Sectioning Elements

### `<section>`

A thematic grouping of content with a heading:

```html
<main>
  <section aria-labelledby="features-heading">
    <h2 id="features-heading">Key Features</h2>
    <ul>
      <li>Auto-scaling</li>
      <li>99.9% uptime SLA</li>
      <li>ISO 27001 certified</li>
    </ul>
  </section>

  <section aria-labelledby="pricing-heading">
    <h2 id="pricing-heading">Pricing Plans</h2>
    <div class="pricing-grid">...</div>
  </section>
</main>
```

Use `<section>` when you would give the group a heading. If it has no natural heading, use `<div>`.

### `<article>`

Self-contained content that could stand alone and be syndicated:

```html
<article>
  <header>
    <h2>Spring Boot 3.2 Released</h2>
    <p>By <address class="author"><a href="/authors/john">John Dev</a></address> ·
       <time datetime="2024-11-15T10:30:00">15 Nov 2024 at 10:30</time></p>
  </header>
  <p>Spring Boot 3.2 introduces several new features...</p>
  <footer>
    <p>Tags: <a href="/tag/spring">Spring</a>, <a href="/tag/java">Java</a></p>
  </footer>
</article>
```

**Use `<article>` for:** blog posts, news articles, forum posts, product cards, social media posts, comments.

### `<aside>`

Content tangentially related to the main content:

```html
<main>
  <article>
    <h1>Introduction to Kafka</h1>
    <p>Apache Kafka is a distributed streaming platform...</p>

    <aside>
      <h2>Related Reading</h2>
      <ul>
        <li><a href="/kafka-consumers">Kafka Consumers</a></li>
        <li><a href="/kafka-producers">Kafka Producers</a></li>
      </ul>
    </aside>

    <p>Kafka was originally developed at LinkedIn...</p>
  </article>

  <!-- Page-level aside: sidebar -->
  <aside aria-label="Sidebar">
    <section>
      <h2>Popular Posts</h2>
      <!-- ... -->
    </section>
    <section>
      <h2>Newsletter</h2>
      <!-- signup form -->
    </section>
  </aside>
</main>
```

---

## 11.4 The `<time>` Element

Machine-readable timestamps:

```html
<!-- Date -->
<time datetime="2024-11-15">15 November 2024</time>

<!-- Time -->
<time datetime="14:30">2:30 PM</time>

<!-- Date and time (UTC) -->
<time datetime="2024-11-15T14:30:00Z">15 Nov 2024 at 2:30 PM UTC</time>

<!-- Date and time (with timezone offset) -->
<time datetime="2024-11-15T20:00:00+05:30">
  15 November 2024 at 8:00 PM IST
</time>

<!-- Duration -->
<time datetime="PT2H30M">2 hours and 30 minutes</time>

<!-- Year only -->
<time datetime="2024">2024</time>
```

Benefits:
- Screen readers can parse and format dates for the user's locale
- Search engines use `datetime` for event markup
- JavaScript can easily parse the ISO format

---

## 11.5 Other Useful HTML5 Elements

### `<figure>` and `<figcaption>`

```html
<figure>
  <img src="/charts/revenue-q3.png" 
       alt="Bar chart showing Q3 revenue at ₹12Cr, up 15% from Q2">
  <figcaption>
    Figure 1: Revenue growth in Q3 2024. Data from financial reporting system.
  </figcaption>
</figure>

<!-- Code listing with caption -->
<figure>
  <figcaption>Listing 3: Spring Boot health check endpoint</figcaption>
  <pre><code class="language-java">
@RestController
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity&lt;String&gt; health() {
        return ResponseEntity.ok("UP");
    }
}
  </code></pre>
</figure>
```

### `<details>` and `<summary>`

Accordion/expandable content without JavaScript:

```html
<details>
  <summary>Advanced Configuration Options</summary>
  <div class="details-body">
    <p>These options are for advanced users only.</p>
    <label>
      <input type="checkbox" name="debugMode"> Enable debug mode
    </label>
  </div>
</details>

<details open>  <!-- open attribute: expanded by default -->
  <summary>System Requirements</summary>
  <ul>
    <li>Java 17 or later</li>
    <li>Maven 3.8+</li>
    <li>PostgreSQL 14+</li>
  </ul>
</details>
```

### `<dialog>`

Native modal dialogs (no JavaScript library needed):

```html
<dialog id="confirmDialog" aria-labelledby="dialog-title">
  <h2 id="dialog-title">Confirm Deletion</h2>
  <p>Are you sure you want to delete this record? This cannot be undone.</p>
  <div class="dialog-actions">
    <button type="button" id="confirmDelete" class="btn btn-danger">Delete</button>
    <button type="button" id="cancelDelete" class="btn">Cancel</button>
  </div>
</dialog>
```

```javascript
const dialog = document.getElementById('confirmDialog');
document.getElementById('deleteBtn').addEventListener('click', () => dialog.showModal());
document.getElementById('cancelDelete').addEventListener('click', () => dialog.close());
```

### `<progress>` and `<meter>`

```html
<!-- Indeterminate progress (loading state) -->
<progress aria-label="Loading..."></progress>

<!-- Determinate progress -->
<label for="upload-progress">Upload progress:</label>
<progress id="upload-progress" value="65" max="100">65%</progress>

<!-- Meter: a scalar measurement within a known range -->
<label for="disk-usage">Disk usage:</label>
<meter id="disk-usage" value="72" min="0" max="100" low="60" high="85" optimum="30">
  72%
</meter>
```

---

## Key Takeaways

- Use semantic elements to describe *meaning*, not just visual structure.
- `<header>`, `<footer>`, `<nav>`, `<main>`, `<aside>` create landmark regions for accessibility.
- One `<main>` per page; `<article>` for self-contained content; `<section>` for thematic groups.
- Use `<time datetime="">` for all dates — enables machine parsing and search engine features.
- `<details>`/`<summary>` provides accordion without JavaScript.
- `<dialog>` provides native modal dialogs.

---

# Module 12 — IDs, Classes & `data-*` Attributes

## 12.1 `id` Attribute

An `id` must be **unique within the page**:

```html
<main id="main-content">
<section id="pricing-section">
<form id="registrationForm">
<h2 id="faq-heading">Frequently Asked Questions</h2>
```

**Uses of `id`:**
- CSS targeting (use sparingly — too specific, hard to reuse)
- JavaScript `document.getElementById()` — fast, O(1) lookup
- Anchor links (`href="#faq-heading"`)
- Form `<label for="">` association
- ARIA `aria-labelledby`, `aria-describedby`

### Naming Convention

```
kebab-case for HTML:  id="main-content"
camelCase for JS:     document.getElementById('mainContent')   // match your JS convention
```

## 12.2 `class` Attribute

Classes can be reused. One element can have multiple classes:

```html
<button class="btn btn-primary btn-lg">Create Account</button>
<span class="badge badge-success badge-pill">Active</span>
<div class="card card-featured card-hover-effect">...</div>
```

**BEM Naming Convention (Block Element Modifier)** — widely used in enterprise projects:

```html
<!-- Block: standalone component -->
<div class="card">
  <!-- Element: part of the block (double underscore) -->
  <div class="card__header">
    <h2 class="card__title">Product Name</h2>
  </div>
  <div class="card__body">
    <p class="card__description">Description text</p>
  </div>
  <!-- Modifier: variant of block or element (double hyphen) -->
  <button class="card__action card__action--primary">Buy Now</button>
</div>

<!-- Modifier on the block itself -->
<div class="card card--featured">...</div>
<div class="card card--disabled">...</div>
```

## 12.3 `data-*` Attributes

Pass arbitrary data from server to JavaScript:

```html
<tr class="employee-row"
    data-employee-id="1042"
    data-department="engineering"
    data-salary-band="senior"
    data-last-review="2024-03-15">
  <td>John Smith</td>
  <td>Engineering</td>
</tr>
```

```javascript
// Reading data attributes
const rows = document.querySelectorAll('.employee-row');
rows.forEach(row => {
  const { employeeId, department, salaryBand } = row.dataset;
  // dataset converts kebab-case to camelCase automatically
  // data-employee-id → dataset.employeeId
  // data-salary-band → dataset.salaryBand
});

// Writing data attributes
row.dataset.lastReview = '2024-11-15';
// Creates: data-last-review="2024-11-15"
```

---

# Module 13 — HTML & Java Integration

## 13.1 Thymeleaf Expression Summary

| Expression | Syntax | Use |
|-----------|--------|-----|
| Variable | `${variable}` | Access model attributes |
| Selection | `*{field}` | Access fields on `th:object` |
| Message | `#{key}` | Internationalisation messages |
| URL | `@{/path}` | Generate URLs with context path |
| Fragment | `~{template :: fragment}` | Include fragments |

## 13.2 Thymeleaf Conditionals

```html
<!-- th:if / th:unless -->
<div th:if="${user.isAdmin}">Admin Panel</div>
<div th:unless="${user.isAdmin}">Regular User View</div>

<!-- th:switch / th:case -->
<div th:switch="${user.role}">
  <p th:case="'ADMIN'">Administrator</p>
  <p th:case="'MANAGER'">Manager</p>
  <p th:case="*">Regular User</p>  <!-- default case -->
</div>
```

## 13.3 Spring MVC Form Binding

```java
// Controller
@PostMapping("/users/create")
public String createUser(@Valid @ModelAttribute("userForm") UserForm userForm,
                          BindingResult result, Model model) {
    if (result.hasErrors()) {
        return "users/create";  // returns to form with errors
    }
    userService.create(userForm);
    return "redirect:/users";
}
```

```html
<!-- Template: users/create.html -->
<form th:action="@{/users/create}" th:object="${userForm}" method="post">
  <div th:classappend="${#fields.hasErrors('email')} ? 'field-error'">
    <label for="email">Email</label>
    <input type="email" id="email" th:field="*{email}">
    <!-- th:field automatically sets name, id, and value -->
    <span th:if="${#fields.hasErrors('email')}" 
          th:errors="*{email}" class="error-message"></span>
  </div>
  <button type="submit">Create</button>
</form>
```

## 13.4 Thymeleaf Fragments (Reusable Components)

```html
<!-- fragments/components.html -->
<div th:fragment="user-card(user)">
  <div class="card">
    <img th:src="${user.avatarUrl}" th:alt="${user.name}" width="64" height="64">
    <h3 th:text="${user.name}">User Name</h3>
    <p th:text="${user.role}">Role</p>
  </div>
</div>

<!-- Usage in another template -->
<div th:replace="~{fragments/components :: user-card(user=${currentUser})}"></div>

<!-- List of fragments -->
<div th:each="user : ${users}">
  <div th:replace="~{fragments/components :: user-card(user=${user})}"></div>
</div>
```

---

# Module 14 — Performance, Accessibility & SEO

## 14.1 Core Web Vitals

Google measures page quality with Core Web Vitals:

| Metric | What it Measures | Good |
|--------|-----------------|------|
| **LCP** — Largest Contentful Paint | Load time of largest visible element | < 2.5s |
| **INP** — Interaction to Next Paint | Response time to user interactions | < 200ms |
| **CLS** — Cumulative Layout Shift | Unexpected layout movement | < 0.1 |

**HTML techniques to improve Core Web Vitals:**
- Specify `width`/`height` on images → reduces CLS
- Use `fetchpriority="high"` on LCP image → improves LCP
- Use `loading="lazy"` on below-fold images → improves LCP
- Use `defer` on scripts → reduces render blocking → improves LCP
- Preload critical fonts and hero images

## 14.2 SEO Meta Tags

```html
<head>
  <!-- Essential SEO -->
  <title>Spring Boot Tutorial — Complete Guide 2024 | Acme Dev</title>
  <meta name="description" content="Complete Spring Boot tutorial covering REST APIs, JPA, security, and deployment. Updated for Spring Boot 3.2.">
  <link rel="canonical" href="https://example.com/blog/spring-boot-tutorial">

  <!-- Open Graph (social sharing) -->
  <meta property="og:type" content="article">
  <meta property="og:title" content="Spring Boot Tutorial — Complete Guide 2024">
  <meta property="og:description" content="Complete Spring Boot tutorial...">
  <meta property="og:image" content="https://example.com/images/spring-boot-og.png">
  <meta property="og:url" content="https://example.com/blog/spring-boot-tutorial">

  <!-- Article specific -->
  <meta property="article:published_time" content="2024-11-15T10:00:00+05:30">
  <meta property="article:author" content="https://example.com/authors/jane">
</head>
```

## 14.3 Structured Data (JSON-LD)

Structured data helps Google understand your content and display rich results:

```html
<!-- Article -->
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "TechArticle",
  "headline": "Spring Boot Tutorial — Complete Guide 2024",
  "datePublished": "2024-11-15",
  "author": { "@type": "Person", "name": "Jane Smith" },
  "publisher": {
    "@type": "Organization",
    "name": "Acme Dev",
    "logo": { "@type": "ImageObject", "url": "https://example.com/logo.png" }
  }
}
</script>

<!-- FAQ Page -->
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "FAQPage",
  "mainEntity": [
    {
      "@type": "Question",
      "name": "What is Spring Boot?",
      "acceptedAnswer": {
        "@type": "Answer",
        "text": "Spring Boot is an opinionated framework..."
      }
    }
  ]
}
</script>
```

## 14.4 WCAG 2.1 AA Checklist for Enterprise HTML

| Criterion | Requirement | HTML Technique |
|-----------|------------|----------------|
| 1.1.1 Non-text Content | All images have text alternatives | `alt` attribute |
| 1.3.1 Info and Relationships | Structure conveyed semantically | Headings, lists, tables with `scope` |
| 1.4.4 Resize Text | 200% zoom without loss of content | Viewport meta without `user-scalable=no` |
| 2.1.1 Keyboard | All functionality via keyboard | No `tabindex>0`, avoid `onclick` on divs |
| 2.4.1 Bypass Blocks | Skip repeated content | Skip navigation link |
| 2.4.2 Page Titled | Each page has a descriptive title | Unique `<title>` per page |
| 2.4.6 Headings and Labels | Headings and labels are descriptive | Proper heading hierarchy, `<label>` on all inputs |
| 3.1.1 Language of Page | Page language identified | `lang` on `<html>` |
| 3.3.1 Error Identification | Errors are described in text | `role="alert"`, `aria-invalid="true"` |
| 4.1.2 Name, Role, Value | UI components have accessible name | `aria-label`, `aria-labelledby` |

---

## Final Checklist Before Pushing to Production

```
□ DOCTYPE declaration present
□ lang attribute on <html>
□ charset=UTF-8 meta tag (first in <head>)
□ Viewport meta tag
□ Unique, descriptive <title> per page
□ Meta description
□ All images have alt text
□ All images have width and height
□ All form inputs have labels
□ All radio/checkbox groups in fieldsets with legends
□ Heading hierarchy is correct (one h1, no skipped levels)
□ CSRF token in all POST forms
□ th:href="@{}" for all internal URLs
□ Skip navigation link present
□ Valid HTML (no errors in W3C validator)
□ ARIA landmarks present (main, nav, header, footer)
□ No tables used for layout
□ Scripts use defer or async
□ No sensitive data in HTML source or comments
```
