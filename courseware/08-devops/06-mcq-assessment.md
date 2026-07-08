# DevOps MCQ Assessment — Question Bank (75 Questions)

> **Instructions:** One correct answer per question. Covers all 5 modules — Git, Docker, Kubernetes, Ansible, CI/CD.

---

## Module 01 — Git and GitHub (Q1–Q18)

**Q1.** What is the difference between Git and GitHub?

- A) Git is a cloud service; GitHub is a command-line tool
- B) Git is the version control tool that runs locally; GitHub is a cloud platform that hosts Git repositories
- C) They are two names for the same product
- D) GitHub replaced Git in 2015

**Answer: B**

---

**Q2.** What are the three areas in Git's data model?

- A) Local, Remote, Cloud
- B) Working Directory, Staging Area (Index), Repository
- C) Source, Build, Deploy
- D) Branch, Commit, Tag

**Answer: B**

---

**Q3.** What does `git add .` do?

- A) Commits all changes to the repository
- B) Stages all changed files in the current directory for the next commit
- C) Creates a new branch
- D) Pushes all changes to the remote

**Answer: B**

---

**Q4.** What is the correct command to create and switch to a new branch called `feature/search` in one step?

- A) `git branch feature/search && git checkout feature/search`
- B) `git checkout -b feature/search`
- C) `git new-branch feature/search`
- D) `git switch feature/search --new`

**Answer: B**
*Both A and B work, but B is the canonical single-command form. `git switch -c feature/search` is the modern equivalent.*

---

**Q5.** What does `git stash` do?

- A) Permanently deletes uncommitted changes
- B) Temporarily saves uncommitted changes to a stack so you can switch branches with a clean working directory
- C) Stages all files for commit
- D) Pushes local commits to a remote stash

**Answer: B**

---

**Q6.** You accidentally committed a file with credentials to your branch (NOT yet pushed). What is the safest way to undo the commit while keeping your file edits?

- A) `git revert HEAD`
- B) `git reset --soft HEAD~1`
- C) `git branch -d HEAD`
- D) `git checkout HEAD~1`

**Answer: B**
*`--soft` removes the commit but keeps changes staged. `--mixed` keeps changes unstaged. `--hard` discards everything.*

---

**Q7.** What does `git revert abc1234` do?

- A) Deletes the commit `abc1234` from history
- B) Creates a new commit that reverses the changes made by `abc1234` — safe for shared branches
- C) Resets the branch pointer to `abc1234`
- D) Marks `abc1234` as a bad commit

**Answer: B**

---

**Q8.** When does a merge conflict occur?

- A) When two branches have different names
- B) When two branches modified the same line of the same file differently
- C) When you try to merge a branch into itself
- D) When the remote repository is unavailable

**Answer: B**

---

**Q9.** What is a Pull Request (PR)?

- A) A Git command that downloads code from the remote
- B) A GitHub feature to propose merging a branch, enabling code review before integration
- C) A request for another developer to push their code
- D) A command to pull the latest changes from upstream

**Answer: B**

---

**Q10.** What is the purpose of a `.gitignore` file?

- A) To list files that will be automatically committed
- B) To specify files and directories that Git should not track or include in commits
- C) To configure the remote repository URL
- D) To define merge conflict resolution rules

**Answer: B**

---

**Q11.** What does `git fetch origin` do compared to `git pull`?

- A) `git fetch` pushes changes; `git pull` downloads them
- B) `git fetch` downloads changes from the remote but does NOT merge them; `git pull` downloads AND merges
- C) They are identical
- D) `git fetch` only works on the main branch; `git pull` works on any branch

**Answer: B**

---

**Q12.** What is the purpose of `git remote add origin <url>`?

- A) Creates a new remote repository
- B) Connects the local repository to a remote URL and names the connection 'origin'
- C) Downloads the remote repository
- D) Sets the default branch name

**Answer: B**

---

**Q13.** In Git Flow, what is the purpose of the `develop` branch?

- A) It is deployed directly to production
- B) It is the integration branch where all feature branches merge before release
- C) It is a backup copy of `main`
- D) It contains only hotfixes

