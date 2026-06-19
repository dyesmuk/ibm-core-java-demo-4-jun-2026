# Module 01 — Introduction and Setup

> **Part A: Getting Started**
> Prerequisites: None · Time: ~1 hour

---

## What Is Java?

Java is a **general-purpose, object-oriented programming language** created by James Gosling at Sun Microsystems in 1995. Oracle acquired Sun in 2010 and owns Java today.

The one-line pitch: **write your code once, run it anywhere.**

That promise — called **WORA (Write Once, Run Anywhere)** — is what made Java dominate enterprise software for 30 years. Your compiled Java code runs identically on Windows, Linux, macOS, or a server in the cloud, without recompilation.

---

## Why Java Still Matters

- Powers the majority of large enterprise backends (banking, insurance, e-commerce)
- Android apps are built in Java (and Kotlin, which runs on the same runtime)
- Massive ecosystem: frameworks, libraries, tools built over 30 years
- Strong typing and the JVM make it excellent for large teams and long-lived systems
- Java 8 (2014) introduced functional programming features that modernized the language significantly

---

## The Big Picture — JDK, JRE, JVM

Three terms you will hear constantly. Here is exactly what each one is:

```
  JDK  (Java Development Kit)
  ├── javac        → compiler: converts your .java source to .class bytecode
  ├── java         → launcher: starts the JVM and runs your program
  ├── javadoc      → generates HTML documentation from source comments
  ├── jar          → packages compiled classes into a .jar archive
  └── JRE          (Java Runtime Environment)
       └── JVM     (Java Virtual Machine)
            ├── Class Loader  → loads .class files into memory
            ├── Bytecode Verifier → security check before execution
            └── Execution Engine → runs your program
                 ├── Interpreter  → executes bytecode line by line
                 └── JIT Compiler → compiles hot code to native machine code
```

**JDK** — what you install as a developer. Contains everything needed to write, compile and run Java code.

**JRE** — what end users need to *run* Java programs. Contains the JVM and standard libraries, but not the compiler. (Since Java 11, the JRE is not distributed separately.)

**JVM** — the engine that actually executes your program. It is platform-specific (there is a Windows JVM, a Linux JVM, a Mac JVM) but they all run the same bytecode — that is how WORA works.

---

## How Java Code Runs

```
  Step 1  You write:     Employee.java        (human-readable source code)
           │
  Step 2  javac compiles: Employee.class       (platform-independent bytecode)
           │
  Step 3  JVM executes:   Output on screen     (JVM translates bytecode to native machine instructions)
```

The `.class` file is **not** machine code for your specific CPU. It is bytecode — instructions for an imaginary machine called the JVM. Every platform has its own JVM that knows how to translate bytecode into that platform's native instructions. That is the magic behind WORA.

---

## Java Versions — What You Need to Know

| Version | Year | Why It Matters |
|---------|------|----------------|
| Java 1.0 | 1996 | The beginning |
| Java 5 | 2004 | Generics, enums, for-each loop |
| **Java 8** | **2014** | **Lambdas, Streams, Optional — changed Java significantly** |
| Java 11 | 2018 | First LTS after 8, HttpClient, String improvements |
| Java 17 | 2021 | Records, sealed classes — modern Java |
| Java 21 | 2023 | Virtual threads — current LTS, use this for new projects |

**LTS = Long Term Support.** Oracle provides updates for LTS versions for several years. Non-LTS versions are stepping stones.

> This courseware uses **Java 8 as the baseline**. Every code example here compiles and runs on Java 8+. Modern features (Java 11 through 24) are covered in dedicated modules at the end.

---

## Setup

### Step 1 — Install JDK

