package com.tracker.workflow.dto;

import lombok.Data;

@Data
public class TaskDelegationRequest {
    private String newAssigneeId;
    private String reason;
}