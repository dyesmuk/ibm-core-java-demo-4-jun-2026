# Module 19 — Lambda Expressions

> **Part E: Java 8 Features**
> Prerequisites: Module 15 (Inner Classes), Module 17 (Exception Handling) · Time: ~1.5 hours

---

## The Problem Lambdas Solve

Before Java 8, passing behaviour as an argument meant writing an anonymous inner class. Even for the simplest case:

```java
// Sort employees by salary — pre Java 8
Arrays.sort(employees, new Comparator<Employee>() {
    @Override
    public int compare(Employee a, Employee b) {
        return Double.compare(a.getSalary(), b.getSalary());
    }
});
```

Six lines. One `@Override`. A class declaration. All to say: **compare by salary**.

With a lambda:

```java
Arrays.sort(employees, (a, b) -> Double.compare(a.getSalary(), b.getSalary()));
```

One line. Same result. That is the point — lambdas let you express behaviour concisely, inline, without the ceremony.

---

## What Is a Lambda?

A lambda expression is an **anonymous function** — a block of code with parameters and a body, but no name and no class.

```
(parameters) -> body
```

A lambda can only be used where a **functional interface** is expected — an interface with exactly one abstract method. The lambda provides the implementation of that method.

---

## Lambda Syntax

### Full form

```java
(Employee a, Employee b) -> {
    return Double.compare(a.getSalary(), b.getSalary());
}
```

### Type inference — types can be omitted

```java
(a, b) -> {
    return Double.compare(a.getSalary(), b.getSalary());
}
```

### Single expression — `return` and braces can be omitted

```java
(a, b) -> Double.compare(a.getSalary(), b.getSalary())
```

### Single parameter — parentheses optional

```java
name -> name.toUpperCase()
```

### No parameters — empty parentheses required

```java
() -> System.out.println("Processing payroll...")
```

### Multi-line body — braces and `return` required

```java
(Employee e) -> {
    double bonus = e.getSalary() * 0.10;
    System.out.println(e.getName() + " bonus: " + bonus);
    return bonus;
}
```

---

## Evolution: Anonymous Class → Lambda

Let's walk through the same behaviour in four stages:

**Stage 1 — Named class (most verbose)**

```java
public class SalaryComparator implements Comparator<Employee> {
    @Override
    public int compare(Employee a, Employee b) {
        return Double.compare(a.getSalary(), b.getSalary());
    }
}

Arrays.sort(employees, new SalaryComparator());
```

**Stage 2 — Anonymous class**

```java
Arrays.sort(employees, new Comparator<Employee>() {
    @Override
    public int compare(Employee a, Employee b) {
        return Double.compare(a.getSalary(), b.getSalary());
    }
});
```

**Stage 3 — Lambda (full form)**

```java
Arrays.sort(employees, (Employee a, Employee b) -> {
    return Double.compare(a.getSalary(), b.getSalary());
});
```

**Stage 4 — Lambda (concise)**

```java
Arrays.sort(employees, (a, b) -> Double.compare(a.getSalary(), b.getSalary()));
```

All four do exactly the same thing. The lambda is not magic — it is just a concise syntax for providing an implementation of a single-method interface.

---

## Lambdas with Built-in Functional Interfaces

Java 8 ships with a set of ready-to-use functional interfaces in `java.util.function`. These are covered in depth in Module 20 — but here is a quick taste:

```java
// Runnable — no parameters, no return value
Runnable task = () -> System.out.println("Payroll processing started.");
task.run();

// Predicate<T> — takes T, returns boolean
Predicate<Employee> isSenior = e -> e.getSalary() > 80000;
System.out.println(isSenior.test(new Employee(101, "Sonu", 75000, "Eng")));  // false
System.out.println(isSenior.test(new Employee(102, "Monu", 82000, "Eng")));  // true

// Function<T, R> — takes T, returns R
Function<Employee, String> getName = e -> e.getName().toUpperCase();
System.out.println(getName.apply(new Employee(103, "Tonu", 55000, "HR")));   // TONU

// Consumer<T> — takes T, returns nothing
Consumer<Employee> printer = e ->
    System.out.printf("%-10s %.2f%n", e.getName(), e.getSalary());
printer.accept(new Employee(104, "Ponu", 91000, "Finance"));   // Ponu       91000.00
```

