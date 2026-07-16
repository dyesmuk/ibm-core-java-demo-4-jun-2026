# DevOps — Discussion Questions & Answers

---

## Git & GitHub — Fundas

**1. What is Git?**

Git is a distributed version control system that tracks changes to files over time. Every developer has a full copy of the repository including its entire history — no single point of failure. Created by Linus Torvalds in 2005 for managing the Linux kernel.

**2. What is the difference between Git and GitHub?**

Git is the version control tool that runs on your local machine. GitHub is a cloud platform that hosts Git repositories and adds collaboration features — pull requests, code review, issue tracking, Actions (CI/CD). Other alternatives: GitLab, Bitbucket.

**3. What are the three areas of Git?**

Working Directory (files you're editing), Staging Area/Index (files marked ready to commit with `git add`), and Repository (permanent commit history in `.git/`). The flow is: edit → stage → commit.

**4. What does `git init` do?**

Creates a new empty Git repository in the current directory by creating a hidden `.git/` folder. This folder contains the entire version history and configuration. Never delete it — that would destroy all version history.

**5. What is the difference between `git fetch` and `git pull`?**

`git fetch` downloads changes from the remote but does not merge them into your branch — safe to run anytime. `git pull` is `git fetch` + `git merge` — downloads and immediately merges. Prefer `git fetch` followed by `git diff` to review changes before merging.

**6. What is a commit?**

A commit is a permanent snapshot of the staged changes saved to the repository history. Each commit has a unique SHA-1 hash, author, timestamp, message, and a pointer to the parent commit. Commits are the building blocks of Git history.

**7. What is the difference between `git reset` and `git revert`?**

`git revert <hash>` creates a new commit that undoes a previous commit — safe for shared branches because history is preserved. `git reset <hash>` moves the branch pointer back — rewrites history and should never be used on pushed shared branches. Use `revert` on shared branches, `reset` only on local unshared work.

**8. What is `git stash`?**

`git stash` temporarily saves uncommitted changes to a stack so you can switch branches with a clean working directory. `git stash pop` restores the most recent stash. Useful when you need to urgently fix something on another branch without losing current work.

**9. What is a merge conflict and how do you resolve it?**

A merge conflict occurs when two branches modified the same line of the same file differently. Git cannot auto-merge and marks the conflict in the file with `<<<<<<<`, `=======`, `>>>>>>>` markers. You manually edit the file to the correct version, then `git add` and `git commit` to complete the merge.

**10. What is a pull request?**

A pull request (PR) is a GitHub feature — not a Git command — to propose merging one branch into another. It provides a code review interface where teammates can comment, request changes, and approve before merging. PRs are the standard collaboration mechanism in team projects.

**11. What is the difference between `git merge` and `git rebase`?**

`git merge` creates a merge commit combining two branch histories — preserves full history. `git rebase` replays your commits on top of another branch — creates a linear, cleaner history but rewrites commit hashes. Use merge for shared branches; rebase for cleaning up local feature branches before merging.

**12. What is `.gitignore`?**

A file listing patterns of files and directories Git should not track. Common entries: `node_modules/`, `.env`, `target/`, `*.class`, `dist/`. Without it, build outputs, secrets, and OS metadata pollute the repository. Create one at the project root before the first commit.

**13. What is the difference between `git clone` and `git fork`?**

`git clone` creates a local copy of a repository on your machine. Forking (a GitHub concept) creates a copy of someone else's repository under your own GitHub account — used for contributing to projects you don't own. You fork on GitHub, then clone your fork locally.

**14. What is branch protection?**

Branch protection rules on GitHub prevent direct pushes to important branches (like `main`). Rules include: require pull requests before merging, require at least N approving reviews, require CI checks to pass. This ensures code quality and prevents accidental overwrites on shared branches.

---

## Docker — Fundas

**15. What is Docker?**

Docker is a platform for packaging applications and their dependencies into containers — lightweight, portable, isolated units that run consistently across any environment. It solves the "works on my machine" problem by bundling the app with everything it needs.

**16. What is the difference between an image and a container?**

An image is a read-only blueprint — a snapshot of the filesystem and configuration. A container is a running instance of an image — like an object instantiated from a class. Multiple containers can run from the same image simultaneously.

**17. What is a Dockerfile?**

A Dockerfile is a text file with instructions to build a Docker image layer by layer. Key instructions: `FROM` (base image), `WORKDIR` (set working directory), `COPY` (copy files), `RUN` (execute commands), `EXPOSE` (document port), `CMD` (default command). Each instruction creates a new layer.

**18. What is Docker layer caching?**

Docker caches each layer during build. If a layer's instruction hasn't changed, Docker reuses the cached layer instead of rebuilding it — much faster. This is why you `COPY package*.json ./` and `RUN npm install` before `COPY . .` — dependency installation is cached unless `package.json` changes.

**19. What is the difference between `CMD` and `ENTRYPOINT` in a Dockerfile?**

`ENTRYPOINT` defines the executable that always runs — it cannot be overridden by `docker run` arguments (only with `--entrypoint`). `CMD` provides default arguments to `ENTRYPOINT` or is the default command if no `ENTRYPOINT` is set — it can be overridden by `docker run` arguments. Use `ENTRYPOINT` for the main executable; `CMD` for default arguments.

**20. What is a multi-stage build in Docker?**

Multi-stage builds use multiple `FROM` statements in one Dockerfile. Earlier stages handle building (installing compilers, running tests), and the final stage copies only the build output — no build tools included. This produces much smaller production images.

**21. What is Docker Compose?**

Docker Compose is a tool for defining and running multi-container applications with a single YAML file (`docker-compose.yml`). `docker compose up` starts all services (app, database, cache) with correct networking and dependencies. Essential for local development of microservices.

**22. What is the difference between a Docker volume and a bind mount?**

A named volume is managed by Docker and stored in Docker's data directory — portable, persists across container restarts. A bind mount maps a specific host directory into the container — great for development (code changes reflect instantly). Use volumes for database data in production; bind mounts for development source code.

**23. What is `docker compose down -v`?**

`docker compose down` stops and removes containers and networks but keeps named volumes (preserving database data). `-v` also removes named volumes — data is deleted. Use `down` for routine stops; `down -v` only when you want a completely fresh start.

**24. What is a Docker registry?**

A Docker registry is a storage and distribution system for Docker images. Docker Hub is the public default registry. Private registries (AWS ECR, Azure ACR, GitHub Container Registry, self-hosted) store proprietary images. `docker push` uploads images; `docker pull` downloads them.

**25. What is the difference between `docker stop` and `docker kill`?**

`docker stop` sends `SIGTERM` to the container — giving the application time to shut down gracefully (save state, close connections) before sending `SIGKILL` after a timeout (default 10s). `docker kill` sends `SIGKILL` immediately — abrupt termination, no cleanup. Always prefer `docker stop`.

---

## Kubernetes — Fundas

**26. What is Kubernetes?**

Kubernetes (K8s) is an open-source container orchestration platform that automates deploying, scaling, and managing containerised applications across a cluster of machines. It provides self-healing, rolling updates, load balancing, service discovery, and configuration management.

**27. What is the difference between Docker and Kubernetes?**

Docker packages and runs containers on a single machine. Kubernetes orchestrates containers across a cluster of machines — scheduling them onto nodes, restarting failures, scaling replicas, and routing traffic. Docker Compose is to one machine what Kubernetes is to a cluster.

**28. What is a Pod?**

A Pod is the smallest deployable unit in Kubernetes — a wrapper around one or more containers that share the same network namespace (same IP, port space) and storage. Containers in the same Pod communicate via `localhost`. Pods are ephemeral — they are created and destroyed; don't rely on their IP addresses.

**29. What is a Deployment in Kubernetes?**

A Deployment manages a set of identical Pods (a ReplicaSet) and handles rolling updates and rollbacks. You declare the desired state (`replicas: 3, image: v2.0`) and Kubernetes continuously reconciles actual state to match. `kubectl rollout undo deployment/myapp` rolls back to the previous version.

**30. What is the difference between a Deployment and a ReplicaSet?**

A ReplicaSet ensures a specified number of identical Pods are running. A Deployment manages ReplicaSets and adds rolling update capability — when you update the image, it creates a new ReplicaSet, scales it up, and scales the old one down. Always use Deployments, not bare ReplicaSets.

**31. What is a Kubernetes Service?**

A Service provides a stable network endpoint for a set of Pods. Since Pod IPs change when Pods are recreated, a Service gives a constant IP/DNS name and load-balances traffic across matching Pods. Types: `ClusterIP` (internal only), `NodePort` (external via node port), `LoadBalancer` (cloud load balancer).

**32. What is the difference between `ClusterIP`, `NodePort`, and `LoadBalancer` services?**

`ClusterIP` exposes the service only within the cluster — for internal communication between services. `NodePort` opens a port on every node in the cluster — accessible externally but not production-grade. `LoadBalancer` provisions a cloud load balancer with an external IP — the standard way to expose services in production on cloud providers.

**33. What is a ConfigMap?**

A ConfigMap stores non-sensitive configuration data as key-value pairs. Pods can consume it as environment variables or mounted files. Separating configuration from the container image allows the same image to run in dev, staging, and production with different settings — without rebuilding.

**34. What is a Kubernetes Secret?**

A Secret stores sensitive data (passwords, tokens, keys) encoded in Base64. They are similar to ConfigMaps but treated with more care — restricted access, not stored in logs. Base64 is encoding, not encryption — enable encryption at rest for production. Inject as env vars or mounted volumes.

**35. What is a liveness probe vs readiness probe?**

A liveness probe checks if the container is alive — if it fails, Kubernetes restarts the container. A readiness probe checks if the container is ready to serve traffic — if it fails, the container is removed from the Service's load balancer but not restarted. Use both: liveness for health, readiness to prevent traffic to a starting or overloaded container.

**36. What is a namespace in Kubernetes?**

A namespace provides logical isolation within a cluster — like a virtual cluster. Use namespaces to separate environments (dev, staging, prod) or teams within the same physical cluster. Resources in different namespaces are isolated by default but can communicate. `default` namespace is used when none is specified.

**37. What is `kubectl` and what are the most used commands?**

`kubectl` is the command-line tool for interacting with a Kubernetes cluster. Most used: `apply -f file.yaml` (create/update resources), `get pods/deployments/svc` (list resources), `describe pod <name>` (details + events), `logs <pod>` (view logs), `exec -it <pod> -- sh` (shell into pod), `rollout undo deployment/<name>` (rollback).

**38. What is a PersistentVolumeClaim (PVC)?**

A PVC is a request for storage by a Pod. The developer declares the required size and access mode; Kubernetes finds or dynamically provisions a matching PersistentVolume and binds them. Data in a PVC persists beyond Pod restarts and rescheduling — essential for databases in Kubernetes.

---

## Ansible

**39. What is Ansible?**

Ansible is an agentless IT automation tool for provisioning servers, deploying applications, and managing configurations. It uses YAML playbooks and connects to managed nodes over SSH — no agent software needed on target machines. It follows the Infrastructure as Code (IaC) principle.

**40. What is idempotency in Ansible?**

An idempotent operation produces the same result regardless of how many times it is run. Ansible tasks are designed to be idempotent — running `apt: name=nginx state=present` twice won't install nginx twice; the second run is a no-op if nginx is already installed. This makes playbooks safe to re-run.

**41. What is the difference between a playbook and a role?**

A playbook is a YAML file containing a list of plays — tasks targeting specific hosts. A role is a reusable, structured collection of tasks, handlers, variables, templates, and files for a specific purpose (install nginx, deploy the app). Roles are included in playbooks and can be shared via Ansible Galaxy.

**42. What is an Ansible inventory?**

The inventory file lists the managed hosts and organises them into groups. `[web]` group contains web servers; `[db]` contains database servers. Group variables (`group_vars/web.yml`) apply to all hosts in the group. The inventory is the "who" — the playbook is the "what."

**43. What is an Ansible handler?**

A handler is a task that only runs when notified by another task that made a change. Example: the nginx config task notifies the "Restart nginx" handler — if the config didn't change (idempotent), the handler never runs. Handlers run once at the end of the play regardless of how many tasks notified them.

**44. What is Ansible Vault?**

Ansible Vault encrypts sensitive data (passwords, API keys) in YAML files so they can be safely committed to version control. `ansible-vault encrypt secrets.yml` encrypts the file. `ansible-playbook site.yml --ask-vault-pass` decrypts at runtime. Without Vault, secrets would be stored in plain text in your repository.

**45. What is the difference between `when` and `register` in Ansible?**

`register: result` saves the output of a task to a variable for later use. `when: result.stdout == "running"` conditionally runs a task based on a condition — including previously registered variables, host facts, or group membership. Together they enable dynamic, conditional automation.

---

## CI/CD & Jenkins

**46. What is CI/CD?**

CI (Continuous Integration) automatically builds and tests code on every push — catching integration problems early. CD (Continuous Delivery/Deployment) automatically deploys tested code to staging or production. Together they eliminate manual release processes and enable multiple deployments per day safely.

**47. What is Jenkins?**

Jenkins is an open-source automation server used to implement CI/CD pipelines. It has a rich plugin ecosystem (Git, Docker, Kubernetes, Ansible), supports declarative and scripted pipelines via Jenkinsfiles, and can trigger builds from webhooks, schedules, or manual triggers.

**48. What is a Jenkinsfile?**

A Jenkinsfile is a text file stored in the project repository that defines the CI/CD pipeline as code using Groovy DSL. Storing it in the repo means the pipeline is version-controlled alongside the application code and reviewed in pull requests — Infrastructure as Code for pipelines.

**49. What is the difference between declarative and scripted pipelines in Jenkins?**

Declarative pipeline has a structured syntax with predefined sections (`pipeline`, `stages`, `steps`) — easier to read, write, and validate. Scripted pipeline is more flexible Groovy code but harder to maintain. Use declarative for most pipelines; use scripted only when declarative's structure is too restrictive.

**50. What is a Jenkins stage?**

A stage is a logical phase of the pipeline — `Checkout`, `Build`, `Test`, `Docker Build`, `Deploy`. Stages are visible in the Jenkins UI as a progress pipeline. Failed stages stop the pipeline (by default) and make it clear exactly where the build broke.

**51. What is the purpose of `post { always { cleanWs() } }` in a Jenkinsfile?**

`post` blocks run after pipeline completion. `always` runs regardless of success or failure. `cleanWs()` removes the workspace files — prevents leftover files from a previous build polluting the next build. Other conditions: `success`, `failure`, `unstable`.

**52. What is a webhook and how does it trigger Jenkins?**

A webhook is an HTTP callback — GitHub sends a POST request to the Jenkins URL when a push event occurs. Jenkins receives the webhook and immediately starts the pipeline for the pushed branch. This eliminates the need for Jenkins to poll for changes, making builds near-instantaneous after a push.

**53. What is the difference between `npm install` and `npm ci` in a CI pipeline?**

`npm install` may update `package-lock.json` and installs based on `package.json` version ranges. `npm ci` installs exactly the versions in `package-lock.json`, deletes `node_modules` first, and fails if lock file is out of sync. `npm ci` is deterministic, faster in CI, and should always be used in pipelines.

**54. How do you store secrets in Jenkins pipelines?**

Use Jenkins Credentials Store — store passwords, tokens, SSH keys via Manage Jenkins → Credentials. Reference in Jenkinsfile with `credentials('my-credential-id')` or `withCredentials([...])` block. Never hardcode secrets in Jenkinsfiles — they would be visible to anyone with repository access.

**55. What happens when a Jenkins pipeline stage fails?**

By default, the pipeline stops at the failed stage, marks the build as `FAILED`, and runs `post { failure { ... } }` blocks. `catchError` or `try-catch` can handle failures gracefully. Use `post { failure { } }` for notifications and rollback logic — e.g. `kubectl rollout undo` after a failed deployment.

---

## Real Time

**56. `git push` rejected — why and how to fix?**

The remote has commits your local branch doesn't have — you need to integrate them first. Run `git pull --rebase` (or `git fetch` + `git rebase origin/main`) to apply your commits on top of the latest remote history, resolve any conflicts, then push again. Never use `git push --force` on shared branches.

**57. Docker build succeeds but container won't start — where to look?**

Run `docker logs <container-name>` immediately — the application's error message is there. Common causes: missing environment variable, wrong port, application crash at startup, missing configuration file. Also check `docker inspect <container>` for the actual command and environment.

**58. Kubernetes Pod in `CrashLoopBackOff` — how to debug?**

`kubectl describe pod <name>` shows events and the exit code. `kubectl logs <pod>` shows the last output before crash. `kubectl logs <pod> --previous` shows logs from the previous (crashed) instance. Common causes: missing Secret/ConfigMap, wrong image, application startup failure, liveness probe misconfigured too aggressively.

**59. Ansible playbook fails on some hosts — how to investigate?**

Re-run with `-vvv` for verbose output showing exact commands and responses per host. Use `--limit hostname` to run only on the failing host. Check SSH connectivity (`ansible hostname -m ping`). Common causes: SSH key not set up, Python not installed on host, wrong user, permission denied, package not available.

**60. Jenkins pipeline builds but app is not updated in Kubernetes — why?**

Most likely: (1) `kubectl set image` was called but used `latest` tag — Kubernetes pulled from cache (always use specific version tags like build number), (2) `imagePullPolicy: IfNotPresent` — Kubernetes didn't pull the new image, (3) Rollout completed but readiness probe is passing with old code (wrong pod being checked). Tag images with build number, set `imagePullPolicy: Always` for `latest`.

---

## More

**61. What is the difference between horizontal and vertical scaling?**

Vertical scaling adds more resources (CPU/RAM) to an existing server — limited by hardware ceiling and involves downtime. Horizontal scaling adds more server instances — theoretically unlimited and the cloud-native approach. Kubernetes makes horizontal scaling trivial: `kubectl scale deployment/app --replicas=10`.

**62. What is Infrastructure as Code (IaC)?**

IaC means managing and provisioning infrastructure through machine-readable configuration files (Ansible playbooks, Kubernetes YAML, Terraform HCL) instead of manual processes. Benefits: repeatable, version-controlled, reviewable, testable infrastructure — same as application code.

**63. What is the difference between Docker Swarm and Kubernetes?**

Both are container orchestration tools. Docker Swarm is simpler to set up and uses Docker Compose-style YAML — good for small-scale. Kubernetes is more complex but vastly more powerful — auto-scaling, advanced networking, rich ecosystem, industry standard for production. Kubernetes has won the orchestration space.

**64. What is a rolling update in Kubernetes?**

A rolling update gradually replaces old Pods with new ones — at no point is the entire application down. Kubernetes creates new Pods (new version), waits for them to be ready, then terminates old ones. Configure with `maxSurge` (max extra pods) and `maxUnavailable` (max pods that can be down during update).

**65. What is the GitOps pattern?**

GitOps is a practice where the desired state of infrastructure and applications is stored in Git. Changes are made via pull requests; a tool (Argo CD, Flux) automatically syncs the cluster to match the Git state. Git becomes the single source of truth for both application code and deployment configuration.

**66. What is the purpose of a `health check` endpoint in an API?**

A health check endpoint (`/api/health`) returns a simple status indicating the application is running and its dependencies (database, message broker) are reachable. Used by Kubernetes liveness/readiness probes, load balancers, monitoring systems, and deployment pipelines to verify the app is healthy before routing traffic.

**67. What is image tagging strategy in Docker CI/CD?**

Use specific, immutable tags — never rely on `latest` in production. Common strategies: semantic versioning (`v1.2.3`), Git commit SHA (`abc1234`), build number (`build-42`). Tagging with the build number or commit SHA makes deployments traceable — you can see exactly which code version is running.

**68. What is the difference between `COPY` and `ADD` in a Dockerfile?**

Both copy files into the image. `ADD` additionally supports URL sources and auto-extracts `.tar` archives. However, `COPY` is preferred for simplicity and predictability — it only copies local files. Use `COPY` unless you specifically need `ADD`'s auto-extraction feature.

**69. What is `kubectl port-forward` used for?**

`kubectl port-forward service/my-service 3000:80` forwards your local port 3000 to the service's port 80 inside the cluster. Used for development and debugging — access a service that has no external exposure without modifying the service type. It is temporary and only available while the command runs.

**70. What is a canary deployment?**

A canary deployment releases a new version to a small percentage of users first — e.g. 5% of traffic goes to the new version while 95% stays on the old. If metrics are healthy, the percentage gradually increases. Reduces risk by exposing potential issues to a small audience before full rollout.

---

## Answer Summary Table

| # | Question (short) | Key answer |
|---|---------|------------|
| 1 | What is Git? | Distributed VCS — full history on every machine |
| 5 | fetch vs pull | fetch downloads; pull downloads + merges |
| 7 | reset vs revert | revert = safe new commit; reset = rewrites history |
| 11 | merge vs rebase | merge preserves history; rebase creates linear history |
| 15 | What is Docker? | Containerisation — portable, isolated app packaging |
| 16 | Image vs container | Blueprint vs running instance |
| 20 | Multi-stage build | Smaller image — build tools not in final image |
| 21 | Docker Compose | Multi-container local orchestration via YAML |
| 26 | What is Kubernetes? | Container orchestration across a cluster |
| 28 | Pod | Smallest K8s unit — one or more containers |
| 29 | Deployment | Manages ReplicaSets + rolling updates |
| 31 | Service | Stable endpoint for ephemeral Pods |
| 35 | Liveness vs readiness | Restart vs traffic routing |
| 39 | Ansible | Agentless automation via SSH + YAML playbooks |
| 40 | Idempotency | Same result every run |
| 44 | Ansible Vault | Encrypted secrets in version control |
| 46 | CI/CD | Auto build/test/deploy on every push |
| 47 | Jenkins | Open-source CI/CD automation server |
| 48 | Jenkinsfile | Pipeline as code in the repository |
| 64 | Rolling update | Gradual replacement — zero downtime |
| 65 | GitOps | Git as single source of truth for infrastructure |
