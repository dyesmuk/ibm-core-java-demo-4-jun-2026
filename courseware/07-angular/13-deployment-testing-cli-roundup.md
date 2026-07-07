# Module 13 — Deploying, Testing, CLI & Course Roundup

---

# Part A — Deploying an Angular App

## Learning Objectives
- Build a production-optimised bundle
- Configure environment files
- Deploy to Vercel, Netlify, and Docker
- Set up CI/CD with GitHub Actions

---

## A.1 Production Build

```bash
ng build --configuration=production
```

What Angular does:
- AOT (Ahead-of-Time) compilation — templates compiled to JavaScript at build time, not runtime
- Tree-shaking — removes unused code
- Minification — removes whitespace, renames variables
- Content hashing — `main.abc123.js` for cache busting
- Differential loading — generates modern and legacy bundles

Output: `dist/ibm-ems-angular/browser/`

```bash
# Preview locally
npx http-server dist/ibm-ems-angular/browser -p 8080
```

---

## A.2 Environment Configuration

```ts
// src/environments/environment.ts (development)
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:3000/api',
  appTitle:   'IBM EMS [Dev]',
}

// src/environments/environment.production.ts
export const environment = {
  production: true,
  apiBaseUrl: 'https://api.ibm-ems.com/v1',
  appTitle:   'IBM EMS',
}
```

```ts
// angular.json — fileReplacements swaps files at build time
"configurations": {
  "production": {
    "fileReplacements": [
      {
        "replace": "src/environments/environment.ts",
        "with":    "src/environments/environment.production.ts"
      }
    ]
  }
}
```

---

## A.3 Deploy to Vercel

```bash
npm install -g vercel
vercel login
vercel --prod
```

### `vercel.json` — SPA routing fix

```json
{
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ]
}
```

Without this, navigating directly to `/employees/1` returns a 404 — Vercel can't find the file. The rewrite sends all requests to `index.html` so Angular Router handles them.

---

## A.4 Deploy to Netlify

```bash
npm run build
# Drag dist/ibm-ems-angular/browser/ to netlify.com/drop
```

### `dist/ibm-ems-angular/browser/_redirects`

Create this file before dragging (or via `angular.json` assets):

```
/*    /index.html   200
```

Or CLI:

```bash
npm install -g netlify-cli
netlify login
netlify deploy --dir=dist/ibm-ems-angular/browser --prod
```

---

## A.5 Docker

```dockerfile
# Dockerfile

# Stage 1 — Build
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration=production

# Stage 2 — Serve with nginx
FROM nginx:stable-alpine
COPY --from=build /app/dist/ibm-ems-angular/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

```nginx
# nginx.conf
server {
  listen 80;
  root /usr/share/nginx/html;
  index index.html;

  location / {
    try_files $uri $uri/ /index.html;   # SPA routing
  }

  location ~* \.(js|css|png|jpg|svg|ico)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
  }
}
```

```bash
docker build -t ibm-ems-angular .
docker run -p 8080:80 ibm-ems-angular
# Open http://localhost:8080
```

---

## A.6 GitHub Actions CI/CD

```yaml
# .github/workflows/deploy.yml
name: Build, Test & Deploy

on:
  push:
    branches: [main]

jobs:
  build-test-deploy:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'

      - name: Install
        run: npm ci

      - name: Lint
        run: npm run lint

      - name: Test
        run: npm test -- --watch=false --browsers=ChromeHeadless

      - name: Build
        run: npm run build -- --configuration=production

      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v25
        with:
          vercel-token:      ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id:     ${{ secrets.VERCEL_ORG_ID }}
          vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
          vercel-args:       '--prod'
