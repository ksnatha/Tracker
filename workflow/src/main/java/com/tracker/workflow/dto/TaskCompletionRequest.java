package com.tracker.workflow.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TaskCompletionRequest {
    private Map<String, Object> taskData;
    private String comments;
}
