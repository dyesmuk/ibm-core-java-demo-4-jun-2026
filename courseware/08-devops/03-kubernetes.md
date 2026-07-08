# Module 03 — Kubernetes

## Learning Objectives
- Understand why Kubernetes exists and what it does
- Set up a local Kubernetes cluster
- Understand all core Kubernetes concepts
- Write YAML manifests for Pods, ReplicaSets, Deployments, and Services
- Understand Kubernetes networking
- EMS: deploy the API and MongoDB to Kubernetes

---

## 3.1 Introduction — Why Kubernetes?

Docker Compose runs containers on **one machine**. When that machine goes down, your app goes down. When traffic spikes, you can't automatically add more containers.

**Kubernetes (K8s)** is a container orchestration platform that:
- Runs containers across a **cluster** of machines
- **Automatically restarts** failed containers
- **Scales** containers up or down based on load
- Performs **rolling updates** with zero downtime
- **Load balances** traffic across container instances
- Manages **secrets and configuration** centrally

### The Control Plane + Worker Nodes Model

```
┌─────────────────────────────────────────────────────────┐
│                   Kubernetes Cluster                     │
│                                                         │
│  ┌─────────────────────┐   ┌──────────┐  ┌──────────┐  │
│  │   Control Plane     │   │  Worker  │  │  Worker  │  │
│  │                     │   │  Node 1  │  │  Node 2  │  │
│  │  API Server         │   │          │  │          │  │
│  │  Scheduler          │   │  Pod     │  │  Pod     │  │
│  │  Controller Manager │   │  Pod     │  │  Pod     │  │
│  │  etcd (database)    │   │  kubelet │  │  kubelet │  │
│  └─────────────────────┘   └──────────┘  └──────────┘  │
└─────────────────────────────────────────────────────────┘
        ↑
  kubectl talks to the API Server
```

| Component | Role |
|-----------|------|
| **API Server** | Front-end for K8s control plane — kubectl talks to this |
| **etcd** | Distributed key-value store — the cluster's source of truth |
| **Scheduler** | Assigns pods to nodes based on resource availability |
| **Controller Manager** | Watches state and reconciles to desired state |
| **kubelet** | Agent on each node — ensures containers are running |
| **kube-proxy** | Manages networking rules on each node |

---

## 3.2 Setting Up Kubernetes

### Option 1: Minikube (Local — Recommended for Learning)

```bash
# Install Minikube
# Mac
brew install minikube

# Linux
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube

# Start a single-node cluster
minikube start

# Set driver (if needed)
minikube start --driver=docker

# Check status
minikube status

# Open the Kubernetes dashboard
minikube dashboard

# Stop the cluster
minikube stop

# Delete the cluster
minikube delete
```

### Option 2: kind (Kubernetes in Docker)

```bash
# Install kind
go install sigs.k8s.io/kind@v0.22.0
# or
brew install kind

# Create a cluster
kind create cluster --name ems-cluster

# Delete
kind delete cluster --name ems-cluster
```

### kubectl — The Kubernetes CLI

```bash
# Install kubectl
# Mac
brew install kubectl

# Linux
sudo apt-get install kubectl

# Verify
kubectl version --client

# See clusters
kubectl config get-contexts

# Switch cluster
kubectl config use-context minikube

# Quick health check
kubectl cluster-info
kubectl get nodes
```

---

## 3.3 YAML Introduction

