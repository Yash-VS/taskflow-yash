package com.taskflow.task.service;

import com.taskflow.task.dto.CreateTaskRequest;
import com.taskflow.task.dto.TaskResponse;
import com.taskflow.task.dto.UpdateTaskRequest;

import java.util.List;
import java.util.UUID;

/**
 * TaskService — ISP: focused on task CRUD and access operations only.
 * DIP: TaskController depends on this interface.
 */
public interface TaskService {

    /** List tasks for a project with optional status/assignee filters. */
    List<TaskResponse> getTasksByProject(UUID projectId, String status, UUID assigneeId, UUID userId);

    /** Create a task in the given project. Creator = current user. */
    TaskResponse createTask(UUID projectId, CreateTaskRequest request, UUID creatorId);

    /** Update a task. Any project member (owner or assignee) can update. */
    TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, UUID userId);

    /** Delete a task. Only project owner OR task creator. */
    void deleteTask(UUID taskId, UUID userId);
}
