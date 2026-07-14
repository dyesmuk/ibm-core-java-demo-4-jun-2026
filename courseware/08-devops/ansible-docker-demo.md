# Ansible Demo — Fully Dockerized, Deploying `ibm-ems-api`

A minimal, classroom-ready demo: Ansible control node runs **inside a Docker container**, manages a target container over SSH, and deploys the `ibm-ems-api` Node/Express project (not just a single script — full `npm install` + `npm start` flow). Includes an optional bonus section on deploying a pre-built `ibm-ems-api` image from Docker Hub instead of copying source.

**Environment:** Windows 11, CMD, Docker Desktop, Kubernetes (not used in this demo).

> **Assumption flagged:** this assumes `ibm-ems-api` has a `package.json` with a `start` script and listens on port `5000`. If your actual entry point or port differs, just swap the values in Step 6 and the `wait_for`/`curl` steps — everything else stays the same.

---

## Architecture

```
Windows 11 (Docker Desktop)
│
├── ansible-control  (container: runs Ansible, Python, SSH client)
│         │  SSH (port 22, over docker network)
│         ▼
└── ansible-target   (container: Ubuntu + SSH server, gets ibm-ems-api deployed)
```

Both containers sit on the same Docker network (`ansible-net`) and talk to each other by container **name** — no port mapping juggling, no WSL2.

---

## Part 1 — Deploy `ibm-ems-api` via Ansible

### Step 1: Create the Docker network

```cmd
docker network create ansible-net
```

### Step 2: Start the target container (the "managed node")

```cmd
docker run -d --name ansible-target --network ansible-net -p 5000:5000 ubuntu:22.04 sleep infinity

docker exec ansible-target bash -c "apt update && apt install -y openssh-server sudo curl && mkdir /var/run/sshd && echo 'root:ansible123' | chpasswd && sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config && service ssh start"
```

`-p 5000:5000` maps the API port to your Windows host so you can `curl localhost:5000` later.

### Step 3: Build the Ansible control image

`Dockerfile`:
```dockerfile
FROM python:3.12-slim
RUN apt-get update && apt-get install -y sshpass openssh-client && \
    pip install ansible
WORKDIR /ansible
```

```cmd
docker build -t my-ansible .
```

### Step 4: Project folder structure

```
ansible-demo/
├── Dockerfile
├── inventory.ini
├── deploy-app.yml
└── files/
    └── ibm-ems-api/          <-- the whole project folder
        ├── package.json
        ├── package-lock.json
        ├── server.js (or index.js / app.js — whatever your entry point is)
        └── ... (routes/, models/, etc.)
```

Copy your entire `ibm-ems-api` project folder into `ansible-demo/files/`. Unlike the single-file demo, Ansible now needs the whole codebase plus `package.json` so it can run `npm install` on the target.

### Step 5: Inventory file

`inventory.ini`:
```ini
[targets]
ansible-target ansible_user=root ansible_password=ansible123 ansible_ssh_common_args='-o StrictHostKeyChecking=no'
```

`ansible-target` here is the **container name**, resolved via the shared Docker network — not `localhost`, not an IP.

### Step 6: The playbook

`deploy-app.yml`:
```yaml
---
- name: Deploy and run ibm-ems-api
  hosts: targets
  become: yes

  tasks:
    - name: Install Node.js
      shell: |
        curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
        apt install -y nodejs
      args:
        creates: /usr/bin/node

    - name: Create app directory
      file:
        path: /opt/ibm-ems-api
        state: directory

    - name: Copy ibm-ems-api project to target
      copy:
        src: files/ibm-ems-api/
        dest: /opt/ibm-ems-api/

    - name: Install npm dependencies
      npm:
        path: /opt/ibm-ems-api

    - name: Run the app in background
      shell: nohup npm start > /opt/ibm-ems-api/app.log 2>&1 &
      args:
        chdir: /opt/ibm-ems-api
      async: 1
      poll: 0

    - name: Wait for server to start
      wait_for:
        port: 5000
        host: 127.0.0.1
        delay: 3
        timeout: 20
```

