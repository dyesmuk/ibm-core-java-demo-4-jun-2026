# JavaScript Courseware — Module 05: Functions

Functions are **first-class citizens** in JavaScript — you can store them in variables, pass them as arguments, and return them from other functions. This single fact unlocks most of JavaScript's power.

---

## 5.1 Four Ways to Define a Function

```javascript
// 1. Function Declaration — hoisted, callable before definition
function greet(name) {
  return `Hello, ${name}!`;
}
console.log(greet('Alice'));  // works even if called before the function definition

// 2. Function Expression — stored in a variable, NOT hoisted
const greet = function(name) {
  return `Hello, ${name}!`;
};

// 3. Arrow Function — concise syntax, no own `this` (most important difference)
const greet = (name) => `Hello, ${name}!`;
const greet = name => `Hello, ${name}!`;   // parens optional for single param
const greet = () => 'Hello!';              // no params: empty parens required
const greet = (name) => {                  // block body needed for multiple statements
  const msg = `Hello, ${name}!`;
  return msg;
};

// 4. Method shorthand in objects and classes
const obj = {
  greet(name) {
    return `Hello, ${name}!`;
  }
};
```

### Which to Use When?

| Situation | Use |
|---|---|
| Top-level utility functions | Function declaration |
| Callbacks, event handlers | Arrow function |
| Object methods | Method shorthand |
| Class methods | Method shorthand |
| Function stored in variable | Arrow function |

### Hoisting

```javascript
sayHi();  // ✅ works — function declarations are fully hoisted

function sayHi() {
  console.log('Hi!');
}

sayBye();  // ❌ ReferenceError — function expressions are NOT hoisted

const sayBye = () => console.log('Bye!');
```

---

## 5.2 Parameters and Arguments

### Default Parameters

```javascript
function createUser(name, role = 'user', isActive = true) {
  return { name, role, isActive };
}
createUser('Alice')               // { name: 'Alice', role: 'user', isActive: true }
createUser('Bob', 'admin')        // { name: 'Bob', role: 'admin', isActive: true }
createUser('Carol', 'user', false) // { name: 'Carol', role: 'user', isActive: false }
```

### Rest Parameters

```javascript
function sum(...numbers) {   // collects all args into array
  return numbers.reduce((acc, n) => acc + n, 0);
}
sum(1, 2, 3, 4)   // 10

function log(level, ...messages) {
  console.log(`[${level}]`, ...messages);
}
log('INFO', 'Server', 'started', 'on port 3000');
```

### Named Parameters via Destructuring

In Java you might use a Builder pattern. In JavaScript, just pass an object and destructure it:

```javascript
// Named parameters — much cleaner than positional args for many options
function createServer({ port = 3000, host = 'localhost', debug = false } = {}) {
  return { port, host, debug };
}

createServer({ port: 8080, debug: true });   // only specify what you need
createServer();                               // = {} prevents crash with no args
createServer({ port: 8080 });                // host and debug use defaults

// Compare to positional args — confusing order, all must be specified:
// createServer(8080, 'localhost', true) — what does true mean here?
```

---

## 5.3 Return Values

```javascript
// Without explicit return → undefined
function noReturn() {}
noReturn();  // undefined

// Arrow function implicit return (no braces = auto return)
const double = n => n * 2;
const square = n => n * n;
const add = (a, b) => a + b;

// Returning an object from arrow function — wrap in parens
const makePoint = (x, y) => ({ x, y });   // ✅
const makePoint = (x, y) => { x, y };     // ❌ SyntaxError — looks like a block

// Returning multiple values via destructuring
function minMax(arr) {
  return { min: Math.min(...arr), max: Math.max(...arr) };
}
const { min, max } = minMax([3, 1, 4, 1, 5, 9]);
// or as array:
function minMax2(arr) {
  return [Math.min(...arr), Math.max(...arr)];
}
const [min2, max2] = minMax2([3, 1, 4, 1, 5, 9]);
```

---

## 5.4 First-Class Functions

**Functions are values** — they can be stored, passed, and returned just like numbers or strings:

