create schema if not exists risk_register_service;

create sequence if not exists risk_register_service.risk_reference_seq start with 1 increment by 1;

create table if not exists risk_register_service.risk_records (
    id uuid primary key,
    risk_id varchar(32) not null unique,
    risk_title varchar(180) not null,
    category varchar(120) not null,
    owner varchar(150) not null,
    business_unit varchar(150) not null,
    description varchar(4000) not null,
    likelihood integer not null,
    impact integer not null,
    inherent_rating varchar(40) not null,
    controls_mapped varchar(2000) not null,
    control_effectiveness varchar(80) not null,
    residual_rating varchar(40) not null,
    treatment_strategy varchar(80) not null,
    status varchar(80) not null,
    next_review_date date not null,
    linked_process varchar(180) not null,
    linked_kri varchar(120) not null,
    action_plan varchar(4000) not null,
    created_at timestamp with time zone not null,
    created_by varchar(120) not null,
    updated_at timestamp with time zone not null,
    updated_by varchar(120) not null,
    deleted boolean not null default false,
    version bigint
);

create index if not exists idx_risk_records_active
    on risk_register_service.risk_records (deleted, created_at desc);
