package com.kyronic.riskengine.auth.application.dto;

import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        boolean active,
        boolean locked,
        Long departmentId,
        Long branchId,
        Set<String> roles,
        Set<String> permissions
) {
}