```javascript
// Store in a variable
const fn = () => 'hello';

// Store in an array
const ops = [Math.min, Math.max, Math.abs];
ops[0](3, 1, 4)  // 1

// Store in an object
const calculator = {
  add: (a, b) => a + b,
  subtract: (a, b) => a - b,
  multiply: (a, b) => a * b
};

// Pass as argument (callback pattern)
[1, 2, 3].forEach(n => console.log(n));
[1, 2, 3].forEach(console.log);   // function reference — same thing

// Return from function (factory pattern)
function multiplier(factor) {
  return n => n * factor;   // returns a function!
}
const double = multiplier(2);
const triple = multiplier(3);
double(5)  // 10
triple(5)  // 15
```

---

## 5.5 Higher-Order Functions

Functions that **take other functions** as arguments, or **return functions** — the backbone of functional-style JavaScript.

```javascript
const numbers = [1, 2, 3, 4, 5];
const users = [
  { id: 1, name: 'Alice', role: 'admin', salary: 80000 },
  { id: 2, name: 'Bob',   role: 'user',  salary: 60000 },
  { id: 3, name: 'Carol', role: 'admin', salary: 90000 }
];

// map — transform each element, returns new array of SAME length
numbers.map(n => n * 2)                  // [2, 4, 6, 8, 10]
users.map(u => u.name)                   // ['Alice', 'Bob', 'Carol']
users.map(u => ({ ...u, salary: u.salary * 1.1 }))  // 10% raise for all

// filter — keep elements that pass test, returns SHORTER (or equal) array
numbers.filter(n => n % 2 === 0)         // [2, 4]
users.filter(u => u.role === 'admin')    // [Alice, Carol]

// reduce — accumulate to a single value
numbers.reduce((acc, n) => acc + n, 0)   // 15  (sum)
numbers.reduce((max, n) => n > max ? n : max, -Infinity)  // 5

// Real-world reduce examples
const cart = [
  { name: 'Widget', price: 100, qty: 2 },
  { name: 'Gadget', price: 250, qty: 1 }
];
const total = cart.reduce((sum, item) => sum + item.price * item.qty, 0);  // 450

// Build a lookup map (id → object) — very common pattern
const byId = users.reduce((acc, user) => {
  acc[user.id] = user;
  return acc;
}, {});
// { 1: {Alice...}, 2: {Bob...}, 3: {Carol...} }
byId[2].name  // 'Bob' — O(1) lookup

// Chain them — very readable
const adminNames = users
  .filter(u => u.role === 'admin')
  .map(u => u.name)
  .join(', ');
// 'Alice, Carol'

// find / findIndex
const admin = users.find(u => u.role === 'admin');       // { Alice... } (first match)
const idx   = users.findIndex(u => u.name === 'Carol');  // 2
users.find(u => u.id === 99)    // undefined (not found)

// every / some
users.every(u => u.salary > 50000)   // true (all earn > 50K)
users.some(u => u.role === 'admin')  // true (at least one admin)
users.every(u => u.role === 'admin') // false (Bob is user)
```

### forEach vs map — Know the Difference

```javascript
// forEach — for side effects, returns undefined
numbers.forEach(n => console.log(n));   // ✅ just printing
const result = numbers.forEach(n => n * 2);  // result is undefined!

// map — for transformations, returns new array
const doubled = numbers.map(n => n * 2);  // [2, 4, 6, 8, 10]
```

---

## 5.6 Closures

A **closure** is a function that "remembers" variables from its outer scope even after that scope has finished executing. This is one of JavaScript's most powerful features.

```javascript
function makeCounter() {
  let count = 0;   // this variable is "closed over"
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
// count is private — NOT accessible from outside

// Each call creates an independent closure
const counter2 = makeCounter();
counter2.increment();
console.log(counter2.value());  // 1 (independent)
console.log(counter.value());   // still 2

// Practical: factory functions
function greet(greeting) {
  return name => `${greeting}, ${name}!`;  // closes over greeting
}
const sayHello = greet('Hello');
const sayBye   = greet('Goodbye');
sayHello('Alice')  // 'Hello, Alice!'
sayBye('Alice')    // 'Goodbye, Alice!'

// Practical: module pattern with private state
const userModule = (() => {
  let _users = [];   // private — not accessible outside

  return {
    add(user)   { _users.push(user); },
    getAll()    { return [..._users]; },  // return a copy
    count()     { return _users.length; }
  };
})();  // IIFE: immediately invoked function expression

userModule.add({ name: 'Alice' });
userModule.count();   // 1
userModule._users;    // undefined — private!
```

---

## 5.7 `this` Keyword — The Most Confusing Part for Java Developers

