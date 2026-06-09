# 🍃 MongoDB Courseware
### IBM Java Full Stack Development Program
**Duration:** 1.5 Days &nbsp;|&nbsp; **Level:** Beginner → Intermediate &nbsp;|&nbsp; **Primary Setup:** Windows 11 · Community Server · Compass · Shell

---

## 📋 Table of Contents

1. [Course Navigation and Practice Tasks Overview](#1-course-navigation-and-practice-tasks-overview)
2. [Introduction to MongoDB](#2-introduction-to-mongodb)
3. [MongoDB Installation Options](#3-mongodb-installation-options)
4. [Installing MongoDB on a Local Computer](#4-installing-mongodb-on-a-local-computer)
5. [Installing MongoDB on a Dedicated or VPS Server](#5-installing-mongodb-on-a-dedicated-or-vps-server)
6. [Using MongoDB as a Service — Cloud MongoDB](#6-using-mongodb-as-a-service--cloud-mongodb)
7. [Installing GUI Tools for MongoDB Management](#7-installing-gui-tools-for-mongodb-management)
8. [Introduction to the MongoDB Shell](#8-introduction-to-the-mongodb-shell)
9. [Primary MongoDB Data Types](#9-primary-mongodb-data-types)
10. [CRUD Operations](#10-crud-operations)
11. [MongoDB Queries](#11-mongodb-queries)
12. [Updating Documents](#12-updating-documents)
13. [Delete Operations](#13-delete-operations)
14. [Aggregation Framework](#14-aggregation-framework)
15. [Indexes](#15-indexes)
16. [Utilities](#16-utilities)
17. [Wrap Up](#17-wrap-up)

---

## 1. Course Navigation and Practice Tasks Overview

### How to Use This Courseware

Welcome to the MongoDB module of the IBM Java Full Stack Development Program. Whether you've touched databases before or you're starting fresh — this is built for you.

Every section follows the same pattern so you always know what's coming:

| Block | What It Is |
|-------|-----------|
| 💡 **Concept** | Plain-English explanation — the "why" |
| 🔧 **Syntax** | The command structure — the "how" |
| 💻 **Code Example** | Real, runnable code you can copy directly |
| ✅ **Practice Task** | Hands-on exercise at the end of each section |

Work through sections **in order** — each one builds on the last. The capstone project at the end (Section 17) ties everything together using a Student Management System.

### Prerequisites

You don't need to be an expert, but these help:

- Basic JSON knowledge (key-value pairs, arrays, nested objects)
- JDK 11+ installed on your machine
- Some SQL familiarity is useful but not required

### Tools You'll Use

| Tool | Purpose |
|------|---------|
| MongoDB Community Server | The core database engine — runs locally on your PC |
| `mongosh` (MongoDB Shell) | CLI tool — type commands, interact with the database |
| MongoDB Compass | GUI tool — visual explorer, no commands needed |
| MongoDB Atlas | Cloud-hosted MongoDB (for the cloud section) |

### Practice Task Map

| Task | Section | Topic |
|------|---------|-------|
| Task 1 | §4 | Install MongoDB, verify version |
| Task 2 | §6 | Set up a free Atlas cluster |
| Task 3 | §7 | Install Compass, connect and explore |
| Task 4 | §8 | Shell basics — navigate databases |
| Task 5 | §9 | Insert a doc with 6+ data types |
| Task 6 | §10 | CRUD — Create & Read |
| Task 7 | §11 | Query operators |
| Task 8 | §12 | Update operators |
| Task 9 | §13 | Delete operations |
| Task 10 | §14 | Aggregation pipeline |
| Task 11 | §15 | Create and test indexes |
| Task 12 | §16 | Export, import, backup |

---

## 2. Introduction to MongoDB

### What Even Is MongoDB?

MongoDB is an open-source **NoSQL document database** built for flexibility, speed, and scale. Unlike traditional SQL databases that store data in rows and tables, MongoDB stores data as **JSON-like documents** — meaning each record can have a different structure, and you don't need to define a rigid schema upfront.

> 💬 The name comes from **"humongous"** — it was literally built to handle massive amounts of data.

### SQL vs MongoDB — The Real Difference

| Feature | SQL (MySQL / Oracle) | MongoDB |
|---------|---------------------|---------|
| Data Format | Rows & columns in tables | Documents (JSON / BSON) |
| Schema | Fixed — every row has the same columns | Flexible — each document can be different |
| Relationships | Foreign keys + JOINs | Embedded documents or `$lookup` |
| Scaling | Vertical (upgrade the server 💸) | Horizontal (add more servers 🤝) |
| Best For | Structured, predictable data | Flexible, hierarchical, fast-changing data |

### Core Concepts in Plain English

**Database** — A container for collections. Same concept as in SQL.

**Collection** — A group of documents. Think of it like a table, but with no enforced schema — each document can look different.

**Document** — One record, stored as BSON (Binary JSON). This is the fundamental unit of data.

**Field** — A key-value pair inside a document. Equivalent to a column in SQL.

**`_id`** — Every document gets a unique `_id` automatically. MongoDB generates an **ObjectId** if you don't provide one.

### What a Document Looks Like

```json
{
  "_id": "ObjectId('64a7f2c3e4b0a1d2e3f4a5b6')",
  "name": "Ravi Kumar",
  "age": 22,
  "email": "ravi@example.com",
  "courses": ["Java", "MongoDB", "Spring Boot"]
}
```

No rigid table. No empty columns for missing data. One student can have 3 courses, another can have 10 — MongoDB doesn't care.

### SQL → MongoDB Vocab Swap

| SQL Term | MongoDB Term |
|----------|-------------|
| Database | Database |
| Table | Collection |
| Row | Document |
| Column | Field |
| Primary Key | `_id` |
| JOIN | `$lookup` (Aggregation) |
| INDEX | Index |

### Real-World Who Uses This?

- 🛒 **E-commerce** — Product catalogs where each product has totally different attributes
- 📱 **Social Media** — User profiles, posts, comments, reactions
- 🎮 **Gaming** — Player state, leaderboards, session data
- 📡 **IoT** — High-frequency sensor data with flexible schemas
- 📰 **Content Platforms** — Articles, blogs, videos, metadata

---

## 3. MongoDB Installation Options

Before you install anything, know your three options:

### Option 1 — Local Installation *(This Course's Primary Setup)*

Install MongoDB Community Edition directly on your Windows 11 (or Mac) machine. Best for **learning, development, and this program**.

✅ No internet dependency after setup  
✅ Full control over the database  
✅ Free

### Option 2 — Dedicated / VPS Server

Install MongoDB on a remote Linux server (AWS EC2, DigitalOcean, etc.). Best for **staging or production environments** where your app needs a persistent, always-on database.

✅ Team-accessible  
✅ Production-ready  
⚠️ Requires Linux knowledge and server management

### Option 3 — Cloud MongoDB (Atlas)

MongoDB's fully managed cloud service. Best for **quick starts, team collaboration, and production deployments** where you don't want to manage infrastructure.

✅ Zero setup — works in minutes  
✅ Free tier available  
✅ Scales automatically  
⚠️ Requires internet connection

> 💡 For this course: **use Option 1 (local)** for all practice tasks. We'll also briefly touch Option 3 (Atlas) in Section 6.

---

## 4. Installing MongoDB on a Local Computer

### 🪟 Windows 11 — Step by Step *(Primary Setup)*

#### Step 1 — Download

Go to: **https://www.mongodb.com/try/download/community**

- Platform: **Windows**
- Package: **MSI**
- Click **Download**

#### Step 2 — Run the Installer

- Accept the license agreement
- Select **Complete** installation type
- ✅ Check **"Install MongoDB as a Service"** — MongoDB will start automatically when Windows starts
- ✅ Optionally check **"Install MongoDB Compass"** — saves you a separate download

#### Step 3 — Verify the Installation

Open **Command Prompt** or **Windows Terminal**:

```bash
mongod --version
mongosh --version
```

You should see version numbers printed. If you get "not recognized", MongoDB isn't in your PATH — either restart your terminal or manually add `C:\Program Files\MongoDB\Server\<version>\bin` to your system Environment Variables.

#### Step 4 — Start / Stop the Service

MongoDB runs as a Windows Service and starts with your PC. To control it manually:

```bash
# Start MongoDB
net start MongoDB

# Stop MongoDB
net stop MongoDB
```

> 💡 You can also manage the MongoDB service through **Windows Services**: press `Win + R` → type `services.msc` → find "MongoDB Server".

**Default data directory:** `C:\Program Files\MongoDB\Server\<version>\data`

---

### 🍎 macOS — Step by Step

#### Option A — Homebrew *(Recommended)*

```bash
# Install Homebrew if you don't have it
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Add the MongoDB tap
brew tap mongodb/brew

# Install MongoDB Community Edition
brew install mongodb-community

# Start MongoDB as a background service
brew services start mongodb-community

# Verify
mongosh --version
```

#### Option B — Manual

- Download the `.tgz` package from https://www.mongodb.com/try/download/community
- Extract the archive and move the binaries to `/usr/local/bin`

#### Starting / Stopping on Mac

```bash
brew services start mongodb-community    # Start
brew services stop mongodb-community     # Stop
brew services restart mongodb-community  # Restart
```

---

### ✅ Practice Task 1

1. Install MongoDB Community Server on your machine (Windows 11 is the primary target for this course)
2. Open Command Prompt and run `mongod --version` and `mongosh --version`
3. Screenshot the version output — share it with your trainer to confirm setup

---

## 5. Installing MongoDB on a Dedicated or VPS Server

### When Would You Use This?

When your application needs a database that's always online, accessible to multiple developers or services, and not tied to your local laptop. Common in staging and production environments.

### Common VPS Providers

- AWS EC2
- DigitalOcean Droplets
- Google Cloud Compute Engine
- Azure Virtual Machines

### Setup on a Linux VPS (Ubuntu/Debian)

#### Step 1 — SSH Into Your Server

```bash
ssh username@your-server-ip
```

#### Step 2 — Install MongoDB

```bash
# Import MongoDB public GPG key
curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | \
   sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg --dearmor

# Add MongoDB repository
echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] \
https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | \
sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list

# Update and install
sudo apt-get update
sudo apt-get install -y mongodb-org

# Start and enable MongoDB
sudo systemctl start mongod
sudo systemctl enable mongod

# Verify
mongod --version
```

#### Step 3 — Allow Remote Connections

Edit the MongoDB config file:

```bash
sudo nano /etc/mongod.conf
```

Change `bindIp` to allow all IPs (restrict this in production!):

```yaml
net:
  port: 27017
  bindIp: 0.0.0.0
```

#### Step 4 — Open the Firewall Port

```bash
sudo ufw allow 27017/tcp
sudo ufw reload
```

#### Step 5 — Enable Authentication *(Security Best Practice)*

```bash
# Open the shell
mongosh

# Create an admin user
use admin
db.createUser({
  user: "adminUser",
  pwd: "SecurePassword123",
  roles: [{ role: "userAdminAnyDatabase", db: "admin" }]
})
exit
```

Update `mongod.conf` to enable auth:

```yaml
security:
  authorization: enabled
```

Restart MongoDB:

```bash
sudo systemctl restart mongod
```

#### Step 6 — Connect Remotely

```bash
mongosh "mongodb://adminUser:SecurePassword123@your-server-ip:27017/admin"
```

> ⚠️ **Never expose MongoDB to the internet without authentication enabled.** Always restrict `bindIp` and use strong passwords in production.

---

## 6. Using MongoDB as a Service — Cloud MongoDB

### What Is MongoDB Atlas?

**Atlas** is MongoDB's official cloud database service (Database-as-a-Service / DBaaS). It handles backups, scaling, security, and monitoring automatically — you just use the database.

> 💡 Think of Atlas as "MongoDB in the cloud, managed for you". No servers to configure.

### Getting Started with Atlas (Free Tier)

#### Step 1 — Create an Account

Visit: **https://www.mongodb.com/atlas** → Sign up for free

#### Step 2 — Create a Free Cluster

- Click **"Build a Database"**
- Choose **Free (Shared)** tier — M0 is always free
- Pick a cloud provider (AWS / GCP / Azure) and region closest to you
- Click **"Create Cluster"** (takes ~2 minutes)

#### Step 3 — Configure Access

**Database Access:** Create a user with a username and password
**Network Access:** Add your IP address, or use `0.0.0.0/0` to allow all (fine for dev, never for production)

#### Step 4 — Get Your Connection String

- Click **"Connect"** → **"Connect your application"**
- Copy the URI, it looks like:

```
mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/<dbname>?retryWrites=true&w=majority
```

#### Step 5 — Connect via Shell

```bash
mongosh "mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/myFirstDatabase"
```

#### Step 6 — Connect from Java (Spring Boot)

```properties
# application.properties
spring.data.mongodb.uri=mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/mydb
```

### Atlas Key Features

| Feature | What It Does |
|---------|-------------|
| **Automated Backups** | Point-in-time restore, no manual dumps |
| **Performance Advisor** | Automatically suggests indexes for slow queries |
| **Data Explorer** | Browse documents visually in the browser |
| **Charts** | Built-in data visualization tool |
| **Global Clusters** | Replicate data across multiple regions |
| **Alerts** | CPU / memory / storage notifications |

### Atlas vs Local — When to Use What

| Use Case | Recommendation |
|----------|----------------|
| Learning / practice | Local (Community Server) |
| Team project during course | Atlas free tier |
| Production app | Atlas paid tier or self-managed VPS |

---

### ✅ Practice Task 2

1. Create a free MongoDB Atlas account at https://www.mongodb.com/atlas
2. Set up an M0 (free) cluster
3. Connect to your cluster via `mongosh` using the connection string from Atlas
4. Run `show dbs` to confirm the connection works

---

## 7. Installing GUI Tools for MongoDB Management

### MongoDB Compass — The Official GUI *(Primary Tool for This Course)*

Compass is MongoDB's official graphical interface. It lets you browse documents, run queries, build aggregation pipelines, and manage indexes — all without typing a command.

**Download:** https://www.mongodb.com/products/compass

> 💡 If you checked the "Install MongoDB Compass" box during the Community Server install (Section 4), you already have it.

#### Connecting Compass to Your Local MongoDB

1. Open MongoDB Compass
2. In the connection field, type: `mongodb://localhost:27017`
3. Click **Connect**

You'll land on a screen showing three system databases: `admin`, `local`, `config`. Leave those alone.

#### What You Can Do in Compass

| Tab / Feature | What It Does |
|--------------|-------------|
| **Documents** | Browse, add, edit, and delete documents visually |
| **Filter bar** | Write query filters with autocomplete |
| **Aggregations** | Build pipelines with a visual stage builder |
| **Indexes** | Create, view, and drop indexes |
| **Schema** | Auto-analyze field types and distributions |
| **Explain Plan** | See how MongoDB executes a query |
| **Performance** | Real-time server metrics |

> 💡 **Pro tip:** Use Compass to **explore and debug** your data visually. Use the shell to **script and automate**. You'll use both throughout this course.

### Other GUI Tools Worth Knowing

| Tool | Platform | Cost | Best For |
|------|----------|------|---------|
| **MongoDB Compass** | Win / Mac / Linux | Free | Official — most features, use this first |
| **Studio 3T** | Win / Mac / Linux | Free / Paid | SQL-like query interface for MongoDB |
| **NoSQLBooster** | Win / Mac / Linux | Free / Paid | Code completion, scripting |
| **Robo 3T** | Win / Mac / Linux | Free | Lightweight and fast |
| **TablePlus** | Mac / Win | Free / Paid | Multi-database support |

---

### ✅ Practice Task 3

1. Open MongoDB Compass and connect to `mongodb://localhost:27017`
2. Explore the `admin`, `local`, and `config` databases — what collections do you see inside each?
3. Create a new database called `ibmtraining` using Compass (Database → Create Database)

---

## 8. Introduction to the MongoDB Shell

### What Is `mongosh`?

`mongosh` (MongoDB Shell) is the official CLI for MongoDB. It's a **full JavaScript environment** — you can write variables, loops, and functions alongside your database commands. It's the fastest way to run queries and scripts.

### Opening the Shell

On Windows 11, open **Command Prompt** or **Windows Terminal**:

```bash
# Connect to local MongoDB (default port 27017)
mongosh

# Connect to a specific database directly
mongosh "mongodb://localhost:27017/mydb"

# Connect to Atlas
mongosh "mongodb+srv://user:pass@cluster0.mongodb.net/mydb"
```

You'll see a prompt like `test>` — that's your current active database.

### The Shell Survival Kit

```javascript
db                      // Show current database name
show dbs                // List all databases (only shows ones with data)
use ibmtraining         // Switch to (or create) a database
show collections        // List collections in the current database
help                    // General help menu
db.help()               // Help for database-level commands
cls                     // Clear the screen
exit                    // Exit mongosh
```

> 💡 `use ibmtraining` won't actually create the database until you insert your first document. MongoDB only persists things when there's real data.

### Running JavaScript in the Shell

Because `mongosh` is a full JS environment, you can do this:

```javascript
// Variables
let name = "MongoDB"
print("Hello, " + name)

// Loops — great for generating test data
for (let i = 1; i <= 5; i++) {
  print("Student " + i)
}

// Functions
function greet(user) {
  return "Welcome, " + user
}
greet("Ravi")
```

### Handy Shell One-Liners

```javascript
db.students.find().pretty()          // Pretty-print query results
db.students.find().limit(5)          // Show only first 5 results
db.students.countDocuments()         // Count all documents in a collection
db.serverStatus()                    // Full server stats and info
```

---

### ✅ Practice Task 4

1. Open `mongosh` — confirm it connects to `localhost:27017`
2. Run `show dbs` — note the system databases that appear
3. Run `use ibmtraining` to switch to your training database
4. Run `db` to confirm the active database is `ibmtraining`

---

## 9. Primary MongoDB Data Types

MongoDB stores data in **BSON** format (Binary JSON) — a superset of JSON with additional types for things like dates, binary data, and high-precision decimals.

### BSON Data Types Reference

| BSON Type | Example | Description |
|-----------|---------|-------------|
| `String` | `"name": "Ravi"` | UTF-8 text |
| `Integer` | `"age": 22` | 32-bit whole number |
| `Double` | `"gpa": 8.5` | Floating-point number |
| `Boolean` | `"active": true` | `true` or `false` |
| `Date` | `"dob": ISODate("2002-05-15")` | Date and time |
| `ObjectId` | `"_id": ObjectId("...")` | Auto-generated 12-byte unique ID |
| `Array` | `"courses": ["Java","MongoDB"]` | List of values |
| `Embedded Doc` | `"address": {"city": "Mumbai"}` | Nested document object |
| `Null` | `"middleName": null` | Explicit no-value |
| `Regex` | `"pattern": /^Ravi/` | Regular expression |
| `Binary Data` | `"photo": BinData(...)` | Raw binary content |
| `Long` | `"studentId": NumberLong("202400123456")` | 64-bit integer |
| `Decimal128` | `"price": NumberDecimal("19.99")` | High-precision decimal |

### All Data Types in One Document

```javascript
db.students.insertOne({
  name: "Priya Sharma",                       // String
  age: 21,                                     // Integer
  gpa: 9.1,                                    // Double
  isEnrolled: true,                            // Boolean
  enrolledDate: new Date("2024-07-01"),        // Date
  courses: ["Java", "Spring", "MongoDB"],      // Array
  address: {                                   // Embedded Document
    street: "12 MG Road",
    city: "Bengaluru",
    pincode: "560001"
  },
  profilePic: null,                            // Null
  studentId: NumberLong("202400123456")        // Long Integer
})
```

### The ObjectId — Auto-ID Explained

Every document gets an `_id` automatically. The ObjectId is a **12-byte unique identifier** that's packed with information:

```
ObjectId("64a7f2c3e4b0a1d2e3f4a5b6")
          |------| |----| |--|  |--|
          4-byte   3-byte  2    3-byte
          timestamp machine PID  random
```

```javascript
// Create and inspect an ObjectId
let id = new ObjectId()
print(id)
print(id.getTimestamp())   // Extracts the creation timestamp
```

---

### ✅ Practice Task 5

1. In the `ibmtraining` database, create a collection called `employees`
2. Insert one document using **at least 6 different BSON types**
3. View the result with `db.employees.find().pretty()`
4. Identify each field's type in the output

---

## 10. CRUD Operations

CRUD = **C**reate · **R**ead · **U**pdate · **D**elete. Every database operation in existence is one of these four.

### 🔧 Sample Dataset — Set This Up First

Run this once to have data ready for all upcoming exercises:

```javascript
use ibmtraining

db.students.insertMany([
  { name: "Ravi Kumar",   age: 22, city: "Mumbai",    gpa: 8.5, courses: ["Java", "MongoDB"] },
  { name: "Priya Sharma", age: 21, city: "Bengaluru", gpa: 9.1, courses: ["Python", "Django"] },
  { name: "Amit Singh",   age: 23, city: "Delhi",     gpa: 7.8, courses: ["Java", "Spring Boot"] },
  { name: "Neha Patel",   age: 20, city: "Pune",      gpa: 8.9, courses: ["Java", "MongoDB", "React"] },
  { name: "Suresh Reddy", age: 24, city: "Hyderabad", gpa: 7.5, courses: ["Node.js", "MongoDB"] }
])
```

---

### ➕ CREATE — Inserting Documents

#### `insertOne()` — Single document

```javascript
db.students.insertOne({
  name: "Kavitha Nair",
  age: 22,
  city: "Chennai",
  gpa: 8.7,
  courses: ["Java", "AWS"]
})
```

**Response:**
```json
{
  "acknowledged": true,
  "insertedId": ObjectId("64a7f2c3e4b0a1d2e3f4a5b6")
}
```

#### `insertMany()` — Multiple documents at once

```javascript
db.students.insertMany([
  { name: "Rahul Verma", age: 23, city: "Jaipur",  gpa: 8.0, courses: ["Java"] },
  { name: "Sneha Joshi", age: 21, city: "Kolkata", gpa: 9.3, courses: ["MongoDB", "Express"] }
])
```

#### Insert Options

```javascript
// Custom _id instead of auto-generated ObjectId
db.students.insertOne({
  _id: "STU001",
  name: "Custom ID Student",
  age: 22
})

// ordered: false — keeps inserting even if one document fails (e.g. duplicate _id)
db.students.insertMany(
  [{ name: "A" }, { name: "B" }, { name: "C" }],
  { ordered: false }
)
```

---

### 🔍 READ — Finding Documents

#### `find()` — The main query method

```javascript
db.students.find()                              // All documents
db.students.find().pretty()                     // All, nicely formatted
db.students.find({ city: "Mumbai" })            // Filter by city
db.students.find({ age: 22, city: "Mumbai" })   // Multiple conditions (AND by default)
```

#### `findOne()` — First matching document only

```javascript
db.students.findOne({ name: "Ravi Kumar" })
```

#### Projection — Control which fields appear

```javascript
// 1 = include, 0 = exclude
db.students.find({}, { name: 1, city: 1, _id: 0 })   // Only name and city
db.students.find({}, { gpa: 0 })                       // Everything except gpa
```

> ⚠️ You can't mix includes and excludes in the same projection (except `_id`).

#### Cursor Methods — Sort, Skip, Limit

```javascript
db.students.find().limit(3)                        // First 3 results
db.students.find().skip(2)                         // Skip first 2
db.students.find().sort({ gpa: -1 })               // Sort by GPA, highest first
db.students.find().sort({ name: 1 })               // Sort alphabetically
db.students.find().sort({ gpa: -1 }).limit(3)      // Top 3 by GPA (chain methods)
db.students.countDocuments({ city: "Bengaluru" })  // Count matching docs
```

---

### ✅ Practice Task 6 (CRUD — Create & Read)

1. Insert 3 students of your own choosing into the `students` collection
2. Find all students from "Delhi"
3. Retrieve only `name` and `gpa` for all students, sorted by `gpa` descending
4. Find the single student with the highest GPA using `sort` + `limit`

---

## 11. MongoDB Queries

### Comparison Operators

```javascript
db.students.find({ age: { $eq: 22 } })            // Equal to
db.students.find({ city: { $ne: "Mumbai" } })     // Not equal
db.students.find({ gpa: { $gt: 8.5 } })           // Greater than
db.students.find({ gpa: { $gte: 8.5 } })          // Greater than or equal
db.students.find({ age: { $lt: 22 } })            // Less than
db.students.find({ age: { $lte: 22 } })           // Less than or equal
db.students.find({ city: { $in: ["Mumbai", "Pune", "Bengaluru"] } })   // Matches any in list
db.students.find({ city: { $nin: ["Delhi", "Hyderabad"] } })           // Matches none in list
```

### Logical Operators

```javascript
// $and — ALL conditions must be true
db.students.find({
  $and: [
    { age: { $gte: 21 } },
    { gpa: { $gte: 8.0 } }
  ]
})

// $or — AT LEAST ONE condition must be true
db.students.find({
  $or: [
    { city: "Mumbai" },
    { gpa: { $gte: 9.0 } }
  ]
})

// $not — Negate a condition
db.students.find({ gpa: { $not: { $gte: 8.0 } } })

// $nor — ALL listed conditions must be false
db.students.find({
  $nor: [
    { city: "Delhi" },
    { gpa: { $lt: 7.0 } }
  ]
})
```

### Element Operators

```javascript
db.students.find({ gpa: { $exists: true } })         // Field exists in document
db.students.find({ middleName: { $exists: false } })  // Field does NOT exist
db.students.find({ age: { $type: "int" } })           // Field is a specific BSON type
db.students.find({ name: { $type: "string" } })
```

### Array Operators

```javascript
// Array contains ALL of these values
db.students.find({ courses: { $all: ["Java", "MongoDB"] } })

// At least one array element satisfies a range condition
db.students.find({ scores: { $elemMatch: { $gt: 85, $lt: 95 } } })

// Array has exactly N elements
db.students.find({ courses: { $size: 3 } })

// Array contains this single value
db.students.find({ courses: "Java" })
```

### Evaluation Operators

```javascript
// $regex — Pattern matching
db.students.find({ name: { $regex: /^Ravi/i } })      // Starts with "Ravi" (case-insensitive)
db.students.find({ name: { $regex: "Kumar$" } })       // Ends with "Kumar"
db.students.find({ name: { $regex: ".*Singh.*" } })    // Contains "Singh"

// $expr — Use aggregation expressions inside a find()
db.students.find({ $expr: { $gt: ["$gpa", 8.0] } })

// $where — JS expression (use sparingly — it's slow)
db.students.find({ $where: "this.age > 21 && this.gpa > 8" })
```

### Querying Embedded Documents

```javascript
// Insert a doc with a nested address
db.employees.insertOne({
  name: "Arjun Mehta",
  address: { city: "Mumbai", state: "Maharashtra", pincode: "400001" }
})

// ✅ Dot notation — always use this for nested fields
db.employees.find({ "address.city": "Mumbai" })
db.employees.find({ "address.pincode": { $regex: /^400/ } })

// ⚠️ Exact match on whole embedded doc — field order matters, avoid this
db.employees.find({ address: { city: "Mumbai", state: "Maharashtra", pincode: "400001" } })
```

---

### ✅ Practice Task 7 (Queries)

1. Find all students with GPA between 8.0 and 9.0 (inclusive)
2. Find students from "Mumbai" OR "Bengaluru" with GPA above 8.5
3. Find students whose `courses` array contains both "Java" AND "MongoDB"
4. Find all students whose name starts with the letter "R"
5. Find students with exactly 2 courses in their `courses` array

---

## 12. Updating Documents

### Update Operators — Quick Reference

| Operator | What It Does |
|----------|-------------|
| `$set` | Set a field to a new value |
| `$unset` | Remove a field from the document |
| `$inc` | Increment a numeric field |
| `$mul` | Multiply a numeric field |
| `$rename` | Rename a field |
| `$min` | Update only if new value is **less** than current |
| `$max` | Update only if new value is **greater** than current |
| `$currentDate` | Set field to the current date/time |
| `$push` | Add an element to an array |
| `$pop` | Remove first (`-1`) or last (`1`) array element |
| `$pull` | Remove elements matching a condition from an array |
| `$pullAll` | Remove multiple specific values from an array |
| `$addToSet` | Add to array only if not already present (no duplicates) |

---

### `updateOne()` — Update the First Match

```javascript
// Update a single field
db.students.updateOne(
  { name: "Ravi Kumar" },       // Filter: who to update
  { $set: { gpa: 9.0 } }       // Update: what to change
)

// Update multiple fields at once
db.students.updateOne(
  { name: "Ravi Kumar" },
  { $set: { gpa: 9.0, city: "Pune", updatedAt: new Date() } }
)
```

### `updateMany()` — Update All Matches

```javascript
// Tag all merit students
db.students.updateMany(
  { gpa: { $gte: 9.0 } },
  { $set: { status: "Merit" } }
)

// Increment everyone's age by 1
db.students.updateMany({}, { $inc: { age: 1 } })
```

### `replaceOne()` — Replace the Whole Document

```javascript
// ⚠️ Wipes ALL existing fields (except _id) and replaces with the new object
db.students.replaceOne(
  { name: "Suresh Reddy" },
  {
    name: "Suresh Reddy",
    age: 25,
    city: "Chennai",
    gpa: 8.2,
    courses: ["Java", "Kafka"]
  }
)
```

> ⚠️ Prefer `updateOne` + `$set` over `replaceOne` unless you explicitly want to wipe all fields.

### Field Operators in Action

```javascript
// Remove a field entirely
db.students.updateOne({ name: "Ravi Kumar" }, { $unset: { status: "" } })

// Rename a field across all documents
db.students.updateMany({}, { $rename: { "city": "location" } })

// Increment age by 1 and bump GPA by 0.2
db.students.updateOne(
  { name: "Priya Sharma" },
  { $inc: { age: 1, gpa: 0.2 } }
)

// Multiply GPA by 1.1 (10% increase)
db.students.updateOne({ name: "Amit Singh" }, { $mul: { gpa: 1.1 } })

// Only update GPA if current value is greater than 8.0
db.students.updateOne({ name: "Neha Patel" }, { $min: { gpa: 8.0 } })

// Set a timestamp to right now
db.students.updateOne({ name: "Ravi Kumar" }, { $currentDate: { lastModified: true } })
```

### Array Update Operators

```javascript
// Add one element to array
db.students.updateOne({ name: "Ravi Kumar" }, { $push: { courses: "Docker" } })

// Add multiple elements at once
db.students.updateOne(
  { name: "Priya Sharma" },
  { $push: { courses: { $each: ["Kubernetes", "AWS"] } } }
)

// Add only if not already in array (prevents duplicates)
db.students.updateOne({ name: "Amit Singh" }, { $addToSet: { courses: "Java" } })

// Remove last element (1) or first element (-1)
db.students.updateOne({ name: "Neha Patel" }, { $pop: { courses: 1 } })

// Remove a specific value from array
db.students.updateOne({ name: "Ravi Kumar" }, { $pull: { courses: "Docker" } })

// Remove multiple specific values at once
db.students.updateOne(
  { name: "Suresh Reddy" },
  { $pullAll: { courses: ["Node.js", "MongoDB"] } }
)
```

### Upsert — Update or Insert

If no document matches the filter, upsert **creates a new one** instead of doing nothing:

```javascript
db.students.updateOne(
  { name: "New Student" },
  { $set: { name: "New Student", age: 20, city: "Nagpur", gpa: 7.5 } },
  { upsert: true }
)
```

---

### ✅ Practice Task 8 (Update)

1. Update Priya Sharma's GPA to 9.5
2. Add the course "DevOps" to all students from Mumbai
3. Remove the `status` field from all documents where it exists
4. Use `$inc` to increase every student's age by 1
5. Use upsert to insert "Manish Tiwari" if that student doesn't already exist

---

## 13. Delete Operations

### `deleteOne()` — Delete the First Match

```javascript
// Delete by a field value
db.students.deleteOne({ name: "Rahul Verma" })

// Delete by _id
db.students.deleteOne({ _id: ObjectId("64a7f2c3e4b0a1d2e3f4a5b6") })
```

### `deleteMany()` — Delete All Matches

```javascript
db.students.deleteMany({ gpa: { $lt: 7.5 } })    // All low-GPA students
db.students.deleteMany({ city: "Delhi" })          // All from Delhi
db.students.deleteMany({})                         // ⚠️ Deletes ALL docs — collection stays
```

### `findOneAndDelete()` — Delete and Return the Document

Useful when you need to know exactly what was deleted:

```javascript
const deletedDoc = db.students.findOneAndDelete({ name: "Sneha Joshi" })
print(deletedDoc)   // Prints the deleted document before it's gone
```

### Drop vs Delete — Know the Difference

```javascript
db.students.deleteMany({})   // Removes all documents. Collection and indexes REMAIN.
db.students.drop()           // Removes the entire collection, all indexes, all metadata.
db.dropDatabase()            // ☢️ Removes the entire database — use with extreme caution.
```

### The Golden Rule Before Deleting

**Always preview what you're about to delete with `find()` first:**

```javascript
// Step 1 — Check exactly what matches
db.students.find({ gpa: { $lt: 7.0 } })

// Step 2 — Delete only after you've confirmed the results look right
db.students.deleteMany({ gpa: { $lt: 7.0 } })
```

> ⚠️ MongoDB has **no recycle bin**. Once deleted, data is gone permanently unless you have a backup. Always check before you delete.

---

### ✅ Practice Task 9 (Delete)

1. Delete one student of your choice by name
2. Delete all students from "Jaipur"
3. Use `findOneAndDelete` to delete a student and print the returned document
4. Drop the `employees` collection you created in Practice Task 5

---

## 14. Aggregation Framework

### What Is It?

The Aggregation Framework is MongoDB's **data processing engine**. You pass documents through a series of **stages** — each stage transforms the data, and the output feeds into the next stage.

Think of it like a factory assembly line:

```
[Raw Docs] → [$match] → [$group] → [$sort] → [$project] → [Result]
```

### The Syntax

```javascript
db.collection.aggregate([
  { $stage1: { /* options */ } },
  { $stage2: { /* options */ } },
  // chain as many stages as needed
])
```

---

### Stage by Stage

#### `$match` — Filter Documents (like `find()`)

```javascript
db.students.aggregate([
  { $match: { gpa: { $gte: 8.0 } } }
])
```

#### `$group` — Summarize and Aggregate

```javascript
// Count students per city + average GPA per city
db.students.aggregate([
  {
    $group: {
      _id: "$city",              // Group by this field
      count: { $sum: 1 },        // Count documents per group
      avgGPA: { $avg: "$gpa" }   // Average GPA per group
    }
  }
])

// Overall stats across the whole collection
db.students.aggregate([
  {
    $group: {
      _id: null,                          // null = no grouping, entire collection
      totalStudents: { $sum: 1 },
      avgGPA: { $avg: "$gpa" },
      maxGPA: { $max: "$gpa" },
      minGPA: { $min: "$gpa" }
    }
  }
])
```

**Group Accumulators:**

| Operator | Description |
|----------|-------------|
| `$sum` | Sum of values |
| `$avg` | Average |
| `$min` | Minimum value |
| `$max` | Maximum value |
| `$count` | Count of documents |
| `$push` | Array of all values in the group |
| `$addToSet` | Array of unique values in the group |
| `$first` | First value in the group |
| `$last` | Last value in the group |

#### `$project` — Reshape Output Fields

```javascript
db.students.aggregate([
  {
    $project: {
      name: 1,
      gpa: 1,
      _id: 0,
      gradeCategory: {
        $cond: {
          if: { $gte: ["$gpa", 9.0] },
          then: "Distinction",
          else: "Pass"
        }
      }
    }
  }
])
```

#### `$sort`, `$limit`, `$skip`

```javascript
db.students.aggregate([
  { $sort: { gpa: -1 } },    // Sort descending by GPA
  { $skip: 2 },              // Skip first 2
  { $limit: 3 }              // Return next 3
])
```

#### `$unwind` — Flatten Arrays

Creates one document per array element — essential before grouping on array fields:

```javascript
// One doc per course (instead of one doc with a courses array)
db.students.aggregate([
  { $unwind: "$courses" }
])

// Count students enrolled per course
db.students.aggregate([
  { $unwind: "$courses" },
  { $group: { _id: "$courses", studentCount: { $sum: 1 } } },
  { $sort: { studentCount: -1 } }
])
```

#### `$lookup` — Join Two Collections (like SQL JOIN)

```javascript
// Add enrollment data first
db.enrollments.insertMany([
  { studentName: "Ravi Kumar",  subject: "Advanced Java",    semester: 3 },
  { studentName: "Priya Sharma", subject: "Machine Learning", semester: 3 }
])

// Join students with their enrollment records
db.students.aggregate([
  {
    $lookup: {
      from: "enrollments",          // Collection to join
      localField: "name",           // Field from students
      foreignField: "studentName",  // Matching field from enrollments
      as: "enrollmentDetails"       // Output array field name
    }
  }
])
```

#### `$addFields` — Add Computed Fields

```javascript
db.students.aggregate([
  {
    $addFields: {
      ageInMonths: { $multiply: ["$age", 12] },
      fullLabel: { $concat: ["$name", " - ", "$city"] }
    }
  }
])
```

#### `$count` — Count Pipeline Results

```javascript
db.students.aggregate([
  { $match: { gpa: { $gte: 8.0 } } },
  { $count: "highPerformers" }
])
```

---

### Full Pipeline Example — Top 3 Cities by Average GPA

```javascript
db.students.aggregate([
  { $match: { gpa: { $gte: 7.5 } } },                   // Stage 1: Filter
  {
    $group: {                                             // Stage 2: Group by city
      _id: "$city",
      avgGPA: { $avg: "$gpa" },
      studentCount: { $sum: 1 },
      names: { $push: "$name" }
    }
  },
  { $sort: { avgGPA: -1 } },                            // Stage 3: Sort by avg GPA
  { $limit: 3 },                                         // Stage 4: Top 3
  {
    $project: {                                           // Stage 5: Clean up output
      city: "$_id",
      avgGPA: { $round: ["$avgGPA", 2] },
      studentCount: 1,
      names: 1,
      _id: 0
    }
  }
])
```

---

### ✅ Practice Task 10 (Aggregation)

1. Count total students per city
2. Calculate the average, minimum, and maximum GPA across all students
3. Find the most popular course — the one with the highest student count
4. List all students with GPA ≥ 8.5, showing only `name` and `gpa`, sorted descending
5. Create a `scores` collection and use `$lookup` to join it with `students`

---

## 15. Indexes

### Why Indexes Matter

Without an index, MongoDB does a **collection scan** — it reads every single document to find a match. On small datasets that's fine. On millions of records, it's painfully slow.

> 💡 An index is like the index at the back of a textbook — instead of reading every page, you jump straight to the entry you need.

### Check Whether an Index Is Being Used

```javascript
db.students.find({ city: "Mumbai" }).explain("executionStats")

// In the output, look for:
// "COLLSCAN"  → Bad. Full scan. No index used.
// "IXSCAN"    → Good. Index is being used.
```

---

### Creating Indexes

#### Single Field Index

```javascript
db.students.createIndex({ city: 1 })    // 1 = Ascending, -1 = Descending
```

#### Compound Index (Multiple Fields)

```javascript
db.students.createIndex({ city: 1, gpa: -1 })
```

> 💡 **Field order matters in compound indexes.** Put equality fields first, range fields last.

#### Unique Index

```javascript
// Enforces that no two documents can have the same email
db.students.createIndex({ email: 1 }, { unique: true })
```

#### Text Index — Full-Text Search

```javascript
db.students.createIndex({ name: "text", city: "text" })

// Use it
db.students.find({ $text: { $search: "Ravi Mumbai" } })
db.students.find({ $text: { $search: "\"Ravi Kumar\"" } })   // Exact phrase
```

#### Sparse Index — Only Index Docs That Have the Field

```javascript
db.students.createIndex({ email: 1 }, { sparse: true })
```

#### TTL Index — Auto-Delete After Time

```javascript
// Automatically delete session documents after 1 hour
db.sessions.createIndex({ createdAt: 1 }, { expireAfterSeconds: 3600 })
```

---

### Managing Indexes

```javascript
db.students.getIndexes()               // List all indexes on a collection
db.students.dropIndex({ city: 1 })     // Drop by field spec
db.students.dropIndex("city_1")        // Drop by index name
db.students.dropIndexes()              // Drop all indexes except _id
```

### Index Best Practices

1. ✅ Index fields you use in `find()`, `sort()`, and `$match`
2. ⚠️ Don't over-index — every index slows down write operations
3. ✅ In compound indexes, put equality fields first, range fields last
4. ✅ Always run `explain()` to verify your index is actually being used
5. ✅ Use sparse indexes for optional fields to save space

### Covered Queries — Maximum Speed

A query is "covered" when every field it needs is present in the index. MongoDB never reads the actual documents at all:

```javascript
// Create a covering compound index
db.students.createIndex({ city: 1, gpa: 1, name: 1 })

// This query is 100% covered — no document reads needed
db.students.find(
  { city: "Mumbai" },
  { gpa: 1, name: 1, _id: 0 }
)
```

---

### ✅ Practice Task 11 (Indexes)

1. Create a single-field index on `gpa`
2. Create a compound index on `city` and `gpa`
3. Use `explain()` before and after adding an index — compare `COLLSCAN` vs `IXSCAN`
4. Create a unique index on `email`
5. List all indexes using `getIndexes()`

---

## 16. Utilities

MongoDB ships with command-line tools for backup, restore, export, and monitoring. Run these in **Command Prompt** (not inside `mongosh`).

### `mongodump` — Backup Your Database

```bash
# Backup the entire MongoDB instance
mongodump --out C:\backup\mongodb\

# Backup one specific database
mongodump --db ibmtraining --out C:\backup\

# Backup one specific collection
mongodump --db ibmtraining --collection students --out C:\backup\
```

### `mongorestore` — Restore from Backup

```bash
# Restore everything
mongorestore C:\backup\mongodb\

# Restore one database
mongorestore --db ibmtraining C:\backup\ibmtraining\

# Drop existing data before restoring (clean restore)
mongorestore --drop C:\backup\mongodb\
```

### `mongoexport` — Export to JSON or CSV

```bash
# Export as JSON
mongoexport --db ibmtraining --collection students --out students.json

# Export as CSV (specify which fields to include)
mongoexport --db ibmtraining --collection students --type=csv ^
  --fields name,age,city,gpa --out students.csv

# Export with a filter (high performers only)
mongoexport --db ibmtraining --collection students ^
  --query "{\"gpa\": {\"$gte\": 8.0}}" --out highperformers.json
```

### `mongoimport` — Import from JSON or CSV

```bash
# Import from JSON
mongoimport --db ibmtraining --collection students --file students.json

# Import from CSV
mongoimport --db ibmtraining --collection students --type=csv ^
  --headerline --file students.csv

# Drop existing collection before import (clean import)
mongoimport --db ibmtraining --collection students --drop --file students.json
```

### `mongostat` — Real-Time Server Stats

```bash
mongostat       # Live stats: inserts/sec, queries/sec, connections, memory
mongostat 2     # Refresh every 2 seconds
```

### `mongotop` — Per-Collection Activity

```bash
mongotop        # Read/write time per collection
mongotop 5      # Refresh every 5 seconds
```

### Shell-Based Monitoring (Inside `mongosh`)

```javascript
db.stats()                               // Database-level stats
db.students.stats()                      // Collection-level stats
db.currentOp()                           // Currently running operations
db.killOp(opid)                          // Kill a slow or stuck operation
db.students.validate()                   // Check collection integrity
db.runCommand({ compact: "students" })   // Reclaim disk space
```

---

### ✅ Practice Task 12 (Utilities)

1. Export the `students` collection to a JSON file using `mongoexport`
2. Create a new collection `students_backup` and import the JSON file into it using `mongoimport`
3. In `mongosh`, run `db.stats()` and `db.students.stats()` — examine the output

---

## 17. Wrap Up

### You Made It! Here's What You Covered

| # | Topic | Key Takeaway |
|---|-------|-------------|
| 1 | Course Navigation | Structure: Concept → Syntax → Code → Practice |
| 2 | Introduction | MongoDB = NoSQL document DB using BSON / JSON |
| 3 | Install Options | Local, VPS, or Atlas — each has a use case |
| 4 | Local Install | Windows 11 MSI installer + verify with `mongod --version` |
| 5 | VPS Server | Linux install + auth + remote access config |
| 6 | Atlas (Cloud) | Managed DBaaS — free tier, connection string, Spring Boot |
| 7 | Compass GUI | Visual browser, query builder, aggregation UI |
| 8 | `mongosh` Shell | JS-based CLI — the fastest way to interact with MongoDB |
| 9 | Data Types | BSON: String, Integer, Double, Boolean, Date, ObjectId, Array, Embedded Doc, etc. |
| 10 | CRUD | `insertOne/Many`, `find/findOne`, `updateOne/Many`, `deleteOne/Many` |
| 11 | Queries | `$eq`, `$gt`, `$in`, `$and`, `$or`, `$regex`, `$all`, `$elemMatch` |
| 12 | Updates | `$set`, `$inc`, `$push`, `$pull`, `$unset`, `$addToSet`, upsert |
| 13 | Deletes | `deleteOne`, `deleteMany`, `findOneAndDelete`, `drop` |
| 14 | Aggregation | Pipeline stages: `$match`, `$group`, `$sort`, `$project`, `$lookup`, `$unwind` |
| 15 | Indexes | Single, compound, unique, text, TTL — use `explain()` to verify |
| 16 | Utilities | `mongodump`, `mongorestore`, `mongoexport`, `mongoimport`, `mongostat` |

---

### 🎯 Capstone Project — Student Management System

Build a complete Student Management System using everything you've learned. This is your proof of work for the module.

**Database:** `sms_db`
**Collections:** `students`, `courses`, `enrollments`, `faculty`

#### Part 1 — Setup Data
- Insert 10 students: `name`, `age`, `email`, `city`, `gpa`, embedded `address`
- Insert 5 courses: `name`, `duration`, faculty reference
- Insert enrollment records linking students to courses

#### Part 2 — Queries
- Find all students with GPA ≥ 8.5
- Find students enrolled in "Advanced Java"
- List cities that have more than 2 students

#### Part 3 — Updates
- Update GPA for a specific student
- Add a new course to a student's `courses` array using `$push`

#### Part 4 — Aggregation
- Average GPA per city
- Most enrolled course (use `$unwind` + `$group`)
- Top 3 students by GPA

#### Part 5 — Indexes
- Create a unique index on `email`
- Create an index on `gpa`
- Use `explain()` to verify both indexes are being used

#### Part 6 — Export
- Export the `students` collection to a CSV file

---

### MongoDB with Java — Quick Reference

**Maven Dependency:**

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

**application.properties:**

```properties
# Local MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=ibmtraining

# Atlas (Cloud MongoDB)
# spring.data.mongodb.uri=mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/mydb
```

**Entity Class:**

```java
@Document(collection = "students")
public class Student {
    @Id
    private String id;
    private String name;
    private int age;
    private String city;
    private double gpa;
    private List<String> courses;
    // getters and setters
}
```

**Repository:**

```java
public interface StudentRepository extends MongoRepository<Student, String> {
    List<Student> findByCity(String city);
    List<Student> findByGpaGreaterThan(double gpa);
}
// Spring Data MongoDB auto-generates the implementation — you just declare the method name
```

**Service:**

```java
@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getTopStudents(double minGpa) {
        return studentRepository.findByGpaGreaterThan(minGpa);
    }
}
```

---

### 📚 Further Learning

| Resource | Link |
|----------|------|
| Official Documentation | https://docs.mongodb.com |
| MongoDB University (free courses) | https://university.mongodb.com |
| MongoDB Atlas | https://www.mongodb.com/atlas |
| MongoDB Developer Blog | https://www.mongodb.com/developer |
| Spring Data MongoDB | https://spring.io/projects/spring-data-mongodb |

---

### ⚡ Quick Reference Cheat Sheet

```javascript
// ===== DATABASE & COLLECTIONS =====
show dbs                            // List databases
use mydb                            // Switch / create database
db.dropDatabase()                   // ☢️ Drop current database
show collections                    // List collections
db.createCollection("name")         // Create collection explicitly
db.name.drop()                      // Drop a collection

// ===== CREATE =====
db.col.insertOne({})
db.col.insertMany([{}, {}])

// ===== READ =====
db.col.find()
db.col.find({ field: value })
db.col.findOne({ field: value })
db.col.find().sort({ field: 1 }).limit(5).skip(2)
db.col.countDocuments({})

// ===== UPDATE =====
db.col.updateOne({ filter }, { $set: { field: val } })
db.col.updateMany({ filter }, { $set: { field: val } })
db.col.replaceOne({ filter }, { newDoc })
db.col.updateOne({ filter }, { $set: {} }, { upsert: true })

// ===== DELETE =====
db.col.deleteOne({ filter })
db.col.deleteMany({ filter })
db.col.findOneAndDelete({ filter })

// ===== INDEXES =====
db.col.createIndex({ field: 1 })
db.col.createIndex({ f1: 1, f2: -1 })
db.col.createIndex({ email: 1 }, { unique: true })
db.col.getIndexes()
db.col.dropIndex({ field: 1 })

// ===== AGGREGATION =====
db.col.aggregate([
  { $match: {} },
  { $group: { _id: "$field", count: { $sum: 1 } } },
  { $sort: { count: -1 } },
  { $limit: 5 },
  { $project: { field: 1, _id: 0 } }
])

// ===== QUERY OPERATORS =====
// Comparison:  $eq  $ne  $gt  $gte  $lt  $lte  $in  $nin
// Logical:     $and  $or  $not  $nor
// Element:     $exists  $type
// Array:       $all  $elemMatch  $size
// Evaluation:  $regex  $expr  $where

// ===== UPDATE OPERATORS =====
// Field:  $set  $unset  $inc  $mul  $rename  $min  $max  $currentDate
// Array:  $push  $pop  $pull  $pullAll  $addToSet
```

---

*IBM Java Full Stack Development Training Program · MongoDB Module · 1.5 Days*
