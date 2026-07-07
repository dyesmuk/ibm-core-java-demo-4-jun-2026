# Module 09 — Pipes: Transforming Template Output

## Learning Objectives
- Use all Angular built-in pipes
- Build custom pure and impure pipes
- Understand pipe chaining
- EMS: salary pipe, department filter pipe, status pipe

---

## 9.1 What Are Pipes?

Pipes transform a value in the template without mutating the original data. They're applied with the `|` operator.

```html
{{ value | pipeName }}
{{ value | pipeName:arg1:arg2 }}
{{ value | pipe1 | pipe2 }}   <!-- chain pipes -->
```

Pipes keep templates clean — transformation logic stays out of the component class.

---

## 9.2 Built-in Pipes Reference

All from `@angular/common` — import individually in standalone components.

### `DatePipe`

```html
{{ employee.joinDate | date }}                  <!-- Nov 15, 2021 -->
{{ employee.joinDate | date:'shortDate' }}       <!-- 11/15/21 -->
{{ employee.joinDate | date:'mediumDate' }}      <!-- Nov 15, 2021 -->
{{ employee.joinDate | date:'longDate' }}        <!-- November 15, 2021 -->
{{ employee.joinDate | date:'yyyy-MM-dd' }}      <!-- 2021-11-15 -->
{{ employee.joinDate | date:'dd MMM yyyy' }}     <!-- 15 Nov 2021 -->
{{ employee.joinDate | date:'fullDate':'':'en-IN' }}  <!-- with locale -->
```

### `CurrencyPipe`

```html
{{ employee.salary | currency }}                           <!-- $95,000.00 -->
{{ employee.salary | currency:'INR' }}                     <!-- ₹95,000.00 -->
{{ employee.salary | currency:'INR':'symbol':'1.0-0' }}    <!-- ₹95,000 -->
{{ employee.salary | currency:'INR':'code':'1.0-0':'en-IN' }} <!-- INR 95,000 -->
```

### `DecimalPipe`

```html
{{ 1234567.89 | number }}             <!-- 1,234,567.89 -->
{{ 1234567.89 | number:'1.0-0' }}     <!-- 1,234,568 -->
{{ 0.856 | number:'1.1-2' }}          <!-- 0.86 (1 integer, 2 decimals max) -->
```

### `PercentPipe`

```html
{{ 0.75 | percent }}          <!-- 75% -->
{{ 0.75 | percent:'1.1-2' }}  <!-- 75.0% -->
```

### `UpperCasePipe` / `LowerCasePipe` / `TitleCasePipe`

```html
{{ employee.name | uppercase }}   <!-- ALICE JOHNSON -->
{{ employee.name | lowercase }}   <!-- alice johnson -->
{{ 'hello world' | titlecase }}   <!-- Hello World -->
```

### `SlicePipe`

```html
{{ employees() | slice:0:5 }}      <!-- first 5 employees -->
{{ 'Hello World' | slice:0:5 }}    <!-- Hello -->
```

### `JsonPipe` (development only)

```html
<!-- Useful for debugging — pretty prints any value -->
<pre>{{ employee() | json }}</pre>
```

### `AsyncPipe`

```html
<!-- Subscribes to Observable/Promise, unsubscribes on destroy -->
{{ employees$ | async | json }}

@if (employees$ | async; as employees) {
  <p>{{ employees.length }} loaded</p>
}
```

### `KeyValuePipe`

```html
<!-- Iterate over object key-value pairs -->
@for (entry of employee() | keyvalue; track entry.key) {
  <p>{{ entry.key }}: {{ entry.value }}</p>
}
```

---

## 9.3 Custom Pipes

### Example 1: Salary Range Pipe

```bash
ng generate pipe shared/pipes/salary-range
```

```ts
// src/app/shared/pipes/salary-range.pipe.ts
import { Pipe, PipeTransform } from '@angular/core'

@Pipe({
  name:       'salaryRange',
  standalone: true,
  pure:       true,   // default — only recalculates when input changes
})
export class SalaryRangePipe implements PipeTransform {
  transform(salary: number): string {
    if (salary < 500_000)  return 'Junior (< ₹5L)'
    if (salary < 1_000_000) return 'Mid (₹5L–₹10L)'
    if (salary < 2_000_000) return 'Senior (₹10L–₹20L)'
    return 'Principal (₹20L+)'
  }
}
```

```html
<!-- Usage -->
<span>{{ employee().salary | salaryRange }}</span>
<!-- "Senior (₹10L–₹20L)" -->
```

### Example 2: Department Color Pipe

```ts
// src/app/shared/pipes/dept-color.pipe.ts
import { Pipe, PipeTransform } from '@angular/core'

const DEPT_COLORS: Record<string, string> = {
  Engineering: '#0062ff',
  Marketing:   '#da1e28',
  HR:          '#24a148',
  Finance:     '#8a3ffc',
  Sales:       '#ff832b',
}

@Pipe({ name: 'deptColor', standalone: true })
export class DeptColorPipe implements PipeTransform {
  transform(department: string): string {
    return DEPT_COLORS[department] ?? '#525252'
  }
}
```

```html
<span [style.background]="employee().department | deptColor">
  {{ employee().department }}
</span>
```

### Example 3: Filter Pipe (search)

