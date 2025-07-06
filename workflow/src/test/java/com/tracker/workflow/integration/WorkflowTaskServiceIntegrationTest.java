package com.tracker.workflow.integration;

import com.tracker.workflow.model.*;
import com.tracker.workflow.repository.ProcessHistoryRepository;
import com.tracker.workflow.repository.TaskGroupRepository;
import com.tracker.workflow.repository.WorkflowTaskRepository;
import com.tracker.workflow.service.WorkflowService;
import com.tracker.workflow.service.WorkflowTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the WorkflowTaskService.
 * These tests verify that the task management system works correctly with a real database.
 */

public class WorkflowTaskServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WorkflowTaskService taskService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private WorkflowTaskRepository taskRepository;

    @Autowired
    private TaskGroupRepository taskGroupRepository;

    @Autowired
    private ProcessHistoryRepository historyRepository;

    /**
     * Test creating and completing a single task.
     */
    @Test
    public void testCreateAndCompleteSingleTask() {
        // Arrange - Start a workflow
        String userId = "U1000";
        Map<String, Object> processData = createTestProcessData(userId);
        String processInstanceId = workflowService.startWorkflow(userId, processData);

        // Act - Create a single task
        String taskName = "Review Expense Request";
        String assignedUserId = "U1000";
        taskService.createSingleTask(processInstanceId, taskName, assignedUserId,
                WorkflowStates.PENDING_PLANNING_BUSINESS_REVIEW, "Please review this expense request");

        // Assert - Task is created
        List<WorkflowTask> tasks = taskRepository.findByProcessInstanceIdOrderByCreatedDate(processInstanceId);
        assertFalse(tasks.isEmpty(), "Task list should not be empty");
        WorkflowTask task = tasks.get(0);
        assertEquals(taskName, task.getTaskName(), "Task name should match");
        assertEquals(assignedUserId, task.getAssignedUserId(), "Assigned user ID should match");
        assertEquals(TaskStatus.PENDING, task.getStatus(), "Task status should be PENDING");

        // Act - Complete the task
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("approved", true);
        taskData.put("comments", "Submitting to finance approval");
        boolean completed = taskService.completeTask(task.getId(), assignedUserId, taskData);

        // Assert - Task is completed
        assertTrue(completed, "Task completion should return true");

        WorkflowTask completedTask = taskRepository.findById(task.getId()).orElse(null);
        assertNotNull(completedTask, "Task should exist");
        assertEquals(TaskStatus.COMPLETED, completedTask.getStatus(), "Task status should be COMPLETED");
        assertNotNull(completedTask.getCompletedDate(), "Completed date should not be null");
        assertEquals(assignedUserId, completedTask.getCompletedByUserId(), "Completed by user ID should match");
    }

    /**
     * Test creating and managing a task group.
     */
    @Test
    public void testTaskGroupWithAnyOneStrategy() {
        // Arrange - Start a workflow
        String userId = "U1000";
        Map<String, Object> processData = createTestProcessData(userId);
        String processInstanceId = workflowService.startWorkflow(userId, processData);

        // Act - Create a task group with ANY_ONE strategy
        String taskName = "Business Review";
        List<String> assignedUsers = Arrays.asList("U1000","U1005");
        taskService.createTaskGroup(processInstanceId, taskName, assignedUsers,
                CompletionStrategy.ANY_ONE, WorkflowStates.PENDING_PLANNING_BUSINESS_REVIEW,
                "Please review this expense request");

        // Assert - Task group and tasks are created
        List<TaskGroup> taskGroups = taskGroupRepository.findByProcessInstanceId(processInstanceId);
        assertFalse(taskGroups.isEmpty(), "Task group list should not be empty");

        TaskGroup taskGroup = taskGroups.get(0);
        assertEquals(taskName, taskGroup.getGroupName(), "Task group name should match");
        assertEquals(CompletionStrategy.ANY_ONE, taskGroup.getCompletionStrategy(), "Completion strategy should be ANY_ONE");
        assertEquals(2, taskGroup.getTotalTasks(), "Total tasks should be 3");
        assertEquals(1, taskGroup.getRequiredCompletions(), "Required completions should be 1 for ANY_ONE strategy");
        assertEquals(TaskStatus.PENDING, taskGroup.getStatus(), "Task group status should be PENDING");

        List<WorkflowTask> tasks = taskRepository.findByTaskGroupIdAndStatus(taskGroup.getId(), TaskStatus.PENDING);
        assertEquals(2, tasks.size(), "Should have 2 tasks in the group");

        // Act - Complete one task in the group
        WorkflowTask firstTask = tasks.get(0);
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("approved", true);
        taskData.put("comments", "Expense approved by finance");
        boolean completed = taskService.completeTask(firstTask.getId(), firstTask.getAssignedUserId(), taskData);

        // Assert - Task group is completed after one task is completed (ANY_ONE strategy)
        assertTrue(completed, "Task completion should return true");

        TaskGroup completedGroup = taskGroupRepository.findById(taskGroup.getId()).orElse(null);
        assertNotNull(completedGroup, "Task group should exist");
        assertEquals(TaskStatus.COMPLETED, completedGroup.getStatus(), "Task group status should be COMPLETED");
        assertNotNull(completedGroup.getCompletedDate(), "Completed date should not be null");
        assertEquals(1, completedGroup.getCompletedTasks(), "Completed tasks should be 1");

        // Assert - Other tasks in the group are skipped
        List<WorkflowTask> remainingTasks = taskRepository.findByTaskGroupIdAndStatus(taskGroup.getId(), TaskStatus.SKIPPED);
        assertEquals(1, remainingTasks.size(), "Should have 2 skipped tasks in the group");
    }

    /**
     * Test task delegation.
     */

    public void testTaskDelegation() {
        // Arrange - Start a workflow and create a task
        /*String userId = "test-user-7";
        Map<String, Object> processData = createTestProcessData(userId);
        String processInstanceId = workflowService.startWorkflow(userId, processData);

        String taskName = "HR Review";
        String originalAssignee = "hr-1";
        taskService.createSingleTask(processInstanceId, taskName, originalAssignee,
                WorkflowStates.PENDING_HR_REVIEW, "Please review this expense request");

        List<WorkflowTask> tasks = taskRepository.findByProcessInstanceIdOrderByCreatedDate(processInstanceId);
        WorkflowTask task = tasks.get(0);

        // Act - Delegate the task
        String newAssignee = "hr-2";
        String delegationReason = "Out of office";
        taskService.delegateTask(task.getId(), originalAssignee, newAssignee, delegationReason);

        // Assert - Task is delegated
        WorkflowTask delegatedTask = taskRepository.findById(task.getId()).orElse(null);
        assertNotNull(delegatedTask, "Task should exist");
        assertEquals(newAssignee, delegatedTask.getAssignedUserId(), "Task should be assigned to the new user");
        assertEquals(TaskStatus.PENDING, delegatedTask.getStatus(), "Task status should still be PENDING");

        // Verify history record for delegation
        List<ProcessHistory> history = historyRepository.findByProcessInstanceIdOrderByTimestamp(processInstanceId);
        boolean hasDelegationEvent = history.stream()
                .anyMatch(h -> h.getEvent() == WorkflowEvents.TASK_DELEGATED);
        assertTrue(hasDelegationEvent, "Should have a TASK_DELEGATED event in history");*/
    }

    /**
     * Test task escalation.
     */

    public void testTaskEscalation() {
        // Arrange - Start a workflow and create a task
        /*String userId = "test-user-8";
        Map<String, Object> processData = createTestProcessData(userId);
        String processInstanceId = workflowService.startWorkflow(userId, processData);

        String taskName = "CEO Review";
        String assignee = "ceo-assistant";
        taskService.createSingleTask(processInstanceId, taskName, assignee,
                WorkflowStates.PENDING_CEO_APPROVAL, "Please review this high-value expense request");

        List<WorkflowTask> tasks = taskRepository.findByProcessInstanceIdOrderByCreatedDate(processInstanceId);
        WorkflowTask task = tasks.get(0);

        // Act - Escalate the task
        String escalationReason = "Needs immediate attention";
        taskService.escalateTask(task.getId(), assignee, escalationReason);

        // Assert - Task is still assigned to the same user but has an escalation record
        WorkflowTask escalatedTask = taskRepository.findById(task.getId()).orElse(null);
        assertNotNull(escalatedTask, "Task should exist");
        assertEquals(assignee, escalatedTask.getAssignedUserId(), "Task should still be assigned to the same user");
        assertEquals(TaskStatus.PENDING, escalatedTask.getStatus(), "Task status should still be PENDING");

        // Verify history record for escalation
        List<ProcessHistory> history = historyRepository.findByProcessInstanceIdOrderByTimestamp(processInstanceId);
        boolean hasEscalationEvent = history.stream()
                .anyMatch(h -> h.getEvent() == WorkflowEvents.TASK_ESCALATED);
        assertTrue(hasEscalationEvent, "Should have a TASK_ESCALATED event in history");*/
    }

    /**
     * Test completing a process and its tasks.
     */
    @Test
    public void testCompleteProcess() {
        // Arrange - Start a workflow and create multiple tasks
        String userId = "U1000";
        Map<String, Object> processData = createTestProcessData(userId);
        String processInstanceId = workflowService.startWorkflow(userId, processData);

        // Create individual tasks
        taskService.createSingleTask(processInstanceId, "Task 1", userId,
                WorkflowStates.PENDING_PLANNING_BUSINESS_REVIEW, "Business Review");
        taskService.createSingleTask(processInstanceId, "Task 2", "U1004",
                WorkflowStates.PENDING_PLANNING_FINANCE_APPROVAL, "Finance Approve");

        // Create a task group
        taskService.createTaskGroup(processInstanceId, "Group Task",
                Arrays.asList("U1000", "U1005"), CompletionStrategy.ALL_REQUIRED,
                WorkflowStates.PENDING_PLANNING_BUSINESS_REVIEW, "Group task description");

        // Act - Complete the process
        taskService.completeProcess(processInstanceId);

        // Assert - All tasks and task groups are completed
        List<WorkflowTask> tasks = taskRepository.findByProcessInstanceIdOrderByCreatedDate(processInstanceId);
        for (WorkflowTask task : tasks) {
            assertEquals(TaskStatus.COMPLETED, task.getStatus(), "All tasks should be COMPLETED");
            assertNotNull(task.getCompletedDate(), "All tasks should have a completed date");
        }

        List<TaskGroup> taskGroups = taskGroupRepository.findByProcessInstanceId(processInstanceId);
        for (TaskGroup group : taskGroups) {
            assertEquals(TaskStatus.COMPLETED, group.getStatus(), "All task groups should be COMPLETED");
            assertNotNull(group.getCompletedDate(), "All task groups should have a completed date");
        }
    }
}