# 03 — YAML Quick Reference
> **DevOps Hands-On Series** | Module 3 of 6 | Reference Guide

---

## Why a Whole Module on YAML?

Because every tool from here on is configured in YAML. One bad indent = broken Kubernetes Deployment. Wrong spacing = Ansible playbook won't parse. A tab instead of spaces = Jenkins pipeline refuses to start.

Spending 20 minutes here saves hours of debugging later.

```
01 Git & GitHub  — version-controlled the app
02 Docker        — docker-compose.yml was your first YAML file
03 YAML          ← YOU ARE HERE
04 Kubernetes    — every Pod, Deployment, Service = a YAML file
05 Ansible       — every playbook and role = YAML
06 Jenkins       — Jenkinsfile references YAML configs
```

**How to use this guide:** Read it once before Module 04. Keep it open while writing Kubernetes and Ansible files. When something breaks, check your indentation first.

---

## What Is YAML?

YAML = **YAML Ain't Markup Language** (yes, it's a recursive acronym)

It's a human-readable configuration language used across the DevOps world:

- Docker Compose
- Kubernetes
- Ansible
- GitHub Actions
- Jenkins pipelines
- Pretty much everything modern

### `.yml` vs `.yaml`

Zero difference. Both work identically.

```
docker-compose.yml   ← totally fine
docker-compose.yaml  ← also fine
```

---

## The One Rule That Breaks Everything

**Use spaces. Not tabs. Only spaces.**

YAML uses indentation to understand structure. Tabs look identical to spaces in most editors but are completely different characters. Most editors default to spaces — but double-check yours.

In VS Code: install the **YAML** extension by Red Hat. It highlights errors in real time.

---

## Syntax Fundamentals

### Key-Value Pairs

```yaml
name: Sonu
age: 47
city: Hyderabad
```

Keys and values separated by `: ` (colon + space). Simple.

### Lists

Use `-` to mark each item. Items must be indented consistently:

```yaml
skills:
  - Java
  - Angular
  - Docker
  - Kubernetes
```

### Nested Objects

Indentation creates hierarchy:

```yaml
employee:
  id: 101
  name: Sonu
  department: IT
```

### Nested + Lists Together

This is very common in Kubernetes and Ansible:

```yaml
employees:
  - id: 101
    name: Sonu

  - id: 102
    name: Monu
```

### Data Types

```yaml
# Booleans
isActive: true
isDeleted: false

# Numbers
salary: 50000
rating: 4.5

# Strings (with or without quotes)
message: Hello World
message: "Hello World"     # same thing

# Multi-line string (preserves newlines with |)
notes: |
  This is line one.
  This is line two.

# Multi-line string folded into one line (use >)
description: >
  This whole block
  becomes one line.
```

### Comments

```yaml
# This is a comment — YAML ignores everything after #
name: Sonu
# age: 47   ← commented out
```

### Document Separators

Multiple YAML documents in one file (common in Kubernetes):

```yaml
apiVersion: v1
kind: Service
---                    # three dashes = start of next document
apiVersion: apps/v1
kind: Deployment
```

---

## Real Examples from the Series

### Docker Compose (you've already seen this)

```yaml
version: '3'

services:

  frontend:
    image: angular-app
    ports:
      - "4200:80"

  backend:
    image: spring-app
    ports:
      - "8080:8080"

  mongodb:
    image: mongo
```

### Kubernetes (what's coming next)

```yaml
apiVersion: apps/v1
kind: Deployment

metadata:
  name: hello-deployment

spec:
  replicas: 3
  selector:
    matchLabels:
      app: hello-app
  template:
    metadata:
      labels:
        app: hello-app
    spec:
      containers:
        - name: hello-container
          image: yourname/hello-express:1.0
          ports:
            - containerPort: 3000
```

Same concepts as Docker Compose (image, container, port) but more verbose because Kubernetes supports far more configuration.

### Ansible (coming in Module 05)

```yaml
---
- hosts: webservers
  become: yes

  tasks:
    - name: Install nginx
      apt:
        name: nginx
        state: present

    - name: Start nginx
      service:
        name: nginx
        state: started
```

---

## Common Mistakes

### Wrong Indentation

```yaml
# ❌ INVALID — same indentation level, no structure
services:
backend:
image: nginx
```

```yaml
# ✅ VALID — indentation creates the hierarchy
services:
  backend:
    image: nginx
```

### Tabs Instead of Spaces

Your editor might look fine but YAML will fail. Configure VS Code to use spaces: **Settings → Editor: Insert Spaces → On**.

### Inconsistent Indent Width

Pick 2 spaces and stick to it throughout the file. Mixing 2 and 4 spaces breaks parsing.

---

## Key Symbols Cheat Sheet

| Symbol | Meaning |
|---|---|
| `:` | Key-value separator (must have a space after it) |
| `-` | List item |
| `#` | Comment |
| `---` | Document separator (multiple docs in one file) |
| `|` | Multi-line string, preserves newlines |
| `>` | Multi-line string, folded to one line |
| `{{ }}` | Jinja2 templating (used in Ansible) |

---

## YAML in DevOps Tools

| Tool | YAML Usage |
|---|---|
| Docker Compose | `docker-compose.yml` — service definitions |
| Kubernetes | Every object (Pod, Deployment, Service, ConfigMap...) |
| Ansible | Every playbook, role, variable file |
| GitHub Actions | `.github/workflows/*.yml` |
| Jenkins | Kubernetes manifests applied by the pipeline |

---

## Mental Model

Think of YAML as a **tree**. Each level of indentation is a branch. Lists are just multiple branches at the same level. Everything connects upward to a root.

```yaml
company:           # root
  name: Acme       # branch
  teams:           # branch with sub-branches
    - name: DevOps
      size: 5
    - name: Backend
      size: 10
```

---

## Quick Tips

- Read YAML top-to-bottom, parent-to-child
- If something breaks, check indentation first
- Use 2 spaces per indent level consistently
- Never mix tabs and spaces
- When in doubt, validate at [yaml.me](https://yaml.me) or in VS Code with the YAML extension

---

> **Next → 04 Kubernetes** — every object you create (Pod, Deployment, Service) is a YAML file applied with `kubectl apply -f`. The structure you just learned applies to every single line of it.
