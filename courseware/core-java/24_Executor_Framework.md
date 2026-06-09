# Module 24 — Executor Framework

> **Part F: Concurrency**
> Prerequisites: Module 23 · Time: ~2 hours

---

## Why Not Raw Threads?

Managing `Thread` objects manually has real problems in production:

- Creating a new thread for every task is expensive — threads consume memory (~512KB stack each)
- No limit on thread count — 10,000 simultaneous tasks = 10,000 threads = out of memory
- No easy way to get a result back from a thread
- No clean exception propagation from background threads
- No scheduling, no cancellation, no timeout support out of the box

The **Executor Framework** (`java.util.concurrent`) solves all of this. It separates the **task** (what to run) from the **execution policy** (how many threads, when to run, how to handle failures).

---

## The Core Abstraction

```
Executor           → submit a task, forget about threads
ExecutorService    → Executor + lifecycle management + result retrieval
ScheduledExecutorService → ExecutorService + scheduled/recurring execution
```

```java
// Without Executor — manual thread management
Thread t = new Thread(() -> processPayroll());
t.start();

// With Executor — thread management handled for you
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> processPayroll());
```

---

## Creating Thread Pools with `Executors`

`Executors` is a factory class for common pool configurations:

### Fixed Thread Pool

Fixed number of threads. Tasks queue up when all threads are busy. Best for known, bounded workloads:

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
// 4 threads, shared work queue, tasks wait if all 4 are busy
```

### Cached Thread Pool

Creates threads as needed, reuses idle threads. Good for many short-lived tasks:

```java
ExecutorService pool = Executors.newCachedThreadPool();
// Threads created on demand, idle threads kept for 60s, then removed
// Can create many threads — use carefully
```

### Single Thread Executor

One thread, tasks run sequentially in submission order. Useful when tasks must not run concurrently:

```java
ExecutorService pool = Executors.newSingleThreadExecutor();
// Guarantees sequential execution and maintains order
```

### Scheduled Thread Pool

For tasks that run after a delay or periodically:

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
```

---

## `execute()` vs `submit()`

| Method | Returns | Exception Handling | Use For |
|--------|---------|-------------------|---------|
| `execute(Runnable)` | void | Thrown to thread's UncaughtExceptionHandler | Fire-and-forget tasks |
| `submit(Runnable)` | `Future<?>` | Stored in Future — retrieved on `get()` | Tasks you want to track or cancel |
| `submit(Callable<T>)` | `Future<T>` | Stored in Future | Tasks that return a result |

---

## `Callable` and `Future`

`Runnable` cannot return a result or throw a checked exception. `Callable<T>` can do both:

```java
import java.util.concurrent.*;

public class SalaryCalculationTask implements Callable<Double> {

    private final List<Employee> employees;
    private final String department;

    public SalaryCalculationTask(List<Employee> employees, String department) {
        this.employees  = employees;
        this.department = department;
    }

    @Override
    public Double call() throws Exception {
        System.out.printf("[%s] Calculating payroll for %s%n",
                          Thread.currentThread().getName(), department);
        Thread.sleep(300);   // simulate work
        return employees.stream()
                        .filter(e -> e.getDepartment().equals(department))
                        .mapToDouble(Employee::getSalary)
                        .sum();
    }
}
```

```java
ExecutorService pool = Executors.newFixedThreadPool(3);

List<Employee> all = Arrays.asList(
    new Employee(101, "Sonu",  75000, "Engineering"),
    new Employee(102, "Monu",  82000, "Engineering"),
    new Employee(103, "Tonu",  55000, "HR"),
    new Employee(104, "Ponu",  91000, "Finance"),
    new Employee(105, "Gonu",  68000, "Operations")
);

// Submit tasks — they start immediately
Future<Double> engFuture  = pool.submit(new SalaryCalculationTask(all, "Engineering"));
Future<Double> hrFuture   = pool.submit(new SalaryCalculationTask(all, "HR"));
Future<Double> finFuture  = pool.submit(new SalaryCalculationTask(all, "Finance"));

// Retrieve results — blocks until each result is ready
try {
    double engTotal = engFuture.get();    // blocks if not done yet
    double hrTotal  = hrFuture.get();
    double finTotal = finFuture.get();

    System.out.printf("Engineering: %.2f%n", engTotal);
    System.out.printf("HR:          %.2f%n", hrTotal);
    System.out.printf("Finance:     %.2f%n", finTotal);
    System.out.printf("Grand total: %.2f%n", engTotal + hrTotal + finTotal);

} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} catch (ExecutionException e) {
    System.out.println("Task failed: " + e.getCause().getMessage());
} finally {
    pool.shutdown();
}
```

