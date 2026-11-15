> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 14: UI End-to-End and Load Tests (run later)

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. **Do not run this until the UI and the API are stable.** These tests run in CI or a dedicated environment, not in the Devin sandbox (the sandbox may have no browser and cannot generate load).

## Inputs

- Stable UI build and API contracts: `<LINKS>`
- UI test tool: `<SELENIUM or PLAYWRIGHT>`; load tool: `<GATLING or JMETER or K6>` [CONFIRM]
- Critical user journeys to cover: `<LIST, e.g. create a loan, complete each section, submit, approve>`
- Load profile and targets from the legacy baseline: `<USERS, REQUESTS PER SECOND, RESPONSE TIME TARGETS>` [CONFIRM from production metrics]
- Test environment and test data approach: `<ENVIRONMENT, SANITIZED DATA>`

## Part A: UI end-to-end

1. Write page objects and stable selectors (agree test ids with the UI team). No sleeps; use explicit waits.
2. Write one end-to-end test per critical journey, plus the visibility, validation and mandatory-if behaviors from the template rules.
3. Keep tests independent and data-isolated. Create and clean up their own test data.
4. Run in CI. Record screenshots and traces on failure.
5. Keep the suite small and fast; push detail down to unit, integration and rule-level tests.

## Part B: Load

1. Define scenarios from the load profile. Include the peak, a soak run and a spike.
2. Write the scripts with realistic data and think time.
3. Set pass or fail thresholds (response time percentiles, error rate, throughput) from the legacy baseline, and agree them with a human before running.
4. Run in the dedicated environment only. Record results and compare with the legacy baseline.

## Rules

- Never point load tests at production or a shared environment without written approval.
- Never use real customer data.
- A flaky test is a defect: fix or remove it, do not retry until green.

## Done when

- The journeys are automated and green in CI
- Load scripts, thresholds and a first baseline report are committed
