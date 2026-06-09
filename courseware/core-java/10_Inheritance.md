# Module 10 — Inheritance

> **Part C: Object-Oriented Programming**
> Prerequisites: Module 08, 09 · Time: ~1.5 hours

---

## What Is Inheritance?

Inheritance is a mechanism where a class **acquires the fields and methods of another class**. The acquiring class can then extend, specialize, or override that behavior.

The core idea: **don't repeat yourself**. If two classes share common state and behavior, extract it into a parent class.

```
Employee (parent)
├── id, name, salary, department
├── getSalary(), applyRaise(), display()
│
├── Manager (child)       → adds: teamSize, getTeamSize(), conductReview()
│
├── Developer (child)     → adds: techStack, getTechStack(), writeCode()
│
└── Contractor (child)    → adds: contractEndDate, getDailyRate()
```

`Manager`, `Developer`, and `Contractor` all get `id`, `name`, `salary`, and all of `Employee`'s methods for free — without rewriting them.

---

## Terminology

| Term | Meaning |
|------|---------|
| Parent class / Superclass | The class being inherited from |
| Child class / Subclass | The class that inherits |
| `extends` | The keyword used to inherit |

---

## `extends` Keyword

```java
package com.ems.bean;

public class Employee {
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

    public int    getId()         { return id; }
    public String getName()       { return name; }
    public double getSalary()     { return salary; }
    public String getDepartment() { return department; }
    public void   setSalary(double salary) { this.salary = salary; }

    public void applyRaise(double percent) {
        this.salary = this.salary * (1 + percent / 100);
    }

    public void display() {
        System.out.printf("%-5d %-12s %-15s %10.2f%n",
                          id, name, department, salary);
    }

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', dept='%s', salary=%.2f}",
                             id, name, department, salary);
    }
}
```

```java
package com.ems.bean;

public class Manager extends Employee {

    private int    teamSize;
    private String projectName;

    public Manager(int id, String name, double salary,
                   String department, int teamSize, String projectName) {
        super(id, name, salary, department);   // call Employee's constructor
        this.teamSize    = teamSize;
        this.projectName = projectName;
    }

    public int    getTeamSize()    { return teamSize; }
    public String getProjectName() { return projectName; }

    public void conductReview() {
        System.out.println(getName() + " is conducting performance review for project: " + projectName);
    }

    @Override
    public void display() {
        super.display();   // call Employee's display first
        System.out.printf("     Team Size: %d, Project: %s%n", teamSize, projectName);
    }
}
```

---

## `super` Keyword

`super` refers to the parent class. Two uses:

### 1. Call the parent constructor

```java
public Manager(int id, String name, double salary,
               String department, int teamSize, String projectName) {
    super(id, name, salary, department);   // MUST be the first statement
    this.teamSize    = teamSize;
    this.projectName = projectName;
}
```

`super()` must be the **first statement** in the child constructor. If you do not call it explicitly, Java automatically inserts `super()` (the no-arg parent constructor). If the parent has no no-arg constructor, you must call `super(...)` explicitly.

### 2. Call a parent method

```java
@Override
public void display() {
    super.display();    // runs Employee's display()
    System.out.printf("     Team Size: %d, Project: %s%n", teamSize, projectName);
}
```

---

## IS-A vs HAS-A

**IS-A** — Inheritance. `Manager` IS-A `Employee`.

```java
Manager m = new Manager(...);
System.out.println(m instanceof Employee);   // true — every Manager is an Employee
System.out.println(m instanceof Manager);    // true
```

**HAS-A** — Composition. `Employee` HAS-A `Department`.

```java
public class Employee {
    private Department department;    // Employee HAS-A Department object
}
```

Prefer composition over inheritance when the relationship is "has-a" rather than "is-a". Inheritance is often overused — only use it when a genuine IS-A relationship exists.

---

## Method Overriding

A subclass can provide its own implementation of a method defined in the parent class. The parent's version is replaced for subclass objects.

**Rules:**
- Same method name, same parameters, same (or covariant) return type
- Access modifier can be the same or wider (never narrower)
- `@Override` annotation — not required, but always use it: it tells the compiler to verify you are actually overriding something

```java
public class Developer extends Employee {

    private String techStack;

    public Developer(int id, String name, double salary,
                     String department, String techStack) {
        super(id, name, salary, department);
        this.techStack = techStack;
    }

    public String getTechStack() { return techStack; }

    @Override
    public void applyRaise(double percent) {
        // Developers get an extra 5% on top of the standard raise
        super.applyRaise(percent + 5);
    }

    @Override
    public void display() {
        super.display();
        System.out.printf("     Tech Stack: %s%n", techStack);
    }
}
```

```java
Developer d = new Developer(201, "Tonu", 75000, "Engineering", "Java, Spring");
d.applyRaise(10);   // gets 15% raise (10 + 5 bonus)
d.display();
```

---

## Overriding vs Overloading

A common point of confusion:

| Aspect | Overriding | Overloading |
|--------|-----------|-------------|
| Where | Parent–child classes | Same class (or child class) |
| Method name | Same | Same |
| Parameters | Same | Different |
| Return type | Same (or covariant) | Can differ |
| Resolved at | **Runtime** | **Compile time** |
| Annotation | `@Override` | None |
| Purpose | Specializing inherited behavior | Multiple versions of a method |

---

## `final` Keyword

### `final` method — cannot be overridden

