> **FILL-IN INSTRUCTIONS (delete this block when done):** This file has one-time placeholders written like `<THIS>`. Search the file for `<` and replace each with your real value. Items marked [CONFIRM] are assumptions to check. Do not start a Devin session that relies on this file until no placeholders remain.

# AGENTS.md

Guidelines and guardrails for AI coding agents (Devin and others) working in this repository.

This repo is part of a modernization of a legacy application into modular Spring Boot services. The legacy system is the source of truth for **business rules**. The UI, database model, module boundaries and architecture are being deliberately redesigned, and those decisions live in `docs/adr/`. Our job is to preserve the approved business rules, behind the new design, in new code.

Fill in the `<PLACEHOLDERS>` once per repo before the first agent session.

---

## 1. Project context

- **Service name:** `<SERVICE_NAME>`
- **Business capability:** `<ONE-SENTENCE DESCRIPTION>`
- **Stack:** Java `<VERSION>`, Spring Boot `<VERSION>`, build tool `<MAVEN|GRADLE>`
- **Production database:** Oracle `<VERSION, e.g. 21>`. The native JSON type is not assumed to be enabled: store JSON in CLOB.
- **Sibling repos:**
  - `home` (docs repo): design docs, ADRs, steering files, playbooks, specs and tickets for the whole effort `<REPO NAME>`
  - `contracts`: OpenAPI specs, event schemas, recorded fixtures (shared source of truth)
  - `parity-harness`: replays recorded inputs against legacy and new, and diffs results
  - `legacy-monolith` and `legacy-ui`: **read-only references**. Never modify, never copy code wholesale.
- **Current slice:** `<SLICE NAME / TICKET LINK>` (work only on this slice per session)

## 2. Environment limits (read first)

- The sandbox **cannot reach Oracle** and has **no Docker/Testcontainers or database images**.
- Do not attempt to install, start, or emulate Oracle. Do not add Testcontainers, embedded Oracle, or Docker-based test dependencies.
- **H2 is allowed for sandbox tests only.** It is a test aid, never proof of Oracle behavior (see section 4).
- **CI can reach a real Oracle** and runs the `oracle-integration` suites. A change is not done until those pass in CI (see section 11).
- All persistence is accessed through **repository ports** (interfaces). Fast tests use in-memory fakes or H2.
- If a task seems to require real Oracle behavior, rely on CI, say so in the PR, and do not work around it in the sandbox.

## 3. Architecture rules

Package layout (hexagonal / ports and adapters):

```
com.<org>.<service>
├── api/            REST controllers, request/response DTOs (thin, no business logic)
├── application/    use cases / services (orchestration, transactions)
├── domain/         entities, value objects, domain services, repository PORTS (interfaces)
├── infrastructure/
│   ├── persistence/   Oracle-backed adapters (JPA/JDBC) implementing the ports
│   └── client/        outbound HTTP/messaging adapters
└── config/         Spring configuration only
```

Hard rules:

1. `domain` has **no** Spring, JPA, or JDBC imports. Plain Java only.
2. `api` depends on `application`; `application` depends on `domain`; `infrastructure` implements `domain` ports. Dependencies point inward only.
3. Controllers contain no business logic and never touch repositories.
4. **Only `infrastructure/persistence` may access the database.** Never from `api`, `application`, or `domain`.
5. **No imports from, or dependencies on, legacy code or packages.** Re-implement from the approved spec.
6. No cross-service database access. A service talks to other services through their contracts. Each table has exactly one owning service.
7. Constructor injection only. No field injection, no `@Autowired` on fields.
8. No static mutable state, no service locators, no reflection hacks to reach private legacy behavior.
9. **Service and module boundaries come from approved ADRs.** Do not create, merge or split services or modules without an approved ADR.

## 4. Persistence: Oracle first, H2 derived

Oracle is the source of truth for the schema and SQL. H2 exists so tests can run in the sandbox.

