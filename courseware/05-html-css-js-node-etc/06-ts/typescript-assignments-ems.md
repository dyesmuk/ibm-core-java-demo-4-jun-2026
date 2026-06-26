# TypeScript Assignments — Employee Management System

> **Prerequisite:** HTML, CSS, JavaScript, Node.js, Express  
> **Next up:** React, Angular  
> **Theme:** All 5 assignments build a single Employee Management System (EMS) progressively.  
> Each assignment reuses and extends the code written in the previous one.

---

## How to run

```bash
# Set up the project
npm init -y
npm install typescript ts-node @types/node --save-dev
npx tsc --init
```

Enable decorators in `tsconfig.json`:
```json
{
  "compilerOptions": {
    "experimentalDecorators": true,
    "emitDecoratorMetadata": true
  }
}
```

Run any assignment:
```bash
npx ts-node index.ts
```

---

## Assignment 1 — Types, interfaces & data modelling
**Level:** Warm-up | **Time:** ~45 min  
**Builds on:** nothing — this is the foundation

Define the core data model for the EMS. Every subsequent assignment will import and extend these types.

### Tasks

1. Define the following union/literal types:
   ```ts
   type Role = "engineer" | "manager" | "hr" | "intern"
   type Status = "active" | "inactive" | "on-leave"
   ```

2. Define `interface Department`:
   - `id: number`
   - `name: string`
   - `location: string`

3. Define `interface Employee`:
   - `id: number`
   - `name: string`
   - `role: Role`
   - `salary: number`
   - `status: Status`
   - `departmentId: number`
   - `email?: string` *(optional)*

4. Define `interface Project`:
   - `id: number`
   - `title: string`
   - `budget: number`
   - `employeeIds: number[]`
   - `deadline: Date`

5. Write the following utility functions:
   - `getFullLabel(e: Employee): string` — returns e.g. `"Alice — manager"`
   - `isActive(e: Employee): boolean` — returns true if status is `"active"`
   - `daysUntilDeadline(p: Project): number` — returns number of days from today to deadline

6. Create mock data arrays:
   - 3 departments (e.g. Engineering, HR, Operations)
   - 6 employees spread across different roles and departments
   - 2 projects, each with some employees assigned

**TS concepts covered:** interfaces, optional fields, union/literal types, typed utility functions

---

## Assignment 2 — Generic repository class
**Level:** Warm-up | **Time:** ~1 hr  
**Builds on:** `Employee`, `Department`, `Project` interfaces from Assignment 1

Build a reusable generic repository that can store and query any EMS entity. This mirrors the repository pattern used in Angular services.

### Tasks

1. Define a base interface:
   ```ts
   interface Identifiable {
     id: number
   }
   ```

2. Create a generic class `Repository<T extends Identifiable>` with a private `Map<number, T>` as internal storage.

3. Add the following methods:
   - `add(item: T): void`
   - `findById(id: number): T | undefined`
   - `getAll(): T[]`
   - `update(id: number, changes: Partial<T>): boolean`
   - `remove(id: number): boolean`

4. Add a `query(predicate: (item: T) => boolean): T[]` method for filtered searches.

5. Instantiate the repository for all three entity types:
   ```ts
   const empRepo = new Repository<Employee>()
   const deptRepo = new Repository<Department>()
   const projectRepo = new Repository<Project>()
   ```
   Seed them with the mock data from Assignment 1.

6. Demonstrate the following operations:
   - Find all active engineers
   - Update an employee's salary using `update()`
   - Remove a project by id
   - Use `query()` to find all employees in a specific department

**TS concepts covered:** generics, generic constraints (`extends`), `Partial<T>`, access modifiers (`private`)

---

## Assignment 3 — Service layer with abstract classes & decorators
**Level:** Intermediate | **Time:** ~1.5 hr  
**Builds on:** `Repository<T>` from Assignment 2

Add a proper service layer on top of the repository using abstract classes and decorators — exactly the pattern Angular uses for its services and component annotations.

### Tasks

1. Write a method decorator `@LogCall` that logs the method name, its arguments, and its return value every time the method is called:
   ```ts
   function LogCall(target: any, key: string, descriptor: PropertyDescriptor) { ... }
   ```

2. Define an abstract class `BaseService<T extends Identifiable>` that:
   - Accepts a `Repository<T>` in its constructor
   - Exposes `add`, `findById`, `getAll`, `update`, `remove` by delegating to the repository

3. Create `EmployeeService extends BaseService<Employee>` with additional methods:
   - `getByDepartment(deptId: number): Employee[]`
   - `promote(id: number, newRole: Role): boolean` — updates the employee's role
   - `getSalaryReport(): { total: number; average: number; highest: Employee }`

