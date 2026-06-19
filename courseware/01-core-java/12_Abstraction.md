# Module 12 — Abstraction

> **Part C: Object-Oriented Programming**
> Prerequisites: Module 10, 11 · Time: ~1.5 hours

---

## What Is Abstraction?

Abstraction means **defining what something does, without defining how it does it**. You expose the interface — the contract — and let subclasses fill in the implementation details.

Real-world analogy: A job posting says "Developer must be able to write code, review PRs, and fix bugs." It doesn't say *how* — that depends on the person hired. The job spec is the abstraction.

Java has two tools for abstraction:

| Tool | Description |
|------|-------------|
| `abstract` class | Partially implemented — has both abstract and concrete methods |
| `interface` | Fully abstract contract — defines what, never how (until Java 8 defaults) |

---

## Abstract Classes

### The `abstract` keyword

An abstract class:
- **Cannot be instantiated** — you cannot do `new Employee()` if `Employee` is abstract
- Can have **abstract methods** — declared but not implemented
- Can have **concrete methods** — fully implemented and inherited
- Can have **fields, constructors, static members** — everything a regular class can have

```java
package com.ems.bean;

public abstract class Employee {

    private int    id;
    private String name;
    private double salary;
    private String department;

    public Employee(int id, String name, double salary, String department) {
        this.id         = id;
        this.name       = name;
        this.salary     = salary;
        this.department = department;
    }

    // ── Abstract methods — MUST be implemented by subclasses ──────────
    public abstract double calculateBonus();
    public abstract String getRole();

    // ── Concrete methods — inherited as-is ───────────────────────────
    public void applyRaise(double percent) {
        this.salary = this.salary * (1 + percent / 100);
    }

    public void display() {
        System.out.printf("%-5d %-12s %-15s %-15s %10.2f%n",
                          id, name, getRole(), department, salary);
    }

    // Getters
    public int    getId()         { return id; }
    public String getName()       { return name; }
    public double getSalary()     { return salary; }
    public String getDepartment() { return department; }
    public void   setSalary(double s) { this.salary = s; }
}
```

Any class that extends `Employee` **must** implement `calculateBonus()` and `getRole()`. If it doesn't, it too becomes abstract.

### Concrete Subclasses

```java
public class Manager extends Employee {

    private int teamSize;

    public Manager(int id, String name, double salary,
                   String department, int teamSize) {
        super(id, name, salary, department);
        this.teamSize = teamSize;
    }

    @Override
    public double calculateBonus() {
        return getSalary() * 0.20 + (teamSize * 500);   // 20% + team bonus
    }

    @Override
    public String getRole() { return "Manager"; }

    public int getTeamSize() { return teamSize; }
}
```

```java
public class Developer extends Employee {

    private String techStack;
    private int    experienceYears;

    public Developer(int id, String name, double salary,
                     String department, String techStack, int experienceYears) {
        super(id, name, salary, department);
        this.techStack       = techStack;
        this.experienceYears = experienceYears;
    }

    @Override
    public double calculateBonus() {
        double base = getSalary() * 0.15;
        return experienceYears > 5 ? base * 1.25 : base;   // seniority bonus
    }

    @Override
    public String getRole() { return "Developer"; }

    public String getTechStack() { return techStack; }
}
```

```java
public class Contractor extends Employee {

    private double dailyRate;

    public Contractor(int id, String name, String department, double dailyRate) {
        super(id, name, 0, department);   // salary not applicable
        this.dailyRate = dailyRate;
    }

    @Override
    public double calculateBonus() {
        return 0;   // contractors don't get bonuses
    }

    @Override
    public String getRole() { return "Contractor"; }

    public double getDailyRate() { return dailyRate; }
}
```

---

## Abstract Class in Action

```java
public class AbstractDemo {
    public static void main(String[] args) {

        // Employee e = new Employee(...);   // compile error — abstract class

        Employee[] team = {
            new Manager(201,   "Ponu",  110000, "Engineering", 8),
            new Developer(301, "Monu",   82000, "Engineering", "Java, Spring", 6),
            new Developer(302, "Tonu",   78000, "Engineering", "Python", 3),
            new Contractor(401,"Gonu",   "Finance", 1500),
        };

        System.out.printf("%-5s %-12s %-15s %-15s %10s %12s%n",
                          "ID", "Name", "Role", "Department", "Salary", "Bonus");
        System.out.println("=".repeat(75));

        double totalBonus = 0;
        for (Employee e : team) {
            double bonus = e.calculateBonus();
            totalBonus += bonus;
            System.out.printf("%-5d %-12s %-15s %-15s %10.2f %12.2f%n",
                              e.getId(), e.getName(), e.getRole(),
                              e.getDepartment(), e.getSalary(), bonus);
        }

        System.out.println("=".repeat(75));
        System.out.printf("%-53s %12.2f%n", "Total Bonus Payout:", totalBonus);
    }
}
```

Output:
```
ID    Name         Role            Department        Salary        Bonus
===========================================================================
201   Ponu         Manager         Engineering    110000.00     26000.00
301   Monu         Developer       Engineering     82000.00     15375.00
302   Tonu         Developer       Engineering     78000.00     11700.00
401   Gonu         Contractor      Finance              0.00         0.00
===========================================================================
Total Bonus Payout:                                             53075.00
```

---

## Interfaces

An interface is a **pure contract** — it declares what a class must do, with no implementation (before Java 8).

Key differences from abstract classes:
- All methods are implicitly `public abstract` (unless `default` or `static` in Java 8+)
- All fields are implicitly `public static final` (constants)
- A class `implements` an interface (not `extends`)
- A class can implement **multiple interfaces** — solving the multiple inheritance problem
- No constructors — cannot be instantiated

