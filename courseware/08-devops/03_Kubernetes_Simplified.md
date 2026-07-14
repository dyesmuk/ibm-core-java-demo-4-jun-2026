# Module 03 --- Kubernetes (Simplified)

## Audience

Windows 11 + Docker Desktop + Command Prompt (CMD)

## 1. Why Kubernetes?

Yesterday we used Docker.

Docker can run containers on **one computer**.

What happens if: - the computer crashes? - more users visit the
application? - we need 5 copies of the application?

Docker alone cannot manage all of this automatically.

**Kubernetes** is a container orchestration platform.

It helps us: - Run containers across multiple machines - Restart failed
containers - Scale applications - Update applications with little or no
downtime

**Remember**

-   Docker **runs** containers.
-   Kubernetes **manages** containers.

------------------------------------------------------------------------

## 2. Kubernetes Architecture

                Control Plane
            API Server
            Scheduler
            Controller
            etcd

                  |

          ---------------------
          |                   |
       Worker Node        Worker Node
           Pods              Pods

### Components

-   API Server -- Front door of Kubernetes
-   Scheduler -- Decides where Pods run
-   Controller -- Keeps the desired number of Pods running
-   etcd -- Stores cluster information
-   Worker Node -- Runs Pods

------------------------------------------------------------------------

## 3. Setup (Docker Desktop)

1.  Open Docker Desktop.
2.  Enable **Kubernetes** in Settings.
3.  Wait until Kubernetes is running.

Verify from CMD:

``` cmd
kubectl version
kubectl cluster-info
kubectl get nodes
```

------------------------------------------------------------------------

## 4. kubectl

Useful commands:

``` cmd
kubectl get nodes
kubectl get pods
kubectl get all
kubectl describe node docker-desktop
kubectl cluster-info
```

------------------------------------------------------------------------

## 5. YAML Basics

Example:

``` yaml
apiVersion: v1
kind: Pod

metadata:
  name: nginx

spec:
  containers:
  - name: nginx
    image: nginx
```

Remember:

-   apiVersion = Version
-   kind = Resource type
-   metadata = Name
-   spec = Configuration

------------------------------------------------------------------------

## 6. Pods

A Pod is the smallest unit in Kubernetes.

Usually:

**1 Pod = 1 Container**

Create:

``` cmd
kubectl apply -f pod.yaml
```

View:

``` cmd
kubectl get pods
```

Delete:

``` cmd
kubectl delete pod nginx
```

------------------------------------------------------------------------

## 7. ReplicaSets

ReplicaSets keep the required number of Pods running.

Example:

    Desired Pods = 3

    One Pod crashes

    ↓

    ReplicaSet creates another Pod

------------------------------------------------------------------------

## 8. Deployments

Deployment is the standard way to deploy applications.

Deployment manages ReplicaSets.

Useful commands:

``` cmd
kubectl get deployments
kubectl rollout status deployment/ems-api
kubectl scale deployment ems-api --replicas=5
```

------------------------------------------------------------------------

## 9. Services

Pods can change their IP addresses.

A Service gives applications one stable address.

Service types:

-   ClusterIP
-   NodePort
-   LoadBalancer

For local practice use **NodePort**.

------------------------------------------------------------------------

## 10. EMS Demo

Deploy: - EMS API Deployment - EMS Service

Then:

``` cmd
kubectl get all
```

Scale:

``` cmd
kubectl scale deployment ems-api --replicas=3
```

Delete a Pod and observe Kubernetes automatically creating a new one.

------------------------------------------------------------------------

## Summary

Docker - Creates containers

Docker Compose - Runs multiple containers on one machine

Kubernetes - Runs and manages containers across a cluster - Restarts
failed Pods - Scales applications - Provides stable networking using
Services
