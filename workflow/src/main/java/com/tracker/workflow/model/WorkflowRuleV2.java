package com.tracker.workflow.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "workflow_rules_v2")
@Data
@NoArgsConstructor
public class WorkflowRuleV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id", nullable = false)
    private WorkflowDefinition workflowDefinition;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Column(name = "rule_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleType ruleType;

    @Column(name = "condition_expression")
    private String conditionExpression;

    @Type(JsonType.class)
    @Column(name = "action_config", columnDefinition = "jsonb")
    private Map<String, Object> actionConfig;

    private Integer priority = 0;

    private Boolean active = true;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    public enum RuleType {
        GUARD, ACTION, VALIDATION
    }
}
