package com.taskflow.task.controller;

import com.taskflow.security.UserPrincipal;
import com.taskflow.task.dto.CreateTaskRequest;
import com.taskflow.task.dto.TaskResponse;
import com.taskflow.task.dto.UpdateTaskRequest;
import com.taskflow.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * TaskController — SRP: HTTP layer only. No business logic.
 * DIP: depends on TaskService interface.
 *
 * Note: no class-level @RequestMapping because the endpoints live under two
 * different path prefixes (/projects/:id/tasks and /tasks/:id).
 */
@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * GET /projects/:id/tasks
     * Lists tasks for the project with optional filters.
     * 200 OK with task array; 403 if user has no access; 404 if project not found.
     */
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(
            @PathVariable UUID projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID assignee,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                taskService.getTasksByProject(projectId, status, assignee, principal.userId())
        );
    }

    /**
     * POST /projects/:id/tasks
     * Creates a task in the given project. Creator = current authenticated user.
     * 201 Created with the new task; 403 if no access; 404 if project/assignee not found.
     */
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(projectId, request, principal.userId()));
    }

    /**
     * PATCH /tasks/:id
     * Updates any combination of task fields. Any project member can update.
     * 200 OK with updated task; 403 if no access; 404 if task/assignee not found.
     */
    @PatchMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(taskService.updateTask(id, request, principal.userId()));
    }

    /**
     * DELETE /tasks/:id
     * Deletes the task. Only the project owner OR the task creator can delete.
     * 204 No Content on success; 403 if unauthorized; 404 if not found.
     */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {

        taskService.deleteTask(id, principal.userId());
        return ResponseEntity.noContent().build();
    }
}
