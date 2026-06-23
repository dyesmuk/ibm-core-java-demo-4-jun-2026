# JavaScript Courseware — Module 08: Strings & String Methods

Strings are immutable in JavaScript — every string method returns a **new string**, never modifying the original. This module covers all the methods you'll use daily.

---

## 8.1 String Creation and Template Literals

```javascript
// Three ways to create strings — all equivalent
const s1 = 'single quotes';
const s2 = "double quotes";
const s3 = `backtick (template literal)`;

// Template literals — prefer these for anything with variables
const name = 'Alice';
const age = 30;

const msg = `Hello, ${name}! You are ${age} years old.`;
// Any expression works inside ${}
const result = `${2 + 2}`;           // '4'
const check = `${age >= 18 ? 'Adult' : 'Minor'}`;
const method = `${obj.getValue()}`;

// Multi-line strings
const html = `
  <div class="card">
    <h2>${name}</h2>
    <p>Age: ${age}</p>
  </div>
`;

// String length
'hello'.length  // 5
```

---

## 8.2 Accessing Characters

```javascript
const str = 'Hello, World!';

// By index
str[0]        // 'H'
str[7]        // 'W'
str[-1]       // undefined (negative index doesn't work like Python)
str.at(-1)    // '!' (ES2022 — supports negative indices)
str.at(-2)    // 'd'

// charAt (older but same as bracket notation for valid indices)
str.charAt(0)  // 'H'
```

---

## 8.3 Searching Strings

```javascript
const str = 'Hello, World! Hello again!';

// includes — returns boolean
str.includes('World')       // true
str.includes('world')       // false (case-sensitive)
str.includes('Hello', 10)   // true (search starting at index 10)

// startsWith / endsWith
str.startsWith('Hello')     // true
str.startsWith('World', 7)  // true (check from index 7)
str.endsWith('!')           // true
str.endsWith('again', 25)   // true (check within first 25 chars)

// indexOf / lastIndexOf — returns index or -1
str.indexOf('Hello')       // 0  (first occurrence)
str.lastIndexOf('Hello')   // 14 (last occurrence)
str.indexOf('xyz')         // -1 (not found)

// search — finds via regex, returns index or -1
'hello123'.search(/\d+/)   // 5

// match — returns match result or null
'abc123'.match(/\d+/)       // ['123', index: 3, ...]
'abc123def456'.match(/\d+/g) // ['123', '456']  (global flag = array of all matches)
'no digits'.match(/\d+/)    // null

// matchAll — all matches with capture groups (ES2020)
const text = 'cat bat sat';
const matches = [...text.matchAll(/[a-z]at/g)];
matches.map(m => m[0])   // ['cat', 'bat', 'sat']
```

---

## 8.4 Extracting Substrings

```javascript
const str = 'Hello, World!';

// slice(start, end) — end is exclusive, supports negative
str.slice(0, 5)     // 'Hello'
str.slice(7)        // 'World!'
str.slice(7, 12)    // 'World'
str.slice(-6)       // 'orld!'  (6 from end)
str.slice(-6, -1)   // 'orld'

// substring(start, end) — no negative support, swaps if start > end
str.substring(0, 5)  // 'Hello'
str.substring(7, 12) // 'World'

// Use slice — it's more capable and consistent

// split — string to array
'a,b,c'.split(',')       // ['a', 'b', 'c']
'hello'.split('')        // ['h', 'e', 'l', 'l', 'o']
'a::b::c'.split('::')    // ['a', 'b', 'c']
'hello world'.split(' ') // ['hello', 'world']
'hello world'.split(' ', 1)  // ['hello'] (limit)
```

---

## 8.5 Modifying / Transforming Strings

```javascript
const str = '  Hello, World!  ';

// Case conversion
str.toUpperCase()   // '  HELLO, WORLD!  '
str.toLowerCase()   // '  hello, world!  '

// Trim whitespace
str.trim()          // 'Hello, World!'
str.trimStart()     // 'Hello, World!  '  (alias: trimLeft)
str.trimEnd()       // '  Hello, World!'  (alias: trimRight)

// Replace
'hello world'.replace('world', 'JS')      // 'hello JS'  (replaces first only)
'hello hello'.replace('hello', 'hi')      // 'hi hello'  (still first only!)
'hello hello'.replaceAll('hello', 'hi')   // 'hi hi'  (ES2021 — replaces ALL)

// Replace with regex
'hello world'.replace(/\w+/g, w => w.toUpperCase())  // 'HELLO WORLD'
'2024-11-15'.replace(/(\d{4})-(\d{2})-(\d{2})/, '$3/$2/$1')  // '15/11/2024'

// Repeat
'ha'.repeat(3)      // 'hahaha'
'-'.repeat(20)      // '--------------------'

// Pad
'5'.padStart(4, '0')    // '0005'  (zero-pad for IDs, codes)
'5'.padEnd(4, '0')      // '5000'
'hi'.padStart(5)        // '   hi'  (default pad char is space)

// Concatenation (use template literals instead)
'Hello' + ', ' + 'World!'  // 'Hello, World!'
'Hello'.concat(', ', 'World!')  // same
```

