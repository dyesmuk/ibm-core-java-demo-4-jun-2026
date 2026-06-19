# Socket.io — Quick Reference

## The Two Core Objects

| Object | What it is | Available in |
|--------|-----------|--------------|
| `io` | The entire server — represents all connections | Server only |
| `socket` | One individual connection — one browser tab | Server + Client |

---

## Emission Methods

| Method | Sends to |
|--------|---------|
| `io.emit(event, data)` | Every connected socket |
| `socket.emit(event, data)` | This socket only (sender) |
| `socket.to(room).emit(event, data)` | Room members **except** sender |
| `io.to(room).emit(event, data)` | Room members **including** sender |
| `socket.to(socketId).emit(event, data)` | One specific socket (private message) |
| `socket.broadcast.emit(event, data)` | Everyone **except** this socket (no room) |

---

## Server-side Methods

| Method | What it does |
|--------|-------------|
| `io.on('connection', cb)` | Listen for new client connections |
| `socket.on(event, cb)` | Listen for a custom event from this client |
| `socket.join(room)` | Add this socket to a named room |
| `socket.leave(room)` | Remove this socket from a room |
| `socket.disconnect()` | Force-disconnect this socket |

---

## Server-side Properties

| Property | What it holds | Example value |
|----------|--------------|---------------|
| `socket.id` | Unique ID for this connection | `"T9sneGYO3NreXAllAAAB"` |
| `socket.rooms` | Set of rooms this socket is in | `Set { "T9sne...", "general" }` |
| `socket.handshake.query` | Query params passed on connect | `{ token: "abc123" }` |
| `socket.handshake.address` | Client's IP address | `"192.168.1.5"` |
| `io.engine.clientsCount` | Total number of connected clients | `42` |

---

## Client-side Methods

| Method | What it does |
|--------|-------------|
| `io(url)` | Connect to the server |
| `socket.emit(event, data, cb)` | Send an event to the server |
| `socket.on(event, cb)` | Listen for an event from the server |
| `socket.off(event)` | Stop listening for an event |
| `socket.disconnect()` | Disconnect from the server |

---

## Client-side Properties

| Property | What it holds | Example value |
|----------|--------------|---------------|
| `socket.id` | This client's socket ID | `"T9sneGYO3NreXAllAAAB"` |
| `socket.connected` | Is currently connected? | `true` / `false` |

---

## Built-in Events — Server

| Event | When it fires |
|-------|--------------|
| `connection` | A new client connects |
| `disconnect` | A client disconnects |
| `disconnecting` | Client about to disconnect (rooms still accessible) |

```javascript
io.on('connection', (socket) => {

  socket.on('disconnecting', () => {
    console.log(socket.rooms); // can still read rooms here
  });

  socket.on('disconnect', (reason) => {
    console.log('Disconnected:', reason);
  });

});
```

---

## Built-in Events — Client

| Event | When it fires |
|-------|--------------|
| `connect` | Successfully connected to server |
| `disconnect` | Disconnected from server |
| `connect_error` | Failed to connect |
| `reconnect` | Successfully reconnected |

```javascript
socket.on('connect', () => { console.log('Connected:', socket.id); });
socket.on('disconnect', (reason) => { console.log('Disconnected:', reason); });
socket.on('connect_error', (err) => { console.error('Error:', err.message); });
socket.on('reconnect', (attempt) => { console.log('Reconnected after', attempt, 'attempts'); });
```

---

## Disconnect Reasons

| Reason | Meaning |
|--------|---------|
| `transport close` | Browser tab closed or network dropped |
| `server namespace disconnect` | Server called `socket.disconnect()` |
| `client namespace disconnect` | Client called `socket.disconnect()` |
| `ping timeout` | Client stopped responding to heartbeats |

---

## Acknowledgement Pattern

One side emits with a callback, the other side calls it to confirm receipt.

```javascript
// Client — emits and waits for confirmation
socket.emit('join', { username: 'Alice', room: 'general' }, (error) => {
  if (error) {
    alert(error);         // server sent back an error
  } else {
    console.log('Joined successfully');
  }
});

// Server — receives and calls back
socket.on('join', (data, callback) => {
  const { error, user } = addUser(data);
  if (error) return callback(error);  // sends error string to client
  callback();                         // sends success (undefined = no error)
});
```

---

## Attach Custom Data to a Socket

Useful for storing user info once and accessing it across all event handlers.

```javascript
socket.on('join', ({ username, room }) => {
  socket.username = username;   // attach to the socket object
  socket.room     = room;
});

socket.on('sendMessage', (text) => {
  console.log(socket.username); // accessible in every other handler
  io.to(socket.room).emit('message', { username: socket.username, text });
});
```

---

## Namespace vs Room

| | Namespace | Room |
|-|-----------|------|
| What | A separate connection endpoint | A logical group within a namespace |
| How client joins | Connects to it: `io('/admin')` | Server calls `socket.join('room')` |
| Default | `/` — all sockets are here | None — must join explicitly |
| Use case | Separate concerns on one server | Chat rooms, game lobbies, doc sessions |

```javascript
// Namespace
const adminNamespace = io.of('/admin');
adminNamespace.on('connection', (socket) => {
  console.log('Admin connected');
});

// Room (within default namespace)
socket.join('general');
io.to('general').emit('message', { text: 'Hello room!' });
```

---

## Server Setup Options

```javascript
const io = new Server(httpServer, {
  cors:             { origin: '*' },   // allowed origins
  pingTimeout:      5000,              // ms before disconnect if no pong
  pingInterval:     10000,             // ms between heartbeat pings
  maxHttpBufferSize: 1e6,              // max message size (default 1 MB)
});
```

---

## Minimal Working Example

**server.js**
```javascript
import { createServer } from 'http';
import { Server } from 'socket.io';

const io = new Server(createServer());

io.on('connection', (socket) => {
  console.log('Connected:', socket.id);

  socket.on('message', (text) => {
    io.emit('message', `${socket.id} says: ${text}`);
  });

  socket.on('disconnect', () => {
    console.log('Disconnected:', socket.id);
  });
});

io.listen(3001);
```

**client.js**
```javascript
import { io } from 'socket.io-client';

const socket = io('http://localhost:3001');

socket.on('connect', () => {
  socket.emit('message', 'Hello everyone!');
});

socket.on('message', (data) => {
  console.log(data);
});
```
