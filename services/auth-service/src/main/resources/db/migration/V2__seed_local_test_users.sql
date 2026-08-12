INSERT INTO user_accounts (id, username, password_hash, active, locked)
VALUES
    (1001, 'risk.inputter', '{noop}ChangeMe123!', TRUE, FALSE),
    (1002, 'dept.head', '{noop}ChangeMe123!', TRUE, FALSE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_name)
VALUES
    (1001, 'INPUTTER'),
    (1002, 'DEPARTMENT_HEAD'),
    (1002, 'AUTHORIZER')
ON CONFLICT DO NOTHING;

INSERT INTO user_permissions (user_id, permission_name)
VALUES
    (1001, 'OLTS_CREATE'),
    (1001, 'OLTS_READ'),
    (1001, 'OLTS_UPDATE'),
    (1001, 'OLTS_SUBMIT'),
    (1002, 'OLTS_AUTHORIZE'),
    (1002, 'OLTS_READ')
ON CONFLICT DO NOTHING;

SELECT setval('user_accounts_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM user_accounts), 1), TRUE);