- **New schema:** Oracle migration scripts live in `db/oracle/`. Repository SQL lives as named `.sql` resources under `src/main/resources/sql/oracle/<repository>/<method>.sql`. These come from the approved target DB model.
- **Legacy DDL in `docs/legacy/`** is a reference for understanding legacy behavior. It is not the schema to build against.
- **H2 test schema** is derived one to one from the approved Oracle scripts, and lives under `src/test/resources/h2/`. Do not edit the Oracle files to suit H2. If the Oracle files need to change, stop and raise it.
- Where an Oracle query cannot run on H2, add an H2 variant with the same file name under `src/test/resources/sql/h2/`, and list it in `docs/db/h2-translation-notes.md`. Keep variants to a minimum.
- If a table, column, or query you need is not in the approved model, **stop and ask**. Do not invent it.
- **Naming conventions are provisional** until the DBAs confirm them (`docs/db/naming-conventions.md`). Mark new objects as provisional in the PR. Keep names in one place so renaming is mechanical.
- Store JSON in `CLOB`, with a `CHECK (column IS JSON)` constraint where CI confirms it works, plus materialized query columns for anything searched or filtered. Never query JSON content from code paths that must also run on H2.
- No triggers and no PL/SQL unless an approved ADR says so. Audit and versioning logic belongs in the application.
- Write SQL explicitly. Avoid clever derived-query magic for anything non-trivial. Parameterize everything.
- Treat Oracle specifics as risky: `MERGE`, `FETCH FIRST`/`ROWNUM` paging, hierarchical queries (`CONNECT BY`), sequences, `DATE` vs `TIMESTAMP` semantics, empty string = `NULL`, `NUMBER` precision, JSON functions. Flag each one you use in the PR description.
- **Never claim Oracle parity from an H2 result.** Persistence tests are written once as a shared suite: the H2 subclass runs in the sandbox, and the Oracle subclass is tagged `@Tag("oracle-integration")`, excluded from the default sandbox run, and runs in CI. A test that only passes because of H2 behavior is a defect.
- Never hardcode connection strings, credentials, or schema names. Use externalized configuration.
- Do not write or run destructive SQL (`DROP`, `TRUNCATE`, unbounded `DELETE`/`UPDATE`) anywhere, including migrations, without explicit written approval in the task. Add new migrations instead of editing approved ones.

## 5. Testing rules

Our safety net is **recorded legacy data and approved rule examples**, not hand-invented expectations. Because the UI and APIs are being redesigned, parity is tested at the **business rule and service level**, not by replaying legacy HTTP requests.

Test layers and where they run:

| Layer | Tools | Where |
|---|---|---|
| Unit | JUnit 5, Mockito | Sandbox |
| Integration (Spring) | Spring Boot tests with H2 | Sandbox |
| Oracle integration | Same persistence suite on Oracle, `@Tag("oracle-integration")` | CI |
| Contract | OpenAPI and event schemas | Sandbox and CI |
| Rule-level parity | Legacy inputs and outputs for rules | Sandbox and CI |
| UI end-to-end | Selenium (or Playwright) | CI, added once the UI and API are stable |
| Load | `<GATLING|JMETER|K6>` | CI or a dedicated environment, added later |

Rules:

- Fixtures live in the `contracts` repo (consumed as a dependency) under `fixtures/<slice>/`. Rule-level parity cases come from recorded legacy inputs and outputs. Where none exist, a **derived example** (worked out from the code) is allowed only if it is marked AMBER in the spec and approved by a human.
- Write tests in this order for every slice:
  1. **Contract tests** against the OpenAPI/event schemas.
  2. **Rule-level parity tests** that load the recorded or approved inputs and assert the new code reproduces the expected outputs.
  3. Unit tests for domain logic.
- Parity tests must be written **before** implementation and should fail first.
- **Never edit a fixture or an expected value to make a test pass.** If a fixture looks wrong, stop and report it. The legacy output is the oracle unless a human says otherwise.
- Never delete, weaken, skip, or `@Disabled` a failing test to get green. Fix the code or report the discrepancy.
- Do not use mocks of the class under test. Prefer in-memory fakes for ports over Mockito stubs for repositories; use Mockito for outbound clients where a fake is overkill.
- Tests must be deterministic: inject `Clock`, ID generators, and random sources. No reliance on wall-clock time, timezone, or locale of the machine.
- Test naming: `methodName_condition_expectedResult`. One behavior per test.
- Coverage thresholds are enforced by the pipeline gates: `<THRESHOLDS>`. Do not lower them.

## 6. Business rule preservation and intended change

