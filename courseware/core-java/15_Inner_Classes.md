# Module 15 — Inner Classes

> **Part D: Core Java Toolkit**
> Prerequisites: Module 08–13 · Time: ~1 hour

---

## What Are Inner Classes?

An inner class is a **class defined inside another class**. Java has four kinds:

| Kind | Where Defined | Has Access To | When to Use |
|------|--------------|---------------|-------------|
| Regular inner class | Inside a class body | All outer class members | Helper tightly coupled to outer class |
| Static nested class | Inside a class body, with `static` | Only static outer members | Logically grouped but independent helper |
| Method-local inner class | Inside a method body | Local `final`/effectively-final variables | Narrow, one-method use case |
| Anonymous inner class | Inline, at point of use | Local `final`/effectively-final variables | One-off implementation of interface or abstract class |

---

## Regular Inner Class

Defined inside the outer class body without `static`. It has access to **all** members of the outer class — including `private` ones.

```java
public class Department {

    private String name;
    private double budget;

    public Department(String name, double budget) {
        this.name   = name;
        this.budget = budget;
    }

    // ── Regular inner class ───────────────────────────────────────────
    public class BudgetReport {

        private String reportedBy;

        public BudgetReport(String reportedBy) {
            this.reportedBy = reportedBy;
        }

        public void print() {
            // Directly accesses outer class private fields — no getter needed
            System.out.println("=== Budget Report ===");
            System.out.println("Department  : " + name);          // outer field
            System.out.println("Budget      : " + budget);        // outer field
            System.out.println("Reported by : " + reportedBy);
        }
    }

    public String getName()   { return name; }
    public double getBudget() { return budget; }
}
```

### Creating an Inner Class Object

An inner class object **always requires an outer class object**:

```java
// Step 1: create outer object
Department dept = new Department("Engineering", 500000.0);

// Step 2: create inner object via outer object
Department.BudgetReport report = dept.new BudgetReport("Ponu");

report.print();
```

Output:
```
=== Budget Report ===
Department  : Engineering
Budget      : 500000.0
Reported by : Ponu
```

### Referencing Outer Class from Inner Class

If a field name is shadowed, use `OuterClass.this.fieldName`:

```java
public class Department {
    private String name = "Engineering";

    public class BudgetReport {
        private String name = "Q1 Report";   // shadows outer 'name'

        public void print() {
            System.out.println(name);               // Q1 Report — inner class field
            System.out.println(Department.this.name); // Engineering — outer class field
        }
    }
}
```

---

## Static Nested Class

Defined inside a class with the `static` modifier. It does **not** hold a reference to an outer class instance — it is just logically grouped with the outer class.

```java
public class Employee {

    private int    id;
    private String name;
    private double salary;

    public Employee(int id, String name, double salary) {
        this.id     = id;
        this.name   = name;
        this.salary = salary;
    }

    public int    getId()     { return id; }
    public String getName()   { return name; }
    public double getSalary() { return salary; }

    // ── Static nested class ───────────────────────────────────────────
    public static class PayrollSummary {

        private int    count;
        private double totalSalary;

        public void add(Employee e) {
            count++;
            totalSalary += e.getSalary();   // accesses via getter — no outer instance
        }

        public void print() {
            System.out.println("Employees   : " + count);
            System.out.printf("Total payroll: %.2f%n", totalSalary);
            System.out.printf("Average      : %.2f%n",
                              count > 0 ? totalSalary / count : 0);
        }
    }
}
```

### Creating a Static Nested Class Object

No outer class object required:

```java
// Create directly using the outer class name
Employee.PayrollSummary summary = new Employee.PayrollSummary();

summary.add(new Employee(101, "Sonu",  75000));
summary.add(new Employee(102, "Monu",  82000));
summary.add(new Employee(103, "Tonu",  55000));
summary.add(new Employee(104, "Ponu",  91000));
summary.add(new Employee(105, "Gonu",  68000));

summary.print();
```

