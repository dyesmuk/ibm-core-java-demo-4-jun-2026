# Module 29 — Java 13 and 14 Features

> **Part I: Modern Java**
> Prerequisites: Module 28 · Time: ~45 minutes
> Java version required: JDK 14+ (text blocks standard in 15, switch expressions standard in 14)

---

## What Changed in Java 13/14

Java 13 (September 2019) and Java 14 (March 2020) were non-LTS releases, but they delivered two features that significantly improved everyday Java code:

| Feature | Introduced | Standard |
|---------|-----------|---------|
| Text Blocks | Java 13 (preview) | Java 15 |
| Switch Expressions | Java 12 (preview) | Java 14 |
| Helpful NullPointerExceptions | Java 14 | Java 14 |
| `instanceof` Pattern Matching | Java 14 (preview) | Java 16 |
| Records | Java 14 (preview) | Java 16 |

This module covers the first three in full. Records and pattern matching for `instanceof` are in Module 30 (Java 17) where they became standard.

---

## Text Blocks

### The Problem

Multi-line strings in Java before text blocks were painful:

```java
// Pre-Java 13 — every line needs explicit \n, quotes need escaping
String json = "{\n" +
              "    \"id\": 101,\n" +
              "    \"name\": \"Sonu\",\n" +
              "    \"department\": \"Engineering\",\n" +
              "    \"salary\": 75000.0\n" +
              "}";

String html = "<html>\n" +
              "    <body>\n" +
              "        <p>Employee: Sonu</p>\n" +
              "    </body>\n" +
              "</html>";
```

Hard to read. Hard to write. Easy to miscount backslashes.

### Text Blocks — The Solution

A text block is a multi-line string literal delimited by `"""`:

```java
String json = """
        {
            "id": 101,
            "name": "Sonu",
            "department": "Engineering",
            "salary": 75000.0
        }
        """;

String html = """
        <html>
            <body>
                <p>Employee: Sonu</p>
            </body>
        </html>
        """;

System.out.println(json);
```

Output:
```
{
    "id": 101,
    "name": "Sonu",
    "department": "Engineering",
    "salary": 75000.0
}
```

No concatenation. No `\n`. No escaped quotes. The string looks exactly like what it produces.

---

## Text Block Rules

### Opening delimiter

The opening `"""` must be followed by a newline — you cannot put content on the same line as `"""`:

```java
String bad  = """hello""";    // compile error — no newline after opening """
String good = """
        hello
        """;
```

### Incidental whitespace removal

The compiler calculates the **common leading whitespace** across all content lines and the closing `"""`, then strips it. This lets you indent the text block to match your code indentation without adding spaces to the string:

```java
public class Example {
    public static void main(String[] args) {
        String report = """
                Name   : Sonu
                Dept   : Engineering
                Salary : 75000
                """;
        System.out.println(report);
    }
}
```

Output — the 16 spaces of indentation are stripped:
```
Name   : Sonu
Dept   : Engineering
Salary : 75000
```

The **position of the closing `"""`** controls how much indentation is stripped:

```java
// Closing """ at column 8 — 8 spaces stripped from each line
String a = """
        Line 1
        Line 2
        """;         // result: "Line 1\nLine 2\n"

// Closing """ at column 0 — nothing stripped
String b = """
        Line 1
        Line 2
""";                  // result: "        Line 1\n        Line 2\n"
```

### Trailing newline

A text block always ends with a newline if the closing `"""` is on its own line. If you don't want the trailing newline, put the closing `"""` on the last content line:

```java
String withNewline    = """
        hello
        """;   // "hello\n"

String withoutNewline = """
        hello""";  // "hello"  — closing on same line as content
```

---

## Text Block Methods

Three new `String` methods work specifically with text blocks:

### `indent(int)`

Adjusts the indentation of each line:

```java
String base = """
        Name   : Sonu
        Salary : 75000
        """;

System.out.println(base.indent(4));   // adds 4 spaces to each line
```

### `stripIndent()`

Removes common leading whitespace — the same algorithm the compiler uses for text blocks, but applied programmatically to a runtime string:

```java
String padded = "        Sonu\n        Monu\n        Tonu\n";
System.out.println(padded.stripIndent());
// Sonu
// Monu
// Tonu
```

### `translateEscapes()`

Interprets escape sequences in a string — useful when a string contains literal `\n`, `\t` etc. that should be treated as actual control characters:

