# Module 17 — Exception Handling

> **Part D: Core Java Toolkit**
> Prerequisites: Module 08–13 · Time: ~2 hours

---

## What Is an Exception?

An exception is an **event that disrupts the normal flow of program execution**. When something goes wrong at runtime — a file is missing, a value is null, a number is divided by zero — the JVM creates an exception object describing the problem and throws it.

If no code handles it, the JVM prints a stack trace and terminates the program. Exception handling gives you a way to intercept that, respond intelligently, and keep the application running.

```
Normal flow:    step 1 → step 2 → step 3 → step 4
Exception flow: step 1 → step 2 → [exception thrown] → handler → recovery
                                  ↑
                                  execution jumps here
```

---

## Exception Hierarchy

```
Throwable
├── Error                          — JVM-level problems, do NOT catch these
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── ...
│
└── Exception                      — application-level problems
    ├── IOException                — checked
    ├── SQLException               — checked
    ├── ParseException             — checked
    ├── ...
    │
    └── RuntimeException           — unchecked
        ├── NullPointerException
        ├── ArrayIndexOutOfBoundsException
        ├── ClassCastException
        ├── IllegalArgumentException
        ├── NumberFormatException
        ├── ArithmeticException
        └── ...
```

### Checked vs Unchecked

| Type | Examples | Compiler requires? | Cause |
|------|----------|-------------------|-------|
| **Checked** | `IOException`, `SQLException` | Yes — must handle or declare | External factors (file missing, DB down) |
| **Unchecked** | `NullPointerException`, `IllegalArgumentException` | No | Programming mistakes |
| **Error** | `OutOfMemoryError` | No | JVM failure — don't catch |

**Checked exceptions:** The compiler forces you to deal with them. This is intentional — if a file might not exist, you cannot pretend otherwise and ship code that silently crashes.

**Unchecked exceptions:** These represent bugs — null dereferences, bad array indices, invalid arguments. Fix the code, don't catch them blindly.

---

## `try-catch`

```java
try {
    // code that might throw
} catch (ExceptionType name) {
    // handle it
}
```

```java
public static double parseSalary(String input) {
    try {
        double salary = Double.parseDouble(input);
        return salary;
    } catch (NumberFormatException e) {
        System.out.println("Invalid salary value: '" + input + "'");
        return 0.0;
    }
}

System.out.println(parseSalary("75000.50"));  // 75000.5
System.out.println(parseSalary("seventy-five thousand"));  // Invalid salary value: '...'  → 0.0
```

---

## `finally`

The `finally` block **always runs** — whether an exception was thrown or not, whether it was caught or not. Used for cleanup that must happen regardless.

```java
public static void readEmployeeFile(String path) {
    FileReader reader = null;
    try {
        reader = new FileReader(path);
        // ... read file ...
        System.out.println("File read successfully.");
    } catch (FileNotFoundException e) {
        System.out.println("File not found: " + path);
    } catch (IOException e) {
        System.out.println("Error reading file: " + e.getMessage());
    } finally {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Error closing reader.");
            }
        }
        System.out.println("finally block executed.");
    }
}
```

`finally` runs even if:
- The `try` block completes normally
- An exception is thrown and caught
- An exception is thrown and not caught (finally runs before it propagates up)
- There is a `return` statement in `try` or `catch`

The **one exception** (pun intended): `System.exit()` in the try/catch will prevent `finally` from running.

---

## `try-with-resources` (Java 7+)

The cleaner, modern replacement for `finally`-based cleanup. Any object implementing `AutoCloseable` (which includes `Closeable`) is automatically closed when the `try` block exits:

```java
// Old way — verbose and error-prone
FileReader reader = null;
try {
    reader = new FileReader("employees.txt");
    // read ...
} finally {
    if (reader != null) reader.close();
}

// Modern way — clean, guaranteed close
try (FileReader reader = new FileReader("employees.txt")) {
    // read ...
}   // reader.close() called automatically here
```

Multiple resources:

```java
try (FileReader   reader = new FileReader("employees.csv");
     BufferedReader br   = new BufferedReader(reader)) {

    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}   // both br and reader closed automatically, in reverse order
```

Resources are closed in **reverse order of declaration**. If both the try block and close throw exceptions, the close exception is **suppressed** (added to the primary exception's suppressed list) and the try exception is the one propagated.

---

## Multiple `catch` Blocks

```java
public static Employee loadEmployee(String idStr) {
    try {
        int      id   = Integer.parseInt(idStr);
        Employee emp  = findById(id);            // might return null
        String   name = emp.getName();           // NullPointerException if emp is null
        return emp;

    } catch (NumberFormatException e) {
        System.out.println("ID must be numeric. Got: " + idStr);
    } catch (NullPointerException e) {
        System.out.println("Employee not found for ID: " + idStr);
    } catch (Exception e) {
        // Catch-all — placed last
        System.out.println("Unexpected error: " + e.getMessage());
    }
    return null;
}
```

**Rules:**
- More specific exceptions must come **before** more general ones — the compiler enforces this
- `catch (Exception e)` at the end catches anything not caught above
- Each `catch` is checked top to bottom — first match wins

### Multi-Catch (Java 7+)

When two unrelated exceptions need the same handling:

```java
try {
    // ...
} catch (NumberFormatException | IllegalArgumentException e) {
    System.out.println("Invalid input: " + e.getMessage());
}
```

The variable `e` is implicitly `final` in a multi-catch.

---

## `throw` — Throwing Exceptions Manually

Use `throw` to throw an exception from your own code:

```java
public void setSalary(double salary) {
    if (salary < 0) {
        throw new IllegalArgumentException("Salary cannot be negative: " + salary);
    }
    this.salary = salary;
}

public Employee findById(int id) {
    Employee found = searchDatabase(id);
    if (found == null) {
        throw new NoSuchElementException("No employee with ID: " + id);
    }
    return found;
}
```

Always provide a clear, descriptive message. The message ends up in logs and stack traces — it should tell you exactly what went wrong without reading the code.

---

## `throws` — Declaring Checked Exceptions

If a method has checked exceptions it does not handle, it must declare them with `throws`:

```java
public Employee readFromFile(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        String line = br.readLine();
        // parse and return employee...
    }
    // IOException not caught — declared in throws clause
    // caller is now responsible for handling it
}
```

`throws` is a contract: "I might throw these — caller, deal with them."

```java
// Caller must handle or propagate
public void loadEmployees() {
    try {
        Employee emp = readFromFile("employees.txt");
    } catch (IOException e) {
        System.out.println("Could not load employees: " + e.getMessage());
    }
}
```

---

## Custom Exceptions

Create your own exception types for domain-specific errors. This makes error handling in your application more expressive:

```java
// Checked custom exception
public class EmployeeNotFoundException extends Exception {

    private final int employeeId;

    public EmployeeNotFoundException(int id) {
        super("Employee not found with ID: " + id);
        this.employeeId = id;
    }

    public EmployeeNotFoundException(int id, Throwable cause) {
        super("Employee not found with ID: " + id, cause);
        this.employeeId = id;
    }

    public int getEmployeeId() { return employeeId; }
}

// Unchecked custom exception
public class InvalidSalaryException extends RuntimeException {

    public InvalidSalaryException(double salary) {
        super(String.format("Salary %.2f is not valid. Must be between 15000 and 10000000.", salary));
    }
}
```

```java
// Usage
public Employee getEmployee(int id) throws EmployeeNotFoundException {
    Employee emp = database.find(id);
    if (emp == null) {
        throw new EmployeeNotFoundException(id);
    }
    return emp;
}

public void updateSalary(int id, double newSalary) throws EmployeeNotFoundException {
    if (newSalary < 15000 || newSalary > 10_000_000) {
        throw new InvalidSalaryException(newSalary);   // unchecked — no need to declare
    }
    Employee emp = getEmployee(id);                    // checked — throws EmployeeNotFoundException
    emp.setSalary(newSalary);
}
```

---

## Exception Chaining

When catching one exception and throwing another, preserve the original as the **cause**. This maintains the full diagnostic trail:

```java
public Employee loadFromDatabase(int id) throws EmployeeNotFoundException {
    try {
        return dbConnection.query("SELECT * FROM employees WHERE id = ?", id);
    } catch (SQLException e) {
        // Wrap the technical exception in a domain exception
        throw new EmployeeNotFoundException(id, e);   // e is the cause
    }
}
```

```java
try {
    Employee e = loadFromDatabase(999);
} catch (EmployeeNotFoundException ex) {
    System.out.println(ex.getMessage());
    System.out.println("Caused by: " + ex.getCause().getMessage());
}
```

Without chaining, the `SQLException` is lost — you only see the `EmployeeNotFoundException` and have no idea why. With chaining, the full story is in the exception tree.

---

## Assertions

Assertions are sanity checks for **developer assumptions** during testing and development. They are disabled in production by default.

```java
double salary = calculateSalary();
assert salary >= 0 : "Salary should never be negative, got: " + salary;
// If salary is negative, throws AssertionError with the message
```

Enable assertions when running:
```bash
java -ea com.ems.app.Main    # -ea = enable assertions
```

**Use assertions for:**
- Internal invariants ("this list should never be empty at this point")
- Post-conditions ("the result should always be positive")

**Do not use assertions for:**
- Validating public method arguments — use `IllegalArgumentException`
- Production error handling — they are disabled by default
- Logic that must always run — assertions can be turned off

---

## Exception Best Practices

### 1. Catch specific, not broad

```java
// Bad — swallows everything, hides bugs
catch (Exception e) { }

// Good — handle what you expect
catch (NumberFormatException e) { ... }
catch (IOException e) { ... }
```

### 2. Never swallow exceptions silently

```java
// Bad — the exception disappears
catch (IOException e) {
    // nothing
}

// Good — at minimum, log it
catch (IOException e) {
    System.err.println("Failed to read file: " + e.getMessage());
    // or: logger.error("Failed to read file", e);
}
```

### 3. Don't use exceptions for flow control

```java
// Bad — expensive, confusing
try {
    int value = Integer.parseInt(input);
} catch (NumberFormatException e) {
    value = 0;
}

// Good — check first
int value = isNumeric(input) ? Integer.parseInt(input) : 0;
```

### 4. Clean up resources with try-with-resources

Always. No exceptions (pun intended again).

### 5. Provide meaningful messages

```java
throw new IllegalArgumentException("ID");                     // Bad — useless
throw new IllegalArgumentException("Employee ID must be positive, got: " + id);  // Good
```

---

## Top 10 Exceptions in Production Java

| Exception | Cause | Fix |
|-----------|-------|-----|
| `NullPointerException` | Calling method on null reference | Null check, Optional, safe navigation |
| `ArrayIndexOutOfBoundsException` | Index < 0 or >= length | Check bounds before accessing |
| `ClassCastException` | Invalid downcast | Check with `instanceof` first |
| `NumberFormatException` | Parsing non-numeric string | Validate input before parsing |
| `IllegalArgumentException` | Invalid method argument | Validate args at start of method |
| `IllegalStateException` | Method called at wrong time | Check object state preconditions |
| `StackOverflowError` | Infinite recursion | Add base case to recursive method |
| `OutOfMemoryError` | Heap exhausted | Fix memory leak, increase heap |
| `ConcurrentModificationException` | Collection modified while iterating | Use iterator's remove, or collect changes |
| `IOException` | File or network failure | Try-with-resources + proper catch |

---

## Practical Example — Employee Service with Full Exception Handling

```java
import java.util.*;

public class EmployeeService {

    private Map<Integer, Employee> employees = new HashMap<>();
    private static final double MIN_SALARY = 15000;
    private static final double MAX_SALARY = 10_000_000;

    public void addEmployee(Employee emp) {
        if (emp == null)
            throw new IllegalArgumentException("Employee cannot be null.");
        if (employees.containsKey(emp.getId()))
            throw new IllegalStateException("Employee already exists with ID: " + emp.getId());
        employees.put(emp.getId(), emp);
    }

    public Employee getEmployee(int id) throws EmployeeNotFoundException {
        Employee emp = employees.get(id);
        if (emp == null) throw new EmployeeNotFoundException(id);
        return emp;
    }

    public void updateSalary(int id, double newSalary) throws EmployeeNotFoundException {
        if (newSalary < MIN_SALARY || newSalary > MAX_SALARY)
            throw new InvalidSalaryException(newSalary);
        Employee emp = getEmployee(id);
        emp.setSalary(newSalary);
        System.out.printf("Salary updated: %s → %.2f%n", emp.getName(), newSalary);
    }

    public static void main(String[] args) {

        EmployeeService service = new EmployeeService();

        // Populate
        service.addEmployee(new Employee(101, "Sonu",  75000, "Engineering"));
        service.addEmployee(new Employee(102, "Monu",  82000, "HR"));

        // Valid update
        try {
            service.updateSalary(101, 85000);
        } catch (EmployeeNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Employee not found
        try {
            service.updateSalary(999, 90000);
        } catch (EmployeeNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Invalid salary
        try {
            service.updateSalary(102, -5000);
        } catch (EmployeeNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidSalaryException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Duplicate add
        try {
            service.addEmployee(new Employee(101, "Duplicate", 50000, "HR"));
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

Output:
```
Salary updated: Sonu → 85000.00
Error: Employee not found with ID: 999
Error: Salary -5000.00 is not valid. Must be between 15000 and 10000000.
Error: Employee already exists with ID: 101
```

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Checked exception | Compiler enforces handling — external failures |
| Unchecked exception | Runtime bugs — fix the code |
| `try-catch-finally` | Handle exceptions; `finally` always runs |
| `try-with-resources` | Auto-closes `AutoCloseable` — prefer over `finally` |
| `throw` | Throw an exception from your code |
| `throws` | Declare checked exceptions a method may throw |
| Custom exception | Extend `Exception` (checked) or `RuntimeException` (unchecked) |
| Exception chaining | Pass original as `cause` to preserve diagnostic trail |
| Multi-catch | `catch (A \| B e)` — same handling for multiple types |
| Assertions | Development-only sanity checks — disabled in production |

---

## What's Next

**Module 18** — Annotations. Metadata you attach to code that frameworks, tools, and the compiler can read and act on.
