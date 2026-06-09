# Module 20 — Functional Interfaces

> **Part E: Java 8 Features**
> Prerequisites: Module 19 · Time: ~1.5 hours

---

## What Is a Functional Interface?

A functional interface has **exactly one abstract method**. That single method is the contract — the lambda you write provides the implementation of that method.

```java
@FunctionalInterface
public interface SalaryCalculator {
    double calculate(double baseSalary);   // the one abstract method
}
```

```java
SalaryCalculator withBonus = salary -> salary * 1.10;
SalaryCalculator withTax   = salary -> salary * 0.90;

System.out.println(withBonus.calculate(75000));  // 82500.0
System.out.println(withTax.calculate(75000));    // 67500.0
```

`@FunctionalInterface` is optional but recommended — the compiler then enforces the single-abstract-method rule and will report an error if you accidentally add a second abstract method.

---

## `java.util.function` Package

Java 8 ships with 43 functional interfaces in `java.util.function`. Most are specialisations of a small core set. You need to know these well — they are used everywhere in streams, collections, and modern APIs.

---

## `Predicate<T>` — Test a condition

**Method:** `boolean test(T t)`

Takes a value, returns `true` or `false`.

```java
Predicate<Employee> isActive      = e -> e.getStatus() == EmployeeStatus.ACTIVE;
Predicate<Employee> isSenior      = e -> e.getSalary() > 80000;
Predicate<Employee> isEngineering = e -> "Engineering".equals(e.getDepartment());
```

```java
Employee sonu = new Employee(101, "Sonu", 75000, "Engineering");
Employee ponu = new Employee(104, "Ponu", 91000, "Finance");

System.out.println(isActive.test(sonu));      // true
System.out.println(isSenior.test(sonu));      // false
System.out.println(isSenior.test(ponu));      // true
```

### Predicate Composition

```java
// AND — both must pass
Predicate<Employee> seniorEngineer = isEngineering.and(isSenior);

// OR — at least one must pass
Predicate<Employee> seniorOrEngineering = isEngineering.or(isSenior);

// NOT — inverts the predicate
Predicate<Employee> notEngineering = isEngineering.negate();

// Static helper — Predicate.not() (Java 11+)
Predicate<String> notBlank = Predicate.not(String::isBlank);
```

```java
List<Employee> employees = List.of(
    new Employee(101, "Sonu",  75000, "Engineering"),
    new Employee(102, "Monu",  82000, "Engineering"),
    new Employee(103, "Tonu",  55000, "HR"),
    new Employee(104, "Ponu",  91000, "Finance"),
    new Employee(105, "Gonu",  68000, "Operations")
);

System.out.println("Senior engineers:");
employees.stream()
         .filter(seniorEngineer)
         .forEach(e -> System.out.println("  " + e.getName()));
// Monu (82000, Engineering)
```

### `BiPredicate<T, U>`

Takes two arguments:

```java
BiPredicate<Employee, Double> earnsMoreThan = (e, threshold) -> e.getSalary() > threshold;

System.out.println(earnsMoreThan.test(sonu, 70000.0));  // true
System.out.println(earnsMoreThan.test(sonu, 80000.0));  // false
```

---

## `Function<T, R>` — Transform a value

**Method:** `R apply(T t)`

Takes a value of type T, returns a value of type R.

```java
Function<Employee, String>  getName       = Employee::getName;
Function<Employee, Double>  getSalary     = Employee::getSalary;
Function<String, String>    toUpperCase   = String::toUpperCase;
Function<Double, String>    formatSalary  = s -> String.format("%.2f", s);
Function<Employee, String>  salaryLabel   = e ->
    e.getName() + ": " + String.format("%.2f", e.getSalary());
```

```java
Employee monu = new Employee(102, "Monu", 82000, "Engineering");

System.out.println(getName.apply(monu));       // Monu
System.out.println(getSalary.apply(monu));     // 82000.0
System.out.println(salaryLabel.apply(monu));   // Monu: 82000.00
```

### Function Composition

```java
Function<Double, Double> deductTax    = s -> s * 0.90;
Function<Double, Double> addAllowance = s -> s + 5000;

// andThen — deductTax first, then addAllowance
Function<Double, Double> netWithAllowance = deductTax.andThen(addAllowance);
System.out.println(netWithAllowance.apply(75000.0));  // 72500.0  (75000*0.9 + 5000)

// compose — addAllowance first, then deductTax
Function<Double, Double> allowanceThenTax = deductTax.compose(addAllowance);
System.out.println(allowanceThenTax.apply(75000.0));  // 72000.0  ((75000+5000)*0.9)
```

