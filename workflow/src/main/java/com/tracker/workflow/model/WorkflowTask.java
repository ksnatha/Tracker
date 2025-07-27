package com.tracker.workflow.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity representing a workflow task.
 */
@Entity
@Table(name = "workflow_tasks")
@Data
@NoArgsConstructor
public class WorkflowTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processInstanceId;
    private String taskName;
    private String assignedUserId;
    private String assignedRole;
    private Long taskGroupId; // For multi-user tasks

    @Enumerated(EnumType.STRING)
    private WorkflowStates currentState;

    @Enumerated(EnumType.STRING)
    private TaskStatus status; // PENDING, COMPLETED, SKIPPED

    private LocalDateTime createdDate;
    private LocalDateTime dueDate;
    private LocalDateTime completedDate;
    private String description;
    private String priority;
    private Integer reworkCount = 0;
    private String completedByUserId;

    @Type(JsonType.class)
    @Column(name = "task_data", columnDefinition = "jsonb")
    private Map<String, Object> taskData;
}
