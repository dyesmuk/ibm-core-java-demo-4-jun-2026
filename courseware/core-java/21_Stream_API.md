# Module 21 — Stream API

> **Part E: Java 8 Features**
> Prerequisites: Module 19, 20 · Time: ~2.5 hours

---

## What Is a Stream?

A stream is a **pipeline for processing sequences of elements** — filtering, transforming, aggregating — in a functional style. It is not a data structure. It does not store elements. It processes them on demand.

```
Source → [intermediate ops] → [intermediate ops] → terminal op → result
```

```java
List<Employee> employees = ...;

double totalSeniorSalary = employees.stream()           // source
    .filter(e -> e.getSalary() > 80000)                 // intermediate
    .mapToDouble(Employee::getSalary)                   // intermediate
    .sum();                                             // terminal
```

Three lines of code. No loop. No accumulator variable. No index. The intent is clear.

---

## Key Properties

**Lazy:** Intermediate operations are not executed until a terminal operation is called. The pipeline is built first, then executed once, in one pass through the data.

**Non-mutating:** A stream does not modify the source. It produces a new result.

**Consumed once:** A stream can only be used once. Using it after a terminal operation throws `IllegalStateException`.

**Sequential or parallel:** Every stream can run on a single thread (default) or split across multiple threads with `parallelStream()`.

---

## Creating Streams

```java
// From a Collection
List<Employee> employees = ...;
Stream<Employee> s1 = employees.stream();

// From an array
String[] names = {"Sonu", "Monu", "Tonu"};
Stream<String> s2 = Arrays.stream(names);

// From individual values
Stream<String> s3 = Stream.of("Ponu", "Gonu");

// Empty stream
Stream<Employee> s4 = Stream.empty();

// Infinite stream — generate
Stream<Double> randoms = Stream.generate(Math::random);   // infinite, use limit()

// Infinite stream — iterate
Stream<Integer> numbers = Stream.iterate(1, n -> n + 1);  // 1, 2, 3, 4, ...

// IntStream, LongStream, DoubleStream — for primitives
IntStream    ids      = IntStream.range(1, 6);       // 1, 2, 3, 4, 5
IntStream    ids2     = IntStream.rangeClosed(1, 5); // 1, 2, 3, 4, 5 (inclusive)
DoubleStream salaries = DoubleStream.of(75000, 82000, 55000);
```

---

## Intermediate Operations

Intermediate operations return a new `Stream`. They are lazy — they describe the work but do not execute it yet.

### `filter(Predicate<T>)` — Keep elements that match

```java
employees.stream()
    .filter(e -> e.getDepartment().equals("Engineering"))
    .filter(e -> e.getSalary() > 70000)
    .forEach(e -> System.out.println(e.getName()));
// Monu (82000, Engineering)
```

### `map(Function<T, R>)` — Transform each element

```java
// Employee → name
employees.stream()
    .map(Employee::getName)
    .forEach(System.out::println);

// Employee → salary string
employees.stream()
    .map(e -> String.format("%s: %.2f", e.getName(), e.getSalary()))
    .forEach(System.out::println);
```

### `mapToInt` / `mapToDouble` / `mapToLong` — Map to primitive stream

Avoids boxing. Unlocks numeric operations like `sum()`, `average()`, `min()`, `max()`:

```java
double totalPayroll = employees.stream()
    .mapToDouble(Employee::getSalary)
    .sum();

OptionalDouble avgSalary = employees.stream()
    .mapToDouble(Employee::getSalary)
    .average();

System.out.println(avgSalary.orElse(0));
```

### `flatMap(Function<T, Stream<R>>)` — Flatten nested streams

```java
// Each employee has a list of projects
List<Employee> team = ...;

List<String> allProjects = team.stream()
    .flatMap(e -> e.getProjects().stream())   // Stream<List<String>> → Stream<String>
    .distinct()
    .sorted()
    .collect(Collectors.toList());
```

Without `flatMap`, you get a `Stream<List<String>>` — a stream of lists. `flatMap` collapses each list into the stream, giving you `Stream<String>`.

### `sorted()` — Sort elements

```java
// Natural order (requires Comparable)
employees.stream()
    .sorted(Comparator.comparing(Employee::getSalary))
    .forEach(e -> System.out.println(e.getName() + " " + e.getSalary()));

// Descending
employees.stream()
    .sorted(Comparator.comparing(Employee::getSalary).reversed())
    .forEach(e -> System.out.println(e.getName()));

// Multi-level sort
employees.stream()
    .sorted(Comparator.comparing(Employee::getDepartment)
                      .thenComparing(Employee::getName))
    .forEach(e -> System.out.printf("%-15s %s%n",
                                    e.getDepartment(), e.getName()));
```

