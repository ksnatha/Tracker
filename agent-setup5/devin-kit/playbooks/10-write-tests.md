> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 10: Agree Seams and Write the Parity Harness (Contract and Rule-Level Parity)

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run per slice, before implementation. Combines the AI Hero tdd method (tests only at agreed seams; avoid implementation-coupled and tautological tests) with the legacy parity approach. Output: failing contract and parity tests that encode the approved spec.

**Why only these tests are written up front.** The tdd method warns against writing all tests first, because bulk tests verify imagined behavior. Contract and parity tests are different: their expected values are recorded from legacy output or taken from the approved spec, not imagined. Unit tests per rule are different: they are written one at a time inside each ticket (playbook 11).

## Inputs

- Approved spec and decisions: `docs/specs/<slice>.md`
- Approved build plan and tickets: `<home repo>/docs/plans/<feature>.md`
- `contracts` repo (OpenAPI, event schemas) and sanitized fixtures: `<PATHS>`
- Approved Oracle SQL and H2 schema (playbooks 07 and 08)
- Test strategy guide: `<home repo>/docs/design/test-strategy.md`

## Steps

1. Read the inputs. Confirm fixtures are sanitized. If you see real-looking PII, stop and report without copying it.
2. Confirm the seams. Read the seams agreed in the plan. If a test you need has no agreed seam, propose one with a one-line note on what it catches and misses, and wait for my confirmation. Write no test at an unconfirmed seam.
3. Create the minimal skeleton (interfaces, DTOs, empty implementations) needed to compile.
4. Write contract tests from the OpenAPI and event schemas.
5. Write rule-level parity tests. Load each recorded or approved input and expected output for a rule, run it through the new code with persistence ports faked or backed by H2, and assert equality on the full result: values, status, ordering, rounding, nulls, error messages. Parity is at rule and service level, not replayed legacy HTTP. Make them data-driven so a new case is a new data row.
6. Write the shared persistence test suite base class (playbook 08) for the repositories in scope: the H2 subclass and the `@Tag("oracle-integration")` Oracle subclass.
7. Run the sandbox tests. **Confirm they fail for the right reason** (missing behavior, not a build or config error). Put the failure summary in the PR.
8. Stop and wait for review.

## Rules

- **No implementation-coupled tests:** no mocking of internal collaborators, no testing private methods, no checking results through a side channel (querying the database instead of using the interface). The tell: the test breaks on a refactor that changed no behavior.
- **No tautological tests:** the expected value must come from an independent source (a recorded legacy output, a worked example in the spec, a known literal), never recomputed the way the code computes it.
- Never edit a fixture or expected value to fit your design. A derived example stays AMBER until approved.
- No tests for behavior with no approved decision, and none for RETIRED rules.
- Tests are deterministic: inject `Clock`, ID generators and random sources.
- Name tests `methodName_condition_expectedResult`. One behavior per test.

## Done when

- Contract, parity and shared persistence tests are committed and failing for the right reason
- Every rule in scope is referenced by at least one parity case, or listed with the reason it is not
- Unit tests are explicitly left for playbook 11
