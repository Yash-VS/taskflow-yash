package com.taskflow.task.model;

/**
 * Task status enum — maps to lowercase strings in the DB via TaskStatusConverter.
 * Values: todo | in_progress | done
 */
public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE
}
