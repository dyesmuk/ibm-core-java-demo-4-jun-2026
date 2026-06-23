# JavaScript Courseware — Module 04: Control Flow

---

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
```

### Guard Clauses — Flat Is Better Than Nested

This is a key professional coding pattern. Instead of deeply nested `if` blocks, bail early:

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

// ✅ Guard clauses — flat, readable, professional
function processOrder(order) {
  if (!order)                        return 'No order';
  if (!order.isValid)                return 'Invalid order';
  if (order.items.length === 0)      return 'No items';
  if (!order.customer.hasCreditCard) return 'No credit card';
  return charge(order);
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
    break;
  case 'Saturday':
  case 'Sunday':
    console.log('Weekend');
    break;
  default:
    console.log('Invalid day');
}
```

Important: `switch` uses `===` internally (strict equality). No type coercion.

Without `break`, execution **falls through** to the next case — this is intentional in some patterns but a bug if forgotten.

### Modern Alternative: Object Lookup Table

For many `switch` cases that map to values or functions, an object is cleaner:

```javascript
// Instead of a long switch for values:
const dayNames = {
  0: 'Sunday', 1: 'Monday', 2: 'Tuesday',
  3: 'Wednesday', 4: 'Thursday', 5: 'Friday', 6: 'Saturday'
};
const today = dayNames[new Date().getDay()] ?? 'Unknown';

// Handler dispatch pattern (common in Node.js / React state management)
const handlers = {
  'CREATE': handleCreate,
  'UPDATE': handleUpdate,
  'DELETE': handleDelete
};
const handler = handlers[action.type];
if (handler) handler(action.payload);
else throw new Error(`Unknown action: ${action.type}`);
```

---

## 4.3 Loops

### `for` Loop — Classic

```javascript
for (let i = 0; i < 5; i++) {
  console.log(i);   // 0 1 2 3 4
}

// Reverse
for (let i = arr.length - 1; i >= 0; i--) {
  console.log(arr[i]);
}
```

### `while` and `do...while`

```javascript
let count = 0;
while (count < 5) {
  console.log(count);
  count++;
}

// do...while: always runs at least once
let attempts = 0;
do {
  attempts++;
  result = tryConnect();
} while (!result && attempts < 3);
```

### `for...of` — Use This for Arrays and Iterables

This is the preferred modern loop for arrays. It gives you **values**:

```javascript
const fruits = ['apple', 'banana', 'cherry'];

for (const fruit of fruits) {
  console.log(fruit);   // apple, banana, cherry
}

// With index — use entries()
for (const [index, fruit] of fruits.entries()) {
  console.log(`${index}: ${fruit}`);
  // 0: apple
  // 1: banana
  // 2: cherry
}

// Works on any iterable: strings, Sets, Maps, generators
for (const char of 'hello') {
  console.log(char);   // h e l l o
}

for (const [key, value] of new Map([['a', 1], ['b', 2]])) {
  console.log(`${key} = ${value}`);
}
```

### `for...in` — For Object Keys (Use Carefully)

`for...in` iterates over **all enumerable property keys** including inherited ones:

```javascript
const person = { name: 'Alice', age: 30, city: 'Bengaluru' };

for (const key in person) {
  console.log(`${key}: ${person[key]}`);
}

// WARNING: includes inherited properties too
// Safer pattern:
for (const key in obj) {
  if (Object.prototype.hasOwnProperty.call(obj, key)) {
    // only own properties
  }
}
```

> **Prefer this over `for...in` for objects:**

```javascript
Object.keys(person)    // ['name', 'age', 'city']
Object.values(person)  // ['Alice', 30, 'Bengaluru']
Object.entries(person) // [['name','Alice'], ['age',30], ['city','Bengaluru']]

// Loop over entries — clean and safe
for (const [key, value] of Object.entries(person)) {
  console.log(`${key}: ${value}`);
}
```

### `break` and `continue`

```javascript
for (let i = 0; i < 10; i++) {
  if (i === 5) break;      // exits loop
  console.log(i);          // 0 1 2 3 4
}

for (let i = 0; i < 10; i++) {
  if (i % 2 === 0) continue;  // skip even
  console.log(i);              // 1 3 5 7 9
}

// Labeled break for nested loops (rare but useful)
outer: for (let i = 0; i < 3; i++) {
  for (let j = 0; j < 3; j++) {
    if (i === 1 && j === 1) break outer;  // exits BOTH loops
    console.log(i, j);
  }
}
```

---

## 4.4 Error Handling

### `try / catch / finally`

```javascript
try {
  const data = JSON.parse('invalid json');
} catch (error) {
  console.error('Parse error:', error.message);
} finally {
  console.log('Always runs — cleanup here');
}

// Optional catch binding (ES2019) — when you don't need the error
try {
  return JSON.parse(text);
} catch {
  return null;
}
```

### Throwing Errors

```javascript
function divide(a, b) {
  if (b === 0) throw new Error('Division by zero');
  return a / b;
}

// Rethrowing with context
function readConfig(path) {
  try {
    const data = fs.readFileSync(path, 'utf8');
    return JSON.parse(data);
  } catch (error) {
    if (error.code === 'ENOENT') {
      throw new Error(`Config file not found: ${path}`);  // wrap with context
    }
    throw error;  // rethrow unknown errors unchanged
  }
}
```

### Built-in Error Types

```javascript
new Error('Something went wrong')     // generic
new TypeError('Expected a string')    // wrong type passed
new RangeError('Index out of bounds') // value out of valid range
new ReferenceError('x is not defined')// undeclared variable
new SyntaxError('Unexpected token')   // invalid syntax
```

### Custom Error Classes (Use in Every Node.js/Express App)

```javascript
class AppError extends Error {
  constructor(message, statusCode = 500) {
    super(message);
    this.name = 'AppError';
    this.statusCode = statusCode;
    this.isOperational = true;  // distinguishes expected errors from bugs
    Error.captureStackTrace(this, this.constructor);
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

// Express error handler (middleware)
app.use((err, req, res, next) => {
  if (err instanceof AppError) {
    return res.status(err.statusCode).json({ error: err.message });
  }
  console.error(err.stack);
  res.status(500).json({ error: 'Internal server error' });
});
```

---

## 4.5 Destructuring in Loops

```javascript
const users = [
  { id: 1, name: 'Alice', role: 'admin' },
  { id: 2, name: 'Bob',   role: 'user'  }
];

// Destructure directly in loop
for (const { id, name, role } of users) {
  console.log(`${id}: ${name} (${role})`);
}

// Destructure Object.entries
const config = { host: 'localhost', port: 3000, debug: false };
for (const [key, value] of Object.entries(config)) {
  console.log(`${key} = ${value}`);
}
```

---

## Key Takeaways

- Use **guard clauses** (early returns) to flatten deeply nested `if` logic — it's a professional pattern.
- `for...of` for arrays and iterables; `Object.entries()` for objects.
- Avoid `for...in` on arrays — use it only on plain objects and guard with `hasOwnProperty`.
- Create **custom Error subclasses** for domain errors in your Node.js apps.
- `switch` uses `===` — no coercion. Don't forget `break`.

---

## Self-Check Questions

1. What is the difference between `for...in` and `for...of`? When should you use each?
2. Rewrite this with guard clauses: `if (user) { if (user.isActive) { doThing() } }`
3. How do you create a custom error class with a `statusCode` property?
4. What does `const port = process.env.PORT || 3000` do? Why might `??` be better?
5. What is the "object lookup table" pattern and when would you use it instead of `switch`?