Download **JDK 21** (LTS) from [https://adoptium.net](https://adoptium.net).
Choose the installer for your OS. Run it. Done.

### Step 2 — Verify

Open a terminal (Command Prompt on Windows, Terminal on Mac/Linux):

```bash
java -version
```
Expected output:
```
openjdk version "21.0.x" ...
```

```bash
javac -version
```
Expected output:
```
javac 21.0.x
```

If both commands work, you are ready.

### Step 3 — Install an IDE

**IntelliJ IDEA Community Edition** is recommended.
Download from [https://www.jetbrains.com/idea/download](https://www.jetbrains.com/idea/download) — choose Community (free).

**Eclipse** works too — [https://www.eclipse.org/downloads](https://www.eclipse.org/downloads)

> An IDE is not strictly required — you can compile and run Java with just a text editor and the terminal. But an IDE gives you error highlighting, code completion, and a run button, which matters a lot when you are learning.

---

## Your First Java Program

Create a file called `HelloWorld.java` with exactly this content:

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

**Compile it:**
```bash
javac HelloWorld.java
```
This produces `HelloWorld.class` in the same directory.

**Run it:**
```bash
java HelloWorld
```
Output:
```
Hello, World!
```

---

## Dissecting the Program

```java
public class HelloWorld {           // 1
    public static void main(String[] args) {    // 2
        System.out.println("Hello, World!");    // 3
    }                               // 4
}                                   // 5
```

**Line 1 — `public class HelloWorld`**
Every Java program lives inside a class. The class name must match the filename exactly — `HelloWorld.java` must contain `class HelloWorld`. `public` means this class is accessible from anywhere.

**Line 2 — `public static void main(String[] args)`**
This is the **entry point** — the JVM looks for a method with exactly this signature to start your program. Every word matters:
- `public` — JVM must be able to call it from outside
- `static` — JVM calls it without creating an object first
- `void` — it returns nothing
- `main` — the name the JVM looks for
- `String[] args` — command-line arguments passed when running the program

**Line 3 — `System.out.println("Hello, World!")`**
Prints text to the console followed by a newline. `System` is a class, `out` is an object inside it representing standard output, `println` is the method that prints.

---

## A More Realistic First Program

`HelloWorld` is tradition, but here is something closer to what real Java looks like:

```java
public class EmployeeGreeting {
    public static void main(String[] args) {

        String name       = "Sonu";
        String department = "Engineering";
        double salary     = 75000.0;

        System.out.println("Employee  : " + name);
        System.out.println("Department: " + department);
        System.out.println("Salary    : " + salary);
    }
}
```

Output:
```
Employee  : Sonu
Department: Engineering
Salary    : 75000.0
```

Same structure — one class, one `main` method — but now we have variables, different data types, and string concatenation with `+`.

---

## Java Syntax — Ground Rules

A few rules the compiler enforces strictly:

**1. Every statement ends with a semicolon.**
```java
System.out.println("Hello");   // correct
System.out.println("Hello")    // compile error — missing ;
```

**2. Case sensitivity — Java is case-sensitive everywhere.**
```java
String name = "Sonu";   // String with capital S — correct
string name = "Sonu";   // compile error — no such type as 'string'
```

**3. File name must match class name exactly.**
A file named `Employee.java` must contain `public class Employee`.

**4. Curly braces define blocks — they must always be matched.**
```java
public class Demo {        // opens class block
    void method() {        // opens method block
        // code here
    }                      // closes method block
}                          // closes class block
```

**5. Whitespace (spaces, tabs, blank lines) is ignored by the compiler.** Use it freely for readability.

---

## Comments

```java
// Single-line comment — from // to end of line

/* Multi-line comment
   spans multiple lines */

/**
 * Javadoc comment — used to generate HTML documentation
 * @param name  the employee name
 * @return      a greeting message
 */
public String greet(String name) {
    return "Hello, " + name;
}
```

Use comments to explain *why*, not *what*. The code already says what it does — good code reads like prose. Comments answer the "why is this logic here?" question.

---

## Tokens — The Smallest Pieces

The Java compiler breaks your source code into **tokens** — the smallest meaningful units:

| Token Type | Description | Examples |
|-----------|-------------|---------|
| **Keywords** | Reserved words with fixed meaning | `public`, `class`, `void`, `int`, `if`, `return` |
| **Identifiers** | Names you define | `Employee`, `salary`, `calculateBonus` |
| **Literals** | Fixed values written directly | `42`, `3.14`, `"Sonu"`, `true` |
| **Operators** | Symbols that perform operations | `+`, `-`, `*`, `==`, `&&` |
| **Separators** | Punctuation that structures code | `;`, `,`, `{`, `}`, `(`, `)` |
| **Comments** | Notes for humans, ignored by compiler | `// ...`, `/* ... */` |

**Identifier naming rules:**
- May contain letters, digits, `_`, `$`
- Cannot start with a digit
- Cannot be a keyword
- Case-sensitive (`salary` ≠ `Salary`)

**Naming conventions (not rules — but everyone follows them):**

| Element | Convention | Example |
|---------|-----------|---------|
| Class | PascalCase | `Employee`, `DepartmentService` |
| Method | camelCase, verb-first | `getSalary()`, `calculateBonus()` |
| Variable | camelCase | `employeeName`, `totalSalary` |
| Constant | UPPER_SNAKE_CASE | `MAX_SALARY`, `DEFAULT_DEPT` |
| Package | all lowercase | `com.ems.service` |

---

## Quick Summary

| Concept | What to Remember |
|---------|-----------------|
| JDK | Install this. Contains javac + java + JRE |
| JVM | Executes your bytecode. Platform-specific but runs the same bytecode |
| WORA | Compile once to bytecode → run on any JVM |
| `.java` | Your source file |
| `.class` | Compiled bytecode file |
| `main` method | Entry point — JVM starts here |
| Java 8 | Baseline of this courseware |

---

## What's Next

**Module 02** — JVM Architecture goes deeper into what happens after you type `java HelloWorld` — how the JVM loads your class, manages memory, and cleans up after you.

**Module 03** — Datatypes, Variables and Operators — the building blocks of every Java program.
