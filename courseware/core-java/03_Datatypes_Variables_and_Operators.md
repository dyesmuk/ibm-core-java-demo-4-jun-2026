# Module 03 — Datatypes, Variables and Operators

> **Part B: Language Fundamentals**
> Prerequisites: Module 01, 02 · Time: ~2 hours

---

## Variables

A variable is a named container that holds a value in memory. Every variable in Java has three things:

- A **type** — what kind of data it holds
- A **name** — how you refer to it in code
- A **value** — the actual data

```java
double salary = 75000.0;
//  ↑type  ↑name  ↑value
```

### Declaring and Initializing

```java
int age;                 // declaration only — value undefined, do not use yet
int age = 28;            // declaration + initialization
age = 30;                // assignment — change the value later
```

Local variables (inside methods) **must** be initialized before use. The compiler will refuse to compile code that reads an uninitialized local variable:

```java
int bonus;
System.out.println(bonus);   // compile error: variable bonus might not have been initialized
```

---

## Data Types

Java has two categories of types:

```
Java Types
├── Primitive   — 8 built-in types, hold actual values
└── Reference   — point to objects on the heap (covered in OOP modules)
```

### The 8 Primitive Types

| Type | Size | Range | Default | Typical Use |
|------|------|-------|---------|-------------|
| `byte` | 1 byte | -128 to 127 | 0 | Raw binary data, file I/O |
| `short` | 2 bytes | -32,768 to 32,767 | 0 | Rarely used directly |
| `int` | 4 bytes | -2,147,483,648 to 2,147,483,647 | 0 | **Standard integer type** |
| `long` | 8 bytes | -9.2 × 10¹⁸ to 9.2 × 10¹⁸ | 0L | Large numbers, timestamps |
| `float` | 4 bytes | ~7 decimal digits precision | 0.0f | Rarely used (use double) |
| `double` | 8 bytes | ~15 decimal digits precision | 0.0d | **Standard decimal type** |
| `char` | 2 bytes | 0 to 65,535 (Unicode) | '\u0000' | Single character |
| `boolean` | JVM-dependent | `true` or `false` | `false` | Flags, conditions |

> **Memory defaults** shown above apply only to **class-level fields** (instance and static variables). Local variables inside methods have no default — they must be explicitly initialized.

### Choosing the Right Type

```java
int     employeeId    = 1001;          // whole number → int
long    aadhaarNumber = 987654321012L; // exceeds int range → long (note the L suffix)
double  salary        = 75000.50;      // decimal number → double
boolean isActive      = true;          // yes/no flag → boolean
char    grade         = 'A';           // single character → char (single quotes)
String  name          = "Sonu";        // text → String (reference type, not primitive)
```

---

## Literals

A literal is a fixed value written directly in code.

### Integer Literals

```java
int a = 42;           // decimal (base 10) — everyday use
int b = 0b101010;     // binary (base 2)  — prefix 0b (Java 7+)
int c = 052;          // octal (base 8)   — prefix 0
int d = 0x2A;         // hexadecimal (base 16) — prefix 0x

// All four hold the same value: 42

long bigNum = 9_000_000_000L;   // underscore as visual separator (Java 7+)
//                          ↑ L suffix required for long literals that exceed int range
```

### Floating-Point Literals

```java
double d1 = 3.14;         // default is double
double d2 = 3.14d;        // explicit double suffix (optional)
float  f1 = 3.14f;        // MUST have f suffix — without it, 3.14 is a double and won't fit float
double sci = 1.5e10;      // scientific notation: 1.5 × 10^10
```

### Character Literals

```java
char c1 = 'A';           // character literal — single quotes
char c2 = 65;            // same as 'A' — char stores Unicode code point
char c3 = '\n';          // escape sequence — newline
char c4 = '\t';          // escape sequence — tab
char c5 = '\'';          // escape sequence — single quote
char c6 = '\\';          // escape sequence — backslash
```

### String Literals

```java
String s = "Sonu";                    // double quotes
String empty = "";                    // empty string — not null
String multiword = "Senior Engineer"; // spaces fine inside quotes
```

String is not a primitive — it is a class. But it gets special treatment (literal syntax, string pool) covered fully in Module 07.

### Boolean Literals

```java
boolean isActive  = true;
boolean isDeleted = false;
// only two possible values — true and false (lowercase, always)
```

---

## Type Conversion

Java allows values of one type to be treated as another type in certain situations.

### Widening (Implicit / Automatic)

Moving to a **larger** type — safe, no data loss, happens automatically:

```
byte → short → int → long → float → double
                          char ↗
```

```java
int    employeeId = 1001;
long   id         = employeeId;    // int → long, automatic, no cast needed
double salary     = employeeId;    // int → double, automatic

System.out.println(id);       // 1001
System.out.println(salary);   // 1001.0
```

### Narrowing (Explicit / Manual Cast)

Moving to a **smaller** type — risky, possible data loss, must be explicit:

