# Module 01 — Git and GitHub

## Learning Objectives
- Understand what Git is and why every developer uses it
- Perform all core Git operations from the command line
- Undo mistakes confidently
- Use GitHub for remote collaboration
- Work with branches, pull requests, forks, and code reviews
- Apply Git workflow to the EMS Node.js project

---

## 1.1 Welcome — What Is Version Control?

Imagine you're building the EMS API. You make a change, it breaks everything, and you want to go back to yesterday's working version. Without version control, you're done. With Git, you just run one command.

**Version control** is a system that records every change made to a set of files over time. You can:
- See exactly what changed, who changed it, and when
- Go back to any previous version
- Work on new features without breaking the working code
- Collaborate with a team without overwriting each other's work

### Git vs GitHub

People often confuse these — they are different things.

| Git | GitHub |
|-----|--------|
| The version control tool itself | A website that hosts Git repositories |
| Runs on your local machine | Runs on the internet |
| Created by Linus Torvalds (2005) | Owned by Microsoft |
| Free, open-source software | Free tier + paid plans |
| Works without internet | Needs internet |

Think of it this way: **Git is the engine; GitHub is the garage where you park your code.**

---

## 1.2 Git Basics

### Install Git

```bash
# Check if already installed
git --version
# git version 2.43.0

# Install on Ubuntu/Debian
sudo apt-get install git

# Install on Mac (via Homebrew)
brew install git

# Install on Windows
# Download from https://git-scm.com/download/win
```

### First-Time Setup

Before using Git, tell it who you are. This information is attached to every commit you make.

```bash
git config --global user.name  "Alice Johnson"
git config --global user.email "alice@ibm.com"
git config --global core.editor "code --wait"   # use VS Code as default editor

# Verify your config
git config --list
```

### Starting a Repository

```bash
# Option 1: Start fresh in an existing folder
cd ibm-ems-api
git init
# Initialized empty Git repository in /home/alice/ibm-ems-api/.git/

# Option 2: Clone an existing remote repository
git clone https://github.com/ibm-team/ems-api.git
cd ems-api
```

`git init` creates a hidden `.git/` folder — this is where Git stores the entire history of your project. Never delete it.

---

### The Three Areas of Git

Understanding this is the most important conceptual step in learning Git.

```
Working Directory       Staging Area         Repository
(your files on disk)    (index / cache)      (.git folder)

    Edit files    →    git add    →    git commit
                                            ↓
                                       History saved
```

| Area | What it is |
|------|-----------|
| **Working Directory** | Files you're currently editing |
| **Staging Area** | Files you've marked as "ready to commit" |
| **Repository** | Permanent history of snapshots (commits) |

### Core Daily Workflow

```bash
# 1. See what's changed
git status

# 2. See the actual diff
git diff                    # unstaged changes
git diff --staged           # staged changes (ready to commit)

# 3. Stage files
git add server.js           # stage one file
git add src/                # stage a folder
git add .                   # stage everything in current directory

# 4. Commit the staged snapshot
git commit -m "Add employee search endpoint"

# 5. See commit history
git log
git log --oneline           # compact view
git log --oneline --graph   # with branch graph

# 6. See what a specific commit changed
git show abc1234
```

### What Makes a Good Commit Message?

```bash
# ❌ Vague — useless in history
git commit -m "fix"
git commit -m "changes"
git commit -m "stuff"

# ✅ Clear — explains WHAT and WHY
git commit -m "Fix employee salary validation to reject negative values"
git commit -m "Add pagination to GET /employees endpoint"
git commit -m "Update Node.js version to 20 LTS in Dockerfile"

# Convention: imperative mood, under 72 characters, present tense
# "Add", "Fix", "Update", "Remove", "Refactor" — not "Added", "Fixed"
```

### `.gitignore` — Tell Git What to Ignore

Create a `.gitignore` file in your project root:

```
# .gitignore for a Node.js project

node_modules/       # never commit this — it's huge and reproducible via npm install
dist/               # compiled output
.env                # secrets and environment variables — NEVER commit
.env.local
*.log               # log files
.DS_Store           # Mac finder metadata
coverage/           # test coverage reports
```

```bash
# Check that .gitignore is working
git status   # node_modules should not appear
```

---

## 1.3 Undoing Things

Mistakes happen. Git has you covered.

### Scenario 1 — Undo changes in working directory (before staging)

```bash
# You edited server.js and want to throw away your changes
git restore server.js          # discard changes to one file
git restore .                  # discard ALL uncommitted changes

# Old syntax (still works)
git checkout -- server.js
```

⚠️ This is **irreversible** — your edits are gone.

### Scenario 2 — Unstage a file (after `git add`, before `git commit`)

```bash
# You accidentally staged server.js — remove it from staging but keep edits
git restore --staged server.js

# Old syntax
git reset HEAD server.js
```

### Scenario 3 — Amend the last commit (not pushed yet)

