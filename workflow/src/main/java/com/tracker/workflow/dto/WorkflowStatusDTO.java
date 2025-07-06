package com.tracker.workflow.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class WorkflowStatusDTO {
    private String processInstanceId;
    private String currentState;
    private boolean isActive;
    private Map<String, Object> processData;
    private List<String> availableActions;
}