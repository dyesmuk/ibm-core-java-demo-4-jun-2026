# Module 25 — IO Streams

> **Part G: I/O and Collections**
> Prerequisites: Module 08–13, Module 17 · Time: ~2 hours

---

## Overview

Java's I/O system handles reading and writing data — files, networks, memory buffers, standard input/output. It is organized around **streams** — sequences of data flowing in (input) or out (output).

```
java.io     — classic stream-based I/O (Java 1.0+)
java.nio    — New I/O, buffer-based, non-blocking (Java 1.4+)
java.nio.file — Modern file API: Path, Files, Paths (Java 7+)
```

For most day-to-day file work, you will use **`java.nio.file`** (the modern API) and **buffered streams** from `java.io`. Both are covered here.

---

## Stream Categories

```
I/O Streams
├── Byte Streams          — raw bytes, for binary data (images, PDFs, serialized objects)
│   ├── InputStream       — abstract base for reading bytes
│   └── OutputStream      — abstract base for writing bytes
│
└── Character Streams     — text data, handles encoding automatically
    ├── Reader            — abstract base for reading characters
    └── Writer            — abstract base for writing characters
```

Every concrete stream class extends one of these four abstract bases.

---

## Byte Streams — `FileInputStream` / `FileOutputStream`

For reading and writing raw binary data:

```java
import java.io.*;

// Write bytes to a file
try (FileOutputStream fos = new FileOutputStream("data.bin")) {
    byte[] data = {72, 101, 108, 108, 111};   // ASCII for "Hello"
    fos.write(data);
    System.out.println("Written.");
}

// Read bytes from a file
try (FileInputStream fis = new FileInputStream("data.bin")) {
    int byteValue;
    while ((byteValue = fis.read()) != -1) {   // read() returns -1 at EOF
        System.out.print((char) byteValue);
    }
}
// Output: Hello
```

`read()` returns each byte as an `int` (0–255) or `-1` at end of file. Always use `try-with-resources` — streams hold OS file handles that must be released.

---

## Character Streams — `FileReader` / `FileWriter`

For reading and writing text files. Handles character encoding:

```java
// Write text
try (FileWriter fw = new FileWriter("employees.txt")) {
    fw.write("101,Sonu,75000,Engineering\n");
    fw.write("102,Monu,82000,Engineering\n");
    fw.write("103,Tonu,55000,HR\n");
    System.out.println("File written.");
}

// Read text
try (FileReader fr = new FileReader("employees.txt")) {
    int ch;
    while ((ch = fr.read()) != -1) {
        System.out.print((char) ch);
    }
}
```

Reading character-by-character is inefficient. Always wrap with a buffered reader.

---

## Buffered Streams — The Right Way for Text Files

Buffered streams reduce the number of actual I/O operations by reading/writing in larger chunks internally:

```java
// BufferedWriter — write text efficiently
try (BufferedWriter bw = new BufferedWriter(new FileWriter("employees.txt"))) {
    bw.write("ID,Name,Salary,Department");
    bw.newLine();   // platform-independent line separator
    bw.write("101,Sonu,75000,Engineering");
    bw.newLine();
    bw.write("102,Monu,82000,Engineering");
    bw.newLine();
    bw.write("103,Tonu,55000,HR");
    bw.newLine();
}

// BufferedReader — read line by line
try (BufferedReader br = new BufferedReader(new FileReader("employees.txt"))) {
    String line;
    boolean firstLine = true;
    while ((line = br.readLine()) != null) {
        if (firstLine) { firstLine = false; continue; }  // skip header
        String[] parts = line.split(",");
        System.out.printf("ID: %-5s Name: %-10s Salary: %-10s Dept: %s%n",
                          parts[0], parts[1], parts[2], parts[3]);
    }
}
```

Output:
```
ID: 101   Name: Sonu       Salary: 75000      Dept: Engineering
ID: 102   Name: Monu       Salary: 82000      Dept: Engineering
ID: 103   Name: Tonu       Salary: 55000      Dept: HR
```

`readLine()` returns `null` at end of file — use `!= null` as the loop condition.

---

## `PrintWriter` — Formatted Text Output

`PrintWriter` wraps any `Writer` and adds convenient `print`/`println`/`printf` methods:

