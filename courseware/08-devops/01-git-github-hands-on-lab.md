# Git & GitHub — Hands-On Lab
### EMS Team Project · 4 Teams × 4 Members · Half-Day Session (~3.5 hrs)

Companion practical to **Module 01 — Git and GitHub**. This lab is not about
writing Java — it's about living through a real fork → branch → commit →
PR → review → conflict → merge → sync cycle, on a real (if small) codebase,
with real teammates.

> **Starter project:** `ibm-ems-api-starter.zip` (Java 17 / Spring Boot 3 /
> Maven) — this is what you'll fork. Every concept below maps to something
> in that repo.

---

## Learning Objectives

By the end of this lab, every trainee will have personally:

- [ ] Forked a repository and cloned their fork locally
- [ ] Configured Git identity and verified remotes (`origin` + `upstream`)
- [ ] Created a branch following a naming convention
- [ ] Staged and committed changes with clear, conventional messages
- [ ] Used `git status`, `git diff`, and `git log` to inspect their own work
- [ ] Deliberately broken and then fixed things using `restore`, `restore --staged`,
      `commit --amend`, `stash`, `revert`, and `reset` (soft / mixed / hard)
- [ ] Pushed a branch and opened a Pull Request
- [ ] Reviewed a teammate's PR (commented, requested changes, approved)
- [ ] Hit a real merge conflict and resolved it correctly
- [ ] Rebased a feature branch onto an updated `main`, resolved a rebase
      conflict, squashed commits with interactive rebase, and force-pushed
      safely with `--force-with-lease`
- [ ] Opened a PR from a fork to an upstream repository
- [ ] Synced their fork with upstream after other teams merged
- [ ] Explained the difference between Git Flow and GitHub Flow

---

## 1. The Scenario

Your batch is building the **EMS (Employee Management System) API** — a
Spring Boot project with four domain entities: **Employees, Departments,
Projects, Jobs**.

- The class is split into **4 teams of 4**, one team per entity.
- Within each team, the **4 layers** of that entity are split one-per-person:
  **Model → Repository → Service → Controller**.
- **I (the trainer) own the central repository.** Every team **forks** it,
  works independently, and sends work back via **Pull Requests** — exactly
  the open-source contribution model from section 1.6 of the module.

```
                    ibm-ems-api  (trainer's repo — "upstream")
                          │
        ┌─────────────┬───┴────────┬─────────────┐
        ▼             ▼            ▼             ▼
   Team Employee  Team Department  Team Project  Team Job
    (fork)          (fork)          (fork)         (fork)
        │             │            │             │
   4 members     4 members     4 members     4 members
   (1 branch each, 1 file each)
```

### Team → Package → Ownership Map

| Team | Owns package | 4 members, 1 role each |
|------|--------------|--------------------------|
| **Team Employee** | `employee/` | Model Engineer · Repository Engineer · Service Engineer · Controller Engineer |
| **Team Department** | `department/` | Model Engineer · Repository Engineer · Service Engineer · Controller Engineer |
| **Team Project** | `project/` | Model Engineer · Repository Engineer · Service Engineer · Controller Engineer |
| **Team Job** | `job/` | Model Engineer · Repository Engineer · Service Engineer · Controller Engineer |

Each of the 16 trainees ends up owning exactly **one file** — that's what
keeps `git log` and PR diffs readable and lets everyone genuinely practice
Git, not watch one person drive.

### Branch naming (applies to everyone, all lab)

```
feature/<entity>-<layer>
```
Examples: `feature/employee-model`, `feature/employee-service`,
`feature/department-controller`. This follows the convention table in
section 1.5 of the module.

---

## 2. Trainer Setup — Do This Before the Session

*(This section is for you, Shridhar — not the trainees.)*

1. **Unzip and push the starter project to your own GitHub repo:**
   ```bash
   unzip ibm-ems-api-starter.zip
   cd ibm-ems-api
   git init
   git add .
   git commit -m "Initial commit — EMS API starter for Git/GitHub lab"
   git branch -M main
   git remote add origin git@github.com:<your-username>/ibm-ems-api.git
   git push -u origin main
   ```

2. **Confirm the project builds** (needs internet access to Maven Central):
   ```bash
   mvn spring-boot:run
   ```

3. **Turn on branch protection on `main`** (GitHub → repo → Settings →
   Branches → Add rule):
   - ✅ Require a pull request before merging
   - ✅ Require at least 1 approving review
   - ✅ Do not allow bypassing the above settings

   This means even you can't push straight to `main` during the lab —
   good, it forces every team's contribution through a real PR, and it's
   the exact setting described in section 1.7 of the module.

