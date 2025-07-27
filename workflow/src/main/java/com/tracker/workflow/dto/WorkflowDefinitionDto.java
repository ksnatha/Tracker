package com.tracker.workflow.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkflowDefinitionDto {
    private Long id;
    private String workflowName;
    private String version;
    private String description;
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime activatedDate;
}
