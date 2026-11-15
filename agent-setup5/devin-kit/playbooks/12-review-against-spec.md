> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 12: Review Against Spec

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run in a fresh session, not the one that wrote the code. Based on the AI Hero code-review method: two separate axes, **Spec** and **Standards**, reported side by side and never merged, so one cannot mask the other.

## Inputs

- Pull request or branch: `<LINK OR NAME>`
- Fixed point to review against: `<main, a commit, or a tag>` (if none is given, ask)
- The ticket, plan, spec rules and decisions, ADRs, glossary
- Standards sources: `AGENTS.md`, `<CODING_STANDARDS.md or CONTRIBUTING.md, if they exist>`, `docs/steering/`

## Steps

1. **Pin the fixed point.** Capture the diff once: `git diff <fixed-point>...HEAD` (three dots, so it compares against the merge-base) and `git log <fixed-point>..HEAD --oneline`. Check that the ref resolves and the diff is not empty. Fail here, not later.
2. **Find the spec source,** in this order: issue or ticket references in the commit messages, a path I gave, a spec or plan file matching the feature. If none exists, ask me. If there is none, skip the Spec axis and say so.
3. **Run the two axes.** Start two **child sessions** in parallel, one per axis, each with a fresh context and briefed with context pointers (the diff command, the spec source, the standards files and the smell list below). Each reports its findings back to you (the parent) in the format below. If child sessions are not possible, do the axes one after the other, in separate sections, without letting the first influence the second.

**Spec axis.** Report with a quote of the spec, ticket or rule line for each finding:
- Requirements or acceptance criteria that are missing or only partly done
- Behavior in the diff that was not asked for (scope creep)
- Requirements that look implemented but look wrong
- Every implemented rule has an approved decision; flag any rule that is RETIRED or has no decision
- Every acceptance criterion has a test and every parity case from the spec is covered
- Persistence: SQL matches the approved Oracle files, no H2-only behavior in production code, the Oracle suites passed in CI, H2 differences are recorded
- Template JSON validates and matches the approved inventory

**Standards axis.** Report each finding against the documented standard it breaks (file and rule):
- Breaches of `AGENTS.md`, ADRs, module and service boundaries, table ownership, glossary naming, error handling, security and audit
- Test quality: tests assert behavior not implementation, none empty or always passing, no fixture or expected value edited
- Code smells as judgement calls, never hard violations: mysterious names, duplicated code, feature envy, data clumps, primitive obsession, repeated switches, shotgun surgery, divergent change, speculative generality, message chains, middle man, refused bequest
- A documented standard overrides the smell baseline. Skip anything the build, formatter or static analysis already enforces.

4. **Report.** Present `## Spec` and `## Standards` separately. Label each finding blocker, major or minor within its axis. Do not merge or re-rank across axes. End with one line: findings per axis and the worst one within each axis. Keep each axis under about 400 words unless there are blockers.
5. Do not change code unless asked. Say whether the ticket status is correct and whether the PR is approved or needs changes.

## Done when

- The review is posted on the PR with both axes and a clear verdict
