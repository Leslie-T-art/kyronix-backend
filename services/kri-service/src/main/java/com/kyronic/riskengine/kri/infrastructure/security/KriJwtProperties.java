package com.kyronic.riskengine.kri.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record KriJwtProperties(String secret) {
}
