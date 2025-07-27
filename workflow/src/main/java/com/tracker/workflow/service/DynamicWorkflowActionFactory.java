package com.tracker.workflow.service;

import com.tracker.workflow.model.CompletionStrategy;
import com.tracker.workflow.model.WorkflowStates;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class DynamicWorkflowActionFactory {
    
    private final WorkflowTaskService taskService;
    private final WorkflowTaskAssignmentService assignmentService;
    
    public Action<String, String> createAction(Map<String, Object> actionConfig) {
        String actionType = (String) actionConfig.get("type");
        
        if (actionType == null) {
            return context -> {};
        }
        
        switch (actionType) {
            case "CREATE_TASK_GROUP":
                return createTaskGroupAction(actionConfig);
            case "CREATE_SINGLE_TASK":
                return createSingleTaskAction(actionConfig);
            case "COMPLETE_PROCESS":
                return completeProcessAction();
            case "SEND_NOTIFICATION":
                return sendNotificationAction(actionConfig);
            default:
                log.warn("Unknown action type: {}", actionType);
                return context -> {};
        }
    }
    
    private Action<String, String> createTaskGroupAction(Map<String, Object> config) {
        return context -> {
            try {
                String processInstanceId = getProcessInstanceId(context);
                String currentState = context.getTarget().getId();
                
                TaskAssignmentConfig assignment = assignmentService.getAssignmentForState(processInstanceId, currentState);
                
                if (assignment != null) {
                    taskService.createTaskGroup(
                        processInstanceId,
                        assignment.getTaskName(),
                        assignment.getAssignees(),
                        assignment.getCompletionStrategy(),
                        WorkflowStates.valueOf(currentState),
                        assignment.getDescription()
                    );
                    
                    log.info("Created task group for state: {} with {} assignees", currentState, assignment.getAssignees().size());
                } else {
                    log.warn("No task assignment configuration found for state: {}", currentState);
                }
            } catch (Exception e) {
                log.error("Error creating task group", e);
            }
        };
    }
    
    private Action<String, String> createSingleTaskAction(Map<String, Object> config) {
        return context -> {
            try {
                String processInstanceId = getProcessInstanceId(context);
                String currentState = context.getTarget().getId();
                
                TaskAssignmentConfig assignment = assignmentService.getAssignmentForState(processInstanceId, currentState);
                
                if (assignment != null && !assignment.getAssignees().isEmpty()) {
                    String assignee = assignment.getAssignees().get(0);
                    
                    taskService.createSingleTask(
                        processInstanceId,
                        assignment.getTaskName(),
                        assignee,
                        WorkflowStates.valueOf(currentState),
                        assignment.getDescription()
                    );
                    
                    log.info("Created single task for state: {} assigned to: {}", currentState, assignee);
                } else {
                    log.warn("No task assignment configuration found for state: {}", currentState);
                }
            } catch (Exception e) {
                log.error("Error creating single task", e);
            }
        };
    }
    
    private Action<String, String> completeProcessAction() {
        return context -> {
            try {
                String processInstanceId = getProcessInstanceId(context);
                taskService.completeProcess(processInstanceId);
                log.info("Completed process: {}", processInstanceId);
            } catch (Exception e) {
                log.error("Error completing process", e);
            }
        };
    }
    
    private Action<String, String> sendNotificationAction(Map<String, Object> config) {
        return context -> {
            try {
                String message = (String) config.get("message");
                String recipient = (String) config.get("recipient");
                
                if (message != null && recipient != null) {
                    log.info("Sending notification to {}: {}", recipient, message);
                }
            } catch (Exception e) {
                log.error("Error sending notification", e);
            }
        };
    }
    
    private String getProcessInstanceId(StateContext<String, String> context) {
        Object processId = context.getExtendedState().getVariables().get("processInstanceId");
        return processId != null ? processId.toString() : "unknown";
    }
    
    public static class TaskAssignmentConfig {
        private final String taskName;
        private final List<String> assignees;
        private final CompletionStrategy completionStrategy;
        private final String description;
        
        public TaskAssignmentConfig(String taskName, List<String> assignees, CompletionStrategy completionStrategy, String description) {
            this.taskName = taskName;
            this.assignees = assignees;
            this.completionStrategy = completionStrategy;
            this.description = description;
        }
        
        public String getTaskName() { return taskName; }
        public List<String> getAssignees() { return assignees; }
        public CompletionStrategy getCompletionStrategy() { return completionStrategy; }
        public String getDescription() { return description; }
    }
}
