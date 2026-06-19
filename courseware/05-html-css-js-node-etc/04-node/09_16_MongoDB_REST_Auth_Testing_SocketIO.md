# Node.js — Module 09: MongoDB & Promises (Task App)

## MongoDB + Mongoose Setup

```bash
npm install mongoose
```

```javascript
// db/connect.js
import mongoose from 'mongoose';

const connectDB = async () => {
  try {
    const conn = await mongoose.connect(process.env.MONGODB_URI);
    console.log(`MongoDB Connected: ${conn.connection.host}`);
  } catch (err) {
    console.error(`DB Connection Error: ${err.message}`);
    process.exit(1);
  }
};

mongoose.connection.on('disconnected', () => console.warn('MongoDB disconnected'));
mongoose.connection.on('error', (err) => console.error('MongoDB error:', err));

export default connectDB;
```

## Mongoose Schemas and Models

```javascript
// models/User.js
import mongoose from 'mongoose';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';

const userSchema = new mongoose.Schema({
  name: {
    type: String,
    required: [true, 'Name is required'],
    trim: true,
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
  password: {
    type: String,
    required: [true, 'Password is required'],
    minlength: [8, 'Password must be at least 8 characters'],
    select: false   // never returned in queries by default
  },
  role: {
    type: String,
    enum: ['user', 'admin'],
    default: 'user'
  },
  avatar: Buffer,
  tokens: [{
    token: { type: String, required: true }
  }]
}, {
  timestamps: true    // adds createdAt and updatedAt
});

// Virtual — not stored in DB, computed property
userSchema.virtual('taskCount', {
  ref: 'Task',
  localField: '_id',
  foreignField: 'owner',
  count: true
});

// Instance method — available on each document
userSchema.methods.generateAuthToken = async function() {
  const token = jwt.sign(
    { _id: this._id.toString() },
    process.env.JWT_SECRET,
    { expiresIn: '7d' }
  );
  this.tokens = this.tokens.concat({ token });
  await this.save();
  return token;
};

// toJSON: control what's returned when converted to JSON
userSchema.methods.toJSON = function() {
  const user = this.toObject();
  delete user.password;
  delete user.tokens;
  delete user.__v;
  return user;
};

// Static method — available on the Model class
userSchema.statics.findByCredentials = async (email, password) => {
  const user = await User.findOne({ email }).select('+password');
  if (!user) throw new Error('Invalid credentials');

  const isMatch = await bcrypt.compare(password, user.password);
  if (!isMatch) throw new Error('Invalid credentials');

  return user;
};

// Pre-save hook (middleware)
userSchema.pre('save', async function(next) {
  if (this.isModified('password')) {
    this.password = await bcrypt.hash(this.password, 10);
  }
  next();
});

// Cascade delete tasks when user is removed
userSchema.pre('deleteOne', { document: true }, async function(next) {
  await Task.deleteMany({ owner: this._id });
  next();
});

const User = mongoose.model('User', userSchema);
export default User;
```

```javascript
// models/Task.js
import mongoose from 'mongoose';

const taskSchema = new mongoose.Schema({
  description: {
    type: String,
    required: [true, 'Description is required'],
    trim: true
  },
  completed: {
    type: Boolean,
    default: false
  },
  priority: {
    type: String,
    enum: ['low', 'medium', 'high'],
    default: 'medium'
  },
  dueDate: Date,
  tags: [String],
  owner: {
    type: mongoose.Schema.Types.ObjectId,
    required: true,
    ref: 'User'     // enables populate()
  }
}, { timestamps: true });

const Task = mongoose.model('Task', taskSchema);
export default Task;
```

## CRUD Operations

```javascript
// Create
const task = new Task({ description: 'Buy groceries', owner: req.user._id });
await task.save();
// or:
const task = await Task.create({ description: 'Buy groceries', owner: req.user._id });

// Read
const tasks = await Task.find({ owner: req.user._id });
const task  = await Task.findById(id);
const task  = await Task.findOne({ description: 'Buy groceries', owner: userId });

// With population (JOIN equivalent)
const tasks = await Task.find({ owner: userId }).populate('owner', 'name email');

// Update
const task = await Task.findByIdAndUpdate(
  id,
  { completed: true },
  { new: true, runValidators: true }  // return updated doc, run validators
);
// or partial update:
await task.updateOne({ completed: true });

// Delete
await Task.findByIdAndDelete(id);
await Task.deleteMany({ owner: userId });
```

---

---

# Node.js — Module 10: REST APIs & Mongoose (Task App)

