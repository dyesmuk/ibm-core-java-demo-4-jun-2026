# IBM EMS — DevOps Courseware

> **Tools:** Git · GitHub · Docker · Kubernetes · Ansible · Jenkins  
> **Project:** Node.js EMS API as the pipeline subject throughout  
> **Audience:** Beginner-friendly — explains every command  
> **Approach:** Cloud-agnostic, hands-on, EMS app as the through-line

---

## Module Index

| # | File | Topics Covered |
|---|------|---------------|
| 01 | `01-git-github.md` | Git basics, staging, commits, undoing things, GitHub, branches, merging, conflicts, forking, PRs, collaboration, Git Flow |
| 02 | `02-docker.md` | Why Docker, images vs containers, Docker commands, docker run, Dockerfile, multi-stage builds, Docker Compose, registry, volumes, networking, Swarm intro |
| 03 | `03-kubernetes.md` | Why K8s, cluster architecture, kubectl, YAML intro, Pods, ReplicaSets, Deployments, Services, ConfigMaps, Secrets, PVCs, networking, Ingress, full EMS deployment |
| 04 | `04-ansible.md` | IaC principles, Ansible architecture, inventory, modules, playbooks, variables, Jinja2 templates, conditionals, loops, handlers, roles, Vault, full EMS automation |
| 05 | `05-cicd-jenkins.md` | CI/CD concepts, Jenkins install, Pipelines, Git+npm CI, Docker integration, Ansible deploy, Kubernetes deploy, full end-to-end pipeline, best practices |
| 06 | `06-mcq-assessment.md` | 75 MCQs with full answer key and topic coverage map |

---

## Quick Start — Run the EMS App the DevOps Way

```bash
# Clone the app
git clone https://github.com/your-team/ibm-ems-api.git
cd ibm-ems-api

# Run with Docker Compose (Module 02)
docker compose up -d

# Check it works
curl http://localhost:3000/api/health

# Deploy to Kubernetes locally (Module 03)
minikube start
kubectl apply -R -f k8s/
kubectl port-forward service/ems-api-service 3000:80 -n ems
```

---

## Suggested Delivery Schedule

| Day | Module | Topics |
|-----|--------|--------|
| Day 1 AM | 01 | Git basics, staging, commits, undoing mistakes |
| Day 1 PM | 01 | GitHub, branches, merging, PRs, collaboration |
| Day 2 AM | 02 | Docker concepts, commands, run, images, Dockerfile |
| Day 2 PM | 02 | Docker Compose, Registry, networking, volumes |
| Day 3 AM | 03 | Kubernetes architecture, kubectl, YAML, Pods, Deployments |
| Day 3 PM | 03 | Services, ConfigMaps, Secrets, full EMS on K8s |
| Day 4 AM | 04 | Ansible concepts, inventory, playbooks, variables |
| Day 4 PM | 04 | Handlers, roles, Vault, EMS Ansible project |
| Day 5 AM | 05 | CI/CD concepts, Jenkins install, first pipeline |
| Day 5 PM | 05 | Docker + Ansible + Kubernetes in pipeline |
| Assessment | 06 | 75-question MCQ |

---

## IBM Syllabus Mapping

### Git and GitHub

| IBM Topic | Module Section |
|-----------|---------------|
| Welcome | 1.1 |
| Git Basics | 1.2 |
| Undoing Things | 1.3 |
| The Basics of GitHub | 1.4 |
| Working with Branches | 1.5 |
| Forking and Contributing | 1.6 |
| Collaboration | 1.7 |

### Docker

| IBM Topic | Module Section |
|-----------|---------------|
| Introduction | 2.1 |
| Docker Commands | 2.3 |
| Docker Run | 2.4 |
| Docker Images | 2.5 |
| Docker Compose | 2.6 |
| Docker Registry | 2.7 |
| Docker Engine, Storage and Networking | 2.8 |
| Docker on Mac & Windows | 2.2 (Install section) |
| Container Orchestration — Docker Swarm & Kubernetes | 2.9 |
| Conclusion | Summary |

### Kubernetes

| IBM Topic | Module Section |
|-----------|---------------|
| Introduction | 3.1 |
| Kubernetes Overview | 3.1 |
| Setup Kubernetes | 3.2 |
| Kubernetes Concepts | 3.3–3.6 |
| YAML Introduction | 3.3 |
| PODs, ReplicaSets, Deployments | 3.4 |
| Networking in Kubernetes | 3.7 |
| Services | 3.5 |
| Conclusion + Appendix (kubeadm) | 3.9 |

