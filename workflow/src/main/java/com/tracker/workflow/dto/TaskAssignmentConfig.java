package com.tracker.workflow.dto;

import com.tracker.workflow.model.WorkflowTaskAssignment;
import com.tracker.workflow.model.CompletionStrategy;
import java.util.List;

public class TaskAssignmentConfig {
    private final String taskName;
    private final List<String> assignees;
    private final String completionStrategy;
    private final String description;
    private final WorkflowTaskAssignment.AssignmentType assignmentType;
    private final String assigneeValue;
    
    public TaskAssignmentConfig(String taskName, List<String> assignees, String completionStrategy, 
                               String description, WorkflowTaskAssignment.AssignmentType assignmentType, String assigneeValue) {
        this.taskName = taskName;
        this.assignees = assignees;
        this.completionStrategy = completionStrategy;
        this.description = description;
        this.assignmentType = assignmentType;
        this.assigneeValue = assigneeValue;
    }
    
    public String getTaskName() { return taskName; }
    public List<String> getAssignees() { return assignees; }
    public String getCompletionStrategy() { return completionStrategy; }
    public String getDescription() { return description; }
    public WorkflowTaskAssignment.AssignmentType getAssignmentType() { return assignmentType; }
    public String getAssigneeValue() { return assigneeValue; }
}
