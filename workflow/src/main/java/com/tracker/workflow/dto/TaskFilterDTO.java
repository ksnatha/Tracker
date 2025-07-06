package com.tracker.workflow.dto;

import com.tracker.workflow.model.TaskStatus;
import com.tracker.workflow.model.WorkflowStates;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskFilterDTO {
    private TaskStatus status;
    private String priority;
    private WorkflowStates state;
    private LocalDateTime dueDateFrom;
    private LocalDateTime dueDateTo;
}