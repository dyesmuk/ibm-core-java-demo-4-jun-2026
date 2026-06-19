# Module 27 — Garbage Collection and Object Lifecycle

> **Part H: Memory**
> Prerequisites: Module 02 (JVM Architecture) · Time: ~1 hour

---

## The Object Lifecycle

Every Java object goes through four stages:

```
1. Created   — new Employee(...)  → allocated on heap, constructor runs
2. In Use     — reachable via at least one live reference
3. Eligible   — no live references remain → GC may collect it
4. Collected  — memory reclaimed, object gone
```

The Garbage Collector handles stages 3 and 4 automatically. Your job is to make sure objects become eligible when you are done with them — mostly by not holding references longer than needed.

---

## GC Roots — Where Reachability Starts

The GC starts from **GC roots** — objects that are always considered live — and traces all references reachable from them. Anything reachable is kept. Anything not reachable is eligible for collection.

GC roots include:
- Local variables and parameters in active stack frames
- Static fields
- JNI references (native code)
- Active threads themselves

```java
public void processPayroll() {
    Employee e = new Employee(101, "Sonu", 75000, "Engineering");  // GC root: local var e
    calculate(e);
    // method returns — 'e' goes out of scope
    // Employee object is no longer reachable — eligible for GC
}
```

---

## When Is an Object Eligible?

### Nulling a reference

```java
Employee e = new Employee(101, "Sonu", 75000, "Engineering");
e = null;   // reference removed → object eligible (assuming no other references)
```

### Going out of scope

```java
{
    Employee temp = new Employee(102, "Monu", 82000, "HR");
    // use temp...
}   // temp goes out of scope → object eligible
```

### Reassigning a reference

```java
Employee e = new Employee(101, "Sonu", 75000, "Engineering");
e = new Employee(102, "Monu", 82000, "HR");
// first Employee object now has no reference → eligible
```

### Island of isolation — mutual references, no external reference

```java
class Node {
    Node next;
}

Node a = new Node();
Node b = new Node();
a.next = b;
b.next = a;   // circular reference

a = null;
b = null;
// Both nodes reference each other — but neither is reachable from any GC root
// Both are eligible — the GC handles cycles correctly
```

Java's GC traces reachability from roots — circular references don't fool it. Only C/C++ reference-counting GCs have problems with cycles.

---

## Heap Regions and the GC Cycle

Recall from Module 02 — the heap is divided into regions:

```
Heap
├── Young Generation
│   ├── Eden           ← new objects land here
│   ├── Survivor S0    ← survived one GC cycle
│   └── Survivor S1    ← survived another GC cycle
└── Old Generation     ← long-lived objects
```

### Minor GC (Young Generation)

Happens frequently. Collects Eden and survivor spaces:

1. Objects created in **Eden**
2. When Eden fills up → Minor GC runs
3. Live Eden objects copied to **S0** (or S1)
4. Objects that have survived several cycles promoted to **Old Generation**
5. Dead objects (most) simply discarded — Eden wiped clean

Most objects die young — created for a method call, used briefly, then abandoned. Minor GC is fast and reclaims most memory.

### Major / Full GC (Old Generation)

Less frequent but slower. Collects the Old Generation. Happens when it fills up or is explicitly triggered. This is the GC pause you notice in production — it can stop all application threads briefly (Stop-The-World).

---

## Reference Types

Java provides four reference strengths that interact with GC differently:

### Strong Reference (default)

A normal variable reference. The object is **never** GC'd while a strong reference exists:

```java
Employee e = new Employee(101, "Sonu", 75000, "Engineering");
// e is a strong reference — object will not be GC'd while e is in scope
```

### Soft Reference

GC'd **only when the JVM is low on memory**. Good for caches — memory-sensitive:

```java
import java.lang.ref.*;

SoftReference<Employee> softRef = new SoftReference<>(
    new Employee(101, "Sonu", 75000, "Engineering"));

Employee e = softRef.get();   // returns the Employee, or null if GC'd
if (e != null) {
    e.display();
} else {
    System.out.println("Employee was collected due to memory pressure.");
}
```

### Weak Reference

GC'd at the **next GC cycle** once no strong references exist — regardless of memory:

```java
WeakReference<Employee> weakRef = new WeakReference<>(
    new Employee(102, "Monu", 82000, "Engineering"));

System.gc();   // hint to run GC

Employee e = weakRef.get();
if (e == null) {
    System.out.println("Employee already GC'd.");
}
```

`WeakHashMap` uses weak keys — entries are automatically removed when the key is no longer strongly referenced. Useful for caches keyed on objects you don't own.

### Phantom Reference

The weakest — `get()` always returns `null`. Used for post-GC cleanup actions, with a `ReferenceQueue`:

```java
ReferenceQueue<Employee> queue = new ReferenceQueue<>();
PhantomReference<Employee> phantomRef = new PhantomReference<>(
    new Employee(103, "Tonu", 55000, "HR"), queue);

// After GC, reference is enqueued — you can do cleanup
// phantomRef.get() always returns null
```

Phantom references replaced `finalize()` as the correct way to do post-collection cleanup.

### Reference Strength Summary

| Type | Collected When | Use Case |
|------|---------------|----------|
| Strong | Never (while referenced) | Everything normal |
| Soft | Low memory | Memory-sensitive caches |
| Weak | Next GC cycle | Short-lived caches, `WeakHashMap` |
| Phantom | After finalization | Post-GC cleanup, resource tracking |

---

## GC Algorithms

Different GC implementations trade off between throughput and latency (pause time):

### Serial GC

Single-threaded collection. Stops all application threads (Stop-The-World) during GC. Only for single-core, small applications:

```bash
java -XX:+UseSerialGC MyApp
```

