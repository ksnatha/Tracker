package com.tracker.workflow.dto;

import com.tracker.workflow.model.WorkflowStates;
import lombok.Data;

@Data
public class ReworkRequest {
    private String reason;
    private WorkflowStates targetState;
    private boolean skipAllowed;
}
