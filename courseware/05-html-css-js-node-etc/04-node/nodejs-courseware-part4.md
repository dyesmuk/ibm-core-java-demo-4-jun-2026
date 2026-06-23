# Node.js Courseware — Part 4
## API Authentication & Security · Sorting, Pagination & Filtering · File Uploads · Sending Emails

---

# MODULE 12 — API Authentication and Security

## 🧠 The Concept

Authentication = "Who are you?"
Authorization = "What are you allowed to do?"

The modern way to handle both in REST APIs: **JWT (JSON Web Tokens)**.

### How JWT Works

```
1. User logs in with email + password
2. Server verifies credentials
3. Server creates a signed JWT token and sends it back
4. Client stores the token (localStorage or cookie)
5. Client sends token in every subsequent request header
6. Server verifies the token signature — no DB lookup needed!
```

A JWT looks like: `xxxxx.yyyyy.zzzzz`
- **Header:** algorithm type (base64)
- **Payload:** user data (base64)
- **Signature:** header + payload signed with a secret key

> **Key insight:** The signature can't be forged without the secret. So if it verifies, you trust the payload.

## 💻 Hands-On

### Setup

```bash
npm install jsonwebtoken bcryptjs
```

### Hashing Passwords

**Never store plain-text passwords.** Use `bcryptjs` to hash them.

```javascript
const bcrypt = require('bcryptjs')

// Hash password before saving
const plainPassword = 'MySecret123'
const hashed = await bcrypt.hash(plainPassword, 8)  // 8 = salt rounds
console.log(hashed)
// → $2a$08$... (60 char hash, different every time!)

// Verify password at login
const isMatch = await bcrypt.compare('MySecret123', hashed)  // true
const isMatch2 = await bcrypt.compare('wrongpass', hashed)   // false
```

### User Model with Auth

```javascript
// models/User.js
const mongoose = require('mongoose')
const bcrypt = require('bcryptjs')
const jwt = require('jsonwebtoken')

const userSchema = new mongoose.Schema({
  name: { type: String, required: true, trim: true },
  email: { type: String, required: true, unique: true, lowercase: true },
  password: {
    type: String,
    required: true,
    minlength: 8,
    validate: {
      validator: (v) => !v.toLowerCase().includes('password'),
      message: 'Password cannot contain the word "password"'
    }
  },
  tokens: [{
    token: { type: String, required: true }
  }]
}, { timestamps: true })

// Don't send password or tokens to the client
userSchema.methods.toJSON = function() {
  const user = this.toObject()
  delete user.password
  delete user.tokens
  return user
}

// Generate a JWT and save it to the user's tokens array
userSchema.methods.generateAuthToken = async function() {
  const token = jwt.sign(
    { _id: this._id.toString() },
    process.env.JWT_SECRET,
    { expiresIn: '7d' }
  )
  this.tokens = this.tokens.concat({ token })
  await this.save()
  return token
}

// Static method to find user by credentials
userSchema.statics.findByCredentials = async (email, password) => {
  const user = await User.findOne({ email })
  if (!user) throw new Error('Invalid email or password')

  const isMatch = await bcrypt.compare(password, user.password)
  if (!isMatch) throw new Error('Invalid email or password')

  return user
}

// Hash password before saving
userSchema.pre('save', async function(next) {
  if (this.isModified('password')) {
    this.password = await bcrypt.hash(this.password, 8)
  }
  next()
})

const User = mongoose.model('User', userSchema)
module.exports = User
```

### Auth Routes (Register + Login + Logout)

```javascript
// routes/auth.js
const express = require('express')
const User = require('../models/User')
const router = express.Router()

// REGISTER
router.post('/register', async (req, res) => {
  try {
    const user = await User.create(req.body)
    const token = await user.generateAuthToken()
    res.status(201).json({ user, token })
  } catch (err) {
    if (err.code === 11000) {
      return res.status(409).json({ error: 'Email already registered' })
    }
    if (err.name === 'ValidationError') {
      const errors = Object.values(err.errors).map(e => e.message)
      return res.status(400).json({ errors })
    }
    res.status(500).json({ error: err.message })
  }
})

// LOGIN
router.post('/login', async (req, res) => {
  try {
    const { email, password } = req.body
    const user = await User.findByCredentials(email, password)
    const token = await user.generateAuthToken()
    res.json({ user, token })
  } catch (err) {
    res.status(401).json({ error: 'Invalid email or password' })
  }
})

// LOGOUT (invalidate current token)
router.post('/logout', auth, async (req, res) => {
  try {
    req.user.tokens = req.user.tokens.filter(t => t.token !== req.token)
    await req.user.save()
    res.json({ message: 'Logged out successfully' })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// LOGOUT ALL (all devices)
router.post('/logout-all', auth, async (req, res) => {
  try {
    req.user.tokens = []
    await req.user.save()
    res.json({ message: 'Logged out from all devices' })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

module.exports = router
```

