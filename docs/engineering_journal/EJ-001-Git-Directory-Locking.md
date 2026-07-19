# EJ-001 - Git Directory Locking Issue

## Date

14 July 2026

---

# Incident Summary

While completing Sprint 2 of FocusGuard, Git repeatedly failed to delete directories during operations such as branch switching, branch deletion, and rebase cleanup.

Typical messages looked like:

```
Deletion of directory 'docs/adr' failed.
Should I try again? (y/n)
```

Despite these messages, the actual Git operations (checkout, branch deletion, etc.) eventually completed successfully.

---

# Background

During Sprint 2 we performed several advanced Git operations:

- Interactive rebase
- Commit splitting
- Commit amendment
- Branch cleanup
- Moving the repository outside OneDrive
- Creating and pushing remote branches

During these operations we encountered repeated directory deletion failures.

---

# Symptoms

Observed symptoms included:

- Git repeatedly asking:

```
Deletion of directory 'docs/adr' failed.
Should I try again? (y/n)
```

Later the same happened for:

- docs/learning
- docs/sprint_notes
- user/controller
- user/dto
- user/entity
- user/repository
- user/service
- db/migration

Sometimes after an interrupted checkout:

- files temporarily appeared deleted
- `git status` showed deleted files
- working tree looked corrupted

Fortunately, commits themselves were never lost.

---

# Investigation Timeline

## Hypothesis 1

### Repository corruption

Reason:

Files appeared deleted.

Actions:

- Examined git status.
- Checked git log.
- Verified commit history.

Result:

❌ Repository healthy.

---

## Hypothesis 2

### Interactive rebase corruption

Actions:

- Attempted rebase abort.
- Deleted rebase metadata manually.
- Restored repository.

Result:

Repository recovered successfully.

No commit history was lost.

---

## Hypothesis 3

### OneDrive locking files

Reason:

Repository originally lived inside OneDrive.

Actions:

- Copied repository to:

```
C:\Projects\focusguard
```

Result:

❌ Same problem occurred.

Conclusion:

Not caused by OneDrive.

---

## Hypothesis 4

### Old Git for Windows version

Original version:

```
2.53.0.windows.2
```

Updated to:

```
2.55.0.windows.3
```

Result:

❌ Problem still occurred.

Conclusion:

Git version alone was not the cause.

---

## Hypothesis 5

### Read-only directory attributes

Inspection showed:

```
attrib docs\adr
```

returned

```
A R
```

Actions:

```
attrib -R docs\adr /S /D
attrib -R docs\learning /S /D
attrib -R docs\sprint_notes /S /D
```

Result:

Read-only attribute removed.

Issue still occurred.

---

# Current Understanding

Evidence collected so far suggests:

- Git repository is healthy.
- Commit history is healthy.
- Branch switching succeeds.
- Git only fails while attempting to remove empty directories.

The remaining hypothesis is that some Windows process is keeping directory handles open during Git operations.

Future investigation will use Microsoft Sysinternals tools (Process Explorer or Handle.exe).

---

# Recovery Procedure

Whenever checkout left the repository looking inconsistent:

```
git restore .
```

successfully restored all tracked files.

This became the standard recovery procedure throughout the investigation.

---

# Commands Used

```
git status

git restore .

git log

git switch

git branch

git branch -f

git commit --amend

git reset --soft

git remote -v

git push

git remote set-url

ssh-keygen

ssh -T

attrib
```

---

# Lessons Learned

- Never panic when Git temporarily shows deleted files.
- Always inspect commit history before assuming data loss.
- `git restore .` is a valuable recovery command.
- Validate hypotheses systematically instead of guessing.
- Updating software does not necessarily resolve filesystem-level issues.
- Good debugging is based on evidence rather than assumptions.

---

# Open Questions

- Which Windows process is holding directory handles?
- Why does checkout succeed even when directory deletion fails?
- Can Process Explorer identify the locking process?

These questions will be investigated in a future Engineering Journal entry if required.

---

# Interview Talking Points

**Possible interview question**

> Tell me about a difficult engineering issue you investigated.

**Example answer**

While building FocusGuard, Git repeatedly failed to remove directories during branch switching. Rather than recreating the repository, I treated it like an engineering investigation. I verified repository integrity, ruled out rebase corruption, tested whether OneDrive was responsible by moving the repository, updated Git for Windows, examined filesystem attributes, and documented each hypothesis. Although the exact Windows process responsible remains under investigation, I confirmed the repository itself was healthy and established a safe recovery procedure using Git commands without risking data loss.

---

# Personal Takeaways

This incident reinforced an important lesson:

> Engineering is not just writing features. A significant part of software engineering is forming hypotheses, collecting evidence, eliminating incorrect assumptions, and documenting the investigation for future reference.

This journal entry captures that process so future debugging sessions can follow the same disciplined approach.