```java
try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("report.txt")))) {
    pw.printf("%-5s %-12s %-15s %10s%n", "ID", "Name", "Department", "Salary");
    pw.println("-".repeat(47));
    pw.printf("%-5d %-12s %-15s %10.2f%n", 101, "Sonu",  "Engineering", 75000.0);
    pw.printf("%-5d %-12s %-15s %10.2f%n", 102, "Monu",  "Engineering", 82000.0);
    pw.printf("%-5d %-12s %-15s %10.2f%n", 103, "Tonu",  "HR",          55000.0);
    pw.println("-".repeat(47));
    pw.printf("%-32s %10.2f%n", "Total:", 212000.0);
}
```

### Appending to an Existing File

```java
// Second argument true = append mode
try (BufferedWriter bw = new BufferedWriter(new FileWriter("employees.txt", true))) {
    bw.write("104,Ponu,91000,Finance");
    bw.newLine();
}
```

---

## `DataInputStream` / `DataOutputStream` — Typed Binary Data

For writing/reading Java primitives in binary format:

```java
// Write typed data
try (DataOutputStream dos = new DataOutputStream(
         new BufferedOutputStream(new FileOutputStream("salary.dat")))) {
    dos.writeInt(101);
    dos.writeUTF("Sonu");
    dos.writeDouble(75000.0);
    dos.writeBoolean(true);
}

// Read typed data — must read in same order
try (DataInputStream dis = new DataInputStream(
         new BufferedInputStream(new FileInputStream("salary.dat")))) {
    int     id       = dis.readInt();
    String  name     = dis.readUTF();
    double  salary   = dis.readDouble();
    boolean isActive = dis.readBoolean();
    System.out.printf("ID: %d, Name: %s, Salary: %.2f, Active: %b%n",
                      id, name, salary, isActive);
}
```

---

## Serialization — Object to File and Back

Serialization converts a Java object to a byte stream so it can be saved to a file or sent over a network. Deserialization is the reverse.

Requirements:
- The class must implement `java.io.Serializable` (marker interface — no methods)
- A `serialVersionUID` field is strongly recommended

```java
import java.io.*;

public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private int    id;
    private String name;
    private double salary;
    private String department;
    private transient String sessionToken;  // transient — NOT serialized

    public Employee(int id, String name, double salary, String department) {
        this.id         = id;
        this.name       = name;
        this.salary     = salary;
        this.department = department;
        this.sessionToken = "token-" + id;
    }

    // getters, toString...
}
```

### Serialize (Write Object)

```java
Employee e1 = new Employee(101, "Sonu",  75000, "Engineering");
Employee e2 = new Employee(102, "Monu",  82000, "HR");

try (ObjectOutputStream oos = new ObjectOutputStream(
         new BufferedOutputStream(new FileOutputStream("employees.ser")))) {
    oos.writeObject(e1);
    oos.writeObject(e2);
    System.out.println("Serialized.");
}
```

### Deserialize (Read Object)

```java
try (ObjectInputStream ois = new ObjectInputStream(
         new BufferedInputStream(new FileInputStream("employees.ser")))) {
    Employee emp1 = (Employee) ois.readObject();
    Employee emp2 = (Employee) ois.readObject();
    System.out.println(emp1);
    System.out.println(emp2);
    // sessionToken will be null — it was transient
}
```

### `transient` keyword

Fields marked `transient` are skipped during serialization. Use for:
- Sensitive data (passwords, tokens)
- Fields that can be recomputed
- Non-serializable objects (database connections, threads)

### `serialVersionUID`

Used during deserialization to verify that the serialized class matches the current class definition. If they don't match, `InvalidClassException` is thrown:

```java
private static final long serialVersionUID = 1L;
```

If you change the class structure (add/remove fields) and want to maintain backward compatibility, keep the same UID and handle missing fields with defaults. If you want to force re-serialization, change the UID.

---

## The Modern File API — `java.nio.file`

Java 7 introduced `Path`, `Paths`, and `Files` — a cleaner, more powerful replacement for the legacy `java.io.File` class.

### `Path` and `Paths`