### Auth Middleware

```javascript
// middleware/auth.js
const jwt = require('jsonwebtoken')
const User = require('../models/User')

const auth = async (req, res, next) => {
  try {
    // Expect: "Authorization: Bearer <token>"
    const authHeader = req.headers.authorization
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ error: 'Authentication required' })
    }

    const token = authHeader.replace('Bearer ', '')
    const decoded = jwt.verify(token, process.env.JWT_SECRET)

    // Find user AND ensure this token is still in their tokens array
    const user = await User.findOne({
      _id: decoded._id,
      'tokens.token': token
    })

    if (!user) return res.status(401).json({ error: 'Token invalid or expired' })

    req.user = user
    req.token = token
    next()
  } catch (err) {
    res.status(401).json({ error: 'Please authenticate' })
  }
}

module.exports = auth
```

### Protecting Routes

```javascript
// server.js
const auth = require('./middleware/auth')
const authRouter = require('./routes/auth')
const tasksRouter = require('./routes/tasks')

app.use('/api/auth', authRouter)
app.use('/api/tasks', auth, tasksRouter)  // all task routes require auth

// Profile route
app.get('/api/me', auth, (req, res) => {
  res.json(req.user)  // user is attached by auth middleware
})

app.patch('/api/me', auth, async (req, res) => {
  const allowedUpdates = ['name', 'email', 'password']
  const updates = Object.keys(req.body)
  if (!updates.every(k => allowedUpdates.includes(k))) {
    return res.status(400).json({ error: 'Invalid updates' })
  }
  try {
    updates.forEach(k => req.user[k] = req.body[k])
    await req.user.save()
    res.json(req.user)
  } catch (err) {
    res.status(400).json({ error: err.message })
  }
})
```

### Testing Auth with curl

```bash
# Register
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Priya","email":"priya@example.com","password":"Secure1234"}'

# Login
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"priya@example.com","password":"Secure1234"}'

# Use the token
curl http://localhost:3000/api/me \
  -H "Authorization: Bearer <paste-token-here>"
```

### Role-Based Authorization

```javascript
// middleware/authorize.js
const authorize = (...roles) => {
  return (req, res, next) => {
    if (!roles.includes(req.user.role)) {
      return res.status(403).json({ error: 'Access denied: insufficient permissions' })
    }
    next()
  }
}

module.exports = authorize
```

```javascript
const authorize = require('./middleware/authorize')

// Only admins can delete users
app.delete('/api/users/:id', auth, authorize('admin'), async (req, res) => {
  // ...
})

// Admins and moderators can access this
app.get('/api/reports', auth, authorize('admin', 'moderator'), (req, res) => {
  // ...
})
```

### Security Checklist

```bash
npm install helmet express-rate-limit express-mongo-sanitize xss-clean
```

```javascript
const helmet = require('helmet')
const rateLimit = require('express-rate-limit')
const mongoSanitize = require('express-mongo-sanitize')
const xss = require('xss-clean')

// Set secure HTTP headers
app.use(helmet())

// Rate limiting
app.use('/api/auth', rateLimit({
  windowMs: 15 * 60 * 1000,  // 15 min
  max: 10,                     // 10 attempts
  message: { error: 'Too many login attempts. Try again in 15 minutes.' }
}))

// Prevent NoSQL injection ($where, $gt attacks in request body)
app.use(mongoSanitize())

// Prevent XSS — sanitize user input
app.use(xss())
```

## 🔥 Challenge

