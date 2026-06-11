# 02 — Spring Framework: Data Access — JDBC, ORM & AOP

> 🏢 **EMS Context:** We're persisting `Employee`, `Department`, `Job`, and `Project` data to a MySQL database. This module goes from raw SQL with Spring JDBC → ORM with Hibernate → cutting through concerns with AOP.

---

## Table of Contents

- [Spring JDBC](#spring-jdbc)
- [Spring ORM](#spring-orm)
- [Spring AOP](#spring-aop)

---

## Spring JDBC

### Why Spring JDBC?

Plain JDBC is a mess:

```java
// Raw JDBC — don't do this
Connection conn = null;
PreparedStatement ps = null;
ResultSet rs = null;
try {
    conn = DriverManager.getConnection(url, user, pass);
    ps = conn.prepareStatement("SELECT * FROM employees WHERE id = ?");
    ps.setInt(1, empId);
    rs = ps.executeQuery();
    if (rs.next()) {
        // extract result...
    }
} catch (SQLException e) {
    // handle...
} finally {
    // close rs, ps, conn — each with their own try/catch
}
```

Spring JDBC replaces all that ceremony with `JdbcTemplate`. You write the SQL, Spring handles everything else.

### Setup

Add to `pom.xml`:

```xml
<!-- Spring JDBC -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>6.1.4</version>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>

<!-- Connection Pool -->
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.1.0</version>
</dependency>
```

### Database setup

Run this in MySQL Workbench:

```sql
CREATE DATABASE emsdb;
USE emsdb;

CREATE TABLE departments (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    location    VARCHAR(100)
);

CREATE TABLE employees (
    id            INT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) UNIQUE,
    salary        DECIMAL(10,2),
    department_id INT,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE projects (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    start_date  DATE,
    dept_id     INT,
    FOREIGN KEY (dept_id) REFERENCES departments(id)
);

-- Sample data
INSERT INTO departments (name, location) VALUES ('Human Resources', 'Mumbai');
INSERT INTO departments (name, location) VALUES ('Information Technology', 'Bangalore');
INSERT INTO departments (name, location) VALUES ('Finance', 'Delhi');

INSERT INTO employees (name, email, salary, department_id)
VALUES ('Priya Sharma', 'priya@ems.com', 75000, 2);
INSERT INTO employees (name, email, salary, department_id)
VALUES ('Arjun Mehta', 'arjun@ems.com', 85000, 2);
INSERT INTO employees (name, email, salary, department_id)
VALUES ('Sneha Rao', 'sneha@ems.com', 65000, 1);
```

### Configure DataSource

**`AppConfig.java`**
```java
package com.ems.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Configuration
@ComponentScan("com.ems")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${ems.db.url}")
    private String dbUrl;

    @Value("${ems.db.username}")
    private String dbUser;

    @Value("${ems.db.password}")
    private String dbPassword;

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(dbUrl);
        ds.setUsername(dbUser);
        ds.setPassword(dbPassword);
        ds.setMaximumPoolSize(10);
        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```

**`application.properties`** (add these):
```properties
ems.db.url=jdbc:mysql://localhost:3306/emsdb
ems.db.username=root
ems.db.password=yourpassword
```

### EmployeeRepository with JdbcTemplate

```java
package com.ems.repository;

import com.ems.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // --- CREATE ---
    public int save(Employee emp) {
        String sql = "INSERT INTO employees (name, email, salary, department_id) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
            emp.getName(), emp.getEmail(), emp.getSalary(), emp.getDepartmentId());
    }

    // --- READ ONE ---
    public Employee findById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new EmployeeRowMapper(), id);
    }

    // --- READ ALL ---
    public List<Employee> findAll() {
        String sql = "SELECT * FROM employees";
        return jdbcTemplate.query(sql, new EmployeeRowMapper());
    }

    // --- READ with condition ---
    public List<Employee> findByDepartment(int deptId) {
        String sql = "SELECT * FROM employees WHERE department_id = ?";
        return jdbcTemplate.query(sql, new EmployeeRowMapper(), deptId);
    }

    // --- UPDATE ---
    public int update(Employee emp) {
        String sql = "UPDATE employees SET name=?, email=?, salary=? WHERE id=?";
        return jdbcTemplate.update(sql,
            emp.getName(), emp.getEmail(), emp.getSalary(), emp.getId());
    }

    // --- DELETE ---
    public int delete(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // --- COUNT ---
    public int countByDepartment(int deptId) {
        String sql = "SELECT COUNT(*) FROM employees WHERE department_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, deptId);
    }

    // RowMapper — converts ResultSet row to Employee object
    private static class EmployeeRowMapper implements RowMapper<Employee> {
        @Override
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Employee emp = new Employee();
            emp.setId(rs.getInt("id"));
            emp.setName(rs.getString("name"));
            emp.setEmail(rs.getString("email"));
            emp.setSalary(rs.getDouble("salary"));
            emp.setDepartmentId(rs.getInt("department_id"));
            return emp;
        }
    }
}
```

### Using Lambda RowMapper (Java 8+)

```java
// Instead of a separate class, inline it as a lambda
public List<Employee> findAll() {
    return jdbcTemplate.query(
        "SELECT * FROM employees",
        (rs, rowNum) -> {
            Employee emp = new Employee();
            emp.setId(rs.getInt("id"));
            emp.setName(rs.getString("name"));
            emp.setEmail(rs.getString("email"));
            emp.setSalary(rs.getDouble("salary"));
            return emp;
        }
    );
}
```

### EmployeeService — calling the repo

```java
package com.ems.service;

import com.ems.model.Employee;
import com.ems.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void hire(Employee emp) {
        int rows = employeeRepository.save(emp);
        System.out.println("Hired " + emp.getName() + " (rows affected: " + rows + ")");
    }

    public void listAll() {
        List<Employee> employees = employeeRepository.findAll();
        System.out.println("=== All Employees ===");
        employees.forEach(System.out::println);
    }

    public void promoteWithRaise(int empId, double newSalary) {
        Employee emp = employeeRepository.findById(empId);
        emp.setSalary(newSalary);
        employeeRepository.update(emp);
        System.out.println("Updated salary for " + emp.getName());
    }
}
```

### Test it — Main.java

```java
public class Main {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        EmployeeService service = context.getBean(EmployeeService.class);

        // Hire a new employee
        Employee newEmp = new Employee();
        newEmp.setName("Kavya Reddy");
        newEmp.setEmail("kavya@ems.com");
        newEmp.setSalary(70000);
        newEmp.setDepartmentId(2);
        service.hire(newEmp);

        // List all
        service.listAll();

        context.close();
    }
}
```

> 💡 **JdbcTemplate cheat sheet:**
> - `update(sql, args...)` → INSERT, UPDATE, DELETE — returns rows affected
> - `queryForObject(sql, type, args...)` → single value or single row
> - `query(sql, rowMapper, args...)` → list of rows

---

## Spring ORM

### Why ORM over plain JDBC?

JDBC makes you write SQL for everything. ORM (Object-Relational Mapping) maps your Java objects to database tables — you work with objects, the framework handles SQL.

Spring ORM integrates with **Hibernate** (the most popular JPA provider).

| JDBC | Hibernate / ORM |
|---|---|
| Write SQL manually | Framework generates SQL |
| Map ResultSet to objects manually | Automatic object mapping |
| No caching | First-level cache built-in |
| Schema changes = SQL changes | Change the class, not SQL |
| More control | More automation |

### Setup

Add to `pom.xml`:

```xml
<!-- Spring ORM -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>6.1.4</version>
</dependency>

<!-- Hibernate -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.4.4.Final</version>
</dependency>
```

### Map your entities

**`Employee.java`** — add JPA annotations
```java
package com.ems.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "salary")
    private double salary;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // No-arg constructor required by JPA
    public Employee() {}

    public Employee(String name, String email, double salary) {
        this.name = name;
        this.email = email;
        this.salary = salary;
    }

    // getters + setters + toString...
}
```

**`Department.java`** — add JPA annotations
```java
package com.ems.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location")
    private String location;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees;

    // constructors, getters, setters...
}
```

### Configure Hibernate + SessionFactory

**`AppConfig.java`** — add ORM beans:
```java
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import java.util.Properties;

@Configuration
@ComponentScan("com.ems")
@EnableTransactionManagement   // enables @Transactional
public class AppConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.ems.model");  // scan for @Entity classes
        factory.setHibernateProperties(hibernateProperties());
        return factory;
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "update"); // create/update tables
        props.setProperty("hibernate.show_sql", "true");        // log SQL
        props.setProperty("hibernate.format_sql", "true");
        return props;
    }

    @Bean
    public HibernateTransactionManager transactionManager(
            LocalSessionFactoryBean sessionFactory) {
        return new HibernateTransactionManager(sessionFactory.getObject());
    }
}
```

> ⚠️ `hbm2ddl.auto` options:
> - `create` — drops and recreates tables every time (dev only)
> - `update` — updates schema without dropping data ✅
> - `validate` — checks schema, doesn't change it (prod)
> - `none` — does nothing (prod safe)

### EmployeeRepository with Hibernate

```java
package com.ems.repository;

import com.ems.model.Employee;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class EmployeeRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public EmployeeRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void save(Employee emp) {
        sessionFactory.getCurrentSession().persist(emp);
    }

    @Transactional(readOnly = true)
    public Employee findById(int id) {
        return sessionFactory.getCurrentSession().get(Employee.class, id);
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM Employee", Employee.class)
            .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Employee> findByDepartmentName(String deptName) {
        return sessionFactory.getCurrentSession()
            .createQuery(
                "FROM Employee e WHERE e.department.name = :deptName",
                Employee.class)
            .setParameter("deptName", deptName)
            .getResultList();
    }

    @Transactional
    public void update(Employee emp) {
        sessionFactory.getCurrentSession().merge(emp);
    }

    @Transactional
    public void delete(int id) {
        Employee emp = findById(id);
        if (emp != null) {
            sessionFactory.getCurrentSession().remove(emp);
        }
    }
}
```

### EmployeeService with `@Transactional`

```java
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // @Transactional on service method — wraps multiple repo calls in one transaction
    @Transactional
    public void transferEmployee(int empId, Department newDept) {
        Employee emp = employeeRepository.findById(empId);
        emp.setDepartment(newDept);
        employeeRepository.update(emp);
        System.out.println("Transferred " + emp.getName() + " to " + newDept.getName());
    }

    @Transactional(readOnly = true)
    public void printITTeam() {
        List<Employee> team = employeeRepository.findByDepartmentName("Information Technology");
        System.out.println("IT Team:");
        team.forEach(e -> System.out.println(" - " + e.getName() + " (" + e.getEmail() + ")"));
    }
}
```

### HQL vs SQL

Hibernate Query Language (HQL) works on **class names and field names**, not table/column names:

```java
// HQL — uses Java class and field names
"FROM Employee"                            // all employees
"FROM Employee e WHERE e.salary > 60000"  // filtered
"FROM Employee e WHERE e.department.name = :dept"  // join via object graph
"SELECT e.name, e.email FROM Employee e"  // projection

// Not SQL:
"SELECT * FROM employees"  // ❌ won't work in HQL
```

---

## Spring AOP

### What is AOP?

AOP — **Aspect-Oriented Programming** — handles **cross-cutting concerns**: things that touch multiple layers (logging, security, transactions, auditing) without polluting business logic.

Without AOP:
```java
public Employee findById(int id) {
    logger.info("findById called with: " + id);      // cross-cutting
    securityCheck();                                   // cross-cutting
    long start = System.currentTimeMillis();          // cross-cutting
    
    Employee emp = repository.findById(id);           // actual logic
    
    long end = System.currentTimeMillis();            // cross-cutting
    logger.info("findById took: " + (end-start)+"ms");
    return emp;
}
```

With AOP, the actual method stays clean:
```java
public Employee findById(int id) {
    return repository.findById(id); // just the logic
}
// logging, timing, security — handled separately by aspects
```

### Core AOP Terminology

| Term | Meaning | EMS analogy |
|---|---|---|
| **Aspect** | The cross-cutting concern (class containing advice) | The security guard module |
| **Advice** | What to do (the actual code) | "Check badge before entry" |
| **Pointcut** | Which methods to intercept | "All service method calls" |
| **JoinPoint** | A specific execution point | One particular method invocation |
| **Weaving** | Applying aspect to target object | Guard assigned to a specific door |

### Advice types

| Type | When it runs |
|---|---|
| `@Before` | Before the method |
| `@After` | After the method (always, like `finally`) |
| `@AfterReturning` | After successful return |
| `@AfterThrowing` | After exception thrown |
| `@Around` | Wraps the method — before + after |

### Setup

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
    <version>6.1.4</version>
</dependency>

<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.21</version>
</dependency>
```

Enable AOP in config:
```java
@Configuration
@EnableAspectJAutoProxy  // enables AOP
@ComponentScan("com.ems")
public class AppConfig { }
```

### EMS Logging Aspect

```java
package com.ems.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // Pointcut: any method in com.ems.service package
    @Pointcut("execution(* com.ems.service.*.*(..))")
    public void serviceMethods() {}

    // Pointcut: any method in com.ems.repository package
    @Pointcut("execution(* com.ems.repository.*.*(..))")
    public void repositoryMethods() {}

    // @Before — runs before the method
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("[LOG] → " + joinPoint.getSignature().getName()
            + " called with " + joinPoint.getArgs().length + " args");
    }

    // @AfterReturning — runs after successful return, can access return value
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("[LOG] ← " + joinPoint.getSignature().getName()
            + " returned: " + result);
    }

    // @AfterThrowing — runs when method throws exception
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        System.out.println("[ERROR] " + joinPoint.getSignature().getName()
            + " threw: " + ex.getMessage());
    }

    // @Around — most powerful: wrap the method entirely
    @Around("repositoryMethods()")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        Object result = joinPoint.proceed(); // actually run the method
        
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[PERF] " + joinPoint.getSignature().getName()
            + " took " + elapsed + "ms");
        
        return result;
    }
}
```

### EMS Audit Aspect

Track who changed what, useful for compliance:

```java
@Aspect
@Component
public class AuditAspect {

    // Matches only save/update/delete methods in any repository
    @Pointcut("execution(* com.ems.repository.*.save(..)) || " +
              "execution(* com.ems.repository.*.update(..)) || " +
              "execution(* com.ems.repository.*.delete(..))")
    public void writeOperations() {}

    @Before("writeOperations()")
    public void auditWrite(JoinPoint joinPoint) {
        System.out.println("[AUDIT] " +
            java.time.LocalDateTime.now() + " | " +
            joinPoint.getSignature().toShortString() + " | " +
            "args: " + java.util.Arrays.toString(joinPoint.getArgs()));
    }
}
```

### Pointcut expression quick reference

```java
// All methods in a class
"execution(* com.ems.service.EmployeeService.*(..))"

// Specific method name in any class in a package
"execution(* com.ems.service.*.find*(..))"

// Method with specific arg type
"execution(* com.ems.service.*.*(int, ..))"

// Methods annotated with @Transactional
"@annotation(org.springframework.transaction.annotation.Transactional)"

// Any bean named employeeService
"bean(employeeService)"
```

---

*Next → [03_Spring_MVC.md](03_Spring_MVC.md) — Web layer: Controllers, Views, Forms, AJAX*
