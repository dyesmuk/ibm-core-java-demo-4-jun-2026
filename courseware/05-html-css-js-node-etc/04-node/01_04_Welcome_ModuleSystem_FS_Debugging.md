# Node.js — Module 01: Welcome, Install & First Steps

## What Is Node.js?

Node.js is a **JavaScript runtime built on Chrome's V8 engine**. It lets you run JavaScript on the server — outside the browser. Key characteristics:

- **Event-driven, non-blocking I/O** — handles thousands of concurrent connections with a single thread
- **npm** — world's largest package ecosystem (2M+ packages)
- **Same language front-to-back** — JavaScript on client and server
- **Excellent for** — REST APIs, real-time apps, CLI tools, microservices, build tools

### Node.js vs Java Spring Boot

| Aspect | Node.js | Spring Boot |
|--------|---------|-------------|
| Concurrency | Event loop (single thread + async) | Thread pool (multi-threaded) |
| Performance | Excellent for I/O-heavy (APIs, real-time) | Excellent for CPU-heavy (computation) |
| Startup time | Fast (milliseconds) | Slower (seconds) |
| Memory | Low footprint | Higher (JVM overhead) |
| Type safety | Optional (TypeScript) | Built-in (Java) |
| Ecosystem | npm (2M+ packages) | Maven Central |
| REST API | Express, Fastify, NestJS | Spring MVC, Spring WebFlux |

---

## Installing Node.js

### Option 1: nvm (Recommended — manage multiple versions)

```bash
# Install nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash

# Install Node.js LTS
nvm install --lts
nvm use --lts

# Verify
node --version    # v20.x.x
npm --version     # 10.x.x
```

### Option 2: Direct Install

