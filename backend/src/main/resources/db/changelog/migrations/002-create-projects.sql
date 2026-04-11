--liquibase formatted sql

-- ============================================================
-- Migration 002 — Create projects table
-- ============================================================

--changeset taskflow:002-create-projects

CREATE TABLE projects
(
    id          UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id    UUID         NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_projects       PRIMARY KEY (id),
    CONSTRAINT fk_projects_owner FOREIGN KEY (owner_id)
        REFERENCES users (id)
        ON DELETE RESTRICT   -- can't delete a user who owns projects
        ON UPDATE CASCADE
);

-- Speed up "list my projects" queries
CREATE INDEX idx_projects_owner_id ON projects (owner_id);

--rollback DROP TABLE IF EXISTS projects CASCADE;