---

## Capturing Variables

A lambda can reference variables from its enclosing scope — but those variables must be **effectively final** (either declared `final` or never reassigned after being read by the lambda).

```java
double taxRate = 0.10;   // effectively final — never reassigned

Function<Double, Double> netCalc = salary -> salary - (salary * taxRate);

System.out.println(netCalc.apply(75000.0));  // 67500.0

// taxRate = 0.15;   // uncommenting this would cause a compile error
                     // because the lambda already captured it
```

This restriction exists because lambdas may run in a different thread or at a different time — a variable that changes unpredictably after being captured would cause race conditions.

```java
// Instance fields and static fields can be captured and are not restricted:
public class PayrollCalculator {
    private double taxRate = 0.10;   // instance field

    public void calculate(List<Employee> employees) {
        employees.forEach(e -> {
            double net = e.getSalary() - (e.getSalary() * taxRate);  // taxRate fine here
            System.out.printf("%-10s Net: %.2f%n", e.getName(), net);
        });
    }
}
```

---

## Method References

A method reference is an even more concise lambda when the lambda body simply calls an existing method. Four types:

### 1. Static method reference

```java
// Lambda
Function<String, Integer> parser = s -> Integer.parseInt(s);

// Method reference
Function<String, Integer> parser = Integer::parseInt;
```

### 2. Instance method reference on a specific instance

```java
Employee emp = new Employee(101, "Sonu", 75000, "Engineering");

// Lambda
Supplier<String> getName = () -> emp.getName();

// Method reference
Supplier<String> getName = emp::getName;
```

### 3. Instance method reference on an arbitrary instance of a type

```java
// Lambda — the parameter IS the instance the method is called on
Function<Employee, String> getRole = e -> e.getRole();

// Method reference
Function<Employee, String> getRole = Employee::getRole;
```

This is the most commonly seen in Stream pipelines:

```java
employees.stream()
         .map(Employee::getName)          // same as e -> e.getName()
         .forEach(System.out::println);   // same as s -> System.out.println(s)
```

### 4. Constructor reference

```java
// Lambda
BiFunction<String, Double, Employee> creator =
    (name, salary) -> new Employee(name, salary);

// Constructor reference
BiFunction<String, Double, Employee> creator = Employee::new;
```

---

## Lambdas Are Not Anonymous Classes

They look similar but have important differences:

| Aspect | Anonymous Class | Lambda |
|--------|----------------|--------|
| `this` | Refers to the anonymous class instance | Refers to the enclosing class instance |
| New scope | Creates a new scope | Shares the enclosing scope |
| State | Can have instance fields | Stateless |
| Interface | Can implement multi-method interfaces | Only single-abstract-method interfaces |
| Compilation | Generates a `.class` file | Uses `invokedynamic` — no class file |

```java
public class ScopeDemo {
    private String name = "Outer";

    public void demo() {
        // Anonymous class — 'this' refers to the anonymous class
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println(this.getClass().getSimpleName()); // ScopeDemo$1
            }
        };

        // Lambda — 'this' refers to the enclosing ScopeDemo instance
        Runnable r2 = () -> System.out.println(this.name);  // Outer
    }
}
```

---

## Practical Example — Employee Processing with Lambdas

