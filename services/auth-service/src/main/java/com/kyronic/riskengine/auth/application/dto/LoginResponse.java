package com.kyronic.riskengine.auth.application.dto;

import java.time.Instant;
import java.util.Set;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        Instant issuedAt,
        Instant expiresAt,
        Long userId,
        String username,
        String fullName,
        Long departmentId,
        Long branchId,
        Set<String> roles,
        Set<String> permissions
) {
}
