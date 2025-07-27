package com.tracker.workflow.dto;

import lombok.Data;

@Data
public class CreateVersionRequest {
    private String version;
    private String basedOnVersion;
    private String createdBy;
}
