# Module 05 — CI/CD with Jenkins, Ansible, Docker & Kubernetes

## Learning Objectives
- Understand what CI/CD is and why it matters
- Install and configure Jenkins
- Build a CI pipeline with Git and Maven/npm
- Integrate Tomcat, Docker, Ansible, and Kubernetes into the pipeline
- Build a complete end-to-end deployment pipeline for the EMS Node.js app

---

## 5.1 Introduction — What Is CI/CD?

### The Problem Without CI/CD

```
Developer writes code
        ↓
Pushes to Git on Friday afternoon
        ↓
"Works on my machine"
        ↓
QA tests on Monday — 50 bugs found
        ↓
Integration nightmare — nobody knows what broke what
        ↓
Emergency hotfix at 2am
```

### With CI/CD

```
Developer writes code
        ↓
Pushes to Git
        ↓
Pipeline triggers AUTOMATICALLY:
  ✅ Code checked out
  ✅ Dependencies installed
  ✅ Tests run
  ✅ Code quality checked
  ✅ Docker image built
  ✅ Image pushed to registry
  ✅ Deployed to staging
  ✅ Smoke tests pass
  ✅ Deployed to production
        ↓
Done in 10 minutes. Every time.
```

### CI vs CD

| Term | Full Name | What it means |
|------|-----------|---------------|
| **CI** | Continuous Integration | Automatically build and test on every commit |
| **CD** | Continuous Delivery | Automatically prepare a release — deploy to staging |
| **CD** | Continuous Deployment | Automatically deploy to production |

```
CI:   Code → Build → Test → Report
CD:   CI + → Deploy to Staging → Approval → Deploy to Production
```

### The EMS Pipeline We'll Build

```
Developer pushes code to GitHub
              ↓
         Jenkins picks up
              ↓
    ┌─────────────────────┐
    │  Stage 1: Checkout  │  git pull from GitHub
    │  Stage 2: Install   │  npm install
    │  Stage 3: Test      │  npm test
    │  Stage 4: Build     │  docker build
    │  Stage 5: Push      │  docker push to registry
    │  Stage 6: Deploy    │  kubectl apply OR ansible-playbook
    └─────────────────────┘
              ↓
      App running in production
```

---

## 5.2 Jenkins — Installation and Setup

Jenkins is the most widely used open-source CI/CD automation server.

### Install Jenkins (Ubuntu)

```bash
# Install Java first (Jenkins requires Java 17+)
sudo apt update
sudo apt install -y openjdk-17-jdk

# Add Jenkins repository
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | \
  sudo tee /usr/share/keyrings/jenkins-keyring.asc > /dev/null

echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/" | \
  sudo tee /etc/apt/sources.list.d/jenkins.list

# Install Jenkins
sudo apt update
sudo apt install -y jenkins

# Start and enable
sudo systemctl start  jenkins
sudo systemctl enable jenkins

# Check status
sudo systemctl status jenkins

# Get initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

Open **http://localhost:8080** — enter the initial password.

Select **"Install suggested plugins"** → create the admin user.

### Run Jenkins with Docker (Easier for Learning)

```bash
# Run Jenkins in Docker
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins-data:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts-jdk17

# Get initial password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Essential Plugins to Install

Go to **Manage Jenkins → Plugins → Available plugins**:

- **Git plugin** — GitHub integration
- **Pipeline** — declarative pipeline support
- **Docker Pipeline** — build and push Docker images in pipeline
- **Kubernetes CLI** — run `kubectl` in pipeline
- **Ansible** — run Ansible playbooks in pipeline
- **Blue Ocean** — better pipeline visualisation (optional)
- **Credentials Binding** — use secrets safely in pipelines

---

## 5.3 Jenkins Fundamentals

### Key Concepts

| Concept | What it is |
|---------|-----------|
| **Job / Project** | A task Jenkins runs |
| **Build** | One execution of a job |
| **Pipeline** | A sequence of stages defined in code (Jenkinsfile) |
| **Stage** | A logical phase of the pipeline (Build, Test, Deploy) |
| **Step** | Individual action within a stage |
| **Agent** | Where the pipeline runs (Jenkins server itself or a node) |
| **Workspace** | Folder where Jenkins checks out code |
| **Credentials** | Stored secrets (passwords, SSH keys, tokens) |

### Storing Credentials Safely

Never hardcode passwords in Jenkinsfiles. Store them as Jenkins credentials.