1. Add a password reset flow: `POST /api/auth/forgot-password` (generates a reset token and stores it hashed) and `POST /api/auth/reset-password/:token` (verifies token and updates password).
2. Add a `refreshToken` endpoint that issues a new JWT using a long-lived refresh token.
3. Implement an `admin` role that can view all users' tasks (not just their own).

## ✅ Key Takeaways

- Never store plain-text passwords — always use `bcrypt`
- JWT is stateless auth: sign on login, verify on every request
- Store tokens on the user document to support logout (token invalidation)
- `Authorization: Bearer <token>` is the standard header format
- Rate limiting + helmet + sanitization = basic security hardened API

---

---

# MODULE 13 — Sorting, Pagination, and Filtering

## 🧠 The Concept

Real APIs don't return 10,000 records at once. They let clients control what they get using:
- **Filtering** — only return matching records
- **Sorting** — order results
- **Pagination** — return a page at a time

These are implemented via **query parameters**.

## 💻 Hands-On

### Filtering

```javascript
// GET /api/tasks?done=false&priority=high

router.get('/', auth, async (req, res) => {
  try {
    const match = { owner: req.user._id }

    // Filter by done status
    if (req.query.done !== undefined) {
      match.done = req.query.done === 'true'
    }

    // Filter by priority
    if (req.query.priority) {
      match.priority = req.query.priority
    }

    // Filter by search text (requires text index on 'title')
    if (req.query.search) {
      match.$text = { $search: req.query.search }
    }

    const tasks = await Task.find(match)
    res.json(tasks)
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})
```

Create the text index once (in your DB setup or seed script):
```javascript
await Task.collection.createIndex({ title: 'text', description: 'text' })
```

### Sorting

```javascript
// GET /api/tasks?sortBy=createdAt:desc
// GET /api/tasks?sortBy=priority:asc

router.get('/', auth, async (req, res) => {
  const sort = {}

  if (req.query.sortBy) {
    const [field, order] = req.query.sortBy.split(':')
    sort[field] = order === 'desc' ? -1 : 1
  }

  const tasks = await Task.find({ owner: req.user._id })
    .sort(sort)

  res.json(tasks)
})
```

### Pagination

```javascript
// GET /api/tasks?page=2&limit=10

router.get('/', auth, async (req, res) => {
  const page = parseInt(req.query.page) || 1
  const limit = parseInt(req.query.limit) || 10
  const skip = (page - 1) * limit

  const [tasks, total] = await Promise.all([
    Task.find({ owner: req.user._id })
      .skip(skip)
      .limit(limit),
    Task.countDocuments({ owner: req.user._id })
  ])

  res.json({
    data: tasks,
    pagination: {
      total,
      page,
      limit,
      totalPages: Math.ceil(total / limit),
      hasNextPage: page < Math.ceil(total / limit),
      hasPrevPage: page > 1
    }
  })
})
```

### All Three Together (Production-Ready)

```javascript
// routes/tasks.js — full GET with filter + sort + paginate

router.get('/', auth, async (req, res) => {
  try {
    // --- Build filter ---
    const match = { owner: req.user._id }

    if (req.query.done !== undefined) {
      match.done = req.query.done === 'true'
    }
    if (req.query.priority) {
      match.priority = req.query.priority
    }

    // --- Build sort ---
    const sort = {}
    if (req.query.sortBy) {
      const [field, order] = req.query.sortBy.split(':')
      const validFields = ['createdAt', 'updatedAt', 'title', 'priority']
      if (validFields.includes(field)) {
        sort[field] = order === 'desc' ? -1 : 1
      }
    } else {
      sort.createdAt = -1  // default: newest first
    }

    // --- Pagination ---
    const page = Math.max(1, parseInt(req.query.page) || 1)
    const limit = Math.min(100, Math.max(1, parseInt(req.query.limit) || 10))
    const skip = (page - 1) * limit

    // --- Execute ---
    const [tasks, total] = await Promise.all([
      Task.find(match).sort(sort).skip(skip).limit(limit),
      Task.countDocuments(match)
    ])

    res.json({
      data: tasks,
      pagination: {
        total,
        page,
        limit,
        totalPages: Math.ceil(total / limit),
        hasNextPage: skip + tasks.length < total,
        hasPrevPage: page > 1
      }
    })

  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})
```

### Date Range Filtering

