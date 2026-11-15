> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 15: Architecture Audit (periodic)

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run every few features, and before a large build. Based on the AI Hero improve-codebase-architecture and codebase-design methods. The output is a short ranked list of candidates and proposed tickets. No code changes.

## Goal

Survey the new codebase for drift from the approved boundaries and for modules worth deepening, and write the findings as tickets.

## Inputs

- Service repos: `<LIST>`
- Approved ADRs, `docs/steering/glossary.md` and `<home repo>/docs/design/`
- Scope limit: `<MODULES TO AUDIT>`
- Optional: the change I am about to make (to find what would make it easier)

## Steps

1. Read the glossary and ADRs first. Use the glossary vocabulary to name candidates.
2. Scope with history. Use git history to find the hot spots (files that change most often and files that change together) and start there.
3. Explore in a **child session** with a fresh context if the scope is large, and have it write its friction notes to a named file. Note friction as you go:
   - Understanding one concept means bouncing between many small modules
   - Shallow modules, tested through a thin interface that adds nothing (apply the deletion test)
   - Logic extracted only to be testable, while the real bugs sit in how it is called
   - Tightly coupled modules that leak across their seams
   - Parts that are hard to test or untested
   - Leaked dependencies across layers or services, shared tables, duplicated logic, business rules in the wrong place
4. Present a numbered list of candidates in order of value, each with: the module or cluster, why it is a problem (friction and evidence, file and line), the test-surface benefit if deepened, and a strength rating (Strong, Worth exploring, Speculative). Name one top recommendation. Do not propose interfaces yet.
5. Where a candidate conflicts with an approved ADR, surface it only if the friction is real enough to revisit the ADR, and mark it clearly. Do not list every theoretical refactor an ADR forbids.
6. Ask me which candidate to explore. Take one candidate per session into design (use the design-it-twice method from playbook 01), and write the result as a proposed ticket.
7. If I reject a candidate for a lasting reason, offer an ADR so it is not suggested again. Skip ephemeral reasons.
8. Write `<home repo>/docs/design/audit-<DATE>.md` and the proposed tickets.

## Rules

- Only audit the listed scope. Do not redesign what was not asked.
- Treat approved ADRs as fixed. A conflict is a finding for a human, not something to fix.
- Do not change code.

## Done when

- The audit and proposed tickets are committed for review
