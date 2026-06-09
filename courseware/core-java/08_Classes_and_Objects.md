# Module 08 — Classes and Objects

> **Part C: Object-Oriented Programming**
> Prerequisites: Module 03–07 · Time: ~2 hours

---

## What Is Object-Oriented Programming?

Before OOP, programs were written as a sequence of procedures operating on separate data. As programs grew, this became hard to manage — data and the logic that operated on it lived far apart, making code difficult to understand, test, and change.

OOP solves this by bundling **data and behavior together** into a single unit called an **object**.

Java is built around four OOP principles:

| Principle | One-line definition |
|-----------|-------------------|
| **Encapsulation** | Bundle data and methods together; control access |
| **Inheritance** | A class can acquire properties and behavior of another class |
| **Polymorphism** | One interface, multiple behaviors |
| **Abstraction** | Expose what is necessary, hide the complexity |

Each gets its own module. This module focuses on the foundation: **classes and objects**.

---

## Class vs Object

A **class** is a blueprint. An **object** is an instance created from that blueprint.

```
Class: Employee           Object: Sonu (an actual employee)
─────────────────         ────────────────────────────────
Fields:                   id        = 101
  int id                  name      = "Sonu"
  String name             salary    = 75000.0
  double salary           department= "Engineering"
  String department
Methods:
  getSalary()
  applyRaise()
  display()
```

You define a class once. You can create as many objects from it as you need.

---

## Anatomy of a Class

```java
package com.ems.bean;

public class Employee {

    // ── Fields (instance variables) ──────────────────────────────────
    private int    id;
    private String name;
    private double salary;
    private String department;

    // ── Static field (shared across all instances) ────────────────────
    private static int employeeCount = 0;

    // ── Static block (runs once when class is loaded) ─────────────────
    static {
        System.out.println("Employee class loaded.");
        // typically used for static initialization
    }

    // ── Instance block (runs every time an object is created, before constructor) ──
    {
        employeeCount++;
    }

    // ── Constructors ──────────────────────────────────────────────────
    public Employee() {
        this.name       = "Unknown";
        this.salary     = 0.0;
        this.department = "Unassigned";
    }

    public Employee(int id, String name, double salary, String department) {
        this.id         = id;
        this.name       = name;
        this.salary     = salary;
        this.department = department;
    }

    // ── Instance methods ──────────────────────────────────────────────
    public void applyRaise(double percent) {
        this.salary = this.salary * (1 + percent / 100);
    }

    public void display() {
        System.out.printf("%-5d %-12s %-15s %10.2f%n",
                          id, name, department, salary);
    }

    // ── Static method ─────────────────────────────────────────────────
    public static int getEmployeeCount() {
        return employeeCount;
    }

    // ── Getters and Setters ───────────────────────────────────────────
    public int    getId()         { return id; }
    public String getName()       { return name; }
    public double getSalary()     { return salary; }
    public String getDepartment() { return department; }

    public void setName(String name)           { this.name = name; }
    public void setSalary(double salary)       { this.salary = salary; }
    public void setDepartment(String dept)     { this.department = dept; }

    // ── toString ──────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', dept='%s', salary=%.2f}",
                             id, name, department, salary);
    }
}
```

---

## Creating Objects

```java
public class EmployeeDemo {
    public static void main(String[] args) {

        // Create objects using the parameterized constructor
        Employee e1 = new Employee(101, "Sonu",  75000.0, "Engineering");
        Employee e2 = new Employee(102, "Monu",  82000.0, "Engineering");
        Employee e3 = new Employee(103, "Tonu",  55000.0, "HR");
        Employee e4 = new Employee(104, "Ponu",  91000.0, "Finance");
        Employee e5 = new Employee(105, "Gonu",  68000.0, "Operations");

        // Call instance methods
        e1.applyRaise(10);
        e1.display();   // 101   Sonu         Engineering       82500.00

        // Access static member through class name (not through object)
        System.out.println("Total employees: " + Employee.getEmployeeCount());   // 5

        // toString is called implicitly when printing
        System.out.println(e2);
        // Employee{id=102, name='Monu', dept='Engineering', salary=82000.00}
    }
}
```

---

## Fields — Instance vs Static

**Instance fields** belong to each object. Each object has its own copy:

```java
Employee e1 = new Employee(101, "Sonu", 75000.0, "Engineering");
Employee e2 = new Employee(102, "Monu", 82000.0, "HR");

// e1 and e2 each have their own name, salary, department
e1.setSalary(80000);    // only changes e1's salary, e2 is unaffected
```