## Full REST API — Task Endpoints

```javascript
// routes/tasks.js
import { Router } from 'express';
import Task from '../models/Task.js';
import requireAuth from '../middleware/auth.js';

const router = Router();

// POST /tasks
router.post('/', requireAuth, async (req, res, next) => {
  try {
    const task = await Task.create({ ...req.body, owner: req.user._id });
    res.status(201).json(task);
  } catch (err) {
    next(err);
  }
});

// GET /tasks
router.get('/', requireAuth, async (req, res, next) => {
  try {
    const match = { owner: req.user._id };
    if (req.query.completed !== undefined) {
      match.completed = req.query.completed === 'true';
    }
    const tasks = await Task.find(match)
      .sort({ createdAt: -1 })
      .limit(parseInt(req.query.limit ?? '20'))
      .skip(parseInt(req.query.skip ?? '0'));
    res.json(tasks);
  } catch (err) {
    next(err);
  }
});

// GET /tasks/:id
router.get('/:id', requireAuth, async (req, res, next) => {
  try {
    const task = await Task.findOne({ _id: req.params.id, owner: req.user._id });
    if (!task) return res.status(404).json({ error: 'Task not found' });
    res.json(task);
  } catch (err) {
    next(err);
  }
});

// PATCH /tasks/:id
router.patch('/:id', requireAuth, async (req, res, next) => {
  const allowed = ['description', 'completed', 'priority', 'dueDate', 'tags'];
  const updates = Object.keys(req.body);
  const isValid = updates.every(u => allowed.includes(u));
  if (!isValid) return res.status(400).json({ error: 'Invalid updates' });

  try {
    const task = await Task.findOne({ _id: req.params.id, owner: req.user._id });
    if (!task) return res.status(404).json({ error: 'Task not found' });

    updates.forEach(key => task[key] = req.body[key]);
    await task.save();   // runs validators and hooks (unlike findByIdAndUpdate)
    res.json(task);
  } catch (err) {
    next(err);
  }
});

// DELETE /tasks/:id
router.delete('/:id', requireAuth, async (req, res, next) => {
  try {
    const task = await Task.findOneAndDelete({ _id: req.params.id, owner: req.user._id });
    if (!task) return res.status(404).json({ error: 'Task not found' });
    res.json({ message: 'Task deleted', task });
  } catch (err) {
    next(err);
  }
});

export default router;
```

---

---

# Node.js — Module 11: Authentication & Security

## JWT Authentication Middleware

```javascript
// middleware/auth.js
import jwt from 'jsonwebtoken';
import User from '../models/User.js';

const requireAuth = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader?.startsWith('Bearer ')) {
      return res.status(401).json({ error: 'No token provided' });
    }

    const token = authHeader.split(' ')[1];
    const decoded = jwt.verify(token, process.env.JWT_SECRET);

    const user = await User.findOne({
      _id: decoded._id,
      'tokens.token': token   // ensure token is in user's active tokens
    });

    if (!user) return res.status(401).json({ error: 'Please authenticate' });

    req.user  = user;
    req.token = token;
    next();
  } catch (err) {
    res.status(401).json({ error: 'Please authenticate' });
  }
};

export default requireAuth;
```

## Auth Routes — Register, Login, Logout

```javascript
// routes/auth.js
import { Router } from 'express';
import User from '../models/User.js';
import requireAuth from '../middleware/auth.js';

const router = Router();

// POST /auth/register
router.post('/register', async (req, res, next) => {
  try {
    const user = await User.create(req.body);
    const token = await user.generateAuthToken();
    res.status(201).json({ user, token });
  } catch (err) {
    if (err.code === 11000) {   // MongoDB duplicate key
      return res.status(409).json({ error: 'Email already registered' });
    }
    next(err);
  }
});

// POST /auth/login
router.post('/login', async (req, res, next) => {
  try {
    const user = await User.findByCredentials(req.body.email, req.body.password);
    const token = await user.generateAuthToken();
    res.json({ user, token });
  } catch (err) {
    res.status(401).json({ error: err.message });
  }
});

// POST /auth/logout (invalidates current token)
router.post('/logout', requireAuth, async (req, res, next) => {
  try {
    req.user.tokens = req.user.tokens.filter(t => t.token !== req.token);
    await req.user.save();
    res.json({ message: 'Logged out' });
  } catch (err) {
    next(err);
  }
});

// POST /auth/logout-all
router.post('/logout-all', requireAuth, async (req, res, next) => {
  try {
    req.user.tokens = [];
    await req.user.save();
    res.json({ message: 'Logged out from all devices' });
  } catch (err) {
    next(err);
  }
});

export default router;
```

