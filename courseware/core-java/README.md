# Core Java — Complete Courseware

> **Author:** Vaman Deshmukh &nbsp;|&nbsp; **Revised:** 2024–25
> **Java 8 baseline · Modern features (Java 11 → 24) covered separately**

---

## What Is This?

A complete, from-scratch Java courseware. Each file covers **one topic**, goes deep enough to be useful, and stops before it gets boring.

- **No fluff.** Every section earns its place.
- **Consistent examples.** All code uses an Employee/Department/Project domain. Names used: Sonu, Monu, Tonu, Ponu, Gonu.
- **Java 8 baseline.** Everything in Parts A–H compiles on any JDK 8+. Modern Java features are in Part I as dedicated files — clean separation, no mixing.
- **Self-contained files.** Pick any topic and jump in. You don't need to read everything in order (though the first time, you should).

---

## The Modules

### Part A — Getting Started

| # | File | What's Inside |
|---|------|---------------|
| 01 | `01_Introduction_and_Setup.md` | What Java is, JDK vs JRE vs JVM, installing JDK, first program, how `.java` → `.class` → output works |
| 02 | `02_JVM_Architecture.md` | Class loading, runtime memory areas (heap, stack, method area), JIT compiler — understanding what happens under the hood |

---

### Part B — Language Fundamentals

| # | File | What's Inside |
|---|------|---------------|
| 03 | `03_Datatypes_Variables_and_Operators.md` | Primitive types, literals, variables, type casting & promotion, operators, assignments, operator precedence |
| 04 | `04_Wrapper_Classes.md` | Integer, Double, Character etc., autoboxing & unboxing, when and why wrappers matter |
| 05 | `05_Flow_Control.md` | if/else, switch, while, do-while, for, for-each, break, continue — with real decision-making examples |
| 06 | `06_Arrays.md` | 1D and 2D arrays, default values, iterating, `java.util.Arrays`, common mistakes |
| 07 | `07_String_Handling.md` | String immutability, String pool, 40+ String methods, StringBuilder, StringBuffer, comparison and when to use what |

---

### Part C — Object-Oriented Programming

| # | File | What's Inside |
|---|------|---------------|
| 08 | `08_Classes_and_Objects.md` | Class anatomy, objects, fields, methods, constructors, `this`, static vs non-static members, blocks, execution order |
| 09 | `09_Access_Modifiers_and_Packages.md` | private / default / protected / public — rules and reasoning; packages, import, FQN, naming conventions |
| 10 | `10_Inheritance.md` | extends, super, constructor chaining, method overriding, `@Override`, `final`, types of inheritance, IS-A vs HAS-A |
| 11 | `11_Polymorphism.md` | Overloading (compile-time) vs overriding (runtime), dynamic dispatch, upcasting, downcasting, instanceof |
| 12 | `12_Abstraction.md` | abstract classes, interfaces, Java 8 default & static methods in interfaces, abstract class vs interface — decision guide |
| 13 | `13_Encapsulation.md` | Private fields + getters/setters, why it matters, immutable classes, JavaBeans pattern |

---

### Part D — Core Java Toolkit

| # | File | What's Inside |
|---|------|---------------|
| 14 | `14_Object_Class_Methods.md` | toString(), equals(), hashCode() contract, clone(), wait()/notify(), finalize() — what each does and when to override |
| 15 | `15_Inner_Classes.md` | Regular inner, static nested, method-local, anonymous — all four types with use cases; how lambdas replaced anonymous classes |
| 16 | `16_Enums.md` | Basic enums, enums with fields & methods, switch on enum, EnumSet, EnumMap, why enums beat int/String constants |
| 17 | `17_Exception_Handling.md` | Exception hierarchy, checked vs unchecked, try-catch-finally, multi-catch, try-with-resources, throw/throws, custom exceptions, assertions |
| 18 | `18_Annotations.md` | What annotations are and why they exist, built-in annotations, meta-annotations, writing custom annotations, reading with Reflection |

---

### Part E — Java 8 Features

| # | File | What's Inside |
|---|------|---------------|
| 19 | `19_Lambda_Expressions.md` | What lambdas are, syntax variations, evolution from anonymous class → lambda, method references (all 4 types) |
| 20 | `20_Functional_Interfaces.md` | @FunctionalInterface, Runnable, Supplier, Consumer, Function, Predicate, BiFunction — with composition examples |
| 21 | `21_Stream_API.md` | Stream pipeline, lazy evaluation, intermediate ops (filter/map/flatMap/sorted/distinct/limit), terminal ops (collect/reduce/count/findFirst/anyMatch), Collectors, parallel streams |
| 22 | `22_Optional.md` | The null problem, Optional creation, map/flatMap/filter, orElse/orElseGet/orElseThrow — eliminating NPEs cleanly |

