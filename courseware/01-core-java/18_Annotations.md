# Module 18 — Annotations

> **Part D: Core Java Toolkit**
> Prerequisites: Module 08–13 · Time: ~1 hour

---

## What Are Annotations?

An annotation is **metadata attached to code** — a structured note that says something about a class, method, field, or parameter. The annotation itself does nothing at runtime. It is the tools, frameworks, and the compiler that *read* the annotation and act on it.

```java
@Override
public String toString() { ... }

@Deprecated
public void oldMethod() { ... }

@SuppressWarnings("unchecked")
public void riskyMethod() { ... }
```

You have been using `@Override` since Module 10. Now it is time to understand what annotations really are, the built-in ones, how to write your own, and how to read them.

---

## Built-in Java Annotations

### `@Override`

Tells the compiler: "I intend this to override a parent method." The compiler verifies it. If you misspell the method name or use the wrong parameters, the compiler catches it immediately:

```java
public class Manager extends Employee {

    @Override
    public String getRole() {          // compiler confirms this overrides something
        return "Manager";
    }

    @Override
    public String getRoel() {          // compile error — no such method in parent
        return "Manager";
    }
}
```

Without `@Override`, a typo silently creates a new method instead of overriding — a classic bug.

### `@Deprecated`

Marks a method, class, or field as outdated. The compiler emits a warning when code uses it:

```java
public class Employee {

    @Deprecated
    public double getSalaryInDollars() {    // old method, replaced by getSalary()
        return salary / 80.0;
    }

    public double getSalary() {
        return salary;
    }
}
```

Since Java 9, `@Deprecated` also accepts attributes:

```java
@Deprecated(since = "2.0", forRemoval = true)
public void legacyMethod() { ... }
```

`forRemoval = true` tells users this will be removed in a future version — a stronger warning than just "deprecated."

### `@SuppressWarnings`

Tells the compiler to stop emitting a specific warning for the annotated element. Use sparingly — warnings usually mean something:

```java
@SuppressWarnings("unchecked")
public void addToRawList() {
    List list = new ArrayList();    // raw type — normally a warning
    list.add("Sonu");
}

@SuppressWarnings({"unchecked", "deprecation"})
public void multipleWarnings() { ... }
```

### `@FunctionalInterface`

Marks an interface as a functional interface (exactly one abstract method). The compiler enforces this — it rejects any attempt to add a second abstract method:

```java
@FunctionalInterface
public interface SalaryCalculator {
    double calculate(double baseSalary);   // exactly one abstract method
    // double anotherMethod();             // compile error — violates @FunctionalInterface
    default double calculateWithBonus(double base) { return calculate(base) * 1.1; }  // ok
}
```

Covered fully in Module 20.

### `@SafeVarargs`

Suppresses unchecked warnings on varargs methods with generic types. Used on methods you are certain are type-safe:

```java
@SafeVarargs
public final void processEmployees(List<Employee>... lists) {
    for (List<Employee> list : lists) {
        list.forEach(Employee::display);
    }
}
```

---

## Meta-Annotations

Annotations that annotate other annotations — they define how annotations behave.

### `@Retention`

Controls **when** the annotation information is available:

| Value | When Available |
|-------|---------------|
| `RetentionPolicy.SOURCE` | Compiler only — discarded after compilation |
| `RetentionPolicy.CLASS` | In the `.class` file — not available at runtime (default) |
| `RetentionPolicy.RUNTIME` | Available at runtime via Reflection |

```java
@Retention(RetentionPolicy.RUNTIME)   // must be RUNTIME to read it via Reflection
public @interface AuditLog { ... }
```

### `@Target`

Specifies **where** the annotation can be applied:

| Value | Where |
|-------|-------|
| `ElementType.TYPE` | Class, interface, enum |
| `ElementType.METHOD` | Method |
| `ElementType.FIELD` | Field |
| `ElementType.PARAMETER` | Method parameter |
| `ElementType.CONSTRUCTOR` | Constructor |
| `ElementType.LOCAL_VARIABLE` | Local variable |
| `ElementType.ANNOTATION_TYPE` | Another annotation |
| `ElementType.PACKAGE` | Package declaration |

```java
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AuditLog { ... }
```

### `@Documented`

Includes the annotation in generated Javadoc:

```java
@Documented
public @interface AuditLog { ... }
```

### `@Inherited`

When applied to a class, the annotation is automatically inherited by subclasses:

```java
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable { }

@Auditable
public class Employee { }

public class Manager extends Employee { }
// Manager.class.isAnnotationPresent(Auditable.class) → true
```

---

## Writing Custom Annotations

An annotation is declared with `@interface`:

```java
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface AuditLog {
    String action()  default "UNKNOWN";
    String module()  default "GENERAL";
    boolean enabled() default true;
}
```

**Annotation elements** look like methods but are really attribute declarations:
- Can have default values with `default`
- Types allowed: primitives, `String`, `Class`, enums, other annotations, and arrays of these
- A single element named `value()` can be used without the name: `@AuditLog("CREATE")`

### Using the Custom Annotation

```java
public class EmployeeService {

    @AuditLog(action = "CREATE", module = "Employee")
    public void addEmployee(Employee emp) {
        System.out.println("Adding: " + emp.getName());
    }

    @AuditLog(action = "UPDATE", module = "Employee")
    public void updateSalary(int id, double salary) {
        System.out.println("Updating salary for ID: " + id);
    }

    @AuditLog(action = "DELETE", module = "Employee", enabled = false)
    public void removeEmployee(int id) {
        System.out.println("Removing employee ID: " + id);
    }

    public void getEmployee(int id) {
        System.out.println("Fetching employee ID: " + id);   // no annotation
    }
}
```

