> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 11: Implement One Ticket (Test-First)

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. One ticket per session; start every ticket in a fresh session. Based on the AI Hero implement and tdd ideas.

## Inputs

- Ticket: `<PATH, e.g. docs/tickets/003-load-template.md>`
- The plan it came from, the spec rule IDs it lists, `docs/steering/glossary.md`, and the relevant ADRs
- Approved Oracle SQL and H2 schema; template JSON for the section
- Target repo: `<SERVICE REPO>`

## Steps

1. Read the ticket, plan, spec rules, ADRs and glossary. Confirm no unfinished ticket blocks this one.
2. Confirm the baseline: the project builds and existing sandbox tests pass. If not, stop and report.
3. Make the ticket's failing tests exist and fail for the right reason: contract tests, rule-level parity tests, the shared persistence suite for the repositories involved. If they are missing, stop; they come from playbook 10.
4. Work in small red, green, refactor steps: one behavior at a time, minimal code to pass, then clean up. Run the affected tests after each step.
5. Follow the package layout, dependency rules and ADRs in `AGENTS.md`. Use glossary terms in names. Add no scope beyond the ticket.
6. Write persistence adapters strictly from the approved Oracle SQL. Use the H2 variants only where playbook 08 recorded a difference. Never change the SQL files to suit H2.
7. If you find a missing, conflicting or unclear rule, stop and log it in `<home repo>/docs/open-questions/` instead of guessing.
8. Run `<BUILD COMMAND>` plus formatting, static analysis and coverage locally.
9. Update docs where reality differs from them. A new structural decision gets an ADR with status `proposed`.
10. Push and open a PR using the template. Wait for CI. The `oracle-integration` suites must pass in CI. Read the results and fix failures. If a parity test fails and the cause is not clearly your bug, stop and report the fixture, expected versus actual, and your hypothesis. Do not change the fixture.
11. Set the ticket status to done only after sandbox and CI tests pass, then check that the status change was saved.

## Rules

- Parity tests are the contract with the legacy behavior. Never change an expected value to make a test pass.
- Do not refactor unrelated code.
- If the ticket is too large for one session, stop and propose a split.

## Done when

- Every acceptance criterion is covered by passing tests, in the sandbox and in CI
- The PR is open with the full description and the ticket status is updated
