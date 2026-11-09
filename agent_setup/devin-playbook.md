# Devin Playbook: Legacy Monolith to Spring Boot Service Migration

Use this as a Devin playbook or knowledge entry (paste it in, or commit it as `DEVIN.md` in each new repo). It describes **how Devin should work**. The repo's `AGENTS.md` describes **what the code must look like**. Do not duplicate `AGENTS.md` here; read it at the start of every session and follow it.

Replace `<PLACEHOLDERS>` per repo.

---

## Mission

Re-implement one slice of the legacy monolith as a modular Spring Boot service in a **new repo**, reproducing legacy behavior exactly. The legacy system is the oracle. Correctness means: recorded legacy fixtures replay through the new code and produce the same results.

## Session start checklist (every session)

1. Read `AGENTS.md` in full.
2. Read the task's slice name and the step you are on (Spec, Tests, or Implement). Do **only** that step.
3. Confirm these inputs exist. If any are missing, stop and ask before doing anything else:
   - Spec step: read access to the legacy repo (read-only) and the entry points for the slice
   - Tests step: an approved `docs/specs/<slice>.md`, the `contracts` repo (OpenAPI + fixtures), sanitized fixtures for the slice
   - Implement step: failing contract and parity tests from the Tests step, plus the DDL/SQL files in `docs/legacy/`
4. Restate the scope back in two or three lines: slice, step, what "done" means. Then begin.

## Hard constraints (non-negotiable)

- **Legacy repo is read-only.** No commits, branches, or PRs against it.
- **No database access.** No Oracle, no Docker, no Testcontainers, no local DB installs. All persistence goes behind repository ports with in-memory fakes.
- **No legacy imports.** Do not copy legacy classes or packages. Re-implement from the spec.
- **Never modify fixtures or contracts to make tests pass.** Report discrepancies instead.
- **Never weaken, skip, or delete a failing test.**
- **Do not guess** schema, SQL, or business rules. Ask.
- **Do not "improve" behavior.** Replicate legacy, note suspected defects.
- One slice per session. If scope grows, stop and propose a split.

## Step 1: Legacy to Spec

**Goal:** a reviewable behavior document. No production code is written in this step.

Use the prompt template in `legacy-to-spec-prompt.md`. Output goes to `docs/specs/<slice>.md`.

Rules for this step:
- Trace from each entry point (controller, message handler, scheduled job) down through services, DAOs, SQL, and any stored procedures or triggers it touches.
- Record behavior as observed in code, with `file:line` references so a human can verify.
- Mark anything you could not determine as an **Open question**. Do not fill gaps with assumptions.
- Distinguish **Confirmed** (read directly in code) from **Inferred** (reasoned from code) in every rule.
- Stop when the spec is complete and open a PR containing only the spec. Wait for human review.

## Step 2: Spec and fixtures to tests

**Goal:** failing tests that encode the approved spec.

- Generate contract tests from the OpenAPI/event schemas in `contracts`.
- Write fixture-driven parity tests: load each recorded request/response (or service input/output) pair, run it through the new code with persistence ports faked from the DAO-level fixtures, and assert equality on the full response, including status codes, error bodies, ordering, rounding, and nulls.
- Add domain unit tests for each rule in the spec, referencing the spec rule ID in the test name or a comment (for example `R-12`).
- Create the minimal skeleton (interfaces, DTOs, empty implementations) needed to compile.
- Run the tests. **Confirm they fail for the right reason** (missing behavior, not a build or config error). Include the failure summary in the PR.
- Stop and wait for review.

## Step 3: Implement to green

**Goal:** passing contract and parity tests, with clean architecture.

- Implement in small increments. Run the affected tests after each one.
- Follow the package layout and dependency rules in `AGENTS.md`.
- Write the Oracle-backed adapter strictly from the supplied DDL and SQL. Tag its tests `@Tag("oracle-integration")`. They cannot run in your sandbox and will be verified in CI or the shared dev environment.
- Run `<BUILD COMMAND, e.g. ./mvnw verify>` plus formatting and static analysis before opening the PR.
- If a parity test fails and the cause is not clearly your bug, **stop**. Report which fixture, expected versus actual, and your hypothesis. Do not change the fixture.

## Handling blockers

Stop and ask (post in the session or PR) when:
- A needed table, column, query, or proc signature is missing
- A fixture contradicts the spec or another fixture
- Legacy behavior is ambiguous, or looks like a defect
- The only way forward is to touch the legacy repo, another service's database, or a shared contract
- You are tempted to work around an environment limit

Format of a blocker report:

```
BLOCKER
Slice / step: <slice>, <spec | tests | implement>
What I was doing: <one line>
What is missing or contradictory: <specifics, with file:line or fixture name>
Options I see: <A, B>
My recommendation: <one line>
```

## Pull request requirements

Every PR description includes:

- Slice and step
- What changed, in two or three sentences
- Spec rules covered (IDs), for tests and implementation PRs
- Test results (commands run and outcome)
- **Oracle-specific constructs used** (`MERGE`, `ROWNUM`, `CONNECT BY`, sequences, PL/SQL calls, and so on)
- **Suspected legacy defects** replicated on purpose
- New dependencies, with justification
- Open questions and assumptions (should be few; prefer asking)

## Quality bar

- Small, readable diffs. Do not reformat unrelated files.
- Names reflect the business domain, not the legacy table names.
- No `TODO` without a ticket. No commented-out code. No debug output.
- If in doubt about scope, do less and ask.

## Do not

- Start the next step without review of the previous one
- Merge your own PRs
- Run broad refactors across the repo
- Upgrade Java, Spring Boot, or build plugins unless the task says so
- Add new infrastructure (Docker files, containers, local databases) to get unstuck
