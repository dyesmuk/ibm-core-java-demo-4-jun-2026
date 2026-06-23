# JavaScript Courseware — Module 10: ES Modules & Modern JavaScript

---

## 10.1 ES Modules

ES Modules are the official JavaScript module system (ES2015+). They replace CommonJS (`require`) in browser code and modern Node.js projects.

### Exporting

```javascript
// math.js

// Named exports — export specific values
export const PI = 3.14159;

export function add(a, b) {
  return a + b;
}

export class Vector {
  constructor(x, y) { this.x = x; this.y = y; }
  magnitude() { return Math.sqrt(this.x**2 + this.y**2); }
}

// Export list at bottom (shows the module's public API at a glance)
const subtract = (a, b) => a - b;
const multiply = (a, b) => a * b;

export { subtract, multiply };
export { subtract as sub, multiply as mul };  // rename on export

// Default export — one per file (the "main" thing the module provides)
export default function greet(name) {
  return `Hello, ${name}!`;
}

// Re-export from another module
export { add as mathAdd } from './utils.js';
export * from './helpers.js';           // re-export everything
export * as utils from './helpers.js';  // re-export as namespace
```

### Importing

```javascript
// app.js

// Named imports
import { add, subtract } from './math.js';
import { add as mathAdd } from './math.js';  // rename on import

// Default import (any name works — it's the default)
import greet from './math.js';
import myGreet from './math.js';   // same export, different local name

// Named + default together
import greet, { add, PI } from './math.js';

// Namespace import (all named exports as an object)
import * as math from './math.js';
math.add(2, 3);  // 5
math.PI;         // 3.14159

// Dynamic import — lazy loading (returns Promise)
const { add } = await import('./math.js');

// Conditional loading
if (needsAdvancedFeature) {
  const module = await import('./advanced.js');
  module.default.init();
}
```

### CommonJS vs ES Modules in Node.js

```javascript
// CommonJS (traditional Node.js — still widely used)
const path = require('path');
const { readFile } = require('fs/promises');
const myModule = require('./myModule');

module.exports = { myFunction };
module.exports = myFunction;   // default-like export

// ES Modules in Node.js (two ways to enable):
// Option 1: use .mjs extension
// Option 2: add "type": "module" to package.json (preferred for new projects)

// Then use import/export:
import { readFile } from 'fs/promises';   // ✅
import path from 'path';                  // ✅
const data = require('./data');           // ❌ require not available in ESM
```

---

## 10.2 Destructuring — Advanced Patterns

```javascript
// Rename + default in one expression
const {
  name: fullName = 'Anonymous',
  age: years = 0,
  address: { city = 'Unknown' } = {}  // nested with default for missing object
} = user;

// Computed key destructuring
const key = 'name';
const { [key]: value } = obj;   // value = obj.name

// Destructuring in function parameters (named params pattern)
async function createUser({
  name,
  email,
  role = 'user',
  permissions = []
} = {}) {
  // = {} prevents crash if called with no args
  return await db.create({ name, email, role, permissions });
}

// Nested array destructuring
const matrix = [[1, 2], [3, 4]];
const [[a, b], [c, d]] = matrix;   // a=1, b=2, c=3, d=4
```

---

## 10.3 Modern String Methods (Review)

```javascript
'hello'.includes('ell')         // true
'hello'.startsWith('hel')       // true
'hello'.endsWith('llo')         // true
'ha'.repeat(3)                  // 'hahaha'
'5'.padStart(4, '0')            // '0005'
'  hello  '.trim()              // 'hello'
'hello hello'.replaceAll('hello', 'hi')  // 'hi hi'  (ES2021)
'hello'.at(-1)                  // 'o'  (ES2022)
```

---

## 10.4 Modern Array Methods (ES2023)

```javascript
const arr = [3, 1, 4, 1, 5];

// Non-mutating alternatives (leave original unchanged)
arr.toSorted((a, b) => a - b)     // [1, 1, 3, 4, 5] — arr unchanged
arr.toReversed()                   // [5, 1, 4, 1, 3] — arr unchanged
arr.toSpliced(2, 1, 99)            // [3, 1, 99, 1, 5] — arr unchanged
arr.with(0, 99)                    // [99, 1, 4, 1, 5] — replace index 0

// findLast / findLastIndex
[1, 2, 3, 4].findLast(n => n % 2 === 0)     // 4 (last even)
[1, 2, 3, 4].findLastIndex(n => n % 2 === 0) // 3
```

---

## 10.5 Logical Assignment Operators (ES2021)