**Manage Jenkins → Credentials → System → Global → Add Credentials:**

| Kind | Use for |
|------|---------|
| Username with password | Docker Hub, NPM registry |
| Secret text | API tokens, JWT secrets |
| SSH Username with private key | SSH into servers |
| Certificate | SSL/TLS certs |

---

## 5.4 Pipeline 1 — CI with Git and npm

The Jenkinsfile is the pipeline definition, stored in the project repository (Infrastructure as Code).

```groovy
// Jenkinsfile — at the root of ibm-ems-api repository

pipeline {
    agent any     // run on any available Jenkins agent

    environment {
        NODE_VERSION = '20'
        APP_NAME     = 'ibm-ems-api'
    }

    stages {

        stage('Checkout') {
            steps {
                // Jenkins automatically checks out the repo
                // because of the SCM configuration — or do it manually:
                git branch: 'main',
                    url: 'https://github.com/ibm-team/ibm-ems-api.git'
                echo "Checked out branch: ${env.GIT_BRANCH}"
                echo "Commit: ${env.GIT_COMMIT}"
            }
        }

        stage('Install Dependencies') {
            steps {
                sh 'node --version'
                sh 'npm --version'
                sh 'npm ci'    // clean install — faster than npm install in CI
            }
        }

        stage('Lint') {
            steps {
                sh 'npm run lint'
            }
        }

        stage('Test') {
            steps {
                sh 'npm test'
            }
            post {
                always {
                    // Publish test results if using JUnit-compatible reporter
                    junit 'test-results/*.xml'
                }
            }
        }

        stage('Security Audit') {
            steps {
                sh 'npm audit --audit-level=high'
            }
        }

    }

    post {
        success {
            echo "✅ Pipeline passed! Build #${env.BUILD_NUMBER}"
            // Notify team (Slack, email, etc.)
        }
        failure {
            echo "❌ Pipeline failed at stage: check the logs"
        }
        always {
            // Clean workspace after build
            cleanWs()
        }
    }
}
```

### Creating a Pipeline Job in Jenkins

1. **New Item** → Enter name: `ems-api-ci` → Select **Pipeline** → OK
2. Under **Pipeline** section → **Definition**: Pipeline script from SCM
3. **SCM**: Git → Repository URL: your GitHub URL
4. **Branch**: `*/main`
5. **Script Path**: `Jenkinsfile`
6. Save → **Build Now**

### Trigger Automatically on GitHub Push

1. In Jenkins job → **Configure** → **Build Triggers**
2. Check **GitHub hook trigger for GITScm polling**
3. In GitHub → Repository → Settings → Webhooks → Add webhook
4. Payload URL: `http://<jenkins-server>:8080/github-webhook/`
5. Content type: `application/json`
6. Which events: **Just the push event**

Now every `git push` triggers the Jenkins pipeline automatically.

---

## 5.5 Pipeline 2 — Integrating a Deploy Server (Tomcat Reference)

> **Note:** The IBM DevOps syllabus references Tomcat as an integration example — a Java web server. In our Node.js EMS context, we use a Node.js app server instead. The concept is identical: deploy the built artifact to a running server.

The pattern for deploying to any server over SSH from Jenkins:

```groovy
stage('Deploy to Server') {
    steps {
        sshagent(['server-ssh-key']) {   // credentials ID
            sh """
                ssh -o StrictHostKeyChecking=no ubuntu@${DEPLOY_HOST} '
                    cd /opt/ems-api &&
                    git pull origin main &&
                    npm ci --only=production &&
                    pm2 restart ems-api
                '
            """
        }
    }
}
```

For Java/Maven projects the pattern is similar — build the WAR, copy to Tomcat's `webapps/` directory.

---

## 5.6 Pipeline 3 — Integrating Docker

