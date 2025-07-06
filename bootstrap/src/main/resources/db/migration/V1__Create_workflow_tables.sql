-- Create workflow_rules table
CREATE TABLE workflow_rules (
    id SERIAL PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    condition TEXT,
    action VARCHAR(255),
    active BOOLEAN DEFAULT TRUE
);

-- Create task_groups table
CREATE TABLE task_groups (
    id SERIAL PRIMARY KEY,
    process_instance_id VARCHAR(255) NOT NULL,
    group_name VARCHAR(255) NOT NULL,
    completion_strategy VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_tasks INTEGER NOT NULL,
    completed_tasks INTEGER DEFAULT 0,
    required_completions INTEGER NOT NULL,
    created_date TIMESTAMP,
    completed_date TIMESTAMP
);

-- Create workflow_tasks table
CREATE TABLE workflow_tasks (
    id SERIAL PRIMARY KEY,
    process_instance_id VARCHAR(255) NOT NULL,
    task_name VARCHAR(255) NOT NULL,
    assigned_user_id VARCHAR(255),
    assigned_role VARCHAR(255),
    task_group_id BIGINT,
    current_state VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_date TIMESTAMP,
    due_date TIMESTAMP,
    completed_date TIMESTAMP,
    description TEXT,
    priority VARCHAR(50),
    rework_count INTEGER DEFAULT 0,
    completed_by_user_id VARCHAR(255),
    task_data JSONB,
    FOREIGN KEY (task_group_id) REFERENCES task_groups(id)
);

-- Create process_history table
CREATE TABLE process_history (
    id SERIAL PRIMARY KEY,
    process_instance_id VARCHAR(255) NOT NULL,
    from_state VARCHAR(50),
    to_state VARCHAR(50) NOT NULL,
    event VARCHAR(50) NOT NULL,
    user_id VARCHAR(255),
    timestamp TIMESTAMP NOT NULL,
    comments TEXT,
    context_data JSONB
);

-- Create indexes for better performance
CREATE INDEX idx_workflow_tasks_process_instance_id ON workflow_tasks(process_instance_id);
CREATE INDEX idx_workflow_tasks_assigned_user_id ON workflow_tasks(assigned_user_id);
CREATE INDEX idx_workflow_tasks_task_group_id ON workflow_tasks(task_group_id);
CREATE INDEX idx_task_groups_process_instance_id ON task_groups(process_instance_id);
CREATE INDEX idx_process_history_process_instance_id ON process_history(process_instance_id);