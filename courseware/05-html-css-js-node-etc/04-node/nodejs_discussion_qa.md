# Node.js — Discussion Questions & Answers

---

## Fundas

**1. What is Node.js?**

Node.js is a JavaScript runtime built on Chrome's V8 engine that allows JavaScript to run on the server side. It is not a language or framework — it is an environment that provides APIs for file system, networking, and OS operations that are not available in the browser.

**2. Why was Node.js created?**

Node.js was created by Ryan Dahl in 2009 to solve the problem of handling many concurrent connections efficiently. Traditional servers (Apache, Tomcat) used one thread per request — expensive and slow under load. Node.js uses a single thread with an event loop to handle thousands of concurrent connections without blocking.

**3. What makes Node.js non-blocking?**

Node.js delegates I/O operations (file reads, database queries, HTTP requests) to the operating system or libuv thread pool and registers a callback. While waiting, the event loop continues processing other requests. When the I/O completes, the callback is queued and executed — no thread sits idle waiting.

**4. What is the V8 engine?**

V8 is Google's open-source JavaScript engine written in C++ — the same engine that powers Chrome. It compiles JavaScript to native machine code (JIT compilation) for high performance. Node.js embeds V8 and adds server-side APIs (file system, networking) on top of it.

**5. Is Node.js single-threaded?**

The JavaScript execution is single-threaded — one thread runs your JavaScript code. However, Node.js internally uses a thread pool (via libuv) for CPU-intensive and blocking I/O operations like file system calls and DNS lookups. For truly CPU-bound work, use `worker_threads`.

**6. What is libuv?**

libuv is the C library that powers Node.js's event loop and asynchronous I/O. It provides the thread pool (default 4 threads) for blocking operations, the event loop itself, and cross-platform abstractions for networking, file system, and timers. The event loop is libuv's, not V8's.

**7. What is the difference between Node.js and browser JavaScript?**

Both run the same JavaScript engine (V8) but have different global objects and APIs. Browser JS has `window`, `document`, `DOM`, `fetch`, `localStorage`. Node.js has `process`, `global`, `fs`, `http`, `path`, `Buffer`. Node.js has no DOM — it cannot manipulate HTML directly.

**8. What is `npm`?**

npm (Node Package Manager) is the default package manager for Node.js — the world's largest software registry with over 2 million packages. It manages project dependencies (`package.json`), scripts (`npm run`), and version control for packages. `npm install` downloads all dependencies listed in `package.json`.

**9. What is `package.json`?**

`package.json` is the manifest file for a Node.js project. It contains the project name, version, entry point, scripts, and most importantly the list of dependencies and devDependencies. Running `npm install` reads this file and installs everything listed.

**10. What is the difference between `dependencies` and `devDependencies`?**

`dependencies` are packages needed to run the application in production (Express, Mongoose, jsonwebtoken). `devDependencies` are only needed during development (nodemon, Jest, ESLint). `npm install --only=production` skips devDependencies — used in Docker production builds.

---

## Core Concepts

**11. What is the event loop in Node.js?**

The event loop is Node.js's mechanism for executing non-blocking async operations. It runs in phases: timers (`setTimeout`/`setInterval`) → I/O callbacks → idle/prepare → poll (wait for I/O) → check (`setImmediate`) → close callbacks. After each phase, microtasks (Promises, `process.nextTick`) are drained.

**12. What is the difference between `process.nextTick` and `setImmediate`?**

`process.nextTick` callbacks run immediately after the current operation, before any I/O or timer callbacks — before the event loop moves to the next phase. `setImmediate` runs in the check phase of the next event loop iteration, after I/O. `nextTick` fires sooner but can starve I/O if used recursively.

**13. What is the difference between `setTimeout(fn, 0)` and `setImmediate(fn)`?**

Both run "as soon as possible" but in different event loop phases. `setTimeout(fn, 0)` fires in the timers phase (minimum delay is ~1ms). `setImmediate` fires in the check phase, after I/O callbacks. Inside an I/O callback, `setImmediate` always fires before `setTimeout(fn, 0)`.

**14. What are Node.js streams?**

Streams are objects that let you read or write data in chunks rather than loading everything into memory. Four types: Readable (file read, HTTP request), Writable (file write, HTTP response), Duplex (both), Transform (modify data in transit). Streams are memory-efficient for large files or continuous data.

**15. What is the `Buffer` class?**

`Buffer` is a Node.js class for handling raw binary data — images, audio, network packets, encrypted data. Unlike JavaScript strings (UTF-16), Buffers hold raw bytes. They were essential before Node.js had full `Uint8Array` support and are still used extensively in file and network operations.

