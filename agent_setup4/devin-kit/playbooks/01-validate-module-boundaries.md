> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 01: Validate Module Boundaries

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Based on the AI Hero codebase-design, domain-modeling, grilling and improve-codebase-architecture ideas. Run before or alongside playbook 03. The independent design is a second opinion for a human to weigh, not the answer.

## Goal

Check the target service and module boundaries by designing them independently from the legacy app, comparing the two designs, and recording the decisions as ADRs and gaps.

## Inputs

- Legacy repos (read-only): `<LEGACY BACKEND>`, `<LEGACY UI>`, and the Devin knowledge wiki
- `docs/steering/glossary.md` if it exists
- Constraints: Oracle `<VERSION>`; scale `<N QUESTIONS, N LOANS>`; integrations `<LIST>`; team ownership `<LIST>`
- Part B only: target architecture and DB model at `docs/design/architecture.md` and `docs/design/db-model.md` (add these to the repo only after Part A is approved)

## Where this runs and writes

Run this session in the **home repo**. Every path in this playbook is relative to the home repo root, so no path changes are needed.

- `docs/design/independent/modules.md` and `docs/design/independent/glossary-draft.md` (Part A)
- `docs/design/boundary-comparison.md` (Part B)
- `docs/design-gaps.md` (new rows, Part B)
- `docs/adr/NNNN-short-title.md`, status `proposed` (Part B)
- `docs/steering/glossary.md` (updates, Part B)

Before the first run, make sure `docs/adr/` and `docs/steering/` exist in the home repo (copy them from `service-repo/docs/` in the kit). Do not commit `architecture.md` or `db-model.md` until Part A is approved.

## Vocabulary

- Module: any unit with an interface, from a class to a service
- Interface: everything a caller or a test needs to know to use the module
- Deep module: a lot of behavior behind a small interface. Shallow: an interface nearly as complex as the implementation
- Seam: where behavior can be swapped or tested without editing the code around it
- Locality: change, bugs and knowledge stay in one place
- Dependency types: in-process, local-substitutable, remote-but-owned (another of our services), true-external

## Part A: Independent design (do not open the target design)

1. Do not read the target architecture or target DB model in Part A.
2. From the legacy code and wiki, list the business capabilities, the data each owns, the workflows and the integrations.
3. Compute evidence, do not only read code:
   - A table-to-code access matrix: which module reads and writes which tables
   - The package dependency graph and any cycles
   - Co-change history from git: which files change together
4. Domain language: find terms that mean different things in different places and different words for the same thing. Write `docs/design/independent/glossary-draft.md`.
5. Propose service and module boundaries. For each: purpose, interface (operations in and out), what it hides, data it owns, dependencies by type.
6. Design it twice: for the 2 or 3 hardest boundaries, produce at least three radically different options. Use parallel sub-agents if available. Compare on depth, locality, seam placement and testability. Pick one with reasons.
7. Write `docs/design/independent/modules.md` with GREEN, AMBER or RED confidence on each claim. Stop and wait for approval.

## Part B: Compare and validate (after Part A is approved)

1. Now read the target architecture and DB model.
2. Compare module by module: agree, differ, missing, extra. For each difference say which is better, with evidence (coupling, shared tables, transaction boundaries, co-change).
3. Check the target boundaries for: shallow modules; chatty calls between services; tables shared across services; distributed transactions; cyclic dependencies; services that must change together; business rules split across services; tables or rules with no clear owner.
4. Write `docs/design/boundary-comparison.md`: one row per module with target design, independent design, difference, recommendation, decision needed.
5. Add each real problem to `docs/design-gaps.md` with severity. Write each boundary decision as an ADR with status `proposed`. Update `docs/steering/glossary.md` for term conflicts.

## Rules

- Scope is module boundaries and domain language only. Do not redesign the DB schema, UI or template format.
- Do not redesign anything not asked for. Stop when the listed boundaries are covered.
- Never treat the independent design as correct by default. Present evidence; a human decides.
- Never invent legacy behavior. Mark anything unconfirmed as unknown.

## Done when

- Part A is committed and approved
- `boundary-comparison.md` is complete
- Every boundary decision has a `proposed` ADR and every problem is in the gap list
