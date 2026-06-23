# Node.js Courseware — Part 3
## Deployment · MongoDB & Promises · REST APIs & Mongoose

---

# MODULE 9 — Application Deployment

## 🧠 The Concept

Your app works locally. Now let's get it running on the internet so you can share it with the world (or just your interviewer).

We'll cover deploying to **Render** (free, beginner-friendly) and the essentials that make any deployment work.

## 💻 Hands-On

### Step 1: Prep Your App for Production

**Use environment variables for everything config-related:**

```javascript
// server.js
require('dotenv').config()

const PORT = process.env.PORT || 3000
const NODE_ENV = process.env.NODE_ENV || 'development'

app.listen(PORT, () => {
  console.log(`Server running in ${NODE_ENV} mode on port ${PORT}`)
})
```

**Update `package.json` to have a start script:**

```json
{
  "name": "my-node-app",
  "version": "1.0.0",
  "scripts": {
    "start": "node server.js",
    "dev": "nodemon server.js"
  },
  "engines": {
    "node": ">=18.0.0"
  }
}
```

**Install nodemon for development:**
```bash
npm install --save-dev nodemon
```

Now `npm run dev` auto-restarts on file changes; `npm start` runs production.

### Step 2: Set Up Git

```bash
# Initialize git repo
git init

# Create .gitignore
echo "node_modules/
.env
*.log
dist/" > .gitignore

# First commit
git add .
git commit -m "Initial commit"
```

**Push to GitHub:**
```bash
git remote add origin https://github.com/yourusername/my-node-app.git
git branch -M main
git push -u origin main
```

### Step 3: Deploy to Render (Free Tier)