### `Function.identity()`

Returns a function that just returns its input — useful as a neutral placeholder:

```java
Function<Employee, Employee> noOp = Function.identity();
```

### `BiFunction<T, U, R>`

Takes two arguments:

```java
BiFunction<Employee, Double, String> appraisalResult =
    (e, targetSalary) -> e.getSalary() >= targetSalary
        ? e.getName() + " meets target"
        : e.getName() + " below target";

System.out.println(appraisalResult.apply(monu, 80000.0));  // Monu meets target
System.out.println(appraisalResult.apply(monu, 90000.0));  // Monu below target
```

### `UnaryOperator<T>` and `BinaryOperator<T>`

Specialisations of `Function` where input and output are the same type:

```java
UnaryOperator<Double>  applyTax   = salary -> salary * 0.90;
BinaryOperator<Double> totalPay   = (base, bonus) -> base + bonus;

System.out.println(applyTax.apply(75000.0));         // 67500.0
System.out.println(totalPay.apply(75000.0, 7500.0)); // 82500.0
```

---

## `Consumer<T>` — Do something with a value

**Method:** `void accept(T t)`

Takes a value, does something with it, returns nothing.

```java
Consumer<Employee> printEmployee =
    e -> System.out.printf("%-5d %-12s %-15s %.2f%n",
                           e.getId(), e.getName(), e.getDepartment(), e.getSalary());

Consumer<Employee> logEmployee =
    e -> System.out.println("[LOG] Processing: " + e.getName());

Consumer<Employee> applyStandardRaise = e -> e.applyRaise(10);
```

```java
Employee tonu = new Employee(103, "Tonu", 55000, "HR");

printEmployee.accept(tonu);
logEmployee.accept(tonu);
applyStandardRaise.accept(tonu);
System.out.println(tonu.getSalary());   // 60500.0
```

### `andThen` — Chain consumers

```java
Consumer<Employee> logThenPrint = logEmployee.andThen(printEmployee);
logThenPrint.accept(tonu);
// [LOG] Processing: Tonu
// 103   Tonu         HR              60500.00
```

### `BiConsumer<T, U>`

Takes two arguments:

```java
BiConsumer<Employee, String> assignProject =
    (e, project) -> System.out.println(e.getName() + " assigned to: " + project);

assignProject.accept(tonu, "Project Beta");
// Tonu assigned to: Project Beta
```

---

## `Supplier<T>` — Provide a value

**Method:** `T get()`

Takes nothing, returns a value. Used for lazy evaluation and object creation.

```java
Supplier<Employee> defaultEmployee =
    () -> new Employee(0, "Placeholder", 0, "Unassigned");

Supplier<String> timestamp =
    () -> java.time.LocalDateTime.now().toString();

Supplier<List<Employee>> emptyList = ArrayList::new;
```

```java
Employee placeholder = defaultEmployee.get();
System.out.println(placeholder);

System.out.println(timestamp.get());

List<Employee> list = emptyList.get();
list.add(new Employee(101, "Sonu", 75000, "Engineering"));
```

### Lazy Evaluation with Supplier

```java
// Without Supplier — expensive object always created, even when not needed
public Employee getEmployeeOrDefault(int id, Employee defaultValue) {
    Employee found = find(id);
    return found != null ? found : defaultValue;   // defaultValue always evaluated
}

// With Supplier — expensive default only created if needed
public Employee getEmployeeOrDefault(int id, Supplier<Employee> defaultSupplier) {
    Employee found = find(id);
    return found != null ? found : defaultSupplier.get();  // get() only called if needed
}

// Call:
Employee emp = service.getEmployeeOrDefault(999,
    () -> new Employee(0, "Guest", 0, "None"));   // only created if 999 not found
```

---

## Putting It All Together

