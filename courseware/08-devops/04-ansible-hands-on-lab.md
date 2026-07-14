# Ansible — Hands-On Lab
### EMS Project · Individual Practice · Half-Day Session (~3.5 hrs)

Companion practical to the **Ansible** module. Same local-machine-only
approach as Docker and Kubernetes — your "servers" this time are Docker
containers with SSH enabled, and you're the control node.

> **Starter kit:** `ansible-starter-kit.zip` — fill-in-the-blank
> Dockerfile, inventory, playbooks, and a Jinja2 template.

---

## Why this shape

Like Docker and Kubernetes, Ansible's core loop — describe desired
state, run it against a target, inspect the result — is solo by
design. The interesting "multiple" here is target **servers**, not
target **people**: you personally act as one control node managing a
small fleet of Docker containers standing in for real machines.

### Scope for this lab

Playbooks here **provision** servers — install Docker Engine, create a
deploy user, confirm they're configured and reachable — the same
groundwork you did by hand at the start of the Docker lab. They don't
pull and run the `ibm-ems-api` image on the target servers; that would
need Docker-in-Docker, real infrastructure complexity that would eat
the session on environment debugging instead of Ansible. It's a flagged
stretch goal instead — closing that last mile is really what a CI/CD
pipeline does, which is exactly where this series is headed next.

---

## Learning Objectives

- [ ] Explain the control node / managed node relationship
- [ ] Build an inventory file with groups
- [ ] Run ad-hoc commands against one host, a group, or the whole fleet
- [ ] Write and run a playbook with multiple tasks
- [ ] Use variables and a Jinja2 template to produce host-specific output
- [ ] Use handlers so actions only fire when something actually changed
- [ ] Prove idempotency — running a playbook twice changes nothing the second time
- [ ] Refactor a playbook into a role
- [ ] Use gathered facts and `when:` conditionals to target specific hosts
- [ ] Explain where Ansible fits relative to Docker and Kubernetes

---

## 1. The Scenario

```
   YOU (control node)
        │
        │  SSH (key-based, no passwords)
        │
   ┌────┼─────────────────┬─────────────────┐
   ▼    ▼                 ▼                 ▼
 web1               web2               db1
 (container,        (container,        (container,
  fake server)        fake server)       fake server)
```

Three Docker containers, each running SSH and Python (Ansible needs
Python on the managed node), standing in for three real machines. You
write playbooks once and run them against one, some, or all three.

---

## 2. Trainer Setup — Do This Before the Session

1. **Confirm Ansible works on your machine:**
   ```bash
   ansible --version
   ```
   On Windows, Ansible needs to run **inside WSL2** — it doesn't run
   natively on Windows. Same WSL2 setup as the Docker lab.
2. **Pre-pull the base image** used for target containers:
   ```bash
   docker pull ubuntu:22.04
   ```
3. **Share `ansible-starter-kit.zip`** with the batch before the
   session starts.
4. Confirm trainees still have Docker working from the previous labs —
   this lab builds target containers the same way.

---

## 3. Trainee Pre-Work Checklist

- [ ] Ansible installed — `ansible --version` works (Windows: inside WSL2)
- [ ] Docker still working — `docker --version`
- [ ] `ansible-starter-kit.zip` extracted

---

## 4. Session Timeline (≈ 3.5 hrs)

| # | Segment | Time |
|---|---------|------|
| — | Kickoff, where Ansible fits vs. Docker/K8s | 10 min |
| — | Trainer walkthrough of the starter kit | 10 min |
| Ex 1 | Build Your Fleet | 20 min |
| Ex 2 | Inventory & Ad-Hoc Commands | 20 min |
| Ex 3 | Your First Playbook | 30 min |
| Ex 4 | Variables & Templates | 25 min |
| Ex 5 | Handlers & Idempotency | 15 min |
| — | Break | 10 min |
| Ex 6 | Roles | 25 min |
| Ex 7 | Facts & Conditionals | 15 min |
| Ex 8 | Provisioning for Deployment | 20 min |
| Ex 9 | Cleanup & Wrap-Up | 10 min |
| | **Total** | **~210 min** |

---

## Exercise 1 — Build Your Fleet (20 min)

**Concepts:** control node vs. managed node, SSH key auth

