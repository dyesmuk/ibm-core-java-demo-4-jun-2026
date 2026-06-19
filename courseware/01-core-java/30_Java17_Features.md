# Module 30 — Java 17 Features

> **Part I: Modern Java**
> Prerequisites: Module 29 · Time: ~1 hour
> Java version required: JDK 17+

---

## About Java 17

Java 17 (September 2021) is the current most-widely-adopted LTS release. Many organisations are on Java 17 or migrating to it. It standardised features that had been in preview since Java 14/15/16:

| Feature | Preview Since | Standard In |
|---------|--------------|-------------|
| Records | Java 14 | **Java 16** |
| Pattern matching for `instanceof` | Java 14 | **Java 16** |
| Sealed classes | Java 15 | **Java 17** |
| Text blocks | Java 13 | Java 15 |

This module covers the three big structural features — records, sealed classes, and pattern matching — which together change how you model and process data in Java.

---

## Records (Java 16)

### The Problem

A common pattern in Java: a class that just holds data — an `id`, a `name`, a `salary`. Writing it properly requires a constructor, getters, `equals()`, `hashCode()`, and `toString()`. That is 40+ lines of boilerplate for what is conceptually a 4-field data carrier:

```java
// The old way — 40+ lines for a simple data class
public final class EmployeeRecord {
    private final int    id;
    private final String name;
    private final double salary;
    private final String department;

    public EmployeeRecord(int id, String name, double salary, String department) {
        this.id         = id;
        this.name       = name;
        this.salary     = salary;
        this.department = department;
    }

    public int    getId()         { return id; }
    public String getName()       { return name; }
    public double getSalary()     { return salary; }
    public String getDepartment() { return department; }

    @Override public boolean equals(Object o) { /* ... */ }
    @Override public int hashCode()           { /* ... */ }
    @Override public String toString()        { /* ... */ }
}
```

### Records — The Solution

```java
public record EmployeeRecord(int id, String name, double salary, String department) { }
```

One line. The compiler generates everything:
- A canonical constructor matching the parameters
- Private final fields for each component
- Public accessor methods: `id()`, `name()`, `salary()`, `department()`
- `equals()`, `hashCode()`, and `toString()` — all based on all components

```java
EmployeeRecord e1 = new EmployeeRecord(101, "Sonu", 75000.0, "Engineering");
EmployeeRecord e2 = new EmployeeRecord(101, "Sonu", 75000.0, "Engineering");

System.out.println(e1.name());        // Sonu     — accessor (not getName())
System.out.println(e1.salary());      // 75000.0
System.out.println(e1);              // EmployeeRecord[id=101, name=Sonu, salary=75000.0, department=Engineering]
System.out.println(e1.equals(e2));   // true — content equality
System.out.println(e1.hashCode() == e2.hashCode()); // true
```

### Records Are Immutable

All fields are `private final`. There are no setters. Once created, a record's state cannot change:

```java
e1.id = 999;    // compile error — field is final
```

This is intentional. Records model **values** — things that are defined by their data, not their identity. If you need to "change" a value, you create a new record:

```java
// Functional "update" — create a new record with changed salary
EmployeeRecord promoted = new EmployeeRecord(
    e1.id(), e1.name(), 85000.0, e1.department());
```

### Custom Compact Constructor

Validate or normalise data without restating the parameters:

```java
public record EmployeeRecord(int id, String name, double salary, String department) {

    // Compact constructor — no parameter list, fields assigned automatically after this block
    public EmployeeRecord {
        if (id <= 0)
            throw new IllegalArgumentException("ID must be positive: " + id);
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be blank.");
        if (salary < 0)
            throw new IllegalArgumentException("Salary cannot be negative.");
        name = name.strip();          // normalise — modifying the parameter before assignment
        department = department.strip();
    }
}
```

```java
EmployeeRecord bad = new EmployeeRecord(-1, "Sonu", 75000, "Eng");
// IllegalArgumentException: ID must be positive: -1
```

### Adding Methods to Records

Records can have instance methods, static methods, and static fields. They cannot have instance fields beyond the record components:

```java
public record EmployeeRecord(int id, String name, double salary, String department) {

    // Static constant
    public static final double MIN_SALARY = 15000.0;

    // Instance method
    public double calculateBonus(double percent) {
        return salary * (percent / 100);
    }

    public boolean isSenior() {
        return salary > 80000;
    }

    public String summary() {
        return String.format("[%d] %s — %s (%.0f)", id, name, department, salary);
    }

    // Static factory method
    public static EmployeeRecord of(String csv) {
        String[] p = csv.split(",");
        return new EmployeeRecord(
            Integer.parseInt(p[0].strip()),
            p[1].strip(),
            Double.parseDouble(p[2].strip()),
            p[3].strip());
    }
}
```

```java
EmployeeRecord e = EmployeeRecord.of("101, Sonu, 75000, Engineering");
System.out.println(e.summary());              // [101] Sonu — Engineering (75000)
System.out.println(e.calculateBonus(10));     // 7500.0
System.out.println(e.isSenior());             // false
```

### Records Implement Interfaces

```java
public interface Displayable {
    void display();
}

public record EmployeeRecord(int id, String name, double salary, String department)
        implements Displayable {

    @Override
    public void display() {
        System.out.printf("%-5d %-12s %-15s %10.2f%n", id, name, department, salary);
    }
}
```

### Records in Collections

Because `equals()` and `hashCode()` are correctly implemented, records work perfectly in sets, maps, and as map keys:

```java
Set<EmployeeRecord> unique = new HashSet<>();
unique.add(new EmployeeRecord(101, "Sonu", 75000, "Engineering"));
unique.add(new EmployeeRecord(101, "Sonu", 75000, "Engineering"));  // duplicate
System.out.println(unique.size());   // 1

Map<EmployeeRecord, String> roles = new HashMap<>();
roles.put(new EmployeeRecord(101, "Sonu", 75000, "Engineering"), "Tech Lead");
String role = roles.get(new EmployeeRecord(101, "Sonu", 75000, "Engineering"));
System.out.println(role);   // Tech Lead
```

### When to Use Records

| Use Records For | Don't Use Records For |
|----------------|-----------------------|
| Data transfer objects (DTOs) | Entities with mutable state |
| Value objects (coordinates, ranges, money) | Classes that need inheritance |
| Method return types carrying multiple values | JPA/Hibernate entities (need no-arg constructor + setters) |
| Immutable configuration holders | Classes with complex lifecycle |

---

## Sealed Classes (Java 17)

### The Problem

Without sealed classes, any class can be subclassed by anyone. If you have an `Employee` hierarchy and want to say "only `Manager`, `Developer`, and `Contractor` can be employees — nothing else", you cannot express this in the type system.

### Sealed Classes — Controlled Inheritance

A `sealed` class explicitly lists which classes may extend it:

```java
public sealed class Employee
        permits Manager, Developer, Contractor {

    private final int    id;
    private final String name;
    private final double salary;

    public Employee(int id, String name, double salary) {
        this.id     = id;
        this.name   = name;
        this.salary = salary;
    }

    public int    getId()     { return id; }
    public String getName()   { return name; }
    public double getSalary() { return salary; }
}
```

Each permitted subclass must be one of:
- `final` — cannot be extended further
- `sealed` — can be extended, but only by listed classes
- `non-sealed` — open to extension by anyone (explicitly opting out)

```java
public final class Manager extends Employee {
    private final int teamSize;

    public Manager(int id, String name, double salary, int teamSize) {
        super(id, name, salary);
        this.teamSize = teamSize;
    }

    public int getTeamSize() { return teamSize; }
}

public final class Developer extends Employee {
    private final String techStack;

    public Developer(int id, String name, double salary, String techStack) {
        super(id, name, salary);
        this.techStack = techStack;
    }

    public String getTechStack() { return techStack; }
}

public non-sealed class Contractor extends Employee {
    // non-sealed — can be extended freely
    public Contractor(int id, String name, double salary) {
        super(id, name, salary);
    }
}

// This would fail:
// public class Intern extends Employee { }   // compile error — not permitted
```

### Sealed Interfaces

Interfaces can also be sealed:

```java
public sealed interface Payable
        permits Employee, Vendor, Freelancer {
    double calculateNetPay();
}
```

### Why Sealed Classes Matter

The real power of sealed classes is in **exhaustive pattern matching**. Since the compiler knows all permitted subtypes, it can verify that a switch covers all cases — no `default` needed and no missed cases possible.

---

## Pattern Matching for `instanceof` (Java 16)

### The Old Way

Every `instanceof` check was followed by a cast:

```java
Object obj = getEmployee();

if (obj instanceof Manager) {
    Manager m = (Manager) obj;   // redundant cast
    System.out.println(m.getTeamSize());
}
```

### Pattern Matching — Check and Bind in One Step

```java
Object obj = getEmployee();

if (obj instanceof Manager m) {
    // m is already bound — no cast needed
    System.out.println(m.getTeamSize());
}

if (obj instanceof Developer d && d.getSalary() > 80000) {
    // d is bound and immediately usable in the condition
    System.out.println(d.getName() + " is a senior developer.");
}
```

The pattern variable `m` (or `d`) is:
- Only in scope where the match is guaranteed to be true
- Already the correct type — no cast
- `final` by convention (though not required)

### Negation Pattern

```java
if (!(obj instanceof Manager m)) {
    // m is NOT in scope here — the match failed
    System.out.println("Not a manager.");
    return;
}
// m IS in scope here — the match succeeded above
System.out.println(m.getTeamSize());
```

---

## Pattern Matching in `switch` (Java 17 Preview → Java 21 Standard)

Combining sealed classes with switch expressions gives you **exhaustive, type-safe dispatch** — the compiler verifies you have handled all cases:

```java
// Java 17 — preview feature (--enable-preview required)
// Java 21 — standard

public static String describeEmployee(Employee e) {
    return switch (e) {
        case Manager   m -> "Manager with team of " + m.getTeamSize();
        case Developer d -> "Developer working on " + d.getTechStack();
        case Contractor c -> "Contractor (daily rate basis)";
        // No default needed — Employee is sealed, all permitted types are covered
    };
}
```

```java
Employee[] team = {
    new Manager(201,   "Ponu", 110000, 8),
    new Developer(301, "Monu",  82000, "Java, Spring"),
    new Contractor(401,"Gonu",  0)
};

for (Employee e : team) {
    System.out.println(e.getName() + ": " + describeEmployee(e));
}
```

Output:
```
Ponu: Manager with team of 8
Monu: Developer working on Java, Spring
Gonu: Contractor (daily rate basis)
```

If you add a new permitted class (`Intern`) to `Employee` later and forget to add it to the switch, the compiler catches it immediately. This is the "sealed classes + pattern matching" combination — type-safe, exhaustive, zero `instanceof` chains.

---

## Other Java 17 Highlights

### Strong Encapsulation of JDK Internals

From Java 17, internal JDK APIs (`sun.*`, internal `com.sun.*`) are strongly encapsulated by default. Code that used to access `sun.misc.Unsafe` directly now gets `InaccessibleObjectException` at runtime.

Impact for you: if a library you use throws `InaccessibleObjectException` or module-access errors, it probably uses internal APIs and needs to be updated to a newer version.

### Restore Always-Strict Floating-Point (JEP 306)

All floating-point operations are now consistently `strictfp` by default. The `strictfp` keyword still exists but is now redundant — it was removed as a meaningful modifier.

### Random Number Generators (JEP 356)

A new hierarchy of interfaces for random number generation:

```java
import java.util.random.*;

RandomGenerator rng = RandomGenerator.of("Xoshiro256PlusPlus");
double randomSalary = 50000 + rng.nextDouble() * 100000;
System.out.printf("Random salary: %.2f%n", randomSalary);

// Splittable for parallel streams
SplittableRandom sr = new SplittableRandom();
int[] ids = sr.ints(5, 100, 200).toArray();   // 5 random ints between 100 and 200
System.out.println(Arrays.toString(ids));
```

---

## Practical Example — Records + Sealed Classes + Pattern Matching