**16. What is `process` in Node.js?**

`process` is a global object providing information about and control over the current Node.js process. Key properties: `process.env` (environment variables), `process.argv` (command-line arguments), `process.exit(code)` (exit the process), `process.pid` (process ID), `process.cwd()` (current working directory).

**17. What is `__dirname` and `__filename`?**

`__dirname` is the absolute path of the directory containing the current file. `__filename` is the absolute path of the current file itself. Both are available in CommonJS modules. In ES Modules (`.mjs` or `"type": "module"`), use `import.meta.url` and `path.dirname(new URL(import.meta.url).pathname)` instead.

**18. What is the CommonJS module system?**

CommonJS is Node.js's built-in module system. Each file is a module. Use `require('./module')` to import and `module.exports = value` to export. It is synchronous — modules are cached after first load so repeated `require()` calls return the same instance (singleton behaviour).

**19. What is the difference between `require()` and `import`?**

`require()` (CommonJS) is synchronous and dynamic — can be called anywhere, even inside conditions. `import` (ES Modules) is static — resolved at parse time before execution, enabling tree shaking. Node.js supports both but you cannot mix them in the same file without workarounds.

**20. What is module caching in Node.js?**

After a module is first `require()`d, Node.js caches it. Every subsequent `require()` of the same file returns the cached export — the module code does not run again. This means exported objects are singletons. Clearing `require.cache[filename]` forces a re-load (useful in testing).

---

## Built-in Modules

**21. What is the `fs` module?**

The `fs` (file system) module provides APIs to interact with the file system — read, write, delete, rename, and watch files and directories. It has sync versions (`fs.readFileSync`) and async versions (`fs.readFile` with callback or `fs.promises.readFile` with Promise). Always prefer the async versions in production.

**22. What is the `path` module?**

The `path` module provides utilities for working with file and directory paths in a cross-platform way. Key methods: `path.join()` (combine paths safely), `path.resolve()` (get absolute path), `path.dirname()` (parent directory), `path.extname()` (file extension), `path.basename()` (file name). Always use `path.join()` instead of string concatenation for paths.

**23. What is the `http` module?**

The built-in `http` module allows Node.js to create HTTP servers and make HTTP requests without any third-party library. `http.createServer((req, res) => { res.end('Hello') }).listen(3000)`. In practice, Express wraps this for a better developer experience.

**24. What is the `events` module and `EventEmitter`?**

`EventEmitter` is the foundation of Node.js's event-driven architecture. Objects that extend `EventEmitter` can emit named events (`emitter.emit('data', payload)`) and register listeners (`emitter.on('data', handler)`). Streams, HTTP servers, and many core modules extend `EventEmitter`.

**25. What is the `os` module?**

The `os` module provides operating system-related utilities: `os.cpus()` (CPU info), `os.totalmem()` / `os.freemem()` (memory), `os.hostname()`, `os.platform()` (linux/darwin/win32), `os.tmpdir()` (temp directory). Useful for diagnostics, health endpoints, and platform-specific logic.

**26. What is the `crypto` module?**

The built-in `crypto` module provides cryptographic functions — hashing (`crypto.createHash('sha256')`), HMAC, encryption/decryption, and random byte generation (`crypto.randomBytes()`). Use it for password hashing (though bcrypt is preferred), token generation, and data integrity checks.

---

## Express & Web Server

**27. What is Express.js?**

Express is a minimal, unopinionated web framework for Node.js. It provides routing, middleware support, and HTTP utilities without imposing a structure. It is the most popular Node.js framework. Everything beyond basics (authentication, validation, ORM) is handled by middleware you choose.

**28. What is middleware in Express?**

Middleware is a function with access to `req`, `res`, and `next`. It can modify the request/response, end the request-response cycle, or call `next()` to pass control to the next middleware. The order of middleware registration matters — they execute in sequence.

**29. What is the difference between `app.use()` and `app.get()`?**

`app.use(path, fn)` registers middleware that runs for ALL HTTP methods on the given path. `app.get(path, fn)` registers a route handler only for GET requests. `app.use()` is for middleware (logging, auth, body parsing); `app.get/post/put/delete()` is for route handlers.

**30. What is the request-response cycle in Express?**

A request enters the Express application and passes through middleware functions in registration order. Each middleware either ends the cycle (`res.json()`, `res.send()`) or calls `next()` to pass to the next one. If no middleware ends the cycle, Express automatically sends a 404 response.

**31. What is `express.json()` middleware?**

