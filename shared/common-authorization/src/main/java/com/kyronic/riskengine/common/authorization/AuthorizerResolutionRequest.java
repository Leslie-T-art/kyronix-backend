package com.kyronic.riskengine.common.authorization;

import java.time.Instant;

public record AuthorizerResolutionRequest(
        Long departmentId,
        Long inputterUserId,
        Long lastModifiedBy,
        String requiredPermission,
        Instant when
) {
}
