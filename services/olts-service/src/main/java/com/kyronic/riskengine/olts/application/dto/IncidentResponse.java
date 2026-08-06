package com.kyronic.riskengine.olts.application.dto;

import com.kyronic.riskengine.common.authorization.AuthorizationStatus;
import com.kyronic.riskengine.olts.domain.model.IncidentStatus;
import com.kyronic.riskengine.olts.domain.model.Severity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        String incidentId,
        UUID departmentId,
        String departmentName,
        UUID branchId,
        String branchName,
        LocalDate incidentDate,
        LocalDate discoveryDate,
        String lossCategory,
        String eventType,
        Severity severity,
        AuthorizationStatus authorizationStatus,
        IncidentStatus status,
        BigDecimal grossLoss,
        BigDecimal recoveries,
        BigDecimal netLoss,
        BigDecimal potentialLoss,
        UUID inputterUserId,
        UUID responsiblePersonId,
        String responsiblePersonName,
        Instant createdAt,
        UUID createdBy
) {
}
