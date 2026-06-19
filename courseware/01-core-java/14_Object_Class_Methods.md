# Module 14 — Object Class Methods

> **Part D: Core Java Toolkit**
> Prerequisites: Module 08–13 · Time: ~1.5 hours

---

## The `Object` Class

Every class in Java implicitly extends `java.lang.Object`. No exceptions. This means every object you ever create — `Employee`, `String`, `ArrayList`, your own classes — inherits these methods from `Object`:

| Method | What It Does |
|--------|-------------|
| `toString()` | Returns a string representation |
| `equals(Object o)` | Tests equality |
| `hashCode()` | Returns an integer hash code |
| `getClass()` | Returns the runtime class |
| `clone()` | Creates a copy of the object |
| `wait()` / `notify()` / `notifyAll()` | Thread coordination |
| `finalize()` | Called before GC (deprecated Java 9+) |

The first three — `toString`, `equals`, `hashCode` — are the ones you override regularly. The rest have specific use cases covered in this module.

---

## `toString()`

### Default Behaviour

If you don't override `toString()`, `Object`'s default implementation returns:

```
ClassName@hexHashCode
```

```java
Employee e = new Employee(101, "Sonu", 75000, "Engineering");
System.out.println(e);   // com.ems.bean.Employee@1b6d3586  ← useless
```

### Override It — Always

```java
@Override
public String toString() {
    return String.format("Employee{id=%d, name='%s', dept='%s', salary=%.2f}",
                         id, name, department, salary);
}
```

```java
System.out.println(e);                // Employee{id=101, name='Sonu', dept='Engineering', salary=75000.00}
System.out.println("Employee: " + e); // String concatenation also calls toString() implicitly
```

`toString()` is called automatically by:
- `System.out.println(obj)`
- String concatenation: `"value: " + obj`
- Debugger watch windows
- Log statements

A good `toString()` includes all fields relevant for debugging and identification.

---

## `equals()`

### Default Behaviour

`Object`'s default `equals()` uses `==` — it checks if two references point to the **same object in memory**:

```java
Employee e1 = new Employee(101, "Sonu", 75000, "Engineering");
Employee e2 = new Employee(101, "Sonu", 75000, "Engineering");

System.out.println(e1 == e2);        // false — different objects in memory
System.out.println(e1.equals(e2));   // false — default equals uses ==
```

Two different `Employee` objects with identical data are considered unequal by default. That is rarely what you want.

### Override for Content Equality

```java
@Override
public boolean equals(Object obj) {
    // 1. Same reference? Shortcut — definitely equal
    if (this == obj) return true;

    // 2. Null check — null is never equal to anything
    if (obj == null) return false;

    // 3. Type check — can only be equal if same type
    if (getClass() != obj.getClass()) return false;

    // 4. Cast and compare fields
    Employee other = (Employee) obj;
    return this.id == other.id
        && Objects.equals(this.name, other.name)
        && Double.compare(this.salary, other.salary) == 0
        && Objects.equals(this.department, other.department);
}
```

```java
Employee e1 = new Employee(101, "Sonu", 75000, "Engineering");
Employee e2 = new Employee(101, "Sonu", 75000, "Engineering");
Employee e3 = new Employee(102, "Monu", 82000, "HR");

System.out.println(e1.equals(e2));   // true  — same content
System.out.println(e1.equals(e3));   // false — different content
System.out.println(e1.equals(null)); // false — null check handled
```

`Objects.equals(a, b)` from `java.util.Objects` handles null safely — it returns `true` if both are null, `false` if only one is null, otherwise calls `a.equals(b)`.

---

## `hashCode()`

### The Contract

`hashCode()` and `equals()` are **always overridden together**. This is not a suggestion — it is the contract:

> If `a.equals(b)` is `true`, then `a.hashCode()` must equal `b.hashCode()`.

The reverse is not required: equal hash codes do not mean the objects are equal (hash collisions are allowed).

### Why It Matters

`HashMap`, `HashSet`, and `Hashtable` use hash codes to determine which bucket to store an object in. If you override `equals` without `hashCode`, these collections break:

```java
Set<Employee> set = new HashSet<>();
set.add(new Employee(101, "Sonu", 75000, "Engineering"));

// Is Sonu in the set?
System.out.println(set.contains(new Employee(101, "Sonu", 75000, "Engineering")));
// false — if hashCode is not overridden, the lookup goes to wrong bucket
// true  — if hashCode is overridden consistently with equals
```

### Override `hashCode`

```java
@Override
public int hashCode() {
    return Objects.hash(id, name, salary, department);
}
```

`Objects.hash(...)` combines multiple fields into a well-distributed hash code. Always include the same fields you used in `equals()`.

