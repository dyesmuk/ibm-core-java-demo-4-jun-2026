# Core Java Courseware — Module 5: Java 8 Features, GUI, Enums & Modern Java

> **Author:** Vaman Deshmukh | **Updated & Modernized:** 2024
> **Covers:** Core Java 12, Other Topics — Lambda, Stream API, Functional Interfaces, Enums, GUI (AWT/Swing), Regex, Networking, Java 8–21 Highlights

---

## Table of Contents

- [1. Java Enums](#1-java-enums)
- [2. Lambda Expressions](#2-lambda-expressions)
- [3. Functional Interfaces](#3-functional-interfaces)
- [4. Stream API](#4-stream-api)
- [5. Optional](#5-optional)
- [6. GUI Programming (AWT and Swing)](#6-gui-programming-awt-and-swing)
- [7. Regular Expressions](#7-regular-expressions)
- [8. Network Programming](#8-network-programming)
- [9. Reflection API](#9-reflection-api)
- [10. Modern Java Features (9–21 Highlights)](#10-modern-java-features-921-highlights)
- [Interview Questions](#interview-questions)
- [Assignments](#assignments)

---

## 1. Java Enums

### What Is an Enum?

An **enum** (enumeration) is a special data type for defining a fixed set of named constants. Use enums whenever you have a variable that can only take one of a predefined set of values.

```java
public enum Day {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
}

// Usage
Day today = Day.MONDAY;
System.out.println(today);           // MONDAY
System.out.println(today.ordinal()); // 1 (0-based position)
System.out.println(today.name());    // "MONDAY"

// Iterate all values
for (Day d : Day.values()) {
    System.out.println(d);
}

// Switch on enum
switch (today) {
    case MONDAY:
    case TUESDAY:
    case WEDNESDAY:
    case THURSDAY:
    case FRIDAY:
        System.out.println("Weekday"); break;
    default:
        System.out.println("Weekend");
}
```

### Enum with Fields and Methods

Enums can have **constructors, fields, and methods**:

```java
public enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS  (4.869e+24, 6.0518e6),
    EARTH  (5.976e+24, 6.37814e6),
    MARS   (6.421e+23, 3.3972e6);

    private final double mass;     // kilograms
    private final double radius;   // meters

    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    double surfaceGravity() {
        final double G = 6.67300E-11;
        return G * mass / (radius * radius);
    }

    double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
}

double earthWeight = 75.0;
double mass = earthWeight / Planet.EARTH.surfaceGravity();
for (Planet p : Planet.values()) {
    System.out.printf("Your weight on %s is %6.2f%n", p, p.surfaceWeight(mass));
}
```

### Enum with State Code Example

```java
public enum State {
    MAHARASHTRA("MH"), TELANGANA("TS"), KARNATAKA("KA"), OTHER("OS");

    public final String code;

    State(String code) { this.code = code; }

    public String getCode() { return code; }
}

// Usage
State s = State.KARNATAKA;
System.out.println(s.getCode());   // KA

// From code to enum (reverse lookup)
State findByCode(String code) {
    for (State st : State.values()) {
        if (st.code.equals(code)) return st;
    }
    return State.OTHER;
}
```

### Enum Best Practices

- Use enums instead of `int` or `String` constants for type safety
- Enums are **implicitly `public static final`**
- Enums can implement interfaces but cannot extend classes (already extend `java.lang.Enum`)
- `EnumSet` and `EnumMap` provide highly efficient implementations for enums

---

## 2. Lambda Expressions

### What Is a Lambda Expression?

A **lambda expression** is an **anonymous function** — a concise way to represent a single-method interface (functional interface) implementation.

```
Traditional anonymous class        Lambda equivalent
────────────────────────────────   ──────────────────────
new Runnable() {                   () -> System.out.println("Running!")
    public void run() {
        System.out.println("Running!");
    }
}
```

### Lambda Syntax

```
(parameters) -> expression
(parameters) -> { statements; }
```

```java
// No parameters, no return
() -> System.out.println("Hello!")

// One parameter (parentheses optional)
name -> System.out.println("Hello, " + name)

// Multiple parameters
(a, b) -> a + b

// Multiple statements
(x, y) -> {
    int sum = x + y;
    return sum;
}
```

### Lambda Evolution Example

```java
interface Greeter {
    void greet(String name);
}

// 1. Concrete class
class FormalGreeter implements Greeter {
    public void greet(String name) { System.out.println("Good day, " + name); }
}

// 2. Anonymous inner class
Greeter anon = new Greeter() {
    public void greet(String name) { System.out.println("Hello, " + name); }
};

// 3. Lambda expression (cleanest)
Greeter lambda = name -> System.out.println("Hi, " + name + "!");

// Usage
lambda.greet("Alice");
```

### Practical Lambda Examples

```java
// Sort a list
List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
names.sort((a, b) -> a.compareTo(b));          // ascending
names.sort((a, b) -> b.compareTo(a));          // descending

// Thread
new Thread(() -> System.out.println("Running in a thread!")).start();

// Event listener (Swing)
button.addActionListener(e -> System.out.println("Button clicked!"));

// Comparator
people.sort(Comparator.comparing(Person::getName));
people.sort(Comparator.comparing(Person::getAge).reversed());
```

### Method References

A shorthand for lambdas that call a single method:

```java
// Lambda                             Method reference
s -> System.out.println(s)          System.out::println
s -> s.toUpperCase()                String::toUpperCase
() -> new ArrayList<>()             ArrayList::new
n -> Math.abs(n)                    Math::abs

// Usage
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(System.out::println);   // method reference

// Constructors
Supplier<ArrayList<String>> listFactory = ArrayList::new;
ArrayList<String> list = listFactory.get();
```

---

## 3. Functional Interfaces

A **functional interface** has exactly **one abstract method** (SAM — Single Abstract Method). It can have `default` and `static` methods.

Mark with `@FunctionalInterface` annotation (optional but recommended — compiler verifies).

### Key Built-in Functional Interfaces (`java.util.function`)

| Interface | Method | Description | NANR/NAWR... |
|-----------|--------|-------------|--------------|
| `Runnable` | `void run()` | No input, no output | NANR |
| `Supplier<T>` | `T get()` | No input, returns T | NAWR |
| `Consumer<T>` | `void accept(T)` | Takes T, no output | WANR |
| `Function<T,R>` | `R apply(T)` | Takes T, returns R | WAWR |
| `Predicate<T>` | `boolean test(T)` | Takes T, returns boolean | WAWR(boolean) |
| `BiFunction<T,U,R>` | `R apply(T,U)` | Takes 2 args, returns R | |
| `UnaryOperator<T>` | `T apply(T)` | Takes T, returns T | |
| `BinaryOperator<T>` | `T apply(T,T)` | Takes 2 T, returns T | |

### Examples

```java
import java.util.function.*;

// Supplier — provides a value
Supplier<String> timestamp = () -> LocalDateTime.now().toString();
System.out.println(timestamp.get());

// Consumer — consumes a value
Consumer<String> printer = s -> System.out.println(">> " + s);
printer.accept("Hello");

// Function — transforms a value
Function<String, Integer> lengthOf = s -> s.length();
System.out.println(lengthOf.apply("Java"));   // 4

// Predicate — tests a condition
Predicate<Integer> isEven = n -> n % 2 == 0;
System.out.println(isEven.test(10));   // true
System.out.println(isEven.test(7));    // false

// Combining predicates
Predicate<Integer> isPositive = n -> n > 0;
Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
Predicate<Integer> isEvenOrNegative = isEven.or(isPositive.negate());

// Function composition
Function<Integer, Integer> times2  = x -> x * 2;
Function<Integer, Integer> plus10  = x -> x + 10;
Function<Integer, Integer> times2ThenPlus10 = times2.andThen(plus10);
System.out.println(times2ThenPlus10.apply(5));  // (5*2)+10 = 20
```

---

## 4. Stream API

The **Stream API** (`java.util.stream`) enables **functional-style operations** on collections and arrays.

> A stream is NOT a data structure — it is a **pipeline** that processes data from a source.

### Stream Characteristics

- **Doesn't store data** — processes on-the-fly
- **Lazy evaluation** — operations are executed only when terminal operation is called
- **Immutable** — operations don't modify the source
- **Consumable** — can be traversed only once
- **Supports parallelism** — `parallelStream()`

### Stream Pipeline

```
Source → Intermediate Operations (lazy) → Terminal Operation (triggers execution)
```

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

int sumOfEvenSquares = numbers.stream()     // source
    .filter(n -> n % 2 == 0)               // intermediate
    .map(n -> n * n)                        // intermediate
    .reduce(0, Integer::sum);               // terminal

System.out.println(sumOfEvenSquares);       // 220
```

### Creating Streams

```java
// From collection
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> s1 = list.stream();

// From array
String[] arr = {"x", "y", "z"};
Stream<String> s2 = Arrays.stream(arr);

// From values
Stream<Integer> s3 = Stream.of(1, 2, 3, 4, 5);

// IntStream / LongStream / DoubleStream (primitive specializations)
IntStream range = IntStream.range(1, 11);       // 1 to 10
IntStream closed = IntStream.rangeClosed(1, 10); // 1 to 10 inclusive
```

### Intermediate Operations (return a Stream)

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Anna", "Brian");

// filter — keep elements matching predicate
names.stream().filter(n -> n.startsWith("A"));   // [Alice, Anna]

// map — transform each element
names.stream().map(String::toUpperCase);          // [ALICE, BOB, ...]

// flatMap — flatten nested streams
List<List<Integer>> nested = Arrays.asList(
    Arrays.asList(1, 2), Arrays.asList(3, 4));
nested.stream().flatMap(Collection::stream);     // [1, 2, 3, 4]

// sorted — sort elements
names.stream().sorted();                         // alphabetical
names.stream().sorted(Comparator.reverseOrder()); // reverse

// distinct — remove duplicates
Stream.of(1, 2, 2, 3, 3, 4).distinct();         // [1, 2, 3, 4]

// limit — take first n elements
names.stream().limit(3);                         // [Alice, Bob, Charlie]

// skip — skip first n elements
names.stream().skip(2);                          // [Charlie, Anna, Brian]

// peek — debug without consuming
names.stream()
    .peek(n -> System.out.println("Before: " + n))
    .map(String::toUpperCase)
    .peek(n -> System.out.println("After: " + n))
    .collect(Collectors.toList());
```

### Terminal Operations (trigger execution)

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Anna");

// collect — gather into a collection
List<String> aNames = names.stream()
    .filter(n -> n.startsWith("A"))
    .collect(Collectors.toList());          // [Alice, Anna]

// toSet, toMap
Set<String> nameSet = names.stream().collect(Collectors.toSet());

Map<Integer, List<String>> byLength = names.stream()
    .collect(Collectors.groupingBy(String::length));
// {5=[Alice, Brian], 3=[Bob, Anna], 7=[Charlie]}

// count
long count = names.stream().filter(n -> n.length() > 4).count();  // 2

// findFirst / findAny
Optional<String> first = names.stream().filter(n -> n.startsWith("A")).findFirst();
first.ifPresent(System.out::println);   // Alice

// anyMatch / allMatch / noneMatch
boolean anyA = names.stream().anyMatch(n -> n.startsWith("A"));  // true
boolean allA = names.stream().allMatch(n -> n.startsWith("A"));  // false
boolean noneZ = names.stream().noneMatch(n -> n.startsWith("Z")); // true

// reduce — aggregate to single value
int sum = IntStream.rangeClosed(1, 10).reduce(0, Integer::sum);  // 55
Optional<String> longest = names.stream()
    .reduce((a, b) -> a.length() >= b.length() ? a : b);         // "Charlie"

// forEach — perform action for each
names.forEach(System.out::println);

// min / max
Optional<String> shortest = names.stream()
    .min(Comparator.comparing(String::length));

// toArray
String[] arr = names.stream().toArray(String[]::new);
```

### Comprehensive Employee Example

```java
class Employee {
    String name;
    String department;
    double salary;
    LocalDate hireDate;

    // constructor, getters...
}

List<Employee> employees = getEmployees(); // assume populated

// Sum of all salaries
double totalSalary = employees.stream()
    .mapToDouble(Employee::getSalary)
    .sum();

// Average salary
OptionalDouble avgSalary = employees.stream()
    .mapToDouble(Employee::getSalary)
    .average();

// Employees with salary > 50000
List<Employee> highEarners = employees.stream()
    .filter(e -> e.getSalary() > 50000)
    .collect(Collectors.toList());

// Names of all employees, sorted
List<String> sortedNames = employees.stream()
    .map(Employee::getName)
    .sorted()
    .collect(Collectors.toList());

// Count by department
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));

// Highest paid employee
Optional<Employee> topEarner = employees.stream()
    .max(Comparator.comparingDouble(Employee::getSalary));

// Salary increased by 15%
List<Double> newSalaries = employees.stream()
    .map(e -> e.getSalary() * 1.15)
    .collect(Collectors.toList());

// Parallel stream (for large datasets)
double total = employees.parallelStream()
    .mapToDouble(Employee::getSalary)
    .sum();
```

---

## 5. Optional

`Optional<T>` is a container for a value that may or may not be present. Introduced in Java 8 to eliminate `NullPointerException`.

```java
import java.util.Optional;

// Create
Optional<String> empty = Optional.empty();
Optional<String> present = Optional.of("Hello");
Optional<String> nullable = Optional.ofNullable(mayBeNull);

// Check and get
if (present.isPresent()) {
    System.out.println(present.get());
}

// Functional style (preferred)
present.ifPresent(s -> System.out.println("Value: " + s));

// Default value
String val = nullable.orElse("default");
String computed = nullable.orElseGet(() -> computeDefault());

// Throw if empty
String required = nullable.orElseThrow(() ->
    new IllegalStateException("Value is required"));

// Transform
Optional<Integer> length = present.map(String::length);

// Chain
Optional<String> result = Optional.ofNullable(getUser())
    .map(User::getAddress)
    .map(Address::getCity)
    .filter(city -> city.startsWith("B"))
    .orElse("Unknown city");
```

---

## 6. GUI Programming (AWT and Swing)

### AWT (Abstract Window Toolkit)

AWT provides platform-native GUI components. Components look different on different OS.

**AWT Hierarchy:**
```
Object
└── Component
    ├── Button, Label, TextField, Checkbox, ...
    └── Container
        ├── Panel
        └── Window
            ├── Dialog
            └── Frame ← your application window
```

```java
import java.awt.*;
import java.awt.event.*;

public class AwtDemo extends Frame {

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(50, 50, 200, 100);

        g.setColor(Color.BLUE);
        g.fillOval(100, 200, 150, 150);

        g.setColor(Color.BLACK);
        g.drawString("Hello AWT!", 80, 300);
    }

    public static void main(String[] args) {
        AwtDemo frame = new AwtDemo();
        frame.setTitle("AWT Demo");
        frame.setSize(400, 400);
        frame.setBackground(Color.YELLOW);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }
}
```

**Disadvantages of AWT:**
- Platform-dependent look and feel
- Heavyweight components (each maps to native OS widget)
- Limited component set
- Doesn't support MVC pattern cleanly

### Swing

Swing is built on top of AWT but provides **platform-independent** components written entirely in Java.

- **Lightweight** — drawn by Java, not OS
- **Consistent look** across platforms
- Supports **pluggable look-and-feel** (Metal, Windows, Nimbus, etc.)
- Rich component set
- Supports MVC architecture

**Swing Hierarchy:**
```
JComponent
├── JLabel, JButton, JTextField, JTextArea, JCheckBox, ...
└── JContainer (not explicit)
    └── JFrame (top-level window)
        └── ContentPane → LayeredPane → RootPane → GlassPane
```

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm {
    public static void main(String[] args) {
        // Always create Swing components on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    static void createAndShowGUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 200);

        JPanel panel = new JPanel(null);  // null layout — manual positioning

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(10, 50, 80, 25);
        panel.add(passLabel);

        JPasswordField passText = new JPasswordField(20);
        passText.setBounds(100, 50, 165, 25);
        panel.add(passText);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(10, 80, 80, 25);

        // Action listener with lambda
        loginBtn.addActionListener(e -> {
            String user = userText.getText();
            String pass = new String(passText.getPassword());
            if ("admin".equals(user) && "1234".equals(pass)) {
                JOptionPane.showMessageDialog(frame, "Welcome, " + user + "!");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(loginBtn);

        frame.add(panel);
        frame.setVisible(true);
    }
}
```

### Common Swing Components

| Component | Use |
|-----------|-----|
| `JFrame` | Top-level window |
| `JPanel` | Container for organizing components |
| `JLabel` | Non-editable text or image |
| `JButton` | Clickable button |
| `JTextField` | Single-line text input |
| `JPasswordField` | Masked text input |
| `JTextArea` | Multi-line text |
| `JCheckBox` | Toggle checkbox |
| `JRadioButton` | Mutually exclusive selection (use `ButtonGroup`) |
| `JComboBox` | Dropdown list |
| `JList` | Scrollable list |
| `JTable` | Tabular data display |
| `JMenu`, `JMenuBar`, `JMenuItem` | Menus |
| `JOptionPane` | Pre-built dialogs (message, input, confirm) |

### Layouts

| Layout | Description |
|--------|-------------|
| `FlowLayout` | Left-to-right, wrap to next line |
| `BorderLayout` | Five regions: NORTH, SOUTH, EAST, WEST, CENTER |
| `GridLayout` | Equal-sized grid of cells |
| `GridBagLayout` | Flexible grid with constraints |
| `BoxLayout` | Horizontal or vertical stack |
| `null` | Manual absolute positioning with `setBounds()` |

> **Modern Note:** For new desktop Java apps, consider **JavaFX** (modern, CSS-styled, FXML-based) instead of Swing. JavaFX supports rich animations, 3D, and modern UI patterns.

---

## 7. Regular Expressions

Regular expressions (**regex**) define patterns for searching, validating, or manipulating strings.

### Java Regex API (`java.util.regex`)

```java
import java.util.regex.*;

// Test if a string matches a pattern
Pattern pattern = Pattern.compile("[0-9]+");
Matcher matcher = pattern.matcher("abc123");
System.out.println(matcher.find());    // true (found digits)

// String.matches() — quick check (entire string must match)
System.out.println("12345".matches("[0-9]+"));    // true
System.out.println("abc123".matches("[0-9]+"));   // false (letters present)
```

### Common Regex Patterns

```java
// Validate email
String email = "user@example.com";
boolean valid = email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

// Validate Indian phone number (starts with 6-9, 10 digits total)
String phone = "9876543210";
boolean phoneOk = phone.matches("[6-9][0-9]{9}");

// Validate strong password
String pwd = "MyP@ss123";
boolean strongPwd = pwd.matches("(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%]).{8,}");

// Find all numbers in a string
Pattern numPattern = Pattern.compile("\\d+");
Matcher m = numPattern.matcher("Order #123 contains 45 items worth $678");
while (m.find()) {
    System.out.println(m.group());   // 123, 45, 678
}

// Replace with regex
String cleaned = "hello   world".replaceAll("\\s+", " ");  // "hello world"
String noDigits = "abc123def456".replaceAll("\\d", "");    // "abcdef"

// Split with regex
String[] parts = "one,two;;three,,four".split("[,;]+");
// ["one", "two", "three", "four"]
```

### Regex Quick Reference

| Pattern | Matches |
|---------|---------|
| `.` | Any character except newline |
| `\d` | Digit [0-9] |
| `\D` | Non-digit |
| `\w` | Word character [a-zA-Z0-9_] |
| `\s` | Whitespace |
| `[abc]` | a, b, or c |
| `[^abc]` | Not a, b, or c |
| `[a-z]` | Any lowercase letter |
| `^` | Start of string |
| `$` | End of string |
| `*` | 0 or more |
| `+` | 1 or more |
| `?` | 0 or 1 |
| `{n}` | Exactly n times |
| `{n,m}` | n to m times |
| `(abc)` | Group |
| `a\|b` | a or b |

---

## 8. Network Programming

### Java Networking Basics

Java's `java.net` package provides classes for network communication using TCP/IP and UDP.

```java
import java.net.*;

// Get local IP
InetAddress local = InetAddress.getLocalHost();
System.out.println("Local IP: " + local.getHostAddress());

// Resolve hostname
InetAddress google = InetAddress.getByName("www.google.com");
System.out.println("Google IP: " + google.getHostAddress());

// URL details
URL url = new URL("https://docs.oracle.com/javase/8/docs/api/");
System.out.println("Protocol: " + url.getProtocol());  // https
System.out.println("Host: " + url.getHost());          // docs.oracle.com
System.out.println("Path: " + url.getPath());          // /javase/8/docs/api/
```

### Simple TCP Socket Example

```java
// Server
try (ServerSocket server = new ServerSocket(8080)) {
    System.out.println("Server waiting on port 8080...");
    try (Socket client = server.accept();
         BufferedReader in = new BufferedReader(
             new InputStreamReader(client.getInputStream()));
         PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
        String msg = in.readLine();
        System.out.println("Received: " + msg);
        out.println("Echo: " + msg);
    }
}

// Client
try (Socket socket = new Socket("localhost", 8080);
     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
     BufferedReader in = new BufferedReader(
         new InputStreamReader(socket.getInputStream()))) {
    out.println("Hello Server!");
    System.out.println("Server replied: " + in.readLine());
}
```

> **Modern Note:** For production networking, consider frameworks like **Netty** (async, high-performance) or use Java's `java.net.http.HttpClient` (Java 11+) for HTTP communication.

---

## 9. Reflection API

The **Reflection API** (`java.lang.reflect`) allows programs to examine and modify the behavior of classes, methods, fields, and constructors at **runtime**.

```java
import java.lang.reflect.*;

// Get class info
Class<?> clazz = Class.forName("java.util.ArrayList");
System.out.println("Class: " + clazz.getName());
System.out.println("Superclass: " + clazz.getSuperclass().getName());

// Get methods
Method[] methods = clazz.getDeclaredMethods();
for (Method m : methods) {
    System.out.println(m.getName());
}

// Invoke a method dynamically
Object list = clazz.getDeclaredConstructor().newInstance();
Method addMethod = clazz.getMethod("add", Object.class);
addMethod.invoke(list, "Hello");
System.out.println(list);   // [Hello]

// Read/write fields (even private ones)
class Secret { private String password = "12345"; }
Secret s = new Secret();
Field f = Secret.class.getDeclaredField("password");
f.setAccessible(true);   // bypass private
System.out.println(f.get(s));   // 12345
```

**Real-world uses of Reflection:**
- Frameworks (Spring, Hibernate use reflection heavily for dependency injection, ORM)
- IDEs (code completion, inspection)
- Test frameworks (JUnit annotations)
- Serialization/deserialization (Jackson, Gson)

> **Caution:** Reflection bypasses compile-time type checks and is slower than direct access. Use only when necessary.

---

## 10. Modern Java Features (9–21 Highlights)

### Java 9 — Modules, Collection Factories

```java
// Immutable collection factories
List<String> list = List.of("A", "B", "C");
Set<Integer> set = Set.of(1, 2, 3);
Map<String, Integer> map = Map.of("one", 1, "two", 2);
```

### Java 10 — var (Local Variable Type Inference)

```java
var names = new ArrayList<String>();   // compiler infers ArrayList<String>
var message = "Hello";                 // compiler infers String

// In for loops
for (var entry : map.entrySet()) {
    System.out.println(entry.getKey() + "=" + entry.getValue());
}
```

### Java 11 — String Methods, HttpClient

```java
// String methods
"  hello  ".strip();           // "hello" (Unicode-aware)
"  hello  ".isBlank();         // false
"ha".repeat(3);                // "hahaha"
"line1\nline2\nline3".lines().forEach(System.out::println);

// HttpClient (replaces HttpURLConnection)
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.github.com"))
    .build();
HttpResponse<String> response = client.send(request,
    HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
```

### Java 14 — Switch Expressions, Records (preview)

```java
// Switch expression (arrow syntax)
int day = 3;
String dayName = switch (day) {
    case 1 -> "Monday";
    case 2 -> "Tuesday";
    case 3 -> "Wednesday";
    default -> "Unknown";
};
```

### Java 16 — Records

Records are immutable data classes with auto-generated constructor, getters, `equals()`, `hashCode()`, `toString()`:

```java
record Point(int x, int y) { }

Point p = new Point(3, 4);
System.out.println(p.x());           // 3
System.out.println(p.y());           // 4
System.out.println(p);               // Point[x=3, y=4]

// Pattern matching instanceof
Object obj = "Hello World";
if (obj instanceof String s && s.length() > 5) {
    System.out.println("Long string: " + s.toUpperCase());
}
```

### Java 17 — Sealed Classes, Text Blocks

```java
// Text blocks (Java 15+)
String json = """
    {
        "name": "Alice",
        "age": 30
    }
    """;

// Sealed classes — restrict who can extend
sealed interface Shape permits Circle, Rectangle, Triangle { }
record Circle(double radius) implements Shape { }
record Rectangle(double w, double h) implements Shape { }
```

### Java 21 — Virtual Threads (Project Loom)

```java
// Virtual threads — lightweight threads managed by JVM
// Create millions of virtual threads without resource exhaustion
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 1_000_000; i++) {
        executor.submit(() -> {
            Thread.sleep(1000);
            return "done";
        });
    }
}
```

---

## Interview Questions

1. What is an enum? When would you use it over constants?
2. Can an enum have methods and constructors? Give an example.
3. What is a lambda expression? What are its parts?
4. What is a functional interface? Name five from `java.util.function`.
5. What is the difference between `Function`, `Consumer`, `Supplier`, and `Predicate`?
6. What is a method reference? Give the four types with examples.
7. What is the Stream API? How is it different from a `Collection`?
8. What is the difference between `map()` and `flatMap()`?
9. What is lazy evaluation in streams?
10. What is `Optional` and why was it introduced?
11. What is the difference between `findFirst()` and `findAny()`?
12. What is the difference between intermediate and terminal stream operations?
13. What is `Collectors.groupingBy()`?
14. What is the difference between AWT and Swing?
15. What is the Event Dispatch Thread (EDT) in Swing?
16. What is a lambda expression used for in GUI programming?
17. What are the advantages of `var` (Java 10)?
18. What is a `record` in Java 16+?
19. What are virtual threads (Java 21)?
20. What is the difference between `parallelStream()` and `stream()`?

---

## Assignments

1. Write lambda expressions for: (a) checking if a string is a palindrome, (b) computing factorial, (c) sorting a list of people by age then name.

2. Using the Stream API on a list of employees, write methods to: (a) find sum of all salaries, (b) list department names and employee count, (c) find the most senior employee, (d) employees with salary > 40000 sorted by name.

3. Using `Predicate`, write a method to validate a username: must end with `_job`, must have at least 8 characters before `_job`.

4. Write a Swing login form that accepts username and password, validates them, and shows appropriate messages using `JOptionPane`.

5. Write a regex validator for: (a) Indian phone numbers, (b) email addresses, (c) passwords (min 8 chars, 1 uppercase, 1 digit, 1 special char).

6. Create an enum `Season` with values `SPRING`, `SUMMER`, `AUTUMN`, `WINTER`, each with a `getDescription()` method. Use it in a switch expression.

7. Refactor a traditional anonymous class-based `Comparator` to use a lambda, then to use a method reference.
