package com.kyronic.riskengine.notifications.application.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Component
public class NotificationReferenceGenerator {

    private final EntityManager entityManager;

    public NotificationReferenceGenerator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public String nextReference() {
        Number value = (Number) entityManager.createNativeQuery(
                        "select nextval('notifications_service.notification_reference_seq')")
                .getSingleResult();
        return "NTF-" + Year.now().getValue() + "-" + String.format("%05d", value.longValue());
    }
}
