package com.tracker.workflow.controller;

import com.tracker.workflow.dto.ReworkRequest;
import com.tracker.workflow.dto.TaskCompletionRequest;
import com.tracker.workflow.dto.TaskDTO;
import com.tracker.workflow.model.TaskStatus;
import com.tracker.workflow.model.WorkflowEvents;
import com.tracker.workflow.model.WorkflowStates;
import com.tracker.workflow.model.WorkflowTask;
import com.tracker.workflow.service.WorkflowService;
import com.tracker.workflow.service.WorkflowTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
class TaskController {

    private final WorkflowTaskService taskService;
    private final WorkflowService workflowService;

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskDTO>> getMyTasks(
            @RequestParam(required = false) String status,
            Authentication auth) {

        String userId = auth.getName();
        TaskStatus taskStatus = status != null ? TaskStatus.valueOf(status) : null;

        List<WorkflowTask> tasks = taskService.getTasksForUser(userId, taskStatus);

        List<TaskDTO> taskDTOs = tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(taskDTOs);
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<String> completeTask(
            @PathVariable Long taskId,
            @RequestBody TaskCompletionRequest request,
            Authentication auth) {

        String userId = auth.getName();
        boolean groupCompleted = taskService.completeTask(taskId, userId, request.getTaskData());

        if (groupCompleted) {
            // Get task to determine next workflow event
            WorkflowTask task = taskService.getTaskById(taskId);

            // Trigger workflow event
            workflowService.triggerWorkflowEvent(
                    task.getProcessInstanceId(),
                    determineEventFromState(task.getCurrentState()),
                    request.getTaskData()
            );
        }

        return ResponseEntity.ok("Task completed successfully");
    }

    @PostMapping("/{taskId}/rework")
    public ResponseEntity<String> requestRework(
            @PathVariable Long taskId,
            @RequestBody ReworkRequest request,
            Authentication auth) {

        String userId = auth.getName();
        WorkflowTask task = taskService.getTaskById(taskId);

        // Set rework context
        Map<String, Object> reworkContext = new HashMap<>();
        reworkContext.put("reworkReason", request.getReason());
        reworkContext.put("skipAllowed", request.isSkipAllowed());

        // Trigger rework event
        workflowService.triggerReworkEvent(
                task.getProcessInstanceId(),
                determineReworkEvent(task.getCurrentState(), request.getTargetState()),
                reworkContext
        );

        return ResponseEntity.ok("Rework requested successfully");
    }

    private TaskDTO convertToDTO(WorkflowTask task) {
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

    /*private WorkflowEvents determineEventFromState(WorkflowStates state) {
        switch (state) {
            case PENDING_MANAGER_APPROVAL:
                return WorkflowEvents.MANAGER_APPROVE;
            case PENDING_HR_REVIEW:
                return WorkflowEvents.HR_APPROVE;
            case PENDING_FINANCE_APPROVAL:
                return WorkflowEvents.FINANCE_APPROVE;
            case PENDING_CEO_APPROVAL:
                return WorkflowEvents.CEO_APPROVE;
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }
    }*/

    private WorkflowEvents determineEventFromState(WorkflowStates state) {
        switch (state) {
            case PENDING_PLANNING_BUSINESS_REVIEW:
                return WorkflowEvents.PLANNING_BUSINESS_SUBMIT;
            case PENDING_PLANNING_FINANCE_APPROVAL:
                return WorkflowEvents.PLANNING_FINANCE_APPROVE;
            case PENDING_PLANNING_OWNER_REVIEW:
                return WorkflowEvents.PLANNING_OWNER_SUBMIT;
            case PENDING_PLANNING_MANAGER_REVIEW:
                return WorkflowEvents.PLANNING_MANAGER_SUBMIT;
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }
    }

    private WorkflowEvents determineReworkEvent(WorkflowStates currentState, WorkflowStates targetState) {
        return null;
        /*if (targetState == null) {
            // Default rework to previous state
            switch (currentState) {
                case PENDING_HR_REVIEW:
                    return WorkflowEvents.REWORK_TO_MANAGER;
                case PENDING_FINANCE_APPROVAL:
                    return WorkflowEvents.REWORK_TO_HR;
                case PENDING_CEO_APPROVAL:
                    return WorkflowEvents.REWORK_TO_HR;
                default:
                    return WorkflowEvents.REWORK_TO_DRAFT;
            }
        }

        // Explicit target state specified
        switch (targetState) {
            case DRAFT:
                return WorkflowEvents.REWORK_TO_DRAFT;
            case PENDING_MANAGER_APPROVAL:
                return WorkflowEvents.REWORK_TO_MANAGER;
            case PENDING_HR_REVIEW:
                return WorkflowEvents.REWORK_TO_HR;
            default:
                throw new IllegalArgumentException("Invalid target state: " + targetState);
        }*/
    }
}
