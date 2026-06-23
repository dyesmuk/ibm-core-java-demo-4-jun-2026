# JavaScript Assessment — MCQ Bank (60 Questions)

> **Instructions:** One correct answer per question unless noted. Covers Modules 01–10. Designed for end-of-training assessment.

---

## Module 01 — Introduction (Q1–Q6)

**Q1.** Which statement about the V8 JavaScript engine is correct?

- A) V8 is used only in Google Chrome
- B) V8 is the engine used in both Chrome and Node.js
- C) V8 compiles JavaScript to Java bytecode
- D) V8 is maintained by Mozilla

**Answer: B**

---

**Q2.** What is the key difference between how Java and JavaScript handle type errors?

- A) JavaScript throws type errors at compile time; Java throws them at runtime
- B) Both languages catch type errors at compile time
- C) Java catches type errors at compile time; JavaScript only catches them at runtime
- D) Neither language ever throws type errors

**Answer: C**

---

**Q3.** Which ECMAScript version introduced `async/await`, arrow functions, and Promises?

- A) ES5 introduced all of them
- B) `async/await` in ES2017; arrow functions and Promises in ES6/ES2015
- C) All three were introduced in ES2020
- D) Arrow functions in ES5; Promises and `async/await` in ES6

**Answer: B**

---

**Q4.** When are ES modules automatically in strict mode?

- A) Only when `'use strict'` is explicitly added
- B) Never — strict mode must always be declared manually
- C) Always — ES modules are always in strict mode
- D) Only in Node.js, not in browsers

**Answer: C**

---

**Q5.** Which of the following APIs is available in Node.js but NOT in a browser?

- A) `fetch`
- B) `setTimeout`
- C) `fs` (file system)
- D) `JSON.parse`

**Answer: C**

---

**Q6.** What does the Node.js REPL stand for?

- A) Run, Execute, Print, Loop
- B) Read, Evaluate, Parse, Log
- C) Read, Eval, Print, Loop
- D) Runtime Execution Protocol Layer

**Answer: C**

---

## Module 02 — Variables & Types (Q7–Q14)

**Q7.** What is the output of the following code?

```javascript
for (var i = 0; i < 3; i++) {
  setTimeout(() => console.log(i), 0);
}
```

- A) `0 1 2`
- B) `3 3 3`
- C) `0 0 0`
- D) `undefined undefined undefined`

**Answer: B**  
*Explanation: `var` is function-scoped, so all callbacks share the same `i`, which is `3` after the loop.*

---

**Q8.** What does `typeof null` return?

- A) `'null'`
- B) `'undefined'`
- C) `'object'`
- D) `'boolean'`

**Answer: C**  
*Explanation: This is a historical bug in JavaScript. `null` is not actually an object.*

---

**Q9.** Which of the following is NOT one of the 8 falsy values in JavaScript?

- A) `0`
- B) `""`
- C) `[]`
- D) `null`

**Answer: C**  
*Explanation: Empty arrays are TRUTHY in JavaScript.*

---

**Q10.** What is the output of `1 + 2 + "3"`?

- A) `"123"`
- B) `"33"`
- C) `6`
- D) `"6"`

**Answer: B**  
*Explanation: Left to right: `1 + 2 = 3`, then `3 + "3" = "33"` (string concatenation).*

---

**Q11.** What does `const user = { name: 'Alice' }; user.name = 'Bob';` do?

- A) Throws a TypeError because `user` is `const`
- B) Successfully changes `user.name` to `'Bob'`
- C) Silently fails and `user.name` stays `'Alice'`
- D) Creates a new object and reassigns `user`

**Answer: B**  
*Explanation: `const` prevents reassigning the binding, not mutating the object.*

---

**Q12.** What is the correct way to check if a value is `NaN`?

- A) `value === NaN`
- B) `value == NaN`
- C) `Number.isNaN(value)`
- D) `isNaN(value)` (legacy function)

**Answer: C**  
*Explanation: `NaN !== NaN` so `===` doesn't work. `Number.isNaN` is preferred over the legacy `isNaN` which coerces its argument.*

---

**Q13.** What is the difference between `undefined` and `null` in JavaScript?

