# Module 11 — Polymorphism

> **Part C: Object-Oriented Programming**
> Prerequisites: Module 10 · Time: ~1 hour

---

## What Is Polymorphism?

Polymorphism means **many forms**. In Java, it means one reference type can point to objects of different actual types, and the correct behavior is selected automatically.

A simpler way to think about it: you send the same message (`applyRaise()`) to different employees, and each responds in their own way — without you needing to know which kind of employee it is.

Java has two types:

| Type | Also Called | Resolved At |
|------|------------|------------|
| Method overloading | Compile-time / Static | Compile time |
| Method overriding | Runtime / Dynamic | Runtime |

---

## Compile-Time Polymorphism — Method Overloading

The compiler picks the right method based on the **number and types of arguments**. This is resolved before the program runs.

```java
public class BonusCalculator {

    // Three overloaded versions of calculateBonus
    public double calculateBonus(double salary) {
        return salary * 0.10;
    }

    public double calculateBonus(double salary, double percent) {
        return salary * (percent / 100);
    }

    public double calculateBonus(double salary, double percent, int yearsOfService) {
        double base = salary * (percent / 100);
        double seniority = yearsOfService > 5 ? base * 0.20 : 0;
        return base + seniority;
    }
}
```

```java
BonusCalculator calc = new BonusCalculator();

System.out.println(calc.calculateBonus(75000));            // 7500.0   — one arg version
System.out.println(calc.calculateBonus(75000, 15));        // 11250.0  — two arg version
System.out.println(calc.calculateBonus(75000, 15, 7));     // 13500.0  — three arg version
```

The compiler looks at the call site and decides which method to use. If no match is found, it's a compile error.

**What is NOT overloading:**

```java
// Changing only the return type is NOT overloading — compile error
public double calculateBonus(double salary) { return salary * 0.10; }
public int    calculateBonus(double salary) { return (int)(salary * 0.10); }  // error
```

---

## Runtime Polymorphism — Method Overriding + Dynamic Dispatch

This is the powerful one. When a parent class reference holds a child class object, the **actual object's method runs** — not the reference type's method.

```java
Employee e;   // reference type: Employee

e = new Manager(201, "Ponu", 110000, "Engineering", 8, "Apollo");
e.applyRaise(10);   // Manager's applyRaise() runs (or Employee's if not overridden)
e.display();        // Manager's display() runs

e = new Developer(301, "Monu", 82000, "Engineering", "Java, Spring");
e.applyRaise(10);   // Developer's applyRaise() runs (developer gets extra 5%)
e.display();        // Developer's display() runs
```

The reference type `Employee` stays the same. The behavior changes based on what object is actually there. The JVM decides at **runtime** which method to call — this is **dynamic dispatch**.

---

## Why This Matters — Processing a Mixed Collection

The real power shows when you have a collection of different employee types and process them uniformly:

```java
Employee[] team = {
    new Employee(101,  "Sonu",  75000.0, "Engineering"),
    new Manager(201,   "Ponu",  110000.0, "Engineering", 8, "Apollo"),
    new Developer(301, "Monu",  82000.0,  "Engineering", "Java, Spring"),
    new Developer(302, "Tonu",  78000.0,  "Engineering", "Python"),
    new Contractor(401,"Gonu",  0,         "Finance",    1500.0, "2025-12-31")
};

System.out.printf("%-5s %-12s %-15s %12s%n", "ID", "Name", "Type", "Salary");
System.out.println("=".repeat(50));

for (Employee e : team) {
    e.display();       // each calls their own version of display()
}

System.out.println("\nApplying 10% raise to all:");
for (Employee e : team) {
    e.applyRaise(10);  // Developer gets 15%, others get 10%
}
```

Without polymorphism, you would need:

```java
// This is what life without polymorphism looks like — tedious and fragile
for (Object obj : team) {
    if (obj instanceof Manager) {
        ((Manager) obj).display();
    } else if (obj instanceof Developer) {
        ((Developer) obj).display();
    } else if (obj instanceof Employee) {
        ((Employee) obj).display();
    }
}
```

Polymorphism eliminates this entirely.

---

## Upcasting and Downcasting

### Upcasting — Implicit, Safe

Assigning a child object to a parent reference. Always safe — a `Manager` is always an `Employee`.

```java
Employee e = new Manager(201, "Ponu", 110000, "Engineering", 8, "Apollo");
// Manager object → Employee reference. Implicit, no cast needed.
```

After upcasting, you can only call methods that `Employee` declares. Manager-specific methods are hidden through the `Employee` reference:

```java
e.display();          // ok — Employee has display()
e.applyRaise(10);     // ok — Employee has applyRaise()
e.conductReview();    // compile error — Employee doesn't know about conductReview()
```

### Downcasting — Explicit, Needs Care

Recasting a parent reference back to the child type to access child-specific methods:

```java
Employee e = new Manager(201, "Ponu", 110000, "Engineering", 8, "Apollo");

// Downcast to access Manager-specific method
Manager m = (Manager) e;     // explicit cast required
m.conductReview();           // now accessible
```

