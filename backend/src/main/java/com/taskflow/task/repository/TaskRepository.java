package com.taskflow.task.repository;

import com.taskflow.task.model.Task;
import com.taskflow.task.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * OCP: extend with new query methods — never modify existing ones.
 */
public interface TaskRepository extends JpaRepository<Task, UUID> {

    /** List tasks for a project. Supports optional status and assignee filters. */
    @Query("""
            SELECT t FROM Task t
            WHERE t.project.id = :projectId
              AND (:status IS NULL OR t.status = :status)
              AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)
            ORDER BY t.createdAt DESC
            """)
    List<Task> findByProjectIdWithFilters(
            @Param("projectId") UUID projectId,
            @Param("status") TaskStatus status,
            @Param("assigneeId") UUID assigneeId
    );

    boolean existsByIdAndCreatorId(UUID taskId, UUID creatorId);

    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.project.id = :projectId GROUP BY t.status")
    List<Object[]> countTasksByStatus(@Param("projectId") UUID projectId);

    @Query("SELECT t.assignee.name, COUNT(t) FROM Task t WHERE t.project.id = :projectId GROUP BY t.assignee.name")
    List<Object[]> countTasksByAssignee(@Param("projectId") UUID projectId);
}
