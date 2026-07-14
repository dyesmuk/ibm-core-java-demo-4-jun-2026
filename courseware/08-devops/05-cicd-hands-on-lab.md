# CI/CD with Jenkins — Hands-On Lab
### EMS Project · Individual Practice · Half-Day Session (~3.5 hrs)

The capstone. Everything from every previous lab gets wired together:
a `git push` triggers Jenkins, which builds and tests with Maven,
packages with Docker, and deploys to your own Kubernetes cluster —
fully automated, one pipeline, no manual steps.

> **Starter kit:** `cicd-starter-kit.zip` — a fill-in-the-blank
> `Jenkinsfile` and a real test fixture.

---

## Why this is individual, and why Jenkins runs natively

Originally this was planned as a shared, LAN-based Jenkins instance —
but since everyone works entirely on their own local machine with no
shared network, **everyone runs their own Jenkins**, same as every
other lab in this series. The one deliberate difference: Jenkins itself
runs **natively** (its WAR file, not a Docker container). Running
Jenkins in Docker would mean volume-mounting your kubeconfig and
Minikube certs with matching absolute paths — a real headache that
breaks differently on Mac, Windows, and Linux. Native Jenkins just
inherits the `docker`, `kubectl`, and `mvn` already on your machine
from previous labs. No extra plumbing.

### Scope for this lab

- **Tomcat** is a light-touch stretch goal only, not a core stage. The
  EMS app runs on Spring Boot's embedded server; genuinely "integrating
  Tomcat" means repackaging as a WAR for an external servlet
  container — a real architecture change that isn't worth making just
  to touch one topic. It's discussed and optionally demoed at the end,
  not built into the main pipeline.
- **Ansible** isn't a pipeline stage here either — but it's worth
  naming where it *would* sit in a real pipeline: provisioning the
  target servers, before any of this even starts. That's covered in
  the wrap-up discussion.

---

## Learning Objectives

- [ ] Install and run Jenkins, create a Pipeline job
- [ ] Configure credentials in Jenkins (never hardcoded in a Jenkinsfile)
- [ ] Write a declarative `Jenkinsfile` with multiple stages
- [ ] Trigger a pipeline automatically via SCM polling
- [ ] Chain Checkout → Build & Test → Docker Build → Push → Deploy in one pipeline
- [ ] Publish and read JUnit test results in the Jenkins UI
- [ ] Watch a pipeline correctly stop on a failing test, before deploying broken code
- [ ] Explain, end to end, how a single `git push` becomes a running container update

---

## 1. The Scenario

```
  git push
     │
     ▼
  Jenkins (polls your repo)
     │
     ├─ Checkout          (Git — lab 1)
     ├─ Build & Test       (Maven)
     ├─ Build Docker Image (Docker — lab 2, reuses docker/Dockerfile)
     ├─ Push to Docker Hub (Docker — lab 2, reuses your registry account)
     └─ Deploy             (Kubernetes — lab 3, reuses k8s/app-deployment.yaml)
                                │
                                ▼
                     your Minikube cluster, updated
```

One push, one command's worth of effort, and every previous lab's
artifact gets used for real.

---

## 2. Trainer Setup — Do This Before the Session

1. Confirm your own environment: Java 17, Docker, `kubectl`, `mvn` all
   already working from previous labs.
2. Download the Jenkins WAR file once and confirm it starts:
   ```bash
   curl -fsSL -o jenkins.war https://get.jenkins.io/war-stable/latest/jenkins.war
   java -jar jenkins.war --httpPort=8080
   ```
3. **Share `cicd-starter-kit.zip`** with the batch before the session.
4. Push trainees to do the Jenkins **download, first startup, unlock,
   and suggested-plugin install** as pre-work (Section 3) — first-run
   plugin installation depends on network speed and can easily eat 15+
   minutes if done live in class.
5. Windows trainees: confirm Jenkins runs from inside **WSL2**, same as
   the Ansible lab, so pipeline `sh` steps behave correctly and can
   reach `docker`/`kubectl`.

---

## 3. Trainee Pre-Work Checklist

- [ ] `minikube start` is running, and your `ems-app` Deployment +
      Service from the Kubernetes lab are already applied
      (`kubectl get deployment ems-app` shows something)
