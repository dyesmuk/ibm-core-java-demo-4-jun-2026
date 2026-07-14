# Spring Framework & Spring Boot — Discussion Questions & Answers

---

## Spring Framework — Fundas

**1. What is the Spring Framework?**
Spring is a comprehensive Java application framework that provides infrastructure support — dependency injection, aspect-oriented programming, transaction management, MVC web layer, and integration with data access technologies. It reduces boilerplate and makes enterprise Java development faster and more testable.

**2. What is the Spring IoC Container?**
The IoC (Inversion of Control) Container is the core of Spring. Instead of your code creating dependencies with `new`, the container creates and wires objects (beans) together based on configuration. Control of object creation is inverted — the framework manages it, not your code.

**3. What is Dependency Injection (DI)?**
DI is the pattern the IoC container uses — it injects the dependencies a class needs rather than the class creating them itself. Spring supports three types: constructor injection (preferred), setter injection, and field injection. DI makes classes easier to test because dependencies can be replaced with mocks.

**4. What is a Spring Bean?**
A Spring Bean is any object managed by the Spring IoC container — created, configured, and wired by the container. Beans are defined via `@Component`, `@Service`, `@Repository`, `@Controller` annotations or in `@Configuration` classes with `@Bean` methods. The container manages their full lifecycle.

