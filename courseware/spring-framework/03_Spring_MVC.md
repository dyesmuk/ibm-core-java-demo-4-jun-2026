# 03 — Spring Framework: Spring MVC — Web Layer

> 🏢 **EMS Context:** Time to put a web interface on the EMS. Users will be able to view employee lists, add new employees, update records, and interact via AJAX — all through Spring MVC.

---

## Table of Contents

- [What is Spring MVC?](#what-is-spring-mvc)
- [Project Setup](#project-setup)
- [Sending Data from Controller to UI](#sending-data-from-controller-to-ui)
- [Sending Data from UI to Controller](#sending-data-from-ui-to-controller)
- [Using ModelMap and String View](#using-modelmap-and-string-view)
- [Spring MVC and ORM](#spring-mvc-and-orm)
- [Spring MVC and AJAX with jQuery](#spring-mvc-and-ajax-with-jquery)

---

## What is Spring MVC?

Spring MVC implements the **Model-View-Controller** pattern for web apps.

```
Browser → HTTP Request → DispatcherServlet
                               ↓
                         HandlerMapping (finds the right controller)
                               ↓
                         Controller method runs
                               ↓
                         Returns Model + View name
                               ↓
                         ViewResolver (maps name → JSP file)
                               ↓
                         JSP renders with model data
                               ↓
                         HTTP Response → Browser
```

| Component | Role |
|---|---|
| **DispatcherServlet** | Front controller — every request goes through this |
| **Controller** | Your Java class — handles requests, builds model |
| **Model** | Data to display (Java objects) |
| **View** | JSP file — renders the HTML |
| **ViewResolver** | Maps logical view names (`"employees/list"`) to actual files (`/WEB-INF/views/employees/list.jsp`) |

---

## Project Setup

### Create a Dynamic Web Project with Maven

1. **File → New → Maven Project**
2. Choose archetype: `maven-archetype-webapp`
3. GroupId: `com.ems`, ArtifactId: `ems-web`
4. Finish

### `pom.xml` — full dependencies

```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <spring.version>6.1.4</spring.version>
</properties>

<dependencies>
    <!-- Spring MVC -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>${spring.version}</version>
    </dependency>

    <!-- Spring ORM + Hibernate -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-orm</artifactId>
        <version>${spring.version}</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>6.4.4.Final</version>
    </dependency>

    <!-- MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.3.0</version>
    </dependency>

    <!-- Connection pool -->
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
        <version>5.1.0</version>
    </dependency>

    <!-- Jakarta EE (Servlet, JSTL) -->
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <version>6.0.0</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>jakarta.servlet.jsp.jstl</groupId>
        <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
        <version>3.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.glassfish.web</groupId>
        <artifactId>jakarta.servlet.jsp.jstl</artifactId>
        <version>3.0.1</version>
    </dependency>

    <!-- JSON (for AJAX responses) -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.17.0</version>
    </dependency>

    <!-- Annotations (@PostConstruct etc.) -->
    <dependency>
        <groupId>jakarta.annotation</groupId>
        <artifactId>jakarta.annotation-api</artifactId>
        <version>2.1.1</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.tomcat.maven</groupId>
            <artifactId>tomcat7-maven-plugin</artifactId>
            <version>2.2</version>
            <configuration>
                <port>8080</port>
                <path>/ems</path>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Project structure

```
ems-web/
├── src/
│   ├── main/
│   │   ├── java/com/ems/
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java
│   │   │   │   ├── WebConfig.java
│   │   │   │   └── WebAppInitializer.java
│   │   │   ├── controller/
│   │   │   │   ├── EmployeeController.java
│   │   │   │   └── DepartmentController.java
│   │   │   ├── model/
│   │   │   │   ├── Employee.java
│   │   │   │   └── Department.java
│   │   │   ├── service/
│   │   │   │   └── EmployeeService.java
│   │   │   └── repository/
│   │   │       └── EmployeeRepository.java
│   │   ├── resources/
│   │   │   └── application.properties
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── views/
│   │       │       ├── employees/
│   │       │       │   ├── list.jsp
│   │       │       │   ├── add.jsp
│   │       │       │   └── detail.jsp
│   │       │       └── home.jsp
│   │       └── resources/
│   │           ├── css/
│   │           └── js/
└── pom.xml
```

### Java-based web config (no web.xml)

**`WebAppInitializer.java`**
```java
package com.ems.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer
        extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        // Non-web beans: services, repos, data source
        return new Class<?>[]{ AppConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        // Web beans: controllers, view resolver
        return new Class<?>[]{ WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{ "/" }; // all requests through DispatcherServlet
    }
}
```

**`WebConfig.java`**
```java
package com.ems.config;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan("com.ems.controller")
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    // Serve static files (CSS, JS, images) from /resources/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }

    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
```

**`AppConfig.java`**
```java
package com.ems.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.ems",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ANNOTATION,
        classes = org.springframework.stereotype.Controller.class))
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class AppConfig {

    @Value("${ems.db.url}")      private String dbUrl;
    @Value("${ems.db.username}") private String dbUser;
    @Value("${ems.db.password}") private String dbPass;

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(dbUrl);
        ds.setUsername(dbUser);
        ds.setPassword(dbPass);
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.ems.model");
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "update");
        props.setProperty("hibernate.show_sql", "true");
        factory.setHibernateProperties(props);
        return factory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(
            LocalSessionFactoryBean sessionFactory) {
        return new HibernateTransactionManager(sessionFactory.getObject());
    }
}
```

---

## Sending Data from Controller to UI

The controller puts data into a **Model**, then the View (JSP) reads from it.

### Basic Controller

**`EmployeeController.java`**
```java
package com.ems.controller;

import com.ems.model.Employee;
import com.ems.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // GET /employees — list all employees
    @GetMapping
    public String listEmployees(Model model) {
        List<Employee> employees = employeeService.findAll();
        
        model.addAttribute("employees", employees);          // list of employees
        model.addAttribute("totalCount", employees.size());  // extra data
        model.addAttribute("pageTitle", "EMS — Employees");
        
        return "employees/list"; // → /WEB-INF/views/employees/list.jsp
    }

    // GET /employees/{id} — employee detail
    @GetMapping("/{id}")
    public String employeeDetail(@PathVariable int id, Model model) {
        Employee emp = employeeService.findById(id);
        
        if (emp == null) {
            model.addAttribute("error", "Employee #" + id + " not found");
            return "error"; // → /WEB-INF/views/error.jsp
        }
        
        model.addAttribute("employee", emp);
        return "employees/detail";
    }
}
```

### The JSP View

**`/WEB-INF/views/employees/list.jsp`**
```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<h1>Employee Directory</h1>
<p>Total employees: <strong>${totalCount}</strong></p>

<table border="1" cellpadding="8">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Department</th>
            <th>Salary</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="emp" items="${employees}">
            <tr>
                <td>${emp.id}</td>
                <td>${emp.name}</td>
                <td>${emp.email}</td>
                <td>${emp.department.name}</td>
                <td>₹${emp.salary}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/employees/${emp.id}">View</a> |
                    <a href="${pageContext.request.contextPath}/employees/${emp.id}/edit">Edit</a>
                </td>
            </tr>
        </c:forEach>

        <c:if test="${empty employees}">
            <tr><td colspan="6">No employees found.</td></tr>
        </c:if>
    </tbody>
</table>

<a href="${pageContext.request.contextPath}/employees/add">+ Add Employee</a>

</body>
</html>
```

**`/WEB-INF/views/employees/detail.jsp`**
```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>${employee.name} — EMS</title></head>
<body>

<h1>${employee.name}</h1>

<table>
    <tr><td><strong>ID:</strong></td>         <td>${employee.id}</td></tr>
    <tr><td><strong>Email:</strong></td>      <td>${employee.email}</td></tr>
    <tr><td><strong>Department:</strong></td> <td>${employee.department.name}</td></tr>
    <tr><td><strong>Salary:</strong></td>     <td>₹${employee.salary}</td></tr>
</table>

<a href="${pageContext.request.contextPath}/employees">← Back to list</a>

</body>
</html>
```

> 💡 `${emp.name}` in JSP calls `emp.getName()` behind the scenes — EL (Expression Language) uses getters automatically.

---

## Sending Data from UI to Controller

### Form-based submission

**Add the form handler to EmployeeController:**
```java
// GET /employees/add — show the empty form
@GetMapping("/add")
public String showAddForm(Model model) {
    model.addAttribute("employee", new Employee()); // empty object for form binding
    model.addAttribute("departments", departmentService.findAll()); // for dropdown
    return "employees/add";
}

// POST /employees/add — handle form submission
@PostMapping("/add")
public String addEmployee(@ModelAttribute Employee employee, Model model) {
    employeeService.save(employee);
    return "redirect:/employees"; // PRG pattern — redirect after POST
}
```

**`/WEB-INF/views/employees/add.jsp`**
```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head><title>Add Employee — EMS</title></head>
<body>

<h1>Add New Employee</h1>

<%-- Spring form tag — binds to the 'employee' model attribute --%>
<form:form method="POST"
           action="${pageContext.request.contextPath}/employees/add"
           modelAttribute="employee">

    <table>
        <tr>
            <td><label>Full Name:</label></td>
            <td><form:input path="name" placeholder="e.g. Priya Sharma"/></td>
            <td><form:errors path="name" cssClass="error"/></td>
        </tr>
        <tr>
            <td><label>Email:</label></td>
            <td><form:input path="email" type="email" placeholder="priya@ems.com"/></td>
            <td><form:errors path="email" cssClass="error"/></td>
        </tr>
        <tr>
            <td><label>Salary:</label></td>
            <td><form:input path="salary" type="number" placeholder="70000"/></td>
        </tr>
        <tr>
            <td><label>Department:</label></td>
            <td>
                <form:select path="department.id">
                    <form:option value="" label="-- Select Department --"/>
                    <form:options items="${departments}" itemValue="id" itemLabel="name"/>
                </form:select>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="Add Employee"/>
                <a href="${pageContext.request.contextPath}/employees">Cancel</a>
            </td>
        </tr>
    </table>

</form:form>

</body>
</html>
```

### Edit / Update

```java
// GET /employees/{id}/edit — show pre-filled form
@GetMapping("/{id}/edit")
public String showEditForm(@PathVariable int id, Model model) {
    Employee emp = employeeService.findById(id);
    model.addAttribute("employee", emp);
    model.addAttribute("departments", departmentService.findAll());
    return "employees/edit";
}

// POST /employees/{id}/edit — handle update
@PostMapping("/{id}/edit")
public String updateEmployee(@PathVariable int id,
                              @ModelAttribute Employee employee) {
    employee.setId(id); // ensure ID is set
    employeeService.update(employee);
    return "redirect:/employees/" + id;
}

// POST /employees/{id}/delete — handle delete
@PostMapping("/{id}/delete")
public String deleteEmployee(@PathVariable int id) {
    employeeService.delete(id);
    return "redirect:/employees";
}
```

### Reading query parameters and path variables

```java
// GET /employees/search?name=Priya&dept=IT
@GetMapping("/search")
public String search(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String dept,
        Model model) {

    List<Employee> results = employeeService.search(name, dept);
    model.addAttribute("employees", results);
    model.addAttribute("searchName", name);
    model.addAttribute("searchDept", dept);
    return "employees/list";
}
```

---

## Using ModelMap and String View

`ModelMap` is an alternative to `Model` — same purpose, slightly more flexible. `String` return type = the logical view name.

```java
import org.springframework.ui.ModelMap;

@GetMapping("/dashboard")
public String dashboard(ModelMap modelMap) {

    // ModelMap vs Model — both work the same way
    modelMap.addAttribute("totalEmployees", employeeService.count());
    modelMap.addAttribute("totalDepartments", departmentService.count());
    modelMap.addAttribute("recentHires", employeeService.findRecentHires(5));

    // Can also chain
    modelMap
        .addAttribute("activeProjects", projectService.countActive())
        .addAttribute("pageTitle", "EMS Dashboard");

    return "dashboard"; // → /WEB-INF/views/dashboard.jsp
}
```

### `ModelAndView` — when you want to set model and view in one object

```java
import org.springframework.web.servlet.ModelAndView;

@GetMapping("/report")
public ModelAndView employeeReport() {
    ModelAndView mav = new ModelAndView("employees/report"); // view name set here

    mav.addObject("employees", employeeService.findAll());
    mav.addObject("generatedAt", java.time.LocalDate.now());

    return mav; // Spring extracts both model and view from this
}
```

### Redirect vs Forward

```java
// Redirect — browser makes a NEW request to the URL (address bar changes)
return "redirect:/employees";
return "redirect:/employees/" + id;
return "redirect:https://external-site.com";

// Forward — server-side, URL stays the same
return "forward:/employees/list";
```

> 💡 **POST-Redirect-GET (PRG) pattern:** After a form POST, always `redirect:` to a GET. This prevents form resubmission on browser refresh.

---

## Spring MVC and ORM

Wiring up the full stack: Controller → Service → Repository (Hibernate) → Database.

### The complete flow for listing employees

```
GET /employees
  ↓
EmployeeController.listEmployees(Model)
  ↓
employeeService.findAll()
  ↓
employeeRepository.findAll()  [Hibernate query: FROM Employee]
  ↓
Hibernate hits DB, maps rows → Employee objects
  ↓
List<Employee> back to controller
  ↓
Added to Model → JSP renders it
```

### Full CRUD Controller wired to Hibernate

```java
@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    @Autowired
    public EmployeeController(EmployeeService employeeService,
                               DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        return "employees/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable int id, Model model) {
        model.addAttribute("employee", employeeService.findById(id));
        return "employees/detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", departmentService.findAll());
        return "employees/add";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute Employee employee) {
        employeeService.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("employee", employeeService.findById(id));
        model.addAttribute("departments", departmentService.findAll());
        return "employees/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable int id, @ModelAttribute Employee employee) {
        employee.setId(id);
        employeeService.update(employee);
        return "redirect:/employees/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id) {
        employeeService.delete(id);
        return "redirect:/employees";
    }
}
```

### `EmployeeService` with transactions

```java
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee findById(int id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    public void save(Employee emp) {
        employeeRepository.save(emp);
    }

    @Transactional
    public void update(Employee emp) {
        employeeRepository.update(emp);
    }

    @Transactional
    public void delete(int id) {
        employeeRepository.delete(id);
    }

    // Business logic example: transfer employee between departments
    @Transactional
    public void transferToDepartment(int empId, int newDeptId) {
        Employee emp = employeeRepository.findById(empId);
        Department newDept = departmentRepository.findById(newDeptId);
        emp.setDepartment(newDept);
        employeeRepository.update(emp);
    }
}
```

---

## Spring MVC and AJAX with jQuery

AJAX lets you fetch/send data without a full page reload — smoother UX.

### Setup — add jQuery to your project

Download jQuery or reference CDN in your JSP:
```html
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
```

### REST-style controller methods for AJAX

Add these to your `EmployeeController` (or a separate `EmployeeApiController`):

```java
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

// Returns JSON list — for AJAX "load all employees" call
@GetMapping(value = "/api/list", produces = "application/json")
@ResponseBody
public List<Employee> getEmployeesJson() {
    return employeeService.findAll();
}

// Returns single employee as JSON
@GetMapping(value = "/api/{id}", produces = "application/json")
@ResponseBody
public ResponseEntity<Employee> getEmployeeJson(@PathVariable int id) {
    Employee emp = employeeService.findById(id);
    if (emp == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(emp);
}

// Save via AJAX (JSON request body)
@PostMapping(value = "/api/save",
             consumes = "application/json",
             produces = "application/json")
@ResponseBody
public ResponseEntity<String> saveEmployeeJson(@RequestBody Employee employee) {
    employeeService.save(employee);
    return ResponseEntity.ok("{\"status\":\"saved\",\"name\":\"" + employee.getName() + "\"}");
}

// Delete via AJAX
@DeleteMapping(value = "/api/{id}", produces = "application/json")
@ResponseBody
public ResponseEntity<String> deleteEmployeeJson(@PathVariable int id) {
    employeeService.delete(id);
    return ResponseEntity.ok("{\"status\":\"deleted\",\"id\":" + id + "}");
}
```

### AJAX in JSP — jQuery examples

**`/WEB-INF/views/employees/list.jsp`** — with AJAX actions:
```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>EMS — Employees</title>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>

<h1>Employees</h1>
<div id="statusMessage"></div>

<button id="loadEmployees">Refresh List (AJAX)</button>

<table id="employeeTable" border="1" cellpadding="8">
    <thead>
        <tr>
            <th>ID</th><th>Name</th><th>Email</th><th>Department</th><th>Actions</th>
        </tr>
    </thead>
    <tbody id="employeeBody">
        <%-- Populated by AJAX --%>
    </tbody>
</table>

<hr/>
<h2>Quick Add Employee (AJAX)</h2>
<form id="addEmployeeForm">
    <input type="text"   id="empName"   placeholder="Name"   required/>
    <input type="email"  id="empEmail"  placeholder="Email"  required/>
    <input type="number" id="empSalary" placeholder="Salary"/>
    <button type="submit">Add</button>
</form>

<script>
const BASE = '${pageContext.request.contextPath}/employees';

// Load and render employees via AJAX
function loadEmployees() {
    $.ajax({
        url: BASE + '/api/list',
        method: 'GET',
        dataType: 'json',
        success: function(employees) {
            let rows = '';
            employees.forEach(function(emp) {
                rows += `
                    <tr id="row-${emp.id}">
                        <td>${emp.id}</td>
                        <td>${emp.name}</td>
                        <td>${emp.email}</td>
                        <td>${emp.department ? emp.department.name : '-'}</td>
                        <td>
                            <button onclick="deleteEmployee(${emp.id})">Delete</button>
                        </td>
                    </tr>`;
            });
            $('#employeeBody').html(rows);
        },
        error: function(xhr) {
            showStatus('Failed to load employees: ' + xhr.status, 'error');
        }
    });
}

// Delete an employee via AJAX (no page reload)
function deleteEmployee(id) {
    if (!confirm('Delete employee #' + id + '?')) return;

    $.ajax({
        url: BASE + '/api/' + id,
        method: 'DELETE',
        success: function(response) {
            $('#row-' + id).fadeOut(300, function() { $(this).remove(); });
            showStatus('Employee deleted successfully.', 'success');
        },
        error: function() {
            showStatus('Delete failed.', 'error');
        }
    });
}

// Add employee via AJAX POST (JSON)
$('#addEmployeeForm').on('submit', function(e) {
    e.preventDefault();

    const employee = {
        name:   $('#empName').val(),
        email:  $('#empEmail').val(),
        salary: parseFloat($('#empSalary').val()) || 0
    };

    $.ajax({
        url: BASE + '/api/save',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(employee),
        success: function(response) {
            showStatus('Added: ' + response.name, 'success');
            loadEmployees(); // refresh list
            $('#addEmployeeForm')[0].reset();
        },
        error: function(xhr) {
            showStatus('Add failed: ' + xhr.responseText, 'error');
        }
    });
});

// Refresh button
$('#loadEmployees').on('click', loadEmployees);

// Helper
function showStatus(msg, type) {
    $('#statusMessage')
        .text(msg)
        .css('color', type === 'error' ? 'red' : 'green');
    setTimeout(() => $('#statusMessage').text(''), 3000);
}

// Load on page ready
$(document).ready(loadEmployees);
</script>

</body>
</html>
```

### AJAX for search with live results

```javascript
// Search as user types
$('#searchInput').on('keyup', function() {
    const query = $(this).val().trim();
    if (query.length < 2) return; // wait for at least 2 chars

    $.ajax({
        url: BASE + '/api/list',
        method: 'GET',
        data: { name: query },
        dataType: 'json',
        success: function(employees) {
            renderEmployees(employees);
        }
    });
});
```

Add the search param to the controller:
```java
@GetMapping(value = "/api/list", produces = "application/json")
@ResponseBody
public List<Employee> getEmployeesJson(
        @RequestParam(required = false) String name) {

    if (name != null && !name.isEmpty()) {
        return employeeService.searchByName(name);
    }
    return employeeService.findAll();
}
```

---

## Quick Reference

### Common Annotations

| Annotation | What it does |
|---|---|
| `@Controller` | Marks class as Spring MVC controller |
| `@RequestMapping("/path")` | Base URL mapping on class |
| `@GetMapping("/path")` | HTTP GET handler |
| `@PostMapping("/path")` | HTTP POST handler |
| `@PutMapping`, `@DeleteMapping` | PUT / DELETE handlers |
| `@PathVariable` | Extract `{id}` from URL |
| `@RequestParam` | Extract `?name=value` from URL |
| `@ModelAttribute` | Bind form data to an object |
| `@ResponseBody` | Return value as JSON/XML (not a view name) |
| `@RequestBody` | Parse JSON request body into object |

### Return types from controller methods

| Return | Meaning |
|---|---|
| `"viewName"` | Logical view name → JSP via ViewResolver |
| `"redirect:/path"` | Send browser to new URL |
| `"forward:/path"` | Server-side forward |
| `ModelAndView` | View name + model data in one object |
| `@ResponseBody String` | Raw string response |
| `@ResponseBody List<T>` | JSON array (Jackson serializes it) |
| `ResponseEntity<T>` | Full control: status code + headers + body |

---

*Next → [04_Spring_Boot.md](04_Spring_Boot.md) — Auto-configuration, embedded Tomcat, Spring Data JPA*
