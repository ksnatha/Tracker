> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 15: Architecture Audit (periodic)

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run every few features. Based on the AI Hero improve-codebase-architecture and codebase-design ideas.

## Goal

Survey the new codebase for drift from the approved boundaries and for modules worth deepening, and write the findings as tickets.

## Inputs

- Service repos: `<LIST>`
- Approved ADRs and `<home repo>/docs/design/`
- Scope limit: `<MODULES TO AUDIT>`

## Steps

1. Read the approved boundaries and ADRs.
2. Survey the code: shallow modules, leaked dependencies across layers or services, shared tables, duplicated logic, business rules in the wrong place, modules that change together.
3. Compare with the approved design and say where the code drifted and where the design itself looks wrong.
4. For each finding give severity, evidence (file:line), the cost of leaving it, and a proposed ticket. Do not change code.
5. Write `<home repo>/docs/design/audit-<DATE>.md` and the proposed tickets.

## Rules

- Only audit the listed scope. Do not redesign what was not asked.
- Treat approved ADRs as fixed. A conflict is a finding for a human, not something to fix.

## Done when

- The audit and proposed tickets are committed for review
