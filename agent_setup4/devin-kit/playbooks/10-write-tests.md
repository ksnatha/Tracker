> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 10: Write Tests (Contract and Rule-Level Parity)

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run per slice, before implementation. Output: failing tests that encode the approved spec.

## Inputs

- Approved spec and decisions: `docs/specs/<slice>.md`
- Approved build plan and tickets: `<home repo>/docs/plans/<feature>.md`
- `contracts` repo (OpenAPI, event schemas) and sanitized fixtures: `<PATHS>`
- Approved Oracle SQL and H2 schema (playbooks 07 and 08)
- Test strategy guide: `<home repo>/docs/design/test-strategy.md`

## Steps

1. Read the inputs. Confirm fixtures are sanitized. If you see real-looking PII, stop and report without copying it.
2. Create the minimal skeleton (interfaces, DTOs, empty implementations) needed to compile.
3. Write contract tests from the OpenAPI and event schemas.
4. Write rule-level parity tests. Load each recorded or approved input and expected output for a rule, run it through the new code with persistence ports faked or backed by H2, and assert equality on the full result: values, status, ordering, rounding, nulls, error messages. Parity is at rule and service level, not replayed legacy HTTP.
5. Add domain unit tests for each rule in the spec, with the rule ID in the test name or a comment (for example `R-12`). Cover only rules whose decision is PRESERVE or an approved CHANGE. Do not write tests for RETIRED rules.
6. Write the shared persistence test suite base class from playbook 08 for the repositories in scope: the H2 subclass and the `@Tag("oracle-integration")` Oracle subclass.
7. Run the sandbox tests. **Confirm they fail for the right reason** (missing behavior, not a build or config error). Put the failure summary in the PR.
8. Stop and wait for review.

## Rules

- Never edit a fixture or expected value to fit your design. A derived example stays AMBER until approved.
- No tests for behavior with no approved decision.
- Tests are deterministic: inject `Clock`, ID generators and random sources.
- Name tests `methodName_condition_expectedResult`. One behavior per test.

## Done when

- Contract, parity, unit and shared persistence tests are committed and failing for the right reason
- Every rule in scope is referenced by at least one test, or listed with the reason it is not
