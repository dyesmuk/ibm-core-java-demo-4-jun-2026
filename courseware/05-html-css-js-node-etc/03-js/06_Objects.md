# JS Basics — Module 06: Objects

## 6.1 Object Literals

An object is a collection of **key-value pairs** (properties). Keys are strings (or Symbols); values can be any type.

```javascript
// Object literal
const user = {
  id: 1,
  name: 'Alice Sharma',
  email: 'alice@example.com',
  isActive: true,
  address: {                 // nested object
    city: 'Bengaluru',
    state: 'Karnataka'
  },
  roles: ['admin', 'user'],  // array property
  greet() {                  // method shorthand
    return `Hello, I'm ${this.name}`;
  }
};

// Accessing properties
user.name               // 'Alice Sharma'  (dot notation)
user['email']           // 'alice@example.com'  (bracket notation)
user.address.city       // 'Bengaluru'
user.roles[0]           // 'admin'
user.greet()            // 'Hello, I\'m Alice Sharma'

// Dynamic key access
const key = 'name';
user[key]               // 'Alice Sharma'

// Adding / modifying properties
user.phone = '+91 9876543210';
user.name = 'Alice Iyer';

// Deleting properties
delete user.phone;

// Checking property existence
'email' in user         // true
'phone' in user         // false
user.hasOwnProperty('email')  // true (won't check prototype)
user.hasOwnProperty('toString')  // false (inherited from Object.prototype)
```

---

## 6.2 Shorthand Property Names

```javascript
const name = 'Alice';
const age = 30;
const city = 'Bengaluru';

// Old way
const user1 = { name: name, age: age, city: city };

// ES6 shorthand: when key = variable name
const user2 = { name, age, city };  // same thing

// Computed property names
const field = 'email';
const user3 = {
  [field]: 'alice@example.com',           // key = 'email'
  [`${field}_verified`]: true             // key = 'email_verified'
};
```

---

## 6.3 Destructuring Objects

Extract properties into variables:

```javascript
const user = { id: 1, name: 'Alice', role: 'admin', city: 'Bengaluru' };

// Basic destructuring
const { name, role } = user;
console.log(name);   // 'Alice'
console.log(role);   // 'admin'

// Rename while destructuring
const { name: fullName, role: userRole } = user;
console.log(fullName);   // 'Alice'

// Default values
const { name, theme = 'light' } = user;
console.log(theme);   // 'light' (user.theme is undefined)

// Nested destructuring
const { address: { city, state = 'Unknown' } } = user;
// Note: address must exist or this throws

// Rest in destructuring
const { id, name, ...rest } = user;
console.log(rest);   // { role: 'admin', city: 'Bengaluru' }

// In function parameters (very common in Node.js/React)
function displayUser({ name, email, role = 'user' }) {
  return `${name} (${email}) — ${role}`;
}

// Practical: extracting from API response
const { data: { users, total }, meta: { page } } = apiResponse;
```

---

## 6.4 Object Methods: `Object.*`

```javascript
const user = { name: 'Alice', age: 30, city: 'NYC' };

// Keys, values, entries
Object.keys(user)     // ['name', 'age', 'city']
Object.values(user)   // ['Alice', 30, 'NYC']
Object.entries(user)  // [['name','Alice'], ['age',30], ['city','NYC']]

// Iterate
for (const [key, value] of Object.entries(user)) {
  console.log(`${key}: ${value}`);
}

// Convert entries back to object
const filtered = Object.fromEntries(
  Object.entries(user).filter(([key]) => key !== 'age')
);
// { name: 'Alice', city: 'NYC' }

// Shallow copy
const copy = Object.assign({}, user);
const copy2 = { ...user };             // preferred: spread

// Deep freeze (immutable object — shallow only)
const config = Object.freeze({ db: 'mongodb://localhost', port: 3000 });
config.port = 8080;  // silently fails (or throws in strict mode)

// Check if empty
Object.keys(obj).length === 0

// Object.create: set prototype explicitly
const proto = { greet() { return `Hello, ${this.name}`; } };
const alice = Object.create(proto);
alice.name = 'Alice';
alice.greet();   // 'Hello, Alice'

// Check descriptors
Object.getOwnPropertyDescriptor(user, 'name');
// { value: 'Alice', writable: true, enumerable: true, configurable: true }
```

---

## 6.5 Prototypes and Prototype Chain

Every JavaScript object has a hidden `[[Prototype]]` link to another object. This is how inheritance works:

```javascript
const animal = {
  describe() {
    return `I am ${this.name}, a ${this.species}`;
  }
};

const dog = Object.create(animal);  // dog's prototype = animal
dog.name = 'Rex';
dog.species = 'dog';
dog.describe();  // 'I am Rex, a dog' — inherited from prototype

// Prototype chain for arrays:
const arr = [1, 2, 3];
// arr → Array.prototype → Object.prototype → null
arr.push    // Array.prototype.push
arr.toString  // Object.prototype.toString
```

---

## 6.6 Classes (ES6+)

Classes are **syntactic sugar** over prototypes — they compile down to the same prototype-based mechanism. If you know Java classes, the syntax will feel familiar:

```javascript
class Animal {
  // Class field (ES2022 — no need for constructor assignment)
  #sound = 'generic sound';   // private field (prefix #)
  static count = 0;           // static field

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
    if (typeof value !== 'string') throw new TypeError('Sound must be a string');
    this.#sound = value;
  }

  // Static method
  static getCount() {
    return Animal.count;
  }

  // toString override
  toString() {
    return this.info;
  }
}

