# 🗃️ MongoDB Assignment — Employee Management System
### Database Training | IBM

> **Scenario:** You are the backend database engineer for an HR platform. The system manages employees, departments, job roles, and projects across a mid-sized organisation. Your job is to build, query, update, and maintain this database using MongoDB — covering every fundamental operation and data pattern the platform depends on.

---

## ⚙️ Setup

### Step 1 — Start MongoDB

```bash
# Start MongoDB (if not running as a service)
mongod --dbpath /data/db

# Or with Docker
docker run -d --name mongo-ems -p 27017:27017 mongo:6
```

### Step 2 — Connect to the Shell

```bash
mongosh
```

### Step 3 — Create and Switch to the Database

```js
use ems
```

### Step 4 — Verify

```js
db                  // should print: ems
show dbs            // ems won't appear yet — it's created on first insert
```

---

## 📐 Document Structure

Read this section carefully. Every exercise below is based on these four collections. Understand the structure before you write a single query.

---

### 🏢 Collection: `departments`

```js
{
  _id: ObjectId("..."),
  code: "ENG",                        // String — short unique code
  name: "Engineering",                // String — full department name
  location: "Bangalore",              // String — office location
  established: ISODate("2015-03-01"), // Date
  headCount: 42,                      // Integer — current employee count
  isActive: true,                     // Boolean
  tags: ["tech", "product", "core"]   // Array of Strings
}
```

---

### 👤 Collection: `employees`

```js
{
  _id: ObjectId("..."),
  employeeId: "EMP001",               // String — unique business ID
  firstName: "Priya",                 // String
  lastName: "Sharma",                 // String
  email: "priya.sharma@acme.com",     // String — unique
  phone: "+91-9876543210",            // String
  salary: NumberDecimal("95000.00"),  // Decimal128 — always use for money
  hireDate: ISODate("2020-06-15"),    // Date
  status: "ACTIVE",                   // String Enum: ACTIVE | INACTIVE | ON_LEAVE | TERMINATED
  department: {                       // Embedded sub-document
    id: ObjectId("..."),
    code: "ENG",
    name: "Engineering"
  },
  role: {                             // Embedded sub-document
    id: ObjectId("..."),
    title: "Senior Engineer",
    level: 4
  },
  skills: ["Java", "Spring Boot", "Docker", "Kubernetes"],  // Array of Strings
  address: {                          // Nested object
    street: "12 MG Road",
    city: "Bangalore",
    state: "Karnataka",
    pincode: "560001"
  },
  metadata: {                         // Audit fields
    createdAt: ISODate("2020-06-15T09:00:00Z"),
    updatedAt: ISODate("2024-01-10T14:30:00Z"),
    createdBy: "admin"
  }
}
```

---

### 🎯 Collection: `roles`

```js
{
  _id: ObjectId("..."),
  title: "Senior Engineer",           // String — unique job title
  department: "Engineering",          // String — department this role belongs to
  level: 4,                           // Integer — seniority: 1 (Junior) to 10 (Principal)
  minSalary: NumberDecimal("70000"),  // Decimal128
  maxSalary: NumberDecimal("120000"), // Decimal128
  isManagement: false,                // Boolean
  responsibilities: [                 // Array of Strings
    "Design and build microservices",
    "Code review",
    "Mentor junior engineers"
  ],
  requiredSkills: ["Java", "Spring Boot", "SQL"]  // Array of Strings
}
```

---

### 📁 Collection: `projects`

```js
{
  _id: ObjectId("..."),
  projectId: "PRJ001",                // String — unique business ID
  name: "Payroll Automation",         // String
  description: "Automate payroll processing end-to-end",  // String
  status: "ACTIVE",                   // String Enum: PLANNED | ACTIVE | ON_HOLD | COMPLETED | CANCELLED
  priority: "HIGH",                   // String Enum: LOW | MEDIUM | HIGH | CRITICAL
  startDate: ISODate("2024-01-01"),   // Date
  endDate: ISODate("2024-12-31"),     // Date (null for ongoing)
  budget: NumberDecimal("500000.00"), // Decimal128
  team: [                             // Array of embedded documents
    {
      employeeId: "EMP001",
      name: "Priya Sharma",
      projectRole: "Tech Lead",
      assignedDate: ISODate("2024-01-05")
    },
    {
      employeeId: "EMP007",
      name: "Arjun Mehta",
      projectRole: "Backend Developer",
      assignedDate: ISODate("2024-02-01")
    }
  ],
  tags: ["payroll", "automation", "finance"],  // Array of Strings
  metadata: {
    createdAt: ISODate("2023-12-01T10:00:00Z"),
    updatedAt: ISODate("2024-01-05T11:00:00Z")
  }
}
```

---

## 🌱 Sample Data — Insert This First

Run all four blocks before attempting any exercises.

### Insert Departments

