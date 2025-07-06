package com.tracker.workflow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskSummaryDTO {
    private Integer totalPending;
    private Integer totalCompleted;
    private Integer highPriorityPending;
    private Integer overdueTasks;
    private Integer reworkTasks;
}