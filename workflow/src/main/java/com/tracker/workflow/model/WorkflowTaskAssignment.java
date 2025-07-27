package com.tracker.workflow.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "workflow_task_assignments")
@Data
@NoArgsConstructor
public class WorkflowTaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id", nullable = false)
    private WorkflowDefinition workflowDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private WorkflowStateDefinition state;

    @Column(name = "assignment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssignmentType assignmentType;

    @Type(JsonType.class)
    @Column(name = "assignment_config", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> assignmentConfig;

    @Column(name = "completion_strategy", nullable = false)
    @Enumerated(EnumType.STRING)
    private CompletionStrategy completionStrategy;

    @Type(JsonType.class)
    @Column(name = "task_template", columnDefinition = "jsonb")
    private Map<String, Object> taskTemplate;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    public enum AssignmentType {
        ROLE, USER, DYNAMIC
    }
}