**Answer: B**

---

**Q14.** What does `git log --oneline` show?

- A) Only uncommitted changes
- B) A compact one-line-per-commit view of the commit history
- C) Files changed in the last commit
- D) Remote tracking information

**Answer: B**

---

**Q15.** What does `git restore server.js` do?

- A) Restores the file from the remote repository
- B) Discards unstaged changes to `server.js` in the working directory, reverting to the last commit
- C) Restores a deleted file from the staging area
- D) Restores a file to its state before the last merge

**Answer: B**

---

**Q16.** What is the difference between forking and cloning a repository?

- A) Forking copies to your local machine; cloning copies to your GitHub account
- B) Forking creates your own copy on GitHub; cloning downloads a copy to your local machine
- C) Forking requires admin access; cloning does not
- D) They are the same operation

**Answer: B**

---

**Q17.** Why do branch protection rules on `main` exist?

- A) To prevent GitHub from deleting the branch
- B) To require pull requests and passing tests before any code is merged into main, ensuring code quality
- C) To encrypt the branch contents
- D) To prevent Git from tracking changes

**Answer: B**

---

**Q18.** What does `git push -u origin main` do that `git push origin main` does not?

- A) It pushes all branches, not just main
- B) It sets `origin main` as the default upstream so future `git push` commands work without arguments
- C) It forces the push even if there are conflicts
- D) It signs the push with a GPG key

**Answer: B**

---

## Module 02 — Docker (Q19–Q36)

**Q19.** What is the fundamental difference between a Docker container and a Virtual Machine?

- A) Containers are slower than VMs
- B) Containers share the host OS kernel and are lightweight; VMs run a full guest OS and are heavy
- C) VMs are faster to start than containers
- D) Containers require a hypervisor; VMs do not

**Answer: B**

---

**Q20.** What is a Docker image?

- A) A running instance of an application
- B) A read-only blueprint/template used to create containers — like a class in OOP
- C) A saved snapshot of a running container's state
- D) The Docker configuration file

**Answer: B**

---

**Q21.** What does `docker run -d -p 3000:3000 --name ems-api ibm-ems/api:latest` do?

- A) Builds a Docker image from the current directory
- B) Runs a container in the background, maps host port 3000 to container port 3000, names it `ems-api`
- C) Pushes the image to Docker Hub
- D) Creates a Docker network

**Answer: B**

---

**Q22.** What is the purpose of `WORKDIR` in a Dockerfile?

- A) Specifies the directory on the host machine to mount
- B) Sets the working directory inside the container for subsequent instructions
- C) Defines the output directory for build artifacts
- D) Restricts which directories the container can access

**Answer: B**

---

**Q23.** Why is the `COPY package*.json ./` instruction placed before `COPY . .` in a Dockerfile?

- A) package.json must be installed before source files can be compiled
- B) To leverage layer caching — if source files change but package.json doesn't, Docker reuses the cached npm install layer
- C) Because Docker processes COPY instructions in reverse order
- D) It is a convention with no performance impact

**Answer: B**

---

**Q24.** What does `docker exec -it ems-api sh` do?

- A) Creates a new container from the ems-api image
- B) Opens an interactive shell inside the already-running ems-api container
- C) Stops and restarts the ems-api container
- D) Executes the container's default CMD

**Answer: B**

---

**Q25.** What is a `.dockerignore` file used for?

- A) Preventing Docker from using cached layers
- B) Specifying files and directories to exclude from the build context sent to the Docker daemon
- C) Configuring which registries Docker can access
- D) Hiding Docker credentials

**Answer: B**

---

**Q26.** What does `docker compose down -v` do compared to `docker compose down`?

- A) `down -v` is faster
- B) `down -v` also removes named volumes (database data), while `down` keeps volumes
- C) `down -v` shows verbose output
- D) They are identical

**Answer: B**

---

**Q27.** In Docker Compose, what does `depends_on` guarantee?

- A) The dependent service is healthy before the other starts
- B) The listed services are STARTED before this service — but not necessarily ready/healthy
- C) The services run on the same Docker network
- D) Services share the same volume

