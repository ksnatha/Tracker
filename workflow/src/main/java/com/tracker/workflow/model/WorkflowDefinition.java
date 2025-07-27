package com.tracker.workflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workflow_definitions")
@Data
@NoArgsConstructor
public class WorkflowDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workflow_name", nullable = false)
    private String workflowName;

    @Column(nullable = false)
    private String version;

    private String description;

    @Column(name = "is_active")
    private Boolean isActive = false;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "activated_date")
    private LocalDateTime activatedDate;

    @OneToMany(mappedBy = "workflowDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkflowStateDefinition> states;

    @OneToMany(mappedBy = "workflowDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkflowTransitionDefinition> transitions;

    @OneToMany(mappedBy = "workflowDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkflowTaskAssignment> taskAssignments;

    @OneToMany(mappedBy = "workflowDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkflowRuleV2> rules;
}