```groovy
// Jenkinsfile — with Docker integration

pipeline {
    agent any

    environment {
        DOCKER_REGISTRY    = 'docker.io'
        DOCKER_IMAGE       = 'yourusername/ibm-ems-api'
        DOCKER_CREDENTIALS = 'dockerhub-credentials'   // credentials ID
        IMAGE_TAG          = "${env.BUILD_NUMBER}"       // e.g. "42"
        IMAGE_TAG_LATEST   = 'latest'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/ibm-team/ibm-ems-api.git'
            }
        }

        stage('Install & Test') {
            steps {
                sh 'npm ci'
                sh 'npm test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build the image
                    dockerImage = docker.build("${DOCKER_IMAGE}:${IMAGE_TAG}")
                }
                // Also tag as latest
                sh "docker tag ${DOCKER_IMAGE}:${IMAGE_TAG} ${DOCKER_IMAGE}:${IMAGE_TAG_LATEST}"
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS) {
                        dockerImage.push("${IMAGE_TAG}")
                        dockerImage.push("${IMAGE_TAG_LATEST}")
                    }
                }
                echo "Pushed ${DOCKER_IMAGE}:${IMAGE_TAG} to registry"
            }
        }

        stage('Clean Up Local Image') {
            steps {
                sh "docker rmi ${DOCKER_IMAGE}:${IMAGE_TAG}"
                sh "docker rmi ${DOCKER_IMAGE}:${IMAGE_TAG_LATEST}"
            }
        }

    }

    post {
        success {
            echo "Docker image ${DOCKER_IMAGE}:${IMAGE_TAG} ready for deployment"
        }
        failure {
            sh "docker rmi ${DOCKER_IMAGE}:${IMAGE_TAG} || true"
        }
    }
}
```

### Jenkins Server Needs Docker Access

```bash
# Add jenkins user to docker group
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

---

## 5.7 Pipeline 4 — Integrating Ansible

```groovy
// Jenkinsfile — with Ansible deployment

pipeline {
    agent any

    environment {
        DOCKER_IMAGE       = 'yourusername/ibm-ems-api'
        DOCKER_CREDENTIALS = 'dockerhub-credentials'
        ANSIBLE_VAULT_PASS = credentials('ansible-vault-password')  // Jenkins secret
        IMAGE_TAG          = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/ibm-team/ibm-ems-api.git'
            }
        }

        stage('Test') {
            steps {
                sh 'npm ci'
                sh 'npm test'
            }
        }

        stage('Build & Push Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS) {
                        def img = docker.build("${DOCKER_IMAGE}:${IMAGE_TAG}")
                        img.push("${IMAGE_TAG}")
                        img.push("latest")
                    }
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                // Write vault password to a temp file
                writeFile file: '.vault_pass', text: "${ANSIBLE_VAULT_PASS}"

                ansiblePlaybook(
                    playbook:       'ansible/deploy.yaml',
                    inventory:      'ansible/inventory/production.ini',
                    vaultCredentialsId: 'ansible-vault-password',
                    extras:         "--extra-vars \"image_tag=${IMAGE_TAG}\""
                )
            }
            post {
                always {
                    sh 'rm -f .vault_pass'   // always clean up the password file
                }
            }
        }

    }
}
```

### `ansible/deploy.yaml` — Called by Jenkins

```yaml
---
- name: Deploy EMS API using Docker
  hosts: web
  become: true
  vars:
    docker_image: "yourusername/ibm-ems-api"

  tasks:
    - name: Pull latest Docker image
      community.docker.docker_image:
        name: "{{ docker_image }}"
        tag: "{{ image_tag }}"
        source: pull
        force_source: true

    - name: Stop existing container (if running)
      community.docker.docker_container:
        name: ems-api
        state: stopped
      ignore_errors: true

    - name: Start new container
      community.docker.docker_container:
        name: ems-api
        image: "{{ docker_image }}:{{ image_tag }}"
        state: started
        restart_policy: unless-stopped
        ports:
          - "3000:3000"
        env:
          NODE_ENV: production
          MONGO_URI: "{{ mongo_uri }}"
          JWT_SECRET: "{{ jwt_secret }}"

    - name: Verify deployment
      uri:
        url: "http://localhost:3000/api/health"
        status_code: 200
      register: health_check
      retries: 5
      delay: 5
      until: health_check.status == 200

    - name: Show health check result
      debug:
        msg: "App is healthy: {{ health_check.json }}"
```

---

## 5.8 Pipeline 5 — Integrating Kubernetes

```groovy
// Jenkinsfile — full CI/CD with Kubernetes deployment

