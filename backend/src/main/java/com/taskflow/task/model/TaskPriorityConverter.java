package com.taskflow.task.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts TaskPriority ↔ lowercase DB string.
 * DB stores: "low" | "medium" | "high"
 * Java stores: LOW | MEDIUM | HIGH
 */
@Converter(autoApply = true)
public class TaskPriorityConverter implements AttributeConverter<TaskPriority, String> {

    @Override
    public String convertToDatabaseColumn(TaskPriority priority) {
        if (priority == null) return null;
        return priority.name().toLowerCase();           // HIGH → "high"
    }

    @Override
    public TaskPriority convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return TaskPriority.valueOf(dbData.toUpperCase()); // "high" → HIGH
    }
}