### Ansible

| IBM Topic | Module Section |
|-----------|---------------|
| Course Overview | 4.1 |
| DevOps Principles and Ansible's Role | 4.1 |
| Ansible Components | 4.4–4.8 |

### CI/CD with Jenkins, Ansible, Docker, Kubernetes

| IBM Topic | Module Section |
|-----------|---------------|
| Introduction | 5.1 |
| CI/CD using GIT, Jenkins and npm | 5.4 |
| Integrating Server in CI/CD pipeline | 5.5 |
| Integrating Docker in CI/CD pipeline | 5.6 |
| Integrating Kubernetes in CI/CD pipeline | 5.8 |

---

## EMS App Repository Structure

```
ibm-ems-api/
│
├── src/                         ← Node.js application code
│   ├── routes/
│   ├── models/
│   └── server.js
│
├── Dockerfile                   ← Module 02: containerise the app
├── .dockerignore
├── docker-compose.yml           ← Module 02: multi-container setup
├── docker-compose.dev.yml
│
├── Jenkinsfile                  ← Module 05: CI/CD pipeline
│
├── k8s/                         ← Module 03: Kubernetes manifests
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── api/
│   │   ├── deployment.yaml
│   │   └── service.yaml
│   └── mongo/
│       ├── deployment.yaml
│       ├── service.yaml
│       └── pvc.yaml
│
├── ansible/                     ← Module 04: Ansible automation
│   ├── site.yaml
│   ├── deploy.yaml
│   ├── inventory/
│   │   ├── production.ini
│   │   └── staging.ini
│   ├── group_vars/
│   └── roles/
│       ├── nodejs/
│       ├── mongodb/
│       └── ems-api/
│
└── .github/
    └── workflows/               ← optional GitHub Actions alongside Jenkins
```

---

## Key Commands — Quick Reference

### Git

```bash
git init                          # start repo
git clone <url>                   # clone remote
git add . && git commit -m "msg"  # stage + commit
git push / git pull               # sync with remote
git checkout -b feature/name      # new branch
git merge feature/name            # merge into current
git stash / git stash pop         # save/restore WIP
git revert <hash>                 # safe undo (shared branches)
git reset --soft HEAD~1           # undo last commit, keep changes
```

### Docker

```bash
docker build -t image:tag .       # build image
docker run -d -p 3000:3000 image  # run container
docker ps / docker ps -a          # list containers
docker logs -f <name>             # follow logs
docker exec -it <name> sh         # shell into container
docker compose up -d              # start all services
docker compose down               # stop all services
docker push registry/image:tag    # push to registry
```

### Kubernetes

```bash
kubectl apply -f file.yaml        # create/update resource
kubectl get pods/deployments/svc  # list resources
kubectl describe pod <name>       # detailed info + events
kubectl logs -f <pod>             # follow logs
kubectl exec -it <pod> -- sh      # shell into pod
kubectl rollout undo deployment/x # rollback
kubectl port-forward svc/x 3000:80 # local access
kubectl delete -f file.yaml       # delete resource
```

### Ansible

```bash
ansible all -m ping               # test connectivity
ansible-playbook site.yaml        # run playbook
ansible-playbook site.yaml --check   # dry run
ansible-playbook site.yaml --diff    # show what changes
ansible-vault create vault.yaml   # create encrypted file
ansible-vault edit vault.yaml     # edit encrypted file
```

### Jenkins

```bash
# Run Jenkins in Docker
docker run -d -p 8080:8080 \
  -v jenkins-data:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts-jdk17

# Get initial password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

---

## The Complete EMS DevOps Flow

```
Developer writes code
        │
        ▼
git push → GitHub
        │
        ▼  (webhook)
Jenkins Pipeline
        │
        ├── npm ci
        ├── npm test         ← fail = stop here, notify
        ├── docker build
        ├── docker push
        │
        ├── Ansible deploy → staging VM (docker pull + run)
        │   curl /api/health ✅
        │
        └── kubectl apply → Kubernetes cluster (production)
            kubectl rollout status
            curl /api/health ✅
                │
                ▼
        App running in production 🚀
```

---

## Tech Versions

```
git                   2.43+
docker                26.x
docker compose        v2.x
kubernetes            1.29+
minikube              1.32+
kubectl               1.29+
ansible               2.16+
jenkins               2.x LTS (JDK 17)
node.js               20 LTS (EMS app)
```
