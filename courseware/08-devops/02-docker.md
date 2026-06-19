# 02 — Docker
> **DevOps Hands-On Series** | Module 2 of 6 | Node.js Express project

---

## The Problem Docker Solves

"It works on my machine."

Docker kills that sentence. It packages your app, its runtime, and all its dependencies into a single portable **image** that runs identically everywhere — your laptop, the CI server, a cloud VM, a Kubernetes cluster.

Git solves the "which version of code?" problem. Docker solves the "which version of everything else?" problem:

| Git solves | Docker solves |
|---|---|
| Which version of the code? | Which version of Node, OS, libraries? |
| Code shared via GitHub | App + runtime shared via Docker Hub |
| `git clone` → you have the source | `docker pull` → you have the running environment |
| `.gitignore` excludes `node_modules` | Docker image *includes* everything to run |

The complete delivery pipeline you're building:

```
git push → GitHub → Jenkins → docker build → docker push → Docker Hub
                                                               ↓
                                                   Server: docker pull & run
                                                   (or Kubernetes pulls it)
```

**Prerequisites:** Module 01 done. Docker Desktop installed on Windows 11.

**What you'll build:** A Node.js Express app → Dockerised → pushed to Docker Hub as `yourname/hello-express:1.0`.

---

## Part 1 — Your First Container

### Start with Git (Always)

```powershell
mkdir hello-docker
cd hello-docker
git init
```

Create `.gitignore` **before writing any code**:

```
node_modules/
.env
*.log
dist/
```

Create `app.js`:

```js
const http = require('http');

const server = http.createServer((req, res) => {
  res.end('Hello from inside Docker!');
});

server.listen(3000, () => console.log('Server running on port 3000'));
```

### Write Your First Dockerfile

A `Dockerfile` is a recipe for building a Docker image — line by line:

```dockerfile
FROM node:18-alpine

WORKDIR /app

COPY app.js .

CMD ["node", "app.js"]
```

| Line | What it does |
|---|---|
| `FROM node:18-alpine` | Start from an existing image that has Node 18. `alpine` = tiny Linux (~5MB base) |
| `WORKDIR /app` | All following commands run inside `/app` in the container |
| `COPY app.js .` | Copy your file from Windows into the image |
| `CMD [...]` | Command to run when the container starts |

### Build and Run

```powershell
docker build -t hello-docker .
docker run -p 3000:3000 hello-docker
```

Open `http://localhost:3000` → **Hello from inside Docker!**

```powershell
docker ps                          # see running containers
docker stop <container-id>         # stop it
```

### The Mental Model

```
Your files        →   Image                   →   Container
(app.js +             (Node runtime +              (Running process,
Dockerfile)            your app, frozen)             isolated, alive)

   Recipe             Cake mould (reusable)          Actual cake
```

- `docker build` → creates an **image** (like a Git commit — immutable snapshot)
- `docker run` → creates and starts a **container** from the image

### Commit the Dockerfile

```powershell
git add .
git commit -m "Add Dockerfile for hello-docker"
```

The Dockerfile belongs in the repo. Anyone who clones it can build the identical image.

**Quick knowledge check:**
- Can you run `docker run` twice and get two independent servers? → Yes. Same image, two separate containers. That's how scaling works.
- Can you change `app.js` and refresh the browser? → No. You must `docker build` again. The image is a frozen snapshot, not a live folder.
- Where is Node.js installed on your laptop? → Inside the container. The runtime is bundled. Doesn't matter what's on your host.

---

## Part 2 — Container Lifecycle

A stopped container still exists — it's paused, not deleted.

```powershell
docker ps -a                    # all containers including stopped
docker start <container-id>     # restart a stopped container
docker start -a <container-id>  # restart and watch logs
```

| Command | Effect |
|---|---|
| `docker run` | Creates a **new** container and starts it |
| `docker start` | Starts an **existing** stopped container |
| `docker stop` | Stops a running container (still exists) |
| `docker rm` | Permanently deletes the container |

The lifecycle: `run → stop → start → stop → start → rm`

`docker run` is only used **once** per container. After that, it's `start` and `stop`.

---

## Part 3 — Express App + Image Layers

### Upgrade the App

Update `app.js`:

