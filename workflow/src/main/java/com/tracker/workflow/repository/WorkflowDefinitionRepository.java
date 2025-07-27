package com.tracker.workflow.repository;

import com.tracker.workflow.model.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {
    
    Optional<WorkflowDefinition> findByWorkflowNameAndIsActiveTrue(String workflowName);
    
    Optional<WorkflowDefinition> findByWorkflowNameAndVersion(String workflowName, String version);
    
    List<WorkflowDefinition> findByWorkflowNameOrderByCreatedDateDesc(String workflowName);
    
    @Query("SELECT wd FROM WorkflowDefinition wd WHERE wd.workflowName = :workflowName AND wd.isActive = true")
    Optional<WorkflowDefinition> findActiveWorkflow(@Param("workflowName") String workflowName);
    
    @Query("SELECT wd FROM WorkflowDefinition wd WHERE wd.isActive = true")
    List<WorkflowDefinition> findAllActiveWorkflows();
}
