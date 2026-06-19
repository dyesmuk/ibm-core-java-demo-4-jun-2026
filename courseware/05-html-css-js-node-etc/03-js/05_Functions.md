# JS Basics — Module 05: Functions

## 5.1 Four Ways to Define a Function

JavaScript has multiple function syntaxes, each with different behaviour:

```javascript
// 1. Function Declaration — hoisted, can be called before definition
function greet(name) {
  return `Hello, ${name}!`;
}
console.log(greet('Alice'));  // works even before the declaration

// 2. Function Expression — not hoisted, stored in a variable
const greet = function(name) {
  return `Hello, ${name}!`;
};

// 3. Arrow Function — concise, no own `this`
const greet = (name) => `Hello, ${name}!`;
const greet = name => `Hello, ${name}!`;  // parens optional for single param
const greet = () => 'Hello!';             // no params: empty parens required
const greet = (name) => {                 // block body for multiple statements
  const msg = `Hello, ${name}!`;
  return msg;
};

// 4. Method shorthand in object/class
const obj = {
  greet(name) {              // shorthand (preferred in objects)
    return `Hello, ${name}!`;
  }
};
```

### Hoisting

```javascript
// Function declarations are fully hoisted
sayHi();  // ✅ works

function sayHi() {
  console.log('Hi!');
}

// Function expressions and arrow functions are NOT hoisted
sayBye();  // ❌ ReferenceError: Cannot access 'sayBye' before initialization

const sayBye = () => console.log('Bye!');
```

---

## 5.2 Parameters and Arguments

```javascript
// Default parameters (ES6)
function createUser(name, role = 'user', isActive = true) {
  return { name, role, isActive };
}
createUser('Alice')              // { name: 'Alice', role: 'user', isActive: true }
createUser('Bob', 'admin')       // { name: 'Bob', role: 'admin', isActive: true }
createUser('Carol', 'user', false) // { name: 'Carol', role: 'user', isActive: false }

// Default expressions (evaluated at call time, not definition time)
function addItem(item, list = []) {  // WARNING: this is fine in JS (unlike Python)
  return [...list, item];            // but use carefully
}

// Rest parameters (must be last)
function sum(...numbers) {
  return numbers.reduce((acc, n) => acc + n, 0);
}
sum(1, 2, 3, 4)  // 10

function log(level, ...messages) {
  console.log(`[${level}]`, ...messages);
}
log('INFO', 'Server', 'started', 'on port 3000');

// Named parameters via destructuring (Java-style builder pattern alternative)
function createServer({ port = 3000, host = 'localhost', debug = false } = {}) {
  return { port, host, debug };
}
createServer({ port: 8080, debug: true });
createServer();  // all defaults (= {} prevents crash if called with no args)
```

---

## 5.3 Return Values

```javascript
// Functions always return a value; without explicit return, returns undefined
function noReturn() {}
noReturn()  // undefined

// Early return
function divide(a, b) {
  if (b === 0) return null;  // or throw new Error
  return a / b;
}

// Returning multiple values via destructuring
function minMax(arr) {
  return {
    min: Math.min(...arr),
    max: Math.max(...arr)
  };
}
const { min, max } = minMax([3, 1, 4, 1, 5, 9]);

// Arrow function implicit return — no return keyword, no braces
const double = n => n * 2;
const square = n => n * n;

// Returning an object literal from arrow function (wrap in parens)
const makePoint = (x, y) => ({ x, y });  // WITHOUT parens: treated as block
const makePoint = (x, y) => { x, y };    // ❌ SyntaxError
```

---

## 5.4 First-Class Functions

In JavaScript, **functions are values**. You can store them in variables, pass them as arguments, and return them from other functions. This is different from Java's method references but conceptually similar.

```javascript
// Store in a variable
const fn = () => console.log('hello');

// Store in an array
const ops = [Math.min, Math.max, Math.abs];
ops[0](3, 1, 4)  // 1

// Store in an object (method)
const calculator = {
  add: (a, b) => a + b,
  subtract: (a, b) => a - b,
  multiply: (a, b) => a * b
};

// Pass as argument (callback)
[1, 2, 3].forEach(function(n) { console.log(n); });
[1, 2, 3].forEach(n => console.log(n));
[1, 2, 3].forEach(console.log);  // function reference

// Return from function (factory)
function multiplier(factor) {
  return n => n * factor;  // returns a function
}
const double = multiplier(2);
const triple = multiplier(3);
double(5)  // 10
triple(5)  // 15
```

