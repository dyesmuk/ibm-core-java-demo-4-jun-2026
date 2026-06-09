# Module 26 — Collections and Generics

> **Part G: I/O and Collections**
> Prerequisites: Module 08–13, Module 19–21 · Time: ~2.5 hours

---

## Why Collections?

Arrays are fixed-size. In most real programs, you don't know how many objects you'll have upfront — employees are added, removed, searched. You need data structures that grow, shrink, and provide efficient operations.

The **Java Collections Framework** provides ready-made, well-tested implementations:

```
Collection
├── List        — ordered, allows duplicates
│   ├── ArrayList
│   └── LinkedList
│
├── Set         — no duplicates
│   ├── HashSet
│   ├── LinkedHashSet
│   └── TreeSet
│
└── Queue       — ordered for processing
    ├── PriorityQueue
    └── Deque
        └── ArrayDeque

Map             — key-value pairs (not a Collection, but part of the framework)
    ├── HashMap
    ├── LinkedHashMap
    └── TreeMap
```

---

## Generics — Type Safety for Collections

Before generics (Java 5), collections held `Object` — everything needed a cast, and type errors appeared only at runtime:

```java
// Pre-generics — dangerous
List employees = new ArrayList();
employees.add(new Employee(101, "Sonu", 75000, "Engineering"));
employees.add("accidentally a String");   // compiles! wrong, but compiles

Employee e = (Employee) employees.get(1);  // ClassCastException at runtime
```

Generics fix this — the type is enforced at compile time:

```java
// With generics — safe
List<Employee> employees = new ArrayList<>();
employees.add(new Employee(101, "Sonu", 75000, "Engineering"));
employees.add("accidentally a String");   // compile error — caught immediately
```

The `<Employee>` is the **type parameter** — it tells the compiler exactly what type this collection holds.

---

## Generic Classes and Methods

### Generic Class

```java
public class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first  = first;
        this.second = second;
    }

    public A getFirst()  { return first; }
    public B getSecond() { return second; }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
```

```java
Pair<String, Double> salaryEntry = new Pair<>("Sonu", 75000.0);
Pair<Integer, String> idName     = new Pair<>(101, "Monu");

System.out.println(salaryEntry);  // (Sonu, 75000.0)
System.out.println(idName);       // (101, Monu)
```

### Generic Method

```java
public static <T> void printAll(List<T> list) {
    for (T item : list) {
        System.out.println(item);
    }
}

// Works with any type
printAll(List.of("Sonu", "Monu", "Tonu"));
printAll(List.of(75000.0, 82000.0, 55000.0));
printAll(List.of(new Employee(101, "Sonu", 75000, "Eng")));
```

### Bounded Type Parameters

```java
// <T extends Number> — T must be Number or a subclass (Integer, Double, etc.)
public static <T extends Number> double sum(List<T> numbers) {
    return numbers.stream().mapToDouble(Number::doubleValue).sum();
}

sum(List.of(1, 2, 3, 4, 5));          // Integer — ok
sum(List.of(1.5, 2.5, 3.0));          // Double — ok
// sum(List.of("a", "b"));            // compile error — String is not a Number
```

### Wildcards

```java
// ? — unknown type
public static void printList(List<?> list) {
    list.forEach(System.out::println);
}

// ? extends T — upper bounded: T or any subtype
// "I'll read from this — could be Employee or any subclass"
public static double totalSalary(List<? extends Employee> employees) {
    return employees.stream().mapToDouble(Employee::getSalary).sum();
}

// ? super T — lower bounded: T or any supertype
// "I'll write to this"
public static void addDefaults(List<? super Employee> list) {
    list.add(new Employee(0, "Default", 0, "None"));
}
```

---

## `List` — Ordered, Allows Duplicates

### `ArrayList`

Backed by a dynamic array. Fast random access (`get(i)` is O(1)), slower insert/delete in the middle (shifts elements):

