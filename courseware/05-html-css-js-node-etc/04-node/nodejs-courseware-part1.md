# 🚀 Node.js — The Complete Courseware
### *From Zero to Full-Stack Beast*

> **Who this is for:** Devs who already know JavaScript and want to take it server-side. Buckle up — we're building real stuff from Day 1.

---

## 📋 How to Use This Courseware

Each module follows this pattern:
- 🧠 **The Concept** — what it is and why it matters
- 💻 **Hands-On** — code you actually type and run
- 🔥 **Challenge** — push yourself a little further
- ✅ **Key Takeaways** — the stuff to remember

> **Prerequisites:** You know JavaScript (variables, functions, arrays, objects, promises, arrow functions). That's it.

---

---

# MODULE 1 — Welcome to Node.js

## 🧠 The Concept

You've been writing JavaScript that runs *in the browser*. Node.js lets you run JavaScript *on your computer* (or a server) — without a browser at all.

Think of it this way:

| Browser JS | Node.js |
|---|---|
| Talks to the DOM | Talks to the File System |
| Runs in Chrome/Firefox | Runs directly on your machine |
| `window`, `document` | `process`, `fs`, `http` |
| Can't touch your files | Can read/write files |
| Can't run a web server | Can run a web server |

**Why does this matter?**  
With Node.js you can:
- Build web servers (APIs, REST, real-time)
- Read and write files
- Connect to databases
- Build CLI tools
- Deploy apps to the cloud

### The V8 Engine
Node.js uses Chrome's **V8 engine** to compile JavaScript to native machine code. This is the same engine that makes Chrome fast — just running outside the browser.

### Node.js is Single-Threaded but Non-Blocking
This is the big brain move. Node doesn't create a new thread for every request (like Java does). Instead, it uses an **event loop** to handle thousands of connections concurrently. More on this in the Async module.

---

---

# MODULE 2 — Installing and Exploring Node.js

## 🧠 The Concept

Before we write a single line of server-side code, let's set up our environment and understand what we're working with.

## 💻 Hands-On: Installation

### Step 1: Install Node.js

