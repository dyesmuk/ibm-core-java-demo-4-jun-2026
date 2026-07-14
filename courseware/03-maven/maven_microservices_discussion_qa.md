# Maven & Microservices — Discussion Questions & Answers

---

# Part A — Maven

## Fundas

**1. What is Maven?**
Maven is a build automation and project management tool for Java projects. It manages dependencies (downloads JAR files from repositories), defines the build lifecycle (compile → test → package → deploy), and enforces a standard project structure. Configured via `pom.xml`.

**2. What is `pom.xml`?**
POM (Project Object Model) is Maven's configuration file. It defines the project's coordinates (groupId, artifactId, version), dependencies, plugins, build configuration, and profiles. Maven reads this file to understand what to build and how.

**3. What are Maven coordinates?**
The three values that uniquely identify a Maven artifact: `groupId` (organisation — `com.ibm.ems`), `artifactId` (project name — `ems-api`), and `version` (`1.0.0`). Together they form a unique identifier: `com.ibm.ems:ems-api:1.0.0`. Used to declare dependencies and publish artifacts.

**4. What is the Maven build lifecycle?**
The default lifecycle has phases executed in order: `validate` → `compile` → `test` → `package` → `verify` → `install` → `deploy`. Running `mvn package` executes all phases up to and including package. Each phase runs bound plugins — e.g. `compile` runs the `maven-compiler-plugin`.

**5. What is the difference between `mvn install` and `mvn deploy`?**
`mvn install` compiles, tests, packages, and installs the artifact to the local Maven repository (`~/.m2`). `mvn deploy` does all of that plus uploads the artifact to a remote repository (Nexus, Artifactory) so other team members or pipelines can download it.

**6. What is the local Maven repository?**
The local repository is a cache on your machine (typically `~/.m2/repository`) where Maven stores downloaded dependencies and installed artifacts. The first time you build, Maven downloads from remote repositories; subsequent builds use the local cache.

**7. What is Maven Central?**
Maven Central is the default public remote repository where open-source Java libraries are published. When you declare a dependency in `pom.xml`, Maven first checks the local cache, then downloads from Maven Central (or configured repositories) if not found.

**8. What is a Maven plugin?**
Plugins are the mechanisms that perform actual work in Maven — compiling code, running tests, packaging JARs. Each plugin has goals (individual tasks). `maven-compiler-plugin` (compile), `maven-surefire-plugin` (tests), `maven-jar-plugin` (package JAR), `spring-boot-maven-plugin` (create fat JAR). Plugins are bound to lifecycle phases.

**9. What is a Maven profile?**
Profiles allow conditional build configuration — different dependencies, properties, or plugins for different environments. Activate with `mvn package -Pprod` or automatically based on OS, JDK version, or property presence. Use for environment-specific configuration without maintaining multiple `pom.xml` files.

**10. What is a multi-module Maven project?**
A multi-module project has a parent POM and multiple child modules (`ems-api`, `ems-core`, `ems-common`). The parent manages shared configuration and dependency versions. Build all modules together: `mvn install` from the parent. Modules can declare each other as dependencies, enabling modular monolith and microservices architectures.

---

## Dependency Management

**11. What is the difference between `<dependencies>` and `<dependencyManagement>`?**
`<dependencies>` directly adds dependencies to the current module's classpath. `<dependencyManagement>` in a parent POM declares versions centrally — child modules inherit the version without specifying it. This ensures consistent versions across modules without repeating version numbers everywhere.

**12. What are Maven dependency scopes?**
Scope controls when a dependency is available: `compile` (default — always available), `test` (JUnit, Mockito — only during testing, not in production JAR), `provided` (Servlet API — available at compile/test but not packaged, provided by the container), `runtime` (JDBC driver — not needed to compile but needed to run), `import` (for BOMs).

**13. What is a BOM (Bill of Materials)?**
A BOM is a special POM imported in `<dependencyManagement>` that provides pre-tested, compatible versions of a set of dependencies. Spring Boot's `spring-boot-dependencies` BOM defines versions for all Spring libraries — you add dependencies without specifying versions, and Spring Boot guarantees they work together.

**14. What is a transitive dependency?**
If your project depends on A, and A depends on B, then B is a transitive dependency of your project — Maven includes it automatically. Use `mvn dependency:tree` to see all transitive dependencies. Use `<exclusions>` to remove a transitive dependency that conflicts or is unwanted.

**15. What does `mvn dependency:tree` do?**
Displays the full dependency tree — your direct dependencies and all their transitive dependencies, with version information. Essential for debugging version conflicts (`[WARNING] Dependency mediation`) and finding where a transitive dependency comes from.

**16. What is the difference between `SNAPSHOT` and `RELEASE` versions?**
A `SNAPSHOT` version (`1.0.0-SNAPSHOT`) is a development version — Maven always checks for the latest snapshot from the remote repository. A `RELEASE` version (`1.0.0`) is immutable — once published, it cannot be changed. Use SNAPSHOTs during development; release a fixed version for production.

