INSERT INTO olts_loss_categories (id, code, name, description)
VALUES
    ('88888888-8888-8888-8888-888888888881', 'INTERNAL_FRAUD', 'Internal Fraud', 'Losses arising from internal fraud events.'),
    ('88888888-8888-8888-8888-888888888882', 'EXTERNAL_FRAUD', 'External Fraud', 'Losses arising from external fraud events.'),
    ('88888888-8888-8888-8888-888888888883', 'PROCESS_FAILURE', 'Process Failure', 'Losses caused by process breakdowns or control failures.'),
    ('88888888-8888-8888-8888-888888888884', 'SYSTEM_FAILURE', 'System Failure', 'Losses caused by technology or system outages.')
ON CONFLICT (code) DO NOTHING;
