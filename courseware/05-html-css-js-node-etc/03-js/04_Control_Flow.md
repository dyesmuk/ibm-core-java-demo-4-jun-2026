# JS Basics — Module 04: Control Flow

## 4.1 `if / else if / else`

Syntax is identical to Java:

```javascript
const score = 78;

if (score >= 90) {
  console.log('A');
} else if (score >= 80) {
  console.log('B');
} else if (score >= 70) {
  console.log('C');
} else {
  console.log('F');
}

// Single-line (no braces) — acceptable for very simple cases
if (score > 50) console.log('Pass');

// Common guard clause pattern (early return)
function processUser(user) {
  if (!user) return null;              // guard: bail early
  if (!user.isActive) return null;     // guard: bail early
  // ... main logic here
  return user.name;
}
```

---

## 4.2 `switch` Statement

```javascript
const day = 'Monday';

switch (day) {
  case 'Monday':
  case 'Tuesday':
  case 'Wednesday':
  case 'Thursday':
  case 'Friday':
    console.log('Weekday');
    break;              // without break, falls through to next case
  case 'Saturday':
  case 'Sunday':
    console.log('Weekend');
    break;
  default:
    console.log('Invalid day');
}

// switch uses === internally — no type coercion
switch (2) {
  case "2": console.log("string");  // not matched
  case 2:   console.log("number");  // matched ✅
}

// Fall-through is intentional here:
switch (phase) {
  case 'setup':
    initialise();
    // falls through intentionally
  case 'running':
    processEvents();
    break;
  case 'done':
    cleanup();
    break;
}
```

### Modern Alternative: Object Lookup

For many `switch` cases mapping to values, an object lookup is cleaner:

```javascript
// Instead of a long switch:
const dayNames = {
  0: 'Sunday',
  1: 'Monday',
  2: 'Tuesday',
  3: 'Wednesday',
  4: 'Thursday',
  5: 'Friday',
  6: 'Saturday'
};
const dayName = dayNames[new Date().getDay()] ?? 'Unknown';

// Handler dispatch pattern (common in Node.js/React)
const handlers = {
  'CREATE': handleCreate,
  'UPDATE': handleUpdate,
  'DELETE': handleDelete
};
const handler = handlers[action.type];
if (handler) handler(action.payload);
```

---

## 4.3 Loops

### `for` Loop

```javascript
// Classic for loop (identical to Java)
for (let i = 0; i < 5; i++) {
  console.log(i);   // 0 1 2 3 4
}

// Reverse
for (let i = 4; i >= 0; i--) {
  console.log(i);   // 4 3 2 1 0
}
```

### `while` and `do...while`

```javascript
let count = 0;
while (count < 5) {
  console.log(count);
  count++;
}

// do...while: always executes at least once
let input;
do {
  input = getInput();   // imagine a function that reads input
} while (input === '');
```

### `for...of` — Iterating Values (Use This for Arrays)

```javascript
const fruits = ['apple', 'banana', 'cherry'];

// for...of: value at each iteration
for (const fruit of fruits) {
  console.log(fruit);   // apple, banana, cherry
}

// With index (use entries())
for (const [index, fruit] of fruits.entries()) {
  console.log(`${index}: ${fruit}`);
  // 0: apple
  // 1: banana
  // 2: cherry
}

// Works on any iterable: strings, Sets, Maps, generators
for (const char of 'hello') {
  console.log(char);   // h, e, l, l, o
}

for (const [key, value] of new Map([['a', 1], ['b', 2]])) {
  console.log(`${key} = ${value}`);
}
```

### `for...in` — Iterating Object Keys (Use with Care)

```javascript
const person = { name: 'Alice', age: 30, city: 'NYC' };

for (const key in person) {
  console.log(`${key}: ${person[key]}`);
  // name: Alice
  // age: 30
  // city: NYC
}

// WARNING: for...in iterates prototype chain too
// Safe pattern:
for (const key in obj) {
  if (Object.prototype.hasOwnProperty.call(obj, key)) {
    // only own properties
  }
}

// Better alternative for objects: Object.keys, Object.values, Object.entries
Object.keys(person).forEach(key => console.log(key));
Object.values(person).forEach(val => console.log(val));
Object.entries(person).forEach(([key, val]) => console.log(`${key}: ${val}`));
```

---

## 4.4 `break` and `continue`

```javascript
// break: exit the loop immediately
for (let i = 0; i < 10; i++) {
  if (i === 5) break;
  console.log(i);   // 0 1 2 3 4
}

// continue: skip current iteration
for (let i = 0; i < 10; i++) {
  if (i % 2 === 0) continue;
  console.log(i);   // 1 3 5 7 9  (odd numbers only)
}

// break with label (rare — like Java's labeled break)
outer: for (let i = 0; i < 3; i++) {
  for (let j = 0; j < 3; j++) {
    if (i === 1 && j === 1) break outer;
    console.log(i, j);
  }
}
// 0 0 | 0 1 | 0 2 | 1 0  (stops at i=1, j=1)
```

---

## 4.5 Error Handling

### `try / catch / finally`