## Security Packages

```bash
npm install helmet cors express-rate-limit express-mongo-sanitize hpp
```

```javascript
import helmet from 'helmet';
import mongoSanitize from 'express-mongo-sanitize';
import hpp from 'hpp';

app.use(helmet());                   // security headers (XSS, clickjacking, etc.)
app.use(mongoSanitize());            // prevent NoSQL injection ($where, etc.)
app.use(hpp());                      // prevent HTTP parameter pollution
app.use(cors({ origin: [...] }));    // CORS

// Input validation with Joi
import Joi from 'joi';

const userSchema = Joi.object({
  name:     Joi.string().min(1).max(50).required(),
  email:    Joi.string().email().required(),
  password: Joi.string().min(8).pattern(/^(?=.*[A-Z])(?=.*[0-9])/).required()
});

const validate = (schema) => (req, res, next) => {
  const { error } = schema.validate(req.body, { abortEarly: false });
  if (error) {
    const messages = error.details.map(d => d.message);
    return res.status(400).json({ errors: messages });
  }
  next();
};

router.post('/register', validate(userSchema), registerUser);
```

---

---

# Node.js — Module 12: Sorting, Pagination & Filtering

```javascript
// GET /tasks?completed=true&priority=high&sortBy=createdAt:desc&limit=10&page=2
router.get('/', requireAuth, async (req, res, next) => {
  try {
    const { completed, priority, sortBy, limit = 10, page = 1, search } = req.query;

    // Build filter
    const filter = { owner: req.user._id };
    if (completed !== undefined) filter.completed = completed === 'true';
    if (priority)   filter.priority = priority;
    if (search)     filter.description = { $regex: search, $options: 'i' };

    // Build sort
    const sort = {};
    if (sortBy) {
      const [field, direction] = sortBy.split(':');
      sort[field] = direction === 'desc' ? -1 : 1;
    } else {
      sort.createdAt = -1;   // default: newest first
    }

    const pageNum  = Math.max(1, parseInt(page));
    const limitNum = Math.min(100, Math.max(1, parseInt(limit)));  // cap at 100
    const skip     = (pageNum - 1) * limitNum;

    const [tasks, total] = await Promise.all([
      Task.find(filter).sort(sort).limit(limitNum).skip(skip),
      Task.countDocuments(filter)
    ]);

    res.json({
      data: tasks,
      pagination: {
        total,
        page: pageNum,
        limit: limitNum,
        totalPages: Math.ceil(total / limitNum),
        hasNext: pageNum * limitNum < total,
        hasPrev: pageNum > 1
      }
    });
  } catch (err) {
    next(err);
  }
});
```

---

---

# Node.js — Module 13: File Uploads

```bash
npm install multer
```

```javascript
// middleware/upload.js
import multer from 'multer';
import path from 'path';

const storage = multer.memoryStorage();   // store in memory as Buffer

const fileFilter = (req, file, cb) => {
  const allowed = ['image/jpeg', 'image/png', 'image/webp'];
  if (allowed.includes(file.mimetype)) {
    cb(null, true);
  } else {
    cb(new Error('Only JPEG, PNG, and WebP images allowed'), false);
  }
};

export const uploadAvatar = multer({
  storage,
  limits: { fileSize: 2 * 1024 * 1024 },  // 2MB
  fileFilter
}).single('avatar');

export const uploadDocuments = multer({
  storage,
  limits: { fileSize: 10 * 1024 * 1024 }  // 10MB
}).array('documents', 5);  // up to 5 files
```

```javascript
// routes/users.js — avatar upload
import sharp from 'sharp';   // npm install sharp
import { uploadAvatar } from '../middleware/upload.js';

router.post('/me/avatar', requireAuth, uploadAvatar, async (req, res, next) => {
  try {
    if (!req.file) return res.status(400).json({ error: 'No file uploaded' });

    // Process image with sharp: resize and convert to PNG
    const processed = await sharp(req.file.buffer)
      .resize(200, 200)          // square thumbnail
      .png()
      .toBuffer();

    req.user.avatar = processed;
    await req.user.save();
    res.json({ message: 'Avatar uploaded' });
  } catch (err) {
    next(err);
  }
}, (err, req, res, next) => {
  // Multer errors
  res.status(400).json({ error: err.message });
});

// Serve avatar
router.get('/:id/avatar', async (req, res, next) => {
  try {
    const user = await User.findById(req.params.id);
    if (!user?.avatar) return res.status(404).json({ error: 'No avatar' });

    res.set('Content-Type', 'image/png');
    res.set('Cache-Control', 'public, max-age=86400');
    res.send(user.avatar);
  } catch (err) {
    next(err);
  }
});

// Delete avatar
router.delete('/me/avatar', requireAuth, async (req, res, next) => {
  req.user.avatar = undefined;
  await req.user.save();
  res.json({ message: 'Avatar removed' });
});
```