Output:
```
Employees   : 5
Total payroll: 371000.00
Average      : 74200.00
```

### Static Nested vs Regular Inner — Key Difference

| Aspect | Regular Inner | Static Nested |
|--------|--------------|---------------|
| Outer class instance required? | Yes — always | No |
| Access to outer instance fields? | Yes — directly | No — only via a reference |
| Memory | Holds implicit reference to outer | Independent |
| Use case | Needs outer object's state | Logically grouped, but independent |

---

## Method-Local Inner Class

Defined inside a method. Only visible within that method. Can access local variables that are `final` or **effectively final** (not modified after assignment).

```java
public class PayrollProcessor {

    public void processForDepartment(String deptName, double[] salaries) {

        final double TAX_RATE = 0.10;

        // ── Method-local inner class ──────────────────────────────────
        class SalaryFormatter {
            public void print(int index, double gross) {
                double net = gross - (gross * TAX_RATE);   // accesses TAX_RATE
                System.out.printf("  Employee %-3d │ Gross: %9.2f │ Net: %9.2f%n",
                                  index + 1, gross, net);
            }
        }

        System.out.println("Department: " + deptName);
        System.out.println("-".repeat(48));
        SalaryFormatter formatter = new SalaryFormatter();
        for (int i = 0; i < salaries.length; i++) {
            formatter.print(i, salaries[i]);
        }
    }
}
```

```java
PayrollProcessor processor = new PayrollProcessor();
processor.processForDepartment("Engineering",
        new double[]{75000, 82000, 55000});
```

Output:
```
Department: Engineering
------------------------------------------------
  Employee 1   │ Gross:  75000.00 │ Net:  67500.00
  Employee 2   │ Gross:  82000.00 │ Net:  73800.00
  Employee 3   │ Gross:  55000.00 │ Net:  49500.00
```

Method-local inner classes are rarely used in modern Java — lambdas (Module 19) handle most of these cases more cleanly. They are still useful when you need multiple methods in a one-off helper.

---

## Anonymous Inner Class

An anonymous inner class has **no name**. It is declared and instantiated in a single expression. Used to provide a one-off implementation of an interface or abstract class.

### Implementing an Interface Anonymously

```java
public interface Printable {
    void print();
}
```

```java
// Named class approach
public class EmployeeReport implements Printable {
    @Override
    public void print() {
        System.out.println("Employee report printed.");
    }
}
Printable p = new EmployeeReport();
p.print();

// Anonymous class approach — no separate class file needed
Printable p = new Printable() {
    @Override
    public void print() {
        System.out.println("Employee report printed (anonymous).");
    }
};
p.print();
```

The `new Printable() { ... }` syntax means: "create an object that implements `Printable`, using this body as the implementation."

### Real Use — Sorting with `Comparator`

Before Java 8, anonymous classes were the standard way to provide custom sort logic:

```java
import java.util.*;

Employee[] employees = {
    new Employee(101, "Ponu",  91000, "Finance"),
    new Employee(102, "Sonu",  75000, "Engineering"),
    new Employee(103, "Gonu",  68000, "Operations"),
    new Employee(104, "Monu",  82000, "Engineering"),
    new Employee(105, "Tonu",  55000, "HR"),
};

// Sort by salary descending — anonymous Comparator
Arrays.sort(employees, new Comparator<Employee>() {
    @Override
    public int compare(Employee a, Employee b) {
        return Double.compare(b.getSalary(), a.getSalary());
    }
});

for (Employee e : employees) {
    System.out.printf("%-10s %.2f%n", e.getName(), e.getSalary());
}
```

Output:
```
Ponu       91000.00
Monu       82000.00
Sonu       75000.00
Gonu       68000.00
Tonu       55000.00
```

### The Lambda Equivalent (Java 8+)

The anonymous class above is verbose for what it does. Java 8 lambdas replace it cleanly:

```java
// Same sort, with lambda (Module 19)
Arrays.sort(employees,
    (a, b) -> Double.compare(b.getSalary(), a.getSalary()));
```