```java
import java.nio.file.*;

Path path1 = Paths.get("employees.txt");
Path path2 = Paths.get("/home/user", "data", "employees.txt");
Path path3 = Path.of("employees.txt");    // Java 11+

System.out.println(path1.toAbsolutePath());
System.out.println(path2.getFileName());   // employees.txt
System.out.println(path2.getParent());     // /home/user/data
System.out.println(path2.getRoot());       // /
```

### `Files` — Reading and Writing

```java
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

Path path = Paths.get("employees.txt");

// Write all lines at once
List<String> lines = Arrays.asList(
    "ID,Name,Salary,Department",
    "101,Sonu,75000,Engineering",
    "102,Monu,82000,Engineering",
    "103,Tonu,55000,HR"
);
Files.write(path, lines, StandardCharsets.UTF_8);

// Read all lines at once — good for small files
List<String> readBack = Files.readAllLines(path, StandardCharsets.UTF_8);
readBack.forEach(System.out::println);

// Read entire file as a single String (Java 11+)
String content = Files.readString(path);

// Write entire String to file (Java 11+)
Files.writeString(path, content, StandardOpenOption.TRUNCATE_EXISTING);

// Append
Files.writeString(path, "\n104,Ponu,91000,Finance",
                  StandardOpenOption.APPEND);
```

### `Files` — Stream-Based Reading (Good for Large Files)

```java
// Stream lines — lazy, good for large files
try (Stream<String> lineStream = Files.lines(path, StandardCharsets.UTF_8)) {
    lineStream
        .skip(1)                          // skip header
        .map(line -> line.split(","))
        .filter(parts -> Double.parseDouble(parts[2]) > 70000)
        .forEach(parts -> System.out.printf("%-10s %.2f%n",
                                            parts[1], Double.parseDouble(parts[2])));
}
```

### File and Directory Operations

```java
Path file = Paths.get("report.txt");
Path dir  = Paths.get("output");

// Check existence
Files.exists(file);
Files.notExists(file);
Files.isDirectory(dir);
Files.isRegularFile(file);
Files.isReadable(file);
Files.isWritable(file);

// Create
Files.createFile(file);                          // create a file
Files.createDirectory(dir);                     // create directory
Files.createDirectories(Paths.get("a/b/c"));   // create full path

// Copy and Move
Files.copy(file, Paths.get("backup.txt"), StandardCopyOption.REPLACE_EXISTING);
Files.move(file, Paths.get("archive/report.txt"), StandardCopyOption.REPLACE_EXISTING);

// Delete
Files.delete(file);                    // throws NoSuchFileException if not found
Files.deleteIfExists(file);           // safe version

// File size and metadata
long size = Files.size(file);
System.out.println("Size: " + size + " bytes");
```

### Walking a Directory Tree

```java
// List files in a directory
try (Stream<Path> stream = Files.list(Paths.get("."))) {
    stream.filter(Files::isRegularFile)
          .forEach(System.out::println);
}

// Walk entire directory tree recursively
try (Stream<Path> walk = Files.walk(Paths.get("src"))) {
    walk.filter(p -> p.toString().endsWith(".java"))
        .forEach(System.out::println);
}

// Find files matching a pattern
try (Stream<Path> found = Files.find(Paths.get("."), 3,
        (p, attr) -> p.toString().endsWith(".txt") && attr.isRegularFile())) {
    found.forEach(System.out::println);
}
```

---

## Legacy `File` Class

The old `java.io.File` class — you will see this in legacy code:

```java
import java.io.File;

File f = new File("employees.txt");

f.exists();
f.isFile();
f.isDirectory();
f.length();                 // size in bytes
f.getName();                // employees.txt
f.getAbsolutePath();        // full path
f.getParent();              // parent directory path
f.delete();
f.mkdir();                  // create directory
f.mkdirs();                 // create directory + parents
f.listFiles();              // array of File objects in directory
f.createNewFile();          // create empty file
f.renameTo(new File("new_name.txt"));
```

Prefer `java.nio.file` for new code — it has better error messages, atomic operations, and a richer API.

---

## Practical Example — Employee CSV Processor

