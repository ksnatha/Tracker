> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 01: Validate Module Boundaries

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run before or alongside playbook 03. Combines the AI Hero codebase-design, domain-modeling and grilling methods with evidence gathered from the legacy code. The independent design is a second opinion for a human to weigh, not the answer.

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

## Vocabulary and principles

Use these terms when reasoning about design. Say **module**, **interface** and **seam** instead of loose words. Use **service** only for a deployable unit, and keep **boundary** for the DDD bounded-context sense.

- **Module:** anything with an interface and an implementation. Scale-agnostic: a function, a class, a package, a service.
- **Interface:** everything a caller must know to use the module correctly: the signature, plus invariants, ordering constraints, error modes, required configuration and performance characteristics.
- **Depth:** leverage at the interface. Deep: a lot of behavior behind a small interface. Shallow: an interface nearly as complex as the implementation.
- **Seam:** a place where behavior can be changed without editing in that place; the location where a module interface lives.
- **Adapter:** a concrete thing that satisfies an interface at a seam (an Oracle repository and an in-memory fake are both adapters).
- **Leverage:** what callers get from depth. **Locality:** what maintainers get: change, bugs and knowledge stay in one place.

Principles:

- **Deletion test.** Imagine deleting the module. If complexity vanishes, it was a pass-through. If it reappears across N callers, it earns its place.
- **The interface is the test surface.** Callers and tests cross the same seam. If you must test past the interface, the module has the wrong shape.
- **One adapter means a hypothetical seam; two means a real one.** Do not cut a seam unless something varies across it (typically production plus test).

Dependency categories, which decide how a module is tested across its seam:

1. **In-process:** pure computation, no I/O. Merge and test through the interface.
2. **Local-substitutable:** has a local stand-in (for example H2 for the database). Test with the stand-in.
3. **Remote but owned:** another of our services. Define a port at the seam, an HTTP adapter for production and an in-memory adapter for tests.
4. **True external:** a third party. Inject a port; tests use a mock adapter.

## Part A: Independent design (do not open the target design)

1. Do not read the target architecture or target DB model in Part A.
2. From the legacy code and wiki, list the business capabilities, the data each owns, the workflows and the integrations.
3. Compute evidence, do not only read code:
   - A table-to-code access matrix: which module reads and writes which tables
   - The package dependency graph and any cycles
   - Co-change history from git: which files change together
   - Hot spots: the files that change most often, to decide which boundaries to examine first
4. Domain language. Write `docs/design/independent/glossary-draft.md`:
   - Find terms that mean different things in different places, and different words for the same thing
   - Propose a precise canonical term for each fuzzy one
   - Stress-test relationships with concrete edge-case scenarios
   - Cross-check what the code does against what the wiki or I say, and surface contradictions
   - Keep entries free of implementation details (no table, class or technology names)
5. Propose modules. For each: purpose, interface (operations, invariants, ordering, error modes), what it hides, data it owns, dependencies by category.
6. Apply the deletion test to every proposed module and record the result.
7. Design it twice for the 2 or 3 hardest modules:
   - First write a short problem-space note: the constraints, the dependencies and their category, and a rough illustrative sketch (not a proposal)
   - Then produce at least three radically different designs, each under a different constraint: (a) minimal interface, 1 to 3 entry points; (b) maximum flexibility; (c) optimize for the most common caller; (d) ports and adapters for cross-seam dependencies
   - Use one **child session** per design, each with a fresh context and given only the problem-space note and its one constraint. Each writes its design to `docs/design/independent/options/<module>-<constraint>.md`. You (the parent) read them, present them one at a time and compare them. If child sessions are not possible, produce the designs one after another in separate sections
   - Each design gives: the interface, a usage example, what it hides, the dependency strategy and adapters, and trade-offs
   - Present them one at a time, then compare on depth, locality and seam placement, and give an opinionated recommendation (or a hybrid)
8. Write `docs/design/independent/modules.md` with GREEN, AMBER or RED confidence on each claim. Stop and wait for approval.

## Part B: Compare and validate (after Part A is approved)

1. Now read the target architecture and DB model. Read only the sections on modules or services, data ownership and communication.
2. Compare module by module: agree, differ, missing, extra. For each difference say which is better, with evidence (coupling, shared tables, transaction boundaries, co-change).
3. Check the target design for:
   - Shallow modules, using the deletion test
   - Modules that cannot be tested through their interface alone
   - Seams with only one adapter (indirection, not a real seam)
   - Every cross-service call: classify its dependency category and name its port and adapters
   - Chatty calls between services; tables shared across services; distributed transactions; cyclic dependencies; services that must change together; business rules split across services; tables or rules with no clear owner
4. Write `docs/design/boundary-comparison.md`: one row per module with target design, independent design, difference, recommendation, decision needed.
5. Decision round. Turn every "decision needed" into a question for me:
   - Numbered questions, each with your recommended answer, worded so "yes" accepts it
   - Ask the whole frontier (every question whose prerequisites are settled), up to 8 per round, then wait for my answers
   - Find facts yourself in the legacy code; the decisions are mine. Never answer your own questions.
   - Settled answers unblock later questions. Recompute the frontier each round.
   - Done when the frontier is empty. Summarize the shared understanding and wait for my confirmation before acting on it.
6. Record the outcome:
   - Add each real problem to `docs/design-gaps.md` with severity
   - Write an ADR (status `proposed`) only when the decision is hard to reverse, surprising without context, and the result of a real trade-off. Skip the rest.
   - When I reject a candidate for a lasting reason, offer an ADR so it is not re-suggested later
   - Update `docs/steering/glossary.md` as terms are resolved, not at the end

## Rules

- Scope is module boundaries and domain language only. Do not redesign the DB schema, UI or template format.
- Do not redesign anything not asked for. Stop when the listed boundaries are covered.
- Never treat the independent design as correct by default. Present evidence; a human decides.
- Never invent legacy behavior. Mark anything unconfirmed as unknown.

## Done when

- Part A is committed and approved
- `boundary-comparison.md` is complete and every decision has an answer from me
- Gaps are listed, and ADRs exist for the decisions that pass the three gates
