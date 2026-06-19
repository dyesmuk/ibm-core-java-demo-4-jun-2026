# Module 02 — JVM Architecture

> **Part A: Getting Started**
> Prerequisites: Module 01 · Time: ~45 minutes

---

## Why Understand the JVM?

You can write Java without knowing how the JVM works. But you will hit moments where understanding it matters a lot:

- Why does a `static` variable behave differently from an instance variable?
- Where do objects actually live in memory?
- What is a `StackOverflowError` and why does recursion cause it?
- Why does `System.gc()` not always collect garbage immediately?
- What is a memory leak in Java and how can it happen if GC exists?

This module answers all of these. It is a map of what happens after you run `java Employee`.

---

## The Big Picture

When you run `java Employee`, the JVM goes through three phases:

```
Phase 1 — Loading      Read Employee.class from disk → load into memory
Phase 2 — Execution    Run the bytecode instructions
Phase 3 — Cleanup      Garbage collect objects no longer in use
```

The JVM has several distinct **memory areas** that serve different purposes during execution:

```
JVM Memory
├── Method Area       class definitions, static variables, constants
├── Heap              all objects created with 'new'
├── Stack             one per thread — method calls and local variables
├── PC Register       one per thread — tracks current instruction
└── Native Stack      for native (C/C++) method calls
```

The ones you deal with daily as a Java developer are the **Heap** and the **Stack**.

---

## Phase 1 — Class Loading

Before any code runs, the JVM must load the class files it needs.

### The Class Loader

The **Class Loader** reads `.class` files from disk and brings them into the Method Area. It has three built-in layers:

| Loader | Loads |
|--------|-------|
| Bootstrap ClassLoader | Core Java classes (`java.lang.String`, `java.util.ArrayList`, etc.) |
| Extension ClassLoader | Optional extensions in `jre/lib/ext` |
| Application ClassLoader | Your own classes and third-party libraries on the classpath |

The search goes bottom-up: if your code asks for class `Employee`, the Application ClassLoader handles it. If it asks for `java.lang.String`, the Bootstrap ClassLoader handles it.

### What Happens During Loading

For each class loaded, the JVM:
1. Reads the `.class` file
2. Verifies the bytecode is valid and safe
3. Allocates memory in the **Method Area** for:
   - The class structure (fields, methods)
   - Static variables (initialized to defaults)
   - The constant pool (string literals, numeric constants)
4. Executes any `static` blocks

Classes are loaded **lazily** — when first referenced, not at startup. The JVM doesn't load your entire codebase upfront.

---

## Memory Areas in Detail

### Method Area (Class Area)

Shared across all threads. Stores:
- Class bytecode and metadata
- Method definitions
- **Static variables** — one copy per class, not per object
- String constants from the String pool

```java
class Employee {
    static String company = "TechCorp";   // lives in Method Area
    String name;                           // lives in Heap (per object)
}
```

### Heap

The largest memory area. All objects created with `new` live here.

```java
Employee e1 = new Employee("Sonu");   // Employee object → Heap
Employee e2 = new Employee("Monu");   // another Employee object → Heap
```

The heap is divided into regions that the Garbage Collector manages:

```
Heap
├── Young Generation
│   ├── Eden Space        new objects land here first
│   ├── Survivor S0       objects that survived one GC
│   └── Survivor S1       objects that survived another GC
└── Old Generation        long-lived objects promoted from Young Gen
```

New objects are created in **Eden**. Most objects die young (created, used briefly, discarded). Objects that survive enough GC cycles get **promoted** to the Old Generation.

### Stack

Every thread has its own Stack. The Stack stores **method call frames** — when a method is called, a frame is pushed. When the method returns, the frame is popped.

Each frame contains:
- Local variables of that method
- Method parameters
- Intermediate computation values

```java
void calculateBonus(double salary) {      // frame for calculateBonus pushed
    double rate = 0.10;                   // rate lives in this frame
    double bonus = salary * rate;         // bonus lives in this frame
    System.out.println(bonus);
}                                         // frame popped, rate and bonus gone
```

When you call method A which calls method B which calls method C:

```
Stack (top to bottom)
┌─────────────────┐
│  Frame: C       │  ← currently executing
├─────────────────┤
│  Frame: B       │
├─────────────────┤
│  Frame: A       │
├─────────────────┤
│  Frame: main    │
└─────────────────┘
```

**StackOverflowError** happens when the stack runs out of space — typically from infinite recursion (a method calls itself endlessly and frames pile up with no base case to stop them).

### PC Register (Program Counter)

One per thread. Holds the address of the **currently executing bytecode instruction**. When a thread is paused (e.g., context switch), the PC saves where it left off so it can resume from the exact right place.

### Native Method Stack

Used when Java calls native code written in C or C++ via JNI (Java Native Interface). You don't interact with this directly.

---

## Phase 2 — Execution Engine

The Execution Engine reads bytecode and runs it. It has two modes:

### Interpreter

Reads and executes bytecode **one instruction at a time**. Simple and starts fast, but slow for code that runs repeatedly because it translates the same instructions over and over.

### JIT Compiler (Just-In-Time)

The JVM monitors which code runs frequently — called **hot spots**. The JIT compiler takes those hot methods and compiles them directly to **native machine code** for the current platform. After that, those methods run at near-native speed.

```
First few runs:     Interpreter executes bytecode → somewhat slow
JIT kicks in:       Hot methods compiled to native code → fast
Subsequent runs:    Native code executes directly → very fast
```

This is why Java applications often feel slow at startup (cold JVM) but speed up significantly once the JIT has compiled the hot paths — this warm-up period is called **JIT warm-up**.

