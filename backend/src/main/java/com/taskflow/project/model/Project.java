package com.taskflow.project.model;

import com.taskflow.task.model.Task;
import com.taskflow.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Project entity — SRP: represents the project data model only.
 *
 * taskCount uses @Formula to compute via SQL sub-query on load,
 * avoiding N+1 for the list endpoint without additional service logic.
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
public class Project {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Computed at SQL-level on every load — no separate count query needed.
     * Consistent with the actual tasks table regardless of in-memory state.
     */
    @Formula("(SELECT COUNT(*) FROM tasks t WHERE t.project_id = id)")
    private Long taskCount;

    /**
     * Lazily loaded. Fetched explicitly only when needed (GET /projects/:id).
     * Use findByIdWithTasks() in ProjectRepository to avoid N+1.
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
