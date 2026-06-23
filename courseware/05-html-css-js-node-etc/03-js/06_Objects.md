# JavaScript Courseware — Module 06: Objects

---

## 6.1 Object Literals

An object is a collection of **key-value pairs** (properties). Keys are strings (or Symbols); values can be anything.

```javascript
const user = {
  id: 1,
  name: 'Alice Sharma',
  email: 'alice@example.com',
  isActive: true,
  address: {                    // nested object
    city: 'Bengaluru',
    state: 'Karnataka'
  },
  roles: ['admin', 'user'],     // array property
  greet() {                     // method shorthand (preferred)
    return `Hello, I'm ${this.name}`;
  }
};

// Accessing properties
user.name               // 'Alice Sharma'  (dot notation — preferred)
user['email']           // 'alice@example.com'  (bracket notation — use for dynamic keys)
user.address.city       // 'Bengaluru'
user.roles[0]           // 'admin'
user.greet()            // "Hello, I'm Alice Sharma"

// Dynamic key access
const key = 'name';
user[key]               // 'Alice Sharma'

// Adding / modifying properties
user.phone = '+91 9876543210';
user.name = 'Alice Iyer';

// Deleting properties
delete user.phone;

// Checking property existence
'email' in user                          // true
'phone' in user                          // false
Object.prototype.hasOwnProperty.call(user, 'email')  // true (own, not inherited)
user.hasOwnProperty('toString')          // false (inherited from Object.prototype)
```

---

## 6.2 Shorthand Syntax

```javascript
const name = 'Alice';
const age = 30;
const city = 'Bengaluru';

// Old way
const user1 = { name: name, age: age, city: city };

// ES6 shorthand — when variable name = key name
const user2 = { name, age, city };   // identical

// Computed property names
const field = 'email';
const user3 = {
  [field]: 'alice@example.com',          // key = 'email'
  [`${field}_verified`]: true            // key = 'email_verified'
};

// Method shorthand
const calc = {
  add(a, b) { return a + b; },          // ✅ preferred
  subtract: function(a, b) { return a - b; }  // works but verbose
};
```

---

## 6.3 Destructuring Objects

Extract properties into named variables:

```javascript
const user = { id: 1, name: 'Alice', role: 'admin', city: 'Bengaluru' };

// Basic destructuring
const { name, role } = user;
console.log(name);   // 'Alice'

// Rename while destructuring
const { name: fullName, role: userRole } = user;
console.log(fullName);   // 'Alice'

// Default values (used when property is undefined)
const { name: n, theme = 'light' } = user;
console.log(theme);   // 'light' (user.theme is undefined)

// Nested destructuring
const user2 = { id: 1, address: { city: 'Mumbai', state: 'MH' } };
const { address: { city, state } } = user2;
console.log(city);   // 'Mumbai'

// Rest — collect remaining properties
const { id, name: userName, ...rest } = user;
console.log(rest);   // { role: 'admin', city: 'Bengaluru' }

// In function parameters — named params pattern
function displayUser({ name, email, role = 'user' }) {
  return `${name} (${email}) — ${role}`;
}
displayUser({ name: 'Alice', email: 'a@b.com' });

// Practical: extracting from API response
const { data: { users, total }, meta: { page } } = apiResponse;
```

---

## 6.4 `Object.*` Methods — The Complete Set

These are essential tools used constantly in real projects.

### Iterating Properties

```javascript
const user = { name: 'Alice', age: 30, city: 'NYC', role: 'admin' };

Object.keys(user)     // ['name', 'age', 'city', 'role']   — array of keys
Object.values(user)   // ['Alice', 30, 'NYC', 'admin']     — array of values
Object.entries(user)  // [['name','Alice'], ['age',30], ...]  — array of [key,value] pairs

// Iterate properties safely (better than for...in)
for (const [key, value] of Object.entries(user)) {
  console.log(`${key}: ${value}`);
}

// Map over properties
const upperUser = Object.fromEntries(
  Object.entries(user).map(([k, v]) => [k, typeof v === 'string' ? v.toUpperCase() : v])
);
// { name: 'ALICE', age: 30, city: 'NYC', role: 'ADMIN' }

// Filter properties
const stringsOnly = Object.fromEntries(
  Object.entries(user).filter(([, v]) => typeof v === 'string')
);
// { name: 'Alice', city: 'NYC', role: 'admin' }
```

### Copying and Merging

```javascript
const defaults = { theme: 'light', lang: 'en', fontSize: 14 };
const userPrefs = { theme: 'dark', fontSize: 16 };

// Object.assign — merges into first arg (mutates target!)
const config1 = Object.assign({}, defaults, userPrefs);
// { theme: 'dark', lang: 'en', fontSize: 16 }

