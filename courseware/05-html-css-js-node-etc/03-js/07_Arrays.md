# JS Basics — Module 07: Arrays

## 7.1 Creating Arrays

```javascript
// Array literal (preferred)
const fruits = ['apple', 'banana', 'cherry'];
const numbers = [1, 2, 3, 4, 5];
const mixed = [1, 'hello', true, null, { id: 1 }];  // any types

// Array constructor (avoid — confusing with single number arg)
new Array(3)          // [empty × 3] — not [3]!
new Array(3).fill(0)  // [0, 0, 0]
new Array(1, 2, 3)    // [1, 2, 3]

// Array.from — create from iterable or array-like
Array.from('hello')         // ['h','e','l','l','o']
Array.from({length: 5}, (_, i) => i)   // [0, 1, 2, 3, 4]
Array.from(new Set([1, 2, 2, 3]))       // [1, 2, 3]
Array.from(document.querySelectorAll('p'))  // NodeList → Array

// Array.of
Array.of(1, 2, 3)     // [1, 2, 3]
Array.of(7)           // [7] (unlike new Array(7))

// Spread from iterable
const chars = [...'hello'];     // ['h','e','l','l','o']
const unique = [...new Set([1,2,2,3])]  // [1, 2, 3]
```

---

## 7.2 Accessing and Modifying

```javascript
const arr = ['a', 'b', 'c', 'd', 'e'];

// Index access (0-based)
arr[0]      // 'a'
arr[4]      // 'e'
arr[-1]     // undefined  (negative index doesn't work like Python)
arr.at(-1)  // 'e'  (ES2022: at() supports negative indices)
arr.at(-2)  // 'd'
arr[arr.length - 1]   // 'e'  (classic way)

// Modifying
arr[0] = 'A';      // ['A', 'b', 'c', 'd', 'e']
arr[10] = 'z';     // creates sparse array — arr[5..9] are empty

// Length
arr.length         // 5
arr.length = 3;    // truncates! ['A', 'b', 'c']

// Checking if array
Array.isArray([])    // true
Array.isArray({})    // false
typeof []            // 'object' — not reliable for array check
```

---

## 7.3 Adding and Removing Elements

```javascript
let arr = [1, 2, 3];

// End (like a stack)
arr.push(4, 5)      // arr = [1,2,3,4,5], returns new length 5
arr.pop()           // arr = [1,2,3,4], returns 5

// Start (slower — shifts all elements)
arr.unshift(0)      // arr = [0,1,2,3,4], returns length
arr.shift()         // arr = [1,2,3,4], returns 0

// Middle: splice(start, deleteCount, ...items)
arr.splice(1, 0, 10, 11)   // insert at index 1: [1,10,11,2,3,4]
arr.splice(1, 2)           // remove 2 from index 1: [1,2,3,4], returns [10,11]
arr.splice(1, 1, 99)       // replace index 1: [1,99,3,4], returns [2]

// Non-mutating alternatives (return new array)
const withItem   = [...arr, 5];              // add to end
const withFront  = [0, ...arr];              // add to start
const without    = arr.filter(n => n !== 2); // remove by value
```

---

## 7.4 Searching Arrays

```javascript
const users = [
  { id: 1, name: 'Alice', role: 'admin' },
  { id: 2, name: 'Bob',   role: 'user'  },
  { id: 3, name: 'Carol', role: 'admin' }
];

// indexOf / lastIndexOf — by value (uses ===)
[1, 2, 3, 2].indexOf(2)       // 1
[1, 2, 3, 2].lastIndexOf(2)   // 3
[1, 2, 3].indexOf(99)         // -1 (not found)

// includes — boolean check
[1, 2, 3].includes(2)         // true
[1, NaN].includes(NaN)        // true (unlike indexOf)

// find / findIndex — by condition
users.find(u => u.id === 2)               // { id: 2, name: 'Bob', role: 'user' }
users.findIndex(u => u.name === 'Carol')  // 2
users.find(u => u.id === 99)              // undefined

// findLast / findLastIndex (ES2023)
[1, 2, 3, 4].findLast(n => n % 2 === 0)     // 4
[1, 2, 3, 4].findLastIndex(n => n % 2 === 0) // 3

// some / every
users.some(u => u.role === 'admin')   // true (Alice is admin)
users.every(u => u.role === 'admin')  // false (Bob is user)
```

---

