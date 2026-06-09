# Module 05 — Flow Control

> **Part B: Language Fundamentals**
> Prerequisites: Module 03, 04 · Time: ~1.5 hours

---

## What Is Flow Control?

By default, Java executes statements **top to bottom**, one after another. Flow control lets you:

- Make **decisions** — run this code only if a condition is true
- **Repeat** — run this code multiple times
- **Jump** — exit a loop early or skip an iteration

---

## Decision Making

### `if` Statement

```java
double salary = 95000;

if (salary > 80000) {
    System.out.println("Senior pay band");
}
```

Curly braces are optional when there is only one statement — but always use them. The cost of a brace is zero; the cost of a bug from omitting it can be high.

### `if-else`

```java
boolean isActive = false;

if (isActive) {
    System.out.println("Employee is active");
} else {
    System.out.println("Employee is inactive");
}
```

### `if-else if-else`

```java
double salary = 72000;
String band;

if (salary >= 100000) {
    band = "Executive";
} else if (salary >= 80000) {
    band = "Senior";
} else if (salary >= 60000) {
    band = "Mid-level";
} else {
    band = "Junior";
}

System.out.println("Pay band: " + band);   // Pay band: Mid-level
```

The conditions are evaluated **top to bottom**. As soon as one is true, its block runs and the rest are skipped.

### Nested `if`

```java
String department = "Engineering";
double salary     = 90000;

if (department.equals("Engineering")) {
    if (salary > 85000) {
        System.out.println("Senior Engineer bonus: 20%");
    } else {
        System.out.println("Engineer bonus: 10%");
    }
} else {
    System.out.println("Standard bonus: 5%");
}
```

Keep nesting shallow — more than two levels deep is a signal to refactor.

---

## `switch` Statement

When you have **one variable being compared against multiple fixed values**, `switch` is cleaner than a long `if-else if` chain.

### Classic `switch` (Java 1+)

```java
String department = "HR";
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
        bonusPercent = 10;    // HR and Finance share the same value — fall-through
        break;
    default:
        bonusPercent = 5;
        break;
}

System.out.println("Bonus: " + bonusPercent + "%");   // Bonus: 10%
```

**`break` is mandatory** to prevent fall-through. Without `break`, execution continues into the next `case` block — sometimes intentional (as in HR/Finance above), usually a bug.

**`switch` works with:** `int`, `byte`, `short`, `char`, `String` (Java 7+), `enum` (Java 5+). Not with `double`, `float`, `long`, or `boolean`.

### Arrow `switch` Expression (Java 14+)

Cleaner syntax — no `break`, no fall-through, returns a value:

```java
String department = "Sales";

int bonusPercent = switch (department) {
    case "Engineering"         -> 20;
    case "Sales"               -> 25;
    case "HR", "Finance"       -> 10;    // multiple labels, comma-separated
    default                    -> 5;
};

System.out.println("Bonus: " + bonusPercent + "%");   // Bonus: 25%
```

Much cleaner. Use this form when on Java 14+. The arrow `->` makes fall-through impossible — each case is isolated.

For multi-line case blocks, use `yield` to return the value:

```java
int bonusPercent = switch (department) {
    case "Sales" -> {
        System.out.println("Sales team — commission included");
        yield 25;    // yield returns the value from a block
    }
    default -> 5;
};
```

---

## Loops

### `while` Loop

Checks the condition **before** each iteration. If the condition is false from the start, the body never runs.

```java
// Print active employees from a list (simulated with index)
int index = 0;
int totalEmployees = 5;

while (index < totalEmployees) {
    System.out.println("Processing employee #" + (index + 1));
    index++;
}
```

```java
// Classic use: keep prompting until valid input
Scanner scanner = new Scanner(System.in);
int salary = -1;

while (salary <= 0) {
    System.out.print("Enter salary (must be positive): ");
    salary = scanner.nextInt();
}
System.out.println("Salary accepted: " + salary);
```

### `do-while` Loop

Executes the body **at least once**, then checks the condition.

```java
int attempts = 0;

do {
    System.out.println("Attempt " + (attempts + 1) + ": validating employee data...");
    attempts++;
} while (attempts < 3);

// Output:
// Attempt 1: validating employee data...
// Attempt 2: validating employee data...
// Attempt 3: validating employee data...
```

Use `do-while` when you need something to run at least once before checking whether to continue — like a menu system or an initial validation attempt.

### `for` Loop

Best when you know **exactly how many iterations** you need:

```java
// syntax: for (initialization; condition; update)

for (int i = 1; i <= 5; i++) {
    System.out.println("Employee #" + i);
}
```

All three parts are optional:

```java
int i = 0;
for (; i < 5; ) {    // init and update moved outside
    System.out.println(i);
    i++;
}
```

An infinite loop (intentional):
```java
for (;;) {
    // runs forever — needs a break to exit
}
```

### `for-each` Loop (Enhanced for)

The cleanest way to iterate over arrays and collections. Use this whenever you just need to visit each element:

```java
String[] employees = {"Sonu", "Monu", "Tonu", "Ponu", "Gonu"};

for (String name : employees) {
    System.out.println("Hello, " + name);
}
```

```java
double[] salaries = {75000, 82000, 55000, 91000, 68000};
double   total    = 0;

for (double salary : salaries) {
    total += salary;
}
System.out.println("Total payroll: " + total);   // 371000.0
```