- A) They are completely identical
- B) `undefined` is set by the system (uninitialized); `null` is set intentionally by the programmer
- C) `null` is set by the system; `undefined` is set by the programmer
- D) `undefined` is for objects; `null` is for primitives

**Answer: B**

---

**Q14.** What does `!!"hello"` evaluate to?

- A) `false`
- B) `"hello"`
- C) `0`
- D) `true`

**Answer: D**  
*Explanation: `"hello"` is truthy. `!"hello"` = `false`. `!false` = `true`. Double negation converts to boolean.*

---

## Module 03 — Operators (Q15–Q21)

**Q15.** What does `const result = 0 ?? 'default'` evaluate to?

- A) `'default'`
- B) `0`
- C) `false`
- D) `null`

**Answer: B**  
*Explanation: `??` only triggers on `null` or `undefined`. `0` is neither, so `0` is returned.*

---

**Q16.** What does `user?.profile?.address?.city` return when `user.profile` is `null`?

- A) Throws a `TypeError`
- B) `null`
- C) `undefined`
- D) `''`

**Answer: C**  
*Explanation: Optional chaining returns `undefined` (not `null`) when any part of the chain is `null` or `undefined`.*

---

**Q17.** What is the difference between the spread and rest operators?

- A) They are the same thing with different names
- B) Spread is in function calls/literals (expands); Rest is in function definitions (collects)
- C) Rest is in function calls; Spread is in function definitions
- D) Spread works with arrays only; Rest works with objects only

**Answer: B**

---

**Q18.** What is the output of `"10" - "3"`?

- A) `"10-3"`
- B) `7`
- C) `NaN`
- D) `"7"`

**Answer: B**  
*Explanation: `-` forces numeric conversion. Both strings coerce to numbers.*

---

**Q19.** Which operator should you use instead of `||` when `0` or `false` are valid default values?

- A) `&&`
- B) `??`
- C) `?.`
- D) `!`

**Answer: B**

---

**Q20.** What does `user.profile ??= {}` do?

- A) Always assigns `{}` to `user.profile`
- B) Assigns `{}` only if `user.profile` is `null` or `undefined`
- C) Assigns `{}` if `user.profile` is falsy
- D) Throws an error if `user.profile` already exists

**Answer: B**

---

**Q21.** What is the result of `5 ** 3`?

- A) `15`
- B) `8`
- C) `125`
- D) `53`

**Answer: C**  
*Explanation: `**` is the exponentiation operator. `5³ = 125`.*

---

## Module 04 — Control Flow (Q22–Q27)

**Q22.** What does `switch` use internally to compare cases?

- A) `==` (loose equality)
- B) `===` (strict equality)
- C) `Object.is()`
- D) `.equals()` method

**Answer: B**

---

**Q23.** What is the "guard clause" pattern?

- A) A `try/catch` block protecting sensitive code
- B) Using early returns to handle edge cases first, reducing nesting
- C) Checking user permissions before running code
- D) A `finally` block that always runs

**Answer: B**

---

**Q24.** What is the output of this code?

```javascript
for (const fruit of ['apple', 'banana', 'cherry']) {
  if (fruit === 'banana') continue;
  console.log(fruit);
}
```

- A) `apple banana cherry`
- B) `apple cherry`
- C) `apple`
- D) `banana`

**Answer: B**

---

**Q25.** When should you use `for...of` vs `for...in`?

- A) `for...of` for objects; `for...in` for arrays
- B) `for...of` for arrays and iterables (values); `for...in` for object keys
- C) They are identical — use either
- D) `for...in` is always preferred in modern JavaScript

**Answer: B**

---

**Q26.** What is the benefit of custom Error classes extending `Error`?

- A) They run faster than built-in Error
- B) They enable `instanceof` checks, custom properties (like `statusCode`), and cleaner error handling
- C) They prevent errors from being thrown
- D) They automatically log errors to the console

**Answer: B**

---

**Q27.** What is the "object lookup table" pattern used as an alternative to?

- A) `try/catch`
- B) `for...of` loops
- C) Long `switch` statements
- D) Closures

**Answer: C**

---

## Module 05 — Functions (Q28–Q35)

**Q28.** What is the key difference between function declarations and function expressions?

- A) Function declarations can have parameters; expressions cannot
- B) Function declarations are hoisted; function expressions are not
- C) Function expressions are hoisted; declarations are not
- D) There is no difference

