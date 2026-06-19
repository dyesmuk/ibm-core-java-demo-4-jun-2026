# Node.js — Module 05: Asynchronous Node.js (Weather App)

## Async Patterns in Node.js

Node.js excels at I/O — reading files, making HTTP requests, querying databases — all simultaneously without blocking. The weather app project demonstrates real-world async patterns.

## Making HTTP Requests — `node-fetch` / built-in `fetch`

Node.js 18+ includes the `fetch` API natively:

```javascript
// Fetching weather data (OpenWeatherMap API)
const API_KEY = process.env.WEATHER_API_KEY;

async function getCoordinates(city) {
  const url = `https://api.openweathermap.org/geo/1.0/direct?q=${encodeURIComponent(city)}&limit=1&appid=${API_KEY}`;
  const response = await fetch(url);
  
  if (!response.ok) {
    throw new Error(`Geocoding failed: ${response.status}`);
  }
  
  const [location] = await response.json();
  if (!location) throw new Error(`City not found: ${city}`);
  
  return { lat: location.lat, lon: location.lon, name: location.name };
}

async function getWeather(lat, lon) {
  const url = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${API_KEY}&units=metric`;
  const response = await fetch(url);
  if (!response.ok) throw new Error(`Weather fetch failed: ${response.status}`);
  return response.json();
}

async function getWeatherForCity(city) {
  const { lat, lon, name } = await getCoordinates(city);
  const weather = await getWeather(lat, lon);
  return {
    city: name,
    temp: weather.main.temp,
    feelsLike: weather.main.feels_like,
    humidity: weather.main.humidity,
    description: weather.weather[0].description
  };
}

// CLI usage
const city = process.argv[2] ?? 'Bengaluru';
getWeatherForCity(city)
  .then(data => {
    console.log(`Weather in ${data.city}:`);
    console.log(`  ${data.temp}°C, feels like ${data.feelsLike}°C`);
    console.log(`  ${data.description}`);
    console.log(`  Humidity: ${data.humidity}%`);
  })
  .catch(err => console.error('Error:', err.message));
```

## Node.js Built-in `http` for Requests

```javascript
import https from 'https';

function httpsGet(url) {
  return new Promise((resolve, reject) => {
    https.get(url, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try { resolve(JSON.parse(data)); }
        catch (e) { reject(e); }
      });
    }).on('error', reject);
  });
}
```

## Error Handling Strategy

```javascript
// Centralised error handling for the weather app
class WeatherError extends Error {
  constructor(message, type) {
    super(message);
    this.name = 'WeatherError';
    this.type = type;   // 'NOT_FOUND', 'API_ERROR', 'NETWORK_ERROR'
  }
}

async function fetchWithRetry(url, maxRetries = 3) {
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      const response = await fetch(url);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      return await response.json();
    } catch (err) {
      if (attempt === maxRetries) throw err;
      const delay = 1000 * 2 ** (attempt - 1);  // exponential backoff: 1s, 2s, 4s
      console.warn(`Attempt ${attempt} failed. Retrying in ${delay}ms...`);
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }
}
```

---

---

# Node.js — Module 06: Web Servers

## Built-in `http` Module

```javascript
import http from 'http';

const server = http.createServer((req, res) => {
  console.log(`${req.method} ${req.url}`);

  if (req.url === '/') {
    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.end('<h1>Hello World</h1>');
  } else if (req.url === '/api/status') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ status: 'ok', uptime: process.uptime() }));
  } else {
    res.writeHead(404);
    res.end('Not Found');
  }
});

server.listen(3000, () => {
  console.log('Server running at http://localhost:3000');
});
```

## Express — The Practical Way

```bash
npm install express
```

```javascript
import express from 'express';

const app = express();
const PORT = process.env.PORT ?? 3000;

// Middleware
app.use(express.json());                          // parse JSON bodies
app.use(express.urlencoded({ extended: true }));  // parse form data
app.use(express.static('public'));                // serve static files

// Routes
app.get('/', (req, res) => {
  res.send('<h1>Weather App</h1>');
});

app.get('/weather', async (req, res) => {
  const { city } = req.query;  // /weather?city=Bengaluru

  if (!city) {
    return res.status(400).json({ error: 'city query parameter is required' });
  }

  try {
    const data = await getWeatherForCity(city);
    res.json(data);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Route parameters
app.get('/users/:id', async (req, res) => {
  const { id } = req.params;
  const user = await User.findById(id);
  if (!user) return res.status(404).json({ error: 'User not found' });
  res.json(user);
});

// Error handling middleware (must have 4 params)
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(err.statusCode ?? 500).json({
    error: err.message ?? 'Internal Server Error'
  });
});

// 404 handler (must be last)
app.use((req, res) => {
  res.status(404).json({ error: 'Route not found' });
});

app.listen(PORT, () => console.log(`Server on http://localhost:${PORT}`));
```

## Middleware Deep Dive

```javascript
// Middleware = function(req, res, next)

// 1. Application-level
app.use((req, res, next) => {
  console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
  next();   // call next() to pass control to next middleware/route
});

// 2. Router-level
const router = express.Router();
router.get('/users', getAllUsers);
app.use('/api/v1', router);

// 3. Authentication middleware
function requireAuth(req, res, next) {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) return res.status(401).json({ error: 'No token provided' });

  try {
    req.user = jwt.verify(token, process.env.JWT_SECRET);
    next();
  } catch {
    res.status(401).json({ error: 'Invalid token' });
  }
}