```js
db.departments.insertMany([
  {
    code: "ENG",
    name: "Engineering",
    location: "Bangalore",
    established: ISODate("2015-03-01"),
    headCount: 42,
    isActive: true,
    tags: ["tech", "product", "core"]
  },
  {
    code: "HR",
    name: "Human Resources",
    location: "Mumbai",
    established: ISODate("2010-01-15"),
    headCount: 12,
    isActive: true,
    tags: ["people", "admin"]
  },
  {
    code: "FIN",
    name: "Finance",
    location: "Delhi",
    established: ISODate("2010-01-15"),
    headCount: 18,
    isActive: true,
    tags: ["accounts", "payroll", "core"]
  },
  {
    code: "OPS",
    name: "Operations",
    location: "Hyderabad",
    established: ISODate("2018-07-01"),
    headCount: 25,
    isActive: true,
    tags: ["infra", "support"]
  },
  {
    code: "MKT",
    name: "Marketing",
    location: "Pune",
    established: ISODate("2020-04-01"),
    headCount: 8,
    isActive: false,
    tags: ["brand", "growth"]
  }
]);
```

### Insert Roles

```js
db.roles.insertMany([
  {
    title: "Junior Engineer",
    department: "Engineering",
    level: 2,
    minSalary: NumberDecimal("40000"),
    maxSalary: NumberDecimal("65000"),
    isManagement: false,
    responsibilities: ["Write unit tests", "Fix bugs", "Code reviews"],
    requiredSkills: ["Java", "SQL", "Git"]
  },
  {
    title: "Senior Engineer",
    department: "Engineering",
    level: 4,
    minSalary: NumberDecimal("70000"),
    maxSalary: NumberDecimal("120000"),
    isManagement: false,
    responsibilities: ["Design microservices", "Mentor juniors", "Code reviews"],
    requiredSkills: ["Java", "Spring Boot", "Docker", "SQL"]
  },
  {
    title: "Engineering Manager",
    department: "Engineering",
    level: 7,
    minSalary: NumberDecimal("130000"),
    maxSalary: NumberDecimal("200000"),
    isManagement: true,
    responsibilities: ["Team leadership", "Sprint planning", "Stakeholder management"],
    requiredSkills: ["Java", "Agile", "Leadership"]
  },
  {
    title: "HR Executive",
    department: "Human Resources",
    level: 2,
    minSalary: NumberDecimal("35000"),
    maxSalary: NumberDecimal("55000"),
    isManagement: false,
    responsibilities: ["Recruitment", "Onboarding", "Payroll coordination"],
    requiredSkills: ["MS Office", "Communication", "HRMS"]
  },
  {
    title: "Finance Analyst",
    department: "Finance",
    level: 3,
    minSalary: NumberDecimal("50000"),
    maxSalary: NumberDecimal("80000"),
    isManagement: false,
    responsibilities: ["Financial reporting", "Budget tracking", "Audits"],
    requiredSkills: ["Excel", "SAP", "Accounting"]
  },
  {
    title: "Principal Engineer",
    department: "Engineering",
    level: 9,
    minSalary: NumberDecimal("200000"),
    maxSalary: NumberDecimal("300000"),
    isManagement: false,
    responsibilities: ["Architecture decisions", "Tech strategy", "Cross-team leadership"],
    requiredSkills: ["System Design", "Cloud", "Java", "Kubernetes"]
  }
]);
```

### Insert Employees

