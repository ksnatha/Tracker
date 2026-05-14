# Domain Rules Card
## [ Domain / Module Name ]

---

> **Internal — Engineering Use Only**
> Not for distribution outside the engineering team.

---

## Validation Gate — Document Readiness

> This document is only valid for sprint use if ALL checks below are YES.
> Any NO or PARTIAL must be resolved before this document is used in planning.

| Validation Check | Status | Notes / Evidence |
|---|---|---|
| All business rules in Section 3 are sourced from actual code — not inferred or assumed | YES / NO | |
| Every rule in Section 3 references a specific class and method | YES / NO | |
| All known state transitions in Section 4 are documented with side effects | YES / NO / PARTIAL | |
| Feature intersections in Section 5 reviewed by the owning team | YES / NO | |
| All ⚠ NEEDS REVIEW flags have been resolved or explicitly accepted | YES / NO / N/A | |
| At least one developer with domain knowledge has reviewed this document | YES / NO | [ Name + Date ] |
| Document reviewed within last 6 months OR since last domain change | YES / NO | [ Last reviewed: DD-MMM-YYYY ] |
| **Overall — approved for sprint use?** | **YES / NO** | **Approved by: [ Name ] Date: [ DD-MMM-YYYY ]** |

> **ℹ️ How To Use This Gate (when AI-generated)**
> When Claude or Devin generates this document from code:
> 1. Review every ⚠ NEEDS REVIEW flag — these are rules the AI was uncertain about and must be verified by a human.
> 2. Confirm every source reference in Section 3 points to a real class and method.
> 3. Sign off the overall status before sharing with another team or using in sprint planning.

---

## 1. Document Information

| Field | Value |
|---|---|
| **Domain Name** | [ e.g. User Role Management ] |
| **Application** | [ e.g. Appraisal Workflow System ] |
| **Owning Team** | [ Team name or lead developer ] |
| **Primary Packages** | [ e.g. com.app.userroles, com.app.records ] |
| **Key DB Tables** | [ e.g. USER_ROLE, RECORD_ROLE_ASSIGNMENT ] |
| **Created By** | [ Name ] |
| **Created Date** | [ DD-MMM-YYYY ] |
| **Last Reviewed** | [ DD-MMM-YYYY ] |
| **Reviewed By** | [ Name ] |
| **Version** | [ 1.0 ] |

> 🔴 **HIGH RISK DOMAIN**
> Any modification to this domain requires review by the owning team before sprint commitment.
> See Section 7 for specific high-risk areas.

---

## 2. Domain Overview

### 2.1 What This Domain Does

<!-- 2-4 sentences describing the business purpose. What problem does it solve? Who uses it?
     Avoid technical detail — this must be readable by a Product Owner. -->

[ Fill in ]

### 2.2 Why It Is Complex

<!-- What makes this domain non-obvious? e.g. status-based rules, role hierarchies, state
     transitions, external dependencies, legacy constraints. This section warns future
     developers what they are walking into. -->

[ Fill in ]

### 2.3 Key Concepts and Terminology

<!-- Define any terms that have specific meaning inside this domain.
     Do not assume the reader knows these. -->

| Term | Definition in This Domain |
|---|---|
| [ Term ] | [ What it means specifically in this domain ] |
| [ Term ] | [ What it means specifically in this domain ] |
| [ Term ] | [ What it means specifically in this domain ] |

---

## 3. Core Business Rules

> List every explicit business rule enforced by this domain.
> Each rule **must** reference the exact class and method where it is implemented.
> If you are unsure whether a rule is intentional or a side-effect, mark it ⚠️.

### 3.1 Creation Rules

<!-- Rules governing when and how entities in this domain can be created. -->

| Rule | Detail | Source Class / Method |
|---|---|---|
| [ Rule name ] | [ Full condition — be specific about when this applies ] | [ ServiceClass.methodName() ] |
| [ Rule name ] | [ Full description ] | [ ServiceClass.methodName() ] |
| [ Rule name ] | [ Full description ] | [ ServiceClass.methodName() ] |

### 3.2 Modification Rules

<!-- Rules governing what can and cannot be changed, and under what conditions. -->

| Rule | Detail | Source Class / Method |
|---|---|---|
| [ Rule name ] | [ Full description ] | [ ServiceClass.methodName() ] |
| [ Rule name ] | [ Full description ] | [ ServiceClass.methodName() ] |
| [ Rule name ] | [ Full description ] | [ ServiceClass.methodName() ] |

### 3.3 Deletion Rules

<!-- Rules governing when deletion is allowed and what must happen before or during deletion. -->

| Rule | Detail | Source Class / Method |
|---|---|---|
| [ Rule name ] | [ Full description — especially any dependency checks ] | [ ServiceClass.methodName() ] |
| [ Rule name ] | [ Full description ] | [ ServiceClass.methodName() ] |

### 3.4 Status-Based Rules

<!-- Rules that only apply at certain statuses. These are among the most dangerous to get
     wrong and the most likely to be missed by a new team. -->

| Status | Allowed Actions | Blocked Actions | Source |
|---|---|---|---|
| [ Status A ] | [ What is allowed ] | [ What is blocked ] | [ Class.method ] |
| [ Status B ] | [ What is allowed ] | [ What is blocked ] | [ Class.method ] |
| [ Status C ] | [ What is allowed ] | [ What is blocked ] | [ Class.method ] |

---

## 4. State Transitions

> Document every valid state change for key entities in this domain.
> The **Side Effects** column is the most critical — this is exactly what future developers
> will miss if it is not written here.

