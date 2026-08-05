package com.kyronic.riskengine.olts.interfaces.rest;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

record AuthenticatedUser(
        UUID userId,
        String username
) {

    static AuthenticatedUser fromJwt(Jwt jwt) {
        Object claim = jwt.getClaims().get("userId");
        if (!(claim instanceof String userIdValue)) {
            throw new IllegalArgumentException("JWT userId claim is missing");
        }
        return new AuthenticatedUser(UUID.fromString(userIdValue), jwt.getSubject());
    }
}
