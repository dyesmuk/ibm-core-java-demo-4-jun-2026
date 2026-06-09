# Module 04 — Wrapper Classes

> **Part B: Language Fundamentals**
> Prerequisites: Module 03 · Time: ~45 minutes

---

## The Problem with Primitives

Primitives are fast and memory-efficient. But they have one limitation: **they are not objects**. This matters when you need to:

- Store numbers in a `List` or any collection (collections only hold objects)
- Represent the absence of a value — a primitive `int` cannot be `null`, but sometimes "no salary assigned yet" is a valid state
- Use utility methods like converting a `String` to an `int`, finding min/max, checking if a value is within range

Java's solution: **Wrapper Classes** — one object type for each primitive that wraps the primitive value inside an object.

---

## The 8 Wrapper Classes

| Primitive | Wrapper Class | Package |
|-----------|--------------|---------|
| `byte`    | `Byte`       | `java.lang` |
| `short`   | `Short`      | `java.lang` |
| `int`     | `Integer`    | `java.lang` |
| `long`    | `Long`       | `java.lang` |
| `float`   | `Float`      | `java.lang` |
| `double`  | `Double`     | `java.lang` |
| `char`    | `Character`  | `java.lang` |
| `boolean` | `Boolean`    | `java.lang` |

All wrapper classes are in `java.lang` — automatically available, no import needed.

---

## Creating Wrapper Objects

### Using `valueOf()` — Preferred

```java
Integer empId   = Integer.valueOf(101);
Double  salary  = Double.valueOf(75000.0);
Boolean active  = Boolean.valueOf(true);
Character grade = Character.valueOf('A');
```

### Parsing Strings — Very Common

This is where wrapper classes earn their keep in real code:

```java
// Reading numbers from user input, config files, or APIs
String input = "82000";
int    salary = Integer.parseInt(input);      // String → int

String rate   = "3.14";
double pi     = Double.parseDouble(rate);     // String → double

String flag   = "true";
boolean b     = Boolean.parseBoolean(flag);   // String → boolean (case-insensitive)

String num    = "FF";
int    hex    = Integer.parseInt(num, 16);    // parse hex string → 255
```

`parseInt` and `parseDouble` throw `NumberFormatException` if the string is not a valid number:

```java
int bad = Integer.parseInt("abc");   // throws NumberFormatException at runtime
```

### Converting Wrapper to String

```java
int    salary = 75000;
String s1 = Integer.toString(salary);    // "75000"
String s2 = String.valueOf(salary);      // "75000" — works for any type
String s3 = "" + salary;                 // "75000" — concatenation trick (works but less clean)
```

---

## Autoboxing and Unboxing

Manually creating wrapper objects gets tedious. Java 5 introduced **autoboxing** and **unboxing** to handle this automatically.

### Autoboxing — Primitive to Wrapper (automatic)

```java
int     id      = 101;
Integer wrappedId = id;         // autoboxing — compiler inserts Integer.valueOf(id)

double  salary  = 75000.0;
Double  wrapped = salary;       // autoboxing

List<Integer> ids = new ArrayList<>();
ids.add(102);                   // autoboxing — int 102 → Integer.valueOf(102)
ids.add(103);
```

### Unboxing — Wrapper to Primitive (automatic)

```java
Integer wrappedSalary = Integer.valueOf(82000);
int     salary        = wrappedSalary;    // unboxing — compiler inserts wrappedSalary.intValue()

Integer a = 100;
Integer b = 200;
int     sum = a + b;                     // unboxing happens during arithmetic
```

### Autoboxing in Practice

```java
List<Double> salaries = new ArrayList<>();
salaries.add(75000.0);    // double → Double (autoboxing)
salaries.add(82000.0);
salaries.add(55000.0);

double total = 0;
for (double s : salaries) {    // Double → double (unboxing) in each iteration
    total += s;
}
System.out.println("Total: " + total);   // 212000.0
```

---

## The Null Problem with Unboxing

Wrapper objects can be `null`. Unboxing a `null` wrapper throws a `NullPointerException`:

```java
Integer salary = null;          // valid — wrapper objects can be null
int     s      = salary;        // NullPointerException at runtime — cannot unbox null
```

This is a common bug. Always null-check wrapper types before using them in arithmetic:

```java
Integer bonus = getBonus();   // might return null
if (bonus != null) {
    int b = bonus;            // safe to unbox
}
```

---

## Integer Cache — A Gotcha with `==`

Java caches `Integer` objects for values **-128 to 127**. This means `==` may work for small numbers but fail for larger ones:

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b);    // true — same cached object

