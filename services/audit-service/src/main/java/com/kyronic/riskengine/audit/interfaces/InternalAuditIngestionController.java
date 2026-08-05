package com.kyronic.riskengine.audit.interfaces;

import com.kyronic.riskengine.audit.application.dto.AuditTrailResponse;
import com.kyronic.riskengine.audit.application.service.AuditTrailService;
import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.common.observability.AuditTrailEntryRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/audit/entries")
public class InternalAuditIngestionController {

    private final AuditTrailService auditTrailService;

    public InternalAuditIngestionController(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuditTrailResponse> ingest(@RequestBody AuditTrailEntryRequest request) {
        return ApiResponse.success(
                "Audit entry recorded successfully",
                auditTrailService.record(request),
                request.correlationId()
        );
    }
}
