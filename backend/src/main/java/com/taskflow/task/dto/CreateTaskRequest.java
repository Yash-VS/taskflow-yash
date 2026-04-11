package com.taskflow.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/** Request body for POST /projects/:id/tasks */
public record CreateTaskRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 500, message = "Title must be at most 500 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Status is required")
        String status,                          // (todo | in_progress | done)

        @NotBlank(message = "Priority is required")
        String priority,                        // (low | medium | high)

        @NotNull(message = "Assignee is required")
        UUID assigneeId,

        @NotNull(message = "Due date is required")
        LocalDate dueDate,

        @NotNull(message = "Story points are required")
        Integer storyPoints
) {}
