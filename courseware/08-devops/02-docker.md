# Module 02 — Docker

## Learning Objectives
- Understand what Docker is and the problem it solves
- Use core Docker CLI commands confidently
- Run containers with various options
- Build custom Docker images
- Use Docker Compose for multi-container apps
- Understand Docker networking and storage
- Push/pull images from a registry
- EMS: containerise the Node.js API and run it anywhere

---

## 2.1 Introduction — Why Docker?

### The "Works on My Machine" Problem

```
Developer's machine:         Production server:
  Node.js 20                   Node.js 16
  MongoDB 7                    MongoDB 4.4
  npm 10                       npm 6
  Ubuntu 22                    CentOS 7

Result: App works in dev, crashes in production.
```

Docker solves this by packaging the application **together with everything it needs** — runtime, dependencies, configuration — into a **container** that runs identically everywhere.

### Containers vs Virtual Machines

```
Virtual Machine:                    Container:
┌─────────────────────┐            ┌─────────────────────┐
│   App A             │            │   App A             │
│   Libraries         │            │   Libraries         │
│   Guest OS (2GB)    │            │   (No OS — 50MB)    │
├─────────────────────┤            ├─────────────────────┤
│   Hypervisor        │            │   Docker Engine     │
├─────────────────────┤            ├─────────────────────┤
│   Host OS           │            │   Host OS           │
│   Hardware          │            │   Hardware          │
└─────────────────────┘            └─────────────────────┘
 Heavy, slow to start               Lightweight, starts in seconds
 Full OS per VM                     Shares host OS kernel
```

### Key Concepts

| Term | Meaning |
|------|---------|
| **Image** | A read-only blueprint — like a class in OOP |
| **Container** | A running instance of an image — like an object |
| **Dockerfile** | Instructions to build a custom image |
| **Registry** | Storage for images (Docker Hub, ECR, ACR) |
| **Docker Compose** | Tool to run multiple containers together |

---

## 2.2 Installing Docker

```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install docker.io
sudo systemctl start docker
sudo systemctl enable docker

# Add your user to docker group (avoid sudo every time)
sudo usermod -aG docker $USER
newgrp docker   # apply without logging out

# Mac / Windows: install Docker Desktop from https://docker.com

# Verify
docker --version        # Docker version 26.x
docker run hello-world  # runs a test container
```

---

## 2.3 Docker Commands

### Image Commands

```bash
# List images on your machine
docker images
docker image ls

# Pull an image from Docker Hub (without running it)
docker pull node:20-alpine
docker pull mongo:7
docker pull nginx:stable-alpine

# Remove an image
docker rmi node:20-alpine
docker image rm node:20-alpine

# Search Docker Hub
docker search node

# Show image details / layers
docker inspect node:20-alpine
docker history node:20-alpine
```

### Container Commands

```bash
# List running containers
docker ps

# List ALL containers (including stopped)
docker ps -a

# Stop a running container
docker stop <container-id or name>

# Start a stopped container
docker start <container-id or name>

# Remove a container (must be stopped first)
docker rm <container-id or name>

# Remove all stopped containers at once
docker container prune

# View container logs
docker logs <container-id>
docker logs -f <container-id>    # follow (live)
docker logs --tail 50 <id>       # last 50 lines

# Execute a command inside a running container
docker exec -it <container-id> bash
docker exec -it <container-id> sh    # if bash not available (Alpine)

# Copy files between host and container
docker cp localfile.txt <container-id>:/app/
docker cp <container-id>:/app/output.txt ./
```

### System Commands

```bash
# Show disk usage
docker system df

# Remove all unused data (images, containers, networks, volumes)
docker system prune
docker system prune -a    # also removes unused images

# Show Docker info
docker info
docker version
```

---

## 2.4 Docker Run

`docker run` is the most important command. It pulls (if needed) and starts a container.

