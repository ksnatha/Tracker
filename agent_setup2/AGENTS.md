# AGENTS.md

Guidelines and guardrails for AI coding agents (Devin and others) working in this repository.

This repo is part of a migration from a legacy monolith to modular Spring Boot services. The legacy system is the source of truth for behavior. Our job is to reproduce it faithfully, behind clean boundaries, in new code.

Fill in the `<PLACEHOLDERS>` once per repo before the first agent session.

---

## 1. Project context

- **Service name:** `<SERVICE_NAME>`
- **Business capability:** `<ONE-SENTENCE DESCRIPTION>`
- **Stack:** Java `<VERSION>`, Spring Boot `<VERSION>`, build tool `<MAVEN|GRADLE>`
- **Sibling repos:**
  - `contracts`: OpenAPI specs, event schemas, recorded fixtures (shared source of truth)
  - `parity-harness`: replays recorded traffic against legacy and new, and diffs results
  - `legacy-monolith`: **read-only reference**. Never modify, never copy code wholesale.
- **Current slice:** `<SLICE NAME / TICKET LINK>` (work only on this slice per session)

## 2. Environment limits (read first)

- The sandbox **cannot reach Oracle** and has **no Docker/Testcontainers or DB images**.
- Do not attempt to install, start, or emulate a database to "make tests work."
- Do not add Testcontainers, embedded Oracle, or Docker-based test dependencies.
- All persistence is accessed through **repository ports** (interfaces). Tests use **in-memory fakes** loaded from fixtures.
- If a task seems to require a real database, stop and report it instead of working around it.

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
4. **Never call Oracle directly** from `api`, `application`, or `domain`. Only `infrastructure/persistence` may.
5. **No imports from, or dependencies on, legacy monolith code or packages.** Re-implement from the behavior spec.
6. No cross-service database access. A service talks to other services through their contracts.
7. Constructor injection only. No field injection, no `@Autowired` on fields.
8. No static mutable state, no service locators, no reflection hacks to reach private legacy behavior.

## 4. Persistence and Oracle guardrails

- Adapters are written from the **DDL, SQL files, and stored-procedure signatures provided** in `docs/legacy/` (or the path named in the task). Do not guess schema.
- If a table, column, or query you need is not provided, **stop and ask**. Do not invent it.
- Write SQL explicitly and keep it in named, reviewable locations (repository methods or `.sql` resources). Avoid clever derived-query magic for anything non-trivial.
- Treat Oracle specifics as risky: `MERGE`, `ROWNUM`/`FETCH FIRST` paging, hierarchical queries (`CONNECT BY`), sequences, `DATE` vs `TIMESTAMP` semantics, empty string = `NULL`, `NUMBER` precision, triggers, PL/SQL. Flag each one you use in the PR description.
- Do **not** use H2 or any other database to claim Oracle parity. Adapter correctness is verified in our CI / shared dev environment, not in the agent sandbox. Mark such tests with `@Tag("oracle-integration")` and exclude them from the default sandbox test run.
- Never hardcode connection strings, credentials, or schema names. Use externalized configuration.
- Do not write or run destructive SQL (`DROP`, `TRUNCATE`, unbounded `DELETE`/`UPDATE`) anywhere, including migrations, without explicit written approval in the task.

## 5. Testing rules

Our safety net is **fixtures recorded from the legacy system**, not hand-invented expectations.

- Fixtures live in the `contracts` repo (consumed as a dependency) under `fixtures/<slice>/`.
- Write tests in this order for every slice:
  1. **Contract tests** against the OpenAPI/event schemas.
  2. **Parity tests** that load recorded legacy request/response pairs and assert the new code reproduces them.
  3. Unit tests for domain logic.
- Parity tests must be written **before** implementation and should fail first.
- **Never edit a fixture to make a test pass.** If a fixture looks wrong, stop and report it. The legacy output is the oracle unless a human says otherwise.
- Never delete, weaken, skip, or `@Disabled` a failing test to get green. Fix the code or report the discrepancy.
- Do not use mocks of the class under test. Prefer in-memory fakes for ports over Mockito stubs for repositories; use Mockito for outbound clients where a fake is overkill.
- Tests must be deterministic: inject `Clock`, ID generators, and random sources. No reliance on wall-clock time, timezone, or locale of the machine.
- Test naming: `methodName_condition_expectedResult`. One behavior per test.

## 6. Behavior preservation

- Do **not** "improve" behavior while migrating. Reproduce legacy results, including odd edge cases, rounding, null handling, ordering, and error messages, unless the spec explicitly says to change them.
- If legacy behavior looks like a bug, **replicate it, and note it** in the PR under "Suspected legacy defects." Fixes are a separate, approved change.
- Preserve: money and decimal handling (`BigDecimal` with explicit scale and `RoundingMode`), date/time zone handling, string trimming/case rules, sort order, pagination defaults, and HTTP status codes and error body shapes.
- Never use `double` or `float` for monetary or rate values.

## 7. Code standards

- Java `<VERSION>` features are fine (records for DTOs/value objects, `var` where it aids readability, switch expressions).
- Follow the existing formatter and static analysis config (`<CHECKSTYLE/SPOTLESS/ETC>`). Run it before committing.
- Prefer immutability. Use records or final fields for DTOs and value objects.
- Validate input at the API boundary with Bean Validation (`jakarta.validation`). Domain objects protect their own invariants.
- Use a consistent error model (`@ControllerAdvice` returning the problem format defined in `contracts`). Do not leak stack traces or SQL errors.
- Logging: SLF4J only. No `System.out`. **Never log PII, account numbers, credentials, tokens, or full request bodies.** Use correlation IDs from the incoming request.
- No `TODO` without a ticket reference. No commented-out code.
- Keep methods small and names domain-meaningful. Avoid utility classes that become dumping grounds.
- Add dependencies only when necessary. Check license and maintenance status, and list new dependencies in the PR description. Do not upgrade the Spring Boot or Java version unless the task says so.

