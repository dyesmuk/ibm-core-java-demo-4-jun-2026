# Module 03 — Tools of the Trade

## 3.1 Choosing Your Editor / IDE

### VS Code (Recommended for HTML/CSS Work)

**Visual Studio Code** is the most widely used editor for front-end development. Essential extensions for a Java Full Stack Developer:

| Extension | Purpose |
|-----------|---------|
| **HTML CSS Support** | Intellisense for class names defined in your CSS |
| **Auto Rename Tag** | Renames closing tag when you rename the opening one |
| **Prettier** | Opinionated formatter; integrates with Maven/Gradle |
| **Live Server** | Instant browser refresh on file save |
| **ESLint** | JavaScript linting |
| **Thymeleaf** | Syntax highlighting and completion for Thymeleaf templates |
| **IntelliCode** | AI-assisted code completion |
| **GitLens** | Enhanced Git integration |

### IntelliJ IDEA / WebStorm

If you use IntelliJ IDEA for your Java work (Ultimate Edition), it has first-class HTML, CSS, and Thymeleaf support built in. Key features:

- HTML structure validation
- CSS class name completion from linked stylesheets
- Thymeleaf attribute completion from the Spring model
- Built-in HTTP client for testing REST endpoints
- Live preview in the built-in browser

### Emmet — Write HTML 10× Faster

**Emmet** is built into both VS Code and IntelliJ. It expands shorthand notation into full HTML:

```
Type:    div.container>header+main>article*3>h2+p
Press:   Tab

Expands to:
<div class="container">
  <header></header>
  <main>
    <article>
      <h2></h2>
      <p></p>
    </article>
    <article>
      <h2></h2>
      <p></p>
    </article>
    <article>
      <h2></h2>
      <p></p>
    </article>
  </main>
</div>
```

**Common Emmet shortcuts:**

| Emmet | Output |
|-------|--------|
| `!` | Full HTML5 boilerplate |
| `ul>li*5` | `<ul>` with 5 `<li>` children |
| `input[type=email]` | `<input type="email">` |
| `a[href=#]{Click me}` | `<a href="#">Click me</a>` |
| `.card>.card-body>h3+p` | Nested divs with classes |
| `form>input:text+input:submit` | Form with inputs |

---

## 3.2 Browser Developer Tools

Browser DevTools are your most powerful debugging tool. Every professional developer keeps them open. Press **F12** (or **Cmd+Opt+I** on macOS) to open.

### Elements / Inspector Panel

- Shows the live DOM tree (not the raw HTML source — the *parsed* DOM)
- Hover over a node to highlight the element on the page
- Double-click any attribute to edit it live
- Right-click → "Force state" to test `:hover`, `:focus`, `:active` CSS states

### Key DevTools Workflows for HTML

**Inspecting generated HTML from Thymeleaf:**
Since Thymeleaf renders on the server, "View Source" shows the final HTML. DevTools shows the *live* DOM, which may differ if JavaScript modified it.

```
View Source  →  What the server sent
DevTools DOM →  What the browser parsed (and JS may have changed)
```

**Finding rendering problems:**
1. Open Elements panel
2. Use the selector tool (cursor icon) to click any element
3. See its box model, applied CSS, computed styles, and accessibility info

**Accessibility tree:**
Elements panel → Accessibility tab shows how screen readers see your page. Critical for enterprise applications that must meet WCAG compliance.

### Network Panel

- See every HTTP request the page makes
- Filter by `Doc`, `CSS`, `JS`, `Img`, `Fetch/XHR`
- Check response headers, status codes, timing
- Identify render-blocking resources
- Simulate slow connections (throttle)

### Console Panel

```javascript
// Find all elements missing alt attributes
document.querySelectorAll('img:not([alt])').forEach(img => console.warn('Missing alt:', img.src));

// Find all form inputs without labels
document.querySelectorAll('input:not([aria-label]):not([id])').forEach(i => console.warn('Unlabelled input:', i));
```

---

## 3.3 HTML Validators

### W3C Markup Validator

