package com.kyronic.riskengine.common.authorization;

import java.time.Instant;
import java.util.Set;

public record AuthorizerCandidate(
        Long userId,
        Long departmentId,
        Set<String> permissions,
        boolean active,
        boolean delegated,
        Long delegatedFromUserId,
        Instant effectiveFrom,
        Instant effectiveTo
) {
    public boolean isEffectiveAt(Instant instant) {
        boolean afterStart = effectiveFrom == null || !instant.isBefore(effectiveFrom);
        boolean beforeEnd = effectiveTo == null || !instant.isAfter(effectiveTo);
        return afterStart && beforeEnd;
    }
}