Head to [nodejs.org](https://nodejs.org) and download the **LTS (Long-Term Support)** version. LTS = stable, battle-tested, the one you want for learning and production.

> **Pro tip:** If you're on a Mac, use `nvm` (Node Version Manager). It lets you switch between Node versions without pain.

```bash
# Check if Node is already installed
node --version    # Should print something like v20.x.x
npm --version     # Node Package Manager — comes bundled with Node
```

### Step 2: Your First Node Script

Create a file called `hello.js`:

```javascript
// hello.js
console.log('Hello from Node.js! 🚀')
console.log('Node version:', process.version)
console.log('Platform:', process.platform)
```

Run it:

```bash
node hello.js
```

Output:
```
Hello from Node.js! 🚀
Node version: v20.11.0
Platform: linux
```

Congrats. You just ran JavaScript without a browser.

### Step 3: The Node REPL

REPL = **Read-Eval-Print Loop**. It's like the browser console, but for Node.

```bash
node
```

You'll see `>`. Now type:

```
> 2 + 2
4
> const name = 'Node'
undefined
> `Hello, ${name}!`
'Hello, Node!'
> process.env.HOME
'/home/yourname'
> .exit
```

### Step 4: Exploring the `process` Object

`process` is a global object available in every Node file. No import needed.

```javascript
// explore.js
console.log('--- process info ---')
console.log('Node version:', process.version)
console.log('Platform:', process.platform)
console.log('Working directory:', process.cwd())
console.log('Script path:', __filename)
console.log('Script directory:', __dirname)
console.log('Command line args:', process.argv)
```

Run it:
```bash
node explore.js hello world
```

Notice `process.argv` — it includes `['node', '/path/to/explore.js', 'hello', 'world']`. We'll use this in Module 4.

### Step 5: Global vs Browser Globals

```javascript
// globals.js

// These exist in Node (NOT in browsers):
console.log(__filename)    // full path to this file
console.log(__dirname)     // directory of this file
console.log(process.argv)  // command line arguments

// These do NOT exist in Node:
// window, document, alert, localStorage — all browser-only

// These exist in BOTH:
console.log(typeof setTimeout)  // 'function' — available in Node too!
console.log(typeof Math.random())  // 'number'
```

### Understanding npm

npm = **Node Package Manager**. It lets you install libraries (called *packages*) that other developers wrote.

```bash
# Initialize a project (creates package.json)
mkdir my-project && cd my-project
npm init -y

# Install a package
npm install chalk

# This creates:
# - node_modules/   (the actual packages)
# - package-lock.json  (exact version lock)
# - package.json gets updated with the dependency
```

> **Golden Rule:** Never commit `node_modules/` to git. Add it to `.gitignore`.

```bash
# .gitignore
node_modules/
```

## 🔥 Challenge

1. Create a script that prints your operating system, Node version, and current date/time.
2. Use `process.argv` to accept a name from the command line and print `"Hello, <name>!"`
3. Run `node --help` and find one flag you didn't know existed. What does it do?

## ✅ Key Takeaways

- Node.js runs JavaScript outside the browser using the V8 engine
- `node filename.js` runs a script; `node` alone opens the REPL
- `process` is your global object for runtime info
- `__filename` and `__dirname` tell you where your script lives
- npm manages packages; never commit `node_modules/`

---

---

# MODULE 3 — Node.js Module System

## 🧠 The Concept

As your codebase grows, you can't dump everything into one file. Modules let you split code into organized, reusable pieces.

Node supports two module systems:
- **CommonJS (CJS)** — the OG Node system, uses `require()` and `module.exports`
- **ES Modules (ESM)** — the modern standard, uses `import`/`export`

We'll focus on **CommonJS** since it's what you'll encounter in most Node codebases, then show ESM too.

## 💻 Hands-On

### CommonJS: `require` and `module.exports`

**Scenario:** We're building a math utility.

```javascript
// math.js  (the module)
const add = (a, b) => a + b
const subtract = (a, b) => a - b
const multiply = (a, b) => a * b

// Export everything you want to share
module.exports = { add, subtract, multiply }
```

```javascript
// app.js  (the consumer)
const math = require('./math')  // ./ means "same directory"

console.log(math.add(5, 3))       // 8
console.log(math.subtract(10, 4)) // 6
console.log(math.multiply(3, 7))  // 21
```

```bash
node app.js
```

### Destructuring imports (cleaner)

```javascript
// app.js
const { add, subtract } = require('./math')

console.log(add(2, 3))       // 5
console.log(subtract(10, 3)) // 7
```

### Exporting a single value

```javascript
// greet.js
const greet = (name) => `Hey ${name}, what's good? 👋`
module.exports = greet   // export the function directly
```

```javascript
// app.js
const greet = require('./greet')
console.log(greet('Priya'))  // Hey Priya, what's good? 👋
```

### How `require` resolves paths

```javascript
require('./math')        // looks for math.js in same folder
require('../utils/math') // goes up one level, into utils/
require('express')       // looks in node_modules/ (installed package)
require('fs')            // built-in Node module (no install needed)
```

### Built-in Modules — Node's Standard Library

Node ships with tons of built-in modules. No install needed.

```javascript
// Using built-in modules
const path = require('path')
const os = require('os')

console.log(path.join(__dirname, 'data', 'file.txt'))
// → /home/user/project/data/file.txt

console.log(os.homedir())   // /home/user
console.log(os.platform())  // linux / darwin / win32
console.log(os.cpus().length)  // number of CPU cores
```

**Commonly used built-in modules:**

| Module | What it does |
|--------|--------------|
| `fs` | File system — read/write files |
| `path` | Handle file paths cross-platform |
| `os` | Operating system info |
| `http` | Create HTTP servers |
| `https` | Create HTTPS servers |
| `events` | EventEmitter pattern |
| `util` | Utility functions |
| `crypto` | Cryptography (hashing, encryption) |

### ES Modules (Modern Syntax)

If your `package.json` has `"type": "module"`, or your file ends in `.mjs`, you can use ESM:

```javascript
// math.mjs
export const add = (a, b) => a + b
export const subtract = (a, b) => a - b
export default multiply = (a, b) => a * b
```

```javascript
// app.mjs
import { add, subtract } from './math.mjs'
import multiply from './math.mjs'

console.log(add(2, 3))
```

> **Reality check:** Most backend Node projects still use CommonJS. ESM is more common in frontend. Know both, default to CJS for Node.

### Creating a Reusable Utility Module

Let's build something actually useful:

```javascript
// utils/validator.js
const isEmail = (str) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(str)
const isStrongPassword = (str) => str.length >= 8 && /[A-Z]/.test(str) && /[0-9]/.test(str)
const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1)