```java
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class EmployeeCSVProcessor {

    static final Path FILE = Paths.get("employees.csv");

    public static void writeEmployees(List<Employee> employees) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("id,name,salary,department");
        for (Employee e : employees) {
            lines.add(String.format("%d,%s,%.2f,%s",
                      e.getId(), e.getName(), e.getSalary(), e.getDepartment()));
        }
        Files.write(FILE, lines, StandardCharsets.UTF_8);
        System.out.println("Written " + employees.size() + " employees to " + FILE);
    }

    public static List<Employee> readEmployees() throws IOException {
        return Files.lines(FILE, StandardCharsets.UTF_8)
                    .skip(1)
                    .map(line -> {
                        String[] p = line.split(",");
                        return new Employee(Integer.parseInt(p[0]), p[1],
                                            Double.parseDouble(p[2]), p[3]);
                    })
                    .collect(Collectors.toList());
    }

    public static void generateReport(List<Employee> employees) throws IOException {
        Path reportPath = Paths.get("salary_report.txt");

        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(reportPath.toFile())))) {

            pw.printf("%-5s %-12s %-15s %10s%n", "ID", "Name", "Department", "Salary");
            pw.println("=".repeat(47));

            employees.stream()
                     .sorted(Comparator.comparing(Employee::getDepartment)
                                       .thenComparing(Employee::getName))
                     .forEach(e -> pw.printf("%-5d %-12s %-15s %10.2f%n",
                                             e.getId(), e.getName(),
                                             e.getDepartment(), e.getSalary()));

            pw.println("=".repeat(47));
            double total = employees.stream().mapToDouble(Employee::getSalary).sum();
            pw.printf("%-32s %10.2f%n", "Total Payroll:", total);
        }

        System.out.println("Report written to " + reportPath.toAbsolutePath());
    }

    public static void main(String[] args) throws IOException {

        List<Employee> employees = Arrays.asList(
            new Employee(101, "Sonu",  75000, "Engineering"),
            new Employee(102, "Monu",  82000, "Engineering"),
            new Employee(103, "Tonu",  55000, "HR"),
            new Employee(104, "Ponu",  91000, "Finance"),
            new Employee(105, "Gonu",  68000, "Operations")
        );

        writeEmployees(employees);

        List<Employee> loaded = readEmployees();
        System.out.println("Read back " + loaded.size() + " employees.");
        loaded.forEach(e -> System.out.println("  " + e));

        generateReport(loaded);
    }
}
```

---

## Stream Decorator Pattern

Java I/O uses the **Decorator pattern** — you wrap streams to add capability:

```java
// Each wrapper adds a layer of functionality
new PrintWriter(                           // adds print/println/printf
    new BufferedWriter(                    // adds buffering
        new FileWriter("output.txt")))     // the actual file target
```

Read it inside-out:
1. `FileWriter` — writes characters to a file
2. `BufferedWriter` — buffers writes for efficiency
3. `PrintWriter` — adds formatted output methods

Common wrapping patterns:

```java
// Reading a text file efficiently
new BufferedReader(new FileReader("file.txt"))

// Reading a binary file efficiently
new DataInputStream(new BufferedInputStream(new FileInputStream("file.dat")))

// Writing objects
new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("data.ser")))

// Formatted text output to file
new PrintWriter(new BufferedWriter(new FileWriter("report.txt")))
```

---

## Quick Summary

| Class | Use For |
|-------|---------|
| `FileInputStream` / `FileOutputStream` | Raw binary file read/write |
| `FileReader` / `FileWriter` | Text file read/write |
| `BufferedReader` / `BufferedWriter` | Efficient text I/O — `readLine()` |
| `PrintWriter` | Formatted text output |
| `DataInputStream` / `DataOutputStream` | Typed primitive binary I/O |
| `ObjectInputStream` / `ObjectOutputStream` | Object serialization |
| `Path`, `Paths`, `Files` | Modern file API — prefer this |
| `Files.readAllLines()` | Read all text lines into a List |
| `Files.lines()` | Lazy stream of lines — good for large files |
| `Files.readString()` / `Files.writeString()` | Whole file as String (Java 11+) |
| `Files.walk()` | Recursive directory traversal |
| `transient` | Skip field during serialization |
| `serialVersionUID` | Version control for serialized classes |

---

## What's Next

**Module 26** — Collections and Generics. Java's full collection framework — List, Set, Queue, Map — and how Generics make them type-safe.
