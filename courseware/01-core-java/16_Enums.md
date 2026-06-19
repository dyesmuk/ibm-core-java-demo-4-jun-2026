# Module 16 — Enums

> **Part D: Core Java Toolkit**
> Prerequisites: Module 08, 09 · Time: ~1 hour

---

## The Problem with Constants

Before enums, developers used `int` or `String` constants to represent fixed sets of values:

```java
// The old way — int constants
public class EmployeeStatus {
    public static final int ACTIVE     = 1;
    public static final int INACTIVE   = 2;
    public static final int TERMINATED = 3;
    public static final int ON_LEAVE   = 4;
}

// Usage
int status = EmployeeStatus.ACTIVE;

if (status == EmployeeStatus.ACTIVE) {
    // ...
}
```

This has real problems:
- **No type safety** — `status = 99` compiles fine, but 99 is not a valid status
- **No meaningful print** — printing `status` gives `1`, not `"ACTIVE"`
- **No behaviour** — you cannot attach methods to an int constant
- **Namespace pollution** — constants float around without a proper type

Enums solve all of this.

---

## Basic Enum

```java
public enum EmployeeStatus {
    ACTIVE,
    INACTIVE,
    TERMINATED,
    ON_LEAVE
}
```

That is it. Now `EmployeeStatus` is a **type** — the only valid values are the four listed. Nothing else compiles.

```java
EmployeeStatus status = EmployeeStatus.ACTIVE;

// Type-safe — this won't compile:
// EmployeeStatus bad = 99;      // compile error
// EmployeeStatus bad = "ACTIVE"; // compile error

System.out.println(status);          // ACTIVE — readable by default
System.out.println(status.name());   // ACTIVE — same
System.out.println(status.ordinal()); // 0 — zero-based position in declaration
```

---

## Enum in `switch`

Enums work naturally with switch — and it is the clearest use:

```java
EmployeeStatus status = EmployeeStatus.ON_LEAVE;

switch (status) {
    case ACTIVE:
        System.out.println("Employee is active — full access granted.");
        break;
    case ON_LEAVE:
        System.out.println("Employee on leave — read-only access.");
        break;
    case INACTIVE:
    case TERMINATED:
        System.out.println("Employee not active — access denied.");
        break;
}
```

With Java 14+ arrow switch (cleaner):

```java
String access = switch (status) {
    case ACTIVE     -> "Full access";
    case ON_LEAVE   -> "Read-only";
    case INACTIVE,
         TERMINATED -> "No access";
};
System.out.println(access);
```

---

## Enum with Fields and Methods

An enum is a class under the hood. Each constant can carry data and behaviour:

```java
public enum Department {

    ENGINEERING ("Engineering",  "Pune",    500000.0),
    HR          ("Human Res.",   "Mumbai",  200000.0),
    FINANCE     ("Finance",      "Delhi",   300000.0),
    OPERATIONS  ("Operations",   "Pune",    250000.0);

    // ── Fields ────────────────────────────────────────────────────────
    private final String displayName;
    private final String location;
    private final double budget;

    // ── Constructor ───────────────────────────────────────────────────
    Department(String displayName, String location, double budget) {
        this.displayName = displayName;
        this.location    = location;
        this.budget      = budget;
    }

    // ── Methods ───────────────────────────────────────────────────────
    public String getDisplayName() { return displayName; }
    public String getLocation()    { return location; }
    public double getBudget()      { return budget; }

    public boolean isInCity(String city) {
        return this.location.equalsIgnoreCase(city);
    }

    public String budgetCategory() {
        if (budget >= 400000) return "High";
        if (budget >= 250000) return "Medium";
        return "Low";
    }
}
```

```java
Department dept = Department.ENGINEERING;

System.out.println(dept.getDisplayName());   // Engineering
System.out.println(dept.getLocation());      // Pune
System.out.println(dept.getBudget());        // 500000.0
System.out.println(dept.budgetCategory());   // High
System.out.println(dept.isInCity("Pune"));   // true

// Iterate over all enum constants
for (Department d : Department.values()) {
    System.out.printf("%-15s %-10s %-10s %10.0f%n",
                      d.getDisplayName(), d.getLocation(),
                      d.budgetCategory(), d.getBudget());
}
```

