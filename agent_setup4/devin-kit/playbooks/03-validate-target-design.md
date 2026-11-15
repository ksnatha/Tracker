> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 03: Validate Target Design

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Based on the AI Hero grill-with-docs, grilling and domain-modeling ideas.

## Goal

Stress-test the target architecture and DB model against the real behavior of the legacy app. Record a glossary, ADRs and a gap list. Do not redesign from scratch and do not write application code.

## Inputs (read these first)

- `<home repo>/docs/design/`: target architecture, DB model, POC design and decisions: `<FILE NAMES>`
- Legacy repos (read-only) and the Devin knowledge wiki
- Hard cases: `<THE HARDEST LEGACY CASES YOU KNOW OF, or "none yet: find them first">`
- Constraints: Oracle `<VERSION>`; about `<N>` questions across `<N>` sections; `<INTEGRATIONS>`
- Focus areas this session: `<e.g. template format, DB model, versioning>`

## How to run

1. Read all inputs first. Summarize the design back in under 15 lines and ask me to correct it.
2. Interview me in rounds of at most 3 to 5 questions. Wait for answers.
3. With every question, include your recommended answer and its evidence. If the legacy code can answer, read it instead of asking.
4. Walk the design in dependency order: settle decisions that others depend on first.
5. Challenge the design with real cases. For each focus area, pick actual legacy fields and rules and test whether the design can represent them.
6. When a term is resolved, write it to `docs/steering/glossary.md` immediately. Flag any term where legacy code, the business and the new design disagree.
7. When a decision is hard to reverse, surprising to a newcomer, or the result of a real trade-off, write an ADR (status `proposed`) immediately.
8. When you find a flaw, add it to `docs/design-gaps.md` with severity, evidence, confidence and a suggested fix.

## Checks to cover

- Rule types: can the template JSON express cross-field, cross-section, computed, repeatable-grid and externally-sourced dropdown rules? Anything it cannot express is marked as code (a strategy bean).
- Dedicated column versus EAV: propose a rule for deciding per field, then test it on at least `<10>` real fields.
- Versioning: in-progress records stay on their original version for templates, static values and business logic.
- Oracle 21 with CLOB JSON: confirm the storage and query approach, including what H2 cannot do.
- Workflow, status transitions, permissions, audit and notifications each have an owner in the design.

## Rules

- Never invent legacy behavior. Mark unknowns.
- Do not silently change an earlier decision. Say so with evidence and ask.
- Stop when the focus areas are covered.

## Done when

- Every focus area was challenged against real legacy cases
- Glossary and ADRs are committed
- `docs/design-gaps.md` lists every gap with severity
- A short summary lists decisions made, gaps found and questions for SMEs
