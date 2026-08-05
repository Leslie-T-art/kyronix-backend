package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.dto.AuditEventResponse;
import com.kyronic.riskengine.auth.application.service.AuditRequestFactory;
import com.kyronic.riskengine.auth.application.service.AuditTrailService;
import com.kyronic.riskengine.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/audit-events")
@Tag(name = "Audit Trail", description = "Immutable authentication and administration audit trail.")
public class AuditTrailController {

    private final AuditTrailService auditTrailService;
    private final AuditRequestFactory auditRequestFactory;

    public AuditTrailController(AuditTrailService auditTrailService, AuditRequestFactory auditRequestFactory) {
        this.auditTrailService = auditTrailService;
        this.auditRequestFactory = auditRequestFactory;
    }

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ADMIN_USERS')")
    @Operation(summary = "List audit events", description = "Retrieve immutable audit events for authentication and administration activity.")
    public ApiResponse<List<AuditEventResponse>> listAuditEvents(HttpServletRequest request) {
        return ApiResponse.success(
                "Audit events retrieved successfully",
                auditTrailService.findAuditEvents(),
                auditRequestFactory.resolveCorrelationId(request)
        );
    }
}
