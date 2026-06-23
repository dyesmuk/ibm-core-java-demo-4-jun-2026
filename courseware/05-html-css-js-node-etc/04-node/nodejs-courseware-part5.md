# Node.js Courseware — Part 5
## Testing Node.js · Real-Time Web with Socket.io · Wrapping Up

---

# MODULE 16 — Testing Node.js

## 🧠 The Concept

Untested code is broken code waiting to happen. Tests catch bugs before users do.

Three levels of testing:
- **Unit tests** — test a single function in isolation
- **Integration tests** — test multiple units working together
- **E2E (End-to-End) tests** — test the full app like a real user

We'll focus on **unit + integration** using **Jest** (the most popular Node testing framework).

## 💻 Hands-On

### Setup

```bash
npm install --save-dev jest supertest
```

```json
// package.json
{
  "scripts": {
    "test": "jest --watchAll",
    "test:ci": "jest --forceExit --detectOpenHandles"
  },
  "jest": {
    "testEnvironment": "node"
  }
}
```

### Unit Testing — Pure Functions

```javascript
// utils/math.js
const add = (a, b) => a + b
const subtract = (a, b) => a - b
const divide = (a, b) => {
  if (b === 0) throw new Error('Division by zero')
  return a / b
}
const average = (nums) => {
  if (!nums.length) return 0
  return nums.reduce((sum, n) => sum + n, 0) / nums.length
}

module.exports = { add, subtract, divide, average }
```

```javascript
// tests/math.test.js
const { add, subtract, divide, average } = require('../utils/math')

describe('Math utils', () => {
  describe('add()', () => {
    test('adds two positive numbers', () => {
      expect(add(2, 3)).toBe(5)
    })

    test('handles negative numbers', () => {
      expect(add(-1, -2)).toBe(-3)
    })

    test('returns a number', () => {
      expect(typeof add(1, 2)).toBe('number')
    })
  })

  describe('divide()', () => {
    test('divides correctly', () => {
      expect(divide(10, 2)).toBe(5)
    })

    test('throws on division by zero', () => {
      expect(() => divide(5, 0)).toThrow('Division by zero')
    })
  })

  describe('average()', () => {
    test('calculates average', () => {
      expect(average([1, 2, 3, 4, 5])).toBe(3)
    })

    test('returns 0 for empty array', () => {
      expect(average([])).toBe(0)
    })
  })
})
```

```bash
npm test
```

### Common Jest Matchers

```javascript
// Equality
expect(value).toBe(42)             // strict equality (===)
expect(obj).toEqual({ a: 1 })     // deep equality
expect(value).not.toBe(null)

// Truthiness
expect(value).toBeTruthy()
expect(value).toBeFalsy()
expect(value).toBeNull()
expect(value).toBeDefined()

// Numbers
expect(n).toBeGreaterThan(5)
expect(n).toBeCloseTo(3.14, 2)    // floating point with 2 decimal places

// Strings
expect(str).toMatch(/pattern/)
expect(str).toContain('substring')

// Arrays
expect(arr).toContain(item)
expect(arr).toHaveLength(3)

// Objects
expect(obj).toHaveProperty('name')
expect(obj).toMatchObject({ name: 'Priya' })  // partial match

// Errors
expect(() => fn()).toThrow()
expect(() => fn()).toThrow('specific message')

// Async
await expect(asyncFn()).resolves.toBe('value')
await expect(asyncFn()).rejects.toThrow('error')
```

### Testing Async Functions

```javascript
// utils/api.js
const fetch = require('node-fetch')

const getUser = async (id) => {
  const res = await fetch(`https://jsonplaceholder.typicode.com/users/${id}`)
  if (!res.ok) throw new Error('User not found')
  return res.json()
}

module.exports = { getUser }
```

```javascript
// tests/api.test.js
const { getUser } = require('../utils/api')

