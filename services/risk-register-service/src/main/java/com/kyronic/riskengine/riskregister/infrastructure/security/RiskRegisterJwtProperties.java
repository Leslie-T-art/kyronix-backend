package com.kyronic.riskengine.riskregister.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record RiskRegisterJwtProperties(String secret) {
}
