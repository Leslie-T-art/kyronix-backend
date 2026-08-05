ALTER TABLE user_accounts
    ADD COLUMN IF NOT EXISTS full_name VARCHAR(255) NOT NULL DEFAULT 'Unknown User',
    ADD COLUMN IF NOT EXISTS department_id UUID,
    ADD COLUMN IF NOT EXISTS branch_id UUID,
    ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS role_definitions (
    id UUID PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS reference_data_entries (
    id UUID PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    CONSTRAINT uk_reference_data_type_code UNIQUE (type, code)
);