```java
double salary  = 75999.99;
int    rounded = (int) salary;     // explicit cast required
System.out.println(rounded);       // 75999 — fractional part truncated (not rounded)

long   bigId  = 99999999999L;
int    smallId = (int) bigId;      // may lose data if value exceeds int range
```

The `(int)` is the **cast operator** — you are telling the compiler "I know this might lose data, do it anyway."

### Important: Truncation, Not Rounding

```java
double d = 9.99;
int i = (int) d;
System.out.println(i);   // 9, not 10 — the decimal part is simply dropped
```

### Type Promotion in Expressions

Java automatically promotes operands in arithmetic expressions:

```java
byte b1 = 10;
byte b2 = 20;
byte b3 = b1 + b2;     // compile error! b1 + b2 produces an int, not a byte
int  b3 = b1 + b2;     // correct — the result is an int
```

**Rules:**
- Any arithmetic involving `byte` or `short` → result is `int`
- Any arithmetic involving `long` → result is `long`
- Any arithmetic involving `float` → result is `float`
- Any arithmetic involving `double` → result is `double`

```java
int    base   = 50000;
double factor = 1.15;
double result = base * factor;   // int × double → double, automatic
```

---

## Operators

### Arithmetic Operators

```java
int a = 17, b = 5;

System.out.println(a + b);   // 22  addition
System.out.println(a - b);   // 12  subtraction
System.out.println(a * b);   // 85  multiplication
System.out.println(a / b);   // 3   integer division — fractional part dropped
System.out.println(a % b);   // 2   modulus (remainder)
```

**Integer division gotcha:**
```java
int hours = 7;
int days  = hours / 8;       // 0, not 0.875 — both operands are int → int result
double d  = hours / 8;       // still 0.0 — division happens first, then widening

// Correct way to get decimal result:
double correct = (double) hours / 8;    // 0.875 — cast before dividing
double also    = hours / 8.0;           // 0.875 — 8.0 is double, promotes hours
```

### Assignment Operators

```java
int salary = 50000;

salary += 5000;    // salary = salary + 5000  → 55000
salary -= 2000;    // salary = salary - 2000  → 53000
salary *= 2;       // salary = salary * 2     → 106000
salary /= 4;       // salary = salary / 4     → 26500
salary %= 3;       // salary = salary % 3     → 1
```

### Increment and Decrement

```java
int count = 0;

count++;           // post-increment: use value first, then increment
++count;           // pre-increment:  increment first, then use value
count--;           // post-decrement
--count;           // pre-decrement
```

The difference matters when used inside an expression:

```java
int a = 5;
int b = a++;       // b = 5, a = 6  (b gets the old value)
int c = ++a;       // c = 7, a = 7  (c gets the new value)
```

### Relational (Comparison) Operators

Always produce a `boolean` result:

```java
int salary1 = 75000;
int salary2 = 82000;

System.out.println(salary1 == salary2);   // false  equal to
System.out.println(salary1 != salary2);   // true   not equal to
System.out.println(salary1 >  salary2);   // false  greater than
System.out.println(salary1 <  salary2);   // true   less than
System.out.println(salary1 >= salary2);   // false  greater than or equal
System.out.println(salary1 <= salary2);   // true   less than or equal
```

> `==` compares **values** for primitives. For objects, `==` compares **references** (addresses), not content. Use `.equals()` for object content comparison — covered in Module 14.

### Logical Operators

Combine boolean expressions:

```java
boolean isActive   = true;
boolean isSenior   = false;
double  salary     = 90000;

// AND — both must be true
System.out.println(isActive && isSenior);           // false

// OR — at least one must be true
System.out.println(isActive || isSenior);           // true

// NOT — inverts the boolean
System.out.println(!isActive);                      // false

// Practical use
boolean getsBonus = isActive && salary > 80000;
System.out.println(getsBonus);                      // true
```

**Short-circuit evaluation:**

`&&` stops evaluating as soon as it finds `false`. `||` stops as soon as it finds `true`.

```java
// if isActive is false, the salary check is never evaluated
if (isActive && salary > 80000) { ... }

// if isActive is true, the salary check is never evaluated
if (isActive || salary > 80000) { ... }
```

This is not just an optimization — it prevents errors:

```java
Employee e = null;
if (e != null && e.getSalary() > 50000) {    // safe: if e is null, second check is skipped
    // ...
}
```

### Bitwise Operators

Operate on individual bits. Rarely needed in day-to-day business code, but show up in flags, permissions, and low-level work:

```java
int a = 5;   // binary: 0101
int b = 3;   // binary: 0011

System.out.println(a & b);    // 1   AND:  0101 & 0011 = 0001
System.out.println(a | b);    // 7   OR:   0101 | 0011 = 0111
System.out.println(a ^ b);    // 6   XOR:  0101 ^ 0011 = 0110
System.out.println(~a);       // -6  NOT:  bitwise complement
System.out.println(a << 1);   // 10  left shift:  0101 → 1010 (multiply by 2)
System.out.println(a >> 1);   // 2   right shift: 0101 → 0010 (divide by 2)
System.out.println(a >>> 1);  // 2   unsigned right shift (fills with 0, not sign bit)
```

