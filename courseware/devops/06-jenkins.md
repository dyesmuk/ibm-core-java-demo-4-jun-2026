# 06 — Jenkins CI/CD Pipelines
> **DevOps Hands-On Series** | Module 6 of 6 | Full CI/CD pipeline

---

## The Last Piece: Automating the Whole Thing

Every previous module built one piece of the delivery system. Jenkins connects them all.

The moment you push code, the entire chain fires automatically:

| What you did manually | What Jenkins now automates |
|---|---|
| Push code → remember to test | Push → tests run automatically |
| `docker build` → `docker push` | Tests pass → image built and pushed |
| `ansible-playbook deploy.yaml` | Image pushed → deployment triggered |
| Hope nobody forgot a step | Every step runs in order, every time |

```
Manual:   Developer pushes → remembers to test → builds → deploys
          (Steps get skipped. Environments drift. Fridays are scary.)

Jenkins:  Developer pushes.
          Everything else happens automatically.
          Pipeline is the process. Process is in Git.
```

**Prerequisites:** Modules 01–05 done. Docker Hub account with an image pushed. GitHub repo ready.

```
01 Git & GitHub  → source of truth; webhook triggers the pipeline
02 Docker        → docker build and docker push happen in the pipeline
03 YAML          → Kubernetes YAMLs applied by the pipeline
04 Kubernetes    → the deployment target; rolling updates happen here
05 Ansible       → provisions the servers the cluster runs on
06 Jenkins       ← YOU ARE HERE — automates everything above, end to end
```

---

## Part 1 — Installing Jenkins on Windows 11

### Run Jenkins in Docker

```powershell
docker run -d `
  --name jenkins `
  -p 8080:8080 `
  -p 50000:50000 `
  -v jenkins_home:/var/jenkins_home `
  -v /var/run/docker.sock:/var/run/docker.sock `
  jenkins/jenkins:lts
```

| Flag | Purpose |
|---|---|
| `-p 8080:8080` | Jenkins web UI — access at `http://localhost:8080` |
| `-p 50000:50000` | Agent communication port |
| `-v jenkins_home:/var/jenkins_home` | Persists all Jenkins data (jobs, credentials, plugins) in a named volume |
| `-v /var/run/docker.sock:/var/run/docker.sock` | Shares the Docker socket so Jenkins can run `docker` commands |

Get the initial admin password:

```powershell
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

> If nothing shows, the container is still starting. Wait 30 seconds and try again, or check `docker logs jenkins`.

Browser → `http://localhost:8080` → paste password → **Install suggested plugins** → create admin user.

### Install the Docker CLI Inside Jenkins

The socket mount lets Jenkins *communicate* with Docker — but only if the `docker` CLI is also present inside the container. Without it, every `docker` command in the pipeline fails with `docker: command not found`:

```powershell
docker exec -it --user root jenkins bash -c "apt-get update && apt-get install -y docker.io"

# Verify
docker exec jenkins docker --version
```

### Install Missing Node.js Library

The Jenkins image is minimal Debian. When the NodeJS plugin downloads Node 18, it needs `libatomic1` — not included by default. Without it every pipeline run fails with:

```
node: error while loading shared libraries: libatomic.so.1: cannot open shared object file
```

Install it now:

```powershell
docker exec -it --user root jenkins bash -c "apt-get update && apt-get install -y libatomic1"
```

You'll see some harmless `debconf` warnings — ignore them, the install succeeds.

> Both `docker.io` and `libatomic1` are one-time fixes per container. If you recreate the Jenkins container, run both `apt-get install` commands again.

### Configure Node.js in Jenkins

Do this now so the `tools { nodejs 'nodejs-18' }` block works in pipelines:

**Manage Jenkins → Tools → NodeJS → Add NodeJS:**
- Name: `nodejs-18`
- Install automatically
- NodeJS 18.x
- Save

### Install Required Plugins