pipeline {
    agent any

    environment {
        DOCKER_IMAGE        = 'yourusername/ibm-ems-api'
        DOCKER_CREDENTIALS  = 'dockerhub-credentials'
        KUBECONFIG_CREDS    = 'kubeconfig'   // kubeconfig file stored as Jenkins secret file
        K8S_NAMESPACE       = 'ems'
        IMAGE_TAG           = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/ibm-team/ibm-ems-api.git'
                script {
                    env.GIT_SHORT_SHA = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                }
            }
        }

        stage('Install Dependencies') {
            steps {
                sh 'npm ci'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'npm test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'test-results/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build \
                        --build-arg BUILD_DATE=\$(date -u +"%Y-%m-%dT%H:%M:%SZ") \
                        --build-arg GIT_COMMIT=${env.GIT_SHORT_SHA} \
                        -t ${DOCKER_IMAGE}:${IMAGE_TAG} \
                        -t ${DOCKER_IMAGE}:${env.GIT_SHORT_SHA} \
                        -t ${DOCKER_IMAGE}:latest \
                        .
                """
            }
        }

        stage('Push to Registry') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: DOCKER_CREDENTIALS,
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                        docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                        docker push ${DOCKER_IMAGE}:${env.GIT_SHORT_SHA}
                        docker push ${DOCKER_IMAGE}:latest
                        docker logout
                    """
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                withKubeConfig([credentialsId: KUBECONFIG_CREDS]) {
                    sh """
                        # Update the image in the deployment
                        kubectl set image deployment/ems-api \
                            ems-api=${DOCKER_IMAGE}:${IMAGE_TAG} \
                            -n ${K8S_NAMESPACE}

                        # Wait for rollout to complete
                        kubectl rollout status deployment/ems-api \
                            -n ${K8S_NAMESPACE} \
                            --timeout=120s
                    """
                }
            }
        }

        stage('Smoke Test (Staging)') {
            steps {
                withKubeConfig([credentialsId: KUBECONFIG_CREDS]) {
                    sh """
                        # Get the service URL
                        SERVICE_URL=\$(kubectl get svc ems-api-service \
                            -n ${K8S_NAMESPACE} \
                            -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

                        # Run health check
                        curl -f http://\$SERVICE_URL/api/health || exit 1
                        echo "Smoke test passed"
                    """
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'   // only deploy to prod from main branch
            }
            input {
                message "Deploy to PRODUCTION?"
                ok      "Yes, deploy now"
                submitter "jenkins-admins"    // only these users can approve
            }
            steps {
                withKubeConfig([credentialsId: 'kubeconfig-production']) {
                    sh """
                        kubectl set image deployment/ems-api \
                            ems-api=${DOCKER_IMAGE}:${IMAGE_TAG} \
                            -n ${K8S_NAMESPACE}

                        kubectl rollout status deployment/ems-api \
                            -n ${K8S_NAMESPACE} \
                            --timeout=180s
                    """
                }
            }
        }

    }

    post {
        success {
            echo """
                ✅ Deployment successful!
                Image: ${DOCKER_IMAGE}:${IMAGE_TAG}
                Commit: ${env.GIT_SHORT_SHA}
                Build: #${env.BUILD_NUMBER}
            """
        }
        failure {
            echo "❌ Pipeline failed — rolling back"
            withKubeConfig([credentialsId: KUBECONFIG_CREDS]) {
                sh "kubectl rollout undo deployment/ems-api -n ${K8S_NAMESPACE}"
            }
        }
        always {
            sh "docker rmi ${DOCKER_IMAGE}:${IMAGE_TAG} || true"
            cleanWs()
        }
    }
}
```

---

## 5.9 The Complete EMS CI/CD Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         EMS CI/CD Pipeline                          │
│                                                                     │
│  Developer                                                          │
│      │ git push                                                     │
│      ▼                                                              │
│  GitHub (ibm-ems-api)                                               │
│      │ webhook                                                      │
│      ▼                                                              │
│  Jenkins                                                            │
│      │                                                              │
│      ├─── Stage 1: Checkout ──── git pull                           │
│      ├─── Stage 2: Install  ──── npm ci                             │
│      ├─── Stage 3: Test     ──── npm test                           │
│      │         ▼ FAIL → Notify team, stop pipeline                  │
│      ├─── Stage 4: Build    ──── docker build -t ems-api:42 .       │
│      ├─── Stage 5: Push     ──── docker push to Docker Hub          │
│      │                                                              │
│      ├─── Stage 6a: Ansible deploy (VM-based staging)               │
│      │         docker pull ems-api:42                               │
│      │         docker stop ems-api-old                              │
│      │         docker run ems-api:42                                │
│      │         curl /api/health ✅                                   │
│      │                                                              │
│      └─── Stage 6b: Kubernetes deploy (k8s production)             │
│               kubectl set image deployment/ems-api ems-api:42      │
│               kubectl rollout status → wait for ready              │
│               curl /api/health ✅                                    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 5.10 Kubernetes Deployment Manifest for CI/CD

Keep Kubernetes manifests in the same Git repository (GitOps pattern):

```yaml
# k8s/api/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ems-api
  namespace: ems
  annotations:
    deployment.kubernetes.io/revision: "1"
