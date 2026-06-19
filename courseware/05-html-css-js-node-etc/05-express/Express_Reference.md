# Express — Module 01–06: Complete Express Reference

## 01. Express Introduction

Express is a minimal, unopinionated web framework for Node.js. It is the most popular Node.js framework with 20M+ weekly downloads. NestJS (enterprise) and Fastify (performance) are built on the same ideas.

### Philosophy

Express has three core concepts: **routes**, **middleware**, and **request/response objects**. That's it. Everything else — authentication, database, validation — you compose yourself.

---

## 02. Request & Response, Router, Express Generator

### Request Object (`req`)

```javascript
app.get('/api/users/:id', (req, res) => {
  // URL params
  req.params.id              // ':id' from URL

  // Query string  /api/users?role=admin&limit=10
  req.query.role             // 'admin'
  req.query.limit            // '10' (always a string!)
  parseInt(req.query.limit)  // 10

  // Request body (requires express.json() middleware)
  req.body.name              // from POST/PUT/PATCH body

  // Headers
  req.headers.authorization
  req.headers['content-type']
  req.get('Authorization')   // case-insensitive helper

  // Other
  req.method                 // 'GET', 'POST', etc.
  req.path                   // '/api/users/123'
  req.url                    // '/api/users/123?role=admin'
  req.hostname               // 'api.example.com'
  req.ip                     // client IP
  req.protocol               // 'http' or 'https'
  req.secure                 // req.protocol === 'https'
  req.cookies                // requires cookie-parser
  req.user                   // set by auth middleware
});
```

### Response Object (`res`)

```javascript
// Status + JSON
res.status(201).json({ id: 1, name: 'Alice' });
res.status(400).json({ error: 'Bad request' });

// Convenience status methods
res.sendStatus(204);    // 204 No Content (no body)
res.sendStatus(404);    // sends '404 Not Found' as body

// HTML
res.send('<h1>Hello</h1>');

// File download
res.download('./report.pdf', 'Q3-Report.pdf');

// Redirect
res.redirect('/login');
res.redirect(301, '/new-url');

// Set headers
res.set('X-Custom-Header', 'value');
res.set('Content-Type', 'application/json');
res.set({ 'Cache-Control': 'no-store', 'X-Powered-By': 'Node.js' });

// Cookies
res.cookie('token', jwtToken, {
  httpOnly: true,
  secure: process.env.NODE_ENV === 'production',
  maxAge: 7 * 24 * 60 * 60 * 1000,  // 7 days in ms
  sameSite: 'strict'
});
res.clearCookie('token');

// Render template (with template engine like EJS or Handlebars)
res.render('dashboard', { user: req.user, title: 'Dashboard' });
```

### Express Router

```javascript
// Modular routing
import { Router } from 'express';

const router = Router();

// Chainable route handlers
router.route('/tasks')
  .get(requireAuth, getAllTasks)
  .post(requireAuth, createTask);

router.route('/tasks/:id')
  .get(requireAuth, getTask)
  .patch(requireAuth, updateTask)
  .delete(requireAuth, deleteTask);

// Router-level middleware
router.use(requireAuth);   // applies to all routes in this router

export default router;
```

### Express Generator (Scaffold a Project)

```bash
npx express-generator --no-view myapp
cd myapp && npm install
npm run start
```

---

## 03. Movie Fan App — Project Walkthrough

```javascript
// A server-side rendered app using Express + EJS

// views/movies.ejs — template
// routes/movies.js
import { Router } from 'express';
import axios from 'axios';

const router = Router();
const TMDB_KEY = process.env.TMDB_API_KEY;

router.get('/', async (req, res) => {
  const { data } = await axios.get(
    `https://api.themoviedb.org/3/movie/popular?api_key=${TMDB_KEY}`
  );
  res.render('movies/index', { movies: data.results, title: 'Popular Movies' });
});

router.get('/:id', async (req, res) => {
  const { data: movie } = await axios.get(
    `https://api.themoviedb.org/3/movie/${req.params.id}?api_key=${TMDB_KEY}`
  );
  res.render('movies/show', { movie });
});

export default router;
```

---

## 04. Building a REST API — Complete Project

```javascript
// src/app.js — production-ready Express setup
import express from 'express';
import helmet from 'helmet';
import cors from 'cors';
import rateLimit from 'express-rate-limit';
import mongoSanitize from 'express-mongo-sanitize';
import compression from 'compression';
import { errorHandler, notFound } from './middleware/errors.js';
import routes from './routes/index.js';

const app = express();

// Security
app.use(helmet());
app.use(cors({ origin: process.env.ALLOWED_ORIGINS?.split(',') ?? '*' }));
app.use(mongoSanitize());
app.use(rateLimit({ windowMs: 15 * 60 * 1000, max: 100 }));

// Body parsing
app.use(express.json({ limit: '10kb' }));
app.use(express.urlencoded({ extended: false }));
app.use(compression());

// Request logging
app.use((req, res, next) => {
  console.log(`${req.method} ${req.path} - ${req.ip}`);
  next();
});

// Routes
app.use('/api/v1', routes);
app.use('/health', (req, res) => res.json({ status: 'ok', uptime: process.uptime() }));

// Error handling
app.use(notFound);
app.use(errorHandler);

export default app;
```

```javascript
// middleware/errors.js
export const notFound = (req, res) => {
  res.status(404).json({ error: `Route ${req.originalUrl} not found` });
};

export const errorHandler = (err, req, res, next) => {
  console.error(err.stack);

  // Mongoose validation error
  if (err.name === 'ValidationError') {
    const messages = Object.values(err.errors).map(e => e.message);
    return res.status(400).json({ error: 'Validation failed', details: messages });
  }

  // Mongoose duplicate key
  if (err.code === 11000) {
    const field = Object.keys(err.keyValue)[0];
    return res.status(409).json({ error: `${field} already exists` });
  }

  // JWT errors
  if (err.name === 'JsonWebTokenError') return res.status(401).json({ error: 'Invalid token' });
  if (err.name === 'TokenExpiredError') return res.status(401).json({ error: 'Token expired' });

  // Custom app errors
  if (err.isOperational) {
    return res.status(err.statusCode ?? 400).json({ error: err.message });
  }

  // Unknown error
  res.status(500).json({ error: 'Internal server error' });
};
```

---

## 05. Passport.js — Authentication Strategies

```bash
npm install passport passport-local passport-jwt
```

```javascript
// config/passport.js
import passport from 'passport';
import { Strategy as LocalStrategy } from 'passport-local';
import { Strategy as JwtStrategy, ExtractJwt } from 'passport-jwt';
import User from '../models/User.js';

// Local strategy: email + password
passport.use(new LocalStrategy(
  { usernameField: 'email' },
  async (email, password, done) => {
    try {
      const user = await User.findByCredentials(email, password);
      done(null, user);
    } catch (err) {
      done(null, false, { message: err.message });
    }
  }
));

// JWT strategy: Bearer token
passport.use(new JwtStrategy(
  {
    jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
    secretOrKey: process.env.JWT_SECRET
  },
  async (payload, done) => {
    try {
      const user = await User.findById(payload._id);
      if (!user) return done(null, false);
      done(null, user);
    } catch (err) {
      done(err, false);
    }
  }
));

export default passport;

// Usage
app.use(passport.initialize());

// Routes
router.post('/login',
  passport.authenticate('local', { session: false }),
  async (req, res) => {
    const token = await req.user.generateAuthToken();
    res.json({ user: req.user, token });
  }
);

router.get('/profile',
  passport.authenticate('jwt', { session: false }),
  (req, res) => res.json(req.user)
);
```
