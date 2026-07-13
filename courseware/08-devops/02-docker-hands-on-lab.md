# Docker — Hands-On Lab
### EMS Project · Individual Practice · Half-Day Session (~3.5 hrs)

Companion practical to the **Docker** module. Same EMS codebase from the
Git & GitHub lab — but this time, no teams. Every trainee containerizes
the **whole app** themselves, solo, end to end.

> **Starter kit:** `ibm-ems-docker-starter.zip` — the merged EMS app plus
> a `docker/` folder with fill-in-the-blank `Dockerfile`, `docker-compose.yml`,
> and `nginx.conf` starters.

---

## Why individual this time

Git needed multiple people — merge, conflict, PR, and review only exist
because independent histories are converging. Docker doesn't have that
shape: build, run, inspect, tag, push is a complete loop for one person
on one machine. Splitting it across a team would mean each person gets
*fewer* reps on the actual skill, not more collaboration value. So here,
everyone builds the entire stack — app image, database, reverse proxy,
Compose orchestration — by themselves. The one place a second person
genuinely helps is proving a registry actually works (Exercise 8), so
that step briefly pairs you with a classmate.

---

## Learning Objectives

- [ ] Explain the difference between an image and a container
- [ ] Run, stop, remove, and inspect containers (`run`, `ps`, `logs`, `exec`, `stop`, `rm`)
- [ ] Write a multi-stage `Dockerfile` and explain why layer order matters
- [ ] Build and tag an image, inspect its layers and history
- [ ] Use named volumes for data persistence across container recreation
- [ ] Create a user-defined network and explain container-to-container DNS
- [ ] Write a multi-service `docker-compose.yml` and bring up a full stack
- [ ] Push an image to Docker Hub and pull a classmate's image to run it
- [ ] Describe, at a high level, where Swarm and Kubernetes fit in (deferred to the Kubernetes lab)

---

## 1. The Scenario

Same EMS API from the Git lab. This time you personally take it from
source code to a running four-container stack:

```
        YOU
         │
         ├── write the Dockerfile → build the app image
         ├── configure Postgres + a persistent volume
         ├── configure an Nginx reverse proxy
         ├── write docker-compose.yml wiring all of it together
         └── push your image to Docker Hub, pull a classmate's
```

No ownership map this time — you own all of it. Think of it as wearing
four hats in sequence rather than four people wearing one hat each:

| Hat | What it covers |
|-----|------------------|
| Dockerfile author | Writing the multi-stage build |
| Builder | Building, tagging, inspecting the image |
| Runner/networker | Running containers, wiring up networking |
| Registry engineer | Volumes, tagging, pushing, pulling |

---

## 2. Trainer Setup — Do This Before the Session

1. **Confirm Docker works on your own machine:**
   ```bash
   docker --version
   docker run hello-world
   ```
2. **Pre-pull the base images used in this lab** — do this on the
   classroom network beforehand so 16 people aren't fighting for
   bandwidth mid-session:
   ```bash
   docker pull maven:3.9-eclipse-temurin-17
   docker pull eclipse-temurin:17-jre-alpine
   docker pull postgres:16-alpine
   docker pull nginx:alpine
   docker pull adminer
   docker pull hello-world
   ```
   If your classroom machines are individual laptops rather than a
   shared lab image, share this pull list with trainees as part of
   their pre-work instead (Section 3) so it doesn't eat into session
   time.
3. **Share `ibm-ems-docker-starter.zip`** with the batch before the
   session starts.
4. Confirm every trainee can reach **hub.docker.com** and has (or can
   create in 2 minutes) a free Docker Hub account — needed for
   Exercise 8.
5. If your classroom is on Windows laptops, confirm Docker Desktop is
   running with the **WSL2 backend** enabled (Settings → General → Use
   the WSL 2 based engine) — commands in this guide assume a Linux-style
   shell (WSL2 terminal, macOS Terminal, or native Linux).
6. Decide pairing for Exercise 8 ahead of time (adjacent seats is
   simplest) so it doesn't cost time mid-session.

---

## 3. Trainee Pre-Work Checklist

- [ ] Docker installed and running — `docker --version` and `docker run hello-world` both work
- [ ] Docker Hub account created — note your **exact username**, you'll need it for image tags
- [ ] Logged in locally: `docker login`
- [ ] Your EMS repo from the Git lab available locally (or a fresh clone of your fork)
- [ ] `ibm-ems-docker-starter.zip` extracted, `docker/` folder copied into your repo

---

## 4. Session Timeline (≈ 3.5 hrs)

