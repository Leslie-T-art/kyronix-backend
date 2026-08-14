package com.kyronic.riskengine.dashboard.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record DashboardJwtProperties(String secret) {
}