**Answer: B**
*`depends_on` only waits for the container to start, not for the application inside to be ready. Use health checks for true readiness.*

---

**Q28.** What is the difference between a Docker volume and a bind mount?

- A) Volumes are faster; bind mounts use more disk space
- B) Volumes are managed by Docker and stored in Docker's area; bind mounts map a specific host directory into the container
- C) Bind mounts are persistent; volumes are ephemeral
- D) Volumes only work on Linux; bind mounts work everywhere

**Answer: B**

---

**Q29.** What does `--restart unless-stopped` do when passed to `docker run`?

- A) Restarts the container every time the application exits successfully
- B) Automatically restarts the container if it stops for any reason, UNLESS it was manually stopped
- C) Prevents the container from stopping
- D) Restarts Docker Engine if the container fails

**Answer: B**

---

**Q30.** What is the benefit of multi-stage Docker builds?

- A) They allow building images on multiple machines simultaneously
- B) They reduce the final image size by using one stage for building and another for the minimal production image
- C) They enable running multiple commands in parallel
- D) They cache all layers permanently

**Answer: B**

---

**Q31.** What does `docker system prune` do?

- A) Uninstalls Docker
- B) Removes all stopped containers, unused networks, dangling images, and build cache
- C) Deletes all named volumes
- D) Resets Docker to factory defaults

**Answer: B**

---

**Q32.** On what port range must Docker Swarm NodePort services be exposed?

- A) 80–8080
- B) 30000–32767
- C) 1024–8192
- D) Any available port

**Answer: B**
*This is actually the Kubernetes NodePort range — same concept applies in Swarm.*

---

**Q33.** What is the purpose of `EXPOSE 3000` in a Dockerfile?

- A) It opens port 3000 on the host machine
- B) It documents which port the containerised application listens on — does NOT actually open or map it
- C) It restricts the container to only use port 3000
- D) It configures the application to listen on port 3000

**Answer: B**

---

**Q34.** When two containers are on the same Docker Compose network, how can they reach each other?

- A) Via the host machine's IP address
- B) Via the service name defined in docker-compose.yml — Docker's embedded DNS resolves it
- C) Via `localhost` on both containers
- D) Via their container IDs

**Answer: B**

---

**Q35.** What does `docker logs -f ems-api` do?

- A) Shows the last 10 log lines and exits
- B) Shows and continuously follows (streams) new log output from the ems-api container
- C) Writes logs to a file
- D) Shows only error-level logs

**Answer: B**

---

**Q36.** What is Docker Swarm?

- A) A tool for building Docker images faster
- B) Docker's built-in container orchestration system for running containers across multiple machines
- C) A Docker network type
- D) A Docker registry for private images

**Answer: B**

---

## Module 03 — Kubernetes (Q37–Q52)

**Q37.** What does the Kubernetes Control Plane's Scheduler do?

- A) Stores cluster state in etcd
- B) Assigns pods to worker nodes based on available resources and constraints
- C) Manages API authentication
- D) Runs the containers on each node

**Answer: B**

---

**Q38.** What is the smallest deployable unit in Kubernetes?

- A) Container
- B) Pod
- C) Node
- D) Deployment

**Answer: B**
*A Pod wraps one or more containers — it is the smallest unit K8s can schedule.*

---

**Q39.** What does `kubectl apply -f deployment.yaml` do compared to `kubectl create -f deployment.yaml`?

- A) `apply` is deprecated; use `create` instead
- B) `apply` creates OR updates the resource; `create` only creates (fails if it already exists)
- C) `create` is declarative; `apply` is imperative
- D) They are identical

**Answer: B**

---

**Q40.** What is the purpose of a ReplicaSet?

- A) Replicate data across database nodes
- B) Maintain a specified number of identical Pod replicas running at all times
- C) Balance network traffic across Pods
- D) Version control for Kubernetes resources

**Answer: B**

---

**Q41.** What does a Kubernetes Deployment add over a ReplicaSet?

- A) The ability to run pods across multiple namespaces
- B) Rolling update strategy and rollback capability
- C) Persistent storage for pods
- D) Network policies

**Answer: B**

---