Kubernetes is configured entirely through YAML files. YAML (YAML Ain't Markup Language) is a human-readable data format.

### YAML Syntax Rules

```yaml
# Comments start with #

# Key-value pairs (string values)
name: ibm-ems-api
version: "1.0.0"        # quote strings with special chars
environment: production

# Numbers and booleans (no quotes)
replicas: 3
enabled: true
port: 3000

# Lists (two ways)
fruits:
  - apple
  - banana
  - cherry

# Inline list
colors: [red, green, blue]

# Nested objects
metadata:
  name: ems-api
  namespace: default
  labels:
    app: ems-api
    tier: backend

# Multi-line strings
command: |
  echo "line 1"
  echo "line 2"

# Same line string
description: >
  This is a long description
  that spans multiple lines
  but renders as one line.
```

### Common YAML Mistakes

```yaml
# ❌ Inconsistent indentation (YAML uses spaces, not tabs)
metadata:
  name: ems-api
    labels:      # wrong — too many spaces

# ✅ Consistent 2-space indent
metadata:
  name: ems-api
  labels:
    app: ems-api

# ❌ Missing quotes on values with colons
message: Error: connection refused   # YAML parser confuses this

# ✅ Quote it
message: "Error: connection refused"
```

### Basic Kubernetes YAML Structure

Every Kubernetes resource YAML follows this structure:

```yaml
apiVersion: apps/v1         # which API group this belongs to
kind: Deployment            # what kind of resource
metadata:
  name: ems-api             # resource name
  namespace: default        # which namespace (default if omitted)
  labels:
    app: ems-api            # key-value labels for selecting
spec:                       # desired state — what you want
  # ... resource-specific config
```

---

## 3.4 Core Kubernetes Concepts

### Pod — The Smallest Unit

A Pod is one or more containers that run together on the same node and share the same network and storage.

```yaml
# pod.yaml — rarely created directly; usually via Deployment
apiVersion: v1
kind: Pod
metadata:
  name: ems-api-pod
  labels:
    app: ems-api
spec:
  containers:
    - name: ems-api
      image: yourusername/ibm-ems-api:latest
      ports:
        - containerPort: 3000
      env:
        - name: NODE_ENV
          value: "production"
        - name: PORT
          value: "3000"
      resources:
        requests:
          memory: "128Mi"
          cpu: "100m"
        limits:
          memory: "256Mi"
          cpu: "500m"
```

```bash
# Apply (create or update) a resource
kubectl apply -f pod.yaml

# See pods
kubectl get pods
kubectl get pods -o wide    # more details including node

# Describe a pod (detailed view + events)
kubectl describe pod ems-api-pod

# Logs
kubectl logs ems-api-pod
kubectl logs -f ems-api-pod          # follow
kubectl logs ems-api-pod -c ems-api  # specific container if multiple

# Shell into a pod
kubectl exec -it ems-api-pod -- sh

# Delete a pod
kubectl delete pod ems-api-pod
kubectl delete -f pod.yaml
```

### ReplicaSet — Keep N Pods Running

A ReplicaSet ensures a specified number of identical Pods are always running. If a Pod crashes, it creates a new one.

```yaml
# replicaset.yaml
apiVersion: apps/v1
kind: ReplicaSet
metadata:
  name: ems-api-rs
spec:
  replicas: 3         # keep 3 pods running at all times
  selector:
    matchLabels:
      app: ems-api    # manage pods with this label
  template:           # pod template — how to create the pods
    metadata:
      labels:
        app: ems-api
    spec:
      containers:
        - name: ems-api
          image: yourusername/ibm-ems-api:latest
          ports:
            - containerPort: 3000
```

```bash
kubectl apply -f replicaset.yaml
kubectl get replicasets
kubectl get rs          # shorthand

# Scale manually
kubectl scale rs ems-api-rs --replicas=5
```

> **You rarely create ReplicaSets directly.** Use Deployments instead — they manage ReplicaSets for you and add rolling update capabilities.

### Deployment — The Standard Way

A Deployment wraps a ReplicaSet and adds:
- Rolling updates (replace pods gradually, zero downtime)
- Rollback (go back to previous version instantly)
- Pause and resume

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ems-api
  labels:
    app: ems-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ems-api
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge:       1   # max extra pods during update
      maxUnavailable: 0   # min pods that must stay running
  template:
    metadata:
      labels:
        app: ems-api
    spec:
      containers:
        - name: ems-api
          image: yourusername/ibm-ems-api:1.0.0
          ports:
            - containerPort: 3000
          env:
            - name: NODE_ENV
              value: "production"
            - name: MONGO_URI
              valueFrom:
                secretKeyRef:       # read from Secret (covered below)
                  name: ems-secrets
                  key: mongo-uri
          livenessProbe:            # restart container if this fails
            httpGet:
              path: /api/health
              port: 3000
            initialDelaySeconds: 15
            periodSeconds: 20
          readinessProbe:           # don't send traffic if this fails
            httpGet:
              path: /api/health
              port: 3000
            initialDelaySeconds: 5
            periodSeconds: 10
          resources:
            requests:
              memory: "128Mi"
              cpu: "100m"
            limits:
              memory: "256Mi"
              cpu: "500m"
```

```bash
kubectl apply -f deployment.yaml

# See deployments
kubectl get deployments
kubectl get deploy       # shorthand

# See rollout status
kubectl rollout status deployment/ems-api

# See rollout history
kubectl rollout history deployment/ems-api

# Update image (triggers rolling update)
kubectl set image deployment/ems-api ems-api=yourusername/ibm-ems-api:1.1.0

# Rollback to previous version
kubectl rollout undo deployment/ems-api

# Rollback to specific revision
kubectl rollout undo deployment/ems-api --to-revision=2

# Scale
kubectl scale deployment ems-api --replicas=5
```

---

## 3.5 Services — Exposing Pods

Pods are ephemeral — they come and go. A Service provides a **stable network endpoint** that routes to whatever Pods match its selector.

### Service Types

| Type | Accessible from | Use case |
|------|----------------|---------|
| `ClusterIP` | Inside cluster only | Internal service-to-service |
| `NodePort` | Outside via node IP + port | Development, testing |
| `LoadBalancer` | Outside via cloud load balancer | Production on cloud |
| `ExternalName` | Aliases an external DNS name | Connect to external services |

### ClusterIP — Internal Only

```yaml
# service-clusterip.yaml
apiVersion: v1
kind: Service
metadata:
  name: ems-api-service
spec:
  type: ClusterIP    # default — only reachable inside the cluster
  selector:
    app: ems-api     # routes to pods with this label
  ports:
    - protocol: TCP
      port: 80       # service port (what clients connect to)
      targetPort: 3000   # pod port (where the app listens)
```

### NodePort — External via Node IP

```yaml
apiVersion: v1
kind: Service
metadata:
  name: ems-api-nodeport
spec:
  type: NodePort
  selector:
    app: ems-api
  ports:
    - port: 80
      targetPort: 3000
      nodePort: 30080    # 30000–32767 range; accessible at <node-ip>:30080
```

```bash
# Get the URL (Minikube)
minikube service ems-api-nodeport --url
# http://192.168.49.2:30080
```

### LoadBalancer — Cloud Production

```yaml
apiVersion: v1
kind: Service
metadata:
  name: ems-api-lb
spec:
  type: LoadBalancer   # cloud provider provisions a load balancer
  selector:
    app: ems-api
  ports:
    - port: 80
      targetPort: 3000
```

---

## 3.6 ConfigMaps and Secrets

### ConfigMap — Non-Sensitive Configuration

```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: ems-config
data:
  NODE_ENV: "production"
  PORT: "3000"
  LOG_LEVEL: "info"
```

```yaml
# Use in deployment
envFrom:
  - configMapRef:
      name: ems-config
```

### Secret — Sensitive Data

```bash
# Create secret from literal values
kubectl create secret generic ems-secrets \
  --from-literal=mongo-uri="mongodb://admin:secret@mongo-service:27017/ems" \
  --from-literal=jwt-secret="super-secret-key-here"
```

```yaml
# Or from YAML (values must be base64 encoded)
apiVersion: v1
kind: Secret
metadata:
  name: ems-secrets
type: Opaque
data:
  mongo-uri:  bW9uZ29kYjovL2FkbWluOnNlY3JldEBtb25nbzoyNzAxNy9lbXM=  # base64
  jwt-secret: c3VwZXItc2VjcmV0LWtleQ==
```

```bash
# Encode/decode base64
echo -n "mongodb://admin:secret@mongo:27017/ems" | base64
echo -n "c3VwZXItc2VjcmV0LWtleQ==" | base64 --decode
```

```yaml
# Use secret in deployment
env:
  - name: MONGO_URI
    valueFrom:
      secretKeyRef:
        name: ems-secrets
        key: mongo-uri
  - name: JWT_SECRET
    valueFrom:
      secretKeyRef:
        name: ems-secrets
        key: jwt-secret
```

---

## 3.7 Networking in Kubernetes

```
Internet
    ↓
Ingress (optional — routes HTTP by hostname/path)
    ↓
Service (stable IP + DNS, load balances to pods)
    ↓
Pods (ephemeral, any IP)
```

### Kubernetes DNS

Services get a DNS name automatically:
```
<service-name>.<namespace>.svc.cluster.local
```

So the EMS API can reach MongoDB using:
```
mongodb://mongo-service.default.svc.cluster.local:27017/ems
# or simply:
mongodb://mongo-service:27017/ems
```

### Ingress — Single Entry Point for HTTP

```yaml
# ingress.yaml
# Requires an Ingress Controller (nginx-ingress, traefik, etc.)
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ems-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
    - host: ems.ibm.local
      http:
        paths:
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: ems-api-service
                port:
                  number: 80
          - path: /
            pathType: Prefix
            backend:
              service:
                name: ems-frontend-service
                port:
                  number: 80
```

---

## 3.8 EMS on Kubernetes — Complete Setup

### Folder structure

```
k8s/
├── namespace.yaml
├── configmap.yaml
├── secret.yaml
├── mongo/
│   ├── deployment.yaml
│   ├── service.yaml
│   └── pvc.yaml             ← persistent storage
└── api/
    ├── deployment.yaml
    └── service.yaml
```

### `k8s/namespace.yaml`

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: ems
```

### `k8s/mongo/pvc.yaml` — Persistent Volume Claim

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mongo-pvc
  namespace: ems
spec:
  accessModes:
    - ReadWriteOnce   # one node can mount at a time
  resources:
    requests:
      storage: 2Gi
```

### `k8s/mongo/deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mongo
  namespace: ems
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mongo
  template:
    metadata:
      labels:
        app: mongo
    spec:
      containers:
        - name: mongo
          image: mongo:7
          ports:
            - containerPort: 27017
          volumeMounts:
            - name: mongo-storage
              mountPath: /data/db
          env:
            - name: MONGO_INITDB_ROOT_USERNAME
              value: admin
            - name: MONGO_INITDB_ROOT_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: ems-secrets
                  key: mongo-password
      volumes:
        - name: mongo-storage
          persistentVolumeClaim:
            claimName: mongo-pvc
```

### `k8s/mongo/service.yaml`

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mongo-service
  namespace: ems
spec:
  selector:
    app: mongo
  ports:
    - port: 27017
      targetPort: 27017
```

### `k8s/api/deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ems-api
  namespace: ems
spec:
  replicas: 2
  selector:
    matchLabels:
      app: ems-api
  template:
    metadata:
      labels:
        app: ems-api
    spec:
      containers:
        - name: ems-api
          image: yourusername/ibm-ems-api:latest
          ports:
            - containerPort: 3000
          envFrom:
            - configMapRef:
                name: ems-config
          env:
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
```

### Deploy Everything

```bash
# Apply all manifests
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/mongo/
kubectl apply -f k8s/api/

# Or apply the entire folder recursively
kubectl apply -R -f k8s/

# Check everything is running
kubectl get all -n ems

# Port-forward to test locally
kubectl port-forward service/ems-api-service 3000:80 -n ems
curl http://localhost:3000/api/health
```

---

## 3.9 Appendix — Multi-Node Cluster with kubeadm

For production-like local setup across multiple VMs (e.g. VirtualBox or cloud VMs):

```bash
# On ALL nodes — install container runtime and kubeadm
sudo apt-get update
sudo apt-get install -y apt-transport-https ca-certificates curl

# Add Kubernetes repo
curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.29/deb/Release.key | \
  sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg

echo "deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] \
  https://pkgs.k8s.io/core:/stable:/v1.29/deb/ /" | \
  sudo tee /etc/apt/sources.list.d/kubernetes.list

sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl

# On the MASTER node only
sudo kubeadm init --pod-network-cidr=10.244.0.0/16

# Copy kubeconfig (as shown in kubeadm output)
mkdir -p $HOME/.kube
sudo cp /etc/kubernetes/admin.conf $HOME/.kube/config

# Install network plugin (Flannel)
kubectl apply -f https://github.com/flannel-io/flannel/releases/latest/download/kube-flannel.yml

# On WORKER nodes — use the join command from kubeadm init output
sudo kubeadm join <master-ip>:6443 --token <token> --discovery-token-ca-cert-hash sha256:<hash>
```

---

## Summary

| Resource | Purpose | Create with |
|----------|---------|-------------|
| Pod | One or more containers, shared network | Rarely directly — use Deployment |
| ReplicaSet | Maintain N pod replicas | Rarely directly — use Deployment |
| Deployment | Manage ReplicaSets + rolling updates | `kubectl apply -f deployment.yaml` |
| Service (ClusterIP) | Internal stable endpoint | `kubectl apply -f service.yaml` |
| Service (NodePort) | External via node port | Same |
| Service (LoadBalancer) | Cloud load balancer | Same |
| ConfigMap | Non-sensitive config | `kubectl create configmap` |
| Secret | Sensitive config | `kubectl create secret` |
| PVC | Request persistent storage | `kubectl apply -f pvc.yaml` |
| Namespace | Logical isolation | `kubectl create namespace` |
| Ingress | HTTP routing by host/path | `kubectl apply -f ingress.yaml` |

**Next → Module 04: Ansible**
