package com.kyronic.riskengine.olts.application.dto;

import com.kyronic.riskengine.olts.domain.model.EventType;
import com.kyronic.riskengine.olts.domain.model.LossCategory;
import com.kyronic.riskengine.olts.domain.model.Severity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateIncidentRequest(
        @NotNull LocalDate incidentDate,
        @NotNull LocalDate discoveryDate,
        @NotNull UUID branchId,
        @NotNull UUID departmentId,
        @NotNull LossCategory lossCategory,
        @NotNull EventType eventType,
        @NotNull Severity severity,
        @NotBlank String description,
        @NotBlank String currencyCode,
        @NotNull @DecimalMin("0.00") BigDecimal grossLoss,
        @NotNull @DecimalMin("0.00") BigDecimal recoveries,
        @NotNull @DecimalMin("0.00") BigDecimal potentialLoss
) {
}
