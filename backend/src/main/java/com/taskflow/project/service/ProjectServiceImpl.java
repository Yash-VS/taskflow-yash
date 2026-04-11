package com.taskflow.project.service;

import com.taskflow.exception.ForbiddenException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.project.dto.CreateProjectRequest;
import com.taskflow.project.dto.ProjectDetailResponse;
import com.taskflow.project.dto.ProjectResponse;
import com.taskflow.project.dto.UpdateProjectRequest;
import com.taskflow.project.mapper.ProjectMapper;
import com.taskflow.project.model.Project;
import com.taskflow.project.repository.ProjectRepository;
import com.taskflow.task.repository.TaskRepository;
import com.taskflow.user.repository.UserRepository;
import com.taskflow.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ProjectServiceImpl — SRP: project business logic only.
 *
 * Access rule: a user can view/access a project if they OWN it
 * OR are ASSIGNED to at least one task in it (enforced in getProjectById).
 *
 * Mutation rules:
 *  - PATCH /projects/:id — owner only (403 otherwise)
 *  - DELETE /projects/:id — owner only (403 otherwise); DB CASCADE removes tasks
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAccessibleProjects(UUID userId) {
        return projectRepository.findAccessibleByUserId(userId)
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProjectDetailResponse createProject(CreateProjectRequest request, UUID ownerId) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        // getReferenceById creates a Hibernate proxy — avoids a DB lookup for the owner FK
        project.setOwner(userRepository.getReferenceById(ownerId));

        Project saved = projectRepository.save(project);

        // Re-fetch with tasks (empty at creation) to populate @Formula taskCount
        return toDetailResponse(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectById(UUID projectId, UUID userId) {
        Project project = projectRepository.findByIdWithTasks(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        requireAccess(project, userId);

        return projectMapper.toDetailResponse(project);
    }

    @Override
    @Transactional
    public ProjectDetailResponse updateProject(UUID projectId, UpdateProjectRequest request, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        requireOwner(project, userId);

        // Apply only non-null fields (PATCH semantics)
        if (request.name() != null) {
            project.setName(request.name());
        }
        if (request.description() != null) {
            project.setDescription(request.description());
        }

        projectRepository.save(project);

        // Re-fetch to ensure tasks and @Formula taskCount are fresh
        return toDetailResponse(projectId);
    }

    @Override
    @Transactional
    public void deleteProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        requireOwner(project, userId);

        // DB CASCADE (fk_tasks_project ON DELETE CASCADE) handles task deletion
        projectRepository.delete(project);
    }

    @Override
    @Transactional(readOnly = true)
    public com.taskflow.project.dto.ProjectStatsResponse getProjectStats(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        requireAccess(project, userId);

        List<Object[]> statusCounts = taskRepository.countTasksByStatus(projectId);
        Map<String, Long> tasksByStatus = statusCounts.stream()
                .collect(Collectors.toMap(
                        arr -> arr[0] != null ? arr[0].toString() : "UNASSIGNED",
                        arr -> (Long) arr[1]
                ));

        List<Object[]> assigneeCounts = taskRepository.countTasksByAssignee(projectId);
        Map<String, Long> tasksByAssignee = assigneeCounts.stream()
                .collect(Collectors.toMap(
                        arr -> arr[0] != null ? arr[0].toString() : "Unassigned",
                        arr -> (Long) arr[1]
                ));

        return new com.taskflow.project.dto.ProjectStatsResponse(tasksByStatus, tasksByAssignee);
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /** Access rule: owner OR assignee on at least one task in the project */
    private void requireAccess(Project project, UUID userId) {
        boolean isOwner = project.getOwner().getId().equals(userId);
        boolean hasTask  = project.getTasks().stream()
                .anyMatch(t -> t.getAssignee() != null && t.getAssignee().getId().equals(userId));

        if (!isOwner && !hasTask) {
            throw new ForbiddenException("Access denied to project");
        }
    }

    /** Stricter check — used for mutating operations (PATCH, DELETE) */
    private void requireOwner(Project project, UUID userId) {
        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only the project owner can perform this action");
        }
    }

    private ProjectDetailResponse toDetailResponse(UUID projectId) {
        Project fresh = projectRepository.findByIdWithTasks(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return projectMapper.toDetailResponse(fresh);
    }
}
