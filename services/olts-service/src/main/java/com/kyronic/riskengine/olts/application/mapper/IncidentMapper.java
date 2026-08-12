package com.kyronic.riskengine.olts.application.mapper;

import com.kyronic.riskengine.olts.application.dto.IncidentResponse;
import com.kyronic.riskengine.olts.domain.model.OltsIncident;
import org.mapstruct.Mapper;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface IncidentMapper {

    default IncidentResponse toResponse(OltsIncident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getEventId(),
                incident.getEventTitle(),
                incident.getEventStatusId(),
                incident.getIncidentDate(),
                incident.getIncidentEndDate(),
                incident.getDetectionDate(),
                incident.getDepartmentId(),
                null,
                incident.getBranchId(),
                null,
                incident.getProcessName(),
                incident.getProductService(),
                incident.getBaselEventCategoryId(),
                incident.getEventDescription(),
                incident.getImmediateActionTaken(),
                incident.getRootCauseCategoryId(),
                incident.getRootCauseDescription(),
                incident.getControlId(),
                incident.getFailedMissingControl(),
                incident.getCurrencyId(),
                incident.getGrossLoss(),
                incident.getRestitutionRemediationCost(),
                incident.getRecoveryMethodId(),
                incident.getNetLoss(),
                incident.getAccountingGlReference(),
                incident.getDataSourceId(),
                incident.getNonFinancialImpactType(),
                incident.getNonFinancialImpactDetails(),
                incident.getOverallEventSeverity(),
                incident.getCorrectiveAction(),
                incident.getActionOwner(),
                incident.getActionTargetDate(),
                incident.getActionStatusId(),
                incident.getPreventiveControlImplemented(),
                incident.getValidationEvidence(),
                incident.getClosureValidationDate(),
                incident.getClosureComment(),
                incident.getAuthorizationStatus(),
                incident.getStatus(),
                incident.getEventOwner(),
                incident.getReportedBy(),
                incident.getCreatedByUsername(),
                incident.getCreatedAt(),
                incident.getLastUpdatedByUsername(),
                incident.getUpdatedAt() == null ? Instant.now() : incident.getUpdatedAt(),
                incident.getRecordVersion()
        );
    }
}