Download from [nodejs.org](https://nodejs.org) → LTS version.

---

## The Node.js REPL

```bash
node           # enter REPL
> 2 + 2        # 4
> .help        # show commands
> .exit        # exit
> Ctrl+C twice # force exit
```

---

## Your First Node.js Script

```javascript
// app.js
console.log('Hello from Node.js!');
console.log('Node version:', process.version);
console.log('Platform:', process.platform);
console.log('Current directory:', process.cwd());
console.log('Environment:', process.env.NODE_ENV ?? 'development');
```

```bash
node app.js
# Hello from Node.js!
# Node version: v20.11.0
# Platform: linux
# Current directory: /home/user/myapp
# Environment: development
```

---

## npm — Package Manager

```bash
# Initialise a new project
npm init             # interactive
npm init -y          # all defaults (quick start)

# Install dependencies
npm install express          # production dependency
npm install jest --save-dev  # dev dependency (testing, build tools)
npm install -g nodemon       # global install (CLI tools)

# package.json scripts
npm run start
npm run dev
npm test

# Update / remove
npm update express
npm uninstall express

# List installed packages
npm list --depth=0
```

### `package.json` Structure

```json
{
  "name": "my-node-app",
  "version": "1.0.0",
  "description": "A Node.js application",
  "main": "src/index.js",
  "type": "module",
  "scripts": {
    "start": "node src/index.js",
    "dev": "nodemon src/index.js",
    "test": "jest",
    "lint": "eslint src/"
  },
  "dependencies": {
    "express": "^4.18.2",
    "mongoose": "^8.0.0"
  },
  "devDependencies": {
    "jest": "^29.7.0",
    "nodemon": "^3.0.0",
    "eslint": "^8.0.0"
  },
  "engines": {
    "node": ">=18.0.0"
  }
}
```

---

---

# Node.js — Module 02: The Module System

## CommonJS (Traditional Node.js)

```javascript
// utils.js — exporting
const add = (a, b) => a + b;
const subtract = (a, b) => a - b;

function formatCurrency(amount) {
  return `₹${amount.toFixed(2)}`;
}

module.exports = { add, subtract, formatCurrency };
// or: exports.add = add; (shorthand but can't reassign exports directly)

// Single default export
module.exports = class UserService { ... };
```

```javascript
// app.js — importing
const { add, subtract } = require('./utils');
const UserService = require('./services/user');

// Built-in modules (no install needed)
const fs      = require('fs');
const path    = require('path');
const http    = require('http');
const os      = require('os');
const crypto  = require('crypto');
const events  = require('events');

// npm packages
const express = require('express');
```

## ES Modules in Node.js (Modern Approach)

```javascript
// package.json
{ "type": "module" }

// utils.mjs or utils.js (with type:module in package.json)
export const add = (a, b) => a + b;
export default class Calculator { ... }

// app.js
import { add } from './utils.js';   // .js extension required in ESM Node
import Calculator from './utils.js';
import fs from 'fs';                // built-ins work too
import { readFile } from 'fs/promises';
```

## Notes App — Module System Example

```javascript
// notes.js — data layer
import { readFileSync, writeFileSync } from 'fs';

const DB_PATH = './notes.json';

function loadNotes() {
  try {
    const dataBuffer = readFileSync(DB_PATH);
    return JSON.parse(dataBuffer.toString());
  } catch {
    return [];
  }
}

function saveNotes(notes) {
  writeFileSync(DB_PATH, JSON.stringify(notes, null, 2));
}

export function addNote(title, body) {
  const notes = loadNotes();
  if (notes.some(n => n.title === title)) {
    throw new Error(`Note with title "${title}" already exists`);
  }
  notes.push({ title, body, createdAt: new Date().toISOString() });
  saveNotes(notes);
  return notes;
}

export function removeNote(title) {
  const notes = loadNotes();
  const remaining = notes.filter(n => n.title !== title);
  if (remaining.length === notes.length) {
    throw new Error(`Note "${title}" not found`);
  }
  saveNotes(remaining);
}

export function listNotes() {
  return loadNotes();
}

export function getNote(title) {
  return loadNotes().find(n => n.title === title) ?? null;
}
```

```javascript
// app.js — CLI entry point
import { addNote, removeNote, listNotes, getNote } from './notes.js';

const [,, command, ...args] = process.argv;

switch (command) {
  case 'add':
    addNote(args[0], args[1]);
    console.log('Note added!');
    break;
  case 'remove':
    removeNote(args[0]);
    console.log('Note removed!');
    break;
  case 'list':
    listNotes().forEach(n => console.log(`- ${n.title}`));
    break;
  case 'read':
    const note = getNote(args[0]);
    note ? console.log(note.body) : console.log('Note not found');
    break;
  default:
    console.log('Commands: add, remove, list, read');
}
```

```bash
node app.js add "First Note" "This is my first note"
node app.js list
node app.js read "First Note"
node app.js remove "First Note"
```

---

---

# Node.js — Module 03: File System & Command Line Args

## `fs` Module

```javascript
import fs from 'fs';
import { promises as fsp } from 'fs';
import path from 'path';

// ── Synchronous (blocks the event loop — use only at startup) ──
const data = fs.readFileSync('./config.json', 'utf8');
const parsed = JSON.parse(data);
fs.writeFileSync('./output.txt', 'Hello', 'utf8');
fs.appendFileSync('./log.txt', `[${new Date()}] Server started\n`);
const files = fs.readdirSync('./src');
const stat = fs.statSync('./file.txt');
stat.isFile()        // true
stat.isDirectory()   // false
fs.mkdirSync('./logs', { recursive: true });
fs.rmSync('./temp', { recursive: true, force: true });
fs.existsSync('./file.txt')  // true/false

// ── Async with Promises (preferred for server code) ──
const data = await fsp.readFile('./config.json', 'utf8');
await fsp.writeFile('./output.txt', content, 'utf8');
await fsp.appendFile('./log.txt', line);
const files = await fsp.readdir('./src');
await fsp.mkdir('./logs', { recursive: true });
await fsp.rm('./temp', { recursive: true });
await fsp.copyFile('./src.txt', './dest.txt');
await fsp.rename('./old.txt', './new.txt');

// ── Streams (for large files — don't load entire file into memory) ──
import { createReadStream, createWriteStream } from 'fs';

const readStream  = createReadStream('./large-file.csv', 'utf8');
const writeStream = createWriteStream('./output.csv');

readStream.pipe(writeStream);   // pipe: read → write

// Process line by line
import readline from 'readline';

const rl = readline.createInterface({
  input: createReadStream('./data.txt'),
  crlfDelay: Infinity
});

for await (const line of rl) {
  console.log(line);
}
```

## The `path` Module

```javascript
import path from 'path';

// __dirname equivalent in ES modules
import { fileURLToPath } from 'url';
const __filename = fileURLToPath(import.meta.url);
const __dirname  = path.dirname(__filename);

path.join('/usr', 'local', 'bin')      // '/usr/local/bin'
path.join(__dirname, 'data', 'file.json')  // absolute path to file

path.resolve('src', 'index.js')        // absolute path from cwd
path.resolve('/base', 'src', 'file')   // '/base/src/file'

path.basename('/path/to/file.js')      // 'file.js'
path.basename('/path/to/file.js', '.js')  // 'file'
path.extname('file.html')             // '.html'
path.dirname('/path/to/file.js')      // '/path/to'
path.parse('/path/to/file.js')
// { root:'/', dir:'/path/to', base:'file.js', ext:'.js', name:'file' }
path.format({ dir: '/path/to', base: 'file.js' })  // '/path/to/file.js'
path.isAbsolute('/usr/local')  // true
path.isAbsolute('src/index')   // false
```

## Command Line Arguments with `yargs`

```bash
npm install yargs
```

```javascript
// app.js
import yargs from 'yargs';
import { hideBin } from 'yargs/helpers';

yargs(hideBin(process.argv))
  .command({
    command: 'add',
    describe: 'Add a new note',
    builder: {
      title: { describe: 'Note title', type: 'string', demandOption: true },
      body:  { describe: 'Note body',  type: 'string', demandOption: true }
    },
    handler(argv) {
      console.log(`Adding: "${argv.title}"`);
      addNote(argv.title, argv.body);
    }
  })
  .command({
    command: 'list',
    describe: 'List all notes',
    handler() { listNotes().forEach(n => console.log(`- ${n.title}`)); }
  })
  .demandCommand(1)
  .help()
  .argv;
```

```bash
node app.js add --title="Meeting Notes" --body="Discussed Q4 goals"
node app.js list
node app.js --help
```

---

---

# Node.js — Module 04: Debugging

## Console Methods

```javascript
console.log('Basic log');
console.error('Error message');      // outputs to stderr
console.warn('Warning message');
console.info('Info message');
console.debug('Debug message');      // filtered out in production

// Formatted output
console.log('%s has %d items', 'Cart', 3);
console.log('%o', { user: 'Alice' });   // pretty-print object

// Table
console.table([
  { name: 'Alice', role: 'admin' },
  { name: 'Bob',   role: 'user'  }
]);

// Grouping
console.group('HTTP Request');
console.log('Method: GET');
console.log('URL: /api/users');
console.groupEnd();

// Timing
console.time('database-query');
await db.query('SELECT * FROM users');
console.timeEnd('database-query');  // 'database-query: 42.123ms'

// Stack trace
console.trace('Where am I?');
```

## The `--inspect` Flag & VS Code Debugger

```bash
# Start with debugger
node --inspect app.js         # listen on port 9229
node --inspect-brk app.js     # break on first line
```

### VS Code launch.json

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "node",
      "request": "launch",
      "name": "Debug App",
      "skipFiles": ["<node_internals>/**"],
      "program": "${workspaceFolder}/src/app.js",
      "env": { "NODE_ENV": "development" }
    },
    {
      "type": "node",
      "request": "attach",
      "name": "Attach to Node",
      "port": 9229
    }
  ]
}
```

## Error Types and Stack Traces

```javascript
// Reading a stack trace
// TypeError: Cannot read properties of undefined (reading 'name')
//     at getUser (app.js:15:22)   ← where error occurred
//     at processUser (app.js:8:18)
//     at main (app.js:3:3)

// Common Node.js error codes
// ENOENT: No such file or directory
// EACCES: Permission denied
// ECONNREFUSED: Connection refused
// ETIMEDOUT: Connection timed out
// EADDRINUSE: Address already in use (port taken)

// Unhandled errors — always handle these!
process.on('uncaughtException', (err) => {
  console.error('Uncaught Exception:', err.stack);
  process.exit(1);   // must exit — process state may be corrupted
});

process.on('unhandledRejection', (reason, promise) => {
  console.error('Unhandled Rejection at:', promise, 'reason:', reason);
  process.exit(1);
});
```

## Using `nodemon` for Development

```bash
npm install -g nodemon

nodemon app.js           # restarts on file change
nodemon --watch src app.js  # watch specific directory
```

```json
// nodemon.json configuration
{
  "watch": ["src"],
  "ext": "js,json",
  "ignore": ["src/**/*.test.js"],
  "exec": "node src/app.js"
}
```