### Ternary Operator

A compact if-else for simple value selection:

```java
// syntax: condition ? valueIfTrue : valueIfFalse
double salary = 95000;
String level  = salary > 80000 ? "Senior" : "Junior";
System.out.println(level);   // Senior

// equivalent if-else:
String level;
if (salary > 80000) {
    level = "Senior";
} else {
    level = "Junior";
}
```

Use the ternary for simple one-liners. For anything complex, use a proper if-else — readability matters.

### instanceof Operator

Checks if an object is of a particular type (covered in depth in the OOP modules):

```java
Object obj = "Sonu";
System.out.println(obj instanceof String);    // true
System.out.println(obj instanceof Integer);   // false
```

---

## Operator Precedence

When multiple operators appear in one expression, precedence determines evaluation order. Higher precedence evaluates first.

| Precedence | Operators |
|-----------|-----------|
| Highest | `++`, `--` (postfix), `()`, `[]` |
| | `++`, `--` (prefix), `!`, `~`, `+`, `-` (unary) |
| | `*`, `/`, `%` |
| | `+`, `-` |
| | `<<`, `>>`, `>>>` |
| | `<`, `>`, `<=`, `>=`, `instanceof` |
| | `==`, `!=` |
| | `&` |
| | `^` |
| | `\|` |
| | `&&` |
| | `\|\|` |
| | `? :` (ternary) |
| Lowest | `=`, `+=`, `-=`, etc. |

You do not need to memorize this. Use **parentheses** to make your intent explicit:

```java
// Unclear — what evaluates first?
int result = 2 + 3 * 4 - 1;        // 13 (multiplication first)

// Clear — parentheses communicate intent
int result = 2 + (3 * 4) - 1;      // still 13, but now obvious
int other  = (2 + 3) * (4 - 1);    // 15 — clearly different intent
```

---

## Practical Example — Salary Calculator

```java
public class SalaryCalculator {
    public static void main(String[] args) {

        String name         = "Sonu";
        double basicSalary  = 75000.0;
        double hraPercent   = 0.40;   // 40% of basic
        double daPercent    = 0.20;   // 20% of basic
        double taxPercent   = 0.10;   // 10% of gross

        double hra       = basicSalary * hraPercent;
        double da        = basicSalary * daPercent;
        double gross     = basicSalary + hra + da;
        double tax       = gross * taxPercent;
        double netSalary = gross - tax;

        System.out.println("=== Salary Slip: " + name + " ===");
        System.out.println("Basic Salary : " + basicSalary);
        System.out.println("HRA (40%)    : " + hra);
        System.out.println("DA  (20%)    : " + da);
        System.out.println("Gross Salary : " + gross);
        System.out.println("Tax (10%)    : " + tax);
        System.out.println("Net Salary   : " + netSalary);

        // Determine pay grade using ternary
        String grade = netSalary > 90000 ? "A" : netSalary > 70000 ? "B" : "C";
        System.out.println("Pay Grade    : " + grade);
    }
}
```

Output:
```
=== Salary Slip: Sonu ===
Basic Salary : 75000.0
HRA (40%)    : 30000.0
DA  (20%)    : 15000.0
Gross Salary : 120000.0
Tax (10%)    : 12000.0
Net Salary   : 108000.0
Pay Grade    : A
```

---

## Variable Scope

A variable only exists within the **block** (pair of `{}`) where it is declared:

```java
public static void main(String[] args) {

    int employeeId = 101;        // exists for the rest of main

    if (employeeId > 100) {
        String status = "Active"; // exists only inside this if block
        System.out.println(status);
    }

    System.out.println(status);   // compile error — status is out of scope here
    System.out.println(employeeId); // fine — employeeId is still in scope
}
```

---

## `var` — Local Type Inference (Java 10+)

Java 10 introduced `var` for local variables. The compiler infers the type from the initializer:

```java
var name       = "Sonu";        // inferred as String
var salary     = 75000.0;       // inferred as double
var employeeId = 1001;          // inferred as int
```

`var` is just syntactic sugar — the type is still fixed at compile time. You cannot reassign a different type later. `var` can only be used for **local variables** — not fields, parameters, or return types.

> This courseware uses explicit types throughout for clarity. `var` is fully covered in the Java 10 section of Module 28.

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| 8 primitive types | `byte short int long float double char boolean` |
| `int` and `double` | The go-to types for whole and decimal numbers |
| Widening | Automatic, safe, no cast needed |
| Narrowing | Manual, risky, requires cast operator `(type)` |
| Integer division | Both operands `int` → result is `int`, fraction dropped |
| `&&` and `\|\|` | Short-circuit — second operand may not evaluate |
| Precedence | Use parentheses instead of memorizing the table |

---

## What's Next

**Module 04** — Wrapper Classes. Every primitive has an object equivalent. You need them for collections, nullability, and utility methods like `Integer.parseInt()`.