Output:
```
[pool-1-thread-1] Calculating payroll for Engineering
[pool-1-thread-2] Calculating payroll for HR
[pool-1-thread-3] Calculating payroll for Finance
Engineering: 157000.00
HR:          55000.00
Finance:     91000.00
Grand total: 303000.00
```

### `Future` Methods

```java
Future<Double> future = pool.submit(task);

future.get();                 // block until result is ready
future.get(5, TimeUnit.SECONDS);  // block with timeout — throws TimeoutException if exceeded
future.isDone();              // true if completed (normally, exceptionally, or cancelled)
future.isCancelled();         // true if cancelled
future.cancel(true);          // attempt to cancel — true = interrupt if running
```

---

## Shutting Down an Executor

Always shut down an `ExecutorService` when done — otherwise the threads keep running and the JVM won't exit:

```java
pool.shutdown();              // stop accepting new tasks, wait for running tasks to finish
pool.shutdownNow();           // stop accepting new tasks, interrupt running tasks (best-effort)

// Wait for termination after shutdown
boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);
if (!finished) {
    System.out.println("Some tasks did not complete in time.");
}
```

### Clean Shutdown Pattern

```java
ExecutorService pool = Executors.newFixedThreadPool(4);

try {
    // submit tasks
    Future<Double> f1 = pool.submit(task1);
    Future<Double> f2 = pool.submit(task2);

    // get results
    System.out.println(f1.get());
    System.out.println(f2.get());

} catch (InterruptedException | ExecutionException e) {
    System.err.println("Task error: " + e.getMessage());
} finally {
    pool.shutdown();   // always in finally
}
```

---

## `invokeAll()` and `invokeAny()`

### `invokeAll()` — Run all tasks, get all results

```java
List<Callable<Double>> tasks = Arrays.asList(
    new SalaryCalculationTask(all, "Engineering"),
    new SalaryCalculationTask(all, "HR"),
    new SalaryCalculationTask(all, "Finance"),
    new SalaryCalculationTask(all, "Operations")
);

ExecutorService pool = Executors.newFixedThreadPool(4);

// Submits all, blocks until all complete
List<Future<Double>> futures = pool.invokeAll(tasks);

double grandTotal = 0;
for (Future<Double> f : futures) {
    grandTotal += f.get();
}
System.out.printf("Grand total payroll: %.2f%n", grandTotal);

pool.shutdown();
```

### `invokeAny()` — Run all, return the first result

```java
// Useful when multiple sources can answer — take the fastest
Double result = pool.invokeAny(tasks);
// Returns the result of whichever task finishes first, cancels the rest
```

---

## `ScheduledExecutorService`

Run tasks after a delay, or repeatedly at fixed intervals:

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

// Run once after a delay
scheduler.schedule(
    () -> System.out.println("Payroll reminder sent."),
    5, TimeUnit.SECONDS);

// Run repeatedly — fixed rate (time between starts)
ScheduledFuture<?> heartbeat = scheduler.scheduleAtFixedRate(
    () -> System.out.println("System heartbeat: " + System.currentTimeMillis()),
    0,    // initial delay
    10,   // period
    TimeUnit.SECONDS);

// Run repeatedly — fixed delay (time between end of one and start of next)
scheduler.scheduleWithFixedDelay(
    () -> System.out.println("Report generated."),
    2,    // initial delay
    30,   // delay after each completion
    TimeUnit.SECONDS);

// Cancel the heartbeat after 60 seconds
scheduler.schedule(() -> {
    heartbeat.cancel(false);
    scheduler.shutdown();
}, 60, TimeUnit.SECONDS);
```

---

## `CompletableFuture` — Async Pipelines

`CompletableFuture` (Java 8) is a modern, composable alternative to `Future`. It allows you to chain async operations, combine results, and handle errors — all without blocking:

```java
import java.util.concurrent.CompletableFuture;