**Limitation of for-each:** You cannot modify the array elements through the loop variable, and you have no access to the index. Use a regular `for` loop when you need those.

---

## Loop Control — `break` and `continue`

### `break` — Exit the Loop Immediately

```java
String[] employees = {"Sonu", "Monu", "TERMINATED", "Ponu", "Gonu"};

for (String name : employees) {
    if (name.equals("TERMINATED")) {
        System.out.println("Invalid record found — stopping.");
        break;
    }
    System.out.println("Processing: " + name);
}

// Output:
// Processing: Sonu
// Processing: Monu
// Invalid record found — stopping.
```

### `continue` — Skip to Next Iteration

```java
double[] salaries = {75000, -1, 82000, -1, 55000};   // -1 means data missing

for (double salary : salaries) {
    if (salary < 0) {
        System.out.println("Skipping invalid salary record.");
        continue;
    }
    System.out.println("Valid salary: " + salary);
}

// Output:
// Valid salary: 75000.0
// Skipping invalid salary record.
// Valid salary: 82000.0
// Skipping invalid salary record.
// Valid salary: 55000.0
```

### Labeled `break` and `continue`

When dealing with nested loops, `break` and `continue` apply to the **innermost loop** by default. Use labels to target an outer loop:

```java
String[] departments = {"Engineering", "HR", "Finance"};
String[] employees   = {"Sonu", "Monu", "TARGET", "Ponu"};

outer:
for (String dept : departments) {
    for (String emp : employees) {
        if (emp.equals("TARGET")) {
            System.out.println("Found in " + dept + " — stopping all loops.");
            break outer;    // breaks out of the outer loop, not just inner
        }
        System.out.println(dept + " — " + emp);
    }
}
```

Labeled breaks are valid but use them sparingly — they can make code hard to follow. Often, extracting the nested loop into a method with a `return` is cleaner.

---

## Nested Loops

```java
// Generate a simple employee-project assignment matrix
String[] employees = {"Sonu", "Monu", "Tonu"};
String[] projects  = {"Alpha", "Beta", "Gamma"};

System.out.printf("%-10s", "");
for (String project : projects) {
    System.out.printf("%-10s", project);
}
System.out.println();

for (String emp : employees) {
    System.out.printf("%-10s", emp);
    for (String project : projects) {
        System.out.printf("%-10s", "Yes");   // all assigned for demo
    }
    System.out.println();
}
```

Output:
```
          Alpha     Beta      Gamma
Sonu      Yes       Yes       Yes
Monu      Yes       Yes       Yes
Tonu      Yes       Yes       Yes
```

---

## Choosing the Right Loop

| Situation | Best Choice |
|-----------|------------|
| Known count / index needed | `for` |
| Iterating elements, no index needed | `for-each` |
| Loop until condition is met | `while` |
| Must run at least once | `do-while` |
| Need to exit early on a condition | Any loop + `break` |
| Need to skip some iterations | Any loop + `continue` |

---

## Practical Example — Payroll Summary

```java
public class PayrollSummary {
    public static void main(String[] args) {

        String[] names      = {"Sonu", "Monu", "Tonu", "Ponu", "Gonu"};
        double[] salaries   = {75000, 82000, 55000, 91000, 68000};
        boolean[] isActive  = {true, true, false, true, true};

        double totalPayroll = 0;
        int    activeCount  = 0;
        double highestSalary = 0;
        String highestEarner = "";

        System.out.printf("%-10s %12s %10s%n", "Name", "Salary", "Status");
        System.out.println("-".repeat(35));

        for (int i = 0; i < names.length; i++) {

            String status = isActive[i] ? "Active" : "Inactive";
            System.out.printf("%-10s %12.2f %10s%n", names[i], salaries[i], status);

            if (!isActive[i]) continue;   // skip inactive employees

            totalPayroll += salaries[i];
            activeCount++;

            if (salaries[i] > highestSalary) {
                highestSalary = salaries[i];
                highestEarner = names[i];
            }
        }

        System.out.println("-".repeat(35));
        System.out.printf("Active employees : %d%n", activeCount);
        System.out.printf("Total payroll    : %.2f%n", totalPayroll);
        System.out.printf("Highest earner   : %s (%.2f)%n", highestEarner, highestSalary);
    }
}
```

Output:
```
Name            Salary     Status
-----------------------------------
Sonu          75000.00     Active
Monu          82000.00     Active
Tonu          55000.00   Inactive
Ponu          91000.00     Active
Gonu          68000.00     Active
-----------------------------------
Active employees : 4
Total payroll    : 316000.00
Highest earner   : Ponu (91000.00)
```

---

## Quick Summary

| Statement | When to Use |
|-----------|------------|
| `if-else if-else` | Multiple conditions on different variables or ranges |
| `switch` | One variable, multiple exact values |
| Arrow switch (Java 14+) | Cleaner switch that returns a value |
| `for` | Known iteration count |
| `for-each` | Iterating collections/arrays without needing index |
| `while` | Unknown count, condition-driven |
| `do-while` | At least one execution guaranteed |
| `break` | Exit loop immediately |
| `continue` | Skip current iteration |

---

## What's Next

**Module 06** — Arrays. The simplest data structure in Java — storing and working with multiple values of the same type.