### `distinct()` — Remove duplicates

```java
List<String> departments = employees.stream()
    .map(Employee::getDepartment)
    .distinct()
    .sorted()
    .collect(Collectors.toList());
// [Engineering, Finance, HR, Operations]
```

### `limit(long)` and `skip(long)` — Pagination

```java
// First 3 employees
employees.stream()
    .limit(3)
    .forEach(e -> System.out.println(e.getName()));

// Skip first 2, take next 3 (page 2 of 3)
employees.stream()
    .skip(2)
    .limit(3)
    .forEach(e -> System.out.println(e.getName()));
```

### `peek(Consumer<T>)` — Debug without breaking the pipeline

```java
employees.stream()
    .filter(e -> e.getSalary() > 70000)
    .peek(e -> System.out.println("After filter: " + e.getName()))
    .map(Employee::getSalary)
    .peek(s -> System.out.println("After map: " + s))
    .forEach(s -> {});   // terminal needed to trigger execution
```

`peek` is for debugging only — not for side effects in production code.

---

## Terminal Operations

Terminal operations trigger the pipeline execution and produce a result. After a terminal operation, the stream is consumed.

### `forEach(Consumer<T>)` — Do something with each element

```java
employees.stream()
    .filter(e -> e.getDepartment().equals("Finance"))
    .forEach(e -> System.out.println(e.getName()));
```

### `collect(Collector)` — Gather elements into a container

The most versatile terminal operation. `Collectors` provides many ready-made collectors:

```java
// → List
List<Employee> engineers = employees.stream()
    .filter(e -> e.getDepartment().equals("Engineering"))
    .collect(Collectors.toList());

// → Set (unique elements)
Set<String> uniqueDepts = employees.stream()
    .map(Employee::getDepartment)
    .collect(Collectors.toSet());

// → Map
Map<Integer, Employee> byId = employees.stream()
    .collect(Collectors.toMap(Employee::getId, e -> e));

// → Map (id → name)
Map<Integer, String> idToName = employees.stream()
    .collect(Collectors.toMap(Employee::getId, Employee::getName));

// → Joined string
String names = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.joining(", "));
// Sonu, Monu, Tonu, Ponu, Gonu

String formatted = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.joining(", ", "[", "]"));
// [Sonu, Monu, Tonu, Ponu, Gonu]
```

### `groupingBy` — Group into a Map

```java
// Group employees by department
Map<String, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));

byDept.forEach((dept, emps) -> {
    System.out.println(dept + ": " + emps.stream()
                                         .map(Employee::getName)
                                         .collect(Collectors.joining(", ")));
});
// Engineering: Sonu, Monu
// HR: Tonu
// Finance: Ponu
// Operations: Gonu

// Count per department
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));

// Average salary per department
Map<String, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment,
             Collectors.averagingDouble(Employee::getSalary)));
```

### `partitioningBy` — Split into two groups

```java
// Split into senior (>80k) and junior
Map<Boolean, List<Employee>> partitioned = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.getSalary() > 80000));

System.out.println("Senior: " + partitioned.get(true).stream()
                                            .map(Employee::getName)
                                            .collect(Collectors.joining(", ")));
System.out.println("Junior: " + partitioned.get(false).stream()
                                            .map(Employee::getName)
                                            .collect(Collectors.joining(", ")));
// Senior: Monu, Ponu
// Junior: Sonu, Tonu, Gonu
```

### `count()` — Count elements

```java
long engineeringCount = employees.stream()
    .filter(e -> e.getDepartment().equals("Engineering"))
    .count();
System.out.println("Engineering headcount: " + engineeringCount);  // 2
```

### `reduce()` — Aggregate to a single value

```java
// Sum of all salaries
Optional<Double> totalOpt = employees.stream()
    .map(Employee::getSalary)
    .reduce((a, b) -> a + b);

// With identity value (no Optional needed)
double total = employees.stream()
    .mapToDouble(Employee::getSalary)
    .reduce(0, Double::sum);

// Find the highest-paid employee
Optional<Employee> topEarner = employees.stream()
    .reduce((a, b) -> a.getSalary() > b.getSalary() ? a : b);

topEarner.ifPresent(e ->
    System.out.println("Top earner: " + e.getName() + " " + e.getSalary()));
```

### `findFirst()` and `findAny()`

```java
Optional<Employee> first = employees.stream()
    .filter(e -> e.getDepartment().equals("HR"))
    .findFirst();

first.ifPresent(e -> System.out.println("First HR employee: " + e.getName()));
```

