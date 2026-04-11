package com.taskflow.task.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request body for PATCH /tasks/:id — all fields optional (PATCH semantics).
 *
 * null = "do not change this field".
 *
 * Assignee note: to change assignee, provide a UUID. To ASSIGN to a different user,
 * send "assigneeId": "new-uuid". Clearing the assignee (unassigning) requires sending
 * "assigneeId": null — BUT because null is indistinguishable from "field absent" in
 * plain Java records, unassigning is not supported via this endpoint by design.
 * Documented tradeoff: a future improvement would use JsonNullable<UUID> from
 * OpenAPI Generator to distinguish absent vs explicit null.
 */
public record UpdateTaskRequest(

        @Size(min = 1, max = 500, message = "Title must be between 1 and 500 characters")
        String title,

        @Size(min = 1, message = "Description cannot be empty")
        String description,

        String status,

        String priority,

        UUID assigneeId,

        LocalDate dueDate,

        Integer storyPoints
) {}
