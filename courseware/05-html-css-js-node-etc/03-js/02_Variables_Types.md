# JavaScript Courseware — Module 02: Variables, Data Types & Type Coercion

---

## 2.1 Variable Declarations: `var`, `let`, `const`

JavaScript gives you three ways to declare variables. The rule is simple and non-negotiable in professional code:

> **Use `const` by default. Use `let` when you need to reassign. Never use `var`.**

```javascript
// const — block-scoped, binding cannot be reassigned
const PI = 3.14159;
const API_URL = 'https://api.example.com';
PI = 3;   // ❌ TypeError: Assignment to constant variable

// let — block-scoped, can be reassigned
let counter = 0;
counter = 1;   // ✅

// var — function-scoped, hoisted, avoid completely
var x = 10;    // ❌ legacy — causes subtle bugs
```

### Why `var` Is Dangerous

```javascript
// var leaks out of blocks
{
  var leaked = 'oops';
}
console.log(leaked);    // 😱 'oops' — leaks out of the block

// The classic loop bug with var
for (var i = 0; i < 3; i++) {
  setTimeout(() => console.log(i), 100);
}
// Prints: 3 3 3  — all closures share the same var i

// Fix: use let — creates a fresh binding per iteration
for (let j = 0; j < 3; j++) {
  setTimeout(() => console.log(j), 100);
}
// Prints: 0 1 2  ✅
```

### `const` Does NOT Mean Immutable

`const` means the **variable binding** can't be reassigned. The value itself can still mutate:

```javascript
const user = { name: 'Alice', age: 30 };
user.age = 31;          // ✅ mutating the object is allowed
user = { name: 'Bob' }; // ❌ reassigning the binding is not

const scores = [1, 2, 3];
scores.push(4);          // ✅ mutating the array is fine
scores = [5, 6];         // ❌ reassigning is not
```

---

## 2.2 The 8 Data Types

JavaScript has **7 primitives + 1 object type**. Everything else (arrays, functions, dates) is an object.

```javascript
// 1. string
const name = 'Alice';
const greeting = "Hello, World!";   // single or double quotes — identical
const template = `Hello, ${name}!`; // template literal (backtick) — preferred

// 2. number  (just ONE numeric type — no int/float/double split like Java)
const age = 30;
const price = 19.99;
const hex = 0xFF;               // 255
const billion = 1_000_000_000;  // numeric separators (ES2021) — readable!
const MAX = Number.MAX_SAFE_INTEGER;  // 9007199254740991 (2^53 - 1)

// 3. bigint  (arbitrarily large integers)
const hugeId = 9007199254740992n;   // suffix 'n'
const result = 100n + 200n;         // ✅ but you cannot mix with regular number

// 4. boolean
const isLoggedIn = true;
const hasPermission = false;

// 5. undefined  — declared but never assigned
let email;
console.log(email);   // undefined

// 6. null  — intentionally set to "no value"
const selectedUser = null;

// 7. symbol  — unique identifier (advanced use, seen in iterators)
const id1 = Symbol('id');
const id2 = Symbol('id');
console.log(id1 === id2);   // false — every Symbol is unique

// 8. object  (arrays, functions, dates are all objects)
const person = { name: 'Alice' };  // object literal
const list = [1, 2, 3];            // array (also an object)
const greet = () => 'hi';          // function (also an object)
```

### Java vs JavaScript Types

| Java | JavaScript | Notes |
|---|---|---|
| `int`, `long`, `float`, `double` | `number` | JS has ONE numeric type |
| `BigInteger` | `bigint` | for integers beyond 2^53 |
| `String` | `string` | immutable in both |
| `boolean` | `boolean` | same |
| `null` | `null` + `undefined` | JS has TWO "empty" values |
| `char` | no char type | single chars are just strings |
| `void` (method) | `undefined` | functions return `undefined` by default |

---

## 2.3 `typeof` Operator

```javascript
typeof "hello"        // 'string'
typeof 42             // 'number'
typeof 3.14           // 'number'
typeof true           // 'boolean'
typeof undefined      // 'undefined'
typeof null           // 'object'   ← famous historical bug — null is NOT an object
typeof {}             // 'object'
typeof []             // 'object'   ← arrays are objects
typeof function(){}   // 'function'
typeof Symbol()       // 'symbol'
typeof 42n            // 'bigint'

// Correct null check — don't rely on typeof null
const value = null;
value === null   // ✅ true
```

---

## 2.4 `null` vs `undefined` — Know the Difference

This trips up Java developers. Java has only `null`. JavaScript has two:

```javascript
// undefined: the system's way of saying "this was never given a value"
let x;                    // x is undefined
function noReturn() {}
noReturn();               // returns undefined
const obj = {};
obj.missingProp;          // undefined

// null: YOUR way of saying "intentionally no value here"
const currentUser = null;  // explicitly cleared
const result = null;       // calculation returned nothing intentional

// Checking for either:
value == null    // true for both null AND undefined (one of the rare valid uses of ==)
value === null   // only null
value === undefined  // only undefined

// Modern preferred pattern:
value ?? 'default'  // use nullish coalescing (covered in Module 03)
```

---

## 2.5 Type Coercion — JavaScript's Most (In)Famous Feature

JavaScript **automatically converts types** when you apply operators to mismatched types. This is called **implicit type coercion** and is responsible for a LOT of bugs.

### The `+` Operator — String Wins

```javascript
// If EITHER operand is a string, + becomes string concatenation
"5" + 3          // "53"    ← not 8!
"5" + true       // "5true"
"5" + null       // "5null"
"5" + undefined  // "5undefined"
1 + 2 + "3"     // "33"  (left to right: 1+2=3, then 3+"3"="33")
"1" + 2 + 3     // "123" (left to right: "1"+2="12", then "12"+3="123")
```