**Q42.** What command rolls back a Kubernetes Deployment to the previous version?

- A) `kubectl reset deployment/ems-api`
- B) `kubectl rollout undo deployment/ems-api`
- C) `kubectl revert deployment/ems-api`
- D) `kubectl deploy --rollback ems-api`

**Answer: B**

---

**Q43.** What is the difference between a `readinessProbe` and a `livenessProbe`?

- A) Liveness restarts the container; readiness controls traffic routing
- B) Readiness controls whether the pod receives traffic; liveness controls whether the container is restarted
- C) They are identical
- D) Readiness checks CPU; liveness checks memory

**Answer: B**

---

**Q44.** Which Service type makes a Kubernetes service accessible from outside the cluster using a cloud-provided load balancer?

- A) ClusterIP
- B) LoadBalancer
- C) NodePort
- D) ExternalName

**Answer: B**

---

**Q45.** What is a Kubernetes ConfigMap used for?

- A) Storing encrypted secrets
- B) Storing non-sensitive configuration data (key-value pairs) that pods can consume as environment variables or files
- C) Configuring the Kubernetes API server
- D) Defining network policies

**Answer: B**

---

**Q46.** How are values stored in a Kubernetes Secret encoded?

- A) AES-256 encrypted
- B) Base64 encoded (not encrypted by default — use encryption at rest for production)
- C) SHA-256 hashed
- D) Plain text

**Answer: B**

---

**Q47.** What is a PersistentVolumeClaim (PVC)?

- A) A claim to a node's CPU resources
- B) A request for storage by a pod — Kubernetes provisions actual storage to match it
- C) A lock on a persistent volume to prevent deletion
- D) A persistent log of volume operations

**Answer: B**

---

**Q48.** What does `kubectl port-forward service/ems-api-service 3000:80 -n ems` do?

- A) Exposes the service permanently on port 3000
- B) Forwards your local port 3000 to the service's port 80 inside the cluster — useful for testing without a LoadBalancer
- C) Changes the service port to 3000
- D) Creates a NodePort service on port 3000

**Answer: B**

---

**Q49.** In Kubernetes, how do pods on the same cluster communicate with each other by service name?

- A) Via the kube-proxy IP tables
- B) Via Kubernetes internal DNS — each service gets a name like `<service-name>.<namespace>.svc.cluster.local`
- C) Via direct pod IP addresses
- D) Via the node's hostname

**Answer: B**

---

**Q50.** What is the purpose of a Kubernetes namespace?

- A) Naming containers within a pod
- B) Providing logical isolation within a cluster — separate environments (dev/staging/prod) in one cluster
- C) Defining the network namespace for pod networking
- D) Organising container images in the registry

**Answer: B**

---

**Q51.** What does `kubectl rollout status deployment/ems-api` show?

- A) The rollout history
- B) Whether the rolling update has completed successfully or is still in progress
- C) The current replica count
- D) Pod resource usage during rollout

**Answer: B**

---

**Q52.** What does the `track` field in `@for` (Angular) have in common with Kubernetes `selector.matchLabels`?

- A) Both use YAML configuration
- B) Both uniquely identify items in a list so the framework/orchestrator can efficiently update only what changed
- C) They use the same syntax
- D) Nothing — they are completely unrelated concepts

**Answer: B**

---

## Module 04 — Ansible (Q53–Q62)

**Q53.** Why is Ansible described as "agentless"?

- A) It does not require configuration files
- B) No software agent needs to be installed on managed nodes — Ansible uses SSH to connect and Python to execute tasks
- C) It works without an internet connection
- D) It does not need a control node

**Answer: B**

---

**Q54.** What is "idempotency" in the context of Ansible?

- A) The ability to run playbooks in parallel
- B) Running the same playbook multiple times produces the same result — tasks that are already in the desired state are skipped
- C) Encrypting sensitive variables in playbooks
- D) Executing tasks in the correct dependency order

**Answer: B**

---

**Q55.** What is an Ansible inventory file?

- A) A list of all modules available in Ansible
- B) A file listing the managed hosts and groups of hosts that Ansible will connect to
- C) A record of all playbook executions
- D) A file containing encrypted credentials