```javascript
// Basic try-catch (identical to Java syntax)
try {
  const data = JSON.parse('invalid json');
} catch (error) {
  console.error('Parse error:', error.message);
} finally {
  console.log('Always runs');
}

// Rethrowing errors
function readConfig(path) {
  try {
    const data = fs.readFileSync(path, 'utf8');
    return JSON.parse(data);
  } catch (error) {
    if (error.code === 'ENOENT') {
      throw new Error(`Config file not found: ${path}`);   // rethrow with context
    }
    throw error;   // rethrow unknown errors
  }
}

// Optional catch binding (ES2019) — when you don't need the error object
try {
  return JSON.parse(text);
} catch {
  return null;
}
```

### Built-in Error Types

```javascript
new Error('Something went wrong')          // generic
new TypeError('Expected a string')         // wrong type
new RangeError('Index out of bounds')      // value out of range
new ReferenceError('x is not defined')     // undeclared variable
new SyntaxError('Unexpected token')        // invalid syntax
new URIError('malformed URI')
new EvalError()                            // eval-related (rare)
```

### Custom Error Classes

```javascript
// Extend Error for domain-specific errors (very useful in Node.js APIs)
class AppError extends Error {
  constructor(message, statusCode = 500) {
    super(message);
    this.name = 'AppError';
    this.statusCode = statusCode;
    this.isOperational = true;    // flag for "expected" errors vs bugs
    Error.captureStackTrace(this, this.constructor);  // cleaner stack trace
  }
}

class NotFoundError extends AppError {
  constructor(resource) {
    super(`${resource} not found`, 404);
    this.name = 'NotFoundError';
  }
}

class ValidationError extends AppError {
  constructor(field, message) {
    super(message, 400);
    this.name = 'ValidationError';
    this.field = field;
  }
}

// Usage
function getUser(id) {
  const user = db.findById(id);
  if (!user) throw new NotFoundError(`User ${id}`);
  return user;
}

// Handling in Express middleware
app.use((err, req, res, next) => {
  if (err instanceof AppError) {
    return res.status(err.statusCode).json({
      error: err.message,
      field: err.field
    });
  }
  // Unexpected error
  console.error(err.stack);
  res.status(500).json({ error: 'Internal server error' });
});
```

---

## 4.6 Short-Circuit Patterns

Very common in JavaScript codebases:

```javascript
// Execute if condition is truthy
isAdmin && deleteUser(id);       // only calls deleteUser if isAdmin
user && console.log(user.name);  // guard against null

// Default with ||
const port = process.env.PORT || 3000;

// Default with ?? (preferred when 0 or "" are valid)
const timeout = config.timeout ?? 5000;

// One-liner functions using && and ||
const isEven = n => n % 2 === 0;
const abs = n => n < 0 ? -n : n;

// Conditional execution
const result = value !== null
  ? processValue(value)
  : getDefault();
```

---

## 4.7 Destructuring in Control Flow

```javascript
// Destructure in loop
const users = [
  { id: 1, name: 'Alice', role: 'admin' },
  { id: 2, name: 'Bob',   role: 'user' }
];

for (const { id, name, role } of users) {
  console.log(`${id}: ${name} (${role})`);
}

// Switch on object property
function handleEvent({ type, payload }) {
  switch (type) {
    case 'LOGIN':  return authenticate(payload);
    case 'LOGOUT': return invalidateSession(payload.userId);
    default:       throw new Error(`Unknown event: ${type}`);
  }
}
```

---

## 4.8 Practical Control Flow Patterns

### Guard Clauses (Preferred over Deep Nesting)

```javascript
// ❌ Deep nesting — hard to read
function processOrder(order) {
  if (order) {
    if (order.isValid) {
      if (order.items.length > 0) {
        if (order.customer.hasCreditCard) {
          return charge(order);
        } else {
          return 'No credit card';
        }
      } else {
        return 'No items';
      }
    } else {
      return 'Invalid order';
    }
  } else {
    return 'No order';
  }
}

// ✅ Guard clauses — flat, readable
function processOrder(order) {
  if (!order)                       return 'No order';
  if (!order.isValid)               return 'Invalid order';
  if (order.items.length === 0)     return 'No items';
  if (!order.customer.hasCreditCard) return 'No credit card';
  return charge(order);
}
```

### Pattern: Exhaustive Checks

```javascript
// TypeScript-style exhaustive switch (useful for future-proofing)
function describeShape(shape) {
  switch (shape.kind) {
    case 'circle':   return `Circle r=${shape.radius}`;
    case 'square':   return `Square s=${shape.side}`;
    case 'rectangle': return `Rect ${shape.width}×${shape.height}`;
    default:
      // If shape is typed in TypeScript, this line catches unhandled cases
      throw new Error(`Unhandled shape: ${shape.kind}`);
  }
}
```

---

## Key Takeaways

- Prefer `for...of` for arrays; `Object.keys/values/entries()` for objects; avoid `for...in`.
- Use guard clauses (early returns) to flatten nested `if` logic.
- Create custom `Error` subclasses for domain errors in Node.js apps.
- Short-circuit patterns (`&&`, `||`, `??`) replace many simple `if` statements in practice.
- `switch` uses strict equality (`===`) — no type coercion.

---

## Self-Check Questions

1. What is the difference between `for...in` and `for...of`? When should you use each?
2. Rewrite the following using guard clauses: `if (user) { if (user.isActive) { doThing() } }`
3. How do you create a custom error class with a custom `statusCode` property?
4. What does `const port = process.env.PORT || 3000` do, and what potential issue does `??` solve?
5. Why is it important to `break` in `switch` cases?
