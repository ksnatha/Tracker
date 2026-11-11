> **FILL-IN INSTRUCTIONS (delete this block when done):** This file has one-time placeholders written like `<THIS>`. Search the file for `<` and replace each with your real value. Items marked [CONFIRM] are assumptions to check. Do not start a Devin session that relies on this file until no placeholders remain.

# Devin Modernization Kit

A complete document set for rebuilding a legacy Java and Spring Boot application as modular services with Devin, using ideas from the AI Hero skills. The skills are not installed; their methods are written into the playbooks.

## What is in the kit

| Folder | What it holds |
|---|---|
| `guides/00-workflow.md` | The sequence, approvals and file map. Start here. |
| `guides/test-strategy.md` | Test layers, where each runs, coverage and data rules |
| `guides/reviewer-guide.md` | How humans review each stage |
| `service-repo/` | Files for each service repo: `AGENTS.md`, `DEVIN.md`, PR template, steering files, DB naming, ADR templates |
| `home-repo/` | Files for the docs home repo: README, AGENTS.md, design, gaps, questions, plans, tickets, retros |
| `playbooks/` | Fifteen playbooks, one per Devin session type |

## Fill-in checklist (do this first)

1. Search every file for `<` and replace the placeholders. Files with one-time placeholders start with a FILL-IN block.
2. Settle the [CONFIRM] items:
   - Oracle version and `COMPATIBLE` setting (assumed 21, JSON type not enabled, so CLOB)
   - Java, Spring Boot and build tool versions; build command; formatter and static analysis config
   - Coverage thresholds, UI test tool, load tool
   - DBA naming conventions (`service-repo/docs/db/naming-conventions.md` stays PROVISIONAL until then)
   - Whether Devin can read CI results, and how many playbooks Devin attaches per session
   - Markdown tickets in the home repo, or an issue tracker
3. Create the repos named in `home-repo/README.md` and copy `service-repo/` files into each service repo and `home-repo/` files into the home repo.
4. Put `playbooks/` content into Devin Playbooks (or Knowledge), one per step.
5. Add the parked items to your backlog: the closed-record snapshot feed and parallel-operation rules are not covered by this kit.

## How to use it

### 1. Set up once

1. Fill in the placeholders and settle the [CONFIRM] items (checklist above).
2. Create the home repo and the service repo(s). Copy the kit files in: `home-repo/` into the home repo, `service-repo/` into each service repo. Copy `guides/test-strategy.md` to `docs/design/test-strategy.md` in the home repo.
3. Give Devin access to: the home repo, the service repo(s), `contracts`, and the legacy repos (backend and UI) as **read-only**.
4. Create one Devin Playbook per file in `playbooks/`. Names: `01-validate-module-boundaries`, `02-propose-target-architecture`, and so on. If Devin attaches only one playbook per session, that is fine: every playbook starts with "read AGENTS.md and DEVIN.md first".

### 2. Run a step (the same way every time)

1. Pick the step from `guides/00-workflow.md`.
2. Start a **new Devin session**. Attach the playbook for that step.
3. Fill the per-run placeholders in the playbook (slice, section, ticket, paths) and give the exact paths of the input documents.
4. Check that Devin restates the scope in two or three lines before it starts. Correct it if wrong.
5. Let it run. It stops at the end of the step and opens a PR or commits documents.
6. Review using `guides/reviewer-guide.md`. Approve by merging, or send it back with comments.
7. Only then start the next step. Nothing downstream relies on a document that is not approved.

Example message to start a session:

```
Use the playbook "04-legacy-to-spec". Slice: loan submission.
Entry points: LoanController#submitApplication (POST /loans/applications).
Legacy backend: <repo>, legacy UI: <repo> (both read-only).
Output: docs/specs/loan-submission.md in <service repo>.
```

### 3. The order for your first weeks

1. Run playbooks 01 and 02 (boundaries, then target architecture options). Decide, and approve the ADRs.
2. Run playbook 03 to validate the design against hard legacy cases.
3. Pick one questionnaire section that mixes dedicated and EAV fields. Run playbook 04 on it, review (AMBER and RED first), run 05 for the decisions, then 06 for the template JSON.
4. Run 07 (Oracle SQL), wait for DBA review and CI, then 08 (H2 schema).
5. Run 09 (plan and tickets), then 10 (tests), then 11 (one ticket per session) and 12 (review in a fresh session).
6. After the first slice works end to end, run 13 (retro), fix the docs and playbooks, then repeat steps 3 to 5 for the other sections and services.
7. Run 14 (UI end-to-end and load) once the UI and API are stable. Run 15 every few features.

### 4. Working rules that keep it on track

- One step and one slice or ticket per session. If scope grows, split.
- Devin proposes and humans decide. Do not let it approve ADRs, decisions or merges.
- Keep all outputs in the repos, not in chat. Sessions do not remember each other.
- If Devin stops with a BLOCKER report, answer it in the session or the PR. Do not tell it to guess.
- After each feature, update the glossary, ADRs, steering files and playbooks from the retro.
- If Devin skips a step, invents behavior or leaves a placeholder in place, point it at `AGENTS.md` section 14 and `DEVIN.md` (blockers) and re-run the step.

## Not covered (parked)

- The feed that snapshots closed legacy records as JSON for display in the new app
- Parallel-operation rules while both apps run
