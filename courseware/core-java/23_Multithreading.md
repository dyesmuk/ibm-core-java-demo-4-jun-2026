# Module 23 — Multithreading

> **Part F: Concurrency**
> Prerequisites: Module 08–13, Module 17 · Time: ~2.5 hours

---

## What Is Multithreading?

A **thread** is the smallest unit of execution. A Java program starts with one thread — the main thread. Multithreading means running multiple threads simultaneously, allowing a program to do several things at once.

Real examples:
- Process 1000 employee records in parallel instead of sequentially
- Keep a UI responsive while a background task loads data
- Handle multiple HTTP requests at the same time in a web server

A single-core CPU switches between threads rapidly (context switching), giving the illusion of parallelism. A multi-core CPU runs threads truly in parallel — one per core.

---

## Thread Lifecycle

```
NEW → RUNNABLE → RUNNING → (BLOCKED / WAITING / TIMED_WAITING) → TERMINATED
```

| State | What It Means |
|-------|--------------|
| `NEW` | Thread created but `start()` not called yet |
| `RUNNABLE` | Ready to run — waiting for CPU |
| `RUNNING` | Currently executing |
| `BLOCKED` | Waiting to acquire a lock |
| `WAITING` | Waiting indefinitely — for `notify()` or `join()` |
| `TIMED_WAITING` | Waiting with a timeout — `sleep()`, `join(timeout)` |
| `TERMINATED` | Finished execution |

---

## Creating Threads

### Option 1 — Extend `Thread`

```java
public class PayrollTask extends Thread {

    private String departmentName;
    private double[] salaries;

    public PayrollTask(String departmentName, double[] salaries) {
        super("Payroll-" + departmentName);   // give the thread a name
        this.departmentName = departmentName;
        this.salaries       = salaries;
    }

    @Override
    public void run() {
        double total = 0;
        for (double s : salaries) total += s;
        System.out.printf("[%s] Department: %-15s Total: %.2f%n",
                          Thread.currentThread().getName(),
                          departmentName, total);
    }
}
```

```java
PayrollTask t1 = new PayrollTask("Engineering", new double[]{75000, 82000, 55000});
PayrollTask t2 = new PayrollTask("Finance",     new double[]{91000, 68000});
PayrollTask t3 = new PayrollTask("HR",          new double[]{55000, 61000});

t1.start();   // starts the thread — calls run() in a new thread
t2.start();
t3.start();
```

**`start()` vs `run()`:**
- `start()` — creates a new OS thread and calls `run()` on it ✓
- `run()` — calls `run()` directly on the current thread, no new thread ✗

### Option 2 — Implement `Runnable` (Preferred)

Separates the task (what to do) from the thread (how to run it). More flexible — a class can implement `Runnable` while still extending another class:

```java
public class SalaryAuditTask implements Runnable {

    private String department;
    private List<Employee> employees;

    public SalaryAuditTask(String department, List<Employee> employees) {
        this.department = department;
        this.employees  = employees;
    }

    @Override
    public void run() {
        long highEarners = employees.stream()
            .filter(e -> e.getDepartment().equals(department))
            .filter(e -> e.getSalary() > 80000)
            .count();
        System.out.printf("[%s] %s — high earners: %d%n",
                          Thread.currentThread().getName(),
                          department, highEarners);
    }
}
```

```java
List<Employee> all = List.of(
    new Employee(101, "Sonu",  75000, "Engineering"),
    new Employee(102, "Monu",  82000, "Engineering"),
    new Employee(103, "Tonu",  55000, "HR"),
    new Employee(104, "Ponu",  91000, "Finance")
);

Thread t1 = new Thread(new SalaryAuditTask("Engineering", all), "Audit-Eng");
Thread t2 = new Thread(new SalaryAuditTask("Finance", all),     "Audit-Fin");

t1.start();
t2.start();
```

### Option 3 — Lambda (Most Concise)

Since `Runnable` is a functional interface:

```java
Thread t1 = new Thread(() -> {
    System.out.println("Processing payroll in: " + Thread.currentThread().getName());
}, "PayrollThread");

t1.start();
```

---

## Thread Methods

### `sleep(millis)` — Pause execution

```java
public void run() {
    System.out.println(getName() + " started.");
    try {
        Thread.sleep(2000);   // pause for 2 seconds
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();   // restore interrupted status
        System.out.println(getName() + " was interrupted.");
    }
    System.out.println(getName() + " finished.");
}
```

`sleep()` throws `InterruptedException` — always catch it and restore the interrupted flag with `Thread.currentThread().interrupt()`.

### `join()` — Wait for a thread to finish

```java
Thread t1 = new Thread(() -> processPayroll("Engineering"), "T1");
Thread t2 = new Thread(() -> processPayroll("Finance"),     "T2");

t1.start();
t2.start();

t1.join();   // main thread waits here until t1 is done
t2.join();   // main thread waits here until t2 is done

System.out.println("All payroll processing complete.");
// This line only runs after both t1 and t2 have finished
```

```java
// join with timeout
t1.join(5000);   // wait at most 5 seconds, then continue regardless
```

### `interrupt()` — Signal a thread to stop