---

---

# Node.js — Module 14: Sending Emails

```bash
npm install nodemailer @sendgrid/mail
```

```javascript
// services/email.js
import nodemailer from 'nodemailer';

const transporter = nodemailer.createTransport({
  service: 'SendGrid',
  auth: {
    user: 'apikey',
    pass: process.env.SENDGRID_API_KEY
  }
});

export async function sendWelcomeEmail(to, name) {
  await transporter.sendMail({
    from: '"Task App" <noreply@taskapp.com>',
    to,
    subject: `Welcome to Task App, ${name}!`,
    text: `Hi ${name}, thanks for joining Task App!`,
    html: `
      <h1>Welcome, ${name}!</h1>
      <p>Thanks for joining Task App. Get started by creating your first task.</p>
      <a href="${process.env.APP_URL}/dashboard">Go to Dashboard</a>
    `
  });
}

export async function sendPasswordResetEmail(to, token) {
  const resetUrl = `${process.env.APP_URL}/reset-password?token=${token}`;
  await transporter.sendMail({
    from: '"Task App" <noreply@taskapp.com>',
    to,
    subject: 'Password Reset Request',
    html: `
      <p>You requested a password reset. Click the link below:</p>
      <a href="${resetUrl}">Reset Password</a>
      <p>This link expires in 1 hour. If you didn't request this, ignore this email.</p>
    `
  });
}

export async function sendCancellationEmail(to, name) {
  await transporter.sendMail({
    from: '"Task App" <noreply@taskapp.com>',
    to,
    subject: 'Your account has been deleted',
    text: `Sorry to see you go, ${name}. Your data has been removed.`
  });
}
```

---

---

# Node.js — Module 15: Testing

```bash
npm install jest supertest --save-dev
```

```json
// package.json
{
  "scripts": { "test": "jest --forceExit --coverage" },
  "jest": {
    "testEnvironment": "node",
    "setupFilesAfterFramework": ["./tests/setup.js"]
  }
}
```

```javascript
// tests/setup.js
import mongoose from 'mongoose';
import app from '../src/app.js';
import { MongoMemoryServer } from 'mongodb-memory-server';

let mongoServer;

beforeAll(async () => {
  mongoServer = await MongoMemoryServer.create();
  await mongoose.connect(mongoServer.getUri());
});

afterAll(async () => {
  await mongoose.disconnect();
  await mongoServer.stop();
});

afterEach(async () => {
  const collections = mongoose.connection.collections;
  for (const key in collections) {
    await collections[key].deleteMany();
  }
});
```

```javascript
// tests/task.test.js
import request from 'supertest';
import app from '../src/app.js';
import User from '../src/models/User.js';
import Task from '../src/models/Task.js';

let testUser, token;

beforeEach(async () => {
  testUser = await User.create({
    name: 'Test User',
    email: 'test@test.com',
    password: 'Password123'
  });
  token = await testUser.generateAuthToken();
});

describe('POST /tasks', () => {
  test('should create a task for authenticated user', async () => {
    const res = await request(app)
      .post('/api/tasks')
      .set('Authorization', `Bearer ${token}`)
      .send({ description: 'Test task' })
      .expect(201);

    expect(res.body.description).toBe('Test task');
    expect(res.body.completed).toBe(false);

    const task = await Task.findById(res.body._id);
    expect(task).not.toBeNull();
  });

  test('should fail without authentication', async () => {
    await request(app)
      .post('/api/tasks')
      .send({ description: 'Test task' })
      .expect(401);
  });

  test('should fail with empty description', async () => {
    const res = await request(app)
      .post('/api/tasks')
      .set('Authorization', `Bearer ${token}`)
      .send({ description: '' })
      .expect(400);

    expect(res.body.error).toBeDefined();
  });
});

describe('GET /tasks', () => {
  beforeEach(async () => {
    await Task.create([
      { description: 'Task 1', completed: false, owner: testUser._id },
      { description: 'Task 2', completed: true, owner: testUser._id },
      { description: 'Task 3', completed: false, owner: testUser._id }
    ]);
  });

  test('should get all tasks for the user', async () => {
    const res = await request(app)
      .get('/api/tasks')
      .set('Authorization', `Bearer ${token}`)
      .expect(200);

    expect(res.body.data).toHaveLength(3);
  });

  test('should filter by completion status', async () => {
    const res = await request(app)
      .get('/api/tasks?completed=true')
      .set('Authorization', `Bearer ${token}`)
      .expect(200);

    expect(res.body.data).toHaveLength(1);
    expect(res.body.data[0].description).toBe('Task 2');
  });
});
```

