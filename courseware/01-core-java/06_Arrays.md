# Module 06 — Arrays

> **Part B: Language Fundamentals**
> Prerequisites: Module 03, 05 · Time: ~1 hour

---

## What Is an Array?

An array is a **fixed-size, ordered collection of elements of the same type**. All elements live in contiguous memory, and each is accessed by its **index** (position), starting at 0.

```
String[] employees:
Index →  [0]     [1]     [2]     [3]     [4]
         "Sonu"  "Monu"  "Tonu"  "Ponu"  "Gonu"
```

**Key properties:**
- Fixed size — once created, size cannot change
- Zero-indexed — first element is at index 0, last is at `length - 1`
- Homogeneous — all elements must be the same type
- Objects on the heap — the array itself is an object even if it holds primitives

---

## Declaring and Creating Arrays

```java
// Declaration — tells Java the type and name
int[] salaries;
String[] names;

// Creation — allocates memory on the heap
salaries = new int[5];       // array of 5 ints, all initialized to 0
names    = new String[5];    // array of 5 Strings, all initialized to null

// Declaration + creation in one line
double[] percentages = new double[4];    // initialized to 0.0
boolean[] flags      = new boolean[3];  // initialized to false
```

**Default values after creation:**

| Type | Default Value |
|------|--------------|
| `int`, `byte`, `short`, `long` | `0` |
| `float`, `double` | `0.0` |
| `char` | `'\u0000'` (null character) |
| `boolean` | `false` |
| Object types (String, etc.) | `null` |

---

## Initializing Arrays

### Assign Elements Individually

```java
int[] salaries = new int[3];
salaries[0] = 75000;
salaries[1] = 82000;
salaries[2] = 55000;
```

### Array Initializer — Declaration and Values Together

```java
// Concise syntax — size inferred from the values
String[] names    = {"Sonu", "Monu", "Tonu", "Ponu", "Gonu"};
double[] salaries = {75000.0, 82000.0, 55000.0, 91000.0, 68000.0};
int[]    ids      = {101, 102, 103, 104, 105};
```

### `new` Keyword with Values

```java
String[] departments = new String[]{"Engineering", "HR", "Finance"};
```

This form is required when you create an array inline without assigning it to a variable:

```java
printAll(new String[]{"Sonu", "Monu", "Tonu"});   // anonymous array
```

---

## Accessing and Modifying Elements

```java
String[] employees = {"Sonu", "Monu", "Tonu", "Ponu", "Gonu"};

// Read
System.out.println(employees[0]);    // Sonu
System.out.println(employees[4]);    // Gonu

// Modify
employees[2] = "Ronu";
System.out.println(employees[2]);    // Ronu

// Array length — property, not a method (no parentheses)
System.out.println(employees.length);  // 5
```

**Out of bounds:**

```java
System.out.println(employees[5]);   // ArrayIndexOutOfBoundsException — valid indices: 0–4
System.out.println(employees[-1]);  // ArrayIndexOutOfBoundsException — no negative indices
```

---

## Iterating Over Arrays

### Using `for` Loop (with index)

Use when you need the index:

```java
String[] names    = {"Sonu", "Monu", "Tonu", "Ponu", "Gonu"};
double[] salaries = {75000, 82000, 55000, 91000, 68000};

for (int i = 0; i < names.length; i++) {
    System.out.printf("%-5s → %.2f%n", names[i], salaries[i]);
}
```

Output:
```
Sonu  → 75000.00
Monu  → 82000.00
Tonu  → 55000.00
Ponu  → 91000.00
Gonu  → 68000.00
```

### Using `for-each` (without index)

Use when you just need each element:

```java
double[] salaries = {75000, 82000, 55000, 91000, 68000};
double total = 0;

for (double salary : salaries) {
    total += salary;
}
System.out.println("Total payroll: " + total);   // 371000.0
```

---

## `java.util.Arrays` — Utility Methods

Import: `import java.util.Arrays;`

```java
int[] scores = {45, 90, 23, 78, 56};

// Sort — ascending order, modifies the original array
Arrays.sort(scores);
System.out.println(Arrays.toString(scores));   // [23, 45, 56, 78, 90]

// Binary search — array must be sorted first
int idx = Arrays.binarySearch(scores, 56);
System.out.println("Found at index: " + idx);  // Found at index: 2

// Fill — set all elements to a value
int[] bonuses = new int[5];
Arrays.fill(bonuses, 1000);
System.out.println(Arrays.toString(bonuses));  // [1000, 1000, 1000, 1000, 1000]

// Copy
int[] copy = Arrays.copyOf(scores, scores.length);       // full copy
int[] part = Arrays.copyOfRange(scores, 1, 4);           // elements at index 1, 2, 3
System.out.println(Arrays.toString(part));               // [45, 56, 78]

// Equals — compare contents
int[] a = {1, 2, 3};
int[] b = {1, 2, 3};
System.out.println(a == b);              // false — different objects
System.out.println(Arrays.equals(a, b)); // true  — same contents

// toString — readable representation (do not just print the array variable directly)
System.out.println(scores);              // [I@1b6d3586  ← useless
System.out.println(Arrays.toString(scores)); // [23, 45, 56, 78, 90]  ← useful
```

---