```js
db.employees.insertMany([
  {
    employeeId: "EMP001",
    firstName: "Priya",
    lastName: "Sharma",
    email: "priya.sharma@acme.com",
    phone: "+91-9876543210",
    salary: NumberDecimal("95000.00"),
    hireDate: ISODate("2020-06-15"),
    status: "ACTIVE",
    department: { code: "ENG", name: "Engineering" },
    role: { title: "Senior Engineer", level: 4 },
    skills: ["Java", "Spring Boot", "Docker", "Kubernetes"],
    address: { street: "12 MG Road", city: "Bangalore", state: "Karnataka", pincode: "560001" },
    metadata: { createdAt: ISODate("2020-06-15T09:00:00Z"), updatedAt: ISODate("2024-01-10T14:30:00Z"), createdBy: "admin" }
  },
  {
    employeeId: "EMP002",
    firstName: "Arjun",
    lastName: "Mehta",
    email: "arjun.mehta@acme.com",
    phone: "+91-9123456789",
    salary: NumberDecimal("58000.00"),
    hireDate: ISODate("2022-03-01"),
    status: "ACTIVE",
    department: { code: "ENG", name: "Engineering" },
    role: { title: "Junior Engineer", level: 2 },
    skills: ["Java", "SQL", "Git"],
    address: { street: "45 Brigade Road", city: "Bangalore", state: "Karnataka", pincode: "560025" },
    metadata: { createdAt: ISODate("2022-03-01T09:00:00Z"), updatedAt: ISODate("2024-01-01T10:00:00Z"), createdBy: "admin" }
  },
  {
    employeeId: "EMP003",
    firstName: "Neha",
    lastName: "Kapoor",
    email: "neha.kapoor@acme.com",
    phone: "+91-9988776655",
    salary: NumberDecimal("160000.00"),
    hireDate: ISODate("2018-09-10"),
    status: "ACTIVE",
    department: { code: "ENG", name: "Engineering" },
    role: { title: "Engineering Manager", level: 7 },
    skills: ["Java", "Agile", "Leadership", "Spring Boot"],
    address: { street: "7 Indiranagar", city: "Bangalore", state: "Karnataka", pincode: "560038" },
    metadata: { createdAt: ISODate("2018-09-10T09:00:00Z"), updatedAt: ISODate("2023-11-15T16:00:00Z"), createdBy: "admin" }
  },
  {
    employeeId: "EMP004",
    firstName: "Rohan",
    lastName: "Desai",
    email: "rohan.desai@acme.com",
    phone: "+91-9071234567",
    salary: NumberDecimal("48000.00"),
    hireDate: ISODate("2021-11-01"),
    status: "ON_LEAVE",
    department: { code: "HR", name: "Human Resources" },
    role: { title: "HR Executive", level: 2 },
    skills: ["MS Office", "Communication", "HRMS"],
    address: { street: "33 Bandra West", city: "Mumbai", state: "Maharashtra", pincode: "400050" },
    metadata: { createdAt: ISODate("2021-11-01T09:00:00Z"), updatedAt: ISODate("2024-02-01T08:00:00Z"), createdBy: "admin" }
  },
  {
    employeeId: "EMP005",
    firstName: "Aisha",
    lastName: "Khan",
    email: "aisha.khan@acme.com",
    phone: "+91-9654321098",
    salary: NumberDecimal("72000.00"),
    hireDate: ISODate("2019-04-22"),
    status: "ACTIVE",
    department: { code: "FIN", name: "Finance" },
    role: { title: "Finance Analyst", level: 3 },
    skills: ["Excel", "SAP", "Accounting", "PowerBI"],
    address: { street: "21 Connaught Place", city: "Delhi", state: "Delhi", pincode: "110001" },
    metadata: { createdAt: ISODate("2019-04-22T09:00:00Z"), updatedAt: ISODate("2023-12-20T13:00:00Z"), createdBy: "admin" }
  },
  {
    employeeId: "EMP006",
    firstName: "Vikram",
    lastName: "Singh",
    email: "vikram.singh@acme.com",
    phone: "+91-9345678901",
    salary: NumberDecimal("250000.00"),
    hireDate: ISODate("2015-01-05"),
    status: "ACTIVE",
    department: { code: "ENG", name: "Engineering" },
    role: { title: "Principal Engineer", level: 9 },
    skills: ["System Design", "Cloud", "Java", "Kubernetes", "Architecture"],
    address: { street: "5 Koramangala", city: "Bangalore", state: "Karnataka", pincode: "560034" },
    metadata: { createdAt: ISODate("2015-01-05T09:00:00Z"), updatedAt: ISODate("2024-01-20T10:00:00Z"), createdBy: "admin" }
  },
  {
    employeeId: "EMP007",
    firstName: "Divya",
    lastName: "Nair",
    email: "divya.nair@acme.com",
    phone: "+91-9786543210",
    salary: NumberDecimal("62000.00"),
    hireDate: ISODate("2023-01-16"),
    status: "ACTIVE",
    department: { code: "OPS", name: "Operations" },
    role: { title: "Junior Engineer", level: 2 },
    skills: ["Linux", "Docker", "Bash", "Monitoring"],
    address: { street: "90 Jubilee Hills", city: "Hyderabad", state: "Telangana", pincode: "500033" },
    metadata: { createdAt: ISODate("2023-01-16T09:00:00Z"), updatedAt: ISODate("2024-01-16T09:00:00Z"), createdBy: "admin" }
  },
  {
    employeeId: "EMP008",
    firstName: "Kiran",
    lastName: "Reddy",
    email: "kiran.reddy@acme.com",
    phone: "+91-9211234567",
    salary: NumberDecimal("88000.00"),
    hireDate: ISODate("2017-07-30"),
    status: "TERMINATED",
    department: { code: "FIN", name: "Finance" },
    role: { title: "Finance Analyst", level: 3 },
    skills: ["Excel", "Tally", "Accounting"],
    address: { street: "14 Banjara Hills", city: "Hyderabad", state: "Telangana", pincode: "500034" },
    metadata: { createdAt: ISODate("2017-07-30T09:00:00Z"), updatedAt: ISODate("2023-06-30T17:00:00Z"), createdBy: "admin" }
  }
]);
```

### Insert Projects