```java
import java.util.*;

List<Employee> employees = new ArrayList<>();

// Add
employees.add(new Employee(101, "Sonu",  75000, "Engineering"));
employees.add(new Employee(102, "Monu",  82000, "Engineering"));
employees.add(new Employee(103, "Tonu",  55000, "HR"));
employees.add(0, new Employee(100, "Boss", 200000, "Management"));  // insert at index

// Access
Employee first = employees.get(0);
int size = employees.size();
boolean empty = employees.isEmpty();

// Search
boolean contains = employees.contains(new Employee(101, "Sonu", 75000, "Engineering"));
int idx = employees.indexOf(employees.get(1));

// Update
employees.set(1, new Employee(101, "Sonu", 82000, "Engineering"));

// Remove
employees.remove(0);                          // by index
employees.remove(new Employee(103, "Tonu", 55000, "HR"));  // by object (uses equals)

// Iterate
for (Employee e : employees) { System.out.println(e.getName()); }
employees.forEach(e -> System.out.println(e.getName()));   // Java 8

// Sort
employees.sort(Comparator.comparing(Employee::getSalary));
employees.sort(Comparator.comparing(Employee::getDepartment)
                         .thenComparing(Employee::getName));

// Sublist
List<Employee> top3 = employees.subList(0, 3);   // view — not a copy

// Convert to array
Employee[] arr = employees.toArray(new Employee[0]);
```

### `LinkedList`

Doubly-linked list. Fast insert/delete anywhere (O(1) once position is known), slow random access (O(n)):

```java
LinkedList<Employee> queue = new LinkedList<>();

queue.addFirst(new Employee(101, "Sonu", 75000, "Engineering"));
queue.addLast(new Employee(102, "Monu", 82000, "Engineering"));

Employee first = queue.getFirst();
Employee last  = queue.getLast();
queue.removeFirst();
queue.removeLast();
```

Use `LinkedList` when you frequently insert/remove at both ends. For most other cases, `ArrayList` is faster.

---

## `Set` — No Duplicates

### `HashSet`

Backed by a `HashMap`. No ordering, no duplicates, O(1) add/contains/remove. Requires `equals()` and `hashCode()` to be properly implemented on the element type:

```java
Set<String> departments = new HashSet<>();
departments.add("Engineering");
departments.add("HR");
departments.add("Finance");
departments.add("Engineering");   // duplicate — silently ignored

System.out.println(departments.size());      // 3
System.out.println(departments.contains("HR")); // true

// Iteration order is not guaranteed
departments.forEach(System.out::println);
```

```java
Set<Employee> uniqueEmployees = new HashSet<>();
uniqueEmployees.add(new Employee(101, "Sonu", 75000, "Engineering"));
uniqueEmployees.add(new Employee(101, "Sonu", 75000, "Engineering"));  // duplicate
System.out.println(uniqueEmployees.size());   // 1 — IF equals/hashCode are implemented
```

### `LinkedHashSet`

Same as `HashSet` but maintains **insertion order**:

```java
Set<String> ordered = new LinkedHashSet<>();
ordered.add("Finance");
ordered.add("Engineering");
ordered.add("HR");
ordered.forEach(System.out::println);
// Finance, Engineering, HR — insertion order preserved
```

### `TreeSet`

Sorted set. Elements are kept in natural order (or by a `Comparator`). `String` and number types sort naturally. Custom types need `Comparable` or a `Comparator`:

```java
Set<String> sorted = new TreeSet<>();
sorted.add("Ponu");
sorted.add("Sonu");
sorted.add("Monu");
sorted.add("Gonu");
sorted.add("Tonu");
sorted.forEach(System.out::println);
// Gonu, Monu, Ponu, Sonu, Tonu — alphabetical

// With Comparator for Employee
Set<Employee> bySalary = new TreeSet<>(Comparator.comparing(Employee::getSalary));
bySalary.add(new Employee(101, "Sonu", 75000, "Engineering"));
bySalary.add(new Employee(104, "Ponu", 91000, "Finance"));
bySalary.add(new Employee(103, "Tonu", 55000, "HR"));
bySalary.forEach(e -> System.out.println(e.getName() + " " + e.getSalary()));
// Tonu 55000 → Sonu 75000 → Ponu 91000
```

