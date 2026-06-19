# TypeScript — Complete Reference

## 01. Getting Started

```bash
npm install -D typescript ts-node @types/node
npx tsc --init      # creates tsconfig.json
```

```json
// tsconfig.json — recommended for Node.js
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "NodeNext",
    "moduleResolution": "NodeNext",
    "outDir": "./dist",
    "rootDir": "./src",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "declaration": true,
    "sourceMap": true,
    "resolveJsonModule": true,
    "forceConsistentCasingInFileNames": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true
  },
  "include": ["src/**/*"],
  "exclude": ["node_modules", "dist", "**/*.test.ts"]
}
```

---

## 02. TypeScript Basics & Types

```typescript
// Primitive types
let name: string = 'Alice';
let age: number = 30;
let isAdmin: boolean = true;
let score: number | undefined = undefined;

// Arrays
let numbers: number[] = [1, 2, 3];
let names: Array<string> = ['Alice', 'Bob'];

// Tuple: fixed-length, fixed-type array
let point: [number, number] = [10, 20];
let user: [string, number, boolean] = ['Alice', 30, true];
const [username, userAge] = user;   // destructuring

// Enums
enum Direction { Up, Down, Left, Right }
enum Status { Active = 'ACTIVE', Inactive = 'INACTIVE', Pending = 'PENDING' }
const dir: Direction = Direction.Up;   // 0
const status: Status = Status.Active;  // 'ACTIVE'

// const enum (inlined at compile time — no JS enum object)
const enum HttpMethod { GET = 'GET', POST = 'POST', PUT = 'PUT', DELETE = 'DELETE' }

// any: opt-out of type checking (avoid!)
let data: any = JSON.parse(jsonString);

// unknown: safe version of any — must narrow before use
let raw: unknown = fetchData();
if (typeof raw === 'string') raw.toUpperCase();  // ✅ narrowed to string

// never: function that never returns (throws or infinite loop)
function fail(msg: string): never {
  throw new Error(msg);
}

// void: function that returns nothing meaningful
function log(msg: string): void {
  console.log(msg);
}

// Type assertions
const input = document.getElementById('email') as HTMLInputElement;
const len = (someValue as string).length;
```

---

## 03. Interfaces and Type Aliases

```typescript
// Interface — defines object shape
interface User {
  readonly id: number;         // readonly: can't reassign
  name: string;
  email: string;
  age?: number;                // optional
  address?: {
    city: string;
    country: string;
  };
}

// Extending interfaces
interface AdminUser extends User {
  role: 'admin';
  permissions: string[];
}

// Type alias — more flexible than interface
type StringOrNumber = string | number;
type UserOrAdmin = User | AdminUser;
type ID = string | number;
type Callback<T> = (error: Error | null, result: T) => void;

// Type alias for objects (similar to interface)
type Point = { x: number; y: number };
type Point3D = Point & { z: number };   // intersection type (like extends)

// Interface vs Type alias:
// - Interface: can be extended (declaration merging), slightly better error messages
// - Type: can define unions, mapped types, conditional types
// Rule: Use 'interface' for object shapes, 'type' for everything else
```

---

## 04. Classes & Access Modifiers

```typescript
class Animal {
  readonly id: number;
  public name: string;
  protected species: string;
  private #sound: string;     // true private (ES2022 field)

  constructor(name: string, species: string) {
    this.id = Math.random();
    this.name = name;
    this.species = species;
    this.#sound = 'generic';
  }

  speak(): string {
    return `${this.name} says ${this.#sound}`;
  }

  get info(): string { return `${this.name} (${this.species})`; }
  set sound(value: string) { this.#sound = value; }

  static create(name: string, species: string): Animal {
    return new Animal(name, species);
  }
}

// Parameter properties shorthand
class Dog extends Animal {
  constructor(
    name: string,
    public breed: string,       // declares + assigns in one step
    private readonly age: number
  ) {
    super(name, 'dog');
  }

  speak(): string {
    return `${this.name} barks!`;
  }
}

// Implementing an interface
interface Serializable {
  toJSON(): object;
  toString(): string;
}

class User implements Serializable {
  constructor(public name: string, public email: string) {}
  toJSON() { return { name: this.name, email: this.email }; }
  toString() { return `${this.name} <${this.email}>`; }
}

// Abstract class
abstract class Shape {
  abstract area(): number;      // must be implemented by subclasses
  abstract perimeter(): number;

  describe(): string {
    return `Area: ${this.area()}, Perimeter: ${this.perimeter()}`;
  }
}

class Circle extends Shape {
  constructor(private radius: number) { super(); }
  area(): number { return Math.PI * this.radius ** 2; }
  perimeter(): number { return 2 * Math.PI * this.radius; }
}
```

---

## 05. Advanced Types

```typescript
// Union types
type StringOrNumber = string | number;
function format(val: StringOrNumber): string {
  return typeof val === 'string' ? val : val.toFixed(2);
}

// Literal types
type Direction = 'north' | 'south' | 'east' | 'west';
type DiceRoll = 1 | 2 | 3 | 4 | 5 | 6;
type HttpStatus = 200 | 201 | 400 | 401 | 403 | 404 | 500;

// Discriminated union (type-safe sum type)
type Shape =
  | { kind: 'circle'; radius: number }
  | { kind: 'square'; side: number }
  | { kind: 'rectangle'; width: number; height: number };

function area(shape: Shape): number {
  switch (shape.kind) {
    case 'circle':    return Math.PI * shape.radius ** 2;
    case 'square':    return shape.side ** 2;
    case 'rectangle': return shape.width * shape.height;
  }
}

