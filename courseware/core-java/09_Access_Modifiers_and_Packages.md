# Module 09 — Access Modifiers and Packages

> **Part C: Object-Oriented Programming**
> Prerequisites: Module 08 · Time: ~1 hour

---

## Access Modifiers

An access modifier controls **who can access** a class, field, method, or constructor. Java has four:

| Modifier | Keyword | Visibility |
|----------|---------|-----------|
| Private | `private` | Only within the same class |
| Default | *(no keyword)* | Within the same package |
| Protected | `protected` | Same package + subclasses (any package) |
| Public | `public` | Everywhere |

Think of it as concentric circles of visibility — private is the tightest, public is the widest.

```
public    → entire application
protected → package + subclasses
default   → package only
private   → class only
```

---

## `private`

The most restrictive. Only the class itself can access the member. This is the right choice for **fields** — it is the foundation of encapsulation.

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

    // Controlled access through public methods
    public double getSalary() { return salary; }
    public void   setSalary(double salary) {
        if (salary > 0) {          // validation inside the class
            this.salary = salary;
        }
    }
}
```

```java
Employee e = new Employee(101, "Sonu", 75000);
System.out.println(e.salary);       // compile error — salary is private
System.out.println(e.getSalary());  // 75000.0 — ok, accessed via public method
e.setSalary(-5000);                 // silently rejected by the validation in setSalary
e.setSalary(80000);                 // accepted
```

---

## Default (Package-Private)

No keyword — just omit the modifier. Accessible within the **same package only**. Classes in other packages, even subclasses, cannot access it.

```java
class Department {            // default class — only visible within its package
    String name;              // default field
    int    headCount;         // default field

    void printInfo() {        // default method
        System.out.println(name + ": " + headCount + " employees");
    }
}
```

Default access is often used for helper classes that are internal to a package and should not be part of the public API.

---

## `protected`

Accessible within the **same package** AND by **subclasses in any package**.

```java
package com.ems.bean;

public class Employee {
    protected int    id;
    protected String name;
    protected double salary;

    protected void display() {
        System.out.println(id + " - " + name + " - " + salary);
    }
}
```

```java
package com.ems.bean.specialist;    // different package

import com.ems.bean.Employee;

public class Manager extends Employee {
    private String teamName;

    public void showDetails() {
        display();                   // ok — Manager is a subclass of Employee
        System.out.println(id);     // ok — protected field accessible in subclass
    }
}
```

```java
// In some unrelated class in a different package:
Employee e = new Employee(101, "Sonu", 75000, "Eng");
System.out.println(e.id);    // compile error — not a subclass, different package
```

---

## `public`

No restrictions. Accessible from anywhere in the application.

```java
public class Employee {
    public static final int MIN_SALARY = 15000;   // public constant

    public Employee(int id, String name, double salary, String department) { ... }
    public double getSalary() { return salary; }
    public void display() { ... }
}
```

```java
// Accessible from anywhere:
System.out.println(Employee.MIN_SALARY);
Employee e = new Employee(101, "Sonu", 75000, "Engineering");
e.display();
```

---

## Summary Table

Let's say you have this structure:

```
com.ems.bean        → Employee.java (defines the members)
com.ems.bean        → EmployeeTest.java (same package, not a subclass)
com.ems.service     → EmployeeService.java (different package, not a subclass)
com.ems.specialist  → Manager.java (different package, subclass of Employee)
```

| Modifier | `EmployeeTest` (same pkg) | `EmployeeService` (diff pkg) | `Manager` (subclass, diff pkg) |
|----------|:---:|:---:|:---:|
| `private` | ✗ | ✗ | ✗ |
| default | ✓ | ✗ | ✗ |
| `protected` | ✓ | ✗ | ✓ |
| `public` | ✓ | ✓ | ✓ |

---

## Access Modifiers on Classes

A **top-level class** (not nested) can only be `public` or default:

```java
public class Employee { }      // visible everywhere
class Department { }           // visible within the package only
private class Team { }         // compile error — not allowed for top-level classes
protected class Project { }    // compile error — not allowed for top-level classes
```

> One `public` class per file, and the filename must match the class name.

Inner/nested classes can have all four modifiers — covered in Module 15.

---

## Good Practice — What to Use Where

| Member | Recommended Modifier | Why |
|--------|---------------------|-----|
| Instance fields | `private` | Hide data, force access through methods |
| Getters / setters | `public` | Controlled access to fields |
| Constructors | `public` | Allow object creation from outside |
| Utility/helper methods | `private` | Internal implementation detail |
| Constants | `public static final` | Safe to expose — they can't change |
| Class | `public` | Usually needed outside the package |

---

## Packages

A **package** is a namespace — a way to organize related classes and avoid name conflicts.

Without packages, if two libraries both define a class called `Employee`, there is no way to distinguish them. With packages:

```java
com.hrms.bean.Employee     // one library's Employee
com.payroll.model.Employee // another library's Employee
```

Both can coexist and be imported explicitly.

---

## Declaring a Package

The `package` statement must be the **very first line** of the source file (before imports, before the class):

```java
package com.ems.bean;

public class Employee {
    // ...
}
```

This means the `Employee.class` file must live in a directory structure matching the package:

```
src/
└── com/
    └── ems/
        └── bean/
            └── Employee.java
