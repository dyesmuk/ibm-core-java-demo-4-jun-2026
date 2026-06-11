# ☕ Running Maven Projects — Step by Step
### A Practical Guide for Running Simple Maven Projects

---

> **Who is this for?** Java developers and students who want to learn how to build and run Maven projects using command line and Eclipse.

---

# 📋 Table of Contents

1. Verify Maven Installation
2. Running Maven Web Application
3. Running Multi Module Project
4. Running Organized Multi Module Project
5. Running Projects in Eclipse
6. Useful Maven Commands
7. Common Problems and Fixes

---

# 1. Verify Maven Installation

Before running any project, verify Maven is installed properly.

Open terminal / command prompt:

```bash
mvn -version
```

Expected output:

```text
Apache Maven 3.x
Java version: 17
```

If this works — Maven setup is correct. ✅

---

# 2. Running Maven Web Application

## Project Name

```text
maven-webapp-hello
```

---

## Step 1 — Extract ZIP

Extract:

```text
maven-webapp-hello.zip
```

---

## Step 2 — Open Terminal

Go inside the project folder:

```bash
cd maven-webapp-hello
```

---

## Step 3 — Build the Project

Run:

```bash
mvn clean package
```

Maven creates:

```text
target/maven-webapp-hello-1.0.0.war
```

---

## Step 4 — Deploy in Tomcat

Copy the WAR file into:

```text
apache-tomcat/webapps/
```

Start Tomcat server.

Open browser:

```text
http://localhost:8080/maven-webapp-hello/
```

Expected output:

```text
Hello World from Maven Web Application
```

---

# 3. Running Multi Module Project

## Project Name

```text
multi-module-hello
```

---

## Step 1 — Extract ZIP

```text
multi-module-hello.zip
```

---

## Step 2 — Open Terminal in Parent Folder

```bash
cd multi-module-hello
```

---

## Step 3 — Build All Modules

```bash
mvn clean install
```

Maven automatically builds:

```text
common-module
web-module
```

in correct dependency order.

---

## Step 4 — Run Main Class

Run this class:

```text
web-module/src/main/java/com/demo/web/App.java
```

Expected output:

```text
Hello from Common Module
```

---

# 4. Running Organized Multi Module Project

## Project Name

```text
organized-multi-module-hello
```

---

## Step 1 — Extract ZIP

```text
organized-multi-module-hello.zip
```

---

## Step 2 — Open Terminal

```bash
cd organized-multi-module-hello
```

---

## Step 3 — Build Entire Project

```bash
mvn clean install
```

Modules build in sequence:

```text
common
→ service
→ api
```

---

## Step 4 — Run Main Class

Run:

```text
api/src/main/java/com/demo/api/App.java
```

Expected output:

```text
Hello from Common -> Service Layer
```

---

# 5. Running Projects in Eclipse

## Import Maven Project

Open Eclipse:

```text
File
→ Import
→ Maven
→ Existing Maven Projects
```

Select extracted project folder.

Click Finish.

---

## Update Maven Dependencies

Right click project:

```text
Maven → Update Project
```

Shortcut:

```text
Alt + F5
```

---

## Run Java Application

Right click:

```text
App.java
```

Then:

```text
Run As → Java Application
```

---

# 6. Useful Maven Commands

| Command | Purpose |
|---|---|
| `mvn clean` | Deletes target folder |
| `mvn compile` | Compiles Java code |
| `mvn test` | Runs tests |
| `mvn package` | Creates JAR/WAR |
| `mvn install` | Installs into local `.m2` |
| `mvn clean install` | Fresh full build |

---

# 7. Common Problems and Fixes

| Problem | Solution |
|---|---|
| `mvn` command not found | Configure `MAVEN_HOME` and PATH |
| Dependencies not downloading | Check internet connection |
| Eclipse shows red errors | Maven → Update Project |
| Java version mismatch | Verify installed JDK version |
| Build failed | Run `mvn clean install` again |

---

# Quick Reference

```text
pom.xml      → Maven configuration
target/      → Build output
.m2/         → Local Maven repository

Main Commands:
mvn clean
mvn compile
mvn package
mvn install
mvn clean install
```

---

# Final Notes

These projects are intentionally kept:

- Minimal
- Beginner friendly
- Simple Hello World examples
- Easy to understand

They are useful for learning:

- Maven basics
- WAR packaging
- Multi-module builds
- Parent-child POM structure
- Dependency relationships

---

*Happy Learning Maven! 🚀*
