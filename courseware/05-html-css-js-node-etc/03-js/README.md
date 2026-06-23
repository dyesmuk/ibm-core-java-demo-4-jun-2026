# JavaScript Courseware
### IBM Cloud Full Stack Training — JavaScript Track

> **Audience:** Java developers stepping into full-stack development.  
> **Prerequisite:** Core Java (covered in a separate module).  
> **What's NOT here:** Node.js APIs, Express.js, npm ecosystem — those are in the Node.js & Express courseware.

---

## Courseware Structure

| Module | Topic | Key Concepts |
|---|---|---|
| 01 | Introduction | JS vs Java, V8 engine, ECMAScript versions, environment setup |
| 02 | Variables & Types | `let`/`const`/`var`, 8 data types, type coercion, truthy/falsy |
| 03 | Operators | Arithmetic, `===`, `??`, `?.`, spread, rest, logical assignment |
| 04 | Control Flow | if/else, switch, loops, `for...of`, guard clauses, custom errors |
| 05 | Functions | 4 syntaxes, closures, `this`, HOFs, `map`/`filter`/`reduce` |
| 06 | Objects | Literals, destructuring, `Object.*` methods, classes, JSON |
| 07 | Arrays | All array methods, Map/Set, practical patterns |
| 08 | Strings | All string methods, regex basics, practical patterns |
| 09 | Async JavaScript | Event loop, Promises, `async/await`, `fetch`, error patterns |
| 10 | Modules & Modern JS | ES modules, `structuredClone`, generators, top-level await |
| 11 | MCQ Assessment | 60 questions covering all modules with answer key |

---

## How to Use This Courseware

### For Trainers
- Each module is a standalone markdown file — render in GitHub or any markdown viewer.
- Modules 01–04 form the foundation (~Day 1).
- Modules 05–08 are the core (~Day 2).
- Modules 09–10 are advanced (~Day 3).
- Use Module 11 (MCQ) for the end-of-section assessment.

### For Trainees
- Read the module, run every code snippet in the Node.js REPL or a `.js` file.
- Answer the **Self-Check Questions** at the end of each module before moving on.
- The MCQ assessment in Module 11 tests everything — study the "Key Takeaways" sections.

### Quick Environment Setup

```bash
# Verify Node.js is installed
node --version   # should be v20.x.x

# Run a JS file
node filename.js

# Start the REPL
node
```

---

## Common Gotchas for Java Developers

These will catch you if you're not expecting them. Know them before the MCQ.

| Gotcha | Why It Trips Java Devs |
|---|---|
| `typeof null === 'object'` | Java `null` has no type. JS `typeof null` returns `'object'` (historical bug) |
| `[] == false` but `if ([])` executes | Empty array is falsy in `==` coercion but TRUTHY in boolean context |
| `0.1 + 0.2 !== 0.3` | IEEE 754 floating point — same in Java, but Java hides it more |
| `NaN !== NaN` | Use `Number.isNaN()`, not `===` |
| `var` leaks out of blocks | Java block scope is the norm; `var` only respects function scope |
| Arrow functions have no `this` | Java methods always have `this`. Arrow functions inherit `this` from outer scope |
| `sort()` is lexicographic | Default sort converts to strings — always pass comparator for numbers |
| `const` object is mutable | `const` prevents reassignment, not mutation |
| `undefined` vs `null` | Java has only `null`. JS has two "no value" states |
| `JSON.parse` loses `Date` type | `Date` serializes to string; does NOT deserialize back |

---

## Key Patterns to Know for Assessment

```javascript
// 1. Safe default with nullish coalescing
const port = config.port ?? 3000;

// 2. Safe deep access
const city = user?.address?.city ?? 'Unknown';

// 3. Async parallel execution
const [users, orders] = await Promise.all([getUsers(), getOrders()]);

// 4. Remove duplicates
const unique = [...new Set(arr)];

// 5. Build lookup map
const byId = users.reduce((acc, u) => ({ ...acc, [u.id]: u }), {});

// 6. Destructure with defaults
const { name = 'Anonymous', role = 'user' } = user ?? {};

// 7. Guard clause pattern
function fn(x) {
  if (!x) return null;
  if (!x.isValid) return null;
  return process(x);
}

// 8. Non-mutating array update (React/state management)
const updated = users.map(u => u.id === id ? { ...u, ...changes } : u);

// 9. Strip property before sending to client
const { password, ...safeUser } = user;

// 10. Fetch with error handling
const response = await fetch(url);
if (!response.ok) throw new Error(`HTTP ${response.status}`);
const data = await response.json();
```

---

## Suggested Study Schedule

| Day | Modules | Focus |
|---|---|---|
| Day 1 AM | 01, 02 | Setup, variables, types, coercion |
| Day 1 PM | 03, 04 | Operators, control flow, error classes |
| Day 2 AM | 05 | Functions, closures, `this`, HOFs |
| Day 2 PM | 06, 07 | Objects + all methods, Arrays + all methods |
| Day 3 AM | 08 | Strings + all methods |
| Day 3 PM | 09, 10 | Async, Promises, `async/await`, modules |
| Assessment | 11 | MCQ — 60 questions |

---

*This courseware is part of the IBM Cloud Full Stack Training program. It is designed to be reusable independently for JavaScript-only training.*
