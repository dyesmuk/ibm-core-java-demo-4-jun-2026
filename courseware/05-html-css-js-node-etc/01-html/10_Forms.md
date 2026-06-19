# Module 10 — Forms

## 10.1 Why Forms Matter in Enterprise Applications

Forms are the primary mechanism for user input in web applications. As a Java Full Stack Developer you will build forms constantly — login screens, data entry, search filters, configuration panels. Understanding HTML forms deeply means:
- Fewer Spring MVC binding errors
- Better validation (client-side + server-side in sync)
- Better accessibility (mandatory in enterprise)
- Better security (CSRF protection, XSS prevention)

---

## 10.2 The `<form>` Element

```html
<form action="/users/create" method="post" id="createUserForm" novalidate>
  <!-- form fields -->
</form>
```

### `<form>` Attributes

| Attribute | Values | Notes |
|-----------|--------|-------|
| `action` | URL | Where to send data; defaults to current page |
| `method` | `get`, `post` | GET appends to URL; POST sends in body |
| `enctype` | `application/x-www-form-urlencoded`, `multipart/form-data`, `text/plain` | Must be `multipart/form-data` for file uploads |
| `novalidate` | Boolean | Disables browser's built-in validation (use when implementing custom validation) |
| `autocomplete` | `on`, `off` | Controls browser autocomplete |
| `target` | `_self`, `_blank` | Where to display response |

### GET vs POST

```html
<!-- GET: data in URL — use for search, filter, pagination (bookmarkable) -->
<form action="/search" method="get">
  <input type="search" name="q" placeholder="Search...">
  <button type="submit">Search</button>
</form>
<!-- Submits to: /search?q=spring+boot -->

<!-- POST: data in body — use for creating/updating data, login, file uploads -->
<form action="/login" method="post" th:action="@{/login}">
  <!-- Spring Security CSRF token (auto-added by Spring Security + Thymeleaf) -->
  <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
  <input type="email" name="username">
  <input type="password" name="password">
  <button type="submit">Log In</button>
</form>
```

---

## 10.3 `<input>` Types

The `type` attribute fundamentally changes the input's behaviour and appearance.

### Text Inputs

```html
<!-- Plain text -->
<input type="text" name="username" id="username" 
       placeholder="Enter username" 
       required 
       minlength="3" 
       maxlength="50"
       autocomplete="username">

<!-- Email -->
<input type="email" name="email" id="email"
       placeholder="you@company.com"
       required
       autocomplete="email">

<!-- Password -->
<input type="password" name="password" id="password"
       required
       minlength="8"
       autocomplete="new-password">

<!-- Search -->
<input type="search" name="q" id="search" 
       placeholder="Search employees..."
       aria-label="Search employees">

<!-- URL -->
<input type="url" name="website" id="website"
       placeholder="https://example.com">

<!-- Tel (no validation, just triggers phone keyboard on mobile) -->
<input type="tel" name="phone" id="phone"
       pattern="[0-9]{10}"
       placeholder="10-digit mobile number">
```

### Numeric Inputs

```html
<!-- Number -->
<input type="number" name="quantity" id="quantity"
       min="1" max="999" step="1" value="1">

<!-- Range (slider) -->
<input type="range" name="confidence" id="confidence"
       min="0" max="100" step="5" value="50">

<!-- Currency workaround: use number with step -->
<input type="number" name="amount" id="amount"
       min="0" max="9999999" step="0.01" 
       placeholder="0.00">
```

### Date/Time Inputs

```html
<!-- Date picker -->
<input type="date" name="dob" id="dob"
       min="1900-01-01" max="2010-12-31"
       value="1990-01-01">

<!-- Date and time -->
<input type="datetime-local" name="meetingTime" id="meetingTime"
       min="2024-01-01T09:00" 
       max="2025-12-31T18:00">

<!-- Month picker -->
<input type="month" name="billingMonth" id="billingMonth">

<!-- Time picker -->
<input type="time" name="startTime" id="startTime"
       min="09:00" max="18:00" step="900">  <!-- step=900 = 15-minute intervals -->

<!-- Week picker -->
<input type="week" name="sprintWeek" id="sprintWeek">
```

### Selection Inputs

