package com.tracker.workflow.repository;

import com.tracker.workflow.model.WorkflowStateDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowStateDefinitionRepository extends JpaRepository<WorkflowStateDefinition, Long> {
    
    List<WorkflowStateDefinition> findByWorkflowDefinitionIdOrderByStateOrder(Long workflowDefinitionId);
    
    Optional<WorkflowStateDefinition> findByWorkflowDefinitionIdAndStateName(Long workflowDefinitionId, String stateName);
    
    List<WorkflowStateDefinition> findByWorkflowDefinitionIdAndStateType(Long workflowDefinitionId, WorkflowStateDefinition.StateType stateType);
}
