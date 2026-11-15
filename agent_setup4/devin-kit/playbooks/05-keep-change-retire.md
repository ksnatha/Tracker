> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 05: Keep, Change or Retire Decisions

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Devin prepares the decision list; **humans decide**. This turns the behavior spec into the modernization input the build uses.

## Goal

For every rule and notable behavior in the approved spec, record whether it is PRESERVED, CHANGED or RETIRED, with the reason, the approver and the date.

## Inputs

- Approved behavior spec: `docs/specs/<slice>.md`
- Suspected defects (section 11), open questions (section 13) and the review summary (section 14)
- Usage evidence if available: `<LOGS / REPORTS / DB COUNTS OF HOW OFTEN EACH FEATURE OR PATH IS USED>`
- Target architecture and ADRs
- Who decides: `<SME NAMES>` and approver `<NAME>`

## Steps

1. Read the inputs. Do not decide anything yourself.
2. Fill section 16 of the spec as a proposal. Default every rule to PRESERVE. For each rule you suggest CHANGE or RETIRE, give the reason and the evidence: suspected defect, obsolete rule, no usage, conflict with the new design.
3. Mark rules that the redesign forces to change anyway (for example a rule tied to a legacy screen or table), and say what replaces them.
4. Group the proposals into a short questionnaire for the SMEs: one question per decision, in plain language, with options and your recommendation. Write it to `<home repo>/docs/open-questions/<slice>-decisions.md`.
5. Stop and wait. Humans fill in the decision, approver and date.
6. After answers arrive, update section 16 and flag any rule whose decision changes the build (tests, SQL, template JSON).

## Rules

- A rule with no recorded human decision stays PRESERVE and is not implemented as changed.
- Do not decide on behalf of an SME. Do not mark a decision approved.
- Never drop a rule silently: RETIRE needs a reason and an approver.

## Done when

- Every rule in section 4 appears in section 16
- Every CHANGE or RETIRE has a reason and an approver
- The affected tests, SQL and template JSON are listed
