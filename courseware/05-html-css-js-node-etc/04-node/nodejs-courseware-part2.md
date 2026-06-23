# Node.js Courseware — Part 2
## Debugging · Async · Web Servers · Browser APIs

---

# MODULE 5 — Debugging Node.js

## 🧠 The Concept

`console.log` debugging gets old fast. Real debugging means setting breakpoints, inspecting variables live, and understanding stack traces. Let's level up.

## 💻 Hands-On

### Reading Stack Traces

When Node throws an error, read it bottom-up:

```javascript
// buggy.js
function getUserAge(user) {
  return user.profile.age   // user.profile might be undefined!
}

const user = { name: 'Rahul' }  // no profile!
console.log(getUserAge(user))
```

```bash
node buggy.js
```

Output:
```
TypeError: Cannot read properties of undefined (reading 'age')
    at getUserAge (/project/buggy.js:2:24)   ← where it broke
    at Object.<anonymous> (/project/buggy.js:5:13)  ← called from here
    ...
```

**Read it like:** Line 2 of `buggy.js` called `user.profile.age` but `user.profile` was `undefined`.

### The Node.js Debugger (Built-in)

```bash
node inspect app.js
```

Commands:
- `cont` (or `c`) — continue to next breakpoint
- `next` (or `n`) — step to next line
- `step` (or `s`) — step into function
- `out` (or `o`) — step out of function
- `repl` — open REPL at current point to inspect variables

### VS Code Debugger (The One You'll Actually Use)

**Method 1: Auto-attach**

1. Open VS Code terminal
2. Run: `node --inspect app.js`
3. VS Code auto-detects and attaches

**Method 2: Launch Config (`.vscode/launch.json`)**

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "node",
      "request": "launch",
      "name": "Debug Node App",
      "program": "${workspaceFolder}/app.js",
      "console": "integratedTerminal"
    }
  ]
}
```

Now press `F5` — boom, debugger attached. Set breakpoints by clicking line numbers.

### Chrome DevTools for Node

```bash
node --inspect-brk app.js
```

Then open Chrome and go to `chrome://inspect`. Click "Open dedicated DevTools for Node". You get the full Chrome debugger for your Node code!

### Common Debugging Patterns

**Defensive programming:**
```javascript
function processUser(user) {
  // Guard clause
  if (!user) throw new Error('user is required')
  if (!user.email) throw new Error('user.email is required')
  
  return user.email.toLowerCase()
}
```

**Better logging with context:**
```javascript
// Instead of:
console.log(user)

// Do this:
console.log('Processing user:', JSON.stringify(user, null, 2))

// Or use util.inspect for deep objects
const util = require('util')
console.log(util.inspect(user, { depth: null, colors: true }))
```

**Error handling in async:**
```javascript
// Bad — errors silently disappear
fs.readFile('missing.txt', 'utf8', (err, data) => {
  console.log(data)  // undefined if error, and you never know
})

// Good — always check err first
fs.readFile('missing.txt', 'utf8', (err, data) => {
  if (err) {
    console.error('Failed to read file:', err.message)
    return
  }
  console.log(data)
})
```

### Using the `debug` npm Package

```bash
npm install debug
```

```javascript
// app.js
const debug = require('debug')

const log = debug('app:main')
const dbLog = debug('app:db')

log('Server starting...')
dbLog('Connecting to database...')
```

```bash
# Enable all debug output
DEBUG=* node app.js

# Enable only specific namespaces
DEBUG=app:main node app.js
DEBUG=app:* node app.js
```

This is how Express and many popular packages do internal logging — you can turn it on/off without changing code.

## 🔥 Challenge

1. Create a script with a deliberately buggy function and use VS Code breakpoints to find and fix the bug.
2. Add `debug` logging to your notes CLI from Module 4 with namespaces `notes:read` and `notes:write`.
3. Create a `try/catch` wrapper function `safeExecute(fn)` that runs a function and prints a friendly error if it throws.

## ✅ Key Takeaways

- Stack traces read bottom-up — the bottom is where things started, the top is where they crashed
- `node --inspect` + VS Code or Chrome DevTools = real debugging
- Always handle the `err` argument in callbacks
- Use `debug` package for toggleable logging instead of `console.log` spam

---

---

# MODULE 6 — Asynchronous Node.js

## 🧠 The Concept

This is the heart of Node. Understanding async is what separates "I can write Node code" from "I actually know Node."

Node is **single-threaded** but handles thousands of concurrent operations. How? The **event loop**.

### The Event Loop (Simplified)