| # | Segment | Time |
|---|---------|------|
| — | Kickoff, recap of the scenario | 10 min |
| — | Trainer walkthrough of the docker/ starter kit | 10 min |
| Ex 1 | Docker Basics Warm-Up | 15 min |
| Ex 2 | Build Your Own App Image | 40 min |
| Ex 3 | Run, Inspect, Debug | 15 min |
| Ex 4 | Volumes & Persistence Lab | 15 min |
| Ex 5 | Custom Networks | 15 min |
| — | Break | 10 min |
| Ex 6 | Compose: Build Your Own Full Stack | 35 min |
| Ex 7 | Full Stack Test | 15 min |
| Ex 8 | Docker Registry: Push & Partner-Pull | 20 min |
| Ex 9 | Cleanup & Wrap-Up | 15 min |
| | **Total** | **~215 min** |

---

## Exercise 1 — Docker Basics Warm-Up (15 min)

**Concepts:** image vs. container, core CLI commands

```bash
docker run hello-world          # your first container
docker images                   # images now on your machine
docker ps                       # running containers (should be empty — hello-world exits immediately)
docker ps -a                    # ALL containers, including stopped ones

docker run -d --name my-nginx -p 8888:80 nginx:alpine
curl http://localhost:8888      # or open in a browser
docker logs my-nginx
docker exec -it my-nginx sh     # get a shell INSIDE the container
   ls /usr/share/nginx/html     # (inside the container)
   exit
docker stop my-nginx
docker rm my-nginx
```

**Checkpoint:** state, in one sentence, the difference between an
**image** (a blueprint) and a **container** (a running instance of that
blueprint).

---

## Exercise 2 — Build Your Own App Image (40 min)

**Concepts:** Dockerfiles, layers, multi-stage builds, build context

Open `docker/Dockerfile.starter`, fill in every `TODO (Lab)` yourself,
then save it as `docker/Dockerfile` (drop `.starter`). Build it from the
**repo root**:

```bash
docker build -f docker/Dockerfile -t ibm-ems-api:local .
docker images                       # confirm it's there
docker history ibm-ems-api:local    # inspect the layers you just created
```

If it fails, re-check: did you copy `pom.xml` before the rest of the
source? Did you skip tests in the package step? Is your runtime stage's
`FROM` line a real image tag?

Once it builds, try this experiment to *feel* why layer order matters:
```bash
# touch a file in src/ (any harmless whitespace change) and rebuild
docker build -f docker/Dockerfile -t ibm-ems-api:local .
# watch which steps say "CACHED" and which re-run
```

**Checkpoint:** your own build produces `ibm-ems-api:local`, and you can
explain which build steps got cached on the second build and why.

---

## Exercise 3 — Run, Inspect, Debug (15 min)

**Concepts:** `docker run` flags, port mapping, env vars, `inspect`

```bash
docker run -d --name ems-app -p 8080:8080 ibm-ems-api:local
docker logs -f ems-app          # watch it start up (Ctrl+C to stop watching)
curl http://localhost:8080/api/employees
docker inspect ems-app | less   # full container metadata — search for "IPAddress", "Mounts", "Env"
docker exec -it ems-app sh
   ps aux                        # (inside) confirm only the JVM process is running
   exit
```

**Checkpoint:** `docker ps` shows your container running and healthy;
`docker inspect` was used to find at least one real piece of metadata
(IP address, mounted volumes, or env vars).

Clean up before the next exercise:
```bash
docker stop ems-app && docker rm ems-app
```

---

## Exercise 4 — Volumes & Persistence Lab (15 min)

**Concepts:** named volumes, why container filesystems are ephemeral

```bash
# Without a volume — data is lost on removal
docker run -d --name no-volume -e POSTGRES_PASSWORD=test postgres:16-alpine
docker exec -it no-volume psql -U postgres -c "CREATE TABLE proof(id int);"
docker rm -f no-volume
docker run -d --name no-volume -e POSTGRES_PASSWORD=test postgres:16-alpine
docker exec -it no-volume psql -U postgres -c "\dt"   # proof table is GONE
docker rm -f no-volume
```

```bash
# With a named volume — data survives
docker volume create pg-data-demo
docker run -d --name with-volume -e POSTGRES_PASSWORD=test \
  -v pg-data-demo:/var/lib/postgresql/data postgres:16-alpine
docker exec -it with-volume psql -U postgres -c "CREATE TABLE proof(id int);"
docker rm -f with-volume
docker run -d --name with-volume -e POSTGRES_PASSWORD=test \
  -v pg-data-demo:/var/lib/postgresql/data postgres:16-alpine
docker exec -it with-volume psql -U postgres -c "\dt"   # proof table is STILL THERE
```