describe('getUser()', () => {
  test('fetches a user by id', async () => {
    const user = await getUser(1)
    expect(user).toHaveProperty('id', 1)
    expect(user).toHaveProperty('name')
    expect(user).toHaveProperty('email')
  })

  test('throws for invalid id', async () => {
    await expect(getUser(99999)).rejects.toThrow('User not found')
  })
})
```

### Mocking — Isolate Your Code

Mocks replace real dependencies (DB, HTTP calls) with fake ones so tests are fast and deterministic.

```javascript
// tests/email.test.js
const { sendWelcomeEmail } = require('../utils/email')

// Mock the entire email module
jest.mock('../utils/email', () => ({
  sendWelcomeEmail: jest.fn().mockResolvedValue(true)
}))

test('sendWelcomeEmail is called with correct args', async () => {
  await sendWelcomeEmail('test@example.com', 'Priya')
  expect(sendWelcomeEmail).toHaveBeenCalledWith('test@example.com', 'Priya')
  expect(sendWelcomeEmail).toHaveBeenCalledTimes(1)
})
```

### Integration Testing: REST API with Supertest

Supertest lets you make HTTP requests to your Express app without starting a real server.

```javascript
// tests/tasks.test.js
const request = require('supertest')
const mongoose = require('mongoose')
const app = require('../app')   // export app without listen()
const User = require('../models/User')
const Task = require('../models/Task')

let token
let userId

// Setup: create a test user before all tests
beforeAll(async () => {
  await mongoose.connect(process.env.MONGO_URI_TEST)
})

// Clean DB before each test
beforeEach(async () => {
  await User.deleteMany()
  await Task.deleteMany()

  // Register and get token
  const res = await request(app)
    .post('/api/auth/register')
    .send({ name: 'Test User', email: 'test@example.com', password: 'Secure1234' })
    .expect(201)

  token = res.body.token
  userId = res.body.user._id
})

afterAll(async () => {
  await mongoose.connection.close()
})

// --- Tests ---

describe('POST /api/tasks', () => {
  test('creates a task for authenticated user', async () => {
    const res = await request(app)
      .post('/api/tasks')
      .set('Authorization', `Bearer ${token}`)
      .send({ title: 'Write tests', priority: 'high' })
      .expect(201)

    expect(res.body.title).toBe('Write tests')
    expect(res.body.done).toBe(false)
    expect(res.body.owner.toString()).toBe(userId)
  })

  test('rejects task creation without auth', async () => {
    await request(app)
      .post('/api/tasks')
      .send({ title: 'Sneaky task' })
      .expect(401)
  })

  test('rejects task without title', async () => {
    await request(app)
      .post('/api/tasks')
      .set('Authorization', `Bearer ${token}`)
      .send({ priority: 'low' })
      .expect(400)
  })
})

describe('GET /api/tasks', () => {
  test('returns tasks for authenticated user', async () => {
    // Create a couple of tasks first
    await Task.create([
      { title: 'Task 1', owner: userId },
      { title: 'Task 2', owner: userId },
    ])

    const res = await request(app)
      .get('/api/tasks')
      .set('Authorization', `Bearer ${token}`)
      .expect(200)

    expect(res.body.data).toHaveLength(2)
    expect(res.body.pagination).toBeDefined()
  })

  test('filters tasks by done status', async () => {
    await Task.create([
      { title: 'Pending', done: false, owner: userId },
      { title: 'Done', done: true, owner: userId },
    ])

    const res = await request(app)
      .get('/api/tasks?done=false')
      .set('Authorization', `Bearer ${token}`)
      .expect(200)

    expect(res.body.data).toHaveLength(1)
    expect(res.body.data[0].title).toBe('Pending')
  })
})

describe('DELETE /api/tasks/:id', () => {
  test('deletes a task', async () => {
    const task = await Task.create({ title: 'Delete me', owner: userId })

    await request(app)
      .delete(`/api/tasks/${task._id}`)
      .set('Authorization', `Bearer ${token}`)
      .expect(200)

    const found = await Task.findById(task._id)
    expect(found).toBeNull()
  })

  test('cannot delete another user\'s task', async () => {
    const otherUser = await User.create({
      name: 'Other', email: 'other@example.com', password: 'Secure1234'
    })
    const task = await Task.create({ title: 'Not yours', owner: otherUser._id })

    await request(app)
      .delete(`/api/tasks/${task._id}`)
      .set('Authorization', `Bearer ${token}`)
      .expect(404)  // appears as "not found" to prevent info leakage
  })
})
```

### Separating App from Server

```javascript
// app.js — export the Express app without starting it
const express = require('express')
const mongoose = require('mongoose')
const cors = require('cors')

