package com.tracker.workflow.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskDTO {
    private Long id;
    private String taskName;
    private String description;
    private String priority;
    private LocalDateTime createdDate;
    private LocalDateTime dueDate;
    private LocalDateTime completedDate;
    private String currentState;
    private String status;
    private Integer reworkCount;
    private Long taskGroupId;
    private String processInstanceId;
}