```javascript
// GET /api/tasks?from=2024-01-01&to=2024-12-31

if (req.query.from || req.query.to) {
  match.createdAt = {}
  if (req.query.from) match.createdAt.$gte = new Date(req.query.from)
  if (req.query.to) match.createdAt.$lte = new Date(req.query.to)
}
```

### Field Selection (Projection)

```javascript
// GET /api/tasks?fields=title,done,priority
// Only return specified fields — reduces payload size

const fields = req.query.fields
  ? req.query.fields.split(',').join(' ')
  : ''

const tasks = await Task.find(match).select(fields).sort(sort).skip(skip).limit(limit)
```

### A Reusable Query Helper

```javascript
// utils/queryBuilder.js

const buildQuery = (reqQuery, allowedFilters = [], allowedSortFields = []) => {
  const match = {}
  const sort = {}
  
  allowedFilters.forEach(field => {
    if (reqQuery[field] !== undefined) {
      if (reqQuery[field] === 'true') match[field] = true
      else if (reqQuery[field] === 'false') match[field] = false
      else match[field] = reqQuery[field]
    }
  })

  if (reqQuery.sortBy) {
    const [field, order] = reqQuery.sortBy.split(':')
    if (allowedSortFields.includes(field)) {
      sort[field] = order === 'desc' ? -1 : 1
    }
  }

  const page = Math.max(1, parseInt(reqQuery.page) || 1)
  const limit = Math.min(100, parseInt(reqQuery.limit) || 10)
  const skip = (page - 1) * limit

  return { match, sort, skip, limit, page }
}

module.exports = buildQuery
```

```javascript
// Using the helper
const buildQuery = require('../utils/queryBuilder')

router.get('/', auth, async (req, res) => {
  const { match, sort, skip, limit, page } = buildQuery(
    req.query,
    ['done', 'priority'],
    ['createdAt', 'title', 'priority']
  )
  match.owner = req.user._id

  const [tasks, total] = await Promise.all([
    Task.find(match).sort(sort).skip(skip).limit(limit),
    Task.countDocuments(match)
  ])

  res.json({ data: tasks, pagination: { total, page, limit, totalPages: Math.ceil(total / limit) } })
})
```

## 🔥 Challenge

1. Add cursor-based pagination (instead of offset): use `lastId` as a cursor to get records after it. This is more efficient for large datasets.
2. Add a `GET /api/tasks/stats` endpoint that returns: total tasks, done count, pending count, breakdown by priority.
3. Implement full-text search across `title` and `description` using MongoDB's `$text` search.

## ✅ Key Takeaways

- Filtering = `req.query` → `match` object for `Model.find(match)`
- Sorting = `req.query.sortBy` → `sort` object for `.sort(sort)`
- Pagination = `skip` + `limit`; use `countDocuments` for total
- Always validate and cap `limit` to prevent abuse (e.g., `?limit=99999`)
- Use `Promise.all([find, countDocuments])` to run both queries in parallel

---

---

# MODULE 14 — File Uploads

## 🧠 The Concept

Profile photos, document uploads, image galleries — file uploads are everywhere. In Node, `multer` is the go-to middleware for handling `multipart/form-data` (file uploads).

Files can be stored:
- **On disk** — simplest, but doesn't scale across multiple servers
- **In memory** — for processing before saving elsewhere
- **In cloud storage** (AWS S3, Cloudinary) — the production choice

## 💻 Hands-On

### Setup

```bash
npm install multer sharp
```

### Basic File Upload (Disk Storage)

```javascript
// middleware/upload.js
const multer = require('multer')
const path = require('path')

const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, 'uploads/')  // save to uploads/ folder
  },
  filename: (req, file, cb) => {
    // Unique filename: timestamp + original extension
    const uniqueName = `${Date.now()}-${Math.round(Math.random() * 1e9)}${path.extname(file.originalname)}`
    cb(null, uniqueName)
  }
})

const fileFilter = (req, file, cb) => {
  const allowedTypes = /jpeg|jpg|png|gif|webp/
  const extValid = allowedTypes.test(path.extname(file.originalname).toLowerCase())
  const mimeValid = allowedTypes.test(file.mimetype)

  if (extValid && mimeValid) {
    cb(null, true)   // accept file
  } else {
    cb(new Error('Only image files are allowed!'), false)
  }
}

const upload = multer({
  storage,
  limits: { fileSize: 5 * 1024 * 1024 },  // 5MB max
  fileFilter
})

module.exports = upload
```

