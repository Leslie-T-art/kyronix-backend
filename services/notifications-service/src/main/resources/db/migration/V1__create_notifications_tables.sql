create schema if not exists notifications_service;

create sequence if not exists notifications_service.notification_reference_seq start with 1 increment by 1;

create table if not exists notifications_service.notifications (
    id uuid primary key,
    notification_reference varchar(32) not null unique,
    recipient_user_id uuid not null,
    type varchar(50) not null,
    priority varchar(20) not null,
    title varchar(255) not null,
    message varchar(2000) not null,
    source_service varchar(120) not null,
    entity_type varchar(120) not null,
    entity_id uuid not null,
    business_reference varchar(120) not null,
    department_id uuid,
    action_url varchar(500) not null,
    state varchar(20) not null,
    read_state varchar(20) not null,
    created_at timestamp with time zone not null,
    read_at timestamp with time zone,
    archived_at timestamp with time zone,
    dismissed_at timestamp with time zone,
    expires_at timestamp with time zone,
    event_id uuid not null,
    correlation_id varchar(120) not null,
    version bigint,
    constraint uq_notifications_dedup unique (event_id, recipient_user_id, type)
);

create table if not exists notifications_service.notification_audit_history (
    id uuid primary key,
    notification_id uuid not null,
    action varchar(50) not null,
    actor_user_id uuid,
    actor_username varchar(120) not null,
    occurred_at timestamp with time zone not null,
    correlation_id varchar(120) not null
);

create index if not exists idx_notifications_recipient_created_at
    on notifications_service.notifications (recipient_user_id, created_at desc);

create index if not exists idx_notifications_recipient_state
    on notifications_service.notifications (recipient_user_id, state, read_state);
