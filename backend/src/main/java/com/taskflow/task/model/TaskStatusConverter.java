package com.taskflow.task.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts TaskStatus ↔ lowercase DB string.
 * DB stores: "todo" | "in_progress" | "done"
 * Java stores: TODO | IN_PROGRESS | DONE
 *
 * SRP: single responsibility — handles enum serialisation to/from DB only.
 * autoApply = true means this converter applies automatically to all TaskStatus fields.
 */
@Converter(autoApply = true)
public class TaskStatusConverter implements AttributeConverter<TaskStatus, String> {

    @Override
    public String convertToDatabaseColumn(TaskStatus status) {
        if (status == null) return null;
        return status.name().toLowerCase();           // IN_PROGRESS → "in_progress"
    }

    @Override
    public TaskStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return TaskStatus.valueOf(dbData.toUpperCase()); // "in_progress" → IN_PROGRESS
    }
}