**Manage Jenkins → Plugins → Available plugins** (search, tick, install):

| Plugin | Used for |
|---|---|
| Pipeline | Declarative pipelines |
| Git | Cloning repos |
| Docker Pipeline | `docker.build`, `docker.push` in pipelines |
| NodeJS | `node` and `npm` builds |
| Kubernetes | `kubectl` in pipelines |
| Blue Ocean | Visual pipeline view |

> Some plugins may already be installed from the suggested plugins set. Check the "Installed" tab before reinstalling.

### Architecture

```
Developer (Windows 11)
    ↓  git push
  GitHub
    ↓  webhook
  Jenkins Controller (Docker container :8080)
    ↓
  Pipeline stages (sh runs inside the Jenkins Linux container)
```

> `sh` in pipeline steps runs inside the Jenkins Linux container — not on Windows. `node`, `npm`, `docker`, `kubectl`, bash all work normally.

---

## Part 2 — Declarative Pipeline Fundamentals

### Create a Pipeline Job (UI Mode First)

For this step only, paste the pipeline directly into the Jenkins UI — no project folder needed yet. From Part 3 onwards, the pipeline moves into a `Jenkinsfile` in your repo.

1. `http://localhost:8080` → **New Item** → name `hello-pipeline` → **Pipeline** → OK
2. Scroll to **Pipeline** section
3. **Definition** → **Pipeline script** (not "from SCM")
4. Paste this:

```groovy
pipeline {
    agent any

    tools {
        nodejs 'nodejs-18'
    }

    environment {
        APP_NAME = 'hello-jenkins'
    }

    stages {
        stage('Hello') {
            steps {
                echo "Building ${APP_NAME}"
                sh 'node --version && npm --version'
            }
        }
    }

    post {
        success { echo 'SUCCESS' }
        failure { echo 'FAILED'  }
        always  { echo 'Always runs — cleanup goes here' }
    }
}
```

5. **Save** → **Build Now**

Click the build number → **Console Output** to watch each line execute.

### Pipeline Syntax Explained

| Block | Purpose |
|---|---|
| `pipeline` | Root wrapper — every Jenkinsfile starts with this |
| `agent any` | Run on any available Jenkins executor |
| `tools` | Pre-configured Node.js from Global Tool Config — puts `node` and `npm` on PATH |
| `environment` | Key-value env vars available to all stages |
| `stages` | Ordered list of stages — one fails, the rest are skipped |
| `stage` | Named phase — shown as a box in Blue Ocean. Use descriptive names. |
| `steps` | The actual commands inside a stage |
| `post` | Runs after all stages. `always` is unconditional; `success`/`failure` are conditional. |

### Built-In Environment Variables

Jenkins provides these automatically to every pipeline — no declaration needed:

| Variable | Value |
|---|---|
| `BUILD_NUMBER` | Auto-incrementing run counter (e.g. `42`) |
| `GIT_BRANCH` | Branch that triggered the build (e.g. `main`) |
| `GIT_COMMIT` | Full commit SHA |
| `WORKSPACE` | Directory where the repo is checked out |
| `JOB_NAME` | Name of the Jenkins job |

`BUILD_NUMBER` is especially useful — from Part 4 onwards it's used as the Docker image tag (e.g. `yourname/hello-jenkins:42`). Every image is traceable to the exact pipeline run that built it.

---

## Part 3 — CI Pipeline: Git + Jenkins + Node.js

The core pipeline: a Node.js Express app built with npm, triggered by GitHub push.

### Create the Project

Open PowerShell and create the project folder:

```powershell
mkdir hello-jenkins
cd hello-jenkins
git init
git remote add origin https://github.com/yourname/hello-jenkins.git
```

> Create the GitHub repo first at github.com/new (name: `hello-jenkins`, public, no README).

Create these files:

**`package.json`**