// Type guards
function isString(val: unknown): val is string {
  return typeof val === 'string';
}

function isUser(val: unknown): val is User {
  return typeof val === 'object' && val !== null && 'name' in val && 'email' in val;
}

// Utility types (built-in)
type PartialUser = Partial<User>;             // all fields optional
type RequiredUser = Required<User>;           // all fields required
type ReadonlyUser = Readonly<User>;           // all fields readonly
type UserName = Pick<User, 'name' | 'email'>; // pick subset of fields
type WithoutId = Omit<User, 'id'>;            // omit fields
type StringValues = Record<string, string>;   // { [key: string]: string }
type StringOrNull = NonNullable<string | null | undefined>;  // string

// Mapped types
type Optional<T> = { [K in keyof T]?: T[K] };
type Nullable<T> = { [K in keyof T]: T[K] | null };

// Conditional types
type IsString<T> = T extends string ? 'yes' : 'no';
type Flatten<T> = T extends Array<infer U> ? U : T;
// Flatten<number[]> = number
// Flatten<string>   = string

// Template literal types (TS 4.1+)
type EventName = `on${Capitalize<string>}`;
type CSSProperty = `--${string}`;
```

---

## 06. Generics

```typescript
// Generic function
function identity<T>(arg: T): T { return arg; }
identity<string>('hello')   // string
identity(42)                // inferred: number

// Generic with constraints
function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
  return obj[key];
}
getProperty({ name: 'Alice', age: 30 }, 'name')   // string
getProperty({ name: 'Alice', age: 30 }, 'missing') // ❌ Error!

// Generic class
class Stack<T> {
  private items: T[] = [];
  push(item: T): void { this.items.push(item); }
  pop(): T | undefined { return this.items.pop(); }
  peek(): T | undefined { return this.items[this.items.length - 1]; }
  get size(): number { return this.items.length; }
}

const numStack = new Stack<number>();
numStack.push(1);
numStack.push('x');  // ❌ Error: string not assignable to number

// Generic async function
async function fetchData<T>(url: string): Promise<T> {
  const response = await fetch(url);
  if (!response.ok) throw new Error(`HTTP ${response.status}`);
  return response.json() as Promise<T>;
}

interface User { id: number; name: string; }
const user = await fetchData<User>('/api/users/1');
user.name   // ✅ TypeScript knows the type
```

---

## 07. Decorators

```typescript
// tsconfig.json: "experimentalDecorators": true

// Class decorator
function Singleton<T extends { new(...args: any[]): {} }>(constructor: T) {
  let instance: T;
  return class extends constructor {
    constructor(...args: any[]) {
      if (instance) return instance;
      super(...args);
      instance = this as any;
    }
  };
}

// Method decorator
function log(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
  const original = descriptor.value;
  descriptor.value = function (...args: any[]) {
    console.log(`Calling ${propertyKey} with`, args);
    const result = original.apply(this, args);
    console.log(`${propertyKey} returned`, result);
    return result;
  };
  return descriptor;
}

// Property decorator
function required(target: any, propertyKey: string) {
  // uses Object.defineProperty to intercept get/set
}

// Usage
@Singleton
class Database {
  constructor(private url: string) {}
  query(sql: string) { return []; }
}

class UserService {
  @log
  findById(id: number): User | null {
    return null;
  }
}
```

---

## 08–12. Webpack with TypeScript & Projects

```bash
npm install -D webpack webpack-cli ts-loader typescript
```

```javascript
// webpack.config.js
const path = require('path');

module.exports = {
  mode: 'production',
  entry: './src/app.ts',
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'dist'),
    clean: true
  },
  module: {
    rules: [{
      test: /\.tsx?$/,
      use: 'ts-loader',
      exclude: /node_modules/
    }]
  },
  resolve: { extensions: ['.tsx', '.ts', '.js'] }
};
```

```bash
npm run build   # "webpack"
npm start       # serve dist/
```

### Project: Location Finder App with Google Maps

```typescript
// src/app.ts
interface Coordinates {
  lat: number;
  lng: number;
}

class LocationApp {
  private map: google.maps.Map;

  constructor(private apiKey: string) {
    this.map = this.initMap();
  }

  private initMap(): google.maps.Map {
    return new google.maps.Map(
      document.getElementById('map') as HTMLElement,
      { zoom: 13, center: { lat: 12.9716, lng: 77.5946 } }  // Bengaluru
    );
  }

  async geocode(address: string): Promise<Coordinates> {
    const params = new URLSearchParams({ address, key: this.apiKey });
    const res = await fetch(`https://maps.googleapis.com/maps/api/geocode/json?${params}`);
    const data = await res.json();

    if (data.status !== 'OK') throw new Error(`Geocoding failed: ${data.status}`);

    const { lat, lng } = data.results[0].geometry.location;
    return { lat, lng };
  }

  async searchAndShow(address: string): Promise<void> {
    const coords = await this.geocode(address);
    this.map.setCenter(coords);
    new google.maps.Marker({ position: coords, map: this.map, title: address });
  }
}

const app = new LocationApp(process.env.MAPS_API_KEY!);

document.getElementById('searchForm')!.addEventListener('submit', async (e) => {
  e.preventDefault();
  const input = document.getElementById('addressInput') as HTMLInputElement;
  try {
    await app.searchAndShow(input.value);
  } catch (err) {
    alert(err instanceof Error ? err.message : 'Search failed');
  }
});
```