**Static fields** belong to the class. One copy shared across all objects:

```java
// employeeCount is incremented every time an object is created
System.out.println(Employee.getEmployeeCount());   // 2 after creating e1 and e2
```

Memory picture:

```
Method Area               Heap
──────────────────        ────────────────────────────────
Employee class            Object @1001 (e1)
  employeeCount = 2         id         = 101
  (static — one copy)       name       = "Sonu"
                            salary     = 75000.0
                            department = "Engineering"

                          Object @1002 (e2)
                            id         = 102
                            name       = "Monu"
                            salary     = 82000.0
                            department = "HR"
```

---

## Methods

### Instance Methods

Operate on the specific object they are called on. They have access to all instance fields of that object via `this`.

```java
public void applyRaise(double percent) {
    this.salary = this.salary * (1 + percent / 100);
    // 'this' refers to the current object
}
```

### Static Methods

Belong to the class, not any object. They cannot access instance fields or call instance methods directly — they have no `this`.

```java
public static int getEmployeeCount() {
    return employeeCount;   // ok — static field
    // return name;         // compile error — name is an instance field
}
```

When to use static methods:
- Utility/helper methods that don't need object state (`Math.sqrt()`, `Integer.parseInt()`)
- Factory methods (`valueOf()` patterns)
- Methods that work on class-level data

### Method Overloading

Multiple methods with the **same name but different parameters**. Resolved at compile time:

```java
public void applyRaise(double percent) {
    this.salary = this.salary * (1 + percent / 100);
}

public void applyRaise(double amount, boolean isAbsolute) {
    if (isAbsolute) {
        this.salary += amount;
    } else {
        this.salary = this.salary * (1 + amount / 100);
    }
}

// Both valid:
e1.applyRaise(10);              // 10% raise
e1.applyRaise(5000, true);      // +5000 flat raise
```

The compiler picks the right method based on the number and types of arguments.

---

## Constructors

A constructor is a special method that runs when an object is created. It initializes the object's state.

**Rules:**
- Same name as the class
- No return type (not even `void`)
- Called automatically by `new`
- If you define no constructor, Java provides a default no-arg constructor
- Once you define any constructor, Java removes the default — you must define your own no-arg if you need it

### Constructor Overloading

Multiple constructors with different parameters:

```java
public class Employee {

    private int    id;
    private String name;
    private double salary;
    private String department;

    // No-arg constructor
    public Employee() {
        this(0, "Unknown", 0.0, "Unassigned");   // delegates to full constructor
    }

    // Partial constructor
    public Employee(String name, String department) {
        this(0, name, 0.0, department);           // delegates to full constructor
    }

    // Full constructor
    public Employee(int id, String name, double salary, String department) {
        this.id         = id;
        this.name       = name;
        this.salary     = salary;
        this.department = department;
    }
}
```

### Constructor Chaining with `this()`

`this()` calls another constructor in the same class. It must be the **first statement** in the constructor body. This avoids duplicating initialization logic.

```java
Employee e1 = new Employee();                            // id=0, name=Unknown
Employee e2 = new Employee("Sonu", "Engineering");       // id=0, name=Sonu
Employee e3 = new Employee(101, "Monu", 82000, "HR");    // fully specified
```

---

## The `this` Keyword

`this` refers to the current object — the one on which the method or constructor was called.

### 1. Disambiguate field from parameter

```java
public Employee(String name, double salary) {
    this.name   = name;    // this.name = field, name = parameter
    this.salary = salary;
}
```

### 2. Call another constructor

```java
public Employee(String name) {
    this(name, 0.0);   // must be first statement
}
```

### 3. Pass the current object to another method

```java
public void register(EmployeeRegistry registry) {
    registry.add(this);   // passing the current Employee object
}
```

---

## Execution Order — Putting It Together

When `new Employee(101, "Sonu", 75000, "Engineering")` is called:

```
1. Memory allocated on heap for the new Employee object
2. Static block runs — only once, when the class is first loaded (not on every new)
3. Instance block runs — every time a new object is created, before the constructor
4. Constructor runs — initializes fields
5. Reference returned and assigned to the variable
```

```java
public class Employee {

    static {
        System.out.println("1. Static block");   // once per class load
    }

    {
        System.out.println("2. Instance block");  // every new Employee
    }

    public Employee(String name) {
        System.out.println("3. Constructor: " + name);
    }
}

// In main:
Employee e1 = new Employee("Sonu");
Employee e2 = new Employee("Monu");
```

