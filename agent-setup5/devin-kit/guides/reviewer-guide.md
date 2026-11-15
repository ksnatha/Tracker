> **FILL-IN INSTRUCTIONS (delete this block when done):** This file has one-time placeholders written like `<THIS>`. Search the file for `<` and replace each with your real value. Items marked [CONFIRM] are assumptions to check. Do not start a Devin session that relies on this file until no placeholders remain.

# Reviewer Guide

How humans review each stage. Devin proposes; you decide.

## Reviewing a behavior spec (playbook 04)

- **Review AMBER and RED first.** Start at the review summary (section 14). Spend expert time on ambiguity and spot-check GREEN rules.
- **Spot-check evidence.** Pick 5 to 10 rules at random and verify the `file:line` citations say what the spec claims.
- **Check entry point and field reconciliation.** Compare section 15 against an independent list (routes, scheduler config, message listeners, form definitions, DB columns). Missing items mean silently dropped behavior.
- **Chase the Oracle section.** Triggers and procs hide rules. Ask a DBA to confirm nothing is missing by querying `ALL_TRIGGERS`, `ALL_SOURCE` and dependency views for the slice tables.
- **Compare against fixtures.** For a few recorded inputs and outputs, walk the spec by hand and confirm it predicts the recorded output.
- **A complete spec can still be wrong.** It may carry forward a legacy bug or an obsolete rule. Ask experts what should be retired or changed.
- **Freeze the slice during analysis.** Ask the legacy team to avoid changing that code, or re-run the extraction on changed files.
- **Answer open questions in the spec or the open-questions file**, not in chat. Mark who answered and when.
- **Watch review capacity.** Extraction runs in parallel; reviewers may not. Plan reviewer time before launching many slices.

## Reviewing decisions (playbook 05)

- Every CHANGE or RETIRE needs a reason, an approver and a date.
- Anything with no decision stays PRESERVE. Do not leave rules undecided silently.

## Reviewing design and ADRs (playbooks 01 to 03)

- Check that each ADR is backed by evidence from the legacy code, not only by argument.
- Treat the independent design as a second opinion. Look for coupling, shared tables, transaction boundaries and co-change evidence.
- Only a human moves an ADR from `proposed` to `approved`.

## Reviewing Oracle SQL and H2 (playbooks 07 and 08)

- DBA review of names, types, indexes, constraints and the data dictionary. Confirm or keep the provisional status of naming.
- Check the H2 translation notes for every difference and confirm each is tested where it can be.
- Confirm the Oracle suites ran in CI and passed.

## Reviewing a PR (playbooks 10 to 12)

- Every acceptance criterion has a test. Every implemented rule has an approved decision.
- No fixture, test or expected value was edited to force a pass.
- Module boundaries and table ownership match the ADRs.
- CI Oracle results are green.
- Merge yourself; Devin does not merge its own PRs.
