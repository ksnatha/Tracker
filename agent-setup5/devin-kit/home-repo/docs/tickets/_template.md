> **TEMPLATE:** Copy this file, rename it, and fill in the placeholders. Delete this block in the copy.

# NN: <Ticket title>

**What to build:** the end-to-end behavior this ticket makes work, from the user's perspective, not a layer-by-layer list. It is a thin vertical slice (API, service logic, persistence, tests) that can be demoed or verified on its own.

**Blocked by:** <numbers or titles of the tickets that gate this one, or "None (can start immediately)">

**Status:** <ready | blocked | in progress | done>  (ready means every blocker is done)

- Plan: <LINK>
- Repo: <SERVICE REPO>
- Touches (modules, tables, migrations, contracts, shared files): <LIST>
- Safe to run in parallel with: <TICKETS, or "none">

## Acceptance criteria

- [ ] <CRITERION>

## Rules covered

<Rule IDs and decisions.>

## Read first

<Docs, ADRs, spec sections, template JSON, SQL files. Documents only: no source file paths for the code to change.>

## Tests to write first

- Agreed seams: <SEAMS>
- Parity cases: <FIXTURES OR EXAMPLES>

## Notes

<Anything the implementer must know. Keep it short. Do not include code snippets.>
