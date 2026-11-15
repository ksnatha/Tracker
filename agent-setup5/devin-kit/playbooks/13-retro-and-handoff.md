> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 13: Retro and Handoff

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Combines the AI Hero retro and handoff methods. Run the retro after each feature or every few tickets. Run a handoff whenever a session must stop with work unfinished.

## Part A: Retro

The aim is to improve the agent's **environment** so future sessions go better, not to judge the agents.

### Inputs

- Feature: `<NAME>`
- Specs, plans, tickets, PRs and review comments for the feature, and the session logs you can read

### Steps

1. Read the primary sources: the PRs, review comments and tickets, plus the session logs if you can read them.
2. Write `<home repo>/docs/retros/<feature>.md` from the template: what worked, what surprised us, rules or decisions missing from the docs, tickets that were too large or badly ordered, review findings that repeated.
3. Look for improvements in these categories, with evidence, and list them in order of severity:
   - **Navigation:** was it slow to find the right files? Would a navigation pointer help?
   - **Automated checks:** did an agent make a mistake a check could catch (compile, format, static analysis, architecture tests, tests)? First read the repo's own build and CI configuration: a check that exists but is unwired or broken is the finding. A repo path with no guardrail is itself a finding.
   - **Coding standards:** classify each violation. A **mechanical** one (a banned API, an import or layering rule, a naming pattern) gets a deterministic check (for example an architecture test or a static analysis rule). Default to building the check over writing a rule. Keep prose standards for real judgement calls.
   - **Steering size:** is `AGENTS.md` or `DEVIN.md` large? Move what is mechanical into checks and what is a judgement into the standards file read at review. Steering files should mostly hold navigation pointers.
   - **No-ops:** instructions that do not change agent behavior. Propose removing them.
   - **Tool economy:** expensive or noisy tool calls that could be streamlined.
   - **Information access:** crucial information the agent could not see (CI logs, test reports, environment details). Propose how to expose it.
4. Apply only the updates I approve to the glossary, ADRs, design docs, `AGENTS.md`, `DEVIN.md` and the playbooks.

### Done when

- The retro is committed and the approved doc updates are made

## Part B: Handoff

Use when a session is ending with unfinished work, or when a fresh session should pick up from here.

### Steps

1. Ask what the next session will focus on and tailor the handoff to it.
2. Write `<home repo>/docs/handoffs/<date>-<topic>.md` from the template. Devin sessions do not share a file system, so the handoff lives in the repo, not in a temporary folder.
3. Include: current state, what is done, what is next, open questions, blockers, and which files to read first.
4. Do not duplicate content that exists elsewhere (specs, plans, ADRs, tickets, PRs, commits). Reference it by path or link.
5. Include a "Suggested playbooks" section naming which playbook files the next session should use.
6. Redact secrets, credentials and personal data.
7. Delete the handoff when it has been picked up and its content is in the real artifacts.

### Done when

- A fresh session could continue using only the handoff and the files it points to