In Java, `this` always refers to the current instance. In JavaScript, **`this` depends on how the function is called**, not where it's defined.

```javascript
// Regular function: this = whoever called it
const obj = {
  name: 'Alice',
  greet() {
    console.log(this.name);  // 'Alice' (called as obj.greet())
  }
};
obj.greet();  // 'Alice'

const fn = obj.greet;
fn();  // undefined in strict mode / global in sloppy mode — 'this' was lost!

// Arrow function: this = enclosing scope's this (lexical this)
const obj2 = {
  name: 'Bob',
  greet() {
    const arrow = () => console.log(this.name);  // this = obj2, always
    arrow();
  }
};
obj2.greet();  // 'Bob' ✅
```

### The Classic Problem: Callbacks Losing `this`

```javascript
class Timer {
  constructor() { this.seconds = 0; }

  start() {
    // ❌ regular function: this is undefined inside setTimeout callback
    setInterval(function() {
      this.seconds++;  // TypeError!
    }, 1000);

    // ✅ arrow function: this = Timer instance (lexical binding)
    setInterval(() => {
      this.seconds++;  // works correctly
    }, 1000);
  }
}
```

### Explicit Binding

```javascript
function introduce(greeting, punctuation) {
  return `${greeting}, I'm ${this.name}${punctuation}`;
}
const alice = { name: 'Alice' };

introduce.call(alice, 'Hello', '!')       // call: args listed
introduce.apply(alice, ['Hello', '!'])    // apply: args as array
const aliceIntro = introduce.bind(alice); // bind: returns new function with this fixed
aliceIntro('Hi', '.');                    // 'Hi, I\'m Alice.'
```

> **In modern JavaScript:** Arrow functions solve 90% of `this` problems. Use them in callbacks. Use regular function syntax only for top-level functions and object methods (where you WANT dynamic `this`).

---

## 5.8 Pure Functions and Side Effects

A **pure function**: same input always → same output, no side effects.

```javascript
// Pure — easy to test, predictable, cacheable
const add = (a, b) => a + b;
const formatName = (first, last) => `${last}, ${first}`;

// Impure — modifies external state (side effect)
let total = 0;
function addToTotal(n) {
  total += n;   // side effect!
  return total;
}

// Impure — depends on external state
function getPrice(id) {
  return db.find(id).price;  // depends on database state
}
```

**Aim for pure functions.** They are trivial to unit test, safe to parallelise, and don't cause hidden bugs.

---

## 5.9 IIFE — Immediately Invoked Function Expression

```javascript
// Define and call immediately
(function() {
  const localVar = 'I am private';
  console.log(localVar);
})();

// Arrow IIFE
(() => {
  console.log('Arrow IIFE');
})();

// Used for the module pattern (pre-ES6 modules)
const MyModule = (() => {
  const private_data = 'secret';
  return {
    getData() { return private_data; }
  };
})();
```

---

## 5.10 Function Composition

Building complex behaviour by combining small functions:

```javascript
// Manual composition
const double = x => x * 2;
const addOne = x => x + 1;
const square = x => x * x;

// Compose two functions: f(g(x))
const compose = (f, g) => x => f(g(x));
const doubleAndAdd = compose(addOne, double);
doubleAndAdd(5)  // addOne(double(5)) = addOne(10) = 11

// Pipe (left-to-right composition)
const pipe = (...fns) => x => fns.reduce((v, f) => f(v), x);
const process = pipe(double, addOne, square);
process(3)  // square(addOne(double(3))) = square(addOne(6)) = square(7) = 49
```

---

## Key Takeaways

- Functions are values — store, pass, and return them freely.
- Arrow functions have **lexical `this`** — they don't create their own `this`. Use them in callbacks.
- Closures give functions memory — foundation of module patterns, factories, and async patterns.
- `this` in regular functions depends on **how** the function is called, not where it's defined.
- `map`, `filter`, `reduce` are the backbone of functional JavaScript — master them.
- Pure functions (no side effects) are easier to test and reason about.

---

## Self-Check Questions

1. What is the difference between a function declaration and a function expression in terms of hoisting?
2. Why do arrow functions solve the `this` problem in class callbacks?
3. What is a closure? Write a `makeCounter()` that demonstrates one, with private state.
4. What is the difference between `.call()`, `.apply()`, and `.bind()`?
5. What is the difference between `map()` and `forEach()`?
6. Implement a `compose(f, g)` function that returns `f(g(x))`.
