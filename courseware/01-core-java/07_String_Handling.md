# Module 07 — String Handling

> **Part B: Language Fundamentals**
> Prerequisites: Module 03, 04 · Time: ~1.5 hours

---

## Strings in Java

A `String` is a **sequence of characters**. In Java, `String` is a class — not a primitive — but it gets special treatment that makes it feel like one.

```java
String name = "Sonu";   // looks like a primitive assignment
```

Under the hood, `"Sonu"` is an object of class `java.lang.String`, and the variable `name` holds a reference to it.

---

## Creating Strings

### String Literals (Common)

```java
String name       = "Sonu";
String department = "Engineering";
String empty      = "";               // empty string — not null
```

### Using `new` (Uncommon)

```java
String name = new String("Sonu");   // explicitly creates a new object in heap
```

You almost never need this form. Prefer literals.

---

## Immutability — The Most Important String Concept

**Once a String object is created, its value cannot be changed.**

```java
String name = "Sonu";
name = name + " Sharma";

System.out.println(name);   // Sonu Sharma
```

Looks like the string changed. It did not. What happened:

1. `"Sonu"` object created in memory
2. `"Sonu" + " Sharma"` created a **new** object `"Sonu Sharma"` in memory
3. `name` now points to the new object
4. The original `"Sonu"` is unchanged — it will eventually be garbage collected

```
Before:  name → ["Sonu"]
After:   name → ["Sonu Sharma"]     old ["Sonu"] still exists until GC
```

**Why immutability?**
- **Security:** String is widely used for passwords, file paths, network addresses. If it were mutable, one piece of code could change a string that another piece of code is holding.
- **Thread safety:** Immutable objects are automatically safe for concurrent access.
- **String pool:** Enables sharing of string objects, saving memory.

---

## The String Pool

Java maintains a special area in the **Method Area** called the **String Pool** (also called String Intern Pool). When you write a string literal:

```java
String a = "Engineering";
String b = "Engineering";
```

Both `a` and `b` point to the **same object** in the pool. Java doesn't create two copies.

```java
String a = "Engineering";
String b = "Engineering";
String c = new String("Engineering");   // forced new object on heap

System.out.println(a == b);              // true  — same pool object
System.out.println(a == c);              // false — c is a different heap object
System.out.println(a.equals(c));         // true  — same content
```

**Rule:** Use `.equals()` to compare String content. Never use `==` for Strings.

```java
String dept1 = "HR";
String dept2 = "HR";

// Wrong:
if (dept1 == dept2) { }          // may work by accident (pool), but wrong approach

// Correct:
if (dept1.equals(dept2)) { }     // always works
if ("HR".equals(dept1)) { }      // even better — prevents NPE if dept1 is null
```

---

## String Methods

`String` has a rich API. These are the ones you will use regularly:

### Length and Character Access

```java
String name = "Sonu Sharma";

System.out.println(name.length());        // 11 — number of characters
System.out.println(name.charAt(0));       // 'S'
System.out.println(name.charAt(5));       // 'S'
System.out.println(name.indexOf('o'));    // 1 — first occurrence
System.out.println(name.lastIndexOf('a'));// 10
System.out.println(name.indexOf("Sharma"));  // 5
```

### Case Conversion

```java
String dept = "Engineering";

System.out.println(dept.toUpperCase());   // ENGINEERING
System.out.println(dept.toLowerCase());   // engineering
```

### Trimming and Stripping

```java
String raw = "   Sonu   ";

System.out.println(raw.trim());           // "Sonu" — removes leading/trailing whitespace
System.out.println(raw.strip());          // "Sonu" — Unicode-aware (Java 11+)
System.out.println(raw.stripLeading());   // "Sonu   "
System.out.println(raw.stripTrailing());  // "   Sonu"
```

`trim()` only removes ASCII whitespace (chars ≤ 32). `strip()` handles Unicode whitespace too. Prefer `strip()` on Java 11+.

### Substring

```java
String fullName = "Sonu Sharma";

System.out.println(fullName.substring(5));      // "Sharma"  — from index 5 to end
System.out.println(fullName.substring(0, 4));   // "Sonu"    — from 0 (inclusive) to 4 (exclusive)
```

### Checking Content

```java
String role = "Senior Engineer";

System.out.println(role.startsWith("Senior"));        // true
System.out.println(role.endsWith("Engineer"));         // true
System.out.println(role.contains("Engineer"));         // true
System.out.println(role.isEmpty());                    // false
System.out.println("".isEmpty());                      // true
System.out.println("  ".isEmpty());                    // false — only checks length == 0
System.out.println("  ".isBlank());                    // true  — whitespace-only (Java 11+)
```

### Replace

```java
String dept = "Engineering Department";

System.out.println(dept.replace('e', 'E'));
// EnginEEring DEpartmEnt — replaces all occurrences

System.out.println(dept.replace("Engineering", "HR"));
// HR Department

System.out.println(dept.replaceAll("[aeiou]", "*"));
// *ng*n**r*ng D*p*rtm*nt — regex replace (Module 29 covers regex in depth)

System.out.println(dept.replaceFirst("[A-Z]", "X"));
// Xngineering Department — replaces first match only
```

