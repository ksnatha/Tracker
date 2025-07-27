package com.tracker.workflow.controller;

import com.tracker.workflow.dto.CreateVersionRequest;
import com.tracker.workflow.dto.CreateWorkflowRequest;
import com.tracker.workflow.dto.WorkflowDefinitionDto;
import com.tracker.workflow.model.WorkflowDefinition;
import com.tracker.workflow.service.WorkflowDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflow-admin")
@RequiredArgsConstructor
@Log4j2
class WorkflowAdminController {
    
    private final WorkflowDefinitionService workflowDefinitionService;
    
    @PostMapping("/definitions")
    ResponseEntity<WorkflowDefinitionDto> createWorkflowDefinition(@RequestBody CreateWorkflowRequest request) {
        WorkflowDefinition definition = workflowDefinitionService.createWorkflowDefinition(
            request.getWorkflowName(),
            request.getVersion(),
            request.getDescription(),
            request.getCreatedBy()
        );
        
        return ResponseEntity.ok(toDto(definition));
    }
    
    @PostMapping("/definitions/{workflowName}/versions")
    ResponseEntity<WorkflowDefinitionDto> createVersion(
            @PathVariable String workflowName, 
            @RequestBody CreateVersionRequest request) {
        
        WorkflowDefinition definition = workflowDefinitionService.createNewVersion(
            workflowName,
            request.getVersion(),
            request.getBasedOnVersion(),
            request.getCreatedBy()
        );
        
        return ResponseEntity.ok(toDto(definition));
    }
    
    @PostMapping("/definitions/{workflowName}/versions/{version}/activate")
    ResponseEntity<Void> activateVersion(
            @PathVariable String workflowName, 
            @PathVariable String version,
            @RequestParam String activatedBy) {
        
        workflowDefinitionService.activateVersion(workflowName, version, activatedBy);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/definitions/{workflowName}/deactivate")
    ResponseEntity<Void> deactivateWorkflow(@PathVariable String workflowName) {
        workflowDefinitionService.deactivateWorkflow(workflowName);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/definitions/{workflowName}/versions")
    ResponseEntity<List<WorkflowDefinitionDto>> getVersions(@PathVariable String workflowName) {
        List<WorkflowDefinition> versions = workflowDefinitionService.getVersionHistory(workflowName);
        List<WorkflowDefinitionDto> dtos = versions.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/definitions/{workflowName}/active")
    ResponseEntity<WorkflowDefinitionDto> getActiveVersion(@PathVariable String workflowName) {
        return workflowDefinitionService.getActiveWorkflow(workflowName)
            .map(this::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/definitions/active")
    ResponseEntity<List<WorkflowDefinitionDto>> getAllActiveWorkflows() {
        List<WorkflowDefinition> activeWorkflows = workflowDefinitionService.getAllActiveWorkflows();
        List<WorkflowDefinitionDto> dtos = activeWorkflows.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    private WorkflowDefinitionDto toDto(WorkflowDefinition definition) {
        WorkflowDefinitionDto dto = new WorkflowDefinitionDto();
        dto.setId(definition.getId());
        dto.setWorkflowName(definition.getWorkflowName());
        dto.setVersion(definition.getVersion());
        dto.setDescription(definition.getDescription());
        dto.setIsActive(definition.getIsActive());
        dto.setCreatedBy(definition.getCreatedBy());
        dto.setCreatedDate(definition.getCreatedDate());
        dto.setActivatedDate(definition.getActivatedDate());
        return dto;
    }
}
