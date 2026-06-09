# Module 22 — Optional

> **Part E: Java 8 Features**
> Prerequisites: Module 19, 20, 21 · Time: ~45 minutes

---

## The Null Problem

Tony Hoare, who invented the null reference in 1965, later called it his "billion-dollar mistake." In Java, `null` means "no value" — but it is also the source of the most common runtime error: `NullPointerException`.

```java
public Employee findById(int id) {
    // Returns null if not found — common pre-Java 8 pattern
    return database.get(id);
}

// Every caller must remember to check
Employee emp = service.findById(999);
System.out.println(emp.getName());   // NullPointerException if not found
```

The problem: `null` is a terrible way to represent "no value." It carries no information about *why* there is no value, it is easy to forget to check for it, and the error appears far from where the null was introduced.

**`Optional<T>`** is Java 8's answer: a container that explicitly represents the possible absence of a value.

---

## What Is Optional?

`Optional<T>` is a wrapper that either:
- **Contains a value** — `Optional.of("Sonu")`, `Optional.of(employee)`
- **Contains nothing** — `Optional.empty()`

It makes the possibility of absence explicit in the type system. A method returning `Optional<Employee>` tells callers directly: "this might not return an employee."

---

## Creating Optional

```java
// Optional with a value — value must not be null
Optional<String> name = Optional.of("Sonu");

// Optional.of(null) throws NullPointerException
Optional<String> bad = Optional.of(null);   // NPE immediately

// Optional that may or may not have a value — use when value might be null
String value = possiblyNull();
Optional<String> safe = Optional.ofNullable(value);   // empty if null, present if not

// Empty Optional — explicitly no value
Optional<Employee> noEmployee = Optional.empty();
```

---

## Checking and Getting the Value

### `isPresent()` and `isEmpty()`

```java
Optional<Employee> result = service.findById(101);

if (result.isPresent()) {
    System.out.println(result.get().getName());
}

if (result.isEmpty()) {   // Java 11+
    System.out.println("Employee not found.");
}
```

`get()` throws `NoSuchElementException` if the Optional is empty — always check `isPresent()` before calling `get()`. But as you'll see below, there are cleaner ways.

### `ifPresent(Consumer<T>)`

Run code only if a value is present:

```java
service.findById(101)
       .ifPresent(e -> System.out.println("Found: " + e.getName()));
```

No if-block. No null check. The code reads exactly like the intent.

### `ifPresentOrElse(Consumer, Runnable)` — Java 9+

```java
service.findById(999).ifPresentOrElse(
    e -> System.out.println("Found: " + e.getName()),
    () -> System.out.println("Employee not found.")
);
```

---

## Getting a Value with a Default

### `orElse(defaultValue)`

Returns the value if present, otherwise returns the default:

```java
Employee emp = service.findById(999)
                      .orElse(new Employee(0, "Guest", 0, "None"));

System.out.println(emp.getName());  // Guest
```

**Note:** `orElse` always evaluates its argument — the `new Employee(...)` is created even if the Optional has a value. If creation is expensive, use `orElseGet`.

### `orElseGet(Supplier<T>)`

Only evaluates the supplier if the Optional is empty — lazy evaluation:

```java
Employee emp = service.findById(999)
                      .orElseGet(() -> createDefaultEmployee());

// The createDefaultEmployee() is only called if findById returns empty
```

### `orElseThrow(Supplier<Exception>)` — Java 8

Throw an exception if empty:

```java
Employee emp = service.findById(999)
                      .orElseThrow(() -> new EmployeeNotFoundException(999));
```

### `orElseThrow()` — Java 10+

Throws `NoSuchElementException` without needing a supplier:

```java
Employee emp = service.findById(101).orElseThrow();
```

---

## Transforming Optional Values

This is where Optional shines — chaining operations without null checks.

### `map(Function<T, R>)`

If present, transform the value. If empty, return empty:

```java
Optional<String> dept = service.findById(101)
                               .map(Employee::getDepartment);

// Without Optional:
Employee e = service.findById(101);
String dept = (e != null) ? e.getDepartment() : null;
```

### `flatMap(Function<T, Optional<R>>)`

Used when the mapping function itself returns an Optional — avoids nested `Optional<Optional<T>>`:

```java
// Employee has an Optional<Department>
Optional<String> location = service.findById(101)
    .flatMap(e -> e.getDepartment())      // returns Optional<Department>
    .map(Department::getLocation);         // returns Optional<String>
```

### `filter(Predicate<T>)`

If present and matches the predicate, return it. Otherwise return empty:

```java
Optional<Employee> seniorEng = service.findById(101)
    .filter(e -> e.getDepartment().equals("Engineering"))
    .filter(e -> e.getSalary() > 80000);
```

---

## Chaining — Real Power

The real value of Optional is the ability to chain operations without explicit null checks at each step:

```java
// Without Optional — 3 null checks
public String getManagerName(int employeeId) {
    Employee emp = findById(employeeId);
    if (emp == null) return "Unknown";
    Department dept = emp.getDepartment();
    if (dept == null) return "Unknown";
    Employee manager = dept.getManager();
    if (manager == null) return "Unknown";
    return manager.getName();
}

// With Optional — clean chain
public String getManagerName(int employeeId) {
    return findById(employeeId)                     // Optional<Employee>
        .map(Employee::getDepartment)               // Optional<Department>
        .flatMap(Department::getManager)            // Optional<Employee>
        .map(Employee::getName)                     // Optional<String>
        .orElse("Unknown");                         // String
}
```

