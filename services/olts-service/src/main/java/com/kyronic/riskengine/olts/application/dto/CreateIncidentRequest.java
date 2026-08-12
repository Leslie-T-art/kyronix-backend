package com.kyronic.riskengine.olts.application.dto;

import com.kyronic.riskengine.olts.domain.model.Severity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateIncidentRequest(
        @NotBlank String eventTitle,
        @NotNull Long eventStatusId,
        @NotNull LocalDate incidentDate,
        LocalDate incidentEndDate,
        @NotNull LocalDate detectionDate,
        @NotNull Long branchId,
        @NotNull Long departmentId,
        @NotBlank String processName,
        @NotBlank String productService,
        @NotNull Long baselEventCategoryId,
        @NotBlank @Size(max = 4000) String eventDescription,
        @Size(max = 4000) String immediateActionTaken,
        @NotNull Long rootCauseCategoryId,
        @Size(max = 4000) String rootCauseDescription,
        Long controlId,
        @NotNull Boolean failedMissingControl,
        @NotNull Long currencyId,
        @NotNull @DecimalMin("0.00") BigDecimal grossLoss,
        @NotNull @DecimalMin("0.00") BigDecimal restitutionRemediationCost,
        Long recoveryMethodId,
        @Size(max = 255) String accountingGlReference,
        Long dataSourceId,
        @Size(max = 255) String nonFinancialImpactType,
        @Size(max = 4000) String nonFinancialImpactDetails,
        @NotNull Severity overallEventSeverity,
        @Size(max = 4000) String correctiveAction,
        @Size(max = 255) String actionOwner,
        LocalDate actionTargetDate,
        Long actionStatusId,
        @NotNull Boolean preventiveControlImplemented,
        @Size(max = 4000) String validationEvidence,
        LocalDate closureValidationDate,
        @Size(max = 4000) String closureComment
) {
}
