UPDATE user_accounts
SET full_name = 'Risk Inputter',
    department_id = 101,
    branch_id = 201,
    deleted = FALSE
WHERE id = 1001;

UPDATE user_accounts
SET full_name = 'Department Head',
    department_id = 101,
    branch_id = 201,
    deleted = FALSE
WHERE id = 1002;

INSERT INTO user_accounts (id, username, full_name, password_hash, active, locked, department_id, branch_id, deleted)
VALUES (1003, 'system.admin', 'System Administrator', '{noop}ChangeMe123!', TRUE, FALSE, 101, 201, FALSE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_name)
VALUES (1003, 'SYSTEM_ADMIN')
ON CONFLICT DO NOTHING;

INSERT INTO user_permissions (user_id, permission_name)
VALUES
    (1003, 'ADMIN_USERS'),
    (1003, 'ADMIN_REFERENCE_DATA')
ON CONFLICT DO NOTHING;

INSERT INTO role_definitions (id, code, name, description, active)
VALUES
    (301, 'INPUTTER', 'Inputter', 'Can create and submit maker workflow records', TRUE),
    (302, 'DEPARTMENT_HEAD', 'Department Head', 'Can authorize department records', TRUE),
    (303, 'SYSTEM_ADMIN', 'System Administrator', 'Can administer users and configuration', TRUE),
    (304, 'ENTERPRISE_ADMIN', 'Enterprise Administrator', 'Organization-wide read-only access to records, analytics, exports, and audit activity', TRUE),
    (305, 'EXECUTIVE', 'Executive', 'Organization-wide read-only access to records, analytics, exports, and audit activity', TRUE)
ON CONFLICT (code) DO NOTHING;

INSERT INTO reference_data_entries (id, type, code, name, active)
VALUES
    (101, 'DEPARTMENT', 'OPS', 'Operations', TRUE),
    (201, 'BRANCH', 'HQ', 'Head Office', TRUE),
    (401, 'LOSS_CATEGORY', 'INTERNAL_FRAUD', 'Internal Fraud', TRUE),
    (501, 'EVENT_TYPE', 'OPERATIONAL_LOSS', 'Operational Loss', TRUE)
ON CONFLICT (type, code) DO NOTHING;

SELECT setval('user_accounts_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM user_accounts), 1), TRUE);
SELECT setval('role_definitions_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM role_definitions), 1), TRUE);
SELECT setval('reference_data_entries_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM reference_data_entries), 1), TRUE);
