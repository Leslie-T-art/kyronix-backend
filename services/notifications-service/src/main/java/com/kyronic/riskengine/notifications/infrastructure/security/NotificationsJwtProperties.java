package com.kyronic.riskengine.notifications.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record NotificationsJwtProperties(String secret) {
}
