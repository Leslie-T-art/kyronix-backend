package com.kyronic.riskengine.olts.domain.model;

import com.kyronic.riskengine.common.authorization.AuthorizationException;
import com.kyronic.riskengine.common.authorization.AuthorizationStatus;
import com.kyronic.riskengine.common.authorization.SegregationOfDutiesPolicy;
import com.kyronic.riskengine.common.api.ErrorCodes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class OltsIncident {

    private final UUID id;
    private final String incidentId;
    private final UUID departmentId;
    private final UUID branchId;
    private final UUID inputterUserId;
    private final LocalDate incidentDate;
    private final LocalDate discoveryDate;
    private final LossCategory lossCategory;
    private final EventType eventType;
    private final Severity severity;
    private final String description;
    private final String currencyCode;
    private final BigDecimal grossLoss;
    private final BigDecimal recoveries;
    private final BigDecimal potentialLoss;
    private final UUID responsiblePersonId;
    private final String responsiblePersonName;
    private final Integer recordVersion;
    private final boolean activeVersion;
    private final Instant createdAt;
    private final UUID createdBy;
    private AuthorizationStatus authorizationStatus;
    private IncidentStatus status;
    private BigDecimal netLoss;
    private UUID submittedBy;
    private Instant submittedAt;
    private UUID authorizedBy;
    private Instant authorizedAt;
    private UUID lastModifiedBy;
    private Instant updatedAt;
    private boolean deleted;
    private UUID deletedBy;
    private Instant deletedAt;

    private OltsIncident(UUID id, String incidentId, UUID departmentId, UUID branchId, UUID inputterUserId,
                         LocalDate incidentDate, LocalDate discoveryDate, LossCategory lossCategory,
                         EventType eventType, Severity severity, String description, String currencyCode,
                         BigDecimal grossLoss, BigDecimal recoveries, BigDecimal potentialLoss,
                         UUID responsiblePersonId, String responsiblePersonName, Integer recordVersion,
                         boolean activeVersion, Instant createdAt, UUID createdBy) {
        this.id = id;
        this.incidentId = incidentId;
        this.departmentId = departmentId;
        this.branchId = branchId;
        this.inputterUserId = inputterUserId;
        this.incidentDate = incidentDate;
        this.discoveryDate = discoveryDate;
        this.lossCategory = lossCategory;
        this.eventType = eventType;
        this.severity = severity;
        this.description = description;
        this.currencyCode = currencyCode;
        this.grossLoss = grossLoss;
        this.recoveries = recoveries;
        this.potentialLoss = potentialLoss;
        this.responsiblePersonId = responsiblePersonId;
        this.responsiblePersonName = responsiblePersonName;
        this.recordVersion = recordVersion;
        this.activeVersion = activeVersion;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.authorizationStatus = AuthorizationStatus.DRAFT;
        this.status = IncidentStatus.DRAFT;
        this.lastModifiedBy = createdBy;
        this.updatedAt = createdAt;
        this.netLoss = calculateNetLoss(grossLoss, recoveries);
        this.deleted = false;
    }

    public static OltsIncident create(String incidentId, UUID departmentId, UUID branchId, UUID inputterUserId,
                                      LocalDate incidentDate, LocalDate discoveryDate, LossCategory lossCategory,
                                      EventType eventType, Severity severity, String description, String currencyCode,
                                      BigDecimal grossLoss, BigDecimal recoveries, BigDecimal potentialLoss,
                                      UUID responsiblePersonId, String responsiblePersonName, Instant now) {
        validateDates(incidentDate, discoveryDate);
        validateAmounts(grossLoss, recoveries, potentialLoss);
        validateCurrencyCode(currencyCode);
        Objects.requireNonNull(description, "description is required");
        Objects.requireNonNull(responsiblePersonId, "responsiblePersonId is required");
        if (description.isBlank()) {
            throw new IllegalArgumentException("description is required");
        }
        return new OltsIncident(UUID.randomUUID(), incidentId, departmentId, branchId, inputterUserId, incidentDate, discoveryDate,
                lossCategory, eventType, severity, description, currencyCode, grossLoss, recoveries, potentialLoss,
                responsiblePersonId, responsiblePersonName, 1, true, now, inputterUserId);
    }

    public void submit(UUID actorUserId, Instant submittedAt) {
        if (authorizationStatus != AuthorizationStatus.DRAFT && authorizationStatus != AuthorizationStatus.RETURNED_FOR_CORRECTION) {
            throw new AuthorizationException("Only draft or returned incidents may be submitted", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        this.authorizationStatus = AuthorizationStatus.PENDING_AUTHORIZATION;
        this.submittedBy = actorUserId;
        this.submittedAt = submittedAt;
        this.lastModifiedBy = actorUserId;
        this.updatedAt = submittedAt;
    }

    public void beginAuthorizationReview(UUID actorUserId, Instant reviewedAt) {
        if (authorizationStatus != AuthorizationStatus.PENDING_AUTHORIZATION) {
            throw new AuthorizationException("Incident is not pending authorization", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        this.authorizationStatus = AuthorizationStatus.UNDER_AUTHORIZATION_REVIEW;
        this.lastModifiedBy = actorUserId;
        this.updatedAt = reviewedAt;
    }

    public void authorize(UUID authorizerUserId, Instant authorizedAt, SegregationOfDutiesPolicy policy) {
        if (authorizationStatus != AuthorizationStatus.UNDER_AUTHORIZATION_REVIEW) {
            throw new AuthorizationException("Incident is not under authorization review", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        policy.validate(inputterUserId, lastModifiedBy, authorizerUserId);
        this.authorizationStatus = AuthorizationStatus.AUTHORIZED;
        this.status = IncidentStatus.AUTHORIZED;
        this.authorizedBy = authorizerUserId;
        this.authorizedAt = authorizedAt;
        this.lastModifiedBy = authorizerUserId;
        this.updatedAt = authorizedAt;
    }

    public void reject(UUID authorizerUserId, String reason, Instant rejectedAt, SegregationOfDutiesPolicy policy) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("rejection reason is required");
        }
        if (authorizationStatus != AuthorizationStatus.UNDER_AUTHORIZATION_REVIEW) {
            throw new AuthorizationException("Incident is not under authorization review", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        policy.validate(inputterUserId, lastModifiedBy, authorizerUserId);
        this.authorizationStatus = AuthorizationStatus.REJECTED;
        this.lastModifiedBy = authorizerUserId;
        this.updatedAt = rejectedAt;
    }

    public void returnForCorrection(UUID authorizerUserId, String reason, Instant returnedAt, SegregationOfDutiesPolicy policy) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("return reason is required");
        }
        if (authorizationStatus != AuthorizationStatus.UNDER_AUTHORIZATION_REVIEW) {
            throw new AuthorizationException("Incident is not under authorization review", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        policy.validate(inputterUserId, lastModifiedBy, authorizerUserId);
        this.authorizationStatus = AuthorizationStatus.RETURNED_FOR_CORRECTION;
        this.lastModifiedBy = authorizerUserId;
        this.updatedAt = returnedAt;
    }

    public void updateDraft(UUID actorUserId,
                            UUID departmentId,
                            UUID branchId,
                            LocalDate incidentDate,
                            LocalDate discoveryDate,
                            LossCategory lossCategory,
                            EventType eventType,
                            Severity severity,
                            String description,
                            String currencyCode,
                            BigDecimal grossLoss,
                            BigDecimal recoveries,
                            BigDecimal potentialLoss,
                            UUID responsiblePersonId,
                            String responsiblePersonName,
                            Instant updatedAt) {
        if (authorizationStatus != AuthorizationStatus.DRAFT && authorizationStatus != AuthorizationStatus.RETURNED_FOR_CORRECTION) {
            throw new AuthorizationException("Only draft or returned incidents may be updated", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        validateDates(incidentDate, discoveryDate);
        validateAmounts(grossLoss, recoveries, potentialLoss);
        validateCurrencyCode(currencyCode);
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description is required");
        }
        this.lastModifiedBy = actorUserId;
        this.updatedAt = updatedAt;
        this.authorizationStatus = AuthorizationStatus.DRAFT;
        this.status = IncidentStatus.DRAFT;
        setMutableFields(departmentId, branchId, incidentDate, discoveryDate, lossCategory, eventType, severity, description,
                currencyCode, grossLoss, recoveries, potentialLoss, responsiblePersonId, responsiblePersonName);
    }

    public void markDeleted(UUID actorUserId, Instant deletedAt) {
        if (authorizationStatus == AuthorizationStatus.PENDING_AUTHORIZATION || authorizationStatus == AuthorizationStatus.UNDER_AUTHORIZATION_REVIEW) {
            throw new AuthorizationException("Pending incidents cannot be deleted", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        if (authorizationStatus == AuthorizationStatus.AUTHORIZED) {
            throw new AuthorizationException("Authorized incidents require amendment or archive workflow", ErrorCodes.INVALID_WORKFLOW_TRANSITION);
        }
        this.deleted = true;
        this.deletedBy = actorUserId;
        this.deletedAt = deletedAt;
        this.lastModifiedBy = actorUserId;
        this.updatedAt = deletedAt;
    }

    public static OltsIncident rehydrate(UUID id,
                                         String incidentId,
                                         UUID departmentId,
                                         UUID branchId,
                                         UUID inputterUserId,
                                         LocalDate incidentDate,
                                         LocalDate discoveryDate,
                                         LossCategory lossCategory,
                                         EventType eventType,
                                         Severity severity,
                                         String description,
                                         String currencyCode,
                                         BigDecimal grossLoss,
                                         BigDecimal recoveries,
                                         BigDecimal potentialLoss,
                                         UUID responsiblePersonId,
                                         String responsiblePersonName,
                                         Integer recordVersion,
                                         boolean activeVersion,
                                         Instant createdAt,
                                         UUID createdBy,
                                         AuthorizationStatus authorizationStatus,
                                         IncidentStatus status,
                                         BigDecimal netLoss,
                                         UUID submittedBy,
                                         Instant submittedAt,
                                         UUID authorizedBy,
                                         Instant authorizedAt,
                                         UUID lastModifiedBy,
                                         Instant updatedAt,
                                         boolean deleted,
                                         UUID deletedBy,
                                         Instant deletedAt) {
        OltsIncident incident = new OltsIncident(id, incidentId, departmentId, branchId, inputterUserId, incidentDate, discoveryDate,
                lossCategory, eventType, severity, description, currencyCode, grossLoss, recoveries, potentialLoss,
                responsiblePersonId, responsiblePersonName, recordVersion, activeVersion, createdAt, createdBy);
        incident.authorizationStatus = authorizationStatus;
        incident.status = status;
        incident.netLoss = netLoss;
        incident.submittedBy = submittedBy;
        incident.submittedAt = submittedAt;
        incident.authorizedBy = authorizedBy;
        incident.authorizedAt = authorizedAt;
        incident.lastModifiedBy = lastModifiedBy;
        incident.updatedAt = updatedAt;
        incident.deleted = deleted;
        incident.deletedBy = deletedBy;
        incident.deletedAt = deletedAt;
        return incident;
    }

    public static BigDecimal calculateNetLoss(BigDecimal grossLoss, BigDecimal recoveries) {
        validateAmounts(grossLoss, recoveries, BigDecimal.ZERO);
        return grossLoss.subtract(recoveries).setScale(2, RoundingMode.HALF_UP);
    }

    private static void validateDates(LocalDate incidentDate, LocalDate discoveryDate) {
        if (incidentDate == null || discoveryDate == null) {
            throw new IllegalArgumentException("incidentDate and discoveryDate are required");
        }
        if (discoveryDate.isBefore(incidentDate)) {
            throw new IllegalArgumentException("discoveryDate cannot precede incidentDate");
        }
        if (incidentDate.isAfter(LocalDate.of(2026, 8, 5).plusDays(1))) {
            throw new IllegalArgumentException("incidentDate cannot be unreasonably in the future");
        }
    }

    private static void validateAmounts(BigDecimal grossLoss, BigDecimal recoveries, BigDecimal potentialLoss) {
        if (grossLoss.signum() < 0 || recoveries.signum() < 0 || potentialLoss.signum() < 0) {
            throw new IllegalArgumentException("financial amounts cannot be negative");
        }
        if (recoveries.compareTo(grossLoss) > 0) {
            throw new IllegalArgumentException("recoveries cannot exceed grossLoss");
        }
    }

    private static void validateCurrencyCode(String currencyCode) {
        if (currencyCode == null || !currencyCode.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("currencyCode must be a valid 3-letter ISO code");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public UUID getBranchId() {
        return branchId;
    }

    public UUID getInputterUserId() {
        return inputterUserId;
    }

    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public LocalDate getDiscoveryDate() {
        return discoveryDate;
    }

    public LossCategory getLossCategory() {
        return lossCategory;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getGrossLoss() {
        return grossLoss;
    }

    public BigDecimal getRecoveries() {
        return recoveries;
    }

    public BigDecimal getNetLoss() {
        return netLoss;
    }

    public BigDecimal getPotentialLoss() {
        return potentialLoss;
    }

    public UUID getResponsiblePersonId() {
        return responsiblePersonId;
    }

    public String getResponsiblePersonName() {
        return responsiblePersonName;
    }

    public Integer getRecordVersion() {
        return recordVersion;
    }

    public boolean isActiveVersion() {
        return activeVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public AuthorizationStatus getAuthorizationStatus() {
        return authorizationStatus;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public UUID getSubmittedBy() {
        return submittedBy;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public UUID getAuthorizedBy() {
        return authorizedBy;
    }

    public Instant getAuthorizedAt() {
        return authorizedAt;
    }

    public UUID getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    private void setMutableFields(UUID departmentId,
                                  UUID branchId,
                                  LocalDate incidentDate,
                                  LocalDate discoveryDate,
                                  LossCategory lossCategory,
                                  EventType eventType,
                                  Severity severity,
                                  String description,
                                  String currencyCode,
                                  BigDecimal grossLoss,
                                  BigDecimal recoveries,
                                  BigDecimal potentialLoss,
                                  UUID responsiblePersonId,
                                  String responsiblePersonName) {
        trySetField("departmentId", departmentId);
        trySetField("branchId", branchId);
        trySetField("incidentDate", incidentDate);
        trySetField("discoveryDate", discoveryDate);
        trySetField("lossCategory", lossCategory);
        trySetField("eventType", eventType);
        trySetField("severity", severity);
        trySetField("description", description);
        trySetField("currencyCode", currencyCode);
        trySetField("grossLoss", grossLoss);
        trySetField("recoveries", recoveries);
        trySetField("potentialLoss", potentialLoss);
        trySetField("responsiblePersonId", responsiblePersonId);
        trySetField("responsiblePersonName", responsiblePersonName);
        this.netLoss = calculateNetLoss(grossLoss, recoveries);
    }

    private void trySetField(String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = OltsIncident.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to update incident field " + fieldName, exception);
        }
    }
}
