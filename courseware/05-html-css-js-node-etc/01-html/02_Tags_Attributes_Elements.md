# Module 02 — Tags, Attributes & Elements

## 2.1 Anatomy of an HTML Element

An **element** is the fundamental building block of HTML. Most elements have three parts:

```
<tagname attribute="value">Content</tagname>
│         │                 │        │
│         │                 │        └── Closing tag
│         │                 └── Content (text, other elements, or both)
│         └── Attribute with a value
└── Opening tag
```

### Example

```html
<a href="https://spring.io" target="_blank" rel="noopener noreferrer">
  Spring Framework
</a>
```

- **Tag name:** `a` (anchor)
- **Attributes:** `href`, `target`, `rel`
- **Content:** the text "Spring Framework"
- The whole thing — opening tag, content, and closing tag — is an **element**

---

## 2.2 Tags

A **tag** is the textual markup delimiter, written with angle brackets:

```html
<p>    ← opening tag
</p>   ← closing tag
```

Tags are **case-insensitive** in HTML5 (both `<P>` and `<p>` are valid), but **lowercase is the universal convention** — always write lowercase tags.

### Void (Self-Closing) Elements

Some elements cannot have content and therefore have no closing tag:

```html
<img src="logo.png" alt="Company Logo">
<input type="text" name="username">
<br>
<hr>
<meta charset="UTF-8">
<link rel="stylesheet" href="styles.css">
```

> **XHTML note:** In XHTML these are written with a self-closing slash: `<br />`, `<img />`. This syntax is **optional** in HTML5 but harmless. You will see it in JSP pages and older Spring templates.

### Complete List of Void Elements

`area`, `base`, `br`, `col`, `embed`, `hr`, `img`, `input`, `link`, `meta`, `param`, `source`, `track`, `wbr`

---

## 2.3 Attributes

Attributes provide **additional information** about an element. They live in the opening tag.

```html
<input type="email" id="email" name="email" placeholder="you@company.com" required>
```

### Attribute Syntax Rules

| Rule | Correct | Incorrect |
|------|---------|-----------|
| Name is lowercase | `class="..."` | `CLASS="..."` |
| Value in double quotes | `href="..."` | `href='...'` (single quotes work but double quotes are standard) |
| Boolean attributes need no value | `required`, `disabled`, `checked` | `required="required"` (valid but redundant) |
| No spaces around `=` | `id="main"` | `id = "main"` |

### Boolean Attributes

Boolean attributes are **present or absent** — the value doesn't matter:

```html
<!-- All of these mean the same thing: the input IS required -->
<input required>
<input required="">
<input required="required">

<!-- This means NOT required (attribute is absent) -->
<input>
```

---

## 2.4 Common Global Attributes

These attributes are valid on **every** HTML element:

| Attribute | Purpose | Example |
|-----------|---------|---------|
| `id` | Unique identifier within the page | `id="loginForm"` |
| `class` | CSS/JS hook; space-separated list | `class="btn btn-primary"` |
| `style` | Inline CSS (avoid in production) | `style="color:red"` |
| `title` | Tooltip on hover | `title="Click to submit"` |
| `lang` | Language of element content | `lang="fr"` |
| `dir` | Text direction (`ltr`, `rtl`, `auto`) | `dir="rtl"` |
| `hidden` | Hides element (like `display:none`) | `hidden` |
| `tabindex` | Keyboard navigation order | `tabindex="1"` |
| `data-*` | Custom data attributes | `data-user-id="42"` |
| `aria-*` | Accessibility attributes | `aria-label="Close"` |
| `contenteditable` | Makes element editable | `contenteditable="true"` |
| `draggable` | Enables drag-and-drop | `draggable="true"` |

### `data-*` Attributes — A Key Pattern for Java Developers

`data-*` attributes let you embed arbitrary data in HTML elements for JavaScript to consume:

```html
<!-- Thymeleaf template -->
<tr th:each="user : ${users}"
    data-user-id="${user.id}"
    data-role="${user.role}"
    class="user-row">
  <td th:text="${user.name}">John Doe</td>
</tr>
```

```javascript
// Reading in JavaScript
document.querySelectorAll('.user-row').forEach(row => {
  const userId = row.dataset.userId;   // camelCase: data-user-id → userId
  const role   = row.dataset.role;
});
```

This pattern avoids hidden form fields and keeps data close to the element that uses it.

---

## 2.5 The Parent/Child (Tree) Structure

HTML documents form a **tree** (the DOM). Every element except `<html>` has exactly one parent and may have zero or more children.

```html
<html>
  <head>
    <title>My App</title>
  </head>
  <body>
    <header>
      <nav>
        <ul>
          <li><a href="/">Home</a></li>
          <li><a href="/about">About</a></li>
        </ul>
      </nav>
    </header>
    <main>
      <h1>Welcome</h1>
      <p>Hello, <strong>world</strong>!</p>
    </main>
  </body>
</html>
```

Visualised:

