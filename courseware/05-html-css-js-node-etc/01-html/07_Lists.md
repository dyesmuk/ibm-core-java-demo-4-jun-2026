# Module 07 — Lists

## 7.1 Types of Lists

HTML provides three types of lists, each with a distinct semantic meaning:

| Element | Meaning | Default Rendering |
|---------|---------|-------------------|
| `<ul>` | Unordered list — order doesn't matter | Bullet points |
| `<ol>` | Ordered list — order matters | Numbers |
| `<dl>` | Description list — term/definition pairs | No bullets |

---

## 7.2 Unordered Lists `<ul>`

```html
<ul>
  <li>Spring Boot</li>
  <li>Hibernate / JPA</li>
  <li>Thymeleaf</li>
  <li>Apache Kafka</li>
</ul>
```

Use when the order of items has no significance — a feature list, a set of options, navigation links.

### List Styling with CSS

```css
/* Remove default bullets and padding */
ul.nav-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

/* Custom bullet using ::before */
ul.feature-list li::before {
  content: "✓";
  color: var(--color-success);
  margin-right: 0.5em;
}
```

---

## 7.3 Ordered Lists `<ol>`

```html
<ol>
  <li>Create a Spring Boot project at start.spring.io</li>
  <li>Add Thymeleaf dependency to pom.xml</li>
  <li>Create a controller with @GetMapping</li>
  <li>Create the Thymeleaf template</li>
  <li>Run the application</li>
</ol>
```

### `<ol>` Attributes

| Attribute | Values | Example |
|-----------|--------|---------|
| `type` | `1`, `a`, `A`, `i`, `I` | `type="a"` for a, b, c... |
| `start` | Integer | `start="5"` begins at 5 |
| `reversed` | Boolean | Counts down |

```html
<!-- Steps from 3 to 1 (reversed) -->
<ol reversed start="3">
  <li>Deploy to production</li>
  <li>Run integration tests</li>
  <li>Build the JAR</li>
</ol>

<!-- Legal document outline with letters -->
<ol type="A">
  <li>Definitions
    <ol type="1">
      <li>"Agreement" means...</li>
      <li>"Service" means...</li>
    </ol>
  </li>
  <li>Obligations</li>
</ol>
```

> **CSS vs HTML attributes:** Prefer CSS for visual styling (`list-style-type`) and use HTML `type`/`start` only when the value has semantic meaning (the number/letter is actually referenced in the document).

---

## 7.4 Description Lists `<dl>`

Description lists define term/definition pairs. Often overlooked but semantically powerful:

```html
<dl>
  <dt>RESTful API</dt>
  <dd>An architectural style for distributed hypermedia systems that uses HTTP verbs and stateless communication.</dd>

  <dt>JWT</dt>
  <dd>JSON Web Token — a compact, URL-safe means of representing claims between two parties.</dd>

  <dt>ORM</dt>
  <dd>Object-Relational Mapping — a technique to map Java objects to database tables.</dd>
  <dd>Commonly implemented in Java using JPA with Hibernate as the provider.</dd>
</dl>
```

- `<dt>` = description term
- `<dd>` = description detail
- One `<dt>` can have multiple `<dd>` elements
- Multiple `<dt>` elements can share a `<dd>`

### Real-World Uses

```html
<!-- Metadata panel in a CMS or admin dashboard -->
<dl class="metadata-grid">
  <dt>Created by</dt>
  <dd>Jane Smith</dd>

  <dt>Created on</dt>
  <dd><time datetime="2024-11-15">15 November 2024</time></dd>

  <dt>Status</dt>
  <dd><span class="badge badge-success">Published</span></dd>

  <dt>Categories</dt>
  <dd>Technology, Enterprise</dd>
</dl>

<!-- HTTP error code reference -->
<dl>
  <dt><code>400 Bad Request</code></dt>
  <dd>The server could not understand the request due to invalid syntax.</dd>

  <dt><code>401 Unauthorized</code></dt>
  <dd>Authentication is required to access this resource.</dd>

  <dt><code>403 Forbidden</code></dt>
  <dd>The client does not have access rights to the content.</dd>

  <dt><code>404 Not Found</code></dt>
  <dd>The server cannot find the requested resource.</dd>
</dl>
```