// 4. Rate limiting
import rateLimit from 'express-rate-limit';
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000,   // 15 minutes
  max: 100                      // 100 requests per window
});
app.use('/api/', limiter);

// 5. CORS
import cors from 'cors';
app.use(cors({
  origin: ['http://localhost:3000', 'https://myapp.com'],
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  credentials: true
}));

// 6. Compression
import compression from 'compression';
app.use(compression());

// 7. Helmet (security headers)
import helmet from 'helmet';
app.use(helmet());
```

## Routing Structure

```javascript
// routes/users.js
import { Router } from 'express';
const router = Router();

router.get('/',    requireAuth, getAllUsers);
router.post('/',   requireAuth, createUser);
router.get('/:id', requireAuth, getUserById);
router.patch('/:id', requireAuth, updateUser);
router.delete('/:id', requireAuth, deleteUser);

export default router;

// routes/index.js
import { Router } from 'express';
import userRoutes    from './users.js';
import productRoutes from './products.js';
import orderRoutes   from './orders.js';

const router = Router();
router.use('/users',    userRoutes);
router.use('/products', productRoutes);
router.use('/orders',   orderRoutes);

export default router;

// app.js
app.use('/api/v1', routes);
```

---

---

# Node.js — Module 07: Accessing APIs from the Browser

## Serving the Frontend

```javascript
// public/index.html — served as static file by Express
```

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Weather App</title>
</head>
<body>
  <h1>Weather Finder</h1>
  <input type="text" id="city" placeholder="Enter city name">
  <button id="searchBtn">Get Weather</button>
  <div id="result"></div>

  <script src="/js/app.js"></script>
</body>
</html>
```

```javascript
// public/js/app.js — runs in browser
const cityInput  = document.getElementById('city');
const searchBtn  = document.getElementById('searchBtn');
const resultDiv  = document.getElementById('result');

searchBtn.addEventListener('click', async () => {
  const city = cityInput.value.trim();
  if (!city) return;

  resultDiv.textContent = 'Loading...';
  try {
    const response = await fetch(`/weather?city=${encodeURIComponent(city)}`);
    
    if (!response.ok) {
      const err = await response.json();
      throw new Error(err.error);
    }
    
    const data = await response.json();
    resultDiv.innerHTML = `
      <h2>${data.city}</h2>
      <p>${data.temp}°C — ${data.description}</p>
      <p>Humidity: ${data.humidity}%</p>
    `;
  } catch (err) {
    resultDiv.textContent = `Error: ${err.message}`;
  }
});
```

## CORS Configuration

Cross-Origin Resource Sharing (CORS) — when browser frontend and API are on different origins:

```javascript
import cors from 'cors';

// Allow specific origins
app.use(cors({
  origin(origin, callback) {
    const allowed = ['http://localhost:3000', 'https://myapp.com'];
    if (!origin || allowed.includes(origin)) {
      callback(null, true);
    } else {
      callback(new Error('Not allowed by CORS'));
    }
  },
  credentials: true,         // allow cookies
  optionsSuccessStatus: 200  // for legacy browsers
}));
```

---

---

# Node.js — Module 08: Deployment

## Environment Variables

```javascript
// .env file (never commit to Git)
PORT=3000
NODE_ENV=production
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/mydb
JWT_SECRET=your-secret-key-here
WEATHER_API_KEY=abc123

// .gitignore
.env
node_modules/
```

```javascript
// Load with dotenv
import dotenv from 'dotenv';
dotenv.config();   // reads .env file into process.env

const PORT = parseInt(process.env.PORT ?? '3000', 10);
const DB_URI = process.env.MONGODB_URI;
const JWT_SECRET = process.env.JWT_SECRET;

if (!DB_URI || !JWT_SECRET) {
  console.error('Missing required environment variables');
  process.exit(1);
}
```

## Production Checklist

```javascript
// config.js — centralised config
const config = {
  port: parseInt(process.env.PORT ?? '3000', 10),
  env: process.env.NODE_ENV ?? 'development',
  isProduction: process.env.NODE_ENV === 'production',
  mongodb: {
    uri: process.env.MONGODB_URI ?? 'mongodb://localhost:27017/myapp'
  },
  jwt: {
    secret: process.env.JWT_SECRET ?? 'dev-secret',
    expiresIn: process.env.JWT_EXPIRES_IN ?? '7d'
  }
};

export default config;

// app.js — production settings
if (config.isProduction) {
  app.set('trust proxy', 1);   // behind reverse proxy (nginx, heroku)
  app.use(helmet());
  app.use(compression());
}
```

## Deploying to Railway / Render

Both Railway and Render are PaaS platforms similar to Heroku:

```bash
# Railway
npm install -g @railway/cli
railway login
railway init
railway up

# Environment variables set in dashboard
# railway variables set PORT=3000
```

```json
// package.json — required start script
{
  "scripts": {
    "start": "node src/index.js"
  },
  "engines": {
    "node": ">=20.0.0"
  }
}
```

## Process Management with PM2

```bash
npm install -g pm2

pm2 start src/index.js --name "weather-app"
pm2 start ecosystem.config.js
pm2 list
pm2 logs
pm2 restart weather-app
pm2 stop weather-app
pm2 startup    # auto-start on system reboot
pm2 save
```

```javascript
// ecosystem.config.js
export default {
  apps: [{
    name: 'weather-app',
    script: 'src/index.js',
    instances: 'max',      // one per CPU core
    exec_mode: 'cluster',  // cluster mode for multi-core
    env: { NODE_ENV: 'development' },
    env_production: { NODE_ENV: 'production' }
  }]
};
```
