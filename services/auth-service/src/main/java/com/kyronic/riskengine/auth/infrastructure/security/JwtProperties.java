package com.kyronic.riskengine.auth.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        String audience,
        long accessTokenTtlSeconds
) {
}
