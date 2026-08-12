package com.kyronic.riskengine.auth.application.dto;

import java.util.Set;

public record AuthMeResponse(
        Long id,
        String username,
        String fullName,
        boolean active,
        boolean locked,
        ReferenceAssignment department,
        ReferenceAssignment branch,
        Set<RoleAssignment> roles,
        Set<String> permissions
) {

    public record ReferenceAssignment(
            Long id,
            String code,
            String name
    ) {
    }

    public record RoleAssignment(
            String code,
            String name
    ) {
    }
}
