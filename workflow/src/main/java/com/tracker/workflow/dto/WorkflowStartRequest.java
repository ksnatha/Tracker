package com.tracker.workflow.dto;

import lombok.Data;

import java.util.Map;

@Data
public class WorkflowStartRequest {
    private Map<String, Object> processData;
}