const app = express()
app.use(express.json())
app.use(cors())

// Routes
app.use('/api/auth', require('./routes/auth'))
app.use('/api/tasks', require('./middleware/auth'), require('./routes/tasks'))

module.exports = app
```

```javascript
// server.js — start the server separately
require('dotenv').config()
const app = require('./app')
const mongoose = require('mongoose')

mongoose.connect(process.env.MONGO_URI).then(() => {
  app.listen(process.env.PORT || 3000, () => {
    console.log('Server running!')
  })
})
```

### Test Coverage

```bash
# Run with coverage report
jest --coverage
```

Aim for >80% coverage on critical business logic. Don't chase 100% — test what matters.

## 🔥 Challenge

1. Write unit tests for your `buildQuery` utility from Module 13.
2. Write integration tests for the full user auth flow: register → login → access protected route → logout → try protected route (should fail).
3. Add a GitHub Actions workflow (`.github/workflows/test.yml`) that runs your tests on every push.

## ✅ Key Takeaways

- Jest: `describe` groups, `test` cases, `expect` assertions
- `beforeAll` / `beforeEach` for setup; `afterAll` for teardown
- Supertest: test HTTP endpoints without starting the server
- Mock external dependencies (email, external APIs) so tests are fast and isolated
- Export `app` separately from `listen()` so tests can import the app cleanly
- Use a separate test database (`MONGO_URI_TEST`)

---

---

# MODULE 17 — Real-Time Web Applications with Socket.io

## 🧠 The Concept

Regular HTTP is **request-response** — the client asks, the server answers, connection closes. For real-time features (chat, live notifications, collaborative editing, live scores), you need a **persistent connection** that allows the server to push data anytime.

**WebSockets** provide this. **Socket.io** wraps WebSockets with fallbacks and conveniences.

### How Socket.io Works

```
Client A connects → socket.io establishes WebSocket connection
Client A emits event → Server receives it
Server emits to all clients → All connected clients receive it instantly
```

## 💻 Hands-On

### Setup

```bash
npm install socket.io
```

```javascript
// server.js
const express = require('express')
const http = require('http')
const { Server } = require('socket.io')
const path = require('path')

const app = express()
const server = http.createServer(app)  // http server wraps express

const io = new Server(server, {
  cors: {
    origin: '*',  // in production, lock this down
    methods: ['GET', 'POST']
  }
})

app.use(express.static(path.join(__dirname, 'public')))

io.on('connection', (socket) => {
  console.log(`✅ Client connected: ${socket.id}`)

  socket.on('disconnect', () => {
    console.log(`❌ Client disconnected: ${socket.id}`)
  })
})

server.listen(3000, () => console.log('Server running at http://localhost:3000'))
```

> **Important:** Use `http.createServer(app)` and pass that to both `socket.io` and `.listen()`.

### Build a Live Chat App

```javascript
// server.js — chat logic
const rooms = new Map()  // track users per room