**Answer: B**

---

**Q56.** What does `become: true` in an Ansible play mean?

- A) The play becomes the default play
- B) Ansible uses privilege escalation (sudo) to run tasks as root or another privileged user
- C) The playbook always succeeds
- D) The play runs in the foreground

**Answer: B**

---

**Q57.** What is an Ansible handler?

- A) A task that runs before all other tasks
- B) A task that only runs when notified by another task — and only once at the end of the play, even if notified multiple times
- C) A special module for managing files
- D) A variable that handles errors

**Answer: B**

---

**Q58.** What is an Ansible Role?

- A) A user role (admin/user) for Ansible authentication
- B) A structured, reusable unit that organises tasks, handlers, variables, templates, and files for a specific purpose
- C) A built-in module for managing user accounts
- D) An Ansible Galaxy plugin

**Answer: B**

---

**Q59.** What is Ansible Vault used for?

- A) Storing playbook execution history
- B) Encrypting sensitive data like passwords and API keys so they can be safely committed to version control
- C) Caching inventory data for offline use
- D) Locking playbooks from being edited

**Answer: B**

---

**Q60.** What does `ansible-playbook site.yaml --check` do?

- A) Validates YAML syntax only
- B) Performs a dry run — shows what changes WOULD be made without actually making them
- C) Runs only the tasks marked with `check_mode: true`
- D) Runs the playbook and generates a report

**Answer: B**

---

**Q61.** What is `group_vars/all/vars.yaml` used for in Ansible?

- A) Storing tasks that apply to all groups
- B) Defining variables that apply to ALL hosts in the inventory
- C) Storing the inventory for all environments
- D) Defining default connection settings for all hosts

**Answer: B**

---

**Q62.** Which Ansible concept allows you to loop over a list of packages and install each one?

- A) `with_items` (legacy) or `loop` (modern) directive
- B) `repeat` directive
- C) `foreach` module
- D) `iterate` keyword

**Answer: A**
*Both `loop` and the legacy `with_items` achieve this. `loop` is the modern standard.*

---

## Module 05 — CI/CD with Jenkins (Q63–Q75)

**Q63.** What is the difference between Continuous Integration and Continuous Deployment?

- A) Continuous Integration deploys to production; Continuous Deployment only builds
- B) CI automatically builds and tests on every commit; Continuous Deployment additionally deploys to production automatically after tests pass
- C) They are the same concept
- D) CI requires Jenkins; Continuous Deployment requires GitHub Actions

**Answer: B**

---

**Q64.** What is a Jenkinsfile?

- A) Jenkins's main configuration file stored on the server
- B) A text file stored in the project repository that defines the CI/CD pipeline as code using a Groovy DSL
- C) A plugin configuration file for Jenkins
- D) A list of credentials used by Jenkins

**Answer: B**

---

**Q65.** What does the `agent any` directive in a declarative Jenkinsfile mean?

- A) The pipeline runs on all available agents simultaneously
- B) Jenkins can run this pipeline on any available agent/executor
- C) The pipeline requires a Docker agent
- D) No agent is needed — the pipeline runs in the cloud

**Answer: B**

---

**Q66.** Why should passwords and API tokens never be hardcoded in a Jenkinsfile?

- A) Jenkins cannot parse strings in Jenkinsfiles
- B) Jenkinsfiles are stored in Git — hardcoded secrets would be visible to anyone with repository access
- C) Jenkins only accepts credentials from the credential store
- D) Hardcoded strings slow down pipeline execution

**Answer: B**

---

**Q67.** What does the `post { always { cleanWs() } }` block in a Jenkinsfile do?

- A) Always runs the next build immediately after the current one
- B) Always cleans the Jenkins workspace after the build, regardless of success or failure — prevents leftover files from affecting future builds
- C) Sends an alert to the team after every build
- D) Resets all environment variables

**Answer: B**

---

**Q68.** What is the purpose of a GitHub webhook in a CI/CD setup?

- A) To authenticate Jenkins with GitHub
- B) To automatically notify Jenkins when a push event occurs so the pipeline triggers immediately
- C) To deploy code to GitHub Pages
- D) To enforce code review requirements

