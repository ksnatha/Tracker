> **FILL-IN INSTRUCTIONS:** Copy this into a Devin Playbook. Placeholders like `<THIS>` are filled **per run** (slice, ticket, paths) unless marked ONE-TIME, which you fill once and keep. Search for `<` before every run. Delete this block in the copy you give Devin.

# Playbook 06: Generate Template JSON

Read `AGENTS.md` and `DEVIN.md` first. Do only this step. Run per questionnaire section, after the spec for that section and its decisions are approved.

## Goal

Generate the template JSON for one section from the approved field catalog and the rules marked as template JSON, validate it against the schema, and list the rules that could not be expressed.

## Inputs

- Section: `<SECTION NAME, e.g. Key Information>`
- Approved spec: `docs/specs/<slice>.md` (field catalog, rules with target home, decisions)
- Template JSON schema: `<PATH TO SCHEMA>` and the target design `<home repo>/docs/design/architecture.md`
- Existing template examples: `<PATHS, or "none">`
- Where to write: `<PATH FOR TEMPLATE JSON FILES>`

## Steps

1. Read the inputs. Confirm every field and rule you will use is approved and not RED. If not, stop and report.
2. Generate the template JSON for the section from the field catalog and the rules whose target home is template JSON and whose decision is PRESERVE or an approved CHANGE. Skip RETIRED rules.
3. Validate the JSON against the schema. Report every error and fix generator mistakes. Do not change the schema.
4. For each rule marked for a strategy bean or other home, do not encode it in JSON. List it as an input for the build plan.
5. List every approved rule you could not express in the template format, with the reason. These go to `docs/design-gaps.md` (this tells the design where the template format is too weak).
6. Add a sample test per rule type that loads the template and checks the outcome (visibility, validation, mandatory-if), using inputs from the spec.
7. Commit the JSON, update the spec's rule table to show where each rule is implemented, and open a PR.

## Rules

- Do not add fields or rules that are not in the approved inventory.
- Do not guess a rule you cannot express. Report it.
- Every JSON element must trace to a field or rule ID.

## Done when

- The template JSON validates against the schema
- The list of unexpressible rules is written and added to the gap list
- The spec shows where each rule lives