```bash
# You made a typo in the commit message or forgot to add a file
git add forgotten-file.js
git commit --amend -m "Correct commit message here"
# This rewrites the last commit — only safe if NOT yet pushed
```

### Scenario 4 — Revert a commit (already pushed — safe)

```bash
# Creates a NEW commit that undoes the changes of commit abc1234
# Safe to use on shared branches because it doesn't rewrite history
git revert abc1234
git push
```

### Scenario 5 — Reset to a previous commit

```bash
# Soft reset — go back to commit abc1234, but keep your changes staged
git reset --soft abc1234

# Mixed reset (default) — go back, keep changes in working directory (unstaged)
git reset abc1234

# Hard reset — go back and DISCARD all changes since abc1234
git reset --hard abc1234
# ⚠️ Destructive — uncommitted changes are lost permanently
```

### Scenario 6 — Recover a deleted file

```bash
git restore server.js           # recover from last commit
git checkout abc1234 -- server.js  # recover from a specific commit
```

### Scenario 7 — Stash work in progress

When you need to switch branches but aren't ready to commit:

```bash
# Save current changes to a temporary stack
git stash

# Switch branch, do some work, come back
git checkout main
# ... do work ...
git checkout feature/search

# Restore your stashed changes
git stash pop           # apply and remove from stash
git stash apply         # apply but keep in stash

# See all stashes
git stash list
```

---

## 1.4 The Basics of GitHub

### Create a Repository on GitHub