Output:
```
1. Static block
2. Instance block
3. Constructor: Sonu
2. Instance block
3. Constructor: Monu
```

Static block ran only once. Instance block ran twice (once per object).

---

## Object References

A variable of a class type holds a **reference** (address) to an object on the heap, not the object itself.

```java
Employee e1 = new Employee(101, "Sonu", 75000, "Engineering");
Employee e2 = e1;   // e2 holds the same reference — no new object created

e2.setSalary(90000);
System.out.println(e1.getSalary());   // 90000 — e1 and e2 point to the same object
```

```
Stack          Heap
──────         ──────────────────────
e1 → @1001     Object @1001
e2 → @1001       name   = "Sonu"
                 salary = 90000    ← changed via e2, seen via e1
```

### `null` Reference

```java
Employee e = null;   // e points to nothing
e.getSalary();       // NullPointerException — there is no object to call on
```

Always check for null before using a reference received from outside:

```java
public void printEmployee(Employee e) {
    if (e == null) {
        System.out.println("No employee provided.");
        return;
    }
    e.display();
}
```

---

## Passing Objects to Methods

Objects are passed by **value of the reference** — the method gets a copy of the reference (address), not a copy of the object. This means the method can modify the object's state, but cannot make the caller's variable point to a different object.

```java
public static void givePromotion(Employee e, String newDept, double raise) {
    e.setDepartment(newDept);    // modifies the original object
    e.applyRaise(raise);         // modifies the original object
    e = null;                    // only affects the local copy of the reference
}

Employee emp = new Employee(101, "Sonu", 75000, "Engineering");
givePromotion(emp, "Management", 20);

System.out.println(emp.getDepartment());  // Management — object state changed
System.out.println(emp);                  // emp is not null — local reassignment had no effect
```

---

## Varargs — Variable Number of Arguments

When you don't know how many arguments a method will receive:

```java
public static double totalSalary(double... salaries) {
    double total = 0;
    for (double s : salaries) total += s;
    return total;
}

// Call with any number of arguments
System.out.println(totalSalary(75000, 82000));                      // 157000.0
System.out.println(totalSalary(75000, 82000, 55000, 91000, 68000)); // 371000.0
System.out.println(totalSalary());                                   // 0.0
```

`double...` is essentially `double[]` — the compiler packs the arguments into an array. Varargs must be the **last parameter** in the method signature.

---

## Practical Example — Department Report

```java
public class DepartmentReport {
    public static void main(String[] args) {

        Employee[] employees = {
            new Employee(101, "Sonu",  75000.0, "Engineering"),
            new Employee(102, "Monu",  82000.0, "Engineering"),
            new Employee(103, "Tonu",  55000.0, "HR"),
            new Employee(104, "Ponu",  91000.0, "Finance"),
            new Employee(105, "Gonu",  68000.0, "Engineering")
        };

        System.out.printf("%-5s %-12s %-15s %12s%n", "ID", "Name", "Department", "Salary");
        System.out.println("=".repeat(50));

        double total = 0;
        for (Employee e : employees) {
            e.display();
            total += e.getSalary();
        }

        System.out.println("=".repeat(50));
        System.out.printf("%-33s %12.2f%n", "Total Payroll:", total);
        System.out.printf("%-33s %12d%n",   "Total Employees:", Employee.getEmployeeCount());
    }
}
```

Output:
```
ID    Name         Department        Salary
==================================================
101   Sonu         Engineering     75000.00
102   Monu         Engineering     82000.00
103   Tonu         HR              55000.00
104   Ponu         Finance         91000.00
105   Gonu         Engineering     68000.00
==================================================
Total Payroll:                    371000.00
Total Employees:                           5
```

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Class | Blueprint — defines fields and methods |
| Object | Instance of a class — created with `new`, lives on the heap |
| Instance field | Each object has its own copy |
| Static field | One copy shared by all objects — belongs to the class |
| Constructor | Initializes an object — same name as class, no return type |
| `this` | Refers to the current object |
| `this()` | Calls another constructor — must be first statement |
| Static block | Runs once when the class is loaded |
| Instance block | Runs before every constructor call |
| Execution order | Static block → Instance block → Constructor |
| Object reference | Variable holds an address, not the object itself |

---

## What's Next

**Module 09** — Access Modifiers and Packages. Controlling who can see and use your classes and their members.