```

---

---

# Part B — Unit Testing in Angular

## Learning Objectives
- Write unit tests for components, services, and pipes
- Use Angular's `TestBed`
- Mock dependencies with `jasmine.createSpyObj`
- Test signals and Observables

---

## B.1 Testing Setup

Angular CLI generates test files automatically with `ng generate`. The default is **Jasmine + Karma** (or **Jest** if configured).

```bash
ng test                          # run tests in watch mode
ng test --watch=false            # run once
ng test --code-coverage          # generate coverage report
```

---

## B.2 Testing a Service

```ts
// src/app/employees/employee.service.spec.ts
import { TestBed } from '@angular/core/testing'
import { EmployeeService } from './employee.service'
import type { Employee } from '../shared/models/employee.model'

const mockEmployee: Employee = {
  id: 99, name: 'Test User', email: 'test@ibm.com',
  department: 'Engineering', salary: 800000, isActive: true, joinDate: '2022-01-01',
}

describe('EmployeeService', () => {
  let service: EmployeeService

  beforeEach(() => {
    TestBed.configureTestingModule({})
    service = TestBed.inject(EmployeeService)
  })

  it('should be created', () => {
    expect(service).toBeTruthy()
  })

  it('should initialise with employees', () => {
    expect(service.employees().length).toBeGreaterThan(0)
  })

  it('should add a new employee', () => {
    const initial = service.employees().length
    service.add({ ...mockEmployee, id: undefined as never })
    expect(service.employees().length).toBe(initial + 1)
    expect(service.employees().at(-1)?.name).toBe('Test User')
  })

  it('should remove an employee by id', () => {
    const id = service.employees()[0].id
    service.remove(id)
    expect(service.employees().find(e => e.id === id)).toBeUndefined()
  })

  it('should toggle employee active status', () => {
    const emp = service.employees()[0]
    const was = emp.isActive
    service.toggleActive(emp.id)
    expect(service.employees().find(e => e.id === emp.id)?.isActive).toBe(!was)
  })

  it('should compute activeCount correctly', () => {
    const activeCount = service.employees().filter(e => e.isActive).length
    expect(service.activeCount()).toBe(activeCount)
  })
})
```

---

## B.3 Testing a Component

```ts
// src/app/employees/employee-card/employee-card.component.spec.ts
import { ComponentFixture, TestBed } from '@angular/core/testing'
import { EmployeeCardComponent } from './employee-card.component'
import { CurrencyPipe, DatePipe } from '@angular/common'
import type { Employee } from '../../shared/models/employee.model'

const mockEmployee: Employee = {
  id: 1, name: 'Alice Johnson', email: 'alice@ibm.com',
  department: 'Engineering', salary: 1200000, isActive: true, joinDate: '2021-03-15',
}

describe('EmployeeCardComponent', () => {
  let component: EmployeeCardComponent
  let fixture:   ComponentFixture<EmployeeCardComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployeeCardComponent, CurrencyPipe, DatePipe],
    }).compileComponents()

    fixture   = TestBed.createComponent(EmployeeCardComponent)
    component = fixture.componentInstance

    // Set required input signal
    fixture.componentRef.setInput('employee', mockEmployee)
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should display the employee name', () => {
    const el = fixture.nativeElement as HTMLElement
    expect(el.querySelector('h3')?.textContent).toContain('Alice Johnson')
  })

  it('should display Active badge when isActive is true', () => {
    const badge = (fixture.nativeElement as HTMLElement).querySelector('.badge')
    expect(badge?.textContent?.trim()).toBe('Active')
  })

  it('should display Inactive badge when isActive is false', async () => {
    fixture.componentRef.setInput('employee', { ...mockEmployee, isActive: false })
    fixture.detectChanges()
    const badge = (fixture.nativeElement as HTMLElement).querySelector('.badge')
    expect(badge?.textContent?.trim()).toBe('Inactive')
  })

  it('should emit select event when card is clicked', () => {
    let emittedId: number | undefined
    component.select.subscribe((id: number) => emittedId = id)

    const card = fixture.nativeElement.querySelector('.card') as HTMLElement
    card.click()
    fixture.detectChanges()

    expect(emittedId).toBe(1)
  })
})
```

---

## B.4 Testing with Mock Services

```ts
// src/app/employees/employee-list-page/employee-list-page.component.spec.ts
import { ComponentFixture, TestBed } from '@angular/core/testing'
import { EmployeeListPageComponent } from './employee-list-page.component'
import { EmployeeService }            from '../employee.service'
import { signal }                     from '@angular/core'
import type { Employee }              from '../../shared/models/employee.model'

