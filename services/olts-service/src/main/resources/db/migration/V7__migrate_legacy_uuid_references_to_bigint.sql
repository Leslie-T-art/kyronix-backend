create sequence if not exists olts_legacy_reference_seq start with 1 increment by 1;

create table if not exists olts_legacy_reference_mapping (
    legacy_uuid uuid primary key,
    generated_id bigint not null unique
);

insert into olts_legacy_reference_mapping (legacy_uuid, generated_id)
select legacy_uuid, nextval('olts_legacy_reference_seq')
from (
    select distinct legacy_uuid
    from (
        select department_id as legacy_uuid from olts_incidents where department_id is not null
        union
        select branch_id as legacy_uuid from olts_incidents where branch_id is not null
        union
        select inputter_user_id as legacy_uuid from olts_incidents where inputter_user_id is not null
        union
        select responsible_person_id as legacy_uuid from olts_incidents where responsible_person_id is not null
        union
        select created_by as legacy_uuid from olts_incidents where created_by is not null
        union
        select submitted_by as legacy_uuid from olts_incidents where submitted_by is not null
        union
        select authorized_by as legacy_uuid from olts_incidents where authorized_by is not null
        union
        select last_modified_by as legacy_uuid from olts_incidents where last_modified_by is not null
        union
        select deleted_by as legacy_uuid from olts_incidents where deleted_by is not null
    ) source_values
) distinct_values
where legacy_uuid not in (select legacy_uuid from olts_legacy_reference_mapping);

alter table olts_incidents
    add column if not exists department_id_bigint bigint,
    add column if not exists branch_id_bigint bigint,
    add column if not exists inputter_user_id_bigint bigint,
    add column if not exists responsible_person_id_bigint bigint,
    add column if not exists created_by_bigint bigint,
    add column if not exists submitted_by_bigint bigint,
    add column if not exists authorized_by_bigint bigint,
    add column if not exists last_modified_by_bigint bigint,
    add column if not exists deleted_by_bigint bigint;

update olts_incidents incident
set department_id_bigint = mapping.generated_id
from olts_legacy_reference_mapping mapping
where incident.department_id = mapping.legacy_uuid;

update olts_incidents incident
set branch_id_bigint = mapping.generated_id
from olts_legacy_reference_mapping mapping
where incident.branch_id = mapping.legacy_uuid;

update olts_incidents incident
set inputter_user_id_bigint = mapping.generated_id
from olts_legacy_reference_mapping mapping
where incident.inputter_user_id = mapping.legacy_uuid;

update olts_incidents incident
set responsible_person_id_bigint = mapping.generated_id
from olts_legacy_reference_mapping mapping
where incident.responsible_person_id = mapping.legacy_uuid;

update olts_incidents incident
set created_by_bigint = mapping.generated_id
from olts_legacy_reference_mapping mapping
where incident.created_by = mapping.legacy_uuid;

update olts_incidents incident
set submitted_by_bigint = mapping.generated_id
from olts_legacy_reference_mapping mapping
where incident.submitted_by = mapping.legacy_uuid;

update olts_incidents incident
set authorized_by_bigint = mapping.generated_id
from olts_legacy_reference_mapping mapping
where incident.authorized_by = mapping.legacy_uuid;

update olts_incidents incident
set last_modified_by_bigint = mapping.generated_id
from olts_legacy_reference_mapping mapping
where incident.last_modified_by = mapping.legacy_uuid;

update olts_incidents incident
set deleted_by_bigint = mapping.generated_id
from olts_legacy_reference_mapping mapping
where incident.deleted_by = mapping.legacy_uuid;

alter table olts_incidents
    drop column department_id,
    drop column branch_id,
    drop column inputter_user_id,
    drop column responsible_person_id,
    drop column created_by,
    drop column submitted_by,
    drop column authorized_by,
    drop column last_modified_by,
    drop column deleted_by;

alter table olts_incidents
    rename column department_id_bigint to department_id;

alter table olts_incidents
    rename column branch_id_bigint to branch_id;

alter table olts_incidents
    rename column inputter_user_id_bigint to inputter_user_id;

alter table olts_incidents
    rename column responsible_person_id_bigint to responsible_person_id;

alter table olts_incidents
    rename column created_by_bigint to created_by;

alter table olts_incidents
    rename column submitted_by_bigint to submitted_by;

alter table olts_incidents
    rename column authorized_by_bigint to authorized_by;

alter table olts_incidents
    rename column last_modified_by_bigint to last_modified_by;

alter table olts_incidents
    rename column deleted_by_bigint to deleted_by;
