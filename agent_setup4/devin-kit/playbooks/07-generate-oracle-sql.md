> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 07: Generate Oracle SQL from the Target DB Model

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Oracle is the source of truth. Devin cannot reach Oracle, so it writes the SQL, pushes the branch, and CI applies it to a real Oracle and reports. Playbook 08 then derives the H2 test schema.

## Inputs

- Approved target DB model, ADRs, `docs/steering/glossary.md`, and the approved spec for the sections in scope
- Oracle version: `<VERSION, e.g. 21>` [CONFIRM the exact version and the COMPATIBLE setting]. The native JSON type is assumed not enabled: store JSON in CLOB.
- Naming conventions: `docs/db/naming-conventions.md` (**PROVISIONAL until the DBAs confirm**)
- Expected data volumes and main query paths: `<FILL IN>`
- Migration tool: `<LIQUIBASE or FLYWAY or WHATEVER CI USES>`

## Note: naming conventions are not confirmed yet

Before this runs for real, get the DBA conventions for object names, prefixes, maximum identifier length, schema and tablespace, audit columns and schema ownership. Until then use the provisional rules in `docs/db/naming-conventions.md`, mark every object provisional in the PR, keep names in one place, and do not invent conventions beyond those. Ask instead.

## Steps

1. Read the inputs. Summarize the model back in under 15 lines and list anything unclear before writing SQL.
2. Write migration scripts under `db/oracle/`, one logical group per file: tables, primary keys, foreign keys, unique and check constraints, indexes, sequences. Each table has one owning service. No foreign keys cross services.
3. Data types: `NUMBER` with explicit precision and scale for money and rates; record the `VARCHAR2` length semantics; decide `DATE` versus `TIMESTAMP` per column and record why. Never rely on an empty string: Oracle treats it as NULL, so the application normalizes it.
4. JSON and templates: store JSON in `CLOB` with `CHECK (column IS JSON)` where CI confirms it works on this setup, plus materialized query columns for anything searched or filtered. If `IS JSON` or `JSON_VALUE` fails in CI, remove it and say so.
5. No triggers and no PL/SQL unless an approved ADR says so. Audit columns and versioning live in the application.
6. Add `COMMENT ON` for every table and column.
7. Write the repository SQL: for every repository port method, one named resource at `src/main/resources/sql/oracle/<repository>/<method>.sql`. Parameterized, explicit columns, explicit ordering, explicit paging. No string concatenation.
8. Write `docs/db/data-dictionary.md`: per table the purpose, owning service, columns, rules enforced, and which queries use which indexes. Add the index and constraint rationale.
9. List every Oracle-specific construct used in the PR description.
10. Push the branch. CI applies the scripts to Oracle and runs the `oracle-integration` suites. Read the results, fix failures, repeat. Do not say the SQL is validated until CI is green.
11. Stop and wait for DBA review.

## Rules

- Never guess about the model. Unclear table, column, relationship or rule: stop and write a blocker report.
- No destructive statements (`DROP`, `TRUNCATE`, unbounded `DELETE` or `UPDATE`) without written approval.
- Do not change an approved script. Add a new migration.
- Mark assumptions GREEN, AMBER or RED in the data dictionary.

## Done when

- Migration scripts, repository SQL and the data dictionary are committed
- CI is green against Oracle
- The DBA has reviewed, and the naming conventions are confirmed or the provisional status is recorded
