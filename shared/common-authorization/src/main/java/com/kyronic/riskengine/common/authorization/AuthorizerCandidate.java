package com.kyronic.riskengine.common.authorization;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record AuthorizerCandidate(
        UUID userId,
        UUID departmentId,
        Set<String> permissions,
        boolean active,
        boolean delegated,
        UUID delegatedFromUserId,
        Instant effectiveFrom,
        Instant effectiveTo
) {
    public boolean isEffectiveAt(Instant instant) {
        boolean afterStart = effectiveFrom == null || !instant.isBefore(effectiveFrom);
        boolean beforeEnd = effectiveTo == null || !instant.isAfter(effectiveTo);
        return afterStart && beforeEnd;
    }
}