---

## Maven in Practice

**17. What is the `target/` directory?**
Maven places all build output in `target/` — compiled `.class` files, packaged JAR/WAR, test reports, and generated sources. It is always in `.gitignore` (never commit it). `mvn clean` deletes this directory; `mvn clean package` cleans then rebuilds.

**18. What is a fat JAR (uber JAR)?**
A fat JAR contains your application code plus all its dependencies bundled into a single executable JAR. Spring Boot's `spring-boot-maven-plugin` produces one — run with `java -jar ems-api.jar`. This simplifies deployment — no separate dependency management on the server.

**19. What is the `maven-surefire-plugin`?**
This plugin runs unit tests during the `test` phase. It discovers JUnit/TestNG tests automatically. Skip tests with `mvn package -DskipTests` (skip execution) or `mvn package -Dmaven.test.skip=true` (skip compilation too). Test reports are generated in `target/surefire-reports/`.

**20. What is the difference between `mvn clean` and `mvn clean install`?**
`mvn clean` only deletes the `target/` directory — nothing is compiled or installed. `mvn clean install` deletes `target/`, then compiles, tests, packages, and installs the artifact to the local repository. Always run `clean` before `install` in CI pipelines to ensure a fresh build.

---

# Part B — Microservices

## Fundas

**21. What are microservices?**
Microservices is an architectural style where an application is built as a collection of small, independently deployable services — each responsible for a specific business capability, running in its own process, and communicating over a network (REST, messaging). Contrast with monolithic architecture where all features are in one deployable unit.

**22. What is the difference between monolithic and microservices architecture?**
A monolith packages all features (authentication, employees, departments, reporting) into one deployable unit — simple to develop initially but becomes hard to scale and maintain as it grows. Microservices split each capability into its own service — independently deployable and scalable, but adds network complexity, distributed system challenges, and operational overhead.

