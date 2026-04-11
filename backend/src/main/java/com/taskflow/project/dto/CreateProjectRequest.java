package com.taskflow.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request body for POST /projects */
public record CreateProjectRequest(

        @NotBlank(message = "Project name is required")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,

        String description  // optional
) {}