```json
{
  "name": "hello-jenkins",
  "version": "1.0.0",
  "main": "app.js",
  "scripts": {
    "start": "node app.js",
    "test": "jest --ci --reporters=default --reporters=jest-junit"
  },
  "dependencies": {
    "express": "4.18.2"
  },
  "devDependencies": {
    "jest": "29.7.0",
    "jest-junit": "16.0.0",
    "supertest": "6.3.4"
  }
}
```

**`app.js`**

```js
const express = require('express');
const app = express();

app.get('/', (req, res) => {
  res.send('Hello from Jenkins CI/CD!');
});

module.exports = app;

if (require.main === module) {
  app.listen(3000, () => console.log('Server running on port 3000'));
}
```

> The `module.exports = app` + `require.main` guard separates the Express app from `listen()`, letting tests import the app without starting a real server.

**`app.test.js`**

```js
const request = require('supertest');
const app = require('./app');

test('GET / returns greeting', async () => {
  const res = await request(app).get('/');
  expect(res.statusCode).toBe(200);
  expect(res.text).toContain('Hello from Jenkins CI/CD!');
});
```

**`.gitignore`**

```
node_modules/
.env
*.log
test-results/
```

Install dependencies and push:

```powershell
npm install               # generates package-lock.json — commit this!
git add .
git commit -m "Initial Node.js Express app"
git push -u origin main
```

### `Jenkinsfile` — Git + Jenkins + Node.js

Create `Jenkinsfile` in the project folder (no extension):

```groovy
pipeline {
    agent any

    tools {
        nodejs 'nodejs-18'
    }

    environment {
        JEST_JUNIT_OUTPUT_DIR  = 'test-results'
        JEST_JUNIT_OUTPUT_NAME = 'junit.xml'
    }

    stages {

        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Install') {
            steps { sh 'npm ci' }
        }

        stage('Test') {
            steps { sh 'npm test' }
            post {
                always { junit 'test-results/junit.xml' }
            }
        }

    }

    post {
        success { echo 'Tests passed. Ready to build.' }
        failure { echo 'Tests failed. Fix before building.' }
    }
}
```

> `npm ci` (not `npm install`) — clean install from `package-lock.json` exactly. Reproducible. Always use `npm ci` in CI pipelines.

```powershell
git add Jenkinsfile
git commit -m "Add Jenkinsfile"
git push
```

### Create the Jenkins Job (SCM Mode)

1. `http://localhost:8080` → **New Item** → name `hello-jenkins` → **Pipeline** → OK
2. Scroll to **Pipeline** section
3. **Definition** → **Pipeline script from SCM**
4. SCM: **Git**
5. Repository URL: `https://github.com/yourname/hello-jenkins`
6. Branch: `*/main`
7. Script Path: `Jenkinsfile`
8. **Save** → **Build Now**

Jenkins clones your repo and runs the Jenkinsfile. Watch the Console Output.

### Set Up Auto-Trigger with GitHub Webhooks

Right now Jenkins only builds when you click **Build Now**. Webhooks make it build automatically on every `git push`.

**The challenge:** Jenkins is running on your laptop. GitHub can't reach it directly. Solution: **ngrok** creates a public tunnel to your local Jenkins.

#### Option A — Webhook with ngrok (instant trigger, real-world pattern)

**Step 1 — Enable trigger in Jenkins:**

In your `hello-jenkins` job → **Configure** → **Build Triggers** → tick **"GitHub hook trigger for GITScm polling"** → Save.

**Step 2 — Start ngrok:**

Open a new PowerShell window and keep it open:

```powershell
# Option 1 — Run ngrok as a Docker container (no install needed)
docker run --rm -it `
  --add-host=host.docker.internal:host-gateway `
  -e NGROK_AUTHTOKEN=YOUR_TOKEN_HERE `
  ngrok/ngrok http host.docker.internal:8080
```

Get your free token at `https://dashboard.ngrok.com/get-started/your-authtoken`.

ngrok shows a URL like:
```
Forwarding  https://a1b2c3d4.ngrok-free.app -> http://localhost:8080
```