### Parallel GC (Throughput Collector)

Default in Java 8. Uses multiple threads for GC — still Stop-The-World but faster. Best for batch processing where total throughput matters more than pause times:

```bash
java -XX:+UseParallelGC MyApp
```

### G1 (Garbage First)

Default since Java 9. Divides heap into equal-sized **regions** instead of fixed Young/Old areas. Collects regions with the most garbage first (hence "Garbage First"). Aims for predictable pause times:

```bash
java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 MyApp
```

Good balance of throughput and latency. The right choice for most production applications.

### ZGC (Z Garbage Collector)

Java 15+ (production-ready). Concurrent — most work happens while the application runs. Pause times under 1ms regardless of heap size. Best for latency-sensitive applications:

```bash
java -XX:+UseZGC MyApp
```

### Shenandoah

Similar to ZGC — ultra-low pause times. Available in OpenJDK builds.

---

## GC Tuning — Common JVM Flags

You will see these in production configurations:

```bash
# Heap size
-Xms512m          # initial heap size: 512 MB
-Xmx4g            # maximum heap size: 4 GB

# GC selection
-XX:+UseG1GC
-XX:+UseZGC

# G1 target pause time
-XX:MaxGCPauseMillis=200

# Print GC logs
-Xlog:gc*          # Java 9+ unified logging
-verbose:gc        # older style

# Metaspace (class metadata — replaced PermGen in Java 8)
-XX:MaxMetaspaceSize=256m
```

> As a developer, you rarely tune GC directly. Understand what these flags mean so you can read production configurations and diagnose memory issues.

---

## Memory Leaks in Java

"But Java has GC — there are no memory leaks!" — Wrong. Memory leaks happen in Java when objects are reachable but never used again. GC cannot collect reachable objects:

### Common Causes

**1. Static collections that grow forever**

```java
public class EmployeeCache {
    private static final Map<Integer, Employee> cache = new HashMap<>();

    public static void cache(Employee e) {
        cache.put(e.getId(), e);   // employees added but never removed
    }
}
// cache grows forever — static field is a GC root, employees are always reachable
```

**2. Listeners not removed**

```java
eventBus.register(employeeListener);   // adds listener
// ... employee is "done" ...
// eventBus.unregister(employeeListener);  // forgot this line
// listener (and everything it references) is held alive by the event bus
```

**3. Long-lived objects holding short-lived ones**

```java
public class ReportGenerator {
    private Employee lastEmployee;   // instance field holds a reference

    public void process(Employee e) {
        lastEmployee = e;   // keeps Employee alive as long as ReportGenerator is alive
        // if ReportGenerator is long-lived, Employee is too — even after processing
    }
}
```

**Detecting leaks:** Use heap profilers — VisualVM, YourKit, or Java Mission Control. They show heap histograms, object counts, and retention trees.

---

## `System.gc()` — Don't Use It

```java
System.gc();             // suggests GC — JVM may ignore it
Runtime.getRuntime().gc(); // same
```

This is a **hint** — the JVM is free to ignore it. In production:
- It can trigger a Full GC at the wrong time, causing a long pause
- It gives false confidence that memory is freed
- It interferes with the GC's own heuristics

Never call `System.gc()` in application code. The only exception: testing and benchmarking where you want a clean heap state.

---

## `finalize()` — Dead and Gone

`finalize()` was deprecated in Java 9 and removed in Java 18. Do not use it:

```java
// Do NOT do this
@Override
protected void finalize() throws Throwable {
    closeResource();   // may never run, may run too late, may cause resurrection
    super.finalize();
}
```

The modern replacement is `AutoCloseable` + `try-with-resources` for deterministic cleanup:

```java
public class DatabaseConnection implements AutoCloseable {
    public DatabaseConnection() { /* open connection */ }
    public void query(String sql) { /* execute */ }

    @Override
    public void close() {
        System.out.println("Connection closed.");
    }
}

try (DatabaseConnection conn = new DatabaseConnection()) {
    conn.query("SELECT * FROM employees");
}   // close() called here — guaranteed, immediate, deterministic
```

---

## Practical Advice

| Situation | What To Do |
|-----------|-----------|
| Long-lived cache | Use `SoftReference` or a bounded cache with eviction |
| Event listeners | Always unregister when done |
| Large object no longer needed | Null the reference explicitly if in a long-lived scope |
| Connection, stream, resource | Always use `try-with-resources` |
| Suspecting a memory leak | Profile with VisualVM — check heap histogram over time |
| Slow GC pauses | Switch from Parallel to G1 or ZGC |
| Seeing `OutOfMemoryError: Java heap space` | Increase heap with `-Xmx`, then profile for leaks |
| Seeing `OutOfMemoryError: Metaspace` | Too many classes — common with dynamic class generation |

---

## Quick Summary

| Concept | Key Point |
|---------|-----------|
| Eligible for GC | No live references remain — either null, out of scope, or only unreachable cycles |
| Strong reference | Never GC'd while held |
| Soft reference | GC'd under memory pressure — use for caches |
| Weak reference | GC'd next cycle — use for short-lived caches |
| Phantom reference | Post-finalization cleanup |
| Minor GC | Young generation — fast, frequent |
| Major / Full GC | Old generation — slower, less frequent |
| G1 GC | Default Java 9+ — good general-purpose choice |
| ZGC | Sub-millisecond pauses — latency-sensitive apps |
| Memory leak | Reachable but never used — GC can't help |
| `finalize()` | Deprecated and removed — use `AutoCloseable` |
| `System.gc()` | Don't call in production code |

---

## What's Next

**Part I — Modern Java.** Modules 28–32 cover new language features from Java 11 through Java 24 — each as a standalone reference you can read independently.