```java
Thread worker = new Thread(() -> {
    while (!Thread.currentThread().isInterrupted()) {
        // do work
        System.out.println("Working...");
    }
    System.out.println("Worker stopped gracefully.");
});

worker.start();
Thread.sleep(100);
worker.interrupt();   // signals the worker to stop
```

Always design threads to respond to interruption — check `isInterrupted()` in loops, and catch `InterruptedException` properly.

### `setDaemon(true)` — Background thread

A daemon thread runs in the background and is automatically killed when all non-daemon (user) threads finish. The JVM does not wait for daemon threads to complete before exiting.

```java
Thread monitor = new Thread(() -> {
    while (true) {
        System.out.println("Heartbeat — system alive.");
        Thread.sleep(1000);
    }
});
monitor.setDaemon(true);   // must be set BEFORE start()
monitor.start();
// When main thread ends, this daemon thread is killed automatically
```

Use for background monitoring, cleanup tasks, log flushing.

### `setPriority(int)` — Hint to the scheduler

```java
Thread critical = new Thread(() -> processUrgentPayroll());
Thread normal   = new Thread(() -> generateReports());

critical.setPriority(Thread.MAX_PRIORITY);   // 10
normal.setPriority(Thread.MIN_PRIORITY);     // 1
// Thread.NORM_PRIORITY = 5 (default)
```

Priority is a **hint** only — the OS scheduler is not obligated to honour it.

---

## Thread Safety and Race Conditions

When two threads access and modify the same data simultaneously, results are unpredictable. This is called a **race condition**.

```java
public class EmployeeCounter {
    private int count = 0;

    public void increment() {
        count++;   // NOT atomic — three operations: read, increment, write
    }

    public int getCount() { return count; }
}
```

```java
EmployeeCounter counter = new EmployeeCounter();

Runnable task = () -> {
    for (int i = 0; i < 1000; i++) {
        counter.increment();
    }
};

Thread t1 = new Thread(task);
Thread t2 = new Thread(task);
t1.start();
t2.start();
t1.join();
t2.join();

System.out.println("Expected: 2000, Got: " + counter.getCount());
// Got: 1843 (or some other unpredictable number)
```

Both threads read-increment-write the same `count` simultaneously. Some increments overwrite each other.

---

## `synchronized` — The Lock

`synchronized` ensures only one thread executes a block or method at a time on the same object. It uses the object's **intrinsic lock (monitor)**.

### Synchronized Method

```java
public class EmployeeCounter {
    private int count = 0;

    public synchronized void increment() {
        count++;   // now thread-safe — only one thread at a time
    }

    public synchronized int getCount() { return count; }
}
```

### Synchronized Block — More Precise

Synchronize only the part that needs protection, not the whole method:

```java
public class PayrollService {

    private final List<Double> processedSalaries = new ArrayList<>();
    private double totalPayroll = 0;
    private final Object lock = new Object();

    public void processSalary(double salary) {
        // expensive calculation — no shared state, no sync needed
        double net = salary * 0.90;

        // update shared state — synchronized
        synchronized (lock) {
            processedSalaries.add(net);
            totalPayroll += net;
        }
    }

    public synchronized double getTotalPayroll() { return totalPayroll; }
}
```

Use the smallest synchronized block possible — holding a lock longer than needed reduces concurrency.

### What `synchronized` Guarantees

1. **Mutual exclusion** — only one thread runs the synchronized block at a time
2. **Visibility** — changes made inside a synchronized block are visible to other threads that subsequently acquire the same lock

---

## `volatile` — Visibility Without Locking

`volatile` ensures that reads and writes to a variable are always done from/to main memory, not a thread's local cache. It guarantees **visibility** but not **atomicity**:

```java
public class PayrollProcessor {

    private volatile boolean running = true;   // visible across all threads

    public void start() {
        Thread worker = new Thread(() -> {
            while (running) {
                processNextBatch();
            }
            System.out.println("Processor stopped.");
        });
        worker.start();
    }

    public void stop() {
        running = false;   // other threads immediately see this change
    }
}
```

Without `volatile`, a thread might cache `running = true` and never see the update from another thread.

`volatile` is suitable for simple flags. For counters and compound operations, use `synchronized` or the `java.util.concurrent.atomic` classes.

---

## `Atomic` Classes

`java.util.concurrent.atomic` provides thread-safe operations on single variables without explicit locking:

```java
import java.util.concurrent.atomic.*;

public class EmployeeCounter {
    private AtomicInteger count = new AtomicInteger(0);

    public void increment()  { count.incrementAndGet(); }
    public int  getCount()   { return count.get(); }

    // Compare-and-set — set new value only if current value matches expected
    public boolean tryReset(int expectedValue) {
        return count.compareAndSet(expectedValue, 0);
    }
}
```

```java
// Now thread-safe, no synchronized needed
EmployeeCounter counter = new EmployeeCounter();
Runnable task = () -> { for (int i = 0; i < 1000; i++) counter.increment(); };

Thread t1 = new Thread(task);
Thread t2 = new Thread(task);
t1.start(); t2.start();
t1.join();  t2.join();

System.out.println("Got: " + counter.getCount());   // always 2000
```

