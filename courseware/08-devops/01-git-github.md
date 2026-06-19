# 01 — Git & GitHub
> **DevOps Hands-On Series** | Module 1 of 6 | Node.js Express project

---

## Why Should You Care About Git?

You know that folder on your desktop called `final_v2_REAL_actualFINAL.js`? Yeah. Git fixes that.

Git is a **version control system** — it tracks every single change you ever make to your code. You can go back in time, experiment without fear of breaking things, and collaborate with a team without overwriting each other's work.

GitHub is where your Git repositories live on the internet. Think of Git as the tool, and GitHub as the cloud storage + collaboration platform built on top of it.

```
Without Git:                       With Git:
final.js                           Every save = a named snapshot
final_v2.js                        You write what changed + why
final_v2_FIXED.js                  Jump to ANY point in history
final_USE_THIS_ONE_SERIOUSLY.js    Your whole team works from the same timeline
```

```
Git     = the tool on YOUR machine that tracks changes
GitHub  = the website that hosts repos + enables team collaboration
```

This is **Module 1 of 6**. The Node.js app you build here travels through every module:

```
01 Git & GitHub  ← YOU ARE HERE
02 Docker        → containerise the same app
03 YAML          → config language for the tools ahead
04 Kubernetes    → orchestrate it at scale
05 Ansible       → automate server setup
06 Jenkins       → automate the entire pipeline on every push
```

**What you need:** Git for Windows (git-scm.com), VS Code, Node.js, a GitHub account.

---

## Part 1 — Git Basics

### Install Git on Windows 11