```
Your Code → Call Stack → Node APIs → Callback Queue → Event Loop → Call Stack
```

When you call `fs.readFile()`, Node:
1. Registers the callback
2. Hands the actual file read off to the OS
3. Keeps running your other code
4. When the file is ready, puts your callback in the queue
5. Event loop picks it up when the call stack is empty

This is why async Node can serve thousands of requests — it never waits.

## 💻 Hands-On

### Callbacks (The OG Async Pattern)

```javascript
// The classic callback pattern
const fs = require('fs')

console.log('1. Starting...')

fs.readFile('./data.txt', 'utf8', (err, data) => {
  if (err) throw err
  console.log('3. File contents:', data)
})

console.log('2. File request sent!')
// Output order: 1, 2, 3
```

**Callback Hell (what we want to avoid):**
```javascript
// 😰 Pyramid of doom
fs.readFile('file1.txt', 'utf8', (err, data1) => {
  fs.readFile('file2.txt', 'utf8', (err, data2) => {
    fs.readFile('file3.txt', 'utf8', (err, data3) => {
      fs.writeFile('combined.txt', data1 + data2 + data3, (err) => {
        console.log('Done... finally')
      })
    })
  })
})
```

### Promises (The Upgrade)

```javascript
// Promisify fs functions
const fs = require('fs').promises  // fs/promises module

async function readFile(path) {
  const data = await fs.readFile(path, 'utf8')
  return data
}

readFile('./data.txt')
  .then(data => console.log(data))
  .catch(err => console.error(err))
```

### async/await (Modern, Clean)

```javascript
const fs = require('fs').promises

async function combineFiles() {
  try {
    const [file1, file2, file3] = await Promise.all([
      fs.readFile('file1.txt', 'utf8'),
      fs.readFile('file2.txt', 'utf8'),
      fs.readFile('file3.txt', 'utf8'),
    ])
    
    await fs.writeFile('combined.txt', file1 + file2 + file3)
    console.log('✅ Files combined!')
    
  } catch (err) {
    console.error('❌ Something went wrong:', err.message)
  }
}

combineFiles()
```

`Promise.all` runs all three reads **in parallel** — much faster than sequential.

### Real-World Async: Fetching from an API

```bash
npm install node-fetch
```

```javascript
// weather.js
const fetch = require('node-fetch')

const getWeather = async (city) => {
  const response = await fetch(
    `https://wttr.in/${city}?format=j1`
  )
  
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }
  
  const data = await response.json()
  const temp = data.current_condition[0].temp_C
  const desc = data.current_condition[0].weatherDesc[0].value
  
  return { city, temp, desc }
}

const main = async () => {
  try {
    const weather = await getWeather('Chennai')
    console.log(`${weather.city}: ${weather.temp}°C — ${weather.desc}`)
  } catch (err) {
    console.error('Failed to get weather:', err.message)
  }
}

main()
```

### The EventEmitter Pattern

Node's event system is built on `EventEmitter`. Many Node built-ins (like streams, http) extend it.

```javascript
const EventEmitter = require('events')

class OrderSystem extends EventEmitter {
  placeOrder(item) {
    console.log(`Order placed: ${item}`)
    this.emit('order:placed', { item, time: new Date() })
  }
  
  shipOrder(item) {
    console.log(`Order shipped: ${item}`)
    this.emit('order:shipped', { item })
  }
}

const orders = new OrderSystem()

// Register listeners
orders.on('order:placed', (data) => {
  console.log(`📧 Email sent for: ${data.item}`)
})

orders.on('order:shipped', (data) => {
  console.log(`📦 SMS sent for: ${data.item}`)
})

orders.placeOrder('Node.js Book')
orders.shipOrder('Node.js Book')
```

### Streams — Handling Big Data

Streams let you process data chunk by chunk instead of loading it all into memory.

```javascript
const fs = require('fs')

// Bad for large files: loads EVERYTHING into memory
const data = fs.readFileSync('huge-file.txt', 'utf8')

// Good: processes chunk by chunk
const readStream = fs.createReadStream('huge-file.txt', 'utf8')
const writeStream = fs.createWriteStream('output.txt')

readStream.on('data', (chunk) => {
  console.log('Got chunk of', chunk.length, 'characters')
})

readStream.on('end', () => {
  console.log('Done reading!')
})

// Or pipe: read → write
fs.createReadStream('input.txt').pipe(fs.createWriteStream('output.txt'))
```

### Promise Utilities You Need to Know

```javascript
// Promise.all — all in parallel, fail if any fail
const results = await Promise.all([fetch(url1), fetch(url2), fetch(url3)])

