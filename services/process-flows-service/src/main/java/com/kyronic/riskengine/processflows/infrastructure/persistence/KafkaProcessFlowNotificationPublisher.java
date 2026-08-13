package com.kyronic.riskengine.processflows.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyronic.riskengine.processflows.application.service.ProcessFlowNotificationPublisher;
import com.kyronic.riskengine.processflows.domain.ProcessFlowRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class KafkaProcessFlowNotificationPublisher implements ProcessFlowNotificationPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProcessFlowNotificationPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishApproved(ProcessFlowRecord record) {
        publish(record, "process-flow.approved.v1", "AUTHORIZED", "NORMAL",
                "Process flow approved", record.getFlowReference() + " was approved.");
    }

    @Override
    public void publishRejected(ProcessFlowRecord record) {
        publish(record, "process-flow.rejected.v1", "REJECTED", "HIGH",
                "Process flow rejected", record.getFlowReference() + " was rejected. Review the comment and update the process flow.");
    }

    @Override
    public void publishReturned(ProcessFlowRecord record) {
        publish(record, "process-flow.returned.v1", "RETURNED_FOR_CORRECTION", "HIGH",
                "Process flow returned", record.getFlowReference() + " was returned for correction.");
    }

    private void publish(ProcessFlowRecord record,
                         String eventType,
                         String type,
                         String priority,
                         String title,
                         String message) {
        if (record.getInputterUserId() == null || record.getInputterUserId() <= 0) {
            return;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", UUID.randomUUID());
        payload.put("eventType", eventType);
        payload.put("sourceService", "process-flows-service");
        payload.put("entityType", "PROCESS_FLOW");
        payload.put("entityId", UUID.nameUUIDFromBytes(("process-flow:" + record.getId()).getBytes(StandardCharsets.UTF_8)));
        payload.put("businessReference", record.getFlowReference());
        payload.put("recipientUserIds", List.of(record.getInputterUserId()));
        payload.put("departmentId", null);
        payload.put("type", type);
        payload.put("priority", priority);
        payload.put("title", title);
        payload.put("message", message);
        payload.put("occurredAt", Instant.now());
        payload.put("correlationId", UUID.randomUUID().toString());

        try {
            kafkaTemplate.send(eventType, objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize notification event " + eventType, exception);
        }
    }
}
