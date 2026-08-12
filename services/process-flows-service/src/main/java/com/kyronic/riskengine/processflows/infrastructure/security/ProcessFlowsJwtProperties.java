package com.kyronic.riskengine.processflows.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record ProcessFlowsJwtProperties(String secret) {
}