**Answer: B**

---

**Q29.** What does this code output?

```javascript
const obj = {
  name: 'Alice',
  greet: () => {
    console.log(this.name);
  }
};
obj.greet();
```

- A) `'Alice'`
- B) `undefined`
- C) Throws a `TypeError`
- D) `'obj'`

**Answer: B**  
*Explanation: Arrow functions have lexical `this`. At the point of definition, `this` refers to the outer scope (global/module), not `obj`. In strict mode, `this` is `undefined`.*

---

**Q30.** What is a closure?

- A) A function that has no return value
- B) A function that remembers variables from its outer scope even after that scope has finished
- C) A function that is immediately invoked
- D) A function that cannot be reassigned

**Answer: B**

---

**Q31.** What is the difference between `.call()` and `.apply()`?

- A) `.call()` binds permanently; `.apply()` binds temporarily
- B) `.call()` passes arguments as a list; `.apply()` passes arguments as an array
- C) `.apply()` passes arguments as a list; `.call()` passes them as an array
- D) They are identical

**Answer: B**

---

**Q32.** What does `Array.prototype.reduce` return if called on `[5]` with no initial value?

- A) `0`
- B) `undefined`
- C) `5` (the single element, since no accumulation happens)
- D) Throws a `TypeError`

**Answer: C**  
*Explanation: With a single element and no initial value, `reduce` returns that element without calling the callback.*

---

**Q33.** What is the difference between `map()` and `forEach()`?

- A) `map()` mutates the array; `forEach()` does not
- B) `map()` returns a new array; `forEach()` always returns `undefined`
- C) `forEach()` can be chained; `map()` cannot
- D) They are identical

**Answer: B**

---

**Q34.** What does this function return?

```javascript
function multiplier(x) {
  return n => n * x;
}
const triple = multiplier(3);
triple(4);
```

- A) `3`
- B) `4`
- C) `12`
- D) `undefined`

**Answer: C**  
*Explanation: `triple` is a closure over `x = 3`. `triple(4)` returns `4 * 3 = 12`.*

---

**Q35.** Which is the correct way to return an object literal from an arrow function?

- A) `const f = x => { id: x };`
- B) `const f = x => ({ id: x });`
- C) `const f = x => return { id: x };`
- D) `const f = (x) -> { id: x };`

**Answer: B**  
*Explanation: Without parentheses, `{}` is treated as a code block, not an object literal.*

---

## Module 06 — Objects (Q36–Q41)

**Q36.** What does `Object.freeze()` do?

- A) Deep-freezes all nested objects recursively
- B) Prevents new properties from being added and existing ones from being modified (shallow)
- C) Makes the object immutable including nested objects
- D) Converts the object to a string

**Answer: B**

---

**Q37.** What does this code do?

```javascript
const { password, ...safeUser } = user;
```

- A) Creates `safeUser` with ONLY the `password` property
- B) Creates `safeUser` with ALL properties EXCEPT `password`
- C) Throws an error if `password` doesn't exist
- D) Encrypts the password

**Answer: B**

---

**Q38.** Which `Object` method converts an array of `[key, value]` pairs to an object?

- A) `Object.assign()`
- B) `Object.create()`
- C) `Object.fromEntries()`
- D) `Object.entries()`

**Answer: C**

---

**Q39.** What happens when you do `JSON.parse(JSON.stringify(date))` where `date` is a `Date` object?

- A) The result is still a `Date` object
- B) The result is a string representation of the date
- C) Throws an error
- D) Returns `null`

**Answer: B**  
*Explanation: `JSON.stringify` converts `Date` to an ISO string. `JSON.parse` keeps it as a string — it does NOT restore the `Date` type.*

---

**Q40.** What is the difference between `Object.assign({}, a, b)` and `structuredClone(a)`?

- A) They are identical
- B) `Object.assign` creates a shallow merge; `structuredClone` creates a deep copy of a single object
- C) `structuredClone` only works on arrays
- D) `Object.assign` creates a deep copy; `structuredClone` creates a shallow copy

**Answer: B**

---

**Q41.** How do you define a truly private field in a JavaScript class?

