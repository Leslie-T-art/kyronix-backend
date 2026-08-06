package com.kyronic.riskengine.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class InAppNotification {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String notificationReference;

    @Column(nullable = false)
    private UUID recipientUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationPriority priority;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(nullable = false, length = 120)
    private String sourceService;

    @Column(nullable = false, length = 120)
    private String entityType;

    @Column(nullable = false)
    private UUID entityId;

    @Column(nullable = false, length = 120)
    private String businessReference;

    @Column
    private UUID departmentId;

    @Column(nullable = false, length = 500)
    private String actionUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationState state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReadState readState;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant readAt;

    @Column
    private Instant archivedAt;

    @Column
    private Instant dismissedAt;

    @Column
    private Instant expiresAt;

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false, length = 120)
    private String correlationId;

    @Version
    private Long version;

    protected InAppNotification() {
    }

    public InAppNotification(UUID id,
                             String notificationReference,
                             UUID recipientUserId,
                             NotificationType type,
                             NotificationPriority priority,
                             String title,
                             String message,
                             String sourceService,
                             String entityType,
                             UUID entityId,
                             String businessReference,
                             UUID departmentId,
                             String actionUrl,
                             NotificationState state,
                             ReadState readState,
                             Instant createdAt,
                             Instant readAt,
                             Instant archivedAt,
                             Instant dismissedAt,
                             Instant expiresAt,
                             UUID eventId,
                             String correlationId,
                             Long version) {
        this.id = id;
        this.notificationReference = notificationReference;
        this.recipientUserId = recipientUserId;
        this.type = type;
        this.priority = priority;
        this.title = title;
        this.message = message;
        this.sourceService = sourceService;
        this.entityType = entityType;
        this.entityId = entityId;
        this.businessReference = businessReference;
        this.departmentId = departmentId;
        this.actionUrl = actionUrl;
        this.state = state;
        this.readState = readState;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.archivedAt = archivedAt;
        this.dismissedAt = dismissedAt;
        this.expiresAt = expiresAt;
        this.eventId = eventId;
        this.correlationId = correlationId;
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public String getNotificationReference() {
        return notificationReference;
    }

    public UUID getRecipientUserId() {
        return recipientUserId;
    }

    public NotificationType getType() {
        return type;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getSourceService() {
        return sourceService;
    }

    public String getEntityType() {
        return entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public String getBusinessReference() {
        return businessReference;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public NotificationState getState() {
        return state;
    }

    public ReadState getReadState() {
        return readState;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public Instant getArchivedAt() {
        return archivedAt;
    }

    public Instant getDismissedAt() {
        return dismissedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public boolean isExpired(Instant now) {
        return expiresAt != null && expiresAt.isBefore(now);
    }

    public void markRead(Instant now) {
        this.readState = ReadState.READ;
        this.readAt = now;
    }

    public void markUnread() {
        this.readState = ReadState.UNREAD;
        this.readAt = null;
    }

    public void archive(Instant now) {
        this.state = NotificationState.ARCHIVED;
        this.archivedAt = now;
    }

    public void dismiss(Instant now) {
        this.state = NotificationState.DISMISSED;
        this.dismissedAt = now;
    }

    public void expire() {
        this.state = NotificationState.EXPIRED;
    }
}
