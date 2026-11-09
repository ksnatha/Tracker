# Legacy-to-Spec Prompt Template

Fill in the `<PLACEHOLDERS>` and paste the **Prompt** section into a new Devin session. The output is a behavior spec, reviewed by a human before any tests or code are written.

---

## Prompt

```
You are doing STEP 1 (Legacy to Spec) for the slice "<SLICE_NAME>" of our monolith migration.

Read AGENTS.md (especially sections 14 and 15), the Devin playbook, and everything in docs/steering/ and docs/adr/ first. This session produces documentation only. Do NOT write production code, tests, or migrations, and do NOT modify the legacy repo.

## Inputs
- Legacy repo (READ-ONLY): <REPO URL / PATH>
- Entry points in scope:
  - <e.g. LoanController#submitApplication  POST /loans/applications>
  - <e.g. LoanStatusService#recalculate  (called by nightly job X)>
  - <add more>
- Out of scope (do not document, but note when the slice calls into them): <MODULES / PACKAGES>
- Schema, SQL, proc signatures provided in: <docs/legacy/ path>
- Recorded fixtures, if available: <contracts repo path>
- Output file: docs/specs/<slice>.md in the new repo
- Shared context to read first: docs/steering/, docs/adr/, specs of neighboring slices in docs/specs/

## Method
1. For each entry point, trace the full call path: controller -> service -> DAO/repository -> SQL -> stored procs/triggers. Follow into shared utilities and base classes when they affect behavior.
2. Document behavior as the code actually does it, not as comments or names suggest.
3. Cite evidence for every rule as legacy-repo `path/File.java:line`.
4. Mark every rule and fact with a confidence marker: GREEN (read directly, unambiguous), AMBER (inferred, or code clear but intent or an input is not), RED (could not determine, or code looks contradictory). Never present an inference as fact.
5. Golden rules: never invent or assume behavior; if code is ambiguous, say so in the output and mark it AMBER or RED; never fill gaps with typical behavior; never silently drop an entry point, branch, or condition. Anything you cannot determine goes under Open Questions.
6. Do not judge or fix. If something looks like a bug, record it under Suspected Defects and still document what it does.

## Required output structure (docs/specs/<slice>.md)

### 1. Summary
Purpose of the slice in 3-5 sentences, entry points covered, and anything explicitly excluded.

### 2. Entry points
For each: type (REST / message / scheduled / internal), path or signature, auth requirements, request and response shape (fields, types, optionality, validation), success and error responses (status codes, body shapes, messages).

### 3. Business rules
Numbered rules with stable IDs (R-01, R-02, ...). Each rule: description, trigger/conditions, outcome, evidence (file:line), confidence (GREEN/AMBER/RED). Include calculations (formulas, rounding mode and scale, units, currency), state transitions, defaults, and derived fields.

### 4. Edge cases and error handling
Null/empty handling, boundary values, ordering and tie-breaking, pagination defaults, duplicate or idempotent submissions, concurrency or locking behavior, exceptions caught or swallowed, error messages returned verbatim, retries, timeouts.

### 5. Data access
Per entry point: tables read and written, the exact SQL (or ORM query with its resolved intent), join and filter logic, ordering, paging approach, sequences used, and fetch/lazy-loading behavior that changes results.

### 6. Oracle-specific logic
Stored procedures, functions, packages, triggers, views, materialized views, database jobs, `MERGE`, `ROWNUM`, `CONNECT BY`, `DECODE`/`NVL` semantics, empty-string-equals-NULL reliance, `DATE` vs `TIMESTAMP` handling, implicit conversions, default values and constraints enforced only in the DB. For each: what it does, who calls it, and a recommended handling (port to service code with fixture verification, or keep behind an adapter), marked as a recommendation.

### 7. Transactions and consistency
Transaction boundaries (annotations, programmatic), isolation or locking assumptions, operations that must be atomic together, anything that spans modules or would span services after the split.

### 8. Dependencies and cross-module calls
Calls to other modules, shared libraries, external systems, files, queues, caches, and scheduled jobs. For each: what is exchanged, whether it is synchronous, and the likely contract boundary.

### 9. Cross-cutting behavior
Security and authorization checks, auditing/logging that has business meaning, feature flags, configuration properties that change behavior (with default values), caching, date/time and timezone handling, locale.

### 10. Suspected defects and oddities
Behavior that looks wrong or surprising. Describe what it does and the evidence. Do not propose fixes here. Default recommendation: replicate.

### 11. Fixture and test recommendations
Scenarios the recorded fixtures must cover to give confidence: happy paths, each error path, each boundary and edge case above, batch/month-end/year-end cases, and any rule with no fixture coverage so far. List missing fixtures to capture.

### 12. Open questions
Numbered, specific, answerable questions for a human (SME or DBA). Each states what is unknown, why it matters, and which rules depend on it. Every AMBER or RED item must appear here or have a clear reason it does not need an answer.

### 13. Review summary for experts
A short table listing every AMBER and RED rule with: rule ID, one-sentence plain-language statement, why it is uncertain, and the exact question for the expert. Reviewers start here.

### 14. Entry point reconciliation
A table of every entry point found in the slice's legacy code (controllers, handlers, scheduled jobs, public service methods called from outside the slice) with its status: documented (rule IDs), classified as technical plumbing, or open question. Nothing may be missing from this table.

## Quality checks before you finish
- Every rule has an ID and evidence. Nothing in sections 3-7 is unsourced.
- Every entry point appears in sections 2, 3, and 5.
- All stored procs/triggers referenced anywhere in the call paths are listed in section 6, or listed as unreadable in section 12.
- Open questions are specific, not generic.
- Every rule has a confidence marker. No RED rule is described as if it were known.
- Section 14 lists every entry point in the slice with no gaps (a rewritten module once lost endpoints that went unnoticed for a month).
- Any new cross-slice learning (glossary term, recurring quirk, convention) is added to docs/steering/.

## Finish
Commit the spec file (plus any docs/steering/ or docs/adr/ updates, kept in separate commits) on a new branch in the new repo and open a PR titled "Spec: <slice>". In the PR description, summarize: number of rules by confidence (GREEN/AMBER/RED), number of Oracle-specific items, number of open questions, entry points reconciled, and the three riskiest areas. Then STOP and wait for review.
```

