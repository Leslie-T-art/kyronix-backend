package com.kyronic.riskengine.common.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.jwt.Jwt;

public record AuthenticatedUser(
        Long userId,
        String username,
        Set<String> roles,
        Set<String> permissions,
        Set<Long> departmentIds,
        Set<Long> branchIds,
        boolean active,
        boolean locked
) {
    public static AuthenticatedUser fromJwt(Jwt jwt) {
        return new AuthenticatedUser(
                parseLongClaim(jwt, "userId"),
                jwt.getSubject(),
                claimSet(jwt, "roles"),
                claimSet(jwt, "permissions"),
                claimSet(jwt, "departmentIds").stream().map(Long::valueOf).collect(Collectors.toSet()),
                claimSet(jwt, "branchIds").stream().map(Long::valueOf).collect(Collectors.toSet()),
                jwt.getClaimAsBoolean("active") == null || jwt.getClaimAsBoolean("active"),
                jwt.getClaimAsBoolean("locked") != null && jwt.getClaimAsBoolean("locked")
        );
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public boolean belongsToDepartment(Long departmentId) {
        return departmentIds.contains(departmentId);
    }

    private static Long parseLongClaim(Jwt jwt, String claimName) {
        String claim = jwt.getClaimAsString(claimName);
        if (claim == null || claim.isBlank()) {
            return null;
        }
        return Long.valueOf(claim);
    }

    private static Set<String> claimSet(Jwt jwt, String claimName) {
        Set<String> values = jwt.getClaimAsStringList(claimName) == null
                ? Set.of()
                : Set.copyOf(jwt.getClaimAsStringList(claimName));
        return values;
    }
}