**23. What are the benefits of microservices?**
Independent deployment (update one service without affecting others), independent scaling (scale only the high-traffic service), technology flexibility (each service can use a different stack), fault isolation (one service failing doesn't bring down everything), and smaller, focused codebases easier to understand and maintain.

**24. What are the challenges of microservices?**
Network latency and failures between services, data consistency across services (no distributed transactions), service discovery complexity, distributed tracing difficulty, operational overhead (many deployments to manage), and testing complexity. Microservices are harder to develop and operate than a well-structured monolith.

**25. When should you NOT use microservices?**
For small teams, early-stage products, or applications that don't need independent scaling. Start with a monolith and extract services when you have clear boundaries, team scaling needs, or specific services with different performance requirements. "Monolith first" is often wiser — don't over-engineer early.

---

## Communication & Patterns

**26. What is the difference between synchronous and asynchronous communication in microservices?**
Synchronous: the caller waits for a response — REST (HTTP) and gRPC. Simple but the caller is blocked and both services must be available simultaneously. Asynchronous: the caller sends a message and continues — message brokers (RabbitMQ, Kafka). Decouples services but adds complexity (eventual consistency, message ordering).

**27. What is an API Gateway?**
An API Gateway is a single entry point for all client requests to the microservices backend. It handles routing, authentication, rate limiting, SSL termination, request aggregation, and logging — so each individual microservice doesn't implement these concerns. Examples: Spring Cloud Gateway, Kong, AWS API Gateway, NGINX.

**28. What is service discovery?**
Service discovery allows microservices to find each other dynamically without hardcoded URLs. Services register themselves with a service registry (Eureka, Consul) when they start. Callers query the registry for the current address of a target service. Kubernetes provides built-in service discovery via DNS (`http://employee-service/api/employees`).

**29. What is the Circuit Breaker pattern?**
A circuit breaker monitors calls to a downstream service. When the failure rate exceeds a threshold, it "trips" — subsequent calls fail fast without actually calling the failing service (preventing cascade failure). After a timeout, it allows a test request through; if it succeeds, the circuit closes. Implemented with Resilience4j in Spring Boot.

**30. What is the difference between the Circuit Breaker and Retry patterns?**
Retry simply retries a failed call N times — useful for transient failures (brief network hiccup). Circuit Breaker stops trying after repeated failures to protect the caller and the failing service from being overwhelmed. Combine them: retry first (transient errors), then circuit breaker (persistent failures).

**31. What is the Saga pattern?**
The Saga pattern manages distributed transactions across multiple microservices. Since you can't use a single database transaction across services, a Saga is a sequence of local transactions — each publishes an event that triggers the next. On failure, compensating transactions undo the previous steps. Two styles: Choreography (event-driven) and Orchestration (central coordinator).

**32. What is the Strangler Fig pattern?**
A strategy for migrating from a monolith to microservices incrementally. The new microservice "strangles" the monolith by handling increasing portions of functionality over time. An API gateway routes requests — new features go to microservices, existing features stay in the monolith until they are extracted. The monolith gradually shrinks.

**33. What is an event-driven architecture?**
Services communicate by publishing and consuming events via a message broker (Kafka, RabbitMQ). The publisher has no knowledge of consumers — loose coupling. Events represent facts: `EmployeeCreated`, `SalaryUpdated`. Consumers react asynchronously. Enables high scalability and decoupling but requires handling eventual consistency.

**34. What is the difference between Kafka and RabbitMQ?**
RabbitMQ is a traditional message broker — messages are routed to queues and consumed once. Good for task queues and RPC patterns. Kafka is a distributed event streaming platform — messages (events) are written to logs and retained for a configurable period, allowing multiple consumers to replay them independently. Use Kafka for event sourcing, audit logs, and high-throughput streams; RabbitMQ for task distribution.

---

## Data Management

**35. What is the "database per service" pattern?**
Each microservice owns its own database (or schema) — no other service can access it directly. Data sharing happens via APIs or events. This ensures loose coupling and allows each service to choose the most appropriate database technology (SQL for transactions, MongoDB for documents, Redis for caching).

**36. What is eventual consistency?**
In microservices, since each service has its own database and communication is asynchronous, data across services is not instantly consistent — it becomes consistent eventually. Example: after creating an order (OrderService), the inventory (InventoryService) decrements stock after receiving the event — there is a brief window of inconsistency. This is the trade-off for loose coupling.

**37. What is CQRS (Command Query Responsibility Segregation)?**
CQRS separates the write model (commands that change state) from the read model (queries that return data). Each can be optimised independently — write model uses a normalised relational database; read model uses a denormalised view optimised for queries. Adds complexity but solves read/write performance mismatches.

**38. What is event sourcing?**
Instead of storing the current state of an entity, event sourcing stores the sequence of events that led to the current state. Current state is derived by replaying events. Provides a complete audit trail, enables time-travel debugging, and integrates naturally with event-driven microservices.

---

## Spring Cloud Microservices

**39. What is Spring Cloud?**
Spring Cloud provides tools for building distributed systems and microservices on top of Spring Boot — service discovery (Eureka), circuit breakers (Resilience4j), distributed configuration (Config Server), API gateway (Spring Cloud Gateway), distributed tracing (Micrometer + Zipkin), and load balancing (Spring Cloud LoadBalancer).

**40. What is Eureka?**
Eureka (Netflix/Spring Cloud) is a service registry — a central server where microservices register themselves at startup. Client services query Eureka to discover the addresses of other services instead of hardcoding URLs. Supports client-side load balancing — the caller chooses which instance to call.

**41. What is Spring Cloud Config Server?**
Config Server externalises configuration for all microservices into a centralised Git repository. Services fetch their configuration at startup via HTTP (`http://config-server/employee-service/prod`). Changing a property no longer requires redeployment — services can refresh configuration at runtime with `/actuator/refresh`.

**42. What is Resilience4j?**
Resilience4j is a lightweight fault tolerance library for Java microservices. It provides Circuit Breaker, Retry, Rate Limiter, Bulkhead, and Time Limiter patterns as function decorators. Integrates with Spring Boot via `spring-boot-starter-resilience4j` and exposes metrics via Actuator.

**43. What is distributed tracing?**
In microservices, a single user request might touch 5-10 services. Distributed tracing tracks the request as it flows through services by assigning a unique trace ID and span IDs. Tools: Micrometer Tracing + Zipkin or Jaeger. You can see the entire call chain and where latency or errors occur.

**44. What is an idempotent API and why is it important in microservices?**
An idempotent API produces the same result if called once or multiple times with the same input. `DELETE /employees/1` is idempotent — the second call finds nothing and returns 404 (or 200) consistently. In microservices, retries are common (circuit breaker, network retry) — idempotent APIs prevent duplicate operations (charging twice, creating twice). Use idempotency keys for non-idempotent operations like payment.

---

## Real Time

**45. Inter-service call is slow — how to diagnose?**
Add distributed tracing (Zipkin) to see where time is spent across the call chain. Check if the slow service has database queries without indexes (`EXPLAIN` in SQL, `explain()` in MongoDB). Check connection pool exhaustion — too few connections to the database. Add circuit breaker with timeout to prevent the slow service from blocking callers indefinitely.

**46. One microservice is failing and taking down other services — what is the pattern to fix?**
The Circuit Breaker pattern. Without it, a slow/failing downstream service causes threads in the caller to pile up waiting — the caller runs out of threads and also fails (cascade failure). With a circuit breaker (Resilience4j), calls to the failing service fail fast, freeing threads. Combine with a fallback (return cached data, empty list, or user-friendly error).

**47. How do you handle authentication across microservices?**
Use JWTs issued by a centralised auth service (or identity provider). The API Gateway validates the JWT for every incoming request and passes the user identity (userId, roles) to downstream services as a trusted header. Individual microservices trust the header without re-validating the JWT — avoiding repeated calls to the auth service.

**48. Data consistency issue — order created but inventory not updated — how to investigate?**
Check message broker (Kafka/RabbitMQ) for unprocessed messages — the event may be stuck in the queue. Check the consumer service logs for processing errors. Check if the consumer committed the offset before or after processing (at-least-once vs exactly-once delivery). Add idempotency to the consumer to handle duplicate deliveries. Implement a dead letter queue for messages that fail repeatedly.

**49. How do you deploy a new version of a microservice without downtime?**
Use Kubernetes rolling updates (`maxUnavailable: 0, maxSurge: 1`) — new Pods are started before old ones are terminated. Use readiness probes so traffic only routes to Pods that have fully started. For database changes, use backward-compatible migrations — the new and old service versions must work with the same schema simultaneously.

**50. How do you test microservices?**
Unit tests for individual classes. Integration tests for a single service with a real database (testcontainers for ephemeral Docker containers). Contract tests (Spring Cloud Contract, Pact) verify the API contract between consumer and producer without deploying both. End-to-end tests in a staging environment run the full system together.

---

## More

**51. What is the difference between horizontal and vertical decomposition of microservices?**
Vertical decomposition slices by business capability — one service per feature (EmployeeService, DepartmentService, ProjectService). Horizontal decomposition slices by technical layer — rarely used as it creates coupling. Always decompose vertically — services should be self-contained business capabilities, not technical layers.

**52. What is a sidecar pattern?**
A sidecar is an additional container deployed alongside the main application container in the same Pod. It handles cross-cutting concerns — logging, service mesh proxy (Envoy/Istio), secrets injection, or monitoring agent. The sidecar extends the main container's capabilities without modifying it. Used heavily in Istio service mesh deployments.

**53. What is a service mesh?**
A service mesh is an infrastructure layer that handles service-to-service communication — mutual TLS (mTLS) encryption, traffic management, circuit breaking, retries, and distributed tracing — transparently via sidecar proxies (Envoy). Istio and Linkerd are popular implementations. The application code doesn't change — the mesh handles resilience at the network level.

**54. What is the Bulkhead pattern?**
The Bulkhead pattern (from ship design) isolates resources for different services — if one service's thread pool is exhausted, it doesn't affect other services' thread pools. In Resilience4j, `@Bulkhead(name = "employeeService", type = THREADPOOL)` limits concurrent calls to a service. Prevents one problematic downstream service from starving all others.

**55. What is the 12-Factor App methodology?**
A set of 12 principles for building cloud-native, scalable, maintainable applications: (1) Codebase in VCS, (2) Explicit dependencies, (3) Config in environment variables, (4) Backing services as attached resources, (5) Separate build/run stages, (6) Stateless processes, (7) Port binding, (8) Concurrency via process scaling, (9) Disposability (fast start/graceful shutdown), (10) Dev/prod parity, (11) Logs as streams, (12) Admin processes. Spring Boot microservices naturally align with most of these.

---

## Answer Summary Table

| # | Question (short) | Key answer |
|---|---------|------------|
| 1 | Maven | Build + dependency management for Java |
| 2 | pom.xml | Project config: coords, deps, plugins, profiles |
| 4 | Build lifecycle | validate → compile → test → package → install → deploy |
| 5 | install vs deploy | install = local repo; deploy = remote repo |
| 11 | dependencies vs dependencyManagement | Direct vs centralised version management |
| 12 | Dependency scopes | compile/test/provided/runtime |
| 13 | BOM | Pre-tested compatible version set |
| 18 | Fat JAR | App + all dependencies in one executable JAR |
| 21 | Microservices | Small, independent, deployable business capabilities |
| 22 | Monolith vs microservices | One unit vs many independent services |
| 26 | Sync vs async communication | HTTP (wait) vs messaging (fire and forget) |
| 27 | API Gateway | Single entry point — routing, auth, rate limiting |
| 28 | Service discovery | Dynamic address lookup via registry |
| 29 | Circuit Breaker | Fail fast to prevent cascade failures |
| 31 | Saga pattern | Distributed transaction via compensating transactions |
| 35 | Database per service | Each service owns its data — no shared DB |
| 36 | Eventual consistency | Data syncs across services with a delay |
| 39 | Spring Cloud | Microservices toolkit: Eureka, Gateway, Config, Resilience4j |
| 43 | Distributed tracing | Track request across services with trace/span IDs |
| 44 | Idempotent API | Same result whether called once or many times |
| 55 | 12-Factor App | Principles for cloud-native applications |
