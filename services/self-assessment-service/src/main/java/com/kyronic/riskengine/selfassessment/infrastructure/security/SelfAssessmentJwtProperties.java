package com.kyronic.riskengine.selfassessment.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record SelfAssessmentJwtProperties(String secret) {
}
