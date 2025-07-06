package com.tracker.workflow.integration;

import com.tracker.workflow.dto.WorkflowStatusDTO;
import com.tracker.workflow.model.ProcessHistory;
import com.tracker.workflow.model.WorkflowEvents;
import com.tracker.workflow.model.WorkflowStates;
import com.tracker.workflow.repository.ProcessHistoryRepository;
import com.tracker.workflow.service.WorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the WorkflowService.
 * These tests verify that the workflow system works correctly with a real database.
 */
public class WorkflowServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private ProcessHistoryRepository historyRepository;

    /**
     * Test starting a new workflow.
     * Verifies that a new workflow is created with the correct initial state.
     */
    @Test
    public void testStartWorkflow() {
        // Arrange
        String userId = "U1000";
        Map<String, Object> processData = createTestProcessData(userId);

        // Act
        String processInstanceId = workflowService.startWorkflow(userId, processData);

        // Assert
        assertNotNull(processInstanceId, "Process instance ID should not be null");
        
        WorkflowStatusDTO status = workflowService.getWorkflowStatus(processInstanceId);
        assertNotNull(status, "Workflow status should not be null");
        assertEquals(processInstanceId, status.getProcessInstanceId(), "Process instance ID should match");
        Map<String, Object> processDataForSubmission = createTestProcessDataForWFTransistion(userId,"Submitting to next step");
        workflowService.triggerWorkflowEvent(processInstanceId,WorkflowEvents.PLANNING_BUSINESS_SUBMIT,processDataForSubmission);
        status = workflowService.getWorkflowStatus(processInstanceId);
        assertNotNull(status, "Workflow status should not be null");
        assertEquals(processInstanceId, status.getProcessInstanceId(), "Process instance ID should match");
        assertEquals(WorkflowStates.PENDING_PLANNING_FINANCE_APPROVAL.name(), status.getCurrentState(),
                "Initial state should be PENDING_PLANNING_FINANCE_APPROVAL after PLANNING_BUSINESS_SUBMIT event");
        
        // Verify history records
        List<ProcessHistory> history = workflowService.getWorkflowHistory(processInstanceId);
        assertFalse(history.isEmpty(), "Process history should not be empty");
        assertEquals(1, history.size(), "Should have  DRAFT->PENDING_PLANNING_FINANCE_APPROVAL (submit)");
        
        ProcessHistory submitEntry = history.get(0);
        assertEquals(WorkflowStates.PENDING_PLANNING_BUSINESS_REVIEW, submitEntry.getFromState(), "From state should be PENDING_PLANNING_BUSINESS_REVIEW");
        assertEquals(WorkflowStates.PENDING_PLANNING_FINANCE_APPROVAL, submitEntry.getToState(), "To state should be PENDING_MANAGER_APPROVAL");
        assertEquals(WorkflowEvents.PLANNING_BUSINESS_SUBMIT, submitEntry.getEvent(), "Event should be SUBMIT");
    }

    /**
     * Test the complete workflow approval process.
     * Verifies that the workflow transitions through all approval states correctly.
     */
    @Test
    public void testCompleteWorkflowApprovalProcess() {
        // Arrange
        String userId = "U1000";
        Map<String, Object> processData = createTestProcessData(userId);
        processData.put("amount", 15000.0); // Amount that requires all approvals
        
        // Act - Start workflow
        String processInstanceId = workflowService.startWorkflow(userId, processData);
        
        // Assert - Initial state
        WorkflowStatusDTO status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.PENDING_PLANNING_BUSINESS_REVIEW.name(), status.getCurrentState(),
                "Initial state should be PENDING_PLANNING_BUSINESS_REVIEW");
        
        // Act - Business Submit
        Map<String, Object> financeData = new HashMap<>();
        financeData.put("approverUserId", "U1004");
        financeData.put("comments", "Approved by Finance");
        workflowService.triggerWorkflowEvent(processInstanceId, WorkflowEvents.PLANNING_BUSINESS_SUBMIT, financeData);
        
        // Assert - After Business submit
        status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.PENDING_PLANNING_FINANCE_APPROVAL.name(), status.getCurrentState(),
                "State after manager approval should be PENDING_PLANNING_FINANCE_APPROVAL");
        
        // Act - Finance approval
        Map<String, Object> ownerData = new HashMap<>();
        ownerData.put("approverUserId", "U1002");
        ownerData.put("comments", "Approved by Owner");
        workflowService.triggerWorkflowEvent(processInstanceId, WorkflowEvents.PLANNING_FINANCE_APPROVE, ownerData);
        
        // Assert - After Finance approval
        status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.PENDING_PLANNING_OWNER_REVIEW.name(), status.getCurrentState(),
                "State after HR approval should be PENDING_PLANNING_OWNER_REVIEW");
        
        // Act - Owner approval
        Map<String, Object> managerData = new HashMap<>();
        managerData.put("approverUserId", "U1003");
        managerData.put("comments", "Approved by Manager");
        workflowService.triggerWorkflowEvent(processInstanceId, WorkflowEvents.PLANNING_OWNER_SUBMIT, managerData);
        
        // Assert - After Owner approval
        status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.PENDING_PLANNING_MANAGER_REVIEW.name(), status.getCurrentState(),
                "State after Finance approval should be PENDING_PLANNING_MANAGER_REVIEW");
        
        // Act - Manager approval
        Map<String, Object> closureData = new HashMap<>();
        closureData.put("comments", "Approved by Manager and end of workflow");
        workflowService.triggerWorkflowEvent(processInstanceId, WorkflowEvents.PLANNING_MANAGER_SUBMIT, closureData);

        
        // Assert - After CEO approval
        status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.COMPLETED.name(), status.getCurrentState(),
                "Final state should be COMPLETED");
        
        // Verify complete history
        List<ProcessHistory> history = workflowService.getWorkflowHistory(processInstanceId);
        assertEquals(4, history.size(), "Should have 6 history records for the complete approval flow");
        
        // Verify the workflow is completed
        /*workflowService.triggerWorkflowEvent(processInstanceId, WorkflowEvents.COMPLETE, null);
        status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.COMPLETED.name(), status.getCurrentState(), 
                "Final state after completion should be COMPLETED");*/
    }

    /**
     * Test the rejection workflow process.
     * Verifies that the workflow can be rejected at any approval stage.
     */

    public void testWorkflowRejection() {
        // Arrange
       /* String userId = "test-user-3";
        Map<String, Object> processData = createTestProcessData(userId);
        
        // Act - Start workflow
        String processInstanceId = workflowService.startWorkflow(userId, processData);
        
        // Assert - Initial state
        WorkflowStatusDTO status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.PENDING_MANAGER_APPROVAL.name(), status.getCurrentState(), 
                "Initial state should be PENDING_MANAGER_APPROVAL");
        
        // Act - Manager rejection
        Map<String, Object> rejectionData = new HashMap<>();
        rejectionData.put("rejectorUserId", "manager-1");
        rejectionData.put("rejectionReason", "Budget constraints");
        workflowService.triggerWorkflowEvent(processInstanceId, WorkflowEvents.MANAGER_REJECT, rejectionData);
        
        // Assert - After rejection
        status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.REJECTED.name(), status.getCurrentState(), 
                "State after rejection should be REJECTED");
        
        // Verify history records
        List<ProcessHistory> history = workflowService.getWorkflowHistory(processInstanceId);
        assertEquals(3, history.size(), "Should have 3 history records: start, submit, and reject");
        
        ProcessHistory rejectEntry = history.get(2);
        assertEquals(WorkflowStates.PENDING_MANAGER_APPROVAL, rejectEntry.getFromState(), "From state should be PENDING_MANAGER_APPROVAL");
        assertEquals(WorkflowStates.REJECTED, rejectEntry.getToState(), "To state should be REJECTED");
        assertEquals(WorkflowEvents.MANAGER_REJECT, rejectEntry.getEvent(), "Event should be MANAGER_REJECT");*/
    }

    /**
     * Test the rework workflow process.
     * Verifies that the workflow can be sent back for rework and then resubmitted.
     */

    public void testWorkflowRework() {
        // Arrange
        /*String userId = "test-user-4";
        Map<String, Object> processData = createTestProcessData(userId);
        
        // Act - Start workflow
        String processInstanceId = workflowService.startWorkflow(userId, processData);
        
        // Assert - Initial state
        WorkflowStatusDTO status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.PENDING_MANAGER_APPROVAL.name(), status.getCurrentState(), 
                "Initial state should be PENDING_MANAGER_APPROVAL");
        
        // Act - Send for rework
        Map<String, Object> reworkContext = new HashMap<>();
        reworkContext.put("skipAllowed", false);
        reworkContext.put("reworkReason", "Need more details");
        workflowService.triggerReworkEvent(processInstanceId, WorkflowEvents.REWORK_TO_DRAFT, reworkContext);
        
        // Assert - After rework request
        status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.REWORK_DRAFT.name(), status.getCurrentState(), 
                "State after rework request should be REWORK_DRAFT");
        
        // Act - Resubmit after rework
        Map<String, Object> updatedData = createTestProcessData(userId);
        updatedData.put("description", "Updated test expense request with more details");
        updatedData.put("additionalInfo", "Additional information requested by manager");
        workflowService.triggerWorkflowEvent(processInstanceId, WorkflowEvents.SUBMIT, updatedData);
        
        // Assert - After resubmission
        status = workflowService.getWorkflowStatus(processInstanceId);
        assertEquals(WorkflowStates.PENDING_MANAGER_APPROVAL.name(), status.getCurrentState(), 
                "State after resubmission should be PENDING_MANAGER_APPROVAL");
        
        // Verify history records
        List<ProcessHistory> history = workflowService.getWorkflowHistory(processInstanceId);
        assertTrue(history.size() >= 4, "Should have at least 4 history records for the rework flow");*/
    }
}