4. **Share the repo URL** with the batch (chat/LMS) a few minutes before
   the session starts: `https://github.com/<your-username>/ibm-ems-api`

5. **Print or share** the Team → Package → Ownership Map from Section 1 so
   teams can self-assign roles instantly.

6. Have the **Trainer's Evaluation Rubric** (Section 12) open in a second
   window during the session for quick scoring as you walk around.

---

## 3. Trainee Pre-Work Checklist

Every trainee should arrive with this already done (verify in the first
5 minutes of the session — don't let this eat into lab time):

- [ ] Git installed — `git --version` works
- [ ] A personal GitHub account
- [ ] Git identity configured:
  ```bash
  git config --global user.name  "Your Name"
  git config --global user.email "you@example.com"
  ```
- [ ] SSH key generated and added to GitHub *(recommended — see module
      section 1.4; HTTPS works too if your account uses a Personal
      Access Token)*:
  ```bash
  ssh-keygen -t ed25519 -C "you@example.com"
  cat ~/.ssh/id_ed25519.pub    # paste into GitHub → Settings → SSH and GPG keys
  ssh -T git@github.com        # should greet you by username
  ```
- [ ] Java 17 and Maven installed — `java -version`, `mvn -version`

---

## 4. Session Timeline (≈ 3.5 hrs)

| # | Segment | Time |
|---|---------|------|
| — | Kickoff, team formation, role assignment | 10 min |
| — | Trainer walkthrough of starter repo structure | 10 min |
| Ex 1 | Fork & Clone | 15 min |
| Ex 2 | Explore Your Territory | 10 min |
| Ex 3 | Claim Your Branch | 10 min |
| Ex 4 | Make Your Mark (implement + commit workflow) | 35 min |
| Ex 5 | Oops Lab (undo playbook) | 30 min |
| — | Break | 10 min |
| Ex 6 | Push & Intra-Team PR + Code Review | 20 min |
| Ex 7 | Conflict Lab | 20 min |
| Ex 8 | Ship It — PR to Upstream | 15 min |
| Ex 9 | Sync Your Fork | 10 min |
| — | Wrap-up, stretch topics, Q&A | 15 min |
| | **Total** | **~210 min** |

---

## Exercise 1 — Fork & Clone (15 min)

**Concepts:** forking, cloning, remotes (`origin` vs `upstream`)

1. Every trainee opens the trainer's repo URL and clicks **Fork** (top
   right). This creates *your own personal copy* under your GitHub
   account.

   > Only **one fork per team is actually needed** for the shared team
   > repo — decide as a team who forks it (suggestion: whoever has the
   > tidiest GitHub username wins). That one fork becomes **your team's
   > shared repository** for the rest of the lab.

2. The team member who forked shares the fork URL with their 3 teammates.

3. **Every team member clones the team's fork** (not the trainer's repo):
   ```bash
   git clone git@github.com:<team-fork-owner>/ibm-ems-api.git
   cd ibm-ems-api
   ```

4. Add the trainer's repo as `upstream`:
   ```bash
   git remote add upstream git@github.com:<trainer-username>/ibm-ems-api.git
   git remote -v
   # origin    → your team's fork   (fetch/push)
   # upstream  → trainer's repo     (fetch only, for now)
   ```

**Checkpoint:** everyone on the team runs `git remote -v` and sees both
`origin` and `upstream` correctly pointed.

---

## Exercise 2 — Explore Your Territory (10 min)

**Concepts:** `git log`, `git status`, `.gitignore`, project structure

1. Look at the commit history you just inherited:
   ```bash
   git log --oneline --graph
   ```
2. Confirm `.gitignore` is doing its job — build the project once, then:
   ```bash
   mvn compile
   git status
   # target/ should NOT show up as untracked
   ```
3. Find your assigned file. Example, if you're the Model Engineer on Team
   Employee:
   ```bash
   src/main/java/com/ibm/training/ems/employee/model/Employee.java
   ```
4. Read the `TODO (Lab)` comment at the top of your file — that's your
   assignment for Exercise 4.
5. Find your team's shared file:
   ```bash
   changelogs/<your-entity>-CHANGELOG.md
   ```
   Leave it alone for now — you'll touch it in Exercise 4, and it matters
   a lot in Exercise 7.

---

## Exercise 3 — Claim Your Branch (10 min)

**Concepts:** branch naming conventions, `git switch -c`