```java
import java.util.*;

public class LambdaDemo {
    public static void main(String[] args) {

        List<Employee> employees = new ArrayList<>(Arrays.asList(
            new Employee(101, "Sonu",  75000, "Engineering"),
            new Employee(102, "Monu",  82000, "Engineering"),
            new Employee(103, "Tonu",  55000, "HR"),
            new Employee(104, "Ponu",  91000, "Finance"),
            new Employee(105, "Gonu",  68000, "Operations")
        ));

        // Sort by salary descending
        employees.sort((a, b) -> Double.compare(b.getSalary(), a.getSalary()));
        System.out.println("=== By Salary (Desc) ===");
        employees.forEach(e ->
            System.out.printf("%-10s %.2f%n", e.getName(), e.getSalary()));

        // Sort by name
        employees.sort((a, b) -> a.getName().compareTo(b.getName()));
        System.out.println("\n=== By Name (A-Z) ===");
        employees.forEach(e -> System.out.println(e.getName()));

        // Filter and print with a Predicate
        System.out.println("\n=== Salary > 70000 ===");
        Predicate<Employee> highEarner = e -> e.getSalary() > 70000;
        employees.stream()
                 .filter(highEarner)
                 .forEach(e -> System.out.println(e.getName() + " - " + e.getSalary()));

        // Apply a raise using a Consumer
        System.out.println("\n=== After 10% Raise ===");
        Consumer<Employee> applyRaise = e -> e.applyRaise(10);
        employees.forEach(applyRaise);
        employees.forEach(e ->
            System.out.printf("%-10s %.2f%n", e.getName(), e.getSalary()));

        // Extract names using a Function + method reference
        System.out.println("\n=== Names Only ===");
        employees.stream()
                 .map(Employee::getName)
                 .forEach(System.out::println);
    }
}
```

Output:
```
=== By Salary (Desc) ===
Ponu       91000.00
Monu       82000.00
Sonu       75000.00
Gonu       68000.00
Tonu       55000.00

=== By Name (A-Z) ===
Gonu
Monu
Ponu
Sonu
Tonu

=== Salary > 70000 ===
Sonu - 75000.0
Monu - 82000.0
Ponu - 91000.0

=== After 10% Raise ===
Gonu       74800.00
Monu       90200.00
Ponu       100100.00
Sonu       82500.00
Tonu       60500.00

=== Names Only ===
Gonu
Monu
Ponu
Sonu
Tonu
```

---

## Composing Lambdas

Lambdas can be composed — chained together to build more complex behaviour from simpler pieces:

```java
// Two predicates
Predicate<Employee> isEngineering = e -> e.getDepartment().equals("Engineering");
Predicate<Employee> isHighSalary  = e -> e.getSalary() > 75000;

// Combine with and / or / negate
Predicate<Employee> engineeringHighEarner = isEngineering.and(isHighSalary);
Predicate<Employee> eitherCondition       = isEngineering.or(isHighSalary);
Predicate<Employee> notEngineering        = isEngineering.negate();

employees.stream()
         .filter(engineeringHighEarner)
         .forEach(e -> System.out.println(e.getName()));  // Monu (82000, Engineering)
```

Function composition with `andThen` and `compose`:

```java
Function<Double, Double> applyTax   = salary -> salary * 0.90;   // deduct 10% tax
Function<Double, Double> applyBonus = salary -> salary * 1.10;   // add 10% bonus

// andThen — apply applyTax first, then applyBonus
Function<Double, Double> taxThenBonus = applyTax.andThen(applyBonus);
System.out.println(taxThenBonus.apply(75000.0));  // 74250.0  (75000 * 0.9 * 1.1)

// compose — apply applyBonus first, then applyTax
Function<Double, Double> bonusThenTax = applyTax.compose(applyBonus);
System.out.println(bonusThenTax.apply(75000.0));  // 74250.0  (75000 * 1.1 * 0.9 — same here)
```

---

## Quick Summary

| Syntax | When to Use |
|--------|------------|
| `() -> expr` | No params, returns value |
| `() -> { stmts; }` | No params, multiple statements |
| `x -> expr` | One param, returns value |
| `(x, y) -> expr` | Two params, returns value |
| `(x, y) -> { stmts; return val; }` | Two params, multiple statements |
| `ClassName::staticMethod` | Static method reference |
| `instance::method` | Specific instance method reference |
| `ClassName::instanceMethod` | Arbitrary instance method reference |
| `ClassName::new` | Constructor reference |

| Concept | Key Point |
|---------|-----------|
| Lambda requires | A functional interface — exactly one abstract method |
| Captured variables | Must be effectively final |
| `this` in lambda | Refers to enclosing class, not the lambda itself |
| Method reference | Shorthand for a lambda that just calls an existing method |
| Composition | `and()`, `or()`, `negate()`, `andThen()`, `compose()` |

---

## What's Next

**Module 20** — Functional Interfaces. The full catalogue of `java.util.function` interfaces — Predicate, Function, Consumer, Supplier, BiFunction and more — with composition patterns.