```bash
# Basic run — starts container, runs command, exits
docker run ubuntu echo "Hello from Ubuntu"

# Run interactively (-it = interactive + tty)
docker run -it ubuntu bash
# Now you're inside the container
# exit to leave

# Run in background / detached (-d)
docker run -d nginx

# Give the container a name
docker run -d --name my-nginx nginx

# Map ports: host:container
docker run -d -p 8080:80 nginx
# http://localhost:8080 → nginx inside container on port 80

# Map multiple ports
docker run -d -p 3000:3000 -p 9229:9229 node:20-alpine

# Set environment variables
docker run -d \
  -e NODE_ENV=production \
  -e MONGO_URI=mongodb://mongo:27017/ems \
  -p 3000:3000 \
  my-ems-api

# Mount a volume: host_path:container_path
docker run -d \
  -v /home/alice/data:/data/db \
  mongo:7

# Automatically remove container when it exits (--rm)
docker run --rm ubuntu echo "Clean up after me"

# Limit resources
docker run -d \
  --memory="512m" \
  --cpus="0.5" \
  my-ems-api

# Full example — run EMS API
docker run -d \
  --name ems-api \
  -p 3000:3000 \
  -e NODE_ENV=production \
  -e MONGO_URI=mongodb://host.docker.internal:27017/ems \
  --restart unless-stopped \
  ibm-ems/api:latest
```

### `--restart` Policies

| Policy | Behaviour |
|--------|-----------|
| `no` | Never restart (default) |
| `always` | Always restart, even on Docker daemon restart |
| `unless-stopped` | Restart unless manually stopped |
| `on-failure` | Restart only on non-zero exit code |

---

## 2.5 Docker Images — Building Your Own

### Dockerfile Basics

A Dockerfile is a recipe for building a custom image.

```dockerfile
# Each line = one layer in the image
FROM node:20-alpine          # start from official Node.js image (Alpine = tiny Linux)

WORKDIR /app                 # set working directory inside container

COPY package*.json ./        # copy package files first (for layer caching)
RUN npm ci                   # install dependencies

COPY . .                     # copy rest of the source code

EXPOSE 3000                  # document which port the app uses (doesn't actually open it)

CMD ["node", "server.js"]    # command to run when container starts
```

### Dockerfile for EMS API

```dockerfile
# Dockerfile
# ── Stage 1: Base ────────────────────────────────────────────────────────────
FROM node:20-alpine AS base

# Install dumb-init for proper signal handling inside containers
RUN apk add --no-cache dumb-init

WORKDIR /app

# Copy and install deps separately — cached if package.json unchanged
COPY package*.json ./
RUN npm ci --only=production

# ── Stage 2: Build ───────────────────────────────────────────────────────────
FROM base AS production

# Copy source code
COPY . .

# Create non-root user (security best practice)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 3000

# dumb-init handles signals correctly (e.g. Ctrl+C, SIGTERM from Kubernetes)
ENTRYPOINT ["dumb-init", "--"]
CMD ["node", "server.js"]
```

### `.dockerignore`

Like `.gitignore` — prevents unnecessary files from being copied into the image.

```
node_modules
npm-debug.log
.git
.gitignore
*.md
.env
.env.local
coverage/
dist/
```

### Build the Image

```bash
# Build the image
# -t = tag (name:version)
# . = build context (current directory)
docker build -t ibm-ems/api:1.0.0 .
docker build -t ibm-ems/api:latest .

# Build with a specific Dockerfile
docker build -f Dockerfile.prod -t ibm-ems/api:prod .

# See the image
docker images | grep ibm-ems
```

### Layer Caching — Why Order Matters

```dockerfile
# ❌ Inefficient — copies source BEFORE installing deps
# Every code change invalidates the npm ci layer
COPY . .
RUN npm ci

# ✅ Efficient — copy package files first
# npm ci only re-runs when package.json changes
COPY package*.json ./
RUN npm ci
COPY . .
```

### Multi-Stage Builds

Multi-stage builds reduce final image size by throwing away build tools.

```dockerfile
# Stage 1 — Install ALL dependencies (including devDependencies)
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci                  # includes devDependencies for building
COPY . .
RUN npm run build           # compile TypeScript, etc.

# Stage 2 — Only production dependencies + built output
FROM node:20-alpine AS production
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production   # no devDependencies
COPY --from=builder /app/dist ./dist   # copy compiled output from stage 1
EXPOSE 3000
CMD ["node", "dist/server.js"]

# Result: final image has no TypeScript compiler, no test tools, etc.
# Much smaller image.
```

```bash
# Compare sizes
docker images ibm-ems/api
# REPOSITORY    TAG         SIZE
# ibm-ems/api   single     420MB   ← single stage
# ibm-ems/api   multi      95MB    ← multi stage
```

