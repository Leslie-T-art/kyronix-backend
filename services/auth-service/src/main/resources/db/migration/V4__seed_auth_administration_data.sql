UPDATE user_accounts
SET full_name = 'Risk Inputter',
    department_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    branch_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    deleted = FALSE
WHERE id = '11111111-1111-1111-1111-111111111111';

UPDATE user_accounts
SET full_name = 'Department Head',
    department_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    branch_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    deleted = FALSE
WHERE id = '22222222-2222-2222-2222-222222222222';

INSERT INTO user_accounts (id, username, full_name, password_hash, active, locked, department_id, branch_id, deleted)
VALUES ('33333333-3333-3333-3333-333333333333', 'system.admin', 'System Administrator', '{noop}ChangeMe123!', TRUE, FALSE, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', FALSE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_name)
VALUES ('33333333-3333-3333-3333-333333333333', 'SYSTEM_ADMIN')
ON CONFLICT DO NOTHING;

INSERT INTO user_permissions (user_id, permission_name)
VALUES
    ('33333333-3333-3333-3333-333333333333', 'ADMIN_USERS'),
    ('33333333-3333-3333-3333-333333333333', 'ADMIN_REFERENCE_DATA')
ON CONFLICT DO NOTHING;

INSERT INTO role_definitions (id, code, name, description, active)
VALUES
    ('44444444-4444-4444-4444-444444444444', 'INPUTTER', 'Inputter', 'Can create and submit maker workflow records', TRUE),
    ('55555555-5555-5555-5555-555555555555', 'DEPARTMENT_HEAD', 'Department Head', 'Can authorize department records', TRUE),
    ('66666666-6666-6666-6666-666666666666', 'SYSTEM_ADMIN', 'System Administrator', 'Can administer users and configuration', TRUE)
ON CONFLICT (code) DO NOTHING;

INSERT INTO reference_data_entries (id, type, code, name, active)
VALUES
    ('77777777-7777-7777-7777-777777777771', 'DEPARTMENT', 'OPS', 'Operations', TRUE),
    ('77777777-7777-7777-7777-777777777772', 'BRANCH', 'HQ', 'Head Office', TRUE),
    ('77777777-7777-7777-7777-777777777773', 'LOSS_CATEGORY', 'INTERNAL_FRAUD', 'Internal Fraud', TRUE),
    ('77777777-7777-7777-7777-777777777774', 'EVENT_TYPE', 'OPERATIONAL_LOSS', 'Operational Loss', TRUE)
ON CONFLICT (type, code) DO NOTHING;
