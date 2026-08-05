package com.kyronic.riskengine.auth.application.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditEventResponse(
        UUID id,
        String eventType,
        String action,
        String serviceName,
        String entityType,
        String entityId,
        String businessReference,
        UUID userId,
        String username,
        String roles,
        String permissions,
        String result,
        String failureReason,
        String requestMethod,
        String requestPath,
        String sourceIp,
        String userAgent,
        String correlationId,
        String oldValues,
        String newValues,
        Instant occurredAt
) {
}
