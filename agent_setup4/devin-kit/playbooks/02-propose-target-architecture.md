> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 02: Propose Target Architecture

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Based on the AI Hero grilling, wayfinder and codebase-design ideas: interview, compare options, record decisions. Devin proposes; a human decides. Use this when parts of the target architecture are undecided or you want a second opinion.

## Goal

Produce compared options and a recommendation for the target architecture, shaped by my stated preferences, and record the chosen direction as proposed ADRs.

## Inputs

- My architectural preferences and constraints: `<LIST, e.g. microservices, template-driven questionnaire, snapshots for versioning, Oracle 21 with CLOB JSON, H2 for sandbox tests>`
- Existing target design and decisions, if any: `<PATHS>`
- Legacy knowledge wiki and the independent boundary design (`docs/design/independent/modules.md`) if it exists
- Non-functional needs: `<SCALE, SECURITY, AUDIT, AVAILABILITY>`
- Decisions already made that must not be re-opened: `<LIST OF APPROVED ADRs>`

## Steps

1. Read the inputs. Summarize my preferences and constraints back in under 15 lines and wait for corrections.
2. Interview me in rounds of at most 3 to 5 questions. Give your recommended answer and evidence with each question. Answer from the legacy code where you can instead of asking.
3. For each open architecture topic, produce at least three genuinely different options (use parallel sub-agents if available). Topics: service decomposition, data ownership, communication (sync or async), template engine placement, versioning of templates and rules, API style, security and authorization, audit, error handling.
4. Compare options on: fit to my preferences, depth of modules, locality of change, testability (including H2 sandbox and Oracle CI), operational complexity, risk. Show the trade-offs in a table.
5. Recommend one option per topic with reasons, and say what evidence would change your mind.
6. Write `docs/design/architecture-options.md`. Stop and wait for my decisions.
7. After decisions, write each as an ADR with status `proposed` and update `docs/design/architecture.md`.

## Rules

- My stated preferences are inputs, not suggestions to argue away. If one looks risky, say so with evidence and still show the option.
- Do not re-open approved ADRs.
- Do not design the DB schema or UI here.
- Mark claims GREEN, AMBER or RED. Do not present inference as fact.

## Done when

- Options and a recommendation exist for every listed topic
- My decisions are recorded as `proposed` ADRs
- `architecture.md` matches the decisions
