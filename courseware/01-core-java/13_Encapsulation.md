# Module 13 — Encapsulation

> **Part C: Object-Oriented Programming**
> Prerequisites: Module 08, 09 · Time: ~45 minutes

---

## What Is Encapsulation?

Encapsulation means **bundling data and the methods that operate on it into a single unit, and controlling access to that data**.

The goal: an object manages its own state. Outside code cannot reach in and corrupt it directly. Instead, it must go through the object's defined methods — which can validate, transform, or restrict what happens.

Think of it as a vending machine. You press a button (call a method). You get a snack (result). You cannot reach inside and take whatever you want (direct field access).

---

## The Problem Without Encapsulation

```java
public class Employee {
    public int    id;
    public String name;
    public double salary;    // public — anyone can set this to anything
}

// In some other class:
Employee e = new Employee();
e.salary = -99999;    // valid — no one stopped this
e.name   = null;      // valid — again, no protection
e.id     = -1;        // valid — nonsense data, no complaints
```

The object's state is now corrupt. There is no way to ensure that an `Employee` always has a valid salary, a non-null name, or a positive ID. Any piece of code anywhere can break it.

---

## The Solution — Private Fields + Public Methods

```java
public class Employee {

    private int    id;
    private String name;
    private double salary;
    private String department;

    public Employee(int id, String name, double salary, String department) {
        setId(id);
        setName(name);
        setSalary(salary);
        setDepartment(department);
    }

    // ── Getters ───────────────────────────────────────────────────────
    public int    getId()         { return id; }
    public String getName()       { return name; }
    public double getSalary()     { return salary; }
    public String getDepartment() { return department; }

    // ── Setters with validation ───────────────────────────────────────
    public void setId(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID must be positive: " + id);
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank.");
        this.name = name.strip();
    }

    public void setSalary(double salary) {
        if (salary < 0)
            throw new IllegalArgumentException("Salary cannot be negative: " + salary);
        this.salary = salary;
    }

    public void setDepartment(String department) {
        if (department == null || department.isBlank())
            throw new IllegalArgumentException("Department cannot be blank.");
        this.department = department.strip();
    }

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', dept='%s', salary=%.2f}",
                             id, name, department, salary);
    }
}
```

Now the object protects itself:

```java
Employee e = new Employee(101, "Sonu", 75000, "Engineering");

e.setSalary(-5000);    // throws IllegalArgumentException — caught at runtime
e.setName("");         // throws IllegalArgumentException
e.setId(-1);           // throws IllegalArgumentException

e.setSalary(82000);    // valid — accepted
System.out.println(e.getSalary());   // 82000.0
```

---

## Read-Only and Write-Only Fields

Encapsulation lets you choose exactly what is readable and writable.

### Read-Only (getter only, no setter)

```java
public class Employee {
    private final int    id;       // set once in constructor, never changes
    private final String name;

    public Employee(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    public int    getId()   { return id; }
    public String getName() { return name; }
    // No setId() or setName() — these fields are read-only after construction
}
```

Useful for identity fields — an employee's ID should not change after creation.

### Write-Only (setter only, no getter)

Rare, but useful for sensitive data like passwords:

```java
public class UserAccount {
    private String passwordHash;

    public void setPassword(String plainText) {
        this.passwordHash = hash(plainText);   // store hashed, never expose
    }
    // No getPassword() or getPasswordHash() — write-only
}
```

---

## Immutable Classes

An **immutable class** is the most locked-down form of encapsulation. Once created, its state can never change.

Rules for making a class immutable:
1. Declare the class `final` — no subclass can undermine it
2. All fields `private final`
3. No setters
4. Initialize all fields in the constructor
5. If a field is a mutable object (like an array or `List`), return a defensive copy from the getter

```java
public final class EmployeeSnapshot {

    private final int    id;
    private final String name;
    private final double salary;
    private final String department;

    public EmployeeSnapshot(int id, String name, double salary, String department) {
        if (id <= 0 || name == null || salary < 0 || department == null)
            throw new IllegalArgumentException("Invalid employee data.");
        this.id         = id;
        this.name       = name;
        this.salary     = salary;
        this.department = department;
    }

    public int    getId()         { return id; }
    public String getName()       { return name; }
    public double getSalary()     { return salary; }
    public String getDepartment() { return department; }

    // "Modification" returns a new object — original unchanged
    public EmployeeSnapshot withSalary(double newSalary) {
        return new EmployeeSnapshot(id, name, newSalary, department);
    }

    @Override
    public String toString() {
        return String.format("EmployeeSnapshot{id=%d, name='%s', salary=%.2f}", id, name, salary);
    }
}
```

```java
EmployeeSnapshot snap = new EmployeeSnapshot(101, "Sonu", 75000, "Engineering");
EmployeeSnapshot raised = snap.withSalary(82000);

System.out.println(snap);    // original unchanged: salary=75000.00
System.out.println(raised);  // new object:         salary=82000.00
```

Immutable objects are:
- Inherently **thread-safe** — no synchronization needed
- Safe to share freely — no one can corrupt your data
- Great for value objects — things that represent a snapshot in time

`String` is the most famous example of an immutable class in Java.

---

## Defensive Copies for Mutable Fields

If your class holds a mutable object (like an array), you must defensively copy it — otherwise, callers can modify the internal state through the reference you gave them.

```java
public final class Project {

    private final String   name;
    private final String[] members;   // mutable — must be handled carefully

    public Project(String name, String[] members) {
        this.name    = name;
        this.members = members.clone();   // defensive copy on input
    }

    public String[] getMembers() {
        return members.clone();           // defensive copy on output
    }

    public String getName() { return name; }
}
```

