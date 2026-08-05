package com.kyronic.riskengine.auth.application.service;

import com.kyronic.riskengine.auth.application.dto.AuditEventCommand;
import com.kyronic.riskengine.auth.application.dto.AuditEventResponse;
import com.kyronic.riskengine.auth.domain.AuditEvent;
import com.kyronic.riskengine.auth.infrastructure.persistence.AuditEventRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuditTrailService {

    private static final String SERVICE_NAME = "auth-service";

    private final AuditEventRepository auditEventRepository;

    public AuditTrailService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    public AuditEventResponse record(AuditEventCommand command) {
        AuditEvent auditEvent = new AuditEvent(
                UUID.randomUUID(),
                command.eventType(),
                command.action(),
                SERVICE_NAME,
                command.entityType(),
                command.entityId(),
                command.businessReference(),
                command.userId(),
                command.username(),
                command.roles(),
                command.permissions(),
                command.result(),
                command.failureReason(),
                command.requestMethod(),
                command.requestPath(),
                command.sourceIp(),
                command.userAgent(),
                command.correlationId(),
                command.oldValues(),
                command.newValues(),
                command.occurredAt()
        );
        return toResponse(auditEventRepository.save(auditEvent));
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> findAuditEvents() {
        return auditEventRepository.findAll(Sort.by(Sort.Direction.DESC, "occurredAt")).stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditEventResponse toResponse(AuditEvent auditEvent) {
        return new AuditEventResponse(
                auditEvent.getId(),
                auditEvent.getEventType(),
                auditEvent.getAction(),
                auditEvent.getServiceName(),
                auditEvent.getEntityType(),
                auditEvent.getEntityId(),
                auditEvent.getBusinessReference(),
                auditEvent.getUserId(),
                auditEvent.getUsername(),
                auditEvent.getRoles(),
                auditEvent.getPermissions(),
                auditEvent.getResult(),
                auditEvent.getFailureReason(),
                auditEvent.getRequestMethod(),
                auditEvent.getRequestPath(),
                auditEvent.getSourceIp(),
                auditEvent.getUserAgent(),
                auditEvent.getCorrelationId(),
                auditEvent.getOldValues(),
                auditEvent.getNewValues(),
                auditEvent.getOccurredAt()
        );
    }
}
