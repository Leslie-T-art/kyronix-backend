package com.kyronic.riskengine.dashboard.application.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DashboardAnalyticsRepository {

    private final NamedParameterJdbcTemplate platformJdbcTemplate;
    private final NamedParameterJdbcTemplate authJdbcTemplate;

    public DashboardAnalyticsRepository(@Qualifier("platformJdbcTemplate") NamedParameterJdbcTemplate platformJdbcTemplate,
                                        @Qualifier("authJdbcTemplate") NamedParameterJdbcTemplate authJdbcTemplate) {
        this.platformJdbcTemplate = platformJdbcTemplate;
        this.authJdbcTemplate = authJdbcTemplate;
    }

    public long riskRecordCount(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*)
                from risk_register_service.risk_records
                where :orgWide = true or created_by = :username or updated_by = :username
                """, scope.commonParams());
    }

    public long overdueRiskReviewCount(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*)
                from risk_register_service.risk_records
                where next_review_date < current_date
                  and (:orgWide = true or created_by = :username or updated_by = :username)
                """, scope.commonParams());
    }

    public Map<String, Long> residualRiskDistribution(Scope scope) {
        return aggregate(platformJdbcTemplate, """
                select residual_rating as label, count(*) as total
                from risk_register_service.risk_records
                where :orgWide = true or created_by = :username or updated_by = :username
                group by residual_rating
                order by total desc, residual_rating asc
                """, scope.commonParams());
    }

    public long selfAssessmentCount(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*)
                from self_assessment_service.self_assessments
                where :orgWide = true or department_id in (:departmentIds) or created_by = :username
                """, scope.withDepartments());
    }

    public long selfAssessmentActionRequiredCount(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*)
                from self_assessment_service.self_assessments
                where action_required = true
                  and (:orgWide = true or department_id in (:departmentIds) or created_by = :username)
                """, scope.withDepartments());
    }

    public long overdueSelfAssessmentReviewCount(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*)
                from self_assessment_service.self_assessments
                where next_review_date is not null
                  and next_review_date < current_date
                  and (:orgWide = true or department_id in (:departmentIds) or created_by = :username)
                """, scope.withDepartments());
    }

    public long kriCount(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*)
                from kri_service.kri_records
                where :orgWide = true or created_by = :username or updated_by = :username or owner = :username
                """, scope.commonParams());
    }

    public long overdueKriReviewCount(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*)
                from kri_service.kri_records
                where next_review_date < current_date
                  and (:orgWide = true or created_by = :username or updated_by = :username or owner = :username)
                """, scope.commonParams());
    }

    public Map<String, Long> kriThresholdDistribution(Scope scope) {
        return aggregate(platformJdbcTemplate, """
                select case
                         when direction = 'HIGHER_IS_BETTER' and current_value >= green_upper_bound then 'GREEN'
                         when direction = 'HIGHER_IS_BETTER' and current_value >= amber_threshold then 'AMBER'
                         when direction = 'HIGHER_IS_BETTER' then 'RED'
                         when direction = 'LOWER_IS_BETTER' and current_value <= green_upper_bound then 'GREEN'
                         when direction = 'LOWER_IS_BETTER' and current_value <= amber_threshold then 'AMBER'
                         else 'RED'
                       end as label,
                       count(*) as total
                from kri_service.kri_records
                where :orgWide = true or created_by = :username or updated_by = :username or owner = :username
                group by label
                order by total desc, label asc
                """, scope.commonParams());
    }

    public long oltsIncidentCount(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*)
                from olts_incidents
                where :orgWide = true
                   or department_id::text in (:departmentIdTexts)
                   or created_by_username = :username
                   or reported_by = :username
                """, scope.withDepartmentTexts());
    }

    public Map<String, Long> incidentAuthorizationDistribution(Scope scope) {
        return aggregate(platformJdbcTemplate, """
                select authorization_status as label, count(*) as total
                from olts_incidents
                where :orgWide = true
                   or department_id::text in (:departmentIdTexts)
                   or created_by_username = :username
                   or reported_by = :username
                group by authorization_status
                order by total desc, authorization_status asc
                """, scope.withDepartmentTexts());
    }

    public long processFlowCount(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*)
                from process_flows_service.process_flows
                where :orgWide = true
                   or department_id in (:departmentIds)
                   or inputter_username = :username
                   or coalesce(authorizer_username, '') = :username
                """, scope.withDepartments());
    }

    public Map<String, Long> processFlowWorkflowDistribution(Scope scope) {
        return aggregate(platformJdbcTemplate, """
                select workflow_status as label, count(*) as total
                from process_flows_service.process_flows
                where :orgWide = true
                   or department_id in (:departmentIds)
                   or inputter_username = :username
                   or coalesce(authorizer_username, '') = :username
                group by workflow_status
                order by total desc, workflow_status asc
                """, scope.withDepartments());
    }

    public long notificationCount(Long userId) {
        return count(platformJdbcTemplate, """
                select count(*)
                from notifications_service.notifications
                where recipient_user_id = :userId
                """, new MapSqlParameterSource("userId", userId));
    }

    public long unreadNotificationCount(Long userId) {
        return count(platformJdbcTemplate, """
                select count(*)
                from notifications_service.notifications
                where recipient_user_id = :userId
                  and read_state = 'UNREAD'
                """, new MapSqlParameterSource("userId", userId));
    }

    public long activeNotificationCount(Long userId) {
        return count(platformJdbcTemplate, """
                select count(*)
                from notifications_service.notifications
                where recipient_user_id = :userId
                  and state = 'ACTIVE'
                """, new MapSqlParameterSource("userId", userId));
    }

    public long expiredNotificationCount(Long userId) {
        return count(platformJdbcTemplate, """
                select count(*)
                from notifications_service.notifications
                where recipient_user_id = :userId
                  and state = 'EXPIRED'
                """, new MapSqlParameterSource("userId", userId));
    }

    public Map<String, Long> notificationPriorityDistribution(Long userId) {
        return aggregate(platformJdbcTemplate, """
                select priority as label, count(*) as total
                from notifications_service.notifications
                where recipient_user_id = :userId
                group by priority
                order by total desc, priority asc
                """, new MapSqlParameterSource("userId", userId));
    }

    public Map<String, Long> notificationSourceDistribution(Long userId) {
        return aggregate(platformJdbcTemplate, """
                select source_service as label, count(*) as total
                from notifications_service.notifications
                where recipient_user_id = :userId
                group by source_service
                order by total desc, source_service asc
                """, new MapSqlParameterSource("userId", userId));
    }

    public long auditEventsLast24Hours() {
        return count(platformJdbcTemplate, """
                select count(*)
                from audit_service.platform_audit_trail
                where occurred_at >= now() - interval '24 hours'
                """, new MapSqlParameterSource());
    }

    public long auditEventsLast7Days() {
        return count(platformJdbcTemplate, """
                select count(*)
                from audit_service.platform_audit_trail
                where occurred_at >= now() - interval '7 days'
                """, new MapSqlParameterSource());
    }

    public long failedAuditEventsLast7Days() {
        return count(platformJdbcTemplate, """
                select count(*)
                from audit_service.platform_audit_trail
                where occurred_at >= now() - interval '7 days'
                  and outcome <> 'SUCCESS'
                """, new MapSqlParameterSource());
    }

    public Map<String, Long> serviceActivityLast7Days() {
        return aggregate(platformJdbcTemplate, """
                select service_name as label, count(*) as total
                from audit_service.platform_audit_trail
                where occurred_at >= now() - interval '7 days'
                group by service_name
                order by total desc, service_name asc
                """, new MapSqlParameterSource());
    }

    public Map<String, Long> auditOutcomesLast7Days() {
        return aggregate(platformJdbcTemplate, """
                select outcome as label, count(*) as total
                from audit_service.platform_audit_trail
                where occurred_at >= now() - interval '7 days'
                group by outcome
                order by total desc, outcome asc
                """, new MapSqlParameterSource());
    }

    public List<ActorRow> topActorsLast7Days() {
        return platformJdbcTemplate.query("""
                select coalesce(username, 'anonymous') as username,
                       count(*) as events,
                       sum(case when outcome <> 'SUCCESS' then 1 else 0 end) as failures
                from audit_service.platform_audit_trail
                where occurred_at >= now() - interval '7 days'
                group by coalesce(username, 'anonymous')
                order by events desc, username asc
                limit 5
                """, (rs, rowNum) -> new ActorRow(
                rs.getString("username"),
                rs.getLong("events"),
                rs.getLong("failures")
        ));
    }

    public long loginSuccessesLast30Days() {
        return count(authJdbcTemplate, """
                select count(*)
                from audit_events
                where event_type = 'AUTH_LOGIN_SUCCESS'
                  and occurred_at >= now() - interval '30 days'
                """, new MapSqlParameterSource());
    }

    public long totalUsers() {
        return count(authJdbcTemplate, "select count(*) from user_accounts", new MapSqlParameterSource());
    }

    public long activeUsers() {
        return count(authJdbcTemplate, "select count(*) from user_accounts where active = true and deleted = false", new MapSqlParameterSource());
    }

    public long lockedUsers() {
        return count(authJdbcTemplate, "select count(*) from user_accounts where locked = true and deleted = false", new MapSqlParameterSource());
    }

    public long deletedUsers() {
        return count(authJdbcTemplate, "select count(*) from user_accounts where deleted = true", new MapSqlParameterSource());
    }

    public Map<String, Long> roleAssignments() {
        return aggregate(authJdbcTemplate, """
                select role_name as label, count(*) as total
                from user_roles
                group by role_name
                order by total desc, role_name asc
                """, new MapSqlParameterSource());
    }

    public long pendingWorkflowItems(Scope scope) {
        long olts = count(platformJdbcTemplate, """
                select count(*)
                from olts_incidents
                where authorization_status in ('PENDING_AUTHORIZATION', 'UNDER_AUTHORIZATION_REVIEW')
                  and (:orgWide = true
                    or department_id::text in (:departmentIdTexts)
                    or created_by_username = :username
                    or reported_by = :username)
                """, scope.withDepartmentTexts());
        long processFlows = count(platformJdbcTemplate, """
                select count(*)
                from process_flows_service.process_flows
                where workflow_status = 'PENDING_APPROVAL'
                  and (:orgWide = true
                    or department_id in (:departmentIds)
                    or inputter_username = :username
                    or coalesce(authorizer_username, '') = :username)
                """, scope.withDepartments());
        return olts + processFlows;
    }

    public long returnedWorkflowItems(Scope scope) {
        long olts = count(platformJdbcTemplate, """
                select count(*)
                from olts_incidents
                where authorization_status = 'RETURNED_FOR_CORRECTION'
                  and (:orgWide = true
                    or department_id::text in (:departmentIdTexts)
                    or created_by_username = :username
                    or reported_by = :username)
                """, scope.withDepartmentTexts());
        long processFlows = count(platformJdbcTemplate, """
                select count(*)
                from process_flows_service.process_flows
                where workflow_status = 'RETURNED'
                  and (:orgWide = true
                    or department_id in (:departmentIds)
                    or inputter_username = :username
                    or coalesce(authorizer_username, '') = :username)
                """, scope.withDepartments());
        return olts + processFlows;
    }

    public long rejectedWorkflowItems(Scope scope) {
        long olts = count(platformJdbcTemplate, """
                select count(*)
                from olts_incidents
                where authorization_status = 'REJECTED'
                  and (:orgWide = true
                    or department_id::text in (:departmentIdTexts)
                    or created_by_username = :username
                    or reported_by = :username)
                """, scope.withDepartmentTexts());
        long processFlows = count(platformJdbcTemplate, """
                select count(*)
                from process_flows_service.process_flows
                where workflow_status = 'REJECTED'
                  and (:orgWide = true
                    or department_id in (:departmentIds)
                    or inputter_username = :username
                    or coalesce(authorizer_username, '') = :username)
                """, scope.withDepartments());
        return olts + processFlows;
    }

    public long approvedLast30Days(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*) from (
                    select authorized_at as completed_at
                    from olts_incidents
                    where authorized_at is not null
                      and (:orgWide = true
                        or department_id::text in (:departmentIdTexts)
                        or created_by_username = :username
                        or reported_by = :username)
                    union all
                    select updated_at as completed_at
                    from process_flows_service.process_flows
                    where workflow_status = 'APPROVED'
                      and (:orgWide = true
                        or department_id in (:departmentIds)
                        or inputter_username = :username
                        or coalesce(authorizer_username, '') = :username)
                ) approvals
                where completed_at >= now() - interval '30 days'
                """, scope.withDepartmentsAndTexts());
    }

    public long submittedLast30Days(Scope scope) {
        return count(platformJdbcTemplate, """
                select count(*) from (
                    select submitted_at as submitted_time
                    from olts_incidents
                    where submitted_at is not null
                      and (:orgWide = true
                        or department_id::text in (:departmentIdTexts)
                        or created_by_username = :username
                        or reported_by = :username)
                    union all
                    select updated_at as submitted_time
                    from process_flows_service.process_flows
                    where workflow_status = 'PENDING_APPROVAL'
                      and (:orgWide = true
                        or department_id in (:departmentIds)
                        or inputter_username = :username
                        or coalesce(authorizer_username, '') = :username)
                ) submissions
                where submitted_time >= now() - interval '30 days'
                """, scope.withDepartmentsAndTexts());
    }

    public long averagePendingAgeDays(Scope scope) {
        return Math.round(decimal(platformJdbcTemplate, """
                select coalesce(avg(age_days), 0)
                from (
                    select extract(day from now() - submitted_at) as age_days
                    from olts_incidents
                    where submitted_at is not null
                      and authorization_status in ('PENDING_AUTHORIZATION', 'UNDER_AUTHORIZATION_REVIEW')
                      and (:orgWide = true
                        or department_id::text in (:departmentIdTexts)
                        or created_by_username = :username
                        or reported_by = :username)
                    union all
                    select extract(day from now() - updated_at) as age_days
                    from process_flows_service.process_flows
                    where workflow_status = 'PENDING_APPROVAL'
                      and (:orgWide = true
                        or department_id in (:departmentIds)
                        or inputter_username = :username
                        or coalesce(authorizer_username, '') = :username)
                ) queue
                """, scope.withDepartmentsAndTexts()));
    }

    public Map<String, Long> queueByModule(Scope scope) {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("olts", count(platformJdbcTemplate, """
                select count(*)
                from olts_incidents
                where authorization_status in ('PENDING_AUTHORIZATION', 'UNDER_AUTHORIZATION_REVIEW')
                  and (:orgWide = true
                    or department_id::text in (:departmentIdTexts)
                    or created_by_username = :username
                    or reported_by = :username)
                """, scope.withDepartmentTexts()));
        result.put("processFlows", count(platformJdbcTemplate, """
                select count(*)
                from process_flows_service.process_flows
                where workflow_status = 'PENDING_APPROVAL'
                  and (:orgWide = true
                    or department_id in (:departmentIds)
                    or inputter_username = :username
                    or coalesce(authorizer_username, '') = :username)
                """, scope.withDepartments()));
        return result;
    }

    public Map<String, Long> agingBuckets(Scope scope) {
        return aggregate(platformJdbcTemplate, """
                select bucket as label, count(*) as total
                from (
                    select case
                             when age_days <= 2 then '0-2 days'
                             when age_days <= 7 then '3-7 days'
                             when age_days <= 14 then '8-14 days'
                             else '15+ days'
                           end as bucket
                    from (
                        select extract(day from now() - submitted_at) as age_days
                        from olts_incidents
                        where submitted_at is not null
                          and authorization_status in ('PENDING_AUTHORIZATION', 'UNDER_AUTHORIZATION_REVIEW')
                          and (:orgWide = true
                            or department_id::text in (:departmentIdTexts)
                            or created_by_username = :username
                            or reported_by = :username)
                        union all
                        select extract(day from now() - updated_at) as age_days
                        from process_flows_service.process_flows
                        where workflow_status = 'PENDING_APPROVAL'
                          and (:orgWide = true
                            or department_id in (:departmentIds)
                            or inputter_username = :username
                            or coalesce(authorizer_username, '') = :username)
                    ) pending_items
                ) buckets
                group by bucket
                order by bucket asc
                """, scope.withDepartmentsAndTexts());
    }

    private long count(NamedParameterJdbcTemplate jdbcTemplate, String sql, MapSqlParameterSource params) {
        Long value = jdbcTemplate.queryForObject(sql, params, Long.class);
        return value == null ? 0L : value;
    }

    private double decimal(NamedParameterJdbcTemplate jdbcTemplate, String sql, MapSqlParameterSource params) {
        Double value = jdbcTemplate.queryForObject(sql, params, Double.class);
        return value == null ? 0D : value;
    }

    private Map<String, Long> aggregate(NamedParameterJdbcTemplate jdbcTemplate, String sql, MapSqlParameterSource params) {
        List<Map.Entry<String, Long>> rows = jdbcTemplate.query(sql, params, (rs, rowNum) ->
                Map.entry(rs.getString("label"), rs.getLong("total")));
        Map<String, Long> result = new LinkedHashMap<>();
        rows.forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }

    public record ActorRow(String username, long events, long failures) {
    }

    public record Scope(boolean orgWide, String username, List<Long> departmentIds) {

        public MapSqlParameterSource commonParams() {
            return new MapSqlParameterSource()
                    .addValue("orgWide", orgWide)
                    .addValue("username", username);
        }

        public MapSqlParameterSource withDepartments() {
            return commonParams()
                    .addValue("departmentIds", departmentIds.isEmpty() ? List.of(-1L) : departmentIds);
        }

        public MapSqlParameterSource withDepartmentTexts() {
            return commonParams()
                    .addValue("departmentIdTexts", departmentIds.isEmpty()
                            ? List.of("-1")
                            : departmentIds.stream().map(String::valueOf).toList());
        }

        public MapSqlParameterSource withDepartmentsAndTexts() {
            return withDepartments()
                    .addValue("departmentIdTexts", departmentIds.isEmpty()
                            ? List.of("-1")
                            : departmentIds.stream().map(String::valueOf).toList());
        }
    }
}
