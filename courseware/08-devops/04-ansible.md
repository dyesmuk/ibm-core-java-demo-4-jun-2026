# Module 04 — Ansible

## Learning Objectives
- Understand what Ansible is and how it fits into DevOps
- Understand Infrastructure as Code (IaC) principles
- Install and configure Ansible
- Write playbooks, use inventory, variables, and roles
- Automate server provisioning and application deployment
- EMS: automate environment setup and app deployment with Ansible

---

## 4.1 DevOps Principles and the Role of Ansible

### What Problem Does Ansible Solve?

Imagine you need to set up 10 servers identically — install Node.js, configure MongoDB, set up firewall rules, deploy your app. Without automation:

```
Manual approach:
  SSH into server 1 → run 30 commands → pray nothing differs
  SSH into server 2 → run the same 30 commands → maybe you forget one
  SSH into server 3 → run the same 30 commands → different version installed
  ...
  Result: "Snowflake servers" — each one slightly different, impossible to reproduce
```

With Ansible:

```
Write a playbook once → run it against all 10 servers simultaneously
Result: Identical, reproducible, documented infrastructure
```

This is **Infrastructure as Code (IaC)** — treating infrastructure configuration the same way you treat application code: versioned, reviewed, testable, repeatable.

### Why Ansible Specifically?

| Tool | Approach | Language | Agent needed? |
|------|----------|---------|---------------|
| Ansible | Procedural / declarative | YAML | ❌ Agentless (uses SSH) |
| Chef | Procedural | Ruby | ✅ Needs agent |
| Puppet | Declarative | Puppet DSL | ✅ Needs agent |
| Terraform | Declarative | HCL | ❌ Agentless |

**Ansible advantages:**
- Agentless — works over SSH, nothing to install on target servers
- YAML — readable, no special language to learn
- Idempotent — running a playbook twice gives the same result
- Large community — thousands of pre-built roles on Ansible Galaxy

---

## 4.2 Ansible Architecture

```
Control Node                    Managed Nodes (target servers)
(your laptop / CI server)       (where changes are applied)

┌──────────────┐     SSH       ┌────────────┐
│   Ansible    │ ─────────────▶│  Server 1  │
│              │               └────────────┘
│  Inventory   │     SSH       ┌────────────┐
│  Playbooks   │ ─────────────▶│  Server 2  │
│  Roles       │               └────────────┘
│  Variables   │     SSH       ┌────────────┐
└──────────────┘ ─────────────▶│  Server 3  │
                                └────────────┘
```

**Key point:** No agent is installed on managed nodes. Ansible pushes changes over SSH. The managed nodes only need Python and SSH.

---

## 4.3 Installing Ansible

```bash
# Ubuntu/Debian — control node only
sudo apt update
sudo apt install ansible

# Mac
brew install ansible

# Python pip (any platform)
pip install ansible

# Verify
ansible --version
# ansible [core 2.16.x]
```

---

## 4.4 Ansible Components

### Inventory — Which Servers to Manage

The inventory file lists the servers Ansible manages and groups them.

```ini
# inventory.ini (INI format)

# Ungrouped hosts
192.168.1.100
server01.ibm.local

# Group: web servers
[web]
web01.ibm.local
web02.ibm.local
192.168.1.101

# Group: database servers
[db]
db01.ibm.local  ansible_user=ubuntu  ansible_port=22

# Group of groups
[backend:children]
web
db

# Group variables (applied to all hosts in [web])
[web:vars]
http_port=80
node_env=production
```

```yaml
# inventory.yml (YAML format — more readable for complex setups)
all:
  children:
    web:
      hosts:
        web01.ibm.local:
          ansible_user: ubuntu
        web02.ibm.local:
          ansible_user: ubuntu
    db:
      hosts:
        db01.ibm.local:
          ansible_user: ubuntu
          ansible_port: 2222
  vars:
    ansible_ssh_private_key_file: ~/.ssh/id_ed25519
```

```bash
# Test connectivity
ansible all -i inventory.ini -m ping

# Run ad-hoc command on all hosts
ansible all -i inventory.ini -m shell -a "uptime"

# Run on a specific group
ansible web -i inventory.ini -m shell -a "node --version"

# Run on a single host
ansible web01.ibm.local -i inventory.ini -m ping
```

### Modules — Building Blocks

Modules are the units of work Ansible executes. There are thousands of built-in modules.