## 8. Security and data handling

- Never commit secrets, keys, tokens, or real customer data. Fixtures must be **sanitized**. If you spot real-looking PII or credentials in a fixture, stop and report it without copying it elsewhere.
- Do not disable security, CSRF, authentication, or authorization checks to get a test passing.
- Do not make network calls from the sandbox to anything other than approved package registries.
- Treat all external input as untrusted. Parameterize all SQL. No string-concatenated queries.

## 9. Transactions and consistency

- Transaction boundaries belong in the `application` layer (`@Transactional` on use-case methods), not in controllers or adapters.
- Legacy logic that relied on one transaction spanning what is now multiple services needs an explicit design decision (saga, outbox, or keep together). **Do not decide silently.** Raise it in the spec or PR.
- Make writes idempotent where the contract calls for it, and document how.

## 10. Workflow for each slice

Work in three separate steps, with human review between them:

1. **Legacy to spec.** Read the legacy code (read-only) and write `docs/specs/<slice>.md`: business rules, edge cases, error handling, tables and SQL touched, Oracle-specific logic, transaction behavior, open questions. Stop for review.
2. **Spec and fixtures to tests.** Write the contract and parity tests in this repo. Confirm they fail for the right reason. Stop for review.
3. **Implement to green.** Build the service until contract and parity tests pass, with persistence behind fakes. Open a PR.

Keep each session scoped to **one slice**. If scope grows, stop and split the work.

## 11. Definition of done

A change is done only when all of these hold:

- [ ] Build passes: `<BUILD COMMAND, e.g. ./mvnw verify>`
- [ ] Contract tests and parity tests pass against the recorded fixtures
- [ ] Every legacy entry point in the slice inventory is accounted for: migrated, deliberately retired (with approval), or listed as an open question. Nothing is silently dropped.
- [ ] No fixture, test, or contract was modified to force a pass (or any such change is explained and approved)
- [ ] No imports from legacy code; no direct Oracle access outside `infrastructure/persistence`
- [ ] Static analysis and formatting pass
- [ ] No new secrets, PII, or unlogged sensitive data
- [ ] PR description lists: assumptions, Oracle-specific constructs used, suspected legacy defects, new dependencies, open questions
- [ ] Oracle-backed adapter tests are tagged `oracle-integration` and flagged for verification in CI / dev

## 12. When to stop and ask

Stop and report instead of guessing when:

- Schema, SQL, or stored-procedure details are missing or ambiguous
- A fixture contradicts the spec, or two fixtures contradict each other
- A spec rule is marked AMBER or RED and the task depends on it (see section 14)
- Legacy behavior is unclear, or appears to violate a stated requirement
- The task would require touching the legacy repo, another service's database, or a public contract
- A change would alter an API contract, event schema, or data ownership
- You are about to disable a test, loosen an assertion, or add a workaround for the environment limits in section 2

A clear, specific question is always better than a plausible-looking guess.

## 13. Commits and PRs

- Small, focused commits. Imperative subject line, with a ticket reference: `<TICKET-123> Add loan status mapper`.
- One slice per PR. Do not mix refactors with behavior changes.
- Never force-push to shared branches. Never commit directly to `main`.
- PR description must follow the template in `.github/pull_request_template.md` and include the items in section 11.

## 14. Confidence markers and traceability

Applies to every spec, rule, and extracted fact. This is the main guard against invented behavior.

- Every extracted fact or rule carries a **file:line reference** to the legacy code (or SQL/proc/config source). No reference means it does not count.
- Every fact carries a confidence marker:
  - **GREEN:** read directly in code; behavior is unambiguous.
  - **AMBER:** inferred from code, or code is clear but intent or an input is not (for example config-driven values, dynamic dispatch, data-dependent paths).
  - **RED:** could not be determined, or code appears contradictory. Do not implement from a RED rule; raise it as an open question.
- **Golden rules:**
  1. Never invent or assume behavior. If the code is ambiguous, say so in the output and mark it AMBER or RED.
  2. Never fill gaps with "typical" behavior for this kind of system.
  3. Never silently drop an entry point, branch, or condition. Account for each one (rule ID, classified as technical plumbing, or open question).
  4. Describe what the code does, not what names or comments claim it does.
- Humans review AMBER and RED items first. GREEN items are spot-checked.
- A complete, well-formatted spec is not proof of correctness. A spec can faithfully carry forward a legacy bug or an obsolete rule; those go under "Suspected defects" for expert decision.

## 15. Shared context: steering files and ADRs

AI sessions do not remember each other. What one session learned about module A is not available in the next session on module B, so the repo carries the shared memory.

- **Before starting, read:**
  - `docs/steering/` for domain glossary, naming conventions, rounding/date/money rules that apply across slices, and known legacy quirks
  - `docs/adr/` for architecture decision records. Follow decisions already made; do not re-decide them.
  - `docs/specs/` for specs of neighboring slices, when your slice calls into them
- **Before finishing, update:**
  - Add any new cross-slice learning (a glossary term, a recurring legacy quirk, a convention) to `docs/steering/`
  - If you made or proposed a significant structural choice (new boundary, data ownership, transaction approach), write an ADR in `docs/adr/` and flag it for human approval. Do not treat an unapproved ADR as settled.
- Keep steering files short, factual, and current. If you find one that contradicts the code or another file, report it instead of picking a side.
- Do not copy secrets, PII, or real customer data into steering files or ADRs.