```javascript
// routes/upload.js
const express = require('express')
const upload = require('../middleware/upload')
const path = require('path')
const router = express.Router()

// Serve uploaded files statically
// In server.js: app.use('/uploads', express.static('uploads'))

// Single file upload
router.post('/avatar', upload.single('avatar'), (req, res) => {
  if (!req.file) return res.status(400).json({ error: 'No file uploaded' })

  res.json({
    message: 'Upload successful!',
    filename: req.file.filename,
    url: `/uploads/${req.file.filename}`,
    size: req.file.size
  })
})

// Multiple files
router.post('/gallery', upload.array('photos', 10), (req, res) => {
  if (!req.files?.length) return res.status(400).json({ error: 'No files uploaded' })

  const files = req.files.map(f => ({
    filename: f.filename,
    url: `/uploads/${f.filename}`
  }))

  res.json({ message: `${files.length} files uploaded`, files })
})

// Handle multer errors
router.use((err, req, res, next) => {
  if (err instanceof multer.MulterError) {
    if (err.code === 'LIMIT_FILE_SIZE') {
      return res.status(400).json({ error: 'File too large. Max 5MB.' })
    }
  }
  res.status(400).json({ error: err.message })
})

module.exports = router
```

### Processing Images with Sharp

```javascript
// Process images in memory, then save
const multer = require('multer')
const sharp = require('sharp')
const path = require('path')
const fs = require('fs').promises

const upload = multer({
  storage: multer.memoryStorage(),  // store in buffer, not disk
  limits: { fileSize: 5 * 1024 * 1024 },
  fileFilter: (req, file, cb) => {
    if (file.mimetype.startsWith('image/')) cb(null, true)
    else cb(new Error('Images only!'), false)
  }
})

router.post('/avatar', auth, upload.single('avatar'), async (req, res) => {
  try {
    if (!req.file) return res.status(400).json({ error: 'No file provided' })

    // Resize and convert to PNG
    const filename = `avatar-${req.user._id}.png`
    const outputPath = path.join('uploads', filename)

    await sharp(req.file.buffer)
      .resize(200, 200, { fit: 'cover' })  // crop to 200x200
      .png({ quality: 80 })
      .toFile(outputPath)

    // Save avatar URL to user profile
    req.user.avatarUrl = `/uploads/${filename}`
    await req.user.save()

    res.json({ message: 'Avatar updated!', url: req.user.avatarUrl })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// Delete avatar
router.delete('/avatar', auth, async (req, res) => {
  try {
    if (req.user.avatarUrl) {
      const filePath = path.join(__dirname, '..', req.user.avatarUrl)
      await fs.unlink(filePath).catch(() => {})  // ignore if already deleted
      req.user.avatarUrl = null
      await req.user.save()
    }
    res.json({ message: 'Avatar removed' })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})
```

### Cloud Upload: Cloudinary

```bash
npm install cloudinary multer-storage-cloudinary
```

```javascript
// middleware/cloudinary-upload.js
const cloudinary = require('cloudinary').v2
const { CloudinaryStorage } = require('multer-storage-cloudinary')
const multer = require('multer')

cloudinary.config({
  cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
  api_key: process.env.CLOUDINARY_API_KEY,
  api_secret: process.env.CLOUDINARY_API_SECRET
})

const storage = new CloudinaryStorage({
  cloudinary,
  params: {
    folder: 'my-app/avatars',
    allowed_formats: ['jpg', 'jpeg', 'png', 'webp'],
    transformation: [{ width: 200, height: 200, crop: 'fill' }]
  }
})

module.exports = multer({ storage, limits: { fileSize: 5 * 1024 * 1024 } })
```

```javascript
const cloudinaryUpload = require('../middleware/cloudinary-upload')

router.post('/avatar', auth, cloudinaryUpload.single('avatar'), async (req, res) => {
  req.user.avatarUrl = req.file.path  // Cloudinary URL
  await req.user.save()
  res.json({ url: req.file.path })
})
```

### Frontend: Uploading Files

