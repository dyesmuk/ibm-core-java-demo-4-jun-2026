# JS Basics — Module 08: Asynchronous JavaScript

## 8.1 Why Asynchronous?

JavaScript is **single-threaded** — only one piece of code runs at a time. But many operations are slow: reading files, making HTTP requests, querying databases. In Java you'd create new threads. In JavaScript you use **asynchronous patterns** that let other code run while waiting.

```
Thread-based (Java):           Event-loop (JavaScript):
┌─────────────────────────┐   ┌──────────────────────────┐
│ Thread 1: request A     │   │ Call Stack               │
│ Thread 2: request B     │   │  main()                  │
│ Thread 3: database call │   │                          │
│ (can run in parallel)   │   │ Event Loop checks:       │
└─────────────────────────┘   │  - Microtask Queue       │
                               │  - Macrotask Queue       │
                               │ (single-threaded, fast!) │
                               └──────────────────────────┘
```

---

## 8.2 The Event Loop

Understanding the event loop is essential for writing correct async code:

```javascript
console.log('1');              // synchronous

setTimeout(() => {
  console.log('2');            // macrotask — runs after current + microtasks
}, 0);

Promise.resolve().then(() => {
  console.log('3');            // microtask — runs before macrotasks
});

console.log('4');              // synchronous

// Output: 1, 4, 3, 2

// Why?
// 1. Synchronous code runs first: logs 1, schedules timeout, schedules promise, logs 4
// 2. Microtask queue drains: logs 3
// 3. Macrotask queue: logs 2
```

### Task Priority Order
1. **Synchronous code** (call stack)
2. **Microtasks**: `Promise.then/catch/finally`, `queueMicrotask()`, `MutationObserver`
3. **Macrotasks**: `setTimeout`, `setInterval`, `setImmediate` (Node.js), I/O callbacks

---

## 8.3 Callbacks

The original async pattern — pass a function to be called when the operation completes:

```javascript
// Node.js fs callback pattern: (error, result)
const fs = require('fs');

fs.readFile('./data.txt', 'utf8', (err, data) => {
  if (err) {
    console.error('Error reading file:', err.message);
    return;
  }
  console.log(data);
});
console.log('Reading file...');  // prints before file contents
// Output: 'Reading file...' then (later) file contents
```

### Callback Hell — The Problem

```javascript
// Nested callbacks become unreadable ("Pyramid of Doom")
getUser(userId, (err, user) => {
  if (err) return handleError(err);
  
  getOrders(user.id, (err, orders) => {
    if (err) return handleError(err);
    
    getProducts(orders[0].id, (err, products) => {
      if (err) return handleError(err);
      
      getReviews(products[0].id, (err, reviews) => {
        if (err) return handleError(err);
        
        render({ user, orders, products, reviews });
      });
    });
  });
});
```

This is why Promises were introduced.

---

## 8.4 Promises

A **Promise** represents a value that will be available in the future:

```javascript
// Creating a Promise
const promise = new Promise((resolve, reject) => {
  // This function runs synchronously
  const success = true;
  if (success) {
    resolve('Data loaded!');     // fulfilled
  } else {
    reject(new Error('Failed')); // rejected
  }
});

// Consuming a Promise
promise
  .then(value => {
    console.log(value);   // 'Data loaded!'
    return value.toUpperCase();  // return value passes to next .then()
  })
  .then(upper => {
    console.log(upper);   // 'DATA LOADED!'
  })
  .catch(error => {
    console.error(error.message);   // runs if any .then() throws or promise rejects
  })
  .finally(() => {
    console.log('Always runs');     // cleanup: hide loading spinner, etc.
  });
```

### Promise States

```
Pending → Fulfilled (resolved)
       → Rejected
```

Once a Promise is fulfilled or rejected, it is **settled** — state cannot change.

### Promise Chaining (Solving Callback Hell)