---

## 5.5 Higher-Order Functions

Functions that take other functions as arguments or return them:

```javascript
// map: transform each element
[1, 2, 3].map(n => n * 2)        // [2, 4, 6]
['alice', 'bob'].map(s => s.toUpperCase())  // ['ALICE', 'BOB']

// filter: keep elements that pass a test
[1, 2, 3, 4, 5].filter(n => n % 2 === 0)  // [2, 4]
users.filter(u => u.isActive)

// reduce: accumulate a single value
[1, 2, 3, 4, 5].reduce((acc, n) => acc + n, 0)  // 15
// acc = accumulator (starts at 0), n = current value

// Real-world reduce examples
const cart = [
  { name: 'Widget', price: 10, qty: 2 },
  { name: 'Gadget', price: 25, qty: 1 }
];
const total = cart.reduce((sum, item) => sum + item.price * item.qty, 0);  // 45

const byId = users.reduce((acc, user) => {
  acc[user.id] = user;
  return acc;
}, {});
// { 1: {id:1, ...}, 2: {id:2, ...} } — fast lookup by id

// find / findIndex
const user = users.find(u => u.id === 42);       // first match or undefined
const idx  = users.findIndex(u => u.id === 42);  // index or -1

// every / some
users.every(u => u.isActive)   // true if ALL are active
users.some(u => u.role === 'admin')  // true if ANY is admin

// Custom higher-order function
function pipe(...fns) {
  return x => fns.reduce((v, f) => f(v), x);
}
const process = pipe(
  x => x * 2,
  x => x + 1,
  x => x.toString()
);
process(5)  // "11"  (5*2=10, 10+1=11, 11.toString()="11")
```

---

## 5.6 Closures

A **closure** is a function that "remembers" the variables from its enclosing scope, even after that scope has finished executing. This is one of JavaScript's most powerful — and most confusing — features.

```javascript
// Basic closure
function makeCounter() {
  let count = 0;         // this variable is "closed over"
  return {
    increment() { count++; },
    decrement() { count--; },
    value()     { return count; }
  };
}

const counter = makeCounter();
counter.increment();
counter.increment();
counter.increment();
counter.decrement();
console.log(counter.value());  // 2
// count is private — not accessible from outside

// Each call to makeCounter creates an independent closure
const counter2 = makeCounter();
counter2.increment();
console.log(counter2.value());  // 1 (independent)
console.log(counter.value());   // still 2

// Practical: partial application
function greet(greeting) {
  return name => `${greeting}, ${name}!`;  // closes over greeting
}
const hello = greet('Hello');
const bye   = greet('Goodbye');
hello('Alice')   // 'Hello, Alice!'
bye('Alice')     // 'Goodbye, Alice!'

// Module pattern: encapsulation via closure
const userModule = (() => {
  let _users = [];   // private state

  return {
    add(user)  { _users.push(user); },
    getAll()   { return [..._users]; },  // return a copy
    count()    { return _users.length; }
  };
})();  // IIFE: immediately invoked
```

---

## 5.7 `this` Keyword

`this` in JavaScript depends on **how a function is called**, not where it is defined. This is the biggest source of confusion for Java developers.

```javascript
// In a regular function: this = the caller
const obj = {
  name: 'Alice',
  greet() {
    console.log(this.name);  // 'Alice' — called as obj.greet()
  }
};
obj.greet();  // 'Alice'

const greetFn = obj.greet;
greetFn();    // undefined (or error in strict mode) — this = undefined/global

// Arrow functions: this = enclosing scope's this (lexical this)
const obj2 = {
  name: 'Bob',
  greet() {
    const arrow = () => console.log(this.name);  // this = obj2
    arrow();
  }
};
obj2.greet();  // 'Bob'

// The classic problem: callback loses this
class Timer {
  constructor() {
    this.seconds = 0;
  }

  start() {
    // ❌ regular function: this is undefined
    setInterval(function() {
      this.seconds++;   // TypeError: Cannot read properties of undefined
    }, 1000);

    // ✅ arrow function: this = Timer instance
    setInterval(() => {
      this.seconds++;   // works correctly
    }, 1000);
  }
}

// Explicit binding with call, apply, bind
function introduce(greeting, punctuation) {
  return `${greeting}, I'm ${this.name}${punctuation}`;
}

