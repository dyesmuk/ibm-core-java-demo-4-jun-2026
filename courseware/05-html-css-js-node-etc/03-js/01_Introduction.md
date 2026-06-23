# JavaScript Courseware — Module 01: Introduction to JavaScript

> **Who is this for?** Java developers stepping into the JavaScript world as part of Full Stack development. You already know OOP, types, and how programs run — we'll use that to fast-track your JS understanding instead of starting from zero.

---

## 1.1 What Is JavaScript?

JavaScript is a **dynamic, interpreted, single-threaded, garbage-collected** programming language. It was created by Brendan Eich in 1995 in literally 10 days (yes, that explains some of the quirks). It is the **only language that runs natively in web browsers**, and with Node.js it now runs on the server too.

It has evolved massively. Modern JavaScript (ES2020+) is clean, powerful, and a joy to write — don't let the horror stories from 2005 scare you.

---

## 1.2 JavaScript vs Java — The Real Differences

The name similarity is **marketing, not lineage**. They are fundamentally different languages.

| Concept | Java | JavaScript |
|---|---|---|
| Type system | Static, strong | Dynamic, weak |
| Variable typing | `String name = "Alice"` | `let name = "Alice"` |
| Compilation | Compiled → bytecode (.class) | JIT-compiled at runtime |
| Concurrency | Multi-threaded (JVM threads) | Single-threaded + event loop |
| OOP model | Class-based inheritance | Prototype-based (classes are sugar) |
| "Nothing" values | `null` only | `null` AND `undefined` (two kinds!) |
| Entry point | `public static void main(...)` | Top-level code runs immediately |
| Packages | `package com.example` | ES modules (`import`/`export`) |
| Build tool | Maven / Gradle | npm / yarn / pnpm |

### The Mental Model Shift

In Java you think: *"types first, behaviour through methods on classes."*

In JavaScript you think: *"functions first — objects are just dictionaries that optionally have prototypes."*

JavaScript rewards a **functional style** far more than Java does. You will frequently:
- Pass functions as arguments (callbacks)
- Return functions from functions (factories, closures)
- Compose small functions into larger behaviours

---

## 1.3 Where JavaScript Runs

```
┌──────────────────────────────────────────────┐
│                  JavaScript                   │
├──────────────────────┬───────────────────────┤
│  Browser (Client)    │  Node.js (Server)      │
│                      │                        │
│  V8 engine           │  V8 engine             │
│  DOM API             │  fs, http, path        │
│  fetch, XHR          │  process, Buffer       │
│  localStorage        │  npm ecosystem         │
│  Web Workers         │  streams, events       │
└──────────────────────┴───────────────────────┘
```

**Same V8 engine** (built by Google) powers both Chrome and Node.js. Your JavaScript knowledge transfers completely — the difference is only the available APIs.

> **This courseware** focuses on core JavaScript that works everywhere. Node.js-specific APIs (file system, HTTP server, streams) are covered in the separate Node.js & Express courseware.

---

## 1.4 ECMAScript — The Standard

JavaScript's official standard is **ECMAScript (ES)**, maintained by ECMA International's TC39 committee.

| Version | Year | Key Features |
|---|---|---|
| ES5 | 2009 | `strict mode`, JSON support, `Array.forEach` |
| **ES6 / ES2015** | 2015 | `let`/`const`, arrow functions, classes, Promises, modules, destructuring, template literals |
| ES2017 | 2017 | `async`/`await` |
| ES2020 | 2020 | Optional chaining `?.`, nullish coalescing `??`, `BigInt` |
| ES2022 | 2022 | Class fields, `at()`, top-level `await`, `structuredClone` |
| ES2023+ | ongoing | New array methods (`toSorted`, `findLast`, etc.) |

**Write ES2020+ code.** Node.js 20 LTS supports it fully. Browsers do too.

---

## 1.5 Setting Up Your Environment

### Install Node.js

Download the **LTS** version from [nodejs.org](https://nodejs.org).

```bash
node --version    # v20.x.x
npm --version     # 10.x.x
```

### Your First JavaScript File

```javascript
// hello.js
const greeting = "Hello, IBM!";
console.log(greeting);

const add = (a, b) => a + b;
console.log(add(2, 3));  // 5
```

```bash
node hello.js
# Hello, IBM!
# 5
```

### The Node.js REPL

Like Java's `jshell`, Node.js has a REPL (Read-Eval-Print Loop) — great for quick experiments:

```bash
node
> 2 + 2
4
> "Hello".toUpperCase()
'HELLO'
> [1, 2, 3].map(x => x * 2)
[ 2, 4, 6 ]
> .exit
```

### VS Code Setup

Install these extensions:
- **ESLint** — catches bugs and bad patterns
- **Prettier** — auto-formats your code
- **JavaScript (ES6) code snippets** — shortcuts
- **Thunder Client** — API testing (REST client)

Create `.eslintrc.json` at your project root:

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

## 1.6 How JavaScript Runs — The Engine

Unlike Java which compiles to bytecode upfront, JavaScript is parsed and compiled at runtime:

1. **Parsing** — source code → AST (Abstract Syntax Tree)
2. **Compilation** — AST → bytecode (V8's Ignition compiler)
3. **Optimisation** — hot code paths get JIT-compiled to native machine code (Turbofan)
4. **Execution** — runs on the event loop (covered in Module 08)

Key consequence: errors that Java catches at **compile time** only appear at **runtime** in plain JavaScript. This is the main motivation for TypeScript (used in Angular).

---

## 1.7 `use strict`

Strict mode makes JavaScript safer by converting silent bugs into thrown errors:

```javascript
'use strict';  // add at top of file or function

x = 10;           // ❌ ReferenceError: x is not defined
                   //    (without strict: silently creates a global!)
delete Object.prototype;  // ❌ TypeError
function sum(a, a) {}     // ❌ SyntaxError: duplicate parameter name
```

**Good news:** ES modules (files using `import`/`export`) are **always** in strict mode automatically. In Node.js with `"type": "module"` in `package.json`, all files are strict. So in modern development you don't need to type it manually.

---

## Key Takeaways

- JavaScript is dynamic, interpreted, and single-threaded. Java is static, compiled, and multi-threaded.
- The same V8 engine powers Chrome and Node.js — only the available APIs differ.
- ES2020+ is modern JavaScript. Write it. Node.js 20 LTS supports it fully.
- ES modules are always in strict mode.
- Type errors in plain JavaScript are runtime errors. TypeScript (used in Angular) fixes this.

---

## Self-Check Questions

1. What does "single-threaded" mean in JavaScript, and how does it still handle concurrent tasks?
2. Why does JavaScript have both `null` and `undefined` when Java only has `null`?
3. What is the ECMAScript standard and what is TC39?
4. Name three browser APIs not available in Node.js, and three Node.js APIs not available in the browser.
5. When is `'use strict'` applied automatically without you writing it?