> `npm: path:` runs `npm install` on the target automatically — this is the step that didn't exist in the single-file version, since a real Express project has dependencies to resolve.

### Step 7: Run the control container and execute the playbook

```cmd
docker run --rm -it --network ansible-net -v %cd%:/ansible my-ansible bash
```

Inside the container shell:
```bash
ansible-playbook -i inventory.ini deploy-app.yml
```

### Step 8: Verify from Windows CMD

```cmd
curl http://localhost:5000
```

Expected output: whatever `ibm-ems-api`'s root route (or a known endpoint like `/api/employees`) returns.

---

## Part 2 (Bonus) — Deploying a Pre-Built `ibm-ems-api` Image from Docker Hub

**Short answer: yes, but it works differently than copying source code.** Ansible doesn't naturally "SSH in and docker-pull" — running Docker *inside* a container to manage other containers (Docker-in-Docker) adds real complexity (privileged mode, nested daemons, socket permission issues) for very little teaching value.

The clean pattern instead: give the **Ansible control container** access to your Windows machine's actual Docker daemon by mounting the Docker socket. Ansible then tells your host's Docker Desktop to pull and run the image directly — no nested Docker, no SSH into a target at all for this part.

### Step 1: Add the Docker Ansible collection to your control image

Update `Dockerfile`:
```dockerfile
FROM python:3.12-slim
RUN apt-get update && apt-get install -y sshpass openssh-client && \
    pip install ansible docker
RUN ansible-galaxy collection install community.docker
WORKDIR /ansible
```

Rebuild:
```cmd
docker build -t my-ansible .
```

### Step 2: Run the control container with the Docker socket mounted

```cmd
docker run --rm -it --network ansible-net -v %cd%:/ansible -v //var/run/docker.sock:/var/run/docker.sock my-ansible bash
```

This gives Ansible (running inside the container) the ability to command your **host's** Docker Desktop — pulling and starting containers right alongside `ansible-target`.

### Step 3: A playbook that pulls and runs `ibm-ems-api`

`deploy-image.yml`:
```yaml
---
- name: Deploy ibm-ems-api from Docker Hub
  hosts: localhost
  connection: local
  gather_facts: no

  tasks:
    - name: Pull the image
      community.docker.docker_image:
        name: <your-dockerhub-username>/ibm-ems-api
        tag: latest
        source: pull

    - name: Run the container
      community.docker.docker_container:
        name: ibm-ems-api
        image: <your-dockerhub-username>/ibm-ems-api:latest
        state: started
        recreate: yes
        published_ports:
          - "5000:5000"
```

Run it the same way:
```bash
ansible-playbook -i inventory.ini deploy-image.yml
```

Then from Windows CMD:
```cmd
curl http://localhost:5000
```

### Important caveat

This only works if `ibm-ems-api` has actually been **pushed to Docker Hub** first. If it's currently just a local project, that's a separate one-time step:

```cmd
docker build -t <your-dockerhub-username>/ibm-ems-api:latest .
docker login
docker push <your-dockerhub-username>/ibm-ems-api:latest
```

Worth doing only if you want trainees to practice a "pull and run a real image" workflow — for teaching Ansible fundamentals, Part 1 (copy source + install deps + run) is more instructive since it shows Ansible's file/package/service-management steps, not just Docker orchestration.

---

## Quick Comparison

| | Part 1: Copy & Run Source | Part 2: Pull Prebuilt Image |
|---|---|---|
| What Ansible does | copy, npm, shell, wait_for | docker_image, docker_container |
| Needs SSH into target | Yes | No (uses local Docker socket) |
| Needs image on Docker Hub first | No | Yes |
| Teaches | classic config-management loop | image-based deployment pattern |
| Complexity | Low | Low–Medium (socket mount concept) |

---

## Cleanup

```cmd
docker rm -f ansible-target ibm-ems-api
docker network rm ansible-net
```