const alice = { name: 'Alice' };
introduce.call(alice, 'Hello', '!')       // 'Hello, I\'m Alice!'
introduce.apply(alice, ['Hello', '!'])    // same, but args as array
const aliceIntro = introduce.bind(alice); // returns new function with this fixed
aliceIntro('Hi', '.');                    // 'Hi, I\'m Alice.'
```

---

## 5.8 Pure Functions and Side Effects

A **pure function**: same input always produces same output, no side effects.

```javascript
// Pure: deterministic, no side effects — easy to test
function add(a, b) { return a + b; }
function formatName(first, last) { return `${last}, ${first}`; }

// Impure: reads/writes external state
let total = 0;
function addToTotal(n) {
  total += n;       // side effect: modifies external state
  return total;
}

// Impure: depends on external state
function getPrice(productId) {
  return db.find(productId).price;  // depends on database
}

// Impure: I/O operations (all Node.js file/network ops are impure)
function readConfig(path) {
  return fs.readFileSync(path, 'utf8');
}
```

**Aim for pure functions where possible**. Pure functions:
- Are trivial to unit test
- Can be safely parallelised
- Are referentially transparent (result can be cached)
- Don't cause bugs by modifying shared state

---

## 5.9 Immediately Invoked Function Expressions (IIFE)

```javascript
// Define and call immediately — used for encapsulation
(function() {
  const localVar = 'I am private';
  console.log(localVar);
})();

// Arrow IIFE
(() => {
  console.log('Arrow IIFE');
})();

// With parameters
((doc, win) => {
  // safe aliases for document and window
})(document, window);

// IIFE for module-like encapsulation (pre-ES6 modules pattern)
const MyModule = (() => {
  const private_data = 'secret';
  function privateHelper() { ... }

  return {
    publicMethod() { return privateHelper(); }
  };
})();
```

---

## 5.10 Recursion

```javascript
// Fibonacci (classic example)
function fib(n) {
  if (n <= 1) return n;
  return fib(n - 1) + fib(n - 2);
}

// Factorial
function factorial(n) {
  if (n === 0) return 1;
  return n * factorial(n - 1);
}

// Practical: flatten a deeply nested array
function flatten(arr) {
  return arr.reduce((flat, item) =>
    Array.isArray(item) ? [...flat, ...flatten(item)] : [...flat, item],
  []);
}
flatten([1, [2, [3, [4]]], 5])  // [1, 2, 3, 4, 5]

// Tree traversal (common in Node.js for directories)
function walkTree(node) {
  console.log(node.name);
  if (node.children) {
    node.children.forEach(child => walkTree(child));
  }
}

// Note: JavaScript does NOT have tail call optimisation in most engines
// Use iteration or trampolining for deep recursion to avoid stack overflow
```

---

## Key Takeaways

- Functions are values in JavaScript — store, pass, and return them freely.
- Arrow functions have lexical `this` — they don't create their own `this` binding. Use them in callbacks.
- Closures give functions memory — the foundation of module patterns, factories, and many async patterns.
- `this` in regular functions depends on how the function is called, not where it's defined.
- Higher-order functions (`map`, `filter`, `reduce`) are the backbone of functional-style JavaScript.

---

## Self-Check Questions

1. What is the difference between a function declaration and a function expression in terms of hoisting?
2. Why do arrow functions solve the `this` problem in class callbacks?
3. What is a closure? Write a `makeCounter()` function that demonstrates one.
4. What is the difference between `.call()`, `.apply()`, and `.bind()`?
5. Implement a `compose(f, g)` function that returns a new function applying `g` then `f`.
