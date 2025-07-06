package com.tracker.workflow.exception;

public class TaskGroupNotFoundException extends RuntimeException {
    public TaskGroupNotFoundException(String message) {
        super(message);
    }
}