---

## 7.5 Nested Lists

Lists can be nested to represent hierarchical information:

```html
<ul>
  <li>Frontend
    <ul>
      <li>HTML & CSS</li>
      <li>JavaScript
        <ul>
          <li>React</li>
          <li>TypeScript</li>
        </ul>
      </li>
    </ul>
  </li>
  <li>Backend
    <ul>
      <li>Java / Spring Boot</li>
      <li>REST API Design</li>
      <li>JPA / Hibernate</li>
    </ul>
  </li>
  <li>Database
    <ul>
      <li>PostgreSQL</li>
      <li>Redis</li>
    </ul>
  </li>
</ul>
```

> **Rule:** The nested `<ul>` or `<ol>` must be **inside** a `<li>`, not directly inside the parent `<ul>`/`<ol>`.

```html
<!-- ❌ Wrong: nested list directly in ul -->
<ul>
  <li>Parent</li>
  <ul>
    <li>Child</li>  <!-- invalid! -->
  </ul>
</ul>

<!-- ✅ Correct: nested list inside li -->
<ul>
  <li>Parent
    <ul>
      <li>Child</li>
    </ul>
  </li>
</ul>
```

---

## 7.6 Lists in Thymeleaf

Generating lists dynamically from Java collections:

```html
<!-- Simple list from a List<String> -->
<ul>
  <li th:each="skill : ${skills}" th:text="${skill}">Skill Name</li>
</ul>

<!-- Ordered list of users with index -->
<ol>
  <li th:each="user, stat : ${users}">
    <span th:text="${stat.count}">1</span>.
    <a th:href="@{/users/{id}(id=${user.id})}" th:text="${user.name}">User Name</a>
    <span th:text="${user.role}" class="badge"></span>
  </li>
</ol>

<!-- Description list from a Map<String, String> -->
<dl>
  <th:block th:each="entry : ${systemProperties}">
    <dt th:text="${entry.key}">Property Name</dt>
    <dd th:text="${entry.value}">Property Value</dd>
  </th:block>
</dl>

<!-- Nested list: department → employees -->
<ul>
  <li th:each="dept : ${departments}">
    <strong th:text="${dept.name}">Department</strong>
    <ul th:if="${not #lists.isEmpty(dept.employees)}">
      <li th:each="emp : ${dept.employees}" th:text="${emp.name}">Employee</li>
    </ul>
    <p th:unless="${not #lists.isEmpty(dept.employees)}" class="empty-state">
      No employees in this department.
    </p>
  </li>
</ul>
```

---

## 7.7 Accessibility Considerations

- Screen readers announce the list type and item count: "List, 5 items"
- Removing `list-style` with CSS in some browsers (notably Safari) strips the list semantics — add `role="list"` to the `<ul>` if you remove the visual bullets
- Navigation menus should be `<ul>` with `<li>` items, not `<div>` with `<a>` tags
- Use `aria-label` on `<ul>` to give the list a name when context is needed

```css
/* If you remove list-style, preserve semantics */
ul.custom-list {
  list-style: none;
}
```

```html
<ul class="custom-list" role="list">  <!-- role="list" restores semantics in Safari -->
  <li>Item 1</li>
</ul>
```

---

## Key Takeaways

- Use `<ul>` when order doesn't matter, `<ol>` when it does, `<dl>` for term-definition pairs.
- Nested lists must always be placed inside `<li>`, not directly in the parent list.
- `<dl>` is underused — it's perfect for metadata panels and glossaries in enterprise apps.
- Use `th:each` in Thymeleaf to render lists from Java collections.
- If you remove `list-style` via CSS, add `role="list"` for accessibility.

---

## Self-Check Questions

1. What is the semantic difference between `<ul>` and `<ol>`?
2. When should you use a `<dl>` instead of `<ul>`?
3. What is the HTML structure rule for nested lists?
4. How would you render a `List<User>` in Thymeleaf, showing the position number next to each user?
5. Why might you need `role="list"` on a `<ul>` element?
