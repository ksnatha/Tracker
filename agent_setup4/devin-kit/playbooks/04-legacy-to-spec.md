> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 04: Legacy to Spec

You are doing the legacy-to-spec step for the slice `<SLICE_NAME>`. Read `AGENTS.md` (especially sections 6, 14 and 15), `DEVIN.md`, and everything in `docs/steering/` and `docs/adr/` first. This session produces documentation only. Do NOT write production code, tests or migrations, and do NOT modify the legacy repos.

This merges the existing legacy-to-spec prompt with the questionnaire additions: a field catalog, a target home for each rule, UI tracing, and a place for the preserve, change or retire decision.

## Inputs

- Legacy backend (READ-ONLY): `<REPO URL / PATH>`
- Legacy UI (READ-ONLY): `<REPO URL / PATH>`
- Entry points in scope:
  - `<e.g. LoanController#submitApplication  POST /loans/applications>`
  - `<e.g. LoanStatusService#recalculate (called by nightly job X)>`
  - `<add more>`
- Questionnaire sections in scope (if any): `<SECTION NAMES>`
- Out of scope (do not document, but note when the slice calls into them): `<MODULES / PACKAGES>`
- Schema, SQL and proc signatures in: `<docs/legacy/ path>`
- Recorded fixtures, if any: `<contracts repo path>`
- Output file: `docs/specs/<slice>.md`
- Shared context to read first: `docs/steering/`, `docs/adr/`, specs of neighboring slices

## Method

1. For each entry point, trace the full call path: controller, service, DAO or repository, SQL, stored procs and triggers. Follow into shared utilities and base classes when they affect behavior.
2. Trace the UI too: client-side validations, visibility and mandatory-if logic, computed fields, and dropdown sources.
3. Look in every place a rule can live: backend, UI, SQL, config tables, stored procs, triggers, scheduled jobs, and dead or unreachable code. List the places you searched.
4. Document behavior as the code does it, not as comments or names suggest.
5. Cite evidence for every rule as `path/File.java:line` (or UI file and line).
6. Mark every rule and fact GREEN (read directly, unambiguous), AMBER (inferred, or code clear but intent or an input is not) or RED (could not determine, or code looks contradictory). Never present an inference as fact.
7. Golden rules: never invent or assume behavior; if code is ambiguous say so and mark it AMBER or RED; never fill gaps with typical behavior; never silently drop an entry point, branch or condition. Anything you cannot determine goes under Open Questions.
8. Do not judge or fix. If something looks like a bug, record it under Suspected Defects and still document what it does.
9. Do not decide PRESERVE, CHANGE or RETIRE. Default every rule to PRESERVE (proposed). Humans decide in playbook 05.

## Required output structure (`docs/specs/<slice>.md`)

