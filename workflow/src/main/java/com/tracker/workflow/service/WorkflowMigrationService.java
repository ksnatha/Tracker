package com.tracker.workflow.service;

import com.tracker.workflow.model.*;
import com.tracker.workflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class WorkflowMigrationService {
    
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowStateDefinitionRepository stateRepository;
    private final WorkflowTransitionDefinitionRepository transitionRepository;
    private final WorkflowTaskAssignmentRepository assignmentRepository;
    private final WorkflowRoleRepository roleRepository;
    
    public void migrateHardcodedWorkflowToDatabase() {
        log.info("Starting migration of hardcoded workflow to database");
        
        WorkflowDefinition definition = createWorkflowDefinition();
        
        List<WorkflowStateDefinition> states = createStates(definition);
        
        createTransitions(definition, states);
        
        createTaskAssignments(definition, states);
        
        definition.setIsActive(true);
        definition.setActivatedDate(LocalDateTime.now());
        workflowDefinitionRepository.save(definition);
        
        log.info("Successfully migrated hardcoded workflow to database");
    }
    
    private WorkflowDefinition createWorkflowDefinition() {
        WorkflowDefinition definition = new WorkflowDefinition();
        definition.setWorkflowName("Tracker-core-workflow");
        definition.setVersion("1.0.0");
        definition.setDescription("Initial migration from hardcoded workflow configuration");
        definition.setCreatedBy("system");
        definition.setCreatedDate(LocalDateTime.now());
        definition.setIsActive(false);
        
        definition.setStates(new java.util.ArrayList<>());
        definition.setTransitions(new java.util.ArrayList<>());
        definition.setTaskAssignments(new java.util.ArrayList<>());
        definition.setRules(new java.util.ArrayList<>());
        
        return workflowDefinitionRepository.save(definition);
    }
    
    private List<WorkflowStateDefinition> createStates(WorkflowDefinition definition) {
        WorkflowStateDefinition businessReview = createState(definition, "PENDING_PLANNING_BUSINESS_REVIEW", 
            WorkflowStateDefinition.StateType.INITIAL, "Business Review", 1);
        
        WorkflowStateDefinition financeApproval = createState(definition, "PENDING_PLANNING_FINANCE_APPROVAL", 
            WorkflowStateDefinition.StateType.NORMAL, "Finance Approval", 2);
        
        WorkflowStateDefinition ownerReview = createState(definition, "PENDING_PLANNING_OWNER_REVIEW", 
            WorkflowStateDefinition.StateType.NORMAL, "Owner Review", 3);
        
        WorkflowStateDefinition managerReview = createState(definition, "PENDING_PLANNING_MANAGER_REVIEW", 
            WorkflowStateDefinition.StateType.NORMAL, "Manager Review", 4);
        
        WorkflowStateDefinition completed = createState(definition, "COMPLETED", 
            WorkflowStateDefinition.StateType.END, "Completed", 5);
        
        List<WorkflowStateDefinition> states = Arrays.asList(businessReview, financeApproval, ownerReview, managerReview, completed);
        
        definition.getStates().addAll(states);
        
        return states;
    }
    
    private WorkflowStateDefinition createState(WorkflowDefinition definition, String stateName, 
                                               WorkflowStateDefinition.StateType stateType, String displayName, int order) {
        WorkflowStateDefinition state = new WorkflowStateDefinition();
        state.setWorkflowDefinition(definition);
        state.setStateName(stateName);
        state.setStateType(stateType);
        state.setDisplayName(displayName);
        state.setStateOrder(order);
        
        return stateRepository.save(state);
    }
    
    private void createTransitions(WorkflowDefinition definition, List<WorkflowStateDefinition> states) {
        WorkflowStateDefinition businessReview = findStateByName(states, "PENDING_PLANNING_BUSINESS_REVIEW");
        WorkflowStateDefinition financeApproval = findStateByName(states, "PENDING_PLANNING_FINANCE_APPROVAL");
        WorkflowStateDefinition ownerReview = findStateByName(states, "PENDING_PLANNING_OWNER_REVIEW");
        WorkflowStateDefinition managerReview = findStateByName(states, "PENDING_PLANNING_MANAGER_REVIEW");
        WorkflowStateDefinition completed = findStateByName(states, "COMPLETED");
        
        WorkflowTransitionDefinition t1 = createTransition(definition, businessReview, financeApproval, "PLANNING_BUSINESS_SUBMIT", 
            "Business Review to Finance Approval", createTaskGroupAction());
        
        WorkflowTransitionDefinition t2 = createTransition(definition, financeApproval, ownerReview, "PLANNING_FINANCE_APPROVE", 
            "Finance Approval to Owner Review", createTaskGroupAction());
        
        WorkflowTransitionDefinition t3 = createTransition(definition, ownerReview, managerReview, "PLANNING_OWNER_SUBMIT", 
            "Owner Review to Manager Review", createTaskGroupAction());
        
        WorkflowTransitionDefinition t4 = createTransition(definition, managerReview, completed, "PLANNING_MANAGER_SUBMIT", 
            "Manager Review to Completed", createCompleteProcessAction());
        
        definition.getTransitions().addAll(Arrays.asList(t1, t2, t3, t4));
    }
    
    private WorkflowTransitionDefinition createTransition(WorkflowDefinition definition, 
                                                         WorkflowStateDefinition fromState, 
                                                         WorkflowStateDefinition toState, 
                                                         String eventName, String displayName, 
                                                         Map<String, Object> actionConfig) {
        WorkflowTransitionDefinition transition = new WorkflowTransitionDefinition();
        transition.setWorkflowDefinition(definition);
        transition.setFromState(fromState);
        transition.setToState(toState);
        transition.setEventName(eventName);
        transition.setDisplayName(displayName);
        transition.setActionConfig(actionConfig);
        transition.setTransitionOrder(0);
        
        return transitionRepository.save(transition);
    }
    
    private void createTaskAssignments(WorkflowDefinition definition, List<WorkflowStateDefinition> states) {
        WorkflowTaskAssignment a1 = createRoleBasedAssignment(definition, findStateByName(states, "PENDING_PLANNING_BUSINESS_REVIEW"), 
            List.of("BUSINESS_REVIEWER"), CompletionStrategy.ANY_ONE, "Business Review Task", 
            "Please review the business requirements and planning details");
        
        WorkflowTaskAssignment a2 = createRoleBasedAssignment(definition, findStateByName(states, "PENDING_PLANNING_FINANCE_APPROVAL"), 
            List.of("FINANCE_APPROVER"), CompletionStrategy.ANY_ONE, "Finance Approval Task", 
            "Please review and approve the financial aspects of this request");
        
        WorkflowTaskAssignment a3 = createRoleBasedAssignment(definition, findStateByName(states, "PENDING_PLANNING_OWNER_REVIEW"), 
            List.of("OWNER_REVIEWER"), CompletionStrategy.ANY_ONE, "Owner Review Task", 
            "Please review ownership and responsibility assignments");
        
        WorkflowTaskAssignment a4 = createRoleBasedAssignment(definition, findStateByName(states, "PENDING_PLANNING_MANAGER_REVIEW"), 
            List.of("MANAGER_REVIEWER"), CompletionStrategy.ANY_ONE, "Manager Review Task", 
            "Please provide final management review and approval");
        
        definition.getTaskAssignments().addAll(Arrays.asList(a1, a2, a3, a4));
    }
    
    private WorkflowTaskAssignment createRoleBasedAssignment(WorkflowDefinition definition, WorkflowStateDefinition state, 
                                          List<String> roleNames, CompletionStrategy strategy, 
                                          String taskName, String description) {
        if (state.getId() == null) {
            throw new IllegalArgumentException("State must be persisted before creating task assignment");
        }
        
        WorkflowTaskAssignment assignment = new WorkflowTaskAssignment();
        assignment.setWorkflowDefinition(definition);
        assignment.setState(state);
        assignment.setAssignmentType(WorkflowTaskAssignment.AssignmentType.ROLE);
        assignment.setCompletionStrategy(strategy);
        assignment.setCreatedDate(LocalDateTime.now());
        
        Map<String, Object> assignmentConfig = new HashMap<>();
        assignmentConfig.put("roles", roleNames);
        assignment.setAssignmentConfig(assignmentConfig);
        
        Map<String, Object> taskTemplate = new HashMap<>();
        taskTemplate.put("name", taskName);
        taskTemplate.put("description", description);
        taskTemplate.put("priority", "MEDIUM");
        assignment.setTaskTemplate(taskTemplate);
        
        WorkflowTaskAssignment saved = assignmentRepository.save(assignment);
        log.debug("Created task assignment for state: {} with roles: {}", state.getStateName(), roleNames);
        
        return saved;
    }
    
    private WorkflowStateDefinition findStateByName(List<WorkflowStateDefinition> states, String stateName) {
        return states.stream()
            .filter(state -> state.getStateName().equals(stateName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("State not found: " + stateName));
    }
    
    private Map<String, Object> createTaskGroupAction() {
        Map<String, Object> action = new HashMap<>();
        action.put("type", "CREATE_TASK_GROUP");
        return action;
    }
    
    private Map<String, Object> createCompleteProcessAction() {
        Map<String, Object> action = new HashMap<>();
        action.put("type", "COMPLETE_PROCESS");
        return action;
    }
}
