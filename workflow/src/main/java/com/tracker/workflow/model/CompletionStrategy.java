package com.tracker.workflow.model;

/**
 * Enum representing the completion strategy for a task group.
 */
public enum CompletionStrategy {
    ANY_ONE,      // Any one task in the group needs to be completed
    ALL_REQUIRED, // All tasks in the group need to be completed
    MAJORITY      // Majority of tasks in the group need to be completed
}