// Promise.allSettled — all in parallel, get success/failure for each
const results = await Promise.allSettled([fetch(url1), fetch(url2)])
results.forEach(r => {
  if (r.status === 'fulfilled') console.log('✅', r.value)
  else console.log('❌', r.reason)
})

// Promise.race — resolves with the first one to finish
const fastest = await Promise.race([fetch(fastServer), fetch(slowServer)])

// Promise.any — resolves with first SUCCESS (ignores rejections)
const first = await Promise.any([fetch(url1), fetch(url2)])
```

## 🔥 Challenge

1. Write an async function that reads 3 JSON files in parallel and merges their contents into one object.
2. Create a simple `EventEmitter`-based pub/sub system with `subscribe(event, fn)` and `publish(event, data)`.
3. Implement a `retry(fn, times)` function that retries a failing async operation up to N times.

## ✅ Key Takeaways

- Node's event loop lets it handle concurrency without multiple threads
- Callback → Promise → async/await is the evolution of async patterns
- `async/await` is syntactic sugar over Promises — use it by default
- `Promise.all` for parallel operations, `Promise.allSettled` to handle partial failures
- Streams for large data; EventEmitter for custom event systems

---

---

# MODULE 7 — Web Servers

## 🧠 The Concept

Time to build what Node is famous for: web servers. We'll go from bare-metal `http` module all the way to Express.js.

## 💻 Hands-On

### Bare HTTP Server (Raw Node)

```javascript
// server-raw.js
const http = require('http')

const server = http.createServer((req, res) => {
  console.log(`${req.method} ${req.url}`)
  
  // Set response headers
  res.setHeader('Content-Type', 'text/plain')
  
  if (req.url === '/') {
    res.end('Welcome to my Node server! 🚀')
  } else if (req.url === '/about') {
    res.end('About page')
  } else {
    res.statusCode = 404
    res.end('Page not found')
  }
})

server.listen(3000, () => {
  console.log('Server running at http://localhost:3000')
})
```

```bash
node server-raw.js
```

Open `http://localhost:3000` in your browser.

> The raw `http` module is verbose. That's why Express exists.

### Express.js — The Go-To Framework

```bash
npm install express
```

```javascript
// server.js
const express = require('express')
const app = express()

// Middleware to parse JSON bodies
app.use(express.json())

// Routes
app.get('/', (req, res) => {
  res.send('Hello from Express! 🚀')
})

app.get('/about', (req, res) => {
  res.send('About page')
})

app.get('/users/:id', (req, res) => {
  const { id } = req.params
  res.json({ userId: id, name: 'Demo User' })
})

app.post('/users', (req, res) => {
  const { name, email } = req.body
  console.log('Creating user:', name, email)
  res.status(201).json({ message: 'User created', name, email })
})

// 404 handler (must be last)
app.use((req, res) => {
  res.status(404).json({ error: 'Route not found' })
})

const PORT = process.env.PORT || 3000
app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`)
})
```

```bash
node server.js
```

Test with curl or Postman:
```bash
curl http://localhost:3000/
curl http://localhost:3000/users/42
curl -X POST http://localhost:3000/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Arjun","email":"arjun@example.com"}'
```

### Middleware — The Secret Sauce

Middleware = functions that run between request and response.

```javascript
const express = require('express')
const app = express()

// Logging middleware
const logger = (req, res, next) => {
  const start = Date.now()
  console.log(`→ ${req.method} ${req.url}`)
  
  res.on('finish', () => {
    const duration = Date.now() - start
    console.log(`← ${res.statusCode} (${duration}ms)`)
  })
  
  next()  // CRITICAL: call next() to pass control to the next middleware
}

// Auth middleware
const requireAuth = (req, res, next) => {
  const token = req.headers.authorization
  
  if (!token) {
    return res.status(401).json({ error: 'Authentication required' })
  }
  
  // In reality, you'd verify the token here
  req.user = { id: 1, name: 'Authenticated User' }
  next()
}

// Apply logger to ALL routes
app.use(express.json())
app.use(logger)

// Public route
app.get('/public', (req, res) => {
  res.json({ message: 'Anyone can see this' })
})

// Protected route — only users with auth header
app.get('/dashboard', requireAuth, (req, res) => {
  res.json({ message: `Welcome, ${req.user.name}!` })
})

app.listen(3000)
```

### Serving Static Files

```javascript
const express = require('express')
const path = require('path')
const app = express()

// Serve everything in the 'public' folder
app.use(express.static(path.join(__dirname, 'public')))