Anonymous classes are still needed when:
- The interface has **more than one method** (lambdas only work for single-method interfaces)
- You need to maintain **state** (instance fields) in the implementation
- You are implementing an **abstract class**, not an interface

---

## Practical Example — All Four Types Together

```java
public class InnerClassDemo {

    // Static nested class
    public static class EmployeeStats {
        private double max = Double.MIN_VALUE;
        private double min = Double.MAX_VALUE;
        private double sum = 0;
        private int    count = 0;

        public void record(double salary) {
            if (salary > max) max = salary;
            if (salary < min) min = salary;
            sum += salary;
            count++;
        }

        public void report() {
            System.out.printf("Count: %d | Total: %.2f | Max: %.2f | Min: %.2f%n",
                              count, sum, max, min);
        }
    }

    // Regular inner class
    public class AuditLog {
        private List<String> entries = new ArrayList<>();

        public void log(String message) {
            entries.add("[" + java.time.LocalTime.now() + "] " + message);
        }

        public void printAll() {
            entries.forEach(System.out::println);
        }
    }

    public void run() {
        double[] salaries = {75000, 82000, 55000, 91000, 68000};
        String[] names    = {"Sonu", "Monu", "Tonu", "Ponu", "Gonu"};

        // Static nested — no outer instance needed
        EmployeeStats stats = new EmployeeStats();

        // Regular inner — needs outer instance
        AuditLog log = this.new AuditLog();

        // Method-local inner class
        class SalaryBand {
            public String classify(double salary) {
                return salary >= 80000 ? "Senior" : salary >= 65000 ? "Mid" : "Junior";
            }
        }
        SalaryBand bander = new SalaryBand();

        // Anonymous class
        Runnable printHeader = new Runnable() {
            @Override
            public void run() {
                System.out.printf("%-10s %12s %10s%n", "Name", "Salary", "Band");
                System.out.println("-".repeat(35));
            }
        };

        printHeader.run();

        for (int i = 0; i < names.length; i++) {
            stats.record(salaries[i]);
            log.log(names[i] + " processed");
            System.out.printf("%-10s %12.2f %10s%n",
                              names[i], salaries[i], bander.classify(salaries[i]));
        }

        System.out.println();
        stats.report();
        System.out.println();
        log.printAll();
    }

    public static void main(String[] args) {
        new InnerClassDemo().run();
    }
}
```

Output:
```
Name            Salary       Band
-----------------------------------
Sonu          75000.00        Mid
Monu          82000.00     Senior
Tonu          55000.00     Junior
Ponu          91000.00     Senior
Gonu          68000.00        Mid

Count: 5 | Total: 371000.00 | Max: 91000.00 | Min: 55000.00

[10:23:15.441] Sonu processed
[10:23:15.441] Monu processed
[10:23:15.442] Tonu processed
[10:23:15.442] Ponu processed
[10:23:15.442] Gonu processed
```

---

## Quick Summary

| Type | Keyword | Outer Instance? | Access | Use When |
|------|---------|----------------|--------|----------|
| Regular inner | none | Required | All outer members | Tightly coupled helper |
| Static nested | `static` | Not needed | Outer static only | Logically grouped, independent |
| Method-local | none (in method) | Depends | Effectively-final locals | One-method helper with multiple methods |
| Anonymous | none (inline) | Depends | Effectively-final locals | One-off interface/abstract class implementation |

### Modern Perspective

| Old Way | Modern Replacement |
|---------|-------------------|
| Anonymous `Runnable` | Lambda: `() -> { ... }` |
| Anonymous `Comparator` | Lambda: `(a, b) -> ...` |
| Anonymous single-method interface | Lambda (Module 19) |
| Method-local class with one method | Lambda |

Anonymous and method-local inner classes are less common in new Java 8+ code. Regular inner classes and static nested classes remain widely used.

---

## What's Next

**Module 16** — Enums. Type-safe constants, far more powerful than int or String constants, with methods, fields, and switch support.