const mockEmployees: Employee[] = [
  { id: 1, name: 'Alice', email: 'alice@ibm.com', department: 'Engineering', salary: 900000, isActive: true, joinDate: '2021-01-01' },
  { id: 2, name: 'Bob',   email: 'bob@ibm.com',   department: 'Marketing',   salary: 700000, isActive: false, joinDate: '2020-01-01' },
]

// Create a mock service
const mockEmployeeService = {
  employees:   signal(mockEmployees),
  loading:     signal(false),
  error:       signal(null),
  activeCount: signal(1),
  loadAll:     jasmine.createSpy('loadAll'),
  remove:      jasmine.createSpy('remove'),
}

describe('EmployeeListPageComponent', () => {
  let fixture: ComponentFixture<EmployeeListPageComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:   [EmployeeListPageComponent],
      providers: [
        { provide: EmployeeService, useValue: mockEmployeeService }
      ],
    }).compileComponents()

    fixture = TestBed.createComponent(EmployeeListPageComponent)
    fixture.detectChanges()
  })

  it('should call loadAll on init', () => {
    expect(mockEmployeeService.loadAll).toHaveBeenCalled()
  })

  it('should render employee cards', () => {
    const cards = fixture.nativeElement.querySelectorAll('app-employee-card')
    expect(cards.length).toBe(2)
  })

  it('should show loading spinner when loading is true', () => {
    mockEmployeeService.loading.set(true)
    fixture.detectChanges()
    const spinner = fixture.nativeElement.querySelector('.loading-state')
    expect(spinner).toBeTruthy()
  })
})
```

---

## B.5 Testing Pipes

```ts
// src/app/shared/pipes/salary-range.pipe.spec.ts
import { SalaryRangePipe } from './salary-range.pipe'

describe('SalaryRangePipe', () => {
  const pipe = new SalaryRangePipe()

  it('should create', () => {
    expect(pipe).toBeTruthy()
  })

  it('should return Junior for salary below 500000', () => {
    expect(pipe.transform(400000)).toContain('Junior')
  })

  it('should return Mid for salary between 500000 and 1000000', () => {
    expect(pipe.transform(750000)).toContain('Mid')
  })

  it('should return Senior for salary between 1000000 and 2000000', () => {
    expect(pipe.transform(1500000)).toContain('Senior')
  })

  it('should return Principal for salary above 2000000', () => {
    expect(pipe.transform(2500000)).toContain('Principal')
  })
})
```

---

---

# Part C — Angular CLI Deep Dive

## Key Angular CLI Commands

```bash
# Generate artefacts
ng generate component path/component-name --skip-tests
ng generate service   path/service-name
ng generate pipe      path/pipe-name
ng generate guard     path/guard-name
ng generate directive path/directive-name
ng generate interface  path/interface-name --type=model
ng generate enum      path/enum-name

# Schematics options
ng g c my-comp --inline-template      # template in .ts file
ng g c my-comp --inline-style         # styles in .ts file
ng g c my-comp --flat                 # no folder created

# Build
ng build                               # dev build
ng build --configuration=production    # prod build
ng build --stats-json                  # for bundle analysis

# Run specific test file
ng test --include='**/employee.service.spec.ts'

# Lint and fix
ng lint --fix

# Update Angular
ng update @angular/core @angular/cli