1. Go to [github.com](https://github.com) → **New repository**
2. Name: `ibm-ems-api`
3. Visibility: Public or Private
4. Do NOT initialise with README (you already have local files)
5. Click **Create repository**

### Connect Local Repo to GitHub

```bash
# Add GitHub as the remote (called 'origin' by convention)
git remote add origin https://github.com/alice-johnson/ibm-ems-api.git

# Verify remote was added
git remote -v
# origin  https://github.com/alice-johnson/ibm-ems-api.git (fetch)
# origin  https://github.com/alice-johnson/ibm-ems-api.git (push)

# Push your local code to GitHub
git push -u origin main
# -u sets 'origin main' as the default for future pushes
# Now you can just type: git push
```

### Authentication — SSH Keys (Recommended)

HTTPS asks for your password every time. SSH keys are more secure and convenient.

```bash
# 1. Generate an SSH key pair
ssh-keygen -t ed25519 -C "alice@ibm.com"
# Accept defaults — key saved to ~/.ssh/id_ed25519

# 2. Copy the PUBLIC key
cat ~/.ssh/id_ed25519.pub
# ssh-ed25519 AAAAC3NzaC1lZDI1NTE5... alice@ibm.com

# 3. Add to GitHub
# GitHub → Settings → SSH and GPG keys → New SSH key → paste

# 4. Test the connection
ssh -T git@github.com
# Hi alice-johnson! You've successfully authenticated...

# 5. Change remote URL to SSH
git remote set-url origin git@github.com:alice-johnson/ibm-ems-api.git
```

### The Push / Pull Cycle

```bash
# Send your commits to GitHub
git push

# Get latest changes from GitHub (fetch + merge)
git pull

# Just fetch — download without merging
git fetch origin

# See what's different between local and remote
git fetch origin
git diff main origin/main
```

---

## 1.5 Working with Branches

Branches let you develop features in isolation without affecting the main codebase.

```
main ──────●──────────────────────●────── (stable, always working)
            \                    /
feature      ●────●────●────────   (your new feature)
```

### Branch Commands

```bash
# List all branches
git branch                  # local branches
git branch -r               # remote branches
git branch -a               # all branches

# Create and switch to a new branch
git checkout -b feature/employee-search
# Modern syntax:
git switch -c feature/employee-search

# Switch to an existing branch
git checkout main
git switch main

# Rename current branch
git branch -m feature/emp-search feature/employee-search

# Delete a branch (after merging)
git branch -d feature/employee-search      # safe delete (warns if unmerged)
git branch -D feature/employee-search      # force delete
```

### Merging

```bash
# 1. Finish your feature
git checkout feature/employee-search
git add .
git commit -m "Add full-text search to employee endpoint"

# 2. Switch back to main
git checkout main

# 3. Merge the feature in
git merge feature/employee-search

# 4. Delete the feature branch (clean up)
git branch -d feature/employee-search
```

### Merge Conflicts — How to Resolve

A conflict happens when two branches changed the same line differently.

```bash
git merge feature/employee-search
# CONFLICT (content): Merge conflict in src/routes/employees.js
# Automatic merge failed; fix conflicts and then commit the result.
```

Open the conflicted file — Git marks the conflict:

```javascript
<<<<<<< HEAD              ← your current branch (main)
router.get('/employees', getAll)
=======                   ← dividing line
router.get('/employees', searchEmployees)
>>>>>>> feature/employee-search  ← incoming branch
```

Edit the file to the correct version:

```javascript
// Resolved: keep the new search function
router.get('/employees', searchEmployees)
```

```bash
# Mark as resolved and commit
git add src/routes/employees.js
git commit -m "Merge feature/employee-search — keep searchEmployees handler"
```

### Branch Naming Conventions

```
feature/employee-search       ← new feature
bugfix/salary-validation      ← bug fix
hotfix/jwt-token-expiry       ← urgent production fix
release/1.2.0                 ← release preparation
chore/update-dependencies     ← maintenance
docs/api-documentation        ← documentation only
```

---

## 1.6 Forking and Contributing

**Fork** = your own copy of someone else's repository on GitHub. Use this to contribute to projects you don't own.

### Contribution Workflow (Fork → PR)

```bash
# 1. Fork the repo on GitHub (click "Fork" button)

# 2. Clone YOUR fork locally
git clone git@github.com:alice-johnson/ibm-ems-api.git
cd ibm-ems-api

# 3. Add the ORIGINAL repo as 'upstream'
git remote add upstream git@github.com:ibm-team/ibm-ems-api.git
git remote -v
# origin    git@github.com:alice-johnson/ibm-ems-api.git (fetch)
# upstream  git@github.com:ibm-team/ibm-ems-api.git (fetch)

# 4. Create a feature branch
git checkout -b feature/department-filter

# 5. Make changes, commit
git add .
git commit -m "Add department filter to employee list endpoint"

# 6. Push to YOUR fork
git push origin feature/department-filter

# 7. Go to GitHub → open a Pull Request from your fork's branch
#    to the original repo's main branch
```

### Keeping Your Fork Up to Date

```bash
# Fetch changes from the original repo
git fetch upstream

# Merge them into your local main
git checkout main
git merge upstream/main

# Push the updated main to your fork
git push origin main
```

---

## 1.7 Collaboration

### Pull Requests (PRs)

A Pull Request is a GitHub feature — not a Git command. It's a request to merge your branch into another branch, with a built-in code review process.

**Good PR habits:**
- Keep PRs small and focused — one feature or fix per PR
- Write a description explaining WHAT changed and WHY
- Link to the issue it resolves: `Closes #42`
- Request specific reviewers
- Respond to review comments promptly

### Code Review

When reviewing someone else's PR:
- Comment on specific lines — click the `+` button next to a line
- Use **Request changes** if blocking issues exist
- Use **Approve** when ready to merge
- Never approve code you don't understand

### Protected Branches

For team projects, protect `main` so no one can push directly:

**GitHub → Settings → Branches → Branch protection rules:**
- ✅ Require a pull request before merging
- ✅ Require at least 1 approving review
- ✅ Require status checks to pass (CI tests)
- ✅ Do not allow bypassing

### Git Flow — The Team Branching Strategy

```
main         ──●──────────────────────●─── (production)
               │                      │
release/1.1    └────●────●────────────┘   (release prep)
                    │
develop        ●────●────●────●────────── (integration)
                    │    │
feature/search      └────┘
feature/dept             └────●────●
```

| Branch | Purpose |
|--------|---------|
| `main` | Production-ready code only |
| `develop` | Integration branch — all features merge here |
| `feature/*` | Individual feature development |
| `release/*` | Release preparation and bugfixes |
| `hotfix/*` | Emergency production fixes |

For small teams or solo projects, a simpler **GitHub Flow** works well:
- `main` is always deployable
- Branch for every feature
- Open PR to merge back to `main`
- Deploy immediately after merge

---

## 1.8 EMS Project — Git Workflow in Practice

```bash
# Initial setup
cd ibm-ems-api
git init
echo "node_modules/" > .gitignore
echo ".env" >> .gitignore
git add .
git commit -m "Initial commit — EMS Node.js API"
git remote add origin git@github.com:your-team/ibm-ems-api.git
git push -u origin main

# Daily feature work
git checkout -b feature/employee-pagination
# ... make changes ...
git add src/routes/employees.js
git commit -m "Add cursor-based pagination to GET /employees"
git push origin feature/employee-pagination
# → Open PR on GitHub → Review → Merge → Delete branch

# Sync with team
git checkout main
git pull
git checkout -b feature/department-api
```

---

## Summary

| Command | What it does |
|---------|-------------|
| `git init` | Initialise a new repository |
| `git clone <url>` | Copy a remote repository locally |
| `git status` | See staged, unstaged, and untracked files |
| `git add <file>` | Stage a file for commit |
| `git commit -m "msg"` | Save staged changes as a snapshot |
| `git log --oneline` | See commit history |
| `git push` | Upload commits to remote |
| `git pull` | Download + merge remote commits |
| `git branch -b <name>` | Create and switch to new branch |
| `git merge <branch>` | Merge a branch into current |
| `git restore <file>` | Discard working directory changes |
| `git stash` | Temporarily save uncommitted changes |
| `git revert <hash>` | Safely undo a commit (creates new commit) |
| `git reset --hard <hash>` | Destructive undo — loses changes |

**Next → Module 02: Docker**
