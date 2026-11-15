> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 08: Derive the H2 Test Schema from the Oracle SQL

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run after playbook 07 is approved. H2 exists so fast tests run in the sandbox. It is a test aid, never proof of Oracle behavior.

## Inputs

- Approved Oracle scripts in `db/oracle/`, the repository SQL, `docs/db/data-dictionary.md`
- `AGENTS.md` sections 2, 4 and 5

## Steps

1. Do not edit the Oracle files. If they need a change, stop and report; the fix goes through playbook 07.
2. Create the H2 schema under `src/test/resources/h2/`, derived one to one from the Oracle scripts, with the same table, column and constraint names. Use H2 Oracle compatibility mode where it helps, but verify each construct with a test and do not assume.
3. Translate types and constructs and record each: `NUMBER` to `DECIMAL` or `NUMERIC` with the same precision, `VARCHAR2` to `VARCHAR`, `CLOB` to `CLOB`, `DATE` to a timestamp with seconds (Oracle `DATE` has a time part), sequences, paging syntax. The `IS JSON` constraint and JSON functions do not exist in H2: omit them, and mark behavior that depends on them as Oracle-only, tested only in CI.
4. For each repository SQL file, reuse the Oracle SQL if it runs unchanged on H2. Otherwise add an H2 variant under `src/test/resources/sql/h2/` with the same file name. Keep variants to a minimum.
5. Write `docs/db/h2-translation-notes.md`: one row per difference with the construct, Oracle behavior, H2 handling, risk, and where it is tested (sandbox or CI).
6. Write one shared persistence test suite as an abstract base class. The H2 subclass runs in the sandbox. The Oracle subclass is tagged `@Tag("oracle-integration")` and runs in CI. Both run the same assertions.
7. Add explicit tests for known divergences: empty string versus NULL, `DATE` time part, NULL ordering, case sensitivity and collation, paging, `NUMBER` rounding, CLOB handling.
8. Add a schema comparison check for CI that compares table, column and nullability metadata between H2 and Oracle and fails on drift.
9. Open a PR with the translation notes summarized. CI Oracle results must be green before it is done.

## Rules

- A test that only passes because of H2 behavior is a defect.
- No Docker or Testcontainers. No H2-only SQL in production code paths.
- A query that cannot run on H2 is marked Oracle-only, its tests are tagged `oracle-integration`, and it is flagged in the PR.
- Never modify the Oracle scripts, fixtures or expected values to make an H2 test pass.

## Done when

- The H2 schema and translation notes are committed
- The shared persistence suite passes on H2 in the sandbox and on Oracle in CI
- The schema comparison check passes