```js
db.projects.insertMany([
  {
    projectId: "PRJ001",
    name: "Payroll Automation",
    description: "Automate payroll processing end-to-end using microservices",
    status: "ACTIVE",
    priority: "HIGH",
    startDate: ISODate("2024-01-01"),
    endDate: ISODate("2024-12-31"),
    budget: NumberDecimal("500000.00"),
    team: [
      { employeeId: "EMP001", name: "Priya Sharma",  projectRole: "Tech Lead",          assignedDate: ISODate("2024-01-05") },
      { employeeId: "EMP002", name: "Arjun Mehta",   projectRole: "Backend Developer",  assignedDate: ISODate("2024-02-01") },
      { employeeId: "EMP005", name: "Aisha Khan",    projectRole: "Finance Consultant", assignedDate: ISODate("2024-01-10") }
    ],
    tags: ["payroll", "automation", "finance"]
  },
  {
    projectId: "PRJ002",
    name: "Employee Portal Redesign",
    description: "Rebuild the employee self-service portal with a modern UI",
    status: "PLANNED",
    priority: "MEDIUM",
    startDate: ISODate("2024-04-01"),
    endDate: ISODate("2024-09-30"),
    budget: NumberDecimal("200000.00"),
    team: [
      { employeeId: "EMP003", name: "Neha Kapoor",  projectRole: "Project Manager",  assignedDate: ISODate("2024-03-15") },
      { employeeId: "EMP007", name: "Divya Nair",   projectRole: "DevOps Engineer",  assignedDate: ISODate("2024-03-20") }
    ],
    tags: ["portal", "ui", "hr-tech"]
  },
  {
    projectId: "PRJ003",
    name: "Cloud Migration",
    description: "Migrate all on-premise services to AWS",
    status: "ACTIVE",
    priority: "CRITICAL",
    startDate: ISODate("2023-07-01"),
    endDate: null,
    budget: NumberDecimal("1500000.00"),
    team: [
      { employeeId: "EMP006", name: "Vikram Singh", projectRole: "Architect",         assignedDate: ISODate("2023-07-01") },
      { employeeId: "EMP001", name: "Priya Sharma", projectRole: "Backend Developer", assignedDate: ISODate("2023-08-01") },
      { employeeId: "EMP007", name: "Divya Nair",   projectRole: "DevOps Engineer",   assignedDate: ISODate("2023-09-01") }
    ],
    tags: ["cloud", "aws", "infra", "migration"]
  },
  {
    projectId: "PRJ004",
    name: "HR Analytics Dashboard",
    description: "Build an analytics dashboard for HR metrics and workforce insights",
    status: "COMPLETED",
    priority: "LOW",
    startDate: ISODate("2023-01-01"),
    endDate: ISODate("2023-12-31"),
    budget: NumberDecimal("150000.00"),
    team: [
      { employeeId: "EMP005", name: "Aisha Khan",   projectRole: "Data Analyst",   assignedDate: ISODate("2023-01-10") },
      { employeeId: "EMP004", name: "Rohan Desai",  projectRole: "Business Analyst", assignedDate: ISODate("2023-02-01") }
    ],
    tags: ["analytics", "hr", "dashboard", "reporting"]
  },
  {
    projectId: "PRJ005",
    name: "Security Hardening",
    description: "Implement zero-trust architecture and harden all public endpoints",
    status: "ON_HOLD",
    priority: "HIGH",
    startDate: ISODate("2024-02-01"),
    endDate: ISODate("2024-06-30"),
    budget: NumberDecimal("300000.00"),
    team: [
      { employeeId: "EMP006", name: "Vikram Singh", projectRole: "Security Architect", assignedDate: ISODate("2024-02-01") }
    ],
    tags: ["security", "infra", "compliance"]
  }
]);
```

---

## 📋 Exercises

---

### 🔵 Section 1 — Database & Collection Basics (Q1–Q5)

**Q1.** List all collections in the `ems` database.

**Q2.** Count the total number of documents in the `employees` collection.

**Q3.** Count the total number of documents in each of the four collections. Write a separate count command for each.

**Q4.** Display all documents from the `departments` collection in a human-readable format.

**Q5.** Drop the `departments` collection. Then re-insert the sample departments data to restore it.

> These questions test your understanding of collection management and the basic `find`, `countDocuments`, `drop` commands.

---

### 🟢 Section 2 — Basic CRUD: Create (Q6–Q9)

**Q6.** Insert a new department with the following details:
- Code: `LEGAL`
- Name: `Legal & Compliance`
- Location: `Delhi`
- Established: 1st February 2022
- Head count: 5
- Active: true
- Tags: `["legal", "compliance", "admin"]`

**Q7.** Insert a new employee named **Sanjay Gupta** (`EMP009`):
- Email: `sanjay.gupta@acme.com`
- Department: Engineering, Salary: ₹105,000 (use `NumberDecimal`)
- Role: Senior Engineer, Level 4
- Status: ACTIVE
- Skills: `["Java", "Microservices", "AWS"]`
- Hire date: today's date
- Include a full `address` and `metadata` sub-document

**Q8.** Insert two new roles in a single `insertMany` command:
- `DevOps Engineer` — Engineering dept, Level 3, salary range ₹60,000–₹100,000, not management
- `HR Manager` — HR dept, Level 6, salary range ₹90,000–₹150,000, is management

**Q9.** Insert a new project `PRJ006` — **"AI Chatbot for HR"** — with:
- Status: `PLANNED`, Priority: `HIGH`
- Start date: 1st June 2024, no end date (null)
- Budget: ₹800,000
- Assign EMP001 (Priya Sharma) as `AI Lead`, assigned today
- Tags: `["ai", "chatbot", "hr-tech"]`

---

### 🟡 Section 3 — Basic CRUD: Read (Q10–Q18)

**Q10.** Find all employees whose status is `ACTIVE`.

**Q11.** Find all employees with status `ACTIVE`, but display **only** `employeeId`, `firstName`, `lastName`, `email`, and `status`. Exclude `_id`.

