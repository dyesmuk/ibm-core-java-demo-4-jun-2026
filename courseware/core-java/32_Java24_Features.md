# Module 32 — Java 24 Features

> **Part I: Modern Java**
> Prerequisites: Module 31 · Time: ~45 minutes
> Java version required: JDK 24+

---

## About Java 24

Java 24 (March 2025) is a non-LTS release — a stepping stone toward the next LTS (Java 25, expected September 2025). It delivers a significant number of finalised features, many graduating from preview status in Java 21/22/23.

Key theme: **Project Loom and Project Valhalla reach maturity**. Concurrency becomes easier and safer. The type system gets smarter.

| Feature | JEP | Status |
|---------|-----|--------|
| Primitive Types in Patterns | JEP 488 | Second Preview |
| Flexible Constructor Bodies | JEP 492 | Third Preview |
| Structured Concurrency | JEP 499 | Fourth Preview |
| Scoped Values | JEP 487 | Fourth Preview |
| Stream Gatherers | JEP 485 | Standard |
| Class-File API | JEP 484 | Standard |
| Ahead-of-Time Class Loading | JEP 483 | Standard |
| Remove Finalisation | JEP 421 | Finalised |
| Quantum-Resistant Cryptography | JEP 496, 497 | Standard |

This module focuses on the features most relevant to everyday Java development.

---

## Primitive Types in Patterns (JEP 488 — Second Preview)

In Java 21, pattern matching in `instanceof` and `switch` worked for reference types. Java 24 extends this to **primitives** — you can now match and narrow primitive types directly.

### Primitive `instanceof`

```java
Object value = 75000;   // autoboxed to Integer

// Old way
if (value instanceof Integer i && i > 50000) {
    System.out.println("Integer: " + i);
}

// Java 24 — primitive type patterns
if (value instanceof int i) {
    System.out.println("int: " + i);
}

// Narrowing primitive patterns — safe narrowing in one step
long bigNumber = 75000L;
if (bigNumber instanceof int i) {   // checks if value fits in int range
    System.out.println("Fits in int: " + i);
}

double salary = 75000.0;
if (salary instanceof int i) {   // checks if it's a whole number that fits in int
    System.out.println("Whole number: " + i);
}
```

### Primitive Patterns in Switch

```java
Object salaryObj = 75000.0;

String band = switch (salaryObj) {
    case Integer i when i > 100000 -> "Executive";
    case Integer i when i > 80000  -> "Senior";
    case Integer i when i > 60000  -> "Mid-level";
    case Integer i                 -> "Junior";
    case Double d when d > 80000   -> "Senior (Double)";
    case Double d                  -> "Other (Double)";
    case null                      -> "No salary";
    default                        -> "Unknown type";
};

System.out.println(band);   // Senior (Double)
```

### Why Primitives in Patterns Matter

Before this feature, every primitive had to go through autoboxing to participate in pattern matching — adding allocation overhead and requiring `Integer`/`Double`/etc. in patterns. Direct primitive support makes pattern matching consistent across all Java types and eliminates the boxing/unboxing indirection.

---

## Stream Gatherers (JEP 485 — Standard)

Streams have powerful built-in intermediate operations — `filter`, `map`, `sorted`, etc. But they couldn't be extended with custom intermediate operations cleanly. **Gatherers** fix this — they let you write your own intermediate operations.

### The `Gatherer` Interface

A `Gatherer` is a stream intermediate operation with four optional components:
- **Initializer** — creates mutable state
- **Integrator** — processes each element
- **Combiner** — merges state from parallel streams
- **Finisher** — produces final output when stream ends

```java
import java.util.stream.*;
import java.util.stream.Gatherer;

// Built-in Gatherers (added in Java 24 via Gatherers utility class):
// Gatherers.windowFixed(n)   — fixed-size non-overlapping windows
// Gatherers.windowSliding(n) — sliding windows
// Gatherers.fold(...)        — like reduce, but emits intermediate results
// Gatherers.scan(...)        — running accumulation
// Gatherers.mapConcurrent(n, fn) — parallel map with concurrency limit
```

### Built-in Gatherers Examples