Everyone on the team branches off `main` **at the same time**, before
anyone commits anything. This matters — it's what makes Exercise 7 work
correctly later.

```bash
git checkout main
git pull origin main
git switch -c feature/<entity>-<layer>
# e.g. git switch -c feature/employee-model
```

**Checkpoint:** `git branch` shows your new branch with a `*` next to it.
Confirm as a team that all 4 of you branched from the *same* starting
commit (`git log -1 --oneline` should match across all 4 members).

---

## Exercise 4 — Make Your Mark (35 min)

**Concepts:** staging, committing, commit message quality, `git diff`

Work only inside your own assigned file — model, repository, service, or
controller — plus one shared step at the end.

1. Open your file and implement the `TODO (Lab)` change. Keep it small —
   this is a Git lab, not a coding sprint.
2. Check your work before staging:
   ```bash
   git status
   git diff
   ```
3. Stage and commit **in at least two separate commits** (so you have a
   real history to look at later) — for example, one commit for the field/
   method addition, a second for a small refinement:
   ```bash
   git add <your-file>
   git commit -m "Add phoneNumber field to Employee model"
   # ...make a small follow-up tweak...
   git add <your-file>
   git commit -m "Add pattern validation for phoneNumber"
   ```
   Follow the commit message convention from section 1.2: imperative
   mood, under 72 characters, explain **what** changed.

4. **Now touch the shared file.** Add exactly one line to your team's
   changelog, at the bottom of the `## Unreleased` list:
   ```bash
   # edit changelogs/<entity>-CHANGELOG.md, add your line, then:
   git add changelogs/<entity>-CHANGELOG.md
   git commit -m "Add changelog entry for phoneNumber field"
   ```

   > ⚠️ **Do not run `git pull` again after this step.** Stay on your
   > branch, at your current commit, until Exercise 6. This is
   > intentional — see Exercise 7.

5. Review your own history:
   ```bash
   git log --oneline
   ```

**Checkpoint:** each trainee has 3 commits (file change ×2 + changelog ×1)
on their own branch, and has *not* pulled `main` since branching.

---

## Exercise 5 — Oops Lab: The Undo Playbook (30 min)

**Concepts:** `restore`, `restore --staged`, `commit --amend`, `stash`,
`revert`, `reset --soft/--mixed/--hard`

Work through each mini-scenario **on your own branch**, in a scratch file
so nothing here touches your real Exercise 4 work. Create a throwaway file
first:

```bash
echo "scratch content" > scratch.txt
git add scratch.txt
git commit -m "Add scratch file for undo lab"
```

Now run through all seven scenarios:

**1. Discard an unstaged edit**
```bash
echo "oops, wrong edit" >> scratch.txt
git status              # see the change
git restore scratch.txt # throw it away
cat scratch.txt          # confirm it's back to original
```

**2. Unstage a file you weren't ready to commit**
```bash
echo "half-finished idea" >> scratch.txt
git add scratch.txt
git restore --staged scratch.txt   # unstage, keep the edit
git status
```

**3. Fix your last commit message (not yet pushed)**
```bash
git add scratch.txt
git commit -m "tpyo in this msg"
git commit --amend -m "Fix scratch file content"
git log --oneline -1
```

**4. Stash mid-work to switch branches**
```bash
echo "in progress, not ready" >> scratch.txt
git stash
git checkout main
git checkout -           # jump back to your feature branch
git stash pop
cat scratch.txt          # your work is back
```

**5. Revert a commit safely (as if it were already pushed)**
```bash
git commit -am "Introduce a deliberate mistake in scratch.txt"
git revert HEAD --no-edit
git log --oneline -3      # note: revert ADDS a new commit, doesn't erase history
```

**6. Reset — try all three modes on a copy of your branch**
```bash
git branch reset-practice     # a disposable copy of your current branch
git switch reset-practice

git log --oneline             # note a commit hash from a few commits back, call it <hash>

git reset --soft <hash>       # changes staged, nothing lost
git status

git reset <hash>              # mixed (default) — changes unstaged
git status

git reset --hard <hash>       # ⚠️ destructive — changes gone
git status

git switch feature/<entity>-<layer>   # back to your real branch
git branch -D reset-practice           # clean up the scratch branch
```

**7. Recover a deleted file**
```bash
rm scratch.txt
git status
git restore scratch.txt       # recovers from the last commit
```

Finally, clean up the scratch file from your real branch entirely so it
doesn't pollute your PR:
```bash
git rm scratch.txt
git commit -m "Remove scratch file used for undo lab"
```

