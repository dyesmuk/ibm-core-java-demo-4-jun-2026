# JS Basics — Module 03: Operators

## 3.1 Arithmetic Operators

```javascript
// Standard arithmetic
10 + 3    // 13
10 - 3    // 7
10 * 3    // 30
10 / 3    // 3.3333...  (no integer division — always floating point)
10 % 3    // 1  (remainder)
2 ** 10   // 1024  (exponentiation — ES2016)

// Integer division workaround (like Java's int division)
Math.floor(10 / 3)    // 3
Math.trunc(10 / 3)    // 3  (truncates toward zero, not floor)
Math.trunc(-7 / 2)    // -3  (floor would give -4)

// Increment / Decrement
let i = 5;
i++;         // post-increment: returns 5, then i becomes 6
++i;         // pre-increment: i becomes 7, returns 7
i--;         // post-decrement: returns 7, then i becomes 6
--i;         // pre-decrement: i becomes 5, returns 5

// Compound assignment
let x = 10;
x += 5;    // x = 15
x -= 3;    // x = 12
x *= 2;    // x = 24
x /= 4;    // x = 6
x **= 2;   // x = 36
x %= 10;   // x = 6
```

### Floating-Point Precision (Java Developers Take Note)

```javascript
0.1 + 0.2    // 0.30000000000000004  — NOT 0.3
0.1 + 0.2 === 0.3   // false!

// This is IEEE 754 floating-point arithmetic — same in Java, Python, etc.
// Fix: use toFixed or round when displaying
(0.1 + 0.2).toFixed(2)    // "0.30" (string)
Math.round((0.1 + 0.2) * 100) / 100  // 0.3 (number)

// For financial calculations: multiply to integers, compute, then divide
const price1 = 0.10;  // $0.10
const price2 = 0.20;  // $0.20
const total = (Math.round(price1 * 100) + Math.round(price2 * 100)) / 100;
// 0.30  ✅
```

---

## 3.2 Comparison Operators

```javascript
// Equality (always use ===)
5 === 5           // true
5 === "5"         // false  (different types)
null === null     // true
NaN === NaN       // false  (special case!)

// Inequality (always use !==)
5 !== 10          // true
5 !== "5"         // true

// Relational
5 > 3             // true
5 >= 5            // true
3 < 5             // true
3 <= 3            // true

// String comparison (lexicographic)
"apple" < "banana"  // true  (a < b)
"b" > "a"           // true
"10" > "9"          // false  (because "1" < "9" lexicographically)
10 > 9              // true   (use numbers for numeric comparison)

// Comparing with null/undefined
null > 0     // false
null == 0    // false  (special rule: null only == undefined)
null >= 0    // true   (!) — comparing null numerically converts it to 0
undefined > 0   // false
undefined < 0   // false
undefined == 0  // false
// Summary: avoid relational comparisons with null/undefined — use explicit checks
```

---

## 3.3 Logical Operators

```javascript
// && (AND) — returns first falsy value or last value
true && true     // true
true && false    // false
"hello" && 42    // 42      (both truthy, returns last)
null && "hello"  // null    (first is falsy, short-circuits)
0 && "hello"     // 0       (0 is falsy, short-circuits)

// || (OR) — returns first truthy value or last value
true || false    // true
false || "hello" // "hello" (returns first truthy)
null || "default" // "default"

// ! (NOT)
!true    // false
!false   // true
!0       // true  (0 is falsy)
!""      // true
!!"hello"  // true  (double negation for boolean conversion)

// Short-circuit evaluation (very common in JS)
const user = null;
const name = user && user.name;    // null  (doesn't crash accessing user.name)
const display = name || 'Guest';   // 'Guest'

// Default parameter pattern (pre-ES6)
function greet(name) {
  name = name || 'Guest';   // if name is falsy, use 'Guest'
  return `Hello, ${name}`;
}
// Problem: greet("") returns 'Hello, Guest' — empty string is falsy!
```

---

## 3.4 Nullish Coalescing Operator `??` (ES2020)

`??` returns the right side only when the left side is **`null` or `undefined`** (not other falsy values):

```javascript
// || problem: treats all falsy values the same
const count = 0;
const display1 = count || 'No items';   // 'No items' — WRONG: 0 is a valid count

// ?? solution: only null/undefined trigger the fallback
const display2 = count ?? 'No items';   // 0  — CORRECT

// More examples
null ?? "default"       // "default"
undefined ?? "default"  // "default"
0 ?? "default"          // 0
"" ?? "default"         // ""
false ?? "default"      // false

// Real-world usage
function getConfig(userConfig) {
  return {
    timeout: userConfig.timeout ?? 5000,   // default 5s, but 0 is valid
    retries: userConfig.retries ?? 3,
    debug: userConfig.debug ?? false       // explicitly setting false works
  };
}
```

---

## 3.5 Optional Chaining `?.` (ES2020)

Access nested properties without crashing when an intermediate value is `null`/`undefined`:

```javascript
const user = {
  profile: {
    address: {
      city: 'Bengaluru'
    }
  }
};

// Without optional chaining — verbose guard chain
const city1 = user && user.profile && user.profile.address && user.profile.address.city;

// With optional chaining — clean
const city2 = user?.profile?.address?.city;    // 'Bengaluru'

// When any part is null/undefined, returns undefined (no error)
const country = user?.profile?.address?.country;   // undefined
const zip     = user?.settings?.zip;               // undefined (settings doesn't exist)

// Optional chaining with method calls
const upper = user?.getName?.();     // calls getName() if it exists, else undefined

// Optional chaining with arrays
const first = user?.roles?.[0];     // user.roles[0] if roles exists

// Combine with ?? for defaults
const city = user?.profile?.address?.city ?? 'Unknown';  // 'Bengaluru'
const zip  = user?.profile?.address?.zip  ?? 'N/A';      // 'N/A'
```