**Step 3 — Add webhook in GitHub:**

Repo → **Settings → Webhooks → Add webhook:**
- Payload URL: `https://a1b2c3d4.ngrok-free.app/github-webhook/`
- Content type: `application/json`
- **Add webhook**

A green tick = it works.

**Step 4 — Test it:**

```powershell
git commit --allow-empty -m "trigger test"
git push
```

Jenkins starts a build automatically within seconds.

> The ngrok URL changes every time you restart ngrok. Update the webhook URL in GitHub Settings each time.

#### Option B — SCM Polling (simpler, no ngrok needed)

Add a `triggers` block to your `Jenkinsfile`:

```groovy
pipeline {
    agent any
    triggers {
        pollSCM('H/5 * * * *')    // check GitHub every 5 minutes
    }
    // ... rest of pipeline
}
```

| | Webhook | SCM Polling |
|---|---|---|
| Trigger speed | Instant | Up to 5 minutes |
| Requires ngrok | Yes | No |
| Production pattern | ✅ Always | Rarely |

### npm Commands Reference

| Command | What it does |
|---|---|
| `npm install` | Install dependencies (may update `package-lock.json`) |
| `npm ci` | Clean install from `package-lock.json` exactly — use in CI |
| `npm test` | Run the `test` script from `package.json` |
| `npm start` | Run `node app.js` |
| `npm audit` | Check for known vulnerabilities |
| `npm audit --audit-level=high` | Fail only on high/critical vulnerabilities |

---

## Part 4 — First Deployment: Run the App as a Docker Container

Before pushing to Docker Hub or Kubernetes, the simplest deploy is: build the image, run it locally, verify it responds.

### Add a Dockerfile

```dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./

RUN npm ci --production

COPY app.js .

EXPOSE 3000

CMD ["node", "app.js"]
```

> `npm ci --production` installs only `dependencies`, not `devDependencies` (jest, supertest). Tests run in the pipeline *before* the Docker build — the image only needs runtime code.

```powershell
git add Dockerfile
git commit -m "Add Dockerfile"
git push
```

### `Jenkinsfile` — Build, Run, Verify

```groovy
pipeline {
    agent any

    tools {
        nodejs 'nodejs-18'
    }

    environment {
        APP_NAME = 'hello-jenkins'
        JEST_JUNIT_OUTPUT_DIR  = 'test-results'
        JEST_JUNIT_OUTPUT_NAME = 'junit.xml'
    }

    stages {

        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Install') {
            steps { sh 'npm ci' }
        }

        stage('Test') {
            steps { sh 'npm test' }
            post {
                always { junit 'test-results/junit.xml' }
            }
        }

        stage('Build Image') {
            steps {
                // BUILD_NUMBER used as image tag — every image traceable to the run that built it
                sh "docker build -t ${APP_NAME}:${BUILD_NUMBER} ."
            }
        }

        stage('Run Container') {
            steps {
                // Remove any container from a previous run (|| true = don't fail if nothing exists)
                sh "docker rm -f ${APP_NAME} || true"
                // Run the new container
                sh "docker run -d --name ${APP_NAME} -p 3001:3000 ${APP_NAME}:${BUILD_NUMBER}"
                // Wait for Node.js to start, then verify it responds
                sh "sleep 3 && curl -f http://localhost:3001/ || (docker logs ${APP_NAME} && exit 1)"
            }
        }

    }

    post {
        success { echo "App running at http://localhost:3001/" }
        failure { echo "Build ${BUILD_NUMBER} failed." }
    }
}
```

`http://localhost:3001/` → **Hello from Jenkins CI/CD!**

**Quick knowledge check:**
- Why `docker rm -f hello-jenkins || true` before `docker run`? → `docker run` fails if a container with that name already exists. `|| true` means: try to remove it, if nothing's there that's fine — don't fail the build.
- Why `sleep 3` before `curl`? → The container starts instantly but Node.js takes a moment to bind to the port.