io.on('connection', (socket) => {
  console.log('Connected:', socket.id)

  // User joins a room
  socket.on('join', ({ username, room }) => {
    socket.join(room)
    socket.data.username = username
    socket.data.room = room

    // Track users in room
    if (!rooms.has(room)) rooms.set(room, new Set())
    rooms.get(room).add(username)

    // Tell everyone in the room about the new user
    socket.to(room).emit('user:joined', { username, time: new Date() })

    // Send current user list to the new user
    socket.emit('room:users', Array.from(rooms.get(room)))

    console.log(`${username} joined room: ${room}`)
  })

  // User sends a message
  socket.on('message:send', ({ text }) => {
    const { username, room } = socket.data

    const message = {
      id: Date.now(),
      text,
      username,
      time: new Date().toISOString()
    }

    // Broadcast to everyone in the room (including sender)
    io.to(room).emit('message:receive', message)
  })

  // Typing indicator
  socket.on('typing:start', () => {
    socket.to(socket.data.room).emit('typing:start', { username: socket.data.username })
  })

  socket.on('typing:stop', () => {
    socket.to(socket.data.room).emit('typing:stop', { username: socket.data.username })
  })

  // User disconnects
  socket.on('disconnect', () => {
    const { username, room } = socket.data
    if (room && rooms.has(room)) {
      rooms.get(room).delete(username)
      socket.to(room).emit('user:left', { username })
      io.to(room).emit('room:users', Array.from(rooms.get(room)))
    }
  })
})
```

### Frontend Client

```html
<!-- public/index.html -->
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Live Chat</title>
  <script src="/socket.io/socket.io.js"></script>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: sans-serif; display: flex; height: 100vh; }
    #sidebar { width: 200px; background: #1e1e2e; color: white; padding: 20px; }
    #sidebar h3 { margin-bottom: 10px; color: #cba6f7; }
    #userList { list-style: none; }
    #userList li { padding: 4px 0; font-size: 14px; }
    #main { flex: 1; display: flex; flex-direction: column; }
    #messages { flex: 1; overflow-y: auto; padding: 20px; background: #f8f9fa; }
    .message { margin-bottom: 12px; }
    .message .meta { font-size: 12px; color: #999; margin-bottom: 2px; }
    .message .text { background: white; padding: 8px 12px; border-radius: 8px; display: inline-block; }
    .message.own .text { background: #6366f1; color: white; }
    #typing { height: 20px; font-size: 12px; color: #999; padding: 0 20px; }
    #inputArea { display: flex; padding: 16px; gap: 10px; border-top: 1px solid #e5e7eb; }
    #messageInput { flex: 1; padding: 10px; border: 1px solid #e5e7eb; border-radius: 6px; font-size: 14px; }
    #sendBtn { background: #6366f1; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
    #joinForm { display: flex; flex-direction: column; gap: 12px; max-width: 300px; margin: 100px auto; padding: 30px; background: white; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
    #joinForm h2 { text-align: center; color: #6366f1; }
    #joinForm input { padding: 10px; border: 1px solid #e5e7eb; border-radius: 6px; }
    #joinForm button { background: #6366f1; color: white; border: none; padding: 12px; border-radius: 6px; cursor: pointer; font-size: 16px; }
    .system { text-align: center; font-size: 12px; color: #999; margin: 8px 0; }
  </style>
</head>
<body>

<!-- Join Form -->
<div id="joinForm">
  <h2>💬 Live Chat</h2>
  <input id="usernameInput" placeholder="Your name" />
  <input id="roomInput" placeholder="Room name (e.g. general)" value="general" />
  <button onclick="joinChat()">Join Chat</button>
</div>

<!-- Chat UI (hidden initially) -->
<div id="chatUI" style="display:none; width:100%; display:none; flex:1;">
  <div id="sidebar">
    <h3>👥 Online</h3>
    <ul id="userList"></ul>
  </div>
  <div id="main">
    <div id="messages"></div>
    <div id="typing"></div>
    <div id="inputArea">
      <input id="messageInput" placeholder="Type a message..." />
      <button id="sendBtn" onclick="sendMessage()">Send</button>
    </div>
  </div>
</div>

<script>
  const socket = io()
  let myUsername = ''
  let typingTimer

  function joinChat() {
    const username = document.getElementById('usernameInput').value.trim()
    const room = document.getElementById('roomInput').value.trim()
    if (!username || !room) return alert('Fill in both fields!')

    myUsername = username
    socket.emit('join', { username, room })

    document.getElementById('joinForm').style.display = 'none'
    document.getElementById('chatUI').style.display = 'flex'

    // Focus input
    document.getElementById('messageInput').focus()
  }

  function sendMessage() {
    const input = document.getElementById('messageInput')
    const text = input.value.trim()
    if (!text) return

    socket.emit('message:send', { text })
    input.value = ''
    socket.emit('typing:stop')
    clearTimeout(typingTimer)
  }

  // Typing indicator
  document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('messageInput').addEventListener('keypress', (e) => {
      if (e.key === 'Enter') return sendMessage()

      socket.emit('typing:start')
      clearTimeout(typingTimer)
      typingTimer = setTimeout(() => socket.emit('typing:stop'), 1000)
    })
  })

  // Socket events
  socket.on('message:receive', ({ username, text, time }) => {
    const isOwn = username === myUsername
    const timeStr = new Date(time).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })

    const el = document.createElement('div')
    el.className = `message ${isOwn ? 'own' : ''}`
    el.innerHTML = `
      <div class="meta">${isOwn ? 'You' : username} • ${timeStr}</div>
      <div class="text">${escapeHTML(text)}</div>
    `

    const messages = document.getElementById('messages')
    messages.appendChild(el)
    messages.scrollTop = messages.scrollHeight
  })

  socket.on('room:users', (users) => {
    document.getElementById('userList').innerHTML = users
      .map(u => `<li>• ${u}</li>`).join('')
  })

  socket.on('user:joined', ({ username }) => {
    addSystemMessage(`${username} joined the chat`)
  })

  socket.on('user:left', ({ username }) => {
    addSystemMessage(`${username} left the chat`)
  })

  const typingUsers = new Set()

  socket.on('typing:start', ({ username }) => {
    typingUsers.add(username)
    updateTypingIndicator()
  })

  socket.on('typing:stop', ({ username }) => {
    typingUsers.delete(username)
    updateTypingIndicator()
  })

  function updateTypingIndicator() {
    const el = document.getElementById('typing')
    if (typingUsers.size === 0) {
      el.textContent = ''
    } else {
      const names = Array.from(typingUsers).join(', ')
      el.textContent = `${names} ${typingUsers.size === 1 ? 'is' : 'are'} typing...`
    }
  }

  function addSystemMessage(text) {
    const el = document.createElement('div')
    el.className = 'system'
    el.textContent = text
    document.getElementById('messages').appendChild(el)
  }

  function escapeHTML(str) {
    return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')
  }