Clean up:
```bash
docker rm -f with-volume
docker volume rm pg-data-demo
```

**Checkpoint:** explain why the first table disappeared and the second
one didn't, using the words "container filesystem" and "named volume."

---

## Exercise 5 — Custom Networks (15 min)

**Concepts:** user-defined bridge networks, container DNS

```bash
docker network create ems-demo-net

docker run -d --name demo-db --network ems-demo-net \
  -e POSTGRES_PASSWORD=test postgres:16-alpine

docker run -d --name demo-app --network ems-demo-net \
  -p 8080:8080 ibm-ems-api:local

docker exec -it demo-app sh
   # inside the app container — prove DNS works by name, not IP:
   getent hosts demo-db
   exit
```

Now bring the proxy in. First fill in `docker/proxy/nginx.conf.starter`
(the `proxy_pass` TODO) and save as `docker/proxy/nginx.conf`:

```bash
docker run -d --name demo-proxy --network ems-demo-net \
  -v $(pwd)/docker/proxy/nginx.conf:/etc/nginx/nginx.conf:ro \
  -p 80:80 nginx:alpine

curl http://localhost/api/employees   # proxy → app, over the Docker network
```

**Checkpoint:** the curl through the proxy (port 80) returns the same
data as hitting the app directly (port 8080) — proving `proxy_pass`
correctly resolved the app's **service name**, not an IP address you
had to look up manually.

Clean up:
```bash
docker rm -f demo-db demo-app demo-proxy
docker network rm ems-demo-net
```

---

## Exercise 6 — Compose: Build Your Own Full Stack (35 min)

**Concepts:** `docker-compose.yml`, service dependencies

Open `docker/docker-compose.yml.starter` and fill in **all four**
service blocks yourself — `app`, `db`, `proxy`, `adminer` — using the
hints in the file and what you already configured in Exercises 2–5.
Also copy `docker/db/.env.example` to `docker/db/.env` and set real
values.

Once all four blocks are done, rename the file:
```bash
mv docker/docker-compose.yml.starter docker/docker-compose.yml
```

**Checkpoint:** `docker/docker-compose.yml` has all 4 service blocks,
no leftover `TODO` comments, and references your already-tested
Dockerfile and nginx.conf correctly.

> Optional but good practice: commit `docker/` to your EMS fork now, so
> your Docker setup is versioned alongside your code — reuse your Git
> lab skills here (`git add`, `git commit`, `git push`).

---

## Exercise 7 — Full Stack Test (15 min)

**Concepts:** `docker compose up`, service dependency order

```bash
docker compose -f docker/docker-compose.yml up -d --build
docker compose -f docker/docker-compose.yml ps
```

Verify each piece:
```bash
curl http://localhost/api/employees        # through the proxy
curl http://localhost:8080/api/departments # app directly
```
Open `http://localhost:8081` in a browser for **Adminer** — log in with
the Postgres credentials from `docker/db/.env` and confirm the tables
Hibernate created (`employees`, `departments`, `projects`, `jobs`) are
visible.

```bash
docker compose -f docker/docker-compose.yml logs -f app   # tail one service's logs
```

**Checkpoint:** all 4 containers show `Up` in `docker compose ps`, and
you can see live data through Adminer — a full stack you built entirely
by yourself.

---

## Exercise 8 — Docker Registry: Push & Partner-Pull (20 min)

**Concepts:** tagging convention, `docker push`/`pull`, image portability

This is the one exercise that benefits from a second person — proving a
registry actually works needs someone else to pull what you pushed.

1. Tag your image with **your own Docker Hub username**:
   ```bash
   docker login
   docker tag ibm-ems-api:local <your-dockerhub-username>/ibm-ems-api:1.0
   docker push <your-dockerhub-username>/ibm-ems-api:1.0
   ```
2. Confirm on hub.docker.com that the image is there and **public**.
3. **Pair up with a classmate.** Exchange image names. Each of you runs
   the other's image, on your own machine, with zero access to their
   source code or Dockerfile:
   ```bash
   docker pull <partner-dockerhub-username>/ibm-ems-api:1.0
   docker run -d -p 9090:8080 <partner-dockerhub-username>/ibm-ems-api:1.0
   curl http://localhost:9090/api/employees
   ```

**Checkpoint:** you successfully ran a classmate's image on your machine
with zero access to their source code — that's the whole point of a
registry: build once, run anywhere.

---

## Exercise 9 — Cleanup & Wrap-Up (15 min)

**Concepts:** resource hygiene, `docker system prune`

