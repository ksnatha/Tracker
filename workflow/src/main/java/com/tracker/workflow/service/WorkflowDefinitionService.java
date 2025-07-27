package com.tracker.workflow.service;

import com.tracker.workflow.model.WorkflowDefinition;
import com.tracker.workflow.repository.WorkflowDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class WorkflowDefinitionService {
    
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    
    public Optional<WorkflowDefinition> getActiveWorkflow(String workflowName) {
        return workflowDefinitionRepository.findByWorkflowNameAndIsActiveTrue(workflowName);
    }
    
    public Optional<WorkflowDefinition> getWorkflowVersion(String workflowName, String version) {
        return workflowDefinitionRepository.findByWorkflowNameAndVersion(workflowName, version);
    }
    
    public List<WorkflowDefinition> getVersionHistory(String workflowName) {
        return workflowDefinitionRepository.findByWorkflowNameOrderByCreatedDateDesc(workflowName);
    }
    
    public WorkflowDefinition createWorkflowDefinition(String workflowName, String version, String description, String createdBy) {
        WorkflowDefinition definition = new WorkflowDefinition();
        definition.setWorkflowName(workflowName);
        definition.setVersion(version);
        definition.setDescription(description);
        definition.setCreatedBy(createdBy);
        definition.setCreatedDate(LocalDateTime.now());
        definition.setIsActive(false);
        
        return workflowDefinitionRepository.save(definition);
    }
    
    public WorkflowDefinition createNewVersion(String workflowName, String version, String basedOnVersion, String createdBy) {
        Optional<WorkflowDefinition> baseWorkflow = getWorkflowVersion(workflowName, basedOnVersion);
        
        if (baseWorkflow.isEmpty()) {
            throw new IllegalArgumentException("Base workflow version not found: " + basedOnVersion);
        }
        
        WorkflowDefinition newVersion = cloneWorkflowDefinition(baseWorkflow.get(), version, createdBy);
        return workflowDefinitionRepository.save(newVersion);
    }
    
    public void activateVersion(String workflowName, String version, String activatedBy) {
        Optional<WorkflowDefinition> currentActive = getActiveWorkflow(workflowName);
        if (currentActive.isPresent()) {
            WorkflowDefinition current = currentActive.get();
            current.setIsActive(false);
            workflowDefinitionRepository.save(current);
            log.info("Deactivated workflow version: {} v{}", workflowName, current.getVersion());
        }
        
        Optional<WorkflowDefinition> newActive = getWorkflowVersion(workflowName, version);
        if (newActive.isEmpty()) {
            throw new IllegalArgumentException("Workflow version not found: " + version);
        }
        
        WorkflowDefinition workflow = newActive.get();
        workflow.setIsActive(true);
        workflow.setActivatedDate(LocalDateTime.now());
        workflowDefinitionRepository.save(workflow);
        
        log.info("Activated workflow version: {} v{} by {}", workflowName, version, activatedBy);
    }
    
    public void deactivateWorkflow(String workflowName) {
        Optional<WorkflowDefinition> activeWorkflow = getActiveWorkflow(workflowName);
        if (activeWorkflow.isPresent()) {
            WorkflowDefinition workflow = activeWorkflow.get();
            workflow.setIsActive(false);
            workflowDefinitionRepository.save(workflow);
            log.info("Deactivated workflow: {}", workflowName);
        }
    }
    
    private WorkflowDefinition cloneWorkflowDefinition(WorkflowDefinition source, String newVersion, String createdBy) {
        WorkflowDefinition clone = new WorkflowDefinition();
        clone.setWorkflowName(source.getWorkflowName());
        clone.setVersion(newVersion);
        clone.setDescription("Cloned from version " + source.getVersion());
        clone.setCreatedBy(createdBy);
        clone.setCreatedDate(LocalDateTime.now());
        clone.setIsActive(false);
        
        return clone;
    }
    
    public List<WorkflowDefinition> getAllActiveWorkflows() {
        return workflowDefinitionRepository.findAllActiveWorkflows();
    }
}