### Defining an Interface

```java
package com.ems.service;

public interface Payable {
    double calculateNetPay();        // implicitly public abstract
    void   generatePayslip();        // implicitly public abstract
    double TAX_RATE = 0.10;          // implicitly public static final
}
```

```java
public interface Reviewable {
    void   conductReview(String comments);
    String getLastReviewSummary();
}
```

### Implementing an Interface

```java
public class Manager extends Employee implements Payable, Reviewable {

    private int    teamSize;
    private String lastReview;

    public Manager(int id, String name, double salary,
                   String department, int teamSize) {
        super(id, name, salary, department);
        this.teamSize = teamSize;
    }

    // From Employee (abstract)
    @Override public double calculateBonus() { return getSalary() * 0.20; }
    @Override public String getRole()        { return "Manager"; }

    // From Payable interface
    @Override
    public double calculateNetPay() {
        return getSalary() + calculateBonus() - (getSalary() * Payable.TAX_RATE);
    }

    @Override
    public void generatePayslip() {
        System.out.printf("--- Payslip: %s ---%n", getName());
        System.out.printf("Gross Salary: %.2f%n", getSalary());
        System.out.printf("Bonus:        %.2f%n", calculateBonus());
        System.out.printf("Tax:          %.2f%n", getSalary() * Payable.TAX_RATE);
        System.out.printf("Net Pay:      %.2f%n", calculateNetPay());
    }

    // From Reviewable interface
    @Override
    public void conductReview(String comments) {
        this.lastReview = comments;
        System.out.println(getName() + " review completed: " + comments);
    }

    @Override
    public String getLastReviewSummary() { return lastReview; }
}
```

### Interface as Type

An interface can be used as a reference type — just like an abstract class:

```java
Payable p = new Manager(201, "Ponu", 110000, "Engineering", 8);
p.calculateNetPay();    // ok
p.generatePayslip();    // ok
p.conductReview("...");  // compile error — Payable doesn't know about conductReview
```

---

## Java 8 — `default` and `static` Methods in Interfaces

Before Java 8, adding a new method to an interface broke all classes that implemented it. Java 8 introduced `default` methods to allow interface evolution without breaking existing code.

### `default` method

Provides a default implementation. Implementing classes can override it or use it as-is:

```java
public interface Payable {
    double calculateNetPay();
    void   generatePayslip();

    // Default method — implementing classes get this for free
    default String getPaymentMode() {
        return "Bank Transfer";   // default, overridable
    }
}
```

```java
// Contractor doesn't override getPaymentMode() — uses the default
public class Contractor extends Employee implements Payable {
    // ...
    @Override public double calculateNetPay() { return getDailyRate() * 22; }
    @Override public void   generatePayslip() { /* ... */ }
    // getPaymentMode() returns "Bank Transfer" from default
}

// A special case that needs to override:
public class SalesRep extends Employee implements Payable {
    @Override public String getPaymentMode() { return "Cheque"; }
    // ...
}
```

### `static` method

Belongs to the interface itself, not to implementing classes. Called via the interface name:

```java
public interface Payable {
    static double calculateTax(double salary) {
        return salary * 0.10;
    }
}

// Called as:
double tax = Payable.calculateTax(75000);
```

---

## Abstract Class vs Interface — Decision Guide

| Aspect | Abstract Class | Interface |
|--------|---------------|-----------|
| Instantiation | No | No |
| Fields | Any type | `public static final` only |
| Constructors | Yes | No |
| Method types | abstract + concrete | abstract + default + static |
| Inheritance | `extends` (one only) | `implements` (multiple) |
| Access modifiers | Any | public (abstract/default), public static (static) |
| Use when | Classes share common state and behavior | Unrelated classes share a contract |

### When to use an abstract class

You have common **state** (fields) and **partial implementation** that subclasses should share. Example: `Employee` — every employee has an id, name, salary. The common implementation of `applyRaise` should not be duplicated.

### When to use an interface

You want to define a **capability** that can be mixed into unrelated classes. Example: `Payable` could apply to `Employee`, `Vendor`, and `Contractor` — three completely unrelated hierarchies. An abstract class cannot serve all three; an interface can.

---

## Interface Segregation — Real Design

Don't create one fat interface. Split by concern:

```java
public interface Payable    { double calculateNetPay(); void generatePayslip(); }
public interface Reviewable { void conductReview(String c); String getLastReviewSummary(); }
public interface Trainable  { void assignTraining(String course); boolean hasCompletedTraining(String course); }

// Manager needs all three:
public class Manager extends Employee implements Payable, Reviewable, Trainable { }

// Contractor only needs to be paid:
public class Contractor extends Employee implements Payable { }

// Intern needs training but no formal review:
public class Intern extends Employee implements Trainable { }
```

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Abstract class | Cannot be instantiated; mix of abstract + concrete members |
| Abstract method | No body — subclass must implement or also be abstract |
| Interface | Pure contract — `implements`, multiple allowed |
| `default` method | Interface method with body — Java 8+; classes can override |
| `static` interface method | Belongs to interface — called via `InterfaceName.method()` |
| Abstract class fields | Can be anything — private, instance, mutable |
| Interface fields | Always `public static final` — constants only |
| Choose abstract class | Shared state + partial implementation |
| Choose interface | Shared contract across unrelated classes, or multiple inheritance needed |

---

## What's Next

**Module 13** — Encapsulation. The fourth OOP pillar — protecting data and controlling how it is accessed and modified.
