# TypeScript Progressive Assignments

## Employee Management System

Instead of solving independent problems, you will build **one Employee
Management System** throughout the course. Every assignment extends the
previous one.

------------------------------------------------------------------------

## Assignment 1 -- Project Setup

Create a TypeScript project.

### Tasks

-   Initialize npm.
-   Install TypeScript.
-   Create `tsconfig.json`.
-   Create `src/app.ts`.
-   Compile and run.
-   Print:

``` text
Employee Management System
```

### Concepts

-   TypeScript Compiler
-   tsconfig
-   Project structure

------------------------------------------------------------------------

## Assignment 2 -- Variables and Types

Extend Assignment 1.

Create variables for one employee.

-   employeeId
-   employeeName
-   salary
-   isPermanent
-   joiningDate

Print the employee details.

### Concepts

-   Basic Types
-   Type Inference

------------------------------------------------------------------------

## Assignment 3 -- Functions

Instead of printing directly, create:

``` typescript
displayEmployee()
calculateAnnualSalary()
```

Output:

``` text
Monthly Salary : 50000
Annual Salary : 600000
```

### Concepts

-   Function Types
-   Return Types

------------------------------------------------------------------------

## Assignment 4 -- Objects

Replace individual variables with a single employee object.

``` typescript
const employee = {
    ...
}
```

Pass it to:

``` typescript
displayEmployee(employee)
```

### Concepts

-   Object Types

------------------------------------------------------------------------

## Assignment 5 -- Arrays

Instead of one employee, maintain:

``` typescript
const employees = [];
```

Add five employees.

Calculate:

-   Total Salary
-   Average Salary
-   Highest Salary

### Concepts

-   Arrays
-   Loops

------------------------------------------------------------------------

## Assignment 6 -- Interfaces

Create:

``` typescript
interface Employee
```

Use it throughout the project.

------------------------------------------------------------------------

## Assignment 7 -- Optional Properties

Add optional fields:

-   email?
-   phone?
-   managerId?

------------------------------------------------------------------------

## Assignment 8 -- Type Alias

Create:

``` typescript
type EmployeeID = number;
```

Use it throughout the project.

------------------------------------------------------------------------

## Assignment 9 -- Enums

Create:

``` typescript
enum Department {
    HR,
    IT,
    Finance,
    Sales,
    Admin
}
```

Assign every employee to a department.

------------------------------------------------------------------------

## Assignment 10 -- Classes

Replace interface implementation with:

``` typescript
class Employee
```

Methods:

-   display()
-   incrementSalary()
-   changeDepartment()

------------------------------------------------------------------------

## Assignment 11 -- Constructors

Initialize all properties through a constructor.

------------------------------------------------------------------------

## Assignment 12 -- Access Modifiers

Make:

-   salary → private

Provide:

-   getSalary()
-   setSalary()

------------------------------------------------------------------------

## Assignment 13 -- Readonly

Make Employee ID `readonly`.

------------------------------------------------------------------------

## Assignment 14 -- Inheritance

Create:

``` text
Person
   ↓
Employee
   ↓
Manager
```

Manager has `teamSize`.

Override `display()`.

------------------------------------------------------------------------

## Assignment 15 -- Abstract Classes

Convert `Person` into an abstract class.

Implement `display()` differently in Employee and Manager.

------------------------------------------------------------------------

## Assignment 16 -- Interfaces

Create:

``` typescript
interface Payable
```

Method:

``` typescript
calculateSalary()
```

Implement it in Employee.

------------------------------------------------------------------------

## Assignment 17 -- Generics

Create:

``` typescript
class Repository<T>
```

Use it with:

-   Employee
-   Department
-   Project

------------------------------------------------------------------------

## Assignment 18 -- Generic Repository

Add methods:

-   add()
-   update()
-   delete()
-   find()
-   findAll()

------------------------------------------------------------------------

## Assignment 19 -- Modules

Split the project:

``` text
src/
    Employee.ts
    Department.ts
    Project.ts
    Repository.ts
    app.ts
```

------------------------------------------------------------------------

## Assignment 20 -- Exception Handling

Throw errors for:

-   Negative salary
-   Duplicate Employee ID
-   Empty employee name

Handle all exceptions.

------------------------------------------------------------------------

## Assignment 21 -- Project Entity

Create a `Project` class.

Fields:

-   projectId
-   projectName
-   budget

Assign employees to projects.

------------------------------------------------------------------------

## Assignment 22 -- Department Entity

Create a `Department` class.

Fields:

-   departmentId
-   departmentName
-   location

Employees belong to departments.

------------------------------------------------------------------------

## Assignment 23 -- API Models

Create interfaces matching:

``` json
{
  "success": true,
  "message": "Fetched",
  "data": []
}
```

Create sample objects.

------------------------------------------------------------------------

## Assignment 24 -- Search and Reports

Implement:

-   Search by Employee ID
-   Search by Department
-   Highest Salary
-   Lowest Salary
-   Average Salary
-   Employees per Department

------------------------------------------------------------------------

## Assignment 25 -- Final Mini Project

Console Menu:

``` text
1. Add Employee
2. Delete Employee
3. Update Employee
4. Search Employee
5. View Employees
6. View Departments
7. View Projects
8. Reports
9. Exit
```

Use everything learned:

-   Basic Types
-   Functions
-   Arrays
-   Interfaces
-   Classes
-   Enums
-   Access Modifiers
-   Inheritance
-   Abstract Classes
-   Generics
-   Modules
-   Exception Handling

------------------------------------------------------------------------

## Outcome

By the end of these assignments, students will have built a complete
console-based Employee Management System using TypeScript concepts that
directly prepare them for Angular and React development.