spec:
  replicas: 2
  selector:
    matchLabels:
      app: ems-api
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge:       1
      maxUnavailable: 0
  template:
    metadata:
      labels:
        app: ems-api
    spec:
      containers:
        - name: ems-api
          image: yourusername/ibm-ems-api:latest   # Jenkins updates this
          ports:
            - containerPort: 3000
          env:
            - name: NODE_ENV
              value: production
            - name: MONGO_URI
              valueFrom:
                secretKeyRef:
                  name: ems-secrets
                  key: mongo-uri
          readinessProbe:
            httpGet:
              path: /api/health
              port: 3000
            initialDelaySeconds: 10
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /api/health
              port: 3000
            initialDelaySeconds: 30
            periodSeconds: 20
```

---

## 5.11 Jenkins Pipeline Best Practices

### Use `Jenkinsfile` — Pipeline as Code

```
✅ Store Jenkinsfile in the repository root
✅ Version control your pipeline alongside your application
✅ Review pipeline changes in pull requests
✅ Different branches can have different Jenkinsfiles
```

### Credentials — Never Hardcode

```groovy
// ❌ Never do this
environment {
    DB_PASSWORD = "super-secret-123"
}

// ✅ Always use Jenkins credentials store
environment {
    DB_PASSWORD = credentials('db-password-credential-id')
}

// ✅ Or withCredentials block
withCredentials([string(credentialsId: 'jwt-secret', variable: 'JWT_SECRET')]) {
    sh "JWT_SECRET=$JWT_SECRET npm start"
}
```

### Fail Fast

```groovy
// Put quick stages first — don't build Docker if tests fail
stages:
    1. Checkout  (seconds)
    2. Install   (30s)
    3. Lint      (10s)   ← fail here if code is bad
    4. Test      (60s)   ← fail here if tests break
    5. Build     (2min)  ← only if tests pass
    6. Push      (1min)
    7. Deploy    (2min)
```

### Parallel Stages

```groovy
stage('Test & Lint in Parallel') {
    parallel {
        stage('Unit Tests') {
            steps { sh 'npm test' }
        }
        stage('Lint') {
            steps { sh 'npm run lint' }
        }
        stage('Security Audit') {
            steps { sh 'npm audit --audit-level=high' }
        }
    }
}
```

### Environment-Specific Deployments

```groovy
stage('Deploy') {
    steps {
        script {
            def targetEnv = env.BRANCH_NAME == 'main' ? 'production' : 'staging'
            def namespace  = env.BRANCH_NAME == 'main' ? 'ems-prod'   : 'ems-staging'

            sh "kubectl set image deployment/ems-api ems-api=${DOCKER_IMAGE}:${IMAGE_TAG} -n ${namespace}"
        }
    }
}
```

---

## 5.12 EMS Project — Jenkinsfile Summary

```
ibm-ems-api/
├── src/
├── Dockerfile
├── .dockerignore
├── Jenkinsfile              ← CI/CD pipeline definition
├── docker-compose.yml
├── k8s/
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── api/
│   │   ├── deployment.yaml
│   │   └── service.yaml
│   └── mongo/
│       ├── deployment.yaml
│       ├── service.yaml
│       └── pvc.yaml
└── ansible/
    ├── deploy.yaml
    ├── inventory/
    └── roles/
```

---

## Summary

| Stage | Tool | What happens |
|-------|------|-------------|
| Source control | Git + GitHub | Code versioned, webhooks trigger pipeline |
| CI server | Jenkins | Orchestrates all stages |
| Build tool | npm / Maven | Install deps, compile, package |
| Testing | npm test | Unit, integration, lint tests |
| Containerisation | Docker | Build image, tag with build number |
| Registry | Docker Hub | Store and distribute images |
| Server deploy | Ansible | Pull image, restart container on VMs |
| K8s deploy | kubectl | Rolling update across cluster |
| Monitoring | Health probes | Verify app is healthy post-deploy |

**Next → MCQ Assessment**
