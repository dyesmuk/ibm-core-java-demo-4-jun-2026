# Module 31 — Java 21 Features

> **Part I: Modern Java**
> Prerequisites: Module 30, Module 23 (Multithreading), Module 24 (Executor Framework) · Time: ~1 hour
> Java version required: JDK 21+

---

## About Java 21

Java 21 (September 2023) is the current recommended LTS release. It delivered Project Loom's virtual threads and finalised several language features. This is the most impactful Java release since Java 8.

Key features:
| Feature | Status in Java 21 |
|---------|------------------|
| Virtual Threads | Standard (JEP 444) |
| Sequenced Collections | Standard (JEP 431) |
| Record Patterns | Standard (JEP 440) |
| Pattern Matching in Switch | Standard (JEP 441) |
| String Templates | Preview (JEP 430) |
| Unnamed Classes & Instance Main | Preview (JEP 445) |
| Structured Concurrency | Preview (JEP 453) |

---

## Virtual Threads (Project Loom)

### The Problem with Platform Threads

Traditional Java threads are **platform threads** — each one maps 1:1 to an OS thread. OS threads are expensive:
- ~1MB of memory per thread stack
- Context switching cost
- Typical JVM handles 1,000–10,000 threads comfortably

In a web server handling 50,000 simultaneous requests, you need 50,000 threads — that's 50GB of memory just for stacks. The common workaround: thread pools with async/reactive code. Effective, but complex.

### Virtual Threads — The Solution

Virtual threads are **lightweight threads managed by the JVM**, not the OS:
- Millions can exist simultaneously
- Start in microseconds, use ~few KB of memory
- When a virtual thread blocks (I/O, sleep), the JVM moves it off the OS thread automatically — the OS thread is freed for other work
- Your code looks exactly like regular blocking code — no callbacks, no reactive chains

```java
// Platform thread — expensive, ~1MB stack
Thread platformThread = new Thread(() -> processRequest());
platformThread.start();

// Virtual thread — cheap, few KB, millions possible
Thread virtualThread = Thread.ofVirtual().start(() -> processRequest());
```

### Creating Virtual Threads

```java
// Option 1 — Thread.ofVirtual()
Thread vt = Thread.ofVirtual()
                  .name("payroll-processor")
                  .start(() -> processPayroll());

// Option 2 — Thread.startVirtualThread() (shortcut)
Thread vt2 = Thread.startVirtualThread(() -> sendEmail("Sonu"));

// Option 3 — ExecutorService with virtual threads per task
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
executor.submit(() -> processEmployee(employee));
```

### Virtual Threads in Practice

Process 10,000 employees — each simulated with a 100ms I/O delay:

```java
import java.util.concurrent.*;
import java.util.*;

public class VirtualThreadDemo {

    static void processEmployee(int id) {
        try {
            Thread.sleep(100);   // simulate DB call or HTTP request
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // System.out.println("Processed employee " + id);
    }

    public static void main(String[] args) throws Exception {

        int employeeCount = 10_000;

        // With platform threads — limited by thread pool size
        long start1 = System.currentTimeMillis();
        try (ExecutorService pool = Executors.newFixedThreadPool(200)) {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 1; i <= employeeCount; i++) {
                int id = i;
                futures.add(pool.submit(() -> processEmployee(id)));
            }
            for (Future<?> f : futures) f.get();
        }
        System.out.printf("Platform threads (pool=200): %dms%n",
                          System.currentTimeMillis() - start1);

        // With virtual threads — one per task, no pool needed
        long start2 = System.currentTimeMillis();
        try (ExecutorService vPool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 1; i <= employeeCount; i++) {
                int id = i;
                futures.add(vPool.submit(() -> processEmployee(id)));
            }
            for (Future<?> f : futures) f.get();
        }
        System.out.printf("Virtual threads:             %dms%n",
                          System.currentTimeMillis() - start2);
    }
}
```

Typical output:
```
Platform threads (pool=200): ~5200ms   (10000 tasks / 200 threads * 100ms)
Virtual threads:               ~130ms  (all 10000 run nearly in parallel)
```

### What Virtual Threads Are NOT Good For

Virtual threads help with **I/O-bound** work — waiting for databases, HTTP calls, file reads.

They do **not** help with CPU-bound work — heavy computation. For CPU-bound parallelism, use a fixed thread pool with `Runtime.getRuntime().availableProcessors()` threads.

```java
// CPU-bound — use fixed pool, not virtual threads
int cores = Runtime.getRuntime().availableProcessors();
ExecutorService cpuPool = Executors.newFixedThreadPool(cores);

// I/O-bound — virtual threads shine
ExecutorService ioPool = Executors.newVirtualThreadPerTaskExecutor();
```

