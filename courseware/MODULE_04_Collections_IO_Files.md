# Core Java Courseware — Module 4: Collections, IO Streams & File Handling

> **Author:** Vaman Deshmukh | **Updated & Modernized:** 2024
> **Covers:** Core Java 10, 11 — Collection Framework, IO Streams, Serialization, File API

---

## Table of Contents

- [1. Collection Framework Overview](#1-collection-framework-overview)
- [2. List Interface](#2-list-interface)
- [3. Set Interface](#3-set-interface)
- [4. Queue and Deque](#4-queue-and-deque)
- [5. Map Interface](#5-map-interface)
- [6. Generics](#6-generics)
- [7. Collections Utility Class](#7-collections-utility-class)
- [8. Java IO Streams](#8-java-io-streams)
- [9. Working with Files](#9-working-with-files)
- [10. Serialization and Deserialization](#10-serialization-and-deserialization)
- [Interview Questions](#interview-questions)
- [Assignments](#assignments)

---

## 1. Collection Framework Overview

### Why Collections?

Arrays have limitations:
- Fixed size — cannot grow or shrink
- Only one data type
- No built-in algorithms (sort, search, etc.)

The **Java Collections Framework** solves all of these:

```
java.util package
├── Dynamic sizing
├── Type safety (via Generics)
├── Rich algorithms (sort, shuffle, reverse, binarySearch...)
└── Multiple data structures
```

### The Collection Hierarchy

```
Iterable
└── Collection
    ├── List (ordered, duplicates allowed)
    │   ├── ArrayList
    │   ├── LinkedList
    │   ├── Vector (legacy, synchronized)
    │   └── Stack (legacy, use Deque instead)
    ├── Set (unordered, no duplicates)
    │   ├── HashSet
    │   ├── LinkedHashSet (insertion-ordered)
    │   └── TreeSet (sorted)
    └── Queue (FIFO)
        ├── LinkedList
        ├── PriorityQueue (sorted)
        └── Deque (double-ended)
            ├── ArrayDeque
            └── LinkedList

Map (key-value pairs — separate hierarchy)
├── HashMap (unordered)
├── LinkedHashMap (insertion-ordered)
├── TreeMap (sorted by key)
└── Hashtable (legacy, synchronized)
```

### Properties Comparison

| Collection | Ordered | Duplicates | Sorted | Null | Thread-safe |
|-----------|---------|-----------|--------|------|-------------|
| `ArrayList` | ✓ (insertion) | ✓ | ✗ | ✓ | ✗ |
| `LinkedList` | ✓ | ✓ | ✗ | ✓ | ✗ |
| `HashSet` | ✗ | ✗ | ✗ | ✓ (1) | ✗ |
| `LinkedHashSet` | ✓ (insertion) | ✗ | ✗ | ✓ (1) | ✗ |
| `TreeSet` | ✓ (sorted) | ✗ | ✓ | ✗ | ✗ |
| `HashMap` | ✗ | values ✓ | ✗ | ✓ (1 null key) | ✗ |
| `TreeMap` | ✓ (key sorted) | values ✓ | ✓ | ✗ | ✗ |
| `PriorityQueue` | ✓ (priority) | ✓ | ✓ (min-heap) | ✗ | ✗ |

---

## 2. List Interface

`List` is an **ordered collection** that preserves insertion order and allows duplicates.

### ArrayList

- Backed by a **dynamic array** — random access is O(1)
- Insertion/deletion in the middle is O(n) (requires shifting)
- Best for **reading and accessing** data

```java
import java.util.ArrayList;
import java.util.Collections;

// Create
ArrayList<String> names = new ArrayList<>();

// Add
names.add("Alice");
names.add("Bob");
names.add("Charlie");
names.add(1, "Zara");   // insert at index 1

// Access
System.out.println(names.get(0));   // Alice
System.out.println(names.size());   // 4

// Update
names.set(2, "Bobby");

// Remove
names.remove("Alice");    // by value
names.remove(0);          // by index

// Check
System.out.println(names.contains("Bob"));   // true
System.out.println(names.indexOf("Bob"));    // index of Bob

// Iterate
for (String name : names) {
    System.out.println(name);
}

// Sort
Collections.sort(names);                          // natural order
names.sort((a, b) -> b.compareTo(a));             // reverse order (Java 8+)

// Convert to array
String[] arr = names.toArray(new String[0]);

// Clear
names.clear();
System.out.println(names.isEmpty());   // true
```

### LinkedList

- Backed by a **doubly-linked list**
- Insertion/deletion at any position is O(1) (with a reference)
- Random access is O(n)
- Best for **frequent insertions/deletions**; also implements `Queue` and `Deque`

```java
import java.util.LinkedList;

LinkedList<Integer> ll = new LinkedList<>();
ll.add(10);
ll.add(20);
ll.add(30);

// Deque operations
ll.addFirst(5);    // [5, 10, 20, 30]
ll.addLast(40);    // [5, 10, 20, 30, 40]
ll.removeFirst();  // removes 5
ll.removeLast();   // removes 40

System.out.println(ll.peek());      // view head without removing
System.out.println(ll.poll());      // view and remove head
```

### ArrayList vs LinkedList

| Aspect | ArrayList | LinkedList |
|--------|-----------|------------|
| Internal structure | Dynamic array | Doubly linked list |
| Access by index | O(1) — fast | O(n) — slow |
| Insert/delete at end | O(1) amortized | O(1) |
| Insert/delete in middle | O(n) — slow | O(1) with pointer |
| Memory | Less (no pointers) | More (prev/next pointers) |
| Best for | Random access, read-heavy | Frequent insert/delete |

### Vector (Legacy)

Similar to `ArrayList` but **synchronized** (thread-safe). Slower due to synchronization overhead. Use `ArrayList` + `Collections.synchronizedList()` in modern code.

```java
import java.util.Vector;

Vector<Integer> v = new Vector<>(3, 2);  // initial capacity 3, increment by 2
v.add(10); v.add(20); v.add(30);
System.out.println(v.capacity());        // 3

v.add(40);                               // exceeds capacity
System.out.println(v.capacity());        // 5 (3 + 2)
```

### Stack

LIFO (Last-In-First-Out) data structure. Use `ArrayDeque` in modern code (faster).

```java
import java.util.Stack;

Stack<Integer> stack = new Stack<>();
stack.push(10);
stack.push(20);
stack.push(30);

System.out.println(stack.peek());   // 30 — view top without removing
System.out.println(stack.pop());    // 30 — remove and return top
System.out.println(stack.search(10)); // 3 — position from top (1-based)
System.out.println(stack.empty());  // false

// Modern alternative:
Deque<Integer> modernStack = new ArrayDeque<>();
modernStack.push(10);
modernStack.peek();
modernStack.pop();
```

---

## 3. Set Interface

`Set` does **not allow duplicate elements**. Adding a duplicate returns `false`.

### HashSet

- Uses a **hash table** internally
- No guaranteed order
- Fastest operations — O(1) average for add/remove/contains
- Allows one `null` element

```java
import java.util.HashSet;

HashSet<String> set = new HashSet<>();
set.add("Apple");
set.add("Banana");
set.add("Apple");   // duplicate — ignored, returns false

System.out.println(set);             // [Banana, Apple] — any order
System.out.println(set.size());      // 2
System.out.println(set.contains("Banana"));  // true

set.remove("Banana");

// Iterate
for (String fruit : set) {
    System.out.println(fruit);
}
```

### LinkedHashSet

Maintains **insertion order** (unlike `HashSet`).

```java
import java.util.LinkedHashSet;

LinkedHashSet<String> lhs = new LinkedHashSet<>();
lhs.add("C");
lhs.add("A");
lhs.add("B");

System.out.println(lhs);   // [C, A, B] — insertion order preserved
```

### TreeSet

Stores elements in **sorted (natural/custom) order**. Backed by a Red-Black tree. O(log n) operations. Does not allow `null`.

```java
import java.util.TreeSet;

TreeSet<Integer> ts = new TreeSet<>();
ts.add(50);
ts.add(20);
ts.add(80);
ts.add(10);
ts.add(20);  // duplicate — ignored

System.out.println(ts);          // [10, 20, 50, 80] — sorted ascending
System.out.println(ts.first());  // 10
System.out.println(ts.last());   // 80

// Custom order (descending)
TreeSet<Integer> desc = new TreeSet<>(Collections.reverseOrder());
desc.add(50); desc.add(20); desc.add(80);
System.out.println(desc);   // [80, 50, 20]
```

---

## 4. Queue and Deque

### Queue (FIFO — First In, First Out)

```java
import java.util.Queue;
import java.util.LinkedList;

Queue<String> queue = new LinkedList<>();
queue.offer("First");   // add to tail
queue.offer("Second");
queue.offer("Third");

System.out.println(queue.peek());    // "First" — view head, don't remove
System.out.println(queue.poll());    // "First" — view and remove head
System.out.println(queue);          // [Second, Third]
```

**Queue methods:**

| Method | Action | On failure |
|--------|--------|-----------|
| `offer(e)` | Add to tail | returns `false` |
| `add(e)` | Add to tail | throws exception |
| `poll()` | Remove head | returns `null` |
| `remove()` | Remove head | throws exception |
| `peek()` | View head | returns `null` |
| `element()` | View head | throws exception |

### PriorityQueue

Elements are ordered by **natural order or custom comparator** — smallest element dequeued first.

```java
import java.util.PriorityQueue;

PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(50);
pq.offer(20);
pq.offer(80);
pq.offer(10);

System.out.println(pq);         // internal storage, not sorted display
System.out.println(pq.poll());  // 10 — always smallest first
System.out.println(pq.poll());  // 20

// Max-heap (largest first)
PriorityQueue<Integer> maxPQ = new PriorityQueue<>(Collections.reverseOrder());
```

### Deque (Double-Ended Queue)

Add/remove from both ends. Use `ArrayDeque` for best performance.

```java
import java.util.Deque;
import java.util.ArrayDeque;

Deque<String> deque = new ArrayDeque<>();
deque.addFirst("Middle");
deque.addFirst("Start");
deque.addLast("End");

System.out.println(deque);          // [Start, Middle, End]
System.out.println(deque.peekFirst());   // Start
System.out.println(deque.peekLast());    // End
deque.removeFirst();
deque.removeLast();
System.out.println(deque);          // [Middle]
```

---

## 5. Map Interface

`Map` stores **key-value pairs**. Keys must be **unique**; values can repeat.

### HashMap

- No guaranteed order
- Allows one `null` key, multiple `null` values
- O(1) average for get/put

```java
import java.util.HashMap;
import java.util.Map;

HashMap<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Bob", 87);
scores.put("Charlie", 92);
scores.put("Bob", 90);    // replaces Bob's value

System.out.println(scores.get("Alice"));    // 95
System.out.println(scores.getOrDefault("David", 0));  // 0 (Java 8+)
System.out.println(scores.containsKey("Bob"));   // true
System.out.println(scores.containsValue(95));    // true

// Iterate keys
for (String key : scores.keySet()) {
    System.out.println(key + ": " + scores.get(key));
}

// Iterate key-value pairs (preferred)
for (Map.Entry<String, Integer> entry : scores.entrySet()) {
    System.out.println(entry.getKey() + " = " + entry.getValue());
}

// Java 8+ forEach
scores.forEach((k, v) -> System.out.println(k + ": " + v));

// Remove
scores.remove("Charlie");

// Java 8+ compute methods
scores.compute("Alice", (k, v) -> v + 5);          // add 5 to Alice's score
scores.putIfAbsent("David", 88);                    // only if key not present
scores.merge("Bob", 5, Integer::sum);               // add 5 to Bob's score
```

### LinkedHashMap

Maintains **insertion order** of entries.

```java
import java.util.LinkedHashMap;

LinkedHashMap<String, Integer> lhm = new LinkedHashMap<>();
lhm.put("Banana", 2);
lhm.put("Apple", 1);
lhm.put("Cherry", 3);

System.out.println(lhm);   // {Banana=2, Apple=1, Cherry=3} — insertion order
```

### TreeMap

Stores entries **sorted by keys** (natural order or custom comparator).

```java
import java.util.TreeMap;

TreeMap<String, Integer> tm = new TreeMap<>();
tm.put("Charlie", 3);
tm.put("Alice", 1);
tm.put("Bob", 2);

System.out.println(tm);   // {Alice=1, Bob=2, Charlie=3} — sorted alphabetically
System.out.println(tm.firstKey());   // Alice
System.out.println(tm.lastKey());    // Charlie
```

### When to Use Which Map

| Map | Use when... |
|-----|-------------|
| `HashMap` | Order doesn't matter; fastest |
| `LinkedHashMap` | Insertion order matters |
| `TreeMap` | Keys must be sorted |
| `ConcurrentHashMap` | Multi-threaded access needed |

---

## 6. Generics

Generics allow you to write **type-safe** code that works with any data type, with type checking at **compile time**.

### Without Generics (Before Java 5)

```java
ArrayList list = new ArrayList();
list.add("Hello");
list.add(42);           // no error — but mixing types is dangerous
String s = (String) list.get(1);   // ClassCastException at runtime!
```

### With Generics

```java
ArrayList<String> list = new ArrayList<String>();
list.add("Hello");
// list.add(42);    // COMPILE ERROR — type safety enforced
String s = list.get(0);   // no cast needed
```

### Generic Class

```java
class Pair<A, B> {
    A first;
    B second;

    Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

Pair<String, Integer> p = new Pair<>("Alice", 30);
System.out.println(p);   // (Alice, 30)
```

### Generic Method

```java
static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
}

System.out.println(max(10, 20));          // 20
System.out.println(max("Apple", "Mango")); // Mango
```

### Bounded Type Parameters

```java
// T must be a Number or subclass
static <T extends Number> double sum(List<T> list) {
    double total = 0;
    for (T item : list) total += item.doubleValue();
    return total;
}
```

---

## 7. Collections Utility Class

`java.util.Collections` provides static algorithms for collections:

```java
import java.util.Collections;

List<Integer> nums = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9, 3));

Collections.sort(nums);                          // [1, 2, 3, 5, 8, 9]
Collections.sort(nums, Collections.reverseOrder()); // [9, 8, 5, 3, 2, 1]
Collections.shuffle(nums);                       // random order
Collections.reverse(nums);                       // reverses the list
Collections.swap(nums, 0, 1);                    // swap positions 0 and 1

System.out.println(Collections.max(nums));       // largest element
System.out.println(Collections.min(nums));       // smallest element
System.out.println(Collections.frequency(nums, 5)); // count occurrences

Collections.sort(nums);
System.out.println(Collections.binarySearch(nums, 5));  // index (list must be sorted)

// Thread-safe wrappers
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());

// Immutable collections
List<String> immutable = Collections.unmodifiableList(new ArrayList<>(Arrays.asList("A", "B")));
// immutable.add("C");  // UnsupportedOperationException

// Java 9+ factory methods (even cleaner)
List<String> fixed = List.of("A", "B", "C");
Set<Integer> fixedSet = Set.of(1, 2, 3);
Map<String, Integer> fixedMap = Map.of("one", 1, "two", 2);
```

---

## 8. Java IO Streams

### Stream Hierarchy

```
IO Streams
├── Byte Streams (8-bit data)
│   ├── InputStream
│   │   ├── FileInputStream
│   │   ├── BufferedInputStream
│   │   └── DataInputStream
│   └── OutputStream
│       ├── FileOutputStream
│       ├── BufferedOutputStream
│       └── DataOutputStream
└── Character Streams (16-bit Unicode)
    ├── Reader
    │   ├── FileReader
    │   ├── BufferedReader
    │   └── InputStreamReader
    └── Writer
        ├── FileWriter
        ├── BufferedWriter
        └── PrintWriter
```

### Byte Streams — File Reading/Writing

```java
import java.io.*;

// Read a binary file
try (FileInputStream fis = new FileInputStream("image.png")) {
    int byteData;
    while ((byteData = fis.read()) != -1) {
        // process each byte
    }
}

// Write to a file
String text = "Hello, Java IO!";
try (FileOutputStream fos = new FileOutputStream("output.txt")) {
    fos.write(text.getBytes());
    System.out.println("Written successfully.");
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
}
```

> **Note:** Use byte streams for binary data (images, audio). For text, use character streams.

### Character Streams — Text Reading/Writing

```java
// Read text file line by line
try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Write text to file
try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
    bw.write("First line");
    bw.newLine();
    bw.write("Second line");
} catch (IOException e) {
    e.printStackTrace();
}
```

### Reading Keyboard Input

```java
// Using Scanner (simplest)
Scanner sc = new Scanner(System.in);
String input = sc.nextLine();

// Using BufferedReader (faster, more control)
try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
    String line = br.readLine();
    System.out.println("You entered: " + line);
}
```

### Buffered Streams

Buffering reduces the number of actual read/write operations by collecting data in memory before flushing to disk — dramatically improves performance.

```java
// Without buffering — slow (each operation hits disk)
FileWriter fw = new FileWriter("file.txt");

// With buffering — fast (data batched in memory buffer)
BufferedWriter bw = new BufferedWriter(new FileWriter("file.txt"));
```

### Data Streams (Primitive Types)

Read and write Java primitives (`int`, `double`, `boolean`, etc.) to streams:

```java
// Write primitives
try (DataOutputStream dos = new DataOutputStream(
        new BufferedOutputStream(new FileOutputStream("data.bin")))) {
    dos.writeInt(42);
    dos.writeDouble(3.14);
    dos.writeBoolean(true);
    dos.writeUTF("Hello");
}

// Read primitives (must be read in same order as written)
try (DataInputStream dis = new DataInputStream(
        new BufferedInputStream(new FileInputStream("data.bin")))) {
    int i = dis.readInt();
    double d = dis.readDouble();
    boolean b = dis.readBoolean();
    String s = dis.readUTF();
}
```

### Modern NIO (java.nio) — Java 7+

The `java.nio` package provides more efficient file operations:

```java
import java.nio.file.*;
import java.io.IOException;
import java.util.List;

Path path = Paths.get("myFile.txt");

// Read all lines at once
List<String> lines = Files.readAllLines(path);

// Write all lines at once
Files.write(path, List.of("Line 1", "Line 2", "Line 3"));

// Copy, move, delete
Files.copy(Paths.get("source.txt"), Paths.get("dest.txt"),
           StandardCopyOption.REPLACE_EXISTING);
Files.move(Paths.get("old.txt"), Paths.get("new.txt"));
Files.delete(path);

// Check file properties
System.out.println(Files.exists(path));
System.out.println(Files.size(path));

// Stream lines (Java 8+) — memory efficient for large files
try (java.util.stream.Stream<String> stream = Files.lines(path)) {
    stream.filter(line -> line.contains("error"))
          .forEach(System.out::println);
}
```

---

## 9. Working with Files

### The `File` Class (Legacy)

```java
import java.io.File;
import java.io.IOException;

File file = new File("D:/myData/test.txt");

// Queries
System.out.println(file.exists());         // does the file/dir exist?
System.out.println(file.isFile());         // is it a file?
System.out.println(file.isDirectory());    // is it a directory?
System.out.println(file.getName());        // "test.txt"
System.out.println(file.getParent());      // "D:/myData"
System.out.println(file.length());         // size in bytes
System.out.println(file.lastModified());   // timestamp
System.out.println(file.canRead());
System.out.println(file.canWrite());

// Operations
try {
    boolean created = file.createNewFile();
    System.out.println("Created: " + created);
} catch (IOException e) {
    e.printStackTrace();
}

File renamedFile = new File("D:/myData/renamed.txt");
boolean renamed = file.renameTo(renamedFile);

boolean deleted = file.delete();

// Directory operations
File dir = new File("D:/myData/newFolder");
dir.mkdir();        // create single directory
dir.mkdirs();       // create directory + all missing parents

String[] children = dir.list();         // filenames in directory
File[] fileList = dir.listFiles();      // File objects for contents
```

### Modern Path-Based API (`java.nio.file`) — Recommended

```java
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

Path p = Path.of("data", "config.txt");   // platform-independent

// Create
Files.createFile(p);
Files.createDirectories(Path.of("data/logs/archive"));

// Read
String content = Files.readString(p);   // Java 11+
byte[] bytes = Files.readAllBytes(p);

// Write
Files.writeString(p, "Hello World", StandardOpenOption.CREATE);
Files.write(p, bytes);

// Copy and move
Files.copy(p, Path.of("backup.txt"), StandardCopyOption.REPLACE_EXISTING);

// Attributes
BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
System.out.println("Size: " + attrs.size());
System.out.println("Created: " + attrs.creationTime());

// Walk directory tree
Files.walk(Path.of("src"))
     .filter(path -> path.toString().endsWith(".java"))
     .forEach(System.out::println);
```

---

## 10. Serialization and Deserialization

### What Is Serialization?

**Serialization** converts a Java object into a **byte stream** for:
- Persisting to a file
- Sending over a network
- Caching

**Deserialization** is the reverse — reconstructing an object from the byte stream.

```
Object (in heap) ←→ Byte stream (in file/network)
    Serialize →
    ← Deserialize
```

### Requirements

1. The class must implement `java.io.Serializable` (marker interface — no methods)
2. All fields must be serializable (or marked `transient`)
3. Recommended: define `serialVersionUID` for version control

### Example

```java
import java.io.*;

// Step 1: Serializable class
class Employee implements Serializable {
    private static final long serialVersionUID = 1L;  // recommended

    int eid;
    String name;
    long phone;
    transient double salary;  // transient — NOT serialized (sensitive data)

    Employee(int eid, String name, long phone, double salary) {
        this.eid = eid;
        this.name = name;
        this.phone = phone;
        this.salary = salary;
    }
}

// Step 2: Serialize
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("employee.ser"))) {
    Employee emp = new Employee(101, "Alice", 9876543210L, 75000.00);
    oos.writeObject(emp);
    System.out.println("Serialized successfully.");
} catch (IOException e) {
    e.printStackTrace();
}

// Step 3: Deserialize
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("employee.ser"))) {
    Employee emp = (Employee) ois.readObject();
    System.out.println("ID: " + emp.eid);
    System.out.println("Name: " + emp.name);
    System.out.println("Salary: " + emp.salary);   // 0.0 — transient not restored
} catch (IOException | ClassNotFoundException e) {
    e.printStackTrace();
}
```

### The `transient` Keyword

Marks a field to be **excluded from serialization**. Useful for:
- Sensitive data (passwords, credit card numbers)
- Derived fields (can be recomputed)
- Non-serializable fields (sockets, streams)

```java
class Config implements Serializable {
    String host;             // serialized
    int port;                // serialized
    transient String password;   // NOT serialized
    transient Connection conn;   // NOT serialized (Connection isn't Serializable)
}
```

---

## Interview Questions

1. What is the Java Collections Framework? What are its main components?
2. What is the difference between `ArrayList` and `LinkedList`?
3. What is the difference between `ArrayList` and `Vector`?
4. What is the difference between `HashSet`, `LinkedHashSet`, and `TreeSet`?
5. What is the difference between `HashMap`, `LinkedHashMap`, and `TreeMap`?
6. What is a `Map`? Why does it have a separate hierarchy from `Collection`?
7. What is a load factor and how does it affect `HashMap`?
8. What is the difference between `HashMap` and `Hashtable`?
9. Why are `null` keys not allowed in `TreeMap` and `TreeSet`?
10. What is Generics? What are its advantages?
11. What is the difference between byte streams and character streams?
12. What is a buffered stream and why is it used?
13. What is serialization? What is the purpose of `serialVersionUID`?
14. What does the `transient` keyword do?
15. What is the difference between `Serializable` and `Externalizable`?
16. What is the `File` class? What methods does it provide?
17. What is `java.nio` and how is it different from `java.io`?
18. What is the difference between `Queue` and `Deque`?
19. What is a `PriorityQueue`? What order does it maintain?
20. How do you make a collection thread-safe in Java?

---

## Assignments

1. Write a method that accepts a `HashMap<String, Integer>` and returns the values in sorted order as a `List`.

2. Write a method that accepts a character array and counts the occurrences of each character using a `HashMap<Character, Integer>`.

3. Write a method accepting an `int[]` and returning a `HashMap` of each number and its square.

4. A school gives medals: Gold (≥90), Silver (80–89), Bronze (70–79). Write a method accepting `HashMap<Integer, Integer>` (reg.no → marks) and returning `HashMap<Integer, String>` (reg.no → medal type).

5. Write a method that accepts a `Map<Integer, Integer>` (ID → age) and returns a `List` of IDs eligible to vote (age > 18).

6. Write a program using `BufferedReader` and `BufferedWriter` to copy a text file line by line, adding line numbers.

7. Create a serializable `BankAccount` class and demonstrate serialization/deserialization, marking `balance` as non-transient and `password` as transient.

8. Using `java.nio.file.Files`, write a program to list all `.java` files in a directory and its subdirectories.