</script>
</body>
</html>
```

### Emitting to Different Targets

```javascript
// Just to one client
socket.emit('event', data)

// To everyone EXCEPT the sender
socket.broadcast.emit('event', data)

// To everyone in a room
io.to('room-name').emit('event', data)

// To everyone in a room EXCEPT sender
socket.to('room-name').emit('event', data)

// To everyone connected
io.emit('event', data)

// To a specific socket by ID
io.to(socketId).emit('event', data)
```

### Real-Time Notifications Example

```javascript
// server.js — push notification when a task is created

// Map userId → socketId for targeted notifications
const userSockets = new Map()

io.on('connection', (socket) => {
  socket.on('auth', (userId) => {
    userSockets.set(userId, socket.id)
    socket.on('disconnect', () => userSockets.delete(userId))
  })
})

// In your task creation route:
app.post('/api/tasks', auth, async (req, res) => {
  const task = await Task.create({ ...req.body, owner: req.user._id })
  res.status(201).json(task)

  // Push real-time notification to the user
  const socketId = userSockets.get(req.user._id.toString())
  if (socketId) {
    io.to(socketId).emit('task:created', { task })
  }
})
```

### Persisting Chat History

```javascript
// models/Message.js
const mongoose = require('mongoose')

const messageSchema = new mongoose.Schema({
  room: { type: String, required: true },
  username: { type: String, required: true },
  text: { type: String, required: true }
}, { timestamps: true })

module.exports = mongoose.model('Message', messageSchema)
```

```javascript
// In socket handler:
const Message = require('./models/Message')

socket.on('message:send', async ({ text }) => {
  const { username, room } = socket.data
  const message = await Message.create({ room, username, text })
  io.to(room).emit('message:receive', message)
})