### Splitting and Joining

```java
// Split
String csv = "Sonu,Monu,Tonu,Ponu,Gonu";
String[] names = csv.split(",");

for (String name : names) {
    System.out.println(name);
}
// Sonu
// Monu
// Tonu
// Ponu
// Gonu

// Split with limit — max number of pieces
String[] parts = csv.split(",", 3);
System.out.println(Arrays.toString(parts));   // [Sonu, Monu, Tonu,Ponu,Gonu]

// Join
String joined = String.join(", ", "Sonu", "Monu", "Tonu");
System.out.println(joined);   // Sonu, Monu, Tonu

String joinedFromArray = String.join(" | ", names);
System.out.println(joinedFromArray);   // Sonu | Monu | Tonu | Ponu | Gonu
```

### Comparison

```java
String s1 = "Engineering";
String s2 = "engineering";

System.out.println(s1.equals(s2));              // false — case-sensitive
System.out.println(s1.equalsIgnoreCase(s2));    // true  — case-insensitive
System.out.println(s1.compareTo(s2));           // negative — 'E'(69) < 'e'(101)
System.out.println(s1.compareToIgnoreCase(s2)); // 0 — equal ignoring case
```

### Converting Other Types to String

```java
int    id      = 101;
double salary  = 75000.0;
boolean active = true;

String s1 = String.valueOf(id);       // "101"
String s2 = String.valueOf(salary);   // "75000.0"
String s3 = String.valueOf(active);   // "true"

// String.format for formatted output
String line = String.format("ID: %d, Name: %-10s, Salary: %.2f", 101, "Sonu", 75000.0);
System.out.println(line);   // ID: 101, Name: Sonu      , Salary: 75000.00
```

### `chars()` — Stream of Characters (Java 8+)

```java
String name = "Sonu";
name.chars()
    .forEach(c -> System.out.print((char) c + " "));
// S o n u
```

### `repeat()` — Java 11+

```java
String separator = "-".repeat(30);
System.out.println(separator);   // ------------------------------
```

---

## `StringBuilder` — Mutable Strings

Every `+` operation on strings creates a new object. In a loop, this creates many short-lived objects:

```java
// Bad — creates 1000 String objects
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;   // new String object each time
}
```

`StringBuilder` is a **mutable** character sequence — it edits in-place, no new objects:

```java
// Good — modifies a single StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String result = sb.toString();
```

### StringBuilder Methods

```java
StringBuilder sb = new StringBuilder("Employee: ");

sb.append("Sonu");                  // Employee: Sonu
sb.append(", ID: ").append(101);    // Employee: Sonu, ID: 101 — chaining
sb.insert(0, ">>> ");               // >>> Employee: Sonu, ID: 101
sb.delete(0, 4);                    // Employee: Sonu, ID: 101
sb.replace(0, 8, "Staff");          // Staff: Sonu, ID: 101
sb.reverse();                       // 101 :DI ,unoS :ffatS

StringBuilder sb2 = new StringBuilder("Hello");
sb2.deleteCharAt(0);                // ello
sb2.setCharAt(0, 'Y');              // Yllo

System.out.println(sb2.length());       // 4
System.out.println(sb2.charAt(1));      // 'l'
System.out.println(sb2.indexOf("l"));   // 1
System.out.println(sb2.toString());     // Yllo
```

### Practical StringBuilder Usage

```java
public static String buildEmployeeReport(String[] names, double[] salaries) {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Employee Report ===\n");

    for (int i = 0; i < names.length; i++) {
        sb.append(String.format("%-10s : %.2f%n", names[i], salaries[i]));
    }

    sb.append("======================\n");
    return sb.toString();
}
```

---

## `StringBuffer` — Thread-Safe StringBuilder

`StringBuffer` is identical to `StringBuilder` but all its methods are `synchronized` (thread-safe).

| Class | Mutable | Thread-Safe | Performance |
|-------|---------|-------------|-------------|
| `String` | No | Yes (immutable) | Fast for reads, slow for repeated modification |
| `StringBuilder` | Yes | No | **Fastest for single-threaded string building** |
| `StringBuffer` | Yes | Yes | Slightly slower than StringBuilder due to synchronization |

**Use:**
- `String` — for values that don't change
- `StringBuilder` — for building strings in single-threaded code (default choice)
- `StringBuffer` — only when multiple threads access the same mutable string (rare)

---

## String Comparison — The Full Picture

