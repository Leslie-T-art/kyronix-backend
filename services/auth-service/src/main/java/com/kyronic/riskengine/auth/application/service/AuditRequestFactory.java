package com.kyronic.riskengine.auth.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyronic.riskengine.auth.application.dto.AuditEventCommand;
import com.kyronic.riskengine.auth.application.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AuditRequestFactory {

    public static final String CORRELATION_ID_ATTRIBUTE = "kyronic.correlation-id";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    private final ObjectMapper objectMapper;
    private final Clock clock;

    public AuditRequestFactory(ObjectMapper objectMapper, Clock clock) {
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public AuditEventCommand create(Authentication authentication,
                                    HttpServletRequest request,
                                    String eventType,
                                    String action,
                                    String entityType,
                                    String entityId,
                                    String businessReference,
                                    String result,
                                    String failureReason,
                                    Object oldValues,
                                    Object newValues) {
        return new AuditEventCommand(
                eventType,
                action,
                entityType,
                entityId,
                businessReference,
                extractUserId(authentication),
                extractUsername(authentication),
                extractRoles(authentication),
                extractPermissions(authentication),
                result,
                failureReason,
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                resolveCorrelationId(request),
                serialize(oldValues),
                serialize(newValues),
                Instant.now(clock)
        );
    }

    public AuditEventCommand createUnauthenticated(HttpServletRequest request,
                                                   String eventType,
                                                   String action,
                                                   String entityType,
                                                   String result,
                                                   String failureReason) {
        return new AuditEventCommand(
                eventType,
                action,
                entityType,
                null,
                null,
                null,
                null,
                null,
                null,
                result,
                failureReason,
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                resolveCorrelationId(request),
                null,
                null,
                Instant.now(clock)
        );
    }

    public AuditEventCommand createLoginSuccess(HttpServletRequest request, LoginResponse response) {
        return new AuditEventCommand(
                "AUTH_LOGIN_SUCCESS",
                "LOGIN",
                "AUTH_SESSION",
                response.userId().toString(),
                response.username(),
                response.userId(),
                response.username(),
                String.join(",", response.roles()),
                String.join(",", response.permissions()),
                "SUCCESS",
                null,
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                resolveCorrelationId(request),
                null,
                serialize(Map.of(
                        "userId", response.userId(),
                        "username", response.username(),
                        "fullName", response.fullName(),
                        "departmentId", response.departmentId(),
                        "branchId", response.branchId(),
                        "roles", response.roles(),
                        "permissions", response.permissions(),
                        "issuedAt", response.issuedAt(),
                        "expiresAt", response.expiresAt()
                )),
                Instant.now(clock)
        );
    }

    public String resolveCorrelationId(HttpServletRequest request) {
        Object attribute = request.getAttribute(CORRELATION_ID_ATTRIBUTE);
        if (attribute instanceof String correlationId && !correlationId.isBlank()) {
            return correlationId;
        }
        String header = request.getHeader(CORRELATION_ID_HEADER);
        return header == null || header.isBlank() ? UUID.randomUUID().toString() : header;
    }

    private UUID extractUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        String userId = jwt.getClaimAsString("userId");
        return userId == null ? null : UUID.fromString(userId);
    }

    private String extractUsername(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return authentication.getName();
    }

    private String extractRoles(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .sorted()
                .collect(Collectors.joining(","));
    }

    private String extractPermissions(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith("ROLE_"))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(","));
    }

    private String serialize(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("failed to serialize audit payload", exception);
        }
    }
}