```java
// Sealed hierarchy
public sealed class Employee permits FullTimeEmployee, PartTimeEmployee, Contractor { }

public record FullTimeEmployee(
    int id, String name, double salary, String department
) extends Employee { }

public record PartTimeEmployee(
    int id, String name, double hourlyRate, int hoursPerWeek
) extends Employee { }

public record Contractor(
    int id, String name, double dailyRate, String endDate
) extends Employee { }
```

```java
public class PayrollEngine {

    // Pattern matching switch — exhaustive, no default needed
    public static double calculateMonthlyPay(Employee e) {
        return switch (e) {
            case FullTimeEmployee fte  -> fte.salary();
            case PartTimeEmployee pte  -> pte.hourlyRate() * pte.hoursPerWeek() * 4;
            case Contractor       c    -> c.dailyRate() * 22;
        };
    }

    public static String describe(Employee e) {
        return switch (e) {
            case FullTimeEmployee fte -> String.format(
                "[FT]  %-10s %-15s Monthly: %,.2f",
                fte.name(), fte.department(), fte.salary());
            case PartTimeEmployee pte -> String.format(
                "[PT]  %-10s %.2f/hr × %d hrs/wk",
                pte.name(), pte.hourlyRate(), pte.hoursPerWeek());
            case Contractor c        -> String.format(
                "[CON] %-10s %.2f/day until %s",
                c.name(), c.dailyRate(), c.endDate());
        };
    }

    public static void main(String[] args) {

        List<Employee> workforce = List.of(
            new FullTimeEmployee(101, "Sonu",  75000, "Engineering"),
            new FullTimeEmployee(102, "Monu",  82000, "Finance"),
            new PartTimeEmployee(201, "Tonu",  500,   20),
            new PartTimeEmployee(202, "Ponu",  650,   16),
            new Contractor(301,       "Gonu",  2500,  "2025-12-31")
        );

        System.out.println("=== Workforce Report ===\n");
        workforce.forEach(e -> System.out.println(describe(e)));

        System.out.println("\n=== Monthly Payroll ===");
        double total = 0;
        for (Employee e : workforce) {
            double pay = calculateMonthlyPay(e);
            total += pay;
            System.out.printf("  %-10s %10,.2f%n", employeeName(e), pay);
        }
        System.out.printf("%n  %-10s %10,.2f%n", "TOTAL:", total);
    }

    // Pattern matching instanceof — no cast needed
    static String employeeName(Employee e) {
        if (e instanceof FullTimeEmployee fte)  return fte.name();
        if (e instanceof PartTimeEmployee pte)  return pte.name();
        if (e instanceof Contractor c)          return c.name();
        return "Unknown";
    }
}
```

Output:
```
=== Workforce Report ===

[FT]  Sonu       Engineering     Monthly: 75,000.00
[FT]  Monu       Finance         Monthly: 82,000.00
[PT]  Tonu       500.00/hr × 20 hrs/wk
[PT]  Ponu       650.00/hr × 16 hrs/wk
[CON] Gonu       2500.00/day until 2025-12-31

=== Monthly Payroll ===
  Sonu        75,000.00
  Monu        82,000.00
  Tonu        40,000.00
  Ponu        41,600.00
  Gonu        55,000.00

  TOTAL:     293,600.00
```

---

## Quick Summary

| Feature | Key Point |
|---------|-----------|
| Records | Immutable data classes — compiler generates constructor, accessors, equals/hashCode/toString |
| Record accessor | `name()` not `getName()` — no "get" prefix |
| Compact constructor | Validate/normalise without restating params — fields auto-assigned after |
| Sealed class | Lists exactly which classes may extend it — closed hierarchy |
| `permits` | Lists allowed subclasses — must be `final`, `sealed`, or `non-sealed` |
| `non-sealed` | Permitted subclass that reopens extension to anyone |
| Pattern matching `instanceof` | `obj instanceof Manager m` — check + bind in one step |
| Switch pattern matching | Match on type in switch — exhaustive with sealed hierarchies |
| Records extend sealed | Records can be `permits` targets — immutable sealed subtypes |

---

## What's Next

**Module 31** — Java 21 Features: virtual threads, record patterns, sequenced collections, and structured concurrency — the most significant Java release since Java 8.