---

## Part 5 — Integrating Docker in the CI/CD Pipeline

Push the image to Docker Hub so any server — or Kubernetes — can pull it.

### Add Docker Hub Credentials to Jenkins First

**Manage Jenkins → Credentials → (global) → Add Credentials:**
- Kind: Username with password
- Username: your Docker Hub username
- Password: your Docker Hub password or access token
- ID: `dockerhub-creds`
- Save

> When you write `DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')`, Jenkins automatically creates two derived variables: `DOCKERHUB_CREDENTIALS_USR` (username) and `DOCKERHUB_CREDENTIALS_PSW` (password). These are used in `docker login`. Jenkins masks them in all console output — they're never exposed in logs.

### `Jenkinsfile` — Node.js + Docker Hub

```groovy
pipeline {
    agent any

    tools {
        nodejs 'nodejs-18'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
        IMAGE_NAME = "yourname/hello-jenkins"    // Replace with your Docker Hub username
        IMAGE_TAG  = "${BUILD_NUMBER}"
        JEST_JUNIT_OUTPUT_DIR  = 'test-results'
        JEST_JUNIT_OUTPUT_NAME = 'junit.xml'
    }

    stages {

        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Install & Test') {
            steps { sh 'npm ci && npm test' }
            post {
                always { junit 'test-results/junit.xml' }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                sh "docker tag  ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh """
                    echo ${DOCKERHUB_CREDENTIALS_PSW} | \
                    docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin
                    docker push ${IMAGE_NAME}:${IMAGE_TAG}
                    docker push ${IMAGE_NAME}:latest
                """
            }
        }

        stage('Smoke Test') {
            steps {
                sh "docker rm -f smoke-test || true"
                sh "docker run -d --name smoke-test -p 3002:3000 ${IMAGE_NAME}:${IMAGE_TAG}"
                sh "sleep 5 && curl -f http://localhost:3002/ || (docker logs smoke-test && exit 1)"
                sh "docker rm -f smoke-test"
            }
        }

        stage('Cleanup') {
            steps {
                // Remove local image to save disk space — it's safely stored on Docker Hub
                sh "docker rmi ${IMAGE_NAME}:${IMAGE_TAG} || true"
            }
        }

    }

    post {
        success { echo "Image ${IMAGE_NAME}:${IMAGE_TAG} pushed to Docker Hub." }
        failure { echo "Pipeline failed at build ${BUILD_NUMBER}." }
    }
}
```

### The Docker CI/CD Flow

```
git push → Jenkins
    ↓
npm ci && npm test      (install + test)
    ↓
docker build :42        (package app into image)
    ↓
docker push :42         → Docker Hub
    ↓
Smoke test              (pull + run + curl verify)
    ↓
docker rmi              (clean local image from Jenkins)
```

---

## Part 6 — Integrating Kubernetes in the CI/CD Pipeline

Deploy to Kubernetes with zero-downtime rolling updates and auto-rollback on failure.

### Give Jenkins Access to Kubernetes

Jenkins needs your kubeconfig to run `kubectl`. This requires recreating the container with an extra volume mount.

> Your Jenkins data is safe — it lives in the `jenkins_home` named volume. Removing the *container* doesn't touch the volume.

```powershell
# Stop and remove the existing container
docker stop jenkins && docker rm jenkins

# Recreate with kubeconfig mounted (read-only)
docker run -d `
  --name jenkins `
  -p 8080:8080 -p 50000:50000 `
  -v jenkins_home:/var/jenkins_home `
  -v /var/run/docker.sock:/var/run/docker.sock `
  -v ${env:USERPROFILE}\.kube:/root/.kube:ro `
  jenkins/jenkins:lts
```

Re-install the dependencies (new container):

```powershell
docker exec -it --user root jenkins bash -c "apt-get update && apt-get install -y docker.io libatomic1"
```

