package com.tracker.workflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity for workflow rules.
 */
@Entity
@Table(name = "workflow_rules")
@Data
@NoArgsConstructor
public class WorkflowRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ruleName;
    private String condition; // JSON expression like {"amount": {"$lt": 500}}
    private String action; // SKIP_FINANCE, SKIP_CEO, etc.
    private Boolean active = true;
}
