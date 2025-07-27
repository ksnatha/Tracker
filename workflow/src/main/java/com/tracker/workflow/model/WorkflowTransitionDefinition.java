package com.tracker.workflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Table(name = "workflow_transitions")
@Data
@NoArgsConstructor
public class WorkflowTransitionDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id", nullable = false)
    private WorkflowDefinition workflowDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_state_id")
    private WorkflowStateDefinition fromState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_state_id", nullable = false)
    private WorkflowStateDefinition toState;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "display_name")
    private String displayName;

    private String description;

    @Column(name = "guard_expression")
    private String guardExpression;

    @Column(name = "action_config", columnDefinition = "jsonb")
    private Map<String, Object> actionConfig;

    @Column(name = "transition_order")
    private Integer transitionOrder = 0;
}