[https://validator.w3.org/](https://validator.w3.org/)

Paste HTML or provide a URL. Reports:
- Missing required attributes
- Invalid element nesting
- Deprecated elements
- Duplicate IDs

### html-validate (CLI / CI Integration)

For enterprise projects, integrate HTML validation into your build:

```bash
npm install -g html-validate
html-validate src/main/resources/templates/**/*.html
```

Or as a Maven plugin (via exec-maven-plugin):

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <executions>
    <execution>
      <id>validate-html</id>
      <phase>test</phase>
      <goals><goal>exec</goal></goals>
      <configuration>
        <executable>html-validate</executable>
        <arguments>
          <argument>src/main/resources/templates/**/*.html</argument>
        </arguments>
      </configuration>
    </execution>
  </executions>
</plugin>
```

### axe DevTools (Accessibility Validator)

[https://www.deque.com/axe/](https://www.deque.com/axe/)

Browser extension that audits accessibility in real time. Enterprise applications in regulated industries (healthcare, banking, government) are legally required to meet WCAG 2.1 AA standards.

---

## 3.4 Build Tools That Process HTML

In enterprise Java projects, HTML is often processed by build tools:

### Maven Resources Plugin

Copies and filters HTML/template files:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-resources-plugin</artifactId>
  <configuration>
    <encoding>UTF-8</encoding>
  </configuration>
</plugin>
```

### Webpack / Vite (for Modern Front-End)

If your project uses a JavaScript framework alongside Spring Boot (e.g., React consuming a Spring REST API), Webpack or Vite process HTML:

```javascript
// vite.config.js
import { defineConfig } from 'vite'
export default defineConfig({
  build: {
    outDir: 'src/main/resources/static',  // output to Spring Boot's static folder
  }
})
```

### HTML Minification

Production builds should minify HTML to reduce payload size. In Spring Boot with Thymeleaf:

```properties
# application-prod.properties
spring.thymeleaf.cache=true

# Or use htmlcompressor
```

With Maven:

```xml
<plugin>
  <groupId>com.samaxes.maven</groupId>
  <artifactId>minify-maven-plugin</artifactId>
</plugin>
```

---

## 3.5 Browser Compatibility

### Can I Use

[https://caniuse.com/](https://caniuse.com/) — Check browser support for any HTML/CSS/JS feature before using it.

### Target Browsers for Enterprise Applications

Enterprise applications typically target:
- Chrome / Edge (Chromium-based) — majority of corporate desktops
- Firefox — significant in technical/developer orgs
- Safari — mandatory if supporting macOS / iOS users

Define a `.browserslistrc` file in your project root:

```
# .browserslistrc
last 2 Chrome versions
last 2 Firefox versions
last 2 Edge versions
last 2 Safari versions
> 0.5%
not dead
```

This file is read by tools like Autoprefixer, Babel, and PostCSS to automatically add vendor prefixes and polyfills.

---

## 3.6 Setting Up Your First Project

### Project Structure for Spring Boot + Thymeleaf

```
my-app/
├── src/
│   └── main/
│       ├── java/com/company/myapp/
│       │   ├── controller/
│       │   ├── service/
│       │   └── MyAppApplication.java
│       └── resources/
│           ├── templates/          ← Thymeleaf .html files
│           │   ├── layout/
│           │   │   └── base.html   ← Thymeleaf layout dialect
│           │   ├── index.html
│           │   ├── login.html
│           │   └── dashboard.html
│           ├── static/             ← Static assets (served as-is)
│           │   ├── css/
│           │   │   └── main.css
│           │   ├── js/
│           │   │   └── app.js
│           │   └── images/
│           └── application.properties
└── pom.xml
```

### Essential Spring Boot Dependencies

```xml
<!-- pom.xml -->
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
  </dependency>
  <!-- Live reload during development -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
  </dependency>
</dependencies>
```

---

## Key Takeaways

- Use VS Code or IntelliJ IDEA; learn Emmet to write HTML efficiently.
- Browser DevTools are your primary debugging tool — the Elements panel shows the live DOM, not just raw source.
- Validate HTML with the W3C Validator or `html-validate` in CI.
- Use axe DevTools to check accessibility compliance.
- In Spring Boot, templates go in `src/main/resources/templates/`; static files in `src/main/resources/static/`.
- Use `.browserslistrc` to define target browsers for build tools.

---

## Exercise

1. Install VS Code and the extensions listed above.
2. Create a new Spring Boot project (via [start.spring.io](https://start.spring.io)) with Thymeleaf.
3. Open the generated `index.html` template, type `!` and press Tab in VS Code — observe the Emmet expansion.
4. Start the app, open DevTools, and explore the Elements panel.
5. Run the page through the W3C validator. Fix any reported issues.
