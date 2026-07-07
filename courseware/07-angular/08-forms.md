# Module 08 — Handling Forms in Angular

## Learning Objectives
- Understand reactive vs template-driven forms
- Build full reactive forms with `FormBuilder` and `FormGroup`
- Validate inputs with built-in and custom validators
- Handle async validators
- Build a template-driven form for comparison
- EMS: Create Employee form, Edit Employee form, Department form

---

## 8.1 Two Approaches — Quick Comparison

| | Reactive Forms | Template-Driven Forms |
|--|---------------|----------------------|
| Form model | Defined in TypeScript class | Defined in HTML template |
| Complexity | Higher setup, more control | Less setup, simpler |
| Validation | In TypeScript — testable | In template with directives |
| Dynamic forms | ✅ Easy (add/remove controls) | ❌ Complex |
| Async validators | ✅ First-class | ❌ Awkward |
| Testing | ✅ Easy (no DOM needed) | ❌ Needs DOM |
| **Use for** | **Most forms — production default** | **Simple, 2-3 field forms** |

---

## 8.2 Reactive Forms — Setup

```ts
// app.config.ts or component imports
import { provideFormsModule } from '@angular/forms'
// Or import ReactiveFormsModule directly in each component
import { ReactiveFormsModule } from '@angular/forms'
```

---

## 8.3 Building a Reactive Form

```bash
ng g c employees/employee-form
```

```ts
// src/app/employees/employee-form/employee-form.component.ts
import { Component, inject, input, output, OnInit, computed } from '@angular/core'
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
} from '@angular/forms'
import type { Employee } from '../../shared/models/employee.model'

@Component({
  selector: 'app-employee-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './employee-form.component.html',
})
export class EmployeeFormComponent implements OnInit {
  // Inputs
  employeeToEdit = input<Employee | null>(null)
  mode           = input<'create' | 'edit'>('create')

  // Outputs
  formSubmit = output<Omit<Employee, 'id'>>()
  cancel     = output<void>()

  private fb = inject(FormBuilder)

  form!: FormGroup

  ngOnInit() {
    this.buildForm()

    // If editing, populate with existing data
    if (this.employeeToEdit()) {
      this.form.patchValue(this.employeeToEdit()!)
    }
  }

  private buildForm() {
    this.form = this.fb.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(100),
        ],
      ],
      email: [
        '',
        [Validators.required, Validators.email],
      ],
      phone: ['', [Validators.pattern(/^\+?[\d\s\-()]{10,}$/)]],
      department: ['Engineering', Validators.required],
      salary: [
        70000,
        [Validators.required, Validators.min(10000), Validators.max(5_000_000)],
      ],
      joinDate: ['', Validators.required],
      isActive: [true],
      role: ['user'],
    })
  }

  // Convenience getters — use in template for cleaner access
  get nameCtrl()       { return this.form.get('name')! }
  get emailCtrl()      { return this.form.get('email')! }
  get salaryCtrl()     { return this.form.get('salary')! }
  get departmentCtrl() { return this.form.get('department')! }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched()   // show all errors
      return
    }
    this.formSubmit.emit(this.form.value)
  }

  onCancel() {
    this.cancel.emit()
  }
}
```

```html
<!-- employee-form.component.html -->
<form [formGroup]="form" (ngSubmit)="onSubmit()" novalidate>

  <!-- Name -->
  <div class="form-group">
    <label for="name">Full Name *</label>
    <input id="name" type="text" formControlName="name" placeholder="Alice Johnson" />

    @if (nameCtrl.touched && nameCtrl.errors) {
      @if (nameCtrl.errors['required']) {
        <span class="error">Name is required</span>
      }
      @if (nameCtrl.errors['minlength']) {
        <span class="error">
          Name must be at least {{ nameCtrl.errors['minlength'].requiredLength }} characters
        </span>
      }
    }
  </div>

  <!-- Email -->
  <div class="form-group">
    <label for="email">Email *</label>
    <input id="email" type="email" formControlName="email" />

    @if (emailCtrl.touched && emailCtrl.errors) {
      @if (emailCtrl.errors['required']) {
        <span class="error">Email is required</span>
      }
      @if (emailCtrl.errors['email']) {
        <span class="error">Enter a valid email address</span>
      }
    }
  </div>

  <!-- Department -->
  <div class="form-group">
    <label for="department">Department *</label>
    <select id="department" formControlName="department">
      <option value="">-- Select Department --</option>
      <option value="Engineering">Engineering</option>
      <option value="Marketing">Marketing</option>
      <option value="HR">HR</option>
      <option value="Finance">Finance</option>
      <option value="Sales">Sales</option>
    </select>
  </div>

  <!-- Salary -->
  <div class="form-group">
    <label for="salary">Salary (₹) *</label>
    <input id="salary" type="number" formControlName="salary" />

    @if (salaryCtrl.touched && salaryCtrl.errors) {
      @if (salaryCtrl.errors['min']) {
        <span class="error">Minimum salary is ₹10,000</span>
      }
      @if (salaryCtrl.errors['max']) {
        <span class="error">Salary exceeds maximum</span>
      }
    }
  </div>

  <!-- Join Date -->
  <div class="form-group">
    <label for="joinDate">Join Date *</label>
    <input id="joinDate" type="date" formControlName="joinDate" />
  </div>

  <!-- Active checkbox -->
  <div class="form-group">
    <label>
      <input type="checkbox" formControlName="isActive" />
      Active Employee
    </label>
  </div>

  <!-- Buttons -->
  <div class="form-actions">
    <button type="submit" [disabled]="form.invalid || form.pristine">
      {{ mode() === 'edit' ? 'Save Changes' : 'Create Employee' }}
    </button>
    <button type="button" (click)="onCancel()">Cancel</button>
  </div>

</form>
```

