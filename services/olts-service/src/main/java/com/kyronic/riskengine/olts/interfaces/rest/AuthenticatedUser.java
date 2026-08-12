package com.kyronic.riskengine.olts.interfaces.rest;

import org.springframework.security.oauth2.jwt.Jwt;

record AuthenticatedUser(
        Long userId,
        String username
) {

    static AuthenticatedUser fromJwt(Jwt jwt) {
        Object claim = jwt.getClaims().get("userId");
        if (!(claim instanceof String userIdValue)) {
            throw new IllegalArgumentException("JWT userId claim is missing");
        }
        return new AuthenticatedUser(Long.valueOf(userIdValue), jwt.getSubject());
    }
}