Five lines of null-safe code. The empty Optional propagates through the chain — if any step returns empty, the whole chain returns empty, and `orElse("Unknown")` kicks in.

---

## Optional in Method Signatures

### Return type — yes

```java
// Communicates clearly: employee may not exist
public Optional<Employee> findById(int id) {
    Employee emp = database.get(id);
    return Optional.ofNullable(emp);
}

public Optional<Employee> findByName(String name) {
    return employees.stream()
                    .filter(e -> e.getName().equals(name))
                    .findFirst();
}
```

### Method parameters — no

Do not use Optional as a method parameter. It is awkward and adds no clarity:

```java
// Bad
public void process(Optional<Employee> emp) { ... }

// Good — use method overloading or a null check
public void process(Employee emp) {
    if (emp == null) throw new IllegalArgumentException("Employee cannot be null");
    ...
}
```

### Fields — no

Do not use Optional as a field. It is not `Serializable` and adds overhead:

```java
// Bad
private Optional<String> middleName;

// Good — just use null or an empty string
private String middleName;   // null means no middle name
```

---

## `stream()` — Java 9+

Converts an Optional to a Stream of zero or one elements — useful when working in stream pipelines:

```java
List<Integer> ids = Arrays.asList(101, 102, 999, 103, 888);

List<Employee> found = ids.stream()
    .map(service::findById)           // Stream<Optional<Employee>>
    .flatMap(Optional::stream)        // Stream<Employee> — empties filtered out
    .collect(Collectors.toList());
```

Without this, you'd need to filter out empty Optionals manually.

---

## Practical Example — Employee Lookup Service

```java
import java.util.*;
import java.util.stream.*;

public class EmployeeLookupService {

    private final Map<Integer, Employee> store = new HashMap<>();

    public void add(Employee e) { store.put(e.getId(), e); }

    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Employee> findByName(String name) {
        return store.values().stream()
                    .filter(e -> e.getName().equalsIgnoreCase(name))
                    .findFirst();
    }

    public Optional<Employee> findTopEarnerIn(String department) {
        return store.values().stream()
                    .filter(e -> e.getDepartment().equals(department))
                    .max(Comparator.comparing(Employee::getSalary));
    }

    public static void main(String[] args) {

        EmployeeLookupService service = new EmployeeLookupService();
        service.add(new Employee(101, "Sonu",  75000, "Engineering"));
        service.add(new Employee(102, "Monu",  82000, "Engineering"));
        service.add(new Employee(103, "Tonu",  55000, "HR"));
        service.add(new Employee(104, "Ponu",  91000, "Finance"));
        service.add(new Employee(105, "Gonu",  68000, "Operations"));

        // findById — found
        service.findById(101)
               .ifPresent(e -> System.out.println("Found: " + e.getName()));

        // findById — not found
        String result = service.findById(999)
                               .map(Employee::getName)
                               .orElse("No employee with that ID.");
        System.out.println(result);

        // findByName with transformation
        double salary = service.findByName("Monu")
                               .map(Employee::getSalary)
                               .orElse(0.0);
        System.out.printf("Monu's salary: %.2f%n", salary);

        // findByName — not found, throw
        try {
            Employee ponu = service.findByName("Zonu")
                                   .orElseThrow(() -> new EmployeeNotFoundException("Zonu"));
        } catch (EmployeeNotFoundException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // Top earner in a department
        service.findTopEarnerIn("Engineering")
               .ifPresentOrElse(
                   e -> System.out.printf("Top Engineering earner: %s (%.0f)%n",
                                          e.getName(), e.getSalary()),
                   () -> System.out.println("No engineers found.")
               );

        // Batch lookup — filter out missing
        List<Integer> ids = Arrays.asList(101, 102, 999, 104, 888);
        List<String> foundNames = ids.stream()
            .map(service::findById)
            .flatMap(Optional::stream)
            .map(Employee::getName)
            .collect(Collectors.toList());
        System.out.println("Found: " + foundNames);
    }
}
```

Output:
```
Found: Sonu
No employee with that ID.
Monu's salary: 82000.00
Caught: Employee not found: Zonu
Top Engineering earner: Monu (82000)
Found: [Sonu, Monu, Ponu]
```

---

## Quick Summary

| Method | Behaviour |
|--------|-----------|
| `Optional.of(v)` | Create with non-null value — NPE if null |
| `Optional.ofNullable(v)` | Create — empty if null, present if not |
| `Optional.empty()` | Create empty Optional |
| `isPresent()` | true if has value |
| `isEmpty()` | true if empty (Java 11+) |
| `get()` | Returns value — exception if empty |
| `ifPresent(consumer)` | Run consumer if present |
| `ifPresentOrElse(c, r)` | Run consumer if present, runnable if not (Java 9+) |
| `orElse(default)` | Value or default — always evaluates default |
| `orElseGet(supplier)` | Value or supplier result — lazy |
| `orElseThrow(supplier)` | Value or throw |
| `map(fn)` | Transform value if present |
| `flatMap(fn)` | Transform when fn returns Optional |
| `filter(pred)` | Keep value only if predicate passes |
| `stream()` | 0 or 1 element stream (Java 9+) |

| Do | Don't |
|----|-------|
| Use as return type to signal possible absence | Use as method parameter |
| Chain with `map`/`flatMap`/`filter` | Use as class field |
| Use `orElseGet` for expensive defaults | Call `get()` without `isPresent()` |
| Use `ifPresentOrElse` for both branches | Use `Optional.of(null)` |

---

## What's Next

**Part F — Concurrency.** Module 23 covers Multithreading — writing programs that do multiple things at the same time.