```html
<!-- Checkbox (single) -->
<input type="checkbox" name="agreeTerms" id="agreeTerms" value="yes" required>
<label for="agreeTerms">I agree to the <a href="/terms">Terms of Service</a></label>

<!-- Checkbox group -->
<fieldset>
  <legend>Preferred Contact Methods</legend>
  <label><input type="checkbox" name="contact" value="email"> Email</label>
  <label><input type="checkbox" name="contact" value="phone"> Phone</label>
  <label><input type="checkbox" name="contact" value="sms"> SMS</label>
</fieldset>

<!-- Radio group (always in a fieldset) -->
<fieldset>
  <legend>Account Type</legend>
  <label><input type="radio" name="accountType" value="personal" checked> Personal</label>
  <label><input type="radio" name="accountType" value="business"> Business</label>
  <label><input type="radio" name="accountType" value="enterprise"> Enterprise</label>
</fieldset>
```

### Special Inputs

```html
<!-- File upload -->
<input type="file" name="profilePhoto" id="profilePhoto"
       accept="image/jpeg,image/png,image/webp"
       aria-describedby="photo-hint">
<p id="photo-hint">JPEG or PNG, max 2MB</p>

<!-- Multiple files -->
<input type="file" name="attachments" id="attachments"
       multiple
       accept=".pdf,.docx,.xlsx">

<!-- Hidden field (CSRF token, IDs, etc.) -->
<input type="hidden" name="userId" value="42">

<!-- Colour picker -->
<input type="color" name="brandColor" id="brandColor" value="#0066cc">
```

---

## 10.4 Labels — Mandatory for Accessibility

Every input **must** have a label. Without a label, screen readers can't tell users what to type.

### Explicit Label (Recommended)

```html
<label for="email">Email Address</label>
<input type="email" id="email" name="email" required>
```

`for` attribute matches the input's `id`. Clicking the label focuses the input.

### Implicit Label (input inside label)

```html
<label>
  Email Address
  <input type="email" name="email" required>
</label>
```

### `aria-label` (when no visible label is possible)

```html
<input type="search" 
       name="q" 
       aria-label="Search employees" 
       placeholder="Search...">
```

### `aria-labelledby` (label is elsewhere in the DOM)

```html
<h2 id="address-heading">Billing Address</h2>
<input type="text" name="street" aria-labelledby="address-heading">
```

---

## 10.5 `<textarea>`

```html
<label for="bio">Biography</label>
<textarea id="bio" name="bio" 
          rows="5" cols="40"
          maxlength="500"
          placeholder="Tell us about yourself..."
          aria-describedby="bio-count"></textarea>
<span id="bio-count" aria-live="polite">0/500 characters</span>
```

---

## 10.6 `<select>` — Dropdowns

```html
<!-- Simple select -->
<label for="country">Country</label>
<select id="country" name="country" required>
  <option value="">— Select a country —</option>
  <option value="IN" selected>India</option>
  <option value="US">United States</option>
  <option value="GB">United Kingdom</option>
</select>

<!-- Grouped options (optgroup) -->
<select id="timezone" name="timezone">
  <optgroup label="India">
    <option value="Asia/Kolkata">IST (UTC+5:30)</option>
  </optgroup>
  <optgroup label="United States">
    <option value="America/New_York">EST (UTC-5)</option>
    <option value="America/Los_Angeles">PST (UTC-8)</option>
  </optgroup>
</select>

<!-- Multiple selection -->
<select id="skills" name="skills" multiple size="5">
  <option value="java">Java</option>
  <option value="spring">Spring Boot</option>
  <option value="react">React</option>
  <option value="sql">SQL</option>
  <option value="kafka">Apache Kafka</option>
</select>
```

---

## 10.7 `<fieldset>` and `<legend>`

Group related form fields:

```html
<fieldset>
  <legend>Personal Information</legend>
  
  <div class="field-group">
    <label for="firstName">First Name</label>
    <input type="text" id="firstName" name="firstName" required>
  </div>
  <div class="field-group">
    <label for="lastName">Last Name</label>
    <input type="text" id="lastName" name="lastName" required>
  </div>
</fieldset>

<fieldset>
  <legend>Account Security</legend>
  
  <div class="field-group">
    <label for="password">Password</label>
    <input type="password" id="password" name="password" required minlength="8">
  </div>
  <div class="field-group">
    <label for="confirmPassword">Confirm Password</label>
    <input type="password" id="confirmPassword" name="confirmPassword" required>
  </div>
</fieldset>
```

`<fieldset>` + `<legend>` is **required** for radio button groups and checkbox groups. Screen readers announce the legend before each input in the group.

---

## 10.8 HTML5 Validation Attributes

```html
<input type="text" 
       required              <!-- must not be empty -->
       minlength="2"         <!-- minimum character count -->
       maxlength="100"       <!-- maximum character count -->
       pattern="[A-Za-z ]+" <!-- regex pattern -->
       title="Only letters and spaces allowed">  <!-- custom error message hint -->

<input type="number"
       min="1"
       max="100"
       step="0.01">

<input type="email"
       required>  <!-- validates email format automatically -->
```

