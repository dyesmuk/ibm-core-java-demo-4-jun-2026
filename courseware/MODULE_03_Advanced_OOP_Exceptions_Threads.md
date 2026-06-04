# Core Java Courseware — Module 3: Advanced OOP, Exception Handling & Multithreading

> **Author:** Vaman Deshmukh | **Updated & Modernized:** 2024
> **Covers:** Core Java 08, 09 — Inheritance, Polymorphism, Exception Handling, Multithreading, Inner Classes, GC

---

## Table of Contents

- [1. Inheritance](#1-inheritance)
- [2. Polymorphism](#2-polymorphism)
- [3. Abstract Classes and Interfaces](#3-abstract-classes-and-interfaces)
- [4. Exception Handling](#4-exception-handling)
- [5. Multithreading](#5-multithreading)
- [6. Inner Classes](#6-inner-classes)
- [7. Garbage Collection](#7-garbage-collection)
- [Interview Questions](#interview-questions)
- [Assignments](#assignments)

---

## 1. Inheritance

### What Is Inheritance?

Inheritance is a mechanism where a **child class (subclass)** acquires the properties and behaviors of a **parent class (superclass)**, enabling code reuse and hierarchical organization.

```java
class Animal {                      // superclass / parent
    String name;
    void eat() { System.out.println(name + " is eating."); }
}

class Dog extends Animal {          // subclass / child
    void bark() { System.out.println(name + " is barking!"); }
}

Dog dog = new Dog();
dog.name = "Buddy";
dog.eat();    // inherited from Animal
dog.bark();   // defined in Dog
```

### The `extends` Keyword

```java
class ChildClass extends ParentClass { ... }
```

- Java supports **single inheritance** for classes (one parent only)
- A child class can be the parent of another class (multi-level hierarchy)
- Java supports **multiple inheritance through interfaces**

### Types of Inheritance in Java

```
Single:     A → B
Multi-level: A → B → C
Hierarchical: A → B, A → C
Multiple (via interfaces): B + C → D
```

### The `super` Keyword

`super` refers to the **parent class** and is used to:

1. Call the parent constructor
2. Call an overridden parent method
3. Access a hidden parent field

```java
class Vehicle {
    int speed;
    Vehicle(int speed) {
        this.speed = speed;
        System.out.println("Vehicle created, speed = " + speed);
    }
    void describe() {
        System.out.println("Vehicle at speed " + speed);
    }
}

class Car extends Vehicle {
    String model;

    Car(int speed, String model) {
        super(speed);          // calls Vehicle(int) constructor
        this.model = model;
    }

    @Override
    void describe() {
        super.describe();      // calls Vehicle's describe()
        System.out.println("Model: " + model);
    }
}
```

**Rules for `super()`:**
- Must be the **first statement** in a constructor
- If not explicitly called, Java inserts `super()` (no-arg call) automatically

### Inheriting Members

| Member | Inherited? | Notes |
|--------|-----------|-------|
| `public` fields/methods | ✓ | Directly accessible |
| `protected` fields/methods | ✓ | Accessible within child class |
| `default` fields/methods | ✓ (same package) | Not accessible if child is in different package |
| `private` fields/methods | ✗ | Not inherited; use getters/setters |
| Constructors | ✗ | Not inherited; must call via `super()` |

### Method Overriding

A child class provides its own implementation of a method defined in the parent class.

```java
class Shape {
    double area() { return 0; }   // generic version
}

class Rectangle extends Shape {
    double width, height;

    Rectangle(double w, double h) {
        width = w;
        height = h;
    }

    @Override
    double area() { return width * height; }   // specialized version
}

class Circle extends Shape {
    double radius;

    Circle(double r) { radius = r; }

    @Override
    double area() { return Math.PI * radius * radius; }
}
```

**Rules for method overriding:**
- Method signature (name + parameters) must be **identical**
- Return type must be same or a **covariant (sub)type**
- Access modifier can be same or **less restrictive**
- Cannot override `static`, `final`, or `private` methods
- Use `@Override` annotation — compiler verifies the override

### The `final` Keyword

| Usage | Effect |
|-------|--------|
| `final` class | Cannot be subclassed |
| `final` method | Cannot be overridden |
| `final` variable | Value cannot be changed (constant) |

```java
final class ImmutableConfig { ... }      // nobody can extend this

class Parent {
    final void secureMethod() { ... }    // cannot be overridden
}

final double PI = 3.14159;              // constant
```

---

## 2. Polymorphism

### Compile-Time Polymorphism (Method Overloading)

Multiple methods with the **same name** but **different parameter lists** in the same class.

```java
class Printer {
    void print(int n)          { System.out.println("int: " + n); }
    void print(double d)       { System.out.println("double: " + d); }
    void print(String s)       { System.out.println("String: " + s); }
    void print(int a, int b)   { System.out.println("Two ints: " + a + ", " + b); }
}

Printer p = new Printer();
p.print(10);           // int: 10
p.print(3.14);         // double: 3.14
p.print("Hello");      // String: Hello
p.print(5, 7);         // Two ints: 5, 7
```

**Overloading is determined at compile time** based on the declared type.

### Runtime Polymorphism (Dynamic Dispatch)

A parent reference can point to a child object, and the correct overridden method is called **at runtime**.

```java
Shape s1 = new Rectangle(4, 5);
Shape s2 = new Circle(3);

s1.area();   // calls Rectangle.area() — decided at runtime
s2.area();   // calls Circle.area()    — decided at runtime

// Polymorphic array
Shape[] shapes = { new Rectangle(3, 4), new Circle(5), new Triangle(6, 8) };
for (Shape s : shapes) {
    System.out.println("Area: " + s.area());   // correct method for each type
}
```

### instanceof Operator

Checks if an object is an instance of a class at runtime:

```java
Shape s = new Circle(5);
if (s instanceof Circle) {
    Circle c = (Circle) s;   // safe downcast
    System.out.println("Radius: " + c.radius);
}

// Modern (Java 16+) pattern matching
if (s instanceof Circle c) {
    System.out.println("Radius: " + c.radius);  // c is already cast
}
```

---

## 3. Abstract Classes and Interfaces

### Abstract Classes

A class declared with `abstract` that **cannot be instantiated**. May contain both abstract and concrete methods.

```java
abstract class Animal {
    String name;

    Animal(String name) { this.name = name; }

    abstract void makeSound();   // must be implemented by subclasses

    void breathe() {             // concrete — inherited as-is
        System.out.println(name + " is breathing.");
    }
}

class Dog extends Animal {
    Dog(String name) { super(name); }

    @Override
    void makeSound() { System.out.println(name + " says: Woof!"); }
}

class Cat extends Animal {
    Cat(String name) { super(name); }

    @Override
    void makeSound() { System.out.println(name + " says: Meow!"); }
}
```

**When to use abstract class:** When you have a partial implementation to share AND want to enforce subclasses to fill in specific behavior.

### Interfaces

A **contract** specifying what methods a class must implement. Before Java 8, contained only abstract methods. Since Java 8+, can also contain `default` and `static` methods.

```java
interface Drawable {
    void draw();                     // abstract (public by default)
    void resize(double factor);
}

interface Colorable {
    void setColor(String color);
}

// A class can implement multiple interfaces
class GraphicCircle implements Drawable, Colorable {
    double radius;
    String color;

    @Override public void draw()   { System.out.println("Drawing circle"); }
    @Override public void resize(double f) { radius *= f; }
    @Override public void setColor(String c) { color = c; }
}
```

**Java 8+ Interface Features:**

```java
interface Validator {
    boolean validate(String input);    // abstract — must implement

    // default method — has a body, can be overridden
    default boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    // static method — called on interface itself
    static Validator emailValidator() {
        return s -> s != null && s.contains("@");
    }
}
```

### Abstract Class vs Interface

| Aspect | Abstract Class | Interface |
|--------|---------------|-----------|
| Instantiation | Cannot | Cannot |
| Methods | Abstract + concrete | Abstract + default + static |
| Fields | Any type | Only `public static final` |
| Constructors | Yes | No |
| Inheritance | Single (extends) | Multiple (implements) |
| State | Can maintain | Generally should not |
| Use case | Shared base implementation | Capability contract |

---

## 4. Exception Handling

### What Is an Exception?

An **exception** is an event that disrupts the normal flow of a program. It is an object created when an error occurs, containing information about the error's name, type, and location.

```java
Statement1;           // executes normally
Statement2;           // exception occurs here!
Statement3;           // normally skipped — but restored with handling
```

**Without handling:** The JVM's default handler prints the stack trace and terminates the program.

**With handling:** You intercept the exception, recover gracefully, and continue execution.

### try-catch-finally Syntax

```java
try {
    // Code that might throw an exception
    int result = 10 / 0;
}
catch (ArithmeticException e) {
    // Recovery code — runs when ArithmeticException occurs
    System.out.println("Cannot divide by zero: " + e.getMessage());
}
catch (Exception e) {
    // Catch-all for other exceptions (must be last)
    System.out.println("Unexpected error: " + e.getMessage());
}
finally {
    // Always executes — for cleanup (closing files, connections)
    System.out.println("Cleanup complete.");
}
```

**Key rules:**
- `try` must be followed by at least one `catch` or `finally`
- Multiple `catch` blocks allowed — order from **most specific to most general**
- `finally` always executes, even if an exception was thrown and caught
- Only exception: `System.exit()` prevents `finally` from running

### Exception Class Hierarchy

```
java.lang.Throwable
├── Error (should NOT be caught — JVM-level problems)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── VirtualMachineError
└── Exception
    ├── IOException (Checked)
    │   ├── FileNotFoundException
    │   └── EOFException
    ├── SQLException (Checked)
    └── RuntimeException (Unchecked)
        ├── ArithmeticException          (e.g., divide by zero)
        ├── NullPointerException         (e.g., null.method())
        ├── ArrayIndexOutOfBoundsException
        ├── NumberFormatException        (e.g., Integer.parseInt("abc"))
        ├── ClassCastException
        └── IllegalArgumentException
```

### Checked vs Unchecked Exceptions

| Type | When detected | Must handle? | Examples |
|------|---------------|-------------|---------|
| **Checked** | Compile time | Yes — or declare with `throws` | `IOException`, `SQLException` |
| **Unchecked (Runtime)** | Runtime | No (but good practice) | `NullPointerException`, `ArithmeticException` |
| **Error** | Runtime | No (and shouldn't) | `OutOfMemoryError` |

### Common Exceptions and Causes

```java
// ArithmeticException
int x = 10 / 0;

// NullPointerException
String s = null;
s.length();

// NumberFormatException
int n = Integer.parseInt("abc");

// ArrayIndexOutOfBoundsException
int[] arr = new int[5];
arr[10] = 1;

// ClassCastException
Object obj = "hello";
Integer i = (Integer) obj;
```

### The `throw` Keyword

Explicitly throw an exception based on your own condition:

```java
static void checkAge(int age) {
    if (age < 18) {
        throw new ArithmeticException("Not eligible to vote.");
    }
    System.out.println("Welcome! Please cast your vote.");
}
```

### The `throws` Keyword

Declares that a method **may throw** a checked exception — callers must handle or propagate it:

```java
void readFile(String path) throws IOException {
    FileReader fr = new FileReader(path);   // may throw FileNotFoundException
    // ...
}
```

### `throw` vs `throws`

| | `throw` | `throws` |
|-|---------|---------|
| Purpose | Actually throws an exception | Declares possible exceptions |
| Location | Inside method body | In method signature |
| Followed by | An exception **instance** | Exception class **names** |
| Multiple | Only one at a time | Multiple (comma-separated) |

### User-Defined Exceptions

```java
// Custom exception class
class InsufficientFundsException extends Exception {
    double amount;

    InsufficientFundsException(double amount) {
        super("Insufficient funds. Shortfall: " + amount);
        this.amount = amount;
    }
}

// Using the custom exception
class BankAccount {
    double balance;

    void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException(amount - balance);
        }
        balance -= amount;
    }
}

// Handling it
BankAccount acc = new BankAccount();
acc.balance = 1000;
try {
    acc.withdraw(1500);
} catch (InsufficientFundsException e) {
    System.out.println(e.getMessage());     // custom message
    System.out.println("Shortfall: " + e.amount);
}
```

### Multi-Catch (Java 7+)

```java
try {
    // code that may throw multiple exceptions
} catch (ArithmeticException | NullPointerException e) {
    // handle both the same way
    System.out.println("Math or null error: " + e.getMessage());
} catch (IOException e) {
    System.out.println("IO error: " + e.getMessage());
}
```

### final, finally, finalize — Compared

| | `final` | `finally` | `finalize` |
|-|---------|-----------|------------|
| Type | Keyword/modifier | Block | Method |
| Purpose | Prevent modification/inheritance/override | Ensure cleanup code runs | Pre-GC cleanup (deprecated) |
| Applied to | Classes, methods, variables | `try` block | Object class method |

```java
// final variable
final int MAX = 100;

// finally block
try { } catch (Exception e) { } finally { connection.close(); }

// finalize (deprecated — do not use in new code)
@Override
protected void finalize() throws Throwable { cleanup(); }
```

> **Modern Note:** `finalize()` is deprecated since Java 9 and removed in Java 18. Use `try-with-resources` or `Cleaner` API instead.

### Try-with-Resources (Java 7+)

Automatically closes resources that implement `AutoCloseable`:

```java
// Old way — manual finally block
FileReader fr = null;
try {
    fr = new FileReader("file.txt");
    // read
} finally {
    if (fr != null) fr.close();
}

// Modern way — try-with-resources
try (FileReader fr = new FileReader("file.txt");
     BufferedReader br = new BufferedReader(fr)) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}  // fr and br are closed automatically
```

### Top 10 Exceptions in Production Systems

1. `NullPointerException` — ~70% of production errors
2. `NumberFormatException` — ~55%
3. `IllegalArgumentException` — ~50%
4. `RuntimeException` — ~23%
5. `IllegalStateException` — ~22%
6. `NoSuchMethodException` — ~16%
7. `ClassCastException` — ~15%
8. `Exception` — ~15%
9. `ParseException` — ~13%
10. `InvocationTargetException` — ~13%

---

## 5. Multithreading

### What Is a Thread?

A **thread** is the smallest unit of execution within a process. Java supports **multi-threading** — running multiple threads concurrently within a single program.

**Multitasking vs Multithreading:**

| | Multitasking | Multithreading |
|-|-------------|----------------|
| Unit | Processes | Threads |
| Memory | Each process has its own memory | Threads share process memory |
| Created by | OS | Programmer |
| Overhead | Higher | Lower (threads are "lightweight") |

### Thread Lifecycle

```
NEW → start() → RUNNABLE ⇌ BLOCKED/WAITING/TIMED_WAITING → TERMINATED
```

| State | Description |
|-------|-------------|
| `NEW` | Thread created but not started yet |
| `RUNNABLE` | Running or ready to run |
| `BLOCKED` | Waiting to acquire a monitor lock |
| `WAITING` | Waiting indefinitely for another thread |
| `TIMED_WAITING` | Waiting for a specified time |
| `TERMINATED` | Execution complete |

### Creating Threads — Method 1: Extend Thread

```java
class MyThread extends Thread {
    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(getName() + ": " + i);
            try { Thread.sleep(500); } catch (InterruptedException e) { }
        }
    }
}

// Usage
MyThread t1 = new MyThread();
MyThread t2 = new MyThread();
t1.setName("Thread-A");
t2.setName("Thread-B");
t1.start();   // starts new thread — calls run() in new thread
t2.start();
```

### Creating Threads — Method 2: Implement Runnable (Preferred)

```java
class PrintTask implements Runnable {
    private String message;
    private int count;

    PrintTask(String message, int count) {
        this.message = message;
        this.count = count;
    }

    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
            System.out.println(message + " - " + i);
        }
    }
}

// Usage
Thread t1 = new Thread(new PrintTask("Hello", 5));
Thread t2 = new Thread(new PrintTask("World", 5));
t1.start();
t2.start();
```

**Why prefer `Runnable`?**
- Your class can still extend another class (Java has single inheritance)
- Better separation of task logic from thread management
- Easier to reuse with `ExecutorService`

### Creating Threads — Method 3: Lambda (Java 8+)

```java
Thread t = new Thread(() -> {
    for (int i = 0; i < 5; i++) {
        System.out.println("Running: " + i);
    }
});
t.start();
```

### Key Thread Methods

| Method | Description |
|--------|-------------|
| `start()` | Starts the thread; invokes `run()` in a new thread |
| `run()` | The thread's task — override this |
| `sleep(ms)` | Pauses thread for given milliseconds |
| `join()` | Waits for this thread to finish |
| `getName()` / `setName()` | Get/set thread name |
| `getPriority()` / `setPriority()` | Thread priority (1–10, default 5) |
| `isAlive()` | Returns true if thread is running |
| `currentThread()` | Returns reference to currently running thread |
| `isDaemon()` / `setDaemon()` | Daemon thread status |

### Thread Synchronization

When multiple threads access **shared resources** (like a bank account), **race conditions** can corrupt data.

**Without synchronization (race condition):**
```java
class Counter {
    int count = 0;
    void increment() { count++; }   // NOT thread-safe
}
```

**With synchronized method:**
```java
class Counter {
    int count = 0;
    synchronized void increment() { count++; }   // thread-safe
}
```

**Synchronized block (finer control):**
```java
class SharedResource {
    void doWork() {
        synchronized(this) {           // only one thread at a time
            // critical section
        }
        // non-critical code — can run concurrently
    }
}
```

**The `join()` method example:**
```java
Thread t1 = new Thread(() -> System.out.println("Task A complete"));
Thread t2 = new Thread(() -> System.out.println("Task B complete"));

t1.start();
t1.join();   // main thread waits for t1 to finish before starting t2
t2.start();
```

### Daemon Threads

Daemon threads provide background services and are automatically terminated when all user threads finish.

```java
Thread daemon = new Thread(() -> {
    while (true) {
        System.out.println("Daemon running...");
        try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
    }
});
daemon.setDaemon(true);   // MUST be set before start()
daemon.start();
```

Examples: Garbage Collector, auto-save, log flusher.

### Modern Threading: ExecutorService (Java 5+)

Prefer `ExecutorService` over raw `Thread` for production code:

```java
import java.util.concurrent.*;

ExecutorService executor = Executors.newFixedThreadPool(4);

for (int i = 0; i < 10; i++) {
    final int taskNum = i;
    executor.submit(() -> {
        System.out.println("Task " + taskNum + " by " + Thread.currentThread().getName());
    });
}

executor.shutdown();   // gracefully stop accepting new tasks
executor.awaitTermination(5, TimeUnit.SECONDS);
```

---

## 6. Inner Classes

An **inner class** is a class defined inside another class.

**Advantages:**
- Can access all members of the outer class (including private)
- Better organization — groups related classes together
- Code optimization

### Types of Inner Classes

```
Inner Classes
├── Regular inner class (non-static)
├── Static nested class
├── Method-local inner class
└── Anonymous inner class
```

### Regular Inner Class

```java
class Outer {
    private int x = 10;

    class Inner {
        void show() {
            System.out.println("x = " + x);  // accesses private outer field
        }
    }

    void demo() {
        Inner inner = new Inner();
        inner.show();
    }
}

// Creating inner object from outside:
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();
inner.show();
```

### Static Nested Class

Does not need an outer class instance. Cannot access non-static outer members.

```java
class Outer {
    static int y = 20;

    static class StaticNested {
        void show() {
            System.out.println("y = " + y);  // can access static outer fields
        }
    }
}

// No outer instance needed:
Outer.StaticNested nested = new Outer.StaticNested();
nested.show();
```

### Method-Local Inner Class

Defined inside a method. Only accessible within that method.

```java
class Outer {
    void outerMethod() {
        int localVar = 50;   // must be effectively final

        class MethodLocal {
            void show() {
                System.out.println("Local: " + localVar);
            }
        }

        MethodLocal ml = new MethodLocal();
        ml.show();
    }
}
```

### Anonymous Inner Class

A class without a name, defined and instantiated in a single expression. Commonly used to implement interfaces or extend abstract classes on the fly.

```java
// Traditional way
interface Greeting {
    void greet(String name);
}

// Anonymous implementation
Greeting formal = new Greeting() {
    @Override
    public void greet(String name) {
        System.out.println("Good day, " + name + ".");
    }
};

formal.greet("Alice");

// Modern equivalent with lambda (Java 8+)
Greeting casual = name -> System.out.println("Hey, " + name + "!");
casual.greet("Bob");
```

---

## 7. Garbage Collection

### How GC Works

Java's **Garbage Collector (GC)** automatically reclaims heap memory occupied by objects that are no longer reachable from any live reference.

**When does an object become eligible for GC?**

```java
// 1. Reference set to null
MyObject obj = new MyObject();
obj = null;              // eligible for GC

// 2. Reference reassigned
MyObject a = new MyObject();
MyObject b = new MyObject();
a = b;                   // first object now eligible

// 3. Object goes out of scope
{
    MyObject temp = new MyObject();
}  // temp is out of scope — object eligible
```

### finalize() and System.gc()

```java
// Request GC (not guaranteed)
System.gc();
Runtime.getRuntime().gc();   // equivalent

// finalize() — called before GC (deprecated, do not use in new code)
@Override
protected void finalize() throws Throwable {
    System.out.println("Object being collected");
}
```

> **Modern Note:** `finalize()` is deprecated and unreliable. Use `try-with-resources` for deterministic cleanup of resources (files, connections, etc.). Use `java.lang.ref.Cleaner` for advanced scenarios.

### Modern GC in Java

| Collector | Available since | Best for |
|-----------|----------------|---------|
| Serial GC | Java 1 | Small apps, single-core |
| Parallel GC | Java 1.4 | Throughput-optimized |
| G1 (Garbage First) | Java 7 (default 9+) | Balanced latency/throughput |
| ZGC | Java 11+ | Ultra-low latency (< 1ms pauses) |
| Shenandoah | Java 12+ | Low latency |

---

## Interview Questions

1. What is the difference between method overloading and method overriding?
2. Can you override a `static` method in Java?
3. What is the difference between an abstract class and an interface?
4. When would you use an abstract class instead of an interface?
5. What is `super` and when is it used?
6. What is an exception? What is the difference between checked and unchecked exceptions?
7. What is the purpose of `throw` and `throws` keywords?
8. How can you handle multiple exceptions?
9. What is the `finally` block and when does it execute?
10. What is the difference between `final`, `finally`, and `finalize`?
11. What is try-with-resources?
12. What is a user-defined exception? How do you create one?
13. What is multithreading? What are the two ways to create a thread in Java?
14. What is the difference between `start()` and `run()`?
15. What is thread synchronization and why is it needed?
16. What is a race condition? How do you prevent it?
17. What is a daemon thread?
18. What is the difference between `wait()`, `sleep()`, and `join()`?
19. What is an inner class? What are its types?
20. What is an anonymous inner class? How does it differ from a lambda?
21. What is garbage collection? When does an object become eligible?

---

## Assignments

1. Create a class hierarchy for library items: `Item` (abstract) → `WrittenItem` → `Book`, `JournalPaper`; and `Item` → `MediaItem` → `Video`, `CD`. Implement relevant fields and methods.

2. Write a banking application with `SavingsAccount` and `CurrentAccount` inheriting from `Account`. Override `withdraw()` in each — `SavingsAccount` checks minimum balance; `CurrentAccount` checks overdraft limit.

3. Write a program demonstrating all four types of inner classes.

4. Create a custom exception `EmployeeException` and throw it when employee salary is below 3000.

5. Write a multi-threaded program where one thread copies content from one file to another, printing a message every 10 characters.

6. Write a program demonstrating thread synchronization with a shared counter accessed by 5 threads.

7. Implement a `Runnable`-based timer that refreshes every 10 seconds.
