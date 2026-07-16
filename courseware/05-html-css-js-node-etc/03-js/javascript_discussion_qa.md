# JavaScript — Discussion Questions & Answers

---

## Fundas

**1. What is JavaScript?**

JavaScript is a dynamic, interpreted, single-threaded programming language that runs in browsers and on servers (via Node.js). It is the only language that runs natively in web browsers.

**2. Is JavaScript compiled or interpreted?**

JavaScript is JIT-compiled (Just-In-Time) at runtime by engines like V8. The source code is parsed, compiled to bytecode, and optimised on the fly — not pre-compiled like Java.

**3. What is the difference between Java and JavaScript?**

Despite the name, they are entirely different languages. Java is statically typed, compiled to bytecode, and class-based. JavaScript is dynamically typed, interpreted, and prototype-based. The name similarity is marketing, not lineage.

**4. What are the data types in JavaScript?**

Seven primitives: `string`, `number`, `bigint`, `boolean`, `undefined`, `null`, `symbol` — plus one object type. Everything that is not a primitive (arrays, functions, dates) is an object.

**5. What is the difference between `null` and `undefined`?**

`undefined` means a variable was declared but never assigned a value — set by the JavaScript engine. `null` means intentional absence of a value — set explicitly by the programmer. Both are falsy but `typeof null` returns `'object'` (a historical bug).

**6. What is `typeof` and what are its quirks?**

`typeof` returns a string indicating the type of a value. Known quirk: `typeof null === 'object'` (should be `'null'`) and `typeof function(){} === 'function'` (functions are objects but get their own type). Use `Array.isArray()` to check for arrays since `typeof []` returns `'object'`.

**7. What is the difference between `==` and `===`?**

`==` (loose equality) performs type coercion before comparing — `"5" == 5` is `true`. `===` (strict equality) requires both value and type to match — `"5" === 5` is `false`. Always use `===` to avoid unexpected coercion bugs.

**8. What is type coercion?**

Type coercion is JavaScript's automatic conversion of a value from one type to another when an operator is applied to mismatched types. Example: `"5" + 3 = "53"` (number coerced to string), `"5" - 3 = 2` (string coerced to number). It is a common source of bugs — use explicit conversions instead.

**9. What are truthy and falsy values?**

Falsy values are the eight values that evaluate to `false` in a boolean context: `false`, `0`, `-0`, `0n`, `""`, `null`, `undefined`, `NaN`. Everything else is truthy — including `[]`, `{}`, and `"0"`. This matters for `if` conditions and short-circuit expressions.

**10. What is the difference between `let`, `const`, and `var`?**