# Add libraries
ng add @angular/material
ng add @ngrx/store
```

---

## angular.json — Key Configuration

```json
{
  "projects": {
    "ibm-ems-angular": {
      "architect": {
        "build": {
          "options": {
            "outputPath": "dist/ibm-ems-angular",
            "index": "src/index.html",
            "browser": "src/main.ts",
            "assets": ["src/favicon.ico", "src/assets"],
            "styles": ["src/styles.css"],
            "scripts": []
          },
          "configurations": {
            "production": {
              "optimization": true,
              "sourceMap": false,
              "namedChunks": false,
              "budgets": [
                {
                  "type": "initial",
                  "maximumWarning": "500kb",
                  "maximumError": "1mb"
                }
              ]
            }
          }
        }
      }
    }
  }
}
```

---

---

# Part D — Course Roundup

## What You Built

| Feature | Angular concepts applied |
|---------|--------------------------|
| Employee list with signals | `signal()`, `computed()`, `@for`, `@if` |
| Component tree | `input()`, `output()`, lifecycle hooks |
| Department filter | Directives, pipes, `OnPush` |
| Services + DI | `@Injectable`, `inject()`, singleton vs scoped |
| Multi-page routing | `Router`, `CanActivateFn`, lazy loading, guards |
| Search with RxJS | `switchMap`, `debounceTime`, `toSignal`, `toObservable` |
| Reactive forms | `FormBuilder`, `Validators`, custom validators, `FormArray` |
| Pipes | Built-in + 5 custom pipes |
| HTTP + interceptors | `HttpClient`, `provideHttpClient`, interceptors |
| Authentication | JWT, `AuthService`, role guards |
| Dynamic components | `createComponent`, `@defer` |
| Testing | `TestBed`, mock services, signal testing |
| Deployment | Vercel, Docker, GitHub Actions |

---

## Angular Ecosystem Map

```
Core
  Angular 21 (Signals, standalone components)
  TypeScript (mandatory)

State Management
  Signals (built-in — what we used) ← primary
  NgRx (Redux pattern for large teams)
  Akita / Elf (simpler alternatives)

HTTP & Data
  HttpClient (built-in — what we used)
  TanStack Query Angular (caching, background sync)

Forms
  Reactive Forms (what we used)
  Template-Driven Forms
  ngx-formly (dynamic forms)

UI Libraries
  Angular Material (Google Design System)
  PrimeNG (rich components)
  Ant Design for Angular

Testing
  Jasmine + Karma (default)
  Jest (faster, popular alternative)
  Playwright / Cypress (E2E)

Meta-frameworks
  Analog (Next.js for Angular — SSR, file routing)

Build
  Angular CLI + esbuild (Angular 17+)
  Nx (monorepo tool — common in enterprise)
```

---

## Signals Roadmap — Where Angular Is Going

Angular's roadmap is clear: Signals replace Zone.js-based change detection.

| Angular version | Signal milestone |
|-----------------|-----------------|
| 16 | Signals introduced (developer preview) |
| 17 | Signals stable, `@if`/`@for`, `@defer` |
| 18 | `signal()` inputs (`input()` API), `toSignal()` |
| 19 | Zoneless change detection (experimental) |
| 21 (current) | Signals + standalone as the default pattern |
| Future | Full zoneless — no Zone.js required |

**What this means for you:** The patterns in this course (`signal()`, `computed()`, `effect()`, `input()`, `output()`) are where all Angular development is heading. NgModule and Zone.js are on a long deprecation path.

---

## What to Learn Next

1. **Angular Material** — Google's component library, deeply integrated with Angular
2. **NgRx** — Redux for Angular — state management for very large teams
3. **Analog** — Angular's SSR meta-framework (like Next.js for Angular)
4. **NX Monorepo** — manage multiple apps/libraries in one repo — common in enterprise
5. **Angular CDK** — Component Dev Kit — build custom accessible UI components
6. **RxJS mastery** — `switchMap`, `mergeMap`, `concatMap`, `combineLatest` in depth