Integer x = 200;
Integer y = 200;
System.out.println(x == y);    // false — different objects, cache range exceeded
System.out.println(x.equals(y)); // true — always use equals() for wrapper comparison
```

> **Rule:** Always use `.equals()` to compare wrapper objects, never `==`.

---

## Useful Wrapper Methods

### Integer

```java
Integer.parseInt("42")              // String → int: 42
Integer.valueOf(42)                  // int → Integer
Integer.toString(42)                 // int → String: "42"
Integer.toBinaryString(42)           // "101010"
Integer.toHexString(42)              // "2a"
Integer.toOctalString(42)            // "52"
Integer.MAX_VALUE                    // 2147483647
Integer.MIN_VALUE                    // -2147483648
Integer.compare(a, b)                // compare two ints: negative, 0, or positive
Integer.max(a, b)                    // larger of two ints
Integer.min(a, b)                    // smaller of two ints
Integer.sum(a, b)                    // sum (useful as method reference)
Integer.bitCount(42)                 // number of 1-bits: 3
```

### Double

```java
Double.parseDouble("3.14")           // String → double
Double.isNaN(result)                 // true if result is Not-a-Number
Double.isInfinite(result)            // true if division by zero etc.
Double.MAX_VALUE                     // largest double value
Double.MIN_VALUE                     // smallest positive double value (not most negative)
Double.compare(a, b)                 // compare two doubles
```

### Character

```java
Character.isLetter('A')              // true
Character.isDigit('5')               // true
Character.isWhitespace(' ')          // true
Character.isUpperCase('A')           // true
Character.isLowerCase('a')           // true
Character.toUpperCase('a')           // 'A'
Character.toLowerCase('A')           // 'a'
Character.isAlphabetic('Z')          // true
```

### Boolean

```java
Boolean.parseBoolean("true")         // true
Boolean.parseBoolean("TRUE")         // true (case-insensitive)
Boolean.parseBoolean("yes")          // false — only "true" (any case) returns true
Boolean.toString(true)               // "true"
```

---

## Practical Example — Processing Employee Data from Strings

In real applications, data often comes in as strings (from files, APIs, user input). Wrapper classes bridge the gap:

```java
public class EmployeeParser {
    public static void main(String[] args) {

        // Simulating data arriving as strings (e.g., from a CSV row)
        String rawId         = "1001";
        String rawName       = "Sonu";
        String rawSalary     = "75000.50";
        String rawDepartment = "Engineering";
        String rawActive     = "true";

        // Parse into correct types
        int     id         = Integer.parseInt(rawId);
        String  name       = rawName;
        double  salary     = Double.parseDouble(rawSalary);
        String  department = rawDepartment;
        boolean isActive   = Boolean.parseBoolean(rawActive);

        System.out.println("ID        : " + id);
        System.out.println("Name      : " + name);
        System.out.println("Salary    : " + salary);
        System.out.println("Department: " + department);
        System.out.println("Active    : " + isActive);

        // Bonus calculation using Integer methods
        int base = (int) salary;
        System.out.println("Salary in binary : " + Integer.toBinaryString(base));
        System.out.println("Max possible int : " + Integer.MAX_VALUE);
    }
}
```

Output:
```
ID        : 1001
Name      : Sonu
Salary    : 75000.5
Department: Engineering
Active    : true
Salary in binary : 10010010011101000111
Max possible int : 2147483647
```

---

## Number Formatting — `printf` and `String.format`

While we are here — when printing numbers, use formatting for clean output:

```java
double salary = 75000.5;

// printf — print with format, no return value
System.out.printf("Salary: %.2f%n", salary);          // Salary: 75000.50
System.out.printf("%-15s %8.2f%n", "Sonu", salary);  // Sonu            75000.50

// String.format — returns a formatted String
String line = String.format("%-15s %8.2f", "Monu", 82000.0);
System.out.println(line);   // Monu             82000.00
```

**Common format specifiers:**

| Specifier | Meaning |
|-----------|---------|
| `%d` | Integer |
| `%f` | Floating point |
| `%.2f` | Float with 2 decimal places |
| `%s` | String |
| `%n` | Newline (platform-independent, prefer over `\n` in printf) |
| `%10s` | Right-aligned in field of width 10 |
| `%-10s` | Left-aligned in field of width 10 |

```java
// Formatted salary slip
System.out.printf("%-12s %-15s %10s%n", "ID", "Name", "Salary");
System.out.printf("%-12d %-15s %10.2f%n", 101, "Sonu",  75000.0);
System.out.printf("%-12d %-15s %10.2f%n", 102, "Monu",  82000.0);
System.out.printf("%-12d %-15s %10.2f%n", 103, "Tonu",  55000.0);
```

Output:
```
ID           Name               Salary
101          Sonu             75000.00
102          Monu             82000.00
103          Tonu             55000.00
```

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Wrapper classes | Object version of each primitive — `int` → `Integer`, `double` → `Double`, etc. |
| `parseInt` / `parseDouble` | Convert `String` to primitive — throws `NumberFormatException` on bad input |
| Autoboxing | Primitive → Wrapper, automatic |
| Unboxing | Wrapper → Primitive, automatic |
| Null danger | Unboxing a `null` wrapper → `NullPointerException` |
| `==` on wrappers | Never — use `.equals()` |
| Integer cache | `-128` to `127` are cached — `==` works by accident for small values only |

---

## What's Next

**Module 05** — Flow Control. if/else, switch, loops — controlling what your program does and when.
