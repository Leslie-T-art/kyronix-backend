package com.kyronic.riskengine.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_audit_history")
public class NotificationAuditHistory {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID notificationId;

    @Column(nullable = false, length = 50)
    private String action;

    @Column
    private UUID actorUserId;

    @Column(nullable = false, length = 120)
    private String actorUsername;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(nullable = false, length = 120)
    private String correlationId;

    protected NotificationAuditHistory() {
    }

    public NotificationAuditHistory(UUID id,
                                    UUID notificationId,
                                    String action,
                                    UUID actorUserId,
                                    String actorUsername,
                                    Instant occurredAt,
                                    String correlationId) {
        this.id = id;
        this.notificationId = notificationId;
        this.action = action;
        this.actorUserId = actorUserId;
        this.actorUsername = actorUsername;
        this.occurredAt = occurredAt;
        this.correlationId = correlationId;
    }
}