```html
<!-- HTML form -->
<form id="uploadForm">
  <input type="file" id="avatar" accept="image/*" />
  <button type="button" onclick="uploadAvatar()">Upload</button>
</form>

<script>
async function uploadAvatar() {
  const fileInput = document.getElementById('avatar')
  const file = fileInput.files[0]
  if (!file) return alert('Select a file first!')

  const formData = new FormData()
  formData.append('avatar', file)

  const res = await fetch('/api/upload/avatar', {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` },
    body: formData   // DO NOT set Content-Type manually — browser sets it with boundary
  })

  const data = await res.json()
  console.log('Uploaded:', data.url)
}
</script>
```

## 🔥 Challenge

1. Build a "Photo Gallery" API where authenticated users can upload multiple images, list their uploads, and delete them.
2. Use `sharp` to generate a thumbnail (100x100) alongside the original upload.
3. Add virus/MIME type validation: read the actual file bytes (magic numbers) instead of trusting `file.mimetype`.

## ✅ Key Takeaways

- `multer` handles `multipart/form-data`; configure `storage`, `limits`, and `fileFilter`
- Disk storage for simple cases; memory storage when you need to process before saving
- `sharp` is the go-to for image resizing/conversion (fast, minimal deps)
- For production: use cloud storage (Cloudinary, AWS S3)
- Frontend: use `FormData` to send files; don't set `Content-Type` manually

---

---

# MODULE 15 — Sending Emails

## 🧠 The Concept

Emails are how apps communicate with users: welcome emails, password resets, notifications, receipts. In Node, **Nodemailer** is the standard for sending emails, usually through a service like SendGrid, Gmail, or Mailgun.

## 💻 Hands-On

### Setup with SendGrid (Recommended)

1. Create a free account at [sendgrid.com](https://sendgrid.com) (100 emails/day free)
2. Create an API key under Settings → API Keys
3. Verify a sender email/domain

```bash
npm install @sendgrid/mail
```

```javascript
// utils/email.js
const sgMail = require('@sendgrid/mail')
sgMail.setApiKey(process.env.SENDGRID_API_KEY)

const FROM_EMAIL = process.env.FROM_EMAIL  // your verified sender

const sendWelcomeEmail = async (to, name) => {
  const msg = {
    to,
    from: FROM_EMAIL,
    subject: `Welcome to MyApp, ${name}! 🎉`,
    text: `Hey ${name},\n\nThanks for joining MyApp! We're stoked to have you.\n\nCheers,\nThe MyApp Team`,
    html: `
      <div style="font-family: sans-serif; max-width: 600px; margin: auto;">
        <h1>Welcome, ${name}! 🎉</h1>
        <p>Thanks for joining <strong>MyApp</strong>. We're stoked to have you.</p>
        <a href="${process.env.APP_URL}/dashboard"
           style="background:#6366f1;color:white;padding:12px 24px;border-radius:6px;text-decoration:none;display:inline-block;">
          Go to Dashboard
        </a>
        <p style="color:#999;font-size:12px;margin-top:30px;">
          You're receiving this because you signed up at MyApp.
        </p>
      </div>
    `
  }
  await sgMail.send(msg)
}

const sendPasswordResetEmail = async (to, resetToken) => {
  const resetUrl = `${process.env.APP_URL}/reset-password?token=${resetToken}`
  await sgMail.send({
    to,
    from: FROM_EMAIL,
    subject: 'Password Reset Request',
    html: `
      <h2>Reset Your Password</h2>
      <p>You requested a password reset. Click below — link expires in 1 hour.</p>
      <a href="${resetUrl}">Reset Password</a>
      <p>If you didn't request this, ignore this email.</p>
    `
  })
}

const sendTaskReminderEmail = async (to, tasks) => {
  const taskList = tasks.map(t => `<li>${t.title}</li>`).join('')
  await sgMail.send({
    to,
    from: FROM_EMAIL,
    subject: `You have ${tasks.length} pending tasks`,
    html: `
      <h2>📋 Your Pending Tasks</h2>
      <ul>${taskList}</ul>
      <a href="${process.env.APP_URL}/tasks">View All Tasks</a>
    `
  })
}

module.exports = { sendWelcomeEmail, sendPasswordResetEmail, sendTaskReminderEmail }
```

### Wire Up in Routes

```javascript
// routes/auth.js
const { sendWelcomeEmail } = require('../utils/email')