```java
String raw = "Name: Sonu\\nDept: Engineering";  // \\n is a literal backslash-n
System.out.println(raw);                          // Name: Sonu\nDept: Engineering
System.out.println(raw.translateEscapes());       // Name: Sonu
                                                  // Dept: Engineering
```

---

## Text Blocks in Practice

### SQL queries

```java
String query = """
        SELECT e.id, e.name, e.salary, d.name AS department
        FROM employees e
        JOIN departments d ON e.dept_id = d.id
        WHERE e.salary > 70000
          AND d.name = 'Engineering'
        ORDER BY e.salary DESC
        """;
```

### JSON payloads

```java
public String buildEmployeeJson(int id, String name, double salary, String dept) {
    return """
            {
                "id": %d,
                "name": "%s",
                "salary": %.2f,
                "department": "%s"
            }
            """.formatted(id, name, salary, dept);
}
```

```java
String json = buildEmployeeJson(101, "Sonu", 75000.0, "Engineering");
System.out.println(json);
```

Output:
```json
{
    "id": 101,
    "name": "Sonu",
    "salary": 75000.00,
    "department": "Engineering"
}
```

Note: `String.formatted()` is the instance method equivalent of `String.format()` — added in Java 15, works well with text blocks.

### HTML templates

```java
String page = """
        <!DOCTYPE html>
        <html>
          <head><title>Employee Report</title></head>
          <body>
            <h1>%s</h1>
            <p>Department: %s</p>
            <p>Salary: %.2f</p>
          </body>
        </html>
        """.formatted("Sonu", "Engineering", 75000.0);
```

---

## Switch Expressions (Java 14)

Switch was always a statement — it ran code but produced no value. Java 14 made it an **expression** that produces a value.

### The Old `switch` Statement (Pre-Java 14)

```java
String department = "Engineering";
int bonusPercent;

switch (department) {
    case "Engineering":
        bonusPercent = 20;
        break;
    case "Sales":
        bonusPercent = 25;
        break;
    case "HR":
    case "Finance":
        bonusPercent = 10;
        break;
    default:
        bonusPercent = 5;
}
```

Problems: verbose, `break` is easy to forget, fall-through is a bug magnet, and it cannot be used inline in an expression.

### Arrow Switch Expression (Java 14)

```java
String department = "Engineering";

int bonusPercent = switch (department) {
    case "Engineering" -> 20;
    case "Sales"       -> 25;
    case "HR",
         "Finance"     -> 10;    // multiple labels — comma-separated
    default            -> 5;
};

System.out.println("Bonus: " + bonusPercent + "%");   // Bonus: 20%
```

- Arrow `->` replaces `case:` + `break`
- No fall-through — each case is isolated
- Returns a value directly
- Multiple labels on one case — comma-separated
- `default` is required if the switch doesn't cover all possible values

### Multi-Line Case with `yield`

When a case needs more than one statement, use a block and `yield` to return the value:

```java
double salary = 91000;
String band = switch ((int)(salary / 10000)) {
    case 10, 11, 12 -> "Executive";
    case 8, 9       -> "Senior";
    case 6, 7       -> "Mid-level";
    default         -> {
        if (salary < 20000) {
            yield "Trainee";
        } else {
            yield "Junior";
        }
    }
};
System.out.println("Band: " + band);   // Band: Senior
```

`yield` is only used inside switch expression blocks. It is not a general return-from-lambda keyword.

### Traditional Colon Syntax Still Works

The old `case X:` syntax still exists and can also return values using `yield`:

```java
int bonusPercent = switch (department) {
    case "Engineering":
        yield 20;
    case "Sales":
        yield 25;
    default:
        yield 5;
};
```

But the arrow syntax is cleaner and should be preferred for new code.

### Exhaustiveness

A switch expression must be **exhaustive** — it must cover every possible value. For a `String`, `default` is required. For an `enum`, you can cover all constants without `default`:

```java
public enum Department { ENGINEERING, HR, FINANCE, OPERATIONS }

Department dept = Department.ENGINEERING;

int bonus = switch (dept) {
    case ENGINEERING -> 20;
    case HR          -> 10;
    case FINANCE     -> 15;
    case OPERATIONS  -> 8;
    // No default needed — all enum constants covered
};
```

If you add a new constant to the enum later and forget to add it to the switch, you get a compile error — a useful safety net.

---

## Helpful NullPointerExceptions (Java 14)

Before Java 14, a `NullPointerException` told you almost nothing:

