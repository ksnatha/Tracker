> **TEMPLATE:** Copy this file, rename it, and fill in the placeholders. Delete this block in the copy.

# Build plan: <FEATURE OR SERVICE>

Records decisions already made. It does not make new decisions and does not extract rules. Stop and report if any input below is missing or not approved. Use the glossary vocabulary. Do not put source file paths or code snippets in this plan.

## Inputs (all must be approved)

- Behavior spec(s): `<service repo>/docs/specs/<slice>.md`
- Keep, change or retire decisions: section 16 of the spec
- ADRs: `<LINKS>`
- Template JSON: `<PATHS>`
- Oracle SQL and data dictionary: `<PATHS>`
- Open design gaps for this scope: `<NONE or LINKS>`

## Problem statement

The problem, from the user's perspective (the business or user who needs this).

## Solution

The solution, from the user's perspective.

## User stories (optional)

Only stories that come from the spec's entry points and rules, not invented ones. Format: As an <actor>, I want <capability>, so that <benefit>. Reference the rule IDs.

## Implementation decisions

- Modules built or changed, and their interfaces
- Architectural decisions (link each ADR)
- Schema and template JSON in scope
- API contracts and the error model (link `contracts`)
- Specific interactions and transaction behavior

## Rules covered

| Rule ID | Decision | Lives in (template JSON / strategy bean / workflow / other) |
|---|---|---|
| R-00 | PRESERVE | <HOME> |

## Testing decisions

- What makes a good test here: external behavior only, never implementation details
- Agreed seams (existing seams preferred, highest seam possible, as few as possible; one line each on what it catches and misses): <SEAMS>
- Modules that will be tested: <LIST>
- Parity sources: <recorded fixtures / approved derived examples>
- Prior art: similar tests already in the repo: <LINKS>
- Layers and where they run: see the test strategy guide

## Out of scope

<TEXT>

## Open questions

<LINKS>
