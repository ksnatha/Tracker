package com.tracker.workflow.controller;

import com.tracker.workflow.dto.TaskDTO;
import com.tracker.workflow.dto.TaskDashboardDTO;
import com.tracker.workflow.dto.TaskDelegationRequest;
import com.tracker.workflow.dto.TaskEscalationRequest;
import com.tracker.workflow.dto.TaskFilterDTO;
import com.tracker.workflow.dto.WorkflowStartRequest;
import com.tracker.workflow.dto.WorkflowStatusDTO;
import com.tracker.workflow.model.ProcessHistory;
import com.tracker.workflow.service.TaskDashboardService;
import com.tracker.workflow.service.WorkflowService;
import com.tracker.workflow.service.WorkflowTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
class TaskDashboardController {

    private final WorkflowTaskService taskService;
    private final WorkflowService workflowService;
    private final TaskDashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<TaskDashboardDTO> getDashboard(Authentication auth) {
        String userId = auth.getName();
        TaskDashboardDTO dashboard = dashboardService.getDashboardData(userId);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<TaskDTO>> getFilteredTasks(
            @RequestBody TaskFilterDTO filter,
            Authentication auth) {

        String userId = auth.getName();
        List<TaskDTO> tasks = dashboardService.getTasksByFilter(userId, filter);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/workflow/{processId}/status")
    public ResponseEntity<WorkflowStatusDTO> getWorkflowStatus(@PathVariable String processId) {
        WorkflowStatusDTO status = workflowService.getWorkflowStatus(processId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/workflow/{processId}/history")
    public ResponseEntity<List<ProcessHistory>> getWorkflowHistory(@PathVariable String processId) {
        List<ProcessHistory> history = workflowService.getWorkflowHistory(processId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/workflow/start")
    public ResponseEntity<String> startWorkflow(
            @RequestBody WorkflowStartRequest request,
            Authentication auth) {

        String userId = auth.getName();
        String processInstanceId = workflowService.startWorkflow(userId, request.getProcessData());

        return ResponseEntity.ok(processInstanceId);
    }

    @PostMapping("/{taskId}/delegate")
    public ResponseEntity<String> delegateTask(
            @PathVariable Long taskId,
            @RequestBody TaskDelegationRequest request,
            Authentication auth) {

        String userId = auth.getName();
        taskService.delegateTask(taskId, userId, request.getNewAssigneeId(), request.getReason());

        return ResponseEntity.ok("Task delegated successfully");
    }

    @PostMapping("/{taskId}/escalate")
    public ResponseEntity<String> escalateTask(
            @PathVariable Long taskId,
            @RequestBody TaskEscalationRequest request,
            Authentication auth) {

        String userId = auth.getName();
        taskService.escalateTask(taskId, userId, request.getEscalationReason());

        return ResponseEntity.ok("Task escalated successfully");
    }
}
