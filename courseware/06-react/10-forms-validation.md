# Module 10 — Forms and Form Validation

## Learning Objectives
- Understand controlled vs uncontrolled components
- Build forms manually with controlled inputs
- Use React Hook Form for complex forms
- Validate with Zod schemas
- Handle all input types in React
- EMS: Create Employee and Edit Employee forms

---

## 10.1 Controlled vs Uncontrolled

| | Controlled | Uncontrolled |
|--|-----------|--------------|
| Value stored in | React state | DOM (via `ref`) |
| Read value via | state variable | `ref.current.value` |
| Validation | On every keystroke | On submit |
| Use when | Validation, dynamic UI, most forms | Simple forms, file input |

---

## 10.2 Controlled Inputs

React owns the value — every keystroke updates state, state drives the input value.

```tsx
import { useState } from 'react'

function SimpleForm() {
  const [name, setName]   = useState('')
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()   // prevent page reload

    if (!name.trim()) {
      setError('Name is required')
      return
    }
    if (!email.includes('@')) {
      setError('Valid email is required')
      return
    }

    setError('')
    console.log({ name, email })
  }

  return (
    <form onSubmit={handleSubmit}>
      <label htmlFor="name">Name</label>
      <input
        id="name"
        value={name}
        onChange={e => setName(e.target.value)}
        placeholder="Alice Johnson"
      />

      <label htmlFor="email">Email</label>
      <input
        id="email"
        type="email"
        value={email}
        onChange={e => setEmail(e.target.value)}
        placeholder="alice@ibm.com"
      />

      {error && <p role="alert">{error}</p>}

      <button type="submit">Submit</button>
    </form>
  )
}
```

---

## 10.3 All Input Types in React

```tsx
// Text
<input type="text"     value={name}  onChange={e => setName(e.target.value)} />

// Number
<input type="number"   value={age}   onChange={e => setAge(Number(e.target.value))} />

// Email / Password / URL
<input type="email"    value={email} onChange={e => setEmail(e.target.value)} />
<input type="password" value={pwd}   onChange={e => setPwd(e.target.value)} />

// Textarea
<textarea value={bio} onChange={e => setBio(e.target.value)} rows={4} />

// Select (single)
<select value={dept} onChange={e => setDept(e.target.value)}>
  <option value="">-- Choose Department --</option>
  <option value="Engineering">Engineering</option>
  <option value="Marketing">Marketing</option>
</select>

// Checkbox
<input
  type="checkbox"
  checked={isActive}
  onChange={e => setIsActive(e.target.checked)}   // use e.target.checked, not e.target.value
/>

// Radio
{['admin', 'user', 'viewer'].map(role => (
  <label key={role}>
    <input
      type="radio"
      value={role}
      checked={selectedRole === role}
      onChange={e => setSelectedRole(e.target.value)}
    />
    {role}
  </label>
))}

// Date
<input
  type="date"
  value={joinDate}     // format: YYYY-MM-DD
  onChange={e => setJoinDate(e.target.value)}
/>

// File (uncontrolled — file input value can't be set programmatically)
const fileRef = useRef<HTMLInputElement>(null)
<input type="file" ref={fileRef} accept=".jpg,.png" />
// Read value: fileRef.current?.files?.[0]
```

---

## 10.4 React Hook Form — For Complex Forms

For forms with many fields, cross-field validation, or performance needs, React Hook Form (RHF) is the industry standard.

```bash
npm install react-hook-form
```

```tsx
import { useForm } from 'react-hook-form'

interface EmployeeFormValues {
  name:       string
  email:      string
  department: string
  salary:     number
  joinDate:   string
  isActive:   boolean
}

function CreateEmployeePage() {
  const {
    register,        // connects inputs to the form
    handleSubmit,    // wraps your onSubmit — calls it only if validation passes
    formState: { errors, isSubmitting },
    reset,           // reset to defaults after submit
    watch,           // watch a field's current value
    setValue,        // programmatically set a field
  } = useForm<EmployeeFormValues>({
    defaultValues: {
      name:       '',
      email:      '',
      department: 'Engineering',
      salary:     70000,
      joinDate:   new Date().toISOString().split('T')[0],
      isActive:   true,
    }
  })

  const onSubmit = async (data: EmployeeFormValues) => {
    await employeeService.create(data)
    reset()
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <label htmlFor="name">Full Name *</label>
      <input
        id="name"
        {...register('name', {
          required: 'Name is required',
          minLength: { value: 2, message: 'Name must be at least 2 characters' },
          maxLength: { value: 100, message: 'Name too long' },
        })}
        aria-invalid={!!errors.name}
      />
      {errors.name && <p role="alert">{errors.name.message}</p>}

      <label htmlFor="email">Email *</label>
      <input
        id="email"
        type="email"
        {...register('email', {
          required: 'Email is required',
          pattern: {
            value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
            message: 'Enter a valid email address',
          },
        })}
        aria-invalid={!!errors.email}
      />
      {errors.email && <p role="alert">{errors.email.message}</p>}

      <label htmlFor="department">Department *</label>
      <select id="department" {...register('department', { required: 'Select a department' })}>
        <option value="">-- Select --</option>
        <option value="Engineering">Engineering</option>
        <option value="Marketing">Marketing</option>
        <option value="HR">HR</option>
        <option value="Finance">Finance</option>
        <option value="Sales">Sales</option>
      </select>
      {errors.department && <p role="alert">{errors.department.message}</p>}

      <label htmlFor="salary">Salary *</label>
      <input
        id="salary"
        type="number"
        {...register('salary', {
          required: 'Salary is required',
          min: { value: 10000, message: 'Minimum salary is ₹10,000' },
          max: { value: 5000000, message: 'Salary seems too high' },
          valueAsNumber: true,   // parse as number, not string
        })}
      />
      {errors.salary && <p role="alert">{errors.salary.message}</p>}

      <label htmlFor="joinDate">Join Date</label>
      <input id="joinDate" type="date" {...register('joinDate')} />

      <label>
        <input type="checkbox" {...register('isActive')} />
        Active Employee
      </label>

      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Saving…' : 'Create Employee'}
      </button>
    </form>
  )
}
```