```javascript
// Same nested callback example, rewritten with Promises
getUser(userId)
  .then(user => getOrders(user.id))
  .then(orders => getProducts(orders[0].id))
  .then(products => getReviews(products[0].id))
  .then(reviews => render(reviews))
  .catch(err => handleError(err));
```

### `Promise.resolve` and `Promise.reject`

```javascript
// Wrap a value in a resolved Promise
Promise.resolve(42).then(v => console.log(v));  // 42

// Create an immediately rejected Promise
Promise.reject(new Error('oops')).catch(err => console.error(err.message));
```

### Promise Combinators

```javascript
const p1 = fetch('/api/users');
const p2 = fetch('/api/products');
const p3 = fetch('/api/orders');

// Promise.all — all must resolve, fails fast if any rejects
const [usersRes, productsRes, ordersRes] = await Promise.all([p1, p2, p3]);
// All three requests fire simultaneously (parallel!)

// Promise.allSettled — wait for all, never throws, gives each result
const results = await Promise.allSettled([p1, p2, p3]);
results.forEach(result => {
  if (result.status === 'fulfilled') console.log(result.value);
  if (result.status === 'rejected')  console.error(result.reason);
});

// Promise.race — first to settle (resolve OR reject) wins
const fast = await Promise.race([
  fetch('/api/primary'),
  fetch('/api/fallback')
]);

// Promise.any — first to RESOLVE wins (rejects only if ALL reject)
const first = await Promise.any([p1, p2, p3]);
```

---

## 8.5 `async` / `await`

`async/await` is syntactic sugar over Promises — it makes async code look synchronous:

```javascript
// Any function with 'async' always returns a Promise
async function fetchUser(id) {
  return { id, name: 'Alice' };  // automatically wrapped in Promise.resolve()
}

// await: pause execution until Promise resolves
async function getUserData(userId) {
  const user = await fetchUser(userId);        // waits here
  const orders = await fetchOrders(user.id);   // then waits here
  return { user, orders };
}

// Error handling with try/catch
async function loadDashboard(userId) {
  try {
    const user = await getUser(userId);
    const [orders, notifications] = await Promise.all([
      getOrders(user.id),
      getNotifications(user.id)
    ]);
    return { user, orders, notifications };
  } catch (error) {
    console.error('Failed to load dashboard:', error.message);
    throw error;   // re-throw so caller can handle
  }
}

// IMPORTANT: await can only be used inside async functions
// (or at top-level in ES modules)

// ❌ Common mistake: forgetting await
async function fetchData() {
  const response = fetch('/api/data');   // forgot await — response is a Promise, not data!
  const data = response.json();          // error: response.json is not a function
}

// ✅ Correct
async function fetchData() {
  const response = await fetch('/api/data');
  const data = await response.json();
  return data;
}
```

### Sequential vs Parallel

```javascript
// Sequential: each waits for the previous (SLOW if independent)
async function slow() {
  const users    = await getUsers();     // wait 100ms
  const products = await getProducts();  // wait 100ms
  const orders   = await getOrders();    // wait 100ms
  // Total: ~300ms
}

// Parallel: fire all at once (FAST)
async function fast() {
  const [users, products, orders] = await Promise.all([
    getUsers(),     // all fire simultaneously
    getProducts(),
    getOrders()
  ]);
  // Total: ~100ms
}

// Sequential when later calls depend on earlier results
async function dependent() {
  const user = await getUser(id);             // need user first
  const orders = await getOrders(user.id);    // depends on user
  const items = await getItems(orders[0].id); // depends on orders
}
```

---

## 8.6 `fetch` API (Browser)

```javascript
// Basic GET request
async function getUsers() {
  const response = await fetch('https://api.example.com/users');
  
  if (!response.ok) {
    throw new Error(`HTTP error: ${response.status} ${response.statusText}`);
  }
  
  const users = await response.json();
  return users;
}

// POST request with JSON body
async function createUser(userData) {
  const response = await fetch('https://api.example.com/users', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(userData)
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }

  return response.json();
}

// DELETE
async function deleteUser(id) {
  const response = await fetch(`/api/users/${id}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return response.ok;
}

