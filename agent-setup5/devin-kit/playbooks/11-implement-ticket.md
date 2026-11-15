> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 11: Implement One Ticket (Test-First)

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. One ticket per session; start every ticket in a fresh session. Combines the AI Hero implement and tdd methods with the Oracle and H2 workflow.

## Inputs

- Ticket: `<PATH, e.g. docs/tickets/03-load-template.md>`
- The plan it came from, the spec rule IDs it lists, `docs/steering/glossary.md`, and the relevant ADRs
- Approved Oracle SQL and H2 schema; template JSON for the section
- Target repo: `<SERVICE REPO>`

## Steps

1. State the ticket title. Read the ticket, plan, spec rules, ADRs and glossary. Confirm no unfinished ticket blocks this one. If the ticket reference is ambiguous, ask.
2. Confirm the baseline: the project builds and existing sandbox tests pass. If not, stop and report.
3. Confirm the contract tests, rule-level parity tests and shared persistence suite for this ticket exist and fail for the right reason. If they are missing, stop; they come from playbook 10.
4. Write unit tests one at a time, only at the seams agreed in the plan. If a test needs a seam that was not agreed, stop and ask. Do not write all the unit tests first.
5. For each behavior, work red then green: write one failing test, write only enough code to pass it, repeat. Do not anticipate future tests or add speculative features. Each test is a tracer bullet that responds to what the last cycle taught you.
6. Run it efficiently: compile regularly, run single test classes or methods regularly, and run the full test suite once at the end.
7. Follow the package layout, dependency rules and ADRs in `AGENTS.md`. Use glossary terms in names. Add no scope beyond the ticket.
8. Write persistence adapters strictly from the approved Oracle SQL. Use the H2 variants only where playbook 08 recorded a difference. Never change the SQL files to suit H2.
9. If you find a missing, conflicting or unclear rule, stop and log it in `<home repo>/docs/open-questions/` instead of guessing.
10. Small cleanup of the code you just wrote is fine at the end. Wider refactoring is not part of this loop: raise it as a review finding or a new ticket.
11. Run `<BUILD COMMAND>` plus formatting, static analysis and coverage locally.
12. Update docs where reality differs from them. A new structural decision gets an ADR with status `proposed`, only if it is hard to reverse, surprising without context and a real trade-off.
13. Commit to the ticket branch, push and open a PR using the template. Wait for CI. The `oracle-integration` suites must pass in CI. Read the results and fix failures. If a parity test fails and the cause is not clearly your bug, stop and report the fixture, expected versus actual, and your hypothesis. Do not change the fixture.
14. Set the ticket status to done only after sandbox and CI tests pass, then check that the status change was saved.

## Rules

- Tests verify behavior through public interfaces, not implementation details. Code can change entirely and the tests should not.
- No implementation-coupled tests (mocking internal collaborators, testing private methods, checking through the database instead of the interface). No tautological tests (expected values must come from an independent source: a recorded output, a worked example, the spec).
- Parity tests are the contract with the legacy behavior. Never change an expected value to make a test pass.
- Do not refactor unrelated code.
- If the ticket is too large for one session, stop and propose a split.

## Done when

- Every acceptance criterion is covered by passing tests, in the sandbox and in CI
- The PR is open with the full description and the ticket status is updated
