# Core Java 8 — Complete Courseware

> **Original Author:** Vaman Deshmukh
> **Compiled, Modernized & Enhanced:** 2024
> **Source:** Core Java Notes (CJ-01 through CJ-12, Other Topics, Lab Book)

---

## Courseware Structure

This courseware is organized into **5 learning modules** plus a **lab workbook**, progressing from fundamentals to advanced Java features.

---

## Module Overview

| Module | File | Topics | Source Notes |
|--------|------|--------|-------------|
| **1** | `MODULE_01_Java_Foundations.md` | Intro to Java, Tokens, Data Types, Control Structures, Arrays | CJ-01, CJ-02, CJ-03 |
| **2** | `MODULE_02_OOP_and_Class_Design.md` | OOP Pillars, Classes & Objects, Static/Non-static, Constructors, `this`, Access Modifiers, Packages | CJ-04, CJ-05, CJ-06 |
| **3** | `MODULE_03_Advanced_OOP_Exceptions_Threads.md` | Inheritance, Polymorphism, Abstract/Interface, Exception Handling, Multithreading, Inner Classes, GC | CJ-08, CJ-09 |
| **4** | `MODULE_04_Collections_IO_Files.md` | Collection Framework (List/Set/Map/Queue), Generics, IO Streams, File API, Serialization | CJ-10, CJ-11 |
| **5** | `MODULE_05_Java8_GUI_Modern.md` | Enums, Lambda Expressions, Functional Interfaces, Stream API, Optional, GUI (AWT/Swing), Regex, Networking, Java 9–21 | CJ-12, Other Topics |
| **Labs** | `MODULE_06_Lab_Workbook.md` | Hands-on exercises for all topics, progressive difficulty, Layered Architecture case study | Lab Book |

---

## Recommended Learning Path

```
Week 1–2:   Module 1 — Java Foundations
Week 3–4:   Module 2 — OOP and Class Design
Week 5–6:   Module 3 — Advanced OOP, Exceptions, Threads
Week 7–8:   Module 4 — Collections, IO, Files
Week 9–10:  Module 5 — Java 8 Features, GUI, Modern Java
Throughout: Module 6 — Labs (do labs after each module)
```

---

## What's Covered

### Module 1 — Java Foundations
- Java history and versions (up to Java 21)
- Features: WORA, platform independence, OOPS, robust, secure
- Tokens: keywords, identifiers, literals, operators, separators, comments
- All 8 primitive data types with sizes, ranges, and defaults
- All operator types with precedence
- Control flow: if/else, switch (including Java 14+ arrow syntax), while, do-while, for, for-each
- break, continue, nested loops
- Arrays: 1D, 2D, multi-dimensional, for-each, `java.util.Arrays`

### Module 2 — OOP and Class Design
- Four OOP pillars with examples
- Class anatomy: fields, methods, blocks, constructors
- Static vs non-static context (cinema analogy, bank analogy)
- JVM memory: method area, heap, stack
- Static/non-static blocks and their execution order
- Method categories: NANR, NAWR, WANR, WAWR
- Constructors: default, parameterized, overloaded, chaining with `this()`
- `this` keyword
- Access modifiers: private, default, protected, public
- Packages: creation, subpackages, import, FQN
- Scanner, Random, Math utility classes

### Module 3 — Advanced OOP, Exceptions & Threads
- Inheritance: `extends`, `super`, method overriding, `@Override`, `final`
- Method overloading vs overriding comparison table
- Abstract classes and interfaces (including Java 8 default/static methods)
- Abstract class vs Interface comparison table
- Exception hierarchy: Throwable → Error, Exception → RuntimeException
- Checked vs Unchecked exceptions
- try-catch-finally, multi-catch, try-with-resources
- Custom exceptions, `throw`, `throws`
- Top 10 production exceptions
- Multithreading: Thread, Runnable, lambda threads
- Thread lifecycle states
- Thread methods: start, sleep, join, setPriority, setName, isDaemon
- Thread synchronization: `synchronized` method/block
- ExecutorService (modern threading)
- Inner classes: regular, static nested, method-local, anonymous
- Garbage collection, `finalize()` (deprecated note)

