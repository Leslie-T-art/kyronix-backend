package com.kyronic.riskengine.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record GatewayJwtProperties(String secret) {
}