```ts
// src/app/shared/pipes/filter.pipe.ts
import { Pipe, PipeTransform } from '@angular/core'
import type { Employee } from '../models/employee.model'

@Pipe({
  name:       'filterEmployees',
  standalone: true,
  pure:       false,   // impure — re-runs even when reference unchanged
  // Use impure when filtering a mutable array that Angular won't detect changes in
  // Note: impure pipes run on every change detection cycle — use sparingly
})
export class FilterEmployeesPipe implements PipeTransform {
  transform(employees: Employee[], term: string): Employee[] {
    if (!term.trim()) return employees
    const lower = term.toLowerCase()
    return employees.filter(e =>
      e.name.toLowerCase().includes(lower) ||
      e.department.toLowerCase().includes(lower)
    )
  }
}
```

```html
<!-- Template-based filtering (alternative to filtering in component) -->
@for (emp of employees() | filterEmployees:searchTerm(); track emp.id) {
  <app-employee-card [employee]="emp" />
}
```

> **Performance note:** Prefer filtering in the component with `computed()` signals over impure pipes. Impure pipes run on every change detection cycle.

### Example 4: Relative Time Pipe (time-ago)

```ts
// src/app/shared/pipes/time-ago.pipe.ts
import { Pipe, PipeTransform } from '@angular/core'

@Pipe({ name: 'timeAgo', standalone: true })
export class TimeAgoPipe implements PipeTransform {
  transform(dateString: string): string {
    const date = new Date(dateString)
    const now  = new Date()
    const diffMs = now.getTime() - date.getTime()
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

    if (diffDays === 0)   return 'Today'
    if (diffDays === 1)   return 'Yesterday'
    if (diffDays < 7)     return `${diffDays} days ago`
    if (diffDays < 30)    return `${Math.floor(diffDays / 7)} weeks ago`
    if (diffDays < 365)   return `${Math.floor(diffDays / 30)} months ago`
    return `${Math.floor(diffDays / 365)} years ago`
  }
}
```

```html
<p>Joined: {{ employee().joinDate | timeAgo }}</p>
<!-- "3 years ago" -->
```

### Example 5: Pipe with Multiple Parameters

```ts
// Truncate text to N characters with optional suffix
@Pipe({ name: 'truncate', standalone: true })
export class TruncatePipe implements PipeTransform {
  transform(value: string, limit = 50, ellipsis = '...'): string {
    if (!value) return ''
    return value.length > limit
      ? value.substring(0, limit) + ellipsis
      : value
  }
}
```

```html
{{ employee().bio | truncate:80:'…' }}
{{ department.description | truncate }}  <!-- uses defaults -->
```

---

## 9.4 Pure vs Impure Pipes

```ts
// Pure pipe (default: pure: true)
// - Only runs when the input value reference changes
// - Efficient — Angular can cache the result
// - Best for most transformations
@Pipe({ name: 'myPipe', pure: true })

// Impure pipe (pure: false)
// - Runs on EVERY change detection cycle
// - Use when you need to react to mutations inside an object/array
// - Performance cost — use only when necessary
@Pipe({ name: 'myPipe', pure: false })
```

**Rule:** Always start with a pure pipe. Switch to impure only if you see it missing updates.

---

## 9.5 Pipe Chaining

```html
{{ employee().joinDate | date:'dd MMM yyyy' | uppercase }}
<!-- "15 NOV 2021" -->

{{ employee().salary | currency:'INR':'symbol':'1.0-0' }}
<!-- "₹1,200,000" -->

{{ employee().name | titlecase | slice:0:10 }}
<!-- "Alice John" -->
```

---

## 9.6 Using Pipes in TypeScript (not just templates)

```ts
import { CurrencyPipe } from '@angular/common'
import { inject }       from '@angular/core'

@Component({ /* ... */ })
export class ReportComponent {
  private currencyPipe = inject(CurrencyPipe)

  // In TypeScript code
  formatSalary(salary: number): string {
    return this.currencyPipe.transform(salary, 'INR', 'symbol', '1.0-0') ?? ''
  }
}

// Must add CurrencyPipe to providers[] or imports[]
@Component({
  providers: [CurrencyPipe],
  // ...
})
```

---

## 9.7 EMS — Pipe Usage Summary

```html
<!-- employee-card.component.html with all pipes -->
<div class="card">
  <h3>{{ employee().name | titlecase }}</h3>

  <span [style.background]="employee().department | deptColor" class="dept-badge">
    {{ employee().department }}
  </span>

  <p>{{ employee().email | lowercase }}</p>

  <p>
    <strong>Joined:</strong>
    {{ employee().joinDate | date:'dd MMM yyyy' }}
    <small>({{ employee().joinDate | timeAgo }})</small>
  </p>

  <p class="salary">
    {{ employee().salary | currency:'INR':'symbol':'1.0-0' }}
    <small>{{ employee().salary | salaryRange }}</small>
  </p>
</div>
```

---

## Summary

| Pipe | Use for |
|------|---------|
| `date` | Format dates |
| `currency` | Format monetary values |
| `number` | Format numbers with decimals |
| `percent` | Format ratios as percentages |
| `uppercase`, `lowercase`, `titlecase` | Text case |
| `slice` | Subset arrays or strings |
| `json` | Debug — pretty print objects |
| `async` | Subscribe to Observable/Promise in template |
| `keyvalue` | Iterate object keys |
| Custom pipe | Any domain-specific transformation |

**Next → Module 10: Making HTTP Requests**
