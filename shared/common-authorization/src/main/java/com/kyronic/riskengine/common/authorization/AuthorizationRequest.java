package com.kyronic.riskengine.common.authorization;

import java.time.Instant;
import java.util.UUID;

public record AuthorizationRequest(
        UUID id,
        String serviceName,
        String entityType,
        UUID entityId,
        String businessReference,
        Integer recordVersion,
        UUID departmentId,
        UUID branchId,
        UUID inputterUserId,
        UUID lastModifiedBy,
        UUID proposedAuthorizerUserId,
        UUID actualAuthorizerUserId,
        AuthorizationRequestStatus status,
        Instant submittedAt,
        Instant reviewStartedAt,
        Instant dueAt,
        Instant completedAt,
        String submissionComment,
        String rejectionReason,
        String returnReason,
        boolean escalated,
        int escalationLevel,
        UUID delegatedFromUserId,
        UUID previousAuthorizationRequestId,
        String correlationId,
        long version
) {
}
