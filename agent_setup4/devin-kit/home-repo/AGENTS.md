> **FILL-IN INSTRUCTIONS (delete this block when done):** This file has one-time placeholders written like `<THIS>`. Search the file for `<` and replace each with your real value. Items marked [CONFIRM] are assumptions to check. Do not start a Devin session that relies on this file until no placeholders remain.

# AGENTS.md (home repo)

Rules for agents working in the home repo. This repo holds documents only.

## Rules

- Documents only. No production code, no migrations, no tests here.
- Do not modify the legacy repos. Treat them as read-only references.
- Do not edit another session's approved document. Add a new version or a note and flag it.
- Cross-service ADRs go in `docs/adr/` here with status `proposed`. Only a human approves.
- Do not copy secrets, PII or real customer data into any document.

## Pointers to other repos

Clone these next to this repo if the task needs them (or use the repos attached to your session):

- Service repo: `<SERVICE REPO URL>` (clone to `<PATH>`)
- Contracts: `<CONTRACTS REPO URL>` (clone to `<PATH>`)
- Parity harness: `<PARITY REPO URL>` (clone to `<PATH>`)
- Legacy backend (read-only): `<LEGACY BACKEND URL>`
- Legacy UI (read-only): `<LEGACY UI URL>`

## Read order at the start of a session

1. This file
2. `docs/design/` for the target architecture and boundaries
3. `docs/adr/` for approved decisions
4. The service repo `AGENTS.md`, `DEVIN.md`, `docs/steering/` and `docs/specs/`

## Confidence and blockers

Use GREEN, AMBER and RED markers as defined in the service repo `AGENTS.md` section 14. Use the BLOCKER format in `DEVIN.md` when you cannot continue.