---

## Notes for the reviewer

When reviewing the generated spec:

- **Review AMBER and RED first.** Start with section 13. Spend expert time on ambiguities, and only spot-check GREEN rules.
- **Watch review capacity.** Extraction can run in parallel, but if expert review capacity does not keep up, review becomes the bottleneck. Plan reviewer time before launching many slices.
- **Spot-check evidence.** Pick 5 to 10 rules at random and verify the `file:line` citations actually say what the spec claims.
- **Chase the Oracle section.** Triggers and procs are where hidden rules live. Ask a DBA to confirm nothing is missing: query `ALL_TRIGGERS`, `ALL_SOURCE`, and dependency views for the slice's tables.
- **Answer open questions in the spec**, not in chat, so the next step starts from a single source of truth. Mark each answered question with who answered and when.
- **Compare against fixtures.** For a few recorded request/response pairs, walk through the spec by hand and confirm it predicts the recorded output. Gaps mean missing rules.
- **Check entry point reconciliation.** Compare section 14 against an independent list (routes, scheduler config, message listeners). Missing items mean silently dropped behavior.
- **A complete spec can still be wrong.** It may faithfully carry forward a legacy bug or an obsolete rule. Ask experts specifically what should be retired or changed, and log each decision.
- **Freeze the slice during analysis.** Ask the legacy team to avoid changes to the slice's code during the analysis window, or re-run extraction on changed files.
- **Approve explicitly.** Only after approval does the Tests step begin. Merge the spec so later sessions read it from the repo.

## Variants

- **Large slice:** run the prompt once per entry point, each writing `docs/specs/<slice>/<entry-point>.md`, then a final session to merge shared rules and the open-questions list into `docs/specs/<slice>.md`.
- **Batch or scheduled job:** add to the prompt: "Document input selection criteria, ordering, chunking and commit intervals, restart/failure behavior, and idempotency."
- **Heavy stored-proc logic:** add a separate session that documents only the PL/SQL, with the procedure source supplied as files, producing a line-by-line behavior description for section 6.
