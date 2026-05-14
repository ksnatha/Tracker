# Claude Prompt — Generate Devin Documentation Instructions

Use this file inside your IDE (IntelliJ with Claude plugin, or Claude.ai with the codebase
in context). Paste the prompt below, replacing the placeholder values with your actual
domain details before sending.

---

## How To Use

1. Open Claude in your IDE with the relevant source files in context
   (the service classes, entities, repositories, and any related validators or handlers
   for the domain you want documented).
2. Fill in the placeholders marked `<<LIKE THIS>>` before sending.
3. Send the prompt. Claude will produce a ready-to-paste Devin task.
4. Review the Devin task output before giving it to Devin — adjust scope if needed.

---

## The Prompt

```
You are helping me create a task instruction for Devin (an AI software engineer)
to document the domain rules for a specific module in our Java/Spring Boot enterprise
application.

Your job is to produce a Devin task brief — not the documentation itself.

---

### Context

Application: <<APPLICATION NAME, e.g. Appraisal Workflow System>>
Domain to document: <<DOMAIN NAME, e.g. User Role Management>>
Tech stack: Java, Spring Boot, Oracle DB, Angular frontend
Documentation target format: Markdown, using the template at <<PATH OR LINK TO domain_rules_card_template.md>>

Primary source paths to analyse:
- <<e.g. src/main/java/com/yourapp/userroles/>>
- <<e.g. src/main/java/com/yourapp/records/RecordService.java>>
- <<e.g. src/main/java/com/yourapp/category/CategoryTransitionHandler.java>>

Related paths (do not ignore these — they may contain intersecting logic):
- <<e.g. src/main/java/com/yourapp/tasks/TaskAssignmentService.java>>
- <<e.g. src/main/resources/db/migration/ (Flyway scripts)>>

Key DB tables:
- <<e.g. USER_ROLE, RECORD_ROLE_ASSIGNMENT, TASK_ASSIGNMENT>>

Known complexity or risk areas I am already aware of:
- <<e.g. Category change transitions must clear certain associations — any new feature
  creating associations must participate in this transition>>
- <<e.g. Role deletion depends on task assignment state in a separate service>>
- <<Add any other known gotchas here, or leave blank if none>>

---

### Your Task

Using the context above, produce a complete Devin task brief that instructs Devin to:

1. Read and analyse the source paths listed above
2. Fill in every section of the Domain Rules Card template
3. Follow the validation rules below exactly

The Devin task brief must be self-contained — Devin should be able to execute it
without asking clarifying questions.

---

### Validation Rules To Include in the Devin Task

Embed these rules verbatim in the Devin task brief you produce:

RULE 1 — SOURCE ONLY
Document only what is explicitly enforced in the code. Do not infer rules, do not
assume intent, do not document what the code "probably should" do. If a rule is not
clearly in the code, do not include it.

RULE 2 — FLAG UNCERTAINTY
If you find logic that looks like a business rule but you are not certain it is
intentional, mark it with ⚠️ NEEDS REVIEW and describe what you found and why you
are uncertain. Do not silently include uncertain rules as if they are confirmed.

RULE 3 — SOURCE REFERENCES ARE MANDATORY
Every rule in Section 3 must include the fully qualified class name and method name
where it is enforced. Format: ClassName.methodName(). If you cannot find a source
reference, mark the rule ⚠️ NEEDS REVIEW — Source not located.

RULE 4 — STATE TRANSITIONS — SIDE EFFECTS ARE THE PRIORITY
For Section 4, the most important column is "Side Effects — What Must Be Cleared or
Updated". Do not leave this blank or vague. If you cannot determine the full side
effects of a transition from the code, mark it ⚠️ NEEDS REVIEW — Side effects unclear.

RULE 5 — FEATURE INTERSECTIONS — LOOK BEYOND THE PRIMARY PACKAGE
For Section 5, do not limit your analysis to the primary source paths. Actively search
the codebase for other services, handlers, listeners, or validators that reference the
key DB tables or entity classes of this domain. These are the intersections most likely
to cause production incidents.

RULE 6 — DO NOT FABRICATE PRODUCTION INCIDENTS
Section 6.2 (Past Production Incidents) must only be filled from git history, comments
in the code, or TODO/FIXME annotations that describe past failures. If no such
information is available in the codebase, leave the rows blank with a note:
"No incident history found in codebase — populate from team knowledge".

RULE 7 — OUTPUT FORMAT
Output must be a single Markdown file following the template structure exactly.
Do not add extra sections. Do not remove existing sections. Do not change heading
numbering. Placeholder text that you do not have enough information to fill must be
left as [ Fill in ] rather than removed or guessed.

---

### Devin Task Output Format

Structure the Devin task brief with these sections:

**Task Title**
**Objective** (one paragraph)
**Source Paths To Analyse**
**Output File**
**Step-by-Step Instructions** (numbered, specific enough that Devin does not need
to make judgment calls)
**Validation Rules** (the 7 rules above, verbatim)
**Validation Gate** (a checklist Devin must self-check before submitting the output)
**What To Do If Uncertain** (clear instruction — always flag, never guess)

---

Produce the Devin task brief now.
```

---

## Tips for Better Output

**Before sending this prompt**, make sure you have these source files open or in
context in your IDE session:
- The primary service class(es) for the domain
- The entity/model class(es)
- Any transition handler, validator, or event listener that touches this domain
- At least one related service from a different domain that you know intersects

The more context files Claude can see, the more accurate the Devin task scope will be.
Claude can also identify intersection points you may have missed.

---

## After Claude Produces the Devin Task

Before giving it to Devin, review:

1. **Are the source paths correct?** Devin will follow them literally. A wrong path
   means missed logic.
2. **Are the known complexity areas accurate?** You added these above — verify they
   reflect the current state of the code, not how it used to work.
3. **Is the scope right?** If the domain is large, consider splitting into two Devin
   tasks (e.g. Section 3-4 in one task, Section 5-6 in another). Devin performs better
   on tightly scoped tasks.
4. **Does the output file path make sense?** Devin will write the Markdown file to
   wherever you specify. Make sure it lands somewhere your team will find it
   (e.g. a `/docs/domain-rules/` folder in the repo).

---

## After Devin Completes the Task

Work through the Validation Gate at the top of the generated document:

- [ ] All ⚠️ NEEDS REVIEW flags reviewed and resolved or accepted
- [ ] Source references spot-checked — at least 3 rules verified against actual code
- [ ] State transition side effects verified with a developer who knows the domain
- [ ] Feature intersections confirmed with owning teams of those features
- [ ] Section 6.2 (Production Incidents) populated from team knowledge if Devin
      left it blank
- [ ] Overall gate signed off before sharing or using in sprint planning

---

## Reuse and Scaling

Once you have run this workflow for one domain, subsequent domains are faster:

- Keep a completed domain card as a reference example
- Add to the prompt: `"Reference this completed example for the expected depth and
  style: <<link or paste completed card>>"`
- Devin will match the depth and formatting of the example, reducing review effort

Run one domain card per sprint until the highest-risk domains are covered.
The category-change / association domain should be first.
