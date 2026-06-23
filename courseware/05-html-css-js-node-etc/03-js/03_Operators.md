# JavaScript Courseware — Module 03: Operators

---

## 3.1 Arithmetic Operators

```javascript
10 + 3    // 13
10 - 3    // 7
10 * 3    // 30
10 / 3    // 3.3333...  (no integer division — always floating point)
10 % 3    // 1  (remainder — same as Java)
2 ** 10   // 1024  (exponentiation, ES2016 — Java uses Math.pow)

// Integer division workaround
Math.floor(10 / 3)    // 3  (rounds toward -Infinity)
Math.trunc(10 / 3)    // 3  (truncates toward zero)
Math.trunc(-7 / 2)    // -3  (floor would give -4)

// Increment / Decrement (same as Java)
let i = 5;
i++;    // post-increment: use 5, then i becomes 6
++i;    // pre-increment: i becomes 7, then use 7
i--;    // post-decrement: use 7, then i becomes 6
--i;    // pre-decrement: i becomes 5, then use 5

// Compound assignment
let x = 10;
x += 5;    // x = 15
x -= 3;    // x = 12
x *= 2;    // x = 24
x /= 4;    // x = 6
x **= 2;   // x = 36
x %= 10;   // x = 6
```

### Floating-Point Precision — Know This

```javascript
0.1 + 0.2    // 0.30000000000000004  — NOT 0.3!
0.1 + 0.2 === 0.3   // false

// This is IEEE 754 double-precision — same in Java, Python, C
// Fix for display:
(0.1 + 0.2).toFixed(2)   // "0.30" (returns a string)

// Fix for financial calculations: work in integers (cents, paise)
const price1 = 10;   // paise
const price2 = 20;   // paise
const total = (price1 + price2) / 100;  // ₹0.30  ✅
```

---

## 3.2 Comparison Operators

```javascript
// Always use === and !==
5 === 5           // true
5 === "5"         // false  (different types)
NaN === NaN       // false  (NaN is never equal to anything, including itself)

5 !== 10          // true
5 !== "5"         // true

// Relational (same as Java)
5 > 3    // true
5 >= 5   // true
3 < 5    // true
3 <= 3   // true

// String comparison (lexicographic — like Java)
"apple" < "banana"  // true  ('a' < 'b')
"10" > "9"          // false ← because "1" < "9" lexicographically
10 > 9              // true  ← use numbers for numeric comparison
```

---

## 3.3 Logical Operators

### Short-Circuit Behaviour — Very Important in JS

Unlike Java where `&&` and `||` return booleans, **in JavaScript they return one of their operands**:

```javascript
// && — returns the FIRST falsy value, or the LAST value if all are truthy
true && true       // true
true && false      // false
"hello" && 42      // 42      (both truthy — returns last)
null && "hello"    // null    (first is falsy — short-circuits, returns null)
0 && "hello"       // 0       (0 is falsy)

// || — returns the FIRST truthy value, or the LAST value if all are falsy
true || false      // true
false || "hello"   // "hello" (returns first truthy)
null || "default"  // "default"
0 || 42            // 42

// ! (NOT)
!true    // false
!false   // true
!0       // true
!!"hi"   // true  (double negation = convert to boolean)
```

### Short-Circuit for Guard Checks

```javascript
const user = null;
const name = user && user.name;  // null — doesn't crash accessing user.name
const port = process.env.PORT || 3000;  // default port

// Problem with || for defaults: it triggers on ANY falsy value
const count = 0;
const display = count || 'No items';  // 'No items' — WRONG! 0 is valid
```

---

## 3.4 Nullish Coalescing `??` (ES2020) — Use This for Defaults

`??` returns the right side only when the left side is **`null` or `undefined`** — not other falsy values like `0` or `""`:

```javascript
null ?? "default"       // "default"
undefined ?? "default"  // "default"
0 ?? "default"          // 0      ← 0 is valid, not replaced
"" ?? "default"         // ""     ← empty string is valid
false ?? "default"      // false  ← valid

// Compare with || (which triggers on ALL falsy)
0 || "default"    // "default"  ← replaces 0 (probably a bug)
0 ?? "default"    // 0          ← correctly keeps 0

// Real-world config defaults
function getConfig(userConfig) {
  return {
    timeout: userConfig.timeout ?? 5000,  // 0 timeout is valid
    retries: userConfig.retries ?? 3,
    debug: userConfig.debug ?? false       // explicitly false is valid
  };
}
```

> **Rule:** Use `??` for defaults (not `||`) unless you specifically want to replace `0`, `""`, and `false` too.

---

## 3.5 Optional Chaining `?.` (ES2020) — No More Null Crashes

Access nested properties safely without `null`/`undefined` crashes:

```javascript
const user = {
  profile: {
    address: { city: 'Bengaluru' }
  }
};

// Without optional chaining — verbose and ugly
const city1 = user && user.profile && user.profile.address && user.profile.address.city;

// With optional chaining — clean
const city2 = user?.profile?.address?.city;    // 'Bengaluru'

// When anything in the chain is null/undefined — returns undefined, no error
const country = user?.profile?.address?.country;   // undefined
const zip = user?.settings?.zip;                   // undefined (settings missing)

// Optional chaining with method calls
const upper = user?.getName?.();    // calls getName() only if it exists

// Optional chaining with array/bracket access
const firstRole = user?.roles?.[0];

// Combine with ?? for clean defaults
const city = user?.profile?.address?.city ?? 'Unknown';
const zip  = user?.profile?.address?.zip  ?? 'N/A';
```