4. Create `ProjectService extends BaseService<Project>` with:
   - `assignEmployee(projectId: number, empId: number): boolean` — adds empId to `employeeIds`
   - `getOverdue(): Project[]` — returns projects whose deadline has passed

5. Apply `@LogCall` to `promote`, `assignEmployee`, and `getSalaryReport`.

6. Run all methods and verify the decorator output appears in the console.

**TS concepts covered:** abstract classes, method decorators, return type shapes, `Partial<T>`, utility types

---

## Assignment 4 — Async operations & event callbacks
**Level:** Intermediate | **Time:** ~1.5 hr  
**Builds on:** `EmployeeService`, `ProjectService` from Assignment 3

Make the EMS async-ready — simulate database calls with delays and wire up a simple event system. This is the same shape as Angular's `HttpClient` + `EventEmitter` pattern.

### Tasks

1. Write a reusable helper:
   ```ts
   function delay(ms: number): Promise<void> {
     return new Promise(resolve => setTimeout(resolve, ms))
   }
   ```

2. Wrap each service method to be `async` and `await delay(300)` at the start to simulate a DB call.

3. Create an `EventBus` class:
   - `on(event: string, handler: (payload: unknown) => void): void`
   - `emit(event: string, payload: unknown): void`

4. Fire the following events from `EmployeeService`:
   - `"employee:added"` when an employee is added
   - `"employee:promoted"` when promote is called (include employee id and new role in payload)
   - `"employee:removed"` when an employee is removed

5. Fire these events from `ProjectService`:
   - `"project:assigned"` when an employee is assigned to a project
   - `"project:overdue-check"` when `getOverdue()` is called (include count in payload)

6. In `main()`:
   - Subscribe to all events and log them with a prefix e.g. `[EVENT] employee:promoted → { id: 3, role: "manager" }`
   - Perform the following sequence using `async/await`:
     1. Add a new employee
     2. Assign them to a project
     3. Promote them to manager
     4. Run `getSalaryReport()`
   - Wrap the entire sequence in `try/catch` and handle errors typed as `unknown`

**TS concepts covered:** `async/await`, `Promise<T>`, typed event/callback pattern, `unknown` for safe error handling

---

## Assignment 5 — Persistence, reporting & CLI runner
**Level:** Mini-project | **Time:** ~2 hr  
**Builds on:** everything from Assignments 1–4

Complete the EMS with file-based persistence and a summary reporting layer. At the end of this assignment you will have a fully working Node.js CLI application — the same structure you will later lift into a React or Angular frontend.

### Tasks

1. Add `saveAll(path: string): Promise<void>` to `BaseService`:
   - Serialises the entire repository to a JSON file using `fs.promises.writeFile`

2. Add a static method `loadAll<T>(path: string): Promise<T[]>` that:
   - Reads and parses a JSON file
   - Returns typed records to seed a new `Repository<T>` on startup
   - Returns an empty array (not an error) if the file does not exist

3. Create a `ReportService` class with:
   - `departmentSummary(): { dept: string; headcount: number; avgSalary: number }[]`  
     — groups employees by department and computes headcount and average salary
   - `projectStatus(): { title: string; teamSize: number; daysLeft: number; overdue: boolean }[]`  
     — returns status for each project

4. In `main()`:
   1. Attempt to load data from saved JSON files; seed with mock data if files are missing
   2. Perform a sequence of async operations (add, promote, assign, remove)
   3. Print both reports to the console in a readable format using `console.table()`
   4. Save updated state back to JSON files

5. **Bonus:** Define a snapshot type and export the full system state:
   ```ts
   type EMSSnapshot = {
     employees: Employee[]
     departments: Department[]
     projects: Project[]
     savedAt: string
   }
   ```
   Save this as `ems-snapshot.json` at the end of every run.

**TS concepts covered:** Node.js `fs` types, JSON serialisation/deserialisation, mapped/utility types, bringing the full application together

---

## Progression summary

| Assignment | What you build | What you reuse |
|---|---|---|
| 1 | Interfaces, types, mock data | — |
| 2 | Generic `Repository<T>` | Types from 1 |
| 3 | Service layer + decorators | Repository from 2 |
| 4 | Async wrappers + EventBus | Services from 3 |
| 5 | Persistence + reports + CLI | Everything |

By Assignment 5, trainees have a complete, layered Node.js application. When they move to Angular, the `BaseService → EmployeeService` pattern, decorators, and async service calls will feel immediately familiar.
