package com.kyronic.riskengine.common.observability;

import java.time.Instant;

public record AuditTrailEntryRequest(
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
