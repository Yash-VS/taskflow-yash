--liquibase formatted sql

-- ============================================================
-- Migration 003 — Create tasks table
-- ============================================================
-- Design note: creator_id is not in the spec's data model but is
-- required by the delete permission rule: "project owner OR task creator".

--changeset taskflow:003-create-tasks

CREATE TABLE tasks
(
    id          UUID         NOT NULL,
    title       VARCHAR(500) NOT NULL,
    description TEXT,

    -- VARCHAR + CHECK preferred over native PG ENUM:
    -- avoids ALTER TYPE headaches when adding values in future migrations
    status      VARCHAR(20)  NOT NULL DEFAULT 'todo'   CHECK (status   IN ('todo', 'in_progress', 'done')),
    priority    VARCHAR(20)  NOT NULL DEFAULT 'medium' CHECK (priority IN ('low', 'medium', 'high')),

    project_id  UUID         NOT NULL,
    assignee_id UUID,                  -- nullable — task may be unassigned
    creator_id  UUID         NOT NULL, -- tracks who created the task (for delete auth)
    due_date    DATE,

    created_at  TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_tasks PRIMARY KEY (id),

    -- CASCADE: deleting a project removes all its tasks
    CONSTRAINT fk_tasks_project  FOREIGN KEY (project_id)
        REFERENCES projects (id) ON DELETE CASCADE  ON UPDATE CASCADE,

    -- SET NULL: unassign task if the assignee user is deleted
    CONSTRAINT fk_tasks_assignee FOREIGN KEY (assignee_id)
        REFERENCES users (id)    ON DELETE SET NULL ON UPDATE CASCADE,

    -- RESTRICT: can't delete a user who created tasks
    CONSTRAINT fk_tasks_creator  FOREIGN KEY (creator_id)
        REFERENCES users (id)    ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Indexes for the most common filter/join patterns
CREATE INDEX idx_tasks_project_id  ON tasks (project_id);
CREATE INDEX idx_tasks_assignee_id ON tasks (assignee_id);
CREATE INDEX idx_tasks_status      ON tasks (status);

--rollback DROP TABLE IF EXISTS tasks CASCADE;