// With query params
async function searchUsers(query, page = 1) {
  const params = new URLSearchParams({ q: query, page, limit: 20 });
  const response = await fetch(`/api/users?${params}`);
  return response.json();
}
```

---

## 8.7 Error Handling Patterns

```javascript
// Pattern 1: try/catch in each function
async function getProfile(userId) {
  try {
    const user = await getUser(userId);
    const profile = await getProfile(user.profileId);
    return profile;
  } catch (err) {
    throw new Error(`Failed to get profile for user ${userId}: ${err.message}`);
  }
}

// Pattern 2: result tuple (inspired by Go) — avoids try/catch everywhere
async function safeAsync(promise) {
  try {
    const data = await promise;
    return [null, data];
  } catch (error) {
    return [error, null];
  }
}

const [err, user] = await safeAsync(getUser(id));
if (err) return handleError(err);
const [err2, orders] = await safeAsync(getOrders(user.id));

// Pattern 3: global error handling in Express
// (covered in Node.js module)
```

---

## 8.8 `setTimeout` and `setInterval`

```javascript
// Run once after delay (milliseconds)
const timerId = setTimeout(() => {
  console.log('runs after 2 seconds');
}, 2000);

clearTimeout(timerId);  // cancel if not yet run

// Run repeatedly
const intervalId = setInterval(() => {
  console.log('runs every second');
}, 1000);

clearInterval(intervalId);  // cancel

// Promisify setTimeout (useful for delays in async code)
const delay = ms => new Promise(resolve => setTimeout(resolve, ms));

async function main() {
  console.log('start');
  await delay(2000);
  console.log('2 seconds later');
}

// setImmediate (Node.js only): runs after current event loop iteration
setImmediate(() => console.log('immediate'));

// process.nextTick (Node.js only): runs before any I/O callbacks
process.nextTick(() => console.log('next tick'));
```

---

## 8.9 Generators and Iterators (Advanced)

```javascript
// Generator function: can pause and resume
function* counter(start = 0) {
  while (true) {
    yield start++;    // pause here, return value
  }
}

const gen = counter(5);
gen.next().value    // 5
gen.next().value    // 6
gen.next().value    // 7

// Finite generator
function* range(start, end, step = 1) {
  for (let i = start; i < end; i += step) {
    yield i;
  }
}

for (const n of range(0, 10, 2)) {
  console.log(n);   // 0 2 4 6 8
}
[...range(1, 6)]   // [1, 2, 3, 4, 5]

// Async generators (Node.js streaming)
async function* readLines(filepath) {
  const stream = createReadStream(filepath);
  for await (const chunk of stream) {
    yield chunk.toString();
  }
}

for await (const line of readLines('./data.txt')) {
  console.log(line);
}
```

---

## Key Takeaways

- JavaScript is single-threaded; asynchronous code runs through the **event loop**.
- **Microtasks** (Promises) run before **macrotasks** (setTimeout) — order matters.
- Use `async/await` — it's the most readable pattern; it compiles to Promises.
- `Promise.all()` runs independent async operations **in parallel** — use it when operations don't depend on each other.
- Always handle errors in async code with `try/catch` or `.catch()`.
- `fetch` does not throw on HTTP errors (4xx/5xx) — always check `response.ok`.

---

## Self-Check Questions

1. What is the event loop? Why does JavaScript use it instead of multiple threads?
2. What is the output of: `console.log(1); setTimeout(()=>console.log(2),0); Promise.resolve().then(()=>console.log(3)); console.log(4);`? Why?
3. What is the difference between `Promise.all` and `Promise.allSettled`?
4. Why is `await Promise.all([a(), b(), c()])` faster than three sequential `await` calls?
5. Why does `fetch` not throw on a 404 response? How do you properly check for HTTP errors?
