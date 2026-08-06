package com.kyronic.riskengine.auth.application.dto;

import java.util.Set;
import java.util.UUID;

public record AuthorizerCandidateResponse(
        UUID userId,
        UUID departmentId,
        Set<String> permissions,
        boolean active,
        boolean delegated
) {
}
