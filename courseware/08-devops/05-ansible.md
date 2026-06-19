# 05 — Ansible
> **DevOps Hands-On Series** | Module 5 of 6 | Server provisioning + app deployment

---

## What Ansible Solves

Kubernetes manages your containers. But someone had to set up the servers those containers run on — install Docker, install Kubernetes, configure the OS, set up users, open firewall ports.

Do that manually on one server and it takes 30 minutes. Do it manually on 50 servers and it takes days, and every server is slightly different because humans make inconsistent choices.

Ansible automates all of that. Write a playbook once. Run it. Ansible SSHes into all your servers in parallel and makes them exactly match your specification.

| What you've been doing manually | What Ansible handles |
|---|---|
| Dockerfiles package the app | Installs Docker on every server |
| Kubernetes runs containers | Installs Kubernetes on every node |
| Pushed to Docker Hub | Configures the server hosting the registry |
| Set up MongoDB | Installs, configures, starts the database |
| All of it, one server at a time | Ansible does all of it on **100 servers simultaneously** |

```
Manual ops:   SSH server 1 → run commands → SSH server 2 → run commands → repeat
              (Error-prone. Slow. Not reproducible. Not trackable.)

Ansible:      Write a Playbook once.
              Run it.
              Ansible SSHes all servers in parallel and brings them
              to your exact specification — automatically.
```

**The killer feature: it's agentless.** Nothing is installed on the target servers. Ansible uses plain SSH, which every Linux server already has.

**Environment note:** Ansible cannot run natively on Windows. On Windows 11, it runs inside **WSL2**. All Ansible commands in this module run in a WSL2 terminal — not PowerShell.

---

## One-Time Setup: WSL2 on Windows 11

WSL2 gives you a full Linux environment inside Windows. This is your Ansible control node.

### Install WSL2

Open **PowerShell as Administrator**:

```powershell
wsl --install
```

This installs WSL2 with Ubuntu 22.04. Restart when prompted. After restart, Ubuntu opens and asks you to set a Linux username and password — do it, you'll need it for `sudo`.

### Verify

```powershell
# In PowerShell — confirm WSL2 is running
wsl --list --verbose
```

Expected:
```
  NAME      STATE     VERSION
* Ubuntu    Running   2
```