---

## 2.6 Docker Compose

Running the EMS API alone isn't useful — it needs MongoDB. Docker Compose lets you define and run **multi-container applications** with a single YAML file.

### `docker-compose.yml` for EMS

```yaml
# docker-compose.yml
version: '3.9'

services:

  # ── MongoDB ────────────────────────────────────────────────────────────────
  mongo:
    image: mongo:7
    container_name: ems-mongo
    restart: unless-stopped
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: secret
      MONGO_INITDB_DATABASE: ems
    ports:
      - "27017:27017"     # expose to host for debugging (optional in production)
    volumes:
      - mongo-data:/data/db    # persist data across container restarts

  # ── Node.js API ────────────────────────────────────────────────────────────
  api:
    build:
      context: .             # build from Dockerfile in current directory
      dockerfile: Dockerfile
    container_name: ems-api
    restart: unless-stopped
    environment:
      NODE_ENV: production
      PORT: 3000
      MONGO_URI: mongodb://admin:secret@mongo:27017/ems?authSource=admin
      JWT_SECRET: your-secret-key-here
    ports:
      - "3000:3000"
    depends_on:
      - mongo                # start mongo before api
    volumes:
      - ./logs:/app/logs     # persist logs on host

  # ── (Optional) Mongo Express — web UI for MongoDB ─────────────────────────
  mongo-express:
    image: mongo-express:latest
    container_name: ems-mongo-ui
    restart: unless-stopped
    environment:
      ME_CONFIG_MONGODB_ADMINUSERNAME: admin
      ME_CONFIG_MONGODB_ADMINPASSWORD: secret
      ME_CONFIG_MONGODB_URL: mongodb://admin:secret@mongo:27017/
    ports:
      - "8081:8081"
    depends_on:
      - mongo

# ── Named volumes ─────────────────────────────────────────────────────────────
volumes:
  mongo-data:
```

### Docker Compose Commands

```bash
# Start all services (builds images if needed)
docker compose up

# Start in background (detached)
docker compose up -d

# Build/rebuild images before starting
docker compose up --build

# Stop all services (containers remain)
docker compose stop

# Stop AND remove containers, networks
docker compose down

# Stop AND remove containers + volumes (deletes database data!)
docker compose down -v

# See running services
docker compose ps

# View logs for all services
docker compose logs

# View logs for a specific service
docker compose logs api
docker compose logs -f api    # follow

# Execute command in a running service
docker compose exec api sh
docker compose exec mongo mongosh

# Scale a service (run multiple instances)
docker compose up -d --scale api=3

# Run one-off commands
docker compose run api npm test
```

### Development vs Production Compose

```yaml
# docker-compose.dev.yml — for local development
services:
  api:
    build:
      context: .
      target: development        # use a development stage in multi-stage Dockerfile
    volumes:
      - .:/app                   # mount source code — changes reflect instantly
      - /app/node_modules        # don't overwrite node_modules from container
    environment:
      NODE_ENV: development
    command: npm run dev         # use nodemon for hot reload
```

```bash
# Use a specific compose file
docker compose -f docker-compose.yml -f docker-compose.dev.yml up
```

---

## 2.7 Docker Registry

A registry stores and distributes Docker images. Docker Hub is the default public registry.

### Docker Hub

```bash
# Login to Docker Hub
docker login

# Tag your image with your Docker Hub username
docker tag ibm-ems/api:1.0.0 yourusername/ibm-ems-api:1.0.0
docker tag ibm-ems/api:1.0.0 yourusername/ibm-ems-api:latest

# Push to Docker Hub
docker push yourusername/ibm-ems-api:1.0.0
docker push yourusername/ibm-ems-api:latest

# Pull from Docker Hub (on another machine)
docker pull yourusername/ibm-ems-api:latest
docker run -p 3000:3000 yourusername/ibm-ems-api:latest
```

### Private Registry

```bash
# Run your own registry
docker run -d -p 5000:5000 --name registry registry:2

# Tag for private registry
docker tag ibm-ems/api:1.0.0 localhost:5000/ibm-ems-api:1.0.0

# Push to private registry
docker push localhost:5000/ibm-ems-api:1.0.0

# Pull from private registry
docker pull localhost:5000/ibm-ems-api:1.0.0
```