---

## Reading Annotations with Reflection

Annotations with `RetentionPolicy.RUNTIME` can be read at runtime using the Reflection API:

```java
import java.lang.reflect.*;

public class AuditProcessor {

    public static void processAnnotations(Object target) {
        Class<?> clazz = target.getClass();

        System.out.println("Auditing methods of: " + clazz.getSimpleName());
        System.out.println("-".repeat(55));

        for (Method method : clazz.getDeclaredMethods()) {

            if (method.isAnnotationPresent(AuditLog.class)) {
                AuditLog audit = method.getAnnotation(AuditLog.class);

                if (audit.enabled()) {
                    System.out.printf("%-20s | action=%-10s | module=%s%n",
                                      method.getName(), audit.action(), audit.module());
                } else {
                    System.out.printf("%-20s | DISABLED%n", method.getName());
                }
            } else {
                System.out.printf("%-20s | (not audited)%n", method.getName());
            }
        }
    }

    public static void main(String[] args) {
        EmployeeService service = new EmployeeService();
        processAnnotations(service);
    }
}
```

Output:
```
Auditing methods of: EmployeeService
-------------------------------------------------------
addEmployee          | action=CREATE     | module=Employee
updateSalary         | action=UPDATE     | module=Employee
removeEmployee       | DISABLED
getEmployee          | (not audited)
```

---

## Practical Example — Validation Annotation

A real-world pattern: custom annotations for field validation, similar to what Bean Validation (Hibernate Validator) does:

```java
// Define annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotBlank {
    String message() default "Field must not be blank.";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MinValue {
    double value();
    String message() default "Value is below minimum.";
}
```

```java
// Annotate the bean
public class EmployeeRequest {

    @NotBlank(message = "Employee name is required.")
    private String name;

    @MinValue(value = 15000, message = "Salary must be at least 15000.")
    private double salary;

    @NotBlank(message = "Department is required.")
    private String department;

    public EmployeeRequest(String name, double salary, String department) {
        this.name       = name;
        this.salary     = salary;
        this.department = department;
    }

    // getters...
}
```

```java
// Validation processor using Reflection
import java.lang.reflect.*;
import java.util.*;

public class Validator {

    public static List<String> validate(Object obj) {
        List<String> errors = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);   // access private fields

            try {
                Object value = field.get(obj);

                if (field.isAnnotationPresent(NotBlank.class)) {
                    NotBlank nb = field.getAnnotation(NotBlank.class);
                    if (value == null || value.toString().isBlank()) {
                        errors.add(field.getName() + ": " + nb.message());
                    }
                }

                if (field.isAnnotationPresent(MinValue.class)) {
                    MinValue mv = field.getAnnotation(MinValue.class);
                    if (value instanceof Number && ((Number) value).doubleValue() < mv.value()) {
                        errors.add(field.getName() + ": " + mv.message());
                    }
                }

            } catch (IllegalAccessException e) {
                errors.add("Could not validate field: " + field.getName());
            }
        }
        return errors;
    }

    public static void main(String[] args) {
        EmployeeRequest valid = new EmployeeRequest("Sonu", 75000, "Engineering");
        EmployeeRequest invalid = new EmployeeRequest("", 5000, "");

        List<String> validErrors   = validate(valid);
        List<String> invalidErrors = validate(invalid);

        System.out.println("Valid request errors:   " + validErrors);
        System.out.println("Invalid request errors: " + invalidErrors);
    }
}
```

Output:
```
Valid request errors:   []
Invalid request errors: [name: Employee name is required., salary: Salary must be at least 15000., department: Department is required.]
```

This is essentially what Spring's `@Valid` + Hibernate Validator does under the hood — annotations on fields, a processor that reads them via Reflection.

---

## Annotations in Frameworks (Context)

Once you move to Spring Boot, you will use annotations constantly. Everything is annotation-driven:

| Annotation | Framework | Purpose |
|-----------|-----------|---------|
| `@Component` | Spring | Mark class as a Spring-managed bean |
| `@Autowired` | Spring | Inject dependency |
| `@RestController` | Spring MVC | Handle HTTP requests |
| `@GetMapping` | Spring MVC | Map GET requests to a method |
| `@Transactional` | Spring | Wrap method in a DB transaction |
| `@Entity` | JPA/Hibernate | Map class to a database table |
| `@Column` | JPA/Hibernate | Map field to a table column |
| `@NotNull`, `@Size` | Bean Validation | Validate field values |

Understanding how annotations work at the Java level makes all of these immediately intuitive.

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Annotation | Metadata attached to code — does nothing by itself |
| `@Override` | Compiler verifies you are actually overriding |
| `@Deprecated` | Mark as outdated — compiler warns callers |
| `@SuppressWarnings` | Silence specific compiler warnings |
| `@FunctionalInterface` | Enforce exactly one abstract method |
| `@Retention(RUNTIME)` | Required to read annotation at runtime via Reflection |
| `@Target` | Restricts where annotation can be applied |
| `@interface` | Syntax for declaring a custom annotation |
| Reflection | API to inspect and read annotations at runtime |

---

## What's Next

**Part E — Java 8 Features.** Module 19 starts with Lambda Expressions — the single biggest change in Java's 20-year history.
