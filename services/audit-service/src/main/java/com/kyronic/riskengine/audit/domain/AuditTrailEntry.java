package com.kyronic.riskengine.audit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "platform_audit_trail")
public class AuditTrailEntry {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String serviceName;

    @Column(nullable = false, length = 120)
    private String category;

    @Column(nullable = false, length = 500)
    private String action;

    @Column(nullable = false, length = 16)
    private String httpMethod;

    @Column(nullable = false, length = 500)
    private String requestPath;

    @Column(length = 1000)
    private String queryString;

    @Column(nullable = false)
    private Integer statusCode;

    @Column(nullable = false, length = 16)
    private String outcome;

    @Column(length = 150)
    private String username;

    @Column(length = 120)
    private String userId;

    @Column(length = 120)
    private String sourceIp;

    @Column(length = 1000)
    private String userAgent;

    @Column(nullable = false, length = 120)
    private String correlationId;

    @Column(nullable = false)
    private Instant occurredAt;

    protected AuditTrailEntry() {
    }

    public AuditTrailEntry(UUID id,
                           String serviceName,
                           String category,
                           String action,
                           String httpMethod,
                           String requestPath,
                           String queryString,
                           Integer statusCode,
                           String outcome,
                           String username,
                           String userId,
                           String sourceIp,
                           String userAgent,
                           String correlationId,
                           Instant occurredAt) {
        this.id = id;
        this.serviceName = serviceName;
        this.category = category;
        this.action = action;
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.queryString = queryString;
        this.statusCode = statusCode;
        this.outcome = outcome;
        this.username = username;
        this.userId = userId;
        this.sourceIp = sourceIp;
        this.userAgent = userAgent;
        this.correlationId = correlationId;
        this.occurredAt = occurredAt;
    }

    public UUID getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCategory() {
        return category;
    }

    public String getAction() {
        return action;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getQueryString() {
        return queryString;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
