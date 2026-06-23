# JavaScript Courseware — Module 09: Asynchronous JavaScript

---

## 9.1 Why Asynchronous?

JavaScript is **single-threaded** — only one piece of code runs at a time. But real applications are full of slow operations: reading files, making HTTP requests, querying databases. In Java you'd create new threads. In JavaScript you use **asynchronous patterns** that let other code run while waiting.

```
Thread-based (Java):              Event Loop (JavaScript):
┌────────────────────────────┐    ┌─────────────────────────────┐
│ Thread 1: HTTP request     │    │  Call Stack                 │
│ Thread 2: DB query         │    │   handleRequest()           │
│ Thread 3: File read        │    │                             │
│  (true parallelism)        │    │  Event Loop checks:         │
└────────────────────────────┘    │   1. Microtask Queue        │
                                   │   2. Macrotask Queue        │
                                   │  (single-threaded, fast!)   │
                                   └─────────────────────────────┘
```

---

## 9.2 The Event Loop

Understanding this is essential. Execution priority order:

1. **Synchronous code** (call stack) — runs first
2. **Microtasks** — `Promise.then/catch/finally`, `queueMicrotask()`
3. **Macrotasks** — `setTimeout`, `setInterval`, I/O callbacks

```javascript
console.log('1');           // sync

setTimeout(() => {
  console.log('2');         // macrotask — runs last
}, 0);

Promise.resolve().then(() => {
  console.log('3');         // microtask — runs before macrotask
});

console.log('4');           // sync

// Output: 1, 4, 3, 2
```

Why? Sync runs first (logs 1, schedules timeout, schedules promise, logs 4). Then microtask queue drains (logs 3). Then macrotask queue runs (logs 2).

---

## 9.3 Callbacks

The original async pattern — pass a function to be called when done:

```javascript
// Node.js callback convention: first arg = error, second = result
const fs = require('fs');

fs.readFile('./data.txt', 'utf8', (err, data) => {
  if (err) {
    console.error('Error:', err.message);
    return;
  }
  console.log(data);
});
console.log('Reading...');  // prints BEFORE file contents
```

### Callback Hell — The Problem Callbacks Created

```javascript
// Nested callbacks = "Pyramid of Doom" — unreadable, hard to debug
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
// Error handling: repeated 4 times. Flow: impossible to follow.
```

This is why Promises were introduced in ES6.

---

## 9.4 Promises

A **Promise** represents a value that will be available in the future (or a failure). Three states: **Pending → Fulfilled / Rejected**.

```javascript
// Creating a Promise
const fetchData = new Promise((resolve, reject) => {
  // This executor function runs synchronously
  const success = true;
  if (success) {
    resolve({ id: 1, name: 'Alice' });   // fulfilled with a value
  } else {
    reject(new Error('Fetch failed'));    // rejected with an error
  }
});

// Consuming a Promise
fetchData
  .then(user => {
    console.log(user.name);     // 'Alice'
    return user.name.toUpperCase();  // return value passes to next .then()
  })
  .then(upper => {
    console.log(upper);         // 'ALICE'
  })
  .catch(error => {
    console.error(error.message);  // catches ANY rejection in the chain
  })
  .finally(() => {
    console.log('Done');        // always runs — hide spinner, cleanup, etc.
  });
```

### Promise Chaining — Solving Callback Hell

```javascript
// Same as the callback hell example above — flat and readable
getUser(userId)
  .then(user => getOrders(user.id))
  .then(orders => getProducts(orders[0].id))
  .then(products => getReviews(products[0].id))
  .then(reviews => render(reviews))
  .catch(err => handleError(err));  // one catch handles ALL errors
```

### Promise Combinators — Run Multiple Promises

```javascript
const p1 = fetchUsers();
const p2 = fetchProducts();
const p3 = fetchOrders();

// Promise.all — ALL must resolve; fails fast if ANY rejects
const [users, products, orders] = await Promise.all([p1, p2, p3]);
// All three fire SIMULTANEOUSLY (parallel!) — much faster than sequential

// Promise.allSettled — wait for ALL, never throws, gives each result status
const results = await Promise.allSettled([p1, p2, p3]);
results.forEach(result => {
  if (result.status === 'fulfilled') console.log(result.value);
  if (result.status === 'rejected')  console.error(result.reason);
});

// Promise.race — first to SETTLE (resolve OR reject) wins
const fastest = await Promise.race([fetchFromPrimary(), fetchFromBackup()]);

// Promise.any — first to RESOLVE wins; rejects only if ALL reject (ES2021)
const first = await Promise.any([p1, p2, p3]);
```

---

## 9.5 `async` / `await` — The Modern Way

`async/await` is syntactic sugar over Promises — makes async code look and behave like synchronous code. Use this as your default.

```javascript
// async keyword: function always returns a Promise
async function fetchUser(id) {
  return { id, name: 'Alice' };  // auto-wrapped in Promise.resolve()
}

// await: pause execution until Promise resolves
async function getDashboard(userId) {
  const user = await fetchUser(userId);       // waits here
  const orders = await fetchOrders(user.id);  // then waits here
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
    console.error('Load failed:', error.message);
    throw error;   // re-throw so caller can handle too
  } finally {
    hideSpinner();
  }
}
```

### Sequential vs Parallel — Know the Difference

