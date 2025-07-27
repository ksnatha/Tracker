package com.tracker.workflow.dto;

import lombok.Data;

@Data
public class CreateWorkflowRequest {
    private String workflowName;
    private String version;
    private String description;
    private String createdBy;
}