Output:
```
Engineering     Pune       High          500000
Human Res.      Mumbai     Medium        200000
Finance         Delhi      Medium        300000
Operations      Pune       Medium        250000
```

---

## Enum with Abstract Methods

Each constant can override an abstract method — giving each its own behaviour:

```java
public enum JobGrade {

    JUNIOR {
        @Override
        public double applyBonus(double salary) { return salary * 0.05; }

        @Override
        public String describe() { return "Junior (0-2 yrs)"; }
    },

    MID {
        @Override
        public double applyBonus(double salary) { return salary * 0.10; }

        @Override
        public String describe() { return "Mid-level (2-5 yrs)"; }
    },

    SENIOR {
        @Override
        public double applyBonus(double salary) { return salary * 0.20; }

        @Override
        public String describe() { return "Senior (5+ yrs)"; }
    };

    public abstract double applyBonus(double salary);
    public abstract String describe();
}
```

```java
double salary = 75000;

for (JobGrade grade : JobGrade.values()) {
    System.out.printf("%-10s %-20s Bonus: %.2f%n",
                      grade, grade.describe(), grade.applyBonus(salary));
}
```

Output:
```
JUNIOR     Junior (0-2 yrs)     Bonus: 3750.00
MID        Mid-level (2-5 yrs)  Bonus: 7500.00
SENIOR     Senior (5+ yrs)      Bonus: 15000.00
```

---

## Built-in Enum Methods

```java
// values() — all constants as an array
EmployeeStatus[] all = EmployeeStatus.values();

// valueOf() — get constant from its name string
EmployeeStatus s = EmployeeStatus.valueOf("ACTIVE");   // EmployeeStatus.ACTIVE
// throws IllegalArgumentException if name doesn't match

// name() — constant name as String
System.out.println(EmployeeStatus.ACTIVE.name());      // "ACTIVE"

// ordinal() — zero-based position
System.out.println(EmployeeStatus.TERMINATED.ordinal()); // 2

// compareTo() — compares by ordinal
System.out.println(EmployeeStatus.ACTIVE.compareTo(EmployeeStatus.TERMINATED)); // negative

// toString() — same as name() by default, can be overridden
System.out.println(EmployeeStatus.ON_LEAVE);            // ON_LEAVE
```

---

## `EnumSet` — Efficient Set of Enum Constants

`EnumSet` is a high-performance `Set` implementation for enum types. Internally backed by a bit vector — extremely fast:

```java
import java.util.EnumSet;

// All constants
EnumSet<EmployeeStatus> allStatuses = EnumSet.allOf(EmployeeStatus.class);

// Specific constants
EnumSet<EmployeeStatus> activeStatuses = EnumSet.of(
        EmployeeStatus.ACTIVE,
        EmployeeStatus.ON_LEAVE);

// Range
EnumSet<EmployeeStatus> range = EnumSet.range(
        EmployeeStatus.ACTIVE,
        EmployeeStatus.ON_LEAVE);

// Complement — everything NOT in a set
EnumSet<EmployeeStatus> inactive = EnumSet.complementOf(activeStatuses);

// Usage
EmployeeStatus current = EmployeeStatus.ON_LEAVE;
if (activeStatuses.contains(current)) {
    System.out.println("Employee has some level of access.");
}
```

---

## `EnumMap` — Map with Enum Keys

`EnumMap` is a `Map` implementation optimised for enum keys — faster than `HashMap` for enum keys:

```java
import java.util.EnumMap;
import java.util.ArrayList;
import java.util.List;

EnumMap<Department, List<String>> deptEmployees = new EnumMap<>(Department.class);

// Group employees by department
deptEmployees.put(Department.ENGINEERING, new ArrayList<>(List.of("Sonu", "Monu")));
deptEmployees.put(Department.HR,          new ArrayList<>(List.of("Tonu")));
deptEmployees.put(Department.FINANCE,     new ArrayList<>(List.of("Ponu")));
deptEmployees.put(Department.OPERATIONS,  new ArrayList<>(List.of("Gonu")));

// Print
deptEmployees.forEach((dept, names) ->
    System.out.printf("%-15s %s%n", dept.getDisplayName(), names));
```