`express.json()` parses incoming requests with `Content-Type: application/json` and populates `req.body` with the parsed object. Without this middleware, `req.body` is `undefined`. Register it early: `app.use(express.json())`.

**32. What is route parameter vs query parameter in Express?**

Route parameters are defined in the path with `:` prefix: `/employees/:id` — accessed via `req.params.id`. Query parameters come after `?` in the URL: `/employees?dept=Engineering&page=2` — accessed via `req.query.dept`. Use route params for resource identity; query params for filters, sorting, pagination.

**33. What is an Express Router?**

`express.Router()` creates a mini Express application with its own middleware and routes. Use it to organise routes by feature into separate files: `employeeRouter` handles `/employees/*`, `departmentRouter` handles `/departments/*`. Mount them in the main app with `app.use('/employees', employeeRouter)`.

**34. What is error-handling middleware in Express?**

An error-handling middleware has four parameters: `(err, req, res, next)`. Express recognises it by the extra parameter and routes errors to it when `next(err)` is called. Define it last, after all routes: `app.use((err, req, res, next) => { res.status(err.status || 500).json({ error: err.message }) })`.

**35. What is CORS and how do you enable it in Express?**

CORS (Cross-Origin Resource Sharing) is a browser security mechanism that blocks requests from a different origin (domain/port) unless the server explicitly allows it. Enable with the `cors` middleware: `app.use(cors({ origin: 'http://localhost:4200' }))`. Without it, your Angular/React frontend cannot call the API.

---

## Async Patterns

**36. What is the callback pattern in Node.js?**

The Node.js convention for async callbacks is error-first: `(err, result) => {}`. If `err` is non-null, an error occurred; otherwise `result` contains the data. Example: `fs.readFile('file.txt', 'utf8', (err, data) => { if (err) throw err; console.log(data) })`.

**37. What is callback hell and how do you avoid it?**

Callback hell (the "pyramid of doom") is deeply nested callbacks that make code hard to read and maintain. Avoid it with: Promises (`.then()` chaining), `async/await` (flat, synchronous-looking code), or named functions instead of anonymous inline callbacks.

**38. How do you convert callback-based functions to Promises?**

Use `util.promisify()` from Node's built-in `util` module: `const readFile = util.promisify(fs.readFile)`. Then `await readFile('file.txt', 'utf8')`. Most modern Node.js APIs also have a Promise version directly: `fs.promises.readFile()`.

**39. What is `async/await` in the context of Node.js?**

`async/await` makes asynchronous Node.js code (file reads, DB queries, HTTP calls) read like synchronous code while remaining non-blocking. An `async` function returns a Promise. `await` pauses execution inside the async function only — not the entire event loop — so other requests are still handled.

**40. What happens if you `await` in a loop?**

`await` inside a `for` loop makes each iteration wait for the previous — sequential execution. To run all in parallel: `await Promise.all(items.map(item => asyncFn(item)))`. Use sequential when later iterations depend on earlier results; use parallel when they are independent (much faster).

---

## Error Handling & Security

**41. What is the difference between operational errors and programmer errors in Node.js?**

Operational errors are expected runtime failures — network timeout, file not found, invalid user input. Handle them gracefully and return appropriate HTTP responses. Programmer errors are bugs — wrong variable type, calling undefined functions. These should crash the process (or be caught by a global handler) so bugs are visible.

**42. What is an uncaught exception in Node.js?**

An uncaught exception is an error that propagates to the top without being caught by `try/catch`. Node.js emits an `uncaughtException` event. Listening to it: `process.on('uncaughtException', err => { log(err); process.exit(1) })`. Use it only for cleanup — always exit after an uncaught exception as the process may be in an unstable state.

**43. What is an unhandled Promise rejection?**

When a Promise rejects and no `.catch()` or `try/catch` handles it, Node.js emits `unhandledRejection`. In Node.js 15+, this crashes the process by default. Listen with: `process.on('unhandledRejection', (reason, promise) => { log(reason); process.exit(1) })`. Always handle Promise errors.

**44. What is Helmet.js?**

`helmet` is an Express middleware that sets security-related HTTP headers — preventing common attacks like XSS, clickjacking, MIME-type sniffing. `app.use(helmet())` sets ~11 headers with sensible defaults. It's a one-line security baseline for any Express app.

**45. What is rate limiting and why is it needed?**

Rate limiting restricts how many requests a client can make in a time window — prevents brute-force attacks, API abuse, and denial-of-service. Use `express-rate-limit`: `app.use(rateLimit({ windowMs: 15 * 60 * 1000, max: 100 }))` — 100 requests per 15 minutes per IP.

**46. How do you store passwords securely in Node.js?**

