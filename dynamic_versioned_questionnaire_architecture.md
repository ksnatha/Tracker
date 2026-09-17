# Architecture Blueprint: Dynamic, Versioned Questionnaire & Logic Engine

## 1. Architectural Strategy
To decouple business logic, dropdown variations, and questionnaire structures from hardcoded application releases, the system leverages a **Data-Driven Configuration Graph**. 
* **The Paradigm:** Both UI components and validation constraints are driven entirely by a versioned metadata schema stored in the database.
* **The Benefit:** Non-technical operators can alter forms, drop-down options, validation constraints, and visibility rules via a UI layout engine without requiring software deployments or backend code migrations.

---

## 2. Database Layer Design

The system separates metadata definitions from transactional answers using a specialized **Vertical Normalization (Entity-Attribute-Value / EAV)** topology. This delivers NoSQL fluidity with strict relational integrity.

### A. Core Schema Structure
```
 [Questionnaires Metadata] 
          │ (1-to-Many)
          ▼
   [Questions Metadata] 
          │ (1-to-Many)
          ▼
 [DropdownOptions Metadata] <─── Validates ───┐
                                              │ (Foreign Key Enforced)
                                              │
   [UserAnswers Transactional Table] ─────────┘
```

### B. Transactional Tables Configuration

#### 1. Header Tracking Table (`UserSubmissions`)
Holds high-level structural logs. It contains zero answers.
* **Columns:** `id`, `user_id`, `form_id`, `version_applied`, `submitted_at`

#### 2. Key-Value Storage Table (`UserAnswers`)
Stores raw form inputs, flattening multi-select questions into independent vertical rows to preserve easy analytical queries.
* **Columns:** `id`, `submission_id`, `field_key`, `row_index` *(Defaults to 0 or NULL for standard questions)*, `raw_value`
* **Performance Index:** A vital compound index must be applied on `(submission_id, field_key)` to ensure lightning-fast read operations.

### C. Example Dataset Representation
If a user selects **"USA"** for country, adds an **"Employment History" grid row**, and picks multiple checkboxes (**"Slack"** and **"Jira"**) within that row:

| id | submission_id | field_key | row_index | raw_value | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `001` | `sub_999` | `q_country` | `0` | `USA` | Standard single pick |
| `002` | `sub_999` | `q_company` | `0` | `Google` | Grid Row 1, Column A |
| `003` | `sub_999` | `q_tools_used` | `0` | `slack` | Grid Row 1, Column B (Choice 1) |
| `004` | `sub_999` | `q_tools_used` | `0` | `jira` | Grid Row 1, Column B (Choice 2) |

---

## 3. Dynamic Visibility & Conditional Validation Graph

Deeply nested conditional layers (e.g., *If Country = USA, spawn State field; If State = CA and Approved = NO, spawn mandatory Comments field*) are handled via a metadata-driven dependency structure.

### A. Metadata JSON Definition Blueprint
```json
{
  "id": "q_comments",
  "label": "Please explain your rejection",
  "type": "textarea",
  "visibility_rule": {
    "logical_operator": "AND",
    "conditions": [
      { "depends_on_field": "q_country", "value": "USA" },
      { "depends_on_field": "q_approved", "value": "NO" }
    ]
  },
  "validation": { "required_if_visible": true }
}
```

### B. Execution Rules
* **Sequential Loop Over Graph:** The application iterates through the template definitions sequentially.
* **Pruning Strategy:** If a visibility rule resolves to `false`, the Java backend ignores validation constraints for that question and explicitly **purges/cleanses** any data submitted for that key to block rogue traffic.
* **Mandatory Enforcements:** If `required_if_visible` is true and visibility flags pass, the engine flags a validation exception before routing to business rule execution.

---

## 4. Federated Reference Data Integration (External APIs / Tables)