```
html
├── head
│   └── title
│           "My App"
└── body
    ├── header
    │   └── nav
    │       └── ul
    │           ├── li → a ("Home")
    │           └── li → a ("About")
    └── main
        ├── h1  ("Welcome")
        └── p
            ├── "Hello, "
            ├── strong ("world")
            └── "!"
```

### Terminology

- **Parent**: The element directly containing another (`<ul>` is the parent of `<li>`)
- **Child**: A directly contained element (`<li>` is a child of `<ul>`)
- **Descendant**: Any nested element, not just direct children (`<a>` is a descendant of `<nav>`)
- **Ancestor**: Any element above in the tree (`<nav>` is an ancestor of `<a>`)
- **Sibling**: Elements sharing the same parent (`<li>` elements are siblings)

This terminology is used directly in CSS selectors (descendant selector, child selector) and in JavaScript DOM traversal methods like `.parentElement`, `.children`, `.closest()`.

---

## 2.6 Content Categories

HTML5 defines **content categories** that determine which elements can nest inside which others. Knowing these prevents invalid nesting.

| Category | Elements | Notes |
|----------|----------|-------|
| **Flow content** | Almost everything in `<body>` | `<div>`, `<p>`, `<table>`, headings, etc. |
| **Phrasing content** | Inline elements | `<span>`, `<a>`, `<strong>`, `<img>` |
| **Heading content** | `<h1>`–`<h6>`, `<hgroup>` | |
| **Sectioning content** | `<article>`, `<aside>`, `<nav>`, `<section>` | Create new outline entries |
| **Embedded content** | `<img>`, `<video>`, `<canvas>`, `<iframe>` | External resources |
| **Interactive content** | `<a>`, `<button>`, `<input>`, `<select>` | Requires user interaction |
| **Metadata content** | `<link>`, `<meta>`, `<script>`, `<style>`, `<title>` | Goes in `<head>` |

### Common Nesting Mistakes

```html
<!-- ❌ Wrong: block element inside inline element -->
<span>
  <div>Content</div>
</span>

<!-- ❌ Wrong: <p> cannot contain block elements -->
<p>
  <div>This breaks the paragraph</div>
</p>

<!-- ❌ Wrong: interactive elements cannot nest -->
<a href="/page">
  <button>Click me</button>   <!-- invalid: a inside a is also invalid -->
</a>

<!-- ✅ Correct: use a button styled as a link, or an anchor styled as a button -->
<button class="link-btn" onclick="location.href='/page'">Click me</button>
```

---

## 2.7 Comments

HTML comments are not rendered but are visible in page source (never put secrets in them):

```html
<!-- This is a comment -->

<!-- 
  Multi-line comment
  Used to explain complex markup or 
  temporarily disable sections
-->

<!-- TODO: Replace with Thymeleaf fragment once API is ready -->
<div class="placeholder">Loading...</div>
```

> **Security note:** HTML source is publicly visible. Never include API keys, internal server paths, employee names, or business logic in HTML comments. This is a common finding in web security audits of Java enterprise applications.

---

## 2.8 Special Characters (HTML Entities)

Some characters have special meaning in HTML and must be **escaped**:

| Character | Entity | Numeric Entity |
|-----------|--------|---------------|
| `<` | `&lt;` | `&#60;` |
| `>` | `&gt;` | `&#62;` |
| `&` | `&amp;` | `&#38;` |
| `"` | `&quot;` | `&#34;` |
| `'` | `&apos;` | `&#39;` |
| Non-breaking space | `&nbsp;` | `&#160;` |
| © | `&copy;` | `&#169;` |
| ® | `&reg;` | `&#174;` |
| → | `&rarr;` | `&#8594;` |
| € | `&euro;` | `&#8364;` |

### Thymeleaf Auto-Escaping

Thymeleaf's `th:text` attribute **automatically escapes** HTML entities, which is important for XSS prevention:

```html
<!-- If user.bio = "<script>alert('xss')</script>" -->
<p th:text="${user.bio}">Bio here</p>
<!-- Renders as: &lt;script&gt;alert('xss')&lt;/script&gt; — safe! -->

<!-- th:utext renders UNESCAPED HTML — only use for trusted content -->
<p th:utext="${trustedHtmlContent}">Content here</p>
```

---

## Key Takeaways

- An **element** = opening tag + content + closing tag; a **tag** is just the `<name>` delimiter.
- Void elements (`<img>`, `<input>`, `<br>`, etc.) have no closing tag.
- Attributes provide extra information; boolean attributes need no value.
- `data-*` attributes are the standard way to pass server-side data to JavaScript.
- HTML forms a tree — every CSS selector and DOM API method depends on this structure.
- Content categories define valid nesting rules; invalid nesting causes browser quirks.
- Escape special characters; Thymeleaf's `th:text` does this automatically.

---

## Self-Check Questions

1. What is the difference between a tag and an element?
2. Name five void elements.
3. How do you attach custom data from a Thymeleaf model to an HTML element for JavaScript?
4. Can a `<p>` element contain a `<div>`? Why or why not?
5. Why should you never put sensitive information in HTML comments?
6. What is the difference between `th:text` and `th:utext` in Thymeleaf?
