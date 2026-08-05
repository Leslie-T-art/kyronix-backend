package com.kyronic.riskengine.auth.application.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        Instant issuedAt,
        Instant expiresAt,
        UUID userId,
        String username,
        String fullName,
        UUID departmentId,
        UUID branchId,
        Set<String> roles,
        Set<String> permissions
) {
}
