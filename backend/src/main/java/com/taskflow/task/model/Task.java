package com.taskflow.task.model;

import com.taskflow.project.model.Project;
import com.taskflow.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Task entity — SRP: represents the task data model only.
 *
 * FKs (mirroring migration 003):
 *  - project_id  → CASCADE  (task is deleted with its project)
 *  - assignee_id → SET NULL (can be null; unassigned if user deleted)
 *  - creator_id  → RESTRICT (user who created cannot be deleted while task exists)
 */
@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    /** Stored as lowercase string via TaskStatusConverter (autoApply = true) */
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    /** Stored as lowercase string via TaskPriorityConverter (autoApply = true) */
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;

    /** creator_id: added to support delete permission (project owner OR task creator) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "story_points", nullable = false)
    private Integer storyPoints;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
