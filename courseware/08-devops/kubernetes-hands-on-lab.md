# Kubernetes — Hands-On Lab
### EMS Project · Individual Practice · Half-Day Session (~3.5 hrs)

Companion practical to the **Kubernetes** module. Builds directly on the
Docker lab — you'll deploy the exact image you built and pushed to
Docker Hub there, now onto your own local cluster instead of running it
with `docker run`.

> **Starter kit:** `ibm-ems-k8s-starter.zip` — 7 fill-in-the-blank YAML
> manifests, same style as the Docker lab's `Dockerfile.starter`.

---

## Why this is based on the Docker lab, and why it's individual

Kubernetes doesn't run source code — it runs **images**. The image you
already built, tagged, and proved portable in the Docker lab
(`<your-dockerhub-username>/ibm-ems-api:1.0`) is exactly what gets
deployed here. That's the handoff: "you proved this image runs
anywhere — now a cluster runs it instead of you typing `docker run`."

Like Docker, Kubernetes' core loop — describe desired state, apply it,
inspect it — doesn't need a second person. Everyone runs their own
local cluster via Minikube.

### Scope for this lab

Trimmed to **app + database only** (no reverse proxy, no Adminer this
round) — Kubernetes YAML is verbose, and the goal is depth on Pods,
Deployments, Services, and networking, not breadth across every piece
from the Docker lab. Proxy and Adminer are stretch goals if you finish
early — you've now built the Deployment+Service pattern twice, doing it
a third time on your own is the real test.

One more scope note: the app Pod will prove it can **reach** the
database Pod over the network — that's the real Kubernetes lesson
(Service discovery, DNS). It won't actually **use** Postgres for
persistence in this lab; it still runs on its bundled H2 database.
Fully rewiring that needs a driver dependency + image rebuild, which
belongs in a stretch goal, not the core path.

---

## Learning Objectives

- [ ] Explain the relationship between a cluster, a node, and a Pod
- [ ] Install and start a local cluster with Minikube
- [ ] Write and apply basic YAML manifests with `kubectl apply -f`
- [ ] Explain why Deployments (and the ReplicaSets they manage) exist, not just Pods
- [ ] Observe Kubernetes self-healing a deleted Pod
- [ ] Expose an application with a Service, and explain ClusterIP vs. NodePort
- [ ] Use Kubernetes' internal DNS for Pod-to-Pod communication
- [ ] Use a Secret to avoid hardcoding credentials
- [ ] Use a PersistentVolumeClaim so Pod storage survives Pod recreation
- [ ] Scale a Deployment and perform a rolling update / rollback
- [ ] Debug a running workload with `describe`, `logs`, and `exec`

---

## 1. The Scenario

```
   Docker Hub
   <you>/ibm-ems-api:1.0
         │
         │ pulled by
         ▼
   ┌─────────────────────── your Minikube cluster ───────────────────────┐
   │                                                                      │
   │   Deployment: ems-app  ──Service: ems-app (NodePort)──▶ you (laptop) │
   │        │                                                             │
   │        │  DNS: "ems-db"                                              │
   │        ▼                                                             │
   │   Deployment: ems-db  ──Service: ems-db (ClusterIP, internal only)   │
   │        │                                                             │
   │        ▼                                                             │
   │   PersistentVolumeClaim (survives Pod deletion)                      │
   │                                                                      │
   └──────────────────────────────────────────────────────────────────────┘
```

---

## 2. Trainer Setup — Do This Before the Session

1. **Confirm your own environment works:**
   ```bash
   minikube version
   kubectl version --client
   minikube start
   kubectl get nodes        # should show one node, STATUS Ready
   ```