```java
public class Employee {
    public final double calculateTax() {
        return getSalary() * 0.10;   // fixed tax logic — no subclass should change this
    }
}

public class Manager extends Employee {
    @Override
    public double calculateTax() { }   // compile error — cannot override final method
}
```

### `final` class — cannot be subclassed

```java
public final class TaxCalculator {
    // No class can extend TaxCalculator
}

public class CustomTax extends TaxCalculator { }   // compile error
```

`String` is a `final` class — that is partly why it is immutable.

### `final` variable — cannot be reassigned

```java
public class Employee {
    public static final int MIN_SALARY = 15000;   // constant — never changes
    private final int id;                          // once set in constructor, never changes

    public Employee(int id, ...) {
        this.id = id;   // ok — setting final field for the first time in constructor
        // this.id = 999;  // compile error — cannot reassign
    }
}
```

---

## Types of Inheritance in Java

Java supports the following:

**Single inheritance** — one parent:
```java
class Manager extends Employee { }
```

**Multilevel inheritance** — chain of parent → child → grandchild:
```java
class Employee { }
class Manager extends Employee { }
class Director extends Manager { }   // Director → Manager → Employee
```

**Hierarchical inheritance** — multiple children from one parent:
```java
class Manager   extends Employee { }
class Developer extends Employee { }
class Contractor extends Employee { }
```

**Multiple inheritance — NOT supported for classes in Java:**
```java
class X extends A, B { }   // compile error — not allowed
```

Java avoids multiple class inheritance to prevent the **Diamond Problem** — ambiguity when two parent classes have the same method. Interfaces solve this (Module 12).

---

## Constructor Chaining in Inheritance

When you create a `Manager` object, constructors run in this order:

```
Object() → Employee() → Manager()
```

Every constructor calls its parent's constructor first, all the way up to `Object` (the root of all Java classes):

```java
class Employee {
    Employee() { System.out.println("Employee constructor"); }
}

class Manager extends Employee {
    Manager() {
        super();   // Java inserts this automatically if you don't write it
        System.out.println("Manager constructor");
    }
}

class Director extends Manager {
    Director() {
        super();
        System.out.println("Director constructor");
    }
}

// In main:
Director d = new Director();
```

Output:
```
Employee constructor
Manager constructor
Director constructor
```

---

## The `Object` Class

Every class in Java implicitly extends `java.lang.Object`. If you don't write `extends`, Java adds it automatically:

```java
public class Employee { }
// is the same as:
public class Employee extends Object { }
```

`Object` provides methods that every class inherits:

| Method | Description |
|--------|-------------|
| `toString()` | String representation — override this |
| `equals(Object o)` | Equality comparison — override this |
| `hashCode()` | Hash code — override when overriding equals |
| `getClass()` | Returns the runtime class |
| `clone()` | Creates a copy (requires `Cloneable`) |
| `wait()`, `notify()`, `notifyAll()` | Thread coordination |
| `finalize()` | Called before GC (deprecated in Java 9) |

These are covered in depth in Module 14.

---

## Practical Example — Employee Hierarchy

```java
public class InheritanceDemo {
    public static void main(String[] args) {

        Employee e1 = new Employee(101, "Sonu", 75000, "Engineering");

        Manager m1 = new Manager(201, "Ponu", 110000, "Engineering", 8, "Project Apollo");

        Developer d1 = new Developer(301, "Monu", 82000, "Engineering", "Java, Spring, React");
        Developer d2 = new Developer(302, "Tonu", 78000, "Engineering", "Python, Django");

        Contractor c1 = new Contractor(401, "Gonu", 0, "Finance",
                                       1500.0, "2025-12-31");

        System.out.println("=== Employee Roster ===\n");

        // All of them respond to display() — each in their own way
        e1.display();
        System.out.println();
        m1.display();
        System.out.println();
        d1.display();
        System.out.println();
        c1.display();

        System.out.println("\n=== IS-A Check ===");
        System.out.println("m1 instanceof Employee:  " + (m1 instanceof Employee));  // true
        System.out.println("m1 instanceof Manager:   " + (m1 instanceof Manager));   // true
        System.out.println("m1 instanceof Developer: " + (m1 instanceof Developer)); // false

        System.out.println("\n=== Apply Raises ===");
        d1.applyRaise(10);   // Developer overrides this → gets 15%
        e1.applyRaise(10);   // Employee version → gets 10%
        System.out.printf("Monu new salary: %.2f%n", d1.getSalary());  // 94300.0 (82000 * 1.15)
        System.out.printf("Sonu new salary: %.2f%n", e1.getSalary());  // 82500.0 (75000 * 1.10)
    }
}
```

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| `extends` | Declares inheritance relationship |
| `super()` | Calls parent constructor — must be first statement |
| `super.method()` | Calls parent method from overriding method |
| Method overriding | Same signature, different implementation in subclass |
| `@Override` | Annotation — always use it when overriding |
| `final` method | Cannot be overridden |
| `final` class | Cannot be subclassed |
| IS-A | Inheritance relationship test — use `instanceof` |
| HAS-A | Composition — prefer when IS-A doesn't fit |
| `Object` class | Root of all Java classes — provides `toString()`, `equals()`, etc. |
| Multiple inheritance | Not supported for classes — use interfaces |

---

## What's Next

**Module 11** — Polymorphism. The ability to treat different types through a common interface — one reference type, many actual behaviors.
