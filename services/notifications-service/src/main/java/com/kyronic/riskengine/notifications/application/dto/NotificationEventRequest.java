package com.kyronic.riskengine.notifications.application.dto;

import com.kyronic.riskengine.notifications.domain.NotificationPriority;
import com.kyronic.riskengine.notifications.domain.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record NotificationEventRequest(
        @NotNull UUID eventId,
        @NotBlank String eventType,
        @NotBlank String sourceService,
        @NotBlank String entityType,
        @NotNull UUID entityId,
        @NotBlank String businessReference,
        @NotEmpty List<Long> recipientUserIds,
        UUID departmentId,
        @NotNull NotificationType type,
        @NotNull NotificationPriority priority,
        @NotBlank String title,
        @NotBlank String message,
        Instant occurredAt,
        @NotBlank String correlationId
) {
}