**5. What is the difference between `@Component`, `@Service`, `@Repository`, and `@Controller`?**
All four register a class as a Spring bean — they are specialisations of `@Component`. `@Service` signals business logic. `@Repository` signals data access layer and enables exception translation (JPA exceptions → Spring's `DataAccessException`). `@Controller` signals an MVC web layer component. Use the most specific annotation for clarity.

**6. What is `@Autowired`?**
`@Autowired` tells Spring to inject a dependency automatically by type. Spring finds the matching bean in the container and injects it. Can be applied to constructor (preferred), setter, or field. If multiple beans of the same type exist, use `@Qualifier("beanName")` to specify which one.

**7. What is the difference between constructor injection and field injection?**
Constructor injection is preferred — dependencies are explicitly declared, the class cannot be instantiated without them (no null state), and it makes testing easy (pass mock directly). Field injection (`@Autowired` on a field) hides dependencies and requires Spring to inject them via reflection — harder to test and violates the single responsibility principle.

**8. What is the Spring Bean lifecycle?**
Instantiation → Populate properties → `BeanNameAware`, `BeanFactoryAware` callbacks → `@PostConstruct` / `InitializingBean.afterPropertiesSet()` → Bean is ready → `@PreDestroy` / `DisposableBean.destroy()` → Garbage collected. `@PostConstruct` and `@PreDestroy` are the recommended lifecycle hooks.

**9. What are Bean scopes in Spring?**
Scope defines how many instances of a bean are created. `singleton` (default) — one instance per container, shared everywhere. `prototype` — new instance every time the bean is requested. `request` — one per HTTP request (web apps). `session` — one per HTTP session. Use `@Scope("prototype")` to change the default.

**10. What is the difference between `singleton` and `prototype` scope?**
Singleton beans are created once when the container starts and shared — suitable for stateless services. Prototype beans are created fresh each time they are requested — suitable for stateful objects. Injecting a prototype bean into a singleton bean is tricky — the singleton holds a reference to one prototype instance, not a new one each time.

---

## Spring Core Concepts

**11. What is `ApplicationContext`?**
`ApplicationContext` is the central interface to the Spring IoC container. It provides bean definitions, dependency injection, event publication, and resource loading. Common implementations: `AnnotationConfigApplicationContext` (Java config), `ClassPathXmlApplicationContext` (XML config — legacy). In Spring Boot, it is auto-configured via `SpringApplication.run()`.

**12. What is the difference between `BeanFactory` and `ApplicationContext`?**
`BeanFactory` is the basic container — lazy initialises beans on first request. `ApplicationContext` extends `BeanFactory` with additional enterprise features: eager singleton initialisation, event publishing, `MessageSource` for i18n, AOP integration. Always use `ApplicationContext` — `BeanFactory` is rarely used directly.

**13. What is `@Configuration` and `@Bean`?**
`@Configuration` marks a class as a source of bean definitions. `@Bean` on a method inside a `@Configuration` class tells Spring to manage the return value as a bean. This is the Java-based configuration alternative to XML. Spring calls these methods to create beans and wires them based on method parameters.

**14. What is `@ComponentScan`?**
`@ComponentScan` tells Spring where to look for annotated components (`@Component`, `@Service`, etc.) to register as beans. By default in Spring Boot, it scans the package of the main class and all sub-packages. Specify a base package: `@ComponentScan("com.ibm.ems")`.

**15. What is `@Primary` and `@Qualifier`?**
When multiple beans of the same type exist, Spring doesn't know which to inject. `@Primary` marks one bean as the default choice. `@Qualifier("specificBean")` at the injection point specifies exactly which bean to inject. Use `@Primary` for the most commonly used implementation; `@Qualifier` when you need a specific one.

**16. What is AOP (Aspect-Oriented Programming)?**
AOP separates cross-cutting concerns — logging, security, transactions, caching — from business logic. You define an `Aspect` with advice that runs at specific join points (method calls). This keeps business methods clean and the cross-cutting behaviour in one place. Spring AOP is proxy-based.

**17. What are the key AOP terms?**
Aspect: the cross-cutting concern as a class (`@Aspect`). Advice: the action taken — `@Before`, `@After`, `@AfterReturning`, `@AfterThrowing`, `@Around`. Join Point: a point in execution (method call). Pointcut: expression defining which join points to intercept. Weaving: applying aspects to target objects.

**18. What is `@Around` advice in AOP?**
`@Around` surrounds the method call — it runs before and after the target method and can control whether the method executes at all. It receives a `ProceedingJoinPoint` and must call `proceed()` to invoke the actual method. Used for logging, performance timing, transaction management, and caching.

**19. What is Spring's `@Transactional`?**
`@Transactional` manages database transactions declaratively. Applied to a method or class, Spring creates a transaction before the method runs and commits it on success or rolls it back on exception. Removes the need for manual `begin/commit/rollback` in code. Works via AOP proxy — self-invocations (calling another method in the same class) don't trigger it.

**20. What is transaction propagation in Spring?**
Propagation defines how a transaction behaves when another transactional method is called. `REQUIRED` (default) — join existing transaction or create new one. `REQUIRES_NEW` — always create a new transaction, suspend existing. `NESTED` — nested transaction within the outer. `NOT_SUPPORTED` — suspend transaction. `MANDATORY` — must have existing transaction or throw.

---

## Spring MVC

**21. What is Spring MVC?**
Spring MVC is a web framework built on the Servlet API following the Model-View-Controller pattern. The `DispatcherServlet` is the front controller — it receives all requests and delegates to handler methods (`@Controller`), resolves views, and returns responses.

**22. What is `DispatcherServlet`?**
The `DispatcherServlet` is the heart of Spring MVC — a single servlet that receives all HTTP requests and routes them to the appropriate controller based on URL mappings. It also handles view resolution, exception mapping, and message conversion. In Spring Boot, it is auto-configured.

**23. What is the difference between `@Controller` and `@RestController`?**
`@Controller` is used for traditional MVC — methods return view names (templates). `@RestController` is `@Controller + @ResponseBody` — methods return data (JSON/XML) serialised directly to the HTTP response body. Use `@RestController` for REST APIs; `@Controller` for server-rendered HTML.

**24. What is `@RequestMapping` and its shortcuts?**
`@RequestMapping(path, method)` maps HTTP requests to handler methods. Shorthand annotations: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`, `@DeleteMapping`. Use the shorthand versions — they are more readable and explicit about the HTTP method.

**25. What is the difference between `@PathVariable` and `@RequestParam`?**
`@PathVariable` extracts a value from the URL path: `/employees/{id}` → `@PathVariable Long id`. `@RequestParam` extracts a query parameter: `/employees?dept=Engineering` → `@RequestParam String dept`. Use path variables for resource identity; query params for optional filters.

**26. What is `@RequestBody` and `@ResponseBody`?**
`@RequestBody` maps the HTTP request body (JSON) to a Java object using `HttpMessageConverter` (Jackson). `@ResponseBody` serialises the return value of a method to the HTTP response body. `@RestController` applies `@ResponseBody` to all methods automatically.

**27. What is `ResponseEntity`?**
`ResponseEntity<T>` gives full control over the HTTP response — status code, headers, and body. Example: `return ResponseEntity.status(201).body(savedEmployee)` or `ResponseEntity.notFound().build()`. Use it when you need to customise the status code beyond the default 200.

**28. What is `@ExceptionHandler` and `@ControllerAdvice`?**
`@ExceptionHandler` inside a controller handles exceptions thrown by that controller's methods. `@ControllerAdvice` (or `@RestControllerAdvice`) makes the exception handlers global — applying across all controllers. Combine with `@ExceptionHandler` to handle specific exception types consistently across the API.

---

## Spring Boot — Fundas

**29. What is Spring Boot?**
Spring Boot is an opinionated layer on top of the Spring Framework that auto-configures Spring applications based on classpath dependencies. It eliminates boilerplate configuration — no XML, no manual bean declarations for common setups. Applications are runnable as standalone JARs with embedded servers (Tomcat, Jetty).

**30. What is the difference between Spring Framework and Spring Boot?**
Spring Framework provides the core features (IoC, MVC, Data) but requires explicit configuration. Spring Boot wraps Spring with auto-configuration, starter dependencies, an embedded server, and production-ready features (Actuator). Spring Boot accelerates development — you focus on business logic, not wiring.

**31. What is auto-configuration in Spring Boot?**
Spring Boot's auto-configuration reads the classpath and automatically creates and configures beans. If `spring-boot-starter-data-jpa` is on the classpath and a datasource is configured, Spring Boot auto-configures JPA, EntityManager, and transaction management. Use `@SpringBootApplication` which includes `@EnableAutoConfiguration`.

**32. What is `@SpringBootApplication`?**
`@SpringBootApplication` is a convenience annotation that combines three annotations: `@Configuration` (marks as config class), `@EnableAutoConfiguration` (enables auto-config), and `@ComponentScan` (scans the current package and sub-packages). Place it on the main class.

**33. What are Spring Boot starters?**
Starters are pre-packaged dependency descriptors — `spring-boot-starter-web` pulls in Spring MVC, Jackson, and embedded Tomcat. `spring-boot-starter-data-jpa` pulls in JPA, Hibernate, and JDBC. Starters eliminate dependency management — add one starter and all required transitive dependencies come in with compatible versions.

**34. What is the embedded server in Spring Boot?**
Spring Boot packages an embedded Tomcat (default), Jetty, or Undertow server inside the JAR. No separate server installation is needed — run with `java -jar app.jar`. This makes deployment simpler and identical across environments. Change to Jetty: exclude Tomcat starter, add Jetty starter.

**35. What is `application.properties` / `application.yml`?**
The main configuration file for Spring Boot applications. All Spring Boot auto-configuration can be customised here: `server.port=8080`, `spring.datasource.url=jdbc:...`, `spring.jpa.show-sql=true`. Use `application-dev.properties` and `application-prod.properties` for environment-specific overrides with Spring profiles.

---

## Spring Boot — Data & REST

**36. What is Spring Data JPA?**
Spring Data JPA provides repository interfaces that automatically implement CRUD operations — no SQL or `EntityManager` needed. Extend `JpaRepository<Employee, Long>` and get `findAll()`, `findById()`, `save()`, `delete()` for free. Derive custom queries from method names: `findByDepartmentAndIsActive(dept, true)`.

**37. What is the difference between `JpaRepository`, `CrudRepository`, and `PagingAndSortingRepository`?**
`CrudRepository` provides basic CRUD methods. `PagingAndSortingRepository` adds `findAll(Pageable)` for pagination and sorting. `JpaRepository` extends both and adds JPA-specific methods like `flush()` and batch operations. Use `JpaRepository` — it includes everything.

**38. What is `@Entity` and `@Table`?**
`@Entity` marks a class as a JPA entity — mapped to a database table. `@Table(name = "employees")` specifies the table name (optional — defaults to class name). `@Id` marks the primary key field. `@GeneratedValue(strategy = GenerationType.IDENTITY)` auto-generates the ID.

**39. What is the difference between `@OneToMany`, `@ManyToOne`, and `@ManyToMany`?**
These annotations define JPA relationship mappings. `@ManyToOne` and `@OneToMany` are two sides of the same relationship (e.g. many employees per department). `@ManyToMany` (employee ↔ projects) requires a join table. Set `fetch = FetchType.LAZY` to avoid loading related entities unnecessarily.

**40. What is the N+1 query problem?**
When fetching a list of entities with a lazy-loaded association, JPA executes 1 query for the list and N additional queries for each entity's association — N+1 total. Fix with: `@EntityGraph` to fetch associations in one query, `JOIN FETCH` in JPQL, or `@BatchSize` to fetch in batches. Always check SQL logs with `spring.jpa.show-sql=true`.

**41. What is `@Query` in Spring Data JPA?**
`@Query` defines a custom JPQL or native SQL query on a repository method. `@Query("SELECT e FROM Employee e WHERE e.salary > :minSalary")` with a parameter `@Param("minSalary") double minSalary`. Use it when derived query names become unreadable or for complex queries.

**42. What is pagination in Spring Data JPA?**
Pass a `Pageable` parameter to repository methods: `Page<Employee> findAll(Pageable pageable)`. Create a `Pageable` with: `PageRequest.of(page, size, Sort.by("name"))`. The returned `Page<T>` contains the data and metadata (total elements, total pages, current page). Essential for any production list endpoint.

**43. What is `@Transactional` in the context of Spring Data?**
Spring Data repository methods are transactional by default for write operations. For read-only methods, `@Transactional(readOnly = true)` is more efficient — it tells the persistence context not to track changes. Custom `@Service` methods that call multiple repository methods should be wrapped in `@Transactional` to ensure atomicity.

---

## Spring Boot — REST API

**44. What are HTTP status codes used in REST APIs?**
`200 OK` (success), `201 Created` (resource created), `204 No Content` (success, no body — DELETE), `400 Bad Request` (invalid input), `401 Unauthorised` (not authenticated), `403 Forbidden` (authenticated but no permission), `404 Not Found` (resource doesn't exist), `409 Conflict` (duplicate), `500 Internal Server Error` (unexpected failure).

**45. What is the difference between `PUT` and `PATCH`?**
`PUT` replaces the entire resource with the provided representation — send all fields. `PATCH` applies partial updates — send only the fields that change. In Spring: `@PutMapping` for full replacement, `@PatchMapping` for partial updates. REST best practice prefers `PATCH` for updates since it avoids sending unchanged data.

**46. What is `@Valid` and Bean Validation?**
`@Valid` on a method parameter (`@RequestBody @Valid EmployeeDto dto`) triggers Bean Validation (JSR-380) on the object. Annotate DTO fields: `@NotBlank`, `@Email`, `@Min(10000)`, `@Size(min=2, max=100)`. Spring automatically returns `400 Bad Request` with validation errors when validation fails. Use `@Validated` on the controller class for method-level validation.

**47. What is a DTO (Data Transfer Object)?**
A DTO is a plain object used to transfer data between layers — decoupling the internal domain/entity model from the API contract. The API receives/returns DTOs; the service maps between DTO and entity. Benefits: hide sensitive fields (password), shape the response independently of the database model, and avoid serialising JPA proxies.

**48. What is Spring Boot Actuator?**
Actuator adds production-ready features to Spring Boot — health checks, metrics, environment info, thread dumps, and more — exposed as REST endpoints. `GET /actuator/health` returns `{"status":"UP"}`. Enable endpoints in `application.properties`: `management.endpoints.web.exposure.include=health,info,metrics`. Secure in production.

**49. What is a Spring Boot Profile?**
Profiles allow environment-specific configuration. `@Profile("dev")` beans only load in the dev profile. Activate with `spring.profiles.active=prod` in properties or `--spring.profiles.active=prod` JVM argument. Common profiles: `dev` (H2, debug logging), `staging`, `prod` (real DB, minimal logging).

**50. What is `@Value` in Spring Boot?**
`@Value("${server.port}")` injects a value from `application.properties` directly into a field. Use `${property.name:defaultValue}` for defaults. For type-safe configuration with validation, prefer `@ConfigurationProperties(prefix = "ems")` which maps a whole block of properties to a POJO.

---

## Spring Security

**51. What is Spring Security?**
Spring Security is a powerful, customisable authentication and authorisation framework for Spring applications. It secures web endpoints, protects against CSRF, session fixation, and XSS attacks, and integrates with JWT, OAuth2, LDAP, and database authentication. Applied via a filter chain on top of the Servlet layer.

**52. What is the difference between authentication and authorisation in Spring Security?**
Authentication verifies identity — who is making the request (username/password, JWT). Authorisation determines access — is the authenticated user allowed to perform this action. Spring Security's filter chain handles authentication first; `@PreAuthorize`, `@Secured`, or `antMatchers` handle authorisation.

**53. What is `SecurityFilterChain`?**
In modern Spring Security (6+), `SecurityFilterChain` replaces the old `WebSecurityConfigurerAdapter`. Define it as a `@Bean` returning a `SecurityFilterChain` configured with `HttpSecurity`. Specify which endpoints are public, which require authentication, CSRF settings, JWT filter registration, and session management.

**54. What is a JWT filter in Spring Security?**
A `OncePerRequestFilter` implementation that intercepts every HTTP request, extracts the JWT from the `Authorization: Bearer <token>` header, validates it, and sets the `Authentication` object in the `SecurityContext`. Subsequent security checks read from the context to determine the user's identity and roles.

**55. What is `@PreAuthorize`?**
`@PreAuthorize` enables method-level security with SpEL expressions. `@PreAuthorize("hasRole('ADMIN')")` allows only admins to call the method. `@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")` allows either. Enable with `@EnableMethodSecurity` on a configuration class. More flexible than URL-based rules.

---

## Real Time

**56. `@Autowired` field is null — why?**
The bean is not being managed by Spring — you created it with `new` instead of injecting it. Or the class itself was not picked up by component scanning (wrong package, missing `@Component`). Or you are using field injection in a `@Configuration` class incorrectly. Always let Spring create and inject beans.

**57. Circular dependency — how to fix?**
Circular dependency: Bean A needs Bean B, and Bean B needs Bean A — Spring cannot instantiate either. Fix options: (1) Refactor to break the cycle (preferred), (2) Use `@Lazy` on one injection — defers creation, (3) Use setter injection instead of constructor injection for one side, (4) Extract shared logic to a third bean.

**58. `@Transactional` not working — why?**
Most common reasons: (1) Self-invocation — method in the same class calling another `@Transactional` method bypasses the proxy. (2) Applied to a private method — Spring AOP cannot proxy private methods. (3) Exception type not triggering rollback — only `RuntimeException` rolls back by default; add `rollbackFor = Exception.class` for checked exceptions.

**59. N+1 queries causing performance issues — fix?**
Identify with `spring.jpa.show-sql=true` and count the queries for a list response. Fix with: `@EntityGraph(attributePaths = {"department"})` on the repository method to fetch associations in a JOIN, or `@Query` with explicit `JOIN FETCH`. For very large datasets, use projections (DTOs) instead of full entities.

**60. Spring Boot app starts but endpoints return 404 — why?**
Common causes: (1) `@RestController` or `@Controller` annotation missing, (2) `@RequestMapping` path mismatch, (3) Component scan not reaching the controller's package (controller is outside the main class package), (4) Context path misconfigured (`server.servlet.context-path=/api`), (5) Wrong HTTP method used in the request.

---

## More

**61. What is the difference between `@Component` and `@Bean`?**
`@Component` is applied to a class — Spring auto-detects and registers it via component scanning. `@Bean` is applied to a method in a `@Configuration` class — you manually instantiate and configure the object. Use `@Bean` for third-party classes you cannot annotate (no source access) or when complex initialisation is needed.

**62. What is `@Lazy` in Spring?**
`@Lazy` on a bean delays its creation until it is first requested, rather than at application startup. Useful for: breaking circular dependencies, reducing startup time for rarely-used expensive beans, and conditional initialisation. Apply to `@Bean` methods, `@Component` classes, or at the injection point with `@Autowired @Lazy`.

**63. What is the difference between `@Repository` and a plain `@Component`?**
`@Repository` is semantically equivalent to `@Component` for bean registration, but adds automatic exception translation — JPA/JDBC exceptions are caught and re-thrown as Spring's `DataAccessException` hierarchy. This provides a consistent exception model across different data access technologies.

**64. What is Spring Boot DevTools?**
`spring-boot-devtools` provides automatic restart on classpath changes, LiveReload browser integration, and relaxed property defaults for development (disable template caching, enable debug logging). Add as a devDependency only — it is automatically excluded from production builds. Makes the development cycle faster.

**65. What is the difference between `@PathVariable` being optional and required?**
By default `@PathVariable` is required — Spring returns 500 if the path variable is missing (it would be a routing error anyway since the path wouldn't match). Make it optional with `@PathVariable(required = false)` — only useful with complex route patterns. `@RequestParam` is more commonly made optional with `required = false` and a `defaultValue`.

**66. What is HATEOAS in Spring?**
HATEOAS (Hypermedia as the Engine of Application State) is a REST maturity level where API responses include links to related actions. Spring HATEOAS (`spring-boot-starter-hateoas`) provides `EntityModel`, `CollectionModel`, and `WebMvcLinkBuilder` to build hypermedia-driven APIs. Not used in every project but important for truly RESTful APIs.

**67. What is `CommandLineRunner` and `ApplicationRunner`?**
Both are interfaces whose `run()` method executes after the Spring application context is fully started. `CommandLineRunner` receives raw `String[] args`. `ApplicationRunner` receives `ApplicationArguments`. Use for: initial data seeding, warm-up tasks, scheduled job triggering. Implement in a `@Component` class.

**68. What is `@Scheduled` in Spring Boot?**
`@Scheduled` triggers a method on a fixed schedule. `@Scheduled(cron = "0 0 * * * *")` runs every hour. `@Scheduled(fixedRate = 60000)` runs every 60 seconds. Enable with `@EnableScheduling` on a configuration class. Runs in a single thread by default — configure `TaskScheduler` for concurrent scheduled tasks.

**69. What is `@Async` in Spring?**
`@Async` runs a method in a separate thread from a thread pool — the caller is not blocked. Enable with `@EnableAsync`. The method must return `void` or `Future<T>` / `CompletableFuture<T>` to get the result. Same proxy limitation as `@Transactional` — self-invocations don't work.

**70. What is Spring Boot's health check endpoint?**
`/actuator/health` (provided by `spring-boot-actuator`) returns the application's health status — `{"status": "UP"}` or `"DOWN"`. It checks registered health indicators: database connectivity, disk space, message broker. Used by Kubernetes liveness and readiness probes to determine if the pod should receive traffic or be restarted.

---

## Answer Summary Table

| # | Question (short) | Key answer |
|---|---------|------------|
| 1 | What is Spring? | Comprehensive framework: DI, AOP, MVC, Data |
| 2 | IoC Container | Framework creates and wires objects |
| 3 | Dependency Injection | Container injects dependencies into classes |
| 5 | @Component specialisations | @Service / @Repository / @Controller |
| 7 | Constructor vs field injection | Constructor preferred — explicit, testable |
| 9 | Bean scopes | singleton (default) / prototype / request / session |
| 16 | AOP | Separate cross-cutting concerns from business logic |
| 19 | @Transactional | Declarative transaction management via AOP |
| 21 | Spring MVC | DispatcherServlet → Controller → View |
| 23 | @Controller vs @RestController | View name vs JSON response body |
| 29 | Spring Boot | Auto-configured, standalone, embedded server |
| 31 | Auto-configuration | Configures beans based on classpath |
| 33 | Starters | Pre-packaged compatible dependency sets |
| 36 | Spring Data JPA | Repository interfaces with auto-implemented CRUD |
| 40 | N+1 problem | 1 + N queries for lazy associations; fix with JOIN FETCH |
| 46 | @Valid | Bean Validation on request bodies |
| 47 | DTO | Decouple API contract from entity model |
| 48 | Actuator | Production endpoints: health, metrics, info |
| 51 | Spring Security | Authentication + authorisation filter chain |
| 54 | JWT filter | Extract/validate token, populate SecurityContext |
| 58 | @Transactional not working | Self-invocation, private method, wrong exception type |