---

## 3.6 Ternary Operator

```javascript
const age = 20;
const status = age >= 18 ? 'Adult' : 'Minor';

// Chained ternary for grades (keep it readable)
const grade = score >= 90 ? 'A' :
              score >= 80 ? 'B' :
              score >= 70 ? 'C' : 'F';

// Common in JSX (React)
const buttonClass = isLoading ? 'btn btn-disabled' : 'btn btn-primary';
```

---

## 3.7 Spread Operator `...`

Spread **expands** an iterable into individual elements:

```javascript
// In function calls
Math.max(...[1, 5, 3, 9, 2])   // 9

// In array literals
const a = [1, 2, 3];
const b = [4, 5, 6];
const combined = [...a, ...b];           // [1, 2, 3, 4, 5, 6]
const withExtra = [0, ...a, 3.5, ...b]; // [0, 1, 2, 3, 3.5, 4, 5, 6]

// Shallow copy of array
const copy = [...a];   // [1, 2, 3] — new array, not a reference

// In object literals (merge / override)
const base = { x: 1, y: 2 };
const extended  = { ...base, z: 3 };        // { x: 1, y: 2, z: 3 }
const overridden = { ...base, x: 99 };      // { x: 99, y: 2 }  (later key wins)
const merged = { ...base, ...{ y: 10, z: 3 } }; // { x: 1, y: 10, z: 3 }
```

---

## 3.8 Rest Parameters `...`

Rest **collects** remaining arguments into an array (same syntax `...`, different context):

```javascript
// In function parameters — collects remaining args
function sum(...numbers) {
  return numbers.reduce((acc, n) => acc + n, 0);
}
sum(1, 2, 3, 4, 5)  // 15

function log(level, ...messages) {
  console.log(`[${level}]`, ...messages);
}
log('INFO', 'Server', 'started', 'port 3000');
// [INFO] Server started port 3000
```

> **How to tell spread vs rest apart:** Spread is in a **call** (expanding values out). Rest is in a **definition** (collecting values in).

---

## 3.9 Logical Assignment Operators (ES2021)

Shorthand for common "assign if condition" patterns:

```javascript
// ||= : assign only if current value is falsy
let name = '';
name ||= 'Anonymous';   // name = 'Anonymous'

let user = { name: 'Alice' };
user.role ||= 'user';   // sets role only if not set or falsy

// ??= : assign only if current value is null or undefined (preferred)
let cache = null;
cache ??= {};           // cache = {}

user.profile ??= {};           // initialise if missing
user.profile.bio ??= 'No bio'; // won't overwrite '' or false

// &&= : assign only if current value is truthy
let config = { debug: true };
config.debug &&= false;   // only updates if debug was truthy
```

---

## 3.10 `in` and `instanceof`

```javascript
// in: checks if a property exists in an object (including prototype chain)
const car = { make: 'Toyota', model: 'Camry' };
'make' in car        // true
'horsepower' in car  // false

// instanceof: checks prototype chain
[] instanceof Array           // true
[] instanceof Object          // true  (arrays are objects)
new Date() instanceof Date    // true

class Animal {}
class Dog extends Animal {}
const dog = new Dog();
dog instanceof Dog     // true
dog instanceof Animal  // true  (prototype chain)
dog instanceof Object  // true
```

---

## 3.11 Operator Precedence

When in doubt, use parentheses. Key order to remember:

```
High ──────────────────────────────── Low
  ()  →  **  →  !, ~, unary+/-
  →  *, /, %
  →  +, -
  →  <, <=, >, >=, in, instanceof
  →  ===, !==, ==, !=
  →  &&
  →  ||
  →  ??
  →  ? :
  →  =, +=, -=...
```

```javascript
// Common pitfall: ?? and || cannot be mixed without parens
a ?? b || c     // ❌ SyntaxError
(a ?? b) || c   // ✅
a ?? (b || c)   // ✅

// Always use parens when mixing
2 + 3 * 4       // 14 (not 20)
(2 + 3) * 4     // 20
```

---

## Key Takeaways

- Use `===` and `!==` always — `==` and `!=` cause unpredictable coercion.
- `??` is safer than `||` for defaults — it only triggers on `null`/`undefined`.
- `?.` prevents crashes on null property access — essential for API data.
- Spread `...` expands; Rest `...` collects. Same syntax, opposite jobs.
- `??=` is cleaner than `value = value ?? default` for lazy initialisation.

---

## Self-Check Questions

1. What is the difference between `||` and `??` for providing default values? Give an example where `||` gives the wrong answer.
2. What does `user?.profile?.address?.city` return if `user.profile` is `null`?
3. What is the difference between spread and rest operators? Write one example of each.
4. Why does `0.1 + 0.2 === 0.3` return `false`? How do you handle currency math?
5. Write a one-liner function `getUsername(user)` that returns `user.profile.username` if it exists, or `'Anonymous'` if any part of the chain is null/undefined.
6. What is `??=` useful for? Give a practical example.
