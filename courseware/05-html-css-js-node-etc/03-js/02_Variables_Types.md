# JS Basics — Module 02: Variables, Data Types & Type Coercion

## 2.1 Variable Declarations: `var`, `let`, `const`

JavaScript has three ways to declare variables. The rule is simple: **always use `const` by default; use `let` when you need to reassign; never use `var`.**

```javascript
// const: block-scoped, cannot be reassigned
const PI = 3.14159;
const API_URL = 'https://api.example.com';
PI = 3;   // ❌ TypeError: Assignment to constant variable

// let: block-scoped, can be reassigned
let counter = 0;
counter = 1;   // ✅

// var: function-scoped (legacy, avoid)
var x = 10;    // ❌ Don't use — has surprising hoisting behaviour
```

### Scope Comparison

```javascript
// Block scope with let/const (like Java)
{
  let blockVar = 'inside';
  const blockConst = 'also inside';
  console.log(blockVar);    // ✅ 'inside'
}
console.log(blockVar);      // ❌ ReferenceError: blockVar is not defined

// var ignores blocks — function scope only
{
  var funcScoped = 'leaks out';
}
console.log(funcScoped);    // 😱 'leaks out' — this is why var is avoided

// var in a for loop — the classic bug
for (var i = 0; i < 3; i++) {
  setTimeout(() => console.log(i), 0);
}
// prints: 3 3 3  (all closures share the same `var i`)

for (let j = 0; j < 3; j++) {
  setTimeout(() => console.log(j), 0);
}
// prints: 0 1 2  (let creates a new binding per iteration)
```

### `const` with Objects and Arrays

`const` means the **binding** cannot be reassigned, not that the value is immutable:

```javascript
const user = { name: 'Alice', age: 30 };
user.age = 31;          // ✅ mutating the object is allowed
user = { name: 'Bob' }; // ❌ reassigning the binding is not

const scores = [1, 2, 3];
scores.push(4);          // ✅ mutating the array is allowed
scores = [5, 6];         // ❌ reassigning is not
```

---

## 2.2 Primitive Types

JavaScript has **8 types**: 7 primitives + 1 object type.

```javascript
// string
const name = 'Alice';               // single quotes
const greeting = "Hello, World!";   // double quotes (identical)
const template = `Hello, ${name}!`; // template literal (backtick)

// number (there is only ONE number type — no int, float, double)
const age = 30;
const price = 19.99;
const hex = 0xFF;         // 255
const billion = 1_000_000_000;  // numeric separators (ES2021)
const MAX = Number.MAX_SAFE_INTEGER;   // 9007199254740991 (2^53 - 1)

// bigint (arbitrarily large integers)
const hugeId = 9007199254740992n;   // suffix n
const sum = 100n + 200n;            // ✅ cannot mix with regular number

// boolean
const isLoggedIn = true;
const hasPermission = false;

// undefined — variable declared but not assigned
let email;
console.log(email);   // undefined

// null — intentional absence of value (must be set explicitly)
const selectedUser = null;

// symbol — unique identifier (advanced use)
const id1 = Symbol('id');
const id2 = Symbol('id');
console.log(id1 === id2);   // false — every Symbol is unique

// object (everything else: arrays, functions, dates, etc.)
const person = { name: 'Alice' };  // object literal
const list = [1, 2, 3];           // array (also an object)
const greet = () => 'hi';         // function (also an object)
```

### Java Comparison

| Java | JavaScript | Notes |
|------|-----------|-------|
| `int`, `long`, `float`, `double` | `number` | JS has one numeric type |
| `BigInteger`, `BigDecimal` | `bigint` | for very large integers |
| `String` | `string` | immutable in both |
| `boolean` | `boolean` | same |
| `null` | `null` + `undefined` | two "empty" values |
| `char` | no equivalent | single chars are strings |
| `void` | `undefined` | functions return `undefined` by default |

---

## 2.3 `typeof` Operator

```javascript
typeof "hello"        // 'string'
typeof 42             // 'number'
typeof 3.14           // 'number'
typeof true           // 'boolean'
typeof undefined      // 'undefined'
typeof null           // 'object'  ← famous historical bug in JS, null is NOT an object
typeof {}             // 'object'
typeof []             // 'object'  ← arrays are objects
typeof function(){}   // 'function'
typeof Symbol()       // 'symbol'
typeof 42n            // 'bigint'

// Checking for null correctly:
const value = null;
value === null         // ✅ true  (use strict equality, not typeof)
```

---

## 2.4 Type Coercion — JavaScript's Most Infamous Feature

JavaScript **automatically converts** types when operators are applied to mismatched types. This is called **implicit type coercion** and is a frequent source of bugs.

### String Coercion with `+`

```javascript
// + with a string: converts the other operand to string
"5" + 3          // "53"  (number 3 → string "3")
"5" + true       // "5true"
"5" + null       // "5null"
"5" + undefined  // "5undefined"
[] + []          // ""
{} + []          // 0  (in some contexts)
```

### Numeric Coercion with `-`, `*`, `/`

```javascript
// - * / attempt numeric conversion
"10" - 5     // 5    ("10" → 10)
"10" * "2"   // 20
"10" / "2"   // 5
"10" - "abc" // NaN (Not a Number)
true - false  // 1  (true → 1, false → 0)
null + 1      // 1  (null → 0)
undefined + 1 // NaN
```

