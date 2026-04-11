--liquibase formatted sql

-- ============================================================
-- Migration 001 — Create users table
-- ============================================================

--changeset taskflow:001-create-users

CREATE TABLE users
(
    id         UUID         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,   -- bcrypt hash (cost 12) — NEVER plaintext
    created_at TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_users       PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

-- Fast lookups during login
CREATE INDEX idx_users_email ON users (email);

--rollback DROP TABLE IF EXISTS users CASCADE;