`var` is function-scoped and hoisted — avoid it. `let` is block-scoped and can be reassigned. `const` is block-scoped and cannot be reassigned (though the value it points to can be mutated if it's an object). Use `const` by default, `let` when reassignment is needed.

---

## Concepts

**11. What is hoisting?**

Hoisting is JavaScript's behaviour of moving variable and function declarations to the top of their scope before execution. Function declarations are fully hoisted (callable before they appear). `var` is hoisted but initialised as `undefined`. `let` and `const` are hoisted but not initialised — accessing them before declaration throws a `ReferenceError` (Temporal Dead Zone).

**12. What is the Temporal Dead Zone (TDZ)?**

The TDZ is the period between when a `let` or `const` variable is hoisted and when it is initialised. Accessing the variable during this period throws a `ReferenceError`. This is why `let`/`const` are considered "safer" than `var`.

**13. What is a closure?**

A closure is a function that retains access to variables from its outer (enclosing) scope even after that scope has finished executing. Closures are the foundation of module patterns, factory functions, memoization, and private state in JavaScript.

**14. What is scope in JavaScript?**

Scope determines where a variable is accessible. JavaScript has global scope, function scope, and block scope (with `let`/`const`). Variables defined in an inner scope can access the outer scope (scope chain), but not vice versa.

**15. What is the scope chain?**

When a variable is not found in the current scope, JavaScript looks up the scope chain — checking each enclosing scope until it reaches the global scope. If not found anywhere, a `ReferenceError` is thrown. This is how closures access outer variables.

**16. What is `this` in JavaScript?**

`this` refers to the object that is calling the function. Its value depends on how the function is called — not where it is defined. In a method call `obj.fn()`, `this` is `obj`. In a plain function call, `this` is `undefined` (strict mode) or `global`. Arrow functions do not have their own `this` — they inherit from the enclosing scope.

**17. What is the difference between regular functions and arrow functions?**

Arrow functions have shorter syntax and do not have their own `this`, `arguments`, or `prototype`. They inherit `this` from the enclosing scope (lexical `this`). Use arrow functions for callbacks; use regular functions for object methods where you need `this` to refer to the object.

**18. What is the difference between `call()`, `apply()`, and `bind()`?**

All three explicitly set `this` for a function. `call(thisArg, arg1, arg2)` calls the function immediately with arguments listed. `apply(thisArg, [arg1, arg2])` calls immediately with arguments as an array. `bind(thisArg)` returns a new function with `this` permanently bound — does not call immediately.

**19. What is the prototype chain?**

Every JavaScript object has a hidden `[[Prototype]]` link to another object. When you access a property, JavaScript looks on the object first, then walks up the prototype chain until it finds it or reaches `null`. This is how inheritance works in JavaScript — classes are syntactic sugar over this mechanism.

**20. What is the difference between `Object.create()` and class-based inheritance?**

`Object.create(proto)` directly sets the prototype of a new object. Class syntax (`class Dog extends Animal`) does the same thing but with a familiar OOP syntax that compiles to prototype-based code. Both achieve prototype chain inheritance — classes are just cleaner syntax.

**21. What is event bubbling and capturing?**

When a DOM event fires, it first travels down from the root to the target element (capturing phase), then bubbles back up to the root (bubbling phase). By default, event listeners use bubbling. Use `event.stopPropagation()` to prevent further propagation and `addEventListener('click', fn, true)` for the capturing phase.

**22. What is event delegation?**

Event delegation attaches a single event listener to a parent element instead of individual listeners on each child. When a child is clicked, the event bubbles up to the parent listener. It is more memory-efficient for dynamic lists. Example: one click listener on `<ul>` handles clicks on all `<li>` items.

---

## ES6+ Features

**23. What are template literals?**

Template literals use backticks and `${}` for string interpolation and multi-line strings. Example: `` `Hello, ${name}! You have ${count} messages.` ``. They are cleaner than string concatenation and support any JavaScript expression inside `${}`.

**24. What is destructuring?**

Destructuring extracts values from objects or arrays into named variables in a single statement. Object: `const { name, age } = user`. Array: `const [first, second] = colors`. Supports defaults, renaming, and nesting. It is the most common pattern for reading props in React and parameters in Node.js.

**25. What is the spread operator `...`?**

The spread operator expands an iterable (array or object) into individual elements. In arrays: `[...a, ...b]` merges arrays. In objects: `{ ...defaults, ...overrides }` merges objects (right side wins on conflict). Also used in function calls: `Math.max(...numbers)`.

**26. What is the rest parameter `...`?**

Rest collects remaining function arguments into an array. `function sum(first, ...rest)` — `rest` is an array of all arguments after `first`. Same `...` syntax as spread but in a function definition rather than a call or literal. Must be the last parameter.

**27. What is optional chaining `?.`?**

Optional chaining safely accesses nested properties — returns `undefined` instead of throwing a `TypeError` if any part of the chain is `null` or `undefined`. Example: `user?.address?.city` returns `undefined` if `user` or `address` is null/undefined.

**28. What is the nullish coalescing operator `??`?**

`??` returns the right side only when the left side is `null` or `undefined` — not for other falsy values like `0` or `""`. Example: `const port = config.port ?? 3000` correctly keeps `0` as a valid port, unlike `||` which would replace it with `3000`.

**29. What are Promises?**

A Promise represents a value that will be available in the future. It has three states: pending, fulfilled, resolved. Consumed with `.then()`, `.catch()`, `.finally()`. Promises solve callback hell by allowing async operations to be chained linearly instead of nested.

**30. What is `async/await`?**

`async/await` is syntactic sugar over Promises that makes asynchronous code look synchronous. An `async` function always returns a Promise. `await` pauses execution inside the function until the Promise resolves. Use `try/catch` for error handling instead of `.catch()`.

**31. What is the difference between `Promise.all` and `Promise.allSettled`?**

`Promise.all([p1, p2, p3])` runs all promises in parallel and resolves when ALL resolve — fails fast if any reject. `Promise.allSettled` waits for all to complete regardless of success or failure and returns the status of each. Use `allSettled` when you want results from all even if some fail.

**32. What is `Promise.race` and `Promise.any`?**

`Promise.race` resolves/rejects with whichever Promise settles first (success or error). `Promise.any` resolves with the first Promise that **succeeds** — only rejects if ALL fail. Use `race` for timeouts; use `any` when you have multiple sources and want the fastest successful one.

**33. What is the event loop?**

The event loop is JavaScript's mechanism for handling asynchronous operations in a single-threaded environment. It continuously checks: if the call stack is empty, it moves tasks from the callback queue (macrotasks) or microtask queue (Promises) into the stack for execution. Microtasks (Promises) always run before macrotasks (setTimeout).

**34. What is the difference between microtasks and macrotasks?**

Microtasks (`Promise.then`, `queueMicrotask`) run immediately after the current task completes, before the browser renders or runs the next macrotask. Macrotasks (`setTimeout`, `setInterval`, I/O) run in the next iteration of the event loop. Output of `console.log(1); setTimeout(()=>log(2)); Promise.resolve().then(()=>log(3)); log(4)` is `1, 4, 3, 2`.

---

## Functions & Scope

**35. What is an IIFE?**

An Immediately Invoked Function Expression is a function that is defined and called immediately: `(function() { ... })()`. Used to create a private scope and avoid polluting the global namespace — common in pre-module JavaScript. In modern code, ES modules handle this automatically.

**36. What is a higher-order function?**

A higher-order function takes other functions as arguments or returns a function. Examples: `Array.map()`, `Array.filter()`, `Array.reduce()`, and `setTimeout()`. They enable functional programming patterns — composing behaviour from small, reusable functions.

**37. What is function currying?**

Currying transforms a function that takes multiple arguments into a series of functions each taking one argument. `add(2)(3)` instead of `add(2, 3)`. It enables partial application — fixing some arguments to create specialised functions.

**38. What is memoization?**

Memoization is an optimisation that caches a function's return value for given inputs and returns the cached result on repeated calls with the same inputs. It trades memory for speed — useful for expensive pure functions called with the same arguments repeatedly.

---

## Arrays & Objects

**39. What is the difference between `map()`, `filter()`, and `reduce()`?**

`map()` transforms every element and returns a new array of the same length. `filter()` keeps elements that pass a test and returns a shorter array. `reduce()` accumulates all elements into a single value (sum, object, nested structure). All three are non-mutating and return new arrays/values.

**40. What is the difference between `find()` and `filter()`?**

`find()` returns the first element that matches the condition (or `undefined`). `filter()` returns an array of all matching elements. Use `find()` when looking for a single item by ID; use `filter()` when you need all matching items.

**41. What is the difference between `forEach()` and `map()`?**

`forEach()` iterates for side effects and always returns `undefined` — it cannot be chained. `map()` returns a new transformed array and can be chained. Use `forEach` for logging or DOM manipulation; use `map` when you need the transformed result.

**42. What does `Array.sort()` do by default and what is the gotcha?**

By default, `sort()` converts elements to strings and sorts lexicographically. `[10, 9, 2, 100].sort()` returns `[10, 100, 2, 9]` — wrong for numbers. Always provide a comparator: `.sort((a, b) => a - b)` for ascending numbers.

**43. What is the difference between shallow copy and deep copy?**

A shallow copy creates a new object/array but nested references still point to the original objects. `{ ...obj }` and `[...arr]` are shallow copies — changing nested objects affects the original. A deep copy duplicates everything recursively — use `structuredClone(obj)` (ES2022) for a built-in deep copy.

**44. What is `Object.freeze()`?**

`Object.freeze()` prevents adding, removing, or changing properties on an object. However, it is shallow — nested objects are not frozen. Useful for configuration constants. In strict mode, attempts to modify a frozen object throw a `TypeError`.

**45. What is the difference between `Object.keys()`, `Object.values()`, and `Object.entries()`?**

`Object.keys(obj)` returns an array of the object's own enumerable property names. `Object.values(obj)` returns the values. `Object.entries(obj)` returns `[key, value]` pairs. All three ignore prototype properties and work only on own enumerable properties.

---

## Classes & Prototypes

**46. What are ES6 classes?**

ES6 classes are syntactic sugar over JavaScript's prototype-based inheritance. They provide a cleaner syntax for constructor functions and prototype methods. Under the hood, `class Dog extends Animal` sets up the prototype chain exactly as manual prototype assignment would.

**47. What is the difference between `class` methods and prototype methods?**

Methods defined inside a `class` body are added to the prototype and shared across all instances — memory efficient. Arrow functions defined as class fields are per-instance — each instance gets its own copy. Use prototype methods for regular methods; use class fields for event handlers that need a stable `this`.

**48. What are private class fields (`#`)?**

Private fields (prefixed with `#`) are truly private — inaccessible from outside the class, even in subclasses. They were introduced in ES2022. `this.#salary` can only be read or written inside the class definition.

**49. What is `super()` and when is it required?**

`super()` calls the parent class constructor. In a subclass (one that uses `extends`), you must call `super()` before accessing `this` — otherwise a `ReferenceError` is thrown. `super.method()` calls a parent class method from within an overriding method.

---

## Modules

**50. What is the difference between CommonJS and ES Modules?**

CommonJS (`require`/`module.exports`) is Node.js's original module system — synchronous, runs at runtime. ES Modules (`import`/`export`) is the official standard — static (analysed at parse time), async, works in both browsers and modern Node.js. ES Modules enable tree shaking; CommonJS does not.

**51. What is the difference between named and default exports?**

Named exports: `export const fn = () => {}` — imported with exact name in `{}`: `import { fn } from './module'`. Default export: `export default fn` — imported with any name: `import myFn from './module'`. A module can have many named exports but only one default.

**52. What is dynamic import?**

Dynamic `import()` returns a Promise and loads a module lazily at runtime rather than at parse time. `const module = await import('./feature.js')`. Used for code splitting and conditional loading — load a heavy library only when the user actually needs it.

---

## Error Handling

**53. What is the difference between `throw` and `return` in error handling?**

`return` exits a function normally with a value. `throw` exits immediately and propagates an error up the call stack until caught by a `try/catch`. Use `throw new Error('message')` for unexpected failures; use `return` for expected absence of a value.

**54. What is the purpose of `finally` in try/catch/finally?**

`finally` runs after the `try` and `catch` blocks regardless of whether an error was thrown. Use it for cleanup that must always happen — closing a database connection, hiding a loading spinner, clearing a timer. It runs even if the `catch` block re-throws.

**55. What is a custom error class?**

You can extend `Error` to create domain-specific errors with additional properties: `class NotFoundError extends Error { constructor(msg) { super(msg); this.statusCode = 404; } }`. This allows `instanceof` checks in `catch` blocks to handle different error types differently.

---

## Real Time

**56. Variable is `undefined` — what are the possible reasons?**

(1) Declared but not assigned, (2) function returned nothing (implicit `undefined`), (3) accessing a property that doesn't exist on an object, (4) accessing an array index that doesn't exist, (5) function parameter not passed. Check with `console.log(typeof x)` to distinguish from a `ReferenceError`.

**57. `NaN` appearing in calculations — why and how to fix?**

`NaN` (Not a Number) appears when arithmetic involves a non-numeric value — `parseInt("abc")` or `undefined + 1`. It is contagious — any operation with `NaN` produces `NaN`. Check with `Number.isNaN(value)` (not `isNaN()` which coerces). Fix by validating inputs before arithmetic.

**58. `0.1 + 0.2 !== 0.3` — why?**

JavaScript uses IEEE 754 double-precision floating point. Some decimal fractions cannot be represented exactly in binary, causing tiny rounding errors. Fix for display: `(0.1 + 0.2).toFixed(2)`. Fix for financial calculations: work in integers (paise, cents) and divide for display.

**59. `this` is `undefined` inside a method — why?**

When a method is extracted from an object and called as a plain function, it loses its `this` binding. `const fn = obj.method; fn()` — `this` is `undefined` in strict mode. Fix: use an arrow function, `bind()`, or pass the method directly without extraction.

**60. `async/await` but still getting a Promise object — why?**

You forgot to `await` the function call. `const data = fetchData()` returns a Promise; `const data = await fetchData()` returns the resolved value. Also ensure the calling function is itself `async`, otherwise `await` is a syntax error.

**61. Array mutation causing bugs in React/Angular state — why?**

`push()`, `splice()`, `sort()` mutate the original array — frameworks compare references to detect changes. If you mutate in place, the reference stays the same and no re-render happens. Always return a new array: `[...arr, item]`, `.filter()`, `.map()` instead of mutating.

**62. Callback function not receiving expected arguments — why?**

Often caused by calling the function instead of passing it: `btn.addEventListener('click', handleClick())` calls `handleClick` immediately and passes its return value as the listener. The correct form is `btn.addEventListener('click', handleClick)` — passing the reference.

---

## More

**63. What is `use strict`?**

`'use strict'` at the top of a file or function enables strict mode — a safer subset of JavaScript. It disallows undeclared variables, disables `with`, catches silent errors (like assigning to read-only properties), and fixes confusing `this` behaviour. ES modules are always in strict mode automatically.

**64. What is the difference between `setTimeout` and `setInterval`?**

`setTimeout(fn, ms)` runs the function once after a delay. `setInterval(fn, ms)` runs it repeatedly every `ms` milliseconds. Both return a timer ID. Use `clearTimeout(id)` and `clearInterval(id)` to cancel. For repeating tasks, `setTimeout` inside the callback is often more reliable than `setInterval`.

**65. What is a WeakMap and WeakSet?**

`WeakMap` holds key-value pairs where keys must be objects — if the key object is garbage collected, the entry is automatically removed. `WeakSet` holds objects with the same auto-cleanup behaviour. Both are useful for storing metadata about objects without preventing garbage collection.

**66. What is `Symbol` and when would you use it?**

`Symbol()` creates a guaranteed unique primitive value — no two symbols are ever equal. Used as unique object keys to avoid name collisions (e.g. in libraries), and for well-known symbols like `Symbol.iterator` that customise built-in behaviour. Symbols are not enumerable in `for...in` or `Object.keys()`.

**67. What is a generator function?**

A generator function (`function*`) can pause execution with `yield` and resume later. It returns an iterator. Used for lazy sequences, infinite data streams, and cooperative multitasking. `async/await` is built on generators internally.

**68. What is the difference between `for...of` and `for...in`?**

`for...of` iterates over the values of an iterable (array, string, Map, Set, generator). `for...in` iterates over the enumerable property keys of an object, including inherited ones — use `hasOwnProperty` guard or `Object.keys()` instead. Never use `for...in` on arrays.

**69. What is `structuredClone()`?**

`structuredClone()` (ES2022) creates a true deep copy of an object — nested objects, arrays, `Date`, `Map`, `Set` are all cloned recursively. It replaces the old `JSON.parse(JSON.stringify(obj))` trick, which loses `Date` types and can't handle circular references.

**70. What is the difference between `localStorage`, `sessionStorage`, and cookies?**

`localStorage` persists data with no expiry until explicitly cleared. `sessionStorage` clears when the browser tab is closed. Cookies are sent to the server with every HTTP request and can have expiry dates and `httpOnly`/`Secure` flags. For client-only storage use `localStorage`/`sessionStorage`; for server-accessible auth tokens use `httpOnly` cookies.

**71. What is `JSON.stringify` and `JSON.parse`?**

`JSON.stringify(obj)` converts a JavaScript object to a JSON string for storage or HTTP transmission. `JSON.parse(str)` converts a JSON string back to an object. Gotcha: `Date` objects become strings after stringify/parse — you must manually convert them back with `new Date(str)`.

**72. What is a Set and when to use it?**

A `Set` is a collection of unique values — duplicates are automatically ignored. `new Set([1, 2, 2, 3])` creates `{1, 2, 3}`. Use it to deduplicate arrays: `[...new Set(arr)]`. It also has O(1) `has()` lookup, faster than `array.includes()` for large collections.

**73. What is a Map and how does it differ from a plain object?**

A `Map` holds key-value pairs where keys can be any type (not just strings). It maintains insertion order, has a `.size` property, and is directly iterable. Plain objects only support string/Symbol keys and don't guarantee order. Use `Map` when keys are non-strings or when you need ordered iteration.

**74. What is the difference between `==` and `Object.is()`?**

`Object.is(a, b)` is like `===` but handles two edge cases: `Object.is(NaN, NaN)` is `true` (unlike `===` which is `false`), and `Object.is(+0, -0)` is `false` (unlike `===` which is `true`). Used internally by React and frameworks for change detection.

**75. What is tail call optimisation?**

Tail call optimisation (TCO) allows a recursive call in tail position (the last action of a function) to reuse the current stack frame instead of creating a new one — preventing stack overflow for deep recursion. JavaScript specifies TCO in ES6 but most engines (V8) have not fully implemented it. Use loops or trampolining for deep recursion.

---

## Answer Summary Table

| # | Question (short) | Key answer |
|---|---------|------------|
| 1 | What is JS? | Dynamic, interpreted, single-threaded language |
| 4 | Data types | 7 primitives + object |
| 5 | null vs undefined | undefined = engine; null = programmer |
| 7 | == vs === | Loose (coerces) vs strict (no coercion) |
| 9 | Truthy/falsy | 8 falsy values; everything else truthy |
| 10 | let/const/var | Block vs function scope; const = no reassign |
| 11 | Hoisting | Declarations moved to top of scope |
| 13 | Closure | Function retains outer scope access |
| 16 | this | Depends on how function is called, not where defined |
| 17 | Arrow vs regular | Arrow has no own this; inherits lexically |
| 27 | Optional chaining | `?.` returns undefined instead of throwing |
| 28 | Nullish coalescing | `??` — only triggers on null/undefined |
| 29 | Promise | Future value; pending/fulfilled/rejected |
| 30 | async/await | Syntactic sugar over Promises |
| 33 | Event loop | Single-threaded async via call stack + queues |
| 34 | Micro vs macro tasks | Promises before setTimeout |
| 39 | map/filter/reduce | Transform / keep / accumulate |
| 43 | Shallow vs deep copy | `{...}` shallow; `structuredClone` deep |
| 50 | CJS vs ESM | require/module.exports vs import/export |
| 63 | use strict | Safer JS subset; auto-applied in modules |
| 69 | structuredClone | True deep copy built-in |