Install `kubectl` inside Jenkins:

```powershell
docker exec -it --user root jenkins bash -c `
  "curl -LO https://dl.k8s.io/release/v1.29.0/bin/linux/amd64/kubectl && `
   chmod +x kubectl && mv kubectl /usr/local/bin/"

# Verify it can reach your cluster
docker exec jenkins kubectl get nodes
```

### Create the Initial K8s Deployment (Once)

```powershell
kubectl create deployment hello-jenkins `
  --image=yourname/hello-jenkins:latest `
  --replicas=3

kubectl expose deployment hello-jenkins `
  --type=NodePort `
  --port=80 --target-port=3000 `
  --name=hello-jenkins-svc
```

> This is a one-time setup. The pipeline's `kubectl set image` command updates this existing Deployment on every run.

### `Jenkinsfile` — Full Pipeline: Git → Test → Docker → Kubernetes

```groovy
pipeline {
    agent any

    tools {
        nodejs 'nodejs-18'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')
        IMAGE_NAME      = "yourname/hello-jenkins"    // Replace with your Docker Hub username
        IMAGE_TAG       = "${BUILD_NUMBER}"
        DEPLOYMENT_NAME = "hello-jenkins"
        CONTAINER_NAME  = "hello-jenkins"
        K8S_NAMESPACE   = "default"
        JEST_JUNIT_OUTPUT_DIR  = 'test-results'
        JEST_JUNIT_OUTPUT_NAME = 'junit.xml'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo "Building ${GIT_BRANCH} @ ${GIT_COMMIT[0..7]}"
            }
        }

        stage('Install & Test') {
            steps { sh 'npm ci && npm test' }
            post {
                always { junit 'test-results/junit.xml' }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                sh "docker tag  ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh """
                    echo ${DOCKERHUB_CREDENTIALS_PSW} | \
                    docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin
                    docker push ${IMAGE_NAME}:${IMAGE_TAG}
                    docker push ${IMAGE_NAME}:latest
                """
            }
        }

        stage('Deploy to Kubernetes') {
            // Only deploy when the branch is 'main'
            // Feature branches build + test but never deploy to production
            when { branch 'main' }
            steps {
                sh """
                    kubectl set image deployment/${DEPLOYMENT_NAME} \
                        ${CONTAINER_NAME}=${IMAGE_NAME}:${IMAGE_TAG} \
                        -n ${K8S_NAMESPACE}

                    kubectl rollout status deployment/${DEPLOYMENT_NAME} \
                        -n ${K8S_NAMESPACE} \
                        --timeout=120s
                """
            }
        }

        stage('Verify') {
            when { branch 'main' }
            steps {
                sh "kubectl get pods -n ${K8S_NAMESPACE} -l app=${DEPLOYMENT_NAME}"
                sh "kubectl get svc hello-jenkins-svc -n ${K8S_NAMESPACE}"
            }
        }

        stage('Cleanup') {
            steps { sh "docker rmi ${IMAGE_NAME}:${IMAGE_TAG} || true" }
        }

    }

    post {
        success {
            echo "Build ${BUILD_NUMBER}: image :${IMAGE_TAG} deployed to Kubernetes."
        }
        failure {
            // Automatic rollback on any pipeline failure
            sh """
                echo 'Pipeline failed — rolling back Kubernetes deployment'
                kubectl rollout undo deployment/${DEPLOYMENT_NAME} \
                    -n ${K8S_NAMESPACE} || true
            """
        }
    }
}
```

### The Complete End-to-End Flow

```
Developer: git push origin main
                ↓
        GitHub Webhook → Jenkins
                ↓
┌──────────────────────────────────────────────────────┐
│  Checkout      → git clone                           │
│  Install&Test  → npm ci + jest + junit results       │
│  Docker Build  → docker build :42                    │
│  Docker Push   → docker push :42 → Docker Hub        │
│  K8s Deploy    → kubectl set image :42               │
│                  kubectl rollout status (waits)       │
│  Verify        → kubectl get pods + svc              │
│  Cleanup       → docker rmi local image              │
└──────────────────────────────────────────────────────┘
                ↓
  Kubernetes: rolling update, zero downtime
                ↓
  Failed? → post { failure } → kubectl rollout undo
```

