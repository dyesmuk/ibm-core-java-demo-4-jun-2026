# Kubernetes Hands-On Lab (Simplified)

## Environment

-   Windows 11
-   Docker Desktop (Kubernetes Enabled)
-   Command Prompt (CMD)
-   EMS Docker image already pushed to Docker Hub

------------------------------------------------------------------------

# Learning Objectives

By the end of this lab you will be able to:

-   Verify a Kubernetes cluster
-   Create your first Pod
-   Deploy the EMS application
-   Scale an application
-   Observe Kubernetes self-healing
-   Expose an application using a NodePort Service
-   Perform a rolling update
-   Roll back to a previous version
-   Use basic debugging commands

------------------------------------------------------------------------

# Exercise 1 -- Meet Your Cluster

``` cmd
kubectl version
kubectl cluster-info
kubectl get nodes
kubectl get all
```

**Checkpoint**

-   Is the node in **Ready** state?

------------------------------------------------------------------------

# Exercise 2 -- Create Your First Pod

Complete `pod.yaml.starter`.

Create the Pod.

``` cmd
kubectl apply -f pod.yaml
kubectl get pods
kubectl describe pod ems-api
kubectl logs ems-api
```

Delete it.

``` cmd
kubectl delete pod ems-api
```

**Checkpoint**

Why didn't Kubernetes recreate the Pod?

------------------------------------------------------------------------

# Exercise 3 -- Create a Deployment

Complete `deployment.yaml.starter`.

Use your Docker Hub image.

``` text
<dockerhub-username>/ibm-ems-api:1.0
```

Apply it.

``` cmd
kubectl apply -f deployment.yaml
kubectl get deployments
kubectl get pods
```

------------------------------------------------------------------------

# Exercise 4 -- Self Healing

Delete one Pod.

``` cmd
kubectl get pods
kubectl delete pod <pod-name>
kubectl get pods -w
```

Observe Kubernetes creating another Pod automatically.

------------------------------------------------------------------------

# Exercise 5 -- Scaling

``` cmd
kubectl scale deployment ems-api --replicas=3
kubectl get pods

kubectl scale deployment ems-api --replicas=5

kubectl scale deployment ems-api --replicas=2
```

Observe Pods being created and removed.

------------------------------------------------------------------------

# Exercise 6 -- Create a Service

Complete `service.yaml.starter`.

``` cmd
kubectl apply -f service.yaml
kubectl get services
```

Open the application in your browser using the NodePort.

------------------------------------------------------------------------

# Exercise 7 -- Rolling Update

Push version **1.1** of the EMS image.

Update the Deployment.

``` cmd
kubectl set image deployment/ems-api ems-api=<dockerhub-username>/ibm-ems-api:1.1

kubectl rollout status deployment/ems-api
kubectl rollout history deployment/ems-api
```

------------------------------------------------------------------------

# Exercise 8 -- Rollback

``` cmd
kubectl rollout undo deployment/ems-api

kubectl rollout status deployment/ems-api
```

------------------------------------------------------------------------

# Exercise 9 -- Debugging

Useful commands:

``` cmd
kubectl get all
kubectl describe deployment ems-api
kubectl describe pod <pod-name>
kubectl logs <pod-name>
kubectl exec -it <pod-name> -- cmd
```

------------------------------------------------------------------------

# Exercise 10 -- Cleanup

``` cmd
kubectl delete service ems-api
kubectl delete deployment ems-api

kubectl get all
```

------------------------------------------------------------------------

# Completion Checklist

-   Cluster verified
-   First Pod created
-   Deployment created
-   Pods scaled
-   Self-healing observed
-   Service created
-   EMS accessed in browser
-   Rolling update completed
-   Rollback completed
-   Cleanup completed

------------------------------------------------------------------------

# Command Reference

  Command                   Purpose
  ------------------------- ----------------------
  kubectl get pods          List Pods
  kubectl get deployments   List Deployments
  kubectl get services      List Services
  kubectl describe          Detailed information
  kubectl logs              View logs
  kubectl exec              Open a shell
  kubectl scale             Scale application
  kubectl rollout status    Check rollout
  kubectl rollout undo      Roll back
