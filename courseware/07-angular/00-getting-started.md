# Module 00 — Getting Started with Angular 21

## Learning Objectives
- Understand what Angular is and how it differs from React
- Scaffold a new Angular 21 project with the CLI
- Understand every generated file
- Trace the full bootstrap sequence
- Run and modify your first Angular app

---

## 0.1 What Is Angular?

Angular is a **full-featured, opinionated framework** built and maintained by Google. Unlike React (a UI library), Angular ships with everything you need:

| Feature | Angular (built-in) | React (you choose) |
|---------|-------------------|-------------------|
| Routing | ✅ `@angular/router` | React Router |
| HTTP | ✅ `HttpClient` | Axios / fetch |
| Forms | ✅ Reactive + Template-driven | React Hook Form |
| State | ✅ Signals (Angular 16+) | useState / Redux |
| DI Container | ✅ Built-in | — |
| CLI | ✅ `@angular/cli` | Vite |
| Testing | ✅ Jasmine + Karma / Jest | Vitest |

### Angular vs React — mental model

| | React | Angular |
|--|-------|---------|
| Type | Library | Framework |
| Language | JSX (JS/TS) | TypeScript only |
| Templates | JSX inside `.tsx` | Separate `.html` files |
| Data binding | One-way by default | Two-way with `[(ngModel)]` |
| State (modern) | `useState`, Signals (React 19) | Signals (Angular 16+) |
| Change detection | Virtual DOM diffing | Zone.js → now Signals |
| Learning curve | Moderate | Steeper (but more structured) |

---

## 0.2 Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| Node.js | 20 LTS+ | `node -v` |
| npm | 10+ | `npm -v` |
| Angular CLI | 21 | `ng version` |

Install or update the Angular CLI globally:

```bash
npm install -g @angular/cli@21
ng version   # should show Angular CLI: 21.x
```

### VS Code Extensions
- **Angular Language Service** — autocomplete in templates, go-to-definition
- **Angular Snippets** — type `a-component` → full skeleton
- **ESLint** — code quality
- **Prettier** — formatting
- **Material Icon Theme** — Angular folder structure is easier to navigate

---

## 0.3 Creating the Project

```bash
ng new ibm-ems-angular --routing --style=css --standalone
cd ibm-ems-angular
ng serve
```

**Flag meanings:**

| Flag | Effect |
|------|--------|
| `--routing` | Generates `app.routes.ts` for routing |
| `--style=css` | Use plain CSS (no SCSS/LESS) |
| `--standalone` | Use standalone components (modern Angular, no NgModules needed) |

Open **http://localhost:4200** — you see the Angular welcome page.

---

## 0.4 Project File Anatomy

```
ibm-ems-angular/
│
├── src/
│   ├── app/
│   │   ├── app.component.ts        ← Root component (class + template + styles)
│   │   ├── app.component.html      ← Root template
│   │   ├── app.component.css       ← Root styles (scoped)
│   │   ├── app.component.spec.ts   ← Root component tests
│   │   └── app.routes.ts           ← Route configuration
│   │
│   ├── assets/                     ← Static files (images, fonts)
│   ├── index.html                  ← The one HTML file
│   ├── main.ts                     ← Entry point — bootstraps Angular
│   └── styles.css                  ← Global stylesheet
│
├── angular.json                    ← CLI workspace configuration
├── package.json
└── tsconfig.json
```

### Key files explained

#### `src/index.html`

```html
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>IbmEmsAngular</title>
  <base href="/">           <!-- Angular Router needs this -->
</head>
<body>
  <app-root></app-root>     <!-- Angular mounts the app here -->
</body>
</html>
```

`<app-root>` is the custom HTML element defined by the root `AppComponent`. Angular replaces it with the rendered template.

#### `src/main.ts`

```ts
import { bootstrapApplication } from '@angular/platform-browser'
import { appConfig }            from './app/app.config'
import { AppComponent }         from './app/app.component'

bootstrapApplication(AppComponent, appConfig)
  .catch(err => console.error(err))
```

`bootstrapApplication` is the modern standalone API — no `NgModule` needed.

#### `src/app/app.config.ts` (generated with `--standalone`)

```ts
import { ApplicationConfig } from '@angular/core'
import { provideRouter }     from '@angular/router'
import { routes }            from './app.routes'

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
  ]
}
```

This is where you register global providers — router, HTTP client, etc.

#### `src/app/app.component.ts`

