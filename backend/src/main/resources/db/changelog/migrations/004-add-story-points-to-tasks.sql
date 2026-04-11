--liquibase formatted sql

--changeset taskflow:004-add-story-points-to-tasks
ALTER TABLE tasks ADD COLUMN story_points INTEGER;

--rollback ALTER TABLE tasks DROP COLUMN story_points;