### Module 4 — Collections, IO & Files
- Full Collection Framework hierarchy
- ArrayList: creation, CRUD, sort, iterate, convert
- LinkedList, Vector, Stack (with modern alternatives)
- HashSet, LinkedHashSet, TreeSet
- Queue, PriorityQueue, Deque, ArrayDeque
- HashMap, LinkedHashMap, TreeMap
- Generics: type safety, generic classes, bounded type parameters
- `Collections` utility class: sort, shuffle, reverse, binarySearch, unmodifiable
- Java 9+ `List.of()`, `Set.of()`, `Map.of()`
- Byte streams, character streams, buffered streams, data streams
- `File` class (legacy) and `java.nio.file` (modern NIO)
- Serialization/deserialization, `transient` keyword, `serialVersionUID`

### Module 5 — Java 8, GUI & Modern Java
- Enums: basic, with fields/methods, patterns
- Lambda expressions: syntax, evolution, method references
- Functional interfaces: Runnable, Supplier, Consumer, Function, Predicate
- Predicate/Function composition
- Stream API: pipeline, creation, intermediate ops (filter, map, flatMap, sorted, distinct, limit, skip)
- Stream terminal ops (collect, count, reduce, findFirst, anyMatch, forEach)
- `Collectors.groupingBy()`, `Collectors.toMap()`
- Parallel streams
- `Optional<T>`: creation, functional access, chaining
- AWT: Frame, Graphics, events
- Swing: JFrame, JPanel, JLabel, JButton, JTextField, layouts, ActionListener
- Regular expressions: Pattern, Matcher, common patterns
- Network programming: InetAddress, URL, Socket basics
- Reflection API
- Java 9–21 highlights: var, records, sealed classes, text blocks, virtual threads

### Module 6 — Lab Workbook
- 10 progressive labs covering all topics
- Mandatory and optional exercises per lab
- Method specification tables (name, signature, return type, logic)
- Case studies: Banking system, Employee Insurance System
- Layered Architecture pattern (`bean`, `service`, `pl` packages)
- Stream API case study with 15 queries

---

## Key Java API Reference

### Essential Packages

| Package | Key Classes |
|---------|-------------|
| `java.lang` | `String`, `Math`, `System`, `Thread`, `Object`, `Integer`, `Double` |
| `java.util` | `ArrayList`, `HashMap`, `Scanner`, `Random`, `Arrays`, `Collections`, `Optional` |
| `java.util.stream` | `Stream`, `IntStream`, `Collectors` |
| `java.util.function` | `Function`, `Predicate`, `Consumer`, `Supplier` |
| `java.io` | `FileReader`, `FileWriter`, `BufferedReader`, `ObjectInputStream` |
| `java.nio.file` | `Path`, `Paths`, `Files`, `StandardOpenOption` |
| `java.net` | `Socket`, `ServerSocket`, `URL`, `InetAddress` |
| `javax.swing` | `JFrame`, `JPanel`, `JButton`, `JTextField`, `JOptionPane` |
| `java.util.concurrent` | `ExecutorService`, `Executors`, `Future` |

---

## Java Versions Quick Reference

| Version | Year | Key Feature |
|---------|------|------------|
| Java 5 | 2004 | Generics, Enums, for-each, autoboxing |
| Java 7 | 2011 | try-with-resources, diamond operator |
| **Java 8** | **2014** | **Lambda, Stream API, Optional, default methods** |
| Java 9 | 2017 | Modules, `List.of()`, `var` (preview) |
| Java 10 | 2018 | `var` (local type inference) |
| Java 11 | 2018 | LTS, `String.strip()`, `HttpClient` |
| Java 14 | 2020 | Switch expressions (standard) |
| Java 16 | 2021 | Records, pattern matching `instanceof` |
| Java 17 | 2021 | LTS, sealed classes, text blocks |
| Java 21 | 2023 | LTS, virtual threads, record patterns |

---

## References

- [Oracle Java 8 API Documentation](https://docs.oracle.com/javase/8/docs/api/)
- [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- *Java, The Complete Reference* — Herbert Schildt
- *Thinking in Java* — Bruce Eckel
- *Beginning Java 8 Fundamentals* — Kishori Sharan
- [Baeldung Java Guides](https://www.baeldung.com)