**Q12.** Find the employee whose `employeeId` is `EMP006`.

**Q13.** Find all employees who belong to the `Engineering` department. Use the nested field `department.name`.

**Q14.** Find all projects with a status of `ACTIVE`. Display only `projectId`, `name`, `status`, and `priority`.

**Q15.** Find all roles where `isManagement` is `true`.

**Q16.** Find all departments where `isActive` is `false`.

**Q17.** Find the first 3 employees sorted by `hireDate` ascending — i.e., the three longest-serving employees.

**Q18.** Find all projects and display them sorted by `priority` descending, then by `name` ascending.

---

### 🟠 Section 4 — Comparison & Logical Operators (Q19–Q26)

**Q19.** Find all employees with a salary **greater than** ₹100,000. Display `employeeId`, `firstName`, `lastName`, and `salary` only.

**Q20.** Find all employees with a salary **between** ₹50,000 and ₹100,000 (inclusive on both ends).

**Q21.** Find all roles with a seniority level **greater than or equal to** 7.

**Q22.** Find all employees whose status is **not** `TERMINATED`.

**Q23.** Find all employees who are in the `Engineering` department **AND** have a salary greater than ₹90,000.

**Q24.** Find all employees whose status is either `ON_LEAVE` **OR** `TERMINATED`.

**Q25.** Find all projects with priority `HIGH` **OR** `CRITICAL` that are currently `ACTIVE`.

**Q26.** Find all departments whose `headCount` is **not** between 10 and 20 (i.e., less than 10 or greater than 20).

---

### 🔴 Section 5 — Array Operators (Q27–Q31)

**Q27.** Find all employees who have `"Docker"` in their `skills` array.

**Q28.** Find all employees who have **both** `"Java"` and `"Kubernetes"` in their `skills` array. Use `$all`.

**Q29.** Find all employees who have **at least one** of these skills: `"SAP"`, `"Tally"`, or `"Excel"`. Use `$in`.

**Q30.** Find all projects where `"infra"` is in the `tags` array.

**Q31.** Find all projects that have **exactly 3** members in their `team` array. Use `$size`.

---

### 🟣 Section 6 — Embedded Documents & Dot Notation (Q32–Q36)

**Q32.** Find all employees whose `role.level` is greater than or equal to 7.

**Q33.** Find all employees whose `address.city` is `"Bangalore"`.

**Q34.** Find all employees whose `address.state` is `"Karnataka"` AND `role.level` is less than 5.

**Q35.** Find all projects where at least one team member has the `projectRole` of `"Tech Lead"`. Use `$elemMatch`.

**Q36.** Find all projects where the **first team member** (`team.0`) has the `projectRole` of `"Project Manager"`. Use dot notation on the array index.

---

### ⚪ Section 7 — Regex & Pattern Matching (Q37–Q39)

**Q37.** Find all employees whose `lastName` starts with `"S"`.

**Q38.** Find all projects whose `name` contains the word `"Portal"` (case-insensitive).

**Q39.** Find all employees whose `email` ends with `"@acme.com"`.

---

### 🔷 Section 8 — CRUD: Update (Q40–Q46)

**Q40.** Update employee `EMP004` (Rohan Desai): change their `status` from `ON_LEAVE` to `ACTIVE`.

**Q41.** Give all `ACTIVE` employees in the `Engineering` department a 10% salary increase. Use `$mul` to multiply the existing salary. *(Hint: `$mul` multiplies the field by the given value)*

**Q42.** Add the skill `"Azure"` to employee `EMP006`'s (Vikram Singh) `skills` array. Use `$push`. Ensure no duplicates by using `$addToSet` instead.

**Q43.** Remove `"Tally"` from employee `EMP008`'s `skills` array. Use `$pull`.

**Q44.** Update project `PRJ005` (Security Hardening): change status to `ACTIVE` and update `metadata.updatedAt` to the current timestamp.

**Q45.** Add a new team member to project `PRJ002` using `$push`:
- `employeeId`: `"EMP002"`, `name`: `"Arjun Mehta"`, `projectRole`: `"Backend Developer"`, `assignedDate`: today

**Q46.** Increment the `headCount` of the `Engineering` department by 1. Use `$inc`.

---

### 🔸 Section 9 — CRUD: Delete (Q47–Q49)

**Q47.** Delete the employee with `employeeId: "EMP008"` (Kiran Reddy — already terminated). Use `deleteOne`.

**Q48.** Delete all projects with `status: "COMPLETED"`. Use `deleteMany`. Confirm with a count before and after.

**Q49.** Delete **only the first** department where `isActive` is `false`. Use `deleteOne`.

---

### 🔶 Section 10 — Indexes (Q50–Q52)

**Q50.** Create a **unique index** on the `email` field of the `employees` collection. Then try inserting a document with a duplicate email and observe the error.

**Q51.** Create a **compound index** on `employees` — first by `department.code` ascending, then by `salary` descending. Explain why this index helps queries that filter by department and sort by salary.

**Q52.** List all indexes on the `employees` collection. Then drop the index you created in Q51.

---

### 🟤 Section 11 — Aggregation Pipeline (Q53–Q57)

