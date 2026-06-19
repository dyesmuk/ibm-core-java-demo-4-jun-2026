# 04 — Kubernetes
> **DevOps Hands-On Series** | Module 4 of 6 | Node.js Express project

---

## From "Run This Container" to "Keep This Running Forever"

Docker is great at running containers. Kubernetes is great at *managing* them — at scale, with self-healing, rolling updates, and service discovery built in.

| Docker | Kubernetes |
|---|---|
| Runs containers on **one machine** | Runs containers across **many machines** |
| You manage containers manually | Kubernetes manages them for you |
| A container dies → stays dead | A container dies → Kubernetes restarts it immediately |
| Scaling = you run `docker run` many times | Scaling = change one number in a YAML file |
| Networking = port mapping | Networking = automatic DNS-based service discovery |

The key mindset shift:

```
Docker:     "Run THIS container on THIS port."     (imperative)

Kubernetes: "I want 3 copies of this app running at all times."   (declarative)
            Kubernetes figures out the rest and keeps it that way.
```

**Declarative infrastructure**: you describe the *desired state*. Kubernetes continuously works to match reality to your description.

**Prerequisites:** Modules 01–03 done. `yourname/hello-express:1.0` pushed to Docker Hub. Comfortable with YAML.

**What you'll build:** The Module 02 image deployed as a 3-replica Deployment behind a Service, alongside MongoDB — all on Kubernetes.

---

## Part 1 — Setting Up Kubernetes on Windows 11

Docker Desktop includes a built-in Kubernetes cluster. No separate install needed.

### Enable Kubernetes

1. Open **Docker Desktop**
2. Click the **Kubernetes** icon in the left sidebar (or go to **Settings → Kubernetes**)
3. Click **Create a Kubernetes cluster**
4. Select **Kubeadm** (standard single-node cluster — correct choice for this training)
5. Leave **Show system containers** unchecked
6. Click **Create**
7. Wait for the green Kubernetes indicator at the bottom-left of Docker Desktop

> Docker Desktop v4.x replaced the old "Enable Kubernetes" checkbox with this cluster creation dialog. Kubeadm is the right pick — it creates a standard single-node cluster.

### Verify the Install

```powershell
kubectl version --client
kubectl cluster-info
kubectl get nodes
```

Expected:
```
NAME             STATUS   ROLES           AGE   VERSION
docker-desktop   Ready    control-plane   1m    v1.34.x
```

`STATUS: Ready` = Kubernetes is up and your node is healthy.

### What is `kubectl`?

`kubectl` is to Kubernetes what `docker` is to Docker — the command-line tool for everything. Docker Desktop automatically adds it to your PATH when Kubernetes is enabled.

```
docker build / run / ps / stop
kubectl apply / get / describe / delete
```

---

## YAML — The Language of Kubernetes

Every single Kubernetes object is a YAML file. Every one starts with these four fields:

```yaml
apiVersion: apps/v1       # which Kubernetes API handles this
kind: Deployment          # what type of object
metadata:                 # name, labels — how you identify it
  name: hello-app
spec:                     # the desired state — what you actually want
  ...
```

| Field | Purpose |
|---|---|
| `apiVersion` | Version of the Kubernetes API for this resource |
| `kind` | Type: Pod, Deployment, Service, ConfigMap, etc. |
| `metadata` | Name, labels, namespace — identification |
| `spec` | The heart of every YAML file — what you want |

YAML rules reminder: 2 spaces per indent level, never tabs.

---

## Part 2 — Your First Pod

A **Pod** is the smallest deployable unit in Kubernetes. It wraps one or more containers. Think of it as a thin envelope around your Docker container.

### Create `pod.yaml`

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: hello-pod
  labels:
    app: hello-app
spec:
  containers:
    - name: hello-container
      image: yourname/hello-express:1.0
      ports:
        - containerPort: 3000