// Send message history when user joins
socket.on('join', async ({ username, room }) => {
  socket.join(room)
  socket.data = { username, room }

  // Load last 50 messages
  const history = await Message.find({ room })
    .sort({ createdAt: -1 })
    .limit(50)
    .lean()

  socket.emit('message:history', history.reverse())
})
```

## 🔥 Challenge

1. Add **private messaging** — allow users to click on another user's name and open a DM (direct message) room.
2. Add **message reactions** — emoji reactions on messages that update in real-time for all users in the room.
3. Build a **live collaborative whiteboard** — users in the same room can draw on a canvas together in real-time.

## ✅ Key Takeaways

- Socket.io needs `http.createServer(app)` — not `app.listen()` directly
- `io.on('connection')` fires for each new connected client; each gets a unique `socket.id`
- Rooms allow grouping sockets: `socket.join(room)` and `io.to(room).emit(...)`
- Emit targets: `socket` (sender), `socket.broadcast` (all except sender), `io` (everyone), `io.to(room)` (room)
- Store minimal state on `socket.data`; persist important data to MongoDB

---

---

# MODULE 18 — Wrapping Up

## 🎉 What You've Built

Congratulations — you've covered the full Node.js ecosystem. Let's recap what you now know:

| Module | What You Learned |
|--------|-----------------|
| 1-2 | What Node is, how to install it, the REPL, `process` object |
| 3 | CommonJS modules, `require`, `module.exports`, npm packages |
| 4 | `fs` module, path handling, CLI tools with `process.argv` / `yargs` |
| 5 | Stack traces, VS Code debugger, `debug` package |
| 6 | Event loop, callbacks, Promises, `async/await`, EventEmitter, Streams |
| 7 | HTTP module, Express, middleware, routers, error handling |
| 8 | CORS, `fetch`, query params, browser ↔ API communication |
| 9 | Deployment (Render, Heroku), environment variables, PM2 |
| 10 | MongoDB CRUD, ObjectId, native driver |
| 11 | Mongoose schemas, models, validation, hooks, `populate` |
| 12 | bcrypt, JWT, auth middleware, role-based access, security headers |
| 13 | Filtering, sorting, pagination with MongoDB |
| 14 | `multer`, image processing with `sharp`, Cloudinary |
| 15 | SendGrid, Nodemailer, email templates, `node-cron` |
| 16 | Jest unit tests, Supertest integration tests, mocking |
| 17 | Socket.io, WebSockets, real-time chat, rooms |

---

## 🗺️ What to Build Next

The best way to cement this knowledge is to **build something**. Here are project ideas ranked by complexity:

### Starter Projects
- **URL Shortener** — POST a long URL, get a short code back, redirect on GET
- **Notes REST API** — full CRUD with auth; connect to a simple React/HTML frontend
- **Weather CLI** — pull from OpenWeatherMap API, format nicely in the terminal

### Intermediate Projects
- **Blog Platform** — Posts, comments, users, auth, image uploads, pagination
- **Expense Tracker** — categories, monthly reports, CSV export
- **Job Board API** — listings, applications, recruiter vs. candidate roles

### Advanced Projects
- **Real-Time Collaborative Editor** — Google Docs mini-clone with Socket.io + operational transforms
- **E-Commerce API** — products, cart, orders, payments (Stripe), email receipts
- **Social Media Backend** — follow/following, feed, likes, notifications

---

## 🛠️ Essential Tools & Libraries Reference

### Productivity
```bash
nodemon        # auto-restart on file changes (dev)
dotenv         # load .env files
morgan         # HTTP request logger
```

### Security
```bash
helmet         # set security HTTP headers
bcryptjs       # password hashing
jsonwebtoken   # JWT creation and verification
express-rate-limit  # rate limiting
express-mongo-sanitize  # prevent NoSQL injection
xss-clean      # prevent XSS attacks
```

### Database
```bash
mongoose       # MongoDB ODM
redis          # caching, sessions, queues
```

### Validation
```bash
joi            # schema-based validation
express-validator  # middleware-style validation
zod            # TypeScript-first, works great in JS too
```

### Files & Media
```bash
multer         # file uploads
sharp          # image processing
cloudinary     # cloud image/video storage
```

### Email
```bash
nodemailer     # send emails via SMTP
@sendgrid/mail # SendGrid API
```

### Testing
```bash
jest           # test runner + assertions
supertest      # HTTP testing
```

### Real-Time
```bash
socket.io      # WebSockets with fallbacks
```

### Task Scheduling
```bash
node-cron      # cron-style job scheduling
bull           # Redis-based job queues
```

---

## 📚 Recommended Learning Path After This

### Deepen Node.js
- **Streams** — deep dive into readable, writable, transform, duplex streams
- **Worker Threads** — true parallelism for CPU-heavy tasks
- **Cluster module** — take advantage of multi-core CPUs
- **Performance profiling** — `--prof`, `clinic.js`

### Expand Your Stack
- **TypeScript** — add types to your Node code; catch bugs at compile time
- **NestJS** — opinionated, TypeScript-first framework built on Express
- **Fastify** — high-performance Express alternative (2x faster)
- **Prisma** — modern ORM (works with PostgreSQL, MySQL, SQLite too)

### DevOps & Scale
- **Docker** — containerize your Node app
- **Docker Compose** — run Node + MongoDB + Redis together locally
- **Kubernetes** — orchestrate containers at scale
- **CI/CD** — GitHub Actions for automated test + deploy pipelines
- **Redis** — caching, sessions, pub/sub, job queues

---

## 📋 Final Project: Full-Stack Task Manager

Build a production-grade Task Manager with everything from this course:

**Features:**
- User registration, login, logout (JWT auth)
- CRUD tasks with priority, due dates, tags
- Filter, sort, paginate task list
- Avatar upload (Cloudinary)
- Email on registration + task reminders via cron
- Real-time notifications when tasks are updated by collaborators (Socket.io)
- Full test suite (Jest + Supertest)
- Deployed to Render with environment variables

**Tech Stack:**
- Express + Mongoose + MongoDB Atlas
- JWT + bcrypt
- Multer + Cloudinary
- SendGrid + node-cron
- Socket.io
- Jest + Supertest

This project covers every module in this course and makes a stellar portfolio piece.

---

## 🧰 Quick Reference Card

```javascript
// =============================================
// NODE.JS CHEAT SHEET
// =============================================

