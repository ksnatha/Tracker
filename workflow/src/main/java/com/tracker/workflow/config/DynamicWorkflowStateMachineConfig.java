package com.tracker.workflow.config;

import com.tracker.workflow.model.WorkflowDefinition;
import com.tracker.workflow.model.WorkflowStateDefinition;
import com.tracker.workflow.model.WorkflowTransitionDefinition;
import com.tracker.workflow.service.DynamicWorkflowActionFactory;
import com.tracker.workflow.service.DynamicWorkflowGuardFactory;
import com.tracker.workflow.service.WorkflowDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.Optional;

@Configuration
@EnableStateMachineFactory(name = "dynamicWorkflowStateMachineFactory")
@RequiredArgsConstructor
@Log4j2
public class DynamicWorkflowStateMachineConfig extends StateMachineConfigurerAdapter<String, String> {
    
    private final WorkflowDefinitionService workflowDefinitionService;
    private final DynamicWorkflowActionFactory actionFactory;
    private final DynamicWorkflowGuardFactory guardFactory;
    
    @Override
    public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
        Optional<WorkflowDefinition> activeWorkflow = workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow");
        
        if (activeWorkflow.isEmpty()) {
            log.warn("No active workflow found, falling back to hardcoded configuration");
            configureDefaultStates(states);
            return;
        }
        
        var stateBuilder = states.withStates();
        
        for (WorkflowStateDefinition state : activeWorkflow.get().getStates()) {
            switch (state.getStateType()) {
                case INITIAL:
                    stateBuilder.initial(state.getStateName());
                    break;
                case END:
                    stateBuilder.end(state.getStateName());
                    break;
                case NORMAL:
                    stateBuilder.state(state.getStateName());
                    break;
            }
        }
        
        log.info("Configured {} states from database", activeWorkflow.get().getStates().size());
    }
    
    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
        Optional<WorkflowDefinition> activeWorkflow = workflowDefinitionService.getActiveWorkflow("Tracker-core-workflow");
        
        if (activeWorkflow.isEmpty()) {
            log.warn("No active workflow found, falling back to hardcoded configuration");
            configureDefaultTransitions(transitions);
            return;
        }
        
        for (WorkflowTransitionDefinition transition : activeWorkflow.get().getTransitions()) {
            var transitionBuilder = transitions
                .withExternal()
                .source(transition.getFromState() != null ? transition.getFromState().getStateName() : null)
                .target(transition.getToState().getStateName())
                .event(transition.getEventName());
                
            if (transition.getGuardExpression() != null && !transition.getGuardExpression().trim().isEmpty()) {
                transitionBuilder.guard(guardFactory.createGuard(transition.getGuardExpression()));
            }
            
            if (transition.getActionConfig() != null && !transition.getActionConfig().isEmpty()) {
                transitionBuilder.action(actionFactory.createAction(transition.getActionConfig()));
            }
        }
        
        log.info("Configured {} transitions from database", activeWorkflow.get().getTransitions().size());
    }
    
    private void configureDefaultStates(StateMachineStateConfigurer<String, String> states) throws Exception {
        states
            .withStates()
            .initial("PENDING_PLANNING_BUSINESS_REVIEW")
            .state("PENDING_PLANNING_FINANCE_APPROVAL")
            .state("PENDING_PLANNING_OWNER_REVIEW")
            .state("PENDING_PLANNING_MANAGER_REVIEW")
            .end("COMPLETED");
    }
    
    private void configureDefaultTransitions(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
        transitions
            .withExternal()
            .source("PENDING_PLANNING_BUSINESS_REVIEW")
            .target("PENDING_PLANNING_FINANCE_APPROVAL")
            .event("PLANNING_BUSINESS_SUBMIT")
            .and()
            .withExternal()
            .source("PENDING_PLANNING_FINANCE_APPROVAL")
            .target("PENDING_PLANNING_OWNER_REVIEW")
            .event("PLANNING_FINANCE_APPROVE")
            .and()
            .withExternal()
            .source("PENDING_PLANNING_OWNER_REVIEW")
            .target("PENDING_PLANNING_MANAGER_REVIEW")
            .event("PLANNING_OWNER_SUBMIT")
            .and()
            .withExternal()
            .source("PENDING_PLANNING_MANAGER_REVIEW")
            .target("COMPLETED")
            .event("PLANNING_MANAGER_SUBMIT");
    }
}