```java
String[] team = {"Sonu", "Monu", "Tonu"};
Project p = new Project("Apollo", team);

team[0] = "HACKED";                   // modifying the original array
System.out.println(p.getMembers()[0]); // Sonu — Project is unaffected
```

Without the defensive copies, `team[0] = "HACKED"` would corrupt the project's internal state.

---

## JavaBeans Convention

**JavaBeans** is a standard Java convention for writing encapsulated classes, widely used by frameworks (Spring, Hibernate, JPA):

- Private fields
- Public no-arg constructor
- Public getters named `getFieldName()` (or `isFieldName()` for booleans)
- Public setters named `setFieldName(value)`
- Class implements `Serializable` (optional but common)

```java
import java.io.Serializable;

public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private int     id;
    private String  name;
    private double  salary;
    private String  department;
    private boolean active;

    public Employee() { }   // required no-arg constructor

    public int     getId()           { return id; }
    public String  getName()         { return name; }
    public double  getSalary()       { return salary; }
    public String  getDepartment()   { return department; }
    public boolean isActive()        { return active; }   // 'is' prefix for boolean

    public void setId(int id)                  { this.id = id; }
    public void setName(String name)           { this.name = name; }
    public void setSalary(double salary)       { this.salary = salary; }
    public void setDepartment(String dept)     { this.department = dept; }
    public void setActive(boolean active)      { this.active = active; }
}
```

Frameworks like Spring use reflection to call these getters and setters automatically — which is why the naming convention matters.

---

## Encapsulation vs Just Having Getters and Setters

A common mistake: adding `private` fields with mechanical getters and setters for everything, and calling it encapsulation. That is not encapsulation — it is just a boilerplate ceremony.

Real encapsulation means the class has **control and intelligence**:

```java
// This is NOT encapsulation — just noise
public void setSalary(double salary) {
    this.salary = salary;   // no validation, no logic — might as well be public
}

// This IS encapsulation — the class enforces its invariants
public void setSalary(double salary) {
    if (salary < Employee.MIN_SALARY)
        throw new IllegalArgumentException("Salary below minimum: " + salary);
    if (salary > Employee.MAX_SALARY)
        throw new IllegalArgumentException("Salary exceeds maximum: " + salary);
    this.salary = salary;
}

// This IS encapsulation — business logic lives in the class
public void applyRaise(double percent) {
    if (percent < 0 || percent > 50)
        throw new IllegalArgumentException("Raise percent out of range: " + percent);
    this.salary = this.salary * (1 + percent / 100);
}
```

The object owns its state and enforces the rules. That is the point.

---

## Practical Example — Full Encapsulated Employee

```java
public class EncapsulationDemo {
    public static void main(String[] args) {

        Employee e1 = new Employee(101, "Sonu", 75000, "Engineering");
        Employee e2 = new Employee(102, "Monu", 82000, "HR");

        System.out.println(e1);
        System.out.println(e2);

        // Valid operations
        e1.setSalary(80000);
        e1.setDepartment("Management");
        System.out.println("\nAfter update:");
        System.out.println(e1);

        // Invalid operations — caught cleanly
        try {
            e2.setSalary(-5000);
        } catch (IllegalArgumentException ex) {
            System.out.println("\nCaught: " + ex.getMessage());
        }

        try {
            e2.setName("   ");
        } catch (IllegalArgumentException ex) {
            System.out.println("Caught: " + ex.getMessage());
        }

        // Immutable snapshot
        EmployeeSnapshot snapshot = new EmployeeSnapshot(
                e1.getId(), e1.getName(), e1.getSalary(), e1.getDepartment());

        EmployeeSnapshot promoted = snapshot.withSalary(95000);

        System.out.println("\nSnapshot: " + snapshot);
        System.out.println("Promoted: " + promoted);
    }
}
```

Output:
```
Employee{id=101, name='Sonu', dept='Engineering', salary=75000.00}
Employee{id=102, name='Monu', dept='HR', salary=82000.00}

After update:
Employee{id=101, name='Sonu', dept='Management', salary=80000.00}

Caught: Salary cannot be negative: -5000.0
Caught: Name cannot be null or blank.

Snapshot: EmployeeSnapshot{id=101, name='Sonu', salary=80000.00}
Promoted: EmployeeSnapshot{id=101, name='Sonu', salary=95000.00}
```

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Private fields | The foundation — data is hidden from outside |
| Getters / setters | Controlled access — validation belongs here |
| Read-only field | Getter only — no setter |
| Immutable class | `final` class, `final` fields, no setters, defensive copies |
| Defensive copy | Clone mutable inputs/outputs to prevent state leakage |
| JavaBeans | Standard convention — no-arg constructor, `getX()`/`setX()` pattern |
| True encapsulation | Class enforces its own invariants — not just a getter/setter wrapper |

---

## OOP Pillars — Where We Are

All four pillars are now complete:

| Pillar | Module | Core Idea |
|--------|--------|-----------|
| **Encapsulation** | 13 | Hide data, control access through methods |
| **Inheritance** | 10 | Acquire and extend parent behavior |
| **Polymorphism** | 11 | One interface, many implementations |
| **Abstraction** | 12 | Define what, not how |

---

## What's Next

**Module 14** — Object Class Methods. Every class in Java inherits from `Object`. Understanding `equals()`, `hashCode()`, `toString()`, `clone()`, and the threading methods on `Object` is essential for writing correct Java.
