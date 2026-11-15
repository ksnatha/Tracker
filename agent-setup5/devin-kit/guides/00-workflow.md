> **FILL-IN INSTRUCTIONS (delete this block when done):** This file has one-time placeholders written like `<THIS>`. Search the file for `<` and replace each with your real value. Items marked [CONFIRM] are assumptions to check. Do not start a Devin session that relies on this file until no placeholders remain.

# Workflow: Legacy to New App with Devin

## Sequence

| # | Phase | Playbook | Output | Approver |
|---|---|---|---|---|
| 1 | Boundaries | 01 | `independent/modules.md`, `boundary-comparison.md`, proposed ADRs | `<ARCHITECT>` |
| 2 | Target architecture | 02 | `architecture-options.md`, `architecture.md`, proposed ADRs | `<ARCHITECT>` |
| 3 | Design validation | 03 | Glossary, ADRs, `design-gaps.md` | `<ARCHITECT>` |
| 4 | Extract rules | 04 (one slice at a time) | `docs/specs/<slice>.md` | SMEs, `<REVIEWER>` |
| 5 | Decisions | 05 | Spec section 16, decision questionnaire | SMEs, `<APPROVER>` |
| 6 | Template JSON | 06 (per section) | Template JSON, unexpressible-rule list | `<REVIEWER>` |
| 7 | Oracle SQL | 07 | `db/oracle/`, SQL files, data dictionary | `<DBA>` |
| 8 | H2 schema | 08 | `src/test/resources/h2/`, translation notes | `<REVIEWER>` |
| 9 | Plan and tickets | 09 | `docs/plans/`, `docs/tickets/` | `<TECH LEAD>` |
| 10 | Seams and parity harness | 10 | Agreed seams; failing contract, parity and persistence tests (unit tests come per ticket in step 11) | `<REVIEWER>` |
| 11 | Implement | 11 (one ticket per session) | PR, green sandbox and CI | `<REVIEWER>` |
| 12 | Review | 12 (fresh session) | Review on the PR | `<REVIEWER>` |
| 13 | Retro | 13 | Retro and doc updates | `<TECH LEAD>` |
| 14 | UI e2e and load | 14 (later) | Automated journeys, load baseline | `<TECH LEAD>` |
| 15 | Audit | 15 (every few features) | Audit and tickets | `<ARCHITECT>` |

Phases 1 to 3 settle the design. From phase 4 on, repeat the loop per slice. Phase 4 for one section can start while the design settles, since the rule inventory does not depend on boundaries. Start with one section that mixes dedicated and EAV fields, and build a thin end-to-end slice before scaling.

## Human checkpoints

- Design gaps and ADRs: approved before extraction relies on them
- Behavior spec: reviewed (AMBER and RED first) before decisions
- Decisions: approved by named people before tests or code
- Oracle SQL: DBA review
- Plan: approved before tickets are written
- Tests: reviewed before implementation
- PR: parity tests and CI Oracle results checked, then merged by a human

## How to start a Devin session

1. Pick the step and the slice or ticket.
2. Attach the playbook and make sure `AGENTS.md` and `DEVIN.md` are in the repo.
3. Fill the per-run placeholders in the playbook.
4. Give the exact paths of the input documents.
5. Wait for Devin to restate the scope, check it, then let it run.

## File map

- Service repo: `AGENTS.md`, `DEVIN.md`, `docs/steering/`, `docs/adr/`, `docs/specs/`, `docs/db/`, `db/oracle/`, SQL resources, tests
- Home repo: `docs/design/`, `docs/design-gaps.md`, `docs/open-questions/`, `docs/plans/`, `docs/tickets/`, `docs/retros/`, cross-service `docs/adr/`
- `contracts` repo: OpenAPI, event schemas, fixtures
- Legacy repos: read-only

## Rule of thumb

Devin proposes. Humans decide. Nothing downstream relies on a document that is not approved.