Modern JIT compilers (like GraalVM) are extremely sophisticated and can produce code as fast as C++ for computationally intensive work.

---

## What Happens When You Run This Code

```java
public class EmployeeDemo {

    static int headCount = 0;             // static variable

    String name;
    double salary;

    EmployeeDemo(String name, double salary) {
        this.name = name;
        this.salary = salary;
        headCount++;
    }

    public static void main(String[] args) {
        EmployeeDemo e1 = new EmployeeDemo("Sonu", 75000);
        EmployeeDemo e2 = new EmployeeDemo("Monu", 82000);
        System.out.println("Total employees: " + headCount);
    }
}
```

Step by step:

1. JVM starts. Class Loader reads `EmployeeDemo.class` into the **Method Area**.
2. `headCount` is created in the **Method Area** (static → belongs to the class, not any object), initialized to `0`.
3. `main` frame is pushed onto the **Stack**.
4. `new EmployeeDemo("Sonu", 75000)` — a new `EmployeeDemo` object is created in the **Heap**. Its fields `name` and `salary` live inside that heap object.
5. `e1` is a reference variable. It lives on the **Stack** (inside `main`'s frame) and holds the **address** of the heap object.
6. Same for `e2` — another heap object, another stack reference.
7. `headCount++` runs twice — the single value in the **Method Area** goes from 0 → 1 → 2.
8. `System.out.println(...)` prints `Total employees: 2`.
9. `main` returns — its frame is popped from the Stack.
10. The two `EmployeeDemo` objects on the heap are now unreachable — eligible for garbage collection.

Memory picture at step 7:

```
Method Area          Heap                    Stack (main frame)
────────────         ──────────────────      ──────────────────────
EmployeeDemo class   Object @1001            args    → []
  headCount = 2        name   = "Sonu"       e1      → @1001
  main method          salary = 75000.0      e2      → @1002
  constructor        Object @1002
                       name   = "Monu"
                       salary = 82000.0
```

---

## Phase 3 — Garbage Collection

Java manages memory automatically. You never call `free()` or `delete`. The **Garbage Collector (GC)** finds and reclaims memory occupied by objects that are no longer reachable.

### When Is an Object Eligible for GC?

An object becomes eligible when **no live reference points to it**:

```java
Employee e = new Employee("Sonu");    // object created on heap
e = null;                             // reference cleared → object eligible for GC

Employee a = new Employee("Monu");
Employee b = new Employee("Tonu");
a = b;                                // first object (Monu) now unreachable → eligible
```

### What the GC Does

The GC runs in the background (in its own threads) and periodically:
1. Marks all reachable objects (starting from GC roots — stack references, static variables)
2. Sweeps unreachable objects and reclaims their memory
3. Compacts the heap to reduce fragmentation (in some collectors)

### GC Algorithms

| Collector | Default In | Best For |
|-----------|-----------|----------|
| Serial GC | — | Single-threaded, small apps |
| Parallel GC | Java 8 (default) | Throughput-focused, batch processing |
| G1 (Garbage First) | Java 9+ (default) | Balanced latency and throughput |
| ZGC | Java 15+ | Very low pause times (sub-millisecond) |

> For most developers, you don't choose a GC manually. The defaults are well-tuned. GC tuning is an advanced topic when you're optimizing production systems.

### Can You Force GC?

You can *request* it — but the JVM is not obligated to comply immediately:

```java
System.gc();                    // request GC — not guaranteed to run right away
Runtime.getRuntime().gc();     // same thing
```

> **Do not** use `System.gc()` in application code. It is a hint, not a command. In production, calling it can actually hurt performance by triggering a GC cycle at the wrong time.

---

## Stack vs Heap — The Essential Difference

| Aspect | Stack | Heap |
|--------|-------|------|
| What lives there | Local variables, method frames, references | Objects (created with `new`) |
| Lifetime | Tied to method call — popped when method returns | Until garbage collected |
| Thread | Each thread has its own stack | Shared across all threads |
| Size | Small (usually 512KB–1MB per thread) | Large (configured with `-Xmx`) |
| Speed | Very fast (LIFO, CPU-cache friendly) | Slower (allocated dynamically) |
| Managed by | Automatic (push/pop) | Garbage collector |
| Error when full | `StackOverflowError` | `OutOfMemoryError` |

---

## Common JVM Errors Explained

| Error | Cause | Fix |
|-------|-------|-----|
| `StackOverflowError` | Stack is full — usually infinite recursion | Check recursive method for missing base case |
| `OutOfMemoryError: Java heap space` | Heap is full — too many objects | Increase heap with `-Xmx`, or fix memory leak |
| `OutOfMemoryError: Metaspace` | Method area is full — too many classes loaded | Common with dynamic class generation frameworks |
| `ClassNotFoundException` | Class loader cannot find the `.class` file | Check classpath |
| `NoClassDefFoundError` | Class was available at compile time but not at runtime | Missing JAR or dependency |

---

## JVM Startup Flags You Will See

```bash
java -Xms256m -Xmx1g Employee
#    │          └── maximum heap size: 1 gigabyte
#    └── initial heap size: 256 megabytes

java -verbose:gc Employee
#    └── print GC activity to console (useful for diagnosing memory issues)
```

---

## Quick Summary

| Component | Role |
|-----------|------|
| Class Loader | Reads `.class` files into memory |
| Method Area | Stores class definitions, static variables |
| Heap | Stores all objects — managed by GC |
| Stack | Stores method frames and local variables |
| JIT Compiler | Compiles hot bytecode to native machine code |
| GC | Automatically reclaims unreachable objects |

---

## What's Next

**Module 03** — Datatypes, Variables and Operators. Now that you know where things live in memory, the next module covers the fundamental building blocks you put there.