---

## Deadlock

A deadlock occurs when two threads each hold a lock the other needs, and both wait forever:

```java
Object lockA = new Object();
Object lockB = new Object();

Thread t1 = new Thread(() -> {
    synchronized (lockA) {
        System.out.println("T1 acquired A, waiting for B");
        synchronized (lockB) {   // waits for t2 to release B — forever
            System.out.println("T1 acquired B");
        }
    }
});

Thread t2 = new Thread(() -> {
    synchronized (lockB) {
        System.out.println("T2 acquired B, waiting for A");
        synchronized (lockA) {   // waits for t1 to release A — forever
            System.out.println("T2 acquired A");
        }
    }
});

t1.start();
t2.start();
// Both threads wait forever — deadlock
```

**Preventing deadlocks:**
- Always acquire locks in the same order across all threads
- Use `tryLock()` with a timeout from `java.util.concurrent.locks`
- Minimise the number of locks held simultaneously

---

## Practical Example — Parallel Payroll Processing

```java
import java.util.*;
import java.util.concurrent.*;

public class ParallelPayroll {

    private static final Object printLock = new Object();
    private static double totalPayroll = 0;

    public static void processDepartment(String dept, List<Employee> employees)
            throws InterruptedException {

        double deptTotal = employees.stream()
            .filter(e -> e.getDepartment().equals(dept))
            .mapToDouble(Employee::getSalary)
            .sum();

        Thread.sleep(500);   // simulate processing time

        synchronized (printLock) {
            totalPayroll += deptTotal;
            System.out.printf("[%-20s] %-15s total: %,.2f%n",
                              Thread.currentThread().getName(), dept, deptTotal);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        List<Employee> all = Arrays.asList(
            new Employee(101, "Sonu",  75000, "Engineering"),
            new Employee(102, "Monu",  82000, "Engineering"),
            new Employee(103, "Tonu",  55000, "HR"),
            new Employee(104, "Ponu",  91000, "Finance"),
            new Employee(105, "Gonu",  68000, "Operations"),
            new Employee(106, "Ronu",  78000, "Engineering")
        );

        String[] departments = {"Engineering", "HR", "Finance", "Operations"};
        Thread[] threads = new Thread[departments.length];

        long start = System.currentTimeMillis();

        for (int i = 0; i < departments.length; i++) {
            String dept = departments[i];
            threads[i] = new Thread(() -> {
                try { processDepartment(dept, all); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }, "Worker-" + dept);
            threads[i].start();
        }

        for (Thread t : threads) t.join();   // wait for all threads

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("%nTotal payroll: %,.2f (processed in %dms)%n",
                          totalPayroll, elapsed);
        System.out.println("Sequential equivalent would take ~" +
                           (departments.length * 500) + "ms");
    }
}
```

Output (order of department lines may vary):
```
[Worker-Finance      ] Finance         total: 91,000.00
[Worker-HR           ] HR              total: 55,000.00
[Worker-Operations   ] Operations      total: 68,000.00
[Worker-Engineering  ] Engineering     total: 235,000.00

Total payroll: 449,000.00 (processed in ~520ms)
Sequential equivalent would take ~2000ms
```

4 departments processed in ~500ms instead of ~2000ms — because they ran in parallel.

---

## Thread-Local Variables

`ThreadLocal<T>` gives each thread its own independent copy of a variable:

```java
public class AuditContext {

    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setUser(String user)  { currentUser.set(user); }
    public static String getUser()           { return currentUser.get(); }
    public static void clear()               { currentUser.remove(); }
}

// Each thread sets and reads its own value independently
Thread t1 = new Thread(() -> {
    AuditContext.setUser("Sonu");
    System.out.println("T1 user: " + AuditContext.getUser());  // Sonu
    AuditContext.clear();
});

Thread t2 = new Thread(() -> {
    AuditContext.setUser("Monu");
    System.out.println("T2 user: " + AuditContext.getUser());  // Monu
    AuditContext.clear();
});
```

`ThreadLocal` is widely used in web frameworks to store per-request context (current user, transaction ID) without passing it through every method call.

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| `Thread` vs `Runnable` | Prefer `Runnable` — separates task from thread |
| `start()` vs `run()` | Always `start()` — `run()` does not create a new thread |
| `sleep(ms)` | Pause current thread — must handle `InterruptedException` |
| `join()` | Wait for another thread to finish |
| `interrupt()` | Signal a thread to stop — it must cooperate |
| `synchronized` | Mutual exclusion — one thread at a time |
| `volatile` | Visibility guarantee for simple flags — not for compound ops |
| `AtomicInteger` etc. | Thread-safe counters/references without locking |
| Race condition | Two threads modifying shared data simultaneously — unpredictable results |
| Deadlock | Two threads waiting for each other's lock — both stuck forever |
| Daemon thread | Background thread killed when JVM exits |
| `ThreadLocal` | Per-thread variable — no sharing |

---

## What's Next

**Module 24** — Executor Framework. Managing raw threads manually is error-prone and doesn't scale. The Executor Framework gives you thread pools, task submission, futures, and scheduled execution.