**Q53.** Count the number of employees per department. Output should show the department name and the count, sorted by count descending.

```
Expected output shape:
{ _id: "Engineering", count: 4 }
{ _id: "Finance", count: 2 }
...
```

**Q54.** Calculate the **average salary** by department. Display department name and average salary, rounded to 2 decimal places. Sort by average salary descending.

**Q55.** Find the **highest-paid employee** in each department. Show department name, employee name (firstName + lastName concatenated), and their salary.

**Q56.** Find all projects with `status: "ACTIVE"` and output the **team size** (number of members in the `team` array) for each. Display `name`, `status`, and `teamSize`. Sort by `teamSize` descending.

> *Hint: Use `$project` with `$size` on the `team` field.*

**Q57.** Build a pipeline that:
1. Filters only `ACTIVE` employees
2. Groups by `role.title`
3. Counts employees in each role
4. Calculates the average salary per role
5. Sorts by count descending

---

### 🔑 Section 12 — Miscellaneous & Advanced (Q58–Q60)

**Q58.** Use `$exists` to find all employees who have an `address.street` field. Then find all employees who do **not** have a `phone` field.

**Q59.** Use `$type` to find all employees where the `salary` field is of type `"decimal"` (BSON type 19). This verifies that salary was stored correctly as `NumberDecimal` and not as a plain number.

**Q60.** Use the `distinct` command to:
- Find all unique values of `status` in the `employees` collection
- Find all unique values of `priority` in the `projects` collection
- Find all unique `address.city` values across all employees

---

## ✅ Answer Key

---

### Section 1 — Database & Collection Basics

**A1.**
```js
show collections
```

**A2.**
```js
db.employees.countDocuments()
```

**A3.**
```js
db.departments.countDocuments()
db.employees.countDocuments()
db.roles.countDocuments()
db.projects.countDocuments()
```

**A4.**
```js
db.departments.find().pretty()
```

**A5.**
```js
db.departments.drop()
// Then re-run the insertMany block from the Setup section
```

---

### Section 2 — Create

**A6.**
```js
db.departments.insertOne({
  code: "LEGAL",
  name: "Legal & Compliance",
  location: "Delhi",
  established: ISODate("2022-02-01"),
  headCount: 5,
  isActive: true,
  tags: ["legal", "compliance", "admin"]
})
```

**A7.**
```js
db.employees.insertOne({
  employeeId: "EMP009",
  firstName: "Sanjay",
  lastName: "Gupta",
  email: "sanjay.gupta@acme.com",
  phone: "+91-9000000009",
  salary: NumberDecimal("105000.00"),
  hireDate: new Date(),
  status: "ACTIVE",
  department: { code: "ENG", name: "Engineering" },
  role: { title: "Senior Engineer", level: 4 },
  skills: ["Java", "Microservices", "AWS"],
  address: {
    street: "88 Whitefield",
    city: "Bangalore",
    state: "Karnataka",
    pincode: "560066"
  },
  metadata: {
    createdAt: new Date(),
    updatedAt: new Date(),
    createdBy: "admin"
  }
})
```

**A8.**
```js
db.roles.insertMany([
  {
    title: "DevOps Engineer",
    department: "Engineering",
    level: 3,
    minSalary: NumberDecimal("60000"),
    maxSalary: NumberDecimal("100000"),
    isManagement: false,
    responsibilities: ["CI/CD pipelines", "Container management", "Infrastructure monitoring"],
    requiredSkills: ["Docker", "Kubernetes", "Linux", "CI/CD"]
  },
  {
    title: "HR Manager",
    department: "Human Resources",
    level: 6,
    minSalary: NumberDecimal("90000"),
    maxSalary: NumberDecimal("150000"),
    isManagement: true,
    responsibilities: ["Team management", "Policy enforcement", "Talent acquisition"],
    requiredSkills: ["Leadership", "HRMS", "Communication"]
  }
])
```

**A9.**
```js
db.projects.insertOne({
  projectId: "PRJ006",
  name: "AI Chatbot for HR",
  description: "Build an AI-powered chatbot for HR self-service queries",
  status: "PLANNED",
  priority: "HIGH",
  startDate: ISODate("2024-06-01"),
  endDate: null,
  budget: NumberDecimal("800000.00"),
  team: [
    {
      employeeId: "EMP001",
      name: "Priya Sharma",
      projectRole: "AI Lead",
      assignedDate: new Date()
    }
  ],
  tags: ["ai", "chatbot", "hr-tech"],
  metadata: {
    createdAt: new Date(),
    updatedAt: new Date()
  }
})
```

---

### Section 3 — Basic Read

**A10.**
```js
db.employees.find({ status: "ACTIVE" })
```

**A11.**
```js
db.employees.find(
  { status: "ACTIVE" },
  { employeeId: 1, firstName: 1, lastName: 1, email: 1, status: 1, _id: 0 }
)
```

**A12.**
```js
db.employees.find({ employeeId: "EMP006" })
```

**A13.**
```js
db.employees.find({ "department.name": "Engineering" })
```

