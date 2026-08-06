package com.kyronic.riskengine.notifications.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyronic.riskengine.notifications.application.dto.NotificationEventRequest;
import com.kyronic.riskengine.notifications.application.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public NotificationEventConsumer(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = {
            "authorization.requested.v1",
            "authorization.approved.v1",
            "authorization.rejected.v1",
            "authorization.returned.v1",
            "authorization.resubmitted.v1",
            "authorization.overdue.v1",
            "authorization.escalated.v1",
            "kri.threshold.breached.v1",
            "risk.review-due.v1",
            "self-assessment.assigned.v1",
            "process-flow.published.v1"
    }, groupId = "kyronic-notifications")
    public void consume(String payload) throws Exception {
        notificationService.createFromEvent(objectMapper.readValue(payload, NotificationEventRequest.class));
    }
}