### Pinning — What to Avoid

A virtual thread is **pinned** to its OS thread (cannot unmount) when it is inside a `synchronized` block or when calling native code. Pinned virtual threads behave like platform threads and lose the scalability benefit.

```java
// Avoid synchronized with virtual threads — causes pinning
synchronized (lock) {
    Thread.sleep(100);   // virtual thread pinned during sleep — blocks the OS thread
}

// Use ReentrantLock instead — virtual-thread friendly
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    Thread.sleep(100);   // virtual thread can unmount, OS thread freed
} finally {
    lock.unlock();
}
```

---

## Sequenced Collections (JEP 431)

Java 21 adds three new interfaces to the collections hierarchy to represent collections with a **defined encounter order** (first and last elements accessible):

```
SequencedCollection<E>
    addFirst(E), addLast(E)
    getFirst(), getLast()
    removeFirst(), removeLast()
    reversed()              → a reverse-order view

SequencedSet<E> extends SequencedCollection<E>, Set<E>

SequencedMap<K,V>
    firstEntry(), lastEntry()
    putFirst(K,V), putLast(K,V)
    pollFirstEntry(), pollLastEntry()
    reversed()
```

Before Java 21, getting the first/last element of a `LinkedHashMap` required a workaround:

```java
// Pre-Java 21 — awkward
LinkedHashMap<Integer, Employee> map = new LinkedHashMap<>();
// ... populate ...
Map.Entry<Integer, Employee> first = map.entrySet().iterator().next();  // ugly
Map.Entry<Integer, Employee> last  = null;
for (Map.Entry<Integer, Employee> e : map.entrySet()) last = e;         // iterate all

// Java 21 — clean
LinkedHashMap<Integer, Employee> map = new LinkedHashMap<>();
// ... populate ...
Map.Entry<Integer, Employee> first = map.firstEntry();
Map.Entry<Integer, Employee> last  = map.lastEntry();
```

### Usage Examples

```java
// SequencedCollection — List and Deque implement this
List<Employee> employees = new ArrayList<>(List.of(
    new Employee(101, "Sonu",  75000, "Engineering"),
    new Employee(102, "Monu",  82000, "Engineering"),
    new Employee(103, "Tonu",  55000, "HR")
));

Employee first = employees.getFirst();   // Sonu
Employee last  = employees.getLast();    // Tonu
employees.addFirst(new Employee(100, "Boss", 200000, "Management"));
employees.addLast(new Employee(104,  "Ponu",  91000, "Finance"));
employees.removeFirst();
employees.removeLast();

// Reversed view — no copy, just a view
List<Employee> reversed = employees.reversed();
reversed.forEach(e -> System.out.println(e.getName()));   // Tonu, Monu, Sonu
```

```java
// SequencedMap — LinkedHashMap and TreeMap implement this
LinkedHashMap<Integer, String> idToName = new LinkedHashMap<>();
idToName.put(101, "Sonu");
idToName.put(102, "Monu");
idToName.put(103, "Tonu");

System.out.println(idToName.firstEntry().getValue());  // Sonu
System.out.println(idToName.lastEntry().getValue());   // Tonu

idToName.putFirst(100, "Boss");     // inserts at the beginning
idToName.putLast(104,  "Ponu");     // inserts at the end

idToName.sequencedKeySet().forEach(System.out::println);   // 100, 101, 102, 103, 104
idToName.reversed().forEach((k,v) ->
    System.out.printf("%d → %s%n", k, v));  // 104, 103, 102, 101, 100
```

---

## Record Patterns (JEP 440)

Java 16 gave us pattern matching for `instanceof`. Java 21 extends this to records — you can destructure a record's components directly in the pattern:

```java
// Records from Module 30
record EmployeeRecord(int id, String name, double salary, String department) { }
record Address(String city, String country) { }
record EmployeeWithAddress(EmployeeRecord employee, Address address) { }
```

### Destructuring in `instanceof`

```java
Object obj = new EmployeeRecord(101, "Sonu", 75000, "Engineering");

// Old way — access components via accessor methods
if (obj instanceof EmployeeRecord e) {
    System.out.println(e.name() + " in " + e.department());
}

// Java 21 record pattern — destructure directly
if (obj instanceof EmployeeRecord(int id, String name, double salary, String dept)) {
    System.out.println(name + " earns " + salary + " in " + dept);
}
```

### Nested Record Patterns