router.post('/register', async (req, res) => {
  try {
    const user = await User.create(req.body)
    const token = await user.generateAuthToken()

    // Send welcome email (don't await — don't block the response)
    sendWelcomeEmail(user.email, user.name).catch(err => {
      console.error('Failed to send welcome email:', err.message)
    })

    res.status(201).json({ user, token })
  } catch (err) {
    res.status(400).json({ error: err.message })
  }
})
```

### Nodemailer with Gmail (Development)

```bash
npm install nodemailer
```

```javascript
// utils/email-nodemailer.js
const nodemailer = require('nodemailer')

// For development: use Mailtrap (fake inbox)
// For production: use SendGrid or SMTP
const transporter = nodemailer.createTransport({
  host: process.env.SMTP_HOST || 'smtp.mailtrap.io',
  port: process.env.SMTP_PORT || 2525,
  auth: {
    user: process.env.SMTP_USER,
    pass: process.env.SMTP_PASS
  }
})

const sendEmail = async ({ to, subject, html, text }) => {
  const info = await transporter.sendMail({
    from: `"MyApp" <${process.env.FROM_EMAIL}>`,
    to,
    subject,
    html,
    text
  })
  console.log('Email sent:', info.messageId)
  return info
}

module.exports = sendEmail
```

> **Use Mailtrap for dev** — it's a fake inbox that catches all emails so you don't accidentally spam real people during testing. Sign up at [mailtrap.io](https://mailtrap.io) and copy the SMTP credentials.

### Email Templates with Handlebars

For more complex templates:

```bash
npm install nodemailer-express-handlebars
```

```
/emails/
  welcome.hbs
  reset-password.hbs
  task-reminder.hbs
```

```html
<!-- emails/welcome.hbs -->
<html>
<body>
  <h1>Welcome, {{name}}! 🎉</h1>
  <p>Your account email is: <strong>{{email}}</strong></p>
  {{#if isPremium}}
    <p>Thanks for being a premium member! 🌟</p>
  {{/if}}
</body>
</html>
```

```javascript
const hbs = require('nodemailer-express-handlebars')
const path = require('path')

transporter.use('compile', hbs({
  viewEngine: {
    extname: '.hbs',
    partialsDir: path.join(__dirname, '../emails'),
    defaultLayout: false,
  },
  viewPath: path.join(__dirname, '../emails'),
  extName: '.hbs',
}))

await transporter.sendMail({
  to: user.email,
  subject: 'Welcome!',
  template: 'welcome',
  context: { name: user.name, email: user.email, isPremium: false }
})
```

### Scheduled Emails with `node-cron`

```bash
npm install node-cron
```

```javascript
// jobs/email-reminders.js
const cron = require('node-cron')
const User = require('../models/User')
const Task = require('../models/Task')
const { sendTaskReminderEmail } = require('../utils/email')

// Run every day at 9:00 AM
cron.schedule('0 9 * * *', async () => {
  console.log('🕘 Running daily task reminder job...')

  try {
    const users = await User.find({ isActive: true })

    for (const user of users) {
      const pendingTasks = await Task.find({ owner: user._id, done: false })
      if (pendingTasks.length > 0) {
        await sendTaskReminderEmail(user.email, pendingTasks)
      }
    }

    console.log('✅ Reminder emails sent')
  } catch (err) {
    console.error('❌ Email job failed:', err.message)
  }
})
```

```javascript
// server.js — activate the job
require('./jobs/email-reminders')
```

## 🔥 Challenge

1. Implement a full password reset flow: `POST /forgot-password` sends a reset link via email; `POST /reset-password/:token` verifies a hashed token (stored in DB with expiry) and updates the password.
2. Add an email verification step to registration: user gets a verify link, clicking it sets `isVerified: true` on their account.
3. Create a weekly digest email (using `node-cron`) that sends each user a summary of their completed tasks from the past week.

## ✅ Key Takeaways

- SendGrid is the easiest production email service; Mailtrap for dev/testing
- `nodemailer` wraps SMTP; `@sendgrid/mail` wraps the SendGrid API
- Don't `await` email sends in the response path — fire and forget, log errors separately
- Use Handlebars (or similar) for reusable email templates
- `node-cron` for scheduled jobs: `'0 9 * * *'` = 9 AM daily