2. Images are pulled live from Docker Hub during the session (each
   trainee's own image, plus `postgres:16-alpine`) — confirm your
   classroom network can handle 16 simultaneous pulls, or stagger
   Exercise 3 starts by a few minutes across the room if bandwidth is
   tight.
3. **Share `ibm-ems-k8s-starter.zip`** with the batch before the
   session starts.
4. Remind trainees their Docker Hub image from the previous lab must
   still be **public** — Minikube can't use their `docker login`
   session to pull a private image without extra `imagePullSecrets`
   setup, which is out of scope here.
5. If your classroom is on Windows, confirm Minikube's driver is set to
   `docker` (works inside WSL2/Docker Desktop, same as the Docker lab):
   ```bash
   minikube start --driver=docker
   ```

---

## 3. Trainee Pre-Work Checklist

- [ ] `kubectl` installed — `kubectl version --client` works
- [ ] Minikube installed — `minikube version` works
- [ ] `minikube start` succeeds, `kubectl get nodes` shows `Ready`
- [ ] Your image from the Docker lab is still on Docker Hub and **public**
      (spot check: `docker pull <your-dockerhub-username>/ibm-ems-api:1.0`
      on a machine where you're logged out, or just check the repo page
      on hub.docker.com is visible without logging in)
- [ ] `ibm-ems-k8s-starter.zip` extracted

---

## 4. Session Timeline (≈ 3.5 hrs)

| # | Segment | Time |
|---|---------|------|
| — | Kickoff, Docker → Kubernetes handoff recap | 10 min |
| — | Trainer walkthrough of the starter kit + cluster concepts | 10 min |
| Ex 1 | Meet Your Cluster | 15 min |
| Ex 2 | Your First Pod | 15 min |
| Ex 3 | From Pod to Deployment (self-healing) | 30 min |
| Ex 4 | Expose It — Services | 20 min |
| — | Break | 10 min |
| Ex 5 | Add the Database (Secret, PVC, Deployment, Service) | 35 min |
| Ex 6 | Networking Lab — Pod-to-Pod DNS | 20 min |
| Ex 7 | Scaling & Rolling Updates | 25 min |
| Ex 8 | Debugging & Inspection | 10 min |
| Ex 9 | Cleanup & Wrap-Up | 10 min |
| | **Total** | **~210 min** |

---

## Exercise 1 — Meet Your Cluster (15 min)

**Concepts:** cluster, node, `kubectl` basics

```bash
minikube start
kubectl get nodes
kubectl cluster-info
kubectl get pods -A          # every Pod already running the cluster itself needs
minikube dashboard           # optional — opens a visual UI in your browser, Ctrl+C to close
```

**Checkpoint:** `kubectl get nodes` shows one node with `STATUS Ready`.
State in one sentence what a "node" is versus what the "cluster" is.

---

## Exercise 2 — Your First Pod (15 min)

**Concepts:** the Pod primitive, `kubectl apply`

Open `k8s/pod-solo.yaml.starter`, fill in the two `????`, save as
`k8s/pod-solo.yaml`:

```bash
kubectl apply -f k8s/pod-solo.yaml
kubectl get pods
kubectl describe pod ems-app-solo    # full event history — watch it pull, then start
kubectl logs ems-app-solo
```

Now delete it and notice what happens:
```bash
kubectl delete pod ems-app-solo
kubectl get pods    # gone — nothing brings it back. That's the point of Exercise 3.
```

**Checkpoint:** you can explain why a bare Pod, once deleted, stays
deleted — nothing is watching over it.

---

## Exercise 3 — From Pod to Deployment (30 min)

**Concepts:** Deployments, ReplicaSets, self-healing

Open `k8s/app-deployment.yaml.starter`, fill in the TODOs, save as
`k8s/app-deployment.yaml`:

```bash
kubectl apply -f k8s/app-deployment.yaml
kubectl get deployments
kubectl get replicasets
kubectl get pods --show-labels
```

Notice the Deployment created a ReplicaSet, which created your Pods —
three objects, one command.

**Now the self-healing demo:**
```bash
kubectl get pods                       # note one Pod's exact name
kubectl delete pod <that-exact-name>
kubectl get pods -w                    # watch — Ctrl+C once a NEW pod appears
```

**Checkpoint:** a replacement Pod appeared automatically, with a
different name/IP but the same labels. Explain, using the words
"desired state," why that happened.

---

## Exercise 4 — Expose It: Services (20 min)

**Concepts:** Services, ClusterIP vs. NodePort, stable networking

Open `k8s/app-service.yaml.starter`, fill in the TODOs, save as
`k8s/app-service.yaml`:

```bash
kubectl apply -f k8s/app-service.yaml
kubectl get services
minikube service ems-app --url     # gives you a real URL
curl $(minikube service ems-app --url)/api/employees
```

Delete a Pod again while the Service is running, and immediately
re-curl — no waiting needed:
```bash
kubectl delete pod <a-different-pod-name>
curl $(minikube service ems-app --url)/api/employees   # still works, seamlessly
```

**Checkpoint:** explain, in one sentence, why the curl kept working
even while a Pod behind it was being replaced — what did the Service
give you that a raw Pod IP couldn't?

---

## Exercise 5 — Add the Database (35 min)

**Concepts:** Secrets, PersistentVolumeClaims, multi-object dependency
order

Apply these **in order** — each one depends on the last:

```bash
# 1. credentials
#    fill in k8s/db-secret.yaml.starter → save as k8s/db-secret.yaml
kubectl apply -f k8s/db-secret.yaml
kubectl get secrets

# 2. storage
#    fill in k8s/db-pvc.yaml.starter → save as k8s/db-pvc.yaml
kubectl apply -f k8s/db-pvc.yaml
kubectl get pvc                 # STATUS should become "Bound"

# 3. the database itself
#    fill in k8s/db-deployment.yaml.starter → save as k8s/db-deployment.yaml
kubectl apply -f k8s/db-deployment.yaml
kubectl get pods -w             # wait for ems-db pod to reach Running

# 4. internal-only Service
#    fill in k8s/db-service.yaml.starter → save as k8s/db-service.yaml
kubectl apply -f k8s/db-service.yaml
kubectl get services            # note: ems-db has no EXTERNAL-IP — that's intentional
```

Prove persistence the same way you did in the Docker lab:
```bash
kubectl exec -it deploy/ems-db -- psql -U <your-postgres-user> -d <your-postgres-db> -c "CREATE TABLE proof(id int);"
kubectl delete pod -l app=ems-db          # force it to be recreated
kubectl get pods -w                       # wait for the replacement to be Running
kubectl exec -it deploy/ems-db -- psql -U <your-postgres-user> -d <your-postgres-db> -c "\dt"
# "proof" table is still there — the PVC survived the Pod's death
```

**Checkpoint:** all 4 objects (Secret, PVC, Deployment, Service) exist
and are healthy; the proof table survived a Pod deletion.

---

## Exercise 6 — Networking Lab: Pod-to-Pod DNS (20 min)

**Concepts:** cluster-internal DNS, why "ems-db" resolves automatically

Launch a disposable debug Pod so you're not dependent on whatever
tools happen to be installed in your app image:

```bash
kubectl run netshoot --rm -it --image=busybox -- sh
```
Inside that shell:
```sh
nslookup ems-db
nc -zv ems-db 5432
exit
```

**Checkpoint:** `nslookup` resolved `ems-db` to an internal cluster IP,
and `nc` confirmed port 5432 is reachable — proving Pod-to-Pod
networking and DNS work, without you ever looking up an IP address by
hand. This is the direct successor to the Docker custom-network
exercise, one layer up.

---

## Exercise 7 — Scaling & Rolling Updates (25 min)

**Concepts:** `kubectl scale`, rolling updates, rollback

**Scale:**
```bash
kubectl scale deployment ems-app --replicas=4
kubectl get pods -w      # watch new Pods appear — Ctrl+C once all 4 are Running
kubectl scale deployment ems-app --replicas=2
```

**Rolling update** — reuse your Docker lab skills to give Kubernetes a
new version to roll toward (same image content, new tag, purely so
there's something real to roll):
```bash
docker tag ibm-ems-api:local <your-dockerhub-username>/ibm-ems-api:1.1
docker push <your-dockerhub-username>/ibm-ems-api:1.1

kubectl set image deployment/ems-app ems-app=<your-dockerhub-username>/ibm-ems-api:1.1
kubectl rollout status deployment/ems-app
kubectl rollout history deployment/ems-app
```

**Rollback:**
```bash
kubectl rollout undo deployment/ems-app
kubectl rollout status deployment/ems-app
```

**Checkpoint:** you watched a rolling update replace Pods gradually
(not all at once), and successfully rolled back to the previous image
version.

---

## Exercise 8 — Debugging & Inspection (10 min)

**Concepts:** `describe`, `logs`, `exec`, reading events

```bash
kubectl get pods
kubectl describe pod <any-ems-app-pod-name>   # scroll to the Events section at the bottom
kubectl logs <any-ems-app-pod-name>
kubectl exec -it <any-ems-app-pod-name> -- sh
   ps aux
   exit
```

**Checkpoint:** you can name the three commands you'd reach for, in
order, if a Pod were stuck in `CrashLoopBackOff` (hint: `describe` for
events, `logs` for application output, `exec` if it's actually running
but misbehaving).

---

## Exercise 9 — Cleanup & Wrap-Up (10 min)

```bash
kubectl delete -f k8s/db-service.yaml
kubectl delete -f k8s/db-deployment.yaml
kubectl delete -f k8s/db-pvc.yaml
kubectl delete -f k8s/db-secret.yaml
kubectl delete -f k8s/app-service.yaml
kubectl delete -f k8s/app-deployment.yaml
kubectl get all              # should be back to just the cluster's own system Pods
minikube stop                # or `minikube delete` to fully tear down the VM/container
```

### Stretch goals (if time remains)

- **Rebuild the proxy and Adminer patterns from the Docker lab,
  entirely on your own** — no starter files this time. You already
  know the shape: Deployment + Service, repeated.
- **Fully wire the app to Postgres**: add the PostgreSQL JDBC driver to
  `pom.xml`, rebuild and push a new image tag, then set
  `SPRING_DATASOURCE_URL=jdbc:postgresql://ems-db:5432/<db-name>` (and
  matching username/password) as environment variables in
  `app-deployment.yaml`, sourced from `ems-db-secret`.
- **Multi-node clusters**: Minikube is single-node by design. Real
  production clusters span multiple physical or virtual machines,
  typically bootstrapped with `kubeadm`. That's infrastructure-heavy
  setup, out of scope for a laptop-based half-day lab — worth a
  conceptual walkthrough/demo by the trainer rather than hands-on here.

---

## Completion Checklist (self-tick)

- [ ] Started a local cluster and confirmed a Ready node
- [ ] Applied a raw Pod manifest and watched it NOT recover after deletion
- [ ] Applied a Deployment and watched it self-heal a deleted Pod
- [ ] Exposed a Deployment with a NodePort Service and reached it from outside the cluster
- [ ] Created a Secret and referenced it from a Deployment
- [ ] Created a PersistentVolumeClaim and proved data survives Pod recreation
- [ ] Resolved a Service by name from inside another Pod (DNS)
- [ ] Scaled a Deployment up and down
- [ ] Performed a rolling update and a rollback
- [ ] Used `describe`, `logs`, and `exec` to debug a running Pod
- [ ] Cleaned up all objects and stopped the cluster

---

## Trainer's Evaluation Rubric

| Signal | What good looks like |
|--------|------------------------|
| YAML correctness | No leftover `????` placeholders, correct indentation, valid `kubectl apply` |
| Selector/label matching | Service `selector` genuinely matches the Deployment's Pod `labels` |
| Self-healing demo | Trainee can articulate *why* it happened, not just that it did |
| Service type choice | NodePort for the app (external), ClusterIP for the db (internal) — deliberate, not copy-pasted |
| PVC understanding | Can explain what would happen WITHOUT the PVC, not just that data persisted with it |
| Rolling update | Understands `rollout status`/`history`/`undo`, not just that the image changed |
| Debugging fluency | Reaches for `describe` before `logs` when a Pod won't start |

---

## Common Pitfalls / FAQ

**"My Pod is stuck in `ImagePullBackOff`."**
Almost always a typo in the image name, or the Docker Hub repo isn't
public. Run `kubectl describe pod <name>` and read the Events section —
it tells you exactly what failed.

**"`minikube service ems-app --url` hangs or doesn't open anything."**
That's expected with the `docker` driver on Mac/Windows — the command
prints a URL and needs to keep running to tunnel traffic; leave that
terminal open and use the URL in a separate terminal/browser.

**"My Service has no effect — curl still fails."**
Check `kubectl get endpoints ems-app`. If it's empty, the Service's
`selector` doesn't match any Pod's `labels` — usually a typo between
`app-deployment.yaml` and `app-service.yaml`.

**"PVC stuck in `Pending`, never becomes `Bound`."**
Usually a StorageClass issue on Minikube — try
`kubectl get storageclass` and confirm one is marked `(default)`. If
none exist, `minikube addons enable storage-provisioner` and reapply.

**"`kubectl exec` into the Postgres Pod says `psql: command not found`."**
Make sure you're exec-ing into the `ems-db` Deployment, not the app —
`kubectl exec -it deploy/ems-db -- psql ...`, not `deploy/ems-app`.

**"Everything I do only affects `default` namespace — is that a problem?"**
Not for this lab — every object you create with no `namespace:`
specified lands in `default`, which is fine for a single-user training
cluster. Namespaces matter more once multiple teams share one real
cluster.

---

## Command Reference (this lab)

| Command | What it does |
|---------|---------------|
| `minikube start` / `stop` / `delete` | Manage your local cluster |
| `kubectl get nodes/pods/deployments/services/pvc/secrets` | List objects |
| `kubectl apply -f <file>` | Create/update an object from YAML |
| `kubectl delete -f <file>` | Remove an object |
| `kubectl describe pod <name>` | Full details + event history |
| `kubectl logs <pod>` | View a Pod's application output |
| `kubectl exec -it <pod> -- sh` | Shell into a running Pod |
| `kubectl scale deployment <name> --replicas=N` | Change replica count |
| `kubectl set image deployment/<name> <container>=<image>` | Trigger a rolling update |
| `kubectl rollout status/history/undo deployment/<name>` | Manage rollout state |
| `kubectl run <name> --rm -it --image=<image> -- sh` | Disposable debug Pod |
| `minikube service <name> --url` | Get a reachable URL for a NodePort Service |

---

**Next in this series: Ansible** — individual practice again, each
trainee acting as their own control node against containerized "target
servers." Let me know when you're ready and I'll ask a few scoping
questions first, the same way we did here.
