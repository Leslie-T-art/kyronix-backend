package com.kyronic.riskengine.olts.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyronic.riskengine.common.events.EventEnvelope;
import com.kyronic.riskengine.olts.application.service.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class KafkaNotificationEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaNotificationEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(EventEnvelope eventEnvelope) {
        log.info("kyronic_event type={} aggregate={} businessReference={} correlationId={}",
                eventEnvelope.eventType(),
                eventEnvelope.aggregateType(),
                eventEnvelope.businessReference(),
                eventEnvelope.correlationId());

        toNotificationEvent(eventEnvelope).ifPresent(this::send);
    }

    private Optional<Map<String, Object>> toNotificationEvent(EventEnvelope eventEnvelope) {
        return switch (eventEnvelope.eventType()) {
            case "authorization.requested.v1" -> recipientEvent(
                    eventEnvelope.authorizerUserId(),
                    eventEnvelope,
                    "AUTHORIZATION_REQUIRED",
                    "HIGH",
                    "OLTS authorization required",
                    eventEnvelope.businessReference() + " is awaiting your authorization."
            );
            case "authorization.approved.v1" -> recipientEvent(
                    eventEnvelope.inputterUserId(),
                    eventEnvelope,
                    "AUTHORIZED",
                    "NORMAL",
                    "Record authorized",
                    eventEnvelope.businessReference() + " was authorized."
            );
            case "authorization.rejected.v1" -> recipientEvent(
                    eventEnvelope.inputterUserId(),
                    eventEnvelope,
                    "REJECTED",
                    "HIGH",
                    "Record rejected",
                    eventEnvelope.businessReference() + " was rejected. Review the reason and take the required action."
            );
            case "authorization.returned.v1" -> recipientEvent(
                    eventEnvelope.inputterUserId(),
                    eventEnvelope,
                    "RETURNED_FOR_CORRECTION",
                    "HIGH",
                    "Correction required",
                    eventEnvelope.businessReference() + " was returned for correction."
            );
            default -> Optional.empty();
        };
    }

    private Optional<Map<String, Object>> recipientEvent(UUID recipientUserId,
                                                         EventEnvelope eventEnvelope,
                                                         String type,
                                                         String priority,
                                                         String title,
                                                         String message) {
        if (recipientUserId == null) {
            return Optional.empty();
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", eventEnvelope.eventId());
        payload.put("eventType", eventEnvelope.eventType());
        payload.put("sourceService", eventEnvelope.sourceService());
        payload.put("entityType", eventEnvelope.aggregateType());
        payload.put("entityId", eventEnvelope.aggregateId());
        payload.put("businessReference", eventEnvelope.businessReference());
        payload.put("recipientUserIds", List.of(recipientUserId));
        payload.put("departmentId", eventEnvelope.departmentId());
        payload.put("type", type);
        payload.put("priority", priority);
        payload.put("title", title);
        payload.put("message", message);
        payload.put("occurredAt", eventEnvelope.occurredAt());
        payload.put("correlationId", eventEnvelope.correlationId());
        return Optional.of(payload);
    }

    private void send(Map<String, Object> event) {
        try {
            kafkaTemplate.send(String.valueOf(event.get("eventType")), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize notification event " + event.get("eventType"), exception);
        }
    }
}