```bash
docker compose -f docker/docker-compose.yml down -v   # stop everything, remove volumes too
docker ps -a                                            # confirm nothing stray is left running
docker images                                            # see how much you've accumulated
docker system df                                         # disk usage breakdown
docker system prune                                       # reclaim space (asks for confirmation)
```

### Stretch goals (if time remains)

- `docker stats` — watch live CPU/memory usage of running containers
- Try building your app image with `--platform linux/amd64` and discuss
  why that flag matters if you're on an Apple Silicon Mac and deploying
  to a typical cloud VM
- **Where Swarm and Kubernetes fit in:** Compose is great for one
  machine. The moment you need containers running across *multiple*
  machines, with automatic restart, scaling, and rolling updates —
  that's Swarm or Kubernetes. We'll go hands-on with Kubernetes next.

---

## Completion Checklist (self-tick)

- [ ] Ran, inspected, and removed a container (`run`, `ps`, `logs`, `exec`, `rm`)
- [ ] Wrote a working multi-stage Dockerfile, entirely by myself
- [ ] Built and tagged an image, inspected its layers with `docker history`
- [ ] Proved data loss without a volume, and persistence with one
- [ ] Created a custom network and used container-name DNS
- [ ] Wrote a complete `docker-compose.yml` with all 4 services, entirely by myself
- [ ] Brought up a full multi-container stack with `docker compose up`
- [ ] Pushed an image to Docker Hub
- [ ] Pulled and ran a classmate's image with no access to their source
- [ ] Cleaned up containers, volumes, and images afterward

---

## Trainer's Evaluation Rubric

Score per individual (no team averaging needed this time):

| Signal | What good looks like |
|--------|------------------------|
| Dockerfile | Correct multi-stage split, no leftover TODOs, small final image |
| Layer ordering | `pom.xml` copied before source, so cache isn't busted on every code change |
| Image tags | Meaningful tags (`:1.0`, not just `:latest` for everything) |
| Compose file | All 4 services present, correctly wired with `depends_on`, written solo |
| Networking | Proxy correctly reaches app by service name, not hardcoded IP |
| Volumes | Data genuinely persists across a container recreation, demonstrated live |
| Registry | Image is actually pushed and successfully pulled by a partner |
| Cleanup | Trainee leaves the session with `docker ps -a` and `docker images` reasonably tidy |

---

## Common Pitfalls / FAQ

**"`docker build` fails with 'Cannot connect to the Docker daemon.'"**
Docker Desktop (or the Docker service on Linux) isn't running. Start it
and retry.

**"Port 8080 already in use."**
Another container (or a local process) already has that port. Either
stop it (`docker ps` to find it) or run yours on a different host port:
`-p 8081:8080`.

**"My proxy gets a 502 Bad Gateway."**
Almost always a networking mismatch — either the proxy container isn't
on the same Docker network as the app, or `proxy_pass` is pointing at
`localhost` instead of the app's **service name**.

**"Postgres data disappeared after `docker compose down`."**
Plain `down` keeps named volumes. `down -v` deletes them too — that's
intentional in Exercise 9's cleanup, but don't run `-v` if you actually
want to keep your data.

**"My pushed image works for me but not for my partner."**
Check three things: is the repo **public** on Docker Hub, did you both
use the exact same tag, and — if either of you is on Apple Silicon —
did the image get built for the wrong CPU architecture?

**"WSL2 users: `docker` command not found in my terminal."**
Confirm Docker Desktop → Settings → Resources → WSL Integration has
your distro enabled.

---

## Command Reference (this lab)

| Command | What it does |
|---------|---------------|
| `docker run <image>` | Create and start a container from an image |
| `docker ps` / `docker ps -a` | List running / all containers |
| `docker logs <name>` | View a container's output |
| `docker exec -it <name> sh` | Open a shell inside a running container |
| `docker build -f <Dockerfile> -t <tag> .` | Build an image |
| `docker history <image>` | Inspect an image's layers |
| `docker volume create/rm/ls` | Manage named volumes |
| `docker network create/rm/ls` | Manage custom networks |
| `docker compose up -d --build` | Build and start a full multi-container stack |
| `docker compose down -v` | Stop the stack, remove volumes too |
| `docker tag <local> <user>/<repo>:<tag>` | Prepare an image for pushing |
| `docker push` / `docker pull` | Upload / download an image via a registry |
| `docker system prune` | Reclaim disk space from unused resources |

---

**Next in this series: Kubernetes** — same EMS app, individual practice
again (each trainee on their own local cluster), now orchestrated across
pods and nodes instead of a single Docker host. Let me know when you're
ready and I'll ask a few scoping questions first, the same way we did
here.
