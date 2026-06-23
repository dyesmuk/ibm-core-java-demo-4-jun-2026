# JavaScript Courseware — Module 07: Arrays & Array Methods

Arrays are one of the most-used data structures in JavaScript. This module covers everything — from creation to the full set of methods you'll use daily.

---

## 7.1 Creating Arrays

```javascript
// Array literal (always prefer this)
const fruits = ['apple', 'banana', 'cherry'];
const numbers = [1, 2, 3, 4, 5];
const mixed = [1, 'hello', true, null, { id: 1 }];  // any types

// Array constructor (avoid for single number arg)
new Array(3)          // [empty × 3] — NOT [3]
new Array(3).fill(0)  // [0, 0, 0]

// Array.from — create from iterable or array-like
Array.from('hello')                         // ['h','e','l','l','o']
Array.from({ length: 5 }, (_, i) => i)     // [0, 1, 2, 3, 4]
Array.from({ length: 5 }, (_, i) => i * 2) // [0, 2, 4, 6, 8]
Array.from(new Set([1, 2, 2, 3]))           // [1, 2, 3] (deduplicate)
Array.from(document.querySelectorAll('p'))  // NodeList → Array

// Spread from iterable
const chars = [...'hello'];          // ['h','e','l','l','o']
const unique = [...new Set([1,2,2,3])] // [1, 2, 3]
```

---

## 7.2 Accessing and Basic Operations

```javascript
const arr = ['a', 'b', 'c', 'd', 'e'];

arr[0]      // 'a'
arr[4]      // 'e'
arr[-1]     // undefined  (negative index doesn't work like Python!)
arr.at(-1)  // 'e'  (ES2022 — supports negative indices)
arr.at(-2)  // 'd'
arr[arr.length - 1]   // 'e'  (classic way)

arr.length  // 5
arr.length = 3;  // truncates! arr = ['a', 'b', 'c']

// Type check
Array.isArray([])    // true  ← use this
Array.isArray({})    // false
typeof []            // 'object' — not useful for array check
```

---

## 7.3 Mutating Methods (Modify the Original Array)

Know which methods mutate — important for avoiding bugs, especially in React/Angular:

```javascript
let arr = [1, 2, 3];

// Add / Remove at ends
arr.push(4, 5)    // [1,2,3,4,5] — returns new length
arr.pop()         // [1,2,3,4] — returns removed element (5)
arr.unshift(0)    // [0,1,2,3,4] — add to start, returns new length
arr.shift()       // [1,2,3,4] — remove from start, returns element (0)

// splice(start, deleteCount, ...items) — most versatile mutating method
const arr2 = [1, 2, 3, 4, 5];
arr2.splice(1, 2)          // removes 2 elements from index 1 → arr2 = [1,4,5], returns [2,3]
arr2.splice(1, 0, 10, 11)  // inserts at index 1 (no deletion) → arr2 = [1,10,11,4,5]
arr2.splice(2, 1, 99)      // replace index 2 → arr2 = [1,10,99,4,5], returns [11]

// Reverse and Sort (in-place)
[1, 2, 3].reverse()  // [3, 2, 1] — mutates

// ⚠️ sort() default is LEXICOGRAPHIC (string sort)!
[10, 9, 2, 1, 100].sort()  // [1, 10, 100, 2, 9]  ← WRONG for numbers!

// Always provide a comparator for numbers
[10, 9, 2, 1, 100].sort((a, b) => a - b)   // [1, 2, 9, 10, 100]  ascending
[10, 9, 2, 1, 100].sort((a, b) => b - a)   // [100, 10, 9, 2, 1]  descending

// Sort objects by property
const users = [
  { name: 'Carol', age: 25 },
  { name: 'Alice', age: 30 },
  { name: 'Bob', age: 28 }
];
users.sort((a, b) => a.name.localeCompare(b.name));  // alphabetical
users.sort((a, b) => a.age - b.age);                  // by age ascending

// Multi-key sort
users.sort((a, b) => {
  if (a.role !== b.role) return a.role.localeCompare(b.role);  // primary key
  return a.name.localeCompare(b.name);                         // secondary key
});

// fill
[1, 2, 3, 4, 5].fill(0)        // [0, 0, 0, 0, 0]
[1, 2, 3, 4, 5].fill(0, 2, 4)  // [1, 2, 0, 0, 5]  (fill positions 2–3)
new Array(5).fill(0)            // [0, 0, 0, 0, 0]
```

