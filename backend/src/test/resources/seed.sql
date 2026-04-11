-- Liquibase drops the tables or creates them based on XML, so this script just inserts data.
DELETE FROM tasks;
DELETE FROM projects;
DELETE FROM users;

-- Seed Users (UUID is casted using H2 valid native UUID format)
INSERT INTO users (id, email, name, password, created_at) VALUES 
('00000000-0000-0000-0000-000000000010', 'admin@example.com', 'Admin User', '$2a$10$wOqFl27PIfN599vNlH8i3e.J9vjFm/nBInH.DqBvR2R5sA4G.kL9y', CURRENT_TIMESTAMP);

INSERT INTO users (id, email, name, password, created_at) VALUES 
('00000000-0000-0000-0000-000000000020', 'user@example.com', 'Normal User', '$2a$10$wOqFl27PIfN599vNlH8i3e.J9vjFm/nBInH.DqBvR2R5sA4G.kL9y', CURRENT_TIMESTAMP);

-- Seed Projects
INSERT INTO projects (id, name, description, owner_id, created_at) VALUES
('11111111-1111-1111-1111-111111111111', 'Test Project 1', 'Test Desc', '00000000-0000-0000-0000-000000000010', CURRENT_TIMESTAMP);

-- Seed Tasks
INSERT INTO tasks (id, title, description, status, priority, project_id, creator_id, assignee_id, due_date, story_points, created_at, updated_at) VALUES
('22222222-2222-2222-2222-222222222222', 'Test Task 1', 'Desc 1', 'todo', 'medium', '11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000020', CURRENT_DATE, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