```bash
# Common modules — ad-hoc usage
ansible all -m ping                                   # connectivity test
ansible all -m shell   -a "df -h"                     # run shell command
ansible all -m command -a "ls /app"                   # run command (safer than shell)
ansible all -m copy    -a "src=file.txt dest=/tmp/"   # copy file
ansible all -m file    -a "path=/tmp/test state=directory"  # create directory
ansible all -m apt     -a "name=nginx state=present" --become  # install package
ansible all -m service -a "name=nginx state=started"  # manage services
```

---

## 4.5 Playbooks

A **playbook** is a YAML file that defines a series of tasks to run on managed nodes.

### Anatomy of a Playbook

```yaml
# site.yaml — example playbook
---                         # optional YAML document start marker
- name: Configure web servers   # a "play" — targets a group
  hosts: web                    # which inventory group to target
  become: true                  # use sudo for privileged operations
  vars:                         # variables for this play
    app_dir: /opt/ems-api
    node_version: "20"

  tasks:                        # list of tasks to execute in order
    - name: Update apt cache
      apt:
        update_cache: true
        cache_valid_time: 3600  # only update if cache is older than 1 hour

    - name: Install Node.js dependencies
      apt:
        name:
          - curl
          - git
          - build-essential
        state: present          # ensure these are installed

    - name: Install Node.js via NodeSource
      shell: |
        curl -fsSL https://deb.nodesource.com/setup_{{ node_version }}.x | bash -
        apt-get install -y nodejs
      args:
        creates: /usr/bin/node  # skip if node already exists (idempotency)

    - name: Verify Node.js installation
      command: node --version
      register: node_version_output   # save output to variable

    - name: Print Node.js version
      debug:
        msg: "Node.js version: {{ node_version_output.stdout }}"

    - name: Create app directory
      file:
        path: "{{ app_dir }}"
        state: directory
        owner: ubuntu
        group: ubuntu
        mode: '0755'
```

### Running a Playbook

```bash
# Run the playbook
ansible-playbook -i inventory.ini site.yaml

# Dry run (check mode) — shows what WOULD change without making changes
ansible-playbook -i inventory.ini site.yaml --check

# Show diff of changes
ansible-playbook -i inventory.ini site.yaml --diff

# Run only specific tasks (by tag)
ansible-playbook -i inventory.ini site.yaml --tags "nodejs"

# Run with extra variables
ansible-playbook -i inventory.ini site.yaml --extra-vars "node_version=20"

# Verbose output
ansible-playbook -i inventory.ini site.yaml -v    # verbose
ansible-playbook -i inventory.ini site.yaml -vvv  # very verbose (debugging)

# Limit to specific hosts
ansible-playbook -i inventory.ini site.yaml --limit web01.ibm.local
```

---

## 4.6 Variables

Variables make playbooks reusable across environments.

### Variable Precedence (highest to lowest)

```
1. command line --extra-vars
2. task vars
3. role defaults
4. host_vars file
5. group_vars file
6. inventory vars
7. role vars
8. playbook vars
```

### group_vars and host_vars

```yaml
# group_vars/all.yaml — variables for ALL hosts
app_name: ibm-ems-api
app_dir: /opt/ems-api
app_port: 3000
node_version: "20"
```

```yaml
# group_vars/web.yaml — variables for [web] group only
nginx_port: 80
worker_processes: auto
```

```yaml
# host_vars/web01.ibm.local.yaml — variables for one specific host
ansible_user: ubuntu
server_role: primary
```

### Using Variables in Playbooks

```yaml
- name: Deploy EMS API
  hosts: web
  vars:
    app_version: "1.2.0"
    mongo_host: "{{ hostvars['db01.ibm.local']['ansible_host'] }}"

  tasks:
    - name: Create app directory
      file:
        path: "{{ app_dir }}"
        state: directory

    - name: Deploy application
      git:
        repo: "https://github.com/ibm-team/ibm-ems-api.git"
        dest: "{{ app_dir }}"
        version: "{{ app_version }}"
        force: yes

    - name: Create .env file from template
      template:
        src: templates/env.j2
        dest: "{{ app_dir }}/.env"
        mode: '0600'
```

### Jinja2 Templates

Ansible uses Jinja2 templating for dynamic configuration files.

```
# templates/env.j2 (Jinja2 template)
NODE_ENV={{ node_env | default('production') }}
PORT={{ app_port }}
MONGO_URI=mongodb://{{ mongo_user }}:{{ mongo_password }}@{{ mongo_host }}:27017/ems
JWT_SECRET={{ jwt_secret }}
LOG_LEVEL={{ log_level | default('info') }}
```