```ts
import { Component } from '@angular/core'
import { RouterOutlet } from '@angular/router'

@Component({
  selector:    'app-root',          // the custom HTML tag
  standalone:  true,
  imports:     [RouterOutlet],      // what this component uses
  templateUrl: './app.component.html',
  styleUrl:    './app.component.css',
})
export class AppComponent {
  title = 'ibm-ems-angular'
}
```

---

## 0.5 Full Bootstrap Sequence

```
① Browser requests http://localhost:4200
        ↓
② Angular dev server sends index.html
        ↓
③ Browser finds <app-root> (unknown element — Angular will fill it)
   and the <script> tags injected by the CLI
        ↓
④ main.ts runs:
   bootstrapApplication(AppComponent, appConfig)
        ↓
⑤ Angular reads @Component({ selector: 'app-root', ... })
   Finds <app-root> in the DOM
        ↓
⑥ Angular compiles AppComponent's template
   Renders it into <app-root>
        ↓
⑦ Router reads the URL → matches a route → renders that component
   inside <router-outlet>
        ↓
⑧ Browser paints → user sees the UI
```

---

## 0.6 Angular CLI Commands Reference

```bash
# Start dev server (with hot reload)
ng serve

# Generate components, services, pipes, etc.
ng generate component employees/employee-card
ng generate service   employees/employee
ng generate pipe      shared/salary-format
ng generate guard     auth/auth

# Shorthand
ng g c employees/employee-card
ng g s employees/employee

# Build for production
ng build --configuration=production

# Run tests
ng test       # Jasmine + Karma (or Jest if configured)
ng e2e        # End-to-end (Cypress / Playwright)

# Lint
ng lint
```

---

## 0.7 Cleaning the Boilerplate

### `src/app/app.component.html` — replace entirely

```html
<h1>IBM Employee Management System</h1>
<router-outlet />
```

### `src/app/app.component.ts` — update title

```ts
import { Component } from '@angular/core'
import { RouterOutlet } from '@angular/router'

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'IBM EMS'
}
```

### `src/styles.css` — global reset

```css
*, *::before, *::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif;
  font-size: 16px;
  line-height: 1.6;
  color: #161616;
  background: #f4f4f4;
}

:root {
  --color-primary: #0062ff;
  --color-danger:  #da1e28;
  --color-success: #24a148;
  --color-border:  #e0e0e0;
  --color-surface: #ffffff;
}
```

Save — the browser hot-reloads and shows "IBM Employee Management System".

---

## 0.8 Standalone vs NgModule — Understanding the Shift

Angular originally required every component to be declared in an `NgModule`. Angular 14+ introduced **standalone components** as an opt-in, and Angular 17+ made them the default.

```ts
// OLD approach (NgModule) — you will see this in legacy code
@NgModule({
  declarations: [AppComponent, EmployeeCardComponent],
  imports: [BrowserModule, HttpClientModule],
  bootstrap: [AppComponent],
})
export class AppModule {}

// MODERN approach (Standalone) — what we use
// No NgModule needed — components declare their own dependencies
@Component({
  standalone: true,
  imports: [CommonModule, RouterLink],   // direct imports
  // ...
})
export class EmployeeCardComponent {}
```

> Throughout this course we use **standalone components exclusively**. When you encounter legacy NgModule-based code in the wild, the concepts are the same — the wiring is just different.

---

## 0.9 EMS Project Plan

| Module | Feature added to EMS |
|--------|----------------------|
| 02 | `EmployeeCardComponent` with data binding |
| 03 | Input/Output, component interaction |
| 04 | Debugging tools |
| 05 | `*ngIf`, `*ngFor`, custom directives |
| 06 | Services + Dependency Injection |
| 07 | Routing — list, detail, 404 pages |
| 08 | RxJS Observables |
| 09 | Reactive Forms — create/edit employee |
| 10 | Pipes — salary format, date, search filter |
| 11 | HttpClient — real API calls |
| 12 | Authentication + Route Guards |
| 13 | Dynamic components, lazy loading |
| 14 | Angular Modules, optimising build |
| 15 | Deploy to production |
| 16 | Unit testing |

---

## Summary

- Angular is a complete framework — routing, HTTP, forms, DI all built in
- Angular 21 uses standalone components by default — no NgModule needed
- `bootstrapApplication()` replaces the old `NgModule`-based bootstrap
- Signals are the modern way to handle reactive state (covered from Module 02)
- `ng generate` creates files correctly wired — use it for everything

**Next → Module 01: TypeScript & Angular Fundamentals**
