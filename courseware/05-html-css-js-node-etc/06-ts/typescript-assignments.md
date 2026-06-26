# TypeScript Assignments

> Prerequisite: HTML, CSS, JavaScript, Node.js, Express  
> Next up: React, Angular

---

## Assignment 1 — Student record system
**Level:** Warm-up | **Time:** ~45 min

Define types and interfaces for a simple student record and write functions that operate on them.

### Tasks
1. Define an `interface Student` with fields: `id: number`, `name: string`, `marks: number[]`, `email?: string`
2. Write a function `getAverage(s: Student): number` that returns the average of marks
3. Write `getGrade(s: Student): "A" | "B" | "C" | "F"` using a union return type
4. Create an array of at least 3 students and print a report for each using the above functions

**TS concepts:** interfaces, optional fields, union types, typed function signatures

---

## Assignment 2 — Generic data store
**Level:** Warm-up | **Time:** ~45 min

Build a simple generic class that mimics a key-value store — similar to what you'd see in Angular services or React context.

### Tasks
1. Create a generic class `Store<T>` with a private `Map<string, T>` internally
2. Add methods: `set(key: string, value: T): void`, `get(key: string): T | undefined`, `getAll(): T[]`, `remove(key: string): boolean`
3. Instantiate it as both `Store<number>` and `Store<Student>` (from Assignment 1) to show reuse
4. Add a method `find(predicate: (val: T) => boolean): T[]` that filters the store

**TS concepts:** generics, generic classes, generic methods, access modifiers

---

## Assignment 3 — Shape area calculator with decorators
**Level:** Intermediate | **Time:** ~1 hr

Model geometric shapes using abstract classes and interfaces, then use a decorator to log method calls — patterns used extensively in Angular.

### Tasks
1. Define an `interface Shape` with `area(): number` and `perimeter(): number`
2. Create abstract class `BaseShape` implementing `Shape`, with a `describe(): string` method using the `abstract` keyword for a label
3. Extend it into concrete classes: `Circle`, `Rectangle`, `Triangle`
4. Write a method decorator `@LogCall` that logs the method name and return value each time it is called, and apply it to `area()`

**TS concepts:** abstract classes, interface implementation, method decorators — mirrors Angular's `@Component`, `@Injectable` style

---

## Assignment 4 — Async product catalogue
**Level:** Intermediate | **Time:** ~1.5 hr

Simulate an async product service using Promises and generics — directly mirrors how Angular HttpClient and React fetch hooks work.

### Tasks
1. Define `type Product` with: `id`, `name`, `price`, `category`, `inStock: boolean`
2. Write a `fetchProducts(): Promise<Product[]>` that returns a resolved promise with mock data after a 500ms delay using `setTimeout`
3. Write `filterByCategory(products: Product[], category: string): Product[]` and `sortByPrice(products: Product[], order: "asc" | "desc"): Product[]`
4. Chain the calls using `async/await`, apply filter and sort, and print the final list
5. **Bonus:** Wrap in a try/catch and simulate a rejection — verify the error is caught and typed as `unknown`

**TS concepts:** async/await, `Promise<T>`, union literal types, error handling with `unknown`

---

## Assignment 5 — Mini task manager CLI (mini-project)
**Level:** Mini-project | **Time:** ~2–3 hr

Bring all the above together in a Node.js CLI task manager. The `TaskService` architecture mirrors an Angular service and is good prep for building Angular/React component logic.

### Tasks
1. Define `type Priority = "low" | "medium" | "high"` and `interface Task` with: `id`, `title`, `priority`, `done: boolean`, `createdAt: Date`
2. Create a `TaskService` class (uses `Store<Task>` from Assignment 2 internally) with methods: `add`, `complete`, `remove`, `listByPriority`, `summary()`
3. Add an `EventEmitter`-style mechanism: fire an `onTaskAdded` and `onTaskCompleted` callback when those events occur
4. Write a `saveToFile(path: string): Promise<void>` that serialises tasks to JSON using `fs.promises.writeFile`
5. Drive it all from a `main()` async function: add 5 tasks, complete 2, list by priority, print summary, save to `tasks.json`

**TS concepts:** everything from Assignments 1–4 + Node.js `fs` types, callback patterns, JSON serialisation — architecture mirrors an Angular service

---

## How to run

```bash
# Install TypeScript and ts-node
npm init -y
npm install typescript ts-node @types/node --save-dev
npx tsc --init

# Run any assignment
npx ts-node index.ts
```