- A) `private name = 'Alice';`
- B) `_name = 'Alice';` (convention)
- C) `#name = 'Alice';` (ES2022 class fields)
- D) `const name = 'Alice';` inside the constructor

**Answer: C**

---

## Module 07 — Arrays (Q42–Q48)

**Q42.** What is the output of `[10, 9, 2, 1, 100].sort()`?

- A) `[1, 2, 9, 10, 100]`
- B) `[100, 10, 9, 2, 1]`
- C) `[1, 10, 100, 2, 9]`
- D) `[10, 9, 2, 1, 100]` (unchanged)

**Answer: C**  
*Explanation: Default sort is lexicographic (string sort). `"1" < "10" < "100" < "2" < "9"`.*

---

**Q43.** Which method returns the LAST element matching a condition without modifying the array?

- A) `arr.filter(fn).pop()`
- B) `arr.findLast(fn)` (ES2023)
- C) `arr.lastFind(fn)`
- D) `arr.reverse().find(fn)`

**Answer: B**

---

**Q44.** What is the difference between `slice` and `splice`?

- A) Both mutate the array
- B) `slice` is non-mutating and returns a portion; `splice` mutates the array and can add/remove elements
- C) `splice` is non-mutating; `slice` mutates
- D) `slice` works on strings only; `splice` on arrays only

**Answer: B**

---

**Q45.** What does `[1, null, 2, undefined, 3].filter(Boolean)` return?

- A) `[1, null, 2, undefined, 3]`
- B) `[1, 2, 3]`
- C) `[null, undefined]`
- D) `[false, false]`

**Answer: B**  
*Explanation: `Boolean` is passed as the predicate. It returns `false` for all falsy values (null, undefined), filtering them out.*

---

**Q46.** What does `flatMap` do?

- A) Flattens all nested arrays to a single level
- B) Maps each element and then flattens the result one level
- C) Same as `map` but only for flat arrays
- D) Flattens and then maps

**Answer: B**

---

**Q47.** What is the most efficient way to remove duplicate values from an array?

- A) `arr.filter((v, i) => arr.indexOf(v) === i)`
- B) `[...new Set(arr)]`
- C) `arr.reduce((acc, v) => acc.includes(v) ? acc : [...acc, v], [])`
- D) `arr.unique()`

**Answer: B**  
*Explanation: While A and C work, `Set` is O(n) and the cleanest solution. D doesn't exist.*

---

**Q48.** What does `arr.reduce((acc, item) => { acc[item.id] = item; return acc; }, {})` produce?

- A) An array of ids
- B) A count of items
- C) An object mapping id → item for O(1) lookup
- D) A sorted array

**Answer: C**

---

## Module 08 — Strings (Q49–Q52)

**Q49.** What does `'hello hello'.replace('hello', 'hi')` return?

- A) `'hi hi'`
- B) `'hi hello'`
- C) `'hello hi'`
- D) Throws an error

**Answer: B**  
*Explanation: `replace` without the global regex flag replaces only the FIRST occurrence. Use `replaceAll` for all.*

---

**Q50.** What does `'abc'.at(-1)` return?

- A) `undefined`
- B) `'a'`
- C) `'c'`
- D) `-1`

**Answer: C**  
*Explanation: `.at()` supports negative indices. `-1` is the last character.*

---

**Q51.** What does `'2024-11-15'.split('-')` return?

- A) `['2024', '-', '11', '-', '15']`
- B) `['2024', '11', '15']`
- C) `['20241115']`
- D) `'20241115'`

**Answer: B**

---

**Q52.** Which method checks if a string BEGINS with a specific substring?

- A) `str.includes()`
- B) `str.indexOf() === 0`
- C) `str.startsWith()`
- D) Both B and C are correct

**Answer: D**  
*Explanation: Both work. `startsWith` is more readable and preferred.*

---

## Module 09 — Async JavaScript (Q53–Q57)

**Q53.** What is the output order of this code?

```javascript
console.log('A');
setTimeout(() => console.log('B'), 0);
Promise.resolve().then(() => console.log('C'));
console.log('D');
```

- A) A, B, C, D
- B) A, D, C, B
- C) A, D, B, C
- D) A, C, D, B

**Answer: B**  
*Explanation: Sync first (A, D), then microtasks (C — Promise), then macrotasks (B — setTimeout).*

---