## 7.5 Transformation Methods

```javascript
const numbers = [1, 2, 3, 4, 5];

// map: transform each element → new array (same length)
numbers.map(n => n * 2)             // [2, 4, 6, 8, 10]
numbers.map(n => n.toString())      // ['1','2','3','4','5']
users.map(u => u.name)              // ['Alice', 'Bob', 'Carol']

// filter: keep matching elements → new array (shorter or equal)
numbers.filter(n => n % 2 === 0)    // [2, 4]
users.filter(u => u.role === 'admin') // [{Alice...}, {Carol...}]

// reduce: accumulate to single value
numbers.reduce((sum, n) => sum + n, 0)  // 15
numbers.reduce((max, n) => n > max ? n : max, -Infinity)  // 5

// Chaining: filter then map then reduce
const adminNames = users
  .filter(u => u.role === 'admin')
  .map(u => u.name)
  .join(', ');
// 'Alice, Carol'

// flatMap: map then flatten one level
[[1,2],[3,4],[5]].flatMap(x => x)  // [1,2,3,4,5]
['hello world', 'foo bar'].flatMap(s => s.split(' '))  // ['hello','world','foo','bar']

// flat: flatten nested arrays
[1,[2,[3,[4]]]].flat()      // [1, 2, [3, [4]]]  (one level)
[1,[2,[3,[4]]]].flat(2)     // [1, 2, 3, [4]]    (two levels)
[1,[2,[3,[4]]]].flat(Infinity)  // [1, 2, 3, 4]  (all levels)
```

---

## 7.6 Sorting and Reversing

```javascript
// reverse — in place, mutates
[1, 2, 3].reverse()   // [3, 2, 1]

// sort — in place, mutates (default: lexicographic string sort!)
[10, 9, 2, 1, 100].sort()  // [1, 10, 100, 2, 9]  ← WRONG for numbers!

// Numeric sort: provide comparator
[10, 9, 2, 1, 100].sort((a, b) => a - b)   // [1, 2, 9, 10, 100] ascending
[10, 9, 2, 1, 100].sort((a, b) => b - a)   // [100, 10, 9, 2, 1] descending

// Sort objects
users.sort((a, b) => a.name.localeCompare(b.name))  // by name, locale-aware
users.sort((a, b) => a.id - b.id)                   // by id

// Non-mutating sort (ES2023)
const sorted = arr.toSorted((a, b) => a - b);  // new array, original unchanged
const reversed = arr.toReversed();              // new array, original unchanged

// Multi-key sort
users.sort((a, b) => {
  if (a.role !== b.role) return a.role.localeCompare(b.role);  // primary: role
  return a.name.localeCompare(b.name);                         // secondary: name
});
```

---

## 7.7 Slicing and Joining

```javascript
const arr = [0, 1, 2, 3, 4, 5];

// slice(start, end) — non-mutating, end is exclusive
arr.slice(1, 4)     // [1, 2, 3]
arr.slice(2)        // [2, 3, 4, 5]
arr.slice(-2)       // [4, 5]  (last 2)
arr.slice()         // [0,1,2,3,4,5]  (shallow copy)

// join
['hello', 'world'].join(' ')   // 'hello world'
[1, 2, 3].join(', ')          // '1, 2, 3'
[1, 2, 3].join('')            // '123'

// split (string method — makes array)
'a,b,c'.split(',')    // ['a','b','c']
'hello'.split('')     // ['h','e','l','l','o']

// concat
[1, 2].concat([3, 4], [5])  // [1,2,3,4,5]
[1, 2, ...[3, 4], ...[5]]   // same with spread (preferred)

// fill
new Array(5).fill(0)              // [0,0,0,0,0]
[1,2,3,4,5].fill(0, 2, 4)       // [1,2,0,0,5]  (fill indices 2-3)

// copyWithin
[1,2,3,4,5].copyWithin(1, 3)   // [1,4,5,4,5]  (copy from 3 to position 1)
```

---

## 7.8 Array Destructuring

```javascript
const [first, second, ...rest] = [1, 2, 3, 4, 5];
// first = 1, second = 2, rest = [3, 4, 5]

// Skip elements
const [,, third] = [1, 2, 3, 4, 5];
// third = 3

// Default values
const [a = 0, b = 0, c = 0] = [10, 20];
// a = 10, b = 20, c = 0

// Swap variables (no temp variable needed)
let x = 1, y = 2;
[x, y] = [y, x];
// x = 2, y = 1

// From function return
function getMinMax(arr) {
  return [Math.min(...arr), Math.max(...arr)];
}
const [min, max] = getMinMax([3, 1, 4, 1, 5, 9]);

// Nested destructuring
const [[a1, a2], [b1, b2]] = [[1, 2], [3, 4]];
```

