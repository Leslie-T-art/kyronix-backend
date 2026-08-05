package com.kyronic.riskengine.auth.application.service;

import com.kyronic.riskengine.auth.infrastructure.security.JwtProperties;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenSettings {

    private final JwtProperties properties;

    public AuthTokenSettings(JwtProperties properties) {
        this.properties = properties;
    }

    public String issuer() {
        return properties.issuer();
    }

    public String audience() {
        return properties.audience();
    }

    public long accessTokenTtlSeconds() {
        return properties.accessTokenTtlSeconds();
    }
}
