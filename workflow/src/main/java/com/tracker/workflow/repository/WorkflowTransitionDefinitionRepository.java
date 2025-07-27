package com.tracker.workflow.repository;

import com.tracker.workflow.model.WorkflowTransitionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowTransitionDefinitionRepository extends JpaRepository<WorkflowTransitionDefinition, Long> {
    
    List<WorkflowTransitionDefinition> findByWorkflowDefinitionIdOrderByTransitionOrder(Long workflowDefinitionId);
    
    List<WorkflowTransitionDefinition> findByFromStateIdOrderByTransitionOrder(Long fromStateId);
    
    List<WorkflowTransitionDefinition> findByToStateIdOrderByTransitionOrder(Long toStateId);
}