---

## 7.4 Non-Mutating Methods (Return New Array, Original Unchanged)

**Prefer these** — they're safer and compose well:

```javascript
const arr = [0, 1, 2, 3, 4, 5];

// slice(start, end) — end is exclusive
arr.slice(1, 4)    // [1, 2, 3]
arr.slice(2)       // [2, 3, 4, 5]
arr.slice(-2)      // [4, 5]  (last 2)
arr.slice()        // shallow copy of entire array

// concat
[1, 2].concat([3, 4], [5])  // [1, 2, 3, 4, 5]
[1, 2, ...[3, 4], 5]        // same with spread (preferred)

// Spread for non-mutating add/remove
const withItem  = [...arr, 6];                 // add to end
const withFront = [99, ...arr];                // add to start
const without2  = arr.filter(n => n !== 2);   // remove value 2

// ES2023 non-mutating versions of sort/reverse/splice
const original = [3, 1, 4, 1, 5];
const sorted   = original.toSorted((a, b) => a - b);  // [1,1,3,4,5] — original unchanged
const reversed = original.toReversed();                 // [5,1,4,1,3] — original unchanged
const spliced  = original.toSpliced(2, 1, 99);          // [3,1,99,1,5] — original unchanged
const updated  = original.with(0, 99);                  // [99,1,4,1,5] — replace at index
```

---

## 7.5 Transformation Methods — The Big 3

### `map` — Transform Every Element

Returns a new array of the **same length**:

```javascript
const numbers = [1, 2, 3, 4, 5];

numbers.map(n => n * 2)             // [2, 4, 6, 8, 10]
numbers.map(n => n.toString())      // ['1','2','3','4','5']
numbers.map(n => ({ value: n, doubled: n * 2 }))
// [{ value:1, doubled:2 }, ...]

const users = [
  { id: 1, name: 'Alice', salary: 80000 },
  { id: 2, name: 'Bob',   salary: 60000 }
];
users.map(u => u.name)                           // ['Alice', 'Bob']
users.map(u => ({ ...u, salary: u.salary * 1.1 }))  // 10% raise — immutable update
```

### `filter` — Keep Elements That Pass a Test

Returns a new array that is **shorter or equal** in length:

```javascript
numbers.filter(n => n % 2 === 0)              // [2, 4]
numbers.filter(n => n > 3)                    // [4, 5]
users.filter(u => u.salary > 70000)           // [Alice]

// Remove null/undefined values
[1, null, 2, undefined, 3, ''].filter(Boolean)  // [1, 2, 3]  (filters all falsy)
```

### `reduce` — Accumulate to a Single Value

Most powerful, most flexible:

```javascript
// Syntax: arr.reduce((accumulator, currentValue) => newAccumulator, initialValue)

// Sum
numbers.reduce((sum, n) => sum + n, 0)   // 15

// Max
numbers.reduce((max, n) => n > max ? n : max, -Infinity)  // 5

// Cart total
const cart = [
  { name: 'Widget', price: 100, qty: 2 },
  { name: 'Gadget', price: 250, qty: 1 }
];
const total = cart.reduce((sum, item) => sum + item.price * item.qty, 0);  // 450

// Build lookup map
const byId = users.reduce((acc, user) => {
  acc[user.id] = user;
  return acc;
}, {});
byId[1].name  // 'Alice' — O(1) lookup

// Count occurrences
const words = ['apple', 'banana', 'apple', 'cherry', 'banana', 'apple'];
const counts = words.reduce((acc, word) => {
  acc[word] = (acc[word] ?? 0) + 1;
  return acc;
}, {});
// { apple: 3, banana: 2, cherry: 1 }

// Group by
const people = [
  { name: 'Alice', dept: 'Engineering' },
  { name: 'Bob',   dept: 'Design' },
  { name: 'Carol', dept: 'Engineering' }
];
const byDept = people.reduce((groups, person) => {
  groups[person.dept] ??= [];
  groups[person.dept].push(person);
  return groups;
}, {});
// { Engineering: [Alice, Carol], Design: [Bob] }

// Flatten array
[[1,2],[3,4],[5]].reduce((flat, arr) => [...flat, ...arr], [])
// [1, 2, 3, 4, 5]
```

### Chaining — The Real Power