### Complete Example

```java
import java.util.Objects;

public class Employee {
    private int    id;
    private String name;
    private double salary;
    private String department;

    // ... constructor, getters, setters ...

    @Override
    public boolean equals(Object obj) {
        if (this == obj)  return true;
        if (obj == null)  return false;
        if (getClass() != obj.getClass()) return false;
        Employee other = (Employee) obj;
        return this.id == other.id
            && Objects.equals(this.name, other.name)
            && Double.compare(this.salary, other.salary) == 0
            && Objects.equals(this.department, other.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, salary, department);
    }

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', dept='%s', salary=%.2f}",
                             id, name, department, salary);
    }
}
```

```java
Employee e1 = new Employee(101, "Sonu", 75000, "Engineering");
Employee e2 = new Employee(101, "Sonu", 75000, "Engineering");

System.out.println(e1.equals(e2));       // true
System.out.println(e1.hashCode() == e2.hashCode());   // true — contract satisfied

Set<Employee> set = new HashSet<>();
set.add(e1);
System.out.println(set.contains(e2));    // true — works correctly now
```

---

## `getClass()`

Returns the `Class` object representing the actual runtime type of the object. Useful for logging, debugging, and conditional logic:

```java
Employee e  = new Manager(201, "Ponu", 110000, "Engineering", 8);

System.out.println(e.getClass());              // class com.ems.bean.Manager
System.out.println(e.getClass().getName());    // com.ems.bean.Manager
System.out.println(e.getClass().getSimpleName()); // Manager

// In equals() — strict type check
if (getClass() != obj.getClass()) return false;
```

`instanceof` is more flexible (accepts subclasses). `getClass() ==` is strict (exact type match). Use `getClass()` in `equals()` for strict equality.

---

## `clone()`

Creates and returns a **copy** of the object. To use it:
1. The class must implement the `Cloneable` marker interface
2. Override `clone()` and make it `public`

### Shallow Copy

The default `Object.clone()` does a **shallow copy** — it copies the field values, but if a field is a reference, both the original and the clone point to the same underlying object.

```java
public class Department implements Cloneable {
    private String name;
    private String location;

    public Department(String name, String location) {
        this.name     = name;
        this.location = location;
    }

    @Override
    public Department clone() {
        try {
            return (Department) super.clone();   // shallow copy — fine for primitives and Strings
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    // getters, setters
}
```

```java
Department d1 = new Department("Engineering", "Pune");
Department d2 = d1.clone();

System.out.println(d1 == d2);            // false — different objects
System.out.println(d1.getName().equals(d2.getName())); // true — same content
```

### Deep Copy

When a class has fields that are mutable objects, shallow copy is not enough — you must clone those fields too:

```java
public class Project implements Cloneable {
    private String   name;
    private String[] teamMembers;   // mutable — needs deep copy

    public Project(String name, String[] members) {
        this.name        = name;
        this.teamMembers = members.clone();   // defensive copy in constructor
    }

    @Override
    public Project clone() {
        try {
            Project copy = (Project) super.clone();
            copy.teamMembers = this.teamMembers.clone();  // deep copy the array
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] getTeamMembers() { return teamMembers.clone(); }
    public String   getName()        { return name; }
}
```

### Modern Alternative to `clone()`

`clone()` is awkward (checked exception, marker interface, shallow copy by default). In practice, most Java developers use a **copy constructor** or a static factory method instead:

```java
public class Employee {
    // Copy constructor
    public Employee(Employee source) {
        this.id         = source.id;
        this.name       = source.name;
        this.salary     = source.salary;
        this.department = source.department;
    }
}

Employee original = new Employee(101, "Sonu", 75000, "Engineering");
Employee copy     = new Employee(original);   // clean, no exceptions, no interface needed
```

---

## `wait()`, `notify()`, `notifyAll()`

These are thread coordination methods that allow threads to communicate through shared objects. They must be called from within a `synchronized` block.

| Method | What It Does |
|--------|-------------|
| `wait()` | Releases the lock and suspends the calling thread until notified |
| `wait(long millis)` | Same, but wakes up after the timeout even if not notified |
| `notify()` | Wakes up one thread waiting on this object (chosen arbitrarily) |
| `notifyAll()` | Wakes up all threads waiting on this object |

```java
public class PayrollQueue {
    private boolean payrollReady = false;

    public synchronized void waitForPayroll() throws InterruptedException {
        while (!payrollReady) {
            System.out.println(Thread.currentThread().getName() + " waiting for payroll...");
            wait();   // releases lock, suspends thread
        }
        System.out.println(Thread.currentThread().getName() + " processing payroll.");
    }

    public synchronized void markPayrollReady() {
        payrollReady = true;
        notifyAll();   // wake all waiting threads
    }
}
```

