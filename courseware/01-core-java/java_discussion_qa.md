# Core Java — Discussion Questions & Answers

---

## Fundas

**1. What is Java?**

Java is a statically typed, object-oriented, platform-independent programming language. It compiles to bytecode that runs on the JVM (Java Virtual Machine), making it "Write Once, Run Anywhere." It is maintained by Oracle and widely used for enterprise backend systems.

**2. What is the JVM, JRE, and JDK?**

JDK (Java Development Kit) includes everything needed to develop Java programs — compiler (`javac`), JRE, and tools. JRE (Java Runtime Environment) includes the JVM and standard libraries needed to run Java programs. JVM (Java Virtual Machine) is the engine that executes Java bytecode — it is platform-specific but bytecode is platform-neutral.

**3. What is bytecode?**

Bytecode is the intermediate, platform-neutral code produced by the Java compiler (`javac`). It is not machine code — the JVM interprets or JIT-compiles it to native machine code at runtime. This is why the same `.class` file runs on Windows, Linux, and Mac without recompilation.

**4. What is the difference between `==` and `.equals()` in Java?**

`==` compares object references (memory addresses) for objects, and values for primitives. `.equals()` compares the actual content/value of objects. `"hello" == "hello"` may be `true` (string pool), but `new String("hello") == new String("hello")` is `false`. Always use `.equals()` for object comparison.

**5. What is the difference between primitive types and wrapper classes?**

Primitives (`int`, `double`, `boolean`, `char`, etc.) hold values directly in the stack — fast and memory-efficient. Wrapper classes (`Integer`, `Double`, `Boolean`, `Character`) are objects that box the primitive value — needed for collections (`List<Integer>`), generics, and when `null` is a valid value.

**6. What is autoboxing and unboxing?**

Autoboxing is the automatic conversion of a primitive to its wrapper class by the compiler: `Integer i = 5` (int → Integer). Unboxing is the reverse: `int x = i` (Integer → int). It makes code cleaner but can cause `NullPointerException` if the wrapper is `null` when unboxed.

**7. What is the difference between `String`, `StringBuilder`, and `StringBuffer`?**

`String` is immutable — every modification creates a new object. `StringBuilder` is mutable and not thread-safe — use it for string building in single-threaded code (fast). `StringBuffer` is mutable and thread-safe (synchronised) — use in multi-threaded code. For concatenation in loops, always prefer `StringBuilder`.

**8. What is the difference between `final`, `finally`, and `finalize()`?**

`final` is a keyword: applied to a variable (constant), method (cannot override), or class (cannot extend). `finally` is a block that runs after `try/catch` regardless of the outcome — used for cleanup. `finalize()` is a deprecated method called by the GC before an object is collected — avoid using it.

**9. What is a static keyword in Java?**

`static` means the member belongs to the class, not to any instance. A static field is shared across all instances. A static method can be called without creating an object (`Math.abs()`). Static members are loaded when the class is loaded, not when an object is created.

**10. What is the `main` method signature in Java?**

`public static void main(String[] args)` — each keyword matters: `public` so the JVM can call it, `static` so no object is needed, `void` because it returns nothing, `String[] args` for command-line arguments. The JVM looks for exactly this signature as the entry point.

---

## OOP Concepts

**11. What are the four pillars of OOP?**

Encapsulation (bundling data and methods, hiding internal details), Inheritance (reusing and extending parent class behaviour), Polymorphism (same interface, different implementations), Abstraction (exposing only essential features, hiding complexity). Java implements all four.

**12. What is encapsulation?**

Encapsulation means hiding the internal state of an object and requiring all access through public methods. Declare fields `private` and provide `public` getters/setters. This allows you to change the internal implementation without affecting code that uses the class.

**13. What is inheritance in Java?**

Inheritance allows a child class to acquire the properties and methods of a parent class using `extends`. Java supports single inheritance for classes (one parent only) but multiple inheritance through interfaces. It promotes code reuse but creates tight coupling — favour composition over inheritance when in doubt.

