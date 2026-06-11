# 01 — Spring Framework: Core, IoC & Dependency Injection

> 🏢 **EMS Context:** We're building an **Employee Management System**. Our domain has `Employee`, `Department`, `Job`, and `Project` entities. Every example in this module uses these — so by the end, you'll have a working mental model of a real app, not toy code.

---

## Table of Contents

- [Introduction](#introduction)
- [Software Setup](#software-setup)
- [Spring Core Concepts — IoC & DI](#spring-core-concepts--ioc--di)
- [Setter Injection](#setter-injection)
- [Life Cycle Methods](#life-cycle-methods)
- [Dependency Check, Inner Beans & Scopes](#dependency-check-inner-beans--scopes)
- [Constructor Injection](#constructor-injection)
- [Using Properties](#using-properties)
- [Auto-Wiring](#auto-wiring)
- [Standalone Collections](#standalone-collections)
- [Stereotype Annotations](#stereotype-annotations)
- [Injecting Interfaces](#injecting-interfaces)
- [Java Configuration](#java-configuration)
- [Java Configuration for Web Applications](#java-configuration-for-web-applications)
- [Wrap Up](#wrap-up)

---

## Introduction

### What is Spring?

Spring is a **lightweight, open-source Java framework** that handles all the boring infrastructure stuff — object creation, wiring, transactions, security — so you can focus on actual business logic.

Before Spring, Java enterprise apps (J2EE) were a nightmare: XML everywhere, server restarts to test anything, objects tightly glued to each other. Spring showed up and said *"there's a better way."*

### Why Spring still matters in 2025

| The old pain | Spring's fix |
|---|---|
| You create every object manually | Spring's IoC container creates and manages them |
| Objects hardcode their own dependencies | Dependency Injection wires things together |
| Boilerplate JDBC code everywhere | Spring JDBC/ORM templates |
| Manual transaction management | `@Transactional` — one annotation does it |
| Cross-cutting concerns scattered everywhere | AOP handles it in one place |

> 💡 **The HR analogy:** Spring is like your company's HR + IT team. They onboard new employees (create objects), give them their tools and teammates (inject dependencies), and handle their exit formalities (destroy beans). You just write the job description.

### The Spring Ecosystem

```
Spring Framework (what this module covers)
├── Core Container    → IoC, DI, Bean lifecycle
├── Spring JDBC       → Clean database access
├── Spring ORM        → Hibernate integration
├── Spring MVC        → Web layer (controllers, views)
└── Spring AOP        → Cross-cutting concerns

Beyond this module:
├── Spring Boot       → Auto-config, embedded Tomcat
├── Spring Data JPA   → Repository abstraction
├── Spring Security   → Auth & authorization
└── Spring Cloud      → Microservices toolkit
```

---

## Software Setup

**Environment:** Windows 11 + Eclipse IDE

### Step 1 — Install Spring Tools in Eclipse

1. Open Eclipse → **Help → Eclipse Marketplace**
2. Search: `Spring Tools 4`
3. Install **Spring Tools 4 (aka Spring Tool Suite 4)**
4. Restart Eclipse when prompted

### Step 2 — Create a Maven Project

1. **File → New → Maven Project**
2. Check **"Create a simple project (skip archetype selection)"**
3. Fill in:
   - Group Id: `com.ems`
   - Artifact Id: `ems-spring-core`
   - Packaging: `jar`
4. Click **Finish**

### Step 3 — Add Spring to `pom.xml`

Open `pom.xml` and add inside `<dependencies>`:

```xml
<dependencies>
    <!-- Spring Context pulls in Core, Beans, AOP automatically -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.1.4</version>
    </dependency>

    <!-- For @PostConstruct and @PreDestroy annotations -->
    <dependency>
        <groupId>jakarta.annotation</groupId>
        <artifactId>jakarta.annotation-api</artifactId>
        <version>2.1.1</version>
    </dependency>
</dependencies>
```

Save → Eclipse will auto-download dependencies (check the bottom progress bar).

### Project Structure

```
ems-spring-core/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ems/
│   │   │       ├── model/         ← Employee, Department, Job, Project
│   │   │       ├── service/       ← EmployeeService, DepartmentService
│   │   │       ├── repository/    ← EmployeeRepository
│   │   │       ├── config/        ← AppConfig (Java config)
│   │   │       └── Main.java
│   │   └── resources/
│   │       ├── beans.xml          ← XML config (early topics)
│   │       └── application.properties
└── pom.xml
```

---

## Spring Core Concepts — IoC & DI

### The Problem: Tight Coupling

```java
// Traditional Java — EmployeeService creates its own dependency
public class EmployeeService {
    // Hardcoded. Can't swap it. Can't test it without a real DB.
    private EmployeeRepository repo = new EmployeeRepository();
}
```

If `EmployeeRepository` changes its constructor, `EmployeeService` breaks. If you want to test with a mock, you can't. This is **tight coupling** — the worst.

### The Fix: Inversion of Control (IoC)

Instead of objects creating their dependencies, something external handles that. In Spring, that "something external" is the **IoC Container**.

```java
// Spring way — EmployeeService declares what it needs, Spring provides it
public class EmployeeService {
    private EmployeeRepository repo; // Spring will set this
}
```

**Inversion** = control over object creation is *inverted* — from the object itself to the container.

### Dependency Injection (DI)

DI is the mechanism Spring uses to implement IoC. Spring *injects* dependencies into objects rather than objects fetching them.

Three flavours:
1. **Setter Injection** — Spring calls setter methods
2. **Constructor Injection** — Spring uses the constructor
3. **Field Injection** — `@Autowired` directly on the field (convenient but avoid in production code)

### The ApplicationContext

The container that holds and manages all your beans.

```java
// XML-based config
ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

// Java-based config (covered later)
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```

> 🔑 **Bean** = any object managed by Spring's container. When Spring creates it, wires its dependencies, and manages its lifecycle — it's a bean.

---

## Setter Injection

Spring creates the object first, then calls setter methods to inject values. Good for **optional** dependencies.

### Setting up the EMS domain

**`src/main/java/com/ems/model/Department.java`**
```java
package com.ems.model;

public class Department {
    private int id;
    private String name;
    private String location;

    // Spring needs these setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }

    @Override
    public String toString() {
        return "Department{id=" + id + ", name='" + name + "', location='" + location + "'}";
    }
}
```

**`src/main/java/com/ems/service/EmployeeService.java`**
```java
package com.ems.service;

import com.ems.model.Department;

public class EmployeeService {
    private String serviceName;
    private Department department; // this is a dependency — another object

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void showInfo() {
        System.out.println("Service: " + serviceName);
        System.out.println("Assigned dept: " + department);
    }
}
```

**`src/main/resources/beans.xml`**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Bean 1: Department -->
    <bean id="hrDept" class="com.ems.model.Department">
        <property name="id"       value="10"/>
        <property name="name"     value="Human Resources"/>
        <property name="location" value="Mumbai"/>
    </bean>

    <!-- Bean 2: EmployeeService — injects the Department bean above -->
    <bean id="employeeService" class="com.ems.service.EmployeeService">
        <property name="serviceName" value="EMS Core Service"/>
        <property name="department"  ref="hrDept"/>   <!-- ref = another bean -->
    </bean>

</beans>
```

**`src/main/java/com/ems/Main.java`**
```java
package com.ems;

import com.ems.service.EmployeeService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context =
            new ClassPathXmlApplicationContext("beans.xml");

        EmployeeService service = context.getBean("employeeService", EmployeeService.class);
        service.showInfo();
    }
}
```

**Expected output:**
```
Service: EMS Core Service
Assigned dept: Department{id=10, name='Human Resources', location='Mumbai'}
```

> 💡 `value` is for primitives and Strings. `ref` is for referencing another bean by its `id`.

---

## Life Cycle Methods

Spring manages the full lifecycle of a bean. You can plug in your own logic at two points:
- **Init** — right after dependencies are injected (e.g. open DB connection, load cache)
- **Destroy** — just before the bean is removed (e.g. close connections, flush logs)

```
Container starts
  → Bean created
  → Dependencies injected
  → init-method runs      ← your hook
  → Bean in use
  → destroy-method runs   ← your hook
  → Container shuts down
```

### Option 1: XML config (classic)

```xml
<bean id="employeeService"
      class="com.ems.service.EmployeeService"
      init-method="onStart"
      destroy-method="onStop">
</bean>
```

```java
public class EmployeeService {

    public void onStart() {
        System.out.println("[EMS] Service starting up — loading configs...");
    }

    public void onStop() {
        System.out.println("[EMS] Service shutting down — releasing resources...");
    }
}
```

### Option 2: Annotations — `@PostConstruct` / `@PreDestroy` ✅ (use this)

```java
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class EmployeeService {

    @PostConstruct
    public void init() {
        System.out.println("[EMS] PostConstruct: service initialized");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("[EMS] PreDestroy: cleaning up before shutdown");
    }
}
```

> ⚠️ For `@PreDestroy` to fire, you must close the context explicitly:
> ```java
> ((ClassPathXmlApplicationContext) context).close();
> ```

### Lifecycle order (full picture)

```
1. Bean instantiated (constructor)
2. Dependencies injected (setters / constructor args)
3. @PostConstruct method runs
4. Bean is ready — used by app
5. @PreDestroy method runs
6. Bean destroyed
```

---

## Dependency Check, Inner Beans & Scopes

### Bean Scopes

Scope = how many instances Spring creates.

| Scope | Instances | When to use |
|---|---|---|
| `singleton` | **1 per container** (default) | Stateless services, repositories |
| `prototype` | **New one every time** `getBean()` is called | Stateful objects, forms |
| `request` | 1 per HTTP request | Web apps |
| `session` | 1 per HTTP session | Web apps |

```xml
<!-- Singleton — default, same instance always returned -->
<bean id="employeeService" class="com.ems.service.EmployeeService" scope="singleton"/>

<!-- Prototype — new instance on every getBean() call -->
<bean id="employeeForm" class="com.ems.model.EmployeeForm" scope="prototype"/>
```

**Verify with code:**
```java
EmployeeService s1 = context.getBean("employeeService", EmployeeService.class);
EmployeeService s2 = context.getBean("employeeService", EmployeeService.class);
System.out.println(s1 == s2); // true — same instance (singleton)
```

### Inner Beans

Define a bean inside another bean's `<property>` when it won't be reused elsewhere.

```xml
<bean id="employee" class="com.ems.model.Employee">
    <property name="name" value="Rohan"/>
    <property name="department">
        <!-- Inner bean — no id, not accessible from outside -->
        <bean class="com.ems.model.Department">
            <property name="name"     value="Engineering"/>
            <property name="location" value="Bangalore"/>
        </bean>
    </property>
</bean>
```

> 💡 Use inner beans when the nested object belongs exclusively to the parent and will never be referenced elsewhere.

### Dependency Check

Spring can verify that required properties are set before using a bean. In modern Spring (5+), `@Required` is deprecated — use constructor injection for mandatory deps instead (covered next).

---

## Constructor Injection

Spring injects dependencies via the constructor at object creation time. Best for **mandatory** dependencies — the object can't exist without them.

### EMS Example

**`src/main/java/com/ems/model/Employee.java`**
```java
package com.ems.model;

public class Employee {
    private int id;
    private String name;
    private String email;
    private Department department;

    // Constructor injection — all required at creation
    public Employee(int id, String name, String email, Department department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name +
               "', email='" + email + "', dept=" + department.getName() + "}";
    }
}
```

**`beans.xml`**
```xml
<!-- By index (position in constructor) -->
<bean id="emp1" class="com.ems.model.Employee">
    <constructor-arg index="0" value="101"/>
    <constructor-arg index="1" value="Priya Sharma"/>
    <constructor-arg index="2" value="priya@ems.com"/>
    <constructor-arg index="3" ref="hrDept"/>
</bean>

<!-- By name — much more readable, use this -->
<bean id="emp2" class="com.ems.model.Employee">
    <constructor-arg name="id"         value="102"/>
    <constructor-arg name="name"       value="Arjun Mehta"/>
    <constructor-arg name="email"      value="arjun@ems.com"/>
    <constructor-arg name="department" ref="hrDept"/>
</bean>
```

### Setter vs Constructor — when to use what

| | Setter Injection | Constructor Injection |
|---|---|---|
| When injected | After creation | During creation |
| Optional deps | ✅ Perfect | ❌ Not ideal |
| Mandatory deps | ⚠️ Can forget | ✅ Guaranteed |
| Immutability | ❌ Object can be changed | ✅ Object is consistent |
| Circular deps | ✅ Can handle | ❌ Will throw error |
| Spring team recommendation | — | ✅ Preferred |

---

## Using Properties

Hardcoding config values in beans is bad practice. Put them in a `.properties` file and inject them.

### `src/main/resources/application.properties`

```properties
ems.service.name=Employee Management System
ems.db.url=jdbc:mysql://localhost:3306/emsdb
ems.db.username=emsuser
ems.db.password=ems@123
ems.max.employees=500
```

### XML approach — `<context:property-placeholder>`

```xml
<!-- Add context namespace to beans.xml header first -->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
         http://www.springframework.org/schema/beans
         http://www.springframework.org/schema/beans/spring-beans.xsd
         http://www.springframework.org/schema/context
         http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- Load the properties file -->
    <context:property-placeholder location="classpath:application.properties"/>

    <bean id="employeeService" class="com.ems.service.EmployeeService">
        <property name="serviceName" value="${ems.service.name}"/>
        <property name="maxEmployees" value="${ems.max.employees}"/>
    </bean>

</beans>
```

### Annotation approach — `@Value` ✅ (more common)

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Value("${ems.service.name}")
    private String serviceName;

    @Value("${ems.max.employees}")
    private int maxEmployees;

    public void printConfig() {
        System.out.println("App: " + serviceName);
        System.out.println("Employee cap: " + maxEmployees);
    }
}
```

---

## Auto-Wiring

Manually writing `ref="beanId"` for every dependency gets tedious. Auto-wiring lets Spring detect and inject matching beans automatically.

### XML auto-wire modes

| Mode | How Spring matches |
|---|---|
| `no` | Default — no auto-wiring |
| `byName` | Property name == bean `id` |
| `byType` | Property type == bean class |
| `constructor` | Like `byType` but via constructor |

```xml
<!-- byType: Spring finds a Department bean and injects it -->
<bean id="employeeService" class="com.ems.service.EmployeeService"
      autowire="byType">
    <property name="serviceName" value="EMS Service"/>
    <!-- no need to specify department — Spring finds it automatically -->
</bean>

<bean id="anyNameWorks" class="com.ems.model.Department">
    <property name="name" value="IT"/>
</bean>
```

```xml
<!-- byName: property name 'department' must match bean id 'department' -->
<bean id="employeeService" class="com.ems.service.EmployeeService"
      autowire="byName">
</bean>

<bean id="department" class="com.ems.model.Department">  <!-- id must match -->
    <property name="name" value="IT"/>
</bean>
```

### `@Autowired` — annotation-based ✅ (modern standard)

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;

    // Constructor injection + @Autowired — Spring best practice
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentService departmentService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
    }
}
```

### Multiple beans of the same type? Use `@Qualifier`

```java
// Two implementations of NotificationService exist
@Autowired
@Qualifier("emailNotificationService")
private NotificationService notificationService;
```

---

## Standalone Collections

Inject `List`, `Set`, `Map`, and `Properties` directly through Spring config.

### EMS Example — Employee with skills, certifications, and contact info

**`Employee.java`** (add these fields)
```java
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Employee {
    private String name;
    private List<String> skills;             // ordered, allows duplicates
    private Set<String> roles;              // no duplicates
    private Map<String, String> certifications; // key-value
    private Properties contactInfo;         // String key-value pairs

    // setters + getters...
}
```

**`beans.xml`**
```xml
<bean id="seniorEmployee" class="com.ems.model.Employee">
    <property name="name" value="Divya Nair"/>

    <property name="skills">
        <list>
            <value>Java</value>
            <value>Spring Boot</value>
            <value>Microservices</value>
            <value>Docker</value>
        </list>
    </property>

    <property name="roles">
        <set>
            <value>EMPLOYEE</value>
            <value>TECH_LEAD</value>
        </set>
    </property>

    <property name="certifications">
        <map>
            <entry key="AWS Solutions Architect" value="2023-08-15"/>
            <entry key="Spring Professional"     value="2022-11-01"/>
        </map>
    </property>

    <property name="contactInfo">
        <props>
            <prop key="email">divya@ems.com</prop>
            <prop key="phone">+91-9876543210</prop>
            <prop key="slack">@divya.nair</prop>
        </props>
    </property>
</bean>
```

---

## Stereotype Annotations

Instead of declaring every bean in XML, mark your classes with stereotype annotations and let Spring scan and register them automatically.

| Annotation | Layer | Extra behaviour |
|---|---|---|
| `@Component` | Any | Generic — basic bean |
| `@Service` | Business logic | Signals service layer for AOP |
| `@Repository` | Data access | Converts SQL exceptions to Spring exceptions |
| `@Controller` | Web / MVC | Enables request mapping |
| `@RestController` | REST APIs | `@Controller` + `@ResponseBody` |

### Enable component scanning

**XML:**
```xml
<context:component-scan base-package="com.ems"/>
```

**Java Config (preferred):**
```java
@Configuration
@ComponentScan("com.ems")
public class AppConfig { }
```

### EMS full-stack example

**`EmployeeRepository.java`**
```java
package com.ems.repository;

import com.ems.model.Employee;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.ArrayList;

@Repository
public class EmployeeRepository {

    // Simulated DB — replace with actual JDBC/JPA later
    private List<Employee> employees = new ArrayList<>();

    public void save(Employee e) {
        employees.add(e);
        System.out.println("Saved: " + e.getName());
    }

    public Employee findById(int id) {
        return employees.stream()
            .filter(e -> e.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public List<Employee> findAll() {
        return employees;
    }
}
```

**`EmployeeService.java`**
```java
package com.ems.service;

import com.ems.model.Employee;
import com.ems.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void hire(Employee e) {
        employeeRepository.save(e);
        System.out.println("Employee hired: " + e.getName());
    }

    public Employee getEmployee(int id) {
        return employeeRepository.findById(id);
    }
}
```

---

## Injecting Interfaces

**Golden rule:** program to interfaces, not implementations. This is what makes Spring apps testable, flexible, and swappable.

### EMS Example — Notification system

```java
// Interface — the contract
public interface NotificationService {
    void notify(String recipient, String message);
}
```

```java
// Implementation 1 — Email
@Service("emailNotification")
public class EmailNotificationService implements NotificationService {

    @Override
    public void notify(String recipient, String message) {
        System.out.println("📧 EMAIL → " + recipient + ": " + message);
    }
}
```

```java
// Implementation 2 — SMS
@Service("smsNotification")
public class SmsNotificationService implements NotificationService {

    @Override
    public void notify(String recipient, String message) {
        System.out.println("📱 SMS → " + recipient + ": " + message);
    }
}
```

```java
// Consumer — injects the interface, not a specific implementation
@Service
public class EmployeeOnboardingService {

    private final NotificationService notificationService;

    @Autowired
    @Qualifier("emailNotification") // tell Spring which impl to inject
    public EmployeeOnboardingService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void onboard(Employee emp) {
        // ... save employee logic ...
        notificationService.notify(
            emp.getEmail(),
            "Welcome aboard, " + emp.getName() + "! Your EMS account is ready."
        );
    }
}
```

Want to switch from email to SMS? Change one `@Qualifier`. That's it. The rest of the code doesn't touch.

> 💡 **The courier analogy:** Your office ships packages via BlueDart today, FedEx tomorrow. The shipping desk (your service) only knows "I need a courier service." It doesn't care which company. Spring decides.

---

## Java Configuration

Modern Spring apps ditch XML entirely in favour of Java-based config. It's type-safe, refactorable, and IDE-friendly.

### `@Configuration` + `@Bean`

```java
package com.ems.config;

import com.ems.model.Department;
import com.ems.service.EmployeeService;
import com.ems.repository.EmployeeRepository;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@Configuration                              // marks this as a config class
@ComponentScan("com.ems")                   // scan for @Service, @Repository etc.
@PropertySource("classpath:application.properties")  // load .properties file
public class AppConfig {

    @Value("${ems.service.name}")
    private String serviceName;

    // Explicit bean — use @Bean for things that need custom setup
    // or for third-party classes you can't annotate
    @Bean
    public Department hrDepartment() {
        Department dept = new Department();
        dept.setId(10);
        dept.setName("Human Resources");
        dept.setLocation("Mumbai");
        return dept;
    }

    @Bean
    public Department itDepartment() {
        Department dept = new Department();
        dept.setId(20);
        dept.setName("Information Technology");
        dept.setLocation("Bangalore");
        return dept;
    }
}
```

### Loading Java Config in Main

```java
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(AppConfig.class);

        EmployeeService service = context.getBean(EmployeeService.class);
        service.hire(new Employee(101, "Riya Patel", "riya@ems.com", null));

        context.close(); // triggers @PreDestroy
    }
}
```

### XML vs Java Config vs Annotations — the real-world breakdown

| Approach | When to use |
|---|---|
| **XML config** | Legacy codebases, or when you can't touch source code |
| **Java `@Configuration` + `@Bean`** | Third-party beans (DataSource, RestTemplate, etc.) |
| **Annotations (`@Service`, `@Autowired`)** | Your own classes — default choice |

In a real project, you'll use all three: annotations for your own code, `@Bean` for library integrations, and maybe XML for legacy pieces.

---

## Java Configuration for Web Applications

When your app has a web layer (Spring MVC), you configure it with Java instead of `web.xml`.

### The three key pieces

```java
// 1. Web initializer — replaces web.xml
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer
        extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{ AppConfig.class };  // service, repo beans
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{ WebConfig.class };  // MVC beans
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{ "/" };  // all requests go through DispatcherServlet
    }
}
```

```java
// 2. App config — non-web beans (services, repos, data source)
@Configuration
@ComponentScan(basePackages = "com.ems",
               excludeFilters = @ComponentScan.Filter(
                   type = FilterType.ANNOTATION,
                   classes = Controller.class))
public class AppConfig {
    // datasource, service beans etc.
}
```

```java
// 3. Web config — MVC-specific beans
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.ems.controller")
public class WebConfig implements WebMvcConfigurer {

    // View resolver — maps logical names to JSP files
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    // Serve static resources (CSS, JS, images)
    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
```

> 💡 This setup is the foundation for Spring MVC — covered in detail in `03_Spring_MVC.md`.

---

## Wrap Up

### What you covered

| Topic | Key takeaway |
|---|---|
| IoC Container | Spring creates and manages objects — you don't |
| Setter Injection | Good for optional dependencies — Spring calls setters |
| Constructor Injection | Good for mandatory dependencies — immutable, preferred |
| Lifecycle | `@PostConstruct` and `@PreDestroy` for init/cleanup hooks |
| Scopes | `singleton` (default) vs `prototype` (new instance each time) |
| Inner Beans | Anonymous beans defined inside a parent bean |
| Properties | Externalise config using `.properties` + `@Value` |
| Auto-Wiring | Spring detects and injects matching beans — `@Autowired` |
| Collections | Inject `List`, `Set`, `Map`, `Properties` via XML or annotations |
| Stereotypes | `@Component`, `@Service`, `@Repository`, `@Controller` |
| Interface Injection | Program to interfaces + `@Qualifier` for flexibility |
| Java Config | `@Configuration` + `@Bean` replaces XML |
| Web Java Config | `WebAppInitializer` + `WebConfig` replaces `web.xml` |

### The dependency injection mental model

```
Your class declares what it needs (interface / type)
  ↓
Spring scans, finds matching beans
  ↓
Spring creates them in the right order
  ↓
Spring injects them into your class
  ↓
Your class just works — no manual wiring
```

---

*Next → [02_Spring_Data_Access.md](02_Spring_Data_Access.md) — Spring JDBC, ORM, and AOP*
