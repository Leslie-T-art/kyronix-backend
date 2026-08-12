package com.kyronic.riskengine.notifications.infrastructure.persistence;

import com.kyronic.riskengine.notifications.domain.InAppNotification;
import com.kyronic.riskengine.notifications.domain.NotificationType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<InAppNotification, UUID> {

    Optional<InAppNotification> findByEventIdAndRecipientUserIdAndType(UUID eventId, Long recipientUserId, NotificationType type);

    List<InAppNotification> findAllByRecipientUserId(Long recipientUserId, Sort sort);
}
