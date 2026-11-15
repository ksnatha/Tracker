> **FILL-IN INSTRUCTIONS (delete this block when done):** This file has one-time placeholders written like `<THIS>`. Search the file for `<` and replace each with your real value. Items marked [CONFIRM] are assumptions to check. Do not start a Devin session that relies on this file until no placeholders remain.

# Home repo: <PROJECT NAME>

This is the home of the whole modernization effort. It holds the design documents, decisions, plans, tickets and retros that every Devin session reads. Code lives in the service repos. Playbooks are attached to Devin sessions (copies are in `playbooks/` of the kit).

## Repos

| Repo | Purpose | Access for Devin |
|---|---|---|
| `<HOME REPO NAME>` (this repo) | Design, plans, tickets, retros | Read and write docs |
| `<SERVICE REPO 1>` | `<SERVICE NAME>` | Read and write |
| `contracts` | OpenAPI specs, event schemas, fixtures | Read; write only when the task says so |
| `parity-harness` | Replays recorded inputs against legacy and new | Read |
| `<LEGACY BACKEND REPO>` | Legacy backend | **Read-only** |
| `<LEGACY UI REPO>` | Legacy UI | **Read-only** |

## Layout

```
docs/
├── design/            target architecture, DB model, boundary comparison, independent designs
├── design-gaps.md     gaps found while validating the design
├── open-questions/    questions for SMEs, one file per slice
├── plans/             build plan per feature or service
├── tickets/           one file per ticket, with status
├── retros/            one retro per feature
└── adr/               cross-service decisions (service-level ADRs live in each service repo)
```

## How a session starts

1. Read `AGENTS.md` in this repo, then the `AGENTS.md` and `DEVIN.md` of the repo you are working in.
2. Attach the playbook for the step from the kit.
3. Name the slice or ticket and the exact paths of the input documents.

## Status

- Current phase: `<e.g. design validation>`
- Next approval needed: `<WHAT AND FROM WHOM>`
