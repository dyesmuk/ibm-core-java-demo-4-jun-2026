# Module 14 — Deploying the App to the Web

## Learning Objectives
- Understand what the production build does
- Configure environment variables per environment
- Deploy to Vercel (zero-config) and Netlify
- Containerise with Docker
- Set up a basic CI/CD pipeline with GitHub Actions

---

## 14.1 The Production Build

```bash
npm run build
```

Vite does several things:
- TypeScript → JavaScript
- JSX → optimised JS
- **Tree-shaking** — removes unused code
- **Code splitting** — splits into chunks loaded on demand
- **Minification** — removes whitespace, shortens names
- **Asset hashing** — `EmployeeCard.abc123.js` for cache busting

Output goes to `dist/`:

```
dist/
├── index.html
└── assets/
    ├── index-Bx3k9Prt.js        ← main bundle (minified)
    ├── index-DUJ4WQCK.css
    └── react-DOM.CHkl9Qks.js    ← vendor chunk (cached by browser)
```

Preview the build locally:

```bash
npm run preview   # serves dist/ at http://localhost:4173
```

---

## 14.2 Environment Variables per Environment

```bash
# .env.development       (npm run dev)
VITE_API_BASE_URL=http://localhost:3000/api
VITE_APP_TITLE=IBM EMS [Dev]

# .env.production        (npm run build)
VITE_API_BASE_URL=https://api.ibm-ems.com/v1
VITE_APP_TITLE=IBM EMS

# .env.local             (git-ignored, secrets)
VITE_API_KEY=sk-...
```

```ts
// Access anywhere in your code
const apiUrl = import.meta.env.VITE_API_BASE_URL
const isProd  = import.meta.env.PROD     // boolean
const isDev   = import.meta.env.DEV      // boolean
```

> **Security:** Only `VITE_` prefixed variables are included in the bundle. Never put server-side secrets here — they end up in the public JavaScript.

---

## 14.3 Deploy to Vercel

Vercel is the fastest zero-config option for React/Vite apps.

```bash
npm install -g vercel
vercel login
vercel                   # follow prompts — done in ~60 seconds
```

Or connect via GitHub:
1. Push your project to GitHub
2. Go to [vercel.com](https://vercel.com) → New Project
3. Import the repo → Vercel auto-detects Vite
4. Add environment variables in the dashboard
5. Every push to `main` triggers an auto-deploy

### Vite-specific Vercel config

```json
// vercel.json
{
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ]
}
```

This rewrites all URLs to `index.html` so React Router handles them — without this, refreshing `/employees/1` returns a 404.

---

## 14.4 Deploy to Netlify

```bash
npm run build
# Drag and drop the dist/ folder to netlify.com/drop
```

Or CLI:

```bash
npm install -g netlify-cli
netlify login
netlify deploy --dir=dist          # preview
netlify deploy --dir=dist --prod   # production
```

### `public/_redirects` — for React Router

```
/*    /index.html    200
```

Without this, Netlify returns a 404 on page refresh for any deep route.

---

## 14.5 Docker

Docker packages the app with everything it needs — portable across any server.

### `Dockerfile`

```dockerfile
# Stage 1 — Build
FROM node:20-alpine AS build

WORKDIR /app
COPY package*.json ./
RUN npm ci                    # clean install (faster than npm install in CI)
COPY . .
RUN npm run build             # output in /app/dist

# Stage 2 — Serve with nginx
FROM nginx:stable-alpine

# Copy build output
COPY --from=build /app/dist /usr/share/nginx/html

# Copy nginx config (handles SPA routing)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### `nginx.conf`

```nginx
server {
  listen 80;
  root /usr/share/nginx/html;
  index index.html;

  # SPA routing — all paths serve index.html
  location / {
    try_files $uri $uri/ /index.html;
  }

  # Cache static assets aggressively (they have content hashes)
  location /assets {
    expires 1y;
    add_header Cache-Control "public, immutable";
  }
}
```

```bash
# Build the image
docker build -t ibm-ems-app .

# Run locally
docker run -p 8080:80 ibm-ems-app

# Open http://localhost:8080
```

### `.dockerignore`

```
node_modules
dist
.env
.env.local
*.md
```

---

## 14.6 GitHub Actions CI/CD

```yaml
# .github/workflows/deploy.yml

name: Build, Test & Deploy

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'

      - name: Install dependencies
        run: npm ci

      - name: Lint
        run: npm run lint

      - name: Run tests
        run: npm run test -- --run   # run once (not watch mode)

      - name: Build
        run: npm run build
        env:
          VITE_API_BASE_URL: ${{ secrets.VITE_API_BASE_URL }}

      - name: Upload build artifact
        uses: actions/upload-artifact@v4
        with:
          name: dist
          path: dist/

  deploy:
    needs: build-and-test          # only runs if tests pass
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'   # only on main branch

    steps:
      - name: Download build
        uses: actions/download-artifact@v4
        with:
          name: dist
          path: dist/

      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v25
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
          vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
          vercel-args: '--prod'
```

---

## 14.7 Performance Checklist Before Deploy

```bash
# Analyse your bundle size
npm install -D rollup-plugin-visualizer
```

```ts
// vite.config.ts
import { visualizer } from 'rollup-plugin-visualizer'

export default defineConfig({
  plugins: [
    react(),
    visualizer({ open: true, gzipSize: true }),   // opens bundle map after build
  ],
})
```

**Common optimisations:**

```ts
// 1. Lazy load pages — only load code when the route is visited
import { lazy, Suspense } from 'react'

const EmployeesPage  = lazy(() => import('./pages/EmployeesPage'))
const CreatePage     = lazy(() => import('./pages/CreateEmployeePage'))

// Wrap routes with Suspense
<Suspense fallback={<p>Loading page…</p>}>
  <Routes>
    <Route path="/employees" element={<EmployeesPage />} />
    <Route path="/employees/create" element={<CreatePage />} />
  </Routes>
</Suspense>

// 2. Manual chunk splitting for vendor code
// vite.config.ts
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'redux-vendor': ['@reduxjs/toolkit', 'react-redux'],
        },
      },
    },
  },
})
```

---

## Summary

| Target | Command / Tool |
|--------|---------------|
| Local preview | `npm run preview` |
| Vercel | `vercel --prod` or GitHub auto-deploy |
| Netlify | `netlify deploy --prod` + `_redirects` file |
| Docker | `docker build` + nginx + `nginx.conf` |
| CI/CD | GitHub Actions — test → build → deploy |

**Deployment checklist:**
- [ ] Environment variables set for production
- [ ] SPA redirect rule configured (`_redirects` / `vercel.json` / `nginx.conf`)
- [ ] Tests passing in CI before deploy
- [ ] Bundle size analysed (no accidental large dependencies)
- [ ] `console.log` and debug code removed (or guarded by `import.meta.env.DEV`)

**Next → Bonus Modules (Webpack, Next.js, Animations, Redux Saga)**
