package com.kyronic.riskengine.common.security;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String username,
        Set<String> roles,
        Set<String> permissions,
        Set<UUID> departmentIds,
        Set<UUID> branchIds,
        boolean active,
        boolean locked
) {
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public boolean belongsToDepartment(UUID departmentId) {
        return departmentIds.contains(departmentId);
    }
}
