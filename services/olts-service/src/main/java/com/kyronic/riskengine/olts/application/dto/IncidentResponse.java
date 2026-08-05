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
        UUID branchId,
        LocalDate incidentDate,
        LocalDate discoveryDate,
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
