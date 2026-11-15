> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 09: Plan and Tickets

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run once per feature or service, in one session. Do not clear the session between the plan and the tickets. Combines the AI Hero to-spec and to-tickets methods. The plan records decisions already made. It does not make new decisions and does not extract rules from legacy code.

## Goal

Turn approved decisions and the approved behavior spec into a build plan, then split it into small, ordered tickets Devin can build one per session.

## Inputs

- Feature or service: `<NAME, e.g. questionnaire service plus a minimal consumer>`
- Approved behavior spec(s) with section 16 decisions: `<PATHS>`
- Approved ADRs, `docs/steering/`, `<home repo>/docs/design/`
- Template JSON and Oracle SQL for the sections in scope: `<PATHS>`
- `<home repo>/docs/design-gaps.md`
- Where plans and tickets go: `<MARKDOWN FILES in <home repo>/docs/plans and docs/tickets, or TRACKER NAME>`

## Pre-check (stop if it fails)

Stop and report if any input is missing or not approved, if any rule in scope has no recorded decision, or if the gap list has open blockers for this scope. Do not fill the gap yourself.

## Steps

1. Read all inputs and explore the current state of the service repo. Use the glossary vocabulary throughout and respect the ADRs. Summarize the scope back to me in under 15 lines and wait for corrections.
2. Agree the test seams before writing the plan. Existing seams are preferred to new ones. Use the highest seam possible. The fewer seams across the codebase the better; one is ideal. Give each proposed seam a one-line note on what it catches and what it misses. Wait for my confirmation.
3. Write `docs/plans/<feature>.md` from the template: problem and solution from the user's perspective; implementation decisions (modules, interfaces, schema, API contracts, each linked to its ADR); testing decisions (what makes a good test, the agreed seams, modules tested, similar tests already in the repo); rules covered with their decisions and target homes; out of scope; open questions.
4. Do not invent decisions. Anything undecided goes under open questions. Do not put source file paths or code snippets in the plan or tickets, they go stale. Exception: a prototype snippet that states a decision more precisely than prose (a state machine, a schema), trimmed and marked as coming from a prototype.
5. Stop and wait for my approval of the plan.
6. Draft the tickets as tracer bullets:
   - Each slice cuts a narrow but complete path through every layer (API, service logic, persistence, tests), never one layer alone
   - Each is demoable or verifiable on its own and sized to fit one fresh Devin session
   - Any prefactoring goes first: make the change easy, then make the easy change
   - Each declares its blocking edges: the tickets that must finish first
   - A wide mechanical change that would break many call sites at once is the exception: sequence it as expand (add the new form beside the old), migrate in batches, then contract (remove the old)
   - The first ticket is the thinnest walking skeleton
   - Each ticket lists what it touches (modules, tables, migrations, contracts, shared files). Two tickets that touch the same thing are not parallel-safe: add a blocking edge or merge them. A UI ticket and a service ticket for the same feature are parallel-safe only after the API contract between them is agreed and committed, so the contract comes first.
7. Quiz me before publishing. Show a numbered list of tickets, each with title, blocked by, and what it delivers. Ask: is the granularity right, are the blocking edges correct, which tickets are safe to run in parallel, should any be merged or split? Iterate until I approve.
8. Publish the approved tickets at `docs/tickets/NN-short-title.md` (or to the tracker), one file per ticket, numbered in dependency order with blockers first. Mark a ticket `ready` when it has no open blockers and `blocked` otherwise.
9. Summarize the frontier (tickets ready to start now), which of them are parallel-safe, and the build order.

## Done when

- The plan is committed and approved, and the seams are confirmed
- The tickets are committed, each a vertical slice with acceptance criteria and blocking edges
- The frontier and build order are clear