```css
/* employee-form.component.css */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 16px;
}

.form-group label {
  font-weight: 600;
  font-size: 14px;
}

.form-group input,
.form-group select {
  padding: 8px 12px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 14px;
}

.form-group input.ng-invalid.ng-touched,
.form-group select.ng-invalid.ng-touched {
  border-color: var(--color-danger);
}

.error {
  color: var(--color-danger);
  font-size: 12px;
}

.form-actions {
  display: flex;
  gap: 8px;
  margin-top: 24px;
}
```

---

## 8.4 Custom Validators

```ts
// src/app/shared/validators/employee.validators.ts
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms'

// Synchronous — no IBM email domains allowed in demo
export function noFreeEmailValidator(): ValidatorFn {
  const freeProviders = ['gmail.com', 'yahoo.com', 'hotmail.com']
  return (control: AbstractControl): ValidationErrors | null => {
    const value: string = control.value ?? ''
    const domain = value.split('@')[1]?.toLowerCase()
    if (domain && freeProviders.includes(domain)) {
      return { freeEmail: { domain } }
    }
    return null
  }
}

// Cross-field validator — end date must be after start date
export function dateRangeValidator(startKey: string, endKey: string): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const start = group.get(startKey)?.value
    const end   = group.get(endKey)?.value
    if (start && end && new Date(end) <= new Date(start)) {
      return { dateRange: { message: 'End date must be after start date' } }
    }
    return null
  }
}

// Async validator — check email uniqueness
import { AsyncValidatorFn } from '@angular/forms'
import { inject } from '@angular/core'
import { EmployeeService } from '../../employees/employee.service'
import { map, catchError, of, timer, switchMap } from 'rxjs'

export function uniqueEmailValidator(currentId?: number): AsyncValidatorFn {
  return (control: AbstractControl) => {
    // Debounce — wait 400ms before hitting the service
    return timer(400).pipe(
      switchMap(() => {
        const employeeService = inject(EmployeeService)
        const exists = employeeService.employees()
          .some(e => e.email === control.value && e.id !== currentId)
        return of(exists ? { emailTaken: true } : null)
      }),
      catchError(() => of(null))
    )
  }
}
```

```ts
// Using custom validators in the form
this.form = this.fb.group({
  email: [
    '',
    [Validators.required, Validators.email, noFreeEmailValidator()],
    [uniqueEmailValidator(this.employeeToEdit()?.id)],   // async validators go in 3rd array
  ],
})
```

---

## 8.5 FormArray — Dynamic Form Fields

```ts
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms'

@Component({ /* ... */ })
export class ProjectFormComponent {
  private fb = inject(FormBuilder)

  form = this.fb.group({
    projectName: ['', Validators.required],
    teamMembers: this.fb.array([
      this.fb.control('', Validators.required)   // one member initially
    ]),
  })

  get teamMembers() {
    return this.form.get('teamMembers') as FormArray
  }

  addMember() {
    this.teamMembers.push(this.fb.control('', Validators.required))
  }

  removeMember(index: number) {
    this.teamMembers.removeAt(index)
  }
}
```

