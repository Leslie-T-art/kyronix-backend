package com.kyronic.riskengine.auth.application.dto;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String fullName,
        boolean active,
        boolean locked,
        UUID departmentId,
        UUID branchId,
        Set<String> roles,
        Set<String> permissions
) {
}
