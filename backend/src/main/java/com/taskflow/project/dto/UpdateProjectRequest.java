package com.taskflow.project.dto;

import jakarta.validation.constraints.Size;

/**
 * Request body for PATCH /projects/:id
 * All fields are optional — only non-null fields are applied (PATCH semantics).
 */
public record UpdateProjectRequest(

        @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
        String name,        // null = no change

        String description  // null = no change; empty string = clear description
) {}