```

Replace `yourname` with your Docker Hub username.

> **Important:** Kubernetes pulls this image from Docker Hub at runtime. Your local Docker images are not visible to Kubernetes. The image must already be pushed — `docker push yourname/hello-express:1.0`.

### Apply and Inspect

```powershell
kubectl apply -f pod.yaml          # create the Pod
kubectl get pods                   # see it running
kubectl describe pod hello-pod     # full details
kubectl logs hello-pod             # container logs
```

Expected:
```
NAME        READY   STATUS    RESTARTS   AGE
hello-pod   1/1     Running   0          30s
```

`1/1` = 1 container running out of 1 total.

**If you see `ErrImagePull` or `ImagePullBackOff`:** The image name doesn't match what's on Docker Hub. Check the exact name and push:
```powershell
docker push yourname/hello-express:1.0
```

### Access the App (Temporary)

Pods aren't directly accessible from outside the cluster. Use `port-forward` for testing:

```powershell
kubectl port-forward pod/hello-pod 3000:3000
```

`http://localhost:3000` → **Hello from Express inside Docker!**

Press `Ctrl+C` to stop port-forwarding. The Pod keeps running.

### Clean Up

```powershell
kubectl delete -f pod.yaml
```

### The Pod Mental Model

```
Docker container:           Kubernetes Pod:
"Just run this container"   "Run this container, name it, label it,
                             integrate it into the cluster network"

One-off, manual             Managed, part of a system
```

A standalone Pod has no self-healing — if it dies, it stays dead. That's what Deployments are for.

**Quick knowledge check:**
- What happens if you delete the Pod? → It's gone and stays gone. No self-healing without a Deployment — that's exactly why Deployments exist.
- Where does Kubernetes pull the image from? → Docker Hub (same registry you pushed to in Module 02).
- What does `labels: app: hello-app` do right now? → Nothing visible yet. Labels are metadata — but they become critical when Services use them to route traffic.

---

## Part 3 — Deployments

A **Deployment** is how you actually run apps in Kubernetes. It does two things:

1. Maintains a **ReplicaSet** — always keeps the right number of Pods running
2. **Self-heals** — if a Pod crashes, it's replaced automatically

### Create `deployment.yaml`

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

### Apply and Inspect

```powershell
kubectl apply -f deployment.yaml

kubectl get deployments        # see the Deployment
kubectl get pods               # see the 3 Pods it created
kubectl get replicasets        # see the ReplicaSet behind it
```

### Watch Self-Healing in Action

```powershell
# Delete one Pod by name (copy from kubectl get pods output)
kubectl delete pod hello-deployment-7d9f5c8b6-4xk2p

# Watch what happens immediately
kubectl get pods
```

Within seconds, a new Pod appears. The Deployment noticed one was missing and replaced it.

### Scaling

```powershell
kubectl scale deployment hello-deployment --replicas=5
kubectl get pods       # watch 5 pods appear

kubectl scale deployment hello-deployment --replicas=2
kubectl get pods       # watch 3 pods terminate
```

Or edit the YAML, change `replicas: 3` to `replicas: 5`, and `kubectl apply -f deployment.yaml`.

**Quick knowledge check:**
- Delete a Pod in a Deployment — what happens? → Kubernetes creates a replacement immediately. The Deployment maintains the desired count.
- How do you run 3 copies in Docker Compose? → You can't easily — Compose is single-host. Kubernetes `replicas: 3` distributes across nodes.
- `kubectl apply` vs `kubectl create`? → `apply` is declarative and idempotent (updates if exists). `create` fails if the object already exists. Always use `apply`.

---

## Part 4 — Services

Three Pods are running. But each has a private IP that changes every time a Pod is replaced. How does anything connect to them reliably?

**Services** provide a stable network endpoint in front of Pods, load-balancing traffic across them.

```
Client Request
      ↓
  Service (stable IP + DNS name)
      ↓  load balances
  Pod 1   Pod 2   Pod 3
```

The Service finds Pods using **labels** — this is exactly why you set `app: hello-app` on every Pod.

### Service Types

| Type | What it does | When to use |
|---|---|---|
| `ClusterIP` | Accessible only inside the cluster | Service-to-service communication |
| `NodePort` | Exposes a port on the node's IP | Development and testing |
| `LoadBalancer` | Provisions a cloud load balancer | Production on AWS/GCP/Azure |

### Create `service.yaml`

```yaml
apiVersion: v1
kind: Service
metadata:
  name: hello-service
spec:
  type: NodePort
  selector:
    app: hello-app
  ports:
    - port: 80          # Service listens on this port
      targetPort: 3000  # Container is running on this port
      nodePort: 30080   # Port exposed on the node (30000–32767)
```