- [ ] Your EMS repo has the Docker lab's `docker/` folder and the
      Kubernetes lab's `k8s/` folder both committed
- [ ] `docker login` still works
- [ ] Java 17 available
- [ ] Jenkins WAR downloaded and started at least once:
  ```bash
  curl -fsSL -o jenkins.war https://get.jenkins.io/war-stable/latest/jenkins.war
  java -jar jenkins.war --httpPort=8080
  ```
- [ ] Unlocked Jenkins (initial admin password shown in the terminal
      output, or at the path Jenkins prints), installed **suggested
      plugins**, created your own admin user
- [ ] `cicd-starter-kit.zip` extracted

---

## 4. Session Timeline (≈ 3.5 hrs)

| # | Segment | Time |
|---|---------|------|
| — | Kickoff — how this ties every lab together | 10 min |
| — | Trainer walkthrough of the Jenkinsfile structure | 10 min |
| Ex 1 | Verify & Tour Your Jenkins | 15 min |
| Ex 2 | Connect Jenkins to Your Repo | 20 min |
| Ex 3 | Stage 1 — Checkout & Build | 25 min |
| Ex 4 | Stage 2 — Dockerize | 20 min |
| Ex 5 | Stage 3 — Push to Registry | 15 min |
| — | Break | 10 min |
| Ex 6 | Stage 4 — Deploy to Kubernetes | 30 min |
| Ex 7 | Trigger It For Real | 20 min |
| Ex 8 | Pipeline Resilience — Fail & Fix | 25 min |
| Ex 9 | Cleanup & Wrap-Up | 10 min |
| | **Total** | **~210 min** |

---

## Exercise 1 — Verify & Tour Your Jenkins (15 min)

**Concepts:** Jenkins UI orientation

If Jenkins isn't already running from your pre-work:
```bash
java -jar jenkins.war --httpPort=8080
```
Open `http://localhost:8080`. Tour: **Manage Jenkins**, **New Item**,
the plugin list (confirm **Pipeline**, **Git**, and **Docker Pipeline**
plugins are installed — all included in "suggested plugins").

**Checkpoint:** you're logged in as your own admin user, and can name
where you'd go to add credentials vs. create a new job.

---

## Exercise 2 — Connect Jenkins to Your Repo (20 min)

**Concepts:** credentials, Pipeline jobs, SCM polling

**Add a Docker Hub credential** (Manage Jenkins → Credentials → System
→ Global credentials → Add Credentials):
- Kind: Username with password
- Username/password: your Docker Hub login
- **ID: `dockerhub-creds`** (must match the Jenkinsfile exactly)

**Create a Pipeline job** (New Item → Pipeline):
- Name it `ems-pipeline`
- Under **Pipeline**, set Definition to "Pipeline script from SCM"
- SCM: Git, paste your EMS repo's URL, branch `main` (or your fork's
  default branch)