Never store plain-text passwords. Use `bcrypt`: `bcrypt.hash(password, 10)` to hash and `bcrypt.compare(input, hash)` to verify. The second argument (10) is the salt rounds — higher is more secure but slower. bcrypt is deliberately slow to make brute-force attacks impractical.

---

## JWT & Authentication

**47. What is JWT?**

JSON Web Token (JWT) is a compact, self-contained token that encodes user identity and claims as a Base64-encoded JSON payload, signed with a secret key. The server creates the token on login; the client sends it with every subsequent request. The server verifies the signature — no database lookup needed.

**48. What are the three parts of a JWT?**

A JWT has three Base64URL-encoded parts separated by dots: `header.payload.signature`. The header describes the algorithm (`HS256`). The payload contains claims (userId, role, expiry). The signature is `HMAC(header + payload, secret)` — tamper-evident. **Never put sensitive data in the payload** — it is encoded, not encrypted.

**49. Where should you store JWTs on the client?**

`httpOnly` cookies are the most secure — inaccessible to JavaScript, protected against XSS. `localStorage` is convenient but vulnerable to XSS attacks (malicious scripts can read it). For learning/demos, `localStorage` is acceptable; for production, use `httpOnly` cookies.

**50. What is the difference between authentication and authorisation?**

Authentication verifies **who you are** — checking credentials (email + password) and issuing a token. Authorisation verifies **what you can do** — checking if the authenticated user has permission to access a resource (admin vs regular user). Authentication always comes before authorisation.

---

## Database (MongoDB with Node.js)

**51. What is Mongoose?**

Mongoose is an ODM (Object Document Mapper) for MongoDB in Node.js. It adds schemas, validation, type casting, middleware (hooks), and query helpers on top of the native MongoDB driver. Define a schema once and Mongoose enforces it at the application level.

**52. What is the difference between Mongoose and the native MongoDB driver?**

The native `mongodb` driver is low-level — raw access to collections with no schema enforcement. Mongoose wraps it with schemas, models, validation, middleware, and virtuals. Use the native driver when you need maximum performance or flexibility; use Mongoose for structured applications where schemas add value.

**53. What is a Mongoose Schema?**

A Schema defines the shape of documents in a collection — field names, types, defaults, and validation rules. Example: `new Schema({ name: { type: String, required: true }, salary: { type: Number, min: 0 } })`. Schemas add structure to MongoDB's flexible document model.

**54. What is the difference between `Model.save()` and `Model.create()`?**

`new Employee(data).save()` instantiates a Mongoose document and saves it — useful when you need pre-save hooks or want to modify the document before saving. `Employee.create(data)` is shorthand that does both in one step. Both trigger `pre('save')` middleware.

**55. What is Mongoose middleware (hooks)?**

Mongoose middleware (hooks) are functions that run before or after certain operations — `pre('save')`, `post('find')`, `pre('remove')`. Common use: hash password before saving, populate related documents after find, log deletions. Register with `schema.pre('save', function(next) { ... next() })`.

---

## Performance & Best Practices

**56. What is the cluster module in Node.js?**

The `cluster` module allows Node.js to create child processes (workers) that share the same server port, enabling a multi-core system to be fully utilised. The master process forks one worker per CPU core. Each worker runs its own event loop. If a worker crashes, the master can restart it.

**57. What are worker threads in Node.js?**

`worker_threads` allows true multi-threading in Node.js for CPU-intensive tasks (image processing, cryptography, complex calculations) without blocking the event loop. Unlike the cluster module, worker threads share memory via `SharedArrayBuffer`. Use them only when profiling shows the event loop is blocked.

**58. What is PM2?**

PM2 is a production process manager for Node.js. It keeps your app alive after crashes (`pm2 start server.js`), restarts it on file changes, runs in cluster mode automatically (`--instances max`), provides log management, and shows real-time metrics (`pm2 monit`). Standard tool for running Node.js in production.

**59. What is environment variable management in Node.js?**

Sensitive configuration (DB URIs, API keys, JWT secrets) should never be hardcoded. Store them in `.env` files locally (gitignored) and read them with `process.env.MY_VAR`. The `dotenv` package loads `.env` files: `require('dotenv').config()` or `import 'dotenv/config'`. In production, set env vars via the host (Docker, Kubernetes secrets, cloud config).

**60. What is the difference between `npm install` and `npm ci`?**

`npm install` installs based on `package.json` and may update `package-lock.json`. `npm ci` (clean install) installs exactly what is in `package-lock.json`, deletes `node_modules` first, and fails if `package-lock.json` is out of sync. `npm ci` is faster and deterministic — always use it in CI/CD pipelines.

