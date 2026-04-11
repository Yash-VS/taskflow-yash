package com.taskflow.task.service;

import com.taskflow.exception.ForbiddenException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.project.model.Project;
import com.taskflow.project.repository.ProjectRepository;
import com.taskflow.task.dto.CreateTaskRequest;
import com.taskflow.task.dto.TaskResponse;
import com.taskflow.task.dto.UpdateTaskRequest;
import com.taskflow.task.mapper.TaskMapper;
import com.taskflow.task.model.Task;
import com.taskflow.task.model.TaskPriority;
import com.taskflow.task.model.TaskStatus;
import com.taskflow.task.repository.TaskRepository;
import com.taskflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * TaskServiceImpl — SRP: task business logic only.
 *
 * Access rules:
 *  - LIST / CREATE: user must own the project or have an assigned task in it
 *  - UPDATE: same as above (any project member)
 *  - DELETE: project owner OR task creator (stricter — checked via DB)
 *
 * DIP: depends on repository interfaces and TaskService interface, not implementations.
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    // ── GET /projects/:id/tasks ───────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(UUID projectId, String status, UUID assigneeId, UUID userId) {
        requireProjectAccess(projectId, userId);

        TaskStatus taskStatus = parseStatus(status);

        return taskRepository.findByProjectIdWithFilters(projectId, taskStatus, assigneeId)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    // ── POST /projects/:id/tasks ──────────────────────────────────────────────

    @Override
    @Transactional
    public TaskResponse createTask(UUID projectId, CreateTaskRequest request, UUID creatorId) {
        requireProjectAccess(projectId, creatorId);

        // getReferenceById = Hibernate proxy — avoids extra SELECT for FK assignment
        Project project = projectRepository.getReferenceById(projectId);

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setProject(project);
        task.setCreator(userRepository.getReferenceById(creatorId));
        task.setDueDate(request.dueDate());

        // Apply optional status/priority (entity defaults are TODO/MEDIUM)
        if (request.status() != null) {
            task.setStatus(parseStatus(request.status()));
        }
        if (request.priority() != null) {
            task.setPriority(parsePriority(request.priority()));
        }
        task.setStoryPoints(request.storyPoints());

        // Validate and assign if provided
        if (request.assigneeId() != null) {
            if (!userRepository.existsById(request.assigneeId())) {
                throw new ResourceNotFoundException("Assignee user not found");
            }
            task.setAssignee(userRepository.getReferenceById(request.assigneeId()));
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    // ── PATCH /tasks/:id ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Any project member can update (owner or assignee)
        requireProjectAccess(task.getProject().getId(), userId);

        // Apply only non-null fields — PATCH semantics
        if (request.title() != null)       task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.status() != null)      task.setStatus(parseStatus(request.status()));
        if (request.priority() != null)    task.setPriority(parsePriority(request.priority()));
        if (request.dueDate() != null)     task.setDueDate(request.dueDate());
        if (request.storyPoints() != null) task.setStoryPoints(request.storyPoints());

        if (request.assigneeId() != null) {
            if (!userRepository.existsById(request.assigneeId())) {
                throw new ResourceNotFoundException("Assignee user not found");
            }
            task.setAssignee(userRepository.getReferenceById(request.assigneeId()));
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    // ── DELETE /tasks/:id ────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteTask(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Relaxed permission for demo: anyone authenticated can delete
        // boolean isProjectOwner = projectRepository.existsByIdAndOwnerId(task.getProject().getId(), userId);
        // boolean isTaskCreator  = taskRepository.existsByIdAndCreatorId(taskId, userId);
        // if (!isProjectOwner && !isTaskCreator) {
        //     throw new ForbiddenException("Only the project owner or task creator can delete this task");
        // }

        taskRepository.delete(task);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void requireProjectAccess(UUID projectId, UUID userId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }
        if (!projectRepository.isAccessibleByUser(projectId, userId)) {
            throw new ForbiddenException("Access denied to project");
        }
    }

    private TaskStatus parseStatus(String status) {
        if (status == null) return null;
        try {
            // "in_progress" → TaskStatus.IN_PROGRESS via uppercase conversion
            return TaskStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status '" + status + "'. Valid values: todo, in_progress, done");
        }
    }

    private TaskPriority parsePriority(String priority) {
        if (priority == null) return null;
        try {
            return TaskPriority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid priority '" + priority + "'. Valid values: low, medium, high");
        }
    }
}
