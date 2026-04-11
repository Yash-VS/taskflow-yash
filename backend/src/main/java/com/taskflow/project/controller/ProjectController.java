package com.taskflow.project.controller;

import com.taskflow.project.dto.CreateProjectRequest;
import com.taskflow.project.dto.ProjectDetailResponse;
import com.taskflow.project.dto.ProjectResponse;
import com.taskflow.project.dto.UpdateProjectRequest;
import com.taskflow.project.service.ProjectService;
import com.taskflow.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * ProjectController — SRP: HTTP layer only. No business logic.
 * DIP: depends on ProjectService interface.
 *
 * @AuthenticationPrincipal UserPrincipal extracts the authenticated user
 * from the SecurityContext set by JwtAuthFilter — zero extra DB calls.
 */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /** GET /projects — list all projects the user owns or has tasks in */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listProjects(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(projectService.getAccessibleProjects(principal.userId()));
    }

    /** POST /projects — create a new project (owner = current user) */
    @PostMapping
    public ResponseEntity<ProjectDetailResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(request, principal.userId()));
    }

    /** GET /projects/:id — get project details + its tasks */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDetailResponse> getProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(projectService.getProjectById(id, principal.userId()));
    }

    /** GET /projects/:id/stats — task counts by status and by assignee */
    @GetMapping("/{id}/stats")
    public ResponseEntity<com.taskflow.project.dto.ProjectStatsResponse> getProjectStats(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(projectService.getProjectStats(id, principal.userId()));
    }

    /** PATCH /projects/:id — update name/description (owner only) */
    @PatchMapping("/{id}")
    public ResponseEntity<ProjectDetailResponse> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(projectService.updateProject(id, request, principal.userId()));
    }

    /** DELETE /projects/:id — delete project and all tasks (owner only) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {

        projectService.deleteProject(id, principal.userId());
        return ResponseEntity.noContent().build();
    }
}