```js
const express = require('express');
const app = express();

app.get('/', (req, res) => {
  res.send('Hello from Express inside Docker!');
});

app.listen(3000, () => console.log('Server running on port 3000'));
```

Create `package.json`:

```json
{
  "name": "hello-docker",
  "version": "1.0.0",
  "main": "app.js",
  "dependencies": {
    "express": "4.18.2"
  }
}
```

### .dockerignore — Docker's .gitignore

Create `.dockerignore` in the project root:

```
node_modules
.git
.gitignore
*.log
.env
```

Without this, `COPY . .` would copy your Windows-built `node_modules` into the Linux container — breaking the app and bloating the image. Always create `.dockerignore` alongside `.gitignore`.

| File | Tells | What to exclude |
|---|---|---|
| `.gitignore` | Git | Don't track these |
| `.dockerignore` | Docker | Don't include in build context |
| Both should exclude | `node_modules`, `.env`, logs | Generated, OS-specific, or secret |

### Updated Dockerfile

```dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package.json .

RUN npm install

COPY . .

CMD ["node", "app.js"]
```

```powershell
docker build -t hello-express .
docker run -p 3000:3000 hello-express
```

### Image Layers and Caching

Every Dockerfile instruction creates a **layer**. Docker caches unchanged layers. Run the build twice without changes:

```
=> CACHED [3/5] COPY package.json .
=> CACHED [4/5] RUN npm install        ← from cache, zero time spent
=> CACHED [5/5] COPY . .
```

Now change one word in `app.js` and rebuild:

```
=> CACHED [4/5] RUN npm install        ← still cached, package.json unchanged
=> [5/5] COPY . .                      ← only this re-runs
```

**The caching rule:** Copy what changes least, first. `package.json` rarely changes. `app.js` changes constantly. Always put dependencies before source code.

```dockerfile
# ❌ BAD — every app.js change re-runs npm install (slow!)
COPY . .
RUN npm install

# ✅ GOOD — npm install only re-runs when package.json changes
COPY package.json .
RUN npm install
COPY . .
```

### Commit to Git

```powershell
git add .
git commit -m "Add Express, package.json, .dockerignore"
git push
```

---

## Part 4 — Docker Engine, Storage & Networking

### How Docker Actually Works on Windows 11

```
Docker CLI (the docker commands you type)
      ↓  REST API
Docker Daemon (dockerd — background service inside WSL2 VM)
      ↓
containerd → runc (what actually starts containers using Linux namespaces)
```

Docker Desktop runs a hidden WSL2 Linux VM on your Windows machine. The daemon lives inside that VM. Your PowerShell terminal talks to it over a named pipe.

| Component | Role |
|---|---|
| **Docker CLI** | The `docker` command — sends requests to the daemon |
| **Docker Daemon** | Background service managing everything |
| **containerd** | Manages container lifecycle |
| **runc** | Actually starts containers using Linux kernel features |

### Storage: Volumes vs Bind Mounts

**Volumes** — Docker-managed, recommended for production data:

```powershell
docker volume create mydata
docker run -v mydata:/data hello-express

docker volume ls
docker volume inspect mydata
docker volume rm mydata
```

**Bind Mounts** — Maps a folder from your Windows machine into the container. Great for development (edit a file, the container sees it instantly):

```powershell
docker run -p 3000:3000 -v ${PWD}:/app hello-express
```

