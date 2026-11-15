Use only playbooks/01-validate-module-boundaries.md. Do not open, read or follow
any other playbook, even if one is mentioned or listed. Run Part A only.

First read, in full: the playbook, AGENTS.md and DEVIN.md in this repo.
Then restate the scope in 2-3 lines and wait for my OK before starting.

Values for the placeholders:
- Legacy backend (read-only): <REPO NAME / URL>
- Legacy UI (read-only): <REPO NAME / URL>
- Devin knowledge wiki: <NAME OR LINK>, treat it as a starting point and verify against the code
- Oracle: 21 (JSON type not enabled, CLOB for JSON)
- Scale: <N questions across N sections, N loans / users>
- Integrations: <LIST OF EXTERNAL SYSTEMS, APIs, JOBS>
- Team ownership: <WHO OWNS WHAT, OR "not applicable">

Rules for this run:
- Part A is the independent design. Do not read docs/design/architecture.md,
  docs/design/db-model.md or anything under docs/design/poc/. Do not look for
  the target design anywhere else.
- Write outputs only to the paths the playbook lists, under docs/design/independent/.
- Child sessions are allowed for the design-it-twice options and for evidence
  gathering. Give each child one narrow task, the one playbook section to
  follow, and a named file to write to. Children do not decide anything.
- If you cannot continue, stop and post a BLOCKER report (format in DEVIN.md).
- If the playbook mentions another playbook or step, treat it as a note for a
  human. Do not do that work.
- Stop at the end of Part A, open a PR, and wait for my approval. Do not start Part B.
