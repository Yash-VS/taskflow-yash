package com.taskflow.task.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * TaskResponse — the unified task DTO used in both:
 *  - GET /projects/:id (project detail, tasks nested inside)
 *  - GET /projects/:id/tasks (task list)
 *  - PATCH /tasks/:id (updated task)
 *
 * status and priority are serialised as lowercase strings
 * (conversion done in TaskMapper via expression mapping).
 */
@Data
@Builder
public class TaskResponse {
    private UUID id;
    private String title;
    private String description;
    private String status;      // "todo" | "in_progress" | "done"
    private String priority;    // "low" | "medium" | "high"
    private Integer storyPoints;
    private UUID projectId;
    private UUID assigneeId;    // nullable
    private String assigneeName;
    private UUID creatorId;
    private LocalDate dueDate;  // nullable
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
