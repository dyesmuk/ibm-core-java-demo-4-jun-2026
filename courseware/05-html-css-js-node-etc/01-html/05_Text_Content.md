# Module 05 — Text Content: Headings, Paragraphs & Inline Elements

## 5.1 Headings

HTML provides six levels of heading, `<h1>` through `<h6>`:

```html
<h1>Page Title (One per page)</h1>
<h2>Major Section</h2>
<h3>Sub-section</h3>
<h4>Sub-sub-section</h4>
<h5>Rarely used in practice</h5>
<h6>Rarely used in practice</h6>
```

### Heading Hierarchy Rules

1. **Only one `<h1>` per page** — it represents the main topic of the page (like a book title).
2. **Never skip heading levels** — don't jump from `<h2>` to `<h4>`. Screen readers navigate by heading levels.
3. **Headings define document outline** — the sequence of `<h1>`–`<h6>` is read by accessibility tools, search engines, and browser reader modes.
4. **Don't use headings for styling** — if you want large text, use CSS. Headings have semantic meaning.

```html
<!-- ❌ Wrong: skipped a level, used heading for visual effect only -->
<h1>Dashboard</h1>
<h3>Active Users</h3>   <!-- skipped h2 -->
<h3>Use a heading just because I want big text</h3>

<!-- ✅ Correct: logical outline -->
<h1>Dashboard</h1>
<h2>Statistics</h2>
  <h3>Active Users</h3>
  <h3>Revenue</h3>
<h2>Recent Activity</h2>
```

### Headings in Single-Page Applications

In SPAs (React, Angular), when the route changes, programmatically move focus to the new `<h1>` so screen readers announce the new page:

```javascript
// After route navigation
document.querySelector('h1').focus();
```

---

## 5.2 Paragraphs

```html
<p>A paragraph is a block of text representing a single idea or concept. 
The browser adds margin above and below each paragraph.</p>

<p>A new <code>&lt;p&gt;</code> tag starts a new paragraph with its own margin.</p>
```

### Common Mistakes

```html
<!-- ❌ Using <br> to create paragraph spacing -->
The first paragraph.<br><br>The second paragraph.

<!-- ✅ Use separate <p> elements — semantically correct and easier to style -->
<p>The first paragraph.</p>
<p>The second paragraph.</p>

<!-- ❌ Empty <p> tags for spacing -->
<p></p>

<!-- ✅ Use CSS margin/padding instead -->
```

### `<br>` — Line Break

Use `<br>` only where a line break is part of the *content*, not for visual spacing:

```html
<!-- ✅ Appropriate use: postal address, poetry, song lyrics -->
<address>
  Acme Corp<br>
  Level 12, Tower B<br>
  UB City, Bengaluru 560001<br>
  India
</address>

<!-- ❌ Inappropriate: use CSS margin instead -->
<p>First sentence.<br><br>Second sentence.</p>
```

### `<hr>` — Thematic Break

`<hr>` represents a **thematic break** between sections — not just a visual line:

```html
<section>
  <h2>About the Product</h2>
  <p>Description...</p>
</section>

<hr>  <!-- Signals a topic shift -->

<section>
  <h2>Pricing</h2>
  <p>Details...</p>
</section>
```

---

## 5.3 Inline Text Elements

Inline elements flow within a line of text. Choosing the *semantically correct* element matters for accessibility and SEO.

### `<strong>` vs `<b>`

```html
<!-- <strong>: semantically important — screen readers may emphasise this -->
<p>Warning: <strong>This action cannot be undone.</strong></p>

<!-- <b>: stylistically bold, no semantic importance -->
<p>Results are displayed in <b>bold</b> for readability.</p>
```

### `<em>` vs `<i>`

```html
<!-- <em>: stressed emphasis — changes the meaning of the sentence -->
<p>I <em>never</em> said she stole the money.</p>

<!-- <i>: technical term, foreign phrase, thought, ship name — no stress -->
<p>The <i>Homo sapiens</i> species is remarkable.</p>
<p>The Latin phrase <i>et cetera</i> is abbreviated etc.</p>
```

### `<mark>`

Highlighted/relevant text (search result highlight):

```html
<p>Your search for "spring boot" found:
  <mark>Spring Boot</mark> auto-configuration is a powerful feature.
</p>
```

### `<small>`

Side comments, legal text, copyright notices:

```html
<p><small>© 2024 Acme Corp. All rights reserved. 
  Terms and conditions apply.</small></p>
```

