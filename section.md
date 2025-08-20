# 🧭 Onboarding Guide: Section-Driven Validation & Orchestration Architecture

## 📌 Overview
This architecture enables modular, section-based data validation and persistence for a web application. Each section (e.g., `SecA`, `SecB`) has its own DTO, validator, and service, allowing for:

- **Granular validation** based on operation type (`Save` vs `Submit`)
- **Shared context** across sections during orchestration
- **Plug-and-play extensibility** for future sections

---

## 🧱 Core Components

| Layer               | Purpose                                                                 |
|---------------------|-------------------------------------------------------------------------|
| **DTOs**            | Define section-specific input models with validation annotations        |
| **Validators**      | Apply both bean validation and custom logic per section                 |
| **ValidationContext** | Holds shared data, validation group, and error messages across sections |
| **SectionService**  | Interface for section-specific validation and persistence               |
| **Factory**         | Dynamically resolves section services by name                           |
| **Orchestrator**    | Coordinates multi-section validation and persistence                    |
| **Controller**      | Exposes REST endpoints for saving and submitting sections               |

---

## 🧩 Validation Strategy

- **Marker Interfaces**: `Save` and `Submit` define validation groups.
- **Bean Validation**: Standard annotations like `@NotBlank`, `@Email`, etc.
- **Custom Rules**: Applied inside each validator (e.g., age threshold, comment content).
- **Contextual Awareness**: `ValidationContext` allows cross-section logic and shared data.

---

## 🔄 Orchestration Flow

### ✅ Save Flow
1. Controller receives a single section DTO.
2. Orchestrator sets `Save` group in context.
3. Validator runs for that section.
4. If valid, section is persisted.

### 🚀 Submit Flow
1. Controller receives all section DTOs.
2. Orchestrator sets `Submit` group in context.
3. Validators run for all sections.
4. If all pass, target section is persisted.

---

## 🧠 Extensibility Guidelines

To add a new section (e.g., `SecC`):

1. Create `SecC` entity and `SecCDTO`.
2. Implement `SecCValidator` with bean and custom logic.
3. Create `SecCService` implementing `SectionService`.
4. Register service name (`"SecC"`) in `SectionServiceFactory`.

No changes needed in orchestrator or controller — the system auto-wires based on section name.

---

## 🛡️ Testing Strategy

- **Unit Tests**: For each validator and service
- **Integration Tests**: Use RestAssured + Cucumber to simulate save/submit flows
- **Environment Awareness**: Tests run only in DEV/SIT/UAT, not PROD

---

## 📚 Best Practices

- Keep DTOs lean and focused on validation.
- Use `ValidationContext` for cross-cutting concerns.
- Avoid hardcoding section names — rely on factory resolution.
- Document custom rules clearly in each validator.

---

## 🧭 Quick Reference

| Section | DTO        | Validator       | Service       | Entity   |
|---------|------------|------------------|----------------|----------|
| SecA    | `SecADTO`  | `SecAValidator`  | `SecAService`  | `SecA`   |
| SecB    | `SecBDTO`  | `SecBValidator`  | `SecBService`  | `SecB`   |

---
