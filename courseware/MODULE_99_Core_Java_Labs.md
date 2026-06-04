# Core Java 8 Lab Book

> **Original document:** [Google Docs](https://docs.google.com/document/d/1jsVLzZG5nsWCwYn2jICkMcnCrlsq_IC2XRElmjclnak/edit?usp=sharing)

---

## Table of Contents

- [Project Setup & Submission Guidelines](#project-setup--submission-guidelines)
- [General Coding Guidelines](#general-coding-guidelines)
- [Before You Start](#before-you-start)
- [Getting Started](#getting-started)
  - [Overview](#overview)
  - [Setup Checklist](#setup-checklist)
  - [Instructions](#instructions)
  - [Learning More](#learning-more)
- [Lab 1: Flow Control / Operators / Assignments](#lab-1-flow-control--operators--assignments)
- [Lab 2: Arrays](#lab-2-arrays)
- [Lab 3: Strings and Parsing](#lab-3-strings-and-parsing)
- [Lab 4: Inheritance and Polymorphism](#lab-4-inheritance-and-polymorphism)
- [Lab 5: Exception Handling](#lab-5-exception-handling)
- [Lab 6: Collection Framework](#lab-6-collection-framework)
- [Lab 7: Layered Architecture](#lab-7-layered-architecture)
- [Lab 8: Multithreading](#lab-8-multithreading)
- [Lab 9: Lambda Expressions and Stream API *(Optional)*](#lab-9-lambda-expressions-and-stream-api--optional)
- [Lab 10: File Handling *(Optional)*](#lab-10-file-handling--optional)
- [Appendix A: Naming Conventions](#appendix-a-naming-conventions)

---

## Project Setup & Submission Guidelines

> ⚠️ **Read this section carefully before writing a single line of code. Your submission will not be reviewed if these instructions are not followed.**

### 1. Create Your Eclipse Project

Create a new Java project in Eclipse with the following naming format:

```
<your-firstname>-<your-lastname>-core-java-labs
```

**Examples:**

| Trainee Name | Project Name |
|---|---|
| Sonu Pande | `sonu-pande-core-java-labs` |
| Amit Kumar | `amit-kumar-core-java-labs` |
| Priya Sharma | `priya-sharma-core-java-labs` |

> Use only lowercase letters and hyphens. No spaces or underscores in the project name.

### 2. Package Naming Convention

Each exercise must live in its **own package**. The package name must follow this pattern:

```
com.<firstname>.lab<lab_number>.ex<exercise_number>
```

**Examples:**

| Exercise | Package |
|---|---|
| Lab 1, Exercise 1 | `com.sonu.lab1.ex1` |
| Lab 1, Exercise 5 | `com.sonu.lab1.ex5` |
| Lab 2, Exercise 3 | `com.sonu.lab2.ex3` |
| Lab 6, Exercise 4 | `com.sonu.lab6.ex4` |

> Use your **first name only** (lowercase) in the package name. If your first name has spaces or special characters, remove them. Example: *Sonu Pande* → `com.sonu`.

### 3. Class Naming

Each exercise should have at least one class. Name your main class descriptively based on what it does:

- **Good:** `CalculateSum`, `SortStrings`, `MirrorImage`
- **Avoid:** `Solution`, `Main`, `Test`, `Exercise1`

For exercises that require a driver/test class, name it `<ClassName>Runner` or `<ClassName>Test`. Example: `CalculateSumRunner`.

### 4. Expected Project Structure

Your project should look like this inside Eclipse (Package Explorer):

```
sonu-pande-core-java-labs/
└── src/
    ├── com/sonu/lab1/ex1/          ← Lab 1, Exercise 1
    │   └── SumOfCubes.java
    ├── com/sonu/lab1/ex5/          ← Lab 1, Exercise 5
    │   ├── CalculateSum.java
    │   └── CalculateSumRunner.java
    ├── com/sonu/lab2/ex1/          ← Lab 2, Exercise 1
    │   └── SecondSmallest.java
    ├── com/sonu/lab3/ex2/          ← Lab 3, Exercise 2
    │   └── MirrorImage.java
    ...
```

> For Labs 4 and 7 (which span multiple classes in a layered structure), keep all classes for that lab under a shared base package: `com.sonu.lab4` and `com.sonu.lab7`, with sub-packages as prescribed in the exercise (e.g., `com.sonu.lab7.bean`, `com.sonu.lab7.service`, `com.sonu.lab7.pl`).

### 5. Sharing Your Work

Once you have completed your exercises (or at the end of each day), share your project with the trainer by following these steps:

1. **Right-click** your project in Eclipse → **Export** → **General** → **Archive File**.
2. Name the archive exactly as your project: e.g., `sonu-pande-core-java-labs.zip`.
3. Share the `.zip` file with the trainer via the agreed channel (email / shared drive / Teams).

> Alternatively, if the batch uses a shared GitHub/GitLab repository, push your project to a branch named after you: e.g., `sonu-pande`. **Do not push to `main`.**

### 6. Checklist Before Submitting

Before sharing your work, make sure:

- [ ] Project name follows the `<firstname>-<lastname>-core-java-labs` format.
- [ ] Every exercise is in its own package (`com.<firstname>.lab<n>.ex<n>`).
- [ ] All classes follow Java naming conventions (see [Appendix A](#appendix-a-naming-conventions)).
- [ ] All mandatory exercises are attempted.
- [ ] Code compiles without errors.
- [ ] You have added a brief comment at the top of each class explaining what it does.
- [ ] No hardcoded paths (e.g., `C:\Users\sonu\...`) that only work on your machine.

---

## General Coding Guidelines

Good habits from day one will make you a better developer. Follow these guidelines across all exercises:

### Write Clean Code

- Use **meaningful variable names**: `sum`, `count`, `studentName` — not `x`, `temp`, `s1`.
- Keep methods **short and focused** — one method should do one thing.
- **Avoid magic numbers** — instead of `if (age > 18)`, declare `final int VOTING_AGE = 18`.
- Add a **brief Javadoc comment** on each method explaining its purpose, parameters, and return value:
  ```java
  /**
   * Calculates the sum of the first n natural numbers divisible by 3 or 5.
   * @param n the upper limit
   * @return the sum
   */
  public int calculateSum(int n) { ... }
  ```

### Handle Edge Cases

Always think about what could go wrong:

- What if the input array is `null` or empty?
- What if `n` is 0 or negative?
- What if the user enters a non-integer where an integer is expected?

Where applicable, validate inputs and handle them gracefully with a meaningful message or exception.

### Use the Right Data Structure

| Situation | Prefer |
|---|---|
| Ordered, allows duplicates | `ArrayList` |
| No duplicates, unsorted | `HashSet` |
| No duplicates, sorted | `TreeSet` |
| Key-value pairs, fast lookup | `HashMap` |
| Key-value pairs, sorted keys | `TreeMap` |
| LIFO (stack behaviour) | `Deque` / `ArrayDeque` |
| FIFO (queue behaviour) | `Queue` / `LinkedList` |

### Test Your Code

Don't just run the happy path. For every method you write, test at least:

- A **normal case** — typical expected input.
- A **boundary case** — e.g., `n = 0`, empty array, single-element array.
- An **invalid/edge case** — negative number, null string, etc.

Create a `Runner` class with a `main` method that calls your method with multiple test inputs and prints the results. Example:

```java
public class CalculateSumRunner {
    public static void main(String[] args) {
        CalculateSum cs = new CalculateSum();
        System.out.println(cs.calculateSum(10));  // Expected: 33
        System.out.println(cs.calculateSum(0));   // Expected: 0
        System.out.println(cs.calculateSum(1));   // Expected: 0
    }
}
```

### Exception Handling Best Practices

- **Never swallow exceptions silently**: avoid `catch (Exception e) {}` with an empty body.
- Print a meaningful message or re-throw when catching exceptions.
- Use **specific exception types** (`NumberFormatException`, `IllegalArgumentException`) rather than catching generic `Exception` unless necessary.
- Use `finally` for cleanup (closing streams, connections), or better yet, use **try-with-resources**:
  ```java
  try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
      // read file
  } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
  }
  ```

### Java 8 Specific Tips

- Prefer **enhanced for-each loops** over indexed loops when you don't need the index.
- Use **`String.format()`** or **`StringBuilder`** instead of `+` concatenation inside loops.
- When working with `Optional` (from Stream API): always check with `isPresent()` or use `orElse()` before accessing the value.
- Lambda expressions are concise — but if a lambda body is more than 2–3 lines, extract it into a named method and use a **method reference** instead.
- For sorting with `Comparator`, prefer the fluent API:
  ```java
  list.sort(Comparator.comparing(Employee::getSalary).reversed());
  ```

---

## Before You Start

Here are some tips to make problem-solving easier:

1. Always check if there are direct API methods available to solve the question easily.
2. Use `Collections.sort()` to sort an `ArrayList`. For arrays, convert them first and then sort.
3. Converting numeric data types to `String` can help solve some problems — e.g., use `charAt()` to compare first digits or to reverse digits of a number.
4. To remove duplicates from an array, convert it to a `Set`. If sorted order is also needed, use `TreeSet`.
5. Try to use Collection, String, and Wrapper APIs wherever possible.
6. While using any API method, explore the other methods in the same API — they may help with other problems.
7. The hints provided are only suggestions. You can use your own algorithm/logic to solve each problem.

---

## Getting Started

### Overview

This lab book is a guided tour for learning Core Java version 8. It comprises assignments to be completed. Refer to demos and work through the assignments by referring to case studies, which will expose you to real-world Java application development.

### Setup Checklist

#### Minimum System Requirements

- Intel Pentium 90 or higher (P166 recommended)
- Microsoft Windows 7 or higher
- Memory: 1 GB or more recommended
- Internet Explorer 9.0+ or Google Chrome 43+

#### Prerequisites

- A text editor like Notepad, Eclipse, or STS is installed.
- JDK 1.8 or above is installed. (Referred to as `<java_home>` throughout this document.)

### Instructions

- For all naming conventions, refer to [Appendix A](#appendix-a-naming-conventions). All lab assignments must adhere to naming conventions.
- Create a directory with your name on `<drive>`. Inside it, create a subdirectory called `java_assignments`. For each lab exercise, create a sub-directory named `lab<lab_number>`.

### Learning More

- [Java SE 8 Documentation — Oracle](https://docs.oracle.com/javase/8/docs/)
- *Java: The Complete Reference* — Herbert Schildt
- *Thinking in Java* — Bruce Eckel
- *Beginning Java 8 Fundamentals* — Kishori Sharan

---

## Lab 1: Flow Control / Operators / Assignments

### Optional Exercises

**Exercise 1:** Create a method to find the sum of the cubes of the digits of an n-digit number.

---

**Exercise 2:** Write a Java program that simulates a traffic light. The program lets the user select one of three lights — red, yellow, or green — using integer inputs. On entering the choice, an appropriate message (`"stop"`, `"ready"`, or `"go"`) should appear in the console.

---

**Exercise 3:** The Fibonacci sequence is defined by the following rule: the first 2 values are `1, 1`; every subsequent value is the sum of the 2 preceding values. Write a Java program that uses both **recursive** and **non-recursive** functions to print the nth value of the Fibonacci sequence.

---

**Exercise 4:** Write a Java program that prompts the user for an integer and then prints all **prime numbers** up to that integer.

---

### Mandatory Exercises

**Exercise 5:** Create a class with a method that calculates the sum of the first `n` natural numbers divisible by 3 or 5.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab1_1]`

| Field | Details |
|---|---|
| **Method Name** | `calculateSum` |
| **Description** | Calculate sum |
| **Argument** | `int n` |
| **Return Type** | `int` |
| **Logic** | Calculate the sum of the first `n` natural numbers that are divisible by 3 or 5. |

---

**Exercise 6:** Create a class with a method to find the difference between the **sum of squares** and the **square of the sum** of the first `n` natural numbers.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab1_2]`

| Field | Details |
|---|---|
| **Method Name** | `calculateDifference` |
| **Description** | Calculate the difference |
| **Argument** | `int n` |
| **Return Type** | `int` |
| **Logic** | Find: `(1² + 2² + ... + n²) - (1 + 2 + ... + n)²`. For example, if `n = 10`, compute the sum of squares minus the square of their sum. |

---

**Exercise 7:** Create a method to check if a number is an **increasing number**.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab1_3]`

| Field | Details |
|---|---|
| **Method Name** | `checkNumber` |
| **Description** | Check if a number is an increasing number |
| **Argument** | `int number` |
| **Return Type** | `boolean` |
| **Logic** | A number is an increasing number if no digit is exceeded by the digit to its left. Example: `134468` is an increasing number. |

---

**Exercise 8:** Create a method to check if a number is a **power of two**.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab1_4]`

| Field | Details |
|---|---|
| **Method Name** | `checkNumber` |
| **Description** | Check if the entered number is a power of two |
| **Argument** | `int n` |
| **Return Type** | `boolean` |
| **Logic** | Check if the input is a power of two. Example: `8` is a power of 2. |

---

## Lab 2: Arrays

### Mandatory Exercises

**Exercise 1:** Create a method that accepts an array of integers and returns the **second smallest** element.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab3_1]`

| Field | Details |
|---|---|
| **Method Name** | `getSecondSmallest` |
| **Description** | Get the second smallest element in the array |
| **Argument** | `int[]` |
| **Return Type** | `int` |
| **Logic** | Sort the array and return the second smallest element. |

---

**Exercise 2:** Create a method that accepts an array of `String` objects, sorts them alphabetically, converts the left half to **UPPERCASE** and the right half to **lowercase**, and returns the resulting array.

> **Note:** If the number of strings is odd, `(n/2) + 1` elements should be in UPPERCASE.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab3_2]`

| Field | Details |
|---|---|
| **Method Name** | `sortStrings` |
| **Description** | Sort an array of strings alphabetically with case formatting |
| **Argument** | `String[] arr` |
| **Return Type** | `String[]` |

---

**Exercise 3:** Create a method that accepts an integer array, **reverses the digits** of each number in the array, and returns the resulting array in **sorted order**.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab3_3]`

| Field | Details |
|---|---|
| **Method Name** | `getSorted` |
| **Description** | Reverse the numbers in the array, then sort and return |
| **Argument** | `int[]` |
| **Return Type** | `int[]` |
| **Hint** | Convert each number to a `String` to reverse it. |

---

**Exercise 4:** Create a method that accepts an integer array, **removes all duplicates**, and returns the resulting array in **descending order**.

| Field | Details |
|---|---|
| **Method Name** | `modifyArray` |
| **Description** | Remove duplicates and sort descending |
| **Argument** | `int[]` |
| **Return Type** | `int[]` |
| **Logic** | Remove duplicate elements and sort the array in descending order. |

---

## Lab 3: Strings and Parsing

**Exercise 1:** Write a Java program that reads a line of integers and displays each integer along with the **sum of all integers**. Use the `StringTokenizer` class.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab6_1]`

---

**Exercise 2:** Create a class with a method that generates the **mirror image** of a String, returning both strings separated by a pipe (`|`) symbol.

| Field | Details |
|---|---|
| **Method Name** | `getImage` |
| **Description** | Generate the mirror image of a String |
| **Argument** | `String` |
| **Return Type** | `String` |
| **Logic** | Find the reverse of the string and concatenate the two with a pipe. Example: Input `EARTH` → Output `EARTH│HTRAE`. **Hint:** Use `StringBuffer.reverse()`. |

---

**Exercise 3:** Create a method that accepts a String and replaces all **consonants** with the next alphabet in the sequence.

> **Note:** Consonants are all alphabets excluding vowels (a, e, i, o, u).

| Field | Details |
|---|---|
| **Method Name** | `alterString` |
| **Description** | Replace consonants with the next character |
| **Argument** | `String` |
| **Return Type** | `String` |
| **Logic** | Return the string with each consonant replaced by the character after it. Example: `JAVA` → `KAWA`. |

---

**Exercise 4:** Create a method that accepts a number and **modifies it** so that each digit of the new number is the absolute difference between two consecutive digits of the original. The units-place digit remains as-is.

| Field | Details |
|---|---|
| **Method Name** | `modifyNumber` |
| **Description** | Modify a number by computing differences between consecutive digits |
| **Argument** | `int number1` |
| **Return Type** | `int` |
| **Logic** | Example: Input `45862` → Output `13242`. Convert the number to a String, extract each character with `charAt()`, compute differences, build a new `StringBuffer`, and convert back to `int`. |

---

**Exercise 5:** Write a Java program that displays the number of **characters**, **lines**, and **words** in a text.

---

**Exercise 8:** Create a method that accepts a String and checks if it is a **positive string**. A string is positive if each character comes after the previous character in alphabetical order (left to right). Return `true` if the string is positive.

> Example: `ANT` is a positive string (T > N > A in alphabetical order).

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab6_5]`

---

**Exercise 9:** Create a method that accepts a date and prints the **duration in days, months, and years** relative to the current system date.

---

## Lab 4: Inheritance and Polymorphism

### Optional Exercise

**Exercise 1:** Create `Person` and `Account` classes as shown in the class diagram below. Ensure a minimum balance of INR 500 is maintained in a bank account.

- Create an `Account` for *Smith* with an initial balance of INR 2000 and for *Kathy* with INR 3000 (`accNum` should be auto-generated).
- Deposit INR 2000 to Smith's account.
- Withdraw INR 2000 from Kathy's account.
- Display the updated balances in both accounts.

**Extend with Inheritance:**

Inherit two classes — `SavingsAccount` and `CurrentAccount` — from the `Account` class:

- **SavingsAccount**
  - Add a `minimumBalance` variable with a `final` modifier.
  - Override `withdraw()` to check for minimum balance before allowing withdrawal.

- **CurrentAccount**
  - Add an `overdraftLimit` variable.
  - Override `withdraw()` to check whether the overdraft limit is reached and return a `boolean` accordingly.

---

**Exercise 2:** Create the following packages and classes:

- **`com.cg.eis.bean`** — Create an `Employee` class with attributes: `id`, `name`, `salary`, `designation`, `insuranceScheme`.

- **`com.cg.eis.service`** — Contains the `EmployeeService` interface and its implementation class, providing:
  - Get employee details from the user.
  - Find the insurance scheme for an employee based on salary and designation.
  - Display all details of an employee.

- **`com.cg.eis.pl`** — Contains code for getting input from the user, producing expected output, and invoking services.

---

### Mandatory Exercise

**Exercise 3:** Using an inheritance hierarchy, design a Java program to model items at a **library** (books, journal articles, videos, and CDs).

1. Implement an **abstract superclass** `Item` with common data: identification number, title, number of copies. Define constructors, getters, setters, `equals`, `toString`, `print`, `checkIn`, `checkOut`, `addItem`, etc.

2. Implement an **abstract subclass** `WrittenItem` (extends `Item`) with added private data: `author`.

3. Implement 2 subclasses of `WrittenItem`:
   - **`Book`** — no new private data; override/overload methods as needed.
   - **`JournalPaper`** — added private data: `yearPublished`; override/overload methods as needed.

4. Implement an **abstract subclass** `MediaItem` (extends `Item`) with added private data: `runtime` (integer).

5. Implement 2 subclasses of `MediaItem`:
   - **`Video`** — added private data: `director`, `genre`, `yearReleased`.
   - **`CD`** — added private data: `artist`, `genre`.

Write the definitions of these classes and a client program demonstrating them in use.

> 📁 Complete this exercise in Eclipse.

---

## Lab 5: Exception Handling

**Exercise 1:** Validate the age of a person and display a proper message using a **user-defined exception**. Age must be above 15.

> 📁 Complete this exercise in Eclipse. `[Eclipse Age Exception]`

---

**Exercise 2:** Write a Java program to validate the **full name** of an employee. Create and throw a user-defined exception if `firstName` or `lastName` is blank.

> 📁 Complete this exercise in Eclipse. `[Eclipse Name Exception]`

---

**Exercise 3:** Create an exception class named `EmployeeException` (user-defined) in a package named `com.cg.eis.exception`. Throw this exception if an employee's salary is below 3000. Use proper exception handling.

---

## Lab 6: Collection Framework

### Mandatory Exercises

**Exercise 1:** Create a method that accepts a `HashMap` and returns its values in **sorted order** as a `List`.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab7_1]`

| Field | Details |
|---|---|
| **Method Name** | `getValues` |
| **Description** | Get the values of a map in sorted order |
| **Argument** | `HashMap` |
| **Return Type** | `List` |
| **Logic** | Return the values of a `HashMap` in sorted order. |

---

**Exercise 2:** Create a method that accepts a **character array** and counts how many times each character appears.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab3_4]`

| Field | Details |
|---|---|
| **Method Name** | `countChars` |
| **Description** | Count occurrences of each character in the array |
| **Argument** | `char[] arr` |
| **Return Type** | `Map<Character, Integer>` |

---

**Exercise 3:** Create a method that accepts an array of numbers and returns a `HashMap` of each number and its **square**.

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab7_3]`

| Field | Details |
|---|---|
| **Method Name** | `getSquares` |
| **Description** | Accepts a list of numbers and returns their squares |
| **Argument** | `int[]` |
| **Return Type** | `Map` |
| **Logic** | Iterate through the array; for each number, add an entry `(number → square)` to the map. |

---

**Exercise 4:** A school offers medals to 10th-grade students based on the following criteria:

| Marks | Medal |
|---|---|
| >= 90 | Gold |
| >= 80 and < 90 | Silver |
| >= 70 and < 80 | Bronze |

Write a method that accepts student marks as a `HashMap` (key = registration number, value = marks) and returns a `HashMap` with the registration numbers of medal-eligible students as keys and their medal type as values.

| Field | Details |
|---|---|
| **Method Name** | `getStudents` |
| **Description** | Generate the list of students eligible for medals |
| **Argument** | `HashMap` |
| **Return Type** | `HashMap` |

---

**Exercise 5:** Create a method that accepts an integer array and returns the **second smallest** element. Use Collection APIs.

| Field | Details |
|---|---|
| **Method Name** | `getSecondSmallest` |
| **Description** | Get the second smallest element in the array |
| **Argument** | `int[]` |
| **Return Type** | `int` |
| **Hint** | Convert to `ArrayList`, then use `Collections.sort()`. |

---

**Exercise 6:** Create a method that accepts a `Map` of person IDs and ages and returns a `List` of IDs of all people **eligible to vote** (age > 18).

| Field | Details |
|---|---|
| **Method Name** | `votersList` |
| **Description** | Generate list of voters based on age |
| **Argument** | `Map` (key = ID, value = Date of Birth) |
| **Return Type** | `List` |
| **Logic** | Check age from date of birth. If age > 18, add the ID to the list. |

---

**Exercise 7:** Create a method that accepts an integer array, **reverses the digits** of each number, and returns the array in **sorted order**.

| Field | Details |
|---|---|
| **Method Name** | `getSorted` |
| **Description** | Reverse the numbers in the array, sort and return |
| **Argument** | `int[]` |
| **Return Type** | `int[]` |
| **Hint** | Convert numbers to `String` to reverse; use Collection APIs to sort. |

---

## Lab 7: Layered Architecture

### Case Study: Employee Medical Insurance Scheme

By default, all employees in an organization are assigned a medical insurance scheme based on their salary range and designation:

| Salary | Designation | Insurance Scheme |
|---|---|---|
| > 5000 and < 20000 | System Associate | Scheme C |
| >= 20000 and < 40000 | Programmer | Scheme B |
| >= 40000 | Manager | Scheme A |
| < 5000 | Clerk | No Scheme |

### Package Structure

- **`com.cg.eis.bean`** — Create an `Employee` class with attributes: `id`, `name`, `salary`, `designation`, `insuranceScheme`.

- **`com.cg.eis.service`** — Contains the `EmployeeService` interface and its implementation class.

- **`com.cg.eis.pl`** — Contains code for user input/output and invoking services.

### Requirements

Implement a class that accepts multiple employee details and stores all employee objects in a `HashMap`. The following functionalities must be implemented:

1. Add employee details to the `HashMap`.
2. Accept an insurance scheme from the user and display employee details matching that scheme.
3. Delete an employee's details from the map.
4. Build expectation (display summary).

> 📁 Complete this exercise in Eclipse. `[Eclipse-Lab11]`

---

## Lab 8: Multithreading

**Exercise 1:** Write a program that does the following using a Thread:

- Create a user-defined thread class called `CopyDataThread.java`.
- This class copies content from `source.txt` to `target.txt`. After every 10 characters copied, display the message: `"10 characters are copied"`. Add a delay of 5 seconds after every 10 characters read.
- Create another class `FileProgram.java` that instantiates the above thread. Pass the required `FileStream` objects to the `CopyDataThread` constructor.

---

**Exercise 2:** Write a thread program to display a **timer** that refreshes every 10 seconds. Use the `Runnable` interface.

---

### Lab 9 (Concurrent Patterns)

**Exercise 1:** Implement the multithreading assignments (from Lab 8) using `Executor` and `ExecutorService` interfaces.

**Exercise 2:** Create an application for registering job seeker details. Validate that:
- The username ends with `_job`.
- There are at least 8 characters to the left of `_job`.

Return `true` if validation passes; `false` otherwise.

---

## Lab 9: Lambda Expressions and Stream API *(Optional)*

**Exercise 1:** Write a lambda expression that accepts `x` and `y` and returns `x * y`.

**Exercise 2:** Write a method using a lambda expression to format a given string by inserting a space between each character. Example: Input `"CG"` → Output `"C G"`.

**Exercise 3:** Write a method using a lambda expression that accepts a `username` and `password` and returns `true` or `false`. *(Hint: Use custom values for authentication.)*

**Exercise 4:** Write a class with a `main` method to demonstrate **instance creation using method reference**. *(Hint: Create a simple class with attributes, getters, and setters.)*

**Exercise 5:** Write a method to calculate the **factorial** of a number. Test it using the method reference feature.

---

### Stream API Case Study

Use the following class diagram representing `Employee` and `Department` entities. Also refer to an `EmployeeRepository` class that populates employee collections with sample data.

Create an `EmployeeService` class that queries the collections from `EmployeeRepository` for the following requirements. Create a **separate method** for each.

> **Note:** Each requirement must be solved using lambda expressions / Stream API. At least 5 questions are mandatory; solving all is recommended.

1. Find the **sum of salary** of all employees.
2. List **department names** and the **count of employees** in each department.
3. Find the **senior-most employee** in the organization.
4. List **employee name** and **duration of service** in months and days.
5. Find employees **without a department**.
6. Find departments **without employees**.
7. Find the department(s) with the **highest count of employees**.
8. List employee name, hire date, and the **day of the week** they started.
9. List employee name and hire date for employees who **started on Friday**. *(Hint: Accept a day name like `FRIDAY` and filter accordingly.)*
10. List employee name and their **manager's name** in the format: `"employee name reports to manager name"`.
11. List employee name, salary, and **salary increased by 15%**.
12. Find employees who **don't report to anyone** (employees without a manager).
13. Create a method that accepts a manager's first and last name and prints the **names of all their subordinates**.
14. Sort employees by:
    - Employee ID
    - Department ID
    - First name

---

## Lab 10: File Handling *(Optional)*

**Exercise 1:** Write a Java program that reads a file and displays its content on screen, with a **line number** before each line.

**Exercise 2:** Write a Java program that reads a file name from the user and then displays:
- Whether the file exists
- Whether the file is readable
- Whether the file is writable
- The type of the file
- The length of the file in bytes

---

## Appendix A: Naming Conventions

### Packages

Package names are written in **all lowercase** to avoid conflict with class or interface names. Companies use their reversed Internet domain name as a prefix — e.g., `com.cg.mypackage`. Packages in the Java language itself begin with `java.` or `javax.`.

### Classes and Interfaces

The first letter should be **capitalized**. If multiple words are combined, each inner word should be capitalized (camelCase).

- **Classes** — names should typically be **nouns**:
  - `Dog`
  - `Account`
  - `PrintWriter`

- **Interfaces** — names should typically be **adjectives**:
  - `Runnable`
  - `Serializable`

### Methods

The first letter should be **lowercase**, followed by camelCase. Names should typically be **verb-noun pairs**:

- `getBalance`
- `doCalculation`
- `setCustomerName`

### Variables

Use **camelCase** starting with a lowercase letter. Keep names short and meaningful:

- `buttonWidth`
- `accountBalance`
- `myString`

### Constants

Constants are `static final` variables and should be named in **UPPERCASE with underscores** as separators:

- `MIN_HEIGHT`