---

## `Queue` and `Deque`

### `PriorityQueue`

Elements are removed in priority order (natural or custom):

```java
// Employees processed in order of lowest salary first (natural order by salary)
PriorityQueue<Employee> pq = new PriorityQueue<>(
    Comparator.comparing(Employee::getSalary));

pq.offer(new Employee(101, "Sonu",  75000, "Engineering"));
pq.offer(new Employee(103, "Tonu",  55000, "HR"));
pq.offer(new Employee(104, "Ponu",  91000, "Finance"));

while (!pq.isEmpty()) {
    Employee e = pq.poll();   // removes and returns head (lowest salary)
    System.out.printf("Processing: %-10s %.2f%n", e.getName(), e.getSalary());
}
// Processing: Tonu       55000.00
// Processing: Sonu       75000.00
// Processing: Ponu       91000.00
```

### `ArrayDeque` — Double-Ended Queue

Can be used as both a stack (LIFO) and queue (FIFO). Faster than `Stack` and `LinkedList` for these purposes:

```java
Deque<String> deque = new ArrayDeque<>();

// As a Queue (FIFO)
deque.offer("Sonu");    // add to tail
deque.offer("Monu");
deque.offer("Tonu");
System.out.println(deque.poll());   // Sonu — remove from head

// As a Stack (LIFO)
deque.push("Task-A");   // add to head
deque.push("Task-B");
deque.push("Task-C");
System.out.println(deque.pop());    // Task-C — remove from head (LIFO)
```

---

## `Map` — Key-Value Pairs

### `HashMap`

The workhorse. O(1) average for get/put/containsKey. No ordering guarantee. Keys must implement `equals()` and `hashCode()`:

```java
Map<Integer, Employee> empById = new HashMap<>();

// Put
empById.put(101, new Employee(101, "Sonu",  75000, "Engineering"));
empById.put(102, new Employee(102, "Monu",  82000, "Engineering"));
empById.put(103, new Employee(103, "Tonu",  55000, "HR"));

// Get
Employee sonu = empById.get(101);
Employee none = empById.get(999);   // null if not found

// Safe get with default
Employee safe = empById.getOrDefault(999,
    new Employee(0, "Unknown", 0, "None"));

// Check
empById.containsKey(101);   // true
empById.containsValue(sonu); // true (uses equals)

// Remove
empById.remove(103);
empById.remove(101, sonu);   // remove only if key maps to this value

// Size
empById.size();

// Iterate entries
for (Map.Entry<Integer, Employee> entry : empById.entrySet()) {
    System.out.printf("Key: %d → %s%n", entry.getKey(), entry.getValue().getName());
}

// Java 8 iteration
empById.forEach((id, emp) ->
    System.out.printf("%-5d %s%n", id, emp.getName()));

// Compute / merge
empById.computeIfAbsent(104, id ->
    new Employee(id, "Ponu", 91000, "Finance"));

empById.putIfAbsent(105, new Employee(105, "Gonu", 68000, "Operations"));
```

### `LinkedHashMap`

Maintains **insertion order**. Useful when you need predictable iteration:

```java
Map<String, Double> deptBudget = new LinkedHashMap<>();
deptBudget.put("Engineering", 500000.0);
deptBudget.put("Finance",     300000.0);
deptBudget.put("HR",          200000.0);

deptBudget.forEach((dept, budget) ->
    System.out.printf("%-15s %.2f%n", dept, budget));
// Engineering     500000.00
// Finance         300000.00
// HR              200000.00  — insertion order preserved
```

### `TreeMap`

Sorted by key (natural order or `Comparator`). Good for sorted output, range queries:

```java
Map<String, List<Employee>> byDept = new TreeMap<>();   // sorted by dept name

// Populate
for (Employee e : employees) {
    byDept.computeIfAbsent(e.getDepartment(), k -> new ArrayList<>()).add(e);
}

// Keys in alphabetical order
byDept.forEach((dept, emps) -> {
    System.out.println(dept + ": " +
        emps.stream().map(Employee::getName).collect(Collectors.joining(", ")));
});
// Engineering: Sonu, Monu
// Finance: Ponu
// HR: Tonu
// Operations: Gonu
```

---

## `Collections` Utility Class

`java.util.Collections` provides static utility methods:

```java
List<Employee> list = new ArrayList<>(Arrays.asList(
    new Employee(101, "Sonu",  75000, "Engineering"),
    new Employee(102, "Monu",  82000, "Engineering"),
    new Employee(103, "Tonu",  55000, "HR"),
    new Employee(104, "Ponu",  91000, "Finance"),
    new Employee(105, "Gonu",  68000, "Operations")
));

// Sort
Collections.sort(list, Comparator.comparing(Employee::getSalary));

// Reverse
Collections.reverse(list);

// Shuffle
Collections.shuffle(list);

// Min and Max
Employee min = Collections.min(list, Comparator.comparing(Employee::getSalary));
Employee max = Collections.max(list, Comparator.comparing(Employee::getSalary));

// Frequency
List<String> depts = Arrays.asList("Engineering", "HR", "Engineering", "Finance");
int engCount = Collections.frequency(depts, "Engineering");  // 2

// Unmodifiable view — any modification throws UnsupportedOperationException
List<Employee> readOnly = Collections.unmodifiableList(list);
// readOnly.add(...);   // UnsupportedOperationException

// Synchronized wrapper
List<Employee> syncList = Collections.synchronizedList(list);
```

---

## Java 9+ Factory Methods

Concise, immutable collections — the modern way to create small fixed collections:

```java
// Immutable List
List<String> names = List.of("Sonu", "Monu", "Tonu", "Ponu", "Gonu");

// Immutable Set
Set<String> depts = Set.of("Engineering", "HR", "Finance", "Operations");

// Immutable Map
Map<Integer, String> idToName = Map.of(
    101, "Sonu",
    102, "Monu",
    103, "Tonu"
);

// Immutable Map — more than 10 entries
Map<Integer, String> large = Map.ofEntries(
    Map.entry(101, "Sonu"),
    Map.entry(102, "Monu"),
    Map.entry(103, "Tonu"),
    Map.entry(104, "Ponu")
);

// names.add("Ronu");   // UnsupportedOperationException — truly immutable
```

`null` elements/keys/values are not allowed in these factory methods.

---

## Choosing the Right Collection

| Need | Use |
|------|-----|
| Ordered list, fast random access | `ArrayList` |
| Frequent insert/delete at ends | `ArrayDeque` or `LinkedList` |
| No duplicates, fast lookup | `HashSet` |
| No duplicates, insertion order | `LinkedHashSet` |
| No duplicates, sorted | `TreeSet` |
| Key-value, fast lookup | `HashMap` |
| Key-value, insertion order | `LinkedHashMap` |
| Key-value, sorted by key | `TreeMap` |
| Priority-based processing | `PriorityQueue` |
| Stack (LIFO) | `ArrayDeque` |
| Queue (FIFO) | `ArrayDeque` |
| Thread-safe map | `ConcurrentHashMap` |
| Thread-safe producer-consumer | `BlockingQueue` |
| Small, fixed, immutable | `List.of()`, `Set.of()`, `Map.of()` |

---

## Practical Example — Employee Management with Collections

