package com.taskflow.task.model;

/**
 * Task priority enum — maps to lowercase strings in the DB via TaskPriorityConverter.
 * Values: low | medium | high
 */
public enum TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}