`findFirst()` — deterministic, returns first in encounter order.
`findAny()` — may return any match, potentially faster in parallel streams.

### `anyMatch`, `allMatch`, `noneMatch`

```java
boolean anyHighEarner  = employees.stream().anyMatch(e -> e.getSalary() > 90000);
boolean allActive      = employees.stream().allMatch(e -> e.getStatus() == EmployeeStatus.ACTIVE);
boolean noneTerminated = employees.stream().noneMatch(e -> e.getStatus() == EmployeeStatus.TERMINATED);

System.out.println("Any > 90k: "       + anyHighEarner);   // true
System.out.println("All active: "      + allActive);        // depends on data
System.out.println("None terminated: " + noneTerminated);   // depends on data
```

### `min()` and `max()`

```java
Optional<Employee> lowestPaid = employees.stream()
    .min(Comparator.comparing(Employee::getSalary));

Optional<Employee> highestPaid = employees.stream()
    .max(Comparator.comparing(Employee::getSalary));

lowestPaid.ifPresent(e ->
    System.out.println("Lowest: " + e.getName() + " " + e.getSalary()));
highestPaid.ifPresent(e ->
    System.out.println("Highest: " + e.getName() + " " + e.getSalary()));
```

### `toArray()`

```java
Employee[] array = employees.stream()
    .filter(e -> e.getDepartment().equals("Engineering"))
    .toArray(Employee[]::new);
```

---

## Statistics with `summarizingDouble`

```java
DoubleSummaryStatistics stats = employees.stream()
    .collect(Collectors.summarizingDouble(Employee::getSalary));

System.out.println("Count:   " + stats.getCount());
System.out.println("Sum:     " + stats.getSum());
System.out.println("Average: " + stats.getAverage());
System.out.println("Min:     " + stats.getMin());
System.out.println("Max:     " + stats.getMax());
```

Output:
```
Count:   5
Sum:     371000.0
Average: 74200.0
Min:     55000.0
Max:     91000.0
```

---

## Parallel Streams

Convert any stream to a parallel stream with `parallelStream()` or `.parallel()`. The stream is then split across multiple threads using the ForkJoinPool:

```java
// Sequential
double total = employees.stream()
    .mapToDouble(Employee::getSalary)
    .sum();

// Parallel — automatically uses multiple CPU cores
double totalParallel = employees.parallelStream()
    .mapToDouble(Employee::getSalary)
    .sum();
```

**When to use parallel streams:**
- Large data sets (thousands+ elements)
- Operations that are CPU-intensive and stateless
- Order does not matter (or you use `forEachOrdered`)

**When NOT to:**
- Small collections — thread overhead outweighs the gain
- Operations with shared mutable state — race conditions
- I/O-bound operations — threads block, no gain
- When order matters and you need `findFirst`, `limit` etc. — results may vary

```java
// Thread-safe accumulation with parallel
double totalSafe = employees.parallelStream()
    .mapToDouble(Employee::getSalary)
    .sum();   // sum() is safe for parallel

// UNSAFE — shared mutable state in parallel
List<String> names = new ArrayList<>();   // not thread-safe
employees.parallelStream()
    .map(Employee::getName)
    .forEach(names::add);   // race condition — do not do this
// Use collect() instead — always safe
List<String> safeNames = employees.parallelStream()
    .map(Employee::getName)
    .collect(Collectors.toList());   // Collectors are thread-safe
```

---

## Lazy Evaluation in Action

```java
// The pipeline is built but nothing runs yet
Stream<Employee> pipeline = employees.stream()
    .filter(e -> {
        System.out.println("filter: " + e.getName());
        return e.getSalary() > 70000;
    })
    .map(e -> {
        System.out.println("map: " + e.getName());
        return e;
    });

System.out.println("Pipeline built — nothing run yet.");

// Terminal operation triggers execution
pipeline.findFirst();   // only processes until the first match — then stops!
```

Output:
```
Pipeline built — nothing run yet.
filter: Sonu
map: Sonu
```

Only `Sonu` was processed — the first one to pass the filter. The rest were never touched. This is the power of lazy evaluation: `findFirst()` short-circuits the pipeline.

---

## Practical Example — Employee Analytics Report

