package com.kyronic.riskengine.common.events;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record EventEnvelope(
        UUID eventId,
        String eventType,
        String eventVersion,
        UUID aggregateId,
        String aggregateType,
        String businessReference,
        Integer recordVersion,
        UUID departmentId,
        Instant occurredAt,
        String correlationId,
        String causationId,
        UUID initiatedBy,
        UUID inputterUserId,
        UUID authorizerUserId,
        String sourceService,
        String tenantId,
        Map<String, Object> payload
) {
}
