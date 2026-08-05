CREATE TABLE IF NOT EXISTS user_accounts (
    id UUID PRIMARY KEY,
    username VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    locked BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES user_accounts(id),
    role_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_permissions (
    user_id UUID NOT NULL REFERENCES user_accounts(id),
    permission_name VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS department_head_assignments (
    assignment_id UUID PRIMARY KEY,
    department_id UUID NOT NULL,
    user_id UUID NOT NULL,
    effective_from TIMESTAMP WITH TIME ZONE,
    effective_to TIMESTAMP WITH TIME ZONE,
    active BOOLEAN NOT NULL,
    delegated BOOLEAN NOT NULL,
    delegated_by UUID,
    delegation_reason VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID NOT NULL
);
