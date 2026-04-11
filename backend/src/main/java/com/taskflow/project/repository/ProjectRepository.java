package com.taskflow.project.repository;

import com.taskflow.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * OCP: extend with new query methods — never modify existing ones.
 * DIP: ProjectService depends on this interface.
 */
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    /**
     * Returns projects the user OWNS or has AT LEAST ONE assigned task in.
     * DISTINCT prevents duplicates when a user both owns and is assigned in the same project.
     */
    @Query("""
            SELECT DISTINCT p FROM Project p
            WHERE p.owner.id = :userId
               OR p.id IN (
                   SELECT t.project.id FROM Task t WHERE t.assignee.id = :userId
               )
            ORDER BY p.createdAt DESC
            """)
    List<Project> findAccessibleByUserId(@Param("userId") UUID userId);

    /**
     * Fetch project with tasks in a single JOIN — avoids N+1 for GET /projects/:id.
     * DISTINCT required to deduplicate the project row when it has multiple tasks.
     */
    @Query("""
            SELECT DISTINCT p FROM Project p
            LEFT JOIN FETCH p.tasks t
            WHERE p.id = :id
            """)
    Optional<Project> findByIdWithTasks(@Param("id") UUID id);

    boolean existsByIdAndOwnerId(UUID projectId, UUID ownerId);

    /**
     * Used by TaskService to verify project access before task CRUD.
     * Native query — more readable than JPQL CASE WHEN COUNT for boolean return.
     */
    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM projects p
                WHERE p.id = :projectId
                  AND (p.owner_id = :userId
                    OR EXISTS (
                        SELECT 1 FROM tasks t
                        WHERE t.project_id = p.id
                          AND t.assignee_id = :userId
                    ))
            )
            """, nativeQuery = true)
    boolean isAccessibleByUser(@Param("projectId") UUID projectId, @Param("userId") UUID userId);
}
