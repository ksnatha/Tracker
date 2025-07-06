package com.tracker.workflow.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDTO {
    private String userId;

    private String fullName;

    private List<String> userRoles;
}