| | Volume | Bind Mount |
|---|---|---|
| Storage location | Docker-managed (inside WSL2) | Your Windows folder |
| Production-ready | ✅ | ❌ |
| Dev live-reload | Slower | ✅ |
| Survives `docker rm` | ✅ | ✅ (it's your files) |

### Networking

```powershell
docker network ls                           # see existing networks
docker network create my-network            # create a custom network

# Containers on the same custom network can reach each other by name
docker run -d --network my-network --name mongo mongo:6
docker run -d --network my-network --name app -p 3000:3000 hello-express

docker network inspect my-network
```

**Default Docker networks:**

| Network | Description |
|---|---|
| `bridge` | Default. Containers get private IPs — communicate by IP, not name. |
| `host` | Shares host network stack. Not available on Windows. |
| `none` | Complete network isolation. |
| Custom bridge | Like `bridge` but Docker provides DNS — containers talk **by name**. ✅ Use this. |

Docker Compose always creates a custom bridge network automatically — which is why `mongo:27017` works as a hostname without any extra config.

---

## Part 5 — Docker Compose: Running Multiple Containers

One command to bring up your entire application stack.

### Project Structure

```
hello-docker\
  ├── app.js
  ├── package.json
  ├── Dockerfile
  ├── .dockerignore
  ├── .gitignore
  └── docker-compose.yml
```

### Update `package.json`

```json
{
  "name": "hello-docker",
  "version": "1.0.0",
  "main": "app.js",
  "dependencies": {
    "express": "4.18.2",
    "mongoose": "7.6.3"
  }
}
```

### Update `app.js`

```js
const express = require('express');
const mongoose = require('mongoose');

const app = express();

mongoose.connect('mongodb://mongo:27017/hellodb')
  .then(() => console.log('Connected to MongoDB'))
  .catch(err => console.error('MongoDB error:', err));

app.get('/', (req, res) => {
  const status = mongoose.connection.readyState === 1 ? 'Connected ✅' : 'Disconnected ❌';
  res.send(`Hello from Express! MongoDB: ${status}`);
});

app.listen(3000, () => console.log('Server running on port 3000'));
```

The hostname `mongo` in the connection string is the **service name** in Compose. Docker's built-in DNS resolves it automatically.

### `docker-compose.yml`

```yaml
version: '3'

services:

  app:
    build: .
    ports:
      - "3000:3000"
    depends_on:
      - mongo
    environment:
      - NODE_ENV=production

  mongo:
    image: mongo:6
    volumes:
      - mongo-data:/data/db

volumes:
  mongo-data:
```

### Compose Commands

```powershell
docker compose up -d                # start in background
docker compose up --build -d        # rebuild image then start
docker compose ps                   # service status
docker compose logs                 # all logs
docker compose logs -f app          # follow app logs live
docker compose down                 # stop (keep data)
docker compose down -v              # stop + delete volumes (wipes MongoDB)
```

Open `http://localhost:3000` → **Hello from Express! MongoDB: Connected ✅**

### Three Concepts Compose Teaches

**1. Service name = hostname** — `mongo:27017` works because Compose puts both containers on a custom bridge network and registers DNS by service name.

**2. Automatic private network** — No manual `docker network create`. Compose does it. The outside world can only reach what you explicitly map with `ports:`.

**3. Volumes = persistence** — The `mongo-data` volume outlives containers. `docker compose down` then `up` — your data is still there. `down -v` wipes it.

### Commit

```powershell
git add .
git commit -m "Add docker-compose with MongoDB"
git push
```

---

## Part 6 — Docker Registry (Docker Hub)

### Why a Registry?

```
Git flow:    code → git push → GitHub     → team clones
Docker flow: image → docker push → Docker Hub → servers pull
```

Both GitHub and Docker Hub serve the same purpose for their domain.

### Login and Push

```powershell
docker login

docker tag hello-express yourname/hello-express:1.0
docker push yourname/hello-express:1.0
```

### Pull and Run From Anywhere

```powershell
# No source code, no Node install needed — just Docker
docker pull yourname/hello-express:1.0
docker run -p 3000:3000 yourname/hello-express:1.0
```

This is the deployment model. The server never sees your source code.

### Use the Registry Image in Compose

```yaml
services:
  app:
    image: yourname/hello-express:1.0    # pull from Docker Hub instead of building
    ports:
      - "3000:3000"
    depends_on:
      - mongo
```

---

## Part 7 — Images Deep Dive

```powershell
docker history hello-express        # see all layers + sizes
docker inspect hello-express        # full metadata as JSON
docker image prune                  # remove dangling (untagged) images
docker image prune -a               # remove all unused images
```

### Multi-Stage Builds (Lean Production Images)

```dockerfile
# Stage 1 — install everything including dev tools
FROM node:18-alpine AS builder
WORKDIR /app
COPY package.json .
RUN npm install --production

# Stage 2 — lean runtime image, only copies what's needed
FROM node:18-alpine
WORKDIR /app
COPY --from=builder /app/node_modules ./node_modules
COPY app.js .
CMD ["node", "app.js"]
```

The final image has no build tools, no npm cache, no dev dependencies — only the runtime code.

### Base Image Comparison

| Base image | Approx. size | Notes |
|---|---|---|
| `node:18` | ~950MB | Full Debian — max compatibility |
| `node:18-slim` | ~200MB | Debian, fewer packages |
| `node:18-alpine` | ~50MB | ✅ Recommended — Alpine Linux |

---

## Part 8 — Container Orchestration: Docker Swarm & Kubernetes

### The Scaling Problem

Docker Compose runs on **one machine**. When you need more:
- One machine has limits
- If it crashes, everything crashes
- Updates require downtime

**Orchestration** distributes containers across a **cluster** of machines, restarts failed ones, and handles updates with zero downtime.

### Docker Swarm

Swarm is Docker's built-in clustering system — zero extra install needed:

```powershell
# Turn this machine into a Swarm manager
docker swarm init

# Deploy a multi-service stack
docker stack deploy -c docker-compose.yml hello-stack

# Scale to 5 replicas
docker service scale hello-stack_app=5

# See running containers across all nodes
docker service ps hello-stack_app

# Tear it all down
docker stack rm hello-stack
docker swarm leave --force
```

**Core Swarm concepts:**

| Concept | Meaning |
|---|---|
| **Swarm** | A cluster of Docker hosts managed as one unit |
| **Manager node** | Schedules work, maintains desired state |
| **Worker node** | Runs containers assigned by the manager |
| **Service** | "Run N replicas of this image" — the Swarm unit |
| **Task** | One running container instance of a service |

### Swarm vs Kubernetes

| Feature | Docker Swarm | Kubernetes |
|---|---|---|
| Setup | Built-in, simple | Separate install, complex |
| Learning curve | Low | Steep |
| Features | Basic orchestration | Full platform (autoscaling, RBAC, more) |
| Industry adoption | Shrinking | Dominant standard |
| CLI | `docker stack`, `docker service` | `kubectl` |

Swarm covers the Docker curriculum. Kubernetes is the industry standard and gets a full module next.

### The Orchestration Progression

```
Docker Compose    →   Docker Swarm     →   Kubernetes
(one machine,         (multi-machine,       (multi-machine,
development)          simple)               full production platform)

Same image works in all three. The orchestration layer changes, the container doesn't.
```

---

## Quick Reference

### All Docker Commands

```powershell
# Images
docker images
docker pull mongo:6
docker tag hello-express yourname/hello-express:1.0
docker push yourname/hello-express:1.0
docker rmi hello-express
docker history hello-express
docker image prune

# Containers
docker ps
docker ps -a
docker run -p 3000:3000 <image>
docker run -d -p 3000:3000 <image>
docker start <id>
docker stop <id>
docker rm <id>
docker logs <id>
docker logs -f <id>
docker exec -it <id> sh

# Volumes & Networks
docker volume ls / create / rm
docker network ls / create / inspect

# Compose
docker compose up -d
docker compose up --build -d
docker compose down
docker compose down -v
docker compose ps
docker compose logs -f app

# Swarm
docker swarm init
docker stack deploy -c file.yml mystack
docker service scale mystack_app=5
docker stack rm mystack
docker swarm leave --force

# Cleanup
docker system prune
docker system prune -a
```

### Key Distinctions

| These seem similar... | But they're different because... |
|---|---|
| `docker run` vs `docker start` | `run` creates a new container. `start` restarts an existing stopped one. |
| Image vs Container | Image = frozen blueprint. Container = live running instance. |
| `docker stop` vs `docker rm` | `stop` pauses (still exists). `rm` deletes permanently. |
| Volume vs Bind mount | Volume = Docker-managed, portable. Bind mount = your Windows folder, dev-only. |
| `RUN` vs `CMD` in Dockerfile | `RUN` executes at build time. `CMD` executes at container start. |
| `COPY` vs `ADD` | `COPY` copies files. `ADD` can also extract archives. Default to `COPY`. |
| Swarm vs Kubernetes | Swarm = simple built-in. Kubernetes = powerful industry standard. |

---

> **Next → 03 YAML** — Kubernetes is configured almost entirely in YAML. Before diving in, the next (short) module covers YAML syntax, indentation rules, and common mistakes. Reading it once saves hours of debugging YAML-related errors in Kubernetes.
