package com.tracker.workflow.config;

import com.tracker.workflow.model.CompletionStrategy;
import com.tracker.workflow.model.WorkflowEvents;
import com.tracker.workflow.model.WorkflowStates;
import com.tracker.workflow.service.WorkflowRuleService;
import com.tracker.workflow.service.WorkflowTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@Configuration
@EnableStateMachineFactory(name = "legacyWorkflowStateMachineFactory")
public class AdvancedWorkflowStateMachineConfig extends StateMachineConfigurerAdapter<WorkflowStates, WorkflowEvents> {

    @Autowired
    private WorkflowTaskService taskService;

    @Autowired
    private WorkflowRuleService ruleService;

    @Override
    public void configure(StateMachineStateConfigurer<WorkflowStates, WorkflowEvents> states)
            throws Exception {
        states
                .withStates()
                .initial(WorkflowStates.PENDING_PLANNING_BUSINESS_REVIEW)
                .states(EnumSet.allOf(WorkflowStates.class))
                .end(WorkflowStates.COMPLETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<WorkflowStates, WorkflowEvents> transitions)
            throws Exception {
        transitions
                // Normal flow
                .withExternal()
                .source(WorkflowStates.PENDING_PLANNING_BUSINESS_REVIEW)
                .target(WorkflowStates.PENDING_PLANNING_FINANCE_APPROVAL)
                .event(WorkflowEvents.PLANNING_BUSINESS_SUBMIT)
                .action(createFinanceApprovalTask())
                .guard(processNotInRework())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_PLANNING_FINANCE_APPROVAL)
                .target(WorkflowStates.PENDING_PLANNING_OWNER_REVIEW)
                .event(WorkflowEvents.PLANNING_FINANCE_APPROVE)
                .action(createPlanningOwnerTask())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_PLANNING_OWNER_REVIEW)
                .target(WorkflowStates.PENDING_PLANNING_MANAGER_REVIEW)
                .event(WorkflowEvents.PLANNING_OWNER_SUBMIT)
                .action(createPlanningManagerTask())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_PLANNING_MANAGER_REVIEW)
                .target(WorkflowStates.COMPLETED)
                .event(WorkflowEvents.PLANNING_MANAGER_SUBMIT)
                .action(markAsCompleted());

    }
   /* public void configure(StateMachineTransitionConfigurer<WorkflowStates, WorkflowEvents> transitions)
            throws Exception {
        transitions
                // Normal flow
                .withExternal()
                .source(WorkflowStates.DRAFT)
                .target(WorkflowStates.PENDING_MANAGER_APPROVAL)
                .event(WorkflowEvents.SUBMIT)
                .action(createManagerApprovalTask())
                .guard(processNotInRework())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_MANAGER_APPROVAL)
                .target(WorkflowStates.PENDING_HR_REVIEW)
                .event(WorkflowEvents.MANAGER_APPROVE)
                .action(createHrReviewTask())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_HR_REVIEW)
                .target(WorkflowStates.PENDING_FINANCE_APPROVAL)
                .event(WorkflowEvents.HR_APPROVE)
                .action(createFinanceApprovalTask())
                .guard(financeApprovalRequired())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_HR_REVIEW)
                .target(WorkflowStates.APPROVED)
                .event(WorkflowEvents.HR_APPROVE)
                .action(markAsApproved())
                .guard(financeApprovalNotRequired())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_FINANCE_APPROVAL)
                .target(WorkflowStates.PENDING_CEO_APPROVAL)
                .event(WorkflowEvents.FINANCE_APPROVE)
                .action(createCeoApprovalTask())
                .guard(ceoApprovalRequired())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_FINANCE_APPROVAL)
                .target(WorkflowStates.APPROVED)
                .event(WorkflowEvents.FINANCE_APPROVE)
                .action(markAsApproved())
                .guard(ceoApprovalNotRequired())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_CEO_APPROVAL)
                .target(WorkflowStates.APPROVED)
                .event(WorkflowEvents.CEO_APPROVE)
                .action(markAsApproved())
                // Rework transitions
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_MANAGER_APPROVAL)
                .target(WorkflowStates.REWORK_DRAFT)
                .event(WorkflowEvents.REWORK_TO_DRAFT)
                .action(createReworkTask())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_HR_REVIEW)
                .target(WorkflowStates.REWORK_MANAGER)
                .event(WorkflowEvents.REWORK_TO_MANAGER)
                .action(createReworkTask())
                .and()
                .withExternal()
                .source(WorkflowStates.PENDING_FINANCE_APPROVAL)
                .target(WorkflowStates.REWORK_HR)
                .event(WorkflowEvents.REWORK_TO_HR)
                .action(createReworkTask())
                // Rework forward flow - with potential skips
                .and()
                .withExternal()
                .source(WorkflowStates.REWORK_MANAGER)
                .target(WorkflowStates.PENDING_HR_REVIEW)
                .event(WorkflowEvents.MANAGER_APPROVE)
                .action(createHrReviewTask())
                .guard(reworkSkipNotAllowed())
                .and()
                .withExternal()
                .source(WorkflowStates.REWORK_MANAGER)
                .target(WorkflowStates.APPROVED)
                .event(WorkflowEvents.MANAGER_APPROVE)
                .action(markAsApproved())
                .guard(reworkSkipAllowed());
    }*/

    // Guards for conditional workflow
    private Guard<WorkflowStates, WorkflowEvents> financeApprovalRequired() {
        return context -> {
            Map<String, Object> processData = getProcessData(context);
            return ruleService.evaluateRule("FINANCE_APPROVAL_REQUIRED", processData);
        };
    }

    private Guard<WorkflowStates, WorkflowEvents> financeApprovalNotRequired() {
        return context -> {
            Map<String, Object> processData = getProcessData(context);
            return !ruleService.evaluateRule("FINANCE_APPROVAL_REQUIRED", processData);
        };
    }

    /*private Guard<WorkflowStates, WorkflowEvents> ceoApprovalRequired() {
        return context -> {
            Map<String, Object> processData = getProcessData(context);
            return ruleService.evaluateRule("CEO_APPROVAL_REQUIRED", processData);
        };
    }

    private Guard<WorkflowStates, WorkflowEvents> ceoApprovalNotRequired() {
        return context -> {
            Map<String, Object> processData = getProcessData(context);
            return !ruleService.evaluateRule("CEO_APPROVAL_REQUIRED", processData);
        };
    }*/

    private Guard<WorkflowStates, WorkflowEvents> processNotInRework() {
        return context -> {
            Boolean isRework = context.getExtendedState().get("isRework", Boolean.class);
            return isRework == null || !isRework;
        };
    }

    private Guard<WorkflowStates, WorkflowEvents> reworkSkipAllowed() {
        return context -> {
            Boolean skipAllowed = context.getExtendedState().get("reworkSkipAllowed", Boolean.class);
            return skipAllowed != null && skipAllowed;
        };
    }

    private Guard<WorkflowStates, WorkflowEvents> reworkSkipNotAllowed() {
        return context -> {
            Boolean skipAllowed = context.getExtendedState().get("reworkSkipAllowed", Boolean.class);
            return skipAllowed == null || !skipAllowed;
        };
    }

    private Action<WorkflowStates, WorkflowEvents> createPlanningOwnerTask() {
        return context -> {
            String processInstanceId = getProcessInstanceId(context);
            Map<String, Object> processData = getProcessData(context);

            // Create task for multiple managers if needed
            List<String> managers = List.of("U1002","U1009");
            taskService.createTaskGroup(processInstanceId, "Business Owner Approval", managers,
                    CompletionStrategy.ANY_ONE, WorkflowStates.PENDING_PLANNING_OWNER_REVIEW,
                    "Please review and approve this request");
        };
    }

    private Action<WorkflowStates, WorkflowEvents> createPlanningManagerTask() {
        return context -> {
            String processInstanceId = getProcessInstanceId(context);
            Map<String, Object> processData = getProcessData(context);

            // Create task for multiple managers if needed
            List<String> managers = List.of("U1003","U1007");
            taskService.createTaskGroup(processInstanceId, "Manager Approval", managers,
                    CompletionStrategy.ANY_ONE, WorkflowStates.PENDING_PLANNING_MANAGER_REVIEW,
                    "Please review and approve this request");
        };
    }

    // Actions
    /*private Action<WorkflowStates, WorkflowEvents> createManagerApprovalTask() {
        return context -> {
            String processInstanceId = getProcessInstanceId(context);
            Map<String, Object> processData = getProcessData(context);

            // Create task for multiple managers if needed
            List<String> managers = List.of("U1001");
            taskService.createTaskGroup(processInstanceId, "Manager Approval", managers,
                    CompletionStrategy.ANY_ONE, WorkflowStates.PENDING_MANAGER_APPROVAL,
                    "Please review and approve this request");
        };
    }*/

    /*private Action<WorkflowStates, WorkflowEvents> createHrReviewTask() {
        return context -> {
            String processInstanceId = getProcessInstanceId(context);
            taskService.createSingleTask(processInstanceId, "HR Review", "hr-specialist",
                    WorkflowStates.PENDING_HR_REVIEW,
                    "Please review HR compliance");
        };
    }*/

    private Action<WorkflowStates, WorkflowEvents> createFinanceApprovalTask() {
        return context -> {
            String processInstanceId = getProcessInstanceId(context);
            List<String> financeTeam = Arrays.asList("U1004", "U1010");
            taskService.createTaskGroup(processInstanceId, "Planning Finance Approval", financeTeam,
                    CompletionStrategy.ANY_ONE, WorkflowStates.PENDING_PLANNING_FINANCE_APPROVAL,
                    "Please review financial impact");
        };
    }

    /*private Action<WorkflowStates, WorkflowEvents> createCeoApprovalTask() {
        return context -> {
            String processInstanceId = getProcessInstanceId(context);
            taskService.createSingleTask(processInstanceId, "CEO Approval", "ceo",
                    WorkflowStates.PENDING_CEO_APPROVAL,
                    "CEO approval required for high-value request");
        };
    }*/

    private Action<WorkflowStates, WorkflowEvents> createReworkTask() {
        return context -> {
            String processInstanceId = getProcessInstanceId(context);
            String initiatorUserId = context.getExtendedState().get("initiatorUserId", String.class);

            taskService.createReworkTask(processInstanceId, "Rework Required", initiatorUserId,
                    context.getSource().getId(), context.getTarget().getId());
        };
    }

    private Action<WorkflowStates, WorkflowEvents> markAsApproved() {
        return context -> {
            String processInstanceId = getProcessInstanceId(context);
            taskService.completeProcess(processInstanceId);
        };
    }

    private Action<WorkflowStates, WorkflowEvents> markAsCompleted() {
        return context -> {
            String processInstanceId = getProcessInstanceId(context);
            taskService.completeProcess(processInstanceId);
        };
    }

    private String getProcessInstanceId(StateContext<WorkflowStates, WorkflowEvents> context) {
        return context.getExtendedState().get("processInstanceId", String.class);
    }

    private Map<String, Object> getProcessData(StateContext<WorkflowStates, WorkflowEvents> context) {
        return context.getExtendedState().get("processData", Map.class);
    }
}