> ℹ️ **Why This Section Matters**
> The most common production incident pattern: Developer builds Feature B, doesn't know
> Feature A has a state transition that Feature B must participate in. Feature B ships.
> Production breaks. This section prevents that.

### 4.1 [ Entity Name ] State Transitions

| From State | To State | What Triggers It | Side Effects — What Must Be Cleared or Updated |
|---|---|---|---|
| [ State A ] | [ State B ] | [ e.g. User saves form / API POST /endpoint ] | [ e.g. Clear all Category A associations, reset fields X and Y, notify assigned users ] |
| [ State A ] | [ State C ] | [ e.g. Background Autosys job ] | [ e.g. Archive related tasks, remove from active queue ] |
| [ State B ] | [ State A ] | [ e.g. User clicks Reopen ] | [ e.g. Validate re-open conditions, reinstate assignments ] |

### 4.2 [ Second Entity, if applicable ] State Transitions

| From State | To State | What Triggers It | Side Effects — What Must Be Cleared or Updated |
|---|---|---|---|
| [ State ] | [ State ] | [ Trigger ] | [ Side effects and what must be cleared ] |
| [ State ] | [ State ] | [ Trigger ] | [ Side effects ] |

> ⚠️ **Needs Review**
> If any transition side effects are suspected to be incomplete or unclear, describe the gap here
> so a human can verify: [ describe any gaps or uncertainty ]

---

## 5. Feature Intersections

> Any developer building a feature that touches one of these areas **MUST** consult this
> section first. Feature interaction bugs are the hardest to catch and the most expensive
> to fix in production.

| Related Feature / Module | How It Intersects With This Domain | Must Verify Before Modifying |
|---|---|---|
| [ Feature / Module A ] | [ e.g. Creates associations that this domain validates on deletion ] | [ What must be verified before modifying? ] |
| [ Feature / Module B ] | [ e.g. Manages the status transitions that gate certain rules in Section 3.4 ] | [ What must be verified? ] |
| [ Feature / Module C ] | [ How does it interact? ] | [ What must be verified? ] |

---

## 6. Known Edge Cases and Production History

> This is the institutional memory section. It contains scenarios that are non-obvious,
> have caused incidents, or require special handling. This is what experienced developers
> know that the code does not make obvious.

### 6.1 Known Edge Cases

| Scenario | What Happens / Expected Behaviour | Source or Notes |
|---|---|---|
| [ Scenario — be specific about the sequence of steps ] | [ What the system does and whether that is correct or a known limitation ] | [ Class.method or 'tribal knowledge' ] |
| [ Scenario ] | [ Expected behaviour ] | [ Source ] |

### 6.2 Past Production Incidents

<!-- Each entry here represents a real failure. Reading these is the fastest way to
     understand what this domain's failure modes look like. -->

| Date | What Broke | Root Cause | Regression Test? |
|---|---|---|---|
| [ MMM-YYYY ] | [ What the user experienced ] | [ e.g. Feature interaction — association feature did not participate in category-change transition ] | YES / NO / Partial |
| [ MMM-YYYY ] | [ What broke ] | [ Root cause ] | YES / NO / Partial |

---

## 7. High-Risk Areas and Change Guidance

> **Read this section before writing a single line of code in this domain.**

> 🔴 **Before Modifying This Domain**
> 1. Read Section 4 (State Transitions) in full
> 2. Read Section 5 (Feature Intersections) — confirm your new feature does not need to participate in an existing transition
> 3. Run the full regression test suite for this domain
> 4. Get a review from the owning team if you are not on it

### 7.1 Specific High-Risk Areas

- [ Area 1 — e.g. The category-change transition: any new feature that creates associations must register in the transition cleanup handler ]
- [ Area 2 — e.g. Role deletion: depends on task assignment state managed by a separate service — do not call deletion directly ]
- [ Area 3 — add as many as needed ]

### 7.2 Common Mistakes Made in This Domain

- [ Mistake 1 — e.g. Adding a new association type without registering it in the category-change cleanup logic ]
- [ Mistake 2 — e.g. Calling the deletion service directly and bypassing the dependency check ]
- [ Mistake 3 ]

### 7.3 Safe Way to Introduce New Features

If your feature needs to interact with this domain, follow these steps:

1. [ Step 1 — e.g. Do not call internal methods directly. Use the public interface defined in DomainFacade.java ]
2. [ Step 2 — e.g. Register any new association type in CategoryTransitionHandler so it is included in cleanup ]
3. [ Step 3 — e.g. Add a regression test that exercises the full state transition with your new association type ]

---

## 8. Who To Talk To

> Contact these people before proceeding with any work in this domain.
> **Do not guess at rules that are not clear from the code.**

| Name / Team | Area of Knowledge | Contact |
|---|---|---|
| [ Name ] | [ e.g. Original author — deep knowledge of role deletion rules ] | [ Slack / email ] |
| [ Name ] | [ e.g. DB schema, Oracle constraints and triggers ] | [ Slack / email ] |
| [ Name ] | [ e.g. UI-side validation logic in Angular ] | [ Slack / email ] |

---

## 9. Change Log

> Update this every time the document is revised. It should reflect how understanding
> of the domain has evolved over time.

| Version | Date / Author | What Changed |
|---|---|---|
| 1.0 | [ Date / Name ] | Initial document created |

---

> ℹ️ **Living Document**
> Update this document when:
> - A new feature touches this domain
> - A production incident is caused by this domain
> - New edge cases are discovered during development or testing
>
> An outdated Domain Rules Card is worse than no card — it creates false confidence.