```

---

## Package Naming Conventions

Packages use **all lowercase**, reverse domain name convention:

```
com.ems.bean         → domain: ems.com, sub-package: bean
com.ems.service      → service classes
com.ems.util         → utility/helper classes
com.ems.exception    → custom exceptions
org.example.project  → for non-commercial or open-source projects
```

This guarantees uniqueness — no two organizations share a domain name, so no two packages collide.

---

## Importing Classes

To use a class from another package, you need to either import it or use its **fully qualified name (FQN)**:

### Single Import

```java
package com.ems.service;

import com.ems.bean.Employee;       // import specific class

public class EmployeeService {
    public void process(Employee e) {   // Employee is now usable
        e.display();
    }
}
```

### Wildcard Import

```java
import com.ems.bean.*;    // imports all public classes in com.ems.bean
```

This does **not** import sub-packages. If you also need `com.ems.bean.specialist`, you must import that separately.

### Fully Qualified Name (FQN)

Use the full class name without an import:

```java
com.ems.bean.Employee e = new com.ems.bean.Employee(101, "Sonu", 75000, "Engineering");
```

Useful when two packages have classes with the same name:

```java
import com.ems.bean.Employee;
// ...
com.payroll.model.Employee payrollEmp = new com.payroll.model.Employee();
```

### Static Import

Import static members so you can use them without the class name prefix:

```java
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;
import static java.lang.System.out;    // can import System.out as 'out'

double area = PI * 5 * 5;
double root = sqrt(area);
out.println(root);
```

Use static imports sparingly. They can make code harder to read when it's not clear where a method comes from.

---

## Built-in Packages You Will Use Constantly

| Package | Key Classes |
|---------|------------|
| `java.lang` | `String`, `Math`, `System`, `Object`, `Integer`, `Thread` — **auto-imported** |
| `java.util` | `ArrayList`, `HashMap`, `Scanner`, `Arrays`, `Collections`, `Optional` |
| `java.util.stream` | `Stream`, `Collectors` |
| `java.util.function` | `Function`, `Predicate`, `Consumer`, `Supplier` |
| `java.io` | `File`, `FileReader`, `BufferedReader`, `ObjectInputStream` |
| `java.nio.file` | `Path`, `Paths`, `Files` |
| `java.util.concurrent` | `ExecutorService`, `Future`, `CompletableFuture` |

`java.lang` is the only package that is **automatically imported** — everything else needs an explicit import.

---

## Subpackages

Subpackages are simply packages with a longer name that share a prefix. They are **not** hierarchically nested in terms of access — a class in `com.ems.bean` does not automatically have access to classes in `com.ems.bean.specialist`:

```java
package com.ems.bean;
// No automatic access to com.ems.bean.specialist — must import explicitly
```

---

## Practical Example — Organized Project Structure

```
src/
├── com/ems/bean/
│   ├── Employee.java
│   └── Department.java
├── com/ems/service/
│   └── EmployeeService.java
└── com/ems/app/
    └── Main.java
```

```java
// Employee.java
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

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', dept='%s', salary=%.2f}",
                             id, name, department, salary);
    }
}
```

```java
// Department.java
package com.ems.bean;

public class Department {
    private String name;
    private String location;

    public Department(String name, String location) {
        this.name     = name;
        this.location = location;
    }

    public String getName()     { return name; }
    public String getLocation() { return location; }
}
```

```java
// EmployeeService.java
package com.ems.service;

import com.ems.bean.Employee;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

    private List<Employee> employees = new ArrayList<>();

    public void add(Employee e) {
        employees.add(e);
    }

    public void printAll() {
        System.out.printf("%-5s %-12s %-15s %12s%n", "ID", "Name", "Department", "Salary");
        System.out.println("-".repeat(50));
        for (Employee e : employees) {
            System.out.printf("%-5d %-12s %-15s %12.2f%n",
                e.getId(), e.getName(), e.getDepartment(), e.getSalary());
        }
    }

    public double getTotalPayroll() {
        double total = 0;
        for (Employee e : employees) total += e.getSalary();
        return total;
    }
}
```

```java
// Main.java
package com.ems.app;

import com.ems.bean.Employee;
import com.ems.service.EmployeeService;

public class Main {
    public static void main(String[] args) {

        EmployeeService service = new EmployeeService();

        service.add(new Employee(101, "Sonu",  75000.0, "Engineering"));
        service.add(new Employee(102, "Monu",  82000.0, "Engineering"));
        service.add(new Employee(103, "Tonu",  55000.0, "HR"));
        service.add(new Employee(104, "Ponu",  91000.0, "Finance"));
        service.add(new Employee(105, "Gonu",  68000.0, "Operations"));

        service.printAll();
        System.out.printf("%nTotal Payroll: %.2f%n", service.getTotalPayroll());
    }
}
```

Output:
```
ID    Name         Department         Salary
--------------------------------------------------
101   Sonu         Engineering      75000.00
102   Monu         Engineering      82000.00
103   Tonu         HR               55000.00
104   Ponu         Finance          91000.00
105   Gonu         Operations       68000.00

Total Payroll: 371000.00
```

---

## Quick Summary

| Modifier | Accessible From |
|----------|----------------|
| `private` | Same class only |
| default | Same package |
| `protected` | Same package + subclasses |
| `public` | Everywhere |
| **Package** | **Purpose** |
| `com.ems.bean` | Domain model classes |
| `com.ems.service` | Business logic |
| `com.ems.util` | Helpers and utilities |
| `com.ems.app` | Entry point / main class |

---

## What's Next

**Module 10** — Inheritance. One of OOP's most powerful features — acquiring the properties and behaviors of another class, and extending or overriding them.
