package com.kyronic.riskengine.dashboard.application.service;

import com.kyronic.riskengine.common.security.AuthenticatedUser;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardActivity;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardActorActivity;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardInsight;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardItemAge;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardMetricCard;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardNotifications;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardPortfolio;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardRoleCatalogResponse;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardSummaryResponse;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardUserContext;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardWorkflow;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.RoleAnalyticsSection;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.RoleCatalogEntry;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DashboardService {

    private static final Set<String> ORGANIZATION_WIDE_ROLES = Set.of("SYSTEM_ADMIN", "ENTERPRISE_ADMIN", "EXECUTIVE");

    private final DashboardAnalyticsRepository analyticsRepository;
    private final DashboardRoleCatalog roleCatalog;
    private final Clock clock;

    public DashboardService(DashboardAnalyticsRepository analyticsRepository,
                            DashboardRoleCatalog roleCatalog,
                            Clock clock) {
        this.analyticsRepository = analyticsRepository;
        this.roleCatalog = roleCatalog;
        this.clock = clock;
    }

    public DashboardRoleCatalogResponse roles() {
        return roleCatalog.listRoles();
    }

    public DashboardSummaryResponse summary(AuthenticatedUser user) {
        DashboardAnalyticsRepository.Scope scope = scopeFor(user);
        DashboardPortfolio portfolio = portfolio(scope);
        DashboardWorkflow workflow = workflow(scope);
        DashboardNotifications notifications = notifications(user.userId());
        DashboardActivity activity = activity();
        List<RoleAnalyticsSection> roleAnalytics = user.roles().stream()
                .map(role -> analyticsForRole(role, user))
                .toList();

        return new DashboardSummaryResponse(
                Instant.now(clock),
                new DashboardUserContext(user.userId(), user.username(), user.roles(), user.departmentIds(), user.branchIds()),
                headlineMetrics(user, portfolio, workflow, notifications, activity),
                portfolio,
                workflow,
                notifications,
                activity,
                roleAnalytics,
                insights(user, portfolio, workflow, notifications, activity)
        );
    }

    public RoleAnalyticsSection analyticsForRole(String roleCode, AuthenticatedUser user) {
        RoleCatalogEntry catalogEntry = roleCatalog.get(roleCode);
        DashboardAnalyticsRepository.Scope scope = scopeFor(user);
        DashboardPortfolio portfolio = portfolio(scope);
        DashboardWorkflow workflow = workflow(scope);
        DashboardNotifications notifications = notifications(user.userId());
        DashboardActivity activity = activity();

        List<DashboardMetricCard> metrics = switch (roleCode) {
            case "SYSTEM_ADMIN" -> List.of(
                    metric("users.active", "Active users", analyticsRepository.activeUsers(), "users", null, "info"),
                    metric("users.locked", "Locked users", analyticsRepository.lockedUsers(), "users", null, analyticsRepository.lockedUsers() > 0 ? "warning" : "positive"),
                    metric("audit.failures", "Audit failures (7d)", activity.failedEventsLast7Days(), "events", null, activity.failedEventsLast7Days() > 0 ? "warning" : "positive"),
                    metric("login.success", "Successful logins (30d)", activity.loginSuccessesLast30Days(), "events", null, "info")
            );
            case "ENTERPRISE_ADMIN" -> List.of(
                    metric("portfolio.total", "Total records", portfolio.riskRecords() + portfolio.selfAssessments() + portfolio.kriRecords() + portfolio.oltsIncidents() + portfolio.processFlows(), "records", null, "info"),
                    metric("workflow.pending", "Pending approvals", workflow.pendingApprovals(), "items", null, workflow.pendingApprovals() > 0 ? "warning" : "positive"),
                    metric("reviews.overdue", "Overdue reviews", portfolio.overdueReviews(), "items", null, portfolio.overdueReviews() > 0 ? "warning" : "positive"),
                    metric("kri.red", "KRI red alerts", portfolio.kriThresholdDistribution().getOrDefault("RED", 0L), "kri", null, portfolio.kriThresholdDistribution().getOrDefault("RED", 0L) > 0 ? "critical" : "positive")
            );
            case "INPUTTER" -> List.of(
                    metric("workflow.submitted", "Submitted (30d)", workflow.submittedLast30Days(), "items", null, "info"),
                    metric("workflow.returned", "Returned for correction", workflow.returnedForCorrection(), "items", null, workflow.returnedForCorrection() > 0 ? "warning" : "positive"),
                    metric("notifications.unread", "Unread notifications", notifications.unread(), "alerts", null, notifications.unread() > 0 ? "warning" : "positive"),
                    metric("workflow.rejected", "Rejected items", workflow.rejectedItems(), "items", null, workflow.rejectedItems() > 0 ? "warning" : "positive")
            );
            case "AUTHORIZER" -> List.of(
                    metric("workflow.pending", "Approval queue", workflow.pendingApprovals(), "items", null, workflow.pendingApprovals() > 0 ? "warning" : "positive"),
                    metric("workflow.age", "Average queue age", workflow.averagePendingAgeDays(), "days", null, workflow.averagePendingAgeDays() > 7 ? "warning" : "positive"),
                    metric("workflow.approved", "Approved (30d)", workflow.approvedLast30Days(), "items", null, "info"),
                    metric("workflow.rejected", "Rejected items", workflow.rejectedItems(), "items", null, workflow.rejectedItems() > 0 ? "warning" : "positive")
            );
            case "DEPARTMENT_HEAD" -> List.of(
                    metric("workflow.pending", "Department queue", workflow.pendingApprovals(), "items", null, workflow.pendingApprovals() > 0 ? "warning" : "positive"),
                    metric("rcsa.actions", "RCSA action required", analyticsRepository.selfAssessmentActionRequiredCount(scope), "assessments", null, "warning"),
                    metric("reviews.overdue", "Overdue reviews", portfolio.overdueReviews(), "items", null, portfolio.overdueReviews() > 0 ? "warning" : "positive"),
                    metric("incidents.pending", "Incident approvals", portfolio.incidentAuthorizationDistribution().getOrDefault("PENDING_AUTHORIZATION", 0L), "incidents", null, "info")
            );
            case "EXECUTIVE" -> List.of(
                    metric("residual.high", "High residual risk", sumMatching(portfolio.residualRiskDistribution(), "HIGH", "SEVERE", "CRITICAL"), "risks", null, "critical"),
                    metric("kri.red", "KRI red alerts", portfolio.kriThresholdDistribution().getOrDefault("RED", 0L), "kri", null, "critical"),
                    metric("olts.open", "Open incidents", portfolio.oltsIncidents(), "incidents", null, "info"),
                    metric("reviews.overdue", "Overdue reviews", portfolio.overdueReviews(), "items", null, portfolio.overdueReviews() > 0 ? "warning" : "positive")
            );
            default -> List.of();
        };

        Map<String, Long> breakdowns = switch (roleCode) {
            case "SYSTEM_ADMIN" -> analyticsRepository.roleAssignments();
            case "ENTERPRISE_ADMIN", "EXECUTIVE" -> portfolio.kriThresholdDistribution();
            case "INPUTTER" -> notifications.bySourceService();
            case "AUTHORIZER", "DEPARTMENT_HEAD" -> workflow.queueByModule();
            default -> Map.of();
        };

        return new RoleAnalyticsSection(
                catalogEntry.roleCode(),
                catalogEntry.roleName(),
                catalogEntry.purpose(),
                metrics,
                breakdowns
        );
    }

    private DashboardPortfolio portfolio(DashboardAnalyticsRepository.Scope scope) {
        long overdueReviews = analyticsRepository.overdueRiskReviewCount(scope)
                + analyticsRepository.overdueSelfAssessmentReviewCount(scope)
                + analyticsRepository.overdueKriReviewCount(scope);
        return new DashboardPortfolio(
                analyticsRepository.riskRecordCount(scope),
                analyticsRepository.selfAssessmentCount(scope),
                analyticsRepository.kriCount(scope),
                analyticsRepository.oltsIncidentCount(scope),
                analyticsRepository.processFlowCount(scope),
                overdueReviews,
                analyticsRepository.residualRiskDistribution(scope),
                analyticsRepository.kriThresholdDistribution(scope),
                analyticsRepository.incidentAuthorizationDistribution(scope),
                analyticsRepository.processFlowWorkflowDistribution(scope)
        );
    }

    private DashboardWorkflow workflow(DashboardAnalyticsRepository.Scope scope) {
        Map<String, Long> aging = analyticsRepository.agingBuckets(scope);
        List<DashboardItemAge> agingBuckets = aging.entrySet().stream()
                .map(entry -> new DashboardItemAge(entry.getKey(), entry.getValue()))
                .toList();
        return new DashboardWorkflow(
                analyticsRepository.pendingWorkflowItems(scope),
                analyticsRepository.returnedWorkflowItems(scope),
                analyticsRepository.rejectedWorkflowItems(scope),
                analyticsRepository.approvedLast30Days(scope),
                analyticsRepository.submittedLast30Days(scope),
                analyticsRepository.averagePendingAgeDays(scope),
                analyticsRepository.queueByModule(scope),
                agingBuckets
        );
    }

    private DashboardNotifications notifications(Long userId) {
        return new DashboardNotifications(
                analyticsRepository.notificationCount(userId),
                analyticsRepository.unreadNotificationCount(userId),
                analyticsRepository.activeNotificationCount(userId),
                analyticsRepository.expiredNotificationCount(userId),
                analyticsRepository.notificationPriorityDistribution(userId),
                analyticsRepository.notificationSourceDistribution(userId)
        );
    }

    private DashboardActivity activity() {
        List<DashboardActorActivity> actors = analyticsRepository.topActorsLast7Days().stream()
                .map(actor -> new DashboardActorActivity(actor.username(), actor.events(), actor.failures()))
                .toList();
        return new DashboardActivity(
                analyticsRepository.auditEventsLast24Hours(),
                analyticsRepository.auditEventsLast7Days(),
                analyticsRepository.failedAuditEventsLast7Days(),
                analyticsRepository.loginSuccessesLast30Days(),
                analyticsRepository.serviceActivityLast7Days(),
                analyticsRepository.auditOutcomesLast7Days(),
                actors
        );
    }

    private List<DashboardMetricCard> headlineMetrics(AuthenticatedUser user,
                                                      DashboardPortfolio portfolio,
                                                      DashboardWorkflow workflow,
                                                      DashboardNotifications notifications,
                                                      DashboardActivity activity) {
        List<DashboardMetricCard> cards = new ArrayList<>();
        cards.add(metric("portfolio.records", "Tracked records", portfolio.riskRecords() + portfolio.selfAssessments() + portfolio.kriRecords() + portfolio.oltsIncidents() + portfolio.processFlows(), "records", null, "info"));
        cards.add(metric("workflow.pending", "Pending approvals", workflow.pendingApprovals(), "items", null, workflow.pendingApprovals() > 0 ? "warning" : "positive"));
        cards.add(metric("reviews.overdue", "Overdue reviews", portfolio.overdueReviews(), "items", null, portfolio.overdueReviews() > 0 ? "warning" : "positive"));
        cards.add(metric("notifications.unread", "Unread notifications", notifications.unread(), "alerts", null, notifications.unread() > 0 ? "warning" : "positive"));
        if (user.roles().contains("SYSTEM_ADMIN")) {
            cards.add(metric("users.total", "Total users", analyticsRepository.totalUsers(), "users", null, "info"));
            cards.add(metric("audit.failures", "Audit failures (7d)", activity.failedEventsLast7Days(), "events", null, activity.failedEventsLast7Days() > 0 ? "warning" : "positive"));
        }
        return cards;
    }

    private List<DashboardInsight> insights(AuthenticatedUser user,
                                            DashboardPortfolio portfolio,
                                            DashboardWorkflow workflow,
                                            DashboardNotifications notifications,
                                            DashboardActivity activity) {
        List<DashboardInsight> insights = new ArrayList<>();
        if (workflow.pendingApprovals() > 0) {
            insights.add(new DashboardInsight("warning", "Approval backlog", workflow.pendingApprovals() + " items are waiting for authorization across maker-checker workflows."));
        }
        if (portfolio.overdueReviews() > 0) {
            insights.add(new DashboardInsight("warning", "Review attention needed", portfolio.overdueReviews() + " records are past their review date across risk, RCSA, and KRI modules."));
        }
        if (notifications.unread() > 0) {
            insights.add(new DashboardInsight("info", "Unread notifications", "You still have " + notifications.unread() + " unread notifications requiring attention."));
        }
        if (activity.failedEventsLast7Days() > 0 && user.roles().contains("SYSTEM_ADMIN")) {
            insights.add(new DashboardInsight("critical", "Failed platform activity", activity.failedEventsLast7Days() + " platform audit events ended unsuccessfully in the last 7 days."));
        }
        return insights;
    }

    private DashboardAnalyticsRepository.Scope scopeFor(AuthenticatedUser user) {
        boolean orgWide = user.roles().stream().anyMatch(ORGANIZATION_WIDE_ROLES::contains);
        return new DashboardAnalyticsRepository.Scope(orgWide, user.username(), user.departmentIds().stream().sorted().toList());
    }

    private DashboardMetricCard metric(String key, String label, long value, String unit, String trend, String severity) {
        return new DashboardMetricCard(key, label, value, unit, trend, severity);
    }

    private long sumMatching(Map<String, Long> values, String... keys) {
        long total = 0L;
        for (String key : keys) {
            total += values.getOrDefault(key, 0L);
        }
        return total;
    }
}