module.exports = { isEmail, isStrongPassword, capitalize }
```

```javascript
// app.js
const { isEmail, isStrongPassword, capitalize } = require('./utils/validator')

console.log(isEmail('hey@example.com'))      // true
console.log(isEmail('not-an-email'))          // false
console.log(isStrongPassword('Secure123'))    // true
console.log(isStrongPassword('weak'))         // false
console.log(capitalize('node.js is sick'))    // Node.js is sick
```

### npm Packages as Modules

```bash
npm install validator   # a popular validation library
```

```javascript
const validator = require('validator')

console.log(validator.isEmail('test@example.com'))  // true
console.log(validator.isURL('https://google.com'))  // true
```

## 🔥 Challenge

1. Create a `utils/string.js` module that exports: `truncate(str, length)`, `countWords(str)`, and `isPalindrome(str)`.
2. Create a `utils/array.js` module that exports: `unique(arr)` (remove duplicates), `flatten(arr)` (flatten one level).
3. Import both modules in an `app.js` and test each function.

## ✅ Key Takeaways

- `module.exports` shares code; `require()` consumes it
- Path prefixes: `./` = current dir, `../` = parent dir, no prefix = built-in or npm package
- Built-in modules like `fs`, `path`, `os` come free with Node
- npm packages go into `node_modules/` and are referenced by name
- ES Modules (`import`/`export`) work in Node too — enable via `"type": "module"` in `package.json`

---

---

# MODULE 4 — File System and Command Line Args

## 🧠 The Concept

One of Node's superpowers over browser JS: **direct access to the file system**. You can read configs, write logs, process data files, and build CLI tools.

Node's `fs` module handles all of this. Plus, `process.argv` lets you build command-line interfaces.

## 💻 Hands-On

### Reading Files

```javascript
// read-file.js
const fs = require('fs')

// Synchronous (blocking) — simple but blocks the event loop
const data = fs.readFileSync('./notes.txt', 'utf8')
console.log(data)
```

Create a `notes.txt` file first:
```
Node.js is awesome!
Learning file system stuff.
```

```bash
node read-file.js
```

> **Sync vs Async:** `readFileSync` blocks. For a CLI tool, that's often fine. For a web server handling multiple requests — not fine. We'll cover async properly in Module 5.

```javascript
// Asynchronous (non-blocking) — preferred for servers
const fs = require('fs')

fs.readFile('./notes.txt', 'utf8', (err, data) => {
  if (err) {
    console.error('Error reading file:', err.message)
    return
  }
  console.log(data)
})

console.log('This runs BEFORE the file is read! (async)')
```

### Writing Files

```javascript
// write-file.js
const fs = require('fs')

// Overwrite (or create) a file
fs.writeFileSync('./output.txt', 'Hello, file system! 📁')
console.log('File written!')

// Append to a file
fs.appendFileSync('./output.txt', '\nSecond line added.')
console.log('Appended!')
```

### Async file write

```javascript
const fs = require('fs')

fs.writeFile('./log.txt', 'Server started at ' + new Date().toISOString(), (err) => {
  if (err) throw err
  console.log('Log written!')
})
```

### Checking if a File Exists

```javascript
const fs = require('fs')

// Modern way
if (fs.existsSync('./config.json')) {
  const config = fs.readFileSync('./config.json', 'utf8')
  console.log(JSON.parse(config))
} else {
  console.log('Config file not found — using defaults')
}
```

### Working with Directories

```javascript
const fs = require('fs')
const path = require('path')

// Create a directory
fs.mkdirSync('./data', { recursive: true })  // recursive: true won't throw if exists

// List files in a directory
const files = fs.readdirSync('./')
console.log('Files:', files)

// Delete a file
fs.unlinkSync('./temp.txt')  // throws if file doesn't exist