---

## 8.6 Utility Methods

```javascript
// charCodeAt / fromCharCode
'A'.charCodeAt(0)      // 65
String.fromCharCode(65) // 'A'

// normalize (for Unicode — accented chars)
'\u00e9'.normalize()   // 'é'

// Check for empty / whitespace
const isEmpty = s => s.trim().length === 0;

// Count occurrences of substring
const countOccurrences = (str, sub) =>
  str.split(sub).length - 1;
countOccurrences('banana', 'a')  // 3
```

---

## 8.7 Regex with Strings — Essential Patterns

```javascript
// Test if string matches pattern
/^\d+$/.test('12345')      // true (all digits)
/^\d+$/.test('123abc')     // false

// Extract groups
const dateStr = '2024-11-15';
const match = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})$/);
if (match) {
  const [, year, month, day] = match;  // destructure capture groups
  console.log(year, month, day);  // '2024' '11' '15'
}

// Named capture groups (ES2018)
const { groups: { year, month, day } } = dateStr.match(
  /^(?<year>\d{4})-(?<month>\d{2})-(?<day>\d{2})$/
);

// Common validation patterns
const isEmail   = s => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(s);
const isPhone   = s => /^\+?[\d\s\-()]{10,}$/.test(s);
const isURL     = s => /^https?:\/\/.+/.test(s);
const hasDigits = s => /\d/.test(s);

// Extract numbers from string
'Item 42 costs Rs 150.50'.match(/[\d.]+/g)  // ['42', '150.50']

// Replace with function
const capitalize = str =>
  str.replace(/\b\w/g, char => char.toUpperCase());
capitalize('hello world')  // 'Hello World'
```

---

## 8.8 Number ↔ String Conversions

```javascript
// Number to String
(42).toString()       // "42"
(42).toString(2)      // "101010"  (binary)
(42).toString(16)     // "2a"  (hex)
(3.14159).toFixed(2)  // "3.14"  (rounded, returns string)
(1234567.89).toLocaleString('en-IN')  // "12,34,567.89" (Indian format)

// String to Number
Number('42')        // 42
Number('3.14')      // 3.14
Number('')          // 0
Number('abc')       // NaN
parseInt('42px', 10)  // 42 (stop at non-numeric)
parseFloat('3.14em')  // 3.14
+'42'               // 42 (unary plus)

// Check if a string is a valid number
const isNumericString = s => !isNaN(s) && s.trim() !== '';
isNumericString('42')    // true
isNumericString('  ')    // false
isNumericString('abc')   // false
```

---

## 8.9 Practical String Patterns

```javascript
// Truncate with ellipsis
const truncate = (str, max) =>
  str.length > max ? str.slice(0, max - 3) + '...' : str;
truncate('Hello, World!', 8)  // 'Hello...'

// Slugify for URLs
const slugify = str =>
  str.toLowerCase().trim().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '');
slugify('Hello World! My Blog Post')  // 'hello-world-my-blog-post'

// Camel case to title case
const camelToTitle = str =>
  str.replace(/([A-Z])/g, ' $1').replace(/^./, c => c.toUpperCase());
camelToTitle('userFirstName')  // 'User First Name'

// Count words
const wordCount = str => str.trim().split(/\s+/).length;
wordCount('  Hello world  ')  // 2

// Reverse a string
const reverse = str => [...str].reverse().join('');
reverse('hello')  // 'olleh'
// Note: [...str] splits by Unicode code points — safer than split('')

// Check palindrome
const isPalindrome = str => {
  const clean = str.toLowerCase().replace(/[^a-z0-9]/g, '');
  return clean === [...clean].reverse().join('');
};
isPalindrome('A man a plan a canal Panama')  // true
```

---

## Key Takeaways

- Strings are **immutable** — all methods return new strings, never modify in place.
- Use `includes`, `startsWith`, `endsWith` for simple checks — more readable than `indexOf`.
- Use `replaceAll` (not `replace`) when you want to replace all occurrences.
- Use `slice` (not `substring`) — it supports negative indices.
- Template literals are always preferred over string concatenation.
- `split` converts to array; `join` converts back — powerful combination with array methods.

---

## Self-Check Questions

1. What is the difference between `slice` and `substring`?
2. Why does `'hello hello'.replace('hello', 'hi')` only change the first occurrence?
3. How do you check if a string contains only digits?
4. What does `'hello'.at(-1)` return?
5. Write a function that converts `'hello-world-string'` into `'helloWorldString'` (camelCase). (Hint: `split`, `map`, `join`)
6. What does `'a,b,,c'.split(',')` return? (Careful with empty strings)