### `<del>` and `<ins>`

Track changes in documents:

```html
<p>The meeting is on <del>Monday</del> <ins>Tuesday</ins> at 3pm.</p>
```

### `<sub>` and `<sup>`

```html
<p>Water is H<sub>2</sub>O.</p>
<p>Einstein's equation: E = mc<sup>2</sup>.</p>
<p>This is a footnote reference<sup><a href="#fn1">1</a></sup>.</p>
```

### `<code>`, `<kbd>`, `<samp>`, `<var>`

For technical documentation (common in enterprise developer portals):

```html
<!-- Inline code -->
<p>Call the <code>getUserById()</code> method.</p>

<!-- Keyboard input -->
<p>Press <kbd>Ctrl</kbd>+<kbd>S</kbd> to save.</p>

<!-- Sample output -->
<p>The command returns: <samp>200 OK</samp></p>

<!-- Variable name -->
<p>The variable <var>maxRetries</var> defaults to 3.</p>
```

### `<abbr>`

Abbreviations — reveals full form on hover:

```html
<p><abbr title="Application Programming Interface">API</abbr> documentation is essential.</p>
<p>We use <abbr title="Java Persistence API">JPA</abbr> for data access.</p>
```

### `<cite>` and `<q>` and `<blockquote>`

```html
<!-- Citation: title of a work -->
<p>The pattern is described in <cite>Design Patterns</cite> by the Gang of Four.</p>

<!-- Inline quotation (browser may add quotation marks) -->
<p>As Knuth wrote, <q>premature optimisation is the root of all evil</q>.</p>

<!-- Block quotation -->
<blockquote cite="https://www.oreilly.com/...">
  <p>Any fool can write code that a computer can understand. 
     Good programmers write code that humans can understand.</p>
  <footer>— Martin Fowler, <cite>Refactoring</cite></footer>
</blockquote>
```

---

## 5.4 The `<address>` Element

`<address>` provides contact information for the nearest `<article>` or `<body>`:

```html
<!-- Contact info for the whole page (in <footer>) -->
<footer>
  <address>
    <strong>Acme Corp</strong><br>
    <a href="mailto:info@acme.com">info@acme.com</a><br>
    <a href="tel:+918012345678">+91 80 1234 5678</a><br>
    12 MG Road, Bengaluru 560001
  </address>
</footer>

<!-- Contact info for an article author -->
<article>
  <h2>Quarterly Report</h2>
  <address>
    Written by <a href="mailto:jane@acme.com">Jane Smith</a>
  </address>
  <p>Content...</p>
</article>
```

> Note: `<address>` is for contact information, not for generic postal addresses. For a postal address in a form or data display, use `<p>` with `<br>` separators.

---

## 5.5 Preformatted Text

```html
<pre>
  function greet(name) {
    return `Hello, ${name}!`;
  }
</pre>
```

`<pre>` preserves whitespace and line breaks. Often combined with `<code>` for code blocks:

```html
<pre><code class="language-java">
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        return "Hello, " + name + "!";
    }
}
</code></pre>
```

Syntax highlighting libraries (Prism.js, highlight.js) look for this `<pre><code>` pattern.

---

## 5.6 Whitespace in HTML

HTML **collapses whitespace** — multiple spaces, tabs, and newlines are treated as a single space:

```html
<!-- This HTML: -->
<p>Hello     World
   How       are     you?</p>

<!-- Renders as: -->
Hello World How are you?
```

To control whitespace:
- Use `&nbsp;` for a non-breaking space (use sparingly)
- Use `<pre>` or CSS `white-space` property for preserved whitespace
- Use CSS `word-break`, `overflow-wrap` for long words

---

## Key Takeaways

- Use one `<h1>` per page; never skip heading levels — they define the document outline.
- Use `<p>` for paragraphs, not `<br><br>`; use `<br>` only where the line break is content.
- Choose semantic elements: `<strong>` for importance, `<em>` for emphasis, `<b>` and `<i>` for style only.
- Use `<code>`, `<kbd>`, `<samp>`, `<abbr>` in technical documentation.
- `<address>` is for contact information, not generic postal addresses.

---

## Self-Check Questions

1. Why should you never have two `<h1>` elements on the same page?
2. What is the difference between `<strong>` and `<b>`?
3. When is it appropriate to use `<br>`?
4. How would you display a block of Java code in HTML with syntax highlighting?
5. How would you mark an abbreviation so its full form appears on hover?