---

## Real Time

**61. API returning 500 — where do you look first?**

Check the server logs immediately — the error message and stack trace will tell you exactly what failed. Common causes: unhandled exception in a route handler, database connection failure, missing environment variable, or calling a method on `undefined`. Add global error-handling middleware to catch all unhandled errors.

**62. Express route not matching — why?**

Common causes: (1) route registered after `app.use('*', ...)` catch-all, (2) wrong HTTP method (POST handler for a GET request), (3) path mismatch (`/employees` vs `/employee`), (4) route parameter capturing something it shouldn't. Use `console.log(req.method, req.path)` to debug what's arriving.

**63. `req.body` is `undefined` — why?**

You forgot to add `express.json()` middleware before the route: `app.use(express.json())`. For form data, add `express.urlencoded({ extended: true })`. Ensure the `Content-Type: application/json` header is set in the client request.

**64. Memory leak in a Node.js app — how to diagnose?**

Watch memory with `process.memoryUsage()` or PM2 metrics. Common causes: event listeners not removed (`EventEmitter` leak), closures retaining large data, caching without limits, Promises never resolving. Use `--inspect` flag + Chrome DevTools heap snapshots to find retained objects.

**65. How do you handle file uploads in Node.js?**

Use `multer` middleware for `multipart/form-data` requests. Configure storage (disk or memory), file size limits, and file type filters. `multer.single('photo')` handles one file; `multer.array('photos', 5)` handles multiple. The file info is available in `req.file` / `req.files`.

---

## More

**66. What is Socket.io?**

Socket.io is a library for real-time, bidirectional communication between the server and browser over WebSockets (with fallback to HTTP polling). Use cases: live chat, notifications, collaborative editing, real-time dashboards. The server emits events; the client listens, and vice versa.

**67. What is the difference between REST and WebSocket?**

REST is request-response — the client always initiates. WebSocket is persistent bidirectional — both client and server can send messages at any time after the initial handshake. Use REST for standard CRUD; use WebSockets when the server needs to push updates to clients instantly.

**68. What is `nodemon`?**

`nodemon` is a development tool that watches your project files and automatically restarts the Node.js server when files change — so you don't have to manually stop and restart after every edit. Install as a devDependency: `npm install -D nodemon`. Run with `npx nodemon server.js`.

**69. What is the difference between `app.listen()` and `http.createServer()`?**

`app.listen(3000)` is an Express shorthand — internally it calls `http.createServer(app).listen(3000)`. Use `http.createServer(app)` explicitly when you need access to the server instance — for example to attach Socket.io: `const server = http.createServer(app); io.attach(server); server.listen(3000)`.

**70. What is graceful shutdown in Node.js?**

Graceful shutdown means finishing in-flight requests before stopping the server. Listen for `SIGTERM` (sent by Kubernetes, Docker, PM2 on stop): `process.on('SIGTERM', () => { server.close(() => { db.disconnect(); process.exit(0) }) })`. Without it, abrupt shutdown loses in-progress requests and can corrupt data.

---

## Answer Summary Table

| # | Question (short) | Key answer |
|---|---------|------------|
| 1 | What is Node.js? | JS runtime on V8 for server-side code |
| 3 | Non-blocking | Delegates I/O, registers callback, continues |
| 5 | Single-threaded? | JS is single-threaded; libuv pool handles I/O |
| 11 | Event loop | Phases: timers → I/O → check → microtasks |
| 12 | nextTick vs setImmediate | nextTick = before next phase; setImmediate = check phase |
| 14 | Streams | Chunk-based data flow; memory efficient |
| 19 | require vs import | CJS synchronous/dynamic; ESM static/async |
| 27 | Express | Minimal web framework for routing + middleware |
| 28 | Middleware | fn(req, res, next) — runs in registration order |
| 34 | Error middleware | 4 params (err,req,res,next) — registered last |
| 36 | Callback pattern | Error-first: (err, result) |
| 41 | Operational vs programmer errors | Expected failures vs bugs |
| 43 | Unhandled rejection | Crashes process in Node 15+; always handle |
| 46 | Password storage | bcrypt.hash() — never plain text |
| 47 | JWT | Self-contained signed token; no DB lookup |
| 50 | Auth vs authorisation | Who you are vs what you can do |
| 51 | Mongoose | ODM — adds schema/validation to MongoDB |
| 56 | cluster module | Multi-core via forked worker processes |
| 58 | PM2 | Production process manager; keeps app alive |
| 70 | Graceful shutdown | Finish in-flight requests before exiting |