---

## 3.6 Ternary Operator

```javascript
// condition ? valueIfTrue : valueIfFalse
const age = 20;
const status = age >= 18 ? 'Adult' : 'Minor';

// Nested ternary (use sparingly — hard to read)
const grade = score >= 90 ? 'A' :
              score >= 80 ? 'B' :
              score >= 70 ? 'C' : 'F';

// Common pattern: conditional JSX/class names
const buttonClass = isLoading ? 'btn btn-disabled' : 'btn btn-primary';
const icon = isOpen ? '▲' : '▼';
```

---

## 3.7 Bitwise Operators (Rare, but Encountered)

```javascript
// AND, OR, XOR, NOT, shift (same as Java)
5 & 3    // 1   (101 & 011 = 001)
5 | 3    // 7   (101 | 011 = 111)
5 ^ 3    // 6   (101 ^ 011 = 110)
~5       // -6  (bitwise NOT)
5 << 1   // 10  (left shift)
5 >> 1   // 2   (right shift with sign)
5 >>> 1  // 2   (unsigned right shift)

// Common practical use: fast floor for positive numbers
~~3.9    // 3  (double bitwise NOT — faster than Math.floor for positive numbers)
5 | 0    // 5  (bitwise OR 0 — coerces to 32-bit integer, truncates decimal)
3.7 | 0  // 3  (truncates)
```

---

## 3.8 The `in` and `instanceof` Operators

```javascript
// in: checks if a property exists in an object (or its prototype)
const car = { make: 'Toyota', model: 'Camry', year: 2020 };
'make' in car       // true
'horsepower' in car // false

// Check array indices
const arr = [10, 20, 30];
0 in arr    // true  (index 0 exists)
3 in arr    // false (index 3 doesn't exist)

// instanceof: checks prototype chain (like Java's instanceof)
[] instanceof Array           // true
[] instanceof Object          // true  (arrays are objects)
new Date() instanceof Date    // true
function(){} instanceof Function  // true

// Checking class instances
class Animal {}
class Dog extends Animal {}
const dog = new Dog();
dog instanceof Dog    // true
dog instanceof Animal // true
dog instanceof Object // true
```

---

## 3.9 Spread and Rest Operators

```javascript
// Spread (...): expands an iterable into individual elements

// In function calls
Math.max(...[1, 5, 3, 9, 2])   // 9  (without spread: Math.max([1,5,3]) = NaN)

// In array literals
const a = [1, 2, 3];
const b = [4, 5, 6];
const combined = [...a, ...b];          // [1, 2, 3, 4, 5, 6]
const withExtra = [0, ...a, 3.5, ...b]; // [0, 1, 2, 3, 3.5, 4, 5, 6]

// Copying an array (shallow)
const copy = [...a];  // [1, 2, 3] — new array, not a reference

// In object literals
const base = { x: 1, y: 2 };
const extended = { ...base, z: 3 };         // { x: 1, y: 2, z: 3 }
const overridden = { ...base, x: 99 };      // { x: 99, y: 2 }  (later key wins)
const merged = { ...base, ...{ y: 10, z: 3 } };  // { x: 1, y: 10, z: 3 }

// Rest (...): collects remaining arguments into an array
function sum(...numbers) {        // rest parameter
  return numbers.reduce((acc, n) => acc + n, 0);
}
sum(1, 2, 3, 4, 5)   // 15

function first(a, b, ...rest) {
  console.log(a);     // 1
  console.log(b);     // 2
  console.log(rest);  // [3, 4, 5]
}
first(1, 2, 3, 4, 5);
```

---

## 3.10 Operator Precedence

Like Java, operators have precedence. The full table is at MDN; key rules to remember:

```
High ←──────────────────────────────────────── Low
  ()  →  **  →  !, ~, ++(unary)  →  *, /, %  →  +, -
  →  <<, >>  →  <, <=, >, >=, in, instanceof
  →  ==, !=, ===, !==  →  &&  →  ||  →  ??
  →  ? :  →  =, +=, -=...  →  ,
```

```javascript
// When in doubt, use parentheses for clarity
2 + 3 * 4       // 14  (not 20 — * has higher precedence)
(2 + 3) * 4     // 20

null ?? undefined || 'default'    // ambiguous — use parens:
(null ?? undefined) || 'default'  // 'default'

// ?? and || cannot be mixed without parens (SyntaxError in modern JS)
a ?? b || c     // ❌ SyntaxError
(a ?? b) || c   // ✅
a ?? (b || c)   // ✅
```

---

## Key Takeaways

- Use `===` and `!==` always — `==` and `!=` perform unpredictable type coercion.
- `??` (nullish coalescing) is safer than `||` for defaults — it only triggers on `null`/`undefined`.
- `?.` (optional chaining) prevents crashes when accessing deeply nested properties.
- Spread `...` creates shallow copies and merges arrays/objects.
- Rest `...` collects function arguments into an array.

---

## Self-Check Questions

1. Why is `0.1 + 0.2 !== 0.3`? How do you work around this for currency calculations?
2. What is the difference between `||` and `??` for providing default values?
3. What does `user?.profile?.address?.city` return if `user.profile` is `null`?
4. What is the difference between the spread and rest operators? (They look the same but behave differently — how do you tell them apart?)
5. Write a function `getUsername(user)` that returns `user.profile.username` if it exists, or `'Anonymous'` if any part of the chain is null/undefined.
