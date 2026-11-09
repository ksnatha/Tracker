# References: Legacy Rewrite and Business Rule Extraction

Articles and papers behind the approach in `AGENTS.md`, `devin-playbook.md`, and `legacy-to-spec-prompt.md`. Many are vendor or consultancy write-ups, so treat reported numbers as best-case. Check each link before relying on it.

---

## A. Extracting business rules from code, validated by experts

| Source | Why it matters |
|---|---|
| [Thoughtworks: AI-assisted migration, lessons from the modernization frontlines](https://thoughtworks.com/insights/articles/ai-assisted-migration-lessons) | Java legacy platform. Golden rules against invention, GREEN/AMBER/RED confidence markers, file:line traceability, shared steering files and ADRs, and the SME review bottleneck. Most directly applicable. |
| [Caimito: AI as Your Legacy Code Archaeologist](https://www.caimito.net/en/blog/2026/02/07/ai-as-your-legacy-code-archaeologist.html) | Practitioner workflow: AI extracts each rule into an issue, original authors validate in business language, AI writes tests, experts review the tricky tests rather than everything. |
| [Thoughtworks on martinfowler.com: From Black Box to Blueprint](https://martinfowler.com/articles/black-box-to-blueprint.html) | 650+ tables and 1,200 stored procedures. Database logic hides rules; spec walked through with sys-admins and users. One thin slice, experimental. |
| [AltexSoft: Legacy Modernization Using AI Agents](https://www.altexsoft.com/blog/legacy-modernization-using-ai-agents/) | Cautionary: context does not carry between sessions, and one rewrite silently lost 17 REST endpoints. |
| [NashTech: Reverse-Engineering a Legacy System, Lessons from a Team Leader](https://blog.nashtechglobal.com/reverse-engineering-a-legacy-system-lessons-learned-from-a-team-leaders-perspective/) | Team-level lessons: logic buried in SQL, JS, and server code; magic values; assigning people by specialty; Scrum fits poorly. |
| [IBM and the California DMV case study](https://www.ibm.com/case-studies/ca-dmv) | Large-scale (millions of lines) AI-assisted rule extraction; experts shift from discovery to validation; extraction is incomplete without review. |
| [Kalohia, ACL 2026 Industry Track: Extracting Business Requirements from Legacy COBOL and PL/I](https://preview.aclanthology.org/ingest-acl/2026.acl-industry.4/) | Deterministic parsing plus schema-constrained LLM output with bidirectional traceability to code; reports agreement with expert-authored rules. |
| [Agentic Reverse Engineering of Business Rules at Enterprise Scale (arXiv)](https://arxiv.org/pdf/2609.22719) | Static analysis as the primary extraction mechanism, LLM reserved for synthesis. Research paper, unproven in practice. |
| [COBRAIN vs rule-based extraction (EASE 2025)](https://conf.researchr.org/details/ease-2025/ease-2025-research-papers/57/LLM-Vs-Rule-Based-The-COBRAIN-Tool-and-An-Empirical-Study-on-Extracting-Business-Ru) | Non-technical readers found LLM-extracted rules more understandable, which supports plain-language rule records for experts. |

## B. Proving the new system matches the old

| Source | Why it matters |
|---|---|
| [GitHub: Scientist, Measure Twice, Cut Once](https://github.blog/2015-02-17-scientist/) | Run old and new paths together, record mismatches, and use the data as a feedback loop. Basis for the parity harness. Notes that mismatches sometimes expose bugs in the legacy code or data. |
| [Airbnb: Accelerating Large-Scale Test Migration with LLMs](https://airbnb.tech/?p=366) | LLM migration pipeline with validation at every step, retries with error feedback, and many small independently validated jobs. Basis for one-slice-per-session. Test files, not business logic. |
| [Tian Pan: AI-Assisted Codebase Migration at Scale](https://tianpan.co/blog/2026/04/17/ai-codebase-migration-agents-scale) | Secondary summary of Google's LLM migration study. Warns against large-scale AI migration on low-coverage code, and that AI can change semantics in ways unit tests miss. |

## C. Incremental migration pattern

| Source | Why it matters |
|---|---|
| [InfoQ: Migrating a Monolith towards Microservices with the Strangler Fig Pattern](https://www.infoq.com/news/2020/12/microservices-strangler-fig/) | Real-company talk summary (ScholarPack) on incremental extraction. |
| Martin Fowler's strangler fig article (search martinfowler.com) | Original description of the pattern. Linked indirectly only; find the current version on his site. |

---

## How these map to our files

| Practice | Where it lives |
|---|---|
| file:line evidence on every fact | `AGENTS.md` section 14, spec prompt method and rule format |
| GREEN/AMBER/RED confidence and golden rules | `AGENTS.md` section 14, spec prompt, playbook |
| Flag ambiguity, never resolve it silently | `AGENTS.md` section 14, section 12 |
| Experts review ambiguities first | Spec prompt section 13 and reviewer notes |
| Shared steering files and ADRs | `AGENTS.md` section 15, playbook session checklist |
| Entry point reconciliation (no silent drops) | Spec prompt section 14, `AGENTS.md` definition of done |
| Expert-review capacity as bottleneck | Spec prompt reviewer notes |
| Parity against recorded legacy behavior | Fixtures and parity tests (`AGENTS.md` section 5) |
| One small, independently validated unit per session | Playbook workflow, one slice per session |
| Freeze the slice during analysis | Spec prompt reviewer notes |