---

## 10.5 Zod Schema Validation

Zod lets you define a type-safe validation schema that works at both TypeScript compile time and runtime.

```bash
npm install zod @hookform/resolvers
```

```ts
// src/schemas/employeeSchema.ts
import { z } from 'zod'

export const employeeSchema = z.object({
  name: z
    .string()
    .min(2, 'Name must be at least 2 characters')
    .max(100, 'Name too long'),

  email: z
    .string()
    .email('Enter a valid email address'),

  department: z.enum(
    ['Engineering', 'Marketing', 'HR', 'Finance', 'Sales'],
    { errorMap: () => ({ message: 'Select a valid department' }) }
  ),

  salary: z
    .number({ invalid_type_error: 'Salary must be a number' })
    .min(10000, 'Minimum salary is ₹10,000')
    .max(5_000_000, 'Salary seems too high'),

  joinDate: z.string().min(1, 'Join date is required'),

  isActive: z.boolean(),
})

// TypeScript type inferred from schema — single source of truth
export type EmployeeFormValues = z.infer<typeof employeeSchema>
```

```tsx
// Using Zod with React Hook Form
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { employeeSchema, type EmployeeFormValues } from '../schemas/employeeSchema'

function CreateEmployeePage() {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<EmployeeFormValues>({
    resolver: zodResolver(employeeSchema),   // replaces inline validation rules
    defaultValues: {
      name: '', email: '', department: 'Engineering',
      salary: 70000, joinDate: '', isActive: true,
    }
  })

  // Fields registered the same way — errors now come from Zod
  return (
    <form onSubmit={handleSubmit(async data => {
      await employeeService.create(data)
    })}>
      <input {...register('name')} placeholder="Full Name" />
      {errors.name && <p>{errors.name.message}</p>}

      <input {...register('email')} type="email" placeholder="Email" />
      {errors.email && <p>{errors.email.message}</p>}

      {/* ... rest of fields */}

      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Creating…' : 'Create Employee'}
      </button>
    </form>
  )
}
```

---

## 10.6 Edit Form — Populating from Existing Data

```tsx
// src/pages/EditEmployeePage.tsx
import { useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { employeeSchema, type EmployeeFormValues } from '../schemas/employeeSchema'
import { employeeService } from '../services/employeeService'

function EditEmployeePage() {
  const { id }   = useParams<{ id: string }>()
  const navigate = useNavigate()

  const { register, handleSubmit, reset, formState: { errors, isSubmitting, isDirty } } =
    useForm<EmployeeFormValues>({
      resolver: zodResolver(employeeSchema),
    })

  // Load existing employee data and populate the form
  useEffect(() => {
    if (!id) return
    employeeService.getById(Number(id)).then(emp => {
      reset({           // reset populates the form with existing values
        name:       emp.name,
        email:      emp.email,
        department: emp.department as EmployeeFormValues['department'],
        salary:     emp.salary,
        joinDate:   emp.joinDate,
        isActive:   emp.isActive,
      })
    })
  }, [id, reset])

  const onSubmit = async (data: EmployeeFormValues) => {
    await employeeService.update(Number(id), data)
    navigate(`/employees/${id}`)
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('name')} placeholder="Full Name" />
      {errors.name && <p>{errors.name.message}</p>}

      {/* ... rest of fields */}

      <button type="submit" disabled={isSubmitting || !isDirty}>
        {/* isDirty = true only if user changed something */}
        {isSubmitting ? 'Saving…' : 'Save Changes'}
      </button>
      <button type="button" onClick={() => navigate(-1)}>Cancel</button>
    </form>
  )
}

export default EditEmployeePage
```

---

## 10.7 Cross-Field Validation with Zod

```ts
// Validate that endDate is after startDate
export const projectSchema = z.object({
  name:      z.string().min(1, 'Project name required'),
  startDate: z.string().min(1, 'Start date required'),
  endDate:   z.string().min(1, 'End date required'),
}).refine(
  data => new Date(data.endDate) > new Date(data.startDate),
  {
    message: 'End date must be after start date',
    path: ['endDate'],   // show error on endDate field
  }
)
```

---

## Summary

| Approach | When |
|----------|------|
| Controlled inputs + `useState` | Simple forms, 1-3 fields |
| React Hook Form (no Zod) | Medium forms, built-in validation rules |
| React Hook Form + Zod | Production forms — shared schema, type safety |
| Uncontrolled (`useRef`) | File inputs, rare simple cases |

| RHF concept | What it does |
|-------------|--------------|
| `register` | Connects input to the form |
| `handleSubmit` | Validates before calling your handler |
| `formState.errors` | All current validation errors |
| `isSubmitting` | True while async submit is running |
| `isDirty` | True if any field differs from defaultValues |
| `reset(values)` | Reset form, optionally to new values |
| `setValue(field, value)` | Programmatically set a field |

**Next → Module 11: Redux Toolkit**
