package com.tracker.workflow.service;

import com.tracker.workflow.model.*;
import com.tracker.workflow.repository.UserRoleRepository;
import com.tracker.workflow.repository.WorkflowTaskAssignmentRepository;
import com.tracker.workflow.service.DynamicWorkflowActionFactory.TaskAssignmentConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class WorkflowTaskAssignmentService {
    
    private final WorkflowTaskAssignmentRepository assignmentRepository;
    private final UserRoleRepository userRoleRepository;
    private final WorkflowDefinitionService workflowDefinitionService;
    
    public TaskAssignmentConfig getAssignmentForState(String processInstanceId, String stateName) {
        Optional<WorkflowDefinition> activeWorkflow = workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow");
        
        if (activeWorkflow.isEmpty()) {
            log.warn("No active workflow found for process: {}", processInstanceId);
            return null;
        }
        
        WorkflowStateDefinition stateDefinition = activeWorkflow.get().getStates().stream()
            .filter(state -> state.getStateName().equals(stateName))
            .findFirst()
            .orElse(null);
            
        if (stateDefinition == null) {
            log.warn("State definition not found: {}", stateName);
            return null;
        }
        
        Optional<WorkflowTaskAssignment> assignment = assignmentRepository.findByStateId(stateDefinition.getId());
        
        if (assignment.isEmpty()) {
            log.warn("No task assignment found for state: {}", stateName);
            return null;
        }
        
        return buildTaskAssignmentConfig(assignment.get());
    }
    
    private TaskAssignmentConfig buildTaskAssignmentConfig(WorkflowTaskAssignment assignment) {
        List<String> assignees = resolveAssignees(assignment);
        
        Map<String, Object> taskTemplate = assignment.getTaskTemplate();
        String taskName = taskTemplate != null ? (String) taskTemplate.get("name") : "Review Task";
        String description = taskTemplate != null ? (String) taskTemplate.get("description") : "Please review and complete this task";
        
        return new TaskAssignmentConfig(
            taskName,
            assignees,
            assignment.getCompletionStrategy(),
            description
        );
    }
    
    private List<String> resolveAssignees(WorkflowTaskAssignment assignment) {
        List<String> assignees = new ArrayList<>();
        
        switch (assignment.getAssignmentType()) {
            case ROLE:
                assignees.addAll(resolveRoleBasedAssignees(assignment.getAssignmentConfig()));
                break;
            case USER:
                assignees.addAll(resolveUserBasedAssignees(assignment.getAssignmentConfig()));
                break;
            case DYNAMIC:
                assignees.addAll(resolveDynamicAssignees(assignment.getAssignmentConfig()));
                break;
        }
        
        return assignees;
    }
    
    @SuppressWarnings("unchecked")
    private List<String> resolveRoleBasedAssignees(Map<String, Object> config) {
        List<String> assignees = new ArrayList<>();
        
        Object rolesObj = config.get("roles");
        if (rolesObj instanceof List) {
            List<String> roleNames = (List<String>) rolesObj;
            
            for (String roleName : roleNames) {
                List<String> usersInRole = userRoleRepository.findUserIdsByRoleName(roleName);
                assignees.addAll(usersInRole);
                log.debug("Found {} users for role: {}", usersInRole.size(), roleName);
            }
        }
        
        return assignees;
    }
    
    @SuppressWarnings("unchecked")
    private List<String> resolveUserBasedAssignees(Map<String, Object> config) {
        Object usersObj = config.get("users");
        if (usersObj instanceof List) {
            return new ArrayList<>((List<String>) usersObj);
        }
        return new ArrayList<>();
    }
    
    private List<String> resolveDynamicAssignees(Map<String, Object> config) {
        log.info("Dynamic assignment not yet implemented, returning empty list");
        return new ArrayList<>();
    }
}