**A14.**
```js
db.projects.find(
  { status: "ACTIVE" },
  { projectId: 1, name: 1, status: 1, priority: 1, _id: 0 }
)
```

**A15.**
```js
db.roles.find({ isManagement: true })
```

**A16.**
```js
db.departments.find({ isActive: false })
```

**A17.**
```js
db.employees.find().sort({ hireDate: 1 }).limit(3)
```

**A18.**
```js
db.projects.find().sort({ priority: -1, name: 1 })
```

> Note: `priority` contains string values. For correct priority ordering, use a numeric field in real applications, or map strings to numbers in the aggregation pipeline. In this exercise, alphabetical sort of the string is acceptable.

---

### Section 4 — Comparison & Logical Operators

**A19.**
```js
db.employees.find(
  { salary: { $gt: NumberDecimal("100000") } },
  { employeeId: 1, firstName: 1, lastName: 1, salary: 1, _id: 0 }
)
```

**A20.**
```js
db.employees.find({
  salary: {
    $gte: NumberDecimal("50000"),
    $lte: NumberDecimal("100000")
  }
})
```

**A21.**
```js
db.roles.find({ level: { $gte: 7 } })
```

**A22.**
```js
db.employees.find({ status: { $ne: "TERMINATED" } })
```

**A23.**
```js
db.employees.find({
  "department.name": "Engineering",
  salary: { $gt: NumberDecimal("90000") }
})
```

**A24.**
```js
db.employees.find({
  status: { $in: ["ON_LEAVE", "TERMINATED"] }
})
```

**A25.**
```js
db.projects.find({
  status: "ACTIVE",
  priority: { $in: ["HIGH", "CRITICAL"] }
})
```

**A26.**
```js
db.departments.find({
  $or: [
    { headCount: { $lt: 10 } },
    { headCount: { $gt: 20 } }
  ]
})
```

---

### Section 5 — Array Operators

**A27.**
```js
db.employees.find({ skills: "Docker" })
```

**A28.**
```js
db.employees.find({ skills: { $all: ["Java", "Kubernetes"] } })
```

**A29.**
```js
db.employees.find({ skills: { $in: ["SAP", "Tally", "Excel"] } })
```

**A30.**
```js
db.projects.find({ tags: "infra" })
```

**A31.**
```js
db.projects.find({ team: { $size: 3 } })
```

---

### Section 6 — Embedded Documents & Dot Notation

**A32.**
```js
db.employees.find({ "role.level": { $gte: 7 } })
```

**A33.**
```js
db.employees.find({ "address.city": "Bangalore" })
```

**A34.**
```js
db.employees.find({
  "address.state": "Karnataka",
  "role.level": { $lt: 5 }
})
```

**A35.**
```js
db.projects.find({
  team: { $elemMatch: { projectRole: "Tech Lead" } }
})
```

**A36.**
```js
db.projects.find({ "team.0.projectRole": "Project Manager" })
```

---

### Section 7 — Regex & Pattern Matching

**A37.**
```js
db.employees.find({ lastName: /^S/ })
```

**A38.**
```js
db.projects.find({ name: /Portal/i })
```

**A39.**
```js
db.employees.find({ email: /@acme\.com$/ })
```

---

### Section 8 — Update

**A40.**
```js
db.employees.updateOne(
  { employeeId: "EMP004" },
  { $set: { status: "ACTIVE" } }
)
```

**A41.**
```js
db.employees.updateMany(
  { "department.name": "Engineering", status: "ACTIVE" },
  { $mul: { salary: NumberDecimal("1.10") } }
)
```

**A42.**
```js
// $push (allows duplicates)
db.employees.updateOne(
  { employeeId: "EMP006" },
  { $push: { skills: "Azure" } }
)

// $addToSet (safe — no duplicates)
db.employees.updateOne(
  { employeeId: "EMP006" },
  { $addToSet: { skills: "Azure" } }
)
```

**A43.**
```js
db.employees.updateOne(
  { employeeId: "EMP008" },
  { $pull: { skills: "Tally" } }
)
```

**A44.**
```js
db.projects.updateOne(
  { projectId: "PRJ005" },
  {
    $set: {
      status: "ACTIVE",
      "metadata.updatedAt": new Date()
    }
  }
)
```

**A45.**
```js
db.projects.updateOne(
  { projectId: "PRJ002" },
  {
    $push: {
      team: {
        employeeId: "EMP002",
        name: "Arjun Mehta",
        projectRole: "Backend Developer",
        assignedDate: new Date()
      }
    }
  }
)
```

**A46.**
```js
db.departments.updateOne(
  { code: "ENG" },
  { $inc: { headCount: 1 } }
)
```

---

### Section 9 — Delete

**A47.**
```js
db.employees.deleteOne({ employeeId: "EMP008" })
```

**A48.**
```js
// Count before
db.projects.countDocuments({ status: "COMPLETED" })

// Delete
db.projects.deleteMany({ status: "COMPLETED" })

// Confirm
db.projects.countDocuments({ status: "COMPLETED" })
```

**A49.**
```js
db.departments.deleteOne({ isActive: false })
```

---

### Section 10 — Indexes