---

### Part F — Concurrency

| # | File | What's Inside |
|---|------|---------------|
| 23 | `23_Multithreading.md` | Thread vs Runnable, thread lifecycle, sleep/join/interrupt, priorities, daemon threads, synchronization, race conditions, `synchronized` |
| 24 | `24_Executor_Framework.md` | Why ExecutorService over raw threads, thread pool types, execute vs submit, Callable & Future, ScheduledExecutorService, CompletableFuture, concurrent collections |

---

### Part G — I/O and Collections

| # | File | What's Inside |
|---|------|---------------|
| 25 | `25_IO_Streams.md` | Byte streams, character streams, buffered streams, try-with-resources pattern, java.nio.file (Path, Files), serialization & `transient` |
| 26 | `26_Collections_and_Generics.md` | Full collection hierarchy, List/Set/Queue/Map implementations with comparison, Generics (why, how, bounded types), Collections utility class, Java 9 factory methods |

---

### Part H — Memory and Object Lifecycle

| # | File | What's Inside |
|---|------|---------------|
| 27 | `27_Garbage_Collection.md` | Object lifecycle, GC eligibility, strong/soft/weak/phantom references, GC algorithms (Serial, G1, ZGC), what you can and can't control |

---

### Part I — Modern Java

Each file here is standalone. Read whichever version is relevant to you.

| # | File | What's Inside |
|---|------|---------------|
| 28 | `28_Java11_Features.md` | String API additions, Files.readString/writeString, `var` in lambda params, HttpClient API, running single-file programs |
| 29 | `29_Java13_14_Features.md` | Text blocks, switch expressions (arrow syntax + `yield`), helpful NullPointerExceptions |
| 30 | `30_Java17_Features.md` | Records, sealed classes & interfaces, pattern matching for instanceof |
| 31 | `31_Java21_Features.md` | Virtual threads (Project Loom), record patterns, pattern matching in switch, sequenced collections |
| 32 | `32_Java24_Features.md` | Finalized virtual thread features, primitive types in patterns, structured concurrency, other stable additions |

---

## Suggested Learning Order

If you're going cover to cover, this is the sequence:

```
01 → 02 → 03 → 04 → 05 → 06 → 07    (setup + fundamentals)
08 → 09 → 10 → 11 → 12 → 13          (OOP)
14 → 15 → 16 → 17 → 18               (core toolkit)
19 → 20 → 21 → 22                     (Java 8 features)
23 → 24                               (concurrency)
25 → 26                               (I/O + collections)
27                                     (GC + memory)
28 → 29 → 30 → 31 → 32               (modern Java — pick what you need)
```

**In a hurry?** Modules 01–26 cover everything you need for most Java roles. Modules 27–32 are for completeness and interviews.

---

## Code Style Used Throughout

```
Domain      :  Employee, Department, Project, Job
Names       :  Sonu, Monu, Tonu, Ponu, Gonu
Packages    :  com.ems.bean / com.ems.service / com.ems.util
```

Quick example of what every file's code will look like:

```java
Employee e1 = new Employee(101, "Sonu",  75000.0, "Engineering");
Employee e2 = new Employee(102, "Monu",  82000.0, "Engineering");
Employee e3 = new Employee(103, "Tonu",  55000.0, "HR");
Employee e4 = new Employee(104, "Ponu",  91000.0, "Finance");
Employee e5 = new Employee(105, "Gonu",  68000.0, "Operations");
```

---

## Java Version Quick Reference

| Version | Year | LTS | Highlights |
|---------|------|-----|------------|
| **Java 8** | 2014 | — | Lambdas, Streams, Optional, default methods — **courseware baseline** |
| Java 11 | 2018 | ✓ | HttpClient, String/Files API additions |
| Java 13/14 | 2019/20 | — | Text blocks, switch expressions |
| Java 17 | 2021 | ✓ | Records, sealed classes, pattern matching |
| Java 21 | 2023 | ✓ | Virtual threads, sequenced collections |
| Java 24 | 2024 | — | Finalized Loom/Valhalla features |

> **Which JDK to install?** JDK 21 (LTS). It runs all Java 8 code perfectly and gives you access to everything up to Java 21.

---

## Setup in 3 Steps

```bash
# 1. Download JDK 21 from https://adoptium.net  and install it

# 2. Verify
java -version       # should print: openjdk 21...
javac -version      # should print: javac 21...

# 3. Open Module 01 and follow along
```

Full setup guide with screenshots → `01_Introduction_and_Setup.md`
