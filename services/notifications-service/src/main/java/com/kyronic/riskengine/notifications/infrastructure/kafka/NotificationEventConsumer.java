package com.kyronic.riskengine.notifications.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyronic.riskengine.notifications.application.dto.NotificationEventRequest;
import com.kyronic.riskengine.notifications.application.service.NotificationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final Validator validator;

    public NotificationEventConsumer(ObjectMapper objectMapper,
                                     NotificationService notificationService,
                                     Validator validator) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
        this.validator = validator;
    }

    @KafkaListener(topicPattern = ".*\\.v1", groupId = "kyronic-notifications")
    public void consume(String payload) throws Exception {
        NotificationEventRequest event;
        try {
            event = objectMapper.readValue(payload, NotificationEventRequest.class);
        } catch (Exception exception) {
            log.debug("Ignoring non-notification event payload", exception);
            return;
        }

        Set<ConstraintViolation<NotificationEventRequest>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            log.debug("Ignoring payload that does not match NotificationEventRequest contract: {}", violations);
            return;
        }

        notificationService.createFromEvent(event);
    }
}
