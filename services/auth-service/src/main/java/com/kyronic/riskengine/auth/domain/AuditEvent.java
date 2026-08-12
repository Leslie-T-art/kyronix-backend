package com.kyronic.riskengine.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String eventType;

    @Column(nullable = false, length = 120)
    private String action;

    @Column(nullable = false, length = 120)
    private String serviceName;

    @Column(nullable = false, length = 120)
    private String entityType;

    @Column(length = 120)
    private String entityId;

    @Column(length = 120)
    private String businessReference;

    @Column
    private Long userId;

    @Column(length = 150)
    private String username;

    @Column(length = 1000)
    private String roles;

    @Column(length = 4000)
    private String permissions;

    @Column(nullable = false, length = 16)
    private String result;

    @Column(length = 2000)
    private String failureReason;

    @Column(nullable = false, length = 16)
    private String requestMethod;

    @Column(nullable = false, length = 500)
    private String requestPath;

    @Column(length = 120)
    private String sourceIp;

    @Column(length = 1000)
    private String userAgent;

    @Column(nullable = false, length = 120)
    private String correlationId;

    @Column(length = 8000)
    private String oldValues;

    @Column(length = 8000)
    private String newValues;

    @Column(nullable = false)
    private Instant occurredAt;

    protected AuditEvent() {
    }

    public AuditEvent(Long id,
                      String eventType,
                      String action,
                      String serviceName,
                      String entityType,
                      String entityId,
                      String businessReference,
                      Long userId,
                      String username,
                      String roles,
                      String permissions,
                      String result,
                      String failureReason,
                      String requestMethod,
                      String requestPath,
                      String sourceIp,
                      String userAgent,
                      String correlationId,
                      String oldValues,
                      String newValues,
                      Instant occurredAt) {
        this.id = id;
        this.eventType = eventType;
        this.action = action;
        this.serviceName = serviceName;
        this.entityType = entityType;
        this.entityId = entityId;
        this.businessReference = businessReference;
        this.userId = userId;
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
        this.result = result;
        this.failureReason = failureReason;
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.sourceIp = sourceIp;
        this.userAgent = userAgent;
        this.correlationId = correlationId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.occurredAt = occurredAt;
    }

    public Long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getAction() {
        return action;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getBusinessReference() {
        return businessReference;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRoles() {
        return roles;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getResult() {
        return result;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
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

    public String getOldValues() {
        return oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