### Image Tagging Strategy

```bash
# Semantic versioning — best practice
docker tag ibm-ems/api:1.0.0      registry/ibm-ems-api:1.0.0
docker tag ibm-ems/api:1.0.0      registry/ibm-ems-api:1.0        # minor
docker tag ibm-ems/api:1.0.0      registry/ibm-ems-api:1           # major
docker tag ibm-ems/api:1.0.0      registry/ibm-ems-api:latest      # latest

# Git commit SHA — traceability
docker tag ibm-ems/api registry/ibm-ems-api:$(git rev-parse --short HEAD)
# registry/ibm-ems-api:a1b2c3d
```

---

## 2.8 Docker Engine, Storage, and Networking

### Storage: Volumes vs Bind Mounts

```bash
# Named Volume — managed by Docker, stored in Docker's area
# Best for: database data, persistent app data
docker volume create ems-data
docker run -v ems-data:/data/db mongo:7

# Bind Mount — maps a host directory into the container
# Best for: development (live code reload)
docker run -v $(pwd):/app node:20-alpine

# tmpfs Mount — in-memory, not persisted
# Best for: sensitive temporary data
docker run --tmpfs /tmp node:20-alpine

# Volume commands
docker volume ls
docker volume inspect ems-data
docker volume rm ems-data
docker volume prune      # remove all unused volumes
```

### Networking

Containers on the same Docker network can reach each other by **container name** (not IP).

```bash
# List networks
docker network ls

# Create a custom network
docker network create ems-network

# Connect containers to the network
docker run -d --name mongo    --network ems-network mongo:7
docker run -d --name ems-api  --network ems-network \
  -e MONGO_URI=mongodb://mongo:27017/ems \
  ibm-ems/api:latest
# 'mongo' in the URI resolves to the mongo container's IP — no hardcoding!

# Inspect a network
docker network inspect ems-network
```

**Default networks:**

| Network | Description |
|---------|-------------|
| `bridge` | Default. Containers can talk to each other and internet |
| `host` | Container shares host's network stack (no port mapping needed) |
| `none` | No networking |
| Custom bridge | Like `bridge` but with automatic DNS (use this!) |

> In Docker Compose, all services are automatically on the same custom bridge network — that's why the API can reach `mongo` by name without extra configuration.

---

## 2.9 Container Orchestration — Intro to Swarm

When you need to run multiple containers across multiple servers, you need **orchestration**.

### Docker Swarm — Built-in Orchestration

```bash
# Initialise Swarm on the manager node
docker swarm init --advertise-addr <manager-ip>
# Prints a join command for worker nodes

# On worker nodes:
docker swarm join --token <token> <manager-ip>:2377

# Deploy a stack (like docker compose, but for Swarm)
docker stack deploy -c docker-compose.yml ems-stack

# See running services
docker service ls
docker service ps ems-stack_api

# Scale a service
docker service scale ems-stack_api=3

# Update image (rolling update — zero downtime)
docker service update --image ibm-ems/api:1.1.0 ems-stack_api

# Remove the stack
docker stack rm ems-stack
```

> **Swarm vs Kubernetes:** Swarm is simpler but less powerful. For production-scale orchestration, Kubernetes (Module 03) is the industry standard.

---

## 2.10 EMS Project — Dockerised Setup

```bash
# Project structure
ibm-ems-api/
├── src/
├── Dockerfile
├── .dockerignore
├── docker-compose.yml
├── docker-compose.dev.yml
└── package.json

# Build and run everything
docker compose up --build -d

# Check it's working
curl http://localhost:3000/api/health
# {"status":"ok","timestamp":"...","db":"connected"}

# Stop everything
docker compose down
```

---

## Summary

| Concept | Key Command |
|---------|------------|
| Run a container | `docker run -d -p 3000:3000 myimage` |
| Build an image | `docker build -t myimage:tag .` |
| List containers | `docker ps` / `docker ps -a` |
| View logs | `docker logs -f <container>` |
| Shell into container | `docker exec -it <container> sh` |
| Start all services | `docker compose up -d` |
| Stop all services | `docker compose down` |
| Push image | `docker push registry/image:tag` |
| Create volume | `docker volume create name` |
| Create network | `docker network create name` |

**Next → Module 03: Kubernetes**