1. Go to [render.com](https://render.com) and sign up with GitHub
2. Click **New → Web Service**
3. Connect your GitHub repo
4. Configure:
   - **Build Command:** `npm install`
   - **Start Command:** `npm start`
   - **Environment:** Node
5. Add environment variables in the dashboard (your `.env` contents, except NODE_ENV — Render sets that automatically)
6. Click **Deploy**

Render gives you a free `.onrender.com` URL. Done!

### Step 4: Heroku Alternative

```bash
npm install -g heroku
heroku login
heroku create my-app-name
git push heroku main
heroku logs --tail  # view logs
heroku open         # open in browser
```

### Step 5: Environment-Specific Config

```javascript
// config/index.js
const config = {
  development: {
    dbUrl: 'mongodb://localhost:27017/myapp',
    jwtSecret: 'dev-secret-not-secure',
    port: 3000,
  },
  production: {
    dbUrl: process.env.MONGO_URI,
    jwtSecret: process.env.JWT_SECRET,
    port: process.env.PORT,
  },
  test: {
    dbUrl: 'mongodb://localhost:27017/myapp-test',
    jwtSecret: 'test-secret',
    port: 4000,
  }
}

module.exports = config[process.env.NODE_ENV || 'development']
```

```javascript
// server.js
const config = require('./config')
console.log('DB URL:', config.dbUrl)
```

### Production Best Practices Checklist

```javascript
// 1. Helmet — sets security headers
npm install helmet
app.use(require('helmet')())

// 2. Rate limiting — prevent abuse
npm install express-rate-limit
const rateLimit = require('express-rate-limit')
app.use(rateLimit({
  windowMs: 15 * 60 * 1000,  // 15 minutes
  max: 100,                    // max 100 requests per window
  message: { error: 'Too many requests, slow down!' }
}))

// 3. Compression — shrink response sizes
npm install compression
app.use(require('compression')())

// 4. Process manager with PM2 (for VPS/self-hosted)
npm install -g pm2
pm2 start server.js --name "my-app"
pm2 startup           # auto-start on reboot
pm2 logs              # view logs
pm2 monit             # monitor CPU/memory
```

### Keeping Secrets Safe

```bash
# .env (local only, in .gitignore)
PORT=3000
JWT_SECRET=local-dev-secret
MONGO_URI=mongodb://localhost:27017/myapp

# On Render/Heroku/Railway: set env vars in their dashboard
# They're stored securely and injected at runtime
```

> **Never, ever, EVER put API keys or passwords in your code or commit them to git.** Use environment variables. Always.

## 🔥 Challenge

1. Deploy your Task Manager API from Module 8 to Render.
2. Add the `helmet` and `express-rate-limit` packages to your app before deploying.
3. Set up a `/health` endpoint that returns `{ status: 'ok', uptime: process.uptime() }` — useful for monitoring.

## ✅ Key Takeaways

- Always use `process.env.PORT` — deployment platforms set the port dynamically
- Have a `"start": "node server.js"` script in `package.json`
- `.gitignore` must include `node_modules/` and `.env`
- Environment variables hold secrets — never hardcode them
- Helmet + rate limiting are easy security wins

---

---

# MODULE 10 — MongoDB and Promises

## 🧠 The Concept

Storing data in memory (arrays) is great for learning but terrible for production — data disappears when the server restarts. Enter **MongoDB**.

MongoDB is a **NoSQL document database**. Instead of tables and rows, you have **collections** and **documents** (JSON-like objects).

| SQL | MongoDB |
|-----|---------|
| Database | Database |
| Table | Collection |
| Row | Document |
| Column | Field |
| JOIN | `$lookup` (or embedded docs) |

**Why MongoDB + Node?** Both speak JSON. No conversion layer needed.

## 💻 Hands-On

### Setup: MongoDB Atlas (Cloud, Free)

1. Go to [mongodb.com/atlas](https://www.mongodb.com/atlas) and create a free account
2. Create a free **M0 cluster** (no credit card needed)
3. Create a database user (save the password!)
4. Add your IP to the allowlist (or use `0.0.0.0/0` for development)
5. Click **Connect → Drivers** and copy your connection string:
   ```
   mongodb+srv://username:password@cluster.mongodb.net/myapp?retryWrites=true
   ```

### Connecting with the Native MongoDB Driver

```bash
npm install mongodb
```

```javascript
// db.js
const { MongoClient } = require('mongodb')

const uri = process.env.MONGO_URI  // from .env
const client = new MongoClient(uri)

let db

const connect = async () => {
  await client.connect()
  db = client.db('myapp')
  console.log('✅ Connected to MongoDB')
}

const getDb = () => {
  if (!db) throw new Error('DB not connected! Call connect() first.')
  return db
}

module.exports = { connect, getDb }
```

```javascript
// server.js
const { connect, getDb } = require('./db')

const main = async () => {
  await connect()
  
  const app = express()
  app.use(express.json())
  
  app.get('/api/users', async (req, res) => {
    const db = getDb()
    const users = await db.collection('users').find({}).toArray()
    res.json(users)
  })
  
  app.post('/api/users', async (req, res) => {
    const db = getDb()
    const result = await db.collection('users').insertOne(req.body)
    res.status(201).json({ id: result.insertedId })
  })
  
  app.listen(3000)
}

main().catch(console.error)
```

### CRUD with MongoDB Native Driver

```javascript
const { ObjectId } = require('mongodb')

// CREATE
const result = await db.collection('tasks').insertOne({
  text: 'Learn MongoDB',
  done: false,
  createdAt: new Date()
})
console.log('Inserted ID:', result.insertedId)

// INSERT MANY
await db.collection('tasks').insertMany([
  { text: 'Task 1', done: false },
  { text: 'Task 2', done: true },
])

// READ - find all
const all = await db.collection('tasks').find({}).toArray()

// READ - find with filter
const pending = await db.collection('tasks').find({ done: false }).toArray()

// READ - find one by ID
const task = await db.collection('tasks').findOne({
  _id: new ObjectId('64a1b2c3d4e5f6789012abcd')
})

// UPDATE
await db.collection('tasks').updateOne(
  { _id: new ObjectId(id) },           // filter
  { $set: { done: true, updatedAt: new Date() } }  // update
)

// UPDATE MANY
await db.collection('tasks').updateMany(
  { done: false },
  { $set: { done: true } }
)

// DELETE
await db.collection('tasks').deleteOne({ _id: new ObjectId(id) })

// DELETE MANY
await db.collection('tasks').deleteMany({ done: true })
```

### MongoDB Query Operators

```javascript
// Comparison operators
{ age: { $gt: 18 } }          // greater than
{ age: { $gte: 18 } }         // greater than or equal
{ age: { $lt: 65 } }          // less than
{ price: { $in: [10, 20, 30] } }  // in array
{ status: { $ne: 'inactive' } }   // not equal

// Logical operators
{ $and: [{ age: { $gt: 18 } }, { active: true }] }
{ $or: [{ city: 'Chennai' }, { city: 'Mumbai' }] }

// Array operators
{ tags: { $all: ['node', 'mongodb'] } }  // has all these tags
{ tags: { $elemMatch: { $eq: 'node' } } }

// Text search (requires text index)
await db.collection('tasks').createIndex({ text: 'text' })
await db.collection('tasks').find({ $text: { $search: 'node mongodb' } }).toArray()
```

### Promises Pattern with MongoDB

```javascript
// tasks-service.js
const { getDb } = require('./db')
const { ObjectId } = require('mongodb')

const getAllTasks = () => {
  return getDb().collection('tasks').find({}).toArray()
}

const getTaskById = (id) => {
  return getDb().collection('tasks').findOne({ _id: new ObjectId(id) })
}

const createTask = (taskData) => {
  const task = {
    ...taskData,
    done: false,
    createdAt: new Date()
  }
  return getDb().collection('tasks').insertOne(task)
}

const updateTask = (id, updates) => {
  return getDb().collection('tasks').findOneAndUpdate(
    { _id: new ObjectId(id) },
    { $set: { ...updates, updatedAt: new Date() } },
    { returnDocument: 'after' }  // return updated document
  )
}

const deleteTask = (id) => {
  return getDb().collection('tasks').deleteOne({ _id: new ObjectId(id) })
}

module.exports = { getAllTasks, getTaskById, createTask, updateTask, deleteTask }
```

```javascript
// Using the service in routes
const tasks = require('./tasks-service')

app.get('/api/tasks', async (req, res) => {
  try {
    const allTasks = await tasks.getAllTasks()
    res.json(allTasks)
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})
```

## 🔥 Challenge

1. Connect your Task Manager API to MongoDB Atlas and replace the in-memory array.
2. Add a `GET /api/tasks?done=false&sort=createdAt` endpoint that filters and sorts from MongoDB.
3. Create a `seed.js` script that inserts 10 sample tasks into MongoDB.

## ✅ Key Takeaways

- MongoDB stores JSON-like documents in collections
- `ObjectId` is MongoDB's auto-generated unique ID (replaces numeric IDs)
- Native driver: `collection.find()`, `insertOne()`, `updateOne()`, `deleteOne()`
- Always use `new ObjectId(id)` when querying by ID from a string
- Keep DB logic in a service layer, not directly in routes

---

---

# MODULE 11 — REST APIs and Mongoose

## 🧠 The Concept

Mongoose is an **ODM** (Object Document Mapper) — it wraps the MongoDB driver and adds:
- **Schemas** — define the shape of your documents
- **Models** — classes that interact with collections
- **Validation** — built-in and custom
- **Middleware (hooks)** — run code before/after operations
- **Virtuals** — computed properties

Mongoose makes MongoDB feel much more structured and safe.

## 💻 Hands-On

### Setup

```bash
npm install mongoose
```

```javascript
// db.js
const mongoose = require('mongoose')

const connect = async () => {
  await mongoose.connect(process.env.MONGO_URI)
  console.log('✅ MongoDB connected via Mongoose')
}

module.exports = { connect }
```

### Defining a Schema and Model

```javascript
// models/User.js
const mongoose = require('mongoose')

const userSchema = new mongoose.Schema({
  name: {
    type: String,
    required: [true, 'Name is required'],
    trim: true,
    minlength: [2, 'Name must be at least 2 characters'],
    maxlength: [50, 'Name cannot exceed 50 characters']
  },
  email: {
    type: String,
    required: [true, 'Email is required'],
    unique: true,
    lowercase: true,
    trim: true,
    match: [/^\S+@\S+\.\S+$/, 'Please enter a valid email']
  },
  age: {
    type: Number,
    min: [0, 'Age cannot be negative'],
    max: [150, 'That is... a lot of years']
  },
  role: {
    type: String,
    enum: ['user', 'admin', 'moderator'],
    default: 'user'
  },
  isActive: {
    type: Boolean,
    default: true
  },
  tags: [String],  // Array of strings
  address: {       // Embedded document
    city: String,
    state: String,
    pincode: String
  }
}, {
  timestamps: true  // adds createdAt and updatedAt automatically
})

// Virtual: full name example
userSchema.virtual('initials').get(function() {
  return this.name.split(' ').map(n => n[0]).join('').toUpperCase()
})

const User = mongoose.model('User', userSchema)
module.exports = User
```

### CRUD with Mongoose

```javascript
const User = require('./models/User')

// CREATE
const user = new User({ name: 'Priya Sharma', email: 'priya@example.com', age: 25 })
await user.save()

// OR shorthand:
const user = await User.create({ name: 'Arjun', email: 'arjun@example.com' })

// READ ALL
const users = await User.find()
const activeUsers = await User.find({ isActive: true })
const admins = await User.find({ role: 'admin' }).select('name email')  // only these fields

// READ ONE
const user = await User.findById(id)
const user = await User.findOne({ email: 'priya@example.com' })

// UPDATE
await User.findByIdAndUpdate(id, { age: 26 }, { new: true, runValidators: true })

// DELETE
await User.findByIdAndDelete(id)

// COUNT
const count = await User.countDocuments({ isActive: true })
```

### Building a Full REST API with Mongoose

```javascript
// models/Task.js
const mongoose = require('mongoose')

const taskSchema = new mongoose.Schema({
  title: { type: String, required: true, trim: true },
  description: { type: String, trim: true },
  done: { type: Boolean, default: false },
  priority: { type: String, enum: ['low', 'medium', 'high'], default: 'medium' },
  owner: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true }
}, { timestamps: true })

module.exports = mongoose.model('Task', taskSchema)
```

```javascript
// routes/tasks.js
const express = require('express')
const Task = require('../models/Task')
const router = express.Router()

// GET /api/tasks
router.get('/', async (req, res) => {
  try {
    const tasks = await Task.find({ owner: req.user._id })  // only their tasks
    res.json(tasks)
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// GET /api/tasks/:id
router.get('/:id', async (req, res) => {
  try {
    const task = await Task.findOne({ _id: req.params.id, owner: req.user._id })
    if (!task) return res.status(404).json({ error: 'Task not found' })
    res.json(task)
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

// POST /api/tasks
router.post('/', async (req, res) => {
  try {
    const task = await Task.create({ ...req.body, owner: req.user._id })
    res.status(201).json(task)
  } catch (err) {
    if (err.name === 'ValidationError') {
      const errors = Object.values(err.errors).map(e => e.message)
      return res.status(400).json({ errors })
    }
    res.status(500).json({ error: err.message })
  }
})

// PATCH /api/tasks/:id
router.patch('/:id', async (req, res) => {
  const allowedUpdates = ['title', 'description', 'done', 'priority']
  const updates = Object.keys(req.body)
  const isValid = updates.every(k => allowedUpdates.includes(k))
  
  if (!isValid) return res.status(400).json({ error: 'Invalid update fields' })

  try {
    const task = await Task.findOneAndUpdate(
      { _id: req.params.id, owner: req.user._id },
      req.body,
      { new: true, runValidators: true }
    )
    if (!task) return res.status(404).json({ error: 'Task not found' })
    res.json(task)
  } catch (err) {
    res.status(400).json({ error: err.message })
  }
})

// DELETE /api/tasks/:id
router.delete('/:id', async (req, res) => {
  try {
    const task = await Task.findOneAndDelete({ _id: req.params.id, owner: req.user._id })
    if (!task) return res.status(404).json({ error: 'Task not found' })
    res.json({ message: 'Task deleted' })
  } catch (err) {
    res.status(500).json({ error: err.message })
  }
})

module.exports = router
```

### Mongoose Middleware (Hooks)

```javascript
const bcrypt = require('bcryptjs')

// Before saving a user, hash their password
userSchema.pre('save', async function(next) {
  if (this.isModified('password')) {
    this.password = await bcrypt.hash(this.password, 8)
  }
  next()
})

// After deleting a user, delete their tasks
userSchema.post('findOneAndDelete', async function(doc) {
  if (doc) {
    await Task.deleteMany({ owner: doc._id })
    console.log(`Deleted tasks for user: ${doc._id}`)
  }
})
```

### Populate — Reference Other Documents

```javascript
// In Task model, owner is a ref to User
const task = await Task.findById(id).populate('owner', 'name email')
// Now task.owner is { name: '...', email: '...' } instead of just an ID
```

### Validating with Custom Validators

```javascript
const userSchema = new mongoose.Schema({
  password: {
    type: String,
    required: true,
    minlength: 8,
    validate: {
      validator: (val) => !val.toLowerCase().includes('password'),
      message: 'Password cannot contain the word "password"'
    }
  },
  age: {
    type: Number,
    validate: {
      validator: Number.isInteger,
      message: 'Age must be a whole number'
    }
  }
})
```

## 🔥 Challenge

1. Build a full "Blog API" with Mongoose: `Post` model (title, content, author, tags, published) and `Comment` model (text, author, post reference).
2. Implement a `GET /api/posts?tag=nodejs&author=priya` filtered query.
3. Use `populate` to return author details with each post.
4. Add a Mongoose pre-save hook that auto-generates a `slug` from the post title.

## ✅ Key Takeaways

- Mongoose Schema defines the structure; Model creates the collection interface
- Built-in validation: `required`, `min`, `max`, `enum`, `match`, `minlength`
- `timestamps: true` auto-manages `createdAt` and `updatedAt`
- `findByIdAndUpdate(..., { new: true })` — always pass `new: true` to get the updated doc back
- Middleware hooks (`pre`, `post`) for logic that should run around DB operations
- `populate()` for joining referenced documents
