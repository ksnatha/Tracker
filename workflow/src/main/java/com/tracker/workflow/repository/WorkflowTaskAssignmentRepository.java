package com.tracker.workflow.repository;

import com.tracker.workflow.model.WorkflowTaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowTaskAssignmentRepository extends JpaRepository<WorkflowTaskAssignment, Long> {
    
    List<WorkflowTaskAssignment> findByWorkflowDefinitionId(Long workflowDefinitionId);
    
    Optional<WorkflowTaskAssignment> findByStateId(Long stateId);
    
    List<WorkflowTaskAssignment> findByAssignmentType(WorkflowTaskAssignment.AssignmentType assignmentType);
}
