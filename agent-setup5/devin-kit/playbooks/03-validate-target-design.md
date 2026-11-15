> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 03: Validate Target Design

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Combines the AI Hero grilling and domain-modeling methods (the grill-with-docs skill) with checks specific to this rewrite.

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
2. Map the design as a tree: every decision branches into the decisions that hang off it. Work it in rounds. Each round asks the whole frontier (every decision whose prerequisites are already settled): numbered questions, each with your recommended answer, worded so "yes" accepts it. Up to 8 questions per round. Wait for my answers, recompute the frontier, and ask the next round.
3. Finding facts is your job, not mine. If the legacy code, wiki or docs can answer a question, look it up (use a **child session** for a lookup that needs a fresh context, and have it write its findings to a named file) instead of asking. Do not block: ask the questions that do not depend on the lookup now. The decisions are mine; never answer your own questions.
4. Challenge the design with real cases. For each focus area, pick actual legacy fields and rules and test whether the design can represent them.
5. Domain modeling, as you go:
   - When I use a term that conflicts with the glossary, say so immediately
   - When I use a vague or overloaded term, propose a precise canonical one
   - Stress-test relationships with concrete edge-case scenarios
   - When I say how something works, check the code and surface contradictions
6. Update `docs/steering/glossary.md` the moment a term is resolved, not at the end. Keep it free of implementation details: no table names, class names or technology.
7. Write an ADR (status `proposed`) only when the decision is hard to reverse, surprising without context, and the result of a real trade-off. If I reject an option for a lasting reason, offer an ADR so it is not suggested again. Skip ephemeral reasons.
8. When you find a flaw, add it to `docs/design-gaps.md` with severity, evidence, confidence and a suggested fix.
9. The session is done when the frontier is empty: every branch visited and nothing left silently assumed. Summarize the shared understanding and wait for my confirmation before acting on it.

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

- Every focus area was challenged against real legacy cases and the frontier is empty
- Glossary and ADRs are committed
- `docs/design-gaps.md` lists every gap with severity
- A short summary lists decisions made, gaps found and questions for SMEs
