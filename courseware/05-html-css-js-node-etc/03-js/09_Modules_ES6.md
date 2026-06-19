# JS Basics — Module 09: ES Modules & Modern JavaScript Features

## 9.1 ES Modules

ES Modules are the official JavaScript module system (ES2015). They replace CommonJS (`require`) in browser code and modern Node.js.

### Exporting

```javascript
// math.js — named exports
export const PI = 3.14159;

export function add(a, b) {
  return a + b;
}

export class Vector {
  constructor(x, y) { this.x = x; this.y = y; }
  magnitude() { return Math.sqrt(this.x**2 + this.y**2); }
}

// Export list (at bottom of file — shows all public API at a glance)
const subtract = (a, b) => a - b;
const multiply = (a, b) => a * b;

export { subtract, multiply };
export { subtract as sub, multiply as mul };  // rename on export

// Default export — one per file
export default function greet(name) {
  return `Hello, ${name}!`;
}

// Re-export from another module
export { add as mathAdd } from './other.js';
export * from './utils.js';           // re-export everything
export * as utils from './utils.js';  // re-export as namespace
```

### Importing

```javascript
// app.js

// Named import
import { add, subtract } from './math.js';
import { add as mathAdd } from './math.js';   // rename on import

// Default import (any name works)
import greet from './math.js';
import myGreet from './math.js';   // same thing, different name

// Named + default together
import greet, { add, PI } from './math.js';

// Namespace import (all named exports as object)
import * as math from './math.js';
math.add(2, 3);  // 5
math.PI;         // 3.14159

// Dynamic import (lazy loading — returns Promise)
const { add } = await import('./math.js');

// Conditional dynamic import
if (needsCharting) {
  const Chart = await import('./chart.js');
  Chart.default.render(data);
}
```

### In Node.js: CommonJS vs ES Modules

```javascript
// CommonJS (traditional Node.js — .js files by default)
const path = require('path');
const { readFile } = require('fs/promises');
const myModule = require('./myModule');

module.exports = { myFunction };
module.exports = myFunction;   // default export equivalent

// ES Modules in Node.js (two ways to enable):
// Option 1: rename file to .mjs
// Option 2: add "type": "module" to package.json

// package.json:
// { "type": "module" }
// Then all .js files are treated as ES modules

import { readFile } from 'fs/promises';   // ✅
const data = require('./data');            // ❌ require not available in ESM

// Mixed project: import CommonJS from ESM
import pkg from './commonjs-module.cjs';  // must use .cjs extension
```

---

## 9.2 Destructuring (Review + Advanced)

```javascript
// Renaming + default in destructuring
const { 
  name: fullName = 'Anonymous',
  age: years = 0,
  address: { city = 'Unknown' } = {}   // nested with default for missing object
} = user;

// Destructuring in function params
async function createUser({
  name,
  email,
  role = 'user',
  permissions = []
}) { ... }

// Computed keys in destructuring
const key = 'name';
const { [key]: value } = obj;   // value = obj.name
```

---

## 9.3 Symbols

```javascript
// Symbol: unique, immutable primitive — useful as unique keys
const id   = Symbol('id');    // 'id' is a description, not the value
const id2  = Symbol('id');
id === id2   // false — every Symbol is unique

// As object key
const user = {
  [id]: 123,      // Symbol key
  name: 'Alice'
};
user[id]          // 123
user.name         // 'Alice'
'id' in user      // false — Symbol key is not a string key
Object.keys(user) // ['name'] — Symbols are not enumerated

// Well-known Symbols (customise built-in behaviour)
class Range {
  constructor(start, end) { this.start = start; this.end = end; }

  // Make class iterable with for...of
  [Symbol.iterator]() {
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
[...new Range(1, 5)]  // [1, 2, 3, 4, 5]
```

---

## 9.4 Proxy and Reflect (Advanced)

```javascript
// Proxy: intercept and customise object operations
const handler = {
  get(target, key) {
    console.log(`Getting ${key}`);
    return key in target ? target[key] : `Property ${key} not found`;
  },
  set(target, key, value) {
    if (typeof value !== 'string') throw new TypeError(`${key} must be a string`);
    target[key] = value;
    return true;
  }
};

const user = new Proxy({}, handler);
user.name = 'Alice';      // ✅
user.age = 30;            // ❌ TypeError: age must be a string
user.name                 // logs 'Getting name', returns 'Alice'
user.email                // logs 'Getting email', returns 'Property email not found'

// Practical: validation proxy
function createValidated(schema) {
  return new Proxy({}, {
    set(target, key, value) {
      const validator = schema[key];
      if (validator && !validator(value)) {
        throw new TypeError(`Invalid value for ${key}: ${value}`);
      }
      target[key] = value;
      return true;
    }
  });
}
```

---

## 9.5 Modern String Methods