**Answer: B**

---

**Q69.** What does `npm ci` do differently from `npm install`?

- A) `npm ci` skips optional dependencies
- B) `npm ci` installs exact versions from `package-lock.json` and removes `node_modules` first — deterministic, faster in CI
- C) `npm ci` only installs production dependencies
- D) They are identical

**Answer: B**

---

**Q70.** What does the `input` step in a declarative Jenkins pipeline do?

- A) Reads input from environment variables
- B) Pauses the pipeline and waits for a human to approve before continuing — used as a gate before production deployment
- C) Prompts the user to enter code
- D) Reads the Jenkinsfile configuration

**Answer: B**

---

**Q71.** What is the correct order of stages in a typical CI/CD pipeline?

- A) Deploy → Test → Build → Checkout
- B) Checkout → Install → Test → Build → Push → Deploy
- C) Build → Test → Checkout → Deploy → Push
- D) Push → Build → Test → Deploy → Checkout

**Answer: B**

---

**Q72.** What does `kubectl set image deployment/ems-api ems-api=ibm-ems/api:42` trigger?

- A) Creates a new deployment
- B) A rolling update — Kubernetes gradually replaces existing pods with pods running the new image
- C) Deletes and recreates all pods immediately
- D) Tags the existing image as version 42

**Answer: B**

---

**Q73.** What happens in the `post { failure { ... } }` block of a Jenkinsfile?

- A) It runs after every build regardless of result
- B) It runs only when the pipeline fails — used for rollback, notifications, and cleanup
- C) It skips remaining stages when triggered
- D) It retries the failed stage automatically

**Answer: B**

---

**Q74.** What is the benefit of running Test and Lint stages in `parallel` in a Jenkins pipeline?

- A) It reduces the complexity of the Jenkinsfile
- B) Both stages run simultaneously, reducing total pipeline execution time
- C) It allows stages to share environment variables
- D) It ensures stages always succeed

**Answer: B**

---

**Q75.** What does storing Kubernetes manifests (YAML files) in the same Git repository as application code enable?

- A) Kubernetes can read directly from GitHub
- B) The GitOps pattern — infrastructure changes are versioned, reviewed in PRs, and automatically applied when merged, ensuring infrastructure and app code are always in sync
- C) Faster container startup times
- D) Automatic Kubernetes cluster provisioning

**Answer: B**

---

## Answer Key

| Q | A | Q | A | Q | A | Q | A | Q | A |
|---|---|---|---|---|---|---|---|---|---|
| 1 | B | 16 | B | 31 | B | 46 | B | 61 | B |
| 2 | B | 17 | B | 32 | B | 47 | B | 62 | A |
| 3 | B | 18 | B | 33 | B | 48 | B | 63 | B |
| 4 | B | 19 | B | 34 | B | 49 | B | 64 | B |
| 5 | B | 20 | B | 35 | B | 50 | B | 65 | B |
| 6 | B | 21 | B | 36 | B | 51 | B | 66 | B |
| 7 | B | 22 | B | 37 | B | 52 | B | 67 | B |
| 8 | B | 23 | B | 38 | B | 53 | B | 68 | B |
| 9 | B | 24 | B | 39 | B | 54 | B | 69 | B |
| 10 | B | 25 | B | 40 | B | 55 | B | 70 | B |
| 11 | B | 26 | B | 41 | B | 56 | B | 71 | B |
| 12 | B | 27 | B | 42 | B | 57 | B | 72 | B |
| 13 | B | 28 | B | 43 | B | 58 | B | 73 | B |
| 14 | B | 29 | B | 44 | B | 59 | B | 74 | B |
| 15 | B | 30 | B | 45 | B | 60 | B | 75 | B |

---

## Topic Coverage

| Module | Topic | Questions |
|--------|-------|-----------|
| 01 | Git and GitHub | Q1–Q18 |
| 02 | Docker | Q19–Q36 |
| 03 | Kubernetes | Q37–Q52 |
| 04 | Ansible | Q53–Q62 |
| 05 | CI/CD with Jenkins | Q63–Q75 |