```java
import java.util.stream.Gatherers;
import java.util.*;

List<Employee> employees = List.of(
    new Employee(101, "Sonu",  75000, "Engineering"),
    new Employee(102, "Monu",  82000, "Engineering"),
    new Employee(103, "Tonu",  55000, "HR"),
    new Employee(104, "Ponu",  91000, "Finance"),
    new Employee(105, "Gonu",  68000, "Operations"),
    new Employee(106, "Ronu",  78000, "Engineering")
);

// Fixed-size windows — group employees in batches of 2
employees.stream()
    .gather(Gatherers.windowFixed(2))
    .forEach(window -> {
        System.out.println("Batch: " +
            window.stream().map(Employee::getName)
                           .collect(Collectors.joining(", ")));
    });
// Batch: Sonu, Monu
// Batch: Tonu, Ponu
// Batch: Gonu, Ronu

// Sliding window — overlapping pairs
employees.stream()
    .map(Employee::getSalary)
    .gather(Gatherers.windowSliding(3))
    .forEach(window -> System.out.println("Window: " + window));
// Window: [75000.0, 82000.0, 55000.0]
// Window: [82000.0, 55000.0, 91000.0]
// Window: [55000.0, 91000.0, 68000.0]
// Window: [91000.0, 68000.0, 78000.0]

// Running average salary
employees.stream()
    .mapToDouble(Employee::getSalary)
    .boxed()
    .gather(Gatherers.scan(() -> 0.0,
            (acc, salary) -> (acc + salary)))
    .forEach(running -> System.out.printf("Running total: %.0f%n", running));
```

### Custom Gatherer — Top N per Group

```java
// Custom gatherer: emit only the first N elements that satisfy a condition
public static <T> Gatherer<T, ?, T> firstN(int n, java.util.function.Predicate<T> pred) {
    return Gatherer.ofSequential(
        () -> new int[]{0},                          // state: count
        (state, element, downstream) -> {
            if (pred.test(element) && state[0] < n) {
                state[0]++;
                return downstream.push(element);
            }
            return !downstream.isRejecting();
        }
    );
}

// Use: first 2 employees with salary > 70000
employees.stream()
    .gather(firstN(2, e -> e.getSalary() > 70000))
    .forEach(e -> System.out.println(e.getName() + " " + e.getSalary()));
// Sonu 75000.0
// Monu 82000.0
```

---

## Structured Concurrency — Maturing (JEP 499 — Fourth Preview)

Structured concurrency has been in preview since Java 21. By Java 24 it is well-stabilised (fourth preview) with API refinements. Standard release expected in Java 25.

### What's New Since Java 21

The API has been refined — `StructuredTaskScope` is cleaner and subtask results are handled more expressively:

```java
import java.util.concurrent.*;

public class PayrollAggregator {

    record PayrollResult(String dept, double total) {}

    static PayrollResult fetchDeptPayroll(String dept) throws InterruptedException {
        Thread.sleep(200);   // simulate async I/O
        double total = switch (dept) {
            case "Engineering" -> 235000.0;
            case "Finance"     -> 91000.0;
            case "HR"          -> 55000.0;
            default            -> 0.0;
        };
        return new PayrollResult(dept, total);
    }

    public static void main(String[] args) throws Exception {

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            var eng = scope.fork(() -> fetchDeptPayroll("Engineering"));
            var fin = scope.fork(() -> fetchDeptPayroll("Finance"));
            var hr  = scope.fork(() -> fetchDeptPayroll("HR"));

            scope.join().throwIfFailed();

            // All succeeded
            double total = eng.get().total() + fin.get().total() + hr.get().total();
            System.out.printf("Total payroll: %.2f%n", total);

        }   // scope closed — all tasks guaranteed complete or cancelled
    }
}
```

---

## Scoped Values (JEP 487 — Fourth Preview)

Scoped values are an alternative to `ThreadLocal` — designed specifically for virtual threads and structured concurrency. They are **immutable** per scope, eliminating a whole class of bugs.

### `ThreadLocal` vs `ScopedValue`

| Aspect | `ThreadLocal` | `ScopedValue` |
|--------|-------------|---------------|
| Mutability | Mutable — `set()` any time | Immutable within scope |
| Virtual thread friendly | Works but expensive | Designed for virtual threads |
| Inheritance | Explicit `InheritableThreadLocal` | Automatic in structured concurrency |
| Lifecycle | Must `remove()` manually | Automatically cleaned up at scope boundary |

### Using Scoped Values

```java
import java.lang.ScopedValue;

public class AuditService {

    // Declare as a static final — like ThreadLocal
    static final ScopedValue<String> CURRENT_USER     = ScopedValue.newInstance();
    static final ScopedValue<String> REQUEST_ID       = ScopedValue.newInstance();

    public static void processRequest(String user, String requestId) {
        ScopedValue.where(CURRENT_USER, user)
                   .where(REQUEST_ID, requestId)
                   .run(() -> {
                       // Within this scope, CURRENT_USER and REQUEST_ID are bound
                       validateEmployee();
                       updateSalary();
                       audit();
                   });
        // Outside this scope, CURRENT_USER and REQUEST_ID are unbound
    }

    static void validateEmployee() {
        System.out.println("Validating request " + REQUEST_ID.get() +
                           " for user " + CURRENT_USER.get());
    }

    static void updateSalary() {
        System.out.println("Updating salary — audit user: " + CURRENT_USER.get());
    }

    static void audit() {
        System.out.println("Audit log: " + REQUEST_ID.get() +
                           " completed by " + CURRENT_USER.get());
    }

    public static void main(String[] args) {
        processRequest("Ponu", "REQ-2024-001");
        processRequest("Monu", "REQ-2024-002");
    }
}
```

