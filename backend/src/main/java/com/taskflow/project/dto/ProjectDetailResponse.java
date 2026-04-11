package com.taskflow.project.dto;

import com.taskflow.task.dto.TaskResponse;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Response body for GET /projects/:id and POST /projects — full detail with task list */
@Data
@Builder
public class ProjectDetailResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private String ownerName;
    private OffsetDateTime createdAt;
    private Long taskCount;
    private List<TaskResponse> tasks;
}