```javascript
// ❌ Sequential — slow (300ms total if each takes 100ms)
async function slow() {
  const users    = await getUsers();      // wait 100ms
  const products = await getProducts();   // then wait 100ms
  const orders   = await getOrders();     // then wait 100ms
}

// ✅ Parallel — fast (100ms total)
async function fast() {
  const [users, products, orders] = await Promise.all([
    getUsers(),
    getProducts(),
    getOrders()
  ]);
}

// Sometimes sequential is necessary (when later depends on earlier)
async function dependent() {
  const user   = await getUser(id);           // need user.id first
  const orders = await getOrders(user.id);    // depends on user
}
```

### Common Mistake: Forgetting `await`

```javascript
// ❌ Forgetting await — response is a Promise object, not the actual response
async function fetchData() {
  const response = fetch('/api/data');   // forgot await!
  const data = response.json();          // TypeError: response.json is not a function
}

// ✅ Correct
async function fetchData() {
  const response = await fetch('/api/data');
  const data = await response.json();
  return data;
}
```

---

## 9.6 `fetch` API

The modern way to make HTTP requests in the browser (and Node.js 18+):

```javascript
// GET request
async function getUsers() {
  const response = await fetch('https://api.example.com/users');

  // fetch does NOT throw on 4xx/5xx! Check response.ok
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
  }

  return response.json();  // parse JSON body
}

// POST with JSON body
async function createUser(userData) {
  const response = await fetch('/api/users', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(userData)
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message);
  }

  return response.json();
}

// PUT / PATCH
async function updateUser(id, changes) {
  const response = await fetch(`/api/users/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(changes)
  });
  return response.json();
}

// DELETE
async function deleteUser(id) {
  const response = await fetch(`/api/users/${id}`, { method: 'DELETE' });
  return response.ok;
}

// Query parameters
async function searchUsers(query, page = 1) {
  const params = new URLSearchParams({ q: query, page, limit: 20 });
  const response = await fetch(`/api/users?${params}`);
  return response.json();
}
```

---

## 9.7 Error Handling Patterns

```javascript
// Pattern 1: try/catch (standard)
async function getProfile(userId) {
  try {
    const user = await getUser(userId);
    return await fetchProfile(user.profileId);
  } catch (err) {
    throw new Error(`Profile fetch failed for user ${userId}: ${err.message}`);
  }
}

// Pattern 2: Go-style tuple [error, data] — avoids deeply nested try/catch
async function safeAsync(promise) {
  try {
    return [null, await promise];
  } catch (error) {
    return [error, null];
  }
}

const [err, user] = await safeAsync(getUser(id));
if (err) return res.status(404).json({ error: err.message });

const [err2, orders] = await safeAsync(getOrders(user.id));
if (err2) return res.status(500).json({ error: err2.message });
```

---

## 9.8 Timers

```javascript
// setTimeout — run once after delay (milliseconds)
const timerId = setTimeout(() => {
  console.log('runs after 2 seconds');
}, 2000);

clearTimeout(timerId);  // cancel if not yet fired

// setInterval — run repeatedly
const intervalId = setInterval(() => {
  console.log('every second');
}, 1000);

clearInterval(intervalId);  // cancel

// Promisify setTimeout — useful in async functions
const delay = ms => new Promise(resolve => setTimeout(resolve, ms));

async function retry(fn, times, delayMs) {
  for (let i = 0; i < times; i++) {
    try {
      return await fn();
    } catch (err) {
      if (i === times - 1) throw err;
      console.log(`Retrying in ${delayMs}ms...`);
      await delay(delayMs);
    }
  }
}
```

---

## 9.9 Async Iteration

```javascript
// for await...of — iterate async iterables
async function processResponses(urls) {
  for (const url of urls) {
    const response = await fetch(url);
    const data = await response.json();
    console.log(data);
  }
}

// Async generators (advanced — useful for streaming)
async function* paginate(url) {
  let page = 1;
  while (true) {
    const response = await fetch(`${url}?page=${page}`);
    const data = await response.json();
    if (data.items.length === 0) break;
    yield data.items;
    page++;
  }
}

for await (const items of paginate('/api/products')) {
  items.forEach(item => console.log(item.name));
}
```

---

## Key Takeaways

- JavaScript is single-threaded; it uses the **event loop** to handle async ops — not threads.
- **Microtasks** (Promises) run before **macrotasks** (setTimeout) — order matters.
- Use `async/await` — most readable. It compiles to Promises.
- `Promise.all()` runs independent ops **in parallel** — use it when results don't depend on each other.
- `fetch` does **NOT** throw on 4xx/5xx — always check `response.ok`.
- Always handle errors: `try/catch` in async functions, `.catch()` in Promise chains.

---

## Self-Check Questions

1. What is the event loop? Why does JavaScript use it instead of multiple threads?
2. What is the output of: `console.log(1); setTimeout(()=>console.log(2), 0); Promise.resolve().then(()=>console.log(3)); console.log(4);`
3. What is the difference between `Promise.all` and `Promise.allSettled`?
4. Why is `await Promise.all([a(), b(), c()])` faster than three sequential `await` calls?
5. Why does `fetch` not throw on a 404 response? How do you properly handle HTTP errors?
6. What happens if you use `async/await` sequentially for independent API calls? How do you make them parallel?