```html
<form [formGroup]="form">
  <input formControlName="projectName" placeholder="Project Name" />

  <div formArrayName="teamMembers">
    @for (member of teamMembers.controls; track $index; let i = $index) {
      <div>
        <input [formControlName]="i" placeholder="Member name" />
        <button type="button" (click)="removeMember(i)">Remove</button>
      </div>
    }
  </div>

  <button type="button" (click)="addMember()">+ Add Team Member</button>
</form>
```

---

## 8.6 Template-Driven Forms — When to Use

Template-driven forms are simpler for quick, small forms. They use `ngModel` and HTML5 validation attributes.

```ts
// Must import FormsModule
import { FormsModule } from '@angular/forms'

@Component({
  standalone: true,
  imports: [FormsModule],
  // ...
})
export class QuickSearchComponent {
  searchTerm = ''
  onSearch() { console.log(this.searchTerm) }
}
```

```html
<!-- Template-driven form example -->
<form #searchForm="ngForm" (ngSubmit)="onSearch()">
  <input
    name="searchTerm"
    [(ngModel)]="searchTerm"
    #searchInput="ngModel"
    required
    minlength="2"
    placeholder="Search..."
  />

  @if (searchInput.touched && searchInput.invalid) {
    @if (searchInput.errors?.['required']) {
      <span class="error">Search term required</span>
    }
    @if (searchInput.errors?.['minlength']) {
      <span class="error">At least 2 characters</span>
    }
  }

  <button type="submit" [disabled]="searchForm.invalid">Search</button>
</form>
```

> Use template-driven for: quick search bars, filter inputs, newsletter signups. Use reactive for: create/edit forms, multi-step forms, anything with dynamic fields or async validation.

---

## 8.7 Angular Form State Classes

Angular automatically adds CSS classes to form controls based on their state:

| Class | Meaning |
|-------|---------|
| `ng-pristine` | User hasn't interacted |
| `ng-dirty` | User has changed value |
| `ng-untouched` | User hasn't focused |
| `ng-touched` | User focused then blurred |
| `ng-valid` | All validators pass |
| `ng-invalid` | At least one validator fails |
| `ng-pending` | Async validator running |

```css
/* Show error state only after user has interacted */
input.ng-invalid.ng-touched {
  border-color: var(--color-danger);
  outline-color: var(--color-danger);
}

input.ng-valid.ng-touched {
  border-color: var(--color-success);
}
```

---

## 8.8 EMS — Create and Edit Employee Page

```ts
// src/app/employees/employee-form-page/employee-form-page.component.ts
import { Component, inject, computed, input } from '@angular/core'
import { Router } from '@angular/router'
import { EmployeeService } from '../employee.service'
import { NotificationService } from '../../shared/services/notification.service'
import { EmployeeFormComponent } from '../employee-form/employee-form.component'
import type { Employee } from '../../shared/models/employee.model'

@Component({
  selector: 'app-employee-form-page',
  standalone: true,
  imports: [EmployeeFormComponent],
  template: `
    <div class="page">
      <h1>{{ mode() === 'edit' ? 'Edit Employee' : 'New Employee' }}</h1>
      <app-employee-form
        [employeeToEdit]="employee()"
        [mode]="mode()"
        (formSubmit)="onSave($event)"
        (cancel)="onCancel()"
      />
    </div>
  `,
})
export class EmployeeFormPageComponent {
  // Bound from route params/data via withComponentInputBinding
  id   = input<string>()
  mode = input<string>('create')

  private employeeService = inject(EmployeeService)
  private router          = inject(Router)
  private notifications   = inject(NotificationService)

  employee = computed(() =>
    this.id() ? (this.employeeService.getById(Number(this.id())) ?? null) : null
  )

  onSave(formValue: Omit<Employee, 'id'>) {
    if (this.mode() === 'edit' && this.id()) {
      this.employeeService.update(Number(this.id()), formValue)
      this.notifications.success('Employee updated successfully')
      this.router.navigate(['/employees', this.id()])
    } else {
      const created = this.employeeService.add(formValue)
      this.notifications.success(`${created.name} added successfully`)
      this.router.navigate(['/employees', created.id])
    }
  }

  onCancel() {
    this.router.navigate(['/employees'])
  }
}
```

---

## Summary

| Reactive Forms | Template-Driven Forms |
|---------------|----------------------|
| `FormBuilder`, `FormGroup`, `FormControl` | `ngModel`, `#ref="ngModel"` |
| `formControlName` | `name` + `[(ngModel)]` |
| Validators in TS | Validators in HTML |
| `form.invalid`, `form.get('x')!.errors` | `#ref.invalid`, `#ref.errors` |
| Best for complex forms | Best for simple forms |

**Next → Module 09: Pipes**