### Apply It

```powershell
kubectl apply -f service.yaml
kubectl get services
```

`http://localhost:30080` → **Hello from Express inside Docker!**

No more port-forwarding needed. This is a persistent, load-balanced entry point to all three Pods.

### Service Discovery Inside the Cluster

Every Service automatically gets a DNS name:

```
hello-service                             (same namespace)
hello-service.default                     (service.namespace)
hello-service.default.svc.cluster.local   (fully qualified)
```

Any Pod in the cluster can reach your app at `http://hello-service` — zero IP management.

**Quick knowledge check:**
- How does the Service know which Pods to send traffic to? → The `selector: app: hello-app` matches Pod labels. New Pods automatically get added; replaced Pods automatically get removed.
- Docker Compose used `depends_on: mongo` — what's the Kubernetes equivalent? → DNS-based discovery via Service name. Apps must handle retry/reconnect logic themselves (no start order guarantee in K8s).

---

## Part 5 — Full App: Express + MongoDB on Kubernetes

Deploy a two-service application — just like Docker Compose, but on Kubernetes.

### Project Structure

```
k8s-app\
  ├── mongo-deployment.yaml
  ├── mongo-service.yaml
  ├── app-deployment.yaml
  └── app-service.yaml
```

### `mongo-deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mongo-deployment
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
          image: mongo:6
          ports:
            - containerPort: 27017
          volumeMounts:
            - name: mongo-storage
              mountPath: /data/db
      volumes:
        - name: mongo-storage
          emptyDir: {}
```

### `mongo-service.yaml`

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mongo-service
spec:
  type: ClusterIP
  selector:
    app: mongo
  ports:
    - port: 27017
      targetPort: 27017
```

### Update `app.js` Connection String

```js
mongoose.connect('mongodb://mongo-service:27017/hellodb')
```

The hostname is `mongo-service` — the Kubernetes Service name. Rebuild and push:

```powershell
docker build -t yourname/hello-express:2.0 .
docker push yourname/hello-express:2.0
```

### `app-deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-deployment
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
          image: yourname/hello-express:2.0
          ports:
            - containerPort: 3000
```

### `app-service.yaml`

```yaml
apiVersion: v1
kind: Service
metadata:
  name: app-service
spec:
  type: NodePort
  selector:
    app: hello-app
  ports:
    - port: 80
      targetPort: 3000
      nodePort: 30080
```

### Deploy Everything

```powershell
kubectl apply -f mongo-deployment.yaml
kubectl apply -f mongo-service.yaml
kubectl apply -f app-deployment.yaml
kubectl apply -f app-service.yaml

# Or apply the whole folder at once
kubectl apply -f k8s\
```

> If you get `nodePort: Invalid value: 30080: provided port is already allocated`, delete the old service first:
> ```powershell
> kubectl delete service hello-service
> ```

```powershell
kubectl get all
```

`http://localhost:30080` → **Hello from Express! MongoDB: Connected ✅**

### Docker Compose vs Kubernetes — Side by Side

| Docker Compose | Kubernetes |
|---|---|
| `docker-compose.yml` | Multiple YAML files (or one with `---`) |
| `services:` | Deployment + Service (two separate objects) |
| `image:` | `spec.containers[].image` |
| `ports:` | Service `nodePort` |
| `depends_on:` | DNS-based discovery via Service name |
| `volumes:` | PersistentVolumeClaim |
| `docker compose up` | `kubectl apply -f .` |
| `docker compose down` | `kubectl delete -f .` |

---

## Part 6 — Rolling Updates and Rollbacks

### Push a New Version

Update `app.js` with a new message, then:

```powershell
docker build -t yourname/hello-express:3.0 .
docker push yourname/hello-express:3.0
```

### Update the Deployment

```powershell
kubectl set image deployment/app-deployment hello-container=yourname/hello-express:3.0
```

Or edit `app-deployment.yaml`, change the image tag, then `kubectl apply -f app-deployment.yaml`.

### Watch It Roll Out

```powershell
kubectl rollout status deployment/app-deployment
```

