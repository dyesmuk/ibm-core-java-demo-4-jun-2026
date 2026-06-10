# Core Java 8 Lab Book

> **Original Document:** [Google Docs](https://docs.google.com/document/d/1jsVLzZG5nsWCwYn2jICkMcnCrlsq_IC2XRElmjclnak/edit?usp=sharing)

---

## Table of Contents

- [Before You Start](#before-you-start)
- [Getting Started](#getting-started)
  - [Overview](#overview)
  - [Setup Checklist](#setup-checklist-for-core-java)
  - [Instructions](#instructions)
  - [Learning More](#learning-more)
- [Lab 1: Flow Control / Operators / Assignments](#lab-1-flow-controloperatorsassignments)
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

## Before You Start

Before developing solutions, here are some tips to make problem solving easier:

1. Always check if there are any direct API methods available to solve the question easily.
2. Use `Collections.sort()` to sort an `ArrayList`. For arrays, convert first then sort.
3. Converting numeric data types to `String` may help solve some problems — e.g., use `charAt()` to check first digits or reverse digits of a number.
4. To remove duplicates from an array, convert it to a `Set`. Use `TreeSet` if sorted order is required.
5. Try to use Collection, String, and Wrapper APIs wherever possible.
6. While using any API method, explore other methods in the same API — they may help in other problems.
7. The hints provided are just to guide you. You can use your own algorithm/logic to solve the problem.

---

## Getting Started

### Overview

This lab book is a guided tour for learning Core Java version 8. It comprises assignments to be done. Refer to the demos and work out the assignments given by referring to the case studies, which will expose you to working with Java applications.

### Setup Checklist for Core Java

**Minimum System Requirements**

- Intel Pentium 90 or higher (P166 recommended)
- Microsoft Windows 7 or higher
- Memory: 1 GB or more recommended
- Internet Explorer 9.0 or higher, or Google Chrome 43 or higher

**Please ensure the following is done:**

- A text editor like Notepad, Eclipse, or STS is installed.
- JDK 1.8 or above is installed. (This path is henceforth referred to as `<java_home>`)

### Instructions

- For all naming conventions, refer to [Appendix A](#appendix-a-naming-conventions). All lab assignments should adhere to naming conventions.
- Create a directory by your name in `<drive>`. Inside it, create a subdirectory `java_assignments`. For each lab exercise, create a directory as `lab<lab_number>`.

### Learning More

- [Oracle Java 8 Docs](https://docs.oracle.com/javase/8/docs/)
- *Java, The Complete Reference* — Herbert Schildt
- *Thinking in Java* — Bruce Eckel
- *Beginning Java 8 Fundamentals* — Kishori Sharan

---

## Lab 1: Flow Control/Operators/Assignments

### Optional Exercises

**Exercise 1:** Create a method to find the sum of the cubes of the digits of an n-digit number.

**Exercise 2:** Write a Java program that simulates a traffic light. The program lets the user select one of three lights — red, yellow, or green — using int inputs. On entering the choice, an appropriate message (`"stop"`, `"ready"`, or `"go"`) should appear in the console.

**Exercise 3:** The Fibonacci sequence is defined by the rule: the first 2 values are 1, 1, and every subsequent value is the sum of the 2 preceding values. Write a Java program that uses both recursive and non-recursive functions to print the nth value of the Fibonacci sequence.

**Exercise 4:** Write a Java program that prompts the user for an integer and then prints out all prime numbers up to that integer.

---

### Mandatory Exercises

**Exercise 5:** Create a class with a method that calculates the sum of the first `n` natural numbers which are divisible by 3 or 5.

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab1_1]`

| Property           | Details                                                            |
|--------------------|--------------------------------------------------------------------|
| Method Name        | `calculateSum`                                                     |
| Method Description | Calculate Sum                                                      |
| Argument           | `int n`                                                            |
| Return Type        | `int` – sum                                                        |
| Logic              | Calculate the sum of first n natural numbers divisible by 3 or 5. |

---

**Exercise 6:** Create a class with a method to find the difference between the sum of the squares and the square of the sum of the first `n` natural numbers.

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab1_2]`

| Property           | Details                                                                                     |
|--------------------|---------------------------------------------------------------------------------------------|
| Method Name        | `calculateDifference`                                                                       |
| Method Description | Calculate the difference                                                                    |
| Argument           | `int n`                                                                                     |
| Return Type        | `int` – Sum                                                                                 |
| Logic              | Find the difference between the sum of squares and the square of the sum of first n numbers. Example for n=10: `(1²+2²+...+10²) - (1+2+...+10)²` |

---

**Exercise 7:** Create a method to check if a number is an increasing number.

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab1_3]`

| Property           | Details                                                                                          |
|--------------------|--------------------------------------------------------------------------------------------------|
| Method Name        | `checkNumber`                                                                                    |
| Method Description | Check if a number is an increasing number                                                        |
| Argument           | `int number`                                                                                     |
| Return Type        | `boolean`                                                                                        |
| Logic              | A number is an increasing number if no digit exceeds the digit to its right. Example: `134468` is an increasing number. |

---

**Exercise 8:** Create a method to check if a number is a power of two or not.

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab1_4]`

| Property           | Details                                              |
|--------------------|------------------------------------------------------|
| Method Name        | `checkNumber`                                        |
| Method Description | Checks if the entered number is a power of two       |
| Argument           | `int n`                                              |
| Return Type        | `boolean`                                            |
| Logic              | Check if the input is a power of two. Example: `8` is a power of 2. |

---

## Lab 2: Arrays

### Mandatory Exercises

**Exercise 1:** Create a method which accepts an array of integer elements and returns the second smallest element in the array.

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab3_1]`

| Property           | Details                                                    |
|--------------------|------------------------------------------------------------|
| Method Name        | `getSecondSmallest`                                        |
| Method Description | Get the second smallest element in the array               |
| Argument           | `int[]`                                                    |
| Return Type        | `int`                                                      |
| Logic              | Sort the array and return the second smallest element.     |

---

**Exercise 2:** Create a method that accepts an array of `String` objects, sorts them alphabetically, converts the left half to UPPERCASE and the right half to lowercase, and returns the resulting array.

> **Note:** If there are an odd number of strings, `(n/2) + 1` elements should be in UPPERCASE.

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab3_2]`

| Property           | Details                                                     |
|--------------------|-------------------------------------------------------------|
| Method Name        | `sortStrings`                                               |
| Method Description | Accept an array of String objects and sort alphabetically   |
| Argument           | `String[] arr`                                              |
| Return Type        | `String`                                                    |

---

**Exercise 3:** Create a method which accepts an integer array, reverses the numbers in the array, and returns the resulting array in sorted order.

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab3_3]`

| Property           | Details                                                                       |
|--------------------|-------------------------------------------------------------------------------|
| Method Name        | `getSorted`                                                                   |
| Method Description | Return the resulting array after reversing the numbers and sorting it         |
| Argument           | `int[]`                                                                       |
| Return Type        | `int`                                                                         |
| Logic              | Accept an integer array, reverse the numbers, sort it, and return the result. **Hint:** Convert numbers to `String` to reverse them. |

---

**Exercise 4:** Create a method which accepts an integer array, removes all duplicates, and returns the resulting array in descending order.

| Property           | Details                                                              |
|--------------------|----------------------------------------------------------------------|
| Method Name        | `modifyArray`                                                        |
| Method Description | Remove duplicates                                                    |
| Argument           | `int[]`                                                              |
| Return Type        | `int[]`                                                              |
| Logic              | Remove duplicate elements and sort in descending order.              |

---

## Lab 3: Strings and Parsing

**Exercise 1:** Write a Java program that reads a line of integers and then displays each integer and the sum of all integers. (Use `StringTokenizer` class.)

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab6_1]`

---

**Exercise 2:** Create a class containing a method to create the mirror image of a String. The method should return the two Strings separated with a pipe (`|`) symbol.

| Property           | Details                                                                                      |
|--------------------|----------------------------------------------------------------------------------------------|
| Method Name        | `getImage`                                                                                   |
| Method Description | Generate the mirror image of a String and append it to the original.                         |
| Argument           | `String`                                                                                     |
| Return Type        | `String`                                                                                     |
| Logic              | Find the mirror image of the String and join them with a `\|` separator. Example: Input: `EARTH` → Output: `EARTH\|HTRAE`. **Hint:** Use `StringBuffer.reverse()`. |

---

**Exercise 3:** Create a method which accepts a String and replaces all the consonants with the next alphabet.

> **Note:** Consonants are all alphabets excluding vowels (a, e, i, o, u).

| Property           | Details                                                                 |
|--------------------|-------------------------------------------------------------------------|
| Method Name        | `alterString`                                                           |
| Method Description | Replace consonants with the next character                              |
| Argument           | `String`                                                                |
| Return Type        | `String`                                                                |
| Logic              | Replace all consonants with the next character in the alphabet. Example: `JAVA` → `KAWA` |

---

**Exercise 4:** Create a method that accepts a number and modifies it so that each digit in the new number equals the absolute difference between two consecutive digits in the original number. The unit's place digit remains unchanged.

| Property           | Details                                                                                              |
|--------------------|------------------------------------------------------------------------------------------------------|
| Method Name        | `modifyNumber`                                                                                       |
| Method Description | Accept a number and modify it as per the requirement                                                 |
| Argument           | `int number1`                                                                                        |
| Return Type        | `int`                                                                                                |
| Logic              | Example: Input `45862` → Output `13242`. **Algorithm:** Convert number to String → extract each char using `charAt()` → find absolute difference → build new `StringBuffer` → convert back to int. |

---

**Exercise 5:** Write a Java program that displays the number of characters, lines, and words in a text.

---

**Exercise 8:** Create a method that accepts a String and checks if it is a "positive string" — where each character comes after the previous one in alphabetical order from left to right. Return `true` if positive.

> Example: `ANT` is a positive String (T > N > A alphabetically).

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab6_5]`

---

**Exercise 9:** Create a method to accept a date and print the duration in days, months, and years with respect to the current system date.

---

## Lab 4: Inheritance and Polymorphism

### Optional Exercises

**Exercise 1:** Create `Person` and `Account` classes as shown in the class diagram. Ensure a minimum balance of INR 500 in a bank account.

Tasks:
- (a) Create an Account for Smith with initial balance INR 2000, and for Kathy with INR 3000. (`accNum` should be auto-generated.)
- (b) Deposit INR 2000 to Smith's account.
- (c) Withdraw INR 2000 from Kathy's account.
- (d) Display updated balances for both accounts.
- (e) Extend via Inheritance and Polymorphism — inherit `SavingsAccount` and `CurrentAccount` from `Account`.

**SavingsAccount:**
- Add a `final` variable `minimumBalance`.
- Override `withdraw()` — check for minimum balance before allowing withdrawal.

**CurrentAccount:**
- Add a variable `overdraftLimit`.
- Override `withdraw()` — check if overdraft limit is reached and return a boolean accordingly.

---

**Exercise 2:** Create packages and classes as given below:

- **`com.cg.eis.bean`** — Create an `Employee` class with attributes: `id`, `name`, `salary`, `designation`, `insuranceScheme`.
- **`com.cg.eis.service`** — Contains `EmployeeService` interface and its implementation class with:
  1. Get employee details from user.
  2. Find the insurance scheme for an employee based on salary and designation.
  3. Display all details of an employee.
- **`com.cg.eis.pl`** — Contains code for getting user input, producing output, and invoking services.

---

### Mandatory Exercises

**Exercise 3:** Using an inheritance hierarchy, design a Java program to model items at a library (books, journal articles, videos, and CDs).

1. Implement an abstract superclass `Item` with private data: identification number, title, number of copies. Define common operations: constructors, getters, setters, `equals`, `toString`, `print`, `checkIn`, `checkOut`, `addItem`, etc.
2. Implement an abstract subclass `WrittenItem` (extends `Item`) with added private data: `author`.
3. Implement 2 subclasses of `WrittenItem`:
   - **`Book`** — no new private data; override/overload methods as needed.
   - **`JournalPaper`** — added private data: `yearPublished`; override/overload as needed.
4. Implement an abstract subclass `MediaItem` (extends `Item`) with added private data: `runtime` (int).
5. Implement 2 subclasses of `MediaItem`:
   - **`Video`** — added private data: `director`, `genre`, `yearReleased`.
   - **`CD`** — added private data: `artist`, `genre`.

Write class definitions and a client program demonstrating their use.

> **Note:** Complete this exercise in Eclipse.

---

## Lab 5: Exception Handling

**Exercise 1:** Validate the age of a person and display a proper message using a user-defined exception. Age should be above 15.

> **Note:** Complete this exercise in Eclipse. `[Eclipse Age Exception]`

---

**Exercise 2:** Write a Java program to validate the full name of an employee. Create and throw a user-defined exception if `firstName` and `lastName` are blank.

> **Note:** Complete this exercise in Eclipse. `[Eclipse Name Exception]`

---

**Exercise 3:** Create an exception class `EmployeeException` (user-defined) in the package `com.cg.eis.exception` and throw it if an employee's salary is below 3000. Use proper exception handling.

---

## Lab 6: Collection Framework

### Mandatory Exercises

**Exercise 1:** Create a method which accepts a `HashMap` and returns the values of the map in sorted order as a `List`.

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab7_1]`

| Property           | Details                                    |
|--------------------|--------------------------------------------|
| Method Name        | `getValues`                                |
| Method Description | Get the values of a map in sorted order    |
| Argument           | `HashMap`                                  |
| Return Type        | `List`                                     |
| Logic              | Return the values of a HashMap in sorted order. |

---

**Exercise 2:** Create a method that accepts a character array and counts the number of times each character appears.

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab3_4]`

| Property           | Details                                                   |
|--------------------|-----------------------------------------------------------|
| Method Name        | `countChars`                                              |
| Method Description | Count occurrences of each character in the array          |
| Argument           | `char[] arr`                                              |
| Return Type        | `Map<Character, Integer>`                                 |

---

**Exercise 3:** Create a method which accepts an array of numbers and returns the numbers and their squares in a `HashMap`.

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab7_3]`

| Property           | Details                                                                               |
|--------------------|---------------------------------------------------------------------------------------|
| Method Name        | `getSquares`                                                                          |
| Method Description | Accept a list of numbers and return their squares                                     |
| Argument           | `int[]`                                                                               |
| Return Type        | `Map`                                                                                 |
| Logic              | Iterate through the list, find the square of each number, and add to a map (number as key, square as value). |

---

**Exercise 4:** A school offers medals based on the following criteria:

| Condition                     | Medal  |
|-------------------------------|--------|
| Marks >= 90                   | Gold   |
| Marks >= 80 and < 90          | Silver |
| Marks >= 70 and < 80          | Bronze |

Write a function that accepts marks as a `HashMap` (registration number → marks) and returns a `HashMap` (registration number → medal type) for eligible students.

| Property           | Details                                                              |
|--------------------|----------------------------------------------------------------------|
| Method Name        | `getStudents`                                                        |
| Method Description | Generate the list of students eligible for medals                    |
| Argument           | `HashMap`                                                            |
| Return Type        | `HashMap`                                                            |
| Logic              | Return details of students eligible for medals along with medal type. |

---

**Exercise 5:** Create a method which accepts an integer array and returns the second smallest element.

| Property           | Details                                                                                                         |
|--------------------|-----------------------------------------------------------------------------------------------------------------|
| Method Name        | `getSecondSmallest`                                                                                             |
| Method Description | Get the second smallest element in the array                                                                    |
| Argument           | `int[]`                                                                                                         |
| Return Type        | `int`                                                                                                           |
| Logic              | Sort the array and return the second smallest element. **Hint:** 1. Convert to `ArrayList`. 2. Use `Collections.sort()`. |

---

**Exercise 6:** Create a method which accepts a `Map` of person ID and age, and returns a list of IDs of people eligible to vote (age > 18).

| Property           | Details                                                                                      |
|--------------------|----------------------------------------------------------------------------------------------|
| Method Name        | `votersList`                                                                                 |
| Method Description | Generate the list of voters based on ages                                                    |
| Argument           | `Map`                                                                                        |
| Return Type        | `List`                                                                                       |
| Logic              | Accept a map with ID as key and Date of Birth as value. Check eligibility (age > 18) and return list of eligible IDs. |

---

**Exercise 7:** Create a method which accepts an integer array, reverses the numbers, and returns the resulting array in sorted order.

| Property           | Details                                                                                             |
|--------------------|-----------------------------------------------------------------------------------------------------|
| Method Name        | `getSorted`                                                                                         |
| Method Description | Return the resulting array after reversing the numbers and sorting it                               |
| Argument           | `int[]`                                                                                             |
| Return Type        | `int`                                                                                               |
| Logic              | Accept an integer array, reverse the numbers, sort it, and return the result. **Hint:** 1. Convert numbers to `String` to reverse. 2. Use Collection APIs to sort. |

---

## Lab 7: Layered Architecture

### Case Study: Employee Medical Insurance Scheme

By default, all employees are assigned a medical insurance scheme based on their salary range and designation:

| Salary                  | Designation       | Insurance Scheme |
|-------------------------|-------------------|------------------|
| > 5000 and < 20000      | System Associate  | Scheme C         |
| >= 20000 and < 40000    | Programmer        | Scheme B         |
| >= 40000                | Manager           | Scheme A         |
| < 5000                  | Clerk             | No Scheme        |

**Packages:**

- **`com.cg.eis.bean`** — `Employee` class with attributes: `id`, `name`, `salary`, `designation`, `insuranceScheme`.
- **`com.cg.eis.service`** — `EmployeeService` interface and its implementation class.
- **`com.cg.eis.pl`** — Code for user input/output and invoking services.

**Functionalities to implement:**

- Add employee details to a `HashMap`.
- Accept an insurance scheme from user and display employee details based on that scheme.
- Delete an employee from the map.
- Build expectations (display/output).

> **Note:** Complete this exercise in Eclipse. `[Eclipse-Lab11]`

---

## Lab 8: Multithreading

**Exercise 1:** Write a program to do the following using `Thread`:

- Create a user-defined Thread class `CopyDataThread.java`.
- This class copies content from `source.txt` to `target.txt`, printing `"10 characters are copied"` after every 10 characters. Add a 5-second delay after every 10 characters read.
- Create a class `FileProgram.java` which creates the above thread, passes the required `FileStream` classes to the constructor, and implements the functionality.

---

**Exercise 2:** Write a thread program to display a timer that refreshes every 10 seconds. (Use `Runnable` implementation.)

---

## Lab 09: Concurrent Patterns in Java

**Exercise 1:** Implement the Multithreading assignments using `Executor` and `ExecutorService` interfaces.

**Exercise 2:** Create an application for registering jobseeker details. Validate that:
- The username always ends with `_job`.
- There are at least 8 characters to the left of `_job`.

Return `true` if validation passes, `false` otherwise.

---

## Lab 9: Lambda Expressions and Stream API — *Optional*

**Exercise 1:** Write a lambda expression that accepts `x` and `y` and returns `x^y`.

**Exercise 2:** Write a method using a lambda expression to format a given string by inserting a space between each character. Example: Input `"CG"` → Output `"C G"`.

**Exercise 3:** Write a method using a lambda expression that accepts a username and password and returns `true` or `false`. (**Hint:** Use any custom values for authentication.)

**Exercise 4:** Write a class with a `main` method to demonstrate instance creation using method references. (**Hint:** Create a simple class with attributes, getters, and setters.)

**Exercise 5:** Write a method to calculate the factorial of a number. Test it using method reference.

---

### Case Study: Stream API

Use the `Employee` and `Department` class diagram and an `EmployeeRepository` class (which creates and populates employee collections with sample data).

Create an `EmployeeService` class that queries the collections from `EmployeeRepository` for the following requirements. Create a separate method for each. (**Note:** Use lambda expressions/Stream API. Solve at least 5 — solving all is strongly recommended.)

- Find the sum of salary of all employees.
- List department names and the count of employees in each department.
- Find the most senior employee of the organization.
- List employee name and their duration of service in months and days.
- Find employees without a department.
- Find departments without employees.
- Find departments with the highest count of employees.
- List employee name, hire date, and the day of the week they started.
- List employee name and hire date for employees who started on Friday. (**Hint:** Accept day name e.g., `FRIDAY`.)
- List employee names and the name of the manager they report to, in the format: `"employee name reports to manager name"`.
- List employee name, salary, and salary increased by 15%.
- Find employees who don't report to anyone (i.e., employees without a manager).
- Create a method to accept a manager's first and last name and print all subordinates.
- Sort employees by:
  - Employee ID
  - Department ID
  - First name

---

## Lab 10: File Handling — *Optional*

**Exercise 1:** Write a Java program that reads a file and displays it on screen with a line number before each line.

**Exercise 2:** Write a Java program that reads a file name from the user and then displays:
- Whether the file exists
- Whether the file is readable
- Whether the file is writable
- The type of file
- The length of the file in bytes

---

## Appendix A: Naming Conventions

### Packages

Package names are written in **all lowercase** to avoid conflicts with class or interface names. Companies use their reversed Internet domain name to begin package names (e.g., `com.cg.mypackage`). Java language packages begin with `java.` or `javax.`.

### Classes and Interfaces

The first letter should be **capitalized**. If multiple words are joined, the first letter of each inner word should be uppercase (**CamelCase**).

- Classes — typically **nouns**: `Dog`, `Account`, `PrintWriter`
- Interfaces — typically **adjectives**: `Runnable`, `Serializable`

### Methods

The first letter should be **lowercase**, followed by camelCase. Names should typically be **verb-noun pairs**:

- `getBalance`
- `doCalculation`
- `setCustomerName`

### Variables

Use **camelCase** starting with a lowercase letter. Use short, meaningful names:

- `buttonWidth`
- `accountBalance`
- `myString`

### Constants

Java constants are `static final` variables. Name them in **UPPERCASE** with underscores as separators:

- `MIN_HEIGHT`