app.listen(3000)
```

Create `public/index.html`:
```html
<!DOCTYPE html>
<html>
<head><title>My Node App</title></head>
<body>
  <h1>Hello from static files! 🎉</h1>
</body>
</html>
```

Visit `http://localhost:3000/` and you'll see the HTML.

### Router — Organizing Routes

```javascript
// routes/users.js
const express = require('express')
const router = express.Router()

// In-memory data (we'll use MongoDB later)
let users = [
  { id: 1, name: 'Priya', email: 'priya@example.com' },
  { id: 2, name: 'Arjun', email: 'arjun@example.com' },
]

router.get('/', (req, res) => {
  res.json(users)
})

router.get('/:id', (req, res) => {
  const user = users.find(u => u.id === parseInt(req.params.id))
  if (!user) return res.status(404).json({ error: 'User not found' })
  res.json(user)
})

router.post('/', (req, res) => {
  const { name, email } = req.body
  const newUser = { id: users.length + 1, name, email }
  users.push(newUser)
  res.status(201).json(newUser)
})

router.delete('/:id', (req, res) => {
  const id = parseInt(req.params.id)
  users = users.filter(u => u.id !== id)
  res.json({ message: 'User deleted' })
})

module.exports = router
```

```javascript
// server.js
const express = require('express')
const usersRouter = require('./routes/users')

const app = express()
app.use(express.json())
app.use('/api/users', usersRouter)

app.listen(3000, () => console.log('Server running!'))
```

### Error Handling Middleware

Express has special 4-argument error middleware:

```javascript
// Error handler middleware (4 params — must be LAST)
app.use((err, req, res, next) => {
  console.error(err.stack)
  res.status(err.statusCode || 500).json({
    error: err.message || 'Internal Server Error'
  })
})

// Throw errors from routes like this:
app.get('/risky', (req, res, next) => {
  try {
    throw new Error('Something broke!')
  } catch (err) {
    next(err)  // Pass to error handler
  }
})
```

### Environment Variables with dotenv

```bash
npm install dotenv
```

Create `.env`:
```
PORT=3000
NODE_ENV=development
DB_URL=mongodb://localhost:27017/myapp
JWT_SECRET=supersecretkey123
```

```javascript
// Load at the very top of your main file
require('dotenv').config()

const PORT = process.env.PORT || 3000
const DB_URL = process.env.DB_URL
```

> **Never commit `.env` to git!** Add it to `.gitignore`.

## 🔥 Challenge

1. Build an Express server for a "Product Catalog" with full CRUD: `GET /products`, `GET /products/:id`, `POST /products`, `PUT /products/:id`, `DELETE /products/:id`. Use in-memory array.
2. Add request logging middleware that writes logs to a `logs/requests.log` file (one line per request with timestamp, method, URL, status code).
3. Add input validation middleware for POST/PUT that rejects requests missing required fields.

## ✅ Key Takeaways

- `http.createServer` is raw Node; Express wraps it with convenience
- Express routes: `app.get/post/put/delete(path, handler)`
- Middleware runs between request and response; always call `next()` unless sending a response
- Routers keep your code organized by resource
- `.env` + `dotenv` for configuration; never hardcode secrets
- Error handling middleware takes `(err, req, res, next)` — 4 arguments, placed last

---

---

# MODULE 8 — Accessing API from Browser

## 🧠 The Concept

Your Express server is running. Now you want a frontend (HTML + JS) to talk to it. This sounds simple but there's a sneaky problem: **CORS**.

**CORS** (Cross-Origin Resource Sharing) = the browser's way of blocking requests between different origins (protocol + domain + port combos).

If your frontend is at `http://localhost:5500` and your API is at `http://localhost:3000` — different ports = different origins = browser blocks it.

## 💻 Hands-On

### Setting Up CORS in Express

```bash
npm install cors
```

```javascript
// server.js
const express = require('express')
const cors = require('cors')
const app = express()

// Allow all origins (development only!)
app.use(cors())

// OR be specific about which origins are allowed
app.use(cors({
  origin: ['http://localhost:5500', 'http://localhost:5173', 'https://myapp.com'],
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}))

app.use(express.json())

// Sample API data
const tasks = [
  { id: 1, text: 'Learn Node.js', done: false },
  { id: 2, text: 'Build an API', done: false },
  { id: 3, text: 'Deploy to cloud', done: false },
]

app.get('/api/tasks', (req, res) => res.json(tasks))

app.post('/api/tasks', (req, res) => {
  const task = { id: Date.now(), text: req.body.text, done: false }
  tasks.push(task)
  res.status(201).json(task)
})

app.patch('/api/tasks/:id', (req, res) => {
  const task = tasks.find(t => t.id === parseInt(req.params.id))
  if (!task) return res.status(404).json({ error: 'Not found' })
  task.done = !task.done
  res.json(task)
})

app.delete('/api/tasks/:id', (req, res) => {
  const idx = tasks.findIndex(t => t.id === parseInt(req.params.id))
  if (idx === -1) return res.status(404).json({ error: 'Not found' })
  tasks.splice(idx, 1)
  res.json({ message: 'Deleted' })
})

app.listen(3000, () => console.log('API running at http://localhost:3000'))
```