```javascript
// ES6+
'hello'.includes('ell')        // true
'hello'.startsWith('hel')      // true
'hello'.endsWith('llo')        // true
'ha'.repeat(3)                 // 'hahaha'
'5'.padStart(4, '0')           // '0005'  (zero-pad)
'hi'.padEnd(5, '.')            // 'hi...'
'  hello  '.trimStart()        // 'hello  '
'  hello  '.trimEnd()          // '  hello'
'  hello  '.trim()             // 'hello'

// replaceAll (ES2021)
'a.b.c'.replaceAll('.', '/')   // 'a/b/c'  (replace() only replaces first)

// matchAll (ES2020) — all regex matches with capture groups
const text = 'cat bat sat';
const matches = [...text.matchAll(/[a-z]at/g)];
matches.map(m => m[0])   // ['cat', 'bat', 'sat']

// at() — negative indexing (ES2022)
'hello'.at(-1)     // 'o'
'hello'.at(-2)     // 'l'
```

---

## 9.6 Modern Array Methods (ES2023+)

```javascript
// toSorted, toReversed, toSpliced (non-mutating versions)
const arr = [3, 1, 4, 1, 5];
const sorted = arr.toSorted((a,b) => a - b);  // [1,1,3,4,5]
// arr is unchanged: [3,1,4,1,5]

const reversed = arr.toReversed();   // [5,1,4,1,3], arr unchanged

const spliced = arr.toSpliced(2, 1, 99);  // [3,1,99,1,5], arr unchanged

const updated = arr.with(0, 99);     // [99,1,4,1,5] — replace at index

// findLast / findLastIndex
[1, 2, 3, 4].findLast(n => n % 2 === 0)     // 4
[1, 2, 3, 4].findLastIndex(n => n % 2 === 0) // 3

// Array.from with mapping
Array.from({length: 5}, (_, i) => i * 2)   // [0, 2, 4, 6, 8]
```

---

## 9.7 Logical Assignment Operators (ES2021)

```javascript
// ||= : assign if current value is falsy
let name = '';
name ||= 'Anonymous';   // name = 'Anonymous'

let user = { name: 'Alice' };
user.role ||= 'user';   // adds role if not set or falsy

// &&= : assign if current value is truthy
let config = { debug: true };
config.debug &&= false;   // config.debug = false (only if it was truthy)

// ??= : assign if current value is null or undefined
let cache = null;
cache ??= {};           // cache = {}

user.profile ??= {};    // initialise if missing
user.profile.bio ??= 'No bio yet';
```

---

## 9.8 `structuredClone` — Deep Copy (ES2022)

```javascript
// Problem: spread creates shallow copies
const original = { name: 'Alice', address: { city: 'NYC' } };
const shallow = { ...original };
shallow.address.city = 'LA';
original.address.city;   // 'LA' — mutated the original!

// structuredClone: deep copy (built-in, no library needed)
const deep = structuredClone(original);
deep.address.city = 'LA';
original.address.city;   // still 'NYC' ✅

// Works with: Arrays, Date, Map, Set, RegExp, nested objects
// Doesn't work with: functions, symbols, DOM nodes, class instances
```

---

## 9.9 `globalThis`

```javascript
// Access the global object in any environment
globalThis.setTimeout   // works in browser (window.setTimeout) AND Node.js
globalThis.fetch        // browser: Window.fetch; Node.js 18+: also available
globalThis.process      // Node.js only (undefined in browser)

// Browser:  globalThis === window === self === frames
// Node.js:  globalThis === global
// Worker:   globalThis === self
```

---

## 9.10 Top-Level `await` (ES2022)

In ES modules, you can use `await` at the top level without wrapping in `async`:

```javascript
// config.mjs
const response = await fetch('/api/config');  // ✅ top-level await in ES module
export const config = await response.json();

// index.mjs
import { config } from './config.mjs';  // waits for config to resolve before continuing
```

---

## Key Takeaways

- ES Modules use `import`/`export`; CommonJS uses `require`/`module.exports`. Set `"type": "module"` in `package.json` for ESM in Node.js.
- Dynamic `import()` enables lazy loading and conditional module loading.
- `structuredClone()` for deep copies — no more JSON round-trips or lodash cloneDeep.
- Logical assignment (`||=`, `&&=`, `??=`) enables clean initialisation patterns.
- `Symbol.iterator` makes any object iterable with `for...of` and spread.

---

## Exercises

1. Create a `math.js` module that exports `add`, `subtract`, `multiply`, `divide` as named exports and a `Calculator` class as the default export.
2. Write a `deepMerge(obj1, obj2)` function that deep-merges two objects (hint: use `structuredClone` and recursion).
3. Make a custom `LinkedList` class iterable using `Symbol.iterator`.
4. Write a `cache(fn)` higher-order function that memoises the results of `fn` using a `Map`.