---

---

# Node.js — Module 16: Real-Time Apps with Socket.io (Chat App)

```bash
npm install socket.io
```

```javascript
// server.js
import express from 'express';
import { createServer } from 'http';
import { Server } from 'socket.io';
import path from 'path';
import { fileURLToPath } from 'url';
import { generateMessage, generateLocationMessage } from './utils/messages.js';
import { addUser, removeUser, getUser, getUsersInRoom } from './utils/users.js';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const app = express();
const httpServer = createServer(app);
const io = new Server(httpServer, {
  cors: { origin: '*' }
});

app.use(express.static(path.join(__dirname, '../public')));

io.on('connection', (socket) => {
  console.log('New WebSocket connection:', socket.id);

  socket.on('join', ({ username, room }, callback) => {
    const { error, user } = addUser({ id: socket.id, username, room });
    if (error) return callback(error);

    socket.join(room);

    // Welcome message to joining user
    socket.emit('message', generateMessage('System', `Welcome to ${room}, ${username}!`));

    // Notify others in room
    socket.to(room).emit('message', generateMessage('System', `${username} joined the room`));

    // Update room user list for everyone
    io.to(room).emit('roomData', {
      room,
      users: getUsersInRoom(room)
    });

    callback();
  });

  socket.on('sendMessage', (text, callback) => {
    const user = getUser(socket.id);
    if (!user) return callback('Not in a room');

    // Emit to everyone in room (including sender)
    io.to(user.room).emit('message', generateMessage(user.username, text));
    callback();
  });

  socket.on('sendLocation', ({ lat, lon }, callback) => {
    const user = getUser(socket.id);
    if (!user) return callback('Not in a room');

    io.to(user.room).emit('locationMessage',
      generateLocationMessage(user.username, lat, lon)
    );
    callback();
  });

  socket.on('disconnect', () => {
    const user = removeUser(socket.id);
    if (user) {
      io.to(user.room).emit('message',
        generateMessage('System', `${user.username} left the room`)
      );
      io.to(user.room).emit('roomData', {
        room: user.room,
        users: getUsersInRoom(user.room)
      });
    }
  });
});

httpServer.listen(3000, () => console.log('Server on :3000'));
```

```javascript
// public/js/chat.js — Browser-side Socket.io client
const socket = io();

// Join room
const { username, room } = Qs.parse(location.search, { ignoreQueryPrefix: true });

socket.emit('join', { username, room }, (error) => {
  if (error) {
    alert(error);
    location.href = '/';
  }
});

// Receive messages
socket.on('message', (message) => {
  const el = document.createElement('div');
  el.innerHTML = `
    <p><strong>${message.username}</strong> 
       <span class="time">${moment(message.createdAt).format('h:mm a')}</span></p>
    <p>${message.text}</p>
  `;
  document.getElementById('messages').appendChild(el);
});

// Receive location
socket.on('locationMessage', (msg) => {
  const el = document.createElement('div');
  el.innerHTML = `
    <p><strong>${msg.username}</strong></p>
    <p><a href="${msg.url}" target="_blank">Current Location</a></p>
  `;
  document.getElementById('messages').appendChild(el);
});

// Room members list
socket.on('roomData', ({ room, users }) => {
  document.getElementById('room-name').textContent = room;
  document.getElementById('users').innerHTML =
    users.map(u => `<li>${u.username}</li>`).join('');
});

// Send message
document.getElementById('messageForm').addEventListener('submit', (e) => {
  e.preventDefault();
  const input = e.target.elements.message;
  socket.emit('sendMessage', input.value, (err) => {
    if (err) console.error(err);
    input.value = '';
    input.focus();
  });
});

// Share location
document.getElementById('locationBtn').addEventListener('click', () => {
  if (!navigator.geolocation) return alert('Geolocation not supported');

  navigator.geolocation.getCurrentPosition(({ coords }) => {
    socket.emit('sendLocation', {
      lat: coords.latitude,
      lon: coords.longitude
    }, (err) => { if (err) console.error(err); });
  });
});
```