This is the classic **Producer-Consumer** pattern at the `Object` level. In modern Java, you would use `BlockingQueue`, `CountDownLatch`, or `CompletableFuture` instead — but understanding `wait`/`notify` is important for reading legacy code and understanding what the higher-level APIs do under the hood.

Full concurrency coverage is in Modules 23 and 24.

---

## `finalize()` — Deprecated

`finalize()` was called by the GC before reclaiming an object's memory. It was intended for cleanup (closing resources).

```java
// Old pattern — do NOT use
@Override
protected void finalize() throws Throwable {
    // close resources here
    super.finalize();
}
```

**Why it was deprecated in Java 9 and removed later:**
- No guarantee when or if it runs
- Can cause object resurrection (a finalizable object can make itself reachable again)
- Introduces GC overhead
- Two-pass collection required for finalizable objects

**The modern replacement:** `try-with-resources` with `AutoCloseable`:

```java
// Modern pattern — use this
public class ReportGenerator implements AutoCloseable {
    private final String employeeName;

    public ReportGenerator(String name) {
        this.employeeName = name;
        System.out.println("Report generator opened for: " + name);
    }

    public void generate() {
        System.out.println("Generating report for: " + employeeName);
    }

    @Override
    public void close() {
        System.out.println("Report generator closed for: " + employeeName);
    }
}

// Usage
try (ReportGenerator rg = new ReportGenerator("Sonu")) {
    rg.generate();
}   // close() is called automatically here, even if an exception occurred
```

Output:
```
Report generator opened for: Sonu
Generating report for: Sonu
Report generator closed for: Sonu
```

`try-with-resources` guarantees cleanup. `finalize()` does not. Always use `AutoCloseable` + `try-with-resources` for resource management.

---

## Practical Example — Correct `equals` and `hashCode` in a Collection

```java
import java.util.*;

public class ObjectMethodsDemo {
    public static void main(String[] args) {

        Employee e1 = new Employee(101, "Sonu",  75000, "Engineering");
        Employee e2 = new Employee(101, "Sonu",  75000, "Engineering");  // same as e1
        Employee e3 = new Employee(102, "Monu",  82000, "Engineering");
        Employee e4 = new Employee(103, "Tonu",  55000, "HR");

        // equals and hashCode
        System.out.println("e1.equals(e2): " + e1.equals(e2));   // true
        System.out.println("e1.equals(e3): " + e1.equals(e3));   // false
        System.out.println("e1 == e2:      " + (e1 == e2));       // false

        // HashSet deduplication — works only with correct hashCode + equals
        Set<Employee> uniqueEmployees = new HashSet<>();
        uniqueEmployees.add(e1);
        uniqueEmployees.add(e2);   // duplicate — not added
        uniqueEmployees.add(e3);
        uniqueEmployees.add(e4);

        System.out.println("\nUnique employees: " + uniqueEmployees.size()); // 3

        // HashMap lookup
        Map<Employee, String> roles = new HashMap<>();
        roles.put(e1, "Tech Lead");
        roles.put(e3, "Senior Dev");

        // Lookup with a logically equal key (different object, same data)
        Employee lookupKey = new Employee(101, "Sonu", 75000, "Engineering");
        System.out.println("Role: " + roles.get(lookupKey));  // Tech Lead

        // toString
        System.out.println("\n" + e1);
        System.out.println(e3);
    }
}
```

Output:
```
e1.equals(e2): true
e1.equals(e3): false
e1 == e2:      false

Unique employees: 3
Role: Tech Lead

Employee{id=101, name='Sonu', dept='Engineering', salary=75000.00}
Employee{id=102, name='Monu', dept='Engineering', salary=82000.00}
```

---

## Quick Summary

| Method | When to Override | Key Rule |
|--------|-----------------|----------|
| `toString()` | Always | Include all fields useful for debugging |
| `equals()` | When logical equality matters | Follow the 4-step pattern; use `Objects.equals` for fields |
| `hashCode()` | Always with `equals()` | Use `Objects.hash()` with same fields as `equals()` |
| `getClass()` | Rarely overridden | Use for runtime type info and strict equality |
| `clone()` | When copying is needed | Prefer copy constructor over `clone()` |
| `wait()`/`notify()` | Rarely — use higher-level concurrency | Must be in `synchronized` block |
| `finalize()` | Never | Deprecated — use `AutoCloseable` instead |

---

## What's Next

**Module 15** — Inner Classes. Defining a class inside another class — regular inner, static nested, method-local, and anonymous — and when each one makes sense.
