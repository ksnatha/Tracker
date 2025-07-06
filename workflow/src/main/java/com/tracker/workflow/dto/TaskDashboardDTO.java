package com.tracker.workflow.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class TaskDashboardDTO {
    private List<TaskDTO> pendingTasks;
    private List<TaskDTO> completedTasks;
    private List<TaskGroupDTO> taskGroups;
    private Map<String, WorkflowStatusDTO> workflowStatuses;
    private TaskSummaryDTO summary;
}