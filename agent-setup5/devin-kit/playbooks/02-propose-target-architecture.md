> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 02: Propose Target Architecture

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Combines the AI Hero wayfinder, grilling, research and prototype methods with the design-it-twice comparison. Devin proposes and compares; a human decides. Use it when parts of the target architecture are undecided or you want options. A whole architecture is too big for one session, so this playbook has two modes: chart a map of decisions once, then resolve one decision per session.

## Goal

Reach an approved `docs/design/architecture.md` and its ADRs by working through a map of decisions, shaped by my stated preferences.

## Inputs

- My architectural preferences and constraints: `<LIST, e.g. microservices, template-driven questionnaire, snapshots for versioning, Oracle 21 with CLOB JSON, H2 for sandbox tests>`
- Existing target design and decisions, if any: `<PATHS>`
- Legacy knowledge wiki and the independent boundary design (`docs/design/independent/modules.md`) if it exists
- Non-functional needs: `<SCALE, SECURITY, AUDIT, AVAILABILITY>`
- Decisions already made that must not be re-opened: `<LIST OF APPROVED ADRs>`
- Mode: `<A: chart the map | B: resolve one decision (name it)>`

## Rules for both modes

- Planning only. Produce decisions, not code or deliverables beyond the documents named here.
- A decision lives in exactly one place: its ticket (and ADR if it passes the gates). The map only links and gives a one-line gist.
- Refer to decisions by name, never by bare number.
- My stated preferences are inputs, not suggestions to argue away. If one looks risky, say so with evidence and still show the option.
- Do not re-open approved ADRs. Do not design the DB schema or UI here.
- Mark claims GREEN, AMBER or RED. Do not present inference as fact.
- Never answer your own questions. The decisions are mine.

## Mode A: Chart the map (one session)

1. Name the destination: what reaching the end of this map looks like (for example an approved `architecture.md` plus ADRs for the topics below). Interview me to pin it down, using numbered questions with your recommended answer, worded so "yes" accepts it. Find facts in the code and docs yourself.
2. Map the frontier breadth first: fan out across the whole space and list the open decisions. Topics to cover: service decomposition, data ownership, communication (sync or async), template engine placement, versioning of templates and rules, API style, security and authorization, audit, error handling.
3. If the way is already clear and small enough for one session, do not make a map. Stop and ask me how to proceed.
4. Write `docs/design/architecture-map.md` with: Destination; Notes (preferences, constraints, skills or playbooks to consult); Decisions so far (one line per resolved decision, linking its ticket or ADR); Not yet specified (the fog: decisions you can tell are coming but cannot phrase sharply yet); Out of scope (ruled out of this effort, with the reason).
5. Create one decision ticket per sharp question at `docs/design/decisions/NN-<name>.md`. Each holds the question, its type, and its blocking tickets. Sized to one session. Types:
   - **research** (AFK): a fact outside the repo that a decision waits on, found from primary sources (official docs, source code, specs)
   - **prototype** (with me): a cheap throwaway artifact to react to, when how it should behave or look is the question
   - **grilling** (with me): a conversation. The default.
   - **task**: manual work that must happen before a decision can be made
6. Fog or ticket test: make it a ticket if you can state the question precisely now, even if you cannot answer it yet. Otherwise it stays in Not yet specified.
7. Start each research ticket as a **child session** in parallel, each with a fresh context. Each writes its findings to `docs/design/research/<name>.md`, citing the primary source for every claim, and reports back to you (the parent). Research children do not decide anything.
8. Stop. Charting is one session. Resolve nothing by hand.

## Mode B: Resolve one decision (one session per decision)

1. Load the map (the low-resolution view, not every ticket). Take the ticket I name, or the first unblocked one. Claim it by writing the claim and date in the ticket before any work.
2. Resolve it by its type:
   - **research:** primary sources only, with a citation for every claim
   - **prototype:** a throwaway on a branch named `prototype/<name>`, clearly marked as a prototype, no persistence, no polish, never merged. Show the relevant state after every action. Capture the verdict and the question it answered in the ticket.
   - **grilling:** frontier rounds (numbered questions, recommended answers, up to 8 per round, wait for my answers). For architecture topics also produce at least three genuinely different options and compare them: fit to my preferences, depth of modules, locality of change, testability (H2 in the sandbox, Oracle in CI), operational complexity, risk. Recommend one and say what evidence would change your mind.
   - **task:** do it if you can; otherwise give me a precise checklist
3. Record the resolution in the ticket, close it, and add a one-line gist with a link to the map's Decisions so far.
4. Write an ADR (status `proposed`) only when the decision is hard to reverse, surprising without context, and the result of a real trade-off. Update `docs/design/architecture.md`.
5. Graduate any fog the answer made sharp into new tickets (create them, then add the blocking links). Rule anything past the destination out of scope on the map. Update or delete tickets the decision invalidated.
6. Never resolve more than one ticket per session (research tickets excepted).

## Done when

- Mode A: the map and its decision tickets are committed
- Mode B: the ticket is resolved, the map is updated and the ADR (if any) is proposed
- Overall: no tickets remain and `architecture.md` matches the decisions
