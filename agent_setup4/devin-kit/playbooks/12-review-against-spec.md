> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 12: Review Against Spec

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run in a fresh session, not the one that wrote the code. Based on the AI Hero code-review idea.

## Inputs

- Pull request or branch: `<LINK OR NAME>`
- The ticket, plan, spec rules and decisions, ADRs, glossary

## Steps

1. Review the diff against the spec and the ticket, not only against general code quality.
2. Check every acceptance criterion has a test and every parity case from the spec is covered.
3. Check every implemented rule has an approved decision. Flag any implemented rule that is RETIRED or has no decision.
4. Check ADR conformance, module and service boundaries, table ownership, naming against the glossary, error handling, security and audit.
5. Check test quality: tests assert behavior not implementation, none are empty or always pass, no fixture or expected value was edited.
6. Check persistence: SQL matches the approved Oracle files, no H2-only behavior in production code, Oracle suites passed in CI, H2 differences are recorded.
7. Check the template JSON validates and matches the approved inventory.
8. List findings with severity (blocker, major, minor), file and line, and a suggested fix. Do not change code unless asked.
9. Give a verdict: approve, or changes required. Say whether the ticket status is correct.

## Done when

- The review is posted on the PR with a clear verdict