// --- Modules ---
const mod = require('./module')         // import
module.exports = { fn1, fn2 }          // export

// --- File System ---
const fs = require('fs').promises
const data = await fs.readFile('file.txt', 'utf8')
await fs.writeFile('out.txt', data)

// --- Express Server ---
const app = require('express')()
app.use(express.json())
app.get('/route', (req, res) => res.json({ ok: true }))
app.listen(3000)

// --- Mongoose ---
const Schema = new mongoose.Schema({ field: String })
const Model = mongoose.model('Name', Schema)
await Model.create(data)
await Model.find(filter).sort(sort).skip(skip).limit(limit)
await Model.findByIdAndUpdate(id, update, { new: true })
await Model.findByIdAndDelete(id)

// --- JWT ---
const token = jwt.sign({ _id: user._id }, SECRET, { expiresIn: '7d' })
const decoded = jwt.verify(token, SECRET)

// --- bcrypt ---
const hash = await bcrypt.hash(password, 8)
const ok = await bcrypt.compare(plain, hash)

// --- Socket.io ---
io.on('connection', socket => {
  socket.on('event', data => { /* handle */ })
  socket.emit('event', data)                    // to sender
  socket.to('room').emit('event', data)         // to room (not sender)
  io.emit('event', data)                        // to everyone
})

// --- async/await pattern ---
try {
  const result = await someAsyncFn()
} catch (err) {
  console.error(err.message)
}

// --- Middleware ---
const myMiddleware = (req, res, next) => {
  // do stuff
  next()  // or res.status(401).json(...)
}
```

---

## 🎯 You're Ready

You've gone from zero to building production-ready Node.js applications.

The next step? **Build something.** The fundamentals are in your hands — now go make something you're proud of.

> *"The best way to learn is to build. The best way to remember is to teach."*
> — Ship it, then explain it to your colleagues.

Good luck out there. 🚀

---

*Node.js Courseware — Complete (18 Modules)*
*Built for trainees who mean business.*