```yaml
# Task to generate the file
- name: Generate .env from template
  template:
    src: templates/env.j2
    dest: "{{ app_dir }}/.env"
    owner: ubuntu
    mode: '0600'
```

---

## 4.7 Conditionals, Loops, and Handlers

### Conditionals — `when`

```yaml
- name: Install nginx (Ubuntu only)
  apt:
    name: nginx
    state: present
  when: ansible_os_family == "Debian"

- name: Install nginx (CentOS only)
  yum:
    name: nginx
    state: present
  when: ansible_os_family == "RedHat"

- name: Start service only if port check passed
  service:
    name: ems-api
    state: started
  when:
    - app_deployed is defined
    - app_deployed == true
```

### Loops

```yaml
- name: Install multiple packages
  apt:
    name: "{{ item }}"
    state: present
  loop:
    - nodejs
    - npm
    - git
    - curl

- name: Create multiple directories
  file:
    path: "{{ item }}"
    state: directory
    mode: '0755'
  loop:
    - /opt/ems-api
    - /opt/ems-api/logs
    - /opt/ems-api/uploads

- name: Create users
  user:
    name: "{{ item.name }}"
    groups: "{{ item.groups }}"
    shell: /bin/bash
  loop:
    - { name: alice, groups: developers }
    - { name: bob,   groups: developers }
```

### Handlers — Run Only When Notified

Handlers run at the end of a play, only if triggered by a task that changed something.

```yaml
tasks:
  - name: Copy nginx config
    copy:
      src: nginx.conf
      dest: /etc/nginx/nginx.conf
    notify: Restart nginx       # ← triggers the handler if the file changed

  - name: Copy app config
    template:
      src: app.conf.j2
      dest: /etc/nginx/conf.d/app.conf
    notify: Restart nginx       # same handler, but runs only ONCE at the end

handlers:
  - name: Restart nginx
    service:
      name: nginx
      state: restarted
```

---

## 4.8 Roles — Reusable and Organised

A Role is a structured way to organise related tasks, variables, templates, and files into a reusable unit.

### Role Directory Structure

```
roles/
└── ems-api/
    ├── tasks/
    │   └── main.yaml       ← tasks entry point
    ├── handlers/
    │   └── main.yaml       ← handlers
    ├── templates/
    │   └── env.j2          ← Jinja2 templates
    ├── files/
    │   └── ems.service     ← static files to copy
    ├── vars/
    │   └── main.yaml       ← role variables (high priority)
    ├── defaults/
    │   └── main.yaml       ← default values (low priority — easy to override)
    └── meta/
        └── main.yaml       ← role metadata, dependencies
```

### Create a Role

```bash
# Let Ansible create the structure
ansible-galaxy role init roles/ems-api
ansible-galaxy role init roles/nodejs
ansible-galaxy role init roles/mongodb
```

### `roles/nodejs/tasks/main.yaml`

```yaml
---
- name: Install Node.js dependencies
  apt:
    name:
      - curl
      - git
    state: present
    update_cache: true

- name: Add NodeSource repository
  shell: |
    curl -fsSL https://deb.nodesource.com/setup_{{ nodejs_version }}.x | bash -
  args:
    creates: /etc/apt/sources.list.d/nodesource.list

- name: Install Node.js
  apt:
    name: nodejs
    state: present

- name: Verify Node.js
  command: node --version
  register: node_ver
  changed_when: false   # this task never "changes" anything

- name: Show Node.js version
  debug:
    msg: "Node.js {{ node_ver.stdout }} installed"
```

### `roles/nodejs/defaults/main.yaml`

```yaml
---
nodejs_version: "20"
npm_packages: []
```

### `roles/ems-api/tasks/main.yaml`

```yaml
---
- name: Create app user
  user:
    name: "{{ app_user }}"
    system: true
    shell: /sbin/nologin
    create_home: false

- name: Create app directories
  file:
    path: "{{ item }}"
    state: directory
    owner: "{{ app_user }}"
    mode: '0755'
  loop:
    - "{{ app_dir }}"
    - "{{ app_dir }}/logs"

- name: Clone EMS API repository
  git:
    repo: "{{ app_repo }}"
    dest: "{{ app_dir }}"
    version: "{{ app_version }}"
    force: true
  become_user: "{{ app_user }}"
  notify: Restart EMS API

- name: Install npm dependencies
  npm:
    path: "{{ app_dir }}"
    state: present
  become_user: "{{ app_user }}"

- name: Create .env configuration file
  template:
    src: env.j2
    dest: "{{ app_dir }}/.env"
    owner: "{{ app_user }}"
    mode: '0600'
  notify: Restart EMS API

- name: Install systemd service file
  copy:
    src: ems.service
    dest: /etc/systemd/system/ems-api.service
  notify:
    - Reload systemd
    - Restart EMS API

- name: Enable and start EMS API service
  systemd:
    name: ems-api
    enabled: true
    state: started
```

