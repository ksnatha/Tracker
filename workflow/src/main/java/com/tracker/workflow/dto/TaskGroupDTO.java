package com.tracker.workflow.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskGroupDTO {
    private Long id;
    private String groupName;
    private String completionStrategy;
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer requiredCompletions;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime completedDate;
}