### Equality: `==` vs `===`

```javascript
// == (loose equality): performs type coercion before comparing
"5" == 5       // true  (string coerced to number)
0 == false     // true  (false → 0)
"" == false    // true
null == undefined  // true
null == 0          // false  (special rule)

// === (strict equality): no coercion, types must match
"5" === 5       // false
0 === false     // false
null === undefined  // false

// Rule: ALWAYS use ===. Never use == except for the null-check pattern:
value == null   // true for both null and undefined (sometimes useful)
```

### Truthy and Falsy Values

Every value in JavaScript is either truthy or falsy when evaluated in a boolean context:

```javascript
// FALSY values (only these 8):
false
0
-0
0n          // BigInt zero
""          // empty string
null
undefined
NaN

// TRUTHY: everything else, including:
"0"         // non-empty string — truthy even if "false-looking"
"false"     // non-empty string — truthy
[]          // empty array — truthy  ← common gotcha
{}          // empty object — truthy  ← common gotcha
-1          // any non-zero number
Infinity
```

```javascript
// Real-world implications
if ([]) console.log("array is truthy");   // prints
if ({}) console.log("object is truthy");  // prints

// Checking if an array is empty:
if (items.length === 0) { ... }  // ✅ correct
if (!items) { ... }              // ❌ wrong — non-empty array is always truthy
```

---

## 2.5 Template Literals

```javascript
const user = { name: 'Alice', age: 30 };

// ES5: string concatenation (awkward)
const msg1 = 'Hello, ' + user.name + '! You are ' + user.age + ' years old.';

// ES6: template literal (clean)
const msg2 = `Hello, ${user.name}! You are ${user.age} years old.`;

// Expressions inside ${}
const msg3 = `Status: ${user.age >= 18 ? 'Adult' : 'Minor'}`;

// Multi-line strings
const html = `
  <div class="card">
    <h2>${user.name}</h2>
    <p>Age: ${user.age}</p>
  </div>
`;

// Tagged template literals (advanced: used in libraries like styled-components)
function highlight(strings, ...values) {
  return strings.reduce((result, str, i) =>
    `${result}${str}${values[i] ? `<mark>${values[i]}</mark>` : ''}`, '');
}
const output = highlight`Hello, ${user.name}! You are ${user.age} years old.`;
// "Hello, <mark>Alice</mark>! You are <mark>30</mark> years old."
```

---

## 2.6 Type Conversion (Explicit)

```javascript
// String conversion
String(42)           // "42"
String(true)         // "true"
String(null)         // "null"
String(undefined)    // "undefined"
(42).toString()      // "42"
(255).toString(16)   // "ff" (hex)
(255).toString(2)    // "11111111" (binary)

// Number conversion
Number("42")         // 42
Number("3.14")       // 3.14
Number("")           // 0
Number("abc")        // NaN
Number(true)         // 1
Number(false)        // 0
Number(null)         // 0
Number(undefined)    // NaN
parseInt("42px", 10) // 42  (parses until non-numeric)
parseFloat("3.14em") // 3.14
+"42"                // 42  (unary + operator — quick conversion)

// Boolean conversion
Boolean(0)           // false
Boolean("")          // false
Boolean(null)        // false
Boolean("hello")     // true
Boolean(42)          // true
Boolean([])          // true  ← gotcha
!!value              // double negation — idiomatic boolean conversion
```

---

## 2.7 Special Values: `NaN`, `Infinity`, `undefined` vs `null`

```javascript
// NaN — Not a Number (but typeof NaN === 'number' — another quirk)
const result = parseInt("abc", 10);  // NaN
typeof NaN         // 'number'
NaN === NaN        // false  ← NaN is not equal to itself!
Number.isNaN(NaN)  // true  ← use this to check for NaN
isNaN("hello")     // true  ← legacy function, also coerces — avoid

// Infinity
1 / 0              // Infinity
-1 / 0             // -Infinity
Infinity + 1       // Infinity
Number.isFinite(42)   // true
Number.isFinite(Infinity)  // false

// undefined vs null
let x;                    // undefined — variable exists but has no value
const y = null;           // null — intentional "no value"
typeof undefined          // 'undefined'
typeof null               // 'object'  (historical bug)

// Checking for "no value" — the null-check pattern
function process(value) {
  if (value == null) {    // true for both null and undefined
    return 'no value';
  }
  return value;
}
// or modern:
function process(value) {
  return value ?? 'default';  // nullish coalescing — covered in Module 03
}
```

---

## Key Takeaways

- Use `const` always, `let` when you must reassign, never `var`.
- JavaScript has one `number` type for all numbers (no int/float/double split).
- `null` and `undefined` are different: `undefined` = unset; `null` = intentionally empty.
- Use `===` (strict equality) everywhere — `==` coerces types unpredictably.
- Empty arrays `[]` and empty objects `{}` are **truthy** — check `.length === 0` for empty arrays.
- `NaN !== NaN` — use `Number.isNaN()` to detect it.

---

## Self-Check Questions

1. What is the difference between `let` and `const`? Between `let` and `var`?
2. What does `typeof null` return and why?
3. Why does `"5" + 3` return `"53"` but `"5" - 3` returns `2`?
4. What are the 8 falsy values in JavaScript?
5. How do you check if a variable is `null` or `undefined` in one expression?