Output:
```
Engineering     [Sonu, Monu]
Human Res.      [Tonu]
Finance         [Ponu]
Operations      [Gonu]
```

---

## Enum vs Constants — Why Enum Wins

| Aspect | `static final int/String` | Enum |
|--------|--------------------------|------|
| Type safety | No — any int is accepted | Yes — only declared constants valid |
| Readable print | No — prints 1, 2, 3 | Yes — prints ACTIVE, INACTIVE |
| Switch support | Yes | Yes — and cleaner |
| Methods and fields | No | Yes |
| Iteration | Manual array | `values()` |
| `null` safety | No | Each constant is non-null singleton |
| Singleton guarantee | No | Yes — each constant is exactly one instance |

---

## Practical Example — Employee with Enum Fields

```java
public class Employee {

    private int            id;
    private String         name;
    private double         salary;
    private Department     department;
    private EmployeeStatus status;
    private JobGrade       grade;

    public Employee(int id, String name, double salary,
                    Department dept, EmployeeStatus status, JobGrade grade) {
        this.id         = id;
        this.name       = name;
        this.salary     = salary;
        this.department = dept;
        this.status     = status;
        this.grade      = grade;
    }

    public double calculateBonus() {
        if (status != EmployeeStatus.ACTIVE) return 0;
        return grade.applyBonus(salary);
    }

    public void display() {
        System.out.printf("%-5d %-10s %-15s %-10s %-10s  Bonus: %8.2f%n",
                          id, name, department.getDisplayName(),
                          status, grade, calculateBonus());
    }
}
```

```java
public class EnumDemo {
    public static void main(String[] args) {

        Employee[] employees = {
            new Employee(101, "Sonu",  75000, Department.ENGINEERING,
                         EmployeeStatus.ACTIVE,     JobGrade.MID),
            new Employee(102, "Monu",  82000, Department.ENGINEERING,
                         EmployeeStatus.ACTIVE,     JobGrade.SENIOR),
            new Employee(103, "Tonu",  55000, Department.HR,
                         EmployeeStatus.ON_LEAVE,   JobGrade.JUNIOR),
            new Employee(104, "Ponu", 110000, Department.FINANCE,
                         EmployeeStatus.ACTIVE,     JobGrade.SENIOR),
            new Employee(105, "Gonu",  68000, Department.OPERATIONS,
                         EmployeeStatus.TERMINATED, JobGrade.MID),
        };

        System.out.printf("%-5s %-10s %-15s %-10s %-10s  %s%n",
                          "ID", "Name", "Department", "Status", "Grade", "Bonus");
        System.out.println("=".repeat(72));

        double totalBonus = 0;
        for (Employee e : employees) {
            e.display();
            totalBonus += e.calculateBonus();
        }

        System.out.println("=".repeat(72));
        System.out.printf("Total bonus payout: %.2f%n", totalBonus);
    }
}
```

Output:
```
ID    Name       Department      Status     Grade       Bonus
========================================================================
101   Sonu       Engineering     ACTIVE     MID         Bonus:  7500.00
102   Monu       Engineering     ACTIVE     SENIOR      Bonus: 16400.00
103   Tonu       Human Res.      ON_LEAVE   JUNIOR      Bonus:     0.00
104   Ponu       Finance         ACTIVE     SENIOR      Bonus: 22000.00
105   Gonu       Operations      TERMINATED MID         Bonus:     0.00
========================================================================
Total bonus payout: 45900.00
```

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Basic enum | Type-safe named constants — only declared values are valid |
| `values()` | Returns all constants as array |
| `valueOf(String)` | Gets constant by name — throws exception if not found |
| `name()` / `ordinal()` | Name string and position index |
| Enum with fields | Add constructor, fields, getters — each constant carries data |
| Enum with abstract method | Each constant provides its own implementation |
| `EnumSet` | Fast set for enum constants — prefer over `HashSet<EnumType>` |
| `EnumMap` | Fast map with enum keys — prefer over `HashMap<EnumType, V>` |

---

## What's Next

**Module 17** — Exception Handling. Dealing with things that go wrong at runtime — gracefully, informatively, and without crashing the application.
