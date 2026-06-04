# Core Java Courseware — Module 7: Annotations, Regular Expressions & Concurrency

> **Author:** Vaman Deshmukh | **Updated & Modernized:** 2024
> **Covers:** Core Java 13, 20, 21 — Java Annotations, Regex (full pattern reference), Concurrency (Executor, Callable, Future, Concurrent Collections)

---

## Table of Contents

- [1. Java Annotations](#1-java-annotations)
- [2. Regular Expressions](#2-regular-expressions)
- [3. Concurrency in Java](#3-concurrency-in-java)
- [Interview Questions](#interview-questions)
- [Assignments](#assignments)

---

## 1. Java Annotations

### What Is an Annotation?

An **annotation** is **metadata** — data about data. It provides information about a program element (class, method, field, parameter, package) to:

- The **compiler** — to detect errors or suppress warnings
- **Tools** at compile/deploy time — to generate code, XML configs, etc.
- **Frameworks at runtime** — to configure behavior (Spring, Hibernate, JUnit, etc.)

Annotations start with the `@` symbol and were introduced in **Java 1.5**.

```java
@Override                         // tells the compiler: this method overrides a parent method
public String toString() { ... }

@Deprecated                       // tells users: do not use this anymore
public void oldMethod() { ... }

@SuppressWarnings("unchecked")    // tells the compiler: suppress this specific warning
public void rawTypeMethod() { ... }
```

### Why Use Annotations?

Annotations enable **declarative programming**:

- **Less boilerplate** — frameworks generate code from annotations instead of you writing it
- **Eliminate config files** — application configuration embedded directly in source (replacing XML files in frameworks like Struts and Hibernate)
- **Easier maintenance** — behavior defined at the point of declaration, not in a separate file

---

### Built-in Java Annotations

Java defines **seven** built-in annotations, split across two packages.

#### From `java.lang` — applied to your code:

**`@Override`**

Signals to the compiler that the annotated method must override a method in a superclass or implement a method from an interface. If no such method exists in the parent, the compiler raises an error — preventing silent bugs.

```java
class Animal {
    public void speak() { System.out.println("..."); }
}

class Dog extends Animal {
    @Override
    public void speak() { System.out.println("Woof!"); }  // verified by compiler

    // @Override
    // public void speek() { }  // COMPILE ERROR — no such method in parent
}
```

**`@Deprecated`**

Marks a method, class, or field as outdated. The compiler shows a warning wherever the deprecated element is used, encouraging developers to migrate to newer alternatives.

```java
@Deprecated
public static void connectOldWay(String url) {
    // old implementation
}

// Modern replacement
public static void connect(String url, int timeout) {
    // new implementation
}
```

> **Modern Note (Java 9+):** `@Deprecated` gained two new attributes:
> ```java
> @Deprecated(since = "9", forRemoval = true)
> public void oldMethod() { }
> ```

**`@SuppressWarnings`**

Instructs the compiler to suppress specific categories of warnings.

```java
@SuppressWarnings("unchecked")               // suppress one category
public void addToRawList(List list) { ... }

@SuppressWarnings({"unchecked", "deprecation"})  // suppress multiple
public void legacyCode() { ... }
```

Common warning types: `"unchecked"`, `"deprecation"`, `"unused"`, `"rawtypes"`, `"serial"`

---

#### From `java.lang.annotation` — meta-annotations (annotations for annotations):

**`@Retention`**

Specifies **how long** the annotation is kept:

| Policy | Kept in... | Accessible via Reflection? |
|--------|-----------|--------------------------|
| `SOURCE` | Source file only | No — discarded at compile time |
| `CLASS` | `.class` file | No — not accessible at runtime (default) |
| `RUNTIME` | `.class` file + JVM | Yes — can be read at runtime |

```java
@Retention(RetentionPolicy.RUNTIME)   // available at runtime via reflection
@interface MyAnnotation {
    String author();
    int version() default 1;
}
```

**`@Target`**

Specifies **where** the annotation can be applied:

| `ElementType` | Applicable to |
|---------------|--------------|
| `TYPE` | Class, interface, enum |
| `METHOD` | Methods |
| `FIELD` | Fields/variables |
| `PARAMETER` | Method parameters |
| `CONSTRUCTOR` | Constructors |
| `LOCAL_VARIABLE` | Local variables |
| `PACKAGE` | Packages |
| `ANNOTATION_TYPE` | Other annotations |

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    String value() default "default";
}
```

**`@Documented`**

Indicates the annotation should appear in the **Javadoc** HTML output.

```java
@Documented
@interface ClassPreamble {
    String author();
    String date();
    int currentRevision() default 1;
}

@ClassPreamble(author = "Alice", date = "2024-01-15", currentRevision = 3)
public class MyService { ... }
```

**`@Inherited`**

When applied to a class annotation, specifies that subclasses automatically **inherit** the annotation from their parent.

```java
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Audited { }

@Audited
class BaseService { }

class UserService extends BaseService { }
// UserService is also @Audited — inherited automatically
```

---

### Types of Annotations

| Type | Description | Example |
|------|-------------|---------|
| **Marker** | No attributes — presence alone carries meaning | `@Override`, `@Deprecated` |
| **Single-value** | One attribute | `@SuppressWarnings("unchecked")` |
| **Multi-value** | Two or more attributes | `@Author(name="Alice", date="2024")` |

---

### Defining a Custom Annotation

```java
// Step 1 — Define the annotation type
@Retention(RetentionPolicy.RUNTIME)    // readable at runtime
@Target(ElementType.TYPE)              // applies to classes
public @interface Version {
    int major() default 1;
    int minor() default 0;
    String release() default "GA";     // default value
}

// Step 2 — Apply it
@Version(major = 2, minor = 5, release = "BETA")
public class PaymentService {
    // ...
}
```

**Rules for annotation element declarations:**
- No parameters or `throws` clause
- Return types restricted to: primitives, `String`, `Class`, enums, other annotations, and arrays of these
- Default values set with `default` keyword

---

### Reading Annotations at Runtime (Reflection API)

```java
import java.lang.reflect.*;

// Read annotation from a class
Class<?> clazz = PaymentService.class;

if (clazz.isAnnotationPresent(Version.class)) {
    Version v = clazz.getAnnotation(Version.class);
    System.out.println("Version: " + v.major() + "." + v.minor());
    System.out.println("Release: " + v.release());
}

// Read annotation from a method
Method[] methods = clazz.getDeclaredMethods();
for (Method m : methods) {
    if (m.isAnnotationPresent(Deprecated.class)) {
        System.out.println("Deprecated method: " + m.getName());
    }
}
```

---

### Common Framework Annotations

| Framework | Annotation | Purpose |
|-----------|-----------|---------|
| **JUnit** | `@Test`, `@BeforeEach`, `@AfterEach` | Mark test methods, setup/teardown |
| **Spring** | `@Component`, `@Service`, `@Repository` | Mark beans for dependency injection |
| **Spring** | `@Autowired`, `@Value` | Inject dependencies/config values |
| **Spring MVC** | `@Controller`, `@RequestMapping`, `@GetMapping` | Map HTTP requests to methods |
| **Hibernate/JPA** | `@Entity`, `@Table`, `@Id`, `@Column` | Map Java classes to database tables |
| **Hibernate/JPA** | `@OneToMany`, `@ManyToOne`, `@ManyToMany` | Define table relationships |
| **EJB** | `@EJB`, `@Remote`, `@Stateless` | Define Enterprise Java Beans |
| **JAX-WS** | `@WebService`, `@WebMethod` | Define SOAP web services |
| **JAX-RS** | `@Path`, `@GET`, `@POST`, `@Produces` | Define REST endpoints |

```java
// Spring + JPA example showing annotations in action
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository repo;

    public User findById(Long id) {
        return repo.findById(id).orElseThrow();
    }
}

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;
}
```

---

## 2. Regular Expressions

### What Is a Regular Expression?

A **regular expression (regex)** is a sequence of characters that defines a **search pattern**. Used for:

- **Validation** — email, phone, password, URLs, dates
- **Search** — find patterns within text
- **Replace** — replace patterns with other text
- **Split** — split strings by a pattern

### Java Regex API (`java.util.regex`)

Three key classes:

| Class | Role |
|-------|------|
| `Pattern` | Compiled representation of a regex — immutable and thread-safe |
| `Matcher` | Engine that performs match operations using a `Pattern` |
| `PatternSyntaxException` | Thrown when a regex pattern is syntactically invalid |

```java
import java.util.regex.*;

// Step 1: compile the pattern
Pattern pattern = Pattern.compile("\\d{3}-\\d{4}");

// Step 2: create a matcher for the input string
Matcher matcher = pattern.matcher("Call us at 123-4567 or 987-6543");

// Step 3: use the matcher
while (matcher.find()) {
    System.out.println("Found: " + matcher.group());     // 123-4567 then 987-6543
    System.out.println("  at index: " + matcher.start()); // position
}
```

---

### Three Matching Methods Compared

| Method | Fixed start? | Fixed end? | Description |
|--------|-------------|-----------|-------------|
| `matches()` | Yes | Yes | Entire string must match the pattern |
| `lookingAt()` | Yes | No | Input must start with the pattern |
| `find()` | No | No | Finds next occurrence anywhere in the input |

```java
Pattern p = Pattern.compile("bcd");
Matcher m = p.matcher("abcde");

System.out.println(m.matches());    // false — "abcde" ≠ "bcd"
System.out.println(m.lookingAt());  // false — "abcde" does not start with "bcd"
System.out.println(m.find());       // true  — "bcd" is found inside "abcde"
```

---

### Pattern Flags

```java
// Case-insensitive matching
Pattern p = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE);

// Multiline mode — ^ and $ match each line boundary
Pattern p2 = Pattern.compile("^start", Pattern.MULTILINE);

// Dotall mode — . matches newlines too
Pattern p3 = Pattern.compile(".*", Pattern.DOTALL);

// Combine flags
Pattern p4 = Pattern.compile("[a-z]+",
    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
```

---

### Complete Regex Pattern Reference

#### Character Classes

| Pattern | Matches |
|---------|---------|
| `[abc]` | a, b, or c |
| `[^abc]` | Any character except a, b, c (negation) |
| `[a-z]` | Any lowercase letter a through z |
| `[A-Z]` | Any uppercase letter |
| `[0-9]` | Any digit |
| `[a-zA-Z]` | Any letter (upper or lower) |
| `[a-zA-Z0-9]` | Any alphanumeric character |
| `[a-d[m-p]]` | a through d, or m through p (union) |
| `[a-z&&[def]]` | d, e, or f (intersection) |
| `[a-z&&[^bc]]` | a through z, except b and c (subtraction) |

#### Predefined Character Classes (Shorthand)

| Pattern | Equivalent | Matches |
|---------|-----------|---------|
| `.` | (any) | Any character (except newline by default) |
| `\d` | `[0-9]` | Any digit |
| `\D` | `[^0-9]` | Any non-digit |
| `\w` | `[a-zA-Z_0-9]` | Any word character |
| `\W` | `[^\w]` | Any non-word character |
| `\s` | `[ \t\n\x0B\f\r]` | Any whitespace character |
| `\S` | `[^\s]` | Any non-whitespace character |
| `\h` | horizontal whitespace | Space, tab, etc. |
| `\v` | vertical whitespace | Newline, form-feed, etc. |

> **Java reminder:** In Java string literals, `\` must be escaped as `\\`. So the regex `\d` is written as `"\\d"` in Java code.

#### POSIX Character Classes

| Pattern | Matches |
|---------|---------|
| `\p{Lower}` | `[a-z]` — lowercase letters |
| `\p{Upper}` | `[A-Z]` — uppercase letters |
| `\p{Alpha}` | `[a-zA-Z]` — letters |
| `\p{Digit}` | `[0-9]` — digits |
| `\p{Alnum}` | `[a-zA-Z0-9]` — alphanumeric |
| `\p{Punct}` | Punctuation characters |
| `\p{Space}` | Whitespace characters |
| `\p{XDigit}` | `[0-9a-fA-F]` — hex digits |

#### Boundary Matchers

| Pattern | Matches |
|---------|---------|
| `^` | Beginning of a line |
| `$` | End of a line |
| `\b` | Word boundary |
| `\B` | Non-word boundary |
| `\A` | Beginning of entire input |
| `\z` | End of entire input |
| `\G` | End of previous match |

#### Quantifiers

| Greedy | Reluctant | Possessive | Meaning |
|--------|-----------|-----------|---------|
| `X?` | `X??` | `X?+` | X once or not at all |
| `X*` | `X*?` | `X*+` | X zero or more times |
| `X+` | `X+?` | `X++` | X one or more times |
| `X{n}` | `X{n}?` | `X{n}+` | X exactly n times |
| `X{n,}` | `X{n,}?` | `X{n,}+` | X at least n times |
| `X{n,m}` | `X{n,m}?` | `X{n,m}+` | X between n and m times |

- **Greedy** — matches as much as possible, then backtracks
- **Reluctant** — matches as little as possible
- **Possessive** — matches as much as possible, no backtracking (fastest)

#### Logical Operators

| Pattern | Meaning |
|---------|---------|
| `XY` | X followed by Y |
| `X\|Y` | Either X or Y |
| `(X)` | X as a capturing group |
| `(?:X)` | X as a non-capturing group |
| `(?<name>X)` | X as a named capturing group |

#### Lookahead and Lookbehind (Zero-width Assertions)

| Pattern | Meaning |
|---------|---------|
| `(?=X)` | Positive lookahead — followed by X |
| `(?!X)` | Negative lookahead — not followed by X |
| `(?<=X)` | Positive lookbehind — preceded by X |
| `(?<!X)` | Negative lookbehind — not preceded by X |

```java
// Find digits followed by "px"
Pattern p = Pattern.compile("\\d+(?=px)");
Matcher m = p.matcher("font: 16px, margin: 8px");
while (m.find()) System.out.println(m.group());  // 16, 8

// Find "cat" not preceded by "wild"
Pattern p2 = Pattern.compile("(?<!wild)cat");
```

---

### Practical Validation Examples

```java
// Email validation
String emailRegex = "[^\\d][\\w-.]+@[^\\d][\\w]+\\.[^\\d][\\w]+";
System.out.println("user@example.com".matches(emailRegex));  // true

// More robust email
String emailFull = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

// Indian mobile number (starts with 6–9, total 10 digits)
String phoneRegex = "[6-9][0-9]{9}";
System.out.println("9876543210".matches(phoneRegex));   // true
System.out.println("1234567890".matches(phoneRegex));   // false

// Strong password (min 8 chars, at least 1 uppercase, 1 digit, 1 special)
String pwdRegex = "(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%!]).{8,}";
System.out.println("MyPass@1".matches(pwdRegex));   // true

// Valid Java identifier
String identifierRegex = "[^\\d][\\w]*";

// Postal code (Indian PIN — 6 digits, doesn't start with 0)
String pinRegex = "[1-9][0-9]{5}";

// Date (DD/MM/YYYY basic)
String dateRegex = "(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/(19|20)\\d{2}";

// URL
String urlRegex = "https?://[\\w.-]+(/[\\w./-]*)?";
```

### Find, Replace, Split with Regex

```java
String text = "Java 8 was released in 2014 and Java 11 in 2018";

// Find all years
Pattern yearPat = Pattern.compile("\\d{4}");
Matcher yearMatcher = yearPat.matcher(text);
while (yearMatcher.find()) {
    System.out.println("Year: " + yearMatcher.group()
        + " at position " + yearMatcher.start());
}

// Replace — mask all digits
String masked = text.replaceAll("\\d", "*");
System.out.println(masked);  // Java * was released in **** ...

// Replace multiple spaces with one
String cleaned = "too   many    spaces".replaceAll("\\s+", " ");

// Split on any delimiter
String csv = "one,two;;three,,four";
String[] parts = csv.split("[,;]+");   // ["one", "two", "three", "four"]

// Extract groups (capturing groups)
Pattern datePat = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
Matcher dateMatcher = datePat.matcher("Event on 2024-03-15");
if (dateMatcher.find()) {
    System.out.println("Year:  " + dateMatcher.group(1));  // 2024
    System.out.println("Month: " + dateMatcher.group(2));  // 03
    System.out.println("Day:   " + dateMatcher.group(3));  // 15
}

// Named capturing groups (Java 7+)
Pattern named = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
Matcher nm = named.matcher("2024-03-15");
if (nm.matches()) {
    System.out.println(nm.group("year"));   // 2024
    System.out.println(nm.group("month"));  // 03
}
```

### String Class Regex Methods

```java
String s = "Hello World 123";

s.matches("Hello.*");              // true — entire string must match
s.replaceAll("\\d+", "NUM");       // "Hello World NUM"
s.replaceFirst("\\s+", "_");       // "Hello_World 123"
s.split("\\s+");                   // ["Hello", "World", "123"]
```

---

## 3. Concurrency in Java

### The Problem with Raw Threads

Using `Thread` directly has drawbacks:

- Must manually create and manage each thread
- No return value from thread execution
- No easy way to manage a pool of reusable threads
- No built-in handling of task results or exceptions

```java
// Problem: manually creating N threads — not scalable
for (int i = 1; i <= 10; i++) {
    Thread t = new Thread(new MyTask());
    t.start();   // new OS thread created for every task — expensive
}
```

**Solution:** The `java.util.concurrent` package — Java 5+.

---

### The Executor Framework

The **Executor pattern** decouples **task submission** from **thread management**.

```
Your code submits tasks → ExecutorService manages threads → threads execute tasks
```

#### `Executor` Interface

The simplest abstraction — execute a `Runnable`:

```java
Executor executor = Executors.newSingleThreadExecutor();
executor.execute(() -> System.out.println("Running task"));
```

#### `ExecutorService` Interface

Extends `Executor` with lifecycle management and the ability to submit `Callable` tasks returning `Future` results.

```java
import java.util.concurrent.*;

ExecutorService service = Executors.newFixedThreadPool(4);

// Submit Runnable (no return value)
service.execute(() -> System.out.println("Task " + Thread.currentThread().getName()));

// Submit Callable (returns a Future)
Future<Integer> future = service.submit(() -> {
    int sum = 0;
    for (int i = 1; i <= 100; i++) sum += i;
    return sum;
});

System.out.println("Sum = " + future.get());   // blocks until result is ready

// Always shut down when done
service.shutdown();   // graceful — completes submitted tasks
// service.shutdownNow();   // immediate — interrupts running tasks
```

> **Important:** Always call `shutdown()` — otherwise non-daemon threads keep the JVM alive after `main()` exits.

---

### Types of Thread Pools

| Factory Method | Pool Type | Description | Use Case |
|---------------|-----------|-------------|---------|
| `newSingleThreadExecutor()` | 1 thread | Tasks execute sequentially, one at a time | Ordered task processing |
| `newFixedThreadPool(n)` | n threads | Fixed pool, tasks queue if all busy | CPU-bound tasks, n ≈ CPU cores |
| `newCachedThreadPool()` | Unlimited | Creates threads on demand, reuses idle ones | Short-lived async tasks |
| `newScheduledThreadPool(n)` | n threads | Supports delayed and periodic tasks | Scheduled jobs, timers |
| `newVirtualThreadPerTaskExecutor()` | Virtual threads (Java 21+) | Lightweight thread per task | I/O-bound, high-concurrency |

```java
// Fixed pool — good for CPU-bound work
ExecutorService fixed = Executors.newFixedThreadPool(
    Runtime.getRuntime().availableProcessors()
);

// Cached pool — good for short-lived I/O tasks
ExecutorService cached = Executors.newCachedThreadPool();

// Scheduled — run after a delay or at intervals
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

// Run once after 5 seconds
scheduler.schedule(() -> System.out.println("Delayed task"), 5, TimeUnit.SECONDS);

// Run every 10 seconds, starting after 0 second delay
scheduler.scheduleAtFixedRate(
    () -> System.out.println("Periodic task"),
    0, 10, TimeUnit.SECONDS
);
```

---

### `Runnable` vs `Callable`

| | `Runnable` | `Callable<V>` |
|-|-----------|-------------|
| Method | `void run()` | `V call() throws Exception` |
| Return value | None | Returns value of type V |
| Checked exceptions | Cannot throw | Can throw checked exceptions |
| Submission | `execute()` or `submit()` | `submit()` only |
| Result | No `Future` result | Returns `Future<V>` |

```java
// Runnable — fire and forget
Runnable task = () -> System.out.println("No return value");
executorService.execute(task);

// Callable — returns a result
Callable<Integer> sumTask = () -> {
    int total = 0;
    for (int i = 1; i <= 100; i++) total += i;
    return total;   // returns 5050
};

Future<Integer> future = executorService.submit(sumTask);
Integer result = future.get();   // blocks until done
System.out.println("Sum: " + result);   // Sum: 5050
```

---

### `Future<V>` Interface

A `Future` represents the **result of an asynchronous computation** — you get a ticket when you submit the task, and redeem it later for the result.

```java
ExecutorService es = Executors.newFixedThreadPool(3);

Future<String> f1 = es.submit(() -> { Thread.sleep(2000); return "Task 1 done"; });
Future<String> f2 = es.submit(() -> { Thread.sleep(1000); return "Task 2 done"; });
Future<String> f3 = es.submit(() -> { Thread.sleep(500);  return "Task 3 done"; });

// isDone() — non-blocking check
System.out.println("f3 done? " + f3.isDone());   // may be true

// get() — blocks until result is available
System.out.println(f3.get());   // "Task 3 done"
System.out.println(f2.get());   // "Task 2 done"
System.out.println(f1.get());   // "Task 1 done"

// get(timeout) — give up after specified time
try {
    String result = f1.get(3, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    f1.cancel(true);   // cancel if too slow
}

es.shutdown();
```

**Key `Future` methods:**

| Method | Description |
|--------|-------------|
| `get()` | Blocks and returns the result |
| `get(timeout, unit)` | Blocks for up to the given time |
| `isDone()` | Returns `true` if computation is complete |
| `isCancelled()` | Returns `true` if cancelled |
| `cancel(mayInterrupt)` | Attempts to cancel execution |

---

### `CompletableFuture` (Java 8+)

`CompletableFuture` is a powerful extension of `Future` that supports **non-blocking, composable async pipelines**:

```java
import java.util.concurrent.CompletableFuture;

// Simple async task
CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
    // runs in common pool
    return "Hello from async!";
});

// Chain operations (non-blocking)
cf.thenApply(s -> s.toUpperCase())         // transform result
  .thenAccept(s -> System.out.println(s))  // consume result
  .exceptionally(ex -> {                   // handle errors
      System.out.println("Error: " + ex.getMessage());
      return null;
  });

// Combine two futures
CompletableFuture<Integer> price = CompletableFuture.supplyAsync(() -> 100);
CompletableFuture<Integer> tax   = CompletableFuture.supplyAsync(() -> 18);

CompletableFuture<Integer> total = price.thenCombine(tax, Integer::sum);
System.out.println(total.get());   // 118

// Run all tasks in parallel and wait
CompletableFuture<Void> all = CompletableFuture.allOf(
    CompletableFuture.runAsync(() -> processA()),
    CompletableFuture.runAsync(() -> processB()),
    CompletableFuture.runAsync(() -> processC())
);
all.join();   // wait for all to complete
```

---

### Thread Safety and Synchronization

When multiple threads access shared mutable state, **race conditions** corrupt data.

```java
// UNSAFE — two threads may read the same value and both increment it
class Counter {
    private int count = 0;
    public void increment() { count++; }     // read-modify-write: NOT atomic
    public int get()        { return count; }
}
```

**Approaches to thread safety:**

#### 1. `synchronized` keyword

```java
class SafeCounter {
    private int count = 0;

    public synchronized void increment() { count++; }  // only one thread at a time
    public synchronized int get()        { return count; }
}

// Synchronized block (finer-grained)
public void increment() {
    synchronized(this) {
        count++;
    }
}
```

#### 2. `volatile` keyword

Ensures changes to a variable are **immediately visible** to all threads (no CPU cache). Only appropriate for single read/write operations — does NOT make compound operations atomic.

```java
class FlagHolder {
    private volatile boolean running = true;

    void stop()         { running = false; }       // write
    boolean isRunning() { return running; }         // read
}
```

#### 3. `java.util.concurrent.atomic` package

Lock-free, thread-safe operations on single variables using CAS (Compare-And-Swap):

```java
import java.util.concurrent.atomic.*;

AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();       // atomic increment
counter.getAndAdd(5);            // atomic add
counter.compareAndSet(10, 20);   // set to 20 only if current value is 10

AtomicLong    longVal  = new AtomicLong(0);
AtomicBoolean flag     = new AtomicBoolean(false);
AtomicReference<String> ref = new AtomicReference<>("initial");
```

#### 4. `Lock` interface (`java.util.concurrent.locks`)

More flexible than `synchronized` — supports tryLock, timed lock, interruptible lock:

```java
import java.util.concurrent.locks.*;

ReentrantLock lock = new ReentrantLock();

public void increment() {
    lock.lock();
    try {
        count++;
    } finally {
        lock.unlock();   // always unlock in finally
    }
}

// Try to acquire without blocking
if (lock.tryLock()) {
    try {
        // critical section
    } finally {
        lock.unlock();
    }
} else {
    // couldn't acquire — do something else
}
```

---

### Concurrent Collections

Standard collections (`ArrayList`, `HashMap`, etc.) are **not thread-safe**. Java provides thread-safe alternatives:

| Thread-safe collection | Description |
|------------------------|-------------|
| `ConcurrentHashMap<K,V>` | Thread-safe hash map — segments allow concurrent reads/writes |
| `CopyOnWriteArrayList<E>` | Thread-safe list — writes make a copy, reads are lock-free |
| `CopyOnWriteArraySet<E>` | Thread-safe set backed by `CopyOnWriteArrayList` |
| `ConcurrentLinkedQueue<E>` | Lock-free thread-safe FIFO queue |
| `BlockingQueue<E>` | Thread-safe queue that blocks on empty dequeue / full enqueue |
| `ArrayBlockingQueue<E>` | Bounded blocking queue backed by array |
| `LinkedBlockingQueue<E>` | Optionally bounded blocking queue backed by linked list |

```java
import java.util.concurrent.*;

// ConcurrentHashMap — best replacement for synchronized HashMap
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("key", 1);
map.computeIfAbsent("key2", k -> k.length());  // atomic operation

// CopyOnWriteArrayList — safe for read-heavy, rare-write scenarios
CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
list.add(1);

// Two threads: one adds, one iterates — no ConcurrentModificationException
Thread writer = new Thread(() -> {
    for (int i = 0; i <= 10; i++) { list.add(i); }
});
Thread reader = new Thread(() -> {
    for (Integer item : list) { System.out.println(item); }
});
writer.start();
reader.start();

// BlockingQueue — classic producer-consumer pattern
BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

// Producer
new Thread(() -> {
    try {
        queue.put("item1");   // blocks if queue is full
        queue.put("item2");
    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
}).start();

// Consumer
new Thread(() -> {
    try {
        String item = queue.take();   // blocks if queue is empty
        System.out.println("Consumed: " + item);
    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
}).start();
```

---

### Complete Executor Pattern Example

```java
import java.util.concurrent.*;
import java.util.*;

public class ConcurrencyDemo {

    public static void main(String[] args) throws Exception {

        // Task that returns a result
        Callable<Integer> sumTask = () -> {
            int sum = 0;
            for (int i = 1; i <= 10; i++) sum += i;
            System.out.println(Thread.currentThread().getName() + " computed: " + sum);
            return sum;
        };

        // Fixed pool with 3 threads
        ExecutorService pool = Executors.newFixedThreadPool(3);

        // Submit multiple tasks, collect futures
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futures.add(pool.submit(sumTask));
        }

        // Collect all results
        int grandTotal = 0;
        for (Future<Integer> f : futures) {
            grandTotal += f.get();   // blocks per future
        }

        System.out.println("Grand total: " + grandTotal);   // 55 × 5 = 275

        // invokeAll — submit all and get all results at once
        List<Future<Integer>> all = pool.invokeAll(
            List.of(sumTask, sumTask, sumTask)
        );

        // invokeAny — return first successful result, cancel others
        Integer first = pool.invokeAny(
            List.of(sumTask, sumTask, sumTask)
        );
        System.out.println("First result: " + first);

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}
```

---

### Concurrency Utilities Summary

| Utility | Package | Purpose |
|---------|---------|---------|
| `CountDownLatch` | `j.u.c` | Wait for N events to occur before proceeding |
| `CyclicBarrier` | `j.u.c` | Multiple threads meet at a barrier point |
| `Semaphore` | `j.u.c` | Limit concurrent access to a resource |
| `Exchanger` | `j.u.c` | Two threads swap data at a synchronization point |
| `Phaser` | `j.u.c` | Flexible multi-phase synchronizer |

```java
// CountDownLatch — wait for 3 services to initialize
CountDownLatch latch = new CountDownLatch(3);

for (int i = 0; i < 3; i++) {
    final int svc = i;
    new Thread(() -> {
        System.out.println("Service " + svc + " started");
        latch.countDown();   // decrement counter
    }).start();
}

latch.await();   // main thread waits until count reaches 0
System.out.println("All services ready!");

// Semaphore — limit to 3 concurrent database connections
Semaphore dbSemaphore = new Semaphore(3);

void queryDatabase() throws InterruptedException {
    dbSemaphore.acquire();       // block if 3 threads already hold permits
    try {
        // database work
    } finally {
        dbSemaphore.release();   // always release
    }
}
```

---

## Interview Questions

### Annotations

1. What is an annotation in Java? What is it used for?
2. What is the difference between `@Override`, `@Deprecated`, and `@SuppressWarnings`?
3. What are meta-annotations? Name all four.
4. What is `@Retention`? What are the three retention policies?
5. What is `@Target`? Give three examples of `ElementType`.
6. How do you define a custom annotation in Java?
7. How can you read annotation values at runtime?
8. What is `@FunctionalInterface` and why is it useful?
9. What annotations does JUnit use for testing?
10. What annotations does Spring use for dependency injection?

### Regular Expressions

11. What is a regular expression? What is it used for in Java?
12. What are the three main classes in `java.util.regex`?
13. What is the difference between `matches()`, `lookingAt()`, and `find()`?
14. What does `\d`, `\w`, and `\s` match?
15. What is the difference between `+`, `*`, and `?` quantifiers?
16. What is the difference between greedy, reluctant, and possessive quantifiers?
17. What is a capturing group? How do you access group values?
18. What is a lookahead in regex? Give an example.
19. Write a regex for a valid email address.
20. Write a regex for an Indian mobile number.

### Concurrency

21. What are the problems with using raw `Thread` directly?
22. What is the `Executor` pattern and why is it preferred?
23. What is the difference between `execute()` and `submit()`?
24. What is the difference between `Runnable` and `Callable`?
25. What is a `Future`? How do you get the result?
26. What are the four types of thread pools provided by `Executors`?
27. What is `CompletableFuture` and how is it different from `Future`?
28. What is a race condition? How do you prevent it?
29. What is the difference between `synchronized`, `volatile`, and `AtomicInteger`?
30. What is `ConcurrentHashMap` and how is it different from `Hashtable`?
31. What is `CopyOnWriteArrayList`? When would you use it?
32. What is `BlockingQueue`? What is the producer-consumer pattern?
33. What is `CountDownLatch` and when do you use it?
34. What is a `Semaphore`?
35. Why must `shutdown()` always be called on an `ExecutorService`?

---

## Assignments

### Annotations

1. Create a custom annotation `@ApiVersion(version = "1.0", author = "Alice")` with `RUNTIME` retention and `TYPE` target. Apply it to a class and read its values using the Reflection API.

2. Create a custom annotation `@NotNull` that can be applied to method parameters. Write a utility that uses reflection to check if any parameter annotated with `@NotNull` receives a `null` value and throws `IllegalArgumentException`.

3. Create a custom annotation `@LogExecutionTime` for methods. Using a proxy or reflection, print the method name and how long it took to execute.

### Regular Expressions

4. Write regex-based validator methods for:
   - Email addresses
   - Indian mobile numbers (starts with 6–9, 10 digits)
   - Strong passwords (min 8 chars, at least 1 uppercase, 1 digit, 1 special character)
   - Indian PIN codes (6 digits, not starting with 0)
   - Dates in DD/MM/YYYY format

5. Write a program that reads a block of text and:
   - Extracts all URLs
   - Extracts all email addresses
   - Counts occurrences of each word
   - Masks all digits with `*`

6. Write a log file parser using regex that extracts the timestamp, log level, and message from lines like: `2024-03-15 10:32:45 [ERROR] Database connection failed`

### Concurrency

7. Implement the **producer-consumer pattern** using `BlockingQueue`:
   - Producer thread generates 20 integers (1–20) with a 100ms delay
   - Two consumer threads read and print them

8. Write a program that submits 10 `Callable<Integer>` tasks to a `FixedThreadPool(4)`, each computing the square of its index, and prints the total sum of all results.

9. Rewrite the Lab 8 multithreading exercises (file copy thread, timer) using `ExecutorService` and `ScheduledExecutorService` instead of raw `Thread`.

10. Write a program that uses `CompletableFuture` to:
    - Fetch a "user" asynchronously (simulate with `Thread.sleep`)
    - Then fetch the user's "orders" asynchronously
    - Then combine both results and print a summary

11. Write a thread-safe `BankAccount` class using `ReentrantLock` that allows concurrent deposits and withdrawals. Test it by running 100 threads that each make 10 transactions.