### `-`, `*`, `/` — Number Mode

```javascript
// These try to convert to numbers
"10" - 5      // 5    ("10" → 10)
"10" * "2"    // 20
"10" / "2"    // 5
"10" - "abc"  // NaN  (can't convert "abc" to number)
true - false  // 1    (true → 1, false → 0)
null + 1      // 1    (null → 0)
undefined + 1 // NaN
```

### Equality: `==` vs `===` — Always Use `===`

```javascript
// == (loose equality): type coercion happens first
"5" == 5        // true  ← types differ but JS converts
0 == false      // true  ← false coerced to 0
"" == false     // true
null == undefined  // true  (special rule)
null == 0          // false (another special rule — inconsistent!)

// === (strict equality): types MUST match, no coercion
"5" === 5       // false  ← types differ
0 === false     // false
null === undefined  // false

// THE RULE: always use === and !== everywhere.
// The only acceptable use of == is: value == null (catches both null AND undefined)
```

### Truthy and Falsy Values

Every JavaScript value is either **truthy** or **falsy** when used in a boolean context (`if`, `while`, `!`, `&&`, `||`).

```javascript
// FALSY values — memorise these 8:
false
0
-0
0n          // BigInt zero
""          // empty string (not " " — space is truthy!)
null
undefined
NaN

// TRUTHY: everything else, including these common gotchas:
"0"         // non-empty string — TRUTHY even though it "looks" false
"false"     // non-empty string — TRUTHY
[]          // empty array — TRUTHY  ← very common gotcha
{}          // empty object — TRUTHY  ← very common gotcha
-1          // non-zero number
Infinity
```

```javascript
// Gotcha in practice
if ([]) console.log("empty array is truthy!");   // ✅ this prints
if ({}) console.log("empty object is truthy!");  // ✅ this prints

// Correct way to check for empty array:
if (items.length === 0) { ... }   // ✅
if (!items) { ... }               // ❌ never truthy unless items is null/undefined
```

---

## 2.6 Explicit Type Conversion

When you want to convert deliberately (not rely on coercion):

```javascript
// → String
String(42)            // "42"
String(true)          // "true"
String(null)          // "null"
String(undefined)     // "undefined"
(42).toString()       // "42"
(255).toString(16)    // "ff"  (hex)
(255).toString(2)     // "11111111"  (binary)

// → Number
Number("42")          // 42
Number("3.14")        // 3.14
Number("")            // 0    ← gotcha
Number("abc")         // NaN
Number(true)          // 1
Number(false)         // 0
Number(null)          // 0    ← gotcha
Number(undefined)     // NaN
parseInt("42px", 10)  // 42   (stops at first non-numeric char)
parseFloat("3.14em")  // 3.14
+"42"                 // 42   (unary + — quick conversion, common in code)

// → Boolean
Boolean(0)            // false
Boolean("")           // false
Boolean(null)         // false
Boolean("hello")      // true
Boolean([])           // true  ← gotcha
!!value               // idiomatic double-negation to boolean (very common)
```

---

## 2.7 Template Literals

Prefer template literals over string concatenation. Always.

```javascript
const user = { name: 'Alice', age: 30 };

// Old way — messy
const msg1 = 'Hello, ' + user.name + '! You are ' + user.age + ' years old.';

// Template literal — clean
const msg2 = `Hello, ${user.name}! You are ${user.age} years old.`;

// Any expression inside ${}
const msg3 = `Status: ${user.age >= 18 ? 'Adult' : 'Minor'}`;
const msg4 = `Total: ${(price * qty).toFixed(2)}`;

// Multi-line strings (no \n needed)
const html = `
  <div class="card">
    <h2>${user.name}</h2>
    <p>Age: ${user.age}</p>
  </div>
`;
```

---

## 2.8 Special Values: `NaN` and `Infinity`

```javascript
// NaN — Not a Number (but typeof NaN === 'number' — yes, really)
const result = parseInt("abc", 10);   // NaN
typeof NaN         // 'number'  ← quirk
NaN === NaN        // false!  ← NaN is the only value not equal to itself

Number.isNaN(NaN)   // true  ← use this
Number.isNaN("abc") // false ← correctly says string is not NaN
isNaN("abc")        // true  ← legacy, coerces first, avoid

// Infinity
1 / 0              // Infinity
-1 / 0             // -Infinity
Infinity + 1       // Infinity
Number.isFinite(42)          // true
Number.isFinite(Infinity)    // false
Number.isFinite(NaN)         // false
```

---

## Key Takeaways

- Use `const` always, `let` when you must reassign, never `var`.
- JavaScript has ONE `number` type for all numbers (no int/float/double split).
- `null` = intentional absence; `undefined` = never assigned. Both check with `== null`.
- Use `===` (strict equality) everywhere — `==` coerces types unpredictably.
- Empty arrays `[]` and objects `{}` are **truthy** — don't use `if (!arr)` to check empty.
- `NaN !== NaN` — use `Number.isNaN()` to detect it.

---

## Self-Check Questions

1. What is the difference between `let` and `const`? Between `let` and `var`?
2. What does `typeof null` return, and why is it misleading?
3. Why does `"5" + 3` return `"53"` but `"5" - 3` returns `2`?
4. What are the 8 falsy values in JavaScript? Which ones surprise Java developers the most?
5. How do you check if a variable is either `null` or `undefined` in a single expression?
6. Why is `[] == false` true but `if ([])` executes the block?
