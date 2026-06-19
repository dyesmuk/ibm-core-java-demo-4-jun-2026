# Module 01 — Introduction to HTML

## 1.1 What Is HTML?

HTML — **HyperText Markup Language** — is the standard language for creating web pages. It was invented by Tim Berners-Lee at CERN in 1989/1990 as a way to share scientific documents over the internet. The key insight was *hypertext*: documents that link to other documents, creating a navigable web of information.

HTML is **not** a programming language. It is a **markup language**: it annotates plain text with tags that give structure and meaning. Browsers read those tags and render a visual page.

### The Three Pillars of the Web

Every web page is built from three technologies that work together:

```
HTML  →  Structure & Content   ("what is on the page")
CSS   →  Presentation          ("how it looks")
JS    →  Behaviour             ("what it does")
```

Keeping these concerns separate is a fundamental principle of professional web development. A Java developer who mixes SQL in the presentation layer draws criticism; mixing CSS in HTML or JS in CSS deserves the same scrutiny.

---

## 1.2 A Brief History of HTML

| Year | Milestone |
|------|-----------|
| 1991 | HTML 1.0 — Tim Berners-Lee publishes 18 elements |
| 1995 | HTML 2.0 — First formal specification (RFC 1866) |
| 1997 | HTML 3.2 / 4.0 — Tables, frames, scripting |
| 1999 | HTML 4.01 — Stable; introduced `strict`, `transitional`, `frameset` doctypes |
| 2000 | XHTML 1.0 — XML-serialised HTML; strict well-formedness rules |
| 2008 | HTML5 draft — WHATWG begins work; `<video>`, `<canvas>`, new semantics |
| 2014 | HTML5 — W3C official recommendation |
| 2019 | WHATWG Living Standard — Single authoritative spec; HTML is now maintained as a living document |

> **For enterprise developers:** You will encounter XHTML in legacy codebases (especially old JSP applications). XHTML requires every tag to be closed, attributes to be lowercase, and values quoted — all habits worth keeping in HTML5 too.

---

## 1.3 How Browsers Render HTML — The Critical Rendering Path

Understanding what happens *after* the server sends HTML to the browser will make you a better developer. The sequence is:

1. **DNS Resolution** — The domain name is resolved to an IP address.
2. **TCP Handshake / TLS** — A connection is established (HTTPS = TLS handshake too).
3. **HTTP Request/Response** — The browser requests the HTML; the server responds.
4. **HTML Parsing → DOM** — The browser's HTML parser reads bytes, decodes them, tokenises tags, and builds the **Document Object Model (DOM)** — a tree of objects representing the page.
5. **CSS Parsing → CSSOM** — Stylesheets are parsed into the **CSS Object Model**.
6. **Render Tree** — DOM + CSSOM are combined into a render tree (only visible nodes).
7. **Layout (Reflow)** — The browser calculates exact sizes and positions of every element.
8. **Paint** — Pixels are drawn to the screen layer by layer.
9. **Composite** — Layers are composited into the final image you see.

### Why This Matters for Java Developers

- A Spring Boot controller returning HTML must ensure the `<head>` loads CSS before `<body>` content to prevent a **Flash of Unstyled Content (FOUC)**.
- **Render-blocking resources** (`<script>` in `<head>`) delay the DOM build. Use `defer` or `async` attributes.
- Server-Side Rendering (SSR) in Thymeleaf pre-builds the DOM on the server — faster First Contentful Paint (FCP) than a React SPA loading data after hydration.
- Chunked HTTP responses allow browsers to start parsing HTML before the full page is sent.

---

## 1.4 HTML Standards and the WHATWG Living Standard

HTML is governed by the **WHATWG (Web Hypertext Application Technology Working Group)**, which publishes the [Living Standard](https://html.spec.whatwg.org/). This is a continuously updated document — not a versioned release.

### What "Valid HTML" Means

**Valid HTML** passes the [W3C Markup Validation Service](https://validator.w3.org/). Validation:
- Catches structural errors (unclosed tags, wrong nesting)
- Ensures accessibility tools work correctly
- Is a requirement in many enterprise quality gates and CI/CD pipelines

> **Enterprise Note:** Many organisations enforce HTML validation as part of automated testing. Tools like `html-validate` can be integrated into Maven/Gradle builds or CI pipelines.

---

## 1.5 Character Encoding

Every HTML document must declare its character encoding. Without it, browsers guess — and they guess wrong for special characters.

```html
<meta charset="UTF-8">
```

**Always use UTF-8.** It supports every Unicode character, is the default encoding for HTTP/1.1, and eliminates encoding bugs with international content — a common source of defects in enterprise Java applications that handle multiple locales.

In Spring Boot, ensure your `application.properties` also specifies:

```properties
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.force=true
spring.thymeleaf.encoding=UTF-8
```

---

## 1.6 Inline HTML vs. Server-Generated HTML

As a Java Full Stack Developer you will work with HTML in several ways:

| Approach | Technology | Use Case |
|----------|-----------|----------|
| Static HTML files | Plain `.html` files | Prototyping, static sites |
| Server-Side Templates | Thymeleaf, JSP, Freemarker | Spring MVC web apps |
| Fragment rendering | Thymeleaf fragments, HTMX | Partial page updates |
| Client-side rendering | React, Angular, Vue | SPAs consuming REST APIs |
| Hybrid rendering | Next.js, Nuxt.js | SSR + SPA |

Understanding raw HTML deeply means you can debug any of these layers when they go wrong.

---

## Key Takeaways

- HTML is a markup language for structuring content; CSS and JS add style and behaviour.
- The WHATWG Living Standard is the current authoritative reference.
- The browser's critical rendering path explains why tag order, resource loading, and server-side rendering all affect performance.
- Always use UTF-8 encoding.
- Valid HTML is a professional standard, not a suggestion.

---

## Self-Check Questions

1. What does "HyperText" mean in HTML?
2. In what order does a browser build the DOM, CSSOM, and Render Tree?
3. Why is render-blocking JavaScript in `<head>` a performance problem?
4. What is the difference between HTML5 and the WHATWG Living Standard?
5. How do you ensure UTF-8 encoding in both a Thymeleaf template and Spring Boot?
