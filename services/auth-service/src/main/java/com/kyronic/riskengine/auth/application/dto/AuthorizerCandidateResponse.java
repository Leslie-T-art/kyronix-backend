package com.kyronic.riskengine.auth.application.dto;

import java.util.Set;

public record AuthorizerCandidateResponse(
        Long userId,
        Long departmentId,
        Set<String> permissions,
        boolean active,
        boolean delegated
) {
}