Generate a dedicated keypair for this lab (don't reuse your GitHub key):
```bash
ssh-keygen -t ed25519 -f ~/.ssh/id_ansible_lab -N ""
```

Fill in `target-image/Dockerfile.starter`'s TODOs, save as
`target-image/Dockerfile`, then copy your new public key next to it:
```bash
cp ~/.ssh/id_ansible_lab.pub target-image/id_ansible_lab.pub
docker build -f target-image/Dockerfile -t ansible-target target-image/
```

Launch your fleet — three containers, three different host ports:
```bash
docker run -d --name web1 -p 2222:22 ansible-target
docker run -d --name web2 -p 2223:22 ansible-target
docker run -d --name db1  -p 2224:22 ansible-target
docker ps
```

Prove SSH works manually, before involving Ansible at all:
```bash
ssh -i ~/.ssh/id_ansible_lab -p 2222 ansible@127.0.0.1
   whoami
   exit
```

**Checkpoint:** you can manually SSH into all three containers without
being prompted for a password.

---

## Exercise 2 — Inventory & Ad-Hoc Commands (20 min)

**Concepts:** inventory files, groups, `ansible` ad-hoc CLI

Fill in the ports in `inventory.ini.starter`, save as `inventory.ini`.

```bash
ansible-inventory --list          # see how Ansible parsed your groups

ansible fleet -m ping              # the classic first test — no real "ping", checks Python + SSH
ansible webservers -a "uptime"
ansible db1 -a "whoami"
ansible all -m command -a "hostname"
```

**Checkpoint:** `ansible fleet -m ping` returns `SUCCESS` for all three
hosts. If any fail, re-check `inventory.ini` ports against
`docker ps` output.

---

## Exercise 3 — Your First Playbook (30 min)

**Concepts:** playbooks, tasks, modules, idempotency

Fill in the Exercise 3 TODOs in `site.yml.starter` (the nginx install
and service tasks only — leave the rest for later exercises), save as
`site.yml`.

```bash
ansible-playbook site.yml
```

Watch the output — each task reports `changed` or `ok`. Run it again
immediately:
```bash
ansible-playbook site.yml
```

**Checkpoint:** on the second run, every task reports `ok`, none report
`changed` — that's idempotency. Verify nginx is actually running:
```bash
ansible fleet -a "systemctl status nginx" --become
```

---

## Exercise 4 — Variables & Templates (25 min)

**Concepts:** `vars_files`, Jinja2 templates, per-host rendering

Fill in `group_vars/all.yml.starter` and `templates/index.html.j2.starter`,
save both without `.starter`. Fill in the "Deploy status page" task's
TODOs in `site.yml` (the `template` task — leave `notify` blank for now,
that's Exercise 5).

```bash
ansible-playbook site.yml
```

Check the rendered result on two different hosts:
```bash
curl http://127.0.0.1:2222 --max-time 2 2>/dev/null || \
  ansible web1 -a "cat /var/www/html/index.html" --become
ansible web2 -a "cat /var/www/html/index.html" --become
```

**Checkpoint:** `web1`'s and `web2`'s pages both say the correct
hostname for themselves — one template, two different rendered outputs.

---

## Exercise 5 — Handlers & Idempotency (15 min)

**Concepts:** `notify`/`handlers`, change-triggered actions

Fill in the `notify:` value and the handler's `name:` in `site.yml` so
they match exactly.

```bash
ansible-playbook site.yml -v      # -v shows which tasks report "changed"
```

Now change nothing and run again:
```bash
ansible-playbook site.yml -v
```

**Checkpoint:** the handler only fires (shows in the output) on runs
where the template task actually reports `changed` — not on every run.
Explain why that matters for a real production playbook run against
hundreds of servers.

---

## Exercise 6 — Roles (25 min)

**Concepts:** role structure, reusability

Scaffold a proper role:
```bash
ansible-galaxy init roles/nginx_setup
```

This creates `roles/nginx_setup/{tasks,handlers,templates,defaults,...}`.
Move your nginx-related tasks from `site.yml` into
`roles/nginx_setup/tasks/main.yml`, the handler into
`roles/nginx_setup/handlers/main.yml`, and the template file into
`roles/nginx_setup/templates/`. Then simplify `site.yml` to:

```yaml
---
- name: Provision EMS deployment servers
  hosts: fleet
  become: true
  vars_files:
    - group_vars/all.yml
  roles:
    - nginx_setup
  tasks:
    # (Docker Engine + deploy user tasks stay here for now — Exercise 8)
```

```bash
ansible-playbook site.yml
```

**Checkpoint:** the playbook behaves identically to before, but nginx
setup is now a self-contained, reusable role you could drop into any
other project.

---

## Exercise 7 — Facts & Conditionals (15 min)

**Concepts:** `gather_facts`, `when:`, `group_names`

Fill in the `when:` TODO in `facts-demo.yml.starter`, save as
`facts-demo.yml`.

```bash
ansible-playbook facts-demo.yml
```

**Checkpoint:** the "database servers only" message appears for `db1`
but not `web1`/`web2`. Explain what `group_names` contains for each
host and why the conditional works.

---

## Exercise 8 — Provisioning for Deployment (20 min)

**Concepts:** tying Ansible's output to the next stage of a real pipeline

Fill in the Docker Engine and deploy-user tasks at the bottom of
`site.yml` (already scaffolded, minimal TODOs — mostly reusing what
you've already learned this exercise).

```bash
ansible-playbook site.yml
ansible fleet -a "docker --version" --become
ansible fleet -a "id ems-deploy" --become
```

**Checkpoint:** every server in your fleet now has Docker Engine
installed and a `ems-deploy` user in the `docker` group — exactly the
state a real server needs to be in before a CI/CD pipeline (coming up
next in this series) hands it a container to run.

---

## Exercise 9 — Cleanup & Wrap-Up (10 min)

```bash
docker rm -f web1 web2 db1
docker rmi ansible-target
rm ~/.ssh/id_ansible_lab ~/.ssh/id_ansible_lab.pub
```

### Stretch goals (if time remains)

- **Ansible Vault**: `ansible-vault encrypt group_vars/all.yml` — try
  encrypting a variables file and running a playbook against it with
  `--ask-vault-pass`.
- **Actually running the EMS container via Ansible**: install the
  `community.docker` collection (`ansible-galaxy collection install
  community.docker`) and use its `docker_container` module to pull and
  run your `ibm-ems-api` image on a target — this needs Docker socket
  access inside the target container (Docker-in-Docker), noticeably
  fiddlier than everything else in this lab. Good for anyone who
  finishes early and wants the full loop closed for real.
- **Where Ansible fits vs. Docker and Kubernetes**: Docker packages an
  app. Kubernetes orchestrates already-containerized apps across a
  cluster. Ansible configures and provisions the underlying machines —
  container or not — before any of that happens. Discuss as a class
  which of the three you'd reach for to: (a) install security patches
  across 200 VMs, (b) restart a crashed container automatically, (c)
  package an app so it runs identically on any machine.

---

## Completion Checklist (self-tick)

- [ ] Built a target image and launched 3 containers as a fake fleet
- [ ] Connected via SSH key auth with no password prompts
- [ ] Wrote an inventory file with groups
- [ ] Ran ad-hoc commands against one host, a group, and the whole fleet
- [ ] Wrote and ran a multi-task playbook
- [ ] Proved a playbook is idempotent (second run: no changes)
- [ ] Used a Jinja2 template to render host-specific output
- [ ] Used a handler that only fires on an actual change
- [ ] Refactored tasks into a role with `ansible-galaxy init`
- [ ] Used `when:` with a gathered fact/`group_names` to target specific hosts
- [ ] Provisioned servers with Docker Engine + a deploy user

---

## Trainer's Evaluation Rubric

| Signal | What good looks like |
|--------|------------------------|
| Inventory | Correct groups, `fleet:children` used instead of duplicating hosts |
| Playbook structure | Tasks have clear `name:`, not left as raw module calls |
| Idempotency | Second run genuinely shows no `changed` tasks |
| Templates | Uses real Ansible variables/facts, not hardcoded values that happen to work once |
| Handlers | Fires only on change, correctly named to match `notify` |
| Roles | Actually uses `ansible-galaxy init` structure, not just a renamed task file |
| Conditionals | `when:` correctly scoped, not accidentally matching every host |

---

## Common Pitfalls / FAQ

**"`ansible fleet -m ping` fails with a connection timeout."**
Check `docker ps` — are the containers actually running? Do the ports
in `inventory.ini` match the `-p` flags you used in `docker run`?

**"SSH works manually but Ansible still fails."**
Almost always the private key path in `inventory.ini`
(`ansible_ssh_private_key_file`) doesn't match where you actually
generated the key, or the key has the wrong permissions
(`chmod 600 ~/.ssh/id_ansible_lab`).

**"`sudo: a password is required` when a task uses `become: true`."**
The target image's sudoers entry (`NOPASSWD:ALL`) didn't get set up
correctly — re-check the Dockerfile TODOs from Exercise 1 and rebuild
the image.

**"My handler never fires, even when I know the file changed."**
The `notify:` value and the handler's `name:` must match **exactly**,
including case and spacing — Ansible matches these as plain strings.

**"Playbook says `changed` every single run, even the second time."**
Usually means a task isn't actually idempotent — e.g. using the
`command`/`shell` module for something that has a proper dedicated
module (like `apt` or `service`) which already knows how to check
current state before acting.

**"On Windows, `ansible` command not found."**
Ansible doesn't run natively on Windows — install and run it inside
WSL2, same as Docker Desktop's WSL2 backend from the earlier labs.

---

## Command Reference (this lab)

| Command | What it does |
|---------|---------------|
| `ansible-inventory --list` | Show how Ansible parsed your inventory |
| `ansible <group> -m ping` | Test connectivity + Python availability |
| `ansible <group> -a "<cmd>"` | Run an ad-hoc shell command |
| `ansible-playbook <file>.yml` | Run a playbook |
| `ansible-playbook <file>.yml -v` | Run with verbose output (see what changed) |
| `ansible-galaxy init roles/<name>` | Scaffold a proper role structure |
| `ansible-vault encrypt <file>` | Encrypt a sensitive variables file |
| `ansible-playbook <file>.yml --ask-vault-pass` | Run a playbook using an encrypted file |

---

**Next in this series: CI/CD with Jenkins, Ansible, Docker, and
Kubernetes** — this one's a hybrid, not fully individual: everyone
writes and tests their own pipeline stages solo, but the class shares
one repo and one Jenkins instance so a real push genuinely triggers a
build queue and people see each other's builds land. Let me know when
you're ready and I'll ask scoping questions first, the same way we did
here.