### Custom Validation Messages

```javascript
const input = document.getElementById('username');
input.addEventListener('invalid', (e) => {
  if (input.validity.valueMissing) {
    input.setCustomValidity('Please enter your username.');
  } else if (input.validity.tooShort) {
    input.setCustomValidity(`Username must be at least ${input.minLength} characters.`);
  }
});
input.addEventListener('input', () => input.setCustomValidity(''));
```

---

## 10.9 CSRF Protection in Spring Boot

Cross-Site Request Forgery (CSRF) attacks trick users into submitting requests to your app without their knowledge. Spring Security generates a CSRF token that must be included in all state-changing requests.

### Thymeleaf + Spring Security (auto-injection)

When Spring Security and Thymeleaf are both on the classpath, CSRF tokens are **automatically added** to forms using `th:action`:

```html
<!-- Use th:action, NOT action — Thymeleaf adds CSRF token automatically -->
<form th:action="@{/users/create}" method="post">
  <!-- Spring Security + Thymeleaf injects:
       <input type="hidden" name="_csrf" value="abc123..."> -->
  ...
</form>
```

### Manual CSRF (for AJAX requests)

```html
<!-- Include in head for JavaScript to read -->
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
```

```javascript
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

fetch('/api/users', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    [csrfHeader]: csrfToken  // e.g., 'X-CSRF-TOKEN': 'abc123...'
  },
  body: JSON.stringify(userData)
});
```

---

## 10.10 A Complete Registration Form

```html
<form th:action="@{/register}" th:object="${registrationForm}" method="post" novalidate>
  
  <fieldset>
    <legend>Personal Details</legend>
    
    <div class="field" th:classappend="${#fields.hasErrors('firstName')} ? 'has-error' : ''">
      <label for="firstName">First Name <span aria-hidden="true">*</span></label>
      <input type="text" id="firstName" th:field="*{firstName}"
             required minlength="2" maxlength="50"
             aria-required="true"
             th:attr="aria-invalid=${#fields.hasErrors('firstName')}">
      <span class="error" th:if="${#fields.hasErrors('firstName')}" 
            th:errors="*{firstName}" role="alert">Error</span>
    </div>

    <div class="field" th:classappend="${#fields.hasErrors('email')} ? 'has-error' : ''">
      <label for="email">Email Address <span aria-hidden="true">*</span></label>
      <input type="email" id="email" th:field="*{email}"
             required autocomplete="email"
             aria-required="true"
             th:attr="aria-invalid=${#fields.hasErrors('email')}">
      <span class="error" th:if="${#fields.hasErrors('email')}" 
            th:errors="*{email}" role="alert">Error</span>
    </div>
  </fieldset>

  <fieldset>
    <legend>Account Security</legend>
    
    <div class="field">
      <label for="password">Password <span aria-hidden="true">*</span></label>
      <input type="password" id="password" th:field="*{password}"
             required minlength="8" autocomplete="new-password"
             aria-required="true"
             aria-describedby="password-hint">
      <p id="password-hint" class="hint">
        At least 8 characters, including a number and a special character.
      </p>
    </div>
  </fieldset>

  <div class="field">
    <label>
      <input type="checkbox" th:field="*{agreeTerms}" value="true" required>
      I agree to the <a href="/terms" target="_blank" rel="noopener">Terms of Service</a>
    </label>
  </div>

  <div class="form-actions">
    <button type="submit" class="btn btn-primary">Create Account</button>
    <a th:href="@{/login}" class="btn btn-secondary">Already have an account?</a>
  </div>

</form>
```

---

## Key Takeaways

- Every input needs a `<label>` — use `for`/`id` pairing.
- Use `<fieldset>` + `<legend>` for radio/checkbox groups and logical sections.
- Use HTML5 validation attributes as a first line of defence — always validate on the server too.
- Use `th:action` in Thymeleaf forms for automatic CSRF token injection.
- Pass CSRF tokens in AJAX requests via meta tags and request headers.
- `enctype="multipart/form-data"` is required for file uploads.

---

## Self-Check Questions

1. What is the difference between `<input type="text">` and `<textarea>`?
2. Why must radio button groups always be inside a `<fieldset>` with a `<legend>`?
3. What is a CSRF attack and how does Spring Security + Thymeleaf prevent it?
4. What `enctype` is required for a form that uploads a file?
5. How would you display Spring MVC validation errors next to the relevant form fields using Thymeleaf?
