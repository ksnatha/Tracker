package com.tracker.workflow.listener;

import com.tracker.workflow.model.ProcessHistory;
import com.tracker.workflow.model.WorkflowEvents;
import com.tracker.workflow.model.WorkflowStates;
import com.tracker.workflow.repository.ProcessHistoryRepository;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;

import java.time.LocalDateTime;

public class WorkflowStateListener extends StateMachineListenerAdapter<WorkflowStates, WorkflowEvents> {

    private final String processInstanceId;
    private final ProcessHistoryRepository historyRepository;

    public WorkflowStateListener(String processInstanceId, ProcessHistoryRepository historyRepository) {
        this.processInstanceId = processInstanceId;
        this.historyRepository = historyRepository;
    }

    @Override
    public void transition(Transition<WorkflowStates, WorkflowEvents> transition) {
        if (transition.getSource() != null && transition.getTarget() != null) {
            ProcessHistory history = new ProcessHistory();
            history.setProcessInstanceId(processInstanceId);
            history.setFromState(transition.getSource().getId());
            history.setToState(transition.getTarget().getId());
            history.setEvent(transition.getTrigger().getEvent());
            history.setTimestamp(LocalDateTime.now());

            historyRepository.save(history);
        }
    }
}