```java
import java.util.*;
import java.util.stream.*;

public class EmployeeAnalytics {

    public static void main(String[] args) {

        List<Employee> employees = Arrays.asList(
            new Employee(101, "Sonu",  75000, "Engineering"),
            new Employee(102, "Monu",  82000, "Engineering"),
            new Employee(103, "Tonu",  55000, "HR"),
            new Employee(104, "Ponu",  91000, "Finance"),
            new Employee(105, "Gonu",  68000, "Operations"),
            new Employee(106, "Ronu",  78000, "Engineering"),
            new Employee(107, "Bonu",  61000, "HR"),
            new Employee(108, "Konu",  95000, "Finance")
        );

        // 1. Total payroll
        double totalPayroll = employees.stream()
            .mapToDouble(Employee::getSalary).sum();
        System.out.printf("Total payroll: %.2f%n%n", totalPayroll);

        // 2. Department headcount
        System.out.println("Headcount by department:");
        employees.stream()
            .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> System.out.printf("  %-15s %d%n", e.getKey(), e.getValue()));

        // 3. Average salary by department
        System.out.println("\nAverage salary by department:");
        employees.stream()
            .collect(Collectors.groupingBy(Employee::getDepartment,
                     Collectors.averagingDouble(Employee::getSalary)))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(e -> System.out.printf("  %-15s %.2f%n", e.getKey(), e.getValue()));

        // 4. Top 3 earners
        System.out.println("\nTop 3 earners:");
        employees.stream()
            .sorted(Comparator.comparing(Employee::getSalary).reversed())
            .limit(3)
            .forEach(e -> System.out.printf("  %-10s %.2f%n",
                                            e.getName(), e.getSalary()));

        // 5. Salary statistics
        DoubleSummaryStatistics stats = employees.stream()
            .collect(Collectors.summarizingDouble(Employee::getSalary));
        System.out.printf("%nSalary stats → Min: %.0f | Max: %.0f | Avg: %.0f%n",
                          stats.getMin(), stats.getMax(), stats.getAverage());

        // 6. All names sorted, comma-separated
        String nameList = employees.stream()
            .map(Employee::getName)
            .sorted()
            .collect(Collectors.joining(", "));
        System.out.println("\nAll employees: " + nameList);
    }
}
```

Output:
```
Total payroll: 605000.00

Headcount by department:
  Engineering     3
  HR              2
  Finance         2
  Operations      1

Average salary by department:
  Finance         93000.00
  Engineering     78333.33
  Operations      68000.00
  HR              58000.00

Top 3 earners:
  Konu       95000.00
  Ponu       91000.00
  Monu       82000.00

Salary stats → Min: 55000 | Max: 95000 | Avg: 75625

All employees: Bonu, Gonu, Konu, Monu, Ponu, Ronu, Sonu, Tonu
```

---

## Common Mistakes

```java
// Mistake 1: Reusing a consumed stream
Stream<Employee> s = employees.stream().filter(e -> e.getSalary() > 70000);
s.count();        // ok
s.forEach(...);   // IllegalStateException — stream already consumed

// Mistake 2: Modifying source inside stream
employees.stream()
    .forEach(e -> employees.remove(e));  // ConcurrentModificationException
// Collect to a list first, then modify source

// Mistake 3: Forgetting the terminal operation
employees.stream()
    .filter(e -> e.getSalary() > 80000)
    .map(Employee::getName);   // nothing happens — no terminal operation

// Mistake 4: Using forEach for accumulation
double total = 0;
employees.stream()
    .forEach(e -> total += e.getSalary());  // compile error — total must be final
// Use mapToDouble().sum() or reduce() instead
```

---

## Quick Reference

| Operation | Type | Method |
|-----------|------|--------|
| `filter` | Intermediate | `filter(Predicate)` |
| `map` | Intermediate | `map(Function)` |
| `mapToDouble/Int/Long` | Intermediate | `mapToDouble(ToDoubleFunction)` |
| `flatMap` | Intermediate | `flatMap(Function<T, Stream<R>>)` |
| `sorted` | Intermediate | `sorted(Comparator)` |
| `distinct` | Intermediate | `distinct()` |
| `limit` | Intermediate | `limit(long)` |
| `skip` | Intermediate | `skip(long)` |
| `peek` | Intermediate | `peek(Consumer)` |
| `forEach` | Terminal | `forEach(Consumer)` |
| `collect` | Terminal | `collect(Collector)` |
| `count` | Terminal | `count()` |
| `reduce` | Terminal | `reduce(BinaryOperator)` |
| `findFirst/Any` | Terminal | `findFirst()` / `findAny()` |
| `anyMatch/allMatch/noneMatch` | Terminal | `anyMatch(Predicate)` etc. |
| `min` / `max` | Terminal | `min(Comparator)` / `max(Comparator)` |
| `toArray` | Terminal | `toArray(IntFunction)` |
| `sum/average/min/max` | Terminal (primitive) | On `IntStream`/`DoubleStream` |

---

## What's Next

**Module 22** — Optional. Java's way of representing "a value that may or may not be present" — eliminating null checks and NullPointerExceptions from your APIs.