---

## 7.9 Map, Set, WeakMap, WeakSet

```javascript
// Map: key-value pairs, any type as key (unlike Object where keys are strings)
const map = new Map();
map.set('name', 'Alice');
map.set(1, 'one');            // number key
map.set({}, 'obj key');       // object key

map.get('name')               // 'Alice'
map.has('name')               // true
map.size                      // 3
map.delete('name')
map.clear()

// Iterate
for (const [key, value] of map) { ... }
map.forEach((value, key) => { ... })

// From array of pairs
const config = new Map([['host', 'localhost'], ['port', 3000]]);

// Convert to/from object
const obj = Object.fromEntries(config);  // { host: 'localhost', port: 3000 }
const map2 = new Map(Object.entries(obj));

// Set: unique values, any type
const set = new Set([1, 2, 2, 3, 3, 3]);
set.size      // 3
set.has(2)    // true
set.add(4)
set.delete(2)

// Iterate
for (const value of set) { ... }
[...set]      // convert to array

// Use cases
const unique = [...new Set([1, 2, 2, 3, 3])]  // [1, 2, 3]  — deduplicate

// Set operations
const a = new Set([1, 2, 3, 4]);
const b = new Set([3, 4, 5, 6]);

const union        = new Set([...a, ...b]);         // {1,2,3,4,5,6}
const intersection = new Set([...a].filter(x => b.has(x)));  // {3,4}
const difference   = new Set([...a].filter(x => !b.has(x))); // {1,2}
```

---

## 7.10 Practical Array Patterns

```javascript
// Group by (ES2024 or manual)
const people = [
  { name: 'Alice', dept: 'Engineering' },
  { name: 'Bob', dept: 'Design' },
  { name: 'Carol', dept: 'Engineering' }
];

// Manual groupBy using reduce
const byDept = people.reduce((groups, person) => {
  const key = person.dept;
  groups[key] = groups[key] ?? [];
  groups[key].push(person);
  return groups;
}, {});
// { Engineering: [{Alice...}, {Carol...}], Design: [{Bob...}] }

// Paginate
function paginate(arr, page, perPage) {
  return arr.slice((page - 1) * perPage, page * perPage);
}
paginate([1,2,3,4,5,6,7,8,9,10], 2, 3)  // [4, 5, 6]

// Chunk array
function chunk(arr, size) {
  return Array.from({ length: Math.ceil(arr.length / size) },
    (_, i) => arr.slice(i * size, i * size + size));
}
chunk([1,2,3,4,5,6,7], 3)  // [[1,2,3],[4,5,6],[7]]

// Zip two arrays
const zip = (a, b) => a.map((val, i) => [val, b[i]]);
zip([1,2,3], ['a','b','c'])  // [[1,'a'],[2,'b'],[3,'c']]

// Flatten and count
const words = [['hello','world'],['foo','hello','bar']];
words.flat().reduce((counts, word) => {
  counts[word] = (counts[word] ?? 0) + 1;
  return counts;
}, {});
// { hello: 2, world: 1, foo: 1, bar: 1 }
```

---

## Key Takeaways

- Use `map`, `filter`, `reduce` for transformations — they are non-mutating and chainable.
- `sort()` without a comparator is **lexicographic** — always provide a comparator for numbers.
- `slice()` is non-mutating; `splice()` mutates. Same with `push/pop/shift/unshift`.
- `Array.from()` converts iterables to arrays; `[...iterable]` does the same.
- `Set` for unique values; `Map` when you need non-string keys.
- Use `.at(-1)` instead of `arr[arr.length - 1]` for the last element.

---

## Self-Check Questions

1. What is the difference between `map()` and `forEach()`?
2. Why does `[10, 9, 2, 100].sort()` return `[10, 100, 2, 9]`? How do you fix it?
3. How do you remove duplicates from an array using a `Set`?
4. Write a `groupBy(arr, key)` function that groups an array of objects by a given property.
5. What is the difference between `slice` and `splice`? Which one mutates the array?