```javascript
// ||= : assign if current is falsy
let name = '';
name ||= 'Anonymous';   // name = 'Anonymous'

// ??= : assign if current is null or undefined (preferred)
let cache = null;
cache ??= {};

user.profile ??= {};
user.profile.bio ??= 'No bio';

// &&= : assign only if current is truthy
config.debug &&= false;
```

---

## 10.6 `structuredClone` — Deep Copy (ES2022)

```javascript
// Problem: spread is shallow
const original = { name: 'Alice', address: { city: 'NYC' } };
const shallow = { ...original };
shallow.address.city = 'LA';
original.address.city;  // 'LA' — mutated! Shared nested reference

// structuredClone: proper deep copy, built-in (no lodash needed)
const deep = structuredClone(original);
deep.address.city = 'LA';
original.address.city;  // 'NYC' ✅

// Works with: arrays, Date, Map, Set, RegExp, nested objects
const data = structuredClone({
  date: new Date(),
  map: new Map([['a', 1]]),
  set: new Set([1, 2, 3]),
  nested: { arr: [1, 2, 3] }
});

// Does NOT work with: functions, DOM nodes, class methods (those are lost)
```

---

## 10.7 Symbols

```javascript
// Symbol: unique, immutable primitive — great for private-ish keys
const id  = Symbol('id');
const id2 = Symbol('id');
id === id2  // false — every Symbol is unique even with same description

// As object key (won't appear in for...in or Object.keys)
const user = {
  [id]: 123,
  name: 'Alice'
};
user[id]           // 123
Object.keys(user)  // ['name'] — Symbol not included

// Well-known Symbols: customise built-in behaviour
class Range {
  constructor(start, end) { this.start = start; this.end = end; }

  [Symbol.iterator]() {  // make class iterable with for...of and spread
    let current = this.start;
    const end = this.end;
    return {
      next() {
        return current <= end
          ? { value: current++, done: false }
          : { value: undefined, done: true };
      }
    };
  }
}

for (const n of new Range(1, 5)) {
  console.log(n);   // 1 2 3 4 5
}
[...new Range(1, 5)]   // [1, 2, 3, 4, 5]
```

---

## 10.8 Generators (Intermediate)

```javascript
// Generator function: pauses at each yield, resumes on next()
function* counter(start = 0) {
  while (true) {
    yield start++;
  }
}

const gen = counter(5);
gen.next().value   // 5
gen.next().value   // 6
gen.next().value   // 7

// Finite generator
function* range(start, end, step = 1) {
  for (let i = start; i < end; i += step) {
    yield i;
  }
}

for (const n of range(0, 10, 2)) {
  console.log(n);  // 0 2 4 6 8
}
[...range(1, 6)]  // [1, 2, 3, 4, 5]
```

---

## 10.9 Top-Level `await` (ES2022)

In ES modules, you can use `await` at the top level without wrapping in async:

```javascript
// config.mjs
const response = await fetch('/api/config');  // ✅ top-level await in ES module
export const config = await response.json();

// index.mjs
import { config } from './config.mjs';  // waits for config to resolve
console.log(config.port);
```

---

## 10.10 `globalThis`

```javascript
// The global object — works consistently in all environments
globalThis.setTimeout   // browser: window.setTimeout AND Node.js global
globalThis.fetch        // browser AND Node.js 18+
globalThis.process      // Node.js only (undefined in browser)

// Browser:  globalThis === window
// Node.js:  globalThis === global
// Worker:   globalThis === self
```

---

## Key Takeaways

- ES Modules use `import`/`export`; CommonJS uses `require`/`module.exports`. Set `"type": "module"` in `package.json` for ESM in Node.js.
- Dynamic `import()` enables lazy loading and conditional module loading.
- `structuredClone()` for deep copies — replaces the old JSON round-trip hack or lodash `cloneDeep`.
- Logical assignment (`||=`, `&&=`, `??=`) for clean initialisation patterns.
- `Symbol.iterator` makes any class work with `for...of` and spread.
- `toSorted`, `toReversed`, `toSpliced`, `with` are the modern non-mutating array alternatives.

---

## Self-Check Questions

1. What is the difference between a named export and a default export? Can a module have both?
2. How do you enable ES modules in Node.js?
3. What is the difference between `structuredClone` and spread (`{...obj}`)? When does spread fail?
4. What is a generator function? What does `yield` do?
5. Write a module `utils.js` that exports `add`, `subtract` as named exports and a `Calculator` class as default.
6. What does `??=` do? How is it different from `||=`?