### `roles/ems-api/files/ems.service`

```ini
[Unit]
Description=IBM EMS Node.js API
After=network.target

[Service]
Type=simple
User=ems
WorkingDirectory=/opt/ems-api
ExecStart=/usr/bin/node server.js
Restart=on-failure
RestartSec=10
StandardOutput=append:/opt/ems-api/logs/app.log
StandardError=append:/opt/ems-api/logs/error.log
EnvironmentFile=/opt/ems-api/.env

[Install]
WantedBy=multi-user.target
```

### Using Roles in a Playbook

```yaml
# site.yaml — top-level playbook
---
- name: Set up web servers
  hosts: web
  become: true
  roles:
    - role: nodejs
      vars:
        nodejs_version: "20"
    - role: ems-api
      vars:
        app_version: "main"
        app_repo: "https://github.com/ibm-team/ibm-ems-api.git"

- name: Set up database servers
  hosts: db
  become: true
  roles:
    - role: mongodb
      vars:
        mongodb_version: "7"
```

---

## 4.9 Vault — Encrypting Secrets

```bash
# Create an encrypted file
ansible-vault create group_vars/all/vault.yaml
# Enter vault password when prompted
# Add secrets:
# vault_mongo_password: "super-secret"
# vault_jwt_secret: "another-secret"

# Edit an encrypted file
ansible-vault edit group_vars/all/vault.yaml

# Encrypt an existing file
ansible-vault encrypt group_vars/all/vault.yaml

# Decrypt a file
ansible-vault decrypt group_vars/all/vault.yaml

# Run playbook with vault (prompts for password)
ansible-playbook site.yaml --ask-vault-pass

# Run playbook with vault password file
ansible-playbook site.yaml --vault-password-file ~/.vault_pass.txt
```

```yaml
# group_vars/all/vault.yaml (encrypted)
vault_mongo_password: "super-secret"
vault_jwt_secret: "jwt-secret-key"

# group_vars/all/vars.yaml (plain text — references vault vars)
mongo_password: "{{ vault_mongo_password }}"
jwt_secret:     "{{ vault_jwt_secret }}"
```

---

## 4.10 EMS — Complete Ansible Project

```
ansible/
├── inventory/
│   ├── production.ini
│   └── staging.ini
├── group_vars/
│   ├── all/
│   │   ├── vars.yaml
│   │   └── vault.yaml     ← encrypted
│   └── web/
│       └── vars.yaml
├── host_vars/
│   └── web01.ibm.local.yaml
├── roles/
│   ├── common/            ← base setup (users, packages, firewall)
│   ├── nodejs/            ← install Node.js
│   ├── mongodb/           ← install & configure MongoDB
│   └── ems-api/           ← deploy the app
├── templates/
│   └── env.j2
├── site.yaml              ← main playbook
└── deploy.yaml            ← deployment-only playbook
```

```bash
# Initial full setup
ansible-playbook -i inventory/staging.ini site.yaml --ask-vault-pass

# Deploy only (faster — skips infra setup)
ansible-playbook -i inventory/staging.ini deploy.yaml --ask-vault-pass

# Check what would change
ansible-playbook -i inventory/production.ini site.yaml --check --diff
```

---

## Summary

| Concept | Description |
|---------|-------------|
| Inventory | List of servers Ansible manages |
| Module | A unit of work (apt, copy, shell, service, git…) |
| Task | One invocation of a module |
| Play | Set of tasks targeting a group of hosts |
| Playbook | YAML file containing one or more plays |
| Handler | Task triggered by notify — runs once at end of play |
| Role | Reusable, structured collection of tasks, vars, templates |
| Variable | Parameterised values — group_vars, host_vars, defaults |
| Template | Jinja2 file with variables replaced at runtime |
| Vault | Encrypted storage for secrets |
| Idempotency | Running the same playbook multiple times = same result |

**Next → Module 05: CI/CD with Jenkins, Ansible, Docker & Kubernetes**