> **`kubectl set image` vs `kubectl rollout status`:** `set image` issues the update instruction. `rollout status` waits and watches until all Pods are healthy. Without `rollout status`, the pipeline would finish immediately — before knowing if the deploy actually worked. The `--timeout=120s` triggers auto-rollback if Pods aren't healthy within 2 minutes.

**Quick knowledge check:**
- Why does the K8s deploy stage have `when { branch 'main' }`? → Feature branches build and test but never deploy to production. Only code reviewed and merged to `main` reaches the cluster.
- What happens to live traffic during `kubectl set image`? → Kubernetes replaces Pods one at a time (rolling update). Some run old, some new, all behind the Service. Zero downtime.

---

## Part 7 — Advanced Pipeline Patterns

### Parallel Stages

Run lint, tests, and security audit simultaneously instead of sequentially:

```groovy
stage('Validate') {
    parallel {
        stage('Unit Tests')   { steps { sh 'npm test'                    } }
        stage('Lint')         { steps { sh 'npm run lint'                } }
        stage('Audit')        { steps { sh 'npm audit --audit-level=high'} }
    }
}
```

If each takes 1 minute: sequentially = 3 minutes, parallel = 1 minute. Independent stages with no shared data — if any one fails, the overall stage fails.

> Add a `lint` script to `package.json`:
> ```json
> "lint": "eslint ."
> ```

### Conditional Logic

```groovy
when { branch 'main' }                                        // only on main
when { not { branch 'main' } }                                // all except main
when { changeset '**/Dockerfile' }                            // only when Dockerfile changed
when { expression { return params.DEPLOY == 'true' } }        // only if parameter set
```

### Parameterised Builds

Allow the person triggering the build to pass in values:

```groovy
parameters {
    string(name: 'IMAGE_TAG',   defaultValue: 'latest', description: 'Tag to deploy')
    booleanParam(name: 'SKIP_TESTS', defaultValue: false)
    choice(name: 'ENVIRONMENT', choices: ['dev','staging','prod'])
}
```

After adding `parameters {}`, click **Build with Parameters** in the Jenkins UI instead of **Build Now**.

### Blue Ocean — Visual Pipeline View

**Manage Jenkins → Plugins → Available → Blue Ocean → Install**

`http://localhost:8080/blue` — horizontal flow diagram, pass/fail per stage, PR integration.

---

## Jenkins Commands & URLs Reference

```powershell
# Start Jenkins (after Step 6 setup with kubeconfig)
docker run -d --name jenkins `
  -p 8080:8080 -p 50000:50000 `
  -v jenkins_home:/var/jenkins_home `
  -v /var/run/docker.sock:/var/run/docker.sock `
  -v ${env:USERPROFILE}\.kube:/root/.kube:ro `
  jenkins/jenkins:lts

# Get initial admin password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Daily ops
docker restart jenkins
docker logs -f jenkins
docker stop jenkins

# Install Docker CLI + Node.js library (after any container recreation)
docker exec -it --user root jenkins bash -c "apt-get update && apt-get install -y docker.io libatomic1"

# Install kubectl (after Step 6 container recreation)
docker exec -it --user root jenkins bash -c `
  "curl -LO https://dl.k8s.io/release/v1.29.0/bin/linux/amd64/kubectl && `
   chmod +x kubectl && mv kubectl /usr/local/bin/"

# Verify cluster access
docker exec jenkins kubectl get nodes

# Start ngrok (keep this terminal open during lab)
docker run --rm -it `
  --add-host=host.docker.internal:host-gateway `
  -e NGROK_AUTHTOKEN=YOUR_TOKEN_HERE `
  ngrok/ngrok http host.docker.internal:8080
```