- Script Path: `Jenkinsfile` (default, matches where you'll commit it)

**Set up polling** (since a public repo has no way to push a webhook
to your laptop behind home/classroom NAT): under **Build Triggers**,
check **Poll SCM**, schedule: `H/5 * * * *` (checks for new commits
every ~5 minutes).

**Checkpoint:** the job exists and is configured, even though it'll
fail on first run — you haven't committed a Jenkinsfile yet. That's
Exercise 3.

---

## Exercise 3 — Stage 1: Checkout & Build (25 min)

**Concepts:** declarative pipeline syntax, Maven in CI, JUnit reporting

Copy `EmployeeModelTest.java` into your repo at exactly:
```
src/test/java/com/ibm/training/ems/employee/EmployeeModelTest.java
```

Fill in the `Checkout` and `Build & Test` TODOs in `Jenkinsfile.starter`
(leave the later stages' TODOs for now), save as `Jenkinsfile` at your
repo root, then commit and push:
```bash
git add Jenkinsfile src/test/java/com/ibm/training/ems/employee/EmployeeModelTest.java
git commit -m "Add Jenkinsfile and first test"
git push origin main
```

In Jenkins, click **Build Now** on `ems-pipeline` (don't wait for
polling this first time).

**Checkpoint:** the build goes green through the "Build & Test" stage,
and clicking into the build shows a **Test Result** with 1 test passed.

---

## Exercise 4 — Stage 2: Dockerize (20 min)

**Concepts:** invoking `docker build` from a pipeline

Fill in the `Build Docker Image` TODO, reusing your `docker/Dockerfile`
from the Docker lab. Commit and push:
```bash
git add Jenkinsfile
git commit -m "Add Docker build stage"
git push origin main
```
**Build Now** again.

**Checkpoint:** `docker images` on your machine shows a new image
tagged `<your-dockerhub-username>/ibm-ems-api:jenkins-<build-number>`,
built entirely by Jenkins, not by you typing `docker build`.

---

## Exercise 5 — Stage 3: Push to Registry (15 min)

**Concepts:** using stored credentials in a pipeline, never hardcoding secrets

This stage is already complete in the starter (it references your
`dockerhub-creds` credential) — no TODOs here. Just confirm the
`DOCKERHUB_USER` environment variable at the top of the file matches
your real Docker Hub username, commit if you changed anything, and
**Build Now**.

**Checkpoint:** check hub.docker.com — a new tag
(`jenkins-<build-number>`) appears on your `ibm-ems-api` repo, pushed
by Jenkins.

---

## Exercise 6 — Stage 4: Deploy to Kubernetes (30 min)

**Concepts:** automated rollout, closing the full loop

Fill in the final TODO in the `Deploy to Kubernetes` stage — after
`kubectl set image`, add a command that waits for the rollout to
actually finish before the pipeline reports success (same command you
used manually in the Kubernetes lab's Exercise 7).

Commit, push, **Build Now**.

```bash
kubectl get pods -w        # watch the rollout happen, triggered entirely by Jenkins
```

**Checkpoint:** `kubectl rollout history deployment/ems-app` shows a
new revision, and `kubectl describe deployment ems-app` shows the new
`jenkins-<build-number>` image tag — none of it typed by hand this
time.

---

## Exercise 7 — Trigger It For Real (20 min)

**Concepts:** the actual point of CI/CD — a push alone does everything

Make a small, real code change — e.g., add the `TODO (Lab)` field or
endpoint you skipped back in the Docker lab's Exercise 2 for one of
your entities. Commit and push:
```bash
git add .
git commit -m "Small feature change to trigger the full pipeline"
git push origin main
```

This time, **don't** click Build Now — wait for Poll SCM to pick it up
(up to ~5 minutes), or reduce the polling schedule temporarily to
`* * * * *` (every minute) to see it faster.

**Checkpoint:** you watched a build start with zero manual intervention
from a plain `git push` — checkout, build, test, image, push, deploy,
all automatic.

---

## Exercise 8 — Pipeline Resilience: Fail & Fix (25 min)

**Concepts:** what CI/CD is actually for — catching problems before
they reach production

Deliberately break the test:
```java
// in EmployeeModelTest.java
assertEquals("WrongName", employee.getFirstName());   // was "Ada"
```
Commit and push. **Build Now**.

**Checkpoint 1:** the pipeline fails at the **Build & Test** stage —
red, stopped. Confirm in the Jenkins UI: did the Docker Build, Push, or
Deploy stages run at all? (They shouldn't have.) Check
`kubectl rollout history deployment/ems-app` — no new revision, because
the broken build never reached the deploy stage.

Now fix it:
```java
assertEquals("Ada", employee.getFirstName());   // back to correct
```
Commit and push again.

**Checkpoint 2:** the pipeline goes green end to end, and a new
revision appears in `kubectl rollout history`. Explain, in your own
words, why this behavior — stopping BEFORE deploy on a failing test —
is the entire reason CI/CD pipelines exist.

---

## Exercise 9 — Cleanup & Wrap-Up (10 min)

```bash
# stop Jenkins with Ctrl+C in its terminal
kubectl delete deployment ems-app
kubectl delete service ems-app
minikube stop
```

### Stretch goals (if time remains)

- **Tomcat, done lightly**: package a trivial standalone WAR (not the
  real EMS app — Spring Boot's embedded server means a real WAR
  repackage is a genuine architecture change) and deploy it into a
  plain `tomcat:10` container with `docker cp` or the Tomcat Manager
  app, just to see the contrast with container-native deployment.
- **Where would Ansible fit?** Discuss as a class: in a real
  organization, an Ansible playbook would provision the target
  server — install Docker, create the deploy user, open firewall
  ports — **before** any of this pipeline ever runs. Jenkins assumes
  a ready server; Ansible is what makes it ready.
- **Parallel stages**: could `Build Docker Image` and publishing test
  reports happen in parallel instead of sequentially? Look at
  declarative pipeline's `parallel` block.
- **Webhooks instead of polling**: if this were a real server with a
  public IP, GitHub could push a webhook to Jenkins instantly instead
  of Jenkins polling every 5 minutes. Discuss why that wasn't an option
  here (no public network) and what changes in a real company setup.

---

## Completion Checklist (self-tick)

- [ ] Installed and unlocked Jenkins, created an admin user
- [ ] Configured a Docker Hub credential (never hardcoded a password)
- [ ] Created a Pipeline job pointed at my own repo with SCM polling
- [ ] Wrote a Jenkinsfile with Checkout, Build & Test, Docker Build, Push, and Deploy stages
- [ ] Saw JUnit test results published in the Jenkins UI
- [ ] Watched a plain `git push` trigger the entire pipeline automatically
- [ ] Watched a failing test correctly stop the pipeline before deploy
- [ ] Fixed the test and watched the pipeline go green end to end
- [ ] Can explain, in order, everything that happens between `git push` and a running container update

---

## Trainer's Evaluation Rubric

| Signal | What good looks like |
|--------|------------------------|
| Credentials | Used Jenkins credentials store, no password ever visible in the Jenkinsfile or console log |
| Jenkinsfile structure | Clear stage names, `post` blocks used appropriately |
| Test integration | `junit` step actually publishes results, not just console text |
| End-to-end trigger | Genuinely triggered via `git push` + polling at least once, not only "Build Now" |
| Fail & Fix | Can explain specifically WHICH stage stopped the pipeline and why that stage ordering matters |
| Conceptual grasp | Can explain where Ansible and Tomcat would fit, even though not built into this pipeline |

---

## Common Pitfalls / FAQ

**"Jenkins job fails immediately with 'Jenkinsfile not found.'"**
Confirm you actually committed and pushed `Jenkinsfile` to your repo
root, on the branch the job is configured to watch.

**"`docker: command not found` inside the pipeline."**
Since Jenkins runs natively, it should inherit your PATH — but if you
started Jenkins from a different shell/session than the one where you
normally use `docker`, it may not see it. Restart Jenkins from a
terminal where `docker --version` already works.

**"`kubectl` in the pipeline can't reach my cluster."**
Same cause as above — start Jenkins from a terminal where
`kubectl get nodes` already succeeds.

**"Poll SCM never seems to trigger."**
Give it the full interval (`H/5 * * * *` can take up to 5 minutes), or
temporarily set it to `* * * * *` for faster feedback during the lab,
then set it back afterward.

**"Push to Docker Hub fails with 'unauthorized.'"**
Double check the credential ID is exactly `dockerhub-creds` in both the
Jenkins UI and the Jenkinsfile — Jenkins matches these as exact
strings, and check the Docker Hub repo name matches your actual
username.

**"Deploy stage succeeds but I don't see any change in my app."**
Check you're actually hitting the updated Pods —
`kubectl get pods -o jsonpath='{.items[*].spec.containers[*].image}'`
should show the new tag. If not, the rollout may still be in progress —
that's exactly why Exercise 6 asks you to wait for rollout completion
before declaring success.

---

## Command Reference (this lab)

| Command | What it does |
|---------|---------------|
| `java -jar jenkins.war --httpPort=8080` | Start Jenkins natively |
| `kubectl get deployment ems-app` | Confirm the prerequisite Deployment exists |
| `kubectl rollout history deployment/ems-app` | See deployment revisions, including ones Jenkins triggered |
| `kubectl rollout status deployment/ems-app` | Wait for a rollout to finish |
| `docker login` / `docker push` | Manual equivalents of what the pipeline automates |

---

**That's the full series** — Git & GitHub, Docker, Kubernetes, Ansible,
and now CI/CD, all built around the same EMS project, each lab reusing
real artifacts from the one before it. If you want, I can also put
together a single "big picture" one-page diagram tying all five labs
together for trainees to see the whole journey at a glance.