// Run async, no return value
CompletableFuture<Void> task = CompletableFuture.runAsync(
    () -> System.out.println("Payroll processing started..."));

// Run async, with return value
CompletableFuture<Double> salaryFuture = CompletableFuture.supplyAsync(
    () -> calculateTotalSalary("Engineering"));

// Chain — transform the result
CompletableFuture<String> report = salaryFuture
    .thenApply(total -> String.format("Engineering payroll: %.2f", total));

// Chain — run another async step
CompletableFuture<String> emailFuture = report
    .thenApplyAsync(msg -> sendEmail("manager@ems.com", msg));

// Block and get the final result
System.out.println(emailFuture.get());
```

### Chaining Operations

```java
CompletableFuture
    .supplyAsync(() -> loadEmployees("Engineering"))          // async: load
    .thenApply(emps -> emps.stream()                          // transform
                           .filter(e -> e.getSalary() > 70000)
                           .collect(Collectors.toList()))
    .thenAccept(filtered ->                                   // consume
        filtered.forEach(e -> System.out.println(e.getName())))
    .join();   // wait for completion (does not throw checked exceptions)
```

### Combining Two Futures

```java
CompletableFuture<Double> engPayroll  = CompletableFuture.supplyAsync(
    () -> calculateTotalSalary("Engineering"));

CompletableFuture<Double> finPayroll  = CompletableFuture.supplyAsync(
    () -> calculateTotalSalary("Finance"));

// Run both in parallel, combine results when both done
CompletableFuture<Double> combined = engPayroll.thenCombine(
    finPayroll, (eng, fin) -> eng + fin);

System.out.printf("Combined payroll: %.2f%n", combined.get());
```

### Error Handling

```java
CompletableFuture<Employee> future = CompletableFuture
    .supplyAsync(() -> {
        // Simulate failure
        if (Math.random() > 0.5) throw new RuntimeException("DB connection failed");
        return new Employee(101, "Sonu", 75000, "Engineering");
    })
    .exceptionally(ex -> {
        System.out.println("Error: " + ex.getMessage());
        return new Employee(0, "Default", 0, "None");   // fallback
    });

Employee emp = future.get();
System.out.println("Got: " + emp.getName());
```

### `whenComplete` — Run after completion (success or failure)

```java
CompletableFuture
    .supplyAsync(() -> loadEmployee(101))
    .whenComplete((employee, exception) -> {
        if (exception != null) {
            System.out.println("Failed: " + exception.getMessage());
        } else {
            System.out.println("Loaded: " + employee.getName());
        }
    });
```

---

## Concurrent Collections

Thread-safe collection implementations — no external synchronization needed:

| Class | Use Case |
|-------|----------|
| `ConcurrentHashMap` | Thread-safe HashMap — high concurrency |
| `CopyOnWriteArrayList` | Thread-safe List — reads are lock-free, writes copy the whole list |
| `ConcurrentLinkedQueue` | Lock-free thread-safe queue |
| `BlockingQueue` | Producer-consumer queue — blocks on put/take |
| `ArrayBlockingQueue` | Bounded blocking queue |
| `LinkedBlockingQueue` | Unbounded blocking queue |

```java
ConcurrentHashMap<String, Double> deptPayroll = new ConcurrentHashMap<>();

// Multiple threads can safely read and write simultaneously
Thread t1 = new Thread(() -> deptPayroll.put("Engineering", 235000.0));
Thread t2 = new Thread(() -> deptPayroll.put("Finance",     91000.0));
Thread t3 = new Thread(() -> deptPayroll.put("HR",          55000.0));

t1.start(); t2.start(); t3.start();
t1.join();  t2.join();  t3.join();

deptPayroll.forEach((dept, total) ->
    System.out.printf("%-15s %.2f%n", dept, total));
```

### `BlockingQueue` — Producer-Consumer Pattern

```java
import java.util.concurrent.*;

BlockingQueue<Employee> queue = new LinkedBlockingQueue<>(10);