class Dog extends Animal {
  #tricks = [];

  constructor(name) {
    super(name, 'dog');      // must call super() first
    this.#tricks = [];
  }

  speak() {
    return `${this.name} barks: Woof!`;
  }

  learn(trick) {
    this.#tricks.push(trick);
    return this;             // method chaining
  }

  showTricks() {
    return `${this.name} knows: ${this.#tricks.join(', ')}`;
  }
}

const rex = new Dog('Rex');
rex.learn('sit').learn('shake').learn('roll over');
rex.speak()       // 'Rex barks: Woof!'
rex.showTricks()  // 'Rex knows: sit, shake, roll over'
rex instanceof Dog     // true
rex instanceof Animal  // true
Animal.getCount()      // 1

// Comparison with Java
// Java:  private String name;  →  JS: this.name (by convention) or #name (truly private)
// Java:  @Override              →  JS: just redefine the method
// Java:  super.method()         →  JS: super.method()  (same)
// Java:  implements Interface   →  JS: no interface keyword (use TypeScript)
```

---

## 6.7 The Spread Operator with Objects

```javascript
// Merge objects (last key wins)
const defaults = { theme: 'light', lang: 'en', fontSize: 14 };
const userPrefs = { theme: 'dark', fontSize: 16 };
const config = { ...defaults, ...userPrefs };
// { theme: 'dark', lang: 'en', fontSize: 16 }

// Shallow copy with override
const updatedUser = { ...user, name: 'Bob', updatedAt: new Date() };

// Remove a property (rest destructuring)
const { password, ...safeUser } = user;
// safeUser has everything except password — useful before sending to client

// Add conditional properties
const payload = {
  name: user.name,
  email: user.email,
  ...(user.isAdmin && { adminToken: generateToken() })  // only if admin
};
```

---

## 6.8 Optional Chaining and Nullish Coalescing with Objects

```javascript
const response = {
  data: {
    user: {
      profile: {
        avatar: '/images/alice.jpg'
      }
    }
  }
};

// Safe deep access
const avatar = response?.data?.user?.profile?.avatar ?? '/images/default.jpg';

// Safe method call
const formatted = user?.getFormattedAddress?.() ?? 'Address not available';

// Safe array access
const firstRole = user?.roles?.[0] ?? 'No role';
```

---

## 6.9 JSON: Serialisation and Deserialisation

```javascript
const user = { id: 1, name: 'Alice', createdAt: new Date() };

// Object → JSON string
const json = JSON.stringify(user);
// '{"id":1,"name":"Alice","createdAt":"2024-11-15T10:30:00.000Z"}'

// Pretty-print (for logging/debugging)
console.log(JSON.stringify(user, null, 2));

// Custom replacer
const safeJson = JSON.stringify(user, ['id', 'name']);   // only include these keys
const noNullJson = JSON.stringify(data, (key, value) => value ?? undefined);

// JSON string → Object
const parsed = JSON.parse(json);
parsed.name   // 'Alice'

// Note: Date is serialised as string, NOT deserialized back to Date
typeof parsed.createdAt   // 'string' — need manual conversion

// Custom reviver
const parsed2 = JSON.parse(json, (key, value) => {
  if (key === 'createdAt') return new Date(value);
  return value;
});
parsed2.createdAt instanceof Date  // true

// Parse safely (network data can be malformed)
function safeParseJSON(text) {
  try {
    return { data: JSON.parse(text), error: null };
  } catch (err) {
    return { data: null, error: err.message };
  }
}
```

---

## 6.10 WeakMap and WeakRef (Advanced)

```javascript
// WeakMap: keys must be objects, not garbage-collected if object is GC'd
// Use case: store private data associated with an object without preventing GC

const privateData = new WeakMap();

class Node {
  constructor(value) {
    privateData.set(this, { internalId: Math.random() });
    this.value = value;
  }
  getId() {
    return privateData.get(this).internalId;
  }
}
// When the Node instance is garbage collected, privateData entry is also freed
```

---

## Key Takeaways

- Objects are just dictionaries: `{ key: value }` pairs.
- Destructuring extracts properties into variables; rest collects remaining ones.
- `Object.assign` and spread `{...obj}` create **shallow** copies — nested objects are still references.
- Classes are syntactic sugar over prototypes; `extends` sets up the prototype chain.
- Private fields (`#field`) are truly private — inaccessible from outside the class.
- `JSON.stringify`/`JSON.parse` for serialisation; `Date` loses its type through JSON.

---

## Self-Check Questions

1. What is the difference between `const { name: fullName } = user` and `const { name } = user`?
2. What does `const { password, ...rest } = user` do? When is this useful?
3. What is the prototype chain? How does `extends` in a class relate to it?
4. Why is `Object.assign({}, a, b)` a shallow copy? Give an example where this causes a bug.
5. Write a `pick(obj, keys)` function that returns a new object with only the specified keys (hint: use `Object.fromEntries` and `Object.entries`).
