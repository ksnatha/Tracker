package com.tracker.workflow.service;

import com.tracker.workflow.dto.TaskDTO;
import com.tracker.workflow.dto.TaskDashboardDTO;
import com.tracker.workflow.dto.TaskFilterDTO;
import com.tracker.workflow.dto.TaskGroupDTO;
import com.tracker.workflow.dto.TaskSummaryDTO;
import com.tracker.workflow.dto.WorkflowStatusDTO;
import com.tracker.workflow.model.TaskGroup;
import com.tracker.workflow.model.TaskStatus;
import com.tracker.workflow.model.WorkflowTask;
import com.tracker.workflow.repository.ProcessHistoryRepository;
import com.tracker.workflow.repository.TaskGroupRepository;
import com.tracker.workflow.repository.WorkflowTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskDashboardService {

    private final WorkflowTaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;
    private final ProcessHistoryRepository historyRepository;
    private final WorkflowService workflowService;

    public TaskDashboardDTO getDashboardData(String userId) {
        // Get user's tasks
        List<WorkflowTask> pendingTasks = taskRepository.findByAssignedUserIdAndStatus(userId, TaskStatus.PENDING);
        List<WorkflowTask> completedTasks = taskRepository.findByAssignedUserIdAndStatus(userId, TaskStatus.COMPLETED);

        // Get task groups where user is involved
        List<Long> taskGroupIds = pendingTasks.stream()
                .map(WorkflowTask::getTaskGroupId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<TaskGroup> taskGroups = taskGroupRepository.findAllById(taskGroupIds);

        // Get workflow status for each process
        Map<String, WorkflowStatusDTO> workflowStatuses = new HashMap<>();
        Set<String> processIds = pendingTasks.stream()
                .map(WorkflowTask::getProcessInstanceId)
                .collect(Collectors.toSet());

        for (String processId : processIds) {
            WorkflowStatusDTO status = workflowService.getWorkflowStatus(processId);
            if (status != null) {
                workflowStatuses.put(processId, status);
            }
        }

        return TaskDashboardDTO.builder()
                .pendingTasks(convertToTaskDTOs(pendingTasks))
                .completedTasks(convertToTaskDTOs(completedTasks))
                .taskGroups(convertToTaskGroupDTOs(taskGroups))
                .workflowStatuses(workflowStatuses)
                .summary(createTaskSummary(pendingTasks, completedTasks))
                .build();
    }

    public List<TaskDTO> getTasksByFilter(String userId, TaskFilterDTO filter) {
        List<WorkflowTask> tasks;

        if (filter.getStatus() != null) {
            tasks = taskRepository.findByAssignedUserIdAndStatus(userId, filter.getStatus());
        } else {
            tasks = taskRepository.findByAssignedUserIdAndStatusNot(userId, TaskStatus.SKIPPED);
        }

        // Apply additional filters
        if (filter.getPriority() != null) {
            tasks = tasks.stream()
                    .filter(task -> filter.getPriority().equals(task.getPriority()))
                    .collect(Collectors.toList());
        }

        if (filter.getState() != null) {
            tasks = tasks.stream()
                    .filter(task -> filter.getState().equals(task.getCurrentState()))
                    .collect(Collectors.toList());
        }

        if (filter.getDueDateFrom() != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getDueDate().isAfter(filter.getDueDateFrom()))
                    .collect(Collectors.toList());
        }

        if (filter.getDueDateTo() != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getDueDate().isBefore(filter.getDueDateTo()))
                    .collect(Collectors.toList());
        }

        return convertToTaskDTOs(tasks);
    }

    private List<TaskDTO> convertToTaskDTOs(List<WorkflowTask> tasks) {
        return tasks.stream()
                .map(this::convertToTaskDTO)
                .collect(Collectors.toList());
    }

    private TaskDTO convertToTaskDTO(WorkflowTask task) {
        return TaskDTO.builder()
                .id(task.getId())
                .taskName(task.getTaskName())
                .description(task.getDescription())
                .priority(task.getPriority())
                .createdDate(task.getCreatedDate())
                .dueDate(task.getDueDate())
                .completedDate(task.getCompletedDate())
                .currentState(task.getCurrentState().name())
                .status(task.getStatus().name())
                .reworkCount(task.getReworkCount())
                .taskGroupId(task.getTaskGroupId())
                .processInstanceId(task.getProcessInstanceId())
                .build();
    }

    private List<TaskGroupDTO> convertToTaskGroupDTOs(List<TaskGroup> taskGroups) {
        return taskGroups.stream()
                .map(this::convertToTaskGroupDTO)
                .collect(Collectors.toList());
    }

    private TaskGroupDTO convertToTaskGroupDTO(TaskGroup taskGroup) {
        return TaskGroupDTO.builder()
                .id(taskGroup.getId())
                .groupName(taskGroup.getGroupName())
                .completionStrategy(taskGroup.getCompletionStrategy().name())
                .totalTasks(taskGroup.getTotalTasks())
                .completedTasks(taskGroup.getCompletedTasks())
                .requiredCompletions(taskGroup.getRequiredCompletions())
                .status(taskGroup.getStatus().name())
                .createdDate(taskGroup.getCreatedDate())
                .completedDate(taskGroup.getCompletedDate())
                .build();
    }

    private TaskSummaryDTO createTaskSummary(List<WorkflowTask> pending, List<WorkflowTask> completed) {
        return TaskSummaryDTO.builder()
                .totalPending(pending.size())
                .totalCompleted(completed.size())
                .highPriorityPending(pending.stream()
                        .mapToInt(task -> "HIGH".equals(task.getPriority()) ? 1 : 0)
                        .sum())
                .overdueTasks(pending.stream()
                        .mapToInt(task -> task.getDueDate().isBefore(LocalDateTime.now()) ? 1 : 0)
                        .sum())
                .reworkTasks(pending.stream()
                        .mapToInt(task -> task.getReworkCount() > 0 ? 1 : 0)
                        .sum())
                .build();
    }
}