Output:
```
Validating request REQ-2024-001 for user Ponu
Updating salary — audit user: Ponu
Audit log: REQ-2024-001 completed by Ponu
Validating request REQ-2024-002 for user Monu
Updating salary — audit user: Monu
Audit log: REQ-2024-002 completed by Monu
```

With virtual threads handling millions of concurrent requests, scoped values are far more efficient than `ThreadLocal` — no mutable state, no cleanup required, automatic propagation through structured task scopes.

---

## Ahead-of-Time Class Loading (JEP 483 — Standard)

A new JVM startup optimisation — the JVM can cache class-loading work from a previous run and replay it on startup, significantly reducing warm-up time for large applications:

```bash
# Training run — JVM records what was loaded
java -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf -cp app.jar com.ems.Main

# Production run — JVM replays from cache
java -XX:AOTMode=on -XX:AOTConfiguration=app.aotconf -cp app.jar com.ems.Main
```

Large Spring Boot applications that previously took 3–5 seconds to start can start in under 1 second with AOT class loading. No code changes needed.

---

## Finalise Removal (JEP 421 — Completed)

`Object.finalize()` and all related infrastructure is now removed from the JDK in Java 24. Any code that overrides `finalize()` will get a compile warning in Java 18+, and the method is gone by Java 24.

```java
// This no longer compiles or runs in Java 24
@Override
protected void finalize() throws Throwable {
    // compile warning → runtime no-op → removed
}
```

The replacement, as covered in Module 27, is `AutoCloseable` + `try-with-resources` for deterministic resource cleanup.

---

## Quantum-Resistant Cryptography (JEP 496, 497 — Standard)

Java 24 adds NIST-standardised post-quantum cryptographic algorithms:
- **ML-KEM** (Module-Lattice Key Encapsulation Mechanism) — key exchange
- **ML-DSA** (Module-Lattice Digital Signature Algorithm) — digital signatures

These are relevant if you work with cryptography or security-sensitive systems that need to be safe against future quantum computers. They integrate with the standard `java.security` APIs:

```java
import java.security.*;

// ML-KEM key pair generation
KeyPairGenerator kpg = KeyPairGenerator.getInstance("ML-KEM-768");
KeyPair keyPair = kpg.generateKeyPair();

// ML-DSA signing
KeyPairGenerator sigKpg = KeyPairGenerator.getInstance("ML-DSA-65");
KeyPair sigKeyPair = sigKpg.generateKeyPair();

Signature sig = Signature.getInstance("ML-DSA-65");
sig.initSign(sigKeyPair.getPrivate());
sig.update("EmployeeID:101:Sonu".getBytes());
byte[] signature = sig.sign();
System.out.println("Signature length: " + signature.length + " bytes");
```

---

## Flexible Constructor Bodies (JEP 492 — Third Preview)

Before Java 24, statements in a constructor had to come **after** `super()` or `this()`. This restriction meant you couldn't validate or prepare arguments before the `super()` call:

```java
// Old restriction — had to compute before the constructor
public Manager(int id, String name, double salary, String department, int teamSize) {
    super(id, name, salary, department);   // must be first
    if (teamSize <= 0) throw new IllegalArgumentException("Team size must be positive");
    this.teamSize = teamSize;
}
```

Java 24 (flexible constructor bodies) relaxes this — you can run statements before `super()` as long as you don't access `this`:

```java
// Java 24 — validate before calling super
public Manager(int id, String name, double salary, String department, int teamSize) {
    if (teamSize <= 0)                           // runs before super()
        throw new IllegalArgumentException("Team size must be positive: " + teamSize);
    super(id, name, salary, department);         // now runs after validation
    this.teamSize = teamSize;
}
```

This also helps when transforming arguments before passing them to `super()`:

```java
public Developer(int id, String rawName, double salary, String department, String techStack) {
    var name = rawName == null ? "Unknown" : rawName.strip();  // prepare before super
    super(id, name, salary, department);
    this.techStack = techStack;
}
```

---

## Practical Example — Modern Java 24 Payroll