```java
import java.util.*;
import java.util.stream.*;

public class EmployeeRegistry {

    private final Map<Integer, Employee>          byId         = new LinkedHashMap<>();
    private final Map<String, List<Employee>>     byDepartment = new TreeMap<>();
    private final TreeSet<Employee>               bySalary     = new TreeSet<>(
            Comparator.comparing(Employee::getSalary).reversed()
                       .thenComparing(Employee::getId));

    public void add(Employee e) {
        byId.put(e.getId(), e);
        byDepartment.computeIfAbsent(e.getDepartment(), k -> new ArrayList<>()).add(e);
        bySalary.add(e);
    }

    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<Employee> findByDepartment(String dept) {
        return byDepartment.getOrDefault(dept, Collections.emptyList());
    }

    public List<Employee> topEarners(int n) {
        return bySalary.stream().limit(n).collect(Collectors.toList());
    }

    public Map<String, DoubleSummaryStatistics> salaryStatsByDept() {
        return byId.values().stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.summarizingDouble(Employee::getSalary)));
    }

    public static void main(String[] args) {
        EmployeeRegistry registry = new EmployeeRegistry();

        registry.add(new Employee(101, "Sonu",  75000, "Engineering"));
        registry.add(new Employee(102, "Monu",  82000, "Engineering"));
        registry.add(new Employee(103, "Tonu",  55000, "HR"));
        registry.add(new Employee(104, "Ponu",  91000, "Finance"));
        registry.add(new Employee(105, "Gonu",  68000, "Operations"));
        registry.add(new Employee(106, "Ronu",  78000, "Engineering"));
        registry.add(new Employee(107, "Bonu",  61000, "HR"));

        // Find by ID
        registry.findById(103)
                .ifPresent(e -> System.out.println("Found: " + e));

        // Top 3 earners
        System.out.println("\nTop 3 Earners:");
        registry.topEarners(3).forEach(e ->
            System.out.printf("  %-10s %.2f%n", e.getName(), e.getSalary()));

        // Department breakdown
        System.out.println("\nEngineering team:");
        registry.findByDepartment("Engineering").forEach(e ->
            System.out.printf("  %-10s %.2f%n", e.getName(), e.getSalary()));

        // Stats per department
        System.out.println("\nSalary stats by department:");
        registry.salaryStatsByDept().forEach((dept, stats) ->
            System.out.printf("  %-15s Count: %d  Avg: %,.0f  Max: %,.0f%n",
                              dept, stats.getCount(),
                              stats.getAverage(), stats.getMax()));
    }
}
```

Output:
```
Found: Employee{id=103, name='Tonu', dept='HR', salary=55000.00}

Top 3 Earners:
  Ponu       91000.00
  Monu       82000.00
  Ronu       78000.00

Engineering team:
  Sonu       75000.00
  Monu       82000.00
  Ronu       78000.00

Salary stats by department:
  Engineering     Count: 3  Avg: 78,333  Max: 82,000
  Finance         Count: 1  Avg: 91,000  Max: 91,000
  HR              Count: 2  Avg: 58,000  Max: 61,000
  Operations      Count: 1  Avg: 68,000  Max: 68,000
```

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Generics `<T>` | Type safety — errors caught at compile time, not runtime |
| `ArrayList` | Default List — fast random access, slow mid-insert |
| `LinkedList` | Fast insert/remove at ends — rarely needed over ArrayList |
| `HashSet` | Default Set — fast, no order |
| `LinkedHashSet` | Set with insertion order |
| `TreeSet` | Sorted set — needs Comparable or Comparator |
| `HashMap` | Default Map — fast key lookup |
| `LinkedHashMap` | Map with insertion order |
| `TreeMap` | Sorted by key |
| `ArrayDeque` | Stack and Queue — faster than Stack/LinkedList |
| `PriorityQueue` | Process elements in priority order |
| `Collections` | Utility: sort, reverse, shuffle, unmodifiable, synchronized |
| `List.of()` / `Set.of()` / `Map.of()` | Immutable, concise, null-safe (Java 9+) |

---

## What's Next

**Part H — Memory.** Module 27 covers Garbage Collection and the full object lifecycle — how objects are born, live, and die in the JVM.