Dropdown options fed by external systems or shared tables (such as ISO Country codes or dynamic department directories) require isolated integration schemas.
* **Store Immutable Codes Only:** the system saves the standardized business key (e.g., `USA`), never the shifting display name label (`"United States"`).
* **Group & Tag Appending:** To execute logic across dynamic segments (e.g., *apply a rule to all European Union countries*), the external Reference API tags data points (`"tags": ["EU", "EMEA"]`).
* **Caching Infrastructure:** To maintain massive scalability, reference endpoints are cached locally in Java (e.g., utilizing a Caffeine or Redis cluster cache wrapper) to avoid performing repetitive network hops on every validation loop.
* **Soft Deprecations:** When an option is removed or changed, reference sources use soft deletes or dynamic expiry flags (`is_active: false`). This guarantees new forms don't expose obsolete options, while past responses still map flawlessly back to historical labels for audit reviews.

---

## 5. Application Layer Design (Java Execution Blueprint)

The core architecture completely avoids fragile Java Reflection and slow runtime class lookup queries. Instead, it pairs the **Strategy Pattern** with **Spring Dependency Injection (DI) Component Registries**.

### A. The Engine Layout
```
[Database Form Version Rule Matrix]
               │
               ▼
[Java Application Core Service] ──> [Strategy Factory] ──> [Executes Target Spring Component Bean]
```

### B. The Code Setup

#### 1. Contract Strategy Interface
```java
public interface BusinessRuleStrategy {
    boolean isMatch(String version, String questionCode, String rawValue);
    BigDecimal execute(SubmissionData data);
}
```

#### 2. Group / Tag-Aware Bean Implementation
```java
@Component
public class EUTaxStrategy implements BusinessRuleStrategy {
    @Autowired private ReferenceServiceCache referenceCache;

    @Override
    public boolean isMatch(String version, String questionCode, String rawValue) {
        CountryMetadata country = referenceCache.getCountry(rawValue);
        return country != null && country.getTags().contains("EU");
    }

    @Override
    public BigDecimal execute(SubmissionData data) {
        return data.getAmount().multiply(new BigDecimal("0.25"));
    }
}
```

#### 3. High-Performance Factory Processing Service
```java
@Component
public class BusinessLogicEngine {
    @Autowired private List<BusinessRuleStrategy> registeredStrategies;

    public void processSubmission(Submission submission) {
        // Stream search for the appropriate decoupled bean path
        BusinessRuleStrategy strategy = registeredStrategies.stream()
            .filter(s -> s.isMatch(submission.getVersion(), submission.getFieldKey(), submission.getRawValue()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No matching business rule strategy found"));

        strategy.execute(submission.getData());
    }
}
```

---

## 6. Framework Scale & Portal Maintenance

### A. Scalability Mitigations (At 500+ Questions)
Shifting 500 rows per record vertically into an engine risks I/O delays and Hibernate ORM memory bloating. The layout scales cleanly through these implementations:
* **Bypass Classic JPA Entity States:** Implement lightweight data layers like **Spring JDBC Template**, **MyBatis**, or **jOOQ** to map database rows natively straight to thin Java Maps, bypassing Hibernate context-tracking bottlenecks.
* **Mandatory DB Batch Inserts:** Enforce explicit JDBC batch streaming drivers to commit the 500 records down to the database in a single database roundtrip transaction.

### B. No-Code Management Tools
To phase out IT department dependencies for everyday questionnaire management, wrap the JSON configurations inside pre-engineered **Form Infrastructure Platforms**:

* **SurveyJS:** Extensively aligns with this specific Java architecture. The Form Creator is embedded into your team's admin portal to let business groups graphically design fields, constraints, and cascading rules. This exports native JSON schema layouts directly into your database. The end-user site leverages their free, open-source (MIT) core engine to render forms natively in front of your custom Java APIs.
* **Form.io:** A strong full-stack alternative. It prefers taking over the database layer natively via automated backend APIs and data stores, which would require structural overrides to match a custom, normalized Java SQL analytics setup.