// Rename / move a file
fs.renameSync('./old.txt', './new.txt')
```

### The `path` Module — Your Best Friend

File paths are different on Windows (`\`) vs Mac/Linux (`/`). `path` handles this for you.

```javascript
const path = require('path')

// Join paths safely (cross-platform)
const filePath = path.join(__dirname, 'data', 'users.json')
console.log(filePath)  // /project/data/users.json

// Get parts of a path
const fullPath = '/home/user/project/app.js'
console.log(path.basename(fullPath))   // app.js
console.log(path.dirname(fullPath))    // /home/user/project
console.log(path.extname(fullPath))    // .js
console.log(path.parse(fullPath))
// { root: '/', dir: '/home/user/project', base: 'app.js', ext: '.js', name: 'app' }
```

### Building a CLI Tool with `process.argv`

```javascript
// notes-cli.js
const fs = require('fs')
const path = require('path')

const notesFile = path.join(__dirname, 'notes.json')

// Helper: load notes
const loadNotes = () => {
  if (!fs.existsSync(notesFile)) return []
  const raw = fs.readFileSync(notesFile, 'utf8')
  return JSON.parse(raw)
}

// Helper: save notes
const saveNotes = (notes) => {
  fs.writeFileSync(notesFile, JSON.stringify(notes, null, 2))
}

// process.argv = ['node', 'notes-cli.js', 'command', 'arg1', ...]
const [,, command, ...args] = process.argv

if (command === 'add') {
  const title = args.join(' ')
  if (!title) return console.log('❌ Please provide a note title')
  const notes = loadNotes()
  notes.push({ title, createdAt: new Date().toISOString() })
  saveNotes(notes)
  console.log(`✅ Note added: "${title}"`)

} else if (command === 'list') {
  const notes = loadNotes()
  if (notes.length === 0) return console.log('📭 No notes yet!')
  notes.forEach((note, i) => {
    console.log(`${i + 1}. ${note.title} (${note.createdAt})`)
  })

} else if (command === 'remove') {
  const titleToRemove = args.join(' ')
  let notes = loadNotes()
  const before = notes.length
  notes = notes.filter(n => n.title !== titleToRemove)
  if (notes.length === before) return console.log('❌ Note not found')
  saveNotes(notes)
  console.log(`🗑️ Removed: "${titleToRemove}"`)

} else {
  console.log('Usage:')
  console.log('  node notes-cli.js add <title>')
  console.log('  node notes-cli.js list')
  console.log('  node notes-cli.js remove <title>')
}
```

Test it:
```bash
node notes-cli.js add "Learn Node.js"
node notes-cli.js add "Build a REST API"
node notes-cli.js list
node notes-cli.js remove "Learn Node.js"
node notes-cli.js list
```

### Using `yargs` for Better CLI Args

```bash
npm install yargs
```

```javascript
// notes-yargs.js
const fs = require('fs')
const yargs = require('yargs')
const { hideBin } = require('yargs/helpers')

yargs(hideBin(process.argv))
  .command('add', 'Add a note', {
    title: { describe: 'Note title', type: 'string', demandOption: true }
  }, (argv) => {
    console.log(`Adding note: ${argv.title}`)
  })
  .command('list', 'List all notes', {}, () => {
    console.log('Listing notes...')
  })
  .demandCommand(1)
  .help()
  .parse()
```

```bash
node notes-yargs.js add --title "My note"
node notes-yargs.js --help
```

## 🔥 Challenge

1. Build a CLI tool that manages a JSON "todo list" — support `add`, `list`, `done <id>`, and `clear` commands.
2. Add a `--json` flag that outputs the todo list as formatted JSON instead of human-readable text.
3. Add timestamps to each todo item, and add a `due` option: `--due "tomorrow"`.

## ✅ Key Takeaways

- `fs.readFileSync` / `fs.writeFileSync` — synchronous, simple, blocks execution
- `fs.readFile` / `fs.writeFile` — async, callback-based, non-blocking
- Always use `path.join(__dirname, ...)` to build safe, cross-platform paths
- `process.argv` gives you command-line arguments (first two are node + script path)
- `yargs` makes building CLI tools way more ergonomic