## 2D Arrays

A 2D array is an array of arrays — think rows and columns:

```java
// 3 employees, 3 months of performance scores
int[][] scores = {
    {85, 90, 88},   // Sonu
    {70, 75, 80},   // Monu
    {92, 88, 95}    // Tonu
};

// Access: [row][column]
System.out.println(scores[0][1]);   // 90 — Sonu's month 2 score
System.out.println(scores[2][2]);   // 95 — Tonu's month 3 score

// Dimensions
System.out.println(scores.length);     // 3 — number of rows
System.out.println(scores[0].length);  // 3 — number of columns in row 0
```

### Iterating a 2D Array

```java
String[] names = {"Sonu", "Monu", "Tonu"};

for (int row = 0; row < scores.length; row++) {
    System.out.printf("%-6s: ", names[row]);
    for (int col = 0; col < scores[row].length; col++) {
        System.out.printf("%4d", scores[row][col]);
    }
    System.out.println();
}
```

Output:
```
Sonu  :   85  90  88
Monu  :   70  75  80
Tonu  :   92  88  95
```

### Jagged Arrays

Rows can have different lengths:

```java
int[][] jagged = new int[3][];
jagged[0] = new int[]{1, 2, 3};          // 3 elements
jagged[1] = new int[]{10, 20};           // 2 elements
jagged[2] = new int[]{100, 200, 300, 400}; // 4 elements
```

---

## Sorting Arrays of Objects

`Arrays.sort()` works on primitive arrays automatically. For object arrays, the objects must implement `Comparable`, or you provide a `Comparator`. This is covered properly in Module 26 (Collections and Generics).

Quick preview using lambda (Module 19):

```java
String[] names = {"Ponu", "Sonu", "Monu", "Gonu", "Tonu"};
Arrays.sort(names);   // lexicographic sort — works because String implements Comparable
System.out.println(Arrays.toString(names));   // [Gonu, Monu, Ponu, Sonu, Tonu]
```

---

## Arrays as Method Parameters and Return Types

```java
public class PayrollUtils {

    // Array as parameter
    public static double calculateTotal(double[] salaries) {
        double total = 0;
        for (double s : salaries) total += s;
        return total;
    }

    // Array as return type
    public static double[] applyRaise(double[] salaries, double percent) {
        double[] raised = new double[salaries.length];
        for (int i = 0; i < salaries.length; i++) {
            raised[i] = salaries[i] * (1 + percent / 100);
        }
        return raised;
    }

    public static void main(String[] args) {
        double[] salaries = {75000, 82000, 55000, 91000, 68000};

        System.out.println("Total: " + calculateTotal(salaries));

        double[] raised = applyRaise(salaries, 10);
        System.out.println("After 10% raise: " + Arrays.toString(raised));
    }
}
```

Output:
```
Total: 371000.0
After 10% raise: [82500.0, 90200.0, 60500.0, 100100.0, 74800.0]
```

**Important:** Arrays are passed by reference — the method receives the address of the array, not a copy. Modifying elements inside a method changes the original array:

```java
public static void doubleAll(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        arr[i] *= 2;       // modifies the original!
    }
}

int[] data = {1, 2, 3};
doubleAll(data);
System.out.println(Arrays.toString(data));   // [2, 4, 6] — original changed
```

---

## Converting Between Arrays and Collections

```java
// Array → List
String[] namesArr  = {"Sonu", "Monu", "Tonu"};
List<String> list  = Arrays.asList(namesArr);   // fixed-size List backed by array

// For a fully mutable List:
List<String> mutableList = new ArrayList<>(Arrays.asList(namesArr));

// List → Array
String[] backToArray = list.toArray(new String[0]);
```

Collections are covered fully in Module 26.

---

## Common Mistakes

```java
// Mistake 1: printing array directly
int[] arr = {1, 2, 3};
System.out.println(arr);               // [I@6d06d69c  ← garbage
System.out.println(Arrays.toString(arr)); // [1, 2, 3]  ← correct

// Mistake 2: off-by-one in loop
for (int i = 0; i <= arr.length; i++) {   // i <= length will hit arr[3] → exception
    System.out.println(arr[i]);
}
// Correct: i < arr.length

// Mistake 3: arrays are not resizable — you cannot add more elements
int[] fixed = {1, 2, 3};
// fixed[3] = 4;  // runtime exception — index 3 does not exist
// Use ArrayList when you need dynamic sizing

// Mistake 4: null check for arrays
String[] arr2 = null;
System.out.println(arr2.length);  // NullPointerException
if (arr2 != null) {
    System.out.println(arr2.length);  // safe
}
```

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Fixed size | Set at creation, cannot change — use `ArrayList` for dynamic |
| Zero-indexed | First element at `[0]`, last at `[length-1]` |
| Default values | Numbers → 0, booleans → false, objects → null |
| `Arrays.toString()` | Always use this to print arrays |
| `Arrays.sort()` | Sorts in-place |
| Pass by reference | Methods receive the array address — changes affect the original |
| 2D array | Array of arrays — access with `[row][col]` |

---

## What's Next

**Module 07** — String Handling. Strings are everywhere in Java programs and deserve their own module — immutability, the String pool, 40+ methods, StringBuilder, and common patterns.