**A50.**
```js
// Create the unique index
db.employees.createIndex({ email: 1 }, { unique: true })

// Try inserting a duplicate — this will throw a duplicate key error
db.employees.insertOne({
  employeeId: "EMP099",
  email: "priya.sharma@acme.com"   // already exists — error expected
})
```

**A51.**
```js
db.employees.createIndex(
  { "department.code": 1, salary: -1 },
  { name: "idx_dept_salary" }
)
```
> This index helps queries that filter by `department.code` first and then sort or range-filter by `salary`. MongoDB reads an index in order, so this compound index satisfies both the equality filter and the sort in one scan — without a separate in-memory sort step.

**A52.**
```js
// List all indexes
db.employees.getIndexes()

// Drop the compound index by name
db.employees.dropIndex("idx_dept_salary")
```

---

### Section 11 — Aggregation Pipeline

**A53.**
```js
db.employees.aggregate([
  {
    $group: {
      _id: "$department.name",
      count: { $sum: 1 }
    }
  },
  { $sort: { count: -1 } }
])
```

**A54.**
```js
db.employees.aggregate([
  {
    $group: {
      _id: "$department.name",
      avgSalary: { $avg: { $toDouble: "$salary" } }
    }
  },
  {
    $project: {
      _id: 1,
      avgSalary: { $round: ["$avgSalary", 2] }
    }
  },
  { $sort: { avgSalary: -1 } }
])
```

**A55.**
```js
db.employees.aggregate([
  {
    $sort: { salary: -1 }
  },
  {
    $group: {
      _id: "$department.name",
      topEmployee: { $first: "$firstName" },
      topLastName: { $first: "$lastName" },
      topSalary: { $first: "$salary" }
    }
  },
  {
    $project: {
      department: "$_id",
      name: { $concat: ["$topEmployee", " ", "$topLastName"] },
      salary: "$topSalary",
      _id: 0
    }
  }
])
```

**A56.**
```js
db.projects.aggregate([
  { $match: { status: "ACTIVE" } },
  {
    $project: {
      name: 1,
      status: 1,
      teamSize: { $size: "$team" },
      _id: 0
    }
  },
  { $sort: { teamSize: -1 } }
])
```

**A57.**
```js
db.employees.aggregate([
  { $match: { status: "ACTIVE" } },
  {
    $group: {
      _id: "$role.title",
      count: { $sum: 1 },
      avgSalary: { $avg: { $toDouble: "$salary" } }
    }
  },
  {
    $project: {
      role: "$_id",
      count: 1,
      avgSalary: { $round: ["$avgSalary", 2] },
      _id: 0
    }
  },
  { $sort: { count: -1 } }
])
```

---

### Section 12 — Miscellaneous & Advanced

**A58.**
```js
// Employees WITH a street field
db.employees.find({ "address.street": { $exists: true } })

// Employees WITHOUT a phone field
db.employees.find({ phone: { $exists: false } })
```

**A59.**
```js
// BSON type 19 = Decimal128 (NumberDecimal)
db.employees.find({ salary: { $type: 19 } })

// You can also use the string alias
db.employees.find({ salary: { $type: "decimal" } })
```

**A60.**
```js
// Unique employee statuses
db.employees.distinct("status")

// Unique project priorities
db.projects.distinct("priority")

// Unique cities across all employees
db.employees.distinct("address.city")
```

---

## 📚 Concept Coverage Map

| Concept | Questions |
|---|---|
| Collection management (`show`, `drop`, `countDocuments`) | Q1, Q2, Q3, Q5 |
| `insertOne` / `insertMany` | Q6, Q7, Q8, Q9 |
| `find` with projection | Q11, Q14, Q19 |
| Comparison operators (`$gt`, `$gte`, `$lt`, `$lte`, `$ne`) | Q19–Q22 |
| Logical operators (`$and`, `$or`, `$in`, `$nin`) | Q23–Q26, Q29 |
| Array operators (`$all`, `$in`, `$size`, `$elemMatch`) | Q27–Q31, Q35 |
| Dot notation on nested objects | Q13, Q32, Q33, Q34 |
| Dot notation on array index | Q36 |
| Regex / pattern matching | Q37, Q38, Q39 |
| `$set`, `$inc`, `$mul` | Q40, Q41, Q44, Q46 |
| `$push`, `$addToSet`, `$pull` | Q42, Q43, Q45 |
| `deleteOne` / `deleteMany` | Q47, Q48, Q49 |
| Sorting and limiting | Q17, Q18 |
| Indexes (unique, compound, drop) | Q50, Q51, Q52 |
| `$group`, `$sum`, `$avg`, `$first` | Q53, Q54, Q55, Q57 |
| `$match`, `$project`, `$sort` in pipeline | Q56, Q57 |
| `$size` in aggregation | Q56 |
| `$exists` | Q58 |
| `$type` | Q59 |
| `distinct` | Q60 |
| `NumberDecimal` / data types | Q7, Q8, Q19, Q20, Q59 |
| `ISODate` / date handling | Q6, Q9, Q17 |
| Embedded document queries | Q32–Q36 |

---

*Assignment prepared for IBM Database Training — For Training Purposes Only*
