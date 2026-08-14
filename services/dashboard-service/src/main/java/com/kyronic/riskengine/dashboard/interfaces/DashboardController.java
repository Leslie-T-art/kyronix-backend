package com.kyronic.riskengine.dashboard.interfaces;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.common.security.AuthenticatedUser;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardRoleCatalogResponse;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.DashboardSummaryResponse;
import com.kyronic.riskengine.dashboard.application.dto.DashboardDtos.RoleAnalyticsSection;
import com.kyronic.riskengine.dashboard.application.service.DashboardService;
import com.kyronic.riskengine.dashboard.infrastructure.configuration.DashboardOpenApiConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Role-aware dashboard analytics and system activity summaries.")
@SecurityRequirement(name = DashboardOpenApiConfiguration.BEARER_SCHEME)
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/roles")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List dashboard role analytics catalog")
    public ApiResponse<DashboardRoleCatalogResponse> roles() {
        return ApiResponse.success("Dashboard role catalog retrieved successfully", dashboardService.roles(), null);
    }

    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get dashboard summary for the current user")
    public ApiResponse<DashboardSummaryResponse> summary(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.success(
                "Dashboard summary retrieved successfully",
                dashboardService.summary(AuthenticatedUser.fromJwt(jwt)),
                null
        );
    }

    @GetMapping("/roles/{roleCode}/analytics")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get dashboard analytics slice for a specific role")
    public ApiResponse<RoleAnalyticsSection> roleAnalytics(@PathVariable("roleCode") String roleCode,
                                                           @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.success(
                "Role dashboard analytics retrieved successfully",
                dashboardService.analyticsForRole(roleCode.toUpperCase(), AuthenticatedUser.fromJwt(jwt)),
                null
        );
    }
}
