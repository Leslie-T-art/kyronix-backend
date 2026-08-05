package com.kyronic.riskengine.common.authorization;

import java.time.Instant;
import java.util.UUID;

public record AuthorizerResolutionRequest(
        UUID departmentId,
        UUID inputterUserId,
        UUID lastModifiedBy,
        String requiredPermission,
        Instant when
) {
}