```javascript
const adminSalaryTotal = users
  .filter(u => u.role === 'admin')          // keep admins
  .map(u => u.salary)                        // extract salaries
  .reduce((sum, s) => sum + s, 0);           // sum them

const adminNames = users
  .filter(u => u.role === 'admin')
  .map(u => u.name)
  .sort()
  .join(', ');
// 'Alice, Carol'
```

---

## 7.6 Searching Methods

```javascript
const numbers = [1, 2, 3, 2, 1];
const users = [
  { id: 1, name: 'Alice', role: 'admin' },
  { id: 2, name: 'Bob',   role: 'user'  },
  { id: 3, name: 'Carol', role: 'admin' }
];

// indexOf / lastIndexOf — by value (strict ===)
numbers.indexOf(2)       // 1 (first occurrence)
numbers.lastIndexOf(2)   // 3 (last occurrence)
numbers.indexOf(99)      // -1 (not found)

// includes — boolean check
[1, 2, 3].includes(2)    // true
[1, NaN].includes(NaN)   // true (unlike indexOf which uses ===)

// find — by condition, returns FIRST match or undefined
users.find(u => u.id === 2)                // { id: 2, name: 'Bob', ... }
users.find(u => u.role === 'admin')        // { id: 1, name: 'Alice', ... } (first)
users.find(u => u.id === 99)              // undefined

// findIndex — returns INDEX of first match or -1
users.findIndex(u => u.name === 'Carol')  // 2
users.findIndex(u => u.id === 99)         // -1

// findLast / findLastIndex (ES2023)
[1, 2, 3, 4].findLast(n => n % 2 === 0)     // 4 (last even)
[1, 2, 3, 4].findLastIndex(n => n % 2 === 0) // 3

// every / some
users.every(u => u.role === 'admin')   // false (Bob is user)
users.some(u => u.role === 'admin')    // true (Alice is admin)
[].every(x => false)                   // true (vacuous truth — empty array)
[].some(x => true)                     // false (no elements to match)
```

---

## 7.7 `flat` and `flatMap`

```javascript
// flat — flatten nested arrays
[1, [2, [3, [4]]]].flat()         // [1, 2, [3, [4]]]  (one level)
[1, [2, [3, [4]]]].flat(2)        // [1, 2, 3, [4]]    (two levels)
[1, [2, [3, [4]]]].flat(Infinity) // [1, 2, 3, 4]      (all levels)

// flatMap — map then flatten one level (more efficient than map + flat)
[[1,2],[3,4]].flatMap(x => x)   // [1, 2, 3, 4]

// Practical: split sentences into words
['hello world', 'foo bar'].flatMap(s => s.split(' '))
// ['hello', 'world', 'foo', 'bar']

// Expand one-to-many relationship
const orders = [
  { id: 1, items: ['apple', 'banana'] },
  { id: 2, items: ['cherry'] }
];
orders.flatMap(o => o.items)
// ['apple', 'banana', 'cherry']
```

---

## 7.8 Slicing and Joining

```javascript
// join — array to string
['hello', 'world'].join(' ')     // 'hello world'
[1, 2, 3].join(', ')             // '1, 2, 3'
[1, 2, 3].join('')               // '123'
['2024', '11', '15'].join('-')   // '2024-11-15'

// split — string to array (String method)
'a,b,c'.split(',')    // ['a', 'b', 'c']
'hello'.split('')     // ['h', 'e', 'l', 'l', 'o']
'hello'.split('')     // split to chars
```

---

## 7.9 Array Destructuring

```javascript
const [first, second, ...rest] = [1, 2, 3, 4, 5];
// first = 1, second = 2, rest = [3, 4, 5]

// Skip elements with empty slots
const [,, third] = [1, 2, 3, 4, 5];
// third = 3

// Default values
const [a = 0, b = 0, c = 0] = [10, 20];
// a = 10, b = 20, c = 0

// Swap without temp variable
let x = 1, y = 2;
[x, y] = [y, x];   // x = 2, y = 1

// Multiple return values
function getMinMax(arr) {
  return [Math.min(...arr), Math.max(...arr)];
}
const [min, max] = getMinMax([3, 1, 4, 1, 5, 9]);  // min=1, max=9

// From entries
for (const [key, value] of Object.entries(obj)) { ... }
for (const [index, item] of arr.entries()) { ... }
```

---

## 7.10 Map and Set Collections

### `Map` — Key-Value with Any Key Type

