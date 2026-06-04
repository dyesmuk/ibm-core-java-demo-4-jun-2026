# Core Java Courseware — Module 2: OOP and Class Design

> **Author:** Vaman Deshmukh | **Updated & Modernized:** 2024
> **Covers:** Core Java 04, 05, 06 — OOP Concepts, Classes & Objects, Access Modifiers, Packages

---

## Table of Contents

- [1. Object-Oriented Programming (OOP)](#1-object-oriented-programming-oop)
- [2. Classes and Objects](#2-classes-and-objects)
- [3. Class Members](#3-class-members)
- [4. Static vs Non-Static Members](#4-static-vs-non-static-members)
- [5. Constructors](#5-constructors)
- [6. The `this` Keyword](#6-the-this-keyword)
- [7. JVM Memory Architecture](#7-jvm-memory-architecture)
- [8. Access Modifiers](#8-access-modifiers)
- [9. Packages](#9-packages)
- [10. Useful Utility Classes](#10-useful-utility-classes)
- [Interview Questions](#interview-questions)
- [Assignments](#assignments)

---

## 1. Object-Oriented Programming (OOP)

OOP is a programming paradigm where the solution to a problem is modeled as a collection of **objects** that interact with each other.

### The Four Pillars of OOP

#### Encapsulation

Wrapping **data (fields)** and **behavior (methods)** into a single unit (a class), and **restricting direct access** to internal state.

```java
public class BankAccount {
    private double balance;   // hidden from outside

    public double getBalance() {       // controlled access
        return balance;
    }
    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }
}
```

- **What:** Bundling data + methods together
- **How:** Using classes + access modifiers (`private`)
- **Why:** Protects internal state; allows validation before modification

#### Abstraction

Hiding **implementation details** and exposing only the **essential interface**.

```java
// User only knows what the method does, not how
List<String> names = new ArrayList<>();
names.add("Alice");    // how it's stored internally — hidden
names.get(0);          // user just uses the interface
```

- Achieved using `abstract` classes and `interface`s
- **Abstraction** = "What" a class can do
- **Encapsulation** = "How" it achieves that

#### Inheritance

A mechanism where a **child class (subclass)** derives properties and behavior from a **parent class (superclass)**.

```java
class Animal {
    void eat() { System.out.println("Eating..."); }
}

class Dog extends Animal {    // Dog inherits eat()
    void bark() { System.out.println("Barking..."); }
}
```

- Keyword: `extends` (class inheritance), `implements` (interface)
- Promotes **code reuse**
- Java supports **single inheritance** for classes (but multiple interface implementation)

#### Polymorphism

An object's ability to take **many forms** — the same method name behaves differently depending on context.

```java
// Method Overloading (compile-time polymorphism)
class Calculator {
    int add(int a, int b)          { return a + b; }
    double add(double a, double b) { return a + b; }
}

// Method Overriding (runtime polymorphism)
class Shape {
    void draw() { System.out.println("Drawing shape"); }
}
class Circle extends Shape {
    @Override
    void draw() { System.out.println("Drawing circle"); }
}

Shape s = new Circle();
s.draw();   // prints "Drawing circle" — resolved at runtime
```

### Advantages of OOP

- Code reusability through inheritance
- Extensibility without modifying existing code (Open/Closed Principle)
- Easier maintenance via encapsulation
- Modularity — independent development of components
- Real-world modeling

---

## 2. Classes and Objects

### Object

An object is a **real-world entity** with:
1. **Identity** — a unique name/address
2. **Properties** — attributes/data (fields)
3. **Behavior** — what it can do (methods)

Examples: `Employee`, `Car`, `BankAccount`, `Student`

### Class

A class is a **blueprint (template)** for creating objects. It defines the structure (fields) and behavior (methods) that all objects of that type share.

```
Class = Blueprint
Object = Instance built from the blueprint
```

One class → many objects:

```java
Employee emp1 = new Employee(101, "Alice", 75000);
Employee emp2 = new Employee(102, "Bob", 82000);
// Both are different objects of the same Employee class
```

### Class Syntax

```java
public class ClassName {
    // Fields (instance variables)
    dataType fieldName;

    // Constructor
    ClassName(parameters) {
        // initialize fields
    }

    // Methods
    returnType methodName(parameters) {
        // method body
    }
}
```

### A Complete Example

```java
public class Employee {
    // Fields
    int eid;
    String ename;
    double esal;

    // Constructor
    Employee(int eid, String ename, double esal) {
        this.eid = eid;
        this.ename = ename;
        this.esal = esal;
    }

    // Method
    void display() {
        System.out.println("ID: " + eid + ", Name: " + ename + ", Salary: " + esal);
    }

    public static void main(String[] args) {
        Employee e1 = new Employee(101, "Alice", 90000);
        Employee e2 = new Employee(102, "Bob",   80000);
        e1.display();
        e2.display();
    }
}
```

---

## 3. Class Members

A Java class can contain:

| Member | Description |
|--------|-------------|
| **Fields** (Variables) | Store state/data |
| **Methods** | Define behavior |
| **Blocks** | Execute automatically (no explicit call) |
| **Constructors** | Initialize objects |

Fields, methods, and blocks can each be **static** or **non-static**.

---

## 4. Static vs Non-Static Members

Every Java application has two contexts:
- **Static context** — common to all objects (like a bank's branch name)
- **Non-static (instance) context** — specific to each object (like a customer's account number)

### Static Variables

Declared with `static` keyword; shared by **all instances** of the class.

```java
class Bank {
    static String bankName = "HDFC";   // same for all accounts
    int accountNo;                     // unique per object
    String holderName;
}
```

- Initialized at **class loading** time
- Accessed as `ClassName.variableName`

### Non-Static Variables

Belong to a specific object. Different objects have their own copy.

```java
Employee e1 = new Employee();
Employee e2 = new Employee();
e1.salary = 50000;   // only e1's salary
e2.salary = 60000;   // only e2's salary
```

### Static Methods

Belong to the class, not any object. Can be called without creating an object.

```java
class MathUtils {
    static int square(int n) { return n * n; }
}
// Call without creating object
int result = MathUtils.square(5);   // 25
```

**Rules for static methods:**
- Cannot use `this` keyword
- Cannot access non-static fields/methods directly
- Can only access static members of the class

### Non-Static Methods

Belong to a specific object. Must be called on an object reference.

```java
class Counter {
    int count = 0;
    void increment() { count++; }     // non-static
    int getCount()   { return count; }
}

Counter c = new Counter();
c.increment();
System.out.println(c.getCount());   // 1
```

### Static Blocks

A block of code that runs **once automatically** when the class is first loaded — before `main()`.

```java
public class Program {
    static {
        System.out.println("--block 1--");   // runs first
    }

    public static void main(String[] args) {
        System.out.println("--main--");      // runs second
    }

    static {
        System.out.println("--block 2--");   // between block 1 and main
    }
}
// Output:
// --block 1--
// --block 2--
// --main--
```

**Use case:** Loading configuration, database connections, or class-level initialization.

### Non-Static Blocks

Runs **every time** an object is created, before the constructor.

```java
class Test {
    {
        System.out.println("Non-static block");   // runs on each new Test()
    }
    Test() {
        System.out.println("Constructor");
    }
}
```

**Execution order:**
1. Static block (once at class loading)
2. Non-static block (on each object creation)
3. Constructor (on each object creation, after non-static block)

### Method Types (by argument/return)

| Type | Signature | Use Case |
|------|-----------|----------|
| NANR (No Arg, No Return) | `void m()` | Print, side effects |
| NAWR (No Arg, With Return) | `int m()` | Compute/return constant |
| WANR (With Arg, No Return) | `void m(int x)` | Update state |
| WAWR (With Arg, With Return) | `int m(int x, int y)` | Process and return |

```java
class MethodDemo {
    static void nanr()               { System.out.println("No arg, no return"); }
    static int  nawr()               { return 100; }
    static void wanr(int x, int y)   { System.out.println(x + y); }
    static int  wawr(int x, int y)   { return x + y; }
}
```

---

## 5. Constructors

A constructor is a **special method** that:
- Has the **same name as the class**
- Has **no return type** (not even `void`)
- Is called automatically when an object is created with `new`
- Is used to **initialize non-static variables**

```java
class BankAccount {
    int accNo;
    String holder;
    double balance;

    // Parameterized constructor
    BankAccount(int accNo, String holder, double balance) {
        this.accNo = accNo;
        this.holder = holder;
        this.balance = balance;
    }
}

BankAccount acc = new BankAccount(1001, "Alice", 5000.0);
```

### Default Constructor

If you define **no constructor**, the compiler supplies a **zero-argument constructor** with an empty body automatically. As soon as you define any constructor, the compiler stops generating the default.

```java
class Dog {
    String name;
    // compiler adds: Dog() { } — default constructor
}
Dog d = new Dog();   // works fine
```

### Constructor Overloading

Multiple constructors with different parameter lists:

```java
class Rectangle {
    double width, height;

    Rectangle() {                          // square by default
        width = height = 1.0;
    }
    Rectangle(double side) {              // square with given side
        width = height = side;
    }
    Rectangle(double w, double h) {       // custom dimensions
        width = w;
        height = h;
    }

    double area() { return width * height; }
}
```

### Constructor Chaining with `this()`

One constructor can call another using `this(...)`:

```java
class Config {
    String host;
    int port;
    boolean ssl;

    Config() { this("localhost", 8080); }              // calls 2nd
    Config(String host, int port) { this(host, port, false); }   // calls 3rd
    Config(String host, int port, boolean ssl) {
        this.host = host;
        this.port = port;
        this.ssl = ssl;
    }
}
```

---

## 6. The `this` Keyword

`this` is a **reference to the current object** — the object on which a method or constructor is being invoked.

### Use 1: Disambiguate field from parameter

```java
class Student {
    String name;
    int age;

    Student(String name, int age) {
        this.name = name;   // this.name = field; name = parameter
        this.age = age;
    }
}
```

### Use 2: Call another constructor

```java
Config() {
    this("localhost", 8080);   // delegates to another constructor
}
```

### Use 3: Pass current object as argument

```java
class Printer {
    void print(Object obj) { System.out.println(obj); }
    void run() {
        print(this);   // passes itself
    }
}
```

**Key rule:** `this` cannot be used in a `static` context (static methods don't have an associated object).

---

## 7. JVM Memory Architecture

Understanding memory helps explain how Java programs actually execute.

```
JVM Memory
├── Method Area (Class Area)
│   ├── Class bytecode (loaded class files)
│   ├── Static variables
│   └── Method definitions
│
├── Heap Area
│   ├── All objects created with `new`
│   └── Non-static (instance) variables
│
└── Stack (one per thread)
    ├── Method call frames
    ├── Local variables
    └── Parameters
```

### What Happens When You Write `new Employee(101, "Alice", 90000)`?

1. JVM allocates memory in the **Heap** for the Employee object
2. Non-static fields are set to default values
3. Non-static block runs (if any)
4. Constructor runs, initializing fields with provided values
5. Constructor returns the memory **address** of the new object
6. The reference variable (e.g., `emp1`) stores this address on the **Stack**

### Garbage Collection

- JVM's **Garbage Collector (GC)** automatically deallocates heap memory for objects that are no longer referenced
- An object becomes eligible for GC when there are no more references pointing to it
- You can request GC (not guaranteed): `System.gc()`
- GC calls `finalize()` on an object before collecting it (deprecated since Java 9)

---

## 8. Access Modifiers

Access modifiers control **visibility** of class members.

### The Four Levels

| Modifier | Within Class | Within Package | Subclass (outside package) | Anywhere |
|----------|-------------|----------------|---------------------------|----------|
| `private` | ✓ | ✗ | ✗ | ✗ |
| `(default)` | ✓ | ✓ | ✗ | ✗ |
| `protected` | ✓ | ✓ | ✓ | ✗ |
| `public` | ✓ | ✓ | ✓ | ✓ |

### Where Each Can Be Applied

| Modifier | Class | Method | Field | Constructor | Block |
|----------|-------|--------|-------|-------------|-------|
| `private` | ✗* | ✓ | ✓ | ✓ | ✗ |
| `default` | ✓ | ✓ | ✓ | ✓ | ✗ |
| `protected` | ✗* | ✓ | ✓ | ✓ | ✗ |
| `public` | ✓ | ✓ | ✓ | ✓ | ✗ |

*Inner classes can be `private` or `protected`.

**Why can't `private` apply to a top-level class?**
If the whole class were private, no other class could communicate with it — making it useless.

**Why can't `protected` apply to a top-level class?**
Protected implies parent-child relationship. An entire class being protected makes no sense outside that hierarchy.

**Why can't any access modifier apply to a block?**
Blocks have no identity (no name) so they cannot be accessed explicitly.

### Examples

```java
class First {
    private static int i = 10;   // only accessible within First
    static int j = 20;           // package-level access
}

class Second {
    public static void main(String[] args) {
        // System.out.println(First.i);  // ERROR: private
        System.out.println(First.j);    // OK: default = package-level
    }
}
```

### Private Constructor Pattern

Making a constructor `private` prevents external instantiation — used in Singleton pattern:

```java
class Singleton {
    private static Singleton instance;

    private Singleton() { }   // can't call new Singleton() outside

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

---

## 9. Packages

A **package** is a directory that groups related classes and interfaces. It prevents naming conflicts and organizes code.

### Java's Built-in Packages

```
java
├── lang      — String, System, Math, Object, Thread (auto-imported)
├── util      — Scanner, ArrayList, HashMap, Random, Date
├── io        — FileReader, FileWriter, BufferedReader
├── net       — Socket, URL, InetAddress
├── sql       — Connection, Statement, ResultSet
└── awt       — Button, Frame, Graphics (GUI)
```

### Creating a User Package

```java
// File: com/company/app/Hello.java
package com.company.app;

public class Hello {
    public void greet() {
        System.out.println("Hello from com.company.app!");
    }
}
```

**Compile:**
```bash
javac -d . Hello.java    # -d creates package directory structure
```

### The `import` Statement

```java
import java.util.Scanner;         // import specific class
import java.util.*;               // import all classes in package
import static java.lang.Math.PI;  // static import (Java 5+)
```

> **Note:** `java.lang` package is auto-imported — you don't need `import java.lang.String;`

### Fully Qualified Name (FQN)

Use the full package path instead of importing — useful when two packages have classes with the same name:

```java
java.util.Date utilDate = new java.util.Date();
java.sql.Date sqlDate  = new java.sql.Date(System.currentTimeMillis());
```

### Connecting Classes Across Packages

```java
// File: p1/First.java
package p1;

public class First {              // must be public to be accessible outside p1
    public void check() {
        System.out.println("p1.First.check()");
    }
}

// File: p2/Second.java
package p2;
import p1.First;

public class Second {
    public static void main(String[] args) {
        First obj = new First();
        obj.check();
    }
}
```

### Package Naming Convention

- All lowercase, using reversed domain: `com.google.android`, `org.apache.commons`
- Sub-packages separated by `.`: `com.company.module.feature`

---

## 10. Useful Utility Classes

### Scanner (java.util.Scanner)

Read user input from the console, files, or strings.

```java
import java.util.Scanner;

Scanner sc = new Scanner(System.in);

System.out.print("Enter your name: ");
String name = sc.nextLine();

System.out.print("Enter your age: ");
int age = sc.nextInt();

System.out.println("Hello, " + name + "! You are " + age + " years old.");
sc.close();
```

**Common Scanner methods:**

| Method | Returns |
|--------|---------|
| `nextInt()` | `int` |
| `nextDouble()` | `double` |
| `nextLine()` | `String` (entire line) |
| `next()` | `String` (next token, whitespace-delimited) |
| `nextBoolean()` | `boolean` |

### Random (java.util.Random)

```java
import java.util.Random;

Random rand = new Random();
int anyInt = rand.nextInt();         // any int
int zeroTo99 = rand.nextInt(100);    // 0 to 99
double d = rand.nextDouble();        // 0.0 to 1.0
boolean b = rand.nextBoolean();

// Modern alternative (Java 8+)
int n = (int)(Math.random() * 100);  // 0 to 99
```

### Math (java.lang.Math)

```java
Math.abs(-5)          // 5
Math.max(10, 20)      // 20
Math.min(10, 20)      // 10
Math.sqrt(16)         // 4.0
Math.pow(2, 10)       // 1024.0
Math.floor(3.7)       // 3.0
Math.ceil(3.2)        // 4.0
Math.round(3.5)       // 4
Math.PI               // 3.141592653589793
```

---

## Interview Questions

1. What is OOP? What are its four pillars?
2. What is the difference between a class and an object?
3. What is encapsulation and why is it important?
4. What is the difference between abstraction and encapsulation?
5. What is polymorphism? Give an example of each type.
6. What is inheritance and how is it achieved in Java?
7. What is the difference between method overloading and method overriding?
8. What is a constructor? How is it different from a method?
9. What is a default constructor? When does the compiler provide one?
10. What is the `this` keyword and when is it used?
11. What is the difference between static and non-static members?
12. Explain the JVM memory areas: method area, heap, and stack.
13. What is garbage collection? How does it work?
14. What are the four access modifiers in Java?
15. Why can't `private` be applied to a top-level class?
16. What is a package? Why is it used?
17. What is a fully qualified name (FQN) and when do you need it?
18. What is the difference between `import java.util.Scanner` and `import java.util.*`?
19. Why is `main()` both `public` and `static`?
20. Can we have multiple static blocks in a class?

---

## Assignments

1. Create an `Employee` class with fields `id`, `name`, `salary`, `department`. Add constructors, getters/setters, and a `display()` method. Create 3 employee objects and print their details.

2. Create a `BankAccount` class with `accountNo`, `holderName`, and `balance`. Implement `deposit(double)`, `withdraw(double)` (with minimum balance check of ₹500), and `getBalance()` methods.

3. Create a `Circle` class with a `radius` field. Add methods to compute `area()` and `circumference()`. Demonstrate constructor overloading with a no-arg constructor (radius = 1) and a parameterized constructor.

4. Demonstrate the use of all four access modifiers with a class hierarchy spanning two packages.

5. Create a `Counter` class with static and non-static variables to count both total objects created and each object's individual count.

6. Write a program showing execution order of static block, non-static block, and constructor when creating multiple objects.
