# Clean Code — Java Foundation Level 0.5 (Internal)

> A structured learning guide for writing readable, maintainable, and professional Java code.
> **Duration:** 0.5 days | **Facilitation:** Internal trainers | **Track:** Java Foundation

---

## Table of Contents

1. [What is Clean Code?](#1-what-is-clean-code)
2. [Meaningful Names](#2-meaningful-names)
3. [Small, Focused Methods](#3-small-focused-methods)
4. [Comments — Less is More](#4-comments--less-is-more)
5. [Consistent Formatting](#5-consistent-formatting)
6. [DRY — Don't Repeat Yourself](#6-dry--dont-repeat-yourself)
7. [Single Responsibility Principle (SRP) — Intro](#7-single-responsibility-principle-srp--intro)
8. [Quick Reference Cheat Sheet](#8-quick-reference-cheat-sheet)
9. [Tools & IDE Setup](#9-tools--ide-setup)
10. [Recommended Reading](#10-recommended-reading)

---

## 1. What is Clean Code?

Clean code is code that is **easy to read, easy to understand, and easy to change**.

> *"Any fool can write code that a computer can understand. Good programmers write code that humans can understand."*
> — Martin Fowler

Clean code is not about being clever. It is about being **clear**. In a professional team environment, your code will be read many more times than it is written — by your teammates, your future self, and during code reviews.

### Why it matters in this session

This is a half-day session facilitated by internal trainers as part of the Java Foundation track. The goal is to build **habits** early:
- Name things clearly
- Keep methods small
- Remove noise
- Avoid duplication

These habits compound. A team that writes clean code spends less time debugging, less time onboarding new members, and less time untangling mistakes.

---

## 2. Meaningful Names

Names are the primary communication tool in code. A good name eliminates the need for a comment.

### 2.1 Core Rules

| Rule | Detail |
|------|--------|
| Use descriptive names | The name should reveal intent without needing a comment |
| Avoid abbreviations | `custAddr` → `customerAddress`, `d` → `elapsedDays` |
| Class names = nouns | `User`, `OrderService`, `PaymentProcessor` |
| Method names = verbs | `sendEmail()`, `calculateTotal()`, `isValidAge()` |
| Booleans use is/has/can/should | `isActive`, `hasPermission`, `canRetry` |
| Avoid noise words | `UserData`, `UserInfo`, `UserObject` all mean the same thing |
| Use domain language | Use terms your business stakeholders would recognise |

### 2.2 Variables and Fields

```java
// ❌ Avoid — intent is unclear
int d;
String n;
boolean flag;
List lst;
double x;

// ✅ Prefer — self-documenting
int elapsedDays;
String customerName;
boolean isEmailVerified;
List<Order> pendingOrders;
double discountedPrice;
```

### 2.3 Methods

```java
// ❌ Avoid — vague verbs, unclear purpose
void proc(User u);
boolean check(String s);
int calc(int a, int b);
void handleStuff();

// ✅ Prefer — verb + context
void sendWelcomeEmail(User user);
boolean isValidEmailFormat(String email);
int calculateTotalPrice(int quantity, int unitPrice);
void cancelExpiredSessions();
```

### 2.4 Classes

```java
// ❌ Avoid — too generic or multi-purpose implied
class Manager { }
class Processor { }
class Handler { }
class Utils { }        // utility dumping ground

// ✅ Prefer — specific noun, single concept
class SessionManager { }
class InvoiceProcessor { }
class PaymentGatewayHandler { }
class DateFormatter { }
```

### 2.5 Booleans

```java
// ❌ Avoid — ambiguous
boolean status;
boolean value;
boolean done;
boolean result;

// ✅ Prefer — reads like a question
boolean isLoggedIn;
boolean hasUnreadNotifications;
boolean canAccessAdminPanel;
boolean shouldRetryRequest;
```

### 2.6 Naming Conventions Quick Reference

```
Classes       →  PascalCase          → CustomerOrder, EmailValidator
Methods       →  camelCase           → processPayment(), getCustomerById()
Variables     →  camelCase           → totalAmount, currentUser
Constants     →  UPPER_SNAKE_CASE    → MAX_RETRY_COUNT, DEFAULT_TIMEOUT_MS
Packages      →  lowercase           → com.company.payments.service
```

### Tip

> Read your variable name out loud. If you would not say it in a sentence describing what the code does, rename it.

---

## 3. Small, Focused Methods

A method should do **one thing**, do it **well**, and do it **only**.

### 3.1 Core Rules

| Rule | Detail |
|------|--------|
| Single Responsibility | One method = one task |
| Keep it short | Aim for 5–15 lines; never exceed ~30 |
| Limit parameters | Max 2–3 params; more = extract a parameter object |
| One level of abstraction | Don't mix high-level logic with low-level detail |
| Avoid side effects | A method named `getUser()` should not also save logs |

### 3.2 Splitting Responsibilities

```java
// ❌ Avoid — this method does validation, calculation, discount, persistence, and email
void processOrder(Order order) {
    // validate
    if (order.getItems().isEmpty()) throw new IllegalArgumentException("No items");
    if (order.getCustomer() == null) throw new IllegalArgumentException("No customer");

    // calculate total
    double total = 0;
    for (Item item : order.getItems()) {
        total += item.getPrice() * item.getQuantity();
    }

    // apply discount
    if (order.getCustomer().isPremium()) {
        total = total * 0.9;
    }

    // save and notify
    orderRepository.save(order);
    emailService.sendOrderConfirmation(order);
}

// ✅ Prefer — each method does exactly one thing
void processOrder(Order order) {
    validateOrder(order);
    double total = calculateOrderTotal(order);
    order.setTotal(total);
    orderRepository.save(order);
    emailService.sendOrderConfirmation(order);
}

private void validateOrder(Order order) {
    if (order.getItems().isEmpty()) throw new IllegalArgumentException("Order has no items");
    if (order.getCustomer() == null) throw new IllegalArgumentException("Order has no customer");
}

private double calculateOrderTotal(Order order) {
    double subtotal = order.getItems().stream()
        .mapToDouble(item -> item.getPrice() * item.getQuantity())
        .sum();
    return applyDiscount(subtotal, order.getCustomer());
}

private double applyDiscount(double amount, Customer customer) {
    return customer.isPremium() ? amount * PREMIUM_DISCOUNT_RATE : amount;
}
```

### 3.3 Too Many Parameters

```java
// ❌ Avoid — hard to call, hard to read, easy to mix up argument order
void createUser(String firstName, String lastName, String email,
                String phone, String address, boolean isAdmin);

// ✅ Prefer — group related data into an object
void createUser(UserRegistrationRequest request);

// Where UserRegistrationRequest is a simple data class (or record in Java 16+)
record UserRegistrationRequest(
    String firstName,
    String lastName,
    String email,
    String phone,
    String address,
    boolean isAdmin
) {}
```

### Tip

> Name your method first. If the name contains "and" (e.g., `validateAndSave`, `fetchAndFormat`), the method is doing too much — split it.

---

## 4. Comments — Less is More

The best comment is **no comment** — write code that explains itself. Comments that describe *what* the code does are noise. Comments that explain *why* are valuable.

### 4.1 Core Rules

| Rule | Detail |
|------|--------|
| Don't say what the code says | Redundant comments add noise, not clarity |
| Explain WHY, not WHAT | Business rules, trade-offs, non-obvious decisions |
| Delete commented-out code | Use version control (Git) instead |
| No misleading comments | An outdated comment is worse than no comment |
| Avoid TODOs without a ticket | `// TODO fix this` → `// TODO [PROJ-421] fix edge case` |

### 4.2 Noisy vs. Useful Comments

```java
// ❌ Avoid — these comments add zero value

// increment counter
counter++;

// check if user is null
if (user == null) {
    return;
}

// set the name
user.setName(name);

// loop through items
for (Item item : items) {
    process(item);  // process the item
}
```

```java
// ✅ Prefer — comments that explain WHY or capture important context

// GST is fixed at 18% as per Finance Act 2023, applicable until March 2026
private static final double GST_RATE = 0.18;

// Retry once on transient DB errors per architecture decision ADR-14
// See: confluence.internal/adr-14
if (e instanceof TransientDataAccessException) {
    return retryOperation(operation);
}

// The external payment API returns HTTP 200 even for failed transactions.
// We must check the response body status field explicitly.
if (!"SUCCESS".equals(response.getStatus())) {
    throw new PaymentFailedException(response.getErrorCode());
}
```

### 4.3 Commented-out Code

```java
// ❌ Avoid — dead code that clutters the file
public double calculatePrice(Product product) {
    // double price = product.getBasePrice() * 1.1;
    // if (product.isOnSale()) price = price * 0.85;
    // return Math.round(price * 100.0) / 100.0;
    return product.getCurrentPrice();
}

// ✅ Prefer — delete it. It lives in Git history if you ever need it.
public double calculatePrice(Product product) {
    return product.getCurrentPrice();
}
```

### Tip

> Before adding a comment, ask yourself: can I rename a variable or extract a method to make this comment unnecessary?

---

## 5. Consistent Formatting

Consistent formatting reduces cognitive load. It lets readers focus on **logic**, not **layout**.

### 5.1 Core Rules

| Rule | Detail |
|------|--------|
| Indent with 4 spaces | Java standard; do not use tabs |
| Opening brace on same line | K&R style — Java convention |
| One blank line between methods | Visual separation; improves scannability |
| Max line length ~120 chars | Avoids horizontal scrolling |
| One statement per line | Never chain multiple statements on one line |
| Blank lines within methods | Separate logical blocks with one blank line |

### 5.2 Before and After

```java
// ❌ Avoid — compressed, inconsistent, hard to parse
public class Order{
private List<Item> items;
private Customer customer;
public Order(Customer customer){this.customer=customer;this.items=new ArrayList<>();}
public void addItem(Item i){if(i!=null)items.add(i);}
public double getTotal(){double t=0;for(Item i:items)t+=i.getPrice();return t;}
}
```

```java
// ✅ Prefer — clean, readable, conventional Java style
public class Order {

    private final List<Item> items;
    private final Customer customer;

    public Order(Customer customer) {
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        if (item != null) {
            items.add(item);
        }
    }

    public double getTotal() {
        return items.stream()
                    .mapToDouble(Item::getPrice)
                    .sum();
    }
}
```

### 5.3 Logical Grouping Within Methods

```java
// ✅ Group related steps with blank lines for visual paragraphs
public void registerUser(UserRegistrationRequest request) {
    // Step 1: validate input
    validateRegistrationRequest(request);

    // Step 2: prepare domain object
    User user = buildUserFromRequest(request);

    // Step 3: persist and notify
    userRepository.save(user);
    emailService.sendVerificationEmail(user);
}
```

### 5.4 IDE Shortcuts

| IDE | Reformat File | Reformat Selection |
|-----|---------------|--------------------|
| IntelliJ IDEA | `Ctrl+Alt+L` / `Cmd+Option+L` | Same shortcut on selection |
| Eclipse | `Ctrl+Shift+F` | Same shortcut on selection |
| VS Code (Java) | `Shift+Alt+F` / `Shift+Option+F` | Same shortcut on selection |

### Tip

> Configure your team's formatter profile (e.g., Google Java Style or your company's `.editorconfig`) and commit it to the repository so everyone formats consistently.

---

## 6. DRY — Don't Repeat Yourself

Every piece of knowledge should have a **single, authoritative representation** in the system. Duplication is the root of most maintenance problems.

### 6.1 Core Rules

| Rule | Detail |
|------|--------|
| Extract duplicate code | 3+ duplicated lines → extract a shared method |
| Use named constants | Replace literal values with `static final` constants |
| Centralise business rules | A validation rule defined in 5 places will be fixed in 4 |
| DRY in tests is optional | In tests, readability trumps DRY — duplication is acceptable |

### 6.2 Magic Numbers → Named Constants

```java
// ❌ Avoid — what do 18, 100, 0.18 mean? What if they change?
if (age < 18) {
    denyAccess();
}
if (score > 100) {
    score = 100;
}
double tax = price * 0.18;
Thread.sleep(5000);

// ✅ Prefer — named constants are self-documenting and easy to change
private static final int    MINIMUM_AGE_FOR_ACCESS = 18;
private static final int    MAX_SCORE              = 100;
private static final double GST_RATE               = 0.18;
private static final long   DB_RETRY_DELAY_MS      = 5_000;

if (age < MINIMUM_AGE_FOR_ACCESS) {
    denyAccess();
}
if (score > MAX_SCORE) {
    score = MAX_SCORE;
}
double tax = price * GST_RATE;
Thread.sleep(DB_RETRY_DELAY_MS);
```

### 6.3 Extracting Duplicate Logic

```java
// ❌ Avoid — validation logic duplicated across two methods
public void createUser(String email) {
    if (!email.contains("@") || email.length() < 5) {
        throw new IllegalArgumentException("Invalid email: " + email);
    }
    // ... create user
}

public void updateEmail(String userId, String email) {
    if (!email.contains("@") || email.length() < 5) {
        throw new IllegalArgumentException("Invalid email: " + email);
    }
    // ... update email
}

// ✅ Prefer — single source of truth for email validation
private void validateEmail(String email) {
    if (!email.contains("@") || email.length() < 5) {
        throw new IllegalArgumentException("Invalid email format: " + email);
    }
}

public void createUser(String email) {
    validateEmail(email);
    // ... create user
}

public void updateEmail(String userId, String email) {
    validateEmail(email);
    // ... update email
}
```

### 6.4 Structural Duplication

```java
// ❌ Avoid — near-identical methods that differ only in field name
public void printCustomerReport(Customer customer) {
    System.out.println("Name: " + customer.getName());
    System.out.println("Email: " + customer.getEmail());
    System.out.println("---");
}

public void printVendorReport(Vendor vendor) {
    System.out.println("Name: " + vendor.getName());
    System.out.println("Email: " + vendor.getEmail());
    System.out.println("---");
}

// ✅ Prefer — extract a shared interface or use a common method
public void printEntityReport(String name, String email) {
    System.out.println("Name: " + name);
    System.out.println("Email: " + email);
    System.out.println("---");
}
// Or use an interface: Reportable { String getName(); String getEmail(); }
```

### Tip

> If you copy-paste more than 3 lines of code anywhere, stop — extract it into a shared method first.

---

## 7. Single Responsibility Principle (SRP) — Intro

A class should have **only one reason to change**. This is the *S* in SOLID. Within this session, the goal is to **recognise when a class is doing too much** — not to perfectly design class hierarchies.

### 7.1 Core Rules

| Rule | Detail |
|------|--------|
| One class = one concept | A class named `UserEmailDatabaseManager` has at least 3 responsibilities |
| Split data, logic, and I/O | Domain objects should not contain database or network code |
| Watch for the "and" smell | "This class saves users *and* formats reports" — split it |
| Size as a heuristic | Classes over 200 lines often have multiple responsibilities |

### 7.2 Separating Concerns

```java
// ❌ Avoid — User is responsible for its data, persistence, AND email formatting
public class User {
    private String name;
    private String email;

    // Data — fine here
    public String getName() { return name; }
    public String getEmail() { return email; }

    // ❌ Persistence concern — does not belong in a domain object
    public void saveToDatabase(Connection conn) {
        // SQL logic here...
    }

    // ❌ Email formatting concern — does not belong here either
    public String formatForWelcomeEmail() {
        return "Dear " + name + ",\nWelcome! Your account email is " + email;
    }
}

// ✅ Prefer — each class has one clear responsibility

// Domain object: holds user data only
public class User {
    private final String name;
    private final String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName()  { return name; }
    public String getEmail() { return email; }
}

// Repository: handles persistence
public class UserRepository {
    public void save(User user) {
        // database logic
    }

    public Optional<User> findByEmail(String email) {
        // query logic
    }
}

// Formatter: handles email content
public class WelcomeEmailFormatter {
    public String format(User user) {
        return "Dear " + user.getName() + ",\nWelcome! Your account email is " + user.getEmail();
    }
}
```

### 7.3 Common Warning Signs

| Smell | What it suggests |
|-------|-----------------|
| Class name contains "Manager", "Helper", "Utils" | Often a catch-all with no clear responsibility |
| Class has 10+ methods | Probably doing too many things |
| Method changes two unrelated things | Likely two responsibilities merged |
| Imports from 5+ different packages | Class is touching too many concerns |
| Hard to write a one-sentence description of the class | Clear sign of mixed responsibilities |

### Tip

> Ask of every class: *"What is the ONE thing this class is responsible for?"* Write the answer as a one-line comment at the top. If you cannot, the class needs to be split.

---

## 8. Quick Reference Cheat Sheet

```
NAMING
  ✓  Descriptive names that reveal intent
  ✓  camelCase for variables/methods, PascalCase for classes
  ✓  UPPER_SNAKE_CASE for constants
  ✓  Booleans: isX, hasX, canX, shouldX
  ✗  Single letters (except i/j/k in loops)
  ✗  Abbreviations, noise words (data, info, obj)

METHODS
  ✓  Do one thing only
  ✓  5–15 lines (soft), never > 30 (hard)
  ✓  Max 2–3 parameters
  ✓  Verb + noun naming: calculateTotal(), sendEmail()
  ✗  Methods named with "and" (split them)
  ✗  Side effects hidden in getter/query methods

COMMENTS
  ✓  Explain WHY — business rules, trade-offs
  ✓  Reference tickets/decisions (ADR-14, PROJ-421)
  ✗  Explain WHAT the code already says
  ✗  Commented-out code (use Git)
  ✗  Misleading or outdated comments

FORMATTING
  ✓  4-space indentation
  ✓  Opening brace on same line
  ✓  One blank line between methods
  ✓  Max ~120 chars per line
  ✓  Use IDE auto-formatter (Ctrl+Alt+L in IntelliJ)

DRY
  ✓  Extract duplicate code (>3 lines) into shared methods
  ✓  Named constants instead of magic numbers/strings
  ✓  Single source of truth for business rules
  ✗  Copy-paste programming

SRP
  ✓  One reason to change per class
  ✓  Separate domain objects, repositories, formatters
  ✓  Classes < 200 lines (heuristic)
  ✗  Classes named XyzManager, XyzHelper, XyzUtils
```

---

## 9. Tools & IDE Setup

### IntelliJ IDEA (recommended)

| Action | Shortcut (Win/Linux) | Shortcut (Mac) |
|--------|----------------------|----------------|
| Reformat code | `Ctrl+Alt+L` | `Cmd+Option+L` |
| Rename (refactor) | `Shift+F6` | `Shift+F6` |
| Extract method | `Ctrl+Alt+M` | `Cmd+Option+M` |
| Extract constant | `Ctrl+Alt+C` | `Cmd+Option+C` |
| Extract variable | `Ctrl+Alt+V` | `Cmd+Option+V` |
| Inspect code | `Alt+Enter` | `Option+Enter` |

### Checkstyle

Add to your `pom.xml` to enforce style rules automatically:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <configLocation>google_checks.xml</configLocation>
        <failsOnError>true</failsOnError>
    </configuration>
</plugin>
```

### SonarLint

Install the **SonarLint** plugin in IntelliJ or VS Code. It highlights clean code violations inline as you type — including naming issues, long methods, duplicate code, and unused variables.

### `.editorconfig` (add to project root)

```ini
root = true

[*.java]
indent_style = space
indent_size = 4
end_of_line = lf
charset = utf-8
trim_trailing_whitespace = true
insert_final_newline = true
max_line_length = 120
```

---

## 10. Recommended Reading

| Resource | Type | Notes |
|----------|------|-------|
| *Clean Code* — Robert C. Martin | Book | The canonical reference; Chapters 1–5 align directly with this session |
| *The Pragmatic Programmer* — Hunt & Thomas | Book | Broader software craftsmanship; Chapter on DRY is excellent |
| *Refactoring* — Martin Fowler (2nd Ed.) | Book | Practical patterns for improving existing code |
| Google Java Style Guide | Online | `google.github.io/styleguide/javaguide.html` |
| Baeldung — Clean Code in Java | Article | Practical examples; freely available online |

---

## Version & Ownership

| Field | Value |
|-------|-------|
| Duration | 0.5 days (half day) |
| Facilitation | Internal trainers |
| Track | Java Foundation |
| Status | Internal Use Only |
| Next Session | Java Foundation — SOLID Principles Deep Dive |

---

*For questions or feedback on this material, raise it in your team's internal learning channel.*
