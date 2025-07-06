package com.tracker.workflow.service;

import com.tracker.workflow.dto.WorkflowStatusDTO;
import com.tracker.workflow.exception.WorkflowException;
import com.tracker.workflow.listener.WorkflowStateListener;
import com.tracker.workflow.model.ProcessHistory;
import com.tracker.workflow.model.WorkflowEvents;
import com.tracker.workflow.model.WorkflowStates;
import com.tracker.workflow.repository.ProcessHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final StateMachineFactory<WorkflowStates, WorkflowEvents> stateMachineFactory;
    private final WorkflowTaskService taskService;
    private final WorkflowRuleService ruleService;
    private final ProcessHistoryRepository historyRepository;

    private final Map<String, StateMachine<WorkflowStates, WorkflowEvents>> stateMachines = new ConcurrentHashMap<>();

    public String startWorkflow(String initiatorUserId, Map<String, Object> processData) {
        String processInstanceId = UUID.randomUUID().toString();

        // Create new state machine instance
        StateMachine<WorkflowStates, WorkflowEvents> stateMachine = stateMachineFactory.getStateMachine(processInstanceId);

        // Set process context
        stateMachine.getExtendedState().getVariables().put("processInstanceId", processInstanceId);
        stateMachine.getExtendedState().getVariables().put("initiatorUserId", initiatorUserId);
        stateMachine.getExtendedState().getVariables().put("processData", processData);
        stateMachine.getExtendedState().getVariables().put("isRework", false);

        // Store state machine instance
        stateMachines.put(processInstanceId, stateMachine);

        // Add state machine listener for history tracking
        stateMachine.addStateListener(new WorkflowStateListener(processInstanceId, historyRepository));

        // Start the state machine
        stateMachine.start();

        // Send first event to move from DRAFT to first approval state
        //stateMachine.sendEvent(WorkflowEvents.SUBMIT);

        return processInstanceId;
    }

    public void triggerWorkflowEvent(String processInstanceId, WorkflowEvents event, Map<String, Object> eventData) {
        StateMachine<WorkflowStates, WorkflowEvents> stateMachine = stateMachines.get(processInstanceId);

        if (stateMachine == null) {
            throw new WorkflowException("No active workflow found for process: " + processInstanceId);
        }

        // Add event data to context
        if (eventData != null) {
            stateMachine.getExtendedState().getVariables().putAll(eventData);
        }

        // Send event
        boolean eventAccepted = stateMachine.sendEvent(event);

        if (!eventAccepted) {
            throw new WorkflowException("Event " + event + " not accepted in current state: " + stateMachine.getState().getId());
        }
    }

    public void triggerReworkEvent(String processInstanceId, WorkflowEvents reworkEvent, Map<String, Object> reworkContext) {
        StateMachine<WorkflowStates, WorkflowEvents> stateMachine = stateMachines.get(processInstanceId);

        if (stateMachine == null) {
            throw new WorkflowException("No active workflow found for process: " + processInstanceId);
        }

        // Set rework context
        stateMachine.getExtendedState().getVariables().put("isRework", true);
        stateMachine.getExtendedState().getVariables().put("reworkSkipAllowed", reworkContext.get("skipAllowed"));
        stateMachine.getExtendedState().getVariables().put("reworkReason", reworkContext.get("reworkReason"));

        // Send rework event
        stateMachine.sendEvent(reworkEvent);
    }

    public WorkflowStatusDTO getWorkflowStatus(String processInstanceId) {
        StateMachine<WorkflowStates, WorkflowEvents> stateMachine = stateMachines.get(processInstanceId);

        if (stateMachine == null) {
            return getWorkflowStatusFromHistory(processInstanceId);
        }

        return WorkflowStatusDTO.builder()
                .processInstanceId(processInstanceId)
                .currentState(stateMachine.getState().getId().name())
                .isActive(stateMachine.getState().isComposite())
                .processData((Map<String, Object>) stateMachine.getExtendedState().getVariables().get("processData"))
                .build();
    }

    private WorkflowStatusDTO getWorkflowStatusFromHistory(String processInstanceId) {
        List<ProcessHistory> history = historyRepository.findByProcessInstanceIdOrderByTimestamp(processInstanceId);

        if (history.isEmpty()) {
            return null;
        }

        ProcessHistory lastEntry = history.get(history.size() - 1);

        return WorkflowStatusDTO.builder()
                .processInstanceId(processInstanceId)
                .currentState(lastEntry.getToState().name())
                .isActive(false)
                .processData(lastEntry.getContextData())
                .build();
    }

    public List<ProcessHistory> getWorkflowHistory(String processInstanceId) {
        return historyRepository.findByProcessInstanceIdOrderByTimestamp(processInstanceId);
    }

    public void cleanupCompletedWorkflows() {
        // Remove completed state machines from memory
        stateMachines.entrySet().removeIf(entry -> {
            StateMachine<WorkflowStates, WorkflowEvents> sm = entry.getValue();
            return sm.getState().getId() == WorkflowStates.COMPLETED;
        });
    }
}
