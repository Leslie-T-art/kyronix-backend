update olts_incidents
set active_version = true
where active_version is null;

alter table olts_incidents
    alter column active_version set default true;