**Checkpoint:** everyone can explain, in one sentence each, the difference
between `restore`, `revert`, and `reset --hard`. Trainer spot-checks 2–3
trainees per team.

---

## Exercise 6 — Push & Intra-Team PR + Code Review (20 min)

**Concepts:** `git push`, Pull Requests, code review etiquette

1. Push your branch to your team's fork:
   ```bash
   git push origin feature/<entity>-<layer>
   ```
2. On GitHub, open a Pull Request **from your branch into your team fork's
   `main`** (not the trainer's repo yet).
3. In the PR description, write **what** you changed and **why** —
   one or two lines is enough.
4. **Swap with a teammate**: review each other's PR.
   - Comment on at least one specific line (click the `+` next to it)
   - Use **Approve** if it looks good, or **Request changes** if not
   - Never approve a PR you didn't actually read
5. Once approved, **merge your own PR** into your team fork's `main`.

Repeat until all 4 team members have merged — **but stop and read
Exercise 7 before your 3rd or 4th teammate merges.**

**Checkpoint:** team fork's `main` has all 4 members' code merged, each
via its own reviewed PR (check the PR list in the "Pull requests" tab —
should show 4 merged PRs).

---

## Exercise 7 — Conflict Lab (20 min)

**Concepts:** merge conflicts, conflict markers, manual resolution

Here's why we told you not to `pull` after Exercise 4: **all four of you
edited the same file** — `changelogs/<entity>-CHANGELOG.md` — on branches
that all started from the same commit. As your teammates' PRs get merged
one after another in Exercise 6, the 2nd, 3rd, and 4th merges are likely
to hit a **real merge conflict** on that file. If yours didn't conflict
automatically, do this deliberately:

1. Pick two team members who haven't merged their changelog edit yet.
2. Both open a PR into the team fork's `main` at the same time.
3. Merge the **first** PR — goes through cleanly.
4. Try to merge the **second** PR — GitHub will show:
   ```
   This branch has conflicts that must be resolved
   ```
5. Resolve it **locally** (more realistic than the GitHub web editor):
   ```bash
   git checkout feature/<entity>-<layer>
   git fetch origin
   git merge origin/main
   ```
   Git will mark the conflict directly in the file:
   ```
   <<<<<<< HEAD
   - [Priya] — Added a validation TODO note
   =======
   - [Arjun] — Added a new field note
   >>>>>>> origin/main
   ```
6. Edit the file by hand to keep **both** lines (this is almost always the
   right call for a changelog — nobody's work should disappear):
   ```
   - [Priya] — Added a validation TODO note
   - [Arjun] — Added a new field note
   ```
7. Mark it resolved and commit:
   ```bash
   git add changelogs/<entity>-CHANGELOG.md
   git commit -m "Merge main into feature branch — resolve changelog conflict"
   git push origin feature/<entity>-<layer>
   ```
8. Go back to the PR on GitHub — it should now show as mergeable. Merge it.

**Checkpoint:** every team can point to one real conflict marker
(`<<<<<<<` / `=======` / `>>>>>>>`) they personally resolved, and explain
why deleting one side instead of keeping both would have been the wrong
call here.

---

## Exercise 7.5 — Rebase Lab: Clean Up Before You Ship (20 min)

**Concepts:** `git rebase`, replaying commits, linear history vs merge
commits, interactive rebase (`squash`), the "never rebase shared commits"
rule

So far every sync you've done (`merge origin/main`, `fetch` + `merge
upstream/main`) has left a merge commit behind. Rebase does the same
job — bring your branch up to date — but rewrites your commits to sit
**on top of** the latest `main` instead, so the history reads as one
straight line instead of two branches tied together.

1. Make sure your feature branch is a few commits behind `main` (it
   should be, after Exercise 7's merges landed):
   ```bash
   git checkout feature/<entity>-<layer>
   git fetch origin
   git log --oneline origin/main..HEAD      # your commits not yet on main
   git log --oneline HEAD..origin/main      # main's commits you don't have
   ```

2. Instead of merging, rebase your branch onto the updated `main`:
   ```bash
   git rebase origin/main
   ```
   Git detaches your commits, fast-forwards your branch to `origin/main`,
   then replays your commits one at a time on top.

3. If you touched `changelogs/<entity>-CHANGELOG.md` again, you'll likely
   see the same conflict shape as Exercise 7 — but the workflow differs:
   ```
   <<<<<<< HEAD (main's version, since rebase reverses the usual sides)
   - [Arjun] — Added a new field note
   =======
   - [Priya] — Another update from feature branch
   >>>>>>> Your commit message
   ```
   Resolve it in the file, then instead of `git commit`, use:
   ```bash
   git add changelogs/<entity>-CHANGELOG.md
   git rebase --continue
   ```
   Repeat for each commit that conflicts. If it all goes wrong, bail out
   cleanly with:
   ```bash
   git rebase --abort
   ```

4. Compare the two histories side by side:
   ```bash
   git log --oneline --graph feature/<entity>-<layer>   # straight line now
   git log --oneline --graph main                        # has the merge commits from Exercise 7
   ```

5. **Interactive rebase — tidy your commits before opening a PR.** Squash
   your last 3 commits on this branch into one clean commit:
   ```bash
   git rebase -i HEAD~3
   ```
   In the editor, leave the first commit as `pick` and change the other
   two to `squash` (or `s`), save, then write one clear combined commit
   message when prompted.

6. Push your rewritten branch. Since the commit hashes changed, a normal
   push will be rejected — you need a **force push**, but scoped safely:
   ```bash
   git push --force-with-lease origin feature/<entity>-<layer>
   ```
   `--force-with-lease` refuses to overwrite the remote branch if someone
   else pushed to it since you last fetched — always prefer it over plain
   `--force`.

**Checkpoint:** each trainee can show a `git log --oneline --graph` with
zero merge commits on their feature branch, and explain in one sentence
why they used `--force-with-lease` instead of `--force`.

> ⚠️ **Golden rule:** only rebase branches that are still local/yours,
> like the `feature/<entity>-<layer>` branch you alone are working on.
> **Never** rebase (or force-push) `main` or any branch your teammates
> have already pulled — it rewrites commit hashes and breaks everyone
> else's history. Rebase is a *before-you-share* tool; merge is the
> *shared-history* tool.

---

## Exercise 8 — Ship It: PR to Upstream (15 min)

**Concepts:** fork → upstream contribution workflow, protected branches

1. The team fork's `main` now has all 4 members' work merged. Time to
   send it back to the trainer's repo — exactly the workflow from section
   1.6 of the module.

2. One team member (the "team lead" for this step) opens a PR:
   - **Base repository:** trainer's `ibm-ems-api`, branch `main`
   - **Head repository:** your team's fork, branch `main`

3. Title it clearly, e.g. `Team Employee — employee module complete`, and
   summarize what all 4 members contributed.

4. The trainer reviews the PR live (or async) and merges it — this will
   only work because of the branch protection rule set up in Section 2 (PR
   + 1 approval required), so the trainer approves and merges.

**Checkpoint:** trainer's repo `main` now contains one team's completed
package. Repeat for all 4 teams (can happen in parallel).

---

## Exercise 9 — Sync Your Fork (10 min)

**Concepts:** keeping a fork updated, `fetch` + `merge` from upstream

Once a few teams' PRs are merged into the trainer's repo, **everyone**
pulls that combined progress into their own fork — this is the "keeping
your fork up to date" workflow from section 1.6.

```bash
git checkout main
git fetch upstream
git merge upstream/main
git push origin main
```

**Checkpoint:** run `ls src/main/java/com/ibm/training/ems/` — you should
now see **all four** entity packages (`employee/`, `department/`,
`project/`, `job/`) in your local copy, not just your own team's, proving
the full class successfully collaborated on one shared codebase.

---

## Stretch Goals (if time remains)

- **Tag a release:** `git tag -a v1.0 -m "EMS API v1.0 — all 4 modules"`
  then `git push origin v1.0`
- **Protect your own fork's `main`** the same way the trainer did in
  Section 2 — try pushing directly to `main` afterward and watch GitHub
  block it
- **Git Flow vs GitHub Flow discussion:** which one did this lab actually
  follow? *(Answer: GitHub Flow — no `develop`/`release` branches, just
  `main` + short-lived `feature/*` branches merged via reviewed PRs.)*
  Discuss as a class when Git Flow's extra branches (`develop`,
  `release/*`, `hotfix/*`) would actually earn their complexity.

---

## Completion Checklist (self-tick)

- [ ] Forked and cloned a repository
- [ ] Verified `origin` and `upstream` remotes
- [ ] Created a branch using the naming convention
- [ ] Made 3+ commits with clear messages
- [ ] Used `git restore` to discard an edit
- [ ] Used `git restore --staged` to unstage a file
- [ ] Used `git commit --amend` to fix a commit message
- [ ] Used `git stash` and `git stash pop`
- [ ] Used `git revert` on a commit
- [ ] Used `git reset --soft`, `--mixed`, and `--hard`
- [ ] Recovered a deleted file with `git restore`
- [ ] Pushed a branch and opened a PR
- [ ] Reviewed a teammate's PR (comment + approve/request changes)
- [ ] Resolved a real merge conflict by hand
- [ ] Opened a PR from a fork into an upstream repo
- [ ] Synced a fork with `fetch` + `merge` from upstream
- [ ] Can explain GitHub Flow vs Git Flow in one sentence each

---

## 12. Trainer's Evaluation Rubric

Quick scoring guide — walk the room during Exercises 4–9 and note this per
team (not per individual, except where marked):

| Signal | What good looks like |
|--------|------------------------|
| Commit messages | Imperative mood, specific, not "fix" / "update" |
| Commit granularity | 2+ commits per person, not one giant dump |
| Branch naming | Matches `feature/<entity>-<layer>` exactly |
| PR descriptions | States what changed and why, not left blank |
| Code review (per individual) | Left at least one real comment, didn't rubber-stamp |
| Conflict resolution | Kept both sides' intent, didn't blindly pick one |
| Upstream PR | Clean, summarizes all 4 members' work |
| Fork sync | All 4 packages visible locally after Exercise 9 |

---

## Common Pitfalls / FAQ

**"I forked the trainer's repo AND my teammate also forked it — now we
have two different forks."**
Pick one as the team's canonical fork immediately; everyone re-clones
from that one. Don't try to merge two independent forks mid-lab.

**"I pushed to `upstream` by accident and it was rejected."**
Expected — you don't have write access to the trainer's repo, only to
your own fork (`origin`). That's the whole point of the fork model.

**"My conflict resolution broke the file / has duplicate markers left in
it."**
Search the file for `<<<<<<<`, `=======`, `>>>>>>>` — if any remain, the
conflict wasn't fully resolved. Clean them out, then `git add` + commit
again.

**"`git push` says I need to pull first."**
Someone else's PR merged into `main` while you were working. Run
`git fetch origin && git merge origin/main` on your branch, resolve any
conflicts, then push again.

**"I ran `git reset --hard` and lost work I actually needed."**
This is exactly why the Oops Lab has you practice it on a disposable
`scratch.txt` and a throwaway branch first. In real work, `git stash` or
`git revert` are almost always the safer move.

**"My `git push` was rejected after I rebased, even with `--force`."**
That's `--force-with-lease` doing its job — it means someone else pushed
new commits to that branch after you last fetched. Run `git fetch` first,
re-check `git log --oneline --graph`, and only force-push once you're sure
you're not erasing a teammate's work.

**"Should I merge or rebase to update my branch?"**
Rule of thumb from this lab: rebase your own private `feature/*` branch
before opening a PR (Exercise 7.5) to keep history clean; merge when
bringing shared/public history together, like syncing your fork in
Exercise 9. Never rebase a branch other people have already pulled.

---

## Command Reference (this lab)

| Command | What it does |
|---------|---------------|
| `git clone <url>` | Copy a remote repository locally |
| `git remote add upstream <url>` | Track the original repo you forked from |
| `git remote -v` | List configured remotes |
| `git switch -c <branch>` | Create and switch to a new branch |
| `git status` / `git diff` | See what's changed |
| `git add` / `git commit -m "msg"` | Stage and save a snapshot |
| `git log --oneline --graph` | View history |
| `git restore <file>` | Discard unstaged changes |
| `git restore --staged <file>` | Unstage a file |
| `git commit --amend` | Fix the last commit |
| `git stash` / `git stash pop` | Park and restore work in progress |
| `git revert <hash>` | Safely undo a pushed commit |
| `git reset --soft/--mixed/--hard <hash>` | Rewind history (increasing severity) |
| `git push origin <branch>` | Upload your branch |
| `git fetch upstream` / `git merge upstream/main` | Sync fork with the original repo |
| `git rebase origin/main` | Replay your commits on top of the latest main (linear history) |
| `git rebase --continue` / `--abort` | Resolve or bail out of a rebase in progress |
| `git rebase -i HEAD~N` | Interactively edit/squash the last N commits |
| `git push --force-with-lease origin <branch>` | Safely push a rebased branch |

---

**This lab covers every concept in Module 01, plus rebase and interactive
rebase (Exercise 7.5), and tags and branch protection as stretch goals.**
If your team finishes early, help another team debug their conflict —
that's real collaboration too.