```java
import java.util.*;
import java.util.stream.*;
import java.util.stream.Gatherers;
import java.lang.ScopedValue;
import java.util.concurrent.*;

public class ModernPayroll {

    static final ScopedValue<String> APPROVER = ScopedValue.newInstance();

    record PayslipEntry(String name, double gross, double tax, double net) {
        PayslipEntry {
            if (gross < 0) throw new IllegalArgumentException("Gross cannot be negative.");
        }
        double netSalary() { return gross - tax; }
    }

    static List<PayslipEntry> generatePayslips(List<Employee> employees) {
        return employees.stream()
            .map(e -> {
                double tax = e.getSalary() * 0.10;
                return new PayslipEntry(e.getName(), e.getSalary(), tax,
                                        e.getSalary() - tax);
            })
            .collect(Collectors.toList());
    }

    static void processPayrollRun(List<Employee> employees, String approver) {

        ScopedValue.where(APPROVER, approver).run(() -> {

            System.out.println("=== Payroll Run — Approved by: " +
                               APPROVER.get() + " ===\n");

            List<PayslipEntry> payslips = generatePayslips(employees);

            // Print in batches of 2 using Stream Gatherers
            payslips.stream()
                .gather(Gatherers.windowFixed(2))
                .forEach(batch -> {
                    System.out.println("--- Batch ---");
                    batch.forEach(p -> System.out.printf(
                        "  %-10s Gross: %8.2f  Tax: %7.2f  Net: %8.2f%n",
                        p.name(), p.gross(), p.tax(), p.net()));
                });

            // Summary
            double totalGross = payslips.stream().mapToDouble(PayslipEntry::gross).sum();
            double totalNet   = payslips.stream().mapToDouble(PayslipEntry::net).sum();
            System.out.printf("%nTotal Gross: %,.2f%n", totalGross);
            System.out.printf("Total Net:   %,.2f%n", totalNet);
            System.out.printf("Approved by: %s%n", APPROVER.get());
        });
    }

    public static void main(String[] args) throws Exception {

        List<Employee> team = List.of(
            new Employee(101, "Sonu",  75000, "Engineering"),
            new Employee(102, "Monu",  82000, "Engineering"),
            new Employee(103, "Tonu",  55000, "HR"),
            new Employee(104, "Ponu",  91000, "Finance"),
            new Employee(105, "Gonu",  68000, "Operations")
        );

        // Run payroll with scoped approver context
        processPayrollRun(team, "Ponu");
    }
}
```

Output:
```
=== Payroll Run — Approved by: Ponu ===

--- Batch ---
  Sonu       Gross: 75000.00  Tax: 7500.00  Net: 67500.00
  Monu       Gross: 82000.00  Tax: 8200.00  Net: 73800.00
--- Batch ---
  Tonu       Gross: 55000.00  Tax: 5500.00  Net: 49500.00
  Ponu       Gross: 91000.00  Tax: 9100.00  Net: 81900.00
--- Batch ---
  Gonu       Gross: 68000.00  Tax: 6800.00  Net: 61200.00

Total Gross: 371,000.00
Total Net:   333,900.00
Approved by: Ponu
```

---

## What's Next — Java 25 (LTS, September 2025)

Java 25 is the next LTS and will likely standardise:
- Structured Concurrency (currently preview)
- Scoped Values (currently preview)
- Flexible Constructor Bodies (currently preview)
- Primitive Types in Patterns (currently preview)
- Project Valhalla value types (potential preview)

For day-to-day development, **Java 21 LTS** is the production-ready baseline. Java 24 features are solid for teams on the cutting edge or building new greenfield applications.

---

## Quick Summary

| Feature | Status | Key Point |
|---------|--------|-----------|
| Primitive types in patterns | Preview | `instanceof int i` — match and narrow primitives directly |
| Stream Gatherers | **Standard** | Custom intermediate stream operations — `windowFixed`, `windowSliding`, `scan` |
| Structured Concurrency | Preview | Task groups — failure cancels siblings, clean cancellation |
| Scoped Values | Preview | Immutable `ThreadLocal` replacement — designed for virtual threads |
| AOT Class Loading | **Standard** | Faster startup — JVM replays class loading from previous run |
| `finalize()` removed | **Done** | Completely gone — use `AutoCloseable` |
| Quantum cryptography | **Standard** | ML-KEM and ML-DSA — post-quantum key exchange and signatures |
| Flexible constructors | Preview | Statements before `super()` — validate before delegating |

---

## The Modern Java Journey — Complete

You have now covered the full arc of Java:

```
Java 8    Lambdas, Streams, Optional, default methods    — Module 19–22
Java 11   String/Files API, HttpClient, var in lambdas   — Module 28
Java 13/14 Text blocks, switch expressions, helpful NPE  — Module 29
Java 17   Records, sealed classes, pattern matching      — Module 30
Java 21   Virtual threads, sequenced collections         — Module 31
Java 24   Gatherers, scoped values, primitive patterns   — Module 32
```

Each release builds on the previous. The language has evolved significantly since Java 8 — but the core Java skills from Modules 01–27 remain the foundation everything else rests on.