```java
import java.util.*;
import java.util.function.*;

public class FunctionalDemo {

    public static <T> List<T> filter(List<T> list, Predicate<T> pred) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (pred.test(item)) result.add(item);
        }
        return result;
    }

    public static <T, R> List<R> transform(List<T> list, Function<T, R> fn) {
        List<R> result = new ArrayList<>();
        for (T item : list) result.add(fn.apply(item));
        return result;
    }

    public static <T> void process(List<T> list, Consumer<T> action) {
        for (T item : list) action.accept(item);
    }

    public static void main(String[] args) {

        List<Employee> employees = Arrays.asList(
            new Employee(101, "Sonu",  75000, "Engineering"),
            new Employee(102, "Monu",  82000, "Engineering"),
            new Employee(103, "Tonu",  55000, "HR"),
            new Employee(104, "Ponu",  91000, "Finance"),
            new Employee(105, "Gonu",  68000, "Operations")
        );

        // Filter — senior employees
        List<Employee> senior = filter(employees, e -> e.getSalary() > 70000);
        System.out.println("Senior employees: " + senior.size());  // 3

        // Transform — get names
        List<String> names = transform(employees, Employee::getName);
        System.out.println("Names: " + names);

        // Transform — get salary labels
        List<String> labels = transform(employees,
            e -> String.format("%s: %.0f", e.getName(), e.getSalary()));
        System.out.println("Labels: " + labels);

        // Process — apply raise to senior employees
        process(senior, e -> e.applyRaise(15));
        System.out.println("\nAfter 15% raise to senior employees:");
        process(employees, e ->
            System.out.printf("  %-10s %.2f%n", e.getName(), e.getSalary()));
    }
}
```

Output:
```
Senior employees: 3
Names: [Sonu, Monu, Tonu, Ponu, Gonu]
Labels: [Sonu: 75000, Monu: 82000, Tonu: 55000, Ponu: 91000, Gonu: 68000]

After 15% raise to senior employees:
  Sonu       86250.00
  Monu       94300.00
  Tonu       55000.00
  Ponu       104650.00
  Gonu       68000.00
```

---

## Primitive Specialisations

To avoid autoboxing overhead, Java 8 provides primitive variants of the core interfaces:

| Generic | `int` variant | `long` variant | `double` variant |
|---------|--------------|----------------|-----------------|
| `Predicate<T>` | `IntPredicate` | `LongPredicate` | `DoublePredicate` |
| `Function<T,R>` | `IntFunction<R>` | `LongFunction<R>` | `DoubleFunction<R>` |
| `Consumer<T>` | `IntConsumer` | `LongConsumer` | `DoubleConsumer` |
| `Supplier<T>` | `IntSupplier` | `LongSupplier` | `DoubleSupplier` |
| `UnaryOperator<T>` | `IntUnaryOperator` | `LongUnaryOperator` | `DoubleUnaryOperator` |

```java
IntPredicate  isAdultAge  = age -> age >= 18;
DoubleConsumer printSalary = s -> System.out.printf("%.2f%n", s);
IntSupplier   defaultId   = () -> 0;

System.out.println(isAdultAge.test(25));    // true
printSalary.accept(75000.0);                // 75000.00
System.out.println(defaultId.getAsInt());   // 0
```

Use these when working with large numbers of primitives — they avoid boxing and unboxing overhead.

---

## Writing Your Own Functional Interface

Any time you need a behaviour that doesn't fit the standard ones:

```java
@FunctionalInterface
public interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}

@FunctionalInterface
public interface EmployeeValidator {
    ValidationResult validate(Employee employee);
}
```

```java
TriFunction<String, Double, String, Employee> creator =
    (name, salary, dept) -> new Employee(0, name, salary, dept);

Employee gonu = creator.apply("Gonu", 68000.0, "Operations");
System.out.println(gonu);
```

---

## Quick Reference

| Interface | Method | Takes | Returns | Use For |
|-----------|--------|-------|---------|---------|
| `Predicate<T>` | `test(T)` | T | boolean | Filtering, conditions |
| `BiPredicate<T,U>` | `test(T,U)` | T, U | boolean | Two-arg conditions |
| `Function<T,R>` | `apply(T)` | T | R | Transforming values |
| `BiFunction<T,U,R>` | `apply(T,U)` | T, U | R | Two-arg transforms |
| `UnaryOperator<T>` | `apply(T)` | T | T | Transform same type |
| `BinaryOperator<T>` | `apply(T,T)` | T, T | T | Combine two same-type values |
| `Consumer<T>` | `accept(T)` | T | void | Side effects, printing, saving |
| `BiConsumer<T,U>` | `accept(T,U)` | T, U | void | Two-arg side effects |
| `Supplier<T>` | `get()` | — | T | Lazy creation, defaults |
| `Runnable` | `run()` | — | void | Background tasks |

---

## What's Next

**Module 21** — Stream API. Functional-style processing of collections — filter, map, reduce, collect — all backed by the functional interfaces you just learned.
