package com.kyronic.riskengine.dashboard.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DashboardDtos {

    private DashboardDtos() {
    }

    public record DashboardSummaryResponse(
            Instant generatedAt,
            DashboardUserContext user,
            List<DashboardMetricCard> headlineMetrics,
            DashboardPortfolio portfolio,
            DashboardWorkflow workflow,
            DashboardNotifications notifications,
            DashboardActivity activity,
            List<RoleAnalyticsSection> roleAnalytics,
            List<DashboardInsight> insights
    ) {
    }

    public record DashboardUserContext(
            Long userId,
            String username,
            Set<String> roles,
            Set<Long> departmentIds,
            Set<Long> branchIds
    ) {
    }

    public record DashboardMetricCard(
            String key,
            String label,
            long value,
            String unit,
            String trend,
            String severity
    ) {
    }

    public record DashboardPortfolio(
            long riskRecords,
            long selfAssessments,
            long kriRecords,
            long oltsIncidents,
            long processFlows,
            long overdueReviews,
            Map<String, Long> residualRiskDistribution,
            Map<String, Long> kriThresholdDistribution,
            Map<String, Long> incidentAuthorizationDistribution,
            Map<String, Long> processFlowWorkflowDistribution
    ) {
    }

    public record DashboardWorkflow(
            long pendingApprovals,
            long returnedForCorrection,
            long rejectedItems,
            long approvedLast30Days,
            long submittedLast30Days,
            long averagePendingAgeDays,
            Map<String, Long> queueByModule,
            List<DashboardItemAge> agingBuckets
    ) {
    }

    public record DashboardItemAge(
            String bucket,
            long count
    ) {
    }

    public record DashboardNotifications(
            long total,
            long unread,
            long active,
            long expired,
            Map<String, Long> byPriority,
            Map<String, Long> bySourceService
    ) {
    }

    public record DashboardActivity(
            long auditEventsLast24Hours,
            long auditEventsLast7Days,
            long failedEventsLast7Days,
            long loginSuccessesLast30Days,
            Map<String, Long> serviceActivity,
            Map<String, Long> actionOutcomes,
            List<DashboardActorActivity> topActors
    ) {
    }

    public record DashboardActorActivity(
            String username,
            long events,
            long failures
    ) {
    }

    public record RoleAnalyticsSection(
            String roleCode,
            String roleName,
            String focus,
            List<DashboardMetricCard> metrics,
            Map<String, Long> breakdowns
    ) {
    }

    public record DashboardInsight(
            String level,
            String title,
            String description
    ) {
    }

    public record DashboardRoleCatalogResponse(
            List<RoleCatalogEntry> roles
    ) {
    }

    public record RoleCatalogEntry(
            String roleCode,
            String roleName,
            String audience,
            String purpose,
            List<String> primaryKpis,
            List<String> recommendedWidgets
    ) {
    }
}
