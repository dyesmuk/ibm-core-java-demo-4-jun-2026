# ☕ Running Maven Projects — Step by Step
### A Practical Guide for Running Simple Maven Projects

---

> **Who is this for?** Java developers and trainees who want to learn how to install Maven and Tomcat, run Maven projects using command line, and import/run them inside Eclipse.

---

# 📋 Table of Contents

# Part 1 — Installations and Setup
1. Java Verification
2. Maven Installation
3. Apache Tomcat Installation
4. Verifying Installations

# Part 2 — Running Projects Using Command Prompt
5. Running Maven Web Application
6. Running Organized Multi Module Project

# Part 3 — Running Projects in Eclipse
8. Importing Maven Projects into Eclipse
9. Running Multi Module Projects in Eclipse
10. Running Maven Web Application in Eclipse
11. Useful Maven Commands
12. Common Problems and Fixes

---

# Part 1 — Installations and Setup

# 1. Java Verification

Before installing Maven or Tomcat, verify Java is already installed.

Open command prompt:

```bash
java -version
```

Expected output:

```text
Java 17
```

If Java version appears — Java setup is correct. ✅

---

# 2. Maven Installation

## Step 1 — Download Maven

Download from:

https://maven.apache.org/download.cgi

Download:

```text
Binary zip archive
```

Example:

```text
apache-maven-3.9.x-bin.zip
```

---

## Step 2 — Extract Maven

Extract somewhere simple:

```text
C:\maven
```

Example:

```text
C:\maven\apache-maven-3.9.x
```

---

## Step 3 — Configure Environment Variables

Open:

```text
Edit System Environment Variables
```

Then:

```text
Environment Variables
```

Create:

```text
MAVEN_HOME = C:\maven\apache-maven-3.9.x
```

Edit PATH and add:

```text
%MAVEN_HOME%\bin
```

Click OK.

---

# 3. Apache Tomcat Installation

## Step 1 — Download Tomcat

Download from:

https://tomcat.apache.org/download-10.cgi

Download ZIP version.

Example:

```text
apache-tomcat-10.1.xx.zip
```

---

## Step 2 — Extract Tomcat

Extract to:

```text
C:\tomcat
```

Example:

```text
C:\tomcat\apache-tomcat-10.1.xx
```

---

## Step 3 — Start Tomcat Using Command Prompt

Open command prompt.

Go to Tomcat bin folder:

```bash
cd C:\tomcat\apache-tomcat-10.1.xx\bin
```

Start Tomcat:

```bash
startup.bat
```

Tomcat server starts.

---

## Step 4 — Verify Tomcat

Open browser:

```text
http://localhost:8080
```

You should see Apache Tomcat welcome page. ✅

---

# 4. Verifying Installations

## Verify Maven

Run:

```bash
mvn -version
```

Expected:

```text
Apache Maven 3.x
Java version: 17
```

---

## Verify Tomcat

Open:

```text
http://localhost:8080
```

Tomcat homepage should open.

---

# Part 2 — Running Projects Using Command Prompt

# 5. Running Maven Web Application

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

## Step 2 — Open Command Prompt

Go inside project folder:

```bash
cd maven-webapp-hello
```

---

## Step 3 — Build WAR File

Run:

```bash
mvn clean package
```

WAR file gets created inside:

```text
target/
```

Generated WAR:

```text
maven-webapp-hello-1.0.0.war
```

---

## Step 4 — Copy WAR into Tomcat

Copy:

```text
target/maven-webapp-hello-1.0.0.war
```

into:

```text
apache-tomcat/webapps/
```

---

## Step 5 — Restart Tomcat Using Command Prompt

Open another command prompt.

Go to:

```bash
cd C:\tomcat\apache-tomcat-10.1.xx\bin
```

Stop Tomcat:

```bash
shutdown.bat
```

Start Tomcat again:

```bash
startup.bat
```

---

## Step 6 — Open Application

Open browser:

```text
http://localhost:8080/maven-webapp-hello-1.0.0/
```

Expected output:

```text
Hello World from Maven Web Application
```

---



---

# 6. Running Organized Multi Module Project

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

## Step 2 — Open Command Prompt

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

# Part 3 — Running Projects in Eclipse

# 8. Importing Maven Projects into Eclipse

Open Eclipse.

Go to:

```text
File
→ Import
→ Maven
→ Existing Maven Projects
```

Browse to extracted project folder.

Click:

```text
Finish
```

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

# 9. Running Organized Multi Module Project in Eclipse



---

## Run organized-multi-module-hello

Open:

```text
api/src/main/java/com/demo/api/App.java
```

Right click:

```text
Run As → Java Application
```

Expected output:

```text
Hello from Common -> Service Layer
```

---

# 10. Running Maven Web Application in Eclipse

## Step 1 — Build Project

Right click project:

```text
Run As → Maven build
```

Goals:

```text
clean package
```

Click Run.

WAR gets generated inside:

```text
target/
```

---

## Step 2 — Copy WAR to Tomcat

Copy:

```text
maven-webapp-hello-1.0.0.war
```

into:

```text
apache-tomcat/webapps/
```

---

## Step 3 — Restart Tomcat Using Command Prompt

Open command prompt.

Go to:

```bash
cd C:\tomcat\apache-tomcat-10.1.xx\bin
```

Stop Tomcat:

```bash
shutdown.bat
```

Start Tomcat again:

```bash
startup.bat
```

---

## Step 4 — Open Browser

```text
http://localhost:8080/maven-webapp-hello-1.0.0/
```

Expected output:

```text
Hello World from Maven Web Application
```

---

# 11. Useful Maven Commands

| Command | Purpose |
|---|---|
| `mvn clean` | Deletes target folder |
| `mvn compile` | Compiles Java code |
| `mvn test` | Runs tests |
| `mvn package` | Creates JAR/WAR |
| `mvn install` | Installs into local `.m2` |
| `mvn clean install` | Fresh full build |

---

# 12. Common Problems and Fixes

| Problem | Solution |
|---|---|
| `mvn` command not found | Configure `MAVEN_HOME` |
| Port 8080 already used | Change Tomcat port |
| Eclipse red errors | Maven → Update Project |
| WAR not opening | Verify correct URL |
| 404 error | Verify WAR copied correctly |
| Tomcat not starting | Verify Java installed |

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

These projects help trainees learn:

- Maven basics
- WAR packaging
- Multi-module projects
- Parent-child POM structure
- Dependency management
- Tomcat deployment
- Eclipse integration

---

*Happy Learning Maven! 🚀*
