# Core Java Courseware — Module 1: Java Foundations

> **Author:** Vaman Deshmukh | **Updated & Modernized:** 2024
> **Covers:** Core Java 01, 02, 03 — Building Blocks, Control Structures, Arrays

---

## Table of Contents

- [1. Introduction to Java](#1-introduction-to-java)
- [2. Building Blocks of Java](#2-building-blocks-of-java)
- [3. Data Types in Java](#3-data-types-in-java)
- [4. Control Structures](#4-control-structures)
- [5. Arrays in Java](#5-arrays-in-java)
- [Interview Questions](#interview-questions)
- [Assignments](#assignments)

---

## 1. Introduction to Java

### Why Learn Java?

- Most widely used programming language in enterprise software
- Best gateway to IT careers (backend, Android, cloud, big data)
- Write Once, Run Anywhere (WORA) — platform independent
- Rich ecosystem: Spring, Hibernate, Kafka, Android SDK, etc.

### What Is Java?

Java is a **general-purpose, object-oriented programming language** designed around the concept of classes and objects. Programs compile to **bytecode** that runs on any platform with a JVM (Java Virtual Machine) — no recompilation needed.

> Java inherited its syntax largely from C and C++, but removed unsafe features like pointers and manual memory management.

### History of Java

| Release | Year | Notes |
|---------|------|-------|
| Project Java started | 1991 | James Gosling & team at Sun Microsystems |
| JDK Beta | 1995 | First public release |
| JDK 1.0 | 1996 | Official v1.0 |
| JDK 1.1 | 1997 | Inner classes, JDBC |
| J2SE 1.2 | 1998 | Swing, Collections |
| J2SE 5.0 | 2004 | Generics, Enums, Autoboxing |
| **Java SE 8** | **2014** | **Lambda, Stream API, Optional** |
| Java SE 11 | 2018 | LTS release |
| Java SE 17 | 2021 | LTS release, Records, Sealed Classes |
| Java SE 21 | 2023 | LTS release, Virtual Threads |

> **Modern Note:** Java 8 remains extremely common in production. Java 17 and 21 are the current LTS versions and preferred for new projects.

### Key Features of Java

| Feature | Description |
|---------|-------------|
| **Simple** | C++-like syntax, no pointers, automatic memory management |
| **Object-Oriented** | Everything is organized around classes and objects |
| **Robust** | Strong type checking, exception handling, garbage collection |
| **Platform Independent** | Bytecode runs on any JVM — WORA |
| **Secure** | No external pointers; programs run inside JVM sandbox |
| **Multi-threaded** | Built-in support for concurrent execution |
| **Distributed** | RMI, EJB support for networked apps |
| **High Performance** | JIT (Just-In-Time) compiler for near-native speed |

### Your First Java Program

```java
// HelloWorld.java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

**Compile and run:**
```bash
javac HelloWorld.java   # produces HelloWorld.class (bytecode)
java HelloWorld         # JVM executes the bytecode
```

---

## 2. Building Blocks of Java

### Tokens in Java

A **token** is the smallest meaningful element that the Java compiler recognizes. Java has seven types of tokens:

1. **Keywords** — reserved words with special meaning
2. **Identifiers** — names for variables, methods, classes
3. **Literals** — values assigned directly (e.g., `10`, `"hello"`, `true`)
4. **Operators** — symbols that perform operations
5. **Separators** — punctuation that structures code (`;`, `,`, `{}`, `()`, `[]`, `.`)
6. **Whitespace** — spaces, tabs, newlines (separates tokens)
7. **Comments** — developer notes, ignored by compiler

---

### Keywords

Java has **50+ reserved words** that cannot be used as identifiers.

**By category:**

| Category | Keywords |
|----------|----------|
| Data types | `byte` `short` `int` `long` `float` `double` `char` `boolean` |
| Flow control | `if` `else` `for` `while` `do` `switch` `case` `default` `break` `continue` `return` |
| Exception handling | `try` `catch` `finally` `throw` `throws` `assert` |
| Access modifiers | `public` `private` `protected` |
| Other modifiers | `static` `final` `abstract` `synchronized` `volatile` `transient` `native` `strictfp` |
| Class/Object | `class` `interface` `extends` `implements` `new` `this` `super` `instanceof` |
| Package | `package` `import` |
| Reserved (unused) | `goto` `const` |
| Literals (special) | `true` `false` `null` |

> **Note:** All Java keywords are lowercase. `True` is not a keyword — `true` is.

---

### Identifiers

Identifiers are **programmer-defined names** for variables, methods, classes, packages, and interfaces.

**Rules:**
- May contain letters (A–Z, a–z), digits (0–9), `_`, `$`
- Must NOT start with a digit
- Cannot be a keyword
- Are **case-sensitive** (`count` ≠ `Count`)
- Cannot contain whitespace

**Naming Conventions (standard practice):**

| Element | Convention | Example |
|---------|-----------|---------|
| Class | `PascalCase` | `BankAccount` |
| Interface | `PascalCase` (often adjective) | `Serializable` |
| Method | `camelCase` (verb-noun) | `getBalance()` |
| Variable | `camelCase` | `accountBalance` |
| Constant | `UPPER_SNAKE_CASE` | `MAX_SIZE` |
| Package | `all.lowercase` | `com.company.project` |

---

### Literals

A literal is a **fixed value** written directly in code.

| Type | Examples |
|------|----------|
| Integer | `10`, `0`, `-5`, `0xFF` (hex), `0b1010` (binary, Java 7+) |
| Long | `100L`, `9999999999L` |
| Float | `3.14f`, `2.5F` |
| Double | `3.14`, `2.5d` |
| Character | `'A'`, `'\n'`, `'\t'`, `'\\'` |
| Boolean | `true`, `false` |
| String | `"Hello"`, `"Java 8"` |
| Null | `null` |

---

### Operators

#### Arithmetic Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `+` | Addition / String concatenation | `5 + 3 = 8` |
| `-` | Subtraction | `5 - 3 = 2` |
| `*` | Multiplication | `5 * 3 = 15` |
| `/` | Division | `10 / 3 = 3` (integer) |
| `%` | Remainder / Modulo | `10 % 3 = 1` |

#### Relational (Comparison) Operators

| Operator | Description |
|----------|-------------|
| `==` | Equal to |
| `!=` | Not equal to |
| `>` | Greater than |
| `>=` | Greater than or equal |
| `<` | Less than |
| `<=` | Less than or equal |

> **Modern Note:** For object comparison, always use `.equals()` instead of `==`. The `==` operator on objects checks *reference equality*, not *value equality*.

#### Logical Operators

| Operator | Description | Short-circuits? |
|----------|-------------|----------------|
| `&&` | Logical AND | Yes — stops at first `false` |
| `\|\|` | Logical OR | Yes — stops at first `true` |
| `!` | Logical NOT | — |

#### Unary Operators

| Operator | Description |
|----------|-------------|
| `++x` | Pre-increment: increment then use |
| `x++` | Post-increment: use then increment |
| `--x` | Pre-decrement |
| `x--` | Post-decrement |
| `-x` | Negation |
| `!x` | Boolean NOT |

#### Shortcut (Compound Assignment) Operators

| Operator | Equivalent |
|----------|-----------|
| `a += 3` | `a = a + 3` |
| `a -= 10` | `a = a - 10` |
| `a *= 4` | `a = a * 4` |
| `a /= 2` | `a = a / 2` |
| `a %= 5` | `a = a % 5` |

#### Conditional (Ternary) Operator

```java
result = (condition) ? valueIfTrue : valueIfFalse;

// Example
int max = (a > b) ? a : b;
```

#### Operator Precedence (high to low, simplified)

`()` → `++/--` (postfix) → `*/% ` → `+-` → `< > <= >=` → `== !=` → `&&` → `||` → `?:` → `=`

---

### Comments

```java
// Single-line comment

/* Multi-line
   comment */

/**
 * Javadoc comment — used for API documentation generation
 * @param args command-line arguments
 */
```

---

## 3. Data Types in Java

### Overview

Java is **strongly typed** — every variable must have a declared type.

```
Data Types
├── Primitive (8 types) — store raw values directly in memory
│   ├── Integral: byte, short, int, long
│   ├── Floating-point: float, double
│   ├── Character: char
│   └── Boolean: boolean
└── Reference — store a memory address pointing to an object
    ├── class (String, Scanner, ArrayList, ...)
    ├── array
    ├── interface
    └── enum
```

### Primitive Data Types

| Type | Size | Range | Default |
|------|------|-------|---------|
| `byte` | 8 bits | -128 to 127 | `0` |
| `short` | 16 bits | -32,768 to 32,767 | `0` |
| `int` | 32 bits | -2,147,483,648 to 2,147,483,647 | `0` |
| `long` | 64 bits | -9.2×10¹⁸ to 9.2×10¹⁸ | `0L` |
| `float` | 32 bits | ~7 significant digits | `0.0f` |
| `double` | 64 bits | ~15 significant digits | `0.0d` |
| `char` | 16 bits | `\u0000` to `\uffff` (0–65,535) | `'\u0000'` |
| `boolean` | N/A | `true` or `false` | `false` |

> **Modern Note:** `int` is the default for integer literals. Use `long` suffix (`L`) for large numbers. `double` is the default for floating-point literals. Floating-point arithmetic is **approximate** — never use `==` to compare floats/doubles. Use `BigDecimal` for exact financial calculations.

### Special Characters (Escape Sequences)

| Escape | Meaning |
|--------|---------|
| `\n` | Newline |
| `\t` | Tab |
| `\r` | Carriage return |
| `\\` | Backslash |
| `\'` | Single quote |
| `\"` | Double quote |

### Variable Declaration

```java
// Syntax
dataType variableName;
dataType variableName = value;

// Examples
int score;
int score = 100;
double price = 19.99;
String name = "Java";
boolean isActive = true;

// Multiple declarations
int x, y, z;
int a = 1, b = 2, c = 3;
```

### Type Casting

**Implicit (widening) — automatic, no data loss:**
```java
int i = 100;
long l = i;       // int → long: safe
double d = i;     // int → double: safe
```

**Explicit (narrowing) — manual, possible data loss:**
```java
double pi = 3.14159;
int approx = (int) pi;   // approx = 3 (decimal truncated)

long bigNum = 1234567890123L;
int smallNum = (int) bigNum;  // potential overflow
```

### Promotion in Mixed-Type Arithmetic

When operands have different types, the lower-precision type is automatically promoted:

```
byte/short/char → int → long → float → double
```

```java
byte b = 10;
short s = 20;
int result = b + s;   // both promoted to int before adding
```

---

## 4. Control Structures

Control structures determine the **order of execution** in a program. Java has two main categories: branching and looping.

### Branching Statements

#### if Statement

```java
if (condition) {
    // executes only when condition is true
}
```

#### if-else Statement

```java
if (condition) {
    // executes when true
} else {
    // executes when false
}

// Example: even or odd
int number = 7;
if (number % 2 == 0) {
    System.out.println("Even");
} else {
    System.out.println("Odd");
}
```

#### if-else-if (Multi-branch)

```java
if (score >= 90) {
    System.out.println("Grade: A");
} else if (score >= 80) {
    System.out.println("Grade: B");
} else if (score >= 70) {
    System.out.println("Grade: C");
} else {
    System.out.println("Grade: F");
}
```

#### Nested if

```java
if (mood == 1) {
    if (friends == 1) {
        System.out.println("Go to movie.");
    } else {
        System.out.println("Go shopping.");
    }
} else {
    if (friends == 1) {
        System.out.println("Watch TV.");
    } else {
        System.out.println("Go to sleep.");
    }
}
```

#### switch-case Statement

Use `switch` when choosing between many **discrete, integral** values.

```java
switch (controllingExpression) {
    case value1:
        // statements
        break;
    case value2:
        // statements
        break;
    default:
        // fallback
}
```

**Example:**
```java
char grade = 'B';
switch (grade) {
    case 'A':
    case 'B':
    case 'C':
    case 'D':
        System.out.println("Pass");
        break;
    case 'M':
        System.out.println("Malpractice");
        break;
    default:
        System.out.println("Fail");
}
```

> **Modern Note (Java 14+):** Arrow syntax in switch expressions is much cleaner:
> ```java
> String result = switch (grade) {
>     case 'A', 'B', 'C', 'D' -> "Pass";
>     case 'M' -> "Malpractice";
>     default -> "Fail";
> };
> ```
> `switch` can also be used on `String` since Java 7.

---

### Looping Statements

All loops follow the **ICU** structure: **I**nitialization, **C**ondition, **U**pdate.

#### while Loop

```java
// Syntax
initialization;
while (condition) {
    // body
    update;
}

// Example: print 1 to 5
int i = 1;
while (i <= 5) {
    System.out.println(i);
    i++;
}
```

- Checks condition **before** executing body
- Body may execute **zero** times if condition is initially false

#### do-while Loop

```java
do {
    // body (executes at least once)
} while (condition);

// Example: menu-driven program
int choice;
do {
    System.out.println("1. View  2. Add  3. Exit");
    choice = scanner.nextInt();
} while (choice != 3);
```

- Checks condition **after** executing body
- Body executes **at least once**

#### for Loop

```java
for (initialization; condition; update) {
    // body
}

// Example: sum of first 10 natural numbers
int sum = 0;
for (int i = 1; i <= 10; i++) {
    sum += i;
}
System.out.println("Sum = " + sum); // Sum = 55
```

#### Enhanced for Loop (for-each)

Introduced in Java 5. Best for iterating over arrays and collections.

```java
int[] numbers = {10, 20, 30, 40, 50};
for (int num : numbers) {
    System.out.println(num);
}
```

- No index required
- Read-only access to elements (cannot modify the array through `num`)
- More readable and less error-prone

---

### Loop Control: break and continue

```java
// break — exits the innermost loop immediately
for (int i = 1; i <= 10; i++) {
    if (i == 5) break;
    System.out.println(i);  // prints 1, 2, 3, 4
}

// continue — skips current iteration, moves to next
for (int i = 1; i <= 10; i++) {
    if (i == 5) continue;
    System.out.println(i);  // prints 1, 2, 3, 4, 6, 7, 8, 9, 10
}
```

---

## 5. Arrays in Java

### What Is an Array?

An array is a **fixed-size, indexed container** that holds multiple values of the **same type**.

- Index always starts at **0**
- Last element is at index `array.length - 1`
- Size is fixed at creation time (use `ArrayList` for dynamic sizing)
- Stored in **heap memory**

### Declaring and Creating Arrays

```java
// Declaration only — no memory allocated yet
int[] scores;

// Declaration + creation (elements initialized to default values)
int[] scores = new int[5];        // [0, 0, 0, 0, 0]
boolean[] flags = new boolean[3]; // [false, false, false]
String[] names = new String[4];   // [null, null, null, null]

// Declaration + initialization with values
int[] grades = {95, 87, 72, 91, 88};
String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
```

### Accessing and Modifying Array Elements

```java
int[] a = {10, 20, 30, 40, 50};

// Access
System.out.println(a[0]);  // 10
System.out.println(a[4]);  // 50

// Modify
a[2] = 99;

// Length
System.out.println(a.length);  // 5
```

### Default Values

| Type | Default |
|------|---------|
| `byte`, `short`, `int`, `long` | `0` |
| `float`, `double` | `0.0` |
| `char` | `'\u0000'` |
| `boolean` | `false` |
| Object reference | `null` |

### Iterating Over Arrays

```java
int[] data = {10, 20, 30, 40, 50};

// Traditional for loop (when index is needed)
for (int i = 0; i < data.length; i++) {
    System.out.println("data[" + i + "] = " + data[i]);
}

// Enhanced for loop (cleaner)
for (int value : data) {
    System.out.println(value);
}
```

### ArrayIndexOutOfBoundsException

Accessing an invalid index throws `ArrayIndexOutOfBoundsException` at runtime:

```java
int[] arr = new int[5];
arr[5] = 10;   // Error! Valid indices: 0–4
arr[-1] = 5;   // Error! Negative index
```

### Multi-Dimensional Arrays

In Java, multi-dimensional arrays are **arrays of arrays**.

```java
// 2D array — 3 rows, 4 columns
int[][] matrix = new int[3][4];

// Inline initialization
int[][] grid = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

// Access element at row 1, column 2
System.out.println(grid[1][2]);  // 6

// Iterate with nested loops
for (int i = 0; i < grid.length; i++) {
    for (int j = 0; j < grid[i].length; j++) {
        System.out.print(grid[i][j] + "\t");
    }
    System.out.println();
}
```

### Useful Array Utilities (java.util.Arrays)

```java
import java.util.Arrays;

int[] arr = {5, 2, 8, 1, 9, 3};

Arrays.sort(arr);                          // sort in-place
System.out.println(Arrays.toString(arr));  // [1, 2, 3, 5, 8, 9]

int idx = Arrays.binarySearch(arr, 8);     // find element (array must be sorted)
int[] copy = Arrays.copyOf(arr, 4);        // copy first 4 elements
Arrays.fill(arr, 0);                       // set all elements to 0
```

> **Modern Note:** For dynamic collections, prefer `ArrayList<Integer>` over arrays. Arrays are best when size is known and fixed, and performance is critical.

### Advantages and Disadvantages

**Advantages:**
- Fast random access by index — O(1)
- Memory-efficient (no overhead per element)
- Ideal for fixed-size data

**Disadvantages:**
- Fixed size — cannot grow or shrink after creation
- Can only hold one data type
- Insertion/deletion is expensive (requires shifting)

**Solution:** Java's **Collection Framework** (covered in Module 4) — `ArrayList`, `LinkedList`, etc.

---

## Interview Questions

1. What does WORA mean in Java, and how is it achieved?
2. What are the 8 primitive data types in Java?
3. What is the difference between `==` and `.equals()` for String comparison?
4. What is the difference between `while` and `do-while` loops?
5. What is the difference between `break` and `continue`?
6. Can you change the size of an array once created?
7. What is `ArrayIndexOutOfBoundsException`?
8. What is the difference between `float` and `double`?
9. What is the default value of an `int` array element?
10. What is a two-dimensional array in Java?
11. What is the difference between multi-branch `if` and nested `if`?
12. What is the use of `break` in a `switch` statement?
13. Explain the use of comments in Java.

---

## Assignments

1. Write a program to print the sum of cubes of digits of an n-digit number.
2. Write a program to check if a number is prime.
3. Write a Fibonacci sequence program using both recursive and non-recursive approaches.
4. Write a traffic light simulator using `switch`.
5. Write a program to find the largest and smallest number in an array.
6. Write a program to sort an array in ascending and descending order.
7. Write a program to eliminate duplicates from an array.
8. Write a program to calculate the sum of first n natural numbers.
9. Write a program to display the multiplication table for a given number.
10. Write a program to check if a number is a palindrome.