// Spread — preferred, more readable
const config2 = { ...defaults, ...userPrefs };
// { theme: 'dark', lang: 'en', fontSize: 16 }

// Both are SHALLOW copies — nested objects are still references
const original = { name: 'Alice', address: { city: 'NYC' } };
const copy = { ...original };
copy.address.city = 'LA';
original.address.city;   // 'LA' — mutated! shared reference

// Deep copy — use structuredClone (ES2022)
const deepCopy = structuredClone(original);
deepCopy.address.city = 'LA';
original.address.city;   // 'NYC' ✅ — independent
```

### Freezing and Sealing

```javascript
// Object.freeze — makes object immutable (shallow)
const CONFIG = Object.freeze({ db: 'mongodb://localhost', port: 3000 });
CONFIG.port = 8080;   // silently fails (throws in strict mode)
CONFIG.port;          // still 3000

// Object.seal — can modify existing props, but can't add/delete
const obj = Object.seal({ name: 'Alice', age: 30 });
obj.name = 'Bob';     // ✅ modifying existing is OK
obj.email = 'x@x.com'; // ❌ adding new property fails
delete obj.age;       // ❌ deleting fails

// Check status
Object.isFrozen(CONFIG)   // true
Object.isSealed(obj)      // true
```

### Object Creation

```javascript
// Object.create — create with explicit prototype
const proto = {
  greet() { return `Hello, ${this.name}`; }
};
const alice = Object.create(proto);
alice.name = 'Alice';
alice.greet();   // 'Hello, Alice' — inherited from proto

// Check prototype
Object.getPrototypeOf(alice) === proto  // true

// Check own vs inherited
alice.hasOwnProperty('name')   // true (own)
alice.hasOwnProperty('greet')  // false (inherited)

// Object.fromEntries — convert array of pairs to object
const entries = [['name', 'Alice'], ['age', 30]];
Object.fromEntries(entries)  // { name: 'Alice', age: 30 }

// Very useful with Map
const map = new Map([['host', 'localhost'], ['port', 3000]]);
Object.fromEntries(map)  // { host: 'localhost', port: 3000 }
```

### Property Descriptors (Advanced but Important)

```javascript
// Every property has descriptor metadata
Object.getOwnPropertyDescriptor(user, 'name');
// { value: 'Alice', writable: true, enumerable: true, configurable: true }

// Define properties with specific descriptors
Object.defineProperty(obj, 'id', {
  value: 42,
  writable: false,      // cannot be changed
  enumerable: true,     // shows up in for...in, Object.keys
  configurable: false   // cannot be deleted or redefined
});

// Define multiple at once
Object.defineProperties(obj, {
  name: { value: 'Alice', writable: true, enumerable: true, configurable: true },
  id:   { value: 1,       writable: false, enumerable: true, configurable: false }
});
```

---

## 6.5 Spread Operator with Objects

```javascript
// Shallow copy with modifications (immutable update pattern)
const user = { id: 1, name: 'Alice', age: 30 };
const updatedUser = { ...user, age: 31 };   // doesn't mutate original

// Remove a property (via rest destructuring)
const { password, ...safeUser } = user;
// safeUser has everything EXCEPT password — use before sending to client

// Conditional properties
const isAdmin = true;
const payload = {
  name: user.name,
  email: user.email,
  ...(isAdmin && { adminToken: 'abc123', permissions: ['read', 'write'] })
};
// adminToken and permissions only included if isAdmin is true

// Merge with override (last key wins)
const merged = { ...obj1, ...obj2 };  // obj2 values override obj1 for same keys
```

---

## 6.6 Classes — OOP in JavaScript

Classes are **syntactic sugar** over prototypes — they compile to the same prototype-based mechanism, but the syntax is familiar to Java developers.

```javascript
class Animal {
  // Class fields (ES2022)
  #sound = 'generic sound';  // private field (# prefix — truly private)
  static count = 0;          // static field

  constructor(name, species) {
    this.name = name;
    this.species = species;
    Animal.count++;
  }

  // Instance method
  speak() {
    return `${this.name} says: ${this.#sound}`;
  }

  // Getter
  get info() {
    return `${this.name} (${this.species})`;
  }

  // Setter
  set sound(value) {
    if (typeof value !== 'string') throw new TypeError('Sound must be string');
    this.#sound = value;
  }

  // Static method
  static getCount() {
    return Animal.count;
  }

  toString() {
    return this.info;
  }
}

// Inheritance
class Dog extends Animal {
  #tricks = [];

  constructor(name) {
    super(name, 'dog');   // must call super() before accessing this
  }

  speak() {
    return `${this.name} barks: Woof!`;  // overrides Animal.speak
  }

  learn(trick) {
    this.#tricks.push(trick);
    return this;   // method chaining!
  }

