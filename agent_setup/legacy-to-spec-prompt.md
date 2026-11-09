# Legacy-to-Spec Prompt Template

Fill in the `<PLACEHOLDERS>` and paste the **Prompt** section into a new Devin session. The output is a behavior spec, reviewed by a human before any tests or code are written.

---

## Prompt

```
You are doing STEP 1 (Legacy to Spec) for the slice "<SLICE_NAME>" of our monolith migration.

Read AGENTS.md and the Devin playbook first. This session produces documentation only. Do NOT write production code, tests, or migrations, and do NOT modify the legacy repo.

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

## Method
1. For each entry point, trace the full call path: controller -> service -> DAO/repository -> SQL -> stored procs/triggers. Follow into shared utilities and base classes when they affect behavior.
2. Document behavior as the code actually does it, not as comments or names suggest.
3. Cite evidence for every rule as legacy-repo `path/File.java:line`.
4. Label each rule Confirmed (read directly) or Inferred (reasoned). Never present an inference as fact.
5. If something cannot be determined from the code and files provided, list it under Open Questions. Do not guess. Do not fill gaps with typical behavior.
6. Do not judge or fix. If something looks like a bug, record it under Suspected Defects and still document what it does.

## Required output structure (docs/specs/<slice>.md)

### 1. Summary
Purpose of the slice in 3-5 sentences, entry points covered, and anything explicitly excluded.

### 2. Entry points
For each: type (REST / message / scheduled / internal), path or signature, auth requirements, request and response shape (fields, types, optionality, validation), success and error responses (status codes, body shapes, messages).

### 3. Business rules
Numbered rules with stable IDs (R-01, R-02, ...). Each rule: description, trigger/conditions, outcome, evidence (file:line), Confirmed/Inferred. Include calculations (formulas, rounding mode and scale, units, currency), state transitions, defaults, and derived fields.

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
Numbered, specific, answerable questions for a human (SME or DBA). Each states what is unknown, why it matters, and which rules depend on it.

## Quality checks before you finish
- Every rule has an ID and evidence. Nothing in sections 3-7 is unsourced.
- Every entry point appears in sections 2, 3, and 5.
- All stored procs/triggers referenced anywhere in the call paths are listed in section 6, or listed as unreadable in section 12.
- Open questions are specific, not generic.

## Finish
Commit only the spec file on a new branch in the new repo and open a PR titled "Spec: <slice>". In the PR description, summarize: number of rules, number of Oracle-specific items, number of open questions, and the three riskiest areas. Then STOP and wait for review.
```

---

## Notes for the reviewer

When reviewing the generated spec:

- **Spot-check evidence.** Pick 5 to 10 rules at random and verify the `file:line` citations actually say what the spec claims.
- **Chase the Oracle section.** Triggers and procs are where hidden rules live. Ask a DBA to confirm nothing is missing: query `ALL_TRIGGERS`, `ALL_SOURCE`, and dependency views for the slice's tables.
- **Answer open questions in the spec**, not in chat, so the next step starts from a single source of truth. Mark each answered question with who answered and when.
- **Compare against fixtures.** For a few recorded request/response pairs, walk through the spec by hand and confirm it predicts the recorded output. Gaps mean missing rules.
- **Approve explicitly.** Only after approval does the Tests step begin. Merge the spec so later sessions read it from the repo.

## Variants

- **Large slice:** run the prompt once per entry point, each writing `docs/specs/<slice>/<entry-point>.md`, then a final session to merge shared rules and the open-questions list into `docs/specs/<slice>.md`.
- **Batch or scheduled job:** add to the prompt: "Document input selection criteria, ordering, chunking and commit intervals, restart/failure behavior, and idempotency."
- **Heavy stored-proc logic:** add a separate session that documents only the PL/SQL, with the procedure source supplied as files, producing a line-by-line behavior description for section 6.