**14. What is the difference between method overloading and overriding?**

Overloading is defining multiple methods with the same name but different parameter lists in the same class — resolved at compile time (static polymorphism). Overriding is redefining a parent class method in a child class with the same signature — resolved at runtime (dynamic polymorphism). Use `@Override` annotation to catch mistakes.

**15. What is polymorphism?**

Polymorphism means "many forms" — the same method call behaves differently depending on the object type at runtime. `Animal a = new Dog(); a.speak()` calls `Dog.speak()` even though the reference type is `Animal`. This is runtime (dynamic) polymorphism via method overriding.

**16. What is abstraction in Java?**

Abstraction means exposing only the essential behaviour and hiding implementation details. Achieved through abstract classes (partial implementation, cannot be instantiated) and interfaces (pure contract, all methods abstract by default pre-Java 8). It reduces complexity and increases maintainability.

**17. What is the difference between abstract class and interface?**

An abstract class can have constructors, instance fields, concrete methods, and abstract methods — use for partial implementation with shared state. An interface (pre-Java 8) can only have abstract methods and constants; Java 8+ added `default` and `static` methods. A class can implement multiple interfaces but extend only one class.

**18. What is the difference between `implements` and `extends`?**

`extends` is used for class-to-class or interface-to-interface inheritance. `implements` is used when a class implements an interface. A class can `extends` only one class but can `implements` multiple interfaces. Interfaces can `extends` multiple other interfaces.

**19. What is a constructor?**

A constructor is a special method with the same name as the class and no return type. It initialises a new object when `new` is called. If you don't define one, Java provides a no-arg default constructor. Constructors can be overloaded. Use `this()` to call another constructor in the same class; use `super()` to call the parent constructor.

**20. What is the difference between `this` and `super`?**

`this` refers to the current object instance — used to differentiate instance variables from parameters and to call other constructors in the same class. `super` refers to the parent class — used to call parent constructors (`super()`) or parent methods (`super.method()`). Both must be the first statement in a constructor.

---

## Collections Framework

**21. What is the Java Collections Framework?**

The Collections Framework is a unified architecture for storing and manipulating groups of objects. It includes interfaces (`List`, `Set`, `Map`, `Queue`), implementations (`ArrayList`, `HashMap`, `TreeSet`), and algorithms (`Collections.sort()`). All collections work with generics for type safety.

**22. What is the difference between `ArrayList` and `LinkedList`?**

`ArrayList` is backed by a dynamic array — fast random access `O(1)`, slow insertion/deletion in the middle `O(n)`. `LinkedList` is a doubly-linked list — slow random access `O(n)`, fast insertion/deletion at ends `O(1)`. Prefer `ArrayList` for most use cases; `LinkedList` for queue/deque operations.

**23. What is the difference between `HashMap`, `LinkedHashMap`, and `TreeMap`?**

`HashMap` stores key-value pairs with no order guarantee — fastest, O(1) average. `LinkedHashMap` maintains insertion order. `TreeMap` keeps keys in natural sorted order — O(log n) for operations. Use `HashMap` by default; `TreeMap` when sorted keys are needed.

**24. What is the difference between `HashSet`, `LinkedHashSet`, and `TreeSet`?**

`HashSet` stores unique elements in no particular order — fastest. `LinkedHashSet` maintains insertion order. `TreeSet` keeps elements sorted. All are backed by their `Map` counterparts. Use `HashSet` for uniqueness checks; `TreeSet` for sorted unique collections.

**25. What is the difference between `List`, `Set`, and `Map`?**

`List` is an ordered collection that allows duplicates — access by index. `Set` is an unordered collection that does not allow duplicates. `Map` stores key-value pairs — keys are unique, values can repeat. These are the three fundamental collection types.

**26. What is the difference between `Iterator` and `ListIterator`?**

`Iterator` can traverse any collection forward-only and supports `remove()`. `ListIterator` works only on `List` and supports bidirectional traversal, element replacement, and element addition. Both should be used instead of index-based loops when you need to remove elements during iteration (to avoid `ConcurrentModificationException`).