**Q54.** What is the difference between `Promise.all` and `Promise.allSettled`?

- A) `Promise.all` waits for all; `Promise.allSettled` only waits for the first
- B) `Promise.all` rejects immediately if any Promise rejects; `Promise.allSettled` waits for all regardless and never rejects
- C) They are identical
- D) `Promise.allSettled` throws an error if any Promise rejects

**Answer: B**

---

**Q55.** Why does `fetch('/api/users')` NOT throw an error on a 404 response?

- A) `fetch` automatically handles 404 silently
- B) `fetch` only rejects on network failures; HTTP error codes (4xx, 5xx) are considered "successful" responses
- C) 404 is not a real error in REST APIs
- D) You must pass `{ strict: true }` for `fetch` to throw on errors

**Answer: B**

---

**Q56.** What is the problem with this code?

```javascript
async function loadData() {
  const users    = await getUsers();
  const products = await getProducts();
  const orders   = await getOrders();
  return { users, products, orders };
}
```

- A) `async/await` cannot return objects
- B) All three calls are sequential when they could run in parallel, making it slower
- C) `getUsers()` must complete before the function can be declared `async`
- D) There is no problem with this code

**Answer: B**

---

**Q57.** What does `async function fn() { return 42; }` return when called as `fn()`?

- A) `42`
- B) `Promise { 42 }`
- C) `undefined`
- D) `Promise { pending }`

**Answer: B**  
*Explanation: `async` functions always return a Promise. The return value is wrapped in `Promise.resolve(42)`.*

---

## Module 10 — Modules & Modern JS (Q58–Q60)

**Q58.** What is the difference between a named export and a default export?

- A) Named exports are faster; default exports are slower
- B) A module can have many named exports but only ONE default export; named imports use `{}`, default imports don't
- C) Default exports require `{}` on import; named exports don't
- D) Named exports cannot be renamed on import

**Answer: B**

---

**Q59.** What does `structuredClone` do that spread (`{...obj}`) does not?

- A) It creates a reference to the original object
- B) It works on arrays but not objects
- C) It creates a deep copy — nested objects are also copied, not shared
- D) It freezes the cloned object

**Answer: C**

---

**Q60.** What does `"type": "module"` in `package.json` do?

- A) Enables TypeScript support
- B) Makes all `.js` files in the project use ES module syntax (`import`/`export`) instead of CommonJS (`require`)
- C) Only affects files with the `.mjs` extension
- D) Disables the CommonJS `module.exports` system globally

**Answer: B**

---

## Answer Key — Quick Reference

| Q | A | Q | A | Q | A | Q | A | Q | A |
|---|---|---|---|---|---|---|---|---|---|
| 1 | B | 13 | B | 25 | B | 37 | B | 49 | B |
| 2 | C | 14 | D | 26 | B | 38 | C | 50 | C |
| 3 | B | 15 | B | 27 | C | 39 | B | 51 | B |
| 4 | C | 16 | C | 28 | B | 40 | B | 52 | D |
| 5 | C | 17 | B | 29 | B | 41 | C | 53 | B |
| 6 | C | 18 | B | 30 | B | 42 | C | 54 | B |
| 7 | B | 19 | B | 31 | B | 43 | B | 55 | B |
| 8 | C | 20 | B | 32 | C | 44 | B | 56 | B |
| 9 | C | 21 | C | 33 | B | 45 | B | 57 | B |
| 10 | B | 22 | B | 34 | C | 46 | B | 58 | B |
| 11 | B | 23 | B | 35 | B | 47 | B | 59 | C |
| 12 | C | 24 | B | 36 | B | 48 | C | 60 | B |

---

## Topic Coverage Map

| Module | Topic | Questions |
|---|---|---|
| 01 | Introduction & Setup | Q1–Q6 |
| 02 | Variables & Types | Q7–Q14 |
| 03 | Operators | Q15–Q21 |
| 04 | Control Flow | Q22–Q27 |
| 05 | Functions | Q28–Q35 |
| 06 | Objects | Q36–Q41 |
| 07 | Arrays | Q42–Q48 |
| 08 | Strings | Q49–Q52 |
| 09 | Async JavaScript | Q53–Q57 |
| 10 | Modules & Modern JS | Q58–Q60 |