```java
String a = "Sonu";
String b = "Sonu";
String c = new String("Sonu");

// == compares references
System.out.println(a == b);       // true  (same pool object)
System.out.println(a == c);       // false (c is a new heap object)

// .equals() compares content
System.out.println(a.equals(b));  // true
System.out.println(a.equals(c));  // true

// .equalsIgnoreCase()
System.out.println("SONU".equalsIgnoreCase("sonu"));   // true

// compareTo — lexicographic comparison
System.out.println("Apple".compareTo("Banana"));  // negative (A < B)
System.out.println("Banana".compareTo("Apple"));  // positive (B > A)
System.out.println("Apple".compareTo("Apple"));   // 0 (equal)
```

---

## Common String Patterns

### Check if a String is a Valid Number

```java
public static boolean isNumeric(String s) {
    if (s == null || s.isBlank()) return false;
    try {
        Double.parseDouble(s);
        return true;
    } catch (NumberFormatException e) {
        return false;
    }
}

System.out.println(isNumeric("75000"));   // true
System.out.println(isNumeric("75k"));     // false
System.out.println(isNumeric(null));      // false
```

### Null-Safe String Check

```java
String name = null;

// Risky:
if (name.equals("Sonu")) { }       // NullPointerException

// Safe — put the literal first:
if ("Sonu".equals(name)) { }       // false, no exception

// Or explicit null check:
if (name != null && name.equals("Sonu")) { }
```

### Count Character Occurrences

```java
public static int countOccurrences(String text, char target) {
    int count = 0;
    for (char c : text.toCharArray()) {
        if (c == target) count++;
    }
    return count;
}

System.out.println(countOccurrences("Engineering", 'e'));  // 1
System.out.println(countOccurrences("Engineering", 'i'));  // 2
```

### Reverse a String

```java
String original = "Sonu";
String reversed = new StringBuilder(original).reverse().toString();
System.out.println(reversed);   // unoS
```

### Palindrome Check

```java
public static boolean isPalindrome(String s) {
    String cleaned  = s.toLowerCase().replaceAll("[^a-z0-9]", "");
    String reversed = new StringBuilder(cleaned).reverse().toString();
    return cleaned.equals(reversed);
}

System.out.println(isPalindrome("racecar"));    // true
System.out.println(isPalindrome("Engineering")); // false
```

---

## Practical Example — Employee Name Formatter

```java
public class NameFormatter {
    public static void main(String[] args) {

        String[] rawNames = {
            "  sonu sharma  ",
            "MONU VERMA",
            "tonu   ",
            "   PONU SINGH",
            "gonu"
        };

        System.out.printf("%-25s %-25s %-10s%n", "Raw", "Formatted", "Initials");
        System.out.println("-".repeat(65));

        for (String raw : rawNames) {
            String trimmed   = raw.strip();
            String formatted = toTitleCase(trimmed);
            String initials  = getInitials(formatted);
            System.out.printf("%-25s %-25s %-10s%n", raw.strip(), formatted, initials);
        }
    }

    static String toTitleCase(String s) {
        String[] words  = s.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1))
                  .append(" ");
            }
        }
        return sb.toString().stripTrailing();
    }

    static String getInitials(String fullName) {
        StringBuilder initials = new StringBuilder();
        for (String word : fullName.split(" ")) {
            initials.append(word.charAt(0));
        }
        return initials.toString();
    }
}
```

Output:
```
Raw                       Formatted                 Initials
-----------------------------------------------------------------
sonu sharma               Sonu Sharma               SS
MONU VERMA                Monu Verma                MV
tonu                      Tonu                      T
PONU SINGH                Ponu Singh                PS
gonu                      Gonu                      G
```

---

## Quick Reference — Method Cheat Sheet

| Method | What It Does |
|--------|-------------|
| `length()` | Number of characters |
| `charAt(i)` | Character at index i |
| `indexOf(s)` | First occurrence index (-1 if not found) |
| `substring(start, end)` | Portion from start (inclusive) to end (exclusive) |
| `toUpperCase()` / `toLowerCase()` | Case conversion |
| `trim()` / `strip()` | Remove leading/trailing whitespace |
| `replace(old, new)` | Replace all occurrences |
| `split(regex)` | Split into array |
| `String.join(sep, ...)` | Join parts with separator |
| `equals(s)` | Content comparison |
| `equalsIgnoreCase(s)` | Case-insensitive comparison |
| `contains(s)` | Does it contain s? |
| `startsWith(s)` / `endsWith(s)` | Prefix/suffix check |
| `isEmpty()` / `isBlank()` | Empty or whitespace-only check |
| `String.valueOf(x)` | Any type → String |
| `String.format(...)` | Formatted String |

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Immutability | Strings never change — operations return new Strings |
| String pool | Literals share objects — `==` is unreliable, use `.equals()` |
| `StringBuilder` | Use for repeated string building — mutable and fast |
| `StringBuffer` | Thread-safe version of StringBuilder — use only when needed |
| Null-safe compare | Put literal first: `"value".equals(variable)` |
| `String.format()` | Cleanest way to build formatted strings |

---

## What's Next

**Module 08** — Classes and Objects. Now that you know the language fundamentals, it is time to design your own types using Java's object-oriented features.