**27. What is `ConcurrentModificationException` and how to avoid it?**

This exception is thrown when a collection is modified while being iterated with a for-each or `Iterator`. Avoid it by: using `Iterator.remove()` instead of `list.remove()`, using `CopyOnWriteArrayList` for concurrent access, or collecting indices to remove after the loop.

**28. What is the difference between `Comparable` and `Comparator`?**

`Comparable` defines the natural ordering of a class — implement `compareTo()` inside the class itself. `Comparator` defines an external ordering — implement `compare()` in a separate class or lambda. Use `Comparable` for default ordering; `Comparator` for multiple or custom sort strategies: `list.sort(Comparator.comparing(Employee::getSalary))`.

---

## Exception Handling

**29. What is the difference between checked and unchecked exceptions?**

Checked exceptions (extend `Exception`) must be declared with `throws` or caught — the compiler enforces this. Examples: `IOException`, `SQLException`. Unchecked exceptions (extend `RuntimeException`) do not need to be declared — they indicate programming bugs. Examples: `NullPointerException`, `ArrayIndexOutOfBoundsException`.

**30. What is the exception hierarchy in Java?**

`Throwable` is the root. It has two children: `Error` (JVM-level problems like `OutOfMemoryError` — don't catch) and `Exception` (application-level problems — handle these). `Exception` has `RuntimeException` as a subclass for unchecked exceptions. All others under `Exception` are checked.

**31. What is the difference between `throw` and `throws`?**

`throw` is used inside a method body to explicitly throw an exception: `throw new IllegalArgumentException("Invalid id")`. `throws` is used in the method signature to declare that the method may throw a checked exception: `public void read() throws IOException`. `throw` is an action; `throws` is a declaration.

**32. What is a multi-catch block (Java 7+)?**

A single `catch` block can handle multiple exception types using `|`: `catch (IOException | SQLException e) { ... }`. The variable `e` is implicitly `final`. This reduces code duplication when the same handling logic applies to multiple exception types.

**33. What is try-with-resources (Java 7+)?**

Try-with-resources automatically closes resources that implement `AutoCloseable` after the try block completes, even if an exception occurs. `try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) { ... }` — no `finally` block needed for closing. This prevents resource leaks.

---

## Java 8+ Features

**34. What are lambda expressions?**

A lambda expression is a concise way to represent an anonymous function (an implementation of a functional interface). `(parameters) -> expression` or `(parameters) -> { statements }`. Example: `list.sort((a, b) -> a.getName().compareTo(b.getName()))`. They enable functional programming style in Java.

**35. What is a functional interface?**

A functional interface has exactly one abstract method — it can be implemented with a lambda. Annotate with `@FunctionalInterface` (optional but recommended). Built-in examples: `Runnable`, `Callable`, `Comparator`, `Predicate<T>`, `Function<T,R>`, `Consumer<T>`, `Supplier<T>`.

**36. What is the Stream API?**

Streams provide a functional, declarative way to process collections of data. Operations: intermediate (lazy — `filter`, `map`, `sorted`, `distinct`) and terminal (eager — `collect`, `forEach`, `reduce`, `count`). Streams don't modify the source collection and can be parallelised with `.parallelStream()`.

**37. What is the difference between `map()` and `flatMap()` in Streams?**

`map()` transforms each element — one input produces one output. `flatMap()` transforms each element to a stream and then flattens all streams into one — one input produces zero or more outputs. Use `flatMap()` when each element maps to a collection: `employees.stream().flatMap(e -> e.getSkills().stream())`.

**38. What is `Optional` in Java 8?**

`Optional<T>` is a container object that may or may not contain a non-null value. It explicitly represents the possibility of absence, forcing callers to handle the null case. Methods: `Optional.of(val)`, `Optional.ofNullable(val)`, `.isPresent()`, `.get()`, `.orElse(default)`, `.orElseThrow()`, `.map()`. Prefer it over returning `null` from methods.

**39. What is a method reference?**

A method reference is a shorthand for a lambda that calls a single existing method. Four types: static (`Integer::parseInt`), instance on particular object (`str::startsWith`), instance on arbitrary object of type (`String::toUpperCase`), constructor (`Employee::new`). More readable than equivalent lambdas when the lambda just calls a method.

**40. What are default and static methods in interfaces (Java 8)?**

`default` methods provide a method implementation inside an interface — allows adding new methods to interfaces without breaking existing implementations. `static` methods in interfaces belong to the interface itself, not to implementing classes. Both were added to support the evolution of the Collections API (e.g. `List.sort()`).

---

## Multithreading

**41. What is the difference between a process and a thread?**

A process is an independent program with its own memory space. A thread is a lightweight unit of execution within a process — threads in the same process share memory (heap) but have their own stack. Threads are cheaper to create and faster to context-switch than processes.

**42. What are the ways to create a thread in Java?**

Three ways: (1) Extend `Thread` and override `run()`, (2) Implement `Runnable` and pass to `Thread` constructor, (3) Implement `Callable`, wrap in `FutureTask`, and pass to `Thread` — `Callable` can return a value and throw checked exceptions. Prefer `Runnable`/`Callable` over extending `Thread` (composition over inheritance).

**43. What is the difference between `Runnable` and `Callable`?**

`Runnable.run()` returns void and cannot throw checked exceptions. `Callable.call()` returns a value and can throw checked exceptions. `Callable` is used with `ExecutorService.submit()` which returns a `Future<T>` to retrieve the result.

**44. What is synchronisation in Java?**

Synchronisation controls access to shared resources in a multi-threaded environment to prevent race conditions. The `synchronized` keyword locks an object's monitor — only one thread can execute a synchronised block/method at a time. Use it to make compound operations atomic.

**45. What is the difference between `synchronized` method and `synchronized` block?**

A `synchronized` method locks the entire method on `this` (or the class for static methods). A `synchronized` block locks only a specific section on a specified object — finer-grained control with less contention. Prefer synchronised blocks to minimise the locked region.

**46. What is a deadlock?**

Deadlock occurs when two or more threads are permanently blocked, each waiting for a lock held by the other. Thread A holds lock 1 and waits for lock 2; Thread B holds lock 2 and waits for lock 1. Prevention strategies: always acquire locks in the same order, use `tryLock()` with timeout, minimise synchronised sections.

**47. What is the `volatile` keyword?**

`volatile` guarantees that a variable's value is always read from and written to main memory, not a thread-local CPU cache. It ensures visibility of changes across threads but does not provide atomicity for compound operations. Use for simple flags; use `AtomicInteger` or synchronisation for compound operations.

**48. What is the Executor Framework?**

The Executor Framework (`java.util.concurrent`) provides a higher-level abstraction for managing threads. `ExecutorService` manages a thread pool; you submit tasks and it handles thread lifecycle. `Executors.newFixedThreadPool(n)`, `newCachedThreadPool()`, `newScheduledThreadPool()`. Prefer this over manually managing `Thread` objects.

**49. What is `Future` and `CompletableFuture`?**

`Future<T>` represents the result of an async computation — `future.get()` blocks until the result is available. `CompletableFuture<T>` (Java 8) is a non-blocking alternative with a fluent API: `.thenApply()`, `.thenCompose()`, `.thenCombine()`, `.exceptionally()`. Use `CompletableFuture` for composing async operations without blocking.

**50. What is the difference between `wait()`, `notify()`, and `sleep()`?**

`wait()` releases the object's lock and pauses the thread until `notify()`/`notifyAll()` is called — used for inter-thread communication, must be inside `synchronized`. `notify()` wakes one waiting thread. `sleep(ms)` pauses the thread for a fixed time without releasing any lock. `wait`/`notify` are on `Object`; `sleep` is on `Thread`.

---

## JVM Internals

**51. What are the memory areas in the JVM?**

Heap (object allocation, GC managed), Stack (per-thread — local variables, method calls), Method Area / Metaspace (class metadata, static variables), PC Register (current instruction pointer per thread), Native Method Stack (for native C/C++ method calls). The Heap is the primary concern for memory management.

**52. What is garbage collection in Java?**

The JVM automatically reclaims memory from objects that are no longer reachable from any live reference. The GC runs in the background — you don't call `free()` like in C/C++. Modern GCs (G1, ZGC, Shenandoah) aim for low pause times. Calling `System.gc()` is just a suggestion — the JVM may ignore it.

**53. What is the difference between `Serial`, `Parallel`, `G1`, and `ZGC` garbage collectors?**

Serial GC: single-threaded, for small apps. Parallel GC: multi-threaded throughput GC, default in older JVMs. G1 (Garbage First): default since Java 9 — balances throughput and latency. ZGC (Java 15+): ultra-low latency, sub-millisecond pauses — for large heap applications. Choose based on latency vs throughput requirements.

**54. What is a memory leak in Java?**

A memory leak occurs when objects are no longer needed but still referenced, preventing GC from collecting them. Common causes: static collections growing unboundedly, unclosed resources, inner class references to outer class, listeners not removed. Use heap profilers (VisualVM, JProfiler) to detect retained objects.

**55. What is the difference between `ClassLoader` types?**

Bootstrap ClassLoader loads core Java classes (`java.lang`, `java.util`) from `rt.jar`. Extension ClassLoader loads extension libraries. Application ClassLoader loads your application's classes from the classpath. They form a delegation hierarchy — child first checks the parent before loading a class itself.

---

## Real Time

**56. `NullPointerException` — what are the common causes?**

Calling a method on a null reference, accessing a field of a null object, unboxing a null `Integer`/`Double`, accessing an array element through a null array. Java 14+ provides helpful NPE messages that tell you exactly which variable was null. Use `Optional`, null checks, or `@NonNull` annotations to prevent them.

**57. `ClassCastException` — why does it happen?**

You tried to cast an object to a type it is not an instance of: `Object obj = "hello"; Integer i = (Integer) obj`. The compiler allows the cast (since both are `Object` subtypes) but the JVM throws `ClassCastException` at runtime. Always check with `instanceof` before casting.

**58. `StackOverflowError` — what causes it?**

Infinite or excessively deep recursion fills the call stack until no space remains for new frames. Each method call adds a frame to the stack. Fix: add a base case to recursive methods, or refactor deep recursion to use an explicit stack/queue structure (iterative approach).

**59. `OutOfMemoryError` — how to diagnose?**

Heap is exhausted — too many live objects. Diagnose with: `-verbose:gc` flag (GC logs), heap dump (`-XX:+HeapDumpOnOutOfMemoryError`), profilers (VisualVM). Fix: increase heap (`-Xmx`), fix memory leaks, use streaming for large data instead of loading into memory.

**60. How do you make a class immutable?**

Declare the class `final` (can't be subclassed), make all fields `private final`, provide no setters, initialise all fields in the constructor, and return defensive copies of mutable fields (arrays, collections). `String` is the canonical example of an immutable class in Java.

---

## More

**61. What is the difference between `String.equals()` and `String.equalsIgnoreCase()`?**

`equals()` compares strings case-sensitively: `"Java".equals("java")` is `false`. `equalsIgnoreCase()` ignores case: `"Java".equalsIgnoreCase("java")` is `true`. Use `equalsIgnoreCase()` for user input comparisons where case should not matter.

**62. What is the String pool?**

The String pool is a special area in the heap where string literals are stored and reused. `"hello" == "hello"` is `true` because both reference the same pool object. `new String("hello")` bypasses the pool, creating a new object. `String.intern()` manually adds a string to the pool.

**63. What is the `instanceof` operator and pattern matching (Java 16+)?**

`instanceof` checks if an object is an instance of a type. Java 16 added pattern matching: `if (obj instanceof String s) { s.toUpperCase() }` — no explicit cast needed. Java 21 extended this to `switch` pattern matching, making type-based dispatch much cleaner.

**64. What are records in Java (Java 16+)?**

Records are immutable data classes declared concisely: `record Employee(String name, int salary) {}`. The compiler automatically generates a constructor, `equals()`, `hashCode()`, `toString()`, and accessor methods. Ideal for DTOs, value objects, and simple data carriers — no boilerplate.

**65. What is a sealed class (Java 17+)?**

A sealed class restricts which classes can extend it using `permits`: `sealed class Shape permits Circle, Rectangle`. Only the listed classes can extend `Shape`. Combined with pattern matching in `switch`, they enforce exhaustive type handling — the compiler warns if a case is missing.

**66. What is the difference between `HashMap` and `Hashtable`?**

`Hashtable` is the legacy synchronised map — thread-safe but slow. `HashMap` is unsynchronised — faster but not thread-safe. For thread-safe maps, prefer `ConcurrentHashMap` (finer-grained locking, much better performance than `Hashtable`). `Hashtable` also does not allow null keys or values; `HashMap` allows one null key.

**67. What is `enum` in Java?**

An `enum` defines a fixed set of constants: `enum Status { ACTIVE, INACTIVE, ON_LEAVE }`. Enums are classes — they can have fields, methods, and constructors. They are type-safe (unlike String constants), work in `switch` statements, and can be used in `EnumMap`/`EnumSet` for efficient operations.

**68. What is varargs in Java?**

Varargs (`...`) allows a method to accept a variable number of arguments of the same type: `public int sum(int... numbers)`. The caller can pass any number of arguments (including zero), and Java treats them as an array inside the method. Must be the last parameter.

**69. What is the difference between deep copy and shallow copy in Java?**

Shallow copy copies the object's fields — for primitive fields the values are copied, but for object references only the reference is copied (both copies point to the same nested object). Deep copy recursively copies all nested objects so the copies are completely independent. Implement deep copy by cloning nested objects or using serialisation.

**70. What is covariant return type?**

A covariant return type allows an overriding method in a subclass to return a more specific type than declared in the parent class. If `Animal.create()` returns `Animal`, `Dog.create()` can return `Dog`. This was introduced in Java 5 and avoids unnecessary casting on the caller side.

---

## Answer Summary Table

| # | Question (short) | Key answer |
|---|---------|------------|
| 2 | JVM/JRE/JDK | JDK ⊃ JRE ⊃ JVM |
| 4 | == vs .equals() | Reference vs content comparison |
| 7 | String/StringBuilder/StringBuffer | Immutable / mutable-unsafe / mutable-safe |
| 8 | final/finally/finalize | Keyword / block / deprecated method |
| 11 | Four OOP pillars | Encapsulation, Inheritance, Polymorphism, Abstraction |
| 14 | Overloading vs overriding | Compile-time vs runtime polymorphism |
| 17 | Abstract class vs interface | Partial impl+state vs pure contract |
| 22 | ArrayList vs LinkedList | Random access vs insert/delete speed |
| 23 | HashMap/LinkedHashMap/TreeMap | No order / insertion order / sorted |
| 29 | Checked vs unchecked | Compiler enforced vs runtime bugs |
| 34 | Lambda expressions | Anonymous function for functional interfaces |
| 36 | Stream API | Functional, declarative collection processing |
| 38 | Optional | Container for nullable values |
| 41 | Process vs thread | Separate memory vs shared memory |
| 44 | Synchronisation | Prevents race conditions with locks |
| 46 | Deadlock | Circular lock dependency |
| 48 | Executor Framework | Thread pool management |
| 51 | JVM memory areas | Heap, Stack, Metaspace, PC Register |
| 52 | Garbage collection | Auto memory reclamation for unreachable objects |
| 60 | Immutable class | final class, private final fields, no setters |
| 64 | Records | Concise immutable data classes (Java 16+) |
