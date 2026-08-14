package com.kyronic.riskengine.dashboard.application.service;

import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardRoleCatalogResponse;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.RoleCatalogEntry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DashboardRoleCatalog {

    private final Map<String, RoleCatalogEntry> roles = Map.of(
            "SYSTEM_ADMIN", new RoleCatalogEntry(
                    "SYSTEM_ADMIN",
                    "System Administrator",
                    "Platform operations and administration",
                    "Monitors tenant-wide usage, identity health, audit failures, and service activity.",
                    List.of("Active users", "Locked users", "Audit failures", "Login volume", "Role assignments"),
                    List.of("User lifecycle", "Service activity heatmap", "Audit outcome split", "Top actors")
            ),
            "ENTERPRISE_ADMIN", new RoleCatalogEntry(
                    "ENTERPRISE_ADMIN",
                    "Enterprise Administrator",
                    "Enterprise-wide risk and controls oversight",
                    "Tracks cross-module inventory, workflow load, overdue reviews, and portfolio concentrations.",
                    List.of("Portfolio totals", "Pending approvals", "Overdue reviews", "Residual risk mix"),
                    List.of("Cross-module backlog", "Residual risk distribution", "KRI thresholds", "Incident authorization mix")
            ),
            "INPUTTER", new RoleCatalogEntry(
                    "INPUTTER",
                    "Inputter",
                    "Makers and record originators",
                    "Focuses on owned records, drafts, submissions, returns for correction, and unread notifications.",
                    List.of("My submissions", "Returned items", "Unread notifications", "Open actions"),
                    List.of("Submission throughput", "My queue by module", "Notification sources", "Returned item aging")
            ),
            "AUTHORIZER", new RoleCatalogEntry(
                    "AUTHORIZER",
                    "Authorizer",
                    "Workflow checkers and approvers",
                    "Focuses on approval queue volume, queue aging, rejection rates, and approvals completed.",
                    List.of("Pending queue", "Average queue age", "Approvals completed", "Rejected items"),
                    List.of("Queue aging", "Queue by module", "Approval throughput", "Notification priorities")
            ),
            "DEPARTMENT_HEAD", new RoleCatalogEntry(
                    "DEPARTMENT_HEAD",
                    "Department Head",
                    "Department risk owners and approvers",
                    "Tracks department workflow load, self-assessment actions, overdue reviews, and incidents requiring escalation.",
                    List.of("Department backlog", "Action-required RCSA", "Overdue reviews", "Pending approvals"),
                    List.of("Department queue", "Residual risk mix", "Incident states", "Review due items")
            ),
            "EXECUTIVE", new RoleCatalogEntry(
                    "EXECUTIVE",
                    "Executive",
                    "Leadership and board reporting consumers",
                    "Focuses on high-level risk posture, threshold breaches, operational losses, and activity signals.",
                    List.of("Top-line portfolio", "KRI red/amber", "Operational incidents", "Overdue reviews"),
                    List.of("Executive scorecard", "KRI threshold split", "Residual risk mix", "Service activity")
            )
    );

    public DashboardRoleCatalogResponse listRoles() {
        return new DashboardRoleCatalogResponse(roles.values().stream()
                .sorted((left, right) -> left.roleCode().compareToIgnoreCase(right.roleCode()))
                .toList());
    }

    public RoleCatalogEntry get(String roleCode) {
        RoleCatalogEntry entry = roles.get(roleCode);
        if (entry == null) {
            throw new IllegalArgumentException("Unsupported role for dashboard analytics: " + roleCode);
        }
        return entry;
    }
}
