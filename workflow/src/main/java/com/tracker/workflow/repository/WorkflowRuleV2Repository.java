package com.tracker.workflow.repository;

import com.tracker.workflow.model.WorkflowRuleV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRuleV2Repository extends JpaRepository<WorkflowRuleV2, Long> {
    
    List<WorkflowRuleV2> findByWorkflowDefinitionIdAndActiveTrue(Long workflowDefinitionId);
    
    List<WorkflowRuleV2> findByWorkflowDefinitionIdAndRuleTypeAndActiveTrue(Long workflowDefinitionId, WorkflowRuleV2.RuleType ruleType);
}
