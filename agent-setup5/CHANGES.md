# Changes: playbooks aligned with the AI Hero skills

This file summarizes what changed in the kit after reading the actual skill files. It is not part of the zip.

## What I read

From the `mattpocock/skills` repo (main branch): `codebase-design` (SKILL, DEEPENING, DESIGN-IT-TWICE), `improve-codebase-architecture`, `domain-modeling`, `grilling`, `grill-with-docs`, `wayfinder`, `research`, `prototype`, `to-spec`, `to-tickets`, `implement`, `implement-spec`, `tdd`, `code-review`, `retro`, `handoff`, `to-questionnaire`.

Not read: `HTML-REPORT.md`, the glossary and ADR format files, `tdd` `tests.md` and `mocking.md`, `writing-for-agents`, `setup-matt-pocock-skills`, `diagnosing-bugs`, `ask-matt`.

## Per playbook

| # | Playbook | Added from the skills | Kept from mine | Ignored |
|---|---|---|---|---|
| 01 | Module boundaries | Vocabulary (module, interface, depth, seam, adapter, leverage, locality); deletion test; interface is the test surface; one adapter is hypothetical, two is real; dependency categories with ports and adapters; structured design-it-twice (problem note, four constraints, per-option outputs, recommendation); decision round in grilling format; ADR three gates and ADR for lasting rejections; domain-modeling behaviors; glossary free of implementation details | Blind independent design; evidence (access matrix, dependency graph, co-change); comparison with the target design; gap list | HTML report, strength badges |
| 02 | Target architecture | Wayfinder: destination, map, decision tickets with types (research, prototype, grilling, task), fog of war, one ticket per session, claim before work; research from primary sources; throwaway prototypes on a branch | My option comparison criteria; preference handling; confidence markers | Issue-tracker mechanics (map is a markdown file) |
| 03 | Validate target design | Grilling frontier rounds with recommended answers; agent finds facts, human decides; domain-modeling behaviors; ADR gates; done when the frontier is empty | Template, versioning, Oracle and ownership checks; gap list | The 3 to 5 question cap (now up to 8 per round) |
| 04 | Legacy to spec | to-questionnaire structure for expert questionnaires ("grill the send"); Oracle claims cite official docs; parallel research per entry point for large slices | Your GREEN, AMBER, RED prompt and all 16 sections; file:line evidence; variants | |
| 05 | Keep, change or retire | Grilling-format decision round for the approver; questionnaire structure for experts | Section 16 decision table; PRESERVE by default | |
| 06 | Template JSON | Optional throwaway rule explorer (prototype skill) for expert validation | Schema validation; unexpressible-rule list | Prototype UI branch |
| 07 | Oracle SQL | Primary-source rule for Oracle behavior claims | Everything else | |
| 08 | H2 schema | Two adapters per port makes the seam real | Everything else | |
| 09 | Plan and tickets | to-spec: agreed seams (highest, fewest), problem and solution from the user view, testing decisions, no file paths or code in plans; to-tickets: tracer bullets, prefactoring first, blocking edges, ready frontier, expand-contract for wide changes, quiz me before publishing | Pre-checks; decisions and rule IDs in tickets | Tracker publishing and labels, mandatory long user stories (now optional and derived from the spec) |
| 10 | Seams and parity harness | tdd: seams agreed before tests; anti-patterns (implementation-coupled, tautological, horizontal slicing) | Contract and parity tests up front because expected values are recorded, not imagined; shared persistence suite | Unit tests moved to playbook 11 |
| 11 | Implement ticket | implement and tdd: state the ticket, unit tests one at a time at agreed seams, red then green, single tests often and the full suite once at the end, no refactoring inside the loop | CI Oracle loop; Oracle-first adapters; blocker rules | implement-spec worktrees and integration branch |
| 12 | Review | code-review: pinned fixed point (merge-base diff, non-empty), two separate axes (Spec and Standards), Fowler smell baseline as judgement calls, repo standards override, skip what tooling enforces, no re-ranking across axes | Decision, parity, persistence and template checks | |
| 13 | Retro and handoff | retro: environment categories (navigation, automated checks, mechanical rules become checks, steering size, no-ops, tool economy, information access); handoff: tailored to next session, reference not duplicate, suggested playbooks, redact | Feature retro sections; handoff lives in the repo | Handoff in a temp folder (Devin sessions do not share files) |
| 14 | UI end-to-end and load | none | unchanged | no skill counterpart |
| 15 | Architecture audit | improve-codebase-architecture: hot spots from git, friction list, deletion test, strength ratings, top recommendation, no interfaces yet, one candidate per session, ADR conflict rule | Scope limit; markdown output | HTML report |

## Templates and guides changed

- `open-questions/_template.md` now follows the questionnaire structure
- `plans/_template.md` follows the to-spec structure; `tickets/_template.md` follows to-tickets
- `retros/_template.md` has the environment categories; new `handoffs/_template.md`
- `steering/glossary.md` no longer has implementation columns; new `steering/naming-map.md`
- `guides/test-strategy.md` adds the seam rule, anti-patterns and the up-front versus per-ticket split
- `guides/00-workflow.md` and `DEVIN.md` rename step 10; `home-repo/README.md` lists `handoffs/`

## Not applied: decisions for you (AGENTS.md and DEVIN.md)

1. **Steering size.** The retro skill says files pushed into every session (AGENTS.md) should be sparse, mostly navigation pointers. The kit `AGENTS.md` is about 270 lines. Its mechanical rules (domain has no Spring or JPA imports, no field injection, no `System.out`, no `double` for money, layering direction) are better enforced by automated checks in your CI (for example architecture tests, Checkstyle or forbidden-API rules). Judgement-call standards belong in a `CODING_STANDARDS.md` read at review. Playbook 12 already reads standards sources. I recommend trimming AGENTS.md this way.
2. **AGENTS.md sections 5 and 10** say tests are written in the order contract, parity, unit for the whole slice. Playbooks 10 and 11 now write contract and parity tests up front and unit tests per ticket. The wording in AGENTS.md should match.
3. **Handoffs** live in the repo; DEVIN.md could mention `docs/handoffs/`.

## Parent and child sessions (added after the first version)

Devin has parent and child sessions, so the "sub-agents if available" wording is replaced:

- `DEVIN.md` has a new "Parent and child sessions" section (parent owns the playbook and the output, children do one narrow task, context pointers, results in named repo files, no nesting, a limit on parallel children)
- Playbook 01 (design-it-twice options), 02 (research tickets), 03 (lookups), 04 (large slice, one child per entry point), 12 (the two review axes) and 15 (audit exploration) now use child sessions
- Rule added: use children for non-conflicting scopes (UI versus service, different modules or sections), never for overlapping work; tickets now list what they touch and which tickets are parallel-safe (`DEVIN.md`, ticket template, playbook 09)
- Not done: an orchestrator that spawns one child per ready ticket, in the style of the implement-spec skill. It adds merge conflicts and review load, so it is left for later.

## Assumptions to check

- How a parent hands context to a child, whether children write to the same branch, and the concurrency and cost limits are marked [CONFIRM] in `DEVIN.md`. Playbooks keep a sequential fallback.
- Playbooks refer to `<home repo>/...` paths where the session runs in a service repo; fill them in.
