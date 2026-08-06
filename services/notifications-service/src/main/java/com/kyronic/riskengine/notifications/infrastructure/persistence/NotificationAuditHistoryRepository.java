package com.kyronic.riskengine.notifications.infrastructure.persistence;

import com.kyronic.riskengine.notifications.domain.NotificationAuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationAuditHistoryRepository extends JpaRepository<NotificationAuditHistory, UUID> {
}