1. **Summary.** Purpose in 3 to 5 sentences, entry points covered, and anything excluded.
2. **Entry points.** For each: type (REST, message, scheduled, internal, UI action), path or signature, auth requirements, request and response shape (fields, types, optionality, validation), success and error responses.
3. **Field catalog** (for questionnaire or form slices). One entry per field: id, label as shown, data type, required or optional, length or format limits, dropdown source (static code_set, derived from another table, or external API), proposed storage (dedicated column or EAV, with a one-line reason), legacy source (file:line), confidence.
4. **Business rules.** Numbered stable IDs (R-01, R-02, ...). Each rule: description, trigger or conditions, outcome, evidence, confidence, **kind** (visibility, validation, mandatory-if, calculation, workflow or status transition, permission, dropdown source), **target home** (template JSON, strategy bean, workflow service, other), proposed decision (PRESERVE by default). Include calculations (formulas, rounding mode and scale, units, currency), state transitions, defaults and derived fields.
5. **Edge cases and error handling.** Null and empty handling, boundaries, ordering and tie-breaking, pagination defaults, duplicate or idempotent submissions, concurrency or locking, swallowed exceptions, error messages verbatim, retries, timeouts.
6. **Data access.** Per entry point: tables read and written, the exact SQL (or ORM query with its resolved intent), joins and filters, ordering, paging, sequences, and fetch or lazy-loading behavior that changes results.
7. **Oracle-specific logic.** Stored procs, functions, packages, triggers, views, materialized views, jobs, `MERGE`, `ROWNUM`, `CONNECT BY`, `DECODE`/`NVL` semantics, empty-string-equals-NULL reliance, `DATE` versus `TIMESTAMP`, implicit conversions, defaults and constraints enforced only in the DB. For each: what it does, who calls it, and a recommended handling (port to service code with verification, or keep behind an adapter), marked as a recommendation.
8. **Transactions and consistency.** Transaction boundaries, isolation or locking assumptions, operations that must be atomic together, anything that spans modules or would span services after the split.
9. **Dependencies and cross-module calls.** Other modules, shared libraries, external systems, files, queues, caches, scheduled jobs. For each: what is exchanged, whether synchronous, and the likely contract boundary.
10. **Cross-cutting behavior.** Security and authorization checks, auditing or logging with business meaning, feature flags, configuration properties that change behavior (with defaults), caching, date, time and time zone handling, locale.
11. **Suspected defects and oddities.** What it does and the evidence. No fixes proposed. Default recommendation: replicate until a decision says otherwise.
12. **Fixture and test recommendations.** Scenarios the recorded fixtures must cover: happy paths, each error path, each boundary, batch cases, and rules with no fixture coverage. For every calculation rule, give 5 to 10 worked input and output examples taken from the code or by running legacy, marked as derived (AMBER) where no recorded fixture exists. Do not guess outputs.
13. **Open questions.** Numbered, specific, answerable by an SME or DBA. Each states what is unknown, why it matters and which rules depend on it. Every AMBER or RED item appears here or says why it needs no answer.
14. **Review summary for experts.** A table of every AMBER and RED rule: rule ID, one-sentence plain-language statement, why uncertain, exact question for the expert. Reviewers start here.
15. **Entry point and field reconciliation.** A table of every entry point found in the slice (controllers, handlers, scheduled jobs, public service methods called from outside, UI actions) and every field, with status: documented (rule IDs), classified as technical plumbing, or open question. Reconcile against an independent source (routes, scheduler config, message listeners, form definitions, DB columns). Nothing may be missing.
16. **Decisions (left empty here).** A table for playbook 05 to fill: rule ID, decision (PRESERVE, CHANGE, RETIRE), reason, approver, date.

## Quality checks before you finish

- Every rule has an ID, evidence and a confidence marker. No RED rule is described as if known.
- Nothing in sections 3 to 8 is unsourced.
- Every entry point appears in sections 2, 4 and 6.
- Every stored proc or trigger referenced in a call path is in section 7, or listed as unreadable in section 13.
- Open questions are specific, not generic.
- Section 15 lists every entry point and field with no gaps.
- Any new cross-slice learning (glossary term, recurring quirk, convention) is added to `docs/steering/`.

## Finish

Commit the spec on a new branch (steering and ADR updates in separate commits) and open a PR titled "Spec: `<slice>`". In the description summarize: rules by confidence (GREEN, AMBER, RED), Oracle-specific items, open questions, entry points and fields reconciled, and the three riskiest areas. Then STOP and wait for review.

## Variants

- **Large slice:** run once per entry point, each writing `docs/specs/<slice>/<entry-point>.md`, then a final session to merge shared rules and open questions into `docs/specs/<slice>.md`.
- **Batch or scheduled job:** add: document input selection criteria, ordering, chunking and commit intervals, restart and failure behavior, and idempotency.
- **Heavy stored-proc logic:** run a separate session that documents only the PL/SQL (source supplied as files) with a line-by-line behavior description for section 7.
- **UI-heavy slice:** run a separate session on the legacy UI repo only, writing the visibility, validation and computed-field rules into sections 3 and 4.

## Reviewer notes

See the reviewer guide: review AMBER and RED first (section 14), spot-check 5 to 10 citations, have a DBA check triggers and procs, answer open questions in the spec, and freeze the slice during analysis.