- The approved spec marks every legacy rule **PRESERVE**, **CHANGE** or **RETIRE**, with who approved it. Implement only what is approved.
- **PRESERVE:** reproduce legacy results exactly, including odd edge cases, rounding, null handling, ordering and error messages.
- **CHANGE or RETIRE:** implement the approved new behavior, or leave it out. Do not make the decision yourself.
- Anything with no decision recorded is **not yours to change**: treat it as PRESERVE and raise a question.
- UI, API shape, DB model and internal structure are redesigned per the ADRs. Do not copy legacy structure into the new design.
- If legacy behavior looks like a bug and no decision exists, **replicate it, and note it** in the PR under "Suspected legacy defects." Fixes are a separate, approved change.
- Always preserve: money and decimal handling (`BigDecimal` with explicit scale and `RoundingMode`), date/time zone handling, string trimming/case rules, sort order, and pagination defaults, unless a decision changes them.
- Never use `double` or `float` for monetary or rate values.

## 7. Code standards

- Java `<VERSION>` features are fine (records for DTOs/value objects, `var` where it aids readability, switch expressions).
- Follow the existing formatter and static analysis config (`<CHECKSTYLE/SPOTLESS/ETC>`). Run it before committing.
- Prefer immutability. Use records or final fields for DTOs and value objects.
- Validate input at the API boundary with Bean Validation (`jakarta.validation`). Domain objects protect their own invariants.
- Use a consistent error model (`@ControllerAdvice` returning the problem format defined in `contracts`). Do not leak stack traces or SQL errors.
- Logging: SLF4J only. No `System.out`. **Never log PII, account numbers, credentials, tokens, or full request bodies.** Use correlation IDs from the incoming request.
- No `TODO` without a ticket reference. No commented-out code.
- Keep methods small and names domain-meaningful. Use the terms in `docs/steering/glossary.md`. Avoid utility classes that become dumping grounds.
- Add dependencies only when necessary. Check license and maintenance status, and list new dependencies in the PR description. Do not upgrade the Spring Boot or Java version unless the task says so.

## 8. Security and data handling

- Never commit secrets, keys, tokens, or real customer data. Fixtures and H2 seed data must be **sanitized**. If you spot real-looking PII or credentials in a fixture, stop and report it without copying it elsewhere.
- Do not disable security, CSRF, authentication, or authorization checks to get a test passing.
- Do not make network calls from the sandbox to anything other than approved package registries.
- Treat all external input as untrusted. Parameterize all SQL. No string-concatenated queries.

## 9. Transactions and consistency

- Transaction boundaries belong in the `application` layer (`@Transactional` on use-case methods), not in controllers or adapters.
- Legacy logic that relied on one transaction spanning what is now multiple services needs an explicit design decision (saga, outbox, or keep together). **Do not decide silently.** Raise it in the spec or an ADR.
- Make writes idempotent where the contract calls for it, and document how.

## 10. Workflow

Work in separate steps, with human review between them. Each step has its own playbook.

0. **Design.** Validate module boundaries and the target architecture, and record the decisions as ADRs. Stop for approval. ADRs stay `proposed` until a human approves them.
1. **Legacy to spec.** Read the legacy code (backend and UI, read-only) and write `docs/specs/<slice>.md`: business rules, edge cases, error handling, tables and SQL touched, Oracle-specific logic, transaction behavior, open questions, and the PRESERVE, CHANGE or RETIRE decision for each rule. For questionnaire sections, also produce the field catalog and the template JSON. Stop for review.
2. **Database.** Generate the Oracle SQL from the approved DB model, validate it in CI, then derive the H2 test schema. Stop for DBA review.
3. **Plan and tickets.** Turn the approved spec and ADRs into a build plan and thin vertical tickets. Stop for approval.
4. **Spec and fixtures to tests.** Write the contract and rule-level parity tests. Confirm they fail for the right reason. Stop for review.
5. **Implement to green.** One ticket per session, test-first. Build until the tests pass in the sandbox, then confirm the Oracle suite is green in CI. Open a PR.
6. **Review and retro.** Review each PR in a fresh session against the spec. After each feature, record lessons and update steering files and playbooks.

Keep each session scoped to **one slice** (or one ticket). If scope grows, stop and split the work.

## 11. Definition of done

A change is done only when all of these hold:

- [ ] Build passes: `<BUILD COMMAND, e.g. ./mvnw verify>`
- [ ] Contract tests and rule-level parity tests pass
- [ ] The `oracle-integration` suites pass in CI against real Oracle
- [ ] Every legacy entry point in the slice inventory is accounted for: migrated, deliberately retired (with approval), or listed as an open question. Nothing is silently dropped.
- [ ] Every implemented rule has an approved PRESERVE, CHANGE or RETIRE decision
- [ ] No fixture, test, expected value or contract was modified to force a pass (or any such change is explained and approved)
- [ ] No imports from legacy code; no database access outside `infrastructure/persistence`
- [ ] Static analysis, formatting and coverage thresholds pass
- [ ] No new secrets, PII, or unlogged sensitive data
- [ ] PR description lists: assumptions, Oracle-specific constructs used, H2 differences, provisional names, suspected legacy defects, new dependencies, open questions

## 12. When to stop and ask

Stop and report instead of guessing when:

- The approved DB model, schema or SQL is missing, ambiguous, or conflicts with the Oracle scripts
- A fixture contradicts the spec, or two fixtures contradict each other
- A spec rule is marked AMBER or RED and the task depends on it (see section 14)
- A rule has no PRESERVE, CHANGE or RETIRE decision
- Legacy behavior is unclear, or appears to violate a stated requirement
- The task would require touching the legacy repos, another service's database, or a public contract
- A change would alter an API contract, event schema, module boundary or data ownership, and no approved ADR covers it
- A naming convention question cannot be answered from `docs/db/naming-conventions.md`
- The Oracle suite fails in CI and you cannot explain why
- You are about to disable a test, loosen an assertion, or add a workaround for the environment limits in section 2

A clear, specific question is always better than a plausible-looking guess.

## 13. Commits and PRs

- Small, focused commits. Imperative subject line, with a ticket reference: `<TICKET-123> Add loan status mapper`.
- One slice (or ticket) per PR. Do not mix refactors with behavior changes.
- Never force-push to shared branches. Never commit directly to `main`.
- PR description must follow the template in `.github/pull_request_template.md` and include the items in section 11.

## 14. Confidence markers and traceability

Applies to every spec, rule, and extracted fact. This is the main guard against invented behavior.

- Every extracted fact or rule carries a **file:line reference** to the legacy code (backend, UI, SQL, proc, or config source). No reference means it does not count.
- Every fact carries a confidence marker:
  - **GREEN:** read directly in code; behavior is unambiguous.
  - **AMBER:** inferred from code, or code is clear but intent or an input is not (for example config-driven values, dynamic dispatch, data-dependent paths). Derived test examples are AMBER until approved.
  - **RED:** could not be determined, or code appears contradictory. Do not implement from a RED rule; raise it as an open question.
- **Golden rules:**
  1. Never invent or assume behavior. If the code is ambiguous, say so in the output and mark it AMBER or RED.
  2. Never fill gaps with "typical" behavior for this kind of system.
  3. Never silently drop an entry point, branch, or condition. Account for each one (rule ID, classified as technical plumbing, or open question).
  4. Describe what the code does, not what names or comments claim it does.
- Humans review AMBER and RED items first. GREEN items are spot-checked.
- A complete, well-formatted spec is not proof of correctness. A spec can faithfully carry forward a legacy bug or an obsolete rule; those go under "Suspected defects" for expert decision.

## 15. Shared context: steering files, ADRs and where things live

AI sessions do not remember each other. What one session learned about module A is not available in the next session on module B, so the repos carry the shared memory.

- **Before starting, read:**
  - `docs/steering/` for the domain glossary (`glossary.md`), naming conventions, rounding/date/money rules that apply across slices, and known legacy quirks
  - `docs/adr/` for architecture decision records. Follow decisions that are `approved`; do not re-decide them. Treat a `proposed` ADR as unsettled.
  - `docs/specs/` for specs of neighboring slices, when your slice calls into them
  - `docs/design/` (in the `home` repo) for the target architecture, DB model and boundary decisions
- **Before finishing, update:**
  - Add any new cross-slice learning (a glossary term, a recurring legacy quirk, a convention) to `docs/steering/`
  - If you made or proposed a significant structural choice (new boundary, data ownership, transaction approach), write an ADR in `docs/adr/` with status `proposed` and flag it for human approval
- Keep steering files short, factual, and current. If you find one that contradicts the code or another file, report it instead of picking a side.
- Do not copy secrets, PII, or real customer data into steering files or ADRs.

Where things live:

- Design, boundary comparison, design gaps, tickets, retros: `home` repo `docs/`
- Specs, ADRs, steering files: the repo named in the task (default: this repo's `docs/`)
- Oracle scripts and data dictionary: `db/oracle/` and `docs/db/` in the owning service repo
- Fixtures and contracts: `contracts` repo
