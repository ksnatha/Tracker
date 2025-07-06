package com.tracker.workflow.repository;

import com.tracker.workflow.model.TaskStatus;
import com.tracker.workflow.model.WorkflowTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, Long> {

    List<WorkflowTask> findByAssignedUserIdAndStatus(String assignedUserId, TaskStatus status);

    List<WorkflowTask> findByAssignedUserIdAndStatusNot(String assignedUserId, TaskStatus status);

    List<WorkflowTask> findByTaskGroupIdAndStatus(Long taskGroupId, TaskStatus status);

    int countByTaskGroupIdAndStatus(Long taskGroupId, TaskStatus status);

    Optional<Integer> findMaxReworkCountByProcessInstanceId(String processInstanceId);

    List<WorkflowTask> findByProcessInstanceIdOrderByCreatedDate(String processInstanceId);
}