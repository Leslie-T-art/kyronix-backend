CREATE TABLE IF NOT EXISTS platform_audit_trail (
    id UUID PRIMARY KEY,
    service_name VARCHAR(120) NOT NULL,
    category VARCHAR(120) NOT NULL,
    action VARCHAR(500) NOT NULL,
    http_method VARCHAR(16) NOT NULL,
    request_path VARCHAR(500) NOT NULL,
    query_string VARCHAR(1000),
    status_code INTEGER NOT NULL,
    outcome VARCHAR(16) NOT NULL,
    username VARCHAR(150),
    user_id VARCHAR(120),
    source_ip VARCHAR(120),
    user_agent VARCHAR(1000),
    correlation_id VARCHAR(120) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_platform_audit_trail_occurred_at
    ON platform_audit_trail (occurred_at DESC);

CREATE INDEX IF NOT EXISTS idx_platform_audit_trail_service_name
    ON platform_audit_trail (service_name);