```
Waiting for deployment rollout to finish: 1 out of 3 new replicas updated...
Waiting for deployment rollout to finish: 2 out of 3 new replicas updated...
deployment "app-deployment" successfully rolled out
```

Pods get replaced one at a time. Zero downtime. Traffic is always served by healthy Pods.

### Rollback

```powershell
kubectl rollout undo deployment/app-deployment                    # go back one version
kubectl rollout history deployment/app-deployment                 # see all versions
kubectl rollout undo deployment/app-deployment --to-revision=1    # specific version
```

---

## Kubernetes Architecture

```
                 kubectl (Windows Terminal)
                       ↓
          ┌─────────────────────┐
          │    Control Plane    │
          │  API Server         │  ← all kubectl commands hit this
          │  Scheduler          │  ← decides which node runs what
          │  Controller Manager │  ← maintains desired state
          │  etcd (state store) │  ← the cluster's database
          └────────┬────────────┘
                   │ manages
        ┌──────────┴──────────┐
        ↓                     ↓
  ┌──────────┐          ┌──────────┐
  │  Node 1  │          │  Node 2  │
  │  ┌────┐  │          │  ┌────┐  │
  │  │Pod │  │          │  │Pod │  │
  │  │Pod │  │          │  │Pod │  │
  │  └────┘  │          │  └────┘  │
  └──────────┘          └──────────┘
        ↑ traffic
  ┌──────────┐
  │ Service  │ (load balances across all nodes)
  └──────────┘

In Docker Desktop: both control plane and node = docker-desktop (single node)
```

---

## Key Concepts

**Pod** — Smallest unit. Wraps containers. Almost never created directly — Deployments create them.

**Deployment** — Standard way to run stateless apps. Manages a ReplicaSet. Handles rolling updates and rollbacks. Replaces dead Pods.

**ReplicaSet** — Ensures N identical Pods are always running. Managed by Deployments automatically.

**Service** — Stable network endpoint in front of Pods. DNS-based discovery + load balancing. Types: `ClusterIP`, `NodePort`, `LoadBalancer`.

**Namespace** — Virtual cluster inside a cluster. Separates environments (dev, staging, prod). Default namespace = `default`.

**ConfigMap** — Stores non-sensitive config outside the image.

**Secret** — Like ConfigMap but for passwords, API keys. Base64-encoded, access-controlled.

**PersistentVolumeClaim (PVC)** — Persistent storage that survives Pod restarts (replaces Docker volumes).

---

## All kubectl Commands

```powershell
# Cluster
kubectl cluster-info
kubectl get nodes
kubectl get namespaces

# Create/Update
kubectl apply -f file.yaml
kubectl apply -f .\folder\
kubectl delete -f file.yaml

# Pods
kubectl get pods
kubectl get pods -o wide
kubectl get pods --watch
kubectl describe pod <name>
kubectl logs <pod-name>
kubectl logs <pod-name> -f
kubectl exec -it <pod-name> -- sh

# Deployments
kubectl get deployments
kubectl scale deployment <name> --replicas=5
kubectl set image deployment/<name> <container>=<image>:<tag>
kubectl rollout status deployment/<name>
kubectl rollout history deployment/<name>
kubectl rollout undo deployment/<name>

# Services
kubectl get services
kubectl describe service <name>
kubectl port-forward pod/<name> 3000:3000

# General
kubectl get all
kubectl delete pod <name>
kubectl delete deployment <name>
kubectl delete service <name>
```

### Key Distinctions

| These seem similar... | But they're different because... |
|---|---|
| Pod vs Deployment | Pod = one instance. Deployment = manages many Pods with self-healing. |
| `kubectl apply` vs `kubectl create` | `apply` is idempotent (updates if exists). `create` fails if exists. Always use `apply`. |
| ClusterIP vs NodePort vs LoadBalancer | Internal only / external via port / cloud load balancer. |
| `emptyDir` vs PVC | `emptyDir` is deleted with the Pod. PVC persists across Pod replacements. |
| Deployment vs StatefulSet | Deployment = stateless. StatefulSet = stateful (databases needing stable identity). |

---

> **Next → 05 Ansible** — Kubernetes manages your containers, but someone still has to set up the servers those containers run on. Ansible automates all of that — installing Docker, configuring OS settings, deploying apps — across any number of servers at once.
