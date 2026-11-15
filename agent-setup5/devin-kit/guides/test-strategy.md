> **FILL-IN INSTRUCTIONS (delete this block when done):** This file has one-time placeholders written like `<THIS>`. Search the file for `<` and replace each with your real value. Items marked [CONFIRM] are assumptions to check. Do not start a Devin session that relies on this file until no placeholders remain.

# Test Strategy

Goal: automated coverage that avoids manual testing, expanding over time. Copy this to `<home repo>/docs/design/test-strategy.md` and approve it as an ADR.

## Layers

| Layer | Tools | Where it runs | When added |
|---|---|---|---|
| Unit | JUnit 5, Mockito | Devin sandbox | From the first ticket |
| Integration (Spring, persistence) | Spring Boot tests with H2 | Devin sandbox | From the first ticket |
| Oracle integration | Same persistence suite on Oracle, `@Tag("oracle-integration")` | CI (real Oracle) | From the first persistence ticket |
| Contract | OpenAPI and event schemas | Sandbox and CI | From the first API ticket |
| Rule-level parity | Recorded legacy inputs and outputs, or approved derived examples | Sandbox and CI | Before implementing each slice |
| UI end-to-end | `<SELENIUM or PLAYWRIGHT>` | CI | Once UI and API are stable (playbook 14) |
| Load | `<GATLING or JMETER or K6>` | CI or a dedicated environment | After functional tests are stable (playbook 14) |

## Principles

- Test behavior at seams, not internals. The deep, small interfaces from the design are the seams.
- The pyramid stays wide at the bottom: many unit and integration tests, few end-to-end tests.
- Parity is at the business rule and service level, not replayed legacy HTTP, because the UI and APIs are redesigned.
- H2 is a test aid. Anything depending on Oracle behavior is tested in CI only.
- Tests are deterministic: inject clock, ids and random sources.
- **Agree the seams before writing tests.** A seam is the public interface where behavior is observed. Existing seams are preferred, the highest seam possible, and as few as possible. No test is written at an unconfirmed seam.
- **Avoid three anti-patterns:** implementation-coupled tests (mocking internal collaborators, testing private methods, checking through the database instead of the interface); tautological tests (the expected value recomputed the way the code computes it; expected values must come from an independent source such as a recorded output or a worked example); and horizontal slicing (all unit tests first, then all code).
- **Contract and parity tests are written up front** because their expected values are recorded or specified, not imagined. **Unit tests are written one at a time inside each ticket**, red then green, one test and one minimal implementation per cycle.
- **Refactoring is not part of the red-green loop.** Wider refactors belong to the review step or a new ticket.

## Coverage and gates

- Coverage thresholds (enforced by the pipeline gates): `<LINE %, BRANCH %, PER LAYER IF DIFFERENT>`
- New code coverage threshold: `<%>`
- Consider mutation testing (`<PIT or similar>`) to check that AI-written tests actually catch defects: `<yes/no, when>` [CONFIRM]

## Test data and fixtures

- Fixtures live in the `contracts` repo under `fixtures/<slice>/` and are sanitized (no real PII).
- Rule-level parity cases: recorded legacy inputs and outputs; where none exist, derived examples marked AMBER until approved.
- Closed legacy records, if sanitized, are a source of real inputs and outcomes for parity: `<yes/no, source>` [CONFIRM]
- Test data creation for UI and load tests: `<APPROACH>`

## What each phase needs before it can start

- UI end-to-end: stable UI selectors and a deployed test environment
- Load: a defined load profile and thresholds from the legacy baseline