// Producer thread — adds employees to process
Thread producer = new Thread(() -> {
    String[] names = {"Sonu", "Monu", "Tonu", "Ponu", "Gonu"};
    for (int i = 0; i < names.length; i++) {
        try {
            Employee emp = new Employee(100 + i, names[i], 70000 + (i * 5000), "HR");
            queue.put(emp);    // blocks if queue is full
            System.out.println("Queued: " + emp.getName());
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
});

// Consumer thread — processes employees from queue
Thread consumer = new Thread(() -> {
    int processed = 0;
    while (processed < 5) {
        try {
            Employee emp = queue.take();   // blocks if queue is empty
            System.out.println("Processing: " + emp.getName());
            Thread.sleep(200);
            processed++;
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
});

producer.start();
consumer.start();
producer.join();
consumer.join();
```

---

## Practical Example — Parallel Department Reports

```java
public class ParallelReportGenerator {

    private static double generateReport(String dept, List<Employee> employees)
            throws InterruptedException {
        Thread.sleep(400);   // simulate report generation time
        double total = employees.stream()
            .filter(e -> e.getDepartment().equals(dept))
            .mapToDouble(Employee::getSalary)
            .sum();
        System.out.printf("[%s] Report generated for %-15s total: %,.2f%n",
                          Thread.currentThread().getName(), dept, total);
        return total;
    }

    public static void main(String[] args) throws Exception {

        List<Employee> all = Arrays.asList(
            new Employee(101, "Sonu",  75000, "Engineering"),
            new Employee(102, "Monu",  82000, "Engineering"),
            new Employee(103, "Tonu",  55000, "HR"),
            new Employee(104, "Ponu",  91000, "Finance"),
            new Employee(105, "Gonu",  68000, "Operations"),
            new Employee(106, "Ronu",  78000, "Engineering")
        );

        String[] departments = {"Engineering", "HR", "Finance", "Operations"};
        ExecutorService pool = Executors.newFixedThreadPool(4);

        long start = System.currentTimeMillis();

        // Build Callables
        List<Callable<Double>> tasks = new ArrayList<>();
        for (String dept : departments) {
            tasks.add(() -> generateReport(dept, all));
        }

        // Run all in parallel
        List<Future<Double>> futures = pool.invokeAll(tasks);

        // Collect results
        double grandTotal = 0;
        for (Future<Double> f : futures) grandTotal += f.get();

        long elapsed = System.currentTimeMillis() - start;

        System.out.printf("%nGrand total payroll: %,.2f%n", grandTotal);
        System.out.printf("Completed in: %dms (sequential would take ~%dms)%n",
                          elapsed, departments.length * 400);

        pool.shutdown();
    }
}
```

Output:
```
[pool-1-thread-2] Report generated for HR              total: 55,000.00
[pool-1-thread-1] Report generated for Engineering     total: 235,000.00
[pool-1-thread-4] Report generated for Operations      total: 68,000.00
[pool-1-thread-3] Report generated for Finance         total: 91,000.00

Grand total payroll: 449,000.00
Completed in: 415ms (sequential would take ~1600ms)
```

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| `ExecutorService` | Manages a thread pool — don't create raw threads in production |
| `Executors.newFixedThreadPool(n)` | Fixed n threads — good for bounded, known workloads |
| `Executors.newCachedThreadPool()` | Grows as needed — good for many short tasks |
| `Callable<T>` | Like `Runnable` but returns a result and can throw checked exceptions |
| `Future<T>` | Handle to async result — `get()` blocks until done |
| `invokeAll()` | Submit all tasks, wait for all to complete |
| `invokeAny()` | Submit all tasks, return first result |
| `ScheduledExecutorService` | Delay and periodic task execution |
| `CompletableFuture` | Composable async pipelines — chain, combine, handle errors |
| `ConcurrentHashMap` | Thread-safe map — use instead of `HashMap` in concurrent code |
| `BlockingQueue` | Thread-safe producer-consumer queue — blocks on full/empty |
| `shutdown()` | Always call when done — prevents thread leak |

---

## What's Next

**Part G — I/O and Collections.** Module 25 covers IO Streams — reading and writing files, serialization, and the modern NIO file API.
