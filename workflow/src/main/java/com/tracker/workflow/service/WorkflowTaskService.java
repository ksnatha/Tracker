package com.tracker.workflow.service;

import com.tracker.workflow.exception.TaskGroupNotFoundException;
import com.tracker.workflow.exception.TaskNotFoundException;
import com.tracker.workflow.exception.UnauthorizedException;
import com.tracker.workflow.model.*;
import com.tracker.workflow.repository.ProcessHistoryRepository;
import com.tracker.workflow.repository.TaskGroupRepository;
import com.tracker.workflow.repository.WorkflowTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class WorkflowTaskService {
    @Autowired
    private final WorkflowTaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;
    private final ProcessHistoryRepository historyRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    // Create task group for multiple users
    public void createTaskGroup(String processInstanceId, String taskName, List<String> assignedUsers,
                                CompletionStrategy strategy, WorkflowStates state, String description) {

        // Create task group
        TaskGroup taskGroup = new TaskGroup();
        taskGroup.setProcessInstanceId(processInstanceId);
        taskGroup.setGroupName(taskName);
        taskGroup.setCompletionStrategy(strategy);
        taskGroup.setStatus(TaskStatus.PENDING);
        taskGroup.setTotalTasks(assignedUsers.size());
        taskGroup.setRequiredCompletions(calculateRequiredCompletions(strategy, assignedUsers.size()));
        taskGroup.setCreatedDate(LocalDateTime.now());

        taskGroup = taskGroupRepository.save(taskGroup);

        // Create individual tasks
        for (String userId : assignedUsers) {
            WorkflowTask task = new WorkflowTask();
            task.setProcessInstanceId(processInstanceId);
            task.setTaskName(taskName);
            task.setAssignedUserId(userId);
            task.setTaskGroupId(taskGroup.getId());
            task.setCurrentState(state);
            task.setStatus(TaskStatus.PENDING);
            task.setCreatedDate(LocalDateTime.now());
            task.setDueDate(LocalDateTime.now().plusDays(3));
            task.setDescription(description);
            task.setPriority("MEDIUM");

            taskRepository.save(task);

            // Send notification
            notificationService.notifyUser(userId, "New task assigned: " + taskName);
        }
    }

    // Create single task
    public void createSingleTask(String processInstanceId, String taskName, String assignedUserId,
                                 WorkflowStates state, String description) {

        WorkflowTask task = new WorkflowTask();
        task.setProcessInstanceId(processInstanceId);
        task.setTaskName(taskName);
        task.setAssignedUserId(assignedUserId);
        task.setCurrentState(state);
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedDate(LocalDateTime.now());
        task.setDueDate(LocalDateTime.now().plusDays(3));
        task.setDescription(description);
        task.setPriority("MEDIUM");

        taskRepository.save(task);

        // Send notification
        notificationService.notifyUser(assignedUserId, "New task assigned: " + taskName);
    }

    // Create rework task
    public void createReworkTask(String processInstanceId, String taskName, String assignedUserId,
                                 WorkflowStates fromState, WorkflowStates toState) {

        WorkflowTask task = new WorkflowTask();
        task.setProcessInstanceId(processInstanceId);
        task.setTaskName(taskName);
        task.setAssignedUserId(assignedUserId);
        task.setCurrentState(toState);
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedDate(LocalDateTime.now());
        task.setDueDate(LocalDateTime.now().plusDays(2)); // Shorter due date for rework
        task.setDescription("Rework required - please address feedback and resubmit");
        task.setPriority("HIGH");

        // Increment rework count
        int currentReworkCount = getCurrentReworkCount(processInstanceId);
        task.setReworkCount(currentReworkCount + 1);

        taskRepository.save(task);

        // Send notification
        notificationService.notifyUser(assignedUserId, "Rework required: " + taskName);
    }

    // Complete task with group handling
    public boolean completeTask(Long taskId, String userId, Map<String, Object> taskData) {
        WorkflowTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getAssignedUserId().equals(userId)) {
            throw new UnauthorizedException("Task not assigned to current user");
        }

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("Task is not in pending state");
        }

        // Update task
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedDate(LocalDateTime.now());
        task.setCompletedByUserId(userId);
        task.setTaskData(taskData);
        log.info("taskData {} ",taskData);
        taskRepository.save(task);

        // Record history
        recordProcessHistory(task.getProcessInstanceId(), null, task.getCurrentState(),
                determineEventFromState(task.getCurrentState()), userId, taskData);

        // Check if task group is completed
        boolean groupCompleted = false;
        if (task.getTaskGroupId() != null) {
            groupCompleted = checkAndUpdateTaskGroup(task.getTaskGroupId());
        } else {
            groupCompleted = true; // Single task
        }

        return groupCompleted;
    }

    // Check if task group completion requirements are met
    private boolean checkAndUpdateTaskGroup(Long taskGroupId) {
        TaskGroup taskGroup = taskGroupRepository.findById(taskGroupId)
                .orElseThrow(() -> new TaskGroupNotFoundException("Task group not found"));

        int completedTasks = taskRepository.countByTaskGroupIdAndStatus(taskGroupId, TaskStatus.COMPLETED);
        taskGroup.setCompletedTasks(completedTasks);

        boolean isCompleted = completedTasks >= taskGroup.getRequiredCompletions();

        if (isCompleted && taskGroup.getStatus() == TaskStatus.PENDING) {
            taskGroup.setStatus(TaskStatus.COMPLETED);
            taskGroup.setCompletedDate(LocalDateTime.now());

            // Cancel remaining pending tasks in the group
            List<WorkflowTask> pendingTasks = taskRepository.findByTaskGroupIdAndStatus(taskGroupId, TaskStatus.PENDING);
            for (WorkflowTask pendingTask : pendingTasks) {
                pendingTask.setStatus(TaskStatus.SKIPPED);
                taskRepository.save(pendingTask);
            }
        }

        taskGroupRepository.save(taskGroup);
        return isCompleted;
    }

    // Get tasks for user with filtering
    public List<WorkflowTask> getTasksForUser(String userId, TaskStatus status) {
        if (status != null) {
            return taskRepository.findByAssignedUserIdAndStatus(userId, status);
        }
        return taskRepository.findByAssignedUserIdAndStatusNot(userId, TaskStatus.SKIPPED);
    }

    /**
     * Gets a task by its ID.
     *
     * @param taskId the ID of the task to get
     * @return the task
     * @throws TaskNotFoundException if the task is not found
     */
    public WorkflowTask getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
    }

    // Get rework count for process
    private int getCurrentReworkCount(String processInstanceId) {
        return taskRepository.findMaxReworkCountByProcessInstanceId(processInstanceId)
                .orElse(0);
    }

    // Calculate required completions based on strategy
    private int calculateRequiredCompletions(CompletionStrategy strategy, int totalTasks) {
        switch (strategy) {
            case ANY_ONE:
                return 1;
            case ALL_REQUIRED:
                return totalTasks;
            case MAJORITY:
                return (totalTasks / 2) + 1;
            default:
                return 1;
        }
    }

    // Record process history
    private void recordProcessHistory(String processInstanceId, WorkflowStates fromState,
                                      WorkflowStates toState, WorkflowEvents event, String userId,
                                      Map<String, Object> contextData) {
        ProcessHistory history = new ProcessHistory();
        history.setProcessInstanceId(processInstanceId);
        history.setFromState(fromState);
        history.setToState(toState);
        history.setEvent(event);
        history.setUserId(userId);
        history.setTimestamp(LocalDateTime.now());
        history.setContextData(contextData);

        historyRepository.save(history);
    }

    private WorkflowEvents determineEventFromState(WorkflowStates state) {
        switch (state) {
            case PENDING_PLANNING_BUSINESS_REVIEW:
                return WorkflowEvents.PLANNING_BUSINESS_SUBMIT;
            case PENDING_PLANNING_FINANCE_APPROVAL:
                return WorkflowEvents.PLANNING_FINANCE_APPROVE;
            case PENDING_PLANNING_OWNER_REVIEW:
                return WorkflowEvents.PLANNING_OWNER_SUBMIT;
            case PENDING_PLANNING_MANAGER_REVIEW:
                return WorkflowEvents.PLANNING_MANAGER_SUBMIT;
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }
    }

    /**
     * Delegates a task to another user.
     *
     * @param taskId the ID of the task to delegate
     * @param currentUserId the ID of the current user
     * @param newAssigneeId the ID of the user to delegate to
     * @param reason the reason for delegation
     */
    public void delegateTask(Long taskId, String currentUserId, String newAssigneeId, String reason) {
        WorkflowTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getAssignedUserId().equals(currentUserId)) {
            throw new UnauthorizedException("Task not assigned to current user");
        }

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("Task is not in pending state");
        }

        // Validate new assignee
        if (!userService.validateUser(newAssigneeId)) {
            throw new IllegalArgumentException("Invalid assignee user ID");
        }

        // Update task
        task.setAssignedUserId(newAssigneeId);
        taskRepository.save(task);

        // Record history
        Map<String, Object> contextData = Map.of(
            "previousAssignee", currentUserId,
            "newAssignee", newAssigneeId,
            "reason", reason
        );

        recordProcessHistory(task.getProcessInstanceId(), task.getCurrentState(), task.getCurrentState(), 
                WorkflowEvents.TASK_DELEGATED, currentUserId, contextData);

        // Notify new assignee
        notificationService.notifyUser(newAssigneeId, "Task delegated to you: " + task.getTaskName() + ". Reason: " + reason);

        log.info("Task {} delegated from user {} to user {}", taskId, currentUserId, newAssigneeId);
    }

    /**
     * Escalates a task.
     *
     * @param taskId the ID of the task to escalate
     * @param userId the ID of the user escalating the task
     * @param escalationReason the reason for escalation
     */
    public void escalateTask(Long taskId, String userId, String escalationReason) {
        WorkflowTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getAssignedUserId().equals(userId)) {
            throw new UnauthorizedException("Task not assigned to current user");
        }

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("Task is not in pending state");
        }

        // Record history
        Map<String, Object> contextData = Map.of(
            "escalatedBy", userId,
            "reason", escalationReason
        );

        recordProcessHistory(task.getProcessInstanceId(), task.getCurrentState(), task.getCurrentState(), 
                WorkflowEvents.TASK_ESCALATED, userId, contextData);

        // Notify manager or supervisor
        // This would typically involve finding the manager and sending a notification
        // For now, we'll just log it
        log.info("Task {} escalated by user {}. Reason: {}", taskId, userId, escalationReason);
    }

    /**
     * Completes a workflow process.
     *
     * @param processInstanceId the ID of the process to complete
     */
    public void completeProcess(String processInstanceId) {
        // Find all tasks for this process
        List<WorkflowTask> tasks = taskRepository.findByProcessInstanceIdOrderByCreatedDate(processInstanceId);

        // Mark all pending tasks as completed
        for (WorkflowTask task : tasks) {
            if (task.getStatus() == TaskStatus.PENDING) {
                task.setStatus(TaskStatus.COMPLETED);
                task.setCompletedDate(LocalDateTime.now());
                taskRepository.save(task);
            }
        }

        // Find all task groups for this process
        List<TaskGroup> taskGroups = taskGroupRepository.findByProcessInstanceId(processInstanceId);

        // Mark all pending task groups as completed
        for (TaskGroup taskGroup : taskGroups) {
            if (taskGroup.getStatus() == TaskStatus.PENDING) {
                taskGroup.setStatus(TaskStatus.COMPLETED);
                taskGroup.setCompletedDate(LocalDateTime.now());
                taskGroupRepository.save(taskGroup);
            }
        }

        log.info("Process {} completed", processInstanceId);
    }
}