```java
// Pre-Java 14 NPE message:
// NullPointerException
// (no detail — where exactly was it null?)
```

Java 14 introduced **detailed NPE messages** that tell you exactly which variable or expression was null:

```java
Employee employee = null;
String dept = employee.getDepartment();
```

Pre-Java 14:
```
Exception in thread "main" java.lang.NullPointerException
    at EmployeeDemo.main(EmployeeDemo.java:8)
```

Java 14+:
```
Exception in thread "main" java.lang.NullPointerException:
Cannot invoke "Employee.getDepartment()" because "employee" is null
    at EmployeeDemo.main(EmployeeDemo.java:8)
```

More complex chain:

```java
String city = company.getHeadOffice().getAddress().getCity();
```

Java 14+ tells you exactly which part of the chain was null:
```
Cannot invoke "Address.getCity()" because the return value of
"Office.getAddress()" is null
```

This requires no code changes — it is automatic in JDK 14+ and is enabled by default from Java 14 onwards (was opt-in via `-XX:+ShowCodeDetailsInExceptionMessages` in Java 14, always-on from Java 15).

---

## Practical Example — Employee Report Generator

```java
import java.util.*;
import java.util.stream.*;

public class EmployeeReportGenerator {

    public static String generateJsonReport(List<Employee> employees) {
        String rows = employees.stream()
            .map(e -> """
                        {
                            "id": %d,
                            "name": "%s",
                            "department": "%s",
                            "salary": %.2f,
                            "band": "%s"
                        }""".formatted(
                        e.getId(), e.getName(),
                        e.getDepartment(), e.getSalary(),
                        getSalaryBand(e.getSalary())))
            .collect(Collectors.joining(",\n"));

        return """
                {
                    "employees": [
                %s
                    ],
                    "total": %d
                }
                """.formatted(rows, employees.size());
    }

    static String getSalaryBand(double salary) {
        return switch ((int)(salary / 10000)) {
            case 9, 10 -> "Senior";
            case 7, 8  -> "Mid-level";
            case 5, 6  -> "Junior";
            default    -> salary > 100000 ? "Executive" : "Trainee";
        };
    }

    public static void main(String[] args) {
        List<Employee> team = List.of(
            new Employee(101, "Sonu",  75000, "Engineering"),
            new Employee(102, "Monu",  82000, "Engineering"),
            new Employee(103, "Tonu",  55000, "HR"),
            new Employee(104, "Ponu",  91000, "Finance"),
            new Employee(105, "Gonu",  68000, "Operations")
        );

        System.out.println(generateJsonReport(team));

        // Bonus calculation using switch expression
        System.out.println("Bonus summary:");
        team.forEach(e -> {
            int bonusPct = switch (e.getDepartment()) {
                case "Engineering" -> 20;
                case "Finance"     -> 15;
                case "Sales"       -> 25;
                default            -> 10;
            };
            System.out.printf("  %-10s %s  bonus: %d%%  amount: %.2f%n",
                    e.getName(), e.getDepartment(),
                    bonusPct, e.getSalary() * bonusPct / 100);
        });
    }
}
```

Output:
```json
{
    "employees": [
        {
            "id": 101,
            "name": "Sonu",
            "department": "Engineering",
            "salary": 75000.00,
            "band": "Mid-level"
        },
        ...
    ],
    "total": 5
}
```

```
Bonus summary:
  Sonu       Engineering  bonus: 20%  amount: 15000.00
  Monu       Engineering  bonus: 20%  amount: 16400.00
  Tonu       HR           bonus: 10%  amount: 5500.00
  Ponu       Finance      bonus: 15%  amount: 13650.00
  Gonu       Operations   bonus: 10%  amount: 6800.00
```

---

## Quick Summary

| Feature | Key Point |
|---------|-----------|
| Text blocks `"""` | Multi-line strings — no concatenation, no escape sequences for quotes |
| Incidental whitespace | Common leading indent stripped — closing `"""` position controls it |
| `String.formatted()` | Instance method equivalent of `String.format()` — pairs well with text blocks |
| Switch expression `->` | Returns a value, no fall-through, multiple labels with comma |
| `yield` | Return a value from a multi-statement switch block |
| Switch exhaustiveness | Must cover all values — `default` for String/int, all constants for enum |
| Helpful NPE | JDK 14+ tells you exactly which variable/expression was null |

---

## What's Next

**Module 30** — Java 17 Features: records, sealed classes, and pattern matching for `instanceof` — the features that changed how Java models data.
