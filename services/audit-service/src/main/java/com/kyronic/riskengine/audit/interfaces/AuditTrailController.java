package com.kyronic.riskengine.audit.interfaces;

import com.kyronic.riskengine.audit.application.dto.AuditTrailResponse;
import com.kyronic.riskengine.audit.application.service.AuditTrailService;
import com.kyronic.riskengine.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit/trail")
@Tag(name = "Platform Audit Trail", description = "Platform-wide timeline of activity across all services.")
public class AuditTrailController {

    private final AuditTrailService auditTrailService;

    public AuditTrailController(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @GetMapping
    @Operation(summary = "Get platform audit trail", description = "Retrieve the platform-wide audit trail across all current and future services. Filters are optional.")
    public ApiResponse<List<AuditTrailResponse>> list(HttpServletRequest request,
                                                      @RequestHeader(name = "X-Correlation-Id", required = false) String correlationId,
                                                      @RequestParam(name = "serviceName", required = false) String serviceName,
                                                      @RequestParam(name = "username", required = false) String username,
                                                      @RequestParam(name = "outcome", required = false) String outcome,
                                                      @RequestParam(name = "requestPath", required = false) String requestPath,
                                                      @RequestParam(name = "q", required = false) String searchText,
                                                      @RequestParam(name = "limit", required = false) Integer limit) {
        String resolvedCorrelationId = correlationId == null || correlationId.isBlank()
                ? UUID.randomUUID().toString()
                : correlationId;
        return ApiResponse.success(
                "Audit trail retrieved successfully",
                auditTrailService.listTrail(serviceName, username, outcome, requestPath, searchText, limit),
                resolvedCorrelationId
        );
    }
}