```java
Object record = new EmployeeWithAddress(
    new EmployeeRecord(101, "Sonu", 75000, "Engineering"),
    new Address("Pune", "India")
);

// Nested destructuring — directly access nested components
if (record instanceof EmployeeWithAddress(
        EmployeeRecord(int id, String name, double salary, String dept),
        Address(String city, String country))) {
    System.out.printf("%s from %s, %s earns %.0f%n", name, city, country, salary);
    // Sonu from Pune, India earns 75000
}
```

### Record Patterns in Switch

```java
static String formatEmployee(Object obj) {
    return switch (obj) {
        case EmployeeRecord(var id, var name, var salary, var dept)
                when salary > 80000 ->
            String.format("[SENIOR] %s (%s)", name, dept);

        case EmployeeRecord(var id, var name, var salary, var dept) ->
            String.format("[STAFF]  %s (%s)", name, dept);

        case EmployeeWithAddress(
                EmployeeRecord(var id, var name, var salary, var dept),
                Address(var city, var country)) ->
            String.format("%s at %s, %s", name, city, country);

        default -> "Unknown employee type";
    };
}
```

---

## Pattern Matching in Switch — Standard (JEP 441)

Switch pattern matching (preview in Java 17) is now standard in Java 21. Full details were covered in Module 30, but here is what is new/finalised:

### `when` Guards

Add conditions to switch cases:

```java
Employee e = new Employee(101, "Sonu", 91000, "Engineering");

String classification = switch (e) {
    case Employee emp when emp.getSalary() > 100000 -> "Executive";
    case Employee emp when emp.getSalary() > 80000  -> "Senior";
    case Employee emp when emp.getSalary() > 60000  -> "Mid-level";
    case Employee emp                               -> "Junior";
};
System.out.println(classification);   // Senior
```

### `null` in Switch

Previously, a `null` value passed to a switch caused `NullPointerException`. Now it can be handled explicitly:

```java
Employee e = null;

String result = switch (e) {
    case null    -> "No employee provided";
    case Employee emp when emp.getSalary() > 80000 -> "Senior: " + emp.getName();
    case Employee emp -> "Staff: " + emp.getName();
};
System.out.println(result);   // No employee provided
```

---

## Structured Concurrency (Preview — JEP 453)

Structured concurrency treats multiple tasks running in different threads as a single unit of work. If one task fails, the others are automatically cancelled. If the main task is cancelled, all subtasks are cancelled too.

```java
import java.util.concurrent.StructuredTaskScope;

public class PayrollReport {

    public static void main(String[] args) throws Exception {

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            // Fork subtasks — run concurrently
            StructuredTaskScope.Subtask<Double> engPayroll =
                scope.fork(() -> calculateDeptPayroll("Engineering"));

            StructuredTaskScope.Subtask<Double> finPayroll =
                scope.fork(() -> calculateDeptPayroll("Finance"));

            StructuredTaskScope.Subtask<Double> hrPayroll =
                scope.fork(() -> calculateDeptPayroll("HR"));

            // Wait for all subtasks to complete (or one to fail)
            scope.join();

            // If any subtask failed, this throws its exception
            scope.throwIfFailed();

            // All succeeded — use results
            double total = engPayroll.get() + finPayroll.get() + hrPayroll.get();
            System.out.printf("Total payroll: %.2f%n", total);
        }
        // scope closed — all subtasks guaranteed done or cancelled
    }

    static double calculateDeptPayroll(String dept) throws InterruptedException {
        Thread.sleep(300);   // simulate DB query
        return switch (dept) {
            case "Engineering" -> 235000.0;
            case "Finance"     -> 91000.0;
            case "HR"          -> 116000.0;
            default            -> 0.0;
        };
    }
}
```

`ShutdownOnFailure` — if any subtask throws, all other subtasks are cancelled and the exception is re-thrown at `throwIfFailed()`.

`ShutdownOnSuccess` — as soon as one subtask succeeds, all others are cancelled (useful for try-first-available patterns).

This is preview in Java 21. It pairs naturally with virtual threads — structured concurrency defines the lifecycle, virtual threads handle the scale.

---

## Other Java 21 Highlights

### Unnamed Classes and Instance Main Methods (Preview)

For simple programs — no `public class`, no `public static`, just a `main()` method:

```java
// Unnamed class — no class declaration needed
void main() {
    System.out.println("Hello from Java 21!");
    var names = java.util.List.of("Sonu", "Monu", "Tonu");
    names.forEach(System.out::println);
}
```

Great for scripting and teaching. Preview in Java 21, progressing toward standard.

### Unnamed Patterns and Variables (Preview)

When you need to match a pattern but don't need the bound variable, use `_`:

```java
switch (obj) {
    case Manager _ -> System.out.println("It's a manager");   // don't need the variable
    case Developer d -> System.out.println(d.getTechStack()); // need the variable
    default -> System.out.println("Other");
}
```

---

## Practical Example — Virtual Threads + Sequenced Collections

```java
import java.util.*;
import java.util.concurrent.*;

public class PayrollDashboard {

    record DeptResult(String department, double total, int count, long processingMs) {}

    static DeptResult processDepartment(String dept, List<Employee> employees)
            throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(200);   // simulate DB / payroll system call

        double total = employees.stream()
            .filter(e -> e.getDepartment().equals(dept))
            .mapToDouble(Employee::getSalary)
            .sum();

        int count = (int) employees.stream()
            .filter(e -> e.getDepartment().equals(dept))
            .count();

        return new DeptResult(dept, total, count,
                              System.currentTimeMillis() - start);
    }

    public static void main(String[] args) throws Exception {

        List<Employee> all = List.of(
            new Employee(101, "Sonu",  75000, "Engineering"),
            new Employee(102, "Monu",  82000, "Engineering"),
            new Employee(103, "Tonu",  55000, "HR"),
            new Employee(104, "Ponu",  91000, "Finance"),
            new Employee(105, "Gonu",  68000, "Operations"),
            new Employee(106, "Ronu",  78000, "Engineering")
        );

        String[] depts = {"Engineering", "HR", "Finance", "Operations"};

        long wallStart = System.currentTimeMillis();

        // Process all departments with virtual threads
        LinkedHashMap<String, DeptResult> results = new LinkedHashMap<>();

        try (ExecutorService vPool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<DeptResult>> futures = new ArrayList<>();
            for (String dept : depts) {
                futures.add(vPool.submit(() -> processDepartment(dept, all)));
            }
            for (int i = 0; i < depts.length; i++) {
                DeptResult r = futures.get(i).get();
                results.put(depts[i], r);
            }
        }

        long wallTime = System.currentTimeMillis() - wallStart;

        // Use sequenced collection features
        System.out.println("=== Payroll Dashboard ===\n");
        System.out.printf("%-15s %12s %8s %10s%n",
                          "Department", "Total", "Count", "Avg");
        System.out.println("-".repeat(50));

        results.forEach((dept, r) ->
            System.out.printf("%-15s %12,.2f %8d %10,.2f%n",
                              dept, r.total(), r.count(),
                              r.count() > 0 ? r.total() / r.count() : 0));

        System.out.println("-".repeat(50));

        // SequencedMap features
        DeptResult firstDept = results.firstEntry().getValue();
        DeptResult lastDept  = results.lastEntry().getValue();
        System.out.printf("First in report: %s%n", firstDept.department());
        System.out.printf("Last  in report: %s%n", lastDept.department());

        double grandTotal = results.values().stream()
            .mapToDouble(DeptResult::total).sum();
        System.out.printf("%nGrand total: %,.2f%n", grandTotal);
        System.out.printf("Wall time:   %dms (4 × 200ms tasks ran in parallel)%n", wallTime);
    }
}
```

Output:
```
=== Payroll Dashboard ===

Department       Total    Count        Avg
--------------------------------------------------
Engineering   235,000.00        3   78,333.33
HR             55,000.00        1   55,000.00
Finance        91,000.00        1   91,000.00
Operations     68,000.00        1   68,000.00
--------------------------------------------------
First in report: Engineering
Last  in report: Operations

Grand total: 449,000.00
Wall time:   ~215ms (4 × 200ms tasks ran in parallel)
```

---

## Quick Summary

| Feature | Key Point |
|---------|-----------|
| Virtual threads | Millions of cheap threads — JVM-managed, not OS-managed |
| `Executors.newVirtualThreadPerTaskExecutor()` | One virtual thread per submitted task |
| Virtual threads best for | I/O-bound work — DB calls, HTTP, file reads |
| Virtual threads not for | CPU-bound computation — use fixed thread pool |
| Avoid `synchronized` with VT | Causes pinning — use `ReentrantLock` instead |
| Sequenced collections | `getFirst()`/`getLast()`, `addFirst()`/`addLast()`, `reversed()` |
| `SequencedMap` | `firstEntry()`/`lastEntry()`, `putFirst()`/`putLast()` |
| Record patterns | Destructure record components directly in `instanceof` and `switch` |
| `when` guards | Add conditions to switch patterns |
| `null` in switch | Handle null explicitly — no NPE |
| Structured concurrency | Tasks as a unit — failure cancels siblings (preview) |

---

## What's Next

**Module 32** — Java 24 Features: finalised virtual thread and concurrency improvements, primitive types in patterns, and other stable additions.