  showTricks() {
    return this.#tricks.length
      ? `${this.name} knows: ${this.#tricks.join(', ')}`
      : `${this.name} knows no tricks yet`;
  }
}

const rex = new Dog('Rex');
rex.learn('sit').learn('shake').learn('roll over');  // method chaining
rex.speak()       // 'Rex barks: Woof!'
rex.showTricks()  // 'Rex knows: sit, shake, roll over'
rex instanceof Dog    // true
rex instanceof Animal // true
Animal.getCount()     // 1

// Accessing private fields from outside — not possible
rex.#tricks;   // ❌ SyntaxError — private field
```

### Java vs JavaScript OOP Comparison

| Java | JavaScript |
|---|---|
| `private String name;` | `#name` (truly private) or `this.name` (convention) |
| `@Override` annotation | Just redefine the method — no annotation needed |
| `super.method()` | `super.method()` — same! |
| `implements Interface` | No interface keyword — use TypeScript |
| `abstract class` | No keyword — use pattern or TypeScript |
| `final` method/class | No keyword — use TypeScript `readonly` |

---

## 6.7 Prototypes and Prototype Chain

Understanding this helps you understand HOW classes work under the hood:

```javascript
// Every object has a prototype (a hidden [[Prototype]] link)
const arr = [1, 2, 3];
// arr → Array.prototype → Object.prototype → null
arr.push    // found at Array.prototype.push
arr.toString  // found at Object.prototype.toString

// Classes create prototype chains:
class A { method() {} }
class B extends A {}
const b = new B();
// b → B.prototype → A.prototype → Object.prototype → null

// Property lookup: check own → walk up chain → undefined if not found
b.method()    // found at A.prototype
```

---

## 6.8 JSON: Serialisation and Deserialisation

Critical for every API-based application:

```javascript
const user = { id: 1, name: 'Alice', createdAt: new Date(), roles: ['admin'] };

// Object → JSON string
const json = JSON.stringify(user);
// '{"id":1,"name":"Alice","createdAt":"2024-11-15T10:30:00.000Z","roles":["admin"]}'

// Pretty-print for debugging
console.log(JSON.stringify(user, null, 2));

// Selective serialisation — only include specific keys
const safeJson = JSON.stringify(user, ['id', 'name']);  // omits createdAt, roles

// Custom replacer function
const noNulls = JSON.stringify(data, (key, value) => value === null ? undefined : value);

// JSON string → Object
const parsed = JSON.parse(json);
parsed.name   // 'Alice'

// IMPORTANT: Date becomes a string, NOT a Date object
typeof parsed.createdAt  // 'string' — need to manually convert back!
new Date(parsed.createdAt)  // convert back to Date

// Custom reviver
const parsed2 = JSON.parse(json, (key, value) => {
  if (key === 'createdAt') return new Date(value);
  return value;
});
parsed2.createdAt instanceof Date  // true ✅

// Parse safely (network data can be malformed)
function safeParseJSON(text) {
  try {
    return { data: JSON.parse(text), error: null };
  } catch (err) {
    return { data: null, error: err.message };
  }
}
const { data, error } = safeParseJSON(responseText);
if (error) { /* handle */ }
```

---

## 6.9 Optional Chaining with Objects

```javascript
const response = {
  data: {
    user: {
      profile: { avatar: '/images/alice.jpg' }
    }
  }
};

// Safe deep access — never crashes
const avatar = response?.data?.user?.profile?.avatar ?? '/images/default.jpg';

// Safe method call
const formatted = user?.getFormattedAddress?.() ?? 'Address not available';

// Safe array access
const firstRole = user?.roles?.[0] ?? 'No role';
```

---

## Key Takeaways

- Objects are dictionaries: key-value pairs with optional prototype chain.
- `Object.keys/values/entries` + `Object.fromEntries` are your main tools for transforming object data.
- Spread `{...obj}` creates **shallow** copies — use `structuredClone()` for deep copies.
- `Object.freeze` makes objects immutable (shallow). Use for configuration constants.
- Classes are syntactic sugar over prototypes. `#field` is truly private.
- `JSON.stringify/parse` for serialisation — note that `Date` loses its type through JSON.

---

## Self-Check Questions

1. What is the difference between `const { name: fullName } = user` and `const { name } = user`?
2. What does `const { password, ...rest } = user` do? When is this pattern useful?
3. What is the difference between `Object.assign` / spread and `structuredClone`? Give an example where spread causes a bug.
4. What does `Object.freeze` do? Is it deep or shallow?
5. Write a `pick(obj, keys)` function that returns only the specified keys. (Hint: `Object.fromEntries` + `Object.entries`)
6. Why does `typeof parsed.createdAt` return `'string'` after `JSON.parse`?