### Frontend: Fetching from the API

Create `public/index.html`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Task Manager</title>
  <style>
    body { font-family: sans-serif; max-width: 600px; margin: 50px auto; padding: 0 20px; }
    .task { display: flex; align-items: center; gap: 10px; padding: 10px 0; border-bottom: 1px solid #eee; }
    .task.done span { text-decoration: line-through; color: #999; }
    button { cursor: pointer; }
    #error { color: red; }
  </style>
</head>
<body>
  <h1>📋 Task Manager</h1>
  
  <div>
    <input id="taskInput" type="text" placeholder="Add a task..." />
    <button onclick="addTask()">Add</button>
  </div>
  
  <p id="error"></p>
  <div id="tasks"></div>

  <script>
    const API = 'http://localhost:3000/api'

    // Load tasks on page load
    async function loadTasks() {
      try {
        const res = await fetch(`${API}/tasks`)
        const tasks = await res.json()
        renderTasks(tasks)
      } catch (err) {
        document.getElementById('error').textContent = 'Failed to load tasks: ' + err.message
      }
    }

    function renderTasks(tasks) {
      const container = document.getElementById('tasks')
      container.innerHTML = tasks.map(task => `
        <div class="task ${task.done ? 'done' : ''}">
          <input type="checkbox" ${task.done ? 'checked' : ''}
            onchange="toggleTask(${task.id})" />
          <span>${task.text}</span>
          <button onclick="deleteTask(${task.id})">🗑️</button>
        </div>
      `).join('')
    }

    async function addTask() {
      const input = document.getElementById('taskInput')
      const text = input.value.trim()
      if (!text) return

      const res = await fetch(`${API}/tasks`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text })
      })

      if (res.ok) {
        input.value = ''
        loadTasks()
      }
    }

    async function toggleTask(id) {
      await fetch(`${API}/tasks/${id}`, { method: 'PATCH' })
      loadTasks()
    }

    async function deleteTask(id) {
      await fetch(`${API}/tasks/${id}`, { method: 'DELETE' })
      loadTasks()
    }

    // Allow pressing Enter to add task
    document.addEventListener('DOMContentLoaded', () => {
      document.getElementById('taskInput').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') addTask()
      })
      loadTasks()
    })
  </script>
</body>
</html>
```

Serve it:
```javascript
// Add to server.js
app.use(express.static(path.join(__dirname, 'public')))
```

Open `http://localhost:3000` — full-stack app, right there!

### Query Parameters

```javascript
// Server: support filtering via query params
app.get('/api/tasks', (req, res) => {
  let result = [...tasks]
  
  if (req.query.done !== undefined) {
    result = result.filter(t => t.done === (req.query.done === 'true'))
  }
  
  if (req.query.search) {
    result = result.filter(t => 
      t.text.toLowerCase().includes(req.query.search.toLowerCase())
    )
  }
  
  res.json(result)
})
```

```javascript
// Frontend: build query strings
const params = new URLSearchParams({ done: 'false', search: 'node' })
const res = await fetch(`${API}/tasks?${params}`)
```

### Using Axios (Alternative to Fetch)

```bash
# Client-side via CDN
<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
```

```javascript
// Axios is nicer for more complex requests
const { data } = await axios.get(`${API}/tasks`)

await axios.post(`${API}/tasks`, { text: 'New task' })

// Axios throws on non-2xx status automatically
// Fetch requires you to check res.ok manually
```

## 🔥 Challenge

1. Add a "priority" field to tasks (low/medium/high). Update the API and frontend to support it.
2. Add a filter UI to show all/pending/completed tasks.
3. Add error handling in the frontend that shows user-friendly messages when the server is down.

## ✅ Key Takeaways

- Different ports = different origins = CORS needed
- `npm install cors` + `app.use(cors())` enables cross-origin requests
- `fetch` in the browser, `node-fetch` or `axios` in Node
- Always check `res.ok` with fetch before parsing the body
- Query params `?key=value` are accessible via `req.query` in Express