> Note: `wsl --list --verbose` is a PowerShell command. Once you're inside WSL2, it won't work (you'll get "command not found") — that's normal.

### Enable Docker in WSL2

Without this step, `docker` inside WSL2 gives "command not found":

1. Open **Docker Desktop** on Windows
2. **Settings → Resources → WSL Integration**
3. Enable **"Enable integration with my default WSL distro"**
4. Toggle **on** Ubuntu in the list below
5. **Apply & Restart**

Verify it works inside WSL2:

```bash
docker --version
docker compose version
docker ps
```

No errors = Docker Desktop is connected to your WSL2 environment.

### Open a WSL2 Terminal

In Windows Terminal, click the dropdown arrow → **Ubuntu**. All Ansible work happens here.

### Editing Files in WSL2

Two solid options:

**Option A — nano (simplest, built-in):**
```bash
nano filename.yaml
# Save: Ctrl+X → Y → Enter
# Cancel: Ctrl+X → N
```

**Option B — VS Code (recommended for larger files):**
```bash
code filename.yaml    # opens in VS Code on Windows
code .                # opens the whole folder
```

> Never edit WSL2 files with Windows Notepad — it adds Windows line endings (CRLF) that break YAML parsing.

---

## How Ansible Works

```
Control Node                         Managed Nodes
(WSL2 Ubuntu — Ansible installed)    (target servers — nothing installed)

   ansible-playbook
          |
          | SSH (port 22)
          |
     ┌────┴────┐
     ↓         ↓
  Server 1   Server 2   Server 3 ...
```

| Component | What it is |
|---|---|
| **Control Node** | WSL2 Ubuntu — where Ansible is installed and all commands run |
| **Managed Node** | Any server you're managing. Only needs Python + SSH. |
| **Inventory** | The list of servers — the "who to run against" |
| **Playbook** | A YAML file with automation tasks — the "what to do" |
| **Task** | A single unit of work (install a package, copy a file, start a service) |
| **Module** | The built-in tool that executes a task (`apt`, `copy`, `service`, etc.) |
| **Role** | A reusable bundle of tasks for a specific purpose (e.g., "install Docker") |

---

## Part 1 — Install Ansible in WSL2

All commands from here run inside your **WSL2 Ubuntu terminal**:

```bash
sudo apt update
sudo apt install ansible sshpass -y

ansible --version
```

Expected:
```
ansible [core 2.16.x]
  python version = 3.12.x
  ...
```

`sshpass` is needed for password-based SSH (used in the lab setup below).

---

## Part 2 — Lab Setup: Managed Nodes as Docker Containers

For the hands-on lab, you'll use Docker containers as simulated servers.

### Create the Lab Folder

> **Important:** Create this inside your WSL2 home directory — NOT on a Windows drive (`/mnt/c/` or `/mnt/d/`).
>
> Working on NTFS-mounted Windows drives causes this error:
> ```
> [WARNING]: Ansible is being run in a world writable directory, ignoring it as an ansible.cfg source.
> ```
> Avoid this entirely by working inside `~/`:

```bash
mkdir ansible-lab && cd ansible-lab
```

### Create `docker-compose.yml`

```bash
nano docker-compose.yml
```

```yaml
services:
  node1:
    image: ubuntu:22.04
    container_name: ansible-node1
    command: >
      /bin/bash -c "
        apt-get update &&
        apt-get install -y openssh-server python3 &&
        mkdir /run/sshd &&
        echo 'root:password' | chpasswd &&
        sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config &&
        /usr/sbin/sshd -D
      "
    ports:
      - "2221:22"

  node2:
    image: ubuntu:22.04
    container_name: ansible-node2
    command: >
      /bin/bash -c "
        apt-get update &&
        apt-get install -y openssh-server python3 &&
        mkdir /run/sshd &&
        echo 'root:password' | chpasswd &&
        sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config &&
        /usr/sbin/sshd -D
      "
    ports:
      - "2222:22"
```

```bash
docker compose up -d
```

You now have two "servers" (containers) that Ansible will manage.

### Create `ansible.cfg`

> Create this **before doing anything else with Ansible.** It disables SSH host key checking — required for a Docker-based lab where containers regenerate SSH keys on every restart.

```bash
nano ansible.cfg
```

```ini
[defaults]
host_key_checking = False

[ssh_connection]
ssh_args = -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null
```

| Setting | What it does |
|---|---|
| `host_key_checking = False` | Ansible doesn't check SSH host keys |
| `StrictHostKeyChecking=no` | The underlying SSH client won't block on unknown hosts |
| `UserKnownHostsFile=/dev/null` | Prevents stale key conflicts when containers restart |

---

## Part 3 — Inventory: Telling Ansible What to Manage

The **inventory** lists the servers Ansible will work on and how to connect to them.

### Create `inventory.ini`

```bash
nano inventory.ini
```

```ini
[webservers]
node1 ansible_host=127.0.0.1 ansible_port=2221 ansible_user=root ansible_password=password

[dbservers]
node2 ansible_host=127.0.0.1 ansible_port=2222 ansible_user=root ansible_password=password

[all:vars]
ansible_python_interpreter=/usr/bin/python3
```

| Field | Meaning |
|---|---|
| `[webservers]` | Group name — target groups instead of individual hosts |
| `ansible_host` | The actual IP to connect to |
| `ansible_port` | SSH port |
| `ansible_user` | SSH user |
| `ansible_password` | SSH password (use SSH keys in production) |
| `[all:vars]` | Variables that apply to every host |

> **Keep `ansible.cfg` and `inventory.ini` as separate files.** Ansible config directives like `[defaults]` belong only in `ansible.cfg` — placing them in `inventory.ini` causes a parse error.

### Your lab folder should look like:

```
~/ansible-lab/
  ├── ansible.cfg
  ├── docker-compose.yml
  └── inventory.ini
```

### Test Connectivity

```bash
ansible all -i inventory.ini -m ping
```

Expected:
```
node1 | SUCCESS => {
    "changed": false,
    "ping": "pong"
}
node2 | SUCCESS => {
    "changed": false,
    "ping": "pong"
}
```

> Ansible's `ping` module is **not** ICMP ping. It SSHes into the host, runs a small Python check, and returns `pong`. It's Ansible's SSH connectivity test.

### More Ad-Hoc Commands

```bash
# Run a shell command on all servers
ansible all -i inventory.ini -m shell -a "uname -a"

# Run only on webservers group
ansible webservers -i inventory.ini -m shell -a "whoami"

# Check disk space on dbservers
ansible dbservers -i inventory.ini -m shell -a "df -h"
```

Ad-hoc commands are great for quick one-off tasks. For repeatable sequences, use Playbooks.

**Quick knowledge check:**
- If you want to manage 50 web servers and 10 database servers, how does grouping help? → Run one command against `[webservers]` without listing every server individually.
- Ansible's `ping` vs the `ping` command in your terminal? → Different. Ansible `ping` SSHes in and runs Python. Network ICMP ping is a separate thing.

---

## Part 4 — Your First Playbook

A Playbook is a YAML file with a sequence of tasks. Repeatable, version-controlled, readable.

### Create `install-node.yaml`

```bash
nano install-node.yaml
```

```yaml
---
- name: Install Node.js on web servers
  hosts: webservers
  become: yes

  tasks:
    - name: Update apt cache
      apt:
        update_cache: yes

    - name: Install Node.js
      apt:
        name: nodejs
        state: present

    - name: Install npm
      apt:
        name: npm
        state: present

    - name: Verify Node.js installation
      command: node --version
      register: node_version

    - name: Print Node.js version
      debug:
        msg: "Node.js installed: {{ node_version.stdout }}"
```

### Run It

```bash
ansible-playbook -i inventory.ini install-node.yaml
```

Expected output:
```
TASK [Install Node.js] *****************************
changed: [node1]

TASK [Print Node.js version] ***********************
ok: [node1] => {
    "msg": "Node.js installed: v12.x.x"
}

PLAY RECAP ******************************************
node1 : ok=6  changed=4  unreachable=0  failed=0
```

### `ok` vs `changed` — What They Mean

| Status | Meaning |
|---|---|
| `ok` | Task ran — no change needed (already in desired state) |
| `changed` | Task ran — something was actually modified |
| `failed` | Task failed |
| `unreachable` | Ansible couldn't SSH in |

> **Node.js version note:** Ubuntu 22.04's `apt` installs Node.js v12 (end-of-life). For a current version, add the NodeSource repository:
>
> ```yaml
>     - name: Add NodeSource repository for Node.js 18
>       shell: curl -fsSL https://deb.nodesource.com/setup_18.x | bash -
>       args:
>         creates: /etc/apt/sources.list.d/nodesource.list
>
>     - name: Install Node.js 18
>       apt:
>         name: nodejs
>         state: present
>         update_cache: yes
> ```

### The Idempotency Demo

Run the playbook a **second time** without changing anything:

```bash
ansible-playbook -i inventory.ini install-node.yaml
```

All tasks show `ok` — not `changed`. Node.js is already installed, Ansible detects it and skips the work.

**Idempotency = running the same playbook 10 times produces the same result as running it once.** This is the core reliability guarantee of Ansible. It's safe to re-run anytime.

### Playbook Anatomy

```yaml
---                           # YAML document start
- name: Human-readable title  # The "play"
  hosts: webservers           # Which inventory group to target
  become: yes                 # Use sudo (privilege escalation)

  tasks:
    - name: Task description  # Shown in output
      apt:                    # The MODULE to use
        name: nodejs          # Module arguments
        state: present        # "present" = install, "absent" = uninstall
```

**Quick knowledge check:**
- Run the playbook twice — what's different the second time? → All `ok`, none `changed`. Idempotency.
- What does `become: yes` do? → Uses `sudo`. Needed for system-level tasks.
- `state: present` vs `state: latest`? → `present` installs if missing but won't upgrade. `latest` always upgrades.

---

## Part 5 — Variables, Templates, and Handlers

### Variables

```yaml
---
- name: Deploy Express app
  hosts: webservers
  become: yes
  vars:
    app_port: 3000
    app_name: hello-express
    node_env: production

  tasks:
    - name: Print deployment info
      debug:
        msg: "Deploying {{ app_name }} on port {{ app_port }} in {{ node_env }} mode"
```

`{{ variable_name }}` is Jinja2 templating syntax — used throughout Ansible.

| Variable location | Use case |
|---|---|
| `vars:` in the play | Quick inline variables |
| `vars_files:` | Separate YAML file for many variables |
| `group_vars/webservers.yaml` | Variables for a whole group — auto-loaded |
| `host_vars/node1.yaml` | Variables for one specific host — auto-loaded |
| `-e "app_port=8080"` on CLI | Override at runtime |

### Handlers

A **handler** is a task that only runs when notified — and only once at the end of the play. Perfect for service restarts:

```yaml
---
- name: Configure Nginx
  hosts: webservers
  become: yes

  tasks:
    - name: Install Nginx
      apt:
        name: nginx
        state: present

    - name: Copy Nginx config
      copy:
        src: nginx.conf
        dest: /etc/nginx/nginx.conf
      notify: Restart Nginx    # triggers the handler below

  handlers:
    - name: Restart Nginx
      service:
        name: nginx
        state: restarted
```

If `nginx.conf` hasn't changed → `copy` is `ok` → handler never fires. If you push a new config → `copy` is `changed` → Nginx restarts once at the end.

### Jinja2 Templates

Create `templates/app.env.j2`:

```
NODE_ENV={{ node_env }}
PORT={{ app_port }}
APP_NAME={{ app_name }}
DB_HOST={{ db_host }}
```

Use it in the playbook:

```yaml
    - name: Generate app environment file
      template:
        src: templates/app.env.j2
        dest: /opt/hello-express/.env
      notify: Restart app
```

Same template, different values per environment — one playbook deploys to dev, staging, and production.

---

## Part 6 — Deploying Docker with Ansible

Use Ansible to install Docker on managed nodes and run your Express container.

### Project Structure

```
~/ansible-lab/
  ├── ansible.cfg
  ├── inventory.ini
  ├── deploy-docker.yaml
  └── templates/
        └── docker-compose.j2
```

### `deploy-docker.yaml`

```yaml
---
- name: Install Docker and deploy Express app
  hosts: webservers
  become: yes
  vars:
    app_image: yourname/hello-express:1.0
    app_port: 3000

  tasks:
    - name: Install prerequisite packages
      apt:
        name:
          - apt-transport-https
          - ca-certificates
          - curl
          - software-properties-common
        state: present
        update_cache: yes

    - name: Add Docker GPG key
      apt_key:
        url: https://download.docker.com/linux/ubuntu/gpg
        state: present

    - name: Add Docker repository
      apt_repository:
        repo: "deb [arch=amd64] https://download.docker.com/linux/ubuntu {{ ansible_facts['lsb']['codename'] }} stable"
        state: present

    - name: Install Docker CE
      apt:
        name: docker-ce
        state: present

    - name: Start and enable Docker
      service:
        name: docker
        state: started
        enabled: yes

    - name: Pull the app image from Docker Hub
      community.docker.docker_image:
        name: "{{ app_image }}"
        source: pull

    - name: Run the Express container
      community.docker.docker_container:
        name: hello-express
        image: "{{ app_image }}"
        state: started
        restart_policy: always
        ports:
          - "{{ app_port }}:3000"
```

```bash
ansible-playbook -i inventory.ini deploy-docker.yaml
```

---

## Part 7 — Roles: Reusable Automation

When a playbook gets complex, split it into **roles** — structured, reusable bundles of tasks.

### Create Role Skeletons

```bash
ansible-galaxy init roles/install-docker
ansible-galaxy init roles/deploy-app
```

### Role Directory Structure

```
roles/
  install-docker/
    ├── tasks/
    │     └── main.yaml       ← the tasks
    ├── handlers/
    │     └── main.yaml       ← handlers
    ├── templates/
    │     └── daemon.json.j2  ← Jinja2 templates
    ├── vars/
    │     └── main.yaml       ← high-priority variables
    └── defaults/
          └── main.yaml       ← overridable defaults
```

### `roles/install-docker/tasks/main.yaml`

```yaml
---
- name: Install prerequisite packages
  apt:
    name:
      - apt-transport-https
      - ca-certificates
      - curl
    state: present
    update_cache: yes

- name: Add Docker GPG key
  apt_key:
    url: https://download.docker.com/linux/ubuntu/gpg
    state: present

- name: Install Docker CE
  apt:
    name: docker-ce
    state: present

- name: Start Docker
  service:
    name: docker
    state: started
    enabled: yes
```

### Use the Role in a Playbook

```yaml
---
- name: Provision web servers
  hosts: webservers
  become: yes

  roles:
    - install-docker
    - deploy-app
```

Clean. Reusable. Shareable.

### Ansible Galaxy — Community Roles

```bash
ansible-galaxy search docker
ansible-galaxy install geerlingguy.docker

# Use in playbook
roles:
  - geerlingguy.docker
```

---

## Part 8 — Ansible in the CI/CD Pipeline

### Where Ansible Fits in the Full Pipeline

```
Developer pushes code
        ↓
     GitHub
        ↓
    Jenkins (CI) — runs on Windows via Docker
     - Run tests
     - docker build
     - docker push → Docker Hub
        ↓
    Ansible (CD) — runs inside WSL2
     - ansible-playbook deploy.yaml
     - SSH into production servers
     - docker pull (new image)
     - Restart containers
        ↓
  Production Servers (running updated app)
```

### `deploy-update.yaml`

```yaml
---
- name: Deploy updated application
  hosts: webservers
  become: yes
  vars:
    image_tag: "{{ lookup('env', 'IMAGE_TAG') }}"

  tasks:
    - name: Pull the new image
      community.docker.docker_image:
        name: "yourname/hello-express:{{ image_tag }}"
        source: pull
        force_source: yes

    - name: Stop and remove old container
      community.docker.docker_container:
        name: hello-express
        state: absent
      ignore_errors: yes

    - name: Start new container
      community.docker.docker_container:
        name: hello-express
        image: "yourname/hello-express:{{ image_tag }}"
        state: started
        restart_policy: always
        ports:
          - "3000:3000"
```

### Calling Ansible from Jenkins (Jenkinsfile snippet)

```groovy
stage('Deploy') {
    steps {
        sh """
          wsl ansible-playbook -i inventory.ini deploy-update.yaml \
            -e IMAGE_TAG=${BUILD_NUMBER}
        """
    }
}
```

---

## Common Errors (WSL2 Lab Reference)

| Error | Root cause | Fix |
|---|---|---|
| `Host Key checking is enabled` | `ansible.cfg` missing or not loaded | Create `ansible.cfg` in `~/ansible-lab/` with `host_key_checking = False` |
| `Expected key=value host variable assignment` | `[defaults]` placed inside `inventory.ini` | Move `[defaults]` to `ansible.cfg` — wrong file |
| `REMOTE HOST IDENTIFICATION HAS CHANGED` | Stale SSH keys from previous container run | Run `ssh-keygen -R '[127.0.0.1]:2221'` or set `UserKnownHostsFile=/dev/null` |
| `world writable directory, ignoring ansible.cfg` | Lab folder is on `/mnt/d/` or `/mnt/c/` | Use `~/ansible-lab/` inside WSL2 home |
| `INJECT_FACTS_AS_VARS deprecated` | Using `ansible_lsb.codename` shorthand | Replace with `ansible_facts['lsb']['codename']` |
| `docker: command not found` in WSL2 | Docker Desktop WSL integration not enabled | Docker Desktop → Settings → Resources → WSL Integration → enable Ubuntu |
| `permission denied on Docker socket` | WSL2 session started before Docker group applied | Close and reopen WSL2 terminal |
| `node -v` shows v12 | Ubuntu 22.04 apt default is EOL Node v12 | Use NodeSource setup script (see Part 4 note) |

---

## Core Modules Reference

| Module | What it does | Example |
|---|---|---|
| `apt` | Manage packages on Debian/Ubuntu | `name: nodejs state: present` |
| `yum` / `dnf` | Manage packages on RHEL/CentOS | `name: java-17 state: present` |
| `copy` | Copy a file to the server | `src: app.conf dest: /etc/app.conf` |
| `template` | Render Jinja2 template and copy | `src: app.env.j2 dest: /opt/app/.env` |
| `service` | Start/stop/restart/enable services | `name: nginx state: restarted` |
| `command` | Run a command (no shell features) | `command: node --version` |
| `shell` | Run a shell command (pipes work) | `shell: cat /etc/os-release` |
| `file` | Create/delete files, directories | `path: /opt/app state: directory` |
| `user` | Manage OS user accounts | `name: deploy state: present` |
| `git` | Clone or update a Git repo | `repo: https://github.com/... dest: /opt/app` |
| `debug` | Print a message or variable | `msg: "Version is {{ version }}"` |
| `docker_container` | Run/stop Docker containers | `name: app image: hello:1.0 state: started` |
| `docker_image` | Pull/build Docker images | `name: hello:1.0 source: pull` |

---

## All Ansible Commands

> All commands run inside **WSL2 Ubuntu terminal**

```bash
# Test connectivity
ansible all -i inventory.ini -m ping

# Ad-hoc command
ansible all -i inventory.ini -m shell -a "df -h"

# Run a playbook
ansible-playbook -i inventory.ini playbook.yaml

# Dry run (check what WOULD change without changing anything)
ansible-playbook -i inventory.ini playbook.yaml --check

# Verbose (add more v's for more detail)
ansible-playbook -i inventory.ini playbook.yaml -v

# Run on one host only
ansible-playbook -i inventory.ini playbook.yaml --limit node1

# Pass extra variables
ansible-playbook -i inventory.ini playbook.yaml -e "app_port=8080"

# List hosts in a group
ansible webservers -i inventory.ini --list-hosts

# Create a role skeleton
ansible-galaxy init roles/my-role

# Install a Galaxy role
ansible-galaxy install geerlingguy.docker

# Encrypt sensitive vars
ansible-vault encrypt vars/secrets.yaml
ansible-vault edit vars/secrets.yaml
ansible-playbook -i inventory.ini playbook.yaml --ask-vault-pass
```

### Key Distinctions

| These seem similar... | But they're different because... |
|---|---|
| `command` vs `shell` | `command` runs executables directly. `shell` passes through `/bin/sh` — allows pipes, redirects. |
| `copy` vs `template` | `copy` transfers as-is. `template` processes Jinja2 `{{ }}` first. |
| `state: present` vs `state: latest` | `present` installs if missing. `latest` always upgrades. |
| Handler vs Task | A task always runs. A handler only runs when notified, once per play. |
| Playbook vs Role | Playbook is the entry point. Role is a reusable module called from a playbook. |
| `ansible` vs `ansible-playbook` | `ansible` runs a single ad-hoc task. `ansible-playbook` runs a full YAML file. |

---

> **Next → 06 Jenkins** — you now have code in Git, a container in Docker Hub, an orchestration platform in Kubernetes, and automated server setup in Ansible. Jenkins ties all of it together: a single `git push` automatically triggers tests, build, push, and deploy — end to end.
