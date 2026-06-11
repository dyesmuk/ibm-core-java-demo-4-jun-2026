# ☕ Maven — Build Like a Pro
### A Practical Guide for Java Developers

---

> **Who is this for?** Java developers who want to stop manually managing JARs, fix "works on my machine" problems, and ship projects that actually build anywhere.

---

## 📋 Table of Contents

1. [Introduction](#1-introduction)
2. [Simple Software Setup](#2-simple-software-setup)
3. [Maven Project Creation and Key Concepts](#3-maven-project-creation-and-key-concepts)
4. [Maven in Eclipse](#4-maven-in-eclipse)
5. [Maven Web Application](#5-maven-web-application)
6. [Multi Module Project Creation](#6-multi-module-project-creation)
7. [Organizing the Multi Module Project](#7-organizing-the-multi-module-project)
8. [Scopes](#8-scopes)
9. [Profiles](#9-profiles)
10. [Wrap Up](#10-wrap-up)

---

## 1. Introduction

### What Problem Does Maven Solve?

Imagine you join a team. You clone the repo. You try to run the project. It doesn't build.

"Just download these 12 JARs manually." 😩

**Maven fixes this.** It is a **build automation and dependency management tool** for Java projects. Tell Maven what your project needs — it fetches everything, builds everything, tests everything.

### What Maven Does For You

| Without Maven | With Maven |
|---|---|
| Download JARs manually | Declare dependencies, Maven downloads them |
| "It works on my PC 🤷" | Same build everywhere |
| Scripts scattered everywhere | Standard lifecycle: compile → test → package → deploy |
| No standard project layout | Convention-based folder structure |

### Core Philosophy: Convention Over Configuration

Maven has **opinions** about how a project should look. Follow the convention → less config. Fight it → pain.

### Maven vs. Alternatives

- **Ant** — Older, XML-based, you script everything manually. Powerful but verbose.
- **Gradle** — Modern, Groovy/Kotlin DSL, flexible. Used heavily in Android.
- **Maven** — XML-based, opinionated, huge ecosystem. Industry standard for Java enterprise.

> For Spring Boot, enterprise Java, and CI/CD pipelines — Maven is still king.

---

## 2. Simple Software Setup

### Prerequisites

- ✅ Java JDK 11+ installed
- ✅ `JAVA_HOME` environment variable set
- ✅ Eclipse IDE (2023-06 or later recommended)

### Step 1 — Download Maven

1. Go to [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Download the **Binary zip archive** (e.g., `apache-maven-3.9.x-bin.zip`)
3. Extract it somewhere clean, e.g., `C:\Program Files\Maven\apache-maven-3.9.x`

### Step 2 — Set Environment Variables (Windows 11)

1. Search **"Edit the system environment variables"** → Click **Environment Variables**
2. Under **System Variables**, click **New**:
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Program Files\Maven\apache-maven-3.9.x`
3. Find the `Path` variable → Edit → New → Add: `%MAVEN_HOME%\bin`
4. Click OK everywhere.

### Step 3 — Verify Installation

Open **Command Prompt** and run:

```bash
mvn -version
```

Expected output:
```
Apache Maven 3.9.x (...)
Maven home: C:\Program Files\Maven\apache-maven-3.9.x
Java version: 17.x.x, vendor: ...
```

🎉 If you see this — you're good!

### The Local Repository

When Maven downloads dependencies, it stores them in:

```
C:\Users\<YourName>\.m2\repository
```

Think of it as your **local cache**. Download once, reuse forever (unless the internet is down 😅).

### settings.xml (Optional but Good to Know)

Located at `C:\Users\<YourName>\.m2\settings.xml` — this is where you configure:
- Mirror repositories (proxy servers in corporate environments)
- Custom repository URLs
- Credentials for private repos

---

## 3. Maven Project Creation and Key Concepts

### The Magic File: `pom.xml`

Every Maven project has a **Project Object Model** file — `pom.xml`. This is the heart of your project.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <!-- Project Identity (GAV Coordinates) -->
    <groupId>com.example</groupId>
    <artifactId>my-first-app</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <!-- Human-readable info -->
    <name>My First Maven App</name>

    <!-- Dependencies go here -->
    <dependencies>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.13.0</version>
        </dependency>
    </dependencies>

</project>
```

### GAV Coordinates — The Address of Every Library

Every artifact in Maven is identified by three things:

| Coordinate | Description | Example |
|---|---|---|
| `groupId` | Organization / package namespace | `org.springframework` |
| `artifactId` | Project / library name | `spring-core` |
| `version` | Version of the artifact | `6.1.0` |

> Think of it like an address: **City (groupId) → Street (artifactId) → House Number (version)**

### Standard Directory Structure

```
my-project/
├── pom.xml                         ← The Maven config file
└── src/
    ├── main/
    │   ├── java/                   ← Your Java source code
    │   └── resources/              ← Config files (application.properties, etc.)
    └── test/
        ├── java/                   ← Your test classes
        └── resources/              ← Test-specific configs
```

> **Rule:** Never put source code outside `src/main/java`. Maven won't compile it.

### The Build Lifecycle

Maven builds happen in **phases**. Each phase does one thing:

```
validate → compile → test → package → verify → install → deploy
```

| Phase | What it Does |
|---|---|
| `validate` | Checks the project is correct |
| `compile` | Compiles `src/main/java` |
| `test` | Runs unit tests |
| `package` | Bundles into JAR/WAR |
| `install` | Copies to your local `.m2` repository |
| `deploy` | Pushes to a remote repository |

> **Key insight:** Running `mvn package` automatically runs all phases before it. You don't call each one manually.

### Useful Maven Commands

```bash
mvn compile              # Just compile
mvn test                 # Compile + run tests
mvn package              # Compile + test + package to JAR/WAR
mvn install              # Package + install to local .m2
mvn clean                # Delete the target/ folder
mvn clean package        # Clean build — start fresh then package
mvn clean install -DskipTests   # Skip tests (fast builds during dev)
```

> 💡 `mvn clean install` is the command you'll type 1000 times in your career. Memorize it.

### Plugins — Maven's Superpowers

Maven does its work through **plugins**. Every phase is executed by a plugin.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Common plugins:

| Plugin | Purpose |
|---|---|
| `maven-compiler-plugin` | Compiles Java code |
| `maven-surefire-plugin` | Runs unit tests |
| `maven-war-plugin` | Packages WAR files |
| `maven-jar-plugin` | Packages JAR files |
| `spring-boot-maven-plugin` | Creates runnable Spring Boot JARs |

---

## 4. Maven in Eclipse

### M2Eclipse — Already Built In

Eclipse ships with the **M2Eclipse (m2e)** plugin. No extra installation needed.

### Creating a Maven Project in Eclipse

1. **File → New → Maven Project**
2. Check **"Create a simple project (skip archetype selection)"** for a blank project
   - OR uncheck it to use an **archetype** (project template)
3. Fill in:
   - **Group Id:** `com.yourname`
   - **Artifact Id:** `hello-maven`
   - **Version:** `1.0.0` (default)
   - **Packaging:** `jar`
4. Click **Finish**

Eclipse creates the standard Maven folder structure automatically. ✅

### Using Archetypes (Project Templates)

Archetypes are **project starters** — pre-configured templates.

| Archetype | Use Case |
|---|---|
| `maven-archetype-quickstart` | Simple Java project |
| `maven-archetype-webapp` | Java Web App (WAR) |
| `maven-archetype-j2ee-simple` | Enterprise app skeleton |

To use: uncheck "skip archetype selection" and search/filter from the list.

### Adding Dependencies via Eclipse

**Option A — Edit pom.xml directly** (Recommended — gets you comfortable with XML):
1. Open `pom.xml`
2. Switch to the **pom.xml** tab at the bottom
3. Add your `<dependency>` block

**Option B — Using the Dependency Tab:**
1. Open `pom.xml`
2. Click the **Dependencies** tab
3. Click **Add...** → search and select

After adding, **right-click project → Maven → Update Project** (or `Alt+F5`). Eclipse will download the JARs.

### Importing an Existing Maven Project

Someone gave you a Maven project folder? Import it:

1. **File → Import → Maven → Existing Maven Projects**
2. Browse to the folder containing `pom.xml`
3. Click Finish

Maven auto-downloads all dependencies. No more "send me the libs folder" emails! 🎉

### Running Maven Goals from Eclipse

**Right-click project → Run As:**
- **Maven build...** → type goal (e.g., `clean install`)
- **Maven install** — shortcut for `mvn install`
- **Maven clean** — shortcut for `mvn clean`

Or use the **Maven Build** run configuration for reusable goals.

### Troubleshooting in Eclipse

| Problem | Fix |
|---|---|
| Red errors after adding dependency | Right-click → Maven → Update Project (`Alt+F5`) |
| Dependency not downloading | Check internet; try `mvn dependency:resolve` in terminal |
| `pom.xml` errors | Check XML syntax; validate via the Overview tab |
| Missing source attachment | Right-click dependency in Build Path → Download Sources |

---

## 5. Maven Web Application

### Packaging: JAR vs WAR

| Type | Used For | Runs On |
|---|---|---|
| `jar` | Standalone apps, libraries | JVM / Spring Boot embedded server |
| `war` | Web apps deployed to a server | Tomcat, JBoss, GlassFish |

> With Spring Boot, you mostly use JAR (embedded Tomcat inside). WAR is for traditional deployments.

### Creating a Web Application

**Using Archetype:**
1. New → Maven Project → Uncheck "skip archetype"
2. Filter: `maven-archetype-webapp`
3. Fill GAV → Finish

**Project Structure:**

```
my-webapp/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/example/
        │       └── HelloServlet.java
        ├── resources/
        └── webapp/
            ├── WEB-INF/
            │   └── web.xml          ← Deployment descriptor
            └── index.jsp            ← Your web pages
```

### pom.xml for Web App

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>my-webapp</artifactId>
    <version>1.0.0</version>
    <packaging>war</packaging>          <!-- KEY: packaging is war -->

    <dependencies>
        <!-- Servlet API (provided by the server at runtime) -->
        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>6.0.0</version>
            <scope>provided</scope>     <!-- Don't package this in WAR -->
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.4.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### Sample Servlet

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.getWriter().println("Hello from Maven Web App! 🚀");
    }
}
```

### Running with Tomcat Maven Plugin

Add to `pom.xml` and run `mvn tomcat7:run`:

```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <port>8080</port>
        <path>/myapp</path>
    </configuration>
</plugin>
```

> **With Spring Boot?** Just use `mvn spring-boot:run`. The embedded Tomcat handles everything.

---

## 6. Multi Module Project Creation

### Why Multi Module?

Real applications aren't one giant blob of code. You split them:

```
ecommerce-app/
├── ecommerce-common/      ← Shared models, utilities
├── ecommerce-data/        ← Database layer (repos, entities)
├── ecommerce-service/     ← Business logic
└── ecommerce-web/         ← REST API / Web layer
```

**Benefits:**
- Build only what changed
- Teams own their modules
- Independent versioning possible
- Reuse modules across projects

### The Parent POM — The Boss

The **parent** `pom.xml` ties everything together:

```
ecommerce-app/
├── pom.xml                ← Parent POM (packaging: pom)
├── ecommerce-common/
│   └── pom.xml
├── ecommerce-data/
│   └── pom.xml
└── ecommerce-web/
    └── pom.xml
```

### Creating in Eclipse

**Step 1 — Create the Parent:**
1. New → Maven Project → Simple project
2. **Group Id:** `com.ecommerce`
3. **Artifact Id:** `ecommerce-app`
4. **Packaging:** `pom` ← **Important!**

**Step 2 — Create Child Modules:**
1. Right-click the parent project → **New → Other → Maven Module**
2. Module name: `ecommerce-common`
3. Repeat for other modules

Eclipse automatically links children to the parent.

### Parent pom.xml

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.ecommerce</groupId>
    <artifactId>ecommerce-app</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>        <!-- Must be pom for parent -->

    <modules>
        <module>ecommerce-common</module>
        <module>ecommerce-data</module>
        <module>ecommerce-service</module>
        <module>ecommerce-web</module>
    </modules>
</project>
```

### Child pom.xml

```xml
<project>
    <modelVersion>4.0.0</modelVersion>

    <!-- Reference to parent -->
    <parent>
        <groupId>com.ecommerce</groupId>
        <artifactId>ecommerce-app</artifactId>
        <version>1.0.0</version>
    </parent>

    <!-- Only artifactId needed — inherits groupId and version from parent -->
    <artifactId>ecommerce-common</artifactId>
    <packaging>jar</packaging>
</project>
```

### Building the Multi Module Project

From the **parent directory**:

```bash
mvn clean install        # Builds ALL modules in correct order
mvn clean install -pl ecommerce-common          # Build specific module
mvn clean install -pl ecommerce-web -am         # Build module + its dependencies
```

---

## 7. Organizing the Multi Module Project

### Dependency Management — No More Version Chaos

Without management, every child repeats versions:
```xml
<!-- BAD: Version scattered everywhere -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.0</version>   <!-- Repeated in every child pom! -->
</dependency>
```

**The fix: `<dependencyManagement>` in parent pom**

```xml
<!-- In PARENT pom.xml -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.0</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Now in child pom — **no version needed**:

```xml
<!-- In CHILD pom.xml — version inherited from parent -->
<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

> **Rule of thumb:** Declare versions in parent's `<dependencyManagement>`. Add usage in children's `<dependencies>`. One place to update, all modules benefit.

### Plugin Management

Same concept for plugins:

```xml
<!-- In PARENT pom.xml -->
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

All child modules inherit this compiler config. ✅

### Properties for Reuse

Avoid hardcoding versions inline — use properties:

```xml
<properties>
    <java.version>17</java.version>
    <spring.version>6.1.0</spring.version>
    <jackson.version>2.15.0</jackson.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring.version}</version>   <!-- Reference property -->
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Inter-Module Dependencies

`ecommerce-web` needs `ecommerce-service`? Just add it as a dependency:

```xml
<!-- In ecommerce-web/pom.xml -->
<dependencies>
    <dependency>
        <groupId>com.ecommerce</groupId>
        <artifactId>ecommerce-service</artifactId>
        <version>${project.version}</version>   <!-- Same version as parent -->
    </dependency>
</dependencies>
```

Maven resolves the build order automatically. It builds `ecommerce-service` before `ecommerce-web`. Smart! 🧠

### Recommended Module Structure for a Real App

```
myapp/
├── pom.xml                  ← Parent (packaging: pom)
├── myapp-common/            ← DTOs, Utils, Constants (no deps on other modules)
├── myapp-domain/            ← JPA Entities, Repository interfaces
├── myapp-service/           ← Business logic (depends on domain + common)
└── myapp-api/               ← REST Controllers (depends on service)
```

**Dependency flow** (one direction only — no cycles!):
```
api → service → domain → common
```

---

## 8. Scopes

### What is Scope?

**Scope** controls **when** and **where** a dependency is available.

By default, a dependency is available everywhere — compilation, testing, and runtime. Scopes let you restrict that.

### The 6 Dependency Scopes

| Scope | Compile | Test | Runtime | Packaged in JAR/WAR | Use Case |
|---|:---:|:---:|:---:|:---:|---|
| `compile` (default) | ✅ | ✅ | ✅ | ✅ | Most dependencies |
| `provided` | ✅ | ✅ | ❌ | ❌ | Servlet API (server provides it) |
| `runtime` | ❌ | ✅ | ✅ | ✅ | JDBC drivers, logging impl |
| `test` | ❌ | ✅ | ❌ | ❌ | JUnit, Mockito |
| `system` | ✅ | ✅ | ✅ | ❌ | Local JAR files (avoid this!) |
| `import` | — | — | — | — | Import BOM in dependencyManagement |

### Scope Examples

```xml
<dependencies>

    <!-- compile (default) — Spring Core needed everywhere -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-core</artifactId>
        <version>6.1.0</version>
    </dependency>

    <!-- provided — Servlet API: server provides it, don't bundle it -->
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <version>6.0.0</version>
        <scope>provided</scope>
    </dependency>

    <!-- runtime — MySQL driver: not needed to compile, only to run -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.1.0</version>
        <scope>runtime</scope>
    </dependency>

    <!-- test — JUnit: only for tests, never ship it -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>

</dependencies>
```

### BOM (Bill of Materials) — The `import` Scope

A BOM is a special POM that defines a curated set of dependency versions. Spring Boot ships one:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.2.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Now you can add Spring Boot deps **without versions** — the BOM manages them all:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- No version! Inherited from BOM -->
</dependency>
```

> This is exactly how Spring Boot Starter Parent works behind the scenes.

### Quick Scope Decision Guide

```
"Do I need it to compile?" → No → try runtime
"Does the server provide it?" → Yes → provided
"Only for tests?" → Yes → test
"Normal dependency?" → compile (default)
```

---

## 9. Profiles

### What is a Profile?

Different environments need different configs. Dev uses H2 in-memory DB, staging uses MySQL, production uses PostgreSQL — but you don't want three separate `pom.xml` files.

**Profiles** let you activate different build configurations based on conditions.

### Defining Profiles

```xml
<profiles>

    <!-- Development profile -->
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>  <!-- Active unless told otherwise -->
        </activation>
        <properties>
            <db.url>jdbc:h2:mem:devdb</db.url>
            <log.level>DEBUG</log.level>
            <build.env>development</build.env>
        </properties>
        <dependencies>
            <dependency>
                <groupId>com.h2database</groupId>
                <artifactId>h2</artifactId>
                <version>2.2.224</version>
                <scope>runtime</scope>
            </dependency>
        </dependencies>
    </profile>

    <!-- Production profile -->
    <profile>
        <id>prod</id>
        <properties>
            <db.url>jdbc:mysql://prod-server/myapp</db.url>
            <log.level>WARN</log.level>
            <build.env>production</build.env>
        </properties>
        <dependencies>
            <dependency>
                <groupId>com.mysql</groupId>
                <artifactId>mysql-connector-j</artifactId>
                <version>8.1.0</version>
                <scope>runtime</scope>
            </dependency>
        </dependencies>
    </profile>

    <!-- Testing profile -->
    <profile>
        <id>test</id>
        <properties>
            <db.url>jdbc:h2:mem:testdb</db.url>
            <log.level>INFO</log.level>
        </properties>
    </profile>

</profiles>
```

### Activating Profiles

**From command line:**
```bash
mvn clean install -P prod            # Activate prod profile
mvn clean install -P dev,test        # Activate multiple profiles
mvn clean install -P !prod           # Deactivate a profile
```

**In Eclipse:**
1. Right-click project → **Run As → Maven build...**
2. In **Profiles** field, type: `prod`

**Auto-activation by OS:**
```xml
<activation>
    <os>
        <name>Windows 11</name>
    </os>
</activation>
```

**Auto-activation by JDK version:**
```xml
<activation>
    <jdk>17</jdk>
</activation>
```

**Auto-activation by environment variable:**
```xml
<activation>
    <property>
        <name>env.BUILD_ENV</name>
        <value>production</value>
    </property>
</activation>
```

### Resource Filtering with Profiles

Place environment-specific properties in resource files and have Maven substitute values at build time:

**`src/main/resources/application.properties`:**
```properties
db.url=${db.url}
log.level=${log.level}
app.env=${build.env}
```

**Enable filtering in pom.xml:**
```xml
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>   <!-- Maven replaces ${} placeholders -->
        </resource>
    </resources>
</build>
```

Build with `mvn clean install -P prod` and the output `application.properties` will have the production values substituted in. ✅

### Profile Best Practices

- ✅ Make `dev` the default profile
- ✅ Never hardcode credentials in pom.xml — use environment variables or external config
- ✅ Keep profile-specific differences minimal
- ❌ Don't use profiles as a replacement for proper externalized config (use Spring's `application-{env}.properties` for app configs)

---

## 10. Wrap Up

### What You've Learned

| Topic | Key Takeaway |
|---|---|
| Introduction | Maven = build tool + dependency manager + standard structure |
| Setup | Install, set `MAVEN_HOME`, verify with `mvn -version` |
| pom.xml & GAV | The identity and config of every Maven project |
| Lifecycle | `validate → compile → test → package → install → deploy` |
| Eclipse | M2e built-in; create, import, run Maven goals from IDE |
| Web App | `<packaging>war</packaging>` + Servlet API as `provided` |
| Multi Module | Parent POM (`packaging: pom`) + child modules |
| Organization | `dependencyManagement`, `pluginManagement`, properties |
| Scopes | compile / provided / runtime / test / import |
| Profiles | Environment-specific builds with `-P <profile>` |

### The Commands You'll Use Daily

```bash
mvn clean install            # The workhorse
mvn clean install -DskipTests  # Faster builds during dev
mvn clean install -P prod    # Build for production
mvn dependency:tree          # Debug dependency conflicts
mvn help:effective-pom       # See the final computed pom
```

### What's Next

Now that you have Maven down, here's how it connects to what comes next:

- **Spring Boot** — `spring-boot-starter-parent` as parent, `spring-boot-maven-plugin` for packaging
- **Testing** — JUnit 5 and Mockito with `test` scope, `mvn test` in CI/CD
- **CI/CD (DevOps)** — Jenkins/GitHub Actions run `mvn clean install` on every push
- **Docker** — Build a JAR/WAR with Maven, then `COPY` it into a Docker image

```xml
<!-- Your future Spring Boot pom.xml will look like this -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

Everything you learned here is exactly how that works under the hood. 🚀

---

### Quick Reference Card

```
GAV          = groupId : artifactId : version
pom.xml      = project config file
.m2/         = local repository (cache)
target/      = build output folder

Lifecycle:   validate → compile → test → package → install → deploy

Scopes:      compile | provided | runtime | test | import

Commands:    mvn clean install     ← builds everything
             mvn -P <profile>      ← use a profile
             mvn -pl <module>      ← build one module
             mvn -DskipTests       ← skip tests
             mvn dependency:tree   ← see all deps
```

---

> **Pro tip:** When stuck, `mvn help:effective-pom` shows you the final merged POM — parent + child + defaults. It's like X-Ray vision for Maven. 🔍

---

*Happy building! 🏗️*
