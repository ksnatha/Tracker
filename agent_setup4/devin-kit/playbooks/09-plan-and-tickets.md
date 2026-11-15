> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 09: Plan and Tickets

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run once per feature or service, in one session. Do not clear the session between the plan and the tickets. Based on the AI Hero to-spec and to-tickets ideas: the plan records decisions already made. It does not make new decisions and does not extract rules from legacy code.

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

1. Read all inputs. Summarize the scope back to me in under 15 lines and wait for corrections.
2. Write `docs/plans/<feature>.md` from the template: goal, scope and non-goals; decisions that fix the design (each linked to its ADR); interfaces and contracts; data and template JSON in scope; the rules covered with their decisions and target homes; test seams; parity sources; acceptance criteria; open questions.
3. Do not invent decisions. Anything undecided goes under open questions.
4. Stop and wait for my approval of the plan.
5. After approval, split it into tickets at `docs/tickets/NNN-short-title.md`. Each ticket is a thin vertical slice that works end to end (API, service logic, persistence, tests), never a layer on its own. Each fits one fresh Devin session. The first ticket is the thinnest walking skeleton. Declare dependencies and the build order.
6. Each ticket: goal, acceptance criteria, rule IDs and decisions, docs and files to read first, test seams and parity cases to write first, dependencies, status (todo, in progress, done).
7. Summarize the ticket list with order and dependencies.

## Done when

- The plan is committed and approved
- The tickets are committed, each a vertical slice with acceptance criteria
- The build order is clear
