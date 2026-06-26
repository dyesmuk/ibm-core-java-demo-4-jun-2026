# TypeScript Practice Assignments

These assignments are arranged from basic to advanced and are suitable
for trainees who already know HTML, CSS, JavaScript, Node.js, and
Express, and will be learning Angular and React.

------------------------------------------------------------------------

## Assignment 1: Convert JavaScript to TypeScript

### JavaScript Code

``` javascript
function add(a, b) {
    return a + b;
}

console.log(add(10, 20));
```

### Tasks

-   Convert it to TypeScript.
-   Add parameter types.
-   Add a return type.
-   Compile using `tsc`.

------------------------------------------------------------------------

## Assignment 2: Variables and Types

Declare variables for:

-   Name
-   Age
-   IsEmployed
-   Salary
-   Skills (array)
-   Address (object)

Use proper TypeScript types.

### Expected Output

``` text
Name : John
Age : 25
Employed : true
Skills : HTML,CSS,JS
```

------------------------------------------------------------------------

## Assignment 3: Function Types

Write functions for:

-   Addition
-   Subtraction
-   Multiplication
-   Division

Each function must:

-   Accept only numbers.
-   Return a number.

Also create:

``` typescript
function printResult(result: number): void
```

------------------------------------------------------------------------

## Assignment 4: Optional and Default Parameters

Create:

``` typescript
function greet(name: string, city?: string)
```

Output:

``` text
Hello John
```

or

``` text
Hello John from Hyderabad
```

Then modify it to use a default value:

``` typescript
city = "India"
```

------------------------------------------------------------------------

## Assignment 5: Arrays

Given:

``` typescript
const numbers = [10, 20, 30, 40, 50];
```

Find:

-   Sum
-   Average
-   Largest
-   Smallest

Do **not** use helper functions like `Math.max(...numbers)`.

------------------------------------------------------------------------

## Assignment 6: Interfaces

Create an interface named `Employee`.

Fields:

-   id
-   name
-   salary
-   department

Create three employee objects and print them.

------------------------------------------------------------------------

## Assignment 7: Classes

Create a class `Student`.

### Properties

-   id
-   name
-   marks

### Methods

-   `display()`
-   `isPassed()`

Passing marks = **40**.

Create three student objects.

------------------------------------------------------------------------

## Assignment 8: Inheritance

Create the following hierarchy:

``` text
Person
   ↓
Employee
   ↓
Manager
```

Each class should introduce additional properties.

Override the `display()` method and use `super`.

------------------------------------------------------------------------

## Assignment 9: Access Modifiers

Create:

``` typescript
class BankAccount
```

Fields:

-   `private balance`
-   `public owner`
-   `readonly accountNumber`

Methods:

-   `deposit()`
-   `withdraw()`
-   `getBalance()`

Verify that `account.balance` cannot be accessed directly.

------------------------------------------------------------------------

## Assignment 10: Abstract Class

Create an abstract class:

``` typescript
abstract class Shape
```

with:

``` typescript
abstract area(): number;
```

Implement:

-   Circle
-   Rectangle
-   Triangle

------------------------------------------------------------------------

## Assignment 11: Interface vs Class

Create an interface:

``` typescript
interface Vehicle
```

Methods:

-   `start()`
-   `stop()`

Implement it in:

-   Car
-   Bike
-   Bus

------------------------------------------------------------------------

## Assignment 12: Generics

Create:

``` typescript
function swap<T>(a: T, b: T)
```

Test with:

``` typescript
swap(10, 20);
swap("A", "B");
swap(true, false);
```

------------------------------------------------------------------------

## Assignment 13: Generic Class

Create:

``` typescript
class Stack<T>
```

Methods:

-   `push()`
-   `pop()`
-   `peek()`
-   `display()`

Test with:

-   number
-   string
-   boolean

------------------------------------------------------------------------

## Assignment 14: Enums

Create:

``` typescript
enum Day
```

Print today's day using a `switch` statement.

------------------------------------------------------------------------

## Assignment 15: Union Types

Create:

``` typescript
function printId(id: number | string)
```

Example outputs:

``` text
ID : 100
ID : EMP101
```

------------------------------------------------------------------------

## Assignment 16: Literal Types

Create:

``` typescript
function move(direction: "left" | "right" | "up" | "down")
```

Calling:

``` typescript
move("north");
```

should produce a compilation error.

------------------------------------------------------------------------

## Assignment 17: Type Alias

Create:

``` typescript
type Employee = {
    // fields
};
```

Create five employees and find the employee with the highest salary.

------------------------------------------------------------------------

## Assignment 18: Modules

Create `math.ts` containing:

-   add()
-   subtract()
-   multiply()
-   divide()

Import it into `app.ts`.

------------------------------------------------------------------------

## Assignment 19: Exception Handling

Create:

``` typescript
function divide(a: number, b: number)
```

Throw an error if `b == 0`.

Catch and display the error.

------------------------------------------------------------------------

## Assignment 20: Mini Project -- Employee Management System

Menu:

``` text
1. Add Employee
2. View Employees
3. Search Employee
4. Delete Employee
5. Update Salary
6. Exit
```

Use:

-   Interface
-   Class
-   Array
-   Functions
-   Modules
-   Generics (optional)

No database.

------------------------------------------------------------------------

## Assignment 21: Generic Repository (Angular Preparation)

Create:

``` typescript
class Repository<T>
```

Methods:

-   add()
-   remove()
-   findById()
-   findAll()

Test with:

-   Employee
-   Student
-   Product

------------------------------------------------------------------------

## Assignment 22: Shopping Cart (React Preparation)

Create a `Product` with:

-   id
-   name
-   price
-   quantity

Create a `ShoppingCart` with:

-   addProduct()
-   removeProduct()
-   calculateTotal()
-   displayCart()

Use interfaces and classes.

------------------------------------------------------------------------

## Assignment 23: API Response Types

Given:

``` json
{
  "success": true,
  "message": "Employees fetched",
  "data": [
    {
      "id": 1,
      "name": "John"
    }
  ]
}
```

Create suitable interfaces.

Create an object of that type and print the data.

------------------------------------------------------------------------

## Assignment 24: Student Grade Calculator

Input:

-   Student Name
-   Marks in five subjects

Calculate:

-   Total
-   Average
-   Grade

Use:

-   Interfaces
-   Classes
-   Functions

------------------------------------------------------------------------

## Assignment 25: Library Management System

Book fields:

-   id
-   title
-   author
-   price
-   issued

Operations:

-   Add Book
-   Issue Book
-   Return Book
-   Search Book
-   Remove Book

Use:

-   Interfaces
-   Classes
-   Modules
-   Generics (optional)
