package com.taskflow.project.service;

import com.taskflow.project.dto.CreateProjectRequest;
import com.taskflow.project.dto.ProjectDetailResponse;
import com.taskflow.project.dto.ProjectResponse;
import com.taskflow.project.dto.UpdateProjectRequest;

import java.util.List;
import java.util.UUID;

/**
 * ProjectService — ISP: focused on project CRUD and access operations only.
 * DIP: ProjectController depends on this interface.
 */
public interface ProjectService {

    List<ProjectResponse> getAccessibleProjects(UUID userId);

    ProjectDetailResponse createProject(CreateProjectRequest request, UUID ownerId);

    ProjectDetailResponse getProjectById(UUID projectId, UUID userId);

    ProjectDetailResponse updateProject(UUID projectId, UpdateProjectRequest request, UUID userId);

    void deleteProject(UUID projectId, UUID userId);

    com.taskflow.project.dto.ProjectStatsResponse getProjectStats(UUID projectId, UUID userId);
}
