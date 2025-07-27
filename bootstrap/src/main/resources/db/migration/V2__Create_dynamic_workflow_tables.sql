CREATE TABLE workflow_definitions (
    id SERIAL PRIMARY KEY,
    workflow_name VARCHAR(255) NOT NULL,
    version VARCHAR(50) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activated_date TIMESTAMP,
    UNIQUE(workflow_name, version)
);

CREATE TABLE workflow_states (
    id SERIAL PRIMARY KEY,
    workflow_definition_id BIGINT NOT NULL REFERENCES workflow_definitions(id) ON DELETE CASCADE,
    state_name VARCHAR(100) NOT NULL,
    state_type VARCHAR(50) NOT NULL, -- INITIAL, NORMAL, END
    display_name VARCHAR(255),
    description TEXT,
    state_order INTEGER,
    metadata JSONB,
    UNIQUE(workflow_definition_id, state_name)
);

CREATE TABLE workflow_transitions (
    id SERIAL PRIMARY KEY,
    workflow_definition_id BIGINT NOT NULL REFERENCES workflow_definitions(id) ON DELETE CASCADE,
    from_state_id BIGINT REFERENCES workflow_states(id),
    to_state_id BIGINT NOT NULL REFERENCES workflow_states(id),
    event_name VARCHAR(100) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    guard_expression TEXT, -- JSON-based condition expression
    action_config JSONB, -- Action configuration
    transition_order INTEGER DEFAULT 0
);

CREATE TABLE workflow_roles (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(255),
    description TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL REFERENCES workflow_roles(id) ON DELETE CASCADE,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id)
);

CREATE TABLE workflow_task_assignments (
    id SERIAL PRIMARY KEY,
    workflow_definition_id BIGINT NOT NULL REFERENCES workflow_definitions(id) ON DELETE CASCADE,
    state_id BIGINT NOT NULL REFERENCES workflow_states(id) ON DELETE CASCADE,
    assignment_type VARCHAR(50) NOT NULL, -- ROLE, USER, DYNAMIC
    assignment_config JSONB NOT NULL, -- Role names, user IDs, or dynamic rules
    completion_strategy VARCHAR(50) NOT NULL, -- ANY_ONE, ALL_REQUIRED, MAJORITY
    task_template JSONB, -- Task name, description, priority templates
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE workflow_rules_v2 (
    id SERIAL PRIMARY KEY,
    workflow_definition_id BIGINT NOT NULL REFERENCES workflow_definitions(id) ON DELETE CASCADE,
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL, -- GUARD, ACTION, VALIDATION
    condition_expression TEXT, -- JSON-based expression
    action_config JSONB,
    priority INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workflow_definitions_name_active ON workflow_definitions(workflow_name, is_active);
CREATE INDEX idx_workflow_states_definition ON workflow_states(workflow_definition_id);
CREATE INDEX idx_workflow_transitions_definition ON workflow_transitions(workflow_definition_id);
CREATE INDEX idx_workflow_transitions_from_state ON workflow_transitions(from_state_id);
CREATE INDEX idx_workflow_transitions_to_state ON workflow_transitions(to_state_id);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_workflow_task_assignments_state ON workflow_task_assignments(state_id);
CREATE INDEX idx_workflow_rules_v2_definition ON workflow_rules_v2(workflow_definition_id);

INSERT INTO workflow_roles (role_name, display_name, description) VALUES
('BUSINESS_REVIEWER', 'Business Reviewer', 'Reviews business requirements and planning'),
('FINANCE_APPROVER', 'Finance Approver', 'Approves financial aspects of requests'),
('OWNER_REVIEWER', 'Owner Reviewer', 'Reviews ownership and responsibility assignments'),
('MANAGER_REVIEWER', 'Manager Reviewer', 'Final management review and approval'),
('ADMIN', 'Administrator', 'System administrator with full access');

INSERT INTO user_roles (user_id, role_id) VALUES
('U1001', (SELECT id FROM workflow_roles WHERE role_name = 'BUSINESS_REVIEWER')),
('U1002', (SELECT id FROM workflow_roles WHERE role_name = 'OWNER_REVIEWER')),
('U1003', (SELECT id FROM workflow_roles WHERE role_name = 'MANAGER_REVIEWER')),
('U1004', (SELECT id FROM workflow_roles WHERE role_name = 'FINANCE_APPROVER')),
('U1007', (SELECT id FROM workflow_roles WHERE role_name = 'MANAGER_REVIEWER')),
('U1009', (SELECT id FROM workflow_roles WHERE role_name = 'OWNER_REVIEWER')),
('U1010', (SELECT id FROM workflow_roles WHERE role_name = 'FINANCE_APPROVER'));
