-- Cleanup existing tasks with null fields
UPDATE tasks SET description = 'No description' WHERE description IS NULL;
UPDATE tasks SET due_date = CURRENT_DATE WHERE due_date IS NULL;
UPDATE tasks SET story_points = 0 WHERE story_points IS NULL;

-- Assign missing authors if they are null (sets to creator_id which is guaranteed not null)
UPDATE tasks SET assignee_id = creator_id WHERE assignee_id IS NULL;

-- Apply NOT NULL constraints
ALTER TABLE tasks ALTER COLUMN description SET NOT NULL;
ALTER TABLE tasks ALTER COLUMN assignee_id SET NOT NULL;
ALTER TABLE tasks ALTER COLUMN due_date SET NOT NULL;
ALTER TABLE tasks ALTER COLUMN story_points SET NOT NULL;
