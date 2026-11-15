> **FILL-IN INSTRUCTIONS (delete this block when done):** This file has one-time placeholders written like `<THIS>`. Search the file for `<` and replace each with your real value. Items marked [CONFIRM] are assumptions to check. Do not start a Devin session that relies on this file until no placeholders remain.

# DEVIN.md: Master Playbook for the Modernization Rewrite

Attach or commit this for every Devin session. It says **how Devin works**. `AGENTS.md` says **what the code must look like**. Read both at the start of every session. Each step has its own playbook in the `playbooks/` folder of the kit.

## Mission

Rebuild one slice of the legacy application as a modular Spring Boot service in a **new repo**. Preserve the approved business rules. Replace the UI, database model, structure and architecture as the approved ADRs say. The legacy system is the oracle for business rules; the approved spec says which rules are PRESERVE, CHANGE or RETIRE.

Correctness means: recorded legacy inputs and approved rule examples replay through the new code and give the same results, the Oracle suites pass in CI, and every rule traces to an approved decision.

## Session start checklist (every session)

1. Read `AGENTS.md` in full. Then read `docs/steering/`, `docs/adr/` and the `home` repo `docs/design/`. Sessions do not remember each other; these files are the shared memory.
2. Read the task: which slice or ticket, and which step (see the table below). Do **only** that step.
3. Confirm the inputs the step needs exist. If any are missing, stop and report before doing anything else.
4. Restate the scope back in two or three lines: slice or ticket, step, and what "done" means. Then begin.

## Steps and playbooks

| Step | Playbook file |
|---|---|
| Module boundaries | `01-validate-module-boundaries.md` |
| Target architecture | `02-propose-target-architecture.md` |
| Design validation | `03-validate-target-design.md` |
| Legacy to spec | `04-legacy-to-spec.md` |
| Keep, change or retire | `05-keep-change-retire.md` |
| Template JSON | `06-generate-template-json.md` |
| Oracle SQL | `07-generate-oracle-sql.md` |
| H2 test schema | `08-derive-h2-schema.md` |
| Plan and tickets | `09-plan-and-tickets.md` |
| Write tests | `10-write-tests.md` |
| Implement a ticket | `11-implement-ticket.md` |
| Review | `12-review-against-spec.md` |
| Retro and handoff | `13-retro-and-handoff.md` |
| UI end-to-end and load (later) | `14-ui-e2e-and-load.md` |
| Architecture audit (periodic) | `15-architecture-audit.md` |

## Hard constraints (non-negotiable)

- **Legacy repos are read-only.** No commits, branches or PRs against them.
- **No Oracle in the sandbox.** No Docker, no Testcontainers, no local database installs. H2 is for sandbox tests only. Oracle behavior is verified by the CI suites.
- **No legacy imports.** Do not copy legacy classes or packages. Re-implement from the approved spec.
- **Never modify fixtures, contracts or expected values to make tests pass.** Report discrepancies instead.
- **Never weaken, skip or delete a failing test.**
- **Do not guess** the schema, SQL, business rules or decisions. Ask.
- **Do not decide** PRESERVE, CHANGE or RETIRE. Humans decide.
- Do not create, merge or split modules or services without an approved ADR.
- One slice or ticket per session. If scope grows, stop and propose a split.

## Handling blockers

Stop and ask (post in the session or the PR) when:

- The approved DB model, a table, column, query or proc signature is missing or unclear
- A fixture contradicts the spec or another fixture
- A rule you depend on is AMBER or RED, or has no approved decision
- Legacy behavior is ambiguous or looks like a defect and no decision exists
- A boundary, contract or data ownership change has no approved ADR
- The Oracle suite fails in CI and you cannot explain it
- You are tempted to work around an environment limit

Blocker report format:

```
BLOCKER
Slice or ticket / step: <slice>, <step name>
What I was doing: <one line>
What is missing or contradictory: <specifics, with file:line or fixture name>
Options I see: <A, B>
My recommendation: <one line>
```

## Pull request requirements

Every PR description follows `.github/pull_request_template.md`, including: slice or ticket and step; what changed in two or three sentences; spec rule IDs and their decisions; test results (sandbox and CI Oracle); Oracle-specific constructs; H2 differences; provisional names; suspected legacy defects replicated on purpose; new dependencies; open questions and assumptions.

## Quality bar

- Small, readable diffs. Do not reformat unrelated files.
- Names reflect the business domain and `docs/steering/glossary.md`, not legacy table names.
- No `TODO` without a ticket. No commented-out code. No debug output.
- If in doubt about scope, do less and ask.

## Do not

- Start the next step without review of the previous one
- Merge your own PRs
- Run broad refactors across the repo
- Upgrade Java, Spring Boot or build plugins unless the task says so
- Add infrastructure (Docker files, containers, local databases) to get unstuck

## One-time settings

- Build command: `<BUILD COMMAND, e.g. ./mvnw verify>`
- CI pipeline name and where Devin can read its results: `<CI JOB NAME / LINK>` [CONFIRM that Devin can read CI results; if not, a human pastes failures into the session]
- Playbook attachment: [CONFIRM how many playbooks Devin attaches per session. If only one, each step playbook already starts with "Read AGENTS.md and DEVIN.md first."]
