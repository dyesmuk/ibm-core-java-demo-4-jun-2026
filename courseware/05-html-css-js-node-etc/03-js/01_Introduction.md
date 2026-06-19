# JS Basics — Module 01: Introduction to JavaScript

## 1.1 What Is JavaScript?

JavaScript is a **dynamic, interpreted, single-threaded, garbage-collected** programming language. It was created by Brendan Eich at Netscape in 10 days in 1995 — a fact that explains some of its quirks. It is the only language that runs natively in web browsers, and with Node.js it now runs on the server too.

### JavaScript vs Java — The Key Differences

As a Java developer, you will notice immediately that JavaScript is a fundamentally different kind of language. The name similarity is marketing, not lineage.

| Concept | Java | JavaScript |
|---------|------|-----------|
| Type system | Static, strong | Dynamic, weak |
| Typing | Explicit: `String name = "Alice"` | Inferred: `let name = "Alice"` |
| Compilation | Compiled to bytecode (.class) | JIT-compiled at runtime |
| Concurrency | Multi-threaded (JVM threads) | Single-threaded + event loop |
| OOP model | Class-based inheritance | Prototype-based (classes are syntactic sugar) |
| `null` check | `NullPointerException` | `undefined`, `null` — two different "nothings" |
| Entry point | `public static void main(String[] args)` | Top-level code runs immediately |
| Packages | `package com.example` | ES modules (`import`/`export`) |
| Build tool | Maven/Gradle | npm/yarn/pnpm |

### The Mental Model Shift

In Java you think: *types first, behaviour through methods on classes.*
In JavaScript you think: *functions first, objects are just dictionaries with optional prototypes.*

JavaScript rewards a functional style far more than Java does. You will frequently pass functions as arguments, return functions from functions, and compose behaviour from small units.

---

## 1.2 Where JavaScript Runs

```
┌─────────────────────────────────────────┐
│              JavaScript                  │
├────────────────────┬────────────────────┤
│   Browser (Client) │  Node.js (Server)  │
│                    │                    │
│  V8 engine         │  V8 engine         │
│  DOM API           │  fs, http, path    │
│  fetch, XHR        │  process, Buffer   │
│  localStorage      │  npm ecosystem     │
│  Web Workers       │  streams, events   │
└────────────────────┴────────────────────┘
```

The **V8 engine** (built by Google) is the same JavaScript engine in both Chrome and Node.js. Your language knowledge transfers completely — the difference is the APIs available.

---

## 1.3 The JavaScript Engine — How Code Runs

Unlike Java which compiles to bytecode up front, JavaScript is parsed and executed at runtime:

1. **Parsing** — source code → AST (Abstract Syntax Tree)
2. **Compilation** — AST → bytecode (V8 uses a baseline compiler)
3. **Optimisation** — hot code paths are JIT-compiled to machine code
4. **Execution** — runs on the event loop (covered in Module 08)

This means JavaScript has no compilation step you manually trigger. Errors that Java would catch at compile time (type errors, missing properties) only appear at runtime in plain JavaScript. This is a primary motivation for TypeScript.

---

## 1.4 ECMAScript and the Standard

JavaScript's official standard is **ECMAScript (ES)**, maintained by ECMA International's TC39 committee. Versions you will see referenced:

| Version | Year | Key Features |
|---------|------|-------------|
| ES5 | 2009 | `strict mode`, JSON, `Array.forEach` |
| ES6 / ES2015 | 2015 | `let/const`, arrow functions, classes, Promises, modules, destructuring, template literals |
| ES2017 | 2017 | `async/await` |
| ES2020 | 2020 | Optional chaining `?.`, nullish coalescing `??`, `BigInt` |
| ES2022 | 2022 | Class fields, `at()`, top-level `await` |
| ES2023+ | ongoing | New array methods, `Symbol.iterator` improvements |

**Write ES2020+ code.** Node.js 20 LTS supports it fully. Browsers do too (with optional Babel transpilation for old browsers).

---

## 1.5 Setting Up Your Environment

### Install Node.js

Download the **LTS** version from [nodejs.org](https://nodejs.org). Verify:

```bash
node --version    # v20.x.x
npm --version     # 10.x.x
```

### Your First JavaScript File

```javascript
// hello.js
const greeting = "Hello, World!";
console.log(greeting);

const add = (a, b) => a + b;
console.log(add(2, 3));  // 5
```

```bash
node hello.js
# Hello, World!
# 5
```

### The Node.js REPL

Like Java's `jshell`, Node has a REPL (Read-Eval-Print Loop):

```bash
node
> 2 + 2
4
> "Hello".toUpperCase()
'HELLO'
> [1,2,3].map(x => x * 2)
[ 2, 4, 6 ]
> .exit
```

### VS Code Setup

Install these extensions:
- **ESLint** — linting
- **Prettier** — formatting
- **JavaScript (ES6) code snippets**
- **Node.js Extension Pack**

Create a `.eslintrc.json` at your project root:

```json
{
  "env": { "node": true, "es2022": true },
  "extends": "eslint:recommended",
  "parserOptions": { "ecmaVersion": 2022, "sourceType": "module" },
  "rules": {
    "no-unused-vars": "warn",
    "no-console": "off"
  }
}
```

---

## 1.6 JavaScript in the Browser — A Brief Look

While this track focuses on Node.js, understanding the browser context helps you write better full-stack code:

```html
<!-- index.html -->
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>JS Demo</title>
</head>
<body>
  <h1 id="greeting">Hello</h1>
  <button id="changeBtn">Change Text</button>

  <script type="module" src="app.js"></script>
</body>
</html>
```

```javascript
// app.js
const heading = document.getElementById('greeting');
const btn = document.getElementById('changeBtn');

btn.addEventListener('click', () => {
  heading.textContent = 'Hello, JavaScript!';
  heading.style.color = 'dodgerblue';
});
```

The browser provides the **DOM API** (`document`, `window`) which is not available in Node.js. Node.js provides the **file system** (`fs`), **HTTP**, and **process** APIs which are not available in the browser.

---

## 1.7 `use strict`

Strict mode makes JavaScript safer by converting silent errors into thrown errors:

```javascript
'use strict';  // at top of file or function

x = 10;           // ❌ ReferenceError: x is not defined (in sloppy mode: creates global)
delete Object.prototype;  // ❌ TypeError
function sum(a, a) {}     // ❌ SyntaxError: duplicate parameter
```

**ES modules (files with `import`/`export`) are always in strict mode.** In Node.js with `"type": "module"` in `package.json`, all files are strict automatically.

---

## Key Takeaways

- JavaScript is dynamic, interpreted, and single-threaded; Java is static, compiled, and multi-threaded.
- The same V8 engine powers both Chrome and Node.js — only the available APIs differ.
- Write ES2020+ code. Node.js 20 LTS supports it fully.
- ES modules are always in strict mode.
- Type errors in JavaScript are runtime errors, not compile-time. TypeScript (Part 4) fixes this.

---

## Self-Check Questions

1. What is the difference between the JavaScript engine in the browser and in Node.js?
2. Why does JavaScript have both `null` and `undefined`?
3. What is the ECMAScript standard and why does it matter?
4. What is `use strict` and when is it applied automatically?
5. Name three browser APIs that are not available in Node.js, and three Node.js APIs not available in the browser.
