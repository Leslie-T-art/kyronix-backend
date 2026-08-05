package com.kyronic.riskengine.audit.application.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditTrailResponse(
        UUID id,
        String serviceName,
        String category,
        String action,
        String httpMethod,
        String requestPath,
        String queryString,
        Integer statusCode,
        String outcome,
        String username,
        String userId,
        String sourceIp,
        String userAgent,
        String correlationId,
        Instant occurredAt
) {
}
