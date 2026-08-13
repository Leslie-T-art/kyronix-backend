alter table process_flows_service.process_flows
    rename column name to process_flow_name;

alter table process_flows_service.process_flows
    rename column status to workflow_status;

alter table process_flows_service.process_flows
    alter column workflow_status type varchar(40);

alter table process_flows_service.process_flows
    add column if not exists valid_from_date date,
    add column if not exists valid_to_date date,
    add column if not exists original_file_name varchar(255),
    add column if not exists content_type varchar(255),
    add column if not exists file_size bigint,
    add column if not exists bucket_name varchar(120),
    add column if not exists object_key varchar(500),
    add column if not exists inputter_user_id bigint,
    add column if not exists inputter_username varchar(120),
    add column if not exists authorizer_user_id bigint,
    add column if not exists authorizer_username varchar(120),
    add column if not exists authorizer_comment varchar(1000);

update process_flows_service.process_flows
set valid_from_date = coalesce(valid_from_date, current_date),
    valid_to_date = coalesce(valid_to_date, current_date),
    original_file_name = coalesce(original_file_name, 'legacy-process-flow.pdf'),
    content_type = coalesce(content_type, 'application/pdf'),
    file_size = coalesce(file_size, 0),
    bucket_name = coalesce(bucket_name, 'process-flows-dept-' || department_id),
    object_key = coalesce(object_key, flow_reference || '/legacy-process-flow.pdf'),
    inputter_user_id = coalesce(inputter_user_id, 0),
    inputter_username = coalesce(inputter_username, created_by),
    workflow_status = case
        when workflow_status in ('ACTIVE', 'APPROVED') then 'APPROVED'
        when workflow_status in ('REJECTED', 'RETURNED', 'PENDING_APPROVAL', 'DRAFT') then workflow_status
        else 'DRAFT'
    end;

alter table process_flows_service.process_flows
    alter column valid_from_date set not null,
    alter column valid_to_date set not null,
    alter column original_file_name set not null,
    alter column content_type set not null,
    alter column file_size set not null,
    alter column bucket_name set not null,
    alter column object_key set not null,
    alter column inputter_user_id set not null,
    alter column inputter_username set not null;

drop index if exists process_flows_service.idx_process_flows_status;
create index if not exists idx_process_flows_workflow_status on process_flows_service.process_flows (workflow_status);