### Jenkins URLs

| URL | Purpose |
|---|---|
| `http://localhost:8080` | Dashboard |
| `http://localhost:8080/blue` | Blue Ocean visual UI |
| `http://localhost:8080/manage` | Manage Jenkins |
| `http://localhost:8080/credentials` | Credential store |
| `http://localhost:8080/manage/configureTools/` | Node.js tool config |
| `http://localhost:8080/restart` | Restart Jenkins |
| `http://localhost:8080/github-webhook/` | GitHub webhook endpoint (used in Payload URL) |

---

## Key Concepts Summary

**Pipeline** — Full workflow from commit to deployment. Lives in a `Jenkinsfile` in the repo.

**Jenkinsfile** — Groovy DSL defining the pipeline. Declarative syntax is recommended.

**Stage** — Named phase shown in the UI. A failed stage stops the pipeline.

**Step** — Single command inside a stage. `sh` = shell command. `junit` = publish test results.

**Credential Store** — Secure vault for passwords and tokens. Referenced by ID. Never shown in logs.

**Build Number** — Auto-incrementing per run. Used as Docker image tag for full traceability.

**Webhook** — GitHub calls Jenkins on every push. Instant trigger.

**ngrok** — Gives your local Jenkins a public URL so GitHub webhooks can reach it. Local lab only — production Jenkins runs on a public server.

**Rolling update** — Kubernetes replaces Pods one at a time. Zero downtime.

**Auto-rollback** — `post { failure { kubectl rollout undo } }` — reverts K8s deployment if pipeline fails.

**`npm ci`** — Reproducible install from `package-lock.json`. Always use in CI, never `npm install`.

**`jest-junit`** — Jest reporter that outputs JUnit XML. Required for Jenkins to track test results over time.

**`|| true`** — Shell pattern making a command non-fatal. `docker rm -f app || true` = remove if exists, ignore if not.

### Key Distinctions

| These seem similar... | But they're different because... |
|---|---|
| CI vs CD | CI = test + build on push. CD = automatically deliver to an environment. CI is the prerequisite for CD. |
| Declarative vs Scripted pipeline | Declarative = structured, validated. Scripted = full Groovy. Always use Declarative. |
| `stage` vs `step` | Stage = named UI phase. Step = single command inside a stage. |
| `npm install` vs `npm ci` | `npm install` can update lockfile. `npm ci` installs exactly what's locked. Use `npm ci` in pipelines. |
| Webhook vs SCM polling | Webhook = GitHub calls Jenkins (instant, needs ngrok in lab). Polling = Jenkins checks GitHub periodically (up to 5-minute delay, no ngrok needed). |
| `kubectl set image` vs `kubectl apply` | `set image` updates one field. `apply` replaces full spec from a YAML file. |

---

## Series Complete

You've built the full DevOps pipeline from first principles:

```
Module 01 — Git & GitHub   Code is version-controlled and team-shareable
Module 02 — Docker         App is containerised and image is on Docker Hub
Module 03 — YAML           Config language for everything below
Module 04 — Kubernetes     Container is orchestrated with self-healing and scaling
Module 05 — Ansible        Servers are provisioned and configured automatically
Module 06 — Jenkins        The full pipeline runs on every git push, end to end
```

A single `git push` now triggers:

```
GitHub webhook
      ↓
Jenkins pipeline
      ↓
npm ci && npm test   (install + test)
      ↓
docker build :42     (package)
      ↓
docker push :42      → Docker Hub
      ↓
kubectl set image    → Kubernetes rolling update, zero downtime
      ↓
App live — traceable to the exact commit that triggered it
      ↓
Something failed? → kubectl rollout undo (auto-rollback)
```

The infrastructure (servers Kubernetes runs on) is provisioned by Ansible, version-controlled in Git, and can be re-run at any time to reproduce the environment exactly.