Download from [git-scm.com](https://git-scm.com). During install, pick:
- **Git Bash** as the default terminal (Unix-style commands that match every tutorial on the internet)
- **VS Code** as the default editor

Verify it worked:

```bash
git --version
# Expected: git version 2.4x.x.windows.x
```

### Tell Git Who You Are

Git stamps every commit with your name and email. Set this once:

```bash
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
git config --global core.editor "code --wait"

# Double-check
git config --list
```

### Create Your First Repo

A **repository** (repo) is just a folder that Git is tracking. The `git init` command creates a hidden `.git` subfolder — that's the actual database storing all your history. Never delete it.

```bash
mkdir hello-git
cd hello-git
git init
```

### Build the Node.js App

Create `app.js`:

```js
const express = require('express');
const app = express();

app.get('/', (req, res) => {
  res.send('Hello from Git and Express!');
});

app.listen(3000, () => console.log('Server running on port 3000'));
```

Create `package.json`:

```json
{
  "name": "hello-git",
  "version": "1.0.0",
  "main": "app.js",
  "dependencies": {
    "express": "4.18.2"
  }
}
```

Install dependencies:

```bash
npm install
```

This creates a `node_modules/` folder — about 50MB of files. You do **not** want to commit this.

### .gitignore — Tell Git What to Ignore

Create this file **before** your first commit. No exceptions.

```
# Dependencies — always reproducible via npm install, never commit
node_modules/

# Secrets — API keys, passwords — NEVER commit
.env

# OS clutter
.DS_Store
Thumbs.db

# Logs
*.log
npm-debug.log*

# Build output
dist/
build/
```

Why `node_modules` must stay out of Git:
- It can be 50–200MB of files
- It's fully reproducible with `npm install`
- It's OS-specific — Windows builds may break on Linux
- Anyone who clones your repo just runs `npm install` and gets a clean copy

**Golden rule:** Commit the recipe, not the output.

Check that it's working:

```bash
git status
# node_modules should NOT appear in the list
```

### The Three Areas of Git

This trips people up at first, but once it clicks, everything makes sense:

```
Working Directory      →   Staging Area     →   Repository (.git)
(your files on disk)       (selected changes     (permanent history,
                            ready to commit)      every commit ever made)

          git add                                  git commit
```

| Area | What it is |
|---|---|
| Working Directory | Your actual files as they are right now |
| Staging Area | A holding zone — you hand-pick what goes into the next commit |
| Repository | The permanent record. Immutable once committed. |

The staging area is powerful: you can change 5 files but only commit 2 of them, keeping your history clean and meaningful.

### Make Your First Commit

```bash
# Stage everything
git add .

# See what's staged
git status

# Commit with a descriptive message
git commit -m "Initial commit: Express app with gitignore"
```

You'll see something like:
```
[main (root-commit) a3f9c12] Initial commit: Express app with gitignore
 4 files changed, 15 insertions(+)
```

That `a3f9c12` is the **commit hash** — a unique fingerprint for this snapshot.

### View History and Changes

```bash
# Full history
git log

# Clean one-liner view
git log --oneline

# What changed in a specific commit
git show a3f9c12
```

### Make More Commits

Update `app.js` — change the response message to `'Hello v2'`. Then:

```bash
# See what changed before staging
git diff

git add app.js
git commit -m "Update home route response to v2"

git log --oneline
# b1e2d34 Update home route response to v2
# a3f9c12 Initial commit: Express app with gitignore
```

**Quick knowledge check:**
- Why does a staging area exist? → So you can craft precise, meaningful commits instead of committing everything at once.
- What happens if you skip `.gitignore` and commit `node_modules`? → Thousands of files, huge repo, painful push. Fixing it later means rewriting history. Always create `.gitignore` first.
- Difference between `git add .` and `git add app.js`? → `.` stages everything changed; the filename stages only that file. Precise staging = precise history.

---

## Part 2 — Undoing Things

Git's superpower is the ability to go back. Here's your toolkit:

### Undo Before Staging

Changed a file but haven't staged it yet? Throw it away:

```bash
git restore app.js
```

The file snaps back to the last committed version. **This is permanent** — the change is gone.

### Undo After Staging

Ran `git add` but changed your mind? Unstage it:

```bash
git restore --staged app.js
```

The file stays changed on disk — it's just removed from the staging area.

### Undo a Pushed Commit — `git revert`

You committed something wrong that others might have already pulled. `git revert` creates a **new** commit that reverses the damage — the original commit stays in history, nothing is erased:

```bash
git log --oneline              # find the commit hash to undo
git revert b1e2d34             # creates a new "undo" commit
```

Safe. Transparent. The right tool when the commit is shared.

### Rewrite Local History — `git reset`

For commits that exist only on your machine:

```bash
git reset --soft HEAD~1    # undo commit, keep files staged
git reset HEAD~1           # undo commit, unstage files (keep files)
git reset --hard HEAD~1    # undo commit AND delete file changes (⚠️ gone!)
```

`HEAD~1` = one commit before where you are now.

| Command | Commit undone? | Staging cleared? | File changes gone? |
|---|---|---|---|
| `reset --soft` | ✅ | ❌ | ❌ |
| `reset --mixed` (default) | ✅ | ✅ | ❌ |
| `reset --hard` | ✅ | ✅ | ✅ gone! |

**Rule of thumb:** Use `revert` on anything pushed to GitHub. Use `reset` only on local-only commits nobody else has seen.

### Time-Travel Without Changing Anything

```bash
# Peek at any file at any commit
git show a3f9c12:app.js

# Temporarily go back (read-only exploration)
git checkout a3f9c12
git checkout main          # return to the present
```

**Quick knowledge check:**
- `git revert` vs `git reset`? → `revert` adds a new undo commit (safe for shared history). `reset` moves the pointer back (rewrites history — dangerous on shared branches).
- What does `HEAD` mean? → It's a pointer to your current position in history. `HEAD~1` is one step back, `HEAD~3` is three steps back.

---

## Part 3 — The Basics of GitHub

### Create a GitHub Account

Go to [github.com](https://github.com) and sign up. Your username appears in every repo URL and commit you push.

### Create a Repository on GitHub

1. Click **+** → **New repository**
2. Name: `hello-git`
3. Visibility: Public
4. **Do NOT** tick "Initialize with README" — you already have a local repo
5. Click **Create repository**

### Connect Your Local Repo to GitHub

```bash
# "origin" is the conventional name for your main remote
git remote add origin https://github.com/yourname/hello-git.git

# Confirm it's set
git remote -v

# Push your commits up
git push -u origin main
```

The `-u` flag sets the upstream tracking — after this, plain `git push` and `git pull` work without specifying where.

Open `https://github.com/yourname/hello-git` in your browser — your code is live.

### Authenticate with GitHub (Personal Access Token)

GitHub stopped accepting passwords for `git push`. Use a token instead:

1. GitHub → **Settings → Developer settings → Personal access tokens → Tokens (classic)**
2. **Generate new token** → scope: `repo`
3. Copy it (you won't see it again)

When Git asks for a password on push, paste the token. To save it so you don't re-type it:

```bash
git config --global credential.helper manager
```

Git Credential Manager (ships with Git for Windows) stores it automatically after the first push.

### The Daily Workflow

```bash
git pull                          # grab latest changes before starting
# ...make your changes...
git add .
git commit -m "describe what you changed"
git push
```

### Clone an Existing Repo

```bash
git clone https://github.com/yourname/hello-git.git
cd hello-git
npm install      # node_modules isn't in Git — reinstall it fresh
```

`git clone` downloads everything including full history, and sets up `origin` automatically.

**Quick knowledge check:**
- Why does `npm install` need to run after cloning? → `node_modules` is gitignored. `package-lock.json` IS committed, so `npm install` recreates the exact same versions.
- What does `-u` in `git push -u origin main` do? → Sets the default upstream. After that, you just type `git push`.

---

## Part 4 — Working with Branches

Branches let you work on a feature in complete isolation — without touching the stable main codebase. This is the foundation of all team collaboration.

```
main branch:    A — B — C                    (stable, always deployable)
                          \
feature branch:            D — E — F         (your work in progress)
```

When the feature is ready and tested, merge it back.

### Create and Switch Branches

```bash
# Create a new branch and switch to it
git switch -c feature/add-about-page

# See all branches (* = current)
git branch

# Switch between branches
git switch main
git switch feature/add-about-page
```

### Work on the Feature

Add a new route to `app.js`:

```js
app.get('/about', (req, res) => {
  res.send('About page — Hello Git project');
});
```

```bash
git add app.js
git commit -m "Add /about route"
```

This commit exists only on `feature/add-about-page`. The `main` branch is completely untouched.

### Push the Branch to GitHub

```bash
git push -u origin feature/add-about-page
```

Now your teammate can see it, check it out, and contribute.

### Merge Back Into Main

```bash
git switch main
git merge feature/add-about-page
```

When `main` hasn't changed since the branch was created, Git does a **fast-forward merge** — it just moves the pointer forward. Clean and linear.

### Clean Up Merged Branches

```bash
git branch -d feature/add-about-page          # delete locally
git push origin --delete feature/add-about-page  # delete on GitHub
```

### Handle Merge Conflicts

A conflict happens when two branches change the same line differently. Git marks it:

```
<<<<<<< HEAD
  res.send('Hello from Git and Express! v2');
=======
  res.send('Hello from the feature branch!');
>>>>>>> feature/add-about-page
```

Fix it by:
1. Editing the file — delete the markers, keep what you want
2. `git add app.js`
3. `git commit -m "Resolve merge conflict in app.js"`

### Branch Naming Conventions

| Pattern | Use case |
|---|---|
| `feature/user-login` | New features |
| `fix/null-pointer` | Bug fixes |
| `hotfix/security-patch` | Urgent production fixes |
| `release/v2.0` | Release prep |
| `chore/update-deps` | Maintenance |

**Quick knowledge check:**
- Why not commit directly to `main`? → `main` should always be deployable. Unfinished work on `main` could push broken code to production.
- You switched back to `main` and your feature changes disappeared — where did they go? → They're still there, on the feature branch. Switching branches changes your working directory to match that branch's state.
- `merge` vs `rebase`? → `merge` preserves full history with a merge commit. `rebase` replays your commits on top of the target branch for a cleaner linear history.

---

## Part 5 — Forking and Contributing

### What's a Fork?

A **fork** is your personal copy of someone else's repo on GitHub. You can change anything without affecting the original. When ready, you propose changes back via a **Pull Request**.

```
Original repo (owner's GitHub)
      ↓  Fork
Your fork (your GitHub)
      ↓  Clone
Your local machine
      ↓  Push changes
Your fork
      ↓  Pull Request → original owner reviews + merges
```

### Fork → Clone → Contribute

1. Go to any GitHub repo, click **Fork** → **Create fork**
2. Clone your fork locally:

```bash
git clone https://github.com/yourname/repo-name.git
cd repo-name
```

3. Add the original as `upstream` so you can pull in future updates:

```bash
git remote add upstream https://github.com/originalowner/repo-name.git
git remote -v
# origin    → your fork
# upstream  → the original
```

4. Create a branch, make changes, push, open a PR:

```bash
git switch -c fix/typo-in-readme
# make changes...
git add .
git commit -m "Fix typo in README"
git push origin fix/typo-in-readme
```

On GitHub, a **"Compare & pull request"** banner appears. Click it, describe your change, submit.

### Keep Your Fork in Sync

```bash
git fetch upstream
git switch main
git merge upstream/main
git push origin main
```

---

## Part 6 — Collaboration

### Pull Requests — The Core Collaboration Unit

A PR is a proposal to merge one branch into another. In a team:

1. Developer creates a feature branch, pushes it
2. Opens a PR targeting `main`
3. Team reviews, comments, requests changes
4. Developer addresses feedback with more commits
5. PR approved → merged → branch deleted

PRs are a **GitHub feature** (not a Git feature). They add code review and discussion on top of Git's merge operation.

### Protected Branches

In real teams, nobody pushes directly to `main` — it's protected. All changes go through PRs.

**GitHub repo → Settings → Branches → Add protection rule:**
- Require pull request before merging
- Require at least 1 approving review
- Require CI checks to pass before merging

### GitHub Issues

Issues track bugs, features, and tasks. Every issue gets a number:

```bash
git commit -m "Fix login bug, closes #42"
```

When the PR merges, GitHub automatically closes issue #42.

### The GitHub Flow (Industry Standard)

```
1. Create branch from main
2. Write code, make commits
3. Push branch to GitHub
4. Open Pull Request
5. Review and discuss
6. Merge to main
7. CI/CD pipeline deploys automatically
```

This is the workflow used by most software teams globally. Every step maps to something you'll build in the Jenkins pipeline module.

---

## Quick Reference

### All Commands

```bash
# Setup
git config --global user.name "Name"
git config --global user.email "email"
git config --global credential.helper manager

# Repo
git init
git clone <url>
git remote add origin <url>
git remote -v

# Daily workflow
git status
git diff
git add .
git add <file>
git commit -m "message"
git push
git pull
git fetch

# Branches
git branch
git switch -c new-branch
git switch branch-name
git merge branch-name
git branch -d branch-name
git push origin branch-name
git push origin --delete branch-name

# History
git log
git log --oneline
git log --oneline --graph --all
git show <hash>

# Undoing
git restore <file>              # discard unstaged changes
git restore --staged <file>     # unstage
git reset HEAD~1                # undo last commit, keep files
git reset --hard HEAD~1         # undo last commit, nuke changes
git revert <hash>               # safe undo — creates new commit

# Stash (save WIP without committing)
git stash
git stash list
git stash pop

# Tags
git tag -a v1.0 -m "Release 1.0"
git push origin --tags
```

### Key Distinctions

| These seem similar... | But they're different because... |
|---|---|
| `git fetch` vs `git pull` | `fetch` downloads but doesn't apply. `pull` = fetch + merge. |
| `git merge` vs `git rebase` | `merge` preserves history. `rebase` rewrites for a linear look. |
| `git reset` vs `git revert` | `reset` rewrites history (local only). `revert` adds an undo commit (shared-safe). |
| Fork vs Clone | Fork = your GitHub copy. Clone = download to machine. You fork then clone. |
| PR vs Merge | PR is a GitHub review process. Merge is the Git operation. PRs result in merges. |

---

> **Next → 02 Docker** — your app is now version-controlled. The next module containerises it so it runs identically on any machine. You'll push a Docker image to Docker Hub the same way you just pushed code to GitHub.