```javascript
// Unlike Object, Map keys can be ANY type
const map = new Map();
map.set('name', 'Alice');
map.set(1, 'one');              // number key
map.set(true, 'yes');           // boolean key
const objKey = { id: 1 };
map.set(objKey, 'object value'); // object key

map.get('name')   // 'Alice'
map.has('name')   // true
map.size          // 4
map.delete('name')

// Create from array of pairs
const config = new Map([
  ['host', 'localhost'],
  ['port', 3000],
  ['debug', false]
]);

// Iterate
for (const [key, value] of config) {
  console.log(`${key}: ${value}`);
}
config.forEach((value, key) => console.log(`${key}: ${value}`));

// Convert Map ↔ Object
const obj = Object.fromEntries(config);  // { host: 'localhost', port: 3000, debug: false }
const map2 = new Map(Object.entries(obj));
```

### `Set` — Unique Values

```javascript
const set = new Set([1, 2, 2, 3, 3, 3]);
set.size     // 3 (duplicates removed)
set.has(2)   // true
set.add(4)
set.delete(2)

for (const value of set) { console.log(value); }
[...set]  // convert to array

// Most common use: remove duplicates
const arr = [1, 2, 2, 3, 3, 4];
const unique = [...new Set(arr)];  // [1, 2, 3, 4]

// Set operations
const a = new Set([1, 2, 3, 4]);
const b = new Set([3, 4, 5, 6]);

const union        = new Set([...a, ...b]);              // {1,2,3,4,5,6}
const intersection = new Set([...a].filter(x => b.has(x)));  // {3,4}
const difference   = new Set([...a].filter(x => !b.has(x))); // {1,2}
```

---

## 7.11 Practical Patterns

```javascript
// Paginate
function paginate(arr, page, perPage = 10) {
  return arr.slice((page - 1) * perPage, page * perPage);
}
paginate([1,2,3,4,5,6,7,8,9,10], 2, 3)  // [4, 5, 6]

// Chunk array into sub-arrays
function chunk(arr, size) {
  return Array.from(
    { length: Math.ceil(arr.length / size) },
    (_, i) => arr.slice(i * size, i * size + size)
  );
}
chunk([1,2,3,4,5,6,7], 3)  // [[1,2,3],[4,5,6],[7]]

// Zip two arrays
const zip = (a, b) => a.map((val, i) => [val, b[i]]);
zip(['a','b','c'], [1, 2, 3])  // [['a',1],['b',2],['c',3]]

// Flatten and count words
const sentences = [['hello','world'], ['foo','hello','bar']];
sentences.flat().reduce((counts, word) => {
  counts[word] = (counts[word] ?? 0) + 1;
  return counts;
}, {});
// { hello: 2, world: 1, foo: 1, bar: 1 }

// Get distinct values of a property
const depts = [...new Set(employees.map(e => e.department))];
```

---

## Mutating vs Non-Mutating — Quick Reference

| Mutating | Non-Mutating Equivalent |
|---|---|
| `arr.sort()` | `arr.toSorted()` (ES2023) |
| `arr.reverse()` | `arr.toReversed()` (ES2023) |
| `arr.splice()` | `arr.toSpliced()` (ES2023), `arr.filter()`, `arr.slice()` |
| `arr.push(x)` | `[...arr, x]` |
| `arr.unshift(x)` | `[x, ...arr]` |
| `arr.pop()` | `arr.slice(0, -1)` |
| `arr[i] = x` | `arr.with(i, x)` (ES2023) |

---

## Key Takeaways

- `map`, `filter`, `reduce` are non-mutating and chainable — the backbone of array work.
- `sort()` without a comparator is **lexicographic** — always pass `(a, b) => a - b` for numbers.
- `slice()` is non-mutating; `splice()` mutates. `toSorted`, `toReversed`, `toSpliced` are the modern non-mutating alternatives.
- `Set` for unique values; `Map` when you need non-string keys or guaranteed insertion order.
- `findLast`, `at(-1)`, `toSorted` are ES2022/2023 — supported in Node.js 20+.
- `reduce` can build any data structure — lookup maps, groupings, counts, flattening.

---

## Self-Check Questions

1. What is the difference between `map()` and `forEach()`?
2. Why does `[10, 9, 2, 100].sort()` give the "wrong" answer for numbers? How do you fix it?
3. How do you remove duplicates from an array? Give two approaches.
4. Write a `groupBy(arr, key)` function that groups an array of objects by a property.
5. What is the difference between `find()` and `filter()`?
6. What is `flatMap` useful for? Give a real-world example.
7. When would you use `Map` instead of a plain object?
