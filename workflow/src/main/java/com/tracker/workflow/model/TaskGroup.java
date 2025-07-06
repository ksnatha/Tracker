package com.tracker.workflow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Task Group Entity for Multi-User Tasks.
 */
@Entity
@Table(name = "task_groups")
@Data
@NoArgsConstructor
public class TaskGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processInstanceId;
    private String groupName;

    @Enumerated(EnumType.STRING)
    private CompletionStrategy completionStrategy; // ANY_ONE, ALL_REQUIRED, MAJORITY

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Integer totalTasks;
    private Integer completedTasks = 0;
    private Integer requiredCompletions;

    private LocalDateTime createdDate;
    private LocalDateTime completedDate;
}