**Unsafe downcast causes `ClassCastException`:**

```java
Employee e = new Developer(301, "Monu", 82000, "Engineering", "Java");
Manager m = (Manager) e;     // compiles — but throws ClassCastException at runtime
                             // a Developer is NOT a Manager
```

### Always check with `instanceof` before downcasting

```java
for (Employee e : team) {
    if (e instanceof Manager) {
        Manager m = (Manager) e;
        m.conductReview();
    } else if (e instanceof Developer) {
        Developer d = (Developer) e;
        System.out.println(d.getName() + " works with: " + d.getTechStack());
    }
}
```

### Pattern Matching instanceof (Java 16+)

Combines the check and cast in one step:

```java
for (Employee e : team) {
    if (e instanceof Manager m) {           // check + cast in one line
        m.conductReview();
    } else if (e instanceof Developer d) {
        System.out.println(d.getTechStack());
    }
}
```

Covered more fully in Module 30 (Java 17 Features).

---

## Virtual Method Table (How Dynamic Dispatch Works)

When the JVM loads a class, it builds a **virtual method table (vtable)** for it — a lookup table mapping method names to actual implementations.

When `e.display()` is called on an `Employee` reference that actually holds a `Manager`:

1. JVM looks at the actual object type at runtime: `Manager`
2. Looks up `display()` in `Manager`'s vtable
3. Finds `Manager.display()` — calls it

If `Manager` had not overridden `display()`, the vtable entry would point to `Employee.display()`.

This is why overriding works through a parent reference — the JVM always uses the actual object's table, not the reference type's table.

---

## Polymorphism with Constructors — A Trap

One subtle issue: calling an overridden method from a parent constructor runs the **child's version** — even though the child object is not fully constructed yet.

```java
public class Employee {
    public Employee() {
        printDetails();   // calls overridden version in subclass — dangerous
    }
    public void printDetails() {
        System.out.println("Employee details");
    }
}

public class Manager extends Employee {
    private String projectName = "Apollo";

    @Override
    public void printDetails() {
        System.out.println("Project: " + projectName);  // projectName is null here!
    }
}

// In main:
Manager m = new Manager(...);
// Output: Project: null  ← because Manager's field wasn't initialized yet
```

**Rule:** Never call overridable methods from constructors. Use `private` or `final` methods in constructors.

---

## Practical Example — Payroll Processor

```java
public class PayrollProcessor {

    public static void processPayroll(Employee[] employees, double raisePercent) {
        double totalBefore = 0, totalAfter = 0;

        System.out.println("Processing payroll...\n");
        System.out.printf("%-12s %-10s %12s %12s%n",
                          "Name", "Type", "Before", "After");
        System.out.println("-".repeat(52));

        for (Employee e : employees) {
            double before = e.getSalary();
            totalBefore += before;

            e.applyRaise(raisePercent);

            double after = e.getSalary();
            totalAfter += after;

            String type = e.getClass().getSimpleName();   // Manager, Developer, etc.
            System.out.printf("%-12s %-10s %12.2f %12.2f%n",
                              e.getName(), type, before, after);
        }

        System.out.println("-".repeat(52));
        System.out.printf("%-22s %12.2f %12.2f%n", "Total", totalBefore, totalAfter);
        System.out.printf("Payroll increase: %.2f%n", totalAfter - totalBefore);
    }

    public static void main(String[] args) {
        Employee[] team = {
            new Employee(101,  "Sonu",  75000.0, "Engineering"),
            new Manager(201,   "Ponu",  110000.0, "Engineering", 8, "Apollo"),
            new Developer(301, "Monu",  82000.0,  "Engineering", "Java, Spring"),
            new Developer(302, "Tonu",  78000.0,  "Engineering", "Python"),
        };

        processPayroll(team, 10);
    }
}
```

Output:
```
Processing payroll...

Name         Type          Before         After
----------------------------------------------------
Sonu         Employee    75000.00      82500.00
Ponu         Manager    110000.00     121000.00
Monu         Developer   82000.00      94300.00
Tonu         Developer   78000.00      89700.00
----------------------------------------------------
Total                   345000.00     387500.00
Payroll increase: 42500.00
```

`Sonu` and `Ponu` got 10%. `Monu` and `Tonu` (Developers) got 15% — because `Developer.applyRaise()` adds an extra 5%. `processPayroll` doesn't need to know this — it just calls `applyRaise()` and polymorphism handles the rest.

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Compile-time polymorphism | Method overloading — resolved by compiler |
| Runtime polymorphism | Method overriding + dynamic dispatch — resolved by JVM |
| Upcasting | Child → Parent reference, implicit, safe |
| Downcasting | Parent → Child reference, explicit, needs `instanceof` check |
| `getClass().getSimpleName()` | Returns actual runtime type as a String |
| Dynamic dispatch | JVM always calls the actual object's method, not the reference type's |
| Constructor + overriding | Never call overridable methods from constructors |

---

## What's Next

**Module 12** — Abstraction. Defining what something *does* without specifying how — using abstract classes and interfaces.
