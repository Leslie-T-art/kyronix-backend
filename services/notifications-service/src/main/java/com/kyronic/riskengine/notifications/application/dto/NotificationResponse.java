package com.kyronic.riskengine.notifications.application.dto;

import com.kyronic.riskengine.notifications.domain.NotificationPriority;
import com.kyronic.riskengine.notifications.domain.NotificationState;
import com.kyronic.riskengine.notifications.domain.NotificationType;
import com.kyronic.riskengine.notifications.domain.ReadState;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String notificationReference,
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
        String correlationId
) {
}
