package com.taskflow.project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Response body for GET /projects — list view with task count but no task details */
@Data
@Builder
public class ProjectResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private String ownerName;
    private OffsetDateTime createdAt;
    private Long taskCount;   // computed via @Formula in Project entity
}
