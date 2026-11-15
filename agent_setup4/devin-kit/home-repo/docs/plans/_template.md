> **TEMPLATE:** Copy this file, rename it, and fill in the placeholders. Delete this block in the copy.

# Build plan: <FEATURE OR SERVICE>

Records decisions already made. It does not make new ones and does not extract rules. Stop and report if any input below is missing or not approved.

## Inputs (all must be approved)

- Behavior spec(s): `<service repo>/docs/specs/<slice>.md`
- Keep, change or retire decisions: section 16 of the spec
- ADRs: `<LINKS>`
- Template JSON: `<PATHS>`
- Oracle SQL and data dictionary: `<PATHS>`
- Open design gaps for this scope: `<NONE or LINKS>`

## Goal, scope and non-goals

<TEXT>

## Design decisions that fix the build

<Each decision linked to its ADR.>

## Interfaces and contracts

<Endpoints, events, DTOs, error model. Link `contracts`.>

## Data

<Tables owned, repository ports, JSON storage, template JSON files in scope.>

## Rules covered

| Rule ID | Decision | Lives in (template JSON / strategy bean / workflow / other) |
|---|---|---|
| R-00 | PRESERVE | <HOME> |

## Test strategy for this plan

- Test seams: <REST API / service layer / repository>
- Parity sources: <recorded fixtures / approved derived examples>
- Layers and where they run: see the test strategy guide

## Acceptance criteria

<TEXT>

## Open questions

